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
package org.seasar.doma.extension.domax.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

public class JavaPopupToResourceEditorHandler extends AbstractToResourceEditorHandler {

    public JavaPopupToResourceEditorHandler() {
    }

    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getActiveMenuSelection(event);
        if (selection == null) {
            return null;
        }
        Shell shell = HandlerUtil.getActiveShell(event);
        if (shell == null) {
            return null;
        }
        IMethod method = getMethod(selection);
        if (method == null) {
            return null;
        }
        openFile(method, shell);
        return null;
    }

    protected IMethod getMethod(ISelection selection) {
        if (!(selection instanceof IStructuredSelection)) {
            return null;
        }
        IStructuredSelection structuredSelection = (IStructuredSelection) selection;
        Object element = structuredSelection.getFirstElement();
        if (!(element instanceof IAdaptable)) {
            return null;
        }
        IAdaptable adaptable = (IAdaptable) element;
        return (IMethod) adaptable.getAdapter(IMethod.class);
    }

}
