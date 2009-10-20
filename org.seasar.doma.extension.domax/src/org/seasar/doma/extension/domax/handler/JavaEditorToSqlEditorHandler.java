package org.seasar.doma.extension.domax.handler;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;
import org.seasar.doma.extension.domax.ClassConstants;
import org.seasar.doma.extension.domax.Constants;
import org.seasar.doma.extension.domax.Logger;
import org.seasar.doma.extension.domax.util.AnnotationUtil;
import org.seasar.doma.extension.domax.util.JavaProjectUtil;
import org.seasar.doma.extension.domax.wizard.NewSqlFileWizardDialogOpener;

public class JavaEditorToSqlEditorHandler extends AbstractHandler {

    public JavaEditorToSqlEditorHandler() {
    }

    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (selection == null) {
            return null;
        }
        IEditorPart editor = HandlerUtil.getActiveEditor(event);
        if (selection == null) {
            return null;
        }
        Shell shell = HandlerUtil.getActiveShell(event);
        if (shell == null) {
            return null;
        }
        IJavaElement selectedJavaElement = getSelectedJavaElement(selection,
                editor);
        if (selectedJavaElement == null) {
            return null;
        }
        openSqlFile(selectedJavaElement, shell);
        return null;
    }

    protected IJavaElement getSelectedJavaElement(ISelection selection,
            IEditorPart editor) {
        if (!(selection instanceof ITextSelection)) {
            return null;
        }
        ITextSelection textSelection = (ITextSelection) selection;
        if (textSelection == null) {
            return null;
        }
        IJavaElement javaElement = JavaUI.getEditorInputJavaElement(editor
                .getEditorInput());
        if (javaElement == null) {
            return null;
        }
        ICompilationUnit compilationUnit = (ICompilationUnit) javaElement
                .getAdapter(ICompilationUnit.class);
        if (compilationUnit == null) {
            return null;
        }
        try {
            return compilationUnit.getElementAt(textSelection.getOffset());
        } catch (JavaModelException ignored) {
            return null;
        }
    }

    protected void openSqlFile(IJavaElement javaElement, Shell shell) {
        IMethod method = (IMethod) javaElement.getAdapter(IMethod.class);
        if (method != null) {
            openSqlFile(method, shell);
        } else {
            IType type = (IType) javaElement.getAdapter(IType.class);
            if (type != null) {
                openSqlFile(type);
            }
        }
    }

    protected void openSqlFile(IMethod method, Shell shell) {
        IType type = (IType) method.getParent();
        if (type == null) {
            return;
        }
        if (!AnnotationUtil.isExistent(type, ClassConstants.Dao)) {
            return;
        }
        String className = type.getFullyQualifiedName();
        String methodName = method.getElementName();
        IJavaProject javaProject = type.getJavaProject();
        IFile sqlFile = findSqlFile(javaProject, className, methodName);
        if (sqlFile != null) {
            openSqlFile(sqlFile);
        } else {
            sqlFile = createSqlFileWithWizard(javaProject, className,
                    methodName, shell);
            if (sqlFile != null) {
                // writeSql(sqlFile, method);
                openSqlFile(sqlFile);
            }
        }
    }

    protected void writeSql(IFile sqlFile, IMethod method) {
        String s = "select";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(s
                .getBytes(Charset.forName("UTF-8")));
        try {
            sqlFile.setCharset("UTF-8", null);
            sqlFile.setContents(inputStream, false, false, null);
        } catch (CoreException ignored) {
        }
    }

    protected IFile createSqlFileWithWizard(IJavaProject javaProject,
            String className, String methodName, Shell shell) {
        NewSqlFileWizardDialogOpener opener = new NewSqlFileWizardDialogOpener(
                javaProject, className, methodName, shell);
        return opener.open();
    }

    protected IFile findSqlFile(IJavaProject javaProject, String className,
            String methodName) {
        IProject project = javaProject.getProject();
        IPath path = Path.fromPortableString(className.replace(".", "/"))
                .append(methodName);
        for (IResource sourceFolder : JavaProjectUtil
                .getSourceFolders(javaProject)) {
            IPath sqlFilePath = sourceFolder.getProjectRelativePath().append(
                    Constants.META_INF).append(path).addFileExtension(
                    Constants.SQL_FILE_EXTESION);
            IFile sqlFile = project.getFile(sqlFilePath);
            if (sqlFile.exists()) {
                return sqlFile;
            }
        }
        return null;
    }

    protected void openSqlFile(IType type) {
        if (!AnnotationUtil.isExistent(type, ClassConstants.Dao)) {
            return;
        }
        String className = type.getFullyQualifiedName();
        IFolder sqlFolder = findSqlFolder(type.getJavaProject(), className);
        if (sqlFolder == null) {
            return;
        }
        try {
            for (IResource child : sqlFolder.members()) {
                IFile file = (IFile) child.getAdapter(IFile.class);
                if (file.getFileExtension().equals(Constants.SQL_FILE_EXTESION)
                        && file.exists()) {
                    openSqlFile(file);
                    break;
                }
            }
        } catch (CoreException ignored) {
        }
    }

    protected IFolder findSqlFolder(IJavaProject javaProject, String className) {
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

    protected void openSqlFile(IFile sqlFile) {
        IWorkbenchWindow window = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }
        IWorkbenchPage page = window.getActivePage();
        try {
            IDE.openEditor(page, sqlFile);
        } catch (PartInitException e) {
            Logger.error(e);
        }
    }

}
