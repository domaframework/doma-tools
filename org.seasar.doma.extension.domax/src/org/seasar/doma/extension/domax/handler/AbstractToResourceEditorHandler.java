package org.seasar.doma.extension.domax.handler;

import static org.seasar.doma.extension.domax.util.AssertionUtil.assertNotNull;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.swt.widgets.Shell;
import org.seasar.doma.extension.domax.factory.Factory;
import org.seasar.doma.extension.domax.model.ResourceFile;
import org.seasar.doma.extension.domax.model.ResourceFileFactory;

public abstract class AbstractToResourceEditorHandler extends AbstractHandler {

    protected AbstractToResourceEditorHandler() {
    }

    protected void openFile(IJavaElement javaElement, Shell shell) {
        assertNotNull(javaElement, shell);
        IMethod method = (IMethod) javaElement.getAdapter(IMethod.class);
        if (method != null) {
            openFile(method, shell);
        } else {
            IType type = (IType) javaElement.getAdapter(IType.class);
            if (type != null) {
                ResourceFileFactory resourceFileFactory = Factory
                        .getResourceFileFactory();
                ResourceFile resourceFile = resourceFileFactory
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
        ResourceFileFactory resourceFileFactory = Factory
                .getResourceFileFactory();
        ResourceFile resourceFile = resourceFileFactory.createResourceFile(
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
