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
package eu.chorevolution.studio.eclipse.ui.handlers.dialogs;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIMessages;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.CorrelationChoreographyTaskTableViewerRecord;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.page.CoordinationDelegatesWizardPage;

public class CorrelationTaskDialog extends TitleAreaDialog {
    // private Image image;

    CoordinationDelegatesWizardPage coordinationDelegatesWizardPage;
	private TableViewer viewer;
	private String clientParticipantName;
	private List<CorrelationChoreographyTaskTableViewerRecord> correlationTasks;
	private String[] correlatedWithChoreographyTask;

    private IProject project;
    public CorrelationTaskDialog(CoordinationDelegatesWizardPage coordinationDelegatesWizardPage, String clientParticipantName, List<CorrelationChoreographyTaskTableViewerRecord> correlationTasks, List<String> correlatedWithChoreographyTasks, Shell shell, IProject project) {
        super(shell);
        this.project = project;
        
        this.coordinationDelegatesWizardPage = coordinationDelegatesWizardPage;
		this.clientParticipantName = clientParticipantName;
		this.correlationTasks = correlationTasks;
		
		this.correlatedWithChoreographyTask = new String[correlatedWithChoreographyTasks.size()+1];
		this.correlatedWithChoreographyTask[0] = CorrelationChoreographyTaskTableViewerRecord.NONE_CORRELLATION;
		for(int i=1; i<this.correlatedWithChoreographyTask.length;i++){
		    this.correlatedWithChoreographyTask[i] = correlatedWithChoreographyTasks.get(i-1);
		}
        /*
         * try { image = new Image(null, new FileInputStream("jface.gif")); }
         * catch (FileNotFoundException e) { }
         */
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(ChorevolutionUIMessages.ChorevolutionSynthesisProcessor_title);
    }

    /*
     * public boolean close() { if (image != null) image.dispose(); return
     * super.close(); }
     */
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);

        setTitle("Correlation Tasks");

        setMessage("Set the Task Correlations for the \""+clientParticipantName+"\" Client.", IMessageProvider.INFORMATION);
        

        /*
         * if (image != null) setTitleImage(image);
         */

        return contents;
    }

    protected Control createDialogArea(Composite parent) {
    	Composite composite = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.marginHeight = 3;
		layout.marginWidth = 3;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		//Label title = new Label(composite, SWT.NONE);
		//title.setText("\""+clientParticipantName+"\" "+"Correlated Choreography Tasks:");

		viewer = new TableViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns(parent, viewer);
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		viewer.setContentProvider(new ArrayContentProvider());
		// get the content for the viewer, setInput will call getElements in the
		// contentProvider
		viewer.setInput(correlationTasks);
		// set the sorter for the table

		// define layout for the viewer
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		viewer.getControl().setLayoutData(gridData);
		
		return composite;
	}

	// create the columns for the table
	private void createColumns(final Composite parent, final TableViewer viewer) {
		String[] titles = { "Choreography Task", "Correlated With"};
		int[] bounds = { 200, 200};

		// first column is for the Service Role
		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], SWT.LEFT);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
			    CorrelationChoreographyTaskTableViewerRecord choreographyTask = (CorrelationChoreographyTaskTableViewerRecord) element;
				return choreographyTask.getChoreographyTask();
			}
		});
		
		
		col = createTableViewerColumn(titles[1], bounds[1], SWT.LEFT);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
			    CorrelationChoreographyTaskTableViewerRecord correlatedWithTask = (CorrelationChoreographyTaskTableViewerRecord) element;
				return correlatedWithTask.getCorrelatedWith();
			}
		});
		col.setEditingSupport(new CorrelatedTaskEditingSupport(viewer,correlatedWithChoreographyTask));
		
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
	
	public List<CorrelationChoreographyTaskTableViewerRecord> getCorrelationTasks (){
		return correlationTasks;
	}
	
	
	private class CorrelatedTaskEditingSupport extends EditingSupport {

		private final TableViewer viewer;
		private final CellEditor editor;
		private final String[] choreographyTasks;

		public CorrelatedTaskEditingSupport(TableViewer viewer, String[] choreographyTasks) {
		    super(viewer);
			this.viewer = viewer;
			this.choreographyTasks =choreographyTasks;
			this.editor = new ComboBoxCellEditor(viewer.getTable(),choreographyTasks,SWT.READ_ONLY);
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
		    for(int i=0; i<correlatedWithChoreographyTask.length;i++){
                if (correlatedWithChoreographyTask[i].equals(((CorrelationChoreographyTaskTableViewerRecord) element).getCorrelatedWith())){
                    return i;
                }
            }
		    return 0;
		}

		@Override
		protected void setValue(Object element, Object userInputValue) {
		    ((CorrelationChoreographyTaskTableViewerRecord) element).setCorrelatedWith(choreographyTasks[Integer.valueOf(String.valueOf(userInputValue))]);
		    getTableViewer().update(element, null);
			coordinationDelegatesWizardPage.setPageComplete(coordinationDelegatesWizardPage.validatePage());
		}
	}

    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }

    @Override
    protected boolean isResizable() {
        return true;
    }
}
