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
package eu.chorevolution.studio.eclipse.core;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import org.apache.cxf.common.util.StringUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;

import eu.chorevolution.modelingnotations.chorarch.ChorArchModel;
import eu.chorevolution.modelingnotations.chorarch.impl.ChorarchPackageImpl;
import eu.chorevolution.modelingnotations.security.AuthenticationTypeForwarded;
import eu.chorevolution.modelingnotations.security.SecurityModel;
import eu.chorevolution.modelingnotations.security.impl.SecurityPackageImpl;
import eu.chorevolution.modelingnotations.servicething.ServiceThingModel;
import eu.chorevolution.modelingnotations.servicething.impl.ServicethingPackageImpl;
import eu.chorevolution.studio.eclipse.core.utils.maven.ExecGoalsMavenProject;
import eu.chorevolution.studio.eclipse.core.utils.maven.UpdateMavenProjectJob;
import eu.chorevolution.studio.eclipse.core.utils.syncope.ServiceAuthenticationType;

public final class ChorevolutionCoreUtils {
	public static final String MARKER_ID = "eu.chorevolution.studio.eclipse.core.problemmarker";

	/** URL file schema */
	public static final String FILE_SCHEME = "file";
	/** XML encoding */
	public static final String ENCODING_UTF8 = "UTF-8";
	/**
	 * The extension separator character.
	 */
	public static final char EXTENSION_SEPARATOR = '.';

	public static void saveEObject(EObject model, IFile modelFile) {
		try {
			// Create a resource set
			ResourceSet resourceSet = new ResourceSetImpl();

			// Get the URI of the model file.
			URI fileURI = URI.createPlatformResourceURI(modelFile.getFullPath().toOSString(), true);

			// Create a resource for this file.
			Resource resource = resourceSet.createResource(fileURI);

			// Add the initial model object to the contents.
			if (model != null) {
				resource.getContents().add(model);
			}

			// Save the contents of the resource to the file system.
			Map<Object, Object> options = new HashMap<>();
			options.put(XMLResource.OPTION_ENCODING, ENCODING_UTF8);
			resource.save(options);

		} catch (IOException e) {
			// TODO externalize message
			StatusHandler.log(new Status(IStatus.ERROR, ChorevolutionCorePlugin.PLUGIN_ID,
					"An error occurred saving resource " + model.eResource().getURI(), e));
		}
	}

	// create a generic method
	public static ServiceThingModel loadServiceThingModel(URI chorArchURI) {
		ServicethingPackageImpl.init();

		Resource resource = new XMIResourceFactoryImpl().createResource(chorArchURI);

		try {
			// load the resource
			resource.load(null);

		} catch (IOException e) {
			// TODO externalize message
			StatusHandler.log(new Status(IStatus.ERROR, ChorevolutionCorePlugin.PLUGIN_ID,
					"An error occurred loading resource " + chorArchURI, e));
		}

		ServiceThingModel serviceThingModel = (ServiceThingModel) resource.getContents().get(0);

		return serviceThingModel;

	}

	public static SecurityModel loadSecurityModel(URI securityModelURI) {
		SecurityPackageImpl.init();

		Resource resource = new XMIResourceFactoryImpl().createResource(securityModelURI);

		try {
			// load the resource
			resource.load(null);
		} catch (IOException e) {
			// TODO externalize message
			StatusHandler.log(new Status(IStatus.ERROR, ChorevolutionCorePlugin.PLUGIN_ID,
					"An error occurred loading resource " + securityModelURI, e));
		}

		SecurityModel securityModel = (SecurityModel) resource.getContents().get(0);

		return securityModel;

	}

	public static ChorArchModel loadChoreographyArchitectureModel(URI choreographyArchitectureModelURI) {
		ChorarchPackageImpl.init();

		Resource resource = new XMIResourceFactoryImpl().createResource(choreographyArchitectureModelURI);

		try {
			// load the resource
			resource.load(null);

		} catch (IOException e) {
			// TODO externalize message
			StatusHandler.log(new Status(IStatus.ERROR, ChorevolutionCorePlugin.PLUGIN_ID,
					"An error occurred loading resource " + choreographyArchitectureModelURI, e));
		}

		ChorArchModel choreographyArchitectureModel = (ChorArchModel) resource.getContents().get(0);

		return choreographyArchitectureModel;

	}

	public static ServiceAuthenticationType getAuthenticationType(URI securityModelURI) {
		SecurityModel securityModel = loadSecurityModel(securityModelURI);

		if (securityModel.getSecuritypolicyset().getAuthentication().get(0).getAuthNElement() == null) {
			return ServiceAuthenticationType.NONE;
		} else if (securityModel.getSecuritypolicyset().getAuthentication().get(0)
				.getAuthNTypeForwarded() == AuthenticationTypeForwarded.GENERIC_ACCOUNT) {
			return ServiceAuthenticationType.SHARED;
		} else if (securityModel.getSecuritypolicyset().getAuthentication().get(0)
				.getAuthNTypeForwarded() == AuthenticationTypeForwarded.USER_ACCOUNT) {
			return ServiceAuthenticationType.PER_USER;
		} else if (securityModel.getSecuritypolicyset().getAuthentication().get(0)
				.getAuthNTypeForwarded() == AuthenticationTypeForwarded.CUSTOM_ACCOUNT) {
			return ServiceAuthenticationType.CUSTOM;
		}

		return ServiceAuthenticationType.NONE;
	}

	/**
	 * @param name
	 *            the name of the folder
	 * @param parentContainer
	 *            the parent container
	 * @return the created folder
	 * @throws CoreException
	 */
	public static IFolder createFolder(String name, IContainer parentContainer) throws CoreException {
		Path path = new Path(name);

		IFolder iFolder = parentContainer.getFolder(path);
		if (!iFolder.exists()) {
			iFolder.create(true, true, null);
		}
		parentContainer.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		return iFolder;
	}

	public static IPath createFolderIntoWorkspace(String name, IWorkspaceRoot workspacePath) throws CoreException {

		System.out.println("amleto=" + workspacePath.getLocation().toPortableString());
		Path path = new Path(workspacePath.getLocation().toPortableString() + "/" + name);
		return path;

		/*
		 * IFolder iFolder =
		 * workspacePath.getContainerForLocation(null).getFolder(path); if
		 * (!iFolder.exists()) { iFolder.create(true, true, null); }
		 * workspacePath.refreshLocal(IResource.DEPTH_INFINITE, new
		 * NullProgressMonitor()); return project.getR;
		 */

	}

	public static IFile createFile(String name, IContainer parentContainer, byte[] content) throws CoreException {
		Path pathNotNio = new Path(name);
		java.nio.file.Path path = Paths.get(parentContainer.getRawLocation().append(name).makeAbsolute().toOSString());

		IFile iFile = parentContainer.getFile(pathNotNio);
		if (!iFile.exists()) {
			try {
				Files.write(path, content);
			} catch (IOException e) {

			}
			// iFile.create(new ByteArrayInputStream(content), true, null);//slower
		}
		parentContainer.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		return iFile;
	}

	public static IFile createFile(String name, IContainer parentContainer, String content) throws CoreException {
		return createFile(name, parentContainer, content.getBytes());
	}

	public static String readFile(IFile file) throws CoreException {
		try {
			return IOUtils.toString(file.getContents(true));
		} catch (IOException e) {
			// TODO externalize message
			StatusHandler.log(new Status(IStatus.ERROR, ChorevolutionCorePlugin.PLUGIN_ID,
					"An error occurred loading resource " + file.getFullPath(), e));
		}

		return null;
	}

	public static String unTar(byte[] content, IPath directoryPath) throws IOException {
		byte[] tarContent = uncompressTarGz(content, directoryPath);

		TarArchiveInputStream in = new TarArchiveInputStream(new ByteArrayInputStream(tarContent));
		TarArchiveEntry entry = in.getNextTarEntry();
		String firstDirectoryName = entry.getName().replace("/", "");

		while (entry != null) {
			if (entry.isDirectory()) {
				entry = in.getNextTarEntry();
				continue;
			}
			File curfile = new File(directoryPath.makeAbsolute().toOSString(), entry.getName());
			File parent = curfile.getParentFile();
			if (!parent.exists()) {
				parent.mkdirs();
			}
			OutputStream out = new FileOutputStream(curfile);
			IOUtils.copy(in, out);
			out.close();
			entry = in.getNextTarEntry();
		}
		in.close();
		return firstDirectoryName;
	}

	private static byte[] uncompressTarGz(byte[] targzContent, IPath directory) throws IOException {
		final int buffersize = 2048;
		BufferedInputStream in = new BufferedInputStream(new ByteArrayInputStream(targzContent));

		// File tarFile =
		// FileUtils.getTempDirectory().createTempFile("chorevoltuionstudio", "tar");
		File tarFile = File.createTempFile("chorevolutionstudio", "tar");
		FileOutputStream out = new FileOutputStream(tarFile);
		GzipCompressorInputStream gzIn = new GzipCompressorInputStream(in);
		final byte[] buffer = new byte[buffersize];
		int n = 0;
		while (-1 != (n = gzIn.read(buffer))) {
			out.write(buffer, 0, n);
		}
		out.close();
		gzIn.close();

		byte[] tarContent = FileUtils.readFileToByteArray(tarFile);

		try {
			FileDeleteStrategy.FORCE.delete(tarFile);
		} catch (IOException ex) {

		}

		return tarContent;

	}

	public static IProject importMavenProject(final String projectName) throws CoreException {
		IProject newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		IProjectDescription desc = newProject.getWorkspace().newProjectDescription(newProject.getName());
		desc.setLocationURI(newProject.getLocationURI());
		desc.setNatureIds(new String[] { "org.eclipse.m2e.core.maven2Nature", });

		newProject.create(desc, null);
		newProject.open(null);

		IProject[] projects = new IProject[1];
		projects[0] = newProject;
		new UpdateMavenProjectJob(projects, false, false, true, true, true).schedule();
		return newProject;

	}

	public static IProject openBPELProject(final String projectName) throws CoreException {
		IProjectDescription description = ResourcesPlugin.getWorkspace().loadProjectDescription(
				new Path(ResourcesPlugin.getWorkspace().getRoot().getLocation().toPortableString() + "/" + projectName
						+ "/.project"));
		description.setLocation(new Path(
				ResourcesPlugin.getWorkspace().getRoot().getLocation().toPortableString() + "/" + projectName));
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());

		project.create(description, null);
		project.open(null);
		return project;

		// IProject newProject =
		// ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		// IProjectDescription desc =
		// newProject.getWorkspace().newProjectDescription(newProject.getName());
		// desc.setLocationURI(newProject.getLocationURI());
		// desc.setNatureIds(new String[] { "org.eclipse.m2e.core.maven2Nature", });

		// newProject.create(desc, null);
		// newProject.open(null);
		// return newProject;
	}

	public static IFile executeMavenGoal(IProject project, IProgressMonitor monitor) throws CoreException {
		ExecGoalsMavenProject execGoalsMavenProject = new ExecGoalsMavenProject(project);
		execGoalsMavenProject.exec("clean verify", monitor);
		return project.getFolder("target").getFile(project.getName() + ".war");

	}

	/**
	 * Removes the extension from a filename.
	 *
	 * @param filename
	 *            the filename to query, null returns null
	 * @return the filename minus the extension
	 */
	public static String removeExtension(String filename) {
		int index = filename.lastIndexOf('.');
		if (index == -1 || index == (filename.length() - 1))
			return filename;

		return filename.substring(0, index);

	}

	/**
	 * Removes the extension from a filename.
	 *
	 * @param filename
	 *            the filename to query, null returns null
	 * @param newExtension
	 *            the new extension for the file name to query
	 * @return the filename minus the extension
	 */
	public static String changeExtension(String filename, String newExtension) {
		String newFileName = removeExtension(filename);
		if (newExtension.charAt(0) != '.') {
			newFileName += ".";
		}

		return newFileName + newExtension;

	}

	/**
	 * Returns true if given resource's project has the given nature.
	 */
	public static boolean hasNature(IResource resource, String natureId) {
		if (resource != null && resource.isAccessible()) {
			IProject project = resource.getProject();
			if (project != null) {
				try {
					return project.hasNature(natureId);
				} catch (CoreException e) {
					StatusHandler.log(new Status(IStatus.ERROR, ChorevolutionCorePlugin.PLUGIN_ID,
							"An error occurred inspecting project nature", e));
				}
			}
		}
		return false;
	}

	public static <T> T[] appendIntoArray(T[] array, T element) {
		final int N = array.length;
		array = Arrays.copyOf(array, N + 1);
		array[N] = element;
		return array;
	}

	public static String convertToCamelCase(String string, boolean startsUpperCase) {
		if (startsUpperCase) {
			return removeBlankSpaces(StringUtils.capitalize(string));
		} else {
			return StringUtils.uncapitalize(removeBlankSpaces(string));
		}
	}

	public static String convertToLowerCase(String string) {
		return removeBlankSpaces(string).toLowerCase();
	}

	public static String removeBlankSpaces(String string) {
		return string.replaceAll("\\s", "");
	}

	public static boolean isJDK(String jDKpath) {

		// IVMInstall install= JavaRuntime.getDefaultVMInstall();
		// Path path =
		// Paths.get(install.getInstallLocation().getAbsolutePath()+"/bin/");
		java.nio.file.Path path = Paths.get(jDKpath + "/bin/");

		File folder = new File(path.toAbsolutePath().toString());
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
			if (file.isFile()) {
				String[] filename = file.getName().split("\\.(?=[^\\.]+$)");
				if (filename[0].equalsIgnoreCase("javac"))
					return true;
			}
		}

		return false;
	}

	public static String getDefaultJVMPath() {
		IVMInstall install = JavaRuntime.getDefaultVMInstall();
		if (install != null) {
			return install.getInstallLocation().getAbsolutePath();
		} else {
			return null;
		}
	}

	public static String getDefaultJVMName() {
		IVMInstall install = JavaRuntime.getDefaultVMInstall();
		if (install != null) {
			return install.getName();
		} else {
			return null;
		}
	}

	public static boolean isValidURL(String uRL) {

		try {
			URL url = new URL(uRL);
		} catch (MalformedURLException e) {
			return false;
		}

		return true;
	}

	public static void showPrefPage(String preferencePageId/* , IPreferencePage page */) {
		// IVMInstall prevJRE = null;
		// IExecutionEnvironment prevEnv = null;
		JDIDebugUIPlugin.showPreferencePage(preferencePageId);
	}

}
