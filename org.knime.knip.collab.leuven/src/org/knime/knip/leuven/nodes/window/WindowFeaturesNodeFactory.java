package org.knime.knip.leuven.nodes.window;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "WindowFeatures" Node.
 * 
 *
 * @author Christopher Kintzel
 */
public class WindowFeaturesNodeFactory 
        extends NodeFactory<WindowFeaturesNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public WindowFeaturesNodeModel createNodeModel() {
        return new WindowFeaturesNodeModel();
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
    public NodeView<WindowFeaturesNodeModel> createNodeView(final int viewIndex,
            final WindowFeaturesNodeModel nodeModel) {
        return new WindowFeaturesNodeView(nodeModel);
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
        return new WindowFeaturesNodeDialog();
    }

}

