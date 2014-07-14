package org.knime.knip.leuven.nodes.thinning;

import net.imglib2.type.logic.BitType;

import org.knime.knip.base.data.img.ImgPlusValue;
import org.knime.knip.base.node.ValueToCellNodeDialog;
import org.knime.knip.base.node.dialog.DialogComponentDimSelection;

/**
 * Dialog for the node Thinning where you can select which columns contain the
 * images.
 * 
 * @author wildernm, dietzc (University of Konstanz)
 */
public class ThinningNodeDialog extends
		ValueToCellNodeDialog<ImgPlusValue<BitType>> {

	@Override
	public void addDialogComponents() {

		addDialogComponent(new DialogComponentDimSelection(
				ThinningNodeModel.createDimSelectionModel(),
				"Dimension selection"));

	}

}
