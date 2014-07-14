package org.knime.knip.leuven.nodes.thinning;

import net.imglib2.type.logic.BitType;

import org.knime.knip.base.data.img.ImgPlusValue;
import org.knime.knip.base.node.ValueToCellNodeFactory;

/**
 * Factory for the node Thinning.
 * 
 * @author wildnerm, University of Konstanz
 */
public class ThinningNodeFactory extends
		ValueToCellNodeFactory<ImgPlusValue<BitType>> {

	@Override
	public ThinningNodeModel createNodeModel() {
		return new ThinningNodeModel();
	}

	@Override
	protected ThinningNodeDialog createNodeDialog() {
		return new ThinningNodeDialog();
	}

}
