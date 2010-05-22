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
package org.seasar.doma.extension.domax.wizard;

import org.eclipse.core.resources.IContainer;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.swt.widgets.Shell;
import org.seasar.doma.extension.domax.Constants;

public class SqlFileNewWizardDialogOpener extends
        ResourceFileNewWizardDialogOpener {

    public SqlFileNewWizardDialogOpener(IType type, IMethod method, Shell shell) {
        super(type, method, shell);
    }

    @Override
    protected ResourceFileNewWizard createFileNewWizard() {
        IContainer sqlFileContainer = createSqlFileContainer();
        String sqlFileName = methodName + "." + Constants.SQL_FILE_EXTESION;
        return new SqlFileNewWizard(sqlFileContainer, sqlFileName);
    }

}
