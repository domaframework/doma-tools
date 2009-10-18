package org.seasar.doma.extension.domax;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.seasar.doma.extension.domax.factory.Factory;
import org.seasar.doma.extension.domax.model.DaoMethod;
import org.seasar.doma.extension.domax.model.DaoMethodFactory;

public class SqlFileChangeListener implements IResourceChangeListener {

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		if (event.getBuildKind() != IncrementalProjectBuilder.FULL_BUILD
				&& event.getBuildKind() != IncrementalProjectBuilder.CLEAN_BUILD) {
			IResourceDelta delta = event.getDelta();
			if (delta != null) {
				try {
					delta.accept(new SqlFileDeltaVisitor());
				} catch (CoreException e) {
					Logger.error(e);
				}
			}
		}
	}

	private class SqlFileDeltaVisitor implements IResourceDeltaVisitor {

		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
			case IResourceDelta.REMOVED:
			case IResourceDelta.CHANGED:
				checkSqlFile(resource);
				break;
			}
			return true;
		}
	}

	void checkSqlFile(IResource resource) {
		final IFile sqlFile = (IFile) resource.getAdapter(IFile.class);
		if (sqlFile == null
				|| !sqlFile.getFileExtension().equalsIgnoreCase(
						Constants.SQL_FILE_EXTESION)) {
			return;
		}
		DaoMethodFactory daoMethodFactory = Factory.getDaoMethodFactory();
		DaoMethod daoMethod = daoMethodFactory.createDaoMethod(sqlFile);
		if (daoMethod == null) {
			return;
		}

		IJavaProject javaProject = daoMethod.getJavaProject();
		try {
			IType type = javaProject.findType(daoMethod.getClassName());
			if (type == null) {
				return;
			}
			final ICompilationUnit compilationUnit = type.getCompilationUnit();
			if (compilationUnit == null) {
				return;
			}
			new Job(daoMethod.getClassName()) {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						compilationUnit.becomeWorkingCopy(monitor);
						IBuffer buffer = compilationUnit.getBuffer();
						buffer.append("");
						buffer.save(monitor, true);
						buffer.close();
						compilationUnit.commitWorkingCopy(true, monitor);
						sqlFile.getProject().build(
								IncrementalProjectBuilder.INCREMENTAL_BUILD,
								monitor);
						return Status.OK_STATUS;
					} catch (CoreException e) {
						Logger.error(e);
						return new Status(IStatus.ERROR, Domax.PLUGIN_ID, "", e);
					}
				}
			}.schedule();
		} catch (JavaModelException e) {
			Logger.error(e);
		}
	}
}
