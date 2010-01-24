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
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;
import org.seasar.doma.extension.domax.Constants;
import org.seasar.doma.extension.domax.factory.Factory;
import org.seasar.doma.extension.domax.model.DaoMethod;
import org.seasar.doma.extension.domax.model.DaoMethodFactory;

public class SqlEditorToJavaEditorHandler extends AbstractHandler {

    public SqlEditorToJavaEditorHandler() {
    }

    public Object execute(ExecutionEvent event) throws ExecutionException {
        IEditorPart editor = HandlerUtil.getActiveEditor(event);
        if (editor == null) {
            return null;
        }
        IFile sqlFile = getSqlFile(editor);
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

    protected IFile getSqlFile(IEditorPart editor) {
        ITextEditor textEditor = (ITextEditor) editor
                .getAdapter(ITextEditor.class);
        if (textEditor == null) {
            return null;
        }
        IFile file = (IFile) textEditor.getEditorInput()
                .getAdapter(IFile.class);
        if (file == null) {
            return null;
        }
        if (!Constants.SQL_FILE_EXTESION.equals(file.getFileExtension())) {
            return null;
        }
        return file;
    }

    protected DaoMethod getDaoMethod(IFile sqlFile) {
        DaoMethodFactory daoMethodFactory = Factory.getDaoMethodFactory();
        return daoMethodFactory.createDaoMethod(sqlFile);
    }

}
