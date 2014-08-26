package org.knime.knip.leuven.nodes.interpolation;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;

/**
 * <code>NodeDialog</code> for the "MovementInterpolation" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Christopher Kintzel
 */
public class MovementInterpolationNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring MovementInterpolation node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected MovementInterpolationNodeDialog() {
        super();
                    
    }
}

