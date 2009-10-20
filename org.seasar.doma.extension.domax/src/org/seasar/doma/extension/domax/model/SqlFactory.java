package org.seasar.doma.extension.domax.model;

import org.eclipse.jdt.core.IMethod;

public class SqlFactory {

    public Sql createSql(IMethod method) {
        method.getAnnotation("Select");

        return null;
    }
}
