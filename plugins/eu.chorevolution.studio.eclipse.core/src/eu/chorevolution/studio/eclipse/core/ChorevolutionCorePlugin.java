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

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Central access point for the Chorevolution Studio Core plug-in (id <code>"eu.chorevolution.studio.eclipse.core"</code>).
 */
public class ChorevolutionCorePlugin extends AbstractUIPlugin {

	/**
	 * Plugin identifier for Chorevolution Core (value <code>eu.chorevolution.studio.eclipse.core</code>).
	 */
	public static final String PLUGIN_ID = "eu.chorevolution.studio.eclipse.core";

	
	/**
	 * The identifier for the Chorevolution nature (value <code>"eu.chorevolution.studio.eclipse.core.chorevolutionsynthesisnature"</code>). The presence of this nature on a project indicates that it is Chorevolution-capable.
	 * 
	 * @see org.eclipse.core.resources.IProject#hasNature(java.lang.String)
	 */
	public static final String SYNTHESIS_NATURE_ID = PLUGIN_ID + ".chorevolutionsynthesisnature";
	/**
	 * The identifier for the Chorevolution nature (value <code>"eu.chorevolution.studio.eclipse.core.chorevolutionservicethingnature"</code>). The presence of this nature on a project indicates that it is Chorevolution-capable.
	 * 
	 * @see org.eclipse.core.resources.IProject#hasNature(java.lang.String)
	 */	
	public static final String SERVICE_THING_NATURE_ID = PLUGIN_ID + ".chorevolutionservicethingnature";

	
	private static final String RESOURCE_NAME = PLUGIN_ID + ".messages";
	
	/** Resource bundle */
	private ResourceBundle resourceBundle;

	
	/** The shared instance */
	private static ChorevolutionCorePlugin plugin;
	
	/**
	 * The constructor
	 */
	public ChorevolutionCorePlugin() {
		try {
			resourceBundle = ResourceBundle.getBundle(RESOURCE_NAME);
		} catch (MissingResourceException e) {
			resourceBundle = null;
		}
	}

	/**
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}
	
	
	

	/**
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the single instance of the Chorevolution core plug-in runtime class.
	 */
	public static ChorevolutionCorePlugin getDefault() {
		return plugin;
	}
	
	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}
	
	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	/**
	 * Writes the message to the plug-in's log
	 * 
	 * @param message
	 *            the text to write to the log
	 */
	public static void log(String message, Throwable exception) {
		IStatus status = createErrorStatus(message, exception);
		getDefault().getLog().log(status);
	}

	public static void log(Throwable exception) {
		getDefault().getLog().log(createErrorStatus(getResourceString("Plugin.internal_error"), exception));
	}
	
	/**
	 * Returns a new {@link IStatus} with status "ERROR" for this plug-in.
	 */
	public static IStatus createErrorStatus(String message, Throwable exception) {
		if (message == null) {
			message = "";
		}
		return new Status(IStatus.ERROR, PLUGIN_ID, 0, message, exception);
	}
	
	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		String bundleString;
		ResourceBundle bundle = getDefault().getResourceBundle();
		if (bundle != null) {
			try {
				bundleString = bundle.getString(key);
			} catch (MissingResourceException e) {
				log(e);
				bundleString = "!" + key + "!";
			}
		} else {
			bundleString = "!" + key + "!";
		}
		return bundleString;
	}
	
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
        return getDefault().getWorkbench().getActiveWorkbenchWindow();
    }

    public static Shell getActiveWorkbenchShell() {
        return getActiveWorkbenchWindow().getShell();
    }
	
	

}
