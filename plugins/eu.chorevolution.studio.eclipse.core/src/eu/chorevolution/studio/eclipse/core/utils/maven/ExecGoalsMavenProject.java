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
package eu.chorevolution.studio.eclipse.core.utils.maven;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.RefreshTab;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.m2e.actions.MavenLaunchConstants;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.m2e.core.project.ResolverConfiguration;

import eu.chorevolution.studio.eclipse.core.ChorevolutionCoreUtils;

public class ExecGoalsMavenProject {
	
	private boolean jDKError=false;
	
	static {
		DebugPlugin.getDefault().getLaunchManager().addLaunchListener(new ILaunchesListener2() {
			@Override
			public void launchesRemoved(ILaunch[] launches) {
				for (ILaunch l : launches) {
					ILaunchConfiguration conf = l.getLaunchConfiguration();
					synchronized (conf) {
						conf.notifyAll();
					}
				}
			}

			@Override
			public void launchesChanged(ILaunch[] launches) {
			}

			@Override
			public void launchesAdded(ILaunch[] launches) {
			}
			
			@Override
			public void launchesTerminated(ILaunch[] launches) {
				for (ILaunch l : launches) {
					ILaunchConfiguration conf = l.getLaunchConfiguration();
					synchronized (conf) {
						conf.notifyAll();
					}
				}
			}
			
			
		});
	}

	private IProject project;

	public ExecGoalsMavenProject(IProject project) {
		this.project = project;
	}

	public void exec(String goals, IProgressMonitor monitor) throws CoreException {
		if (project == null) {
			return;
		}

		ILaunchConfiguration launchConfiguration = createLaunchConfiguration(project, goals);
		if (launchConfiguration == null) {
			if(jDKError == true) {
				throw new CoreException(new Status(Status.ERROR, "ExecGoalsMavenProject", "not_a_JDK"));
			}
			else {
				return;
			}
				
		}

		ILaunch launch = launchConfiguration.launch("run", monitor, false, true);

		synchronized (launchConfiguration) {
			while (!launch.isTerminated()) {
				try {
					launchConfiguration.wait(5000L);
				} catch (InterruptedException e) {
				}
			}
			launch.isTerminated();
		}
		
	}

	private ILaunchConfiguration createLaunchConfiguration(IContainer basedir, String goals) {
		try {
			jDKError=false;
			ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfigurationType launchConfigurationType = launchManager
					.getLaunchConfigurationType(MavenLaunchConstants.LAUNCH_CONFIGURATION_TYPE_ID);
			
			ILaunchConfigurationWorkingCopy workingCopy = launchConfigurationType.newInstance(null, "Executing POM");
			workingCopy.setAttribute(MavenLaunchConstants.ATTR_POM_DIR, basedir.getLocation().toOSString());
			workingCopy.setAttribute(MavenLaunchConstants.ATTR_GOALS, goals);
			workingCopy.setAttribute(MavenLaunchConstants.ATTR_UPDATE_SNAPSHOTS, true);
			workingCopy.setAttribute(IDebugUIConstants.ATTR_PRIVATE, true);
			workingCopy.setAttribute(RefreshTab.ATTR_REFRESH_SCOPE, "${project}");
			workingCopy.setAttribute(RefreshTab.ATTR_REFRESH_RECURSIVE, true);

			setProjectConfiguration(workingCopy, basedir);

			IPath path = getJDKContainerPath(basedir);
			
			if (path != null) {
				if(ChorevolutionCoreUtils.isJDK(path.toPortableString())) {
					workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_JRE_CONTAINER_PATH,
							path.toPortableString());
				}
				else {
					//is not a jdk, i should raise an exception
					throw new CoreException(new Status(Status.ERROR, "ExecGoalsMavenProject", "not_a_JDK"));
				}
			}

			return workingCopy;
		} catch (CoreException ex) {
			if (ex.getMessage().equals("not_a_JDK")) {
				jDKError=true;
				return null;
			}
		}
		return null;
	}

	private void setProjectConfiguration(ILaunchConfigurationWorkingCopy workingCopy, IContainer basedir) {
		IMavenProjectRegistry projectManager = MavenPlugin.getMavenProjectRegistry();
		IFile pomFile = basedir.getFile(new Path(IMavenConstants.POM_FILE_NAME));
		IMavenProjectFacade projectFacade = projectManager.create(pomFile, false, new NullProgressMonitor());
		if (projectFacade != null) {
			ResolverConfiguration configuration = projectFacade.getResolverConfiguration();

			String activeProfiles = configuration.getSelectedProfiles();
			if (activeProfiles != null && activeProfiles.length() > 0) {
				workingCopy.setAttribute(MavenLaunchConstants.ATTR_PROFILES, activeProfiles);
			}
		}
	}

	//get JDK Container Path
	private IPath getJDKContainerPath(IContainer basedir) throws CoreException {
		IVMInstall install= JavaRuntime.getDefaultVMInstall();
		if (install != null) {
			return Path.fromOSString(install.getInstallLocation().getAbsolutePath()); 
		} else {
			return getJDKProjectContainerPath(basedir);
		}
	}
	
	
	// TODO ideally it should use MavenProject, but it is faster to scan
	// IJavaProjects
	private IPath getJDKProjectContainerPath(IContainer basedir) throws CoreException {
		IProject project = basedir.getProject();
		if (project != null && project.hasNature(JavaCore.NATURE_ID)) {
			IJavaProject javaProject = JavaCore.create(project);
			IClasspathEntry[] entries = javaProject.getRawClasspath();
			for (int i = 0; i < entries.length; i++) {
				IClasspathEntry entry = entries[i];
				if (JavaRuntime.JRE_CONTAINER.equals(entry.getPath().segment(0))) {
					return entry.getPath();
				}
			}
		}
		return null;
	}

}
