package eu.chorevolution.studio.eclipse.ui.handlers.wizard.page;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;


import org.eclipse.jdt.internal.debug.ui.jres.JREsPreferencePage;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import eu.chorevolution.studio.eclipse.core.ChorevolutionCoreUtils;

public class ChoreographyDeploymentDescriptorJDKWizardPage extends WizardPage {
    
	private Composite container;
	
	public ChoreographyDeploymentDescriptorJDKWizardPage(String pageName) {
		super(pageName);
        setTitle("A JDK must be selected to proceed");
	}

	@Override
	public void createControl(Composite parent) {

		container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 1;
        
        Label labelText = new Label(container, SWT.NONE);
        labelText.setText("A JRE is selected instead of a JRE. Please select a JDK to proceed.");
	

		Link preferencesLink= new Link(container, SWT.NONE);
		preferencesLink.setFont(container.getFont());
		preferencesLink.setText("Use default JDK (currently '"+ChorevolutionCoreUtils.getDefaultJVMName()+"') <a>Configure...</a>");
		preferencesLink.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				ChorevolutionCoreUtils.showPrefPage(JREsPreferencePage.ID);

				//if JDK let the user proceed, else not			
				if(ChorevolutionCoreUtils.isJDK(ChorevolutionCoreUtils.getDefaultJVMPath())) {
					preferencesLink.setText("JDK being used: '"+ChorevolutionCoreUtils.getDefaultJVMName()+"'");
					setPageComplete(true);
				}
				else {
					preferencesLink.setText("Use default JDK (currently '"+ChorevolutionCoreUtils.getDefaultJVMName()+"') <a>Configure...</a>");

				}
						
			}
		});		
        
        
        setControl(container);
        setPageComplete(false);
		
	}
	



	

	
	
	
}
