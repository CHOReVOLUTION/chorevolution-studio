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
package eu.chorevolution.studio.eclipse.core.internal.project.model.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import eu.chorevolution.studio.eclipse.core.ChorevolutionCoreUtils;
import eu.chorevolution.studio.eclipse.core.internal.project.model.ChorevolutionStructureFolder;
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionSynthesisSourceModelPrefs;
import eu.chorevolution.studio.eclipse.core.utils.Tree;
import eu.chorevolution.studio.eclipse.core.utils.TreeNode;

public class ChorevolutionSynthesisProjectStructureFolder implements ChorevolutionStructureFolder {

	public static final String MODELS_FOLDER_NAME = "model";
	public static final String ARTIFACTS_FOLDER_NAME = "artifact";
	public static final String CHOREOGRAPHY_DIAGRAMS_FOLDER_NAME = "Choreography Diagram";
	//public static final String ADDITIONAL_MODELS_FOLDER_NAME = "Additional Models";
	public static final String SYNTHESIS_PROCESSOR_FOLDER_NAME = "Synthesis Processor";
	

	private List<Tree<String>> rootFolders;

	public ChorevolutionSynthesisProjectStructureFolder(Map<String, String> settings) {
		rootFolders = new ArrayList<Tree<String>>();
		init(settings);
	}

	private void init(Map<String, String> settings) {
		// add Choreography Diagrams
		Tree<String> choreographyDiagrams = new Tree<String>();
		choreographyDiagrams.setRootElement(new TreeNode<String>(CHOREOGRAPHY_DIAGRAMS_FOLDER_NAME));
		rootFolders.add(choreographyDiagrams);

		// add Additional Models
		/*Tree<String> additionalModels = new Tree<String>();
		TreeNode<String> additionalModelsRoot = new TreeNode<String>(ADDITIONAL_MODELS_FOLDER_NAME);
		additionalModels.setRootElement(additionalModelsRoot);
		additionalModelsRoot.addChild(new TreeNode<String>(
				settings.get(ChorevolutionSynthesisSourceModelPrefs.PREF_ADDITIONALMODELS_VARIABILITY)));
		additionalModelsRoot.addChild(
				new TreeNode<String>(settings.get(ChorevolutionSynthesisSourceModelPrefs.PREF_ADDITIONALMODELS_QOS)));
		additionalModelsRoot.addChild(new TreeNode<String>(
				settings.get(ChorevolutionSynthesisSourceModelPrefs.PREF_ADDITIONALMODELS_IDENTITY)));
		rootFolders.add(additionalModels);
		 */
		//synthesisProcessorRoot.addChild(addModelAndArtifactTreeNode(settings.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_ADAPTER)));

		
		// add Synthesis Processor Models
		Tree<String> synthesisProcessor = new Tree<String>();
		TreeNode<String> synthesisProcessorRoot = new TreeNode<String>(SYNTHESIS_PROCESSOR_FOLDER_NAME);
		synthesisProcessor.setRootElement(synthesisProcessorRoot);
		synthesisProcessorRoot.addChild(new TreeNode<String>(settings.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_COORD)));
		synthesisProcessorRoot.addChild(new TreeNode<String>(settings.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_ADAPTER)));
		synthesisProcessorRoot.addChild(new TreeNode<String>(settings.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_SECURITYFILTER)));
		synthesisProcessorRoot.addChild(new TreeNode<String>(settings.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_BINDINGCOMPONENT)));
		synthesisProcessorRoot.addChild(new TreeNode<String>(settings.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SYNTHESISPROCESSOR_ARCHITECTURALSTYLE)));
		rootFolders.add(synthesisProcessor);

		// add Choreography Specification
		Tree<String> choreographySpecification = new Tree<String>();
		choreographySpecification.setRootElement(
				new TreeNode<String>(settings.get(ChorevolutionSynthesisSourceModelPrefs.PREF_CHOREOGRAPHYDEPLOYMENT)));
		rootFolders.add(choreographySpecification);

		// add Services
		Tree<String> services = new Tree<String>();
		services.setRootElement(new TreeNode<String>(
				settings.get(ChorevolutionSynthesisSourceModelPrefs.PREF_SERVICEINVENTORY_SERVICES)));
		rootFolders.add(services);

	}

	@Override
	public void createChorevolutionProjectStructure(IContainer container) throws CoreException {
		for (Tree<String> root : rootFolders) {
			ChorevolutionCoreUtils.createFolder(root.getRootElement().data, container);
			createChorevolutionProjectStructureChildren(container, root.getRootElement().children,
					new Path(root.getRootElement().data));
		}

	}

	private void createChorevolutionProjectStructureChildren(IContainer container, List<TreeNode<String>> children,
			IPath parent) throws CoreException {
		if (children != null) {
			for (TreeNode<String> child : children) {
				IPath actualPath = parent.append(child.data);
				ChorevolutionCoreUtils.createFolder(actualPath.toOSString(), container);
				createChorevolutionProjectStructureChildren(container, child.children, actualPath);
			}
		}
	}

	private TreeNode<String> addModelAndArtifactTreeNode(String nodeName) {
		TreeNode<String> root = new TreeNode<String>(nodeName);
		root.addChild(new TreeNode<String>(MODELS_FOLDER_NAME));
		root.addChild(new TreeNode<String>(ARTIFACTS_FOLDER_NAME));
		return root;
	}
	
}
