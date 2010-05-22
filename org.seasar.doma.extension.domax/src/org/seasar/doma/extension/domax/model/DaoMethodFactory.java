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

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.seasar.doma.extension.domax.Constants;
import org.seasar.doma.extension.domax.util.AssertionUtil;
import org.seasar.doma.extension.domax.util.JavaProjectUtil;

/**
 * @author taedium
 * 
 */
public class DaoMethodFactory {

    public DaoMethod createDaoMethod(IFile file) {
        AssertionUtil.assertNotNull(file);

        IJavaProject javaProject = JavaCore.create(file.getProject());
        if (javaProject == null) {
            return null;
        }
        List<IResource> sourceFolders = JavaProjectUtil
                .getSourceFolders(javaProject);
        IPath filePath = file.getProjectRelativePath();
        for (IResource sourceFolder : sourceFolders) {
            IPath metaInfPath = sourceFolder.getProjectRelativePath().append(
                    Constants.META_INF);
            if (metaInfPath.isPrefixOf(filePath)) {
                IPath path = filePath.removeFirstSegments(
                        metaInfPath.segmentCount()).removeFileExtension();
                if (path.segmentCount() < 2) {
                    continue;
                }
                String className = path.removeLastSegments(1)
                        .toPortableString().replace("/", ".");
                String methodName = path.lastSegment();
                int pos = methodName.indexOf("-");
                if (pos > 0) {
                    methodName = methodName.substring(0, pos);
                }
                return new DaoMethod(javaProject, className, methodName);
            }
        }
        return null;
    }

}
