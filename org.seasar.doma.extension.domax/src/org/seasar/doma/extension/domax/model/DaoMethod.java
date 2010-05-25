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

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.PartInitException;
import org.seasar.doma.extension.domax.Logger;
import org.seasar.doma.extension.domax.util.AssertionUtil;

/**
 * 
 * @author taedium
 * 
 */
public class DaoMethod {

    private final IJavaProject javaProject;

    private final String className;

    private final String methodName;

    /**
     * @param className
     * @param methodName
     */
    protected DaoMethod(IJavaProject javaProject, String className,
            String methodName) {
        AssertionUtil.assertNotNull(javaProject, className, methodName);
        this.javaProject = javaProject;
        this.className = className;
        this.methodName = methodName;
    }

    public IJavaProject getJavaProject() {
        return javaProject;
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @return the methodName
     */
    public String getMethodName() {
        return methodName;
    }

    public void openInEditor() {
        try {
            IType type = javaProject.findType(className);
            if (type == null) {
                return;
            }
            for (IMethod method : type.getMethods()) {
                if (method.getElementName().equals(methodName)) {
                    JavaUI.openInEditor(method);
                    return;
                }
            }
            JavaUI.openInEditor(type);
        } catch (JavaModelException e) {
            Logger.error(e);
        } catch (PartInitException e) {
            Logger.error(e);
        }
    }

}
