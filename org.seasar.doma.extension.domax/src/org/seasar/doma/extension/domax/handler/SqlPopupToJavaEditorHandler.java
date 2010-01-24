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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.seasar.doma.extension.domax.factory.Factory;
import org.seasar.doma.extension.domax.model.DaoMethod;
import org.seasar.doma.extension.domax.model.DaoMethodFactory;

public class SqlPopupToJavaEditorHandler extends AbstractHandler {

    public SqlPopupToJavaEditorHandler() {
    }

    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getActiveMenuSelection(event);
        if (selection == null) {
            return null;
        }
        IFile sqlFile = getSqlFile(selection);
        if (sqlFile == null) {
            return null;
        }
        DaoMethod daoMethod = getDaoMethod(sqlFile);
        if (daoMethod == null) {
            return null;
        }
        daoMethod.openInEditor();
        return null;
    }

    protected IFile getSqlFile(ISelection selection) {
        if (!(selection instanceof IStructuredSelection)) {
            return null;
        }
        IStructuredSelection structuredSelection = (IStructuredSelection) selection;
        Object element = structuredSelection.getFirstElement();
        if (!(element instanceof IAdaptable)) {
            return null;
        }
        IAdaptable adaptable = (IAdaptable) element;
        return (IFile) adaptable.getAdapter(IFile.class);
    }

    protected DaoMethod getDaoMethod(IFile sqlFile) {
        DaoMethodFactory daoMethodFactory = Factory.getDaoMethodFactory();
        return daoMethodFactory.createDaoMethod(sqlFile);
    }

}
