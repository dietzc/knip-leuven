package org.knime.knip.leuven.nodes.features;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "MovementFeatures" Node.
 * Movement Features
 *
 * @author Christopher Kintzel
 */
public class MovementFeaturesNodeFactory 
        extends NodeFactory<MovementFeaturesNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public MovementFeaturesNodeModel createNodeModel() {
        return new MovementFeaturesNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<MovementFeaturesNodeModel> createNodeView(final int viewIndex,
            final MovementFeaturesNodeModel nodeModel) {
        return new MovementFeaturesNodeView(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane() {
        return new MovementFeaturesNodeDialog();
    }

}

