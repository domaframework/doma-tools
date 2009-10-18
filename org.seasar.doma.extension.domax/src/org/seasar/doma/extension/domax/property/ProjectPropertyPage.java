package org.seasar.doma.extension.domax.property;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

public class ProjectPropertyPage extends PropertyPage {

	/**
	 * Constructor for SamplePropertyPage.
	 */
	public ProjectPropertyPage() {
		super();
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);

		return composite;
	}

	// private Composite createDefaultComposite(Composite parent) {
	// Composite composite = new Composite(parent, SWT.NULL);
	// GridLayout layout = new GridLayout();
	// layout.numColumns = 1;
	// composite.setLayout(layout);
	//
	// GridData data = new GridData();
	// data.verticalAlignment = GridData.FILL;
	// data.horizontalAlignment = GridData.FILL;
	// data.horizontalSpan = 2;
	// composite.setLayoutData(data);
	//
	// return composite;
	// }

	@Override
	protected void performDefaults() {
	}

	@Override
	public boolean performOk() {
		return true;
	}

	// private IProject getProject() {
	// IProject project = (IProject) getElement().getAdapter(IProject.class);
	// if (project == null) {
	// AssertionUtil.assertNotNull(project);
	// }
	// return project;
	// }
}