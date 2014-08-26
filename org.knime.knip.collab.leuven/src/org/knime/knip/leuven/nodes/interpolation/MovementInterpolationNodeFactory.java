package org.knime.knip.leuven.nodes.interpolation;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "MovementInterpolation" Node.
 * 
 *
 * @author Christopher Kintzel
 */
public class MovementInterpolationNodeFactory 
        extends NodeFactory<MovementInterpolationNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public MovementInterpolationNodeModel createNodeModel() {
        return new MovementInterpolationNodeModel();
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
    public NodeView<MovementInterpolationNodeModel> createNodeView(final int viewIndex,
            final MovementInterpolationNodeModel nodeModel) {
        return new MovementInterpolationNodeView(nodeModel);
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
        return new MovementInterpolationNodeDialog();
    }

}

