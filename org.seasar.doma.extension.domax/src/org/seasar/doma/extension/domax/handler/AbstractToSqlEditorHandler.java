package org.seasar.doma.extension.domax.handler;

import static org.seasar.doma.extension.domax.util.AssertionUtil.assertNotNull;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.swt.widgets.Shell;
import org.seasar.doma.extension.domax.model.ResourceFile;
import org.seasar.doma.extension.domax.model.ResourceFileFactory;

public abstract class AbstractToSqlEditorHandler extends AbstractHandler {

    protected AbstractToSqlEditorHandler() {
    }

    protected void openFile(IJavaElement javaElement, Shell shell) {
        assertNotNull(javaElement, shell);
        IMethod method = (IMethod) javaElement.getAdapter(IMethod.class);
        if (method != null) {
            openFile(method, shell);
        } else {
            IType type = (IType) javaElement.getAdapter(IType.class);
            if (type != null) {
                ResourceFile resourceFile = ResourceFileFactory
                        .createResourceFile(type);
                if (resourceFile != null && resourceFile.exists()) {
                    resourceFile.openInEditor();
                }
            }
        }
    }

    protected void openFile(IMethod method, Shell shell) {
        assertNotNull(method, shell);
        IType type = (IType) method.getParent();
        if (type == null) {
            return;
        }
        ResourceFile resourceFile = ResourceFileFactory.createResourceFile(
                type, method);
        if (resourceFile == null) {
            return;
        }
        if (resourceFile.exists()) {
            resourceFile.openInEditor();
        } else {
            resourceFile.openNewWizardDialog(type, method, shell);
            if (resourceFile.exists()) {
                resourceFile.openInEditor();
            }
        }
    }

}
