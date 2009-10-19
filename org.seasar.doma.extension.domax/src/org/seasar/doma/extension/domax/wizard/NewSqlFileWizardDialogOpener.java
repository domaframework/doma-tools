package org.seasar.doma.extension.domax.wizard;

import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.seasar.doma.extension.domax.Constants;
import org.seasar.doma.extension.domax.Domax;
import org.seasar.doma.extension.domax.Logger;
import org.seasar.doma.extension.domax.util.AssertionUtil;
import org.seasar.doma.extension.domax.util.FolderUtil;
import org.seasar.doma.extension.domax.util.JavaProjectUtil;

public class NewSqlFileWizardDialogOpener {

	private final IJavaProject javaProject;

	private final String className;

	private final String methodName;

	private final Shell shell;

	private final String metaInfPath;

	public NewSqlFileWizardDialogOpener(IJavaProject javaProject,
			String className, String methodName, Shell shell) {
		AssertionUtil.assertNotNull(javaProject, className, methodName, shell);
		this.javaProject = javaProject;
		this.className = className;
		this.methodName = methodName;
		this.shell = shell;
		String metaInfPath = getMetaInfPath();
		if (metaInfPath == null) {
			metaInfPath = findMetaInfPath(javaProject, className);
		}
		this.metaInfPath = metaInfPath;
	}

	protected String findMetaInfPath(IJavaProject javaProject, String className) {
		IProject project = javaProject.getProject();
		List<IResource> sourceFolders = JavaProjectUtil
				.getSourceFolders(javaProject);
		for (IResource sourceFolder : sourceFolders) {
			IPath metaInfFolderPath = sourceFolder.getProjectRelativePath()
					.append(Constants.META_INF);
			IFolder sqlFolder = project.getFolder(metaInfFolderPath
					.append(className.replace(".", "/")));
			if (sqlFolder.exists()) {
				return metaInfFolderPath.toPortableString();
			}
		}
		for (IResource sourceFolder : sourceFolders) {
			IPath metaInfFolderPath = sourceFolder.getProjectRelativePath()
					.append(Constants.META_INF);
			IFolder metaInfFolder = project.getFolder(metaInfFolderPath);
			if (metaInfFolder.exists()) {
				return metaInfFolderPath.toPortableString();
			}
		}
		return null;
	}

	public IFile open() {
		if (metaInfPath == null) {
			IStatus status = new Status(IStatus.ERROR, Domax.PLUGIN_ID,
					"META-INF not found in source folder.");
			ErrorDialog.openError(shell, "META-INF not found.",
					"META-INF not found in source folder.", status);
			return null;
		}
		IContainer sqlFileContainer = createSqlFileContainer();
		String sqlFileName = methodName + "." + Constants.SQL_FILE_EXTESION;
		NewSqlFileWizard newSqlFileWizard = new NewSqlFileWizard(
				sqlFileContainer, sqlFileName);
		WizardDialog dialog = new WizardDialog(shell, newSqlFileWizard);
		if (dialog.open() == WizardDialog.OK) {
			IFile sqlFile = newSqlFileWizard.getNewFile();
			if (sqlFile != null) {
				tryToSaveMetaInfPath(sqlFile);
				return sqlFile;
			}
		}
		return null;
	}

	protected IContainer createSqlFileContainer() {
		IProject project = javaProject.getProject();
		IFolder metaInfFolder = project.getFolder(metaInfPath);
		IPath sqlFolderPath = metaInfFolder.getProjectRelativePath().append(
				className.replace(".", "/"));
		IFolder sqlFolder = project.getFolder(sqlFolderPath);
		if (sqlFolder.exists()) {
			return sqlFolder;
		}
		try {
			FolderUtil.createFolder(sqlFolder, false, false, null);
			return sqlFolder;
		} catch (CoreException e) {
			Logger.error(e);
		}
		return project;
	}

	protected String getMetaInfPath() {
		IDialogSettings dialogSettings = Domax.getDefault().getDialogSettings();
		IDialogSettings section = dialogSettings
				.getSection(Constants.NewSqlFileDialog.SECTION_NAME);
		if (section == null) {
			return null;
		}
		return section.get(Constants.NewSqlFileDialog.META_INF_PATH_KEY);

	}

	protected void tryToSaveMetaInfPath(IFile sqlFile) {
		IPath sqlFilePath = sqlFile.getProjectRelativePath();
		for (int i = 0; i < sqlFilePath.segmentCount(); i++) {
			String segment = sqlFilePath.segment(i);
			if (Constants.META_INF.equals(segment)) {
				saveMetaInfPath(sqlFilePath.uptoSegment(i + 1)
						.toPortableString());
				break;
			}
		}
	}

	protected void saveMetaInfPath(String metaInfPath) {
		IDialogSettings dialogSettings = Domax.getDefault().getDialogSettings();
		IDialogSettings section = dialogSettings
				.getSection(Constants.NewSqlFileDialog.SECTION_NAME);
		if (section == null) {
			section = dialogSettings
					.addNewSection(Constants.NewSqlFileDialog.SECTION_NAME);
		}
		section.put(Constants.NewSqlFileDialog.META_INF_PATH_KEY, metaInfPath);
	}
}
