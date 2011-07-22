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
package org.seasar.doma.extension.domax.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.seasar.doma.extension.domax.Logger;

public final class JavaProjectUtil {

    public static List<IResource> getSourceFolders(IJavaProject javaProject) {
        List<IResource> results = new ArrayList<IResource>();
        try {
            IPackageFragmentRoot[] roots = javaProject
                    .getAllPackageFragmentRoots();
            for (IPackageFragmentRoot root : roots) {
                if (root.getJavaProject() == javaProject
                        && root.getKind() == IPackageFragmentRoot.K_SOURCE) {
                    results.add(root.getCorrespondingResource());
                }
            }
        } catch (JavaModelException e) {
            Logger.error(e);
        }
        return results;
    }
}
