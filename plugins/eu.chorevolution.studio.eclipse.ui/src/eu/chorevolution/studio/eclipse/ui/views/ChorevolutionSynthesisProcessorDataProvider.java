package eu.chorevolution.studio.eclipse.ui.views;

import java.util.ArrayList;
import java.util.List;

import eu.chorevolution.studio.eclipse.ui.views.model.StatusTypesSynthesisProcessor;
import eu.chorevolution.studio.eclipse.ui.views.model.StepTypeSynthesisProcessor;
import eu.chorevolution.studio.eclipse.ui.views.model.SynthesisProcessorStep;

public enum ChorevolutionSynthesisProcessorDataProvider {
	INSTANCE;

	private List<SynthesisProcessorStep> manualStepsSynthesisProcessor;
	private List<SynthesisProcessorStep> automaticStepsSynthesisProcessor;

	private ChorevolutionSynthesisProcessorDataProvider() {
		manualStepsSynthesisProcessor = new ArrayList<SynthesisProcessorStep>();
		manualStepsSynthesisProcessor.add(new SynthesisProcessorStep(StatusTypesSynthesisProcessor.UNPERFORMED, StepTypeSynthesisProcessor.VALIDATE_BPMN.getName(), StepTypeSynthesisProcessor.VALIDATE_BPMN.getNumber()));
		manualStepsSynthesisProcessor.add(new SynthesisProcessorStep(StatusTypesSynthesisProcessor.UNPERFORMED, StepTypeSynthesisProcessor.CHOREOGRAPHY_PROJECTION.getName(), StepTypeSynthesisProcessor.CHOREOGRAPHY_PROJECTION.getNumber()));
		manualStepsSynthesisProcessor.add(new SynthesisProcessorStep(StatusTypesSynthesisProcessor.UNPERFORMED, StepTypeSynthesisProcessor.SELECTION.getName(), StepTypeSynthesisProcessor.SELECTION.getNumber()));
		manualStepsSynthesisProcessor.add(new SynthesisProcessorStep(StatusTypesSynthesisProcessor.UNPERFORMED, StepTypeSynthesisProcessor.BC_GENERATION.getName(), StepTypeSynthesisProcessor.BC_GENERATION.getNumber()));
		manualStepsSynthesisProcessor.add(new SynthesisProcessorStep(StatusTypesSynthesisProcessor.UNPERFORMED, StepTypeSynthesisProcessor.SF_GENERATION.getName(), StepTypeSynthesisProcessor.SF_GENERATION.getNumber()));
		manualStepsSynthesisProcessor.add(new SynthesisProcessorStep(StatusTypesSynthesisProcessor.UNPERFORMED, StepTypeSynthesisProcessor.ADAPTER_GENERATION.getName(), StepTypeSynthesisProcessor.ADAPTER_GENERATION.getNumber()));
		manualStepsSynthesisProcessor.add(new SynthesisProcessorStep(StatusTypesSynthesisProcessor.UNPERFORMED, StepTypeSynthesisProcessor.CD_GENERATION.getName(), StepTypeSynthesisProcessor.CD_GENERATION.getNumber()));
		
		automaticStepsSynthesisProcessor = new ArrayList<SynthesisProcessorStep>();
		automaticStepsSynthesisProcessor.add(new SynthesisProcessorStep(StatusTypesSynthesisProcessor.UNPERFORMED, StepTypeSynthesisProcessor.AUTOMATIC.getName(), StepTypeSynthesisProcessor.AUTOMATIC.getNumber()));
	
	}

	public List<SynthesisProcessorStep> getManualStepsSynthesisProcessor() {
		return manualStepsSynthesisProcessor;
	}

	public List<SynthesisProcessorStep> getAutomaticStepsSynthesisProcessor() {
		return automaticStepsSynthesisProcessor;
	}
	
	public List<SynthesisProcessorStep> getEmptyStepsSynthesisProcessor() {
		return new ArrayList<SynthesisProcessorStep>();
	}
	
}
