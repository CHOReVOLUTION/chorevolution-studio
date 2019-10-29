package eu.chorevolution.studio.eclipse.ui.handlers.wizard;

import org.eclipse.jface.wizard.Wizard;

import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIMessages;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.page.ChoreographyDeploymentDescriptorJDKWizardPage;

public class ChoreographyDeploymentDescriptorJDKWizard extends Wizard {

	private ChoreographyDeploymentDescriptorJDKWizardPage wizardPage = new ChoreographyDeploymentDescriptorJDKWizardPage("A JDK must be selected to proceed");
	
	public ChoreographyDeploymentDescriptorJDKWizard() {
		this.setWindowTitle(ChorevolutionUIMessages.TransformatorORGeneator_Information);
	}
	
	@Override
	public void addPages() {
		  addPage(wizardPage);
	}
	
	@Override
	public boolean performFinish() {

		return true;
	}

}
