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
