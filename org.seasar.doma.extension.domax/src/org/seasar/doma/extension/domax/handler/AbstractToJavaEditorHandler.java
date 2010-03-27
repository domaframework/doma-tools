package org.seasar.doma.extension.domax.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.resources.IFile;
import org.seasar.doma.extension.domax.factory.Factory;
import org.seasar.doma.extension.domax.model.DaoMethod;
import org.seasar.doma.extension.domax.model.DaoMethodFactory;
import static org.seasar.doma.extension.domax.util.AssertionUtil.*;

public abstract class AbstractToJavaEditorHandler extends AbstractHandler {

    protected AbstractToJavaEditorHandler() {
    }

    protected void openDaoMethod(IFile sqlFile) {
        assertNotNull(sqlFile);
        DaoMethod daoMethod = getDaoMethod(sqlFile);
        if (daoMethod != null) {
            daoMethod.openInEditor();
        }
    }

    private DaoMethod getDaoMethod(IFile sqlFile) {
        DaoMethodFactory daoMethodFactory = Factory.getDaoMethodFactory();
        return daoMethodFactory.createDaoMethod(sqlFile);
    }

}
