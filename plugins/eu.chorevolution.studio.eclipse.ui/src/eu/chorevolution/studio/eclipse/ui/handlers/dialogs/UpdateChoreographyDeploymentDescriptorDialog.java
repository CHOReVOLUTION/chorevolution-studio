/*
  * Copyright 2015 The CHOReVOLUTION project
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
package eu.chorevolution.studio.eclipse.ui.handlers.dialogs;

import java.util.Map;
import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionPreferenceData;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionServicesURIPrefs;
import eu.chorevolution.studio.eclipse.core.utils.syncope.EnactmentEngine;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIMessages;

public class UpdateChoreographyDeploymentDescriptorDialog extends TitleAreaDialog {
	// private Image image;

	private Text textServiceInventoryURL;
	private Text textServiceInventoryUsername;
	private Text textServiceInventoryPassword;
	private Text textServiceInventoryDomanin;

	private Map<String, ChorevolutionPreferenceData> propertyValues;
	private IProject project;

	public UpdateChoreographyDeploymentDescriptorDialog(Shell shell, IProject project) {
		super(shell);
		this.project = project;
		new Vector<EnactmentEngine>();
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(ChorevolutionUIMessages.ChorevolutionSynthesisProcessor_title);

	}

	/*
	 * public boolean close() { if (image != null) image.dispose(); return
	 * super.close(); }
	 */
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);

		setTitle("Update Choreography");

		setMessage("Update Choreography.", IMessageProvider.INFORMATION);

		/*
		 * if (image != null) setTitleImage(image);
		 */

		return contents;
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 3;
		layout.marginWidth = 3;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Connect to the Service inventory
		Group groupConnectToInventory = new Group(composite, SWT.SHADOW_ETCHED_IN);
		groupConnectToInventory.setText("");
		groupConnectToInventory.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1));
		GridLayout layoutServiceInventory = new GridLayout();
		layoutServiceInventory.marginHeight = 3;
		layoutServiceInventory.marginWidth = 3;
		layoutServiceInventory.numColumns = 2;
		groupConnectToInventory.setLayout(layoutServiceInventory);

		Label labelServiceInventoryURL = new Label(groupConnectToInventory, SWT.NONE);
		labelServiceInventoryURL.setText("Service Inventory URI:");

		propertyValues = (new ChorevolutionServicesURIPrefs()).readProjectOrWorkspacePreferences(project);
		textServiceInventoryURL = new Text(groupConnectToInventory, SWT.BORDER);
		textServiceInventoryURL.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textServiceInventoryURL
				.setText(propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_URI).getValue());

		Label labelServiceInventoryUsername = new Label(groupConnectToInventory, SWT.NONE);
		labelServiceInventoryUsername.setText("Username:");
		textServiceInventoryUsername = new Text(groupConnectToInventory, SWT.BORDER);
		textServiceInventoryUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textServiceInventoryUsername
				.setText(propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_USERNAME).getValue());

		Label labelServiceInventoryPassword = new Label(groupConnectToInventory, SWT.NONE);
		labelServiceInventoryPassword.setText("Password:");
		textServiceInventoryPassword = new Text(groupConnectToInventory, SWT.BORDER);
		textServiceInventoryPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textServiceInventoryPassword
				.setText(propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_PASSWORD).getValue());

		Label labelServiceInventoryDomain = new Label(groupConnectToInventory, SWT.NONE);
		labelServiceInventoryDomain.setText("Domain:");
		textServiceInventoryDomanin = new Text(groupConnectToInventory, SWT.BORDER);
		textServiceInventoryDomanin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textServiceInventoryDomanin
				.setText(propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_DOMAIN).getValue());
		
		
		return composite;
	}


	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
		getButton(IDialogConstants.OK_ID).setEnabled(true);
	}

	public String getServiceInventoryURL() {
		return propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_URI).getValue();
	}

	public String getServiceInventoryUsername() {
		return propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_USERNAME).getValue();
	}

	public String getServiceInventoryPassword() {
		return propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_PASSWORD).getValue();
	}

	public String getServiceInventoryDomain() {
		return propertyValues.get(ChorevolutionServicesURIPrefs.PREF_APACHE_SYNCOPE_DOMAIN).getValue();
	}
	

	@Override
	protected boolean isResizable() {
		return true;
	}
}
