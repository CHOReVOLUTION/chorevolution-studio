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
package eu.chorevolution.studio.eclipse.ui.handlers.wizard.composite;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIPlugin;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.ParticipantTableViewerRecord;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.page.CoordinationDelegatesWizardPage;


public class ProsumerParticipantsTableViewerComposite {
    CoordinationDelegatesWizardPage coordinationDelegatesWizardPage;
	// static fields to hold the images
	private static final Image CHECKED_DISABLED = ChorevolutionUIPlugin.getImageDescriptor("icons/full/obj16/checked-disabled.gif").createImage();
	
	private TableViewer viewer;
	private List<ParticipantTableViewerRecord> prosumerParticipants;

	public ProsumerParticipantsTableViewerComposite(CoordinationDelegatesWizardPage coordinationDelegatesWizardPage, List<ParticipantTableViewerRecord> prosumerParticipants) {
		this.coordinationDelegatesWizardPage = coordinationDelegatesWizardPage;
		this.prosumerParticipants = prosumerParticipants;
	
	}

	public void createPartControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.marginHeight = 3;
		layout.marginWidth = 3;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label title = new Label(composite, SWT.NONE);
		title.setText("Prosumer participants:");

		viewer = new TableViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns(parent, viewer);
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		viewer.setContentProvider(new ArrayContentProvider());
		// get the content for the viewer, setInput will call getElements in the
		// contentProvider
		viewer.setInput(prosumerParticipants);
		// set the sorter for the table

		// define layout for the viewer
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		viewer.getControl().setLayoutData(gridData);
	}

	// create the columns for the table
	private void createColumns(final Composite parent, final TableViewer viewer) {
		String[] titles = { "Participant", "Generate", "CD Name"};
		int[] bounds = {200, 80, 200};

		// first column is for the Service Role
		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], SWT.LEFT);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
			    ParticipantTableViewerRecord serviceRecord = (ParticipantTableViewerRecord) element;
				return serviceRecord.getParticipant();
			}
		});
		
		
		col = createTableViewerColumn(titles[1], bounds[1], SWT.LEFT);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return null;
			}

			@Override
			public Image getImage(Object element) {
				return CHECKED_DISABLED;
			}
			
		});
		// the image in the cell cannot be centered https://bugs.eclipse.org/bugs/show_bug.cgi?id=26045
		col.getColumn().setResizable(false);
		
		col = createTableViewerColumn(titles[2], bounds[2], SWT.LEFT);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
			    ParticipantTableViewerRecord serviceRecord = (ParticipantTableViewerRecord) element;
				return serviceRecord.getServiceProjectName();
			}
		});
		col.setEditingSupport(new ProjectNameEditingSupport(viewer));
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound, int alignment) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		column.setAlignment(alignment);
		
		return viewerColumn;
	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	public List<ParticipantTableViewerRecord> getServiceTableViewerRecords (){
		return prosumerParticipants;
	}
	
	
	private class ProjectNameEditingSupport extends EditingSupport {

		private final TableViewer viewer;
		private final CellEditor editor;

		public ProjectNameEditingSupport(TableViewer viewer) {
			super(viewer);
			this.viewer = viewer;
			this.editor = new TextCellEditor(viewer.getTable());
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		protected TableViewer getTableViewer() {
			return viewer;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			return ((ParticipantTableViewerRecord) element).getServiceProjectName();
		}

		@Override
		protected void setValue(Object element, Object userInputValue) {
			((ParticipantTableViewerRecord) element).setServiceProjectName(String.valueOf(userInputValue));
			getTableViewer().update(element, null);
			coordinationDelegatesWizardPage.setPageComplete(coordinationDelegatesWizardPage.validatePage());
		}
	}
}
