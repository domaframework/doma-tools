package org.seasar.doma.extension.domax.wizard;

import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.seasar.doma.extension.domax.Constants;
import org.seasar.doma.extension.domax.Domax;
import org.seasar.doma.extension.domax.Logger;
import org.seasar.doma.extension.domax.util.AssertionUtil;
import org.seasar.doma.extension.domax.util.FolderUtil;
import org.seasar.doma.extension.domax.util.JavaProjectUtil;

public class NewSqlFileWizardDialogOpener {

    private final IJavaProject javaProject;

    private final String className;

    private final String methodName;

    private final Shell shell;

    private final String sourceFolderPath;

    public NewSqlFileWizardDialogOpener(IJavaProject javaProject,
            String className, String methodName, Shell shell) {
        AssertionUtil.assertNotNull(javaProject, className, methodName, shell);
        this.javaProject = javaProject;
        this.className = className;
        this.methodName = methodName;
        this.shell = shell;
        String sourceFolderPath = getSourceFolderPath();
        if (sourceFolderPath == null) {
            sourceFolderPath = findSourceFolderPath(javaProject, className);
        }
        this.sourceFolderPath = sourceFolderPath;
    }

    protected String findSourceFolderPath(IJavaProject javaProject,
            String className) {
        IProject project = javaProject.getProject();
        List<IResource> sourceFolders = JavaProjectUtil
                .getSourceFolders(javaProject);
        for (IResource sourceFolder : sourceFolders) {
            IPath sourceFolderPath = sourceFolder.getProjectRelativePath();
            IFolder sqlFolder = project.getFolder(sourceFolderPath.append(
                    Constants.META_INF).append(className.replace(".", "/")));
            if (sqlFolder.exists()) {
                return sourceFolderPath.toPortableString();
            }
        }
        for (IResource sourceFolder : sourceFolders) {
            IPath sourceFolderPath = sourceFolder.getProjectRelativePath();
            IFolder metaInfFolder = project.getFolder(sourceFolder
                    .getProjectRelativePath().append(Constants.META_INF));
            if (metaInfFolder.exists()) {
                return sourceFolderPath.toPortableString();
            }
        }
        for (IResource sourceFolder : sourceFolders) {
            if (sourceFolder.exists()) {
                return sourceFolder.getProjectRelativePath().toPortableString();
            }
        }
        return null;
    }

    public IFile open() {
        if (sourceFolderPath == null) {
            IStatus status = new Status(IStatus.ERROR, Domax.PLUGIN_ID,
                    "source folder is not found.");
            ErrorDialog.openError(shell, "source folder is not found.",
                    "source folder is not found.", status);
            return null;
        }
        IContainer sqlFileContainer = createSqlFileContainer();
        String sqlFileName = methodName + "." + Constants.SQL_FILE_EXTESION;
        NewSqlFileWizard newSqlFileWizard = new NewSqlFileWizard(
                sqlFileContainer, sqlFileName);
        WizardDialog dialog = new WizardDialog(shell, newSqlFileWizard);
        if (dialog.open() == WizardDialog.OK) {
            IFile sqlFile = newSqlFileWizard.getNewFile();
            if (sqlFile != null) {
                tryToSaveSourceFolderPath(sqlFile);
                return sqlFile;
            }
        }
        return null;
    }

    protected IContainer createSqlFileContainer() {
        IProject project = javaProject.getProject();
        IPath sqlFolderPath = project.getFolder(sourceFolderPath)
                .getProjectRelativePath().append(Constants.META_INF).append(
                        className.replace(".", "/"));
        IFolder sqlFolder = project.getFolder(sqlFolderPath);
        if (sqlFolder.exists()) {
            return sqlFolder;
        }
        try {
            FolderUtil.createFolder(sqlFolder, false, false, null);
            return sqlFolder;
        } catch (CoreException e) {
            Logger.error(e);
        }
        return project;
    }

    protected String getSourceFolderPath() {
        IDialogSettings dialogSettings = Domax.getDefault().getDialogSettings();
        IDialogSettings section = dialogSettings
                .getSection(Constants.NewSqlFileDialog.SECTION_NAME);
        if (section == null) {
            return null;
        }
        return section.get(Constants.NewSqlFileDialog.SOURCE_FOLDER_PATH_KEY);

    }

    protected void tryToSaveSourceFolderPath(IFile sqlFile) {
        if (!this.javaProject.equals(JavaCore.create(sqlFile.getProject()))) {
            return;
        }
        IPath sqlFilePath = sqlFile.getProjectRelativePath();
        for (IResource sourceFolder : JavaProjectUtil
                .getSourceFolders(javaProject)) {
            IPath sourceFolderPath = sourceFolder.getProjectRelativePath();
            if (sqlFilePath.isPrefixOf(sourceFolderPath)) {
                saveSourceFolderPath(sourceFolderPath.toPortableString());
                break;
            }
        }
    }

    protected void saveSourceFolderPath(String sourceFolderPath) {
        IDialogSettings dialogSettings = Domax.getDefault().getDialogSettings();
        IDialogSettings section = dialogSettings
                .getSection(Constants.NewSqlFileDialog.SECTION_NAME);
        if (section == null) {
            section = dialogSettings
                    .addNewSection(Constants.NewSqlFileDialog.SECTION_NAME);
        }
        section.put(Constants.NewSqlFileDialog.SOURCE_FOLDER_PATH_KEY,
                sourceFolderPath);
    }
}
