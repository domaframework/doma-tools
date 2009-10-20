/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.seasar.doma.extension.domax.util.AssertionUtil;

/**
 * @author taedium
 * 
 */
public class NewSqlFileWizard extends Wizard implements INewWizard {

    private WizardNewFileCreationPage page;

    private IContainer candidateContainer;

    private String candidatelFileName;

    private IFile newFile;

    public NewSqlFileWizard(IContainer candidateContainer,
            String candidatelFileName) {
        AssertionUtil.assertNotNull(candidateContainer, candidatelFileName);
        this.candidateContainer = candidateContainer;
        this.candidatelFileName = candidatelFileName;
        setWindowTitle("New SQL File");
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
    }

    @Override
    public boolean performFinish() {
        newFile = page.createNewFile();
        return true;
    }

    @Override
    public void addPages() {
        page = new WizardNewFileCreationPage("SqlFileCreate",
                new StructuredSelection(candidateContainer));
        page.setFileName(candidatelFileName);
        page.setTitle("SQL File");
        page.setDescription("Create a new sql file.");
        addPage(page);
    }

    public IFile getNewFile() {
        return newFile;
    }

}
