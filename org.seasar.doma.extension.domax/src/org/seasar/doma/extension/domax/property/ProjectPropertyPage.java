package org.seasar.doma.extension.domax.property;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;
import org.seasar.doma.extension.domax.DomaxNature;
import org.seasar.doma.extension.domax.DomaxNullPointerException;

public class ProjectPropertyPage extends PropertyPage {

	private Button projectButton;

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

		this.projectButton = new Button(createDefaultComposite(composite),
				SWT.CHECK);
		this.projectButton.setText("Doma Project");
		if (hasNature()) {
			this.projectButton.setSelection(true);
		}
		return composite;
	}

	private boolean hasNature() {
		try {
			return getProject().hasNature(DomaxNature.NATURE_ID);
		} catch (CoreException ignored) {
		}
		return false;
	}

	private Composite createDefaultComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);

		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan = 2;
		composite.setLayoutData(data);

		return composite;
	}

	@Override
	protected void performDefaults() {
		removeNature();
		this.projectButton.setSelection(false);
	}

	@Override
	public boolean performOk() {
		if (projectButton.getSelection()) {
			addNature();
		} else {
			removeNature();
		}
		return true;
	}

	private void addNature() {
		IProject project = getProject();
		try {
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();

			for (int i = 0; i < natures.length; ++i) {
				if (DomaxNature.NATURE_ID.equals(natures[i])) {
					return;
				}
			}

			// Add the nature
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = DomaxNature.NATURE_ID;
			description.setNatureIds(newNatures);
			project.setDescription(description, null);
		} catch (CoreException e) {
		}
	}

	private void removeNature() {
		IProject project = getProject();
		try {
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();

			for (int i = 0; i < natures.length; ++i) {
				if (DomaxNature.NATURE_ID.equals(natures[i])) {
					// Remove the nature
					String[] newNatures = new String[natures.length - 1];
					System.arraycopy(natures, 0, newNatures, 0, i);
					System.arraycopy(natures, i + 1, newNatures, i,
							natures.length - i - 1);
					description.setNatureIds(newNatures);
					project.setDescription(description, null);
					return;
				}
			}
		} catch (CoreException e) {
		}
	}

	private IProject getProject() {
		IProject project = (IProject) getElement().getAdapter(IProject.class);
		if (project == null) {
			throw new DomaxNullPointerException("project");
		}
		return project;
	}
}