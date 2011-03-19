package org.eclipselabs.tapiji.tools.core;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipselabs.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipselabs.tapiji.tools.core";

	// The builder extension id
	public static final String BUILDER_EXTENSION_ID = "org.eclipselabs.tapiji.tools.core.builderExtension";

	// The shared instance
	private static Activator plugin;
	
	//Resource bundle.
    private ResourceBundle resourceBundle;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		// save state of ResourceBundleManager
		ResourceBundleManager.saveManagerState();
		
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		if (path.indexOf("icons/") < 0)
			path = "icons/" + path;

		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
    /**
     * Returns the string from the plugin's resource bundle,
     * or 'key' if not found.
     * @param key the key for which to fetch a localized text
     * @return localized string corresponding to key
     */
    public static String getString(String key) {
        ResourceBundle bundle = 
                Activator.getDefault().getResourceBundle();
        try {
            return (bundle != null) ? bundle.getString(key) : key;
        } catch (MissingResourceException e) {
            return key;
        }
    }

    /**
     * Returns the string from the plugin's resource bundle,
     * or 'key' if not found.
     * @param key the key for which to fetch a localized text
     * @param arg1 runtime argument to replace in key value 
     * @return localized string corresponding to key
     */
    public static String getString(String key, String arg1) {
        return MessageFormat.format(getString(key), new String[]{arg1});
    }
    /**
     * Returns the string from the plugin's resource bundle,
     * or 'key' if not found.
     * @param key the key for which to fetch a localized text
     * @param arg1 runtime first argument to replace in key value
     * @param arg2 runtime second argument to replace in key value
     * @return localized string corresponding to key
     */
    public static String getString(String key, String arg1, String arg2) {
        return MessageFormat.format(
                getString(key), new String[]{arg1, arg2});
    }
    /**
     * Returns the string from the plugin's resource bundle,
     * or 'key' if not found.
     * @param key the key for which to fetch a localized text
     * @param arg1 runtime argument to replace in key value 
     * @param arg2 runtime second argument to replace in key value
     * @param arg3 runtime third argument to replace in key value
     * @return localized string corresponding to key
     */
    public static String getString(
            String key, String arg1, String arg2, String arg3) {
        return MessageFormat.format(
                getString(key), new String[]{arg1, arg2, arg3});
    }
    
    /**
     * Returns the plugin's resource bundle.
     * @return resource bundle
     */
    protected ResourceBundle getResourceBundle() {
        return resourceBundle;
    }
    
}