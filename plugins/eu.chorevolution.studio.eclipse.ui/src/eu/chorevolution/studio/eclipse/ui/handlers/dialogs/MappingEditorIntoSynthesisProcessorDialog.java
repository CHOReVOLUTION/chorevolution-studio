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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.SystemUtils;
import org.eclipse.bpmn2.Choreography;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.xml.sax.SAXException;

import eu.chorevolution.studio.eclipse.core.utils.MappingAssociation;
import eu.chorevolution.studio.eclipse.core.utils.MappingList;
import eu.chorevolution.studio.eclipse.core.utils.MappingListReturnType;
import eu.chorevolution.studio.eclipse.core.utils.Tree;
import eu.chorevolution.studio.eclipse.core.utils.TreeNode;
import eu.chorevolution.studio.eclipse.core.utils.TreeNodeDataType;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIMessages;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIPlugin;
import eu.chorevolution.studio.eclipse.ui.Utilities;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.AdapterModelDataType;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.ValidElementsForParticipant;
import eu.chorevolution.studio.eclipse.ui.handlers.wizard.model.WSDLOrGIDLParticipantsData;

public class MappingEditorIntoSynthesisProcessorDialog extends TitleAreaDialog {
	// private Image image;

    Composite composite;
    private MappingList mappingList;
	private WSDLOrGIDLParticipantsData errorToMap;
	private String participant;
	private String roleToMap;
	private String taskName;
	private String taskID;
	private List<Choreography> choreographies;
	private IFile bpmnFile;
	private byte[] wsdlOrGidlFile;
	private byte[] typesXSD;
	private byte[] securityFile;
	
	private int curHeightFirstTree;
	private int curHeightSecondTree;

	private boolean inputDataTypeVisible;
	private boolean outputDataTypeVisible;
	
	private Image greenImage;
	private Image orangeImage;
	private Image redImage;
	
	private TreeNode<TreeNodeDataType> treeNodeSelected;
	private TreeNode<TreeNodeDataType> treeNodeWSDLOrGIDL;
	
	private TreeViewer tv;
	private TreeViewer otherTv;
	
    private Tree thisTree;
	
    //is the adapter passed from the parent, if present
    private AdapterModelDataType adapterPassed;
    private List<MappingAssociation> oldMappingList;
    
	private String selectedTaskName;
	private String selectedSenderMessageName;
	private String selectedReceiverMessageName;
	private String selectedSenderMessageType;
	private String selectedReceiverMessageType;
	
	//the Integer is the y value, the x is not relevant (i use it to draw the horizontal lines)
	private final HashMap<Point, TreeNode<TreeNodeDataType>> hashOfPoints;
	private final HashMap<Point, TreeNode<TreeNodeDataType>> hashOfServicePoints;

    private Canvas canvas;
    private final Point mouseClicked;
    
    //if 0 im waiting for the click on the task node, if 1 im waiting for the click in the service node
	private int mappingPhase = 0;
	private TreeNode<TreeNodeDataType> currentTreeMapping;
    //is the dislevel between the canvas and the treeviewer
    private int heightPadding = 2;
    //the distance from the left side of the canvas
    private int widthDistance = 5;
    //is the offset between the with the mouse click will be associated do element
    private int heightOffset = 10;
    
	public MappingEditorIntoSynthesisProcessorDialog(Shell shell, IProject project, 
			WSDLOrGIDLParticipantsData errorToMap, List<WSDLOrGIDLParticipantsData> allErrorsToMap, 
			List<ValidElementsForParticipant> validElementsForParticipant, List<Choreography> choreographies, 
			IFile bpmnFile, byte[] wsdlOrGidlFile, byte[] typesXSD, byte[] securityFile, AdapterModelDataType adapterPassed, 
			List<MappingAssociation> oldMappingList) {
		
		super(shell);
		this.errorToMap = errorToMap;
		this.participant = errorToMap.getParticipantName();
		this.roleToMap = errorToMap.getProviderParticipantName();
		this.taskName = errorToMap.getTaskName();
		this.taskID = errorToMap.getTaskID();
		this.choreographies = choreographies;
		this.bpmnFile = bpmnFile;
		this.typesXSD = typesXSD;
		this.securityFile = securityFile;
		this.adapterPassed = adapterPassed;
		this.oldMappingList = oldMappingList;
		this.inputDataTypeVisible = true;
		this.outputDataTypeVisible = true;
		
		hashOfPoints = new HashMap<Point, TreeNode<TreeNodeDataType>>();
		hashOfServicePoints = new HashMap<Point, TreeNode<TreeNodeDataType>>();

		mouseClicked = new Point(0, 0);
		currentTreeMapping = null;
		
		greenImage = ChorevolutionUIPlugin.getImageDescriptor("icons/full/view/green_step.png").createImage();
		orangeImage = ChorevolutionUIPlugin.getImageDescriptor("icons/full/view/orange_step.png").createImage();
		redImage = ChorevolutionUIPlugin.getImageDescriptor("icons/full/view/red_step.png").createImage();
		
		//fake function to add a supertype (needed if gidl)
		try {
			
			if(Utilities.isWSDL(wsdlOrGidlFile))
				this.wsdlOrGidlFile = wsdlOrGidlFile;
			else
				this.wsdlOrGidlFile = Utilities.addFakeTypeToGIDL(wsdlOrGidlFile);
			
		} catch (SAXException | IOException | ParserConfigurationException | TransformerException e) {
			// TODO Auto-generated catch block
		}

		if(adapterPassed == null) {
			selectedTaskName = null;
			selectedSenderMessageName = null;
			selectedSenderMessageType = null;
			selectedReceiverMessageName = null;
			selectedReceiverMessageType = null;
		}
		else {
			selectedTaskName = "";
			selectedSenderMessageName = "";
			selectedSenderMessageType = "";
			selectedReceiverMessageName = "";
			selectedReceiverMessageType = "";
		}
		
		//filter the results
		List<WSDLOrGIDLParticipantsData> errorsToMap = new ArrayList<WSDLOrGIDLParticipantsData>();
		for(WSDLOrGIDLParticipantsData singleErrorToMap : allErrorsToMap) {
			if(singleErrorToMap.getParticipantName().equals(participant)) {
				if(singleErrorToMap.getProviderParticipantName().equals(roleToMap)) {
					errorsToMap.add(singleErrorToMap);
				}
			}
		}
		
		new ArrayList<ValidElementsForParticipant>();
		for(ValidElementsForParticipant singleValidElement : validElementsForParticipant) {
			if(singleValidElement.getParticipant().equals(roleToMap)) {
				break;
			}
		}
		
		
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

		setTitle("Participant Mapping");

		setMessage("Mapping Participant "+errorToMap.getParticipantName()+" with Role "+errorToMap.getProviderParticipantName()+". Click on the square to \n start mapping the selected Choreography Item, then click on the triangle to apply the mapping with the selected Service Item.", IMessageProvider.INFORMATION);

		/*
		 * if (image != null) setTitleImage(image);
		 */

		return contents;
	}

	protected Control createDialogArea(Composite parent) {
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		
		
		sc.setLayoutData(new GridData(SWT.NONE, SWT.NONE, true, true));
		sc.setLayout(new GridLayout());
		
		composite = new Composite(sc, SWT.NONE | SWT.NO_SCROLL);
		sc.setContent(composite);
		
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 3;
		layout.marginWidth = 3;
	    layout.makeColumnsEqualWidth = true;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
				
	    Label description1 = new Label(composite, SWT.NULL);
	    description1.setText("Choreography Task");
	    
	    Label description3 = new Label(composite, SWT.NULL);
	    description3.setText("Mapping");
	    
	    Label description2 = new Label(composite, SWT.NULL);
	    description2.setText("Service Operations");
	    
		tv = new TreeViewer(composite, SWT.NO_SCROLL);
	    tv.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    tv.setContentProvider(new TreeContentProvider());
	    tv.setLabelProvider(new TreeLabelProvider());
	    
	    if(adapterPassed == null) {
	    	thisTree = Utilities.buildTreeFromTask(taskName, taskID, choreographies, bpmnFile, typesXSD);
	    }
	    else {
			Tree returnTree = new Tree();
	    	returnTree.setRootElement(adapterPassed.getTreeFromTask());
	    	thisTree = returnTree;
	    }
	    
	    tv.setInput(thisTree.getRootElement());
	    tv.expandAll();
	    
	    //locks the expansion
	    tv.getTree().addListener(SWT.Collapse, new Listener() {
	    	public void handleEvent(final Event event) {
	    		tv.getTree().setRedraw(false);
	    		Display.getDefault().asyncExec(new Runnable() {
	    			public void run() {
	    				((TreeItem)event.item).setExpanded(true);
	    				tv.getTree().setRedraw(true);
	    			}
	    		});
	    	}
	    }); 
	    
	    //locks the expansion on keypress
	    tv.getTree().addKeyListener(new KeyAdapter() {
	        @Override
	        public void keyPressed(KeyEvent e) {
	           e.doit = false;
	        }
	      });
	    
	    if(SystemUtils.IS_OS_WINDOWS) {
	    	tv.getTree().addListener(SWT.MeasureItem, new Listener() {
	    	 	   public void handleEvent(Event event) {
	    	 		  try {
		    	 	      TreeItem item = (TreeItem)event.item;
		    	 	      TreeNode<TreeNodeDataType> treeNode = (TreeNode<TreeNodeDataType>)item.getData();
		    	 	      item.setExpanded(true);
		    	 	      Image image = item.getImage();
		    	 	      if (treeNode != null) {
		    	 	    	  hashOfPoints.put(new Point(item.getBounds().x+item.getBounds().width, event.y+((tv.getTree().getItemHeight())/2)), treeNode);
		    	 	      }
	    	 		  }
	    	 		  catch(Exception e) {
	    	 			  //onLinux = true;
	    	 		  }
	    	 	   }
	    	});
	    }
	    else {
	    	populateFirstTreeHashMapForLinux(tv.getTree(), hashOfPoints);
	    }
	    
	    tv.getTree().addPaintListener(new PaintListener() {
	        public void paintControl(PaintEvent e) {	        	
        		e.gc.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_LINK_FOREGROUND));
	        	Set<Entry<Point, TreeNode<TreeNodeDataType>>> toDraw = hashOfPoints.entrySet();
	        	for(Entry<Point, TreeNode<TreeNodeDataType>> entry : toDraw) {
	        		e.gc.drawLine(entry.getKey().x, entry.getKey().y, canvas.getSize().x, entry.getKey().y);        	
	        	}
	        	
  	 	      	canvas.redraw();

	        }
	    });
	    
	    tv.getTree().addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent event) {
				try {
					Point point = new Point(event.x, event.y);					
					TreeNode<TreeNodeDataType> transformationRuleForTreeNode = (TreeNode<TreeNodeDataType>)tv.getTree().getItem(point).getData();
					
					if(transformationRuleForTreeNode != null) {
						//i have the node to be applied transformation rule
						
						//if the element is part of the output message, open the semd, else do nothing
						if(!transformationRuleForTreeNode.getData().getType().equals(Utilities.getTASK_TYPE()) && 
								!transformationRuleForTreeNode.getData().getType().equals(Utilities.getINITIATING_MESSAGE_TYPE()) &&
								!transformationRuleForTreeNode.getData().getType().equals(Utilities.getINITIATING_TYPE_TYPE()) && 
								!transformationRuleForTreeNode.getData().getType().equals(Utilities.getNON_INITIATING_MESSAGE_TYPE()) &&
								!transformationRuleForTreeNode.getData().getType().equals(Utilities.getNON_INITIATING_TYPE_TYPE()) &&
								Utilities.sonOfType(transformationRuleForTreeNode, Utilities.getNON_INITIATING_TYPE_TYPE(), (TreeNode<TreeNodeDataType>) tv.getInput(), false) &&
								transformationRuleForTreeNode.getData().getComplexOrSimpleType().equals("simpleType")) {
							
							if(transformationRuleForTreeNode.getData().getIsMappedWithTreeNode().isEmpty()) {
						
								SingleElementMappingDialog semd = null;
								semd = new SingleElementMappingDialog(getShell(), transformationRuleForTreeNode, thisTree.getRootElement(), "firstTree");
								semd.create();
								
								if(semd.open() == IDialogConstants.OK_ID) {
									transformationRuleForTreeNode.getData().setTransformationRule(semd.getTransformationRule());
									transformationRuleForTreeNode.getData().setIsMappedWithTreeNode(null);
									
									validateEnableOrDisableOKButton();
									redrawMap();
								}

							}
							else {
								//open the transformation rule dialog
								TransformationRuleDialog trd = null;
		    					trd = new TransformationRuleDialog(getShell(), transformationRuleForTreeNode, treeNodeWSDLOrGIDL, "firstTree", 
		    							inputDataTypeVisible, outputDataTypeVisible, thisTree.getRootElement());	             
		    					trd.create();	
		    					
								if (trd.open() == IDialogConstants.OK_ID) {
									transformationRuleForTreeNode.getData().setTransformationRule(trd.getTransformationRule());
									
									validateEnableOrDisableOKButton();
									redrawMap();
								}
							}
						}
						
					}
				
				}
				catch(Exception e) {
					//the point can be null, do nothing
				}
			}
		});
	    
	    try {
	    	
	    	if(adapterPassed == null) {
				if(Utilities.isGIDL(wsdlOrGidlFile))
					treeNodeWSDLOrGIDL = Utilities.buildTreeFromGIDL(wsdlOrGidlFile).getRootElement();
				else
					treeNodeWSDLOrGIDL = Utilities.buildTreeFromWSDL(wsdlOrGidlFile).getRootElement();
	    	}
		    else {
		    	treeNodeWSDLOrGIDL = adapterPassed.getTreeFromWSDLOrGIDL();
		    }
			
		} catch (SAXException | IOException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		canvas = new Canvas(composite, SWT.NONE | SWT.NO_SCROLL);
		GridData otherData1 = new GridData(GridData.VERTICAL_ALIGN_END);
		otherData1.horizontalSpan = 1;
		otherData1.horizontalAlignment = GridData.FILL;
		otherData1.verticalAlignment = GridData.FILL;

		canvas.setLayoutData(otherData1);
	    canvas.addPaintListener(new PaintListener() {
	        public void paintControl(PaintEvent e) {
	            Rectangle clientArea = canvas.getClientArea();
	            e.gc.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
	            e.gc.fillRectangle(clientArea);
	            
	            Device device = Display.getCurrent();
	            org.eclipse.swt.graphics.Color orange = new org.eclipse.swt.graphics.Color(device, 235, 145, 30);
	            
	        	//draw the mappings
	        	e.gc.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_BLACK));
	        	drawMappings(e.gc, thisTree.getRootElement());
	            
	            
        		e.gc.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
        		//draw the task square
        		Set<Entry<Point, TreeNode<TreeNodeDataType>>> toDraw = hashOfPoints.entrySet();
	        	for(Entry<Point, TreeNode<TreeNodeDataType>> entry : toDraw) {
	        		
	        		if(!entry.getValue().getData().getIsMappedWithTreeNode().isEmpty()) {
	            		e.gc.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
	        		}
	        		else if(entry.getValue().getData().getTransformationRule() != null) {
	            		e.gc.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));	
	        		}
	        		else {
		      	    	if(Utilities.sonOfType(entry.getValue(), Utilities.getINITIATING_TYPE_TYPE(), (TreeNode<TreeNodeDataType>) tv.getInput(), false))
		            		e.gc.setBackground(orange);
		      	    	else
		            		e.gc.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
	        		}
	        		
	        		//if the task is the selected one, draw it bigger
	        		if((mappingPhase == 1) && (entry.getValue() == currentTreeMapping)) {
	        			e.gc.fillRectangle(widthDistance-((heightOffset+5)/2), entry.getKey().y-((heightOffset+5)/2)+heightPadding, heightOffset+5, heightOffset+5);
	        		}
	        		else {
	        			e.gc.fillRectangle(widthDistance-((heightOffset)/2), entry.getKey().y-((heightOffset)/2)+heightPadding, heightOffset, heightOffset);
	        		}
	        		
	        		if(entry.getValue().getData().getTransformationRule() != null) {//draw transformation rule
	            		e.gc.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
		        		int topXT = 0;
		        		int topYT = entry.getKey().y-(heightOffset/2)+heightPadding;
	        			int[] vectT = {topXT+widthDistance+(heightOffset/2), topYT, topXT+widthDistance+(heightOffset/2), topYT+heightOffset, topXT, topYT+(heightOffset/2)};
		        		e.gc.fillPolygon(vectT);	
	        		}
	        		
	        	}
	        	
	        	
        		e.gc.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
	        	//draw the service triangle
        		Set<Entry<Point, TreeNode<TreeNodeDataType>>> toDraw2 = hashOfServicePoints.entrySet();
	        	for(Entry<Point, TreeNode<TreeNodeDataType>> entry : toDraw2) {
	        		
	        		if(entry.getValue().getData().getTransformationRule() != null) {//draw transformation rule
	            		e.gc.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));
		        		int topXT = canvas.getClientArea().width;
		        		int topYT = entry.getKey().y-(heightOffset/2)+heightPadding;
	        			int[] vectT = {topXT, topYT, topXT, topYT+heightOffset, topXT-widthDistance-(heightOffset/2), topYT+(heightOffset/2)};
		        		e.gc.fillPolygon(vectT);	
	        		}
	        		
	        		if(Utilities.isTreeNodeMappedWithItemOfTree(entry.getValue(), thisTree.getRootElement())) {
	            		e.gc.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
	        		}
	        		else if(entry.getValue().getData().getTransformationRule() != null) {//if i have tr rule it is green
	            		e.gc.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
	        		}
	        		else {
		      	    	if(Utilities.sonOfType(entry.getValue(), Utilities.getNON_INITIATING_TYPE_TYPE(), (TreeNode<TreeNodeDataType>) otherTv.getInput(), false))
		            		e.gc.setBackground(orange);
		      	    	else
		            		e.gc.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
	        		}
	        		
	        		int topX = canvas.getClientArea().width-widthDistance-(heightOffset/2);
	        		int topY = entry.getKey().y-(heightOffset/2)+heightPadding;
	        		int[] vect = {topX, topY, topX, topY+heightOffset, canvas.getClientArea().width, topY+(heightOffset/2)};
	        		e.gc.fillPolygon(vect);
	        		
	        	}
	        	
	        }

	    });	

	    
	    canvas.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {

			}
			
			@Override
			public void mouseDown(MouseEvent eb) {
				mouseClicked.x = eb.x;
				mouseClicked.y = eb.y;
				
				if(eb.button == 1) {//left click
					
					//i'm searching for mapping left side (task)
					if(mappingPhase == 0) {
						currentTreeMapping = getSelectedTaskTreeNode(mouseClicked);
						if(currentTreeMapping != null) {
							mappingPhase = 1;
						}
					}
					else if(mappingPhase == 1) {
						TreeNode<TreeNodeDataType> mapWith = getSelectedServiceTreeNode(mouseClicked);
						if((currentTreeMapping != null) && (mapWith != null)) {
							
							boolean toRemove = false;
							for(int j=0; j<currentTreeMapping.getData().getIsMappedWithTreeNode().size(); j++) {
								if(currentTreeMapping.getData().getIsMappedWithTreeNode().get(j) == mapWith) {
									//remove the association
									toRemove = true;
									currentTreeMapping.getData().getIsMappedWithTreeNode().remove(j);
									break;
								}
							}
							
							if(!toRemove) {
							
								currentTreeMapping.getData().addMappedWithTreeNode(mapWith);
								
								//this line is needed to avoid overlapping in trasformation rules for staticvalue, both sides
								currentTreeMapping.getData().setTransformationRule(null);
								mapWith.getData().setTransformationRule(null);
								
								//START logical validation phase
								
								if(currentTreeMapping.getData().getType().equals(Utilities.getTASK_TYPE())) {
									
									if(!mapWith.getData().getType().equals(Utilities.getTASK_TYPE())) {//cannot be mapped
										currentTreeMapping.getData().setIsMappedWithTreeNode(null);
										
										currentTreeMapping = null;
										mappingPhase = 0;
										redrawMap();								
										return;
									}
									
									selectedTaskName = currentTreeMapping.getData().getContent();
									
									for(TreeNode<TreeNodeDataType> tree : currentTreeMapping.getChildren())
										Utilities.unmapTree(tree);
									
									selectedSenderMessageName = null;
									selectedSenderMessageType = null;
									selectedReceiverMessageName = null;
									selectedReceiverMessageType = null;
												
									//auto map the input & output message
								    autoMapTaskFields();
									
								}
								else if(currentTreeMapping.getData().getType().equals(Utilities.getINITIATING_MESSAGE_TYPE())) {
									
									if(!mapWith.getData().getType().equals(Utilities.getINITIATING_MESSAGE_TYPE())) {//cannot be mapped
										currentTreeMapping.getData().setIsMappedWithTreeNode(null);
										
										currentTreeMapping = null;
										mappingPhase = 0;
										redrawMap();								
										return;
									}
									
									selectedSenderMessageName = currentTreeMapping.getData().getContent();
									
									for(TreeNode<TreeNodeDataType> tree : currentTreeMapping.getChildren())
										Utilities.unmapTree(tree);
									
									selectedSenderMessageType = null;
								
									autoMapInitiatingType();
								}
								else if(currentTreeMapping.getData().getType().equals(Utilities.getNON_INITIATING_MESSAGE_TYPE())) {
									
									if(!mapWith.getData().getType().equals(Utilities.getNON_INITIATING_MESSAGE_TYPE())) {//cannot be mapped
										currentTreeMapping.getData().setIsMappedWithTreeNode(null);
										
										currentTreeMapping = null;
										mappingPhase = 0;
										redrawMap();								
										return;
									}
									
									selectedReceiverMessageName = currentTreeMapping.getData().getContent();
								
									for(TreeNode<TreeNodeDataType> tree : currentTreeMapping.getChildren())
										Utilities.unmapTree(tree);
									
									selectedReceiverMessageType = null;
									
									autoMapNonInitiatingType();
								}
								else if(currentTreeMapping.getData().getType().equals(Utilities.getINITIATING_TYPE_TYPE())) {
									
									if(!mapWith.getData().getType().equals(Utilities.getINITIATING_TYPE_TYPE())) {//cannot be mapped
										currentTreeMapping.getData().setIsMappedWithTreeNode(null);
										
										currentTreeMapping = null;
										mappingPhase = 0;
										redrawMap();								
										return;
									}
									
									selectedSenderMessageType = currentTreeMapping.getData().getContent();
								}
								else if(currentTreeMapping.getData().getType().equals(Utilities.getNON_INITIATING_TYPE_TYPE())) {
									
									if(!mapWith.getData().getType().equals(Utilities.getNON_INITIATING_TYPE_TYPE())) {//cannot be mapped
										currentTreeMapping.getData().setIsMappedWithTreeNode(null);
										
										currentTreeMapping = null;
										mappingPhase = 0;
										redrawMap();
										
										return;
									}
									
									selectedReceiverMessageType = currentTreeMapping.getData().getContent();
								}
								
								if((!currentTreeMapping.getData().getType().equals(Utilities.getTASK_TYPE()))&&
										(!currentTreeMapping.getData().getType().equals(Utilities.getINITIATING_MESSAGE_TYPE()))&&
										(!currentTreeMapping.getData().getType().equals(Utilities.getNON_INITIATING_MESSAGE_TYPE()))) {
									for(TreeNode<TreeNodeDataType> tree : currentTreeMapping.getChildren())
										Utilities.unmapTree(tree);
								}
								
								
								//END logical validation phase						
								
							}
							
						}
						
						currentTreeMapping = null;
						mappingPhase = 0;
					}
					
					validateEnableOrDisableOKButton();
					redrawMap();
				
				}
				
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	    
	    
	    otherTv = new TreeViewer(composite, SWT.NO_SCROLL);
	    otherTv.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    otherTv.setContentProvider(new TreeContentProvider());
	    otherTv.setLabelProvider(new OtherTreeLabelProvider());
	    
	    
		try {
			if(securityFile != null) {
				List<String> securityNames = Utilities.getSecurityNamesFromSecurityFile(securityFile);
				for(String name : securityNames) {
					List<MappingListReturnType> flattened = Utilities.flatTreeToTreesWithInputOutputMsg(treeNodeWSDLOrGIDL, 0);
					for(MappingListReturnType flat : flattened) {
						if(flat.getTreeNode().getData().getContent().equals(name)) {

							flat.getTreeNode().getData().setSecurity(true);
						}
					}
				}
				
			}
		}
		catch(Exception e) {
			
		}
	    
	    otherTv.setInput(treeNodeWSDLOrGIDL);
	    otherTv.expandAll();
		
	    //locks the expansion
	    otherTv.getTree().addListener(SWT.Collapse, new Listener() {
	    	public void handleEvent(final Event event) {
	    		otherTv.getTree().setRedraw(false);
	    		Display.getDefault().asyncExec(new Runnable() {
	    			public void run() {
	    				((TreeItem)event.item).setExpanded(true);
	    				otherTv.getTree().setRedraw(true);
	    			}
	    		});
	    	}
	    }); 
	    
	    //locks the expansion on keypress
	    otherTv.getTree().addKeyListener(new KeyAdapter() {
	        @Override
	        public void keyPressed(KeyEvent e) {
	           e.doit = false;
	        }
	      });

	    
	    if(SystemUtils.IS_OS_WINDOWS) {
		    otherTv.getTree().addListener(SWT.MeasureItem, new Listener() {
	 	 	   public void handleEvent(Event event) {
	 	 	      TreeItem item = (TreeItem)event.item;
	 	 	      TreeNode<TreeNodeDataType> treeNode = (TreeNode<TreeNodeDataType>)item.getData();
		 	      item.setExpanded(true);
	 	 	      Image image = item.getImage();
	 	 	      if (treeNode != null) {
	 	 	    	  hashOfServicePoints.put(new Point(item.getBounds().x-image.getBounds().width-20, event.y+((otherTv.getTree().getItemHeight())/2)), treeNode);
	 	 	      }
	
	 	 	   }
		    });
	    }
	    else {
	    	populateSecondTreeHashMapForLinux(otherTv.getTree(), hashOfServicePoints);
	    }
	    
	    
		 otherTv.getTree().addPaintListener(new PaintListener() {
		     public void paintControl(PaintEvent e) {	        	
		 		e.gc.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_RED));
		     	Set<Entry<Point, TreeNode<TreeNodeDataType>>> toDraw = hashOfServicePoints.entrySet();
		     	for(Entry<Point, TreeNode<TreeNodeDataType>> entry : toDraw) {
		     		e.gc.drawLine(0, entry.getKey().y, entry.getKey().x, entry.getKey().y);        	
		     	}

	 	 	    canvas.redraw();

		     }
		 });

	 	otherTv.getTree().addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent event) {
				try {
					Point point = new Point(event.x, event.y);					
					TreeNode<TreeNodeDataType> transformationRuleForTreeNode = (TreeNode<TreeNodeDataType>)otherTv.getTree().getItem(point).getData();
					
					if(transformationRuleForTreeNode != null) {
						//i have the node to be applied transformation rule
						
						//if the element is not mapped and is part of the input message, open the semd
						if(!transformationRuleForTreeNode.getData().getType().equals(Utilities.getTASK_TYPE()) && 
								!transformationRuleForTreeNode.getData().getType().equals(Utilities.getINITIATING_MESSAGE_TYPE()) &&
								!transformationRuleForTreeNode.getData().getType().equals(Utilities.getINITIATING_TYPE_TYPE()) && 
								!transformationRuleForTreeNode.getData().getType().equals(Utilities.getNON_INITIATING_MESSAGE_TYPE()) &&
								!transformationRuleForTreeNode.getData().getType().equals(Utilities.getNON_INITIATING_TYPE_TYPE()) &&
								Utilities.sonOfType(transformationRuleForTreeNode, Utilities.getINITIATING_TYPE_TYPE(), (TreeNode<TreeNodeDataType>) otherTv.getInput(), false) &&
								transformationRuleForTreeNode.getData().getComplexOrSimpleType().equals("simpleType")) {
							
							if(!Utilities.isTreeNodeMappedWithItemOfTree(transformationRuleForTreeNode, thisTree.getRootElement())) {
							
								SingleElementMappingDialog semd = null;
								semd = new SingleElementMappingDialog(getShell(), transformationRuleForTreeNode, treeNodeWSDLOrGIDL, "secondTree");
								semd.create();
								
								if(semd.open() == IDialogConstants.OK_ID) {
									transformationRuleForTreeNode.getData().setTransformationRule(semd.getTransformationRule());
									
									validateEnableOrDisableOKButton();
									redrawMap();
								}

							}
							else {
								//open the transformation rule dialog
								TransformationRuleDialog trd = null;
		    					trd = new TransformationRuleDialog(getShell(), transformationRuleForTreeNode, thisTree.getRootElement(), "secondTree",
		    							inputDataTypeVisible, outputDataTypeVisible, treeNodeWSDLOrGIDL);	             
		    					trd.create();	
		    					
								if (trd.open() == IDialogConstants.OK_ID) {
									transformationRuleForTreeNode.getData().setTransformationRule(trd.getTransformationRule());
									
									validateEnableOrDisableOKButton();
									redrawMap();
								}
							}
						}
						
					}
					
				
				}
				catch(Exception e) {
					//the point can be null, do nothing
				}
			}
		});
		 


		if(curHeightFirstTree > curHeightSecondTree)
			composite.setSize(composite.getBounds().width, curHeightFirstTree+35);
		else
			composite.setSize(composite.getBounds().width, curHeightSecondTree+35);
	 	
		sc.setExpandVertical(true);
		sc.setExpandHorizontal(true);

	    if(SystemUtils.IS_OS_WINDOWS)
			sc.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	    else
	    	sc.setMinSize(new Point(composite.getBounds().width, composite.getBounds().height));
		
	    return composite;
	}

	public void redrawMap() {
    	tv.refresh();
    	otherTv.refresh();
		canvas.redraw();
		canvas.update();
	}

	private void drawMappings(GC gc, TreeNode<TreeNodeDataType> treeNode) {
		try {
			int startX = -1, startY = -1, endX = -1, endY = -1;
			
			startX = widthDistance;
			endX = canvas.getClientArea().width-widthDistance;
			
			List<TreeNode<TreeNodeDataType>> mappedWithList = treeNode.getData().getIsMappedWithTreeNode();
			
			if(!mappedWithList.isEmpty()) {
				
				for(TreeNode<TreeNodeDataType> mappedWith : mappedWithList)  {
			
			    	Set<Entry<Point, TreeNode<TreeNodeDataType>>> toDraw = hashOfPoints.entrySet();
					for(Entry<Point, TreeNode<TreeNodeDataType>> entry : toDraw) {
						if(entry.getValue().equals(treeNode)) {
							startY = entry.getKey().y;
							break;
						}
					}
					
			    	Set<Entry<Point, TreeNode<TreeNodeDataType>>> toDraw2 = hashOfServicePoints.entrySet();
					for(Entry<Point, TreeNode<TreeNodeDataType>> entry : toDraw2) {
						if(entry.getValue().equals(mappedWith)) {
							endY = entry.getKey().y;
							break;
						}
					}
					
					gc.drawLine(startX, startY, endX, endY);
			
				}
			}
			
		}
		catch(Exception e) {
			
		}
		
		for(TreeNode<TreeNodeDataType> node : treeNode.getChildren()) {
			drawMappings(gc, node);
		}
			
	}	
	
	private void autoMapTaskFields() {
		try {
			TreeNode<TreeNodeDataType> firstTree = thisTree.getRootElement();
			TreeNode<TreeNodeDataType> secondTree = treeNodeWSDLOrGIDL;
			
			//there is get(0) because the task can have only one mapping
			TreeNode<TreeNodeDataType> opTree = Utilities.getElementsFromTree(firstTree, Utilities.getTASK_TYPE()).get(0).getData().getIsMappedWithTreeNode().get(0);
			
			TreeNode<TreeNodeDataType> opTreeInitiatingMessages = Utilities.getElementsFromTree(opTree, Utilities.getINITIATING_MESSAGE_TYPE()).get(0);
			TreeNode<TreeNodeDataType> opTreeNonInitiatingMessages = Utilities.getElementsFromTree(opTree, Utilities.getNON_INITIATING_MESSAGE_TYPE()).get(0);
			
			TreeNode<TreeNodeDataType> opTreeInitiatingMessagesType = Utilities.getElementsFromTree(opTreeInitiatingMessages, Utilities.getINITIATING_TYPE_TYPE()).get(0);
			TreeNode<TreeNodeDataType> opTreeNonInitiatingMessagesType = Utilities.getElementsFromTree(opTreeNonInitiatingMessages, Utilities.getNON_INITIATING_TYPE_TYPE()).get(0);
			
			selectedSenderMessageName = opTreeInitiatingMessages.getData().getContent();
			selectedSenderMessageType = opTreeInitiatingMessagesType.getData().getContent();
			selectedReceiverMessageName = opTreeNonInitiatingMessages.getData().getContent();
			selectedReceiverMessageType = opTreeNonInitiatingMessagesType.getData().getContent();
			
			Utilities.getElementsFromTree(firstTree, Utilities.getINITIATING_MESSAGE_TYPE()).get(0).getData().addMappedWithTreeNode(opTreeInitiatingMessages);
			Utilities.getElementsFromTree(firstTree, Utilities.getNON_INITIATING_MESSAGE_TYPE()).get(0).getData().addMappedWithTreeNode(opTreeNonInitiatingMessages);
	
			Utilities.getElementsFromTree(firstTree, Utilities.getINITIATING_TYPE_TYPE()).get(0).getData().addMappedWithTreeNode(opTreeInitiatingMessagesType);
			Utilities.getElementsFromTree(firstTree, Utilities.getNON_INITIATING_TYPE_TYPE()).get(0).getData().addMappedWithTreeNode(opTreeNonInitiatingMessagesType);

		}
		catch(Exception e) {
			
		}
	}


	private void autoMapInitiatingType() {
		try {
			TreeNode<TreeNodeDataType> firstTree = thisTree.getRootElement();			
			TreeNode<TreeNodeDataType> opTreeInitiatingMessages = Utilities.getElementsFromTree(firstTree, Utilities.getINITIATING_MESSAGE_TYPE()).get(0).getData().getIsMappedWithTreeNode().get(0);
			TreeNode<TreeNodeDataType> opTreeInitiatingMessagesType = Utilities.getElementsFromTree(opTreeInitiatingMessages, Utilities.getINITIATING_TYPE_TYPE()).get(0);
			
			selectedSenderMessageType = opTreeInitiatingMessagesType.getData().getContent();
			Utilities.getElementsFromTree(firstTree, Utilities.getINITIATING_TYPE_TYPE()).get(0).getData().addMappedWithTreeNode(opTreeInitiatingMessagesType);
		}
		catch(Exception e) {
			
		}
	}

	private void autoMapNonInitiatingType() {
		try {
			TreeNode<TreeNodeDataType> firstTree = thisTree.getRootElement();			
			TreeNode<TreeNodeDataType> opTreeNonInitiatingMessages = Utilities.getElementsFromTree(firstTree, Utilities.getNON_INITIATING_MESSAGE_TYPE()).get(0).getData().getIsMappedWithTreeNode().get(0);
			TreeNode<TreeNodeDataType> opTreeNonInitiatingMessagesType = Utilities.getElementsFromTree(opTreeNonInitiatingMessages, Utilities.getNON_INITIATING_TYPE_TYPE()).get(0);
			
			selectedReceiverMessageType = opTreeNonInitiatingMessagesType.getData().getContent();
			Utilities.getElementsFromTree(firstTree, Utilities.getNON_INITIATING_TYPE_TYPE()).get(0).getData().addMappedWithTreeNode(opTreeNonInitiatingMessagesType);
		}
		catch(Exception e) {
			
		}
	}
	

	
	
	protected void createButtonsForButtonBar(Composite parent) {
		
		boolean showAutoMap = false;
		try {
			URL url = FileLocator.find(ChorevolutionUIPlugin.getDefault().getBundle(), new Path("plugin.properties"), null);
			url = FileLocator.toFileURL(url);
			File file = URIUtil.toFile(URIUtil.toURI(url));
			Scanner input = new Scanner(file);
			String line;				
			while (input.hasNextLine()) {
				line = input.nextLine();
				if(line.contains("synthesisprocessor.automap.show")) {
					if(line.contains("true"))
						showAutoMap = true;
				}
			}
		}
		catch(Exception e) {
			
		}
		
		if(showAutoMap)
			createButton(parent, IDialogConstants.HELP_ID, "USE-CASES-MAP", true);

		createButton(parent, IDialogConstants.DETAILS_ID, "AUTO-MAP", true);
		getButton(IDialogConstants.DETAILS_ID).addMouseListener(new MouseAdapter() {
			@Override
            public void mouseDown(MouseEvent e) {
				Utilities.cleanTreeMapping(thisTree.getRootElement());
				Utilities.autoMapTreeAlpha(thisTree.getRootElement(), treeNodeWSDLOrGIDL);
				tv.refresh();
				otherTv.refresh();
				canvas.redraw();
			}
		});
		

		
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
		
		if(adapterPassed == null)
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		
		if(showAutoMap) {
			getButton(IDialogConstants.HELP_ID).addMouseListener(new MouseAdapter() {
	
				@Override
	            public void mouseDown(MouseEvent e) {
					try {
						Utilities.cleanTreeMapping(thisTree.getRootElement());
						Utilities.autoMapTree(thisTree.getRootElement(), thisTree.getRootElement(), treeNodeWSDLOrGIDL, wsdlOrGidlFile, roleToMap, null);
						selectedTaskName = "";
						selectedSenderMessageName = "";
						selectedSenderMessageType = "";
						selectedReceiverMessageName = "";
						selectedReceiverMessageType = "";
						tv.refresh();
						otherTv.refresh();
						canvas.redraw();
						
					    TreeNode otherTree = Utilities.createCompleteMappingTreeFromMappedTree(((TreeNode)tv.getInput()));
					    //otherTv.setInput(Utilities.cleanTree(otherTree));
					    //otherTv.expandAll();
						
						getButton(IDialogConstants.OK_ID).setEnabled(true);
	
					    
					} catch (SAXException | IOException | ParserConfigurationException | CoreException | URISyntaxException e1) {
						// TODO Auto-generated catch block
					}
				}
	
	        });
		}
		
		
	}

	//returns the treenode selected by mouse click on canvas or null
	public TreeNode<TreeNodeDataType> getSelectedTaskTreeNode(Point point) {
		
    	Set<Entry<Point, TreeNode<TreeNodeDataType>>> toDraw = hashOfPoints.entrySet();
		int xToBeValidated = widthDistance;

		//the x is acceptable
		if((point.x <= xToBeValidated+(heightOffset/2)) && (point.x >= xToBeValidated-(heightOffset/2))) {
			for(Entry<Point, TreeNode<TreeNodeDataType>> entry : toDraw) {
    			int yToBeValidated = entry.getKey().y;
    			
    			if((point.y <= yToBeValidated+(heightOffset/2)+heightPadding) && (point.y >= yToBeValidated-(heightOffset/2)-heightPadding)) {
    				return entry.getValue();
    			}
    		
    		}
		}
		
		return null;
	}
	
	//returns the treenode selected by mouse click on canvas or null
	public TreeNode<TreeNodeDataType> getSelectedServiceTreeNode(Point point) {
		
    	Set<Entry<Point, TreeNode<TreeNodeDataType>>> toDraw = hashOfServicePoints.entrySet();
		int xToBeValidated = canvas.getClientArea().width-widthDistance;

		//the x is acceptable
		if((point.x <= xToBeValidated+(heightOffset/2)) && (point.x >= xToBeValidated-(heightOffset/2))) {
			for(Entry<Point, TreeNode<TreeNodeDataType>> entry : toDraw) {
    			int yToBeValidated = entry.getKey().y;
    			
    			if((point.y <= yToBeValidated+(heightOffset/2)+heightPadding) && (point.y >= yToBeValidated-(heightOffset/2)-heightPadding)) {
    				return entry.getValue();
    			}
    		
    		}
		}
		
		return null;
	}

	public Image getGreenImage() {
		return greenImage;
	}
	
	public Image getOrangeImage() {
		return orangeImage;
	}
	
	public Image getRedImage() {
		return redImage;
	}
	
	public Canvas getCanvas() {
		return canvas;
	}
	
	public Composite getComposite() {
		return composite;
	}
	
	public TreeViewer getFirstTreeViewer() {
		return tv;
	}
	
	public TreeViewer getSecondTreeViewer() {
		return otherTv;
	}
	
	public String getParticipant() {
		return this.participant;
	}
	
	public String getRoleToMap() {
		return this.roleToMap;
	}
	
	public String getTaskName() {
		return this.taskName;
	}

	public String getTaskID() {
		return this.taskID;
	}
	
	public List<MappingAssociation> getOldMappingList() {
		return oldMappingList;
	}

	
	public TreeNode<TreeNodeDataType> getTreeFromTask() {
		return thisTree.getRootElement();
	}
	
	public byte[] getWsdlOrGidlFile() {
			return wsdlOrGidlFile;		
	}
	
	public TreeNode<TreeNodeDataType> getTreeFromWSDLOrGIDL() {
		return this.treeNodeWSDLOrGIDL;
	}
	
	
	public TreeNode<TreeNodeDataType> getTreeMapping() {
		return Utilities.createCompleteMappingTreeFromMappedTree(thisTree.getRootElement());
	}
	
	
	@Override
	protected boolean isResizable() {
		return true;
	}
	
	
	
    
    class TreeContentProvider implements ITreeContentProvider {
    	  public Object[] getChildren(Object arg0) {
    		  //hides the message type
    		  
	    		  try {
					if(Utilities.isGIDL(wsdlOrGidlFile)) {
					  
						//TODO rewrite better
					  if(((eu.chorevolution.studio.eclipse.core.utils.TreeNode<TreeNodeDataType>) arg0).getNumberOfChildren() > 0) {
						  
						  TreeNode<TreeNodeDataType> toReturn = null;
						  
						  if(((eu.chorevolution.studio.eclipse.core.utils.TreeNode<TreeNodeDataType>) arg0).getChildren().get(0).getData().getType().equals(Utilities.getINITIATING_TYPE_TYPE()))
							  toReturn = ((eu.chorevolution.studio.eclipse.core.utils.TreeNode<TreeNodeDataType>) arg0).getChildren().get(0);
						  else if(((eu.chorevolution.studio.eclipse.core.utils.TreeNode<TreeNodeDataType>) arg0).getChildren().get(0).getData().getType().equals(Utilities.getNON_INITIATING_TYPE_TYPE()))
							  toReturn = ((eu.chorevolution.studio.eclipse.core.utils.TreeNode<TreeNodeDataType>) arg0).getChildren().get(0);
	
						  //eliminates request:request and response:response
						  if(toReturn != null) {
							  if(toReturn.getChildren().get(0).getData().getType().equals(Utilities.getInputFakeType())) {
								  if(toReturn.getChildren().get(0).getData().getContent().equals(Utilities.getInputFakeType()))
									  toReturn = toReturn.getChildren().get(0);
								  
									  //eliminates the inputDataType if not needed, it may be only above the inputfaketype
					    			  try {
					    				  if(toReturn.getChildren().get(0).getData().getContent().equals(Utilities.getInputAddedType())) { 
	
						    				  if(Utilities.isAComplexTypeOnTopOfTree(Utilities.getINITIATING_TYPE_TYPE(), (TreeNode<TreeNodeDataType>) tv.getInput(), "left") == true) {
						    					  //don't skip the node
						    				  }
						    				  else {
						    					  //skips the node
						    					  toReturn = toReturn.getChildren().get(0);
						    					  inputDataTypeVisible = false;
						    				  }
						    			  }	  
						    		  }
					    		  	  catch(Exception e) {
					    		  		  e.printStackTrace();
					    		  	  }	
								  
							  }
							  else if(toReturn.getChildren().get(0).getData().getType().equals(Utilities.getOutputFakeType())) {
								  if(toReturn.getChildren().get(0).getData().getContent().equals(Utilities.getOutputFakeType())) {
									  toReturn = toReturn.getChildren().get(0);
									  
									  //eliminates the outputDataType if not needed, it may be only above the outputfaketype
					    			  try {
					    				  if(toReturn.getChildren().get(0).getData().getContent().equals(Utilities.getOutputAddedType())) { 
					    					  TreeNode<TreeNodeDataType> nodeOnLeft = Utilities.getElementsFromTree((TreeNode<TreeNodeDataType>) tv.getInput(), Utilities.getNON_INITIATING_TYPE_TYPE()).get(0).getChildren().get(0);
	
						    				  if(Utilities.isAComplexTypeOnTopOfTree(Utilities.getNON_INITIATING_TYPE_TYPE(), (TreeNode<TreeNodeDataType>) otherTv.getInput(), "right") == true
						    						  && nodeOnLeft.getData().getComplexOrSimpleType().equals("complexType") && nodeOnLeft.getData().getMaxOccurrences().equals("unbounded")) {
						    					  //don't skip the node
						    				  }
						    				  else {
						    					  //skips the node
						    					  toReturn = toReturn.getChildren().get(0);
						    					  outputDataTypeVisible = false;
						    				  }
						    			  }	  
						    		  }
					    		  	  catch(Exception e) {
					    		  		  e.printStackTrace();
					    		  	  }	
									  
								  }
							  }
							  
						  }
						if(toReturn != null) {
				    		  //skip the security nodes
				    		  List<TreeNode<TreeNodeDataType>> treeList = toReturn.getChildren();
				    		  List<TreeNode<TreeNodeDataType>> treeListReturn =  new ArrayList<TreeNode<TreeNodeDataType>>();

				    		  for(TreeNode<TreeNodeDataType> node : treeList) {
				    			  if(!node.getData().isSecurity()) {
				    				  treeListReturn.add(node);
				    			  }
				    		  }
				    		  return treeListReturn.toArray(); 
							
						}
								  
					  }
					}
					else {
						//If is a wsdl skips the first redundant node, but only in the gidl tree
						  TreeNode<TreeNodeDataType> toReturn = null;
						  
						  if(((TreeNode<TreeNodeDataType>) arg0).getNumberOfChildren() > 0) {
							  if(Utilities.sonOfTreeNode(((TreeNode<TreeNodeDataType>) arg0), (TreeNode<TreeNodeDataType>)otherTv.getInput())) {
							  
								  String messageInputTypeToDelete = Utilities.getElementsFromTree(((TreeNode<TreeNodeDataType>) otherTv.getInput()), Utilities.getINITIATING_TYPE_TYPE()).get(0).getData().getContent();
								  String messageOutputTypeToDelete = Utilities.getElementsFromTree(((TreeNode<TreeNodeDataType>) otherTv.getInput()), Utilities.getNON_INITIATING_TYPE_TYPE()).get(0).getData().getContent();
								  
								  if(((TreeNode<TreeNodeDataType>) arg0).getData().getContent().equals(messageInputTypeToDelete))
									  toReturn = ((TreeNode<TreeNodeDataType>) arg0).getChildren().get(0);
								  else if(((TreeNode<TreeNodeDataType>) arg0).getData().getContent().equals(messageOutputTypeToDelete))
									  toReturn = ((TreeNode<TreeNodeDataType>) arg0).getChildren().get(0);
								  
								  if(toReturn != null) {
						    		  //skip the security nodes
						    		  List<TreeNode<TreeNodeDataType>> treeList = toReturn.getChildren();
						    		  List<TreeNode<TreeNodeDataType>> treeListReturn =  new ArrayList<TreeNode<TreeNodeDataType>>();

						    		  for(TreeNode<TreeNodeDataType> node : treeList) {
						    			  if(!node.getData().isSecurity()) {
						    				  treeListReturn.add(node);
						    			  }
						    		  }
						    		  return treeListReturn.toArray(); 
								  }
							  }
						  }


					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
				}

	    		  //skip the security nodes
	    		  List<TreeNode<TreeNodeDataType>> treeList = ((eu.chorevolution.studio.eclipse.core.utils.TreeNode<TreeNodeDataType>) arg0).getChildren();
	    		  List<TreeNode<TreeNodeDataType>> treeListReturn =  new ArrayList<TreeNode<TreeNodeDataType>>();

	    		  for(TreeNode<TreeNodeDataType> node : treeList) {
	    			  if(!node.getData().isSecurity()) {
	    				  treeListReturn.add(node);
	    			  }
	    		  }
	    		  return treeListReturn.toArray(); 

    	  }

    	  public Object getParent(Object arg0) { 
    		  return null;
    	  }

    	  public boolean hasChildren(Object arg0) {
    	    Object[] obj = getChildren(arg0);
    	    return obj == null ? false : obj.length > 0;
    	  }

    	  public Object[] getElements(Object arg0) {
    		  return ((eu.chorevolution.studio.eclipse.core.utils.TreeNode<TreeNodeDataType>) arg0).getChildren().toArray(); 
    	  }

    	  public void dispose() {
    		  
    	  }

    	  public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
    	  }
    	}

    	class TreeLabelProvider implements ILabelProvider {
    	  private List listeners;
    	  private Image file;

    	  public TreeLabelProvider() {
    	    listeners = new ArrayList();
    	    file = greenImage;
    	    
    	  }

    	  
    	  public Image getImage(Object arg0) {
    		 //if i'm in the inputmessage of the choreography and not mapped it should be orange
    		  
    		TreeNode<TreeNodeDataType> originalNode = ((eu.chorevolution.studio.eclipse.core.utils.TreeNode<TreeNodeDataType>) arg0);
    		List<TreeNode<TreeNodeDataType>> type = originalNode.getData().getIsMappedWithTreeNode();
      	    
      	    if(!type.isEmpty())
      	    	return greenImage;
      	    else if(originalNode.getData().getTransformationRule() != null) {
      	    	return greenImage;
      	    }
      	    else if(originalNode.getData().getComplexOrSimpleType().equals("complexType") && Utilities.isAllSubTypesMapped(originalNode, true)) {//is a complex type and all his son are mapped
      	    	return greenImage;
      	    }
      	    else {
      	    	if(Utilities.sonOfType(originalNode, Utilities.getINITIATING_TYPE_TYPE(), (TreeNode<TreeNodeDataType>) tv.getInput(), false))
          	    	return orangeImage;
      	    	else
      	    		return redImage;
      	    }
      	    	
    	  }

    	  public String getText(Object arg0) {
    	    String text = ((eu.chorevolution.studio.eclipse.core.utils.TreeNode<TreeNodeDataType>) arg0).getData().getContent().toString(); 
    	    String type = ((eu.chorevolution.studio.eclipse.core.utils.TreeNode<TreeNodeDataType>) arg0).getData().getType().toString();
    	    
    	    if(type.equals(Utilities.getTASK_TYPE()))
    	    	text = "Task Name: "+text;
    	    else if(type.equals(Utilities.getINITIATING_MESSAGE_TYPE()))
    	    	text = "Input Message: "+text;
    	    else if(type.equals(Utilities.getNON_INITIATING_MESSAGE_TYPE()))
    	    	text = "Output Message: "+text;
    	    else if(type.equals(Utilities.getINITIATING_TYPE_TYPE()))
    	    	text = "Input Message Type: "+text;
    	    else if(type.equals(Utilities.getNON_INITIATING_TYPE_TYPE()))
    	    	text = "Output Message Type: "+text;
    	    else
    	    	text = text+" : "+type + " - (" + Utilities.getLabelSymbolFromTreeNode(((eu.chorevolution.studio.eclipse.core.utils.TreeNode<TreeNodeDataType>) arg0))+ ")";
    	    
    	    return text;
    	  }

    	  public void addListener(ILabelProviderListener arg0) {
    	    listeners.add(arg0);
    	  }

    	  public void dispose() {
    	    // Dispose the images
    		  
    	    if (file != null)
    	      file.dispose();
 
    	  }

    	  public boolean isLabelProperty(Object arg0, String arg1) {
    	    return false;
    	  }

    	  public void removeListener(ILabelProviderListener arg0) {
    	    listeners.remove(arg0);
    	  }
    	}
    

    	class OtherTreeLabelProvider implements ILabelProvider {
    	  private List listeners;
    	  private Image file;

    	  public OtherTreeLabelProvider() {
    	    listeners = new ArrayList();
    	    file = greenImage;
    	    
    	  }

    	  
    	  public Image getImage(Object arg0) {
     		 	
    		  	//if i'm in the outputmessage of the service and not mapped it should be orange
           	    TreeNode<TreeNodeDataType> type = ((eu.chorevolution.studio.eclipse.core.utils.TreeNode<TreeNodeDataType>) arg0);
          	    
          	    if(Utilities.isTreeNodeMappedWithItemOfTree(type, thisTree.getRootElement()) == true)
          	    	return greenImage;
          	    else if(type.getData().getTransformationRule() != null) {//if i have transformation rule, i'm ok
          	    	return greenImage;
          	    }
          	    else {
          	    	if(Utilities.sonOfType(type, Utilities.getNON_INITIATING_TYPE_TYPE(), (TreeNode<TreeNodeDataType>) otherTv.getInput(), false))
              	    	return orangeImage;
          	    	else
          	    		return redImage;
          	    }

    	  }

    	  public String getText(Object arg0) {
      	    String text = ((eu.chorevolution.studio.eclipse.core.utils.TreeNode<TreeNodeDataType>) arg0).getData().getContent().toString(); 
      	    String type = ((eu.chorevolution.studio.eclipse.core.utils.TreeNode<TreeNodeDataType>) arg0).getData().getType().toString();
      	    
      	    if(type.equals(Utilities.getTASK_TYPE()))
      	    	text = "Operation Name: "+text;
      	    else if(type.equals(Utilities.getINITIATING_MESSAGE_TYPE()))
      	    	text = "Input Message: "+text;
      	    else if(type.equals(Utilities.getNON_INITIATING_MESSAGE_TYPE()))
      	    	text = "Output Message: "+text;
      	    else if(type.equals(Utilities.getINITIATING_TYPE_TYPE()))
      	    	text = "Input Message Type: "+text;
      	    else if(type.equals(Utilities.getNON_INITIATING_TYPE_TYPE()))
      	    	text = "Output Message Type: "+text;
      	    else
      	    	text = text+" : "+type  + " - (" +Utilities.getLabelSymbolFromTreeNode(((eu.chorevolution.studio.eclipse.core.utils.TreeNode<TreeNodeDataType>) arg0))+ ")";
      	          	    
      	    return text;    	    
    	  }

    	  public void addListener(ILabelProviderListener arg0) {
    	    listeners.add(arg0);
    	  }

    	  public void dispose() {
    	    // Dispose the images
    		  
    	    if (file != null)
    	      file.dispose();
 
    	  }

    	  public boolean isLabelProperty(Object arg0, String arg1) {
    	    return false;
    	  }

    	  public void removeListener(ILabelProviderListener arg0) {
    	    listeners.remove(arg0);
    	  }
    	}
    
    
    	public boolean isAdapterFinished() {
    		List<TreeNode<TreeNodeDataType>> treeToValidateList = Utilities.getElementsFromTree(thisTree.getRootElement(), Utilities.getNON_INITIATING_MESSAGE_TYPE());
    		TreeNode<TreeNodeDataType> treeToValidate = treeToValidateList.get(0);
    		return Utilities.isTreeMappingComplete(treeToValidate);

    	}
    
    	private void validateEnableOrDisableOKButton() {
    		if(isAdapterFinished()) {
    				getButton(IDialogConstants.OK_ID).setEnabled(true);
    				getButton(IDialogConstants.OK_ID).setFocus();
    		}
    		else {
    			getButton(IDialogConstants.OK_ID).setEnabled(false);
    		}
    	}
    
    
    	public void populateFirstTreeHashMapForLinux(org.eclipse.swt.widgets.Tree tree, HashMap<Point, TreeNode<TreeNodeDataType>> hashOfPoints) {
    		
    		curHeightFirstTree = 0;
    		
    		for(int i=0; i<tree.getItemCount(); i++) {
    			populateFirstTreeItemHashMapForLinux(tree.getItem(i), hashOfPoints, tree.getItemHeight()+(tree.getItem(0).getBounds().y*2));
    			curHeightFirstTree += tree.getItemHeight()+(tree.getItem(0).getBounds().y*2);
    		}
    		
    	}
    	
    	public void populateFirstTreeItemHashMapForLinux(org.eclipse.swt.widgets.TreeItem treeItem, HashMap<Point, TreeNode<TreeNodeDataType>> hashOfPoints, int staticIncr) {
    		
    		hashOfPoints.put(new Point(treeItem.getBounds().x+treeItem.getBounds().width, curHeightFirstTree+(staticIncr/2)), (TreeNode<TreeNodeDataType>)treeItem.getData());
    		curHeightFirstTree += staticIncr;
    		
    		for(int i=0; i<treeItem.getItemCount(); i++) {
    			populateFirstTreeItemHashMapForLinux(treeItem.getItem(i), hashOfPoints, staticIncr);
    		}
    		
    	}
    	
    	
    	public void populateSecondTreeHashMapForLinux(org.eclipse.swt.widgets.Tree tree, HashMap<Point, TreeNode<TreeNodeDataType>> hashOfPoints) {
    		
    		curHeightSecondTree = 0;
    		
    		for(int i=0; i<tree.getItemCount(); i++) {
    			populateSecondTreeItemHashMapForLinux(tree.getItem(i), hashOfPoints, tree.getItemHeight()+(tree.getItem(0).getBounds().y*2));
    			curHeightSecondTree += tree.getItemHeight()+(tree.getItem(0).getBounds().y*2);
    		}
    		
    	}
    	
    	public void populateSecondTreeItemHashMapForLinux(org.eclipse.swt.widgets.TreeItem treeItem, HashMap<Point, TreeNode<TreeNodeDataType>> hashOfPoints, int staticIncr) {
    		hashOfPoints.put(new Point(treeItem.getBounds().x+treeItem.getImage().getBounds().width-40, curHeightSecondTree+(staticIncr/2)), (TreeNode<TreeNodeDataType>)treeItem.getData());
    		curHeightSecondTree += staticIncr;
    		
    		for(int i=0; i<treeItem.getItemCount(); i++) {
    			populateSecondTreeItemHashMapForLinux(treeItem.getItem(i), hashOfPoints, staticIncr);
    		}
    		
    	}
    	
}
