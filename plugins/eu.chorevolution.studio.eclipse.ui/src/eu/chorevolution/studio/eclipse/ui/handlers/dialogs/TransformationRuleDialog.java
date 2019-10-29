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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xml.sax.SAXException;

import eu.chorevolution.studio.eclipse.core.utils.TreeNode;
import eu.chorevolution.studio.eclipse.core.utils.TreeNodeDataType;
import eu.chorevolution.studio.eclipse.ui.ChorevolutionUIMessages;
import eu.chorevolution.studio.eclipse.ui.Utilities;

public class TransformationRuleDialog extends TitleAreaDialog {

    Composite composite;
	private TreeNode<TreeNodeDataType> treeNodeSelected;
	private TreeNode<TreeNodeDataType> firstTree;
	private TreeNode<TreeNodeDataType> otherTree;
	private Text transformationRuleText;
	private String transformationRule;
	private String whichTree;
    private boolean inputDataTypeVisible;
    private boolean outputDataTypeVisible;
	
	public TransformationRuleDialog(Shell shell, TreeNode<TreeNodeDataType> treeNodeSelected, TreeNode<TreeNodeDataType> firstTree, 
			String whichTree, boolean inputDataTypeVisible, boolean outputDataTypeVisible, TreeNode<TreeNodeDataType> otherTree) {
		
		super(shell);
		this.treeNodeSelected = treeNodeSelected;
		this.firstTree = firstTree;
		this.whichTree = whichTree;
		this.inputDataTypeVisible = inputDataTypeVisible;
		this.outputDataTypeVisible = outputDataTypeVisible;
		this.otherTree = otherTree;
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

		setTitle("Transformation Rule Setting");

		setMessage("Please provide the Transformation Rule for the Item "+treeNodeSelected.getData().getContent(), IMessageProvider.INFORMATION);

		return contents;
	}

	protected Control createDialogArea(Composite parent) {
		
		composite = new Composite(parent, SWT.FILL);
		
		GridLayout layout = new GridLayout(2, false);
	    layout.makeColumnsEqualWidth = false;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
	    Label description1 = new Label(composite, SWT.NULL);
	    description1.setText("Transformation Rule");
	    
	    transformationRuleText = new Text(composite, SWT.WRAP | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
	    GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
	    gridData.horizontalSpan = 2;
	    gridData.grabExcessVerticalSpace = true;
	    gridData.grabExcessHorizontalSpace = true;
	    transformationRuleText.setLayoutData(gridData);
	    
	    if(treeNodeSelected.getData().getTransformationRule() != null)
	    	transformationRuleText.setText(treeNodeSelected.getData().getTransformationRule());
		
		Group groupComposite = new Group(composite, SWT.SHADOW_ETCHED_IN);
		groupComposite.setText("Functions");
		groupComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1));
		GridLayout layoutComposite = new GridLayout();
		layoutComposite.numColumns = 3;
		layoutComposite.horizontalSpacing = 8;
		groupComposite.setLayout(layoutComposite);
	    
	    Button exampleConcat = new Button(groupComposite, SWT.NONE);
		exampleConcat.setText("   +   ");
	    exampleConcat.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {				
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				updateText(Utilities.getTransformationRuleConcat());
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {				
			}
		});
		
	    Button exampleValueOf = new Button(groupComposite, SWT.NONE);
		exampleValueOf.setText("valueof()");
	    exampleValueOf.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {				
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				updateText(Utilities.getTransformationRuleValueof()+"(elementName, \"elementValue_0\":\"returnValue_0\", \"elementValue_1\":\"returnValue_1\")");
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {				
			}
		});
	    
	    Button exampleStatic = new Button(groupComposite, SWT.NONE);
		exampleStatic.setText("static value");
	    exampleStatic.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {				
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				updateText(Utilities.getTransformationRuleStaticvalue());
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {				
			}
		});
	    
	    Button exampleJson = new Button(groupComposite, SWT.NONE);
		exampleJson.setText("json()");
	    exampleJson.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {				
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				updateText(Utilities.getTransformationRuleJson()+"(elementName, \"elementValue\")");
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {				
			}
		});
	    
	    Button exampleParseDate = new Button(groupComposite, SWT.NONE);
		exampleParseDate.setText("parseDate()");
	    exampleParseDate.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {				
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				updateText(Utilities.getTransformationRuleParseDate()+"(elementName, \"regexpToBeParsed\")");
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {				
			}
		});
	    
	    
		Group groupSymbols = new Group(composite, SWT.SHADOW_ETCHED_IN);
		groupSymbols.setText("Members of the transformation rule");
		groupSymbols.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1));
		GridLayout layoutSymbols = new GridLayout();
		layoutSymbols.numColumns = 3;
		layoutSymbols.horizontalSpacing = 8;
		groupSymbols.setLayout(layoutSymbols);
		
		List<String> listSymbols = null;
		
		if(whichTree.equals("secondTree"))
			listSymbols = Utilities.getFullPathOfElementsMappedWith(firstTree, firstTree, treeNodeSelected);
		else
			listSymbols = Utilities.getFullPathOfElementsMapped(firstTree, treeNodeSelected, outputDataTypeVisible);
			
		
		for(String symbol : listSymbols) {
			
		    Button symbolButton = new Button(groupSymbols, SWT.NONE);
			symbolButton.setText(symbol);
		    symbolButton.addMouseListener(new MouseListener() {
				
				@Override
				public void mouseUp(MouseEvent e) {				
				}
				
				@Override
				public void mouseDown(MouseEvent e) {
					updateText(symbol);
				}
				
				@Override
				public void mouseDoubleClick(MouseEvent e) {				
				}
			});
		    
			
		}
		
		
	    if(whichTree.equals("firstTree")) {
			Label emptyLabel = new Label(composite, SWT.NONE);
			emptyLabel.setText("");
			
	    	
	    	Group groupRequest = new Group(composite, SWT.SHADOW_ETCHED_IN);
			groupRequest.setText("Fields From Request");
			groupRequest.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1));
			GridLayout layoutRequest = new GridLayout();
			layoutRequest.numColumns = 3;
			layoutRequest.horizontalSpacing = 8;
			groupRequest.setLayout(layoutRequest);
			
			TreeNode<TreeNodeDataType> startingTree = Utilities.getElementsFromTree(otherTree, Utilities.getINITIATING_TYPE_TYPE()).get(0);
			List<String> listRequest = Utilities.getFullPathOfRequestElements(startingTree, otherTree, true);

			
			for(String request : listRequest) {
				
			    Button symbolRequest = new Button(groupRequest, SWT.NONE);
				symbolRequest.setText(request);
			    symbolRequest.addMouseListener(new MouseListener() {
					
					@Override
					public void mouseUp(MouseEvent e) {				
					}
					
					@Override
					public void mouseDown(MouseEvent e) {
						updateText(Utilities.getTransformationRuleInputMessage()+"("+request+")");
					}
					
					@Override
					public void mouseDoubleClick(MouseEvent e) {				
					}
				});
			    
				
			}
			
			
		
	    }
		
		
	    		
		return composite;
	}


	
	
	protected void createButtonsForButtonBar(Composite parent) {
		
		if(treeNodeSelected.getData().getTransformationRule() != null) {
			createButton(parent, IDialogConstants.NO_ID, "Remove", true);

			getButton(IDialogConstants.NO_ID).addMouseListener(new MouseAdapter() {
				
				@Override
	            public void mouseDown(MouseEvent e) {
					treeNodeSelected.getData().setTransformationRule(null);	
					TransformationRuleDialog.this.close();
				}
	
	        });
		}
		
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);		
		
	}

	public Composite getComposite() {
		return composite;
	}
	
	public TreeNode<TreeNodeDataType> getTreeNodeSelected() {
		return this.treeNodeSelected;
	}
	
	public String getTransformationRule() {
			return transformationRule;		
	}
	
	@Override
	protected boolean isResizable() {
		return true;
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		// TODO Auto-generated method stub
		
		if(buttonId == IDialogConstants.OK_ID)
			transformationRule = transformationRuleText.getText();
		
		super.buttonPressed(buttonId);

	}
	
	protected void updateText(String toAdd) {
		int startPos = transformationRuleText.getSelection().x-1;
		int endPos = transformationRuleText.getSelection().y;
		String text = transformationRuleText.getText(0, startPos);
		String endText = transformationRuleText.getText(endPos, transformationRuleText.getText().length());
		text += toAdd;
		text += endText;
		transformationRuleText.setText(text);
		transformationRuleText.setSelection(startPos+toAdd.length()+1);
	}

    
	
}
