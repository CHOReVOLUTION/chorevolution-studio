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
import eu.chorevolution.studio.eclipse.core.preferences.ChorevolutionServiceThingSourceModelPrefs;
import eu.chorevolution.studio.eclipse.core.utils.Tree;
import eu.chorevolution.studio.eclipse.core.utils.TreeNode;

public class ChorevolutionServiceThingProjectStructureFolder implements ChorevolutionStructureFolder {

	private List<Tree<String>> rootFolders;

	public ChorevolutionServiceThingProjectStructureFolder(Map<String, String> settings) {
		rootFolders = new ArrayList<Tree<String>>();
		init(settings);
	}

	private void init(Map<String, String> settings) {
		// add Interface Description
		Tree<String> interfaceDescription = new Tree<String>();
		interfaceDescription.setRootElement(new TreeNode<String>(
				settings.get(ChorevolutionServiceThingSourceModelPrefs.PREF_SERVICE_THING_INTERFACE_DESCRIPTION)));
		rootFolders.add(interfaceDescription);

		// add Interaction Protocol Description
		Tree<String> interactionprotocolDescription = new Tree<String>();
		interactionprotocolDescription.setRootElement(new TreeNode<String>(settings
				.get(ChorevolutionServiceThingSourceModelPrefs.PREF_SERVICE_THING_INTERACTIONPROTOCOL_DESCRIPTION)));
		rootFolders.add(interactionprotocolDescription);

		// add QoS Description
		/*Tree<String> qosDescription = new Tree<String>();
		qosDescription.setRootElement(new TreeNode<String>(settings.get(ChorevolutionServiceThingSourceModelPrefs.PREF_SERVICE_THING_QOS_DESCRIPTION)));
		rootFolders.add(qosDescription);
		*/
		// add Identity Description
		/*Tree<String> identityDescription = new Tree<String>();
		identityDescription.setRootElement(new TreeNode<String>(settings.get(ChorevolutionServiceThingSourceModelPrefs.PREF_SERVICE_THING_IDENTITY_DESCRIPTION)));
		rootFolders.add(identityDescription);
		*/

		// add Security Description
		Tree<String> securityDescription = new Tree<String>();
		securityDescription.setRootElement(new TreeNode<String>(
				settings.get(ChorevolutionServiceThingSourceModelPrefs.PREF_SERVICE_THING_SECURITY_DESCRIPTION)));
		rootFolders.add(securityDescription);
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

}
