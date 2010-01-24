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

import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.JavaModelException;

public final class AnnotationUtil {

    public static boolean isExistent(IAnnotatable annotatable,
            String annotationQualifiedName) {
        String simpleName = ClassUtil.getSimpleName(annotationQualifiedName);
        try {
            for (IAnnotation annotation : annotatable.getAnnotations()) {
                String elementName = annotation.getElementName();
                if (annotationQualifiedName.equals(annotation.getElementName())
                        || simpleName.equals(elementName)) {
                    return true;
                }
            }
        } catch (JavaModelException e) {
        }
        return false;
    }
}
