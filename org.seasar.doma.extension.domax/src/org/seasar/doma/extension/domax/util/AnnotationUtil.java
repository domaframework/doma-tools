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
