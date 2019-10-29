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

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.text.BadLocationException;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.text.IMarkSelection;
import org.eclipse.jface.text.ITextSelection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import eu.chorevolution.studio.eclipse.core.internal.project.ChorevolutionProjectUtils;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIPlugin;
import eu.chorevolution.studio.eclipse.ui.views.model.SynthesisProcessorStep;

public class ChorevolutionSynthesisProcessorViewPart extends ViewPart{
	private static final Image IMAGE_LED_LIGHT_BLUE = ChorevolutionUIPlugin.getImageDescriptor("icons/full/view/blue_step.png").createImage();
	private static final Image IMAGE_LED_LIGHT_GREEN = ChorevolutionUIPlugin.getImageDescriptor("icons/full/view/green_step.png").createImage();
	private static final Image IMAGE_LED_LIGHT_RED = ChorevolutionUIPlugin.getImageDescriptor("icons/full/view/red_step.png").createImage();
	private static final Image IMAGE_LED_LIGHT_GREY = ChorevolutionUIPlugin.getImageDescriptor("icons/full/view/grey_step.png").createImage();
	private static final int COLUMN_WIDTH_OFFSET = 5;
	
	private TableViewer viewer;
	
	private IProject actualIProject;
	
	private List<SynthesisProcessorStep> serviceRecords;
	// the listener we register with the selection service 
		private ISelectionListener listener = new ISelectionListener() {
			
			@Override
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {
				// we ignore our own selections
				if (part instanceof PackageExplorerPart ) {
				    showSelection(part, selection);
				}
				
			}
		};
		
	@Override
	public void createPartControl(Composite parent) {
		
		serviceRecords = ChorevolutionSynthesisProcessorDataProvider.INSTANCE.getManualStepsSynthesisProcessor();
		
		Composite composite = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.marginHeight = 3;
		layout.marginWidth = 3;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		viewer = new TableViewer(composite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		createColumns(parent, viewer);
		
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(false);
		viewer.setSorter(new ChorevolutionSynthesisProcessorViewerSorter());
		viewer.setContentProvider(new ArrayContentProvider());
		// get the content for the viewer, setInput will call getElements in the
		// contentProvider
		viewer.setInput(serviceRecords);

		
		setWidthColumns();
		
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(listener);
	}

	// create the columns for the table
	private void createColumns(final Composite parent, final TableViewer viewer) {
		String[] titles = { "Status", "Synthesis Processor Phase"};
		int[] bounds = { 70, 200};

		// first column is for the Service Role
		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], SWT.LEFT);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return null;
			}

			@Override
			public Image getImage(Object element) {
				return IMAGE_LED_LIGHT_GREY;
			}
			
		});
		// the image in the cell cannot be centered https://bugs.eclipse.org/bugs/show_bug.cgi?id=26045
		col.getColumn().setResizable(false);
		
		col = createTableViewerColumn(titles[1], bounds[1], SWT.LEFT);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				SynthesisProcessorStep serviceRecord = (SynthesisProcessorStep) element;
				return serviceRecord.getName();
			}
		});
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound, int alignment) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
	//	column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(false);
		column.setAlignment(alignment);
		
		return viewerColumn;
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	@Override
	public void dispose() {
		// important: We need do unregister our listener when the view is disposed
		getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(listener);
		super.dispose();
	}
	
	
	private void setWidthColumns() {
		// auto width columns
		for (int i = 0, n = viewer.getTable().getColumnCount(); i < n; i++) {
			viewer.getTable().getColumn(i).pack();
			viewer.getTable().getColumn(i).setWidth(viewer.getTable().getColumn(i).getWidth() + COLUMN_WIDTH_OFFSET);
		}
	}
	
	/**
	 * Shows the given selection in this view.
	 */
	public void showSelection(IWorkbenchPart sourcepart, ISelection selection) {
		if (selection.isEmpty()){
			setContentDescription("No Synthesis Project selected");
		}else if (selection instanceof IStructuredSelection &&((IStructuredSelection)selection).toArray().length == 1 && ((IStructuredSelection)selection).getFirstElement() instanceof IProject){
			IProject currentProjectSelected = (IProject) ((IStructuredSelection)selection).getFirstElement();
			if (ChorevolutionProjectUtils.isChorevolutionSynthesisProjectNature(currentProjectSelected)){
				setContentDescription("Selected Synthesis Project: " + currentProjectSelected.getName());
			}		
		}
	}

	
}