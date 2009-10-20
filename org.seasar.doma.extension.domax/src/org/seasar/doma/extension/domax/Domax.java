package org.seasar.doma.extension.domax;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Domax extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "org.seasar.doma.extension.domax";

    private static Domax plugin;

    /**
     * The constructor
     */
    public Domax() {
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;

        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        workspace.addResourceChangeListener(new SqlFileChangeListener());
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    public static Domax getDefault() {
        return plugin;
    }

}
