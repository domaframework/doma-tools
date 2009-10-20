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
        if (file == null
                || !file.getFileExtension().equals(Constants.SQL_FILE_EXTESION)) {
            return null;
        }
        return file;
    }

    protected DaoMethod getDaoMethod(IFile sqlFile) {
        DaoMethodFactory daoMethodFactory = Factory.getDaoMethodFactory();
        return daoMethodFactory.createDaoMethod(sqlFile);
    }

}
