package org.knime.knip.leuven.nodes.window;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;

/**
 * <code>NodeDialog</code> for the "WindowFeatures" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Christopher Kintzel
 */
public class WindowFeaturesNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring WindowFeatures node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected WindowFeaturesNodeDialog() {
        super();
        
        addDialogComponent(new DialogComponentNumber(
                new SettingsModelIntegerBounded(
                    WindowFeaturesNodeModel.CFGKEY_WINDOWSIZE,
                    WindowFeaturesNodeModel.DEFAULT_CFGKEY_WINDOWSIZE,
                    Integer.MIN_VALUE, Integer.MAX_VALUE),
                    "Window Size:", /*step*/ 1, /*componentwidth*/ 5));
                    
    }
}

