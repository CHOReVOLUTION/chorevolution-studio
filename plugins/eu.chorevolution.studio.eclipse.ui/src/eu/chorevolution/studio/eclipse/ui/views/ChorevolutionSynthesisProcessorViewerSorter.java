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
package eu.chorevolution.studio.eclipse.ui.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import eu.chorevolution.studio.eclipse.ui.views.model.SynthesisProcessorStep;

public class ChorevolutionSynthesisProcessorViewerSorter extends ViewerSorter {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if ((e1 instanceof SynthesisProcessorStep) && (e2 instanceof SynthesisProcessorStep)) {
			return ((SynthesisProcessorStep) e1).getStepNumber().compareTo(((SynthesisProcessorStep) e2).getStepNumber());
		} else {
			return super.compare(viewer, e1, e2);
		}
	}
}