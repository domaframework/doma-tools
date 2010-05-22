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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

/**
 * @author taedium
 * 
 */
public class ScriptFileNewWizard extends ResourceFileNewWizard implements
        INewWizard {

    public ScriptFileNewWizard(IContainer candidateContainer,
            String candidatelFileName) {
        super(candidateContainer, candidatelFileName);
        setWindowTitle("New Script File");
    }

    @Override
    public void addPages() {
        page = new WizardNewFileCreationPage("ScriptFileCreate",
                new StructuredSelection(candidateContainer));
        page.setFileName(candidatelFileName);
        page.setTitle("Script File");
        page.setDescription("Create a new script file.");
        addPage(page);
    }

}
