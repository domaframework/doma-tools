/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.doma.extension.domax;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.WorkspaceJob;
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
import org.seasar.doma.extension.domax.model.ResourceFile;

public class ResourceFileChangeListener implements IResourceChangeListener {

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

        @Override
        public boolean visit(IResourceDelta delta) throws CoreException {
            IResource resource = delta.getResource();
            switch (delta.getKind()) {
            case IResourceDelta.ADDED:
            case IResourceDelta.REMOVED:
            case IResourceDelta.CHANGED:
                DaoMethod daoMethod = createDaoMethod(resource);
                if (daoMethod != null) {
                    submitJob(resource, daoMethod);
                }
                break;
            }
            return true;
        }
    }

    private DaoMethod createDaoMethod(IResource resource) {
        final IFile file = (IFile) resource.getAdapter(IFile.class);
        if (file == null) {
            return null;
        }
        String extension = file.getFileExtension();
        if (!ResourceFile.isResourceFileExtension(extension)) {
            return null;
        }
        DaoMethodFactory daoMethodFactory = Factory.getDaoMethodFactory();
        return daoMethodFactory.createDaoMethod(file);
    }

    private void submitJob(final IResource resource, DaoMethod daoMethod) {
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

            WorkspaceJob job = new WorkspaceJob("building "
                    + daoMethod.getClassName()) {

                @Override
                public IStatus runInWorkspace(IProgressMonitor monitor)
                        throws CoreException {
                    compilationUnit.becomeWorkingCopy(monitor);
                    IBuffer buffer = compilationUnit.getBuffer();
                    buffer.append("");
                    buffer.save(monitor, true);
                    buffer.close();
                    compilationUnit.commitWorkingCopy(true, monitor);
                    resource.getProject().build(
                            IncrementalProjectBuilder.INCREMENTAL_BUILD,
                            monitor);
                    return Status.OK_STATUS;
                }
            };
            job.setPriority(Job.BUILD);
            job.schedule();
        } catch (JavaModelException e) {
            Logger.error(e);
        }
    }

}
