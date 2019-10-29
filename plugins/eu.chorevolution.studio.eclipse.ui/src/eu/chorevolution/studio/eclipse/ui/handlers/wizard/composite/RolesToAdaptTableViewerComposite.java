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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.bpmn2.Choreography;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
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

import eu.chorevolution.studio.eclipse.core.utils.MappingAssociation;
import eu.chorevolution.studio.eclipse.core.utils.TreeNode;
import eu.chorevolution.studio.eclipse.core.utils.TreeNodeDataType;
import eu.chorevolution.studio.eclipse.ui.Utilities;
import eu.chorevolution.studio.eclipse.ui.handlers.dialogs.MappingEditorIntoSynthesisProcessorDialog;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.AdapterModelDataType;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.ParticipantTableViewerRecord;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.RolesViewerRecord;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.ValidElementsForParticipant;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.WSDLOrGIDLParticipantsData;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.page.AdapterGeneratorWizardPage;

public class RolesToAdaptTableViewerComposite {
    AdapterGeneratorWizardPage adapterGeneratorWizardPage;

    private TableViewer viewer;
    private List<RolesViewerRecord> rolesToAdapt;
    private List<Choreography> choreographies;
    private IFile bpmnFile;
    private byte[] typesXSD;
    private List<ParticipantTableViewerRecord> providerParticipants;
    private List<WSDLOrGIDLParticipantsData> errorsToMap;
    private List<ValidElementsForParticipant> validElementsForParticipant;
    
    private List<AdapterModelDataType> adapterModelsToBeCreated;
    
    private int numberOfAdaptersToBeCreated;
    private int numberOfAdaptersCreated = 0;
    
    Composite composite;
    
    public void setRolesToAdapt(List<RolesViewerRecord> rolesToAdapt) {
		this.rolesToAdapt = rolesToAdapt;
	}

	public RolesToAdaptTableViewerComposite(AdapterGeneratorWizardPage adapterGeneratorWizardPage,
            List<RolesViewerRecord> rolesToAdapt, List<Choreography> choreographies, IFile bpmnFile, List<ParticipantTableViewerRecord> providerParticipants, byte[] typesXSD) {
        this.adapterGeneratorWizardPage = adapterGeneratorWizardPage;
        this.rolesToAdapt = rolesToAdapt;
        this.choreographies = choreographies;
        this.bpmnFile = bpmnFile;
        this.typesXSD = typesXSD;
        this.providerParticipants = providerParticipants;
        
        numberOfAdaptersToBeCreated = 0;
        numberOfAdaptersCreated = 0;
        
        adapterModelsToBeCreated = new ArrayList<AdapterModelDataType>();
    }

    public void createPartControl(Composite parent) {
        composite = new Composite(parent, SWT.NONE);
        
        GridLayout layout = new GridLayout();
        layout.marginHeight = 3;
        layout.marginWidth = 3;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label title = new Label(composite, SWT.NONE);
        title.setText("Participant(s) that needs to be mapped with Service(s):");

        viewer = new TableViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        createColumns(viewer);
        final Table table = viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        viewer.setContentProvider(new ArrayContentProvider());
        // get the content for the viewer, setInput will call getElements in the
        // contentProvider
        viewer.setInput(rolesToAdapt);
   
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
    private void createColumns(final TableViewer viewer) {
        String[] titles = { "Initiating Participant", "Task Name", "Receiving Participant", "Task ID", "Service ID", "Receiving Participant Service", "Service Location", "Adapter Model"};
        // uncomment this bounds to enable Generate and Service Name columns
        int[] bounds = { 200, 200, 200, 100, 230, 230, 170, 170};

        // first column is for the participant
        TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], SWT.LEFT);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                RolesViewerRecord serviceRecord = (RolesViewerRecord) element;
                return serviceRecord.getParticipant();
            }
        });

        
        col = createTableViewerColumn(titles[1], bounds[1], SWT.LEFT);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                RolesViewerRecord serviceRecord = (RolesViewerRecord) element;
                return serviceRecord.getTaskName();
            }

        });

        
        col = createTableViewerColumn(titles[2], bounds[2], SWT.LEFT);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                RolesViewerRecord serviceRecord = (RolesViewerRecord) element;
                return serviceRecord.getRoleToMap();
            }
        });
        
        
        col = createTableViewerColumn(titles[3], bounds[3], SWT.LEFT);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                RolesViewerRecord serviceRecord = (RolesViewerRecord) element;
                return serviceRecord.getTaskID();
            }

        });
        col.getColumn().setWidth(0);
        col.getColumn().setResizable(false);
        
        col = createTableViewerColumn(titles[4], bounds[4], SWT.LEFT);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                RolesViewerRecord serviceRecord = (RolesViewerRecord) element;
                return serviceRecord.getServiceID();
            }

        });
        col.getColumn().setWidth(0);
        col.getColumn().setResizable(false);

        col = createTableViewerColumn(titles[5], bounds[5], SWT.LEFT);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                RolesViewerRecord serviceRecord = (RolesViewerRecord) element;
                return serviceRecord.getServiceName();
            }

        });
        
        col = createTableViewerColumn(titles[6], bounds[6], SWT.LEFT);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                RolesViewerRecord serviceRecord = (RolesViewerRecord) element;
                return serviceRecord.getServiceLocation();
            }

        });

        col = createTableViewerColumn(titles[7], bounds[7], SWT.LEFT);
        col.setLabelProvider(new ColumnLabelProvider() {
            // make sure you dispose these buttons when viewer input changes
            //
        	
            @Override
            public String getText(Object element) {
                RolesViewerRecord serviceRecord = (RolesViewerRecord) element;
                return serviceRecord.getRoleToMap();
            }

            @Override
            public void update(ViewerCell cell) {
                TableItem item = (TableItem) cell.getItem();

                //if the roletomap is empty, don't show the button
                if(!cell.getViewerRow().getCell(2).getText().equals("")) {
	                	
                	//i have to get before the left-top item name
                	ViewerCell tempCell = cell.getViewerRow().getCell(0).getNeighbor(ViewerCell.ABOVE, false);
                	while(tempCell.getText().equals("")) {
                		tempCell = tempCell.getViewerRow().getCell(0).getNeighbor(ViewerCell.ABOVE, false);
                	}
                	
                	String tempCellText = tempCell.getText();
                	String taskIDCellText = cell.getViewerRow().getCell(3).getText();
                	String thisCellText = cell.getViewerRow().getCell(2).getText();

                	cell.setText("toBeCreated");
                	
	                Button button = new Button((Composite) cell.getViewerRow().getControl(), SWT.NONE);
	                button.setText("...");
	                button.setData("participant", cell.getNeighbor(ViewerCell.LEFT, true).getText());
	                button.addListener(SWT.Selection, new Listener() {
	 	
	                	
	                    @Override
	                    public void handleEvent(Event event) {
	                        switch (event.type) {
	                        case SWT.Selection:   	
		                        
	                        	MappingEditorIntoSynthesisProcessorDialog meisp = null;
	                        	
	                        	for(WSDLOrGIDLParticipantsData roleToOpen : errorsToMap) {

	                        		if(roleToOpen.getProviderParticipantName().equals(thisCellText)) {
	                        			if(roleToOpen.getParticipantName().equals(tempCellText)) {
	                        				if(roleToOpen.getTaskID().equals(taskIDCellText)) {
	                        					
	                        					byte[] thisWsdlOrGidlFile = null;
	                        					byte[] securityFile = null;
	                        					
	                        					for(ParticipantTableViewerRecord providerParticipant : providerParticipants) {

	                        						if(providerParticipant.getParticipant().equals(roleToOpen.getProviderParticipantName())) {
	                        							//get the wsdl or gidl file
	                        							thisWsdlOrGidlFile = providerParticipant.getService().getInterfaceDescriptionContent();
	                        							securityFile = providerParticipant.getService().getSecurityDescriptionContent();
	                        							break;
	                        						}
	                        					}
	                        					
	                        					//see if i already have a map and "reload it"
	                        					AdapterModelDataType adapterToPass = null;
	                        					List<MappingAssociation> oldAssociation = null;
	                        					
	        									for(int y=0; y<adapterModelsToBeCreated.size(); y++) {
	        										if(adapterModelsToBeCreated.get(y).getTaskID().equals(roleToOpen.getTaskID())) {
	        											oldAssociation = Utilities.cloneAssociation(adapterModelsToBeCreated.get(y));
	        											adapterToPass = adapterModelsToBeCreated.get(y);
	        											break;
	        										}
	        									}
	                        					
	                        					
	                        					meisp = new MappingEditorIntoSynthesisProcessorDialog(adapterGeneratorWizardPage.getShell(), adapterGeneratorWizardPage.getProject(), roleToOpen, errorsToMap, validElementsForParticipant, choreographies, bpmnFile, thisWsdlOrGidlFile, typesXSD, securityFile, adapterToPass, oldAssociation);	             
	                        					meisp.create();	

	                        					break;
	                        				}
	                        			}
	                        		}
	                        	}
	                        	
	                        	int idOpening = meisp.open();
	                        	
								if (idOpening == IDialogConstants.OK_ID) {
									
									//avoid duplicates
									for(int y=0; y<adapterModelsToBeCreated.size(); y++) {
										if(adapterModelsToBeCreated.get(y).getTaskID().equals(meisp.getTaskID())) {
											adapterModelsToBeCreated.remove(y);
											numberOfAdaptersCreated -= 1;
											break;
										}
									}
									
									numberOfAdaptersCreated += 1;							
									adapterGeneratorWizardPage.getWizard().getContainer().updateButtons();
		
									adapterModelsToBeCreated.add(new AdapterModelDataType(meisp.getParticipant(), meisp.getRoleToMap(), meisp.getTaskName(), meisp.getTaskID(), meisp.getTreeFromTask(), meisp.getWsdlOrGidlFile(), meisp.getTreeMapping(), meisp.getTreeFromWSDLOrGIDL()));
									
									button.setText("Created");
									
									meisp.getCanvas().dispose();
									meisp.getGreenImage().dispose();
									meisp.getOrangeImage().dispose();
									meisp.getRedImage().dispose();
									meisp.getComposite().dispose();
								}
								else if(idOpening == IDialogConstants.CANCEL_ID) {
									//i need to ripristinate the old mapping
									
									for(int y=0; y<adapterModelsToBeCreated.size(); y++) {
										if(adapterModelsToBeCreated.get(y).getTaskID().equals(meisp.getTaskID())) {
											//if i have already created an adapter previously, i also have mapping hashmap
											Utilities.remapTreeUsingList(adapterModelsToBeCreated.get(y).getTreeFromTask(), meisp.getOldMappingList());
											break;
										}
									}

								}
	                        	
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
                else {
                	cell.setText("");
                }
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
    

    public boolean isAdapterCreationFinished() {
    	numberOfAdaptersToBeCreated = 0;
    	
    	TableItem tableRows[] = viewer.getTable().getItems();
    	for(int i=0; i<tableRows.length; i++) {
        	if(tableRows[i].getText(7).equals("toBeCreated"))
        		numberOfAdaptersToBeCreated+=1;
    	}

    	if(numberOfAdaptersCreated != numberOfAdaptersToBeCreated)
    		return false;
    	else
    		return true;
    	
    }
    
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    public void refreshRoleToAdapt(List<WSDLOrGIDLParticipantsData> errorsToMap, List<ValidElementsForParticipant> allValidElementsForParticipant) {
    	this.errorsToMap = errorsToMap;
    	this.validElementsForParticipant = allValidElementsForParticipant;
    	
    	viewer.setContentProvider(new ArrayContentProvider());
        viewer.setInput(rolesToAdapt);
    	viewer.getTable().redraw();
    	viewer.getControl().redraw();
        viewer.refresh();       
        
        
        
        //TODO when going back to firts page, adding a service, coming back to this page, force the redraw of the viewer
        //in order to avoid dialog opening with another service
    }

    public List<RolesViewerRecord> getServiceTableViewerRecords(){
		return rolesToAdapt;
	}
    
    public List<AdapterModelDataType> getAdapterModelsToBeCreated(){
		return this.adapterModelsToBeCreated;
	}
    
    public List<RolesViewerRecord> getUniqueNotEmptyServiceTableViewerRecords(){
    	List<RolesViewerRecord> uniqueRoles = new ArrayList<RolesViewerRecord>();
    	
    	//may be null
    	if(rolesToAdapt != null) {
	    	for(RolesViewerRecord roleToAdapt : rolesToAdapt) {
				boolean present = false;
	
	    		for(RolesViewerRecord uniqueNavigator : uniqueRoles) {
	    			if(roleToAdapt.getRoleToMap().equals(uniqueNavigator.getRoleToMap())) {
	    				present = true;
	    				break;
	    			}
	    		}
	    		
	    		if(!present)
	    			uniqueRoles.add(roleToAdapt);
	    		
	    	}
    	}

    	
    	return uniqueRoles;
	}

}
