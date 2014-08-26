package org.knime.knip.leuven.nodes.features;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;

/**
 * <code>NodeDialog</code> for the "MovementFeatures" Node.
 * Movement Features
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Christopher Kintzel
 */
public class MovementFeaturesNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring MovementFeatures node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected MovementFeaturesNodeDialog() {
        super();
                    
    }
}

