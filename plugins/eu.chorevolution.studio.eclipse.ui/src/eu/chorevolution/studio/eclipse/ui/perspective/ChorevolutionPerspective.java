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
package eu.chorevolution.studio.eclipse.ui.perspective;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.progress.IProgressConstants;
import org.eclipse.ui.texteditor.templates.TemplatesView;


public class ChorevolutionPerspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();

		// Left
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.25f, editorArea); //$NON-NLS-1$
		left.addView(JavaUI.ID_PACKAGES);
		left.addPlaceholder(JavaUI.ID_TYPE_HIERARCHY);
		left.addPlaceholder("org.eclipse.ui.views.ResourceNavigator");
		left.addPlaceholder("org.eclipse.ui.navigator.ProjectExplorer");
		left.addPlaceholder("org.eclipse.ui.navigator.ProjectExplorer");
		
		// Left Bottom
		IFolderLayout left_bottom = layout.createFolder("left_bottom", IPageLayout.BOTTOM, 0.80f, "left");
		left_bottom.addView("org.eclipse.wst.server.ui.ServersView");

		// Right
		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, 0.80f, editorArea); //$NON-NLS-1$
		right.addView(IPageLayout.ID_OUTLINE);
		right.addPlaceholder(TemplatesView.ID);
		
		// Right Bottom
		IFolderLayout right_bottom = layout.createFolder("right_bottom", IPageLayout.BOTTOM, 0.80f, "right");
		//chorevolutionFolder.addView("eu.chorevolution.studio.eclipse.ui.navigator.chorevolutionExplorer");

		// Bottom		
		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.80f, editorArea); //$NON-NLS-1$
		bottom.addView(IConsoleConstants.ID_CONSOLE_VIEW);
		bottom.addView("org.eclipse.ui.views.AllMarkersView");
		bottom.addView(IProgressConstants.PROGRESS_VIEW_ID);
		bottom.addView(IPageLayout.ID_PROP_SHEET);
		//outputfolder.addView("eu.chorevolution.studio.eclipse.ui.views.synthesisprocessor");
		bottom.addPlaceholder(IPageLayout.ID_PROBLEM_VIEW);
		bottom.addPlaceholder(IPageLayout.ID_TASK_LIST);
		bottom.addPlaceholder(JavaUI.ID_JAVADOC_VIEW);
		bottom.addPlaceholder(JavaUI.ID_SOURCE_VIEW);
		bottom.addPlaceholder(NewSearchUI.SEARCH_VIEW_ID);
		bottom.addPlaceholder(IPageLayout.ID_BOOKMARKS);
		bottom.addPlaceholder("*");
		
		
		// Layout
		layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
		layout.addActionSet(JavaUI.ID_ACTION_SET);
		layout.addActionSet(JavaUI.ID_ELEMENT_CREATION_ACTION_SET);
		layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);

		// views - java
		layout.addShowViewShortcut(JavaUI.ID_PACKAGES);
		layout.addShowViewShortcut(JavaUI.ID_TYPE_HIERARCHY);
		layout.addShowViewShortcut(JavaUI.ID_SOURCE_VIEW);
		layout.addShowViewShortcut(JavaUI.ID_JAVADOC_VIEW);

		// views - search
		layout.addShowViewShortcut(NewSearchUI.SEARCH_VIEW_ID);

		// views - debugging
		layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);

		// views - standard workbench
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		// layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
		layout.addShowViewShortcut("org.eclipse.ui.views.AllMarkersView");
		layout.addShowViewShortcut(IPageLayout.ID_PROJECT_EXPLORER);
		// layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);
		layout.addShowViewShortcut(IProgressConstants.PROGRESS_VIEW_ID);
		layout.addShowViewShortcut(TemplatesView.ID);

		
		// new files
		//layout.addNewWizardShortcut("eu.chorevolution.modelingnotations.clts.presentation.CltsModelWizardID");
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewPackageCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewClassCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewInterfaceCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewEnumCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewAnnotationCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.junit.wizards.NewTestCaseCreationWizard");//$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewSourceFolderCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewSnippetFileCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewJavaWorkingSetWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");//$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");//$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.ui.editors.wizards.UntitledTextFileWizard");//$NON-NLS-1$

		// new projects
		layout.addNewWizardShortcut("eu.chorevolution.studio.eclipse.wizard.template.chorevolutionsynthesis");
		layout.addNewWizardShortcut("eu.chorevolution.studio.eclipse.wizard.template.chorevolutionservicething");
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.JavaProjectWizard"); //$NON-NLS-1$
	
		layout.addNewWizardShortcut("org.grails.ide.eclipse.ui.wizard.newGrailsProjectWizard");
		layout.addNewWizardShortcut("org.eclipse.wst.web.ui.internal.wizards.SimpleWebProjectWizard");
		layout.addNewWizardShortcut("org.eclipse.jst.servlet.ui.project.facet.WebProjectWizard");

		// new perspectives
		layout.addPerspectiveShortcut("org.eclipse.jdt.ui.JavaPerspective");
		layout.addPerspectiveShortcut("org.eclipse.debug.ui.DebugPerspective");
		layout.addPerspectiveShortcut("org.eclipse.jdt.ui.JavaHierarchyPerspective");
		layout.addPerspectiveShortcut("org.eclipse.jdt.ui.JavaBrowsingPerspective");
		layout.addPerspectiveShortcut("org.grails.ide.eclipse.perspective");



	}
}