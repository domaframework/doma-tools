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
package org.seasar.doma.extension.domax.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.seasar.doma.extension.domax.ClassConstants;
import org.seasar.doma.extension.domax.Constants;
import org.seasar.doma.extension.domax.util.AnnotationUtil;
import org.seasar.doma.extension.domax.util.JavaProjectUtil;

public class ResourceFileFactory {

    public static ResourceFile createResourceFile(IType type) {
        if (!AnnotationUtil.isExistent(type, ClassConstants.Dao)) {
            return null;
        }
        String className = type.getFullyQualifiedName();
        IFolder folder = findFolder(type.getJavaProject(), className);
        if (folder == null) {
            return null;
        }
        try {
            for (IResource child : folder.members()) {
                IFile file = (IFile) child.getAdapter(IFile.class);
                if (file == null) {
                    continue;
                }
                String extension = file.getFileExtension();
                if (Constants.SQL_FILE_EXTESION.equals(extension)
                        && file.exists()) {
                    return new SqlFile(file);
                } else if (Constants.SCRIPT_FILE_EXTESION.equals(extension)
                        && file.exists()) {
                    return new ScriptFile(file);
                }
            }
        } catch (CoreException ignored) {
        }
        return null;
    }

    public static ResourceFile createResourceFile(IType type, IMethod method) {
        if (!AnnotationUtil.isExistent(type, ClassConstants.Dao)) {
            return null;
        }
        IJavaProject javaProject = type.getJavaProject();
        IPath path = Path.fromPortableString(
                type.getFullyQualifiedName().replace(".", "/")).append(
                method.getElementName());
        if (AnnotationUtil.isExistent(method, ClassConstants.Script)) {
            IFile file = findFile(javaProject, path,
                    Constants.SCRIPT_FILE_EXTESION);
            return new ScriptFile(file);
        }
        IFile file = findFile(javaProject, path, Constants.SQL_FILE_EXTESION);
        return new SqlFile(file);
    }

    private static IFile findFile(IJavaProject javaProject, IPath path,
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

    private static IFolder findFolder(IJavaProject javaProject, String className) {
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

}
