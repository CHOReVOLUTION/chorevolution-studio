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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.project.ProjectConfigurationManager;
import org.eclipse.m2e.core.project.MavenUpdateRequest;
import org.eclipse.m2e.core.ui.internal.M2EUIPluginActivator;
import org.eclipse.m2e.core.ui.internal.actions.OpenMavenConsoleAction;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.IProgressConstants;

import eu.chorevolution.studio.eclipse.core.ChorevolutionCorePlugin;


@SuppressWarnings("restriction")
public class UpdateMavenProjectJob extends WorkspaceJob {

  private final IProject[] projects;

  
  private final boolean offline;

  private final boolean forceUpdateDependencies;

  private final boolean updateConfiguration;

  private final boolean cleanProjects;

  private final boolean refreshFromLocal;

  public UpdateMavenProjectJob(IProject[] projects) {
    this(projects, MavenPlugin.getMavenConfiguration().isOffline(), false /*forceUpdateDependencies*/,
        true /*updateConfiguration*/, true /*rebuild*/, true /*refreshFromLocal*/);
    
  }

  public UpdateMavenProjectJob(IProject[] projects, boolean offline, boolean forceUpdateDependencies,
      boolean updateConfiguration, boolean cleanProjects, boolean refreshFromLocal) {

    super("Updating Maven Project");

    this.projects = projects;
    this.offline = offline;
    this.forceUpdateDependencies = forceUpdateDependencies;
    this.updateConfiguration = updateConfiguration;
    this.cleanProjects = cleanProjects;
    this.refreshFromLocal = refreshFromLocal;
    
    setRule(MavenPlugin.getProjectConfigurationManager().getRule());
  }

  public IStatus runInWorkspace(IProgressMonitor monitor) {
    ProjectConfigurationManager configurationManager = (ProjectConfigurationManager) MavenPlugin
        .getProjectConfigurationManager();

    setProperty(IProgressConstants.ACTION_PROPERTY, new OpenMavenConsoleAction());

    MavenUpdateRequest request = new MavenUpdateRequest(projects, offline, forceUpdateDependencies);
    Map<String, IStatus> updateStatus = configurationManager.updateProjectConfiguration(request, updateConfiguration,
        cleanProjects, refreshFromLocal, monitor);

    Map<String, Throwable> errorMap = new LinkedHashMap<String, Throwable>();
    ArrayList<IStatus> errors = new ArrayList<IStatus>();

    for(Map.Entry<String, IStatus> entry : updateStatus.entrySet()) {
      if(!entry.getValue().isOK()) {
        errors.add(entry.getValue());
        errorMap.put(entry.getKey(), new CoreException(entry.getValue()));
      }
    }

    if(errorMap.size() > 0) {
      handleErrors(errorMap);
    }

    IStatus status = Status.OK_STATUS;
    if(errors.size() == 1) {
      status = errors.get(0);
    } else {
        
      status = new MultiStatus(M2EUIPluginActivator.PLUGIN_ID, -1, errors.toArray(new IStatus[errors.size()]),
          "Unable to update Maven configuration", null);
    }

    return status;
  }

  private void handleErrors(final Map<String, Throwable> updateErrors) {
    final Display display = Display.getDefault();
    if(display != null) {
      display.asyncExec(new Runnable() {
        public void run() {
             MessageDialog.openError(ChorevolutionCorePlugin.getActiveWorkbenchShell(),
                        "Error Updating Maven Configuration",
                        "Unable to update maven configuration for the following projects: "+updateErrors);
            
        
        }
      });
    }
  }
}
