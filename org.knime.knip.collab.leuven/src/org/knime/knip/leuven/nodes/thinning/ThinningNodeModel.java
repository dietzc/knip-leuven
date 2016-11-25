/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2010
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME. The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 * 
 * History
 *   12 Sep 2011 (hornm): created
 */
package org.knime.knip.leuven.nodes.thinning;

import java.util.List;

import net.imagej.ImgPlus;
import net.imglib2.img.Img;
import net.imglib2.ops.operation.SubsetOperations;
import net.imglib2.type.logic.BitType;

import org.knime.core.node.ExecutionContext;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.knip.base.data.img.ImgPlusCell;
import org.knime.knip.base.data.img.ImgPlusCellFactory;
import org.knime.knip.base.data.img.ImgPlusValue;
import org.knime.knip.base.node.ValueToCellNodeModel;
import org.knime.knip.base.node.nodesettings.SettingsModelDimSelection;
import org.knime.knip.core.KNIPGateway;

/**
 * Node Model for node Thinning. Prepares the image to get thinned.
 * 
 * @author wildnerm, University of Konstanz
 * 
 * @param <BitType>
 *            the pixel type of the input and output image
 */
public class ThinningNodeModel extends ValueToCellNodeModel<ImgPlusValue<BitType>, ImgPlusCell<BitType>> {

	private SettingsModelDimSelection m_dimSelection = createDimSelectionModel();

	private ImgPlusCellFactory m_imgCellFactory;

	/**
	 * 
	 */
	protected ThinningNodeModel() {
		super();
	}

	protected static SettingsModelDimSelection createDimSelectionModel() {
		return new SettingsModelDimSelection("dimselection", "X", "Y");
	}

	@Override
	protected void addSettingsModels(List<SettingsModel> settingsModels) {

		settingsModels.add(m_dimSelection);
	}

	/**
	 * Starts the thinning operation.
	 * 
	 * @param cellValue
	 *            the value of the current cell
	 */
	@Override
	protected ImgPlusCell<BitType> compute(ImgPlusValue<BitType> cellValue) throws Exception {

		Img<BitType> res = SubsetOperations.iterate(new Thinning<Img<BitType>>(),
				m_dimSelection.getSelectedDimIndices(cellValue.getImgPlus()), cellValue.getImgPlus(),
				KNIPGateway.ops().create().img(cellValue.getImgPlus()), getExecutorService());

		return m_imgCellFactory.createCell(new ImgPlus<>(res));

	}

	@Override
	protected void prepareExecute(ExecutionContext exec) {
		m_imgCellFactory = new ImgPlusCellFactory(exec);
	}
}
