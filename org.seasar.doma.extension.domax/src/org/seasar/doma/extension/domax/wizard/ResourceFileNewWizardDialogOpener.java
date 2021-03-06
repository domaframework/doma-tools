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
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.seasar.doma.extension.domax.Constants;
import org.seasar.doma.extension.domax.Domax;
import org.seasar.doma.extension.domax.Logger;
import org.seasar.doma.extension.domax.util.AssertionUtil;
import org.seasar.doma.extension.domax.util.FolderUtil;
import org.seasar.doma.extension.domax.util.JavaProjectUtil;

public abstract class ResourceFileNewWizardDialogOpener {

    protected final IJavaProject javaProject;

    protected final String typeName;

    protected final String methodName;

    protected final Shell shell;

    protected final String sourceFolderPath;

    public ResourceFileNewWizardDialogOpener(IType type, IMethod method,
            Shell shell) {
        AssertionUtil.assertNotNull(type, method, shell);
        this.javaProject = type.getJavaProject();
        this.typeName = type.getFullyQualifiedName();
        this.methodName = method.getElementName();
        this.shell = shell;
        this.sourceFolderPath = findSourceFolderPath(javaProject, typeName);
    }

    protected String findSourceFolderPath(IJavaProject javaProject,
            String className) {
        IProject project = javaProject.getProject();
        List<IResource> sourceFolders = JavaProjectUtil
                .getSourceFolders(javaProject);
        for (IResource sourceFolder : sourceFolders) {
            IPath sourceFolderPath = sourceFolder.getProjectRelativePath();
            IFolder sqlFolder = project.getFolder(sourceFolderPath.append(
                    Constants.META_INF).append(className.replace(".", "/")));
            if (sqlFolder.exists()) {
                return sourceFolderPath.toPortableString();
            }
        }
        for (IResource sourceFolder : sourceFolders) {
            IPath sourceFolderPath = sourceFolder.getProjectRelativePath();
            IFolder metaInfFolder = project.getFolder(sourceFolder
                    .getProjectRelativePath().append(Constants.META_INF));
            if (metaInfFolder.exists()) {
                return sourceFolderPath.toPortableString();
            }
        }
        for (IResource sourceFolder : sourceFolders) {
            if (sourceFolder.exists()) {
                return sourceFolder.getProjectRelativePath().toPortableString();
            }
        }
        return null;
    }

    public IFile open() {
        if (sourceFolderPath == null) {
            IStatus status = new Status(IStatus.ERROR, Domax.PLUGIN_ID,
                    "source folder is not found.");
            ErrorDialog.openError(shell, "source folder is not found.",
                    "source folder is not found.", status);
            return null;
        }
        ResourceFileNewWizard wizard = createFileNewWizard();
        WizardDialog dialog = new WizardDialog(shell, wizard);
        if (dialog.open() == WizardDialog.OK) {
            IFile file = wizard.getNewFile();
            if (file != null) {
                return file;
            }
        }
        return null;
    }

    protected abstract ResourceFileNewWizard createFileNewWizard();

    protected IContainer createSqlFileContainer() {
        IProject project = javaProject.getProject();
        IPath sqlFolderPath = project.getFolder(sourceFolderPath)
                .getProjectRelativePath().append(Constants.META_INF)
                .append(typeName.replace(".", "/"));
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

}
