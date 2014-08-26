package org.knime.knip.leuven.nodes.object;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "ObjectFeatures" Node.
 * 
 *
 * @author Christopher Kintzel
 */
public class ObjectFeaturesNodeFactory 
        extends NodeFactory<ObjectFeaturesNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectFeaturesNodeModel createNodeModel() {
        return new ObjectFeaturesNodeModel();
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
    public NodeView<ObjectFeaturesNodeModel> createNodeView(final int viewIndex,
            final ObjectFeaturesNodeModel nodeModel) {
        return new ObjectFeaturesNodeView(nodeModel);
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
        return new ObjectFeaturesNodeDialog();
    }

}

