package org.seasar.doma.extension.domax.handler;

import static org.seasar.doma.extension.domax.util.AssertionUtil.assertNotNull;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.seasar.doma.extension.domax.ClassConstants;
import org.seasar.doma.extension.domax.Constants;
import org.seasar.doma.extension.domax.Logger;
import org.seasar.doma.extension.domax.util.AnnotationUtil;
import org.seasar.doma.extension.domax.util.JavaProjectUtil;
import org.seasar.doma.extension.domax.wizard.ResourceFileNewWizardDialogOpener;
import org.seasar.doma.extension.domax.wizard.ScriptFileNewWizardDialogOpener;
import org.seasar.doma.extension.domax.wizard.SqlFileNewWizardDialogOpener;

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
                openFile(type);
            }
        }
    }

    protected void openFile(IMethod method, Shell shell) {
        assertNotNull(method, shell);
        IType type = (IType) method.getParent();
        if (type == null) {
            return;
        }
        if (!AnnotationUtil.isExistent(type, ClassConstants.Dao)) {
            return;
        }
        IJavaProject javaProject = type.getJavaProject();
        IFile file = findFile(javaProject, type, method);
        if (file != null) {
            openFile(file);
        } else {
            ResourceFileNewWizardDialogOpener opener = createNewWizardDialogOpener(
                    javaProject, type, method, shell);
            file = opener.open();
            if (file != null) {
                openFile(file);
            }
        }
    }

    private ResourceFileNewWizardDialogOpener createNewWizardDialogOpener(
            IJavaProject javaProject, IType type, IMethod method, Shell shell) {
        String typeName = type.getFullyQualifiedName();
        String methodName = method.getElementName();
        if (AnnotationUtil.isExistent(method, ClassConstants.Script)) {
            return new ScriptFileNewWizardDialogOpener(javaProject, typeName,
                    methodName, shell);
        }
        return new SqlFileNewWizardDialogOpener(javaProject, typeName,
                methodName, shell);
    }

    private IFile findFile(IJavaProject javaProject, IType type, IMethod method) {
        IPath path = Path.fromPortableString(
                type.getFullyQualifiedName().replace(".", "/")).append(
                method.getElementName());
        if (AnnotationUtil.isExistent(method, ClassConstants.Script)) {
            return findFile(javaProject, path, Constants.SCRIPT_FILE_EXTESION);
        }
        return findFile(javaProject, path, Constants.SQL_FILE_EXTESION);
    }

    private IFile findFile(IJavaProject javaProject, IPath path,
            String extension) {
        IProject project = javaProject.getProject();
        for (IResource sourceFolder : JavaProjectUtil
                .getSourceFolders(javaProject)) {
            IPath filePath = sourceFolder.getProjectRelativePath().append(
                    Constants.META_INF).append(path)
                    .addFileExtension(extension);
            IFile file = project.getFile(filePath);
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }

    private void openFile(IType type) {
        if (!AnnotationUtil.isExistent(type, ClassConstants.Dao)) {
            return;
        }
        String className = type.getFullyQualifiedName();
        IFolder folder = findFolder(type.getJavaProject(), className);
        if (folder == null) {
            return;
        }
        try {
            for (IResource child : folder.members()) {
                IFile file = (IFile) child.getAdapter(IFile.class);
                if (file == null) {
                    continue;
                }
                String extension = file.getFileExtension();
                if ((Constants.SQL_FILE_EXTESION.equals(extension) || Constants.SCRIPT_FILE_EXTESION
                        .equals(extension))
                        && file.exists()) {
                    openFile(file);
                    break;
                }
            }
        } catch (CoreException ignored) {
        }
    }

    private IFolder findFolder(IJavaProject javaProject, String className) {
        IProject project = javaProject.getProject();
        IPath path = Path.fromPortableString(className.replace(".", "/"));
        for (IResource sourceFolder : JavaProjectUtil
                .getSourceFolders(javaProject)) {
            IPath sqlFolderPath = sourceFolder.getProjectRelativePath().append(
                    Constants.META_INF).append(path);
            IFolder sqlFolder = project.getFolder(sqlFolderPath);
            if (sqlFolder.exists()) {
                return sqlFolder;
            }
        }
        return null;
    }

    private void openFile(IFile file) {
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
