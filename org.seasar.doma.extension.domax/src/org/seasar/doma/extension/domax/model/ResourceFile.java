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
package org.seasar.doma.extension.domax.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.seasar.doma.extension.domax.Constants;
import org.seasar.doma.extension.domax.Logger;
import org.seasar.doma.extension.domax.wizard.ResourceFileNewWizardDialogOpener;

public abstract class ResourceFile {

    protected IFile file;

    protected ResourceFile(IFile file) {
        this.file = file;
    }

    public boolean exists() {
        return existsInternal();
    }

    private boolean existsInternal() {
        return file != null && file.exists();
    }

    public void openNewWizardDialog(IType type, IMethod method, Shell shell) {
        ResourceFileNewWizardDialogOpener opener = createNewWizardDialogOpener(
                type.getJavaProject(), type, method, shell);
        file = opener.open();
    }

    protected abstract ResourceFileNewWizardDialogOpener createNewWizardDialogOpener(
            IJavaProject javaProject, IType type, IMethod method, Shell shell);

    public void openInEditor() {
        if (!existsInternal()) {
            return;
        }
        IWorkbenchWindow window = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }
        IWorkbenchPage page = window.getActivePage();
        try {
            IDE.openEditor(page, file);
        } catch (PartInitException e) {
            Logger.error(e);
        }
    }

    public static boolean isResourceFileExtension(String extension) {
        return Constants.SQL_FILE_EXTESION.equals(extension)
                || Constants.SCRIPT_FILE_EXTESION.equals(extension);
    }

}
