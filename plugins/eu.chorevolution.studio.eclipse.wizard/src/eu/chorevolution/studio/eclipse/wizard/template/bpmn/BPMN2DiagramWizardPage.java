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
package eu.chorevolution.studio.eclipse.wizard.template.bpmn;

import java.io.File;

import org.eclipse.bpmn2.modeler.core.preferences.Bpmn2Preferences;
import org.eclipse.bpmn2.modeler.core.runtime.TargetRuntime;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil.Bpmn2DiagramType;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import eu.chorevolution.studio.eclipse.core.internal.project.model.impl.ChorevolutionSynthesisProjectStructureFolder;


public class BPMN2DiagramWizardPage extends WizardPage {
	private Text containerText;

	private Text fileText;
	private Text targetNamespaceText;

	private ISelection selection;
	
	private IResource diagramContainer;

	private static final String CHOREOGRAPHY_FILE_EXTENSION = ".bpmn2";
	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public BPMN2DiagramWizardPage(ISelection selection) {
		super(BPMN2DiagramWizardMessages.BPMN2DiagramWizardPage_mainPageName); //$NON-NLS-1$
		setTitle(BPMN2DiagramWizardMessages.BPMN2DiagramWizardPage_title);
		setDescription(BPMN2DiagramWizardMessages.BPMN2DiagramWizardPage_description);
		this.selection = selection;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		Label label = new Label(container, SWT.NULL);
		label.setText(BPMN2DiagramWizardMessages.BPMN2DiagramWizardPage_location_Label);

		containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);
		containerText.setEditable(false);
		containerText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				targetNamespaceText.setText(""); //$NON-NLS-1$
				dialogChanged(true);
			}
		});

		Button button = new Button(container, SWT.PUSH);
		button.setText(BPMN2DiagramWizardMessages.BPMN2DiagramWizardPage_browse_Button);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
				dialogChanged(false);
			}
		});
		label = new Label(container, SWT.NULL);
		label.setText(BPMN2DiagramWizardMessages.BPMN2DiagramWizardPage_file_Name_Label);

		fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		fileText.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		fileText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				dialogChanged(false);
			}
		});

		label = new Label(container, SWT.NULL);
		label.setText(BPMN2DiagramWizardMessages.BPMN2DiagramWizardPage_targetNamespace_Label);

		targetNamespaceText = new Text(container, SWT.BORDER | SWT.SINGLE);
		targetNamespaceText.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		targetNamespaceText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				dialogChanged(false);
			}
		});
		
		updatePageDescription();
		updateFilename();
		dialogChanged(true);
		setControl(container);
	}

	private boolean checkChoreographyDiagram() {

		//i can have only 1 bpmn2 files in Choreography Diagrams Folder
		if(getDiagramContainer().getFullPath().lastSegment().toString().equals(ChorevolutionSynthesisProjectStructureFolder.CHOREOGRAPHY_DIAGRAMS_FOLDER_NAME)) {
			//i have to search for another file with bpmn2 extension
			IPath path = getFileContainer().getProject().getLocation().append(getDiagramContainer().getFullPath().removeFirstSegments(1));
			
			if(searchChoreographyFile(path.toString()) != null) {
				setErrorMessage(BPMN2DiagramWizardMessages.BPMN2DiagramWizardPage_error_Duplicate_Choreography);
				return false;
			}
			
		}
		
		setErrorMessage(null);
		return true;
		
	}

	public Bpmn2DiagramType getDiagramType() {
		return ModelUtil.Bpmn2DiagramType.CHOREOGRAPHY;
	}
		
	/**
	 * Tests if the current workbench selection is a suitable diagramContainer to use.
	 */

	private void updatePageDescription() {
		String descriptionType = BPMN2DiagramWizardMessages.BPMN2DiagramWizardPage_choreography_Diagram;
		setDescription(BPMN2DiagramWizardMessages.BPMN2DiagramWizardPage_filename_Prompt+descriptionType);
	}
	
	private void updateFilename() {
		String fileType = "choreography"; //$NON-NLS-1$
		String filename = fileType+".bpmn2"; //$NON-NLS-1$
		
		IContainer container = getFileContainer();
		if (container!=null) {
			String text = container.getFullPath().toString();
			if (text!=null && !text.equals(getContainerName())) {
				containerText.setText(text);
				checkChoreographyDiagram();
			}
			for (int i=1; ; ++i) {
				filename = fileType+"_" + i + ".bpmn2"; //$NON-NLS-1$ //$NON-NLS-2$
				IResource file = container.findMember(filename);
				if (file==null) {
					break;
				}
			}
		}

		String oldFileText = fileText.getText();
		if (filename!=null && !filename.equals(oldFileText))
			fileText.setText(filename);
	}

	protected IContainer getFileContainer() {
		if (selection != null && selection.isEmpty() == false && selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() == 1) {
				Object obj = ssel.getFirstElement();
				if (obj instanceof IAdaptable) {
					Object res = ((IAdaptable)obj).getAdapter(IResource.class);
					if (res!=null)
						obj = res;
				}
				if (obj instanceof Path) {
					obj = ResourcesPlugin.getWorkspace().getRoot().findMember((Path)obj);
				}
				if (obj instanceof IResource) {
					if (obj instanceof IContainer) {
						return (IContainer) obj;
					} else {
						return ((IResource) obj).getParent();
					}
				}
			}
		}
		return null;
	}
	
	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			updatePageDescription();
			updateFilename();
		}
		super.setVisible(visible);
	}

	/**
	 * Uses the standard diagramContainer selection dialog to choose the new value for the diagramContainer field.
	 */

	private void handleBrowse() {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), ResourcesPlugin.getWorkspace()
				.getRoot(), false, BPMN2DiagramWizardMessages.BPMN2DiagramWizardPage_select_Folder_Title);
		if (dialog.open() == Window.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				selection = new TreeSelection(new TreePath(result));
				containerText.setText(((Path) result[0]).toString());
			}
		}
	}

	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged(boolean initialize) {
		boolean complete = false;
		if (validateContainer()) {
			diagramContainer = getFileContainer();
			if (initialize) {
				
				TargetRuntime rt = Bpmn2Preferences.getInstance(diagramContainer.getProject()).getRuntime();
				String targetNamespace = rt.getRuntimeExtension().getTargetNamespace(getDiagramType());
				if (targetNamespace==null)
					targetNamespace = ""; //$NON-NLS-1$
				
				if (rt!=TargetRuntime.getDefaultRuntime() && !targetNamespace.isEmpty()) {
					// Target Runtime will provide its own target namespace
					if (!targetNamespaceText.getText().equals(targetNamespace)) {
						targetNamespaceText.setText(targetNamespace);
						updateFilename();
					}
				}
				else {
					// The default "None" Target Runtime's target namespace may be edited by user.
					String text = targetNamespaceText.getText();
					if (text==null || text.isEmpty()) {
						targetNamespaceText.setText(targetNamespace);
						updateFilename();
					}
				}

			}
			if (validateFileName() && validateTargetNamespace() && checkChoreographyDiagram()) {
				updateStatus(null);
				complete = true;
			}
		}
		setPageComplete(complete);
	}

	private boolean validateContainer() {
		IContainer container = getFileContainer();
		if (container==null) {
			setErrorMessage(BPMN2DiagramWizardMessages.BPMN2DiagramWizardPage_error_No_Container);
			return false;
		}
		if ((container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			setErrorMessage(BPMN2DiagramWizardMessages.BPMN2DiagramWizardPage_error_No_Folder);
			return false;
		}
		if (!container.isAccessible()) {
			setErrorMessage(BPMN2DiagramWizardMessages.BPMN2DiagramWizardPage_error_Container_Readonly);
			return false;
		}
		return true;
	}
	
	private boolean validateFileName() {
		if (!validateContainer())
			return false;
		
		IContainer container = getFileContainer();
		String fileName = getFileName();
		if (fileName.length() == 0) {
			setErrorMessage(BPMN2DiagramWizardMessages.BPMN2DiagramWizardPage_error_No_Filename);
			return false;
		}
		if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
			setErrorMessage(BPMN2DiagramWizardMessages.BPMN2DiagramWizardPage_error_Filename_Invalid);
			return false;
		}
		int dotLoc = fileName.lastIndexOf('.');
		if (dotLoc != -1) {
			String ext = fileName.substring(dotLoc + 1);
			if (ext.equalsIgnoreCase("bpmn") == false && ext.equalsIgnoreCase("bpmn2") == false) { //$NON-NLS-1$ //$NON-NLS-2$
				setErrorMessage(BPMN2DiagramWizardMessages.BPMN2DiagramWizardPage_error_Extension_Invalid);
				return false;
			}
		}
		else {
			setErrorMessage(BPMN2DiagramWizardMessages.BPMN2DiagramWizardPage_error_No_Extension);
			return false;
		}
		IResource file = container.findMember(fileName);
		if (file!=null) {
			setErrorMessage(NLS.bind(BPMN2DiagramWizardMessages.BPMN2DiagramWizardPage_error_Duplicate_File,fileName));
			return false;
		}
		return true;
	}

	private boolean validateTargetNamespace() {
		String targetNamespace = targetNamespaceText.getText();
		if (targetNamespace==null || targetNamespace.isEmpty()) {
			setErrorMessage(BPMN2DiagramWizardMessages.BPMN2DiagramWizardPage_error_No_TargetNamespace);
			return false;
		}
		URI uri = URI.createURI(targetNamespace);
		if (!(uri.hasAuthority() && uri.scheme()!=null)) {
			setErrorMessage(BPMN2DiagramWizardMessages.BPMN2DiagramWizardPage_error_Invalid_TargetNamespace);
			return false;
		}
		return true;
	}
	
	@Override
	public boolean isPageComplete() {
		return validateContainer() &&
				validateFileName() &&
				validateTargetNamespace() &&
				checkChoreographyDiagram();
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getContainerName() {
		return containerText.getText();
	}

	public String getFileName() {
		return fileText.getText();
	}

	public IResource getDiagramContainer() {
		return diagramContainer;
	}

	public String getTargetNamespace() {
		return targetNamespaceText.getText();
	}
	
   public String searchChoreographyFile(String dirName){
	   File dir = new File(dirName);
	   for (File file : dir.listFiles()) {
	     if (file.getName().endsWith((CHOREOGRAPHY_FILE_EXTENSION))) {
	       return file.getAbsolutePath().toString();
	     }
	   }
	   return null;
   }

}
