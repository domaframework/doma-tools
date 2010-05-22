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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.seasar.doma.extension.domax.Logger;

public class DaoFile {

    private final IFile file;

    public DaoFile(IFile file) {
        this.file = file;
    }

    public void openInEditor() {
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

}
