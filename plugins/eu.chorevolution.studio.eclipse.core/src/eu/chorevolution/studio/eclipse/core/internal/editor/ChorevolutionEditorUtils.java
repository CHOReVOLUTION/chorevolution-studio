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
package eu.chorevolution.studio.eclipse.core.internal.editor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sirius.business.api.dialect.DialectManager;
import org.eclipse.sirius.business.api.dialect.command.CreateRepresentationCommand;
import org.eclipse.sirius.business.api.helper.SiriusResourceHelper;
import org.eclipse.sirius.business.api.session.DefaultLocalSessionCreationOperation;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.sirius.tools.api.command.semantic.AddSemanticResourceCommand;
import org.eclipse.sirius.ui.business.api.dialect.DialectUIManager;
import org.eclipse.sirius.ui.business.api.viewpoint.ViewpointSelection;
import org.eclipse.sirius.ui.business.api.viewpoint.ViewpointSelectionCallbackWithConfimation;
import org.eclipse.sirius.ui.business.internal.commands.ChangeViewpointSelectionCommand;
import org.eclipse.sirius.viewpoint.DRepresentation;
import org.eclipse.sirius.viewpoint.DRepresentationDescriptor;
import org.eclipse.sirius.viewpoint.description.RepresentationDescription;
import org.eclipse.sirius.viewpoint.description.Viewpoint;
import org.eclipse.ui.PlatformUI;

import eu.chorevolution.studio.eclipse.core.ChorevolutionCorePlugin;
import eu.chorevolution.studio.eclipse.core.ChorevolutionCoreUtils;

/**
 * This class provides a utility method to manage the graphical editor
 * 
 */
public class ChorevolutionEditorUtils {

	public static final String SIRIUS_EDITOR_FILE_EXTENSION = ".aird";

	/**
	 * 
	 * @param model
	 *            {@link IFile} that represents the model
	 * @return {@link IFile} that represents the graphical editor
	 */
	public static IFile getGraphicalEditor(IFile model) {
		return ChorevolutionCorePlugin.getWorkspace().getRoot()
				.getFileForLocation(model.getLocation().removeLastSegments(1).append(
						ChorevolutionCoreUtils.removeExtension(model.getName()) + SIRIUS_EDITOR_FILE_EXTENSION));
	}

	public static void openDiagram(IFile airdFile, IProgressMonitor monitor) {

		// Step 1: get the .aird file and the corresponding Sirius Session
		URI myRepresentationsFileURI = URI.createPlatformResourceURI(airdFile.getFullPath().toString(), true);
		Session siriusSession = SessionManager.INSTANCE.getSession(myRepresentationsFileURI, monitor);
		
		// Step 2: get the first DRepresentation to open contained in .aird file
		//final DRepresentationDescriptor repDescriptor = (DRepresentationDescriptor) getRepresentationDescriptors("Diagram", siriusSession).toArray()[0];
		final DRepresentationDescriptor repDescriptor = (DRepresentationDescriptor)DialectManager.INSTANCE.getAllRepresentationDescriptors(siriusSession).toArray()[0];
		DRepresentation myRepresentation = repDescriptor.getRepresentation();
	
		// Step 3: open representation
		DialectUIManager.INSTANCE.openEditor(siriusSession, myRepresentation, monitor);

	}
	
		/**
	 * 
	 * @param airdFile
	 *            the aird file should be created
	 * @param modelFile
	 *            the current file for which an aird file should be created
	 * @return {@link IStatus} that indicate if the creation is terminated
	 *         successfully
	 */
    public static IStatus createAird(IFile airdFile, final IFile modelFile, IProgressMonitor monitor) {
		try {
			Set<Viewpoint> availableViewpoints = ViewpointSelection.getViewpoints(modelFile.getFileExtension());
			if (availableViewpoints.isEmpty()) {
				return new Status(IStatus.ERROR, ChorevolutionCorePlugin.PLUGIN_ID, NLS.bind(
						ChorevolutionEditorMessage.OpenGraphicalEditor_viewpointMissingError, modelFile.getFullPath()));
			} else {
				// Now we have to create an aird file
				URI airdFileURI = URI.createPlatformResourceURI(airdFile.getFullPath().toString(), true);

				// Create a Session from the session model URI
				org.eclipse.sirius.business.api.session.SessionCreationOperation sessionCreationOperation = new DefaultLocalSessionCreationOperation(
						airdFileURI, monitor);
				sessionCreationOperation.execute();

				// create viewpoint
				Session session = SessionManager.INSTANCE.getSession(airdFileURI, monitor);

				URI fileURI = URI.createPlatformResourceURI(modelFile.getFullPath().toString(), true);

				// adding the resource to the session
				AddSemanticResourceCommand addCommandToSession = new AddSemanticResourceCommand(session, fileURI,
						monitor);
				session.getTransactionalEditingDomain().getCommandStack().execute(addCommandToSession);
				session.save(monitor);

				// find and add viewpoint

				Set<Viewpoint> viewpoints = new HashSet<Viewpoint>();
				for (Viewpoint p : availableViewpoints)
					viewpoints.add(SiriusResourceHelper.getCorrespondingViewpoint(session, p));

				ViewpointSelection.Callback callback = new ViewpointSelectionCallbackWithConfimation();

				RecordingCommand command = new ChangeViewpointSelectionCommand(session, callback, viewpoints,
						new HashSet<Viewpoint>(), true, monitor);

				TransactionalEditingDomain domain = session.getTransactionalEditingDomain();
				domain.getCommandStack().execute(command);

				Object[] elements1 = session.getSemanticResources().toArray();

				Resource resource = (Resource) elements1[elements1.length - 1];

				final EObject rootObject = resource.getContents().get(0);

				Collection<RepresentationDescription> descriptions = DialectManager.INSTANCE
						.getAvailableRepresentationDescriptions(session.getSelectedViewpoints(false), rootObject);
				if (descriptions.isEmpty()) {
					return new Status(IStatus.ERROR, ChorevolutionCorePlugin.PLUGIN_ID, NLS.bind(
							ChorevolutionEditorMessage.OpenGraphicalEditor_representationMissingError, rootObject));
				}

				RepresentationDescription description = descriptions.iterator().next();

				Command createViewCommand = new CreateRepresentationCommand(session, description, rootObject,
						modelFile.getName(), monitor);

				session.getTransactionalEditingDomain().getCommandStack().execute(createViewCommand);

				SessionManager.INSTANCE.notifyRepresentationCreated(session);

				// save session and refresh workspace
				session.save(monitor);

				modelFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, monitor);
			}
		} catch (final CoreException e) {
			return new Status(IStatus.ERROR, ChorevolutionCorePlugin.PLUGIN_ID,
					NLS.bind(ChorevolutionEditorMessage.OpenGraphicalEditor_openError, modelFile.getFullPath(),
							e.getLocalizedMessage()));
		}

		return new Status(IStatus.OK, ChorevolutionCorePlugin.PLUGIN_ID, NLS
				.bind(ChorevolutionEditorMessage.OpenGraphicalEditor_createGraphicalEditor, modelFile.getFullPath()));
	}
    
    
    //use this method when you have find the name of the representationDescriptionName e.g. chorArchModel, SecurityModel,...
  	/**
       * Get all representation descriptors with the given representation
       * description name in the given session.
       * 
       * @param representationDescriptionName
       *            the name of the representation description. <code>null</code>
       *            is not excepted.
       * @param alternateSession
       *            the session to look for representation
       * @return a {@link Collection} with all representations retrieved.
       */
      protected static final Collection<DRepresentationDescriptor> getRepresentationDescriptors(final String representationDescriptionName, final Session alternateSession) {
          final Collection<DRepresentationDescriptor> allRepDescriptors = DialectManager.INSTANCE.getAllRepresentationDescriptors(alternateSession);

          final Collection<DRepresentationDescriptor> repDescriptors = new HashSet<DRepresentationDescriptor>();

          for (final DRepresentationDescriptor repDescriptor : allRepDescriptors) {
              final RepresentationDescription desc = repDescriptor.getDescription();
              if (representationDescriptionName.equals(desc.getName())) {
                  repDescriptors.add(repDescriptor);
              }
          }
          return repDescriptors;
      }

	/**
	 * If a file is renamed/moved this method deletes the old aird file if it
	 * exists and creates a new one for the new location
	 * 
	 * @param source
	 *            the source before the rename/move
	 * @param newPath
	 *            the full path to the new resource
	 */
	public static void renameAirdFile(final IFile source, final IPath newPath) {
		// update aird file
		new WorkspaceJob("moved aird file") {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) {
				IPath oldAirdPath = source.getFullPath().removeLastSegments(1).append(source.getName() + ".aird");

				IFile oldAirdFile = source.getProject().getWorkspace().getRoot().getFile(oldAirdPath);

				String newAirdString = newPath.toString();

				newAirdString = (newAirdString.substring(0, newAirdString.length()) + ".aird");

				IPath newAirdPath = new Path(newAirdString);

				IFile newFile = source.getProject().getWorkspace().getRoot().getFile(newPath);

				URI newFileUri = (URI.createURI(newFile.getLocationURI().toString()));

				if (oldAirdFile.exists()) {

					try {
						source.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
						oldAirdFile.copy(newAirdPath, false, new NullProgressMonitor());

						IFile newAirdFile = source.getProject().getWorkspace().getRoot().getFile(newAirdPath);

						URI newAirdUri = (URI.createURI(newAirdFile.getLocationURI().toString()));

						BufferedReader reader = new BufferedReader(
								new FileReader(newAirdFile.getLocation().toString()));
						String line = null;
						StringBuilder stringBuilder = new StringBuilder();

						while ((line = reader.readLine()) != null) {
							stringBuilder.append(line);

						}

						String result = stringBuilder.toString();
						result = result.replace(source.getName().toString(), newFile.getName().toString());

						FileOutputStream stream = new FileOutputStream(newAirdFile.getLocation().toString(), false);
						byte[] myBytes = result.getBytes();
						stream.write(myBytes);
						stream.close();

						source.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
						oldAirdFile.delete(true, new NullProgressMonitor());

						Session newSession = SessionManager.INSTANCE.getSession(newAirdUri, new NullProgressMonitor());

						Collection<Resource> resources = newSession.getSemanticResources();

						// Sirius deleted the model entry
						if (resources.size() == 0) {
							PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
								public void run() {
									MessageDialog.openError(
											PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error",
											"It was not possible to copy the layout. We are sorry for the 	inconvenience.");
								}
							});

							AddSemanticResourceCommand addResourceToSession = new AddSemanticResourceCommand(newSession,
									newFileUri, new NullProgressMonitor());

							newSession.getTransactionalEditingDomain().getCommandStack().execute(addResourceToSession);
							newSession.save(new NullProgressMonitor());

							newSession.save(new NullProgressMonitor());

							// This is tricky:
							// Sirius takes in the session the uri to the model,
							// but in the aird it only needs the name of the
							// file. This makes problems in subfolders.
							// Therefore we replace the project relative path
							// with the file name
							BufferedReader reader1 = new BufferedReader(
									new FileReader(newAirdFile.getLocation().toString()));
							String line1 = null;
							StringBuilder stringBuilder1 = new StringBuilder();

							while ((line1 = reader1.readLine()) != null) {
								stringBuilder1.append(line1);

							}

							String result1 = stringBuilder1.toString();
							result1 = result1.replace(newFile.getProjectRelativePath().toString(), newFile.getName());

							FileOutputStream stream1 = new FileOutputStream(newAirdFile.getLocation().toString(),
									false);
							byte[] myBytes1 = result1.getBytes();
							stream1.write(myBytes1);
							stream1.close();

						}
						newFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

					} catch (final CoreException e) {
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
							public void run() {
								MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
										"Error", "Renaming resource failed: " + e.getLocalizedMessage());
							}
						});
					} catch (final FileNotFoundException e) {
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
							public void run() {
								MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
										"Error", "Renaming resource failed: " + e.getLocalizedMessage());
							}
						});
					} catch (final IOException e) {
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
							public void run() {
								MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
										"Error", "Renaming resource failed: " + e.getLocalizedMessage());
							}
						});
					}
				}

				return Status.OK_STATUS;
			}
		}.schedule();

	}

	/**
	 * If a file is copied, this method creates a new aird file for the copy, if
	 * the origin al had one
	 * 
	 * @param source
	 *            the source before the copy
	 * @param newPath
	 *            the full path to the new resource
	 */
	public static void copyAirdFile(final IFile source, final IPath newPath) {
		// copy aird file
		new WorkspaceJob("New aird file for copy") {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) {
				IPath oldAirdPath = source.getFullPath().removeLastSegments(1).append(source.getName() + ".aird");

				IFile oldAirdFile = source.getProject().getWorkspace().getRoot().getFile(oldAirdPath);

				String newAirdString = newPath.toString();

				newAirdString = (newAirdString.substring(0, newAirdString.length()) + ".aird");

				IPath newAirdPath = new Path(newAirdString);

				IFile newFile = source.getProject().getWorkspace().getRoot().getFile(newPath);

				URI newFileUri = (URI.createURI(newFile.getLocationURI().toString()));

				if (oldAirdFile.exists()) {

					try {
						source.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
						oldAirdFile.copy(newAirdPath, false, new NullProgressMonitor());

						IFile newAirdFile = source.getProject().getWorkspace().getRoot().getFile(newAirdPath);

						URI newAirdUri = (URI.createURI(newAirdFile.getLocationURI().toString()));

						BufferedReader reader = new BufferedReader(
								new FileReader(newAirdFile.getLocation().toString()));
						String line = null;
						StringBuilder stringBuilder = new StringBuilder();

						while ((line = reader.readLine()) != null) {
							stringBuilder.append(line);

						}

						String result = stringBuilder.toString();
						result = result.replace(source.getProjectRelativePath().toString(),
								newFile.getProjectRelativePath().toString());

						FileOutputStream stream = new FileOutputStream(newAirdFile.getLocation().toString(), false);
						byte[] myBytes = result.getBytes();
						stream.write(myBytes);
						stream.close();

						Session newSession = SessionManager.INSTANCE.getSession(newAirdUri, new NullProgressMonitor());

						Collection<Resource> resources = newSession.getSemanticResources();

						// Sirius deleted the model entry
						if (resources.size() == 0) {

							PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
								public void run() {
									MessageDialog.openError(
											PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error",
											"It was not possible to copy the layout. We are sorry for the 	inconvenience.");
								}
							});
							AddSemanticResourceCommand addResourceToSession = new AddSemanticResourceCommand(newSession,
									newFileUri, new NullProgressMonitor());

							newSession.getTransactionalEditingDomain().getCommandStack().execute(addResourceToSession);
							newSession.save(new NullProgressMonitor());

							newSession.save(new NullProgressMonitor());

							// This is tricky:
							// Sirius takes in the session the uri to the model,
							// but in the aird it only needs the name of the
							// file. This makes problems in subfolders.
							// Therefore we replace the project relative path
							// with the file name
							BufferedReader reader1 = new BufferedReader(
									new FileReader(newAirdFile.getLocation().toString()));
							String line1 = null;
							StringBuilder stringBuilder1 = new StringBuilder();

							while ((line1 = reader1.readLine()) != null) {
								stringBuilder1.append(line1);

							}

							String result1 = stringBuilder1.toString();
							result1 = result1.replace(newFile.getProjectRelativePath().toString(), newFile.getName());

							FileOutputStream stream1 = new FileOutputStream(newAirdFile.getLocation().toString(),
									false);
							byte[] myBytes1 = result1.getBytes();
							stream1.write(myBytes1);
							stream1.close();

						}

						newFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

					} catch (final CoreException e) {
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
							public void run() {
								MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
										"Error", "Copying resource failed: " + e.getLocalizedMessage());
							}
						});

					} catch (final FileNotFoundException e) {
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
							public void run() {
								MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
										"Error", "Copying resource failed: " + e.getLocalizedMessage());
							}
						});
					} catch (final IOException e) {
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
							public void run() {
								MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
										"Error", "Copying resource failed: " + e.getLocalizedMessage());
							}
						});
					}
				}

				return Status.OK_STATUS;
			}
		}.schedule();
	}

	/**
	 * if a file is deleted, the according aird file is deleted
	 * 
	 * @param source
	 *            the deleted source
	 */
	public static void deleteAirdFile(final IFile source) {
		// delete aird file
		new WorkspaceJob("New aird file for copy") {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) {
				IPath oldRepresentationPath = source.getFullPath().removeLastSegments(1)
						.append(source.getName() + ".aird");

				IFile oldAirdFile = source.getProject().getWorkspace().getRoot().getFile(oldRepresentationPath);
				if (oldAirdFile.exists()) {
					try {
						oldAirdFile.delete(false, new NullProgressMonitor());

						source.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

					} catch (CoreException e) {
						ChorevolutionCorePlugin.log("Deleting resource failed.", e);
					}
				}

				return Status.OK_STATUS;
			}
		}.schedule();
	}

}
