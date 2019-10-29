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

import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import eu.chorevolution.studio.eclipse.core.utils.syncope.ServiceAuthenticationType;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.ParticipantTableViewerRecord;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.page.SecurityFiltersWizardPage;

public class SecuredProviderParticipantsTableViewerComposite {
    SecurityFiltersWizardPage securityFiltersWizardPage;

    private TableViewer viewer;
    private List<ParticipantTableViewerRecord> securedProviderParticipants;

    public SecuredProviderParticipantsTableViewerComposite(SecurityFiltersWizardPage securityFiltersWizardPage,
            List<ParticipantTableViewerRecord> securedProviderParticipants) {
        this.securityFiltersWizardPage = securityFiltersWizardPage;
        this.securedProviderParticipants = securedProviderParticipants;
    }

    public void createPartControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);

        GridLayout layout = new GridLayout();
        layout.marginHeight = 3;
        layout.marginWidth = 3;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label title = new Label(composite, SWT.NONE);
        title.setText("Secured Provider participants:");

        viewer = new TableViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        createColumns(parent, viewer);
        final Table table = viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        viewer.setContentProvider(new ArrayContentProvider());
        // get the content for the viewer, setInput will call getElements in the
        // contentProvider
        viewer.setInput(securedProviderParticipants);

        // used to show only provider participants that they have a security
        // information
        viewer.addFilter(new SecuredProviderParticipantFilter());

        // define layout for the viewer
        GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.horizontalSpan = 2;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.heightHint = new PixelConverter(viewer.getControl()).convertHeightInCharsToPixels(7);
        
        viewer.getControl().setLayoutData(gridData);
    }

    // create the columns for the table
    private void createColumns(final Composite parent, final TableViewer viewer) {
        String[] titles = { "Participant", "Select From Inventory", "Service ID", "Service Name", "Service Location", "Authentication Type", "Username", "Password", "Custom Auth ZIP"};
        // uncomment this bounds to enable Generate and Service Name columns
        int[] bounds = { 200, 0, 230, 140, 200, 130, 100, 100, 140 };

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
            // make sure you dispose these buttons when viewer input changes
            //

            @Override
            public void update(ViewerCell cell) {
                // TODO
            }
        });

        col = createTableViewerColumn(titles[2], bounds[2], SWT.LEFT);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                ParticipantTableViewerRecord serviceRecord = (ParticipantTableViewerRecord) element;
                return serviceRecord.getService() != null ? String.valueOf(serviceRecord.getService().getKey())
                        : null;
            }
        });

        col = createTableViewerColumn(titles[3], bounds[3], SWT.LEFT);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                ParticipantTableViewerRecord serviceRecord = (ParticipantTableViewerRecord) element;
                return serviceRecord.getService() != null ? serviceRecord.getService().getName() : null;
            }

        });

        col = createTableViewerColumn(titles[4], bounds[4], SWT.LEFT);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                ParticipantTableViewerRecord serviceRecord = (ParticipantTableViewerRecord) element;
                return serviceRecord.getService() != null ? serviceRecord.getService().getLocation() : null;
            }

        });
        
        col = createTableViewerColumn(titles[5], bounds[5], SWT.LEFT);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                ParticipantTableViewerRecord serviceRecord = (ParticipantTableViewerRecord) element;
                if (serviceRecord.getService() == null){
                	return "";
                }
                return serviceRecord.getService().getServiceAuthenticationType().name();
            }

        });
        
        col = createTableViewerColumn(titles[6], bounds[6], SWT.LEFT);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                ParticipantTableViewerRecord serviceRecord = (ParticipantTableViewerRecord) element;
                return serviceRecord.getSecurityServiceAuthenticationSharedUsername();
            }

        });
        col.setEditingSupport(new securityServiceAuthenticationSharedUsernameEditingSupport(viewer));
        
        col = createTableViewerColumn(titles[7], bounds[7], SWT.LEFT);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                ParticipantTableViewerRecord serviceRecord = (ParticipantTableViewerRecord) element;
                return serviceRecord.getSecurityServiceAuthenticationSharedPassword();
            }

        });
        col.setEditingSupport(new securityServiceAuthenticationSharedPasswordEditingSupport(viewer));

        col = createTableViewerColumn(titles[8], bounds[8], SWT.LEFT);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                ParticipantTableViewerRecord serviceRecord = (ParticipantTableViewerRecord) element;
                if(serviceRecord.getService() != null) {
                	if(serviceRecord.getService().getCustomAuthFileJAR() != null)
                		return "Loaded";
                	else
                		return "Not Needed";
                }
                else
                	return null;
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

    public void refreshSecuredParticipants() {
        viewer.refresh();
    }

    public List<ParticipantTableViewerRecord> getServiceTableViewerRecords (){
		return securedProviderParticipants;
	}
    
    public class SecuredProviderParticipantFilter extends ViewerFilter {

        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {

            ParticipantTableViewerRecord participantTableViewerRecord = (ParticipantTableViewerRecord) element;
            if (participantTableViewerRecord.getService() != null
                    && participantTableViewerRecord.getService().getSecurityDescriptionType() != null) {
                return true;
            }

            return false;
        }
    }
    
    private class securityServiceAuthenticationSharedUsernameEditingSupport extends EditingSupport {

		private final TableViewer viewer;
		private final CellEditor editor;

		public securityServiceAuthenticationSharedUsernameEditingSupport(TableViewer viewer) {
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
			if(((ParticipantTableViewerRecord) element).getService().getServiceAuthenticationType() == ServiceAuthenticationType.SHARED){
				return true;
			}else{
				return false;
			}
		}

		@Override
		protected Object getValue(Object element) {
			return ((ParticipantTableViewerRecord) element).getSecurityServiceAuthenticationSharedUsername();
		}

		@Override
		protected void setValue(Object element, Object userInputValue) {
			((ParticipantTableViewerRecord) element).setSecurityServiceAuthenticationSharedUsername(String.valueOf(userInputValue));
			getTableViewer().update(element, null);
			securityFiltersWizardPage.setPageComplete(securityFiltersWizardPage.validatePage());
		}
	}
    
    private class securityServiceAuthenticationSharedPasswordEditingSupport extends EditingSupport {

		private final TableViewer viewer;
		private final CellEditor editor;

		public securityServiceAuthenticationSharedPasswordEditingSupport(TableViewer viewer) {
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
			if(((ParticipantTableViewerRecord) element).getService().getServiceAuthenticationType() == ServiceAuthenticationType.SHARED){
				return true;
			}else{
				return false;
			}
		}

		@Override
		protected Object getValue(Object element) {
			return ((ParticipantTableViewerRecord) element).getSecurityServiceAuthenticationSharedPassword();
		}

		@Override
		protected void setValue(Object element, Object userInputValue) {
			((ParticipantTableViewerRecord) element).setSecurityServiceAuthenticationSharedPassword(String.valueOf(userInputValue));
			getTableViewer().update(element, null);
			securityFiltersWizardPage.setPageComplete(securityFiltersWizardPage.validatePage());
		}
	}

}
