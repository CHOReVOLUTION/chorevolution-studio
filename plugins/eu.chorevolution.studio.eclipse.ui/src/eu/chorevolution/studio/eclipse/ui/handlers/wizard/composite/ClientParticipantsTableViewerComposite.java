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
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIPlugin;
import eu.chorevolution.studio.eclipse.ui.handlers.dialogs.CorrelationTaskDialog;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.CorrelationChoreographyTaskTableViewerRecord;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.ParticipantTableViewerRecord;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.page.CoordinationDelegatesWizardPage;

public class ClientParticipantsTableViewerComposite {
	private CoordinationDelegatesWizardPage coordinationDelegatesWizardPage;
	private Map<String, List<String>> correlatedChoreographyTaskWithMap;
	private Map<String, List<CorrelationChoreographyTaskTableViewerRecord>> correlationChoreographyTaskMap;

	// static fields to hold the images
	private static final Image CHECKED_DISABLED = ChorevolutionUIPlugin
			.getImageDescriptor("icons/full/obj16/checked-disabled.gif").createImage();

	private TableViewer viewer;
	private List<ParticipantTableViewerRecord> clientParticipants;

	public ClientParticipantsTableViewerComposite(CoordinationDelegatesWizardPage coordinationDelegatesWizardPage,
			List<ParticipantTableViewerRecord> clientParticipants,
			Map<String, List<CorrelationChoreographyTaskTableViewerRecord>> correlationChoreographyTaskMap,
			Map<String, List<String>> correlatedChoreographyTaskWithMap) {
		this.coordinationDelegatesWizardPage = coordinationDelegatesWizardPage;
		this.clientParticipants = clientParticipants;
		this.correlatedChoreographyTaskWithMap = correlatedChoreographyTaskWithMap;
		this.correlationChoreographyTaskMap = correlationChoreographyTaskMap;
	}

	public void createPartControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.marginHeight = 3;
		layout.marginWidth = 3;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label title = new Label(composite, SWT.NONE);
		title.setText("Client participants:");

		viewer = new TableViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns(parent, viewer);
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(new ArrayContentProvider());
		// get the content for the viewer, setInput will call getElements in the
		// contentProvider
		viewer.setInput(clientParticipants);
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
		String[] titles = { "Participant", "Generate", "CD Name", "Task Correlations" };
		int[] bounds = { 200, 80, 200, 120 };

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
		// the image in the cell cannot be centered
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=26045
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
		col = createTableViewerColumn(titles[3], bounds[3], SWT.LEFT);
		col.setLabelProvider(new ColumnLabelProvider() {
			// make sure you dispose these buttons when viewer input changes
			//

			@Override
			public void update(ViewerCell cell) {
				TableItem item = (TableItem) cell.getItem();
				/*
				 * Button button;
				 * if(buttonsToSerchFromInventory.containsKey(cell.getElement())
				 * ){ button =
				 * buttonsToSerchFromInventory.get(cell.getElement()); } else{
				 * button = new Button((Composite)
				 * cell.getViewerRow().getControl(),SWT.NONE);
				 * button.setText("...");
				 * 
				 * buttonsToSerchFromInventory.put(cell.getElement(), button); }
				 */

				Button button = new Button((Composite) cell.getViewerRow().getControl(), SWT.NONE);
				button.setText("...");
				String clientParticipantName = cell.getNeighbor(ViewerCell.LEFT, true).getNeighbor(ViewerCell.LEFT, true).getNeighbor(ViewerCell.LEFT, true).getText();
				button.setData("participant", clientParticipantName);
				button.addListener(SWT.Selection, new Listener() {

					@Override
					public void handleEvent(Event event) {
						switch (event.type) {
						case SWT.Selection:
							CorrelationTaskDialog correlationTaskDialog = new CorrelationTaskDialog(
									coordinationDelegatesWizardPage,
									clientParticipantName,
									correlationChoreographyTaskMap.get(clientParticipantName),
									correlatedChoreographyTaskWithMap.get(clientParticipantName),
									coordinationDelegatesWizardPage.getShell(),
									coordinationDelegatesWizardPage.getProject());
							correlationTaskDialog.create();
							
							
/*							
							SelectionServiceFromInventoryDialog selectionServiceFromInventoryDialog = new SelectionServiceFromInventoryDialog(
									coordinationDelegatesWizardPage.getShell(),
									coordinationDelegatesWizardPage.getProject(),
									(String) event.widget.getData("participant"));
							selectionServiceFromInventoryDialog.create();
*/
							if (correlationTaskDialog.open() == IDialogConstants.OK_ID) {
								
								/*
								  if (item.getData() instanceof ParticipantTableViewerRecord) {
									  ((ParticipantTableViewerRecord)item.getData()) .setService(
											  correlationTaskDialog.getCorrelationTasks()
								  getSelectedService());
								  viewer.update(item.getData(), null); //update
								  the table viewer inside the secured
								  participant ((SecurityFiltersWizardPage)
								  serviceRoleBindingsWizardPage.getWizard().
								  getPage(ChorevolutionUIMessages.
								  SecurityFiltersWizardPage_name)).
								  refreshSecuredParticipants();
								  
								  }
								  */
								 
							}
							coordinationDelegatesWizardPage
									.setPageComplete(coordinationDelegatesWizardPage.validatePage());
							break;
						}
					}
				});

				TableEditor editor = new TableEditor(item.getParent());
				editor.grabHorizontal = true;
				editor.grabVertical = true;
				editor.setEditor(button, item, cell.getColumnIndex());
				editor.layout();
			}
		});

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

	public List<ParticipantTableViewerRecord> getServiceTableViewerRecords() {
		return clientParticipants;
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
