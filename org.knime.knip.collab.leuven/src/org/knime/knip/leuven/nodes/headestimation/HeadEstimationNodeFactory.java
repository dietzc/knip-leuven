package org.knime.knip.leuven.nodes.headestimation;

import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.algorithm.region.localneighborhood.Neighborhood;
import net.imglib2.algorithm.region.localneighborhood.RectangleShape;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingType;
import net.imglib2.labeling.NativeImgLabeling;
import net.imglib2.meta.ImgPlus;
import net.imglib2.ops.operation.iterable.unary.Sum;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.view.Views;

import org.knime.core.node.ExecutionContext;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.knip.base.data.img.ImgPlusValue;
import org.knime.knip.base.data.labeling.LabelingCell;
import org.knime.knip.base.data.labeling.LabelingCellFactory;
import org.knime.knip.base.node.TwoValuesToCellNodeDialog;
import org.knime.knip.base.node.TwoValuesToCellNodeFactory;
import org.knime.knip.base.node.TwoValuesToCellNodeModel;
import org.knime.knip.core.awt.labelingcolortable.DefaultLabelingColorTable;
import org.knime.knip.core.data.img.DefaultLabelingMetadata;

public class HeadEstimationNodeFactory
		extends
		TwoValuesToCellNodeFactory<ImgPlusValue<BitType>, ImgPlusValue<BitType>> {

	@Override
	public TwoValuesToCellNodeModel<ImgPlusValue<BitType>, ImgPlusValue<BitType>, LabelingCell<String>> createNodeModel() {
		return new TwoValuesToCellNodeModel<ImgPlusValue<BitType>, ImgPlusValue<BitType>, LabelingCell<String>>() {

			private LabelingCellFactory m_fac;

			@Override
			protected void prepareExecute(final ExecutionContext exec) {
				super.prepareExecute(exec);
				m_fac = new LabelingCellFactory(exec);
			}

			@Override
			protected LabelingCell<String> compute(
					final ImgPlusValue<BitType> thinnedValue,
					final ImgPlusValue<BitType> sourceValue) throws Exception {

				if (thinnedValue.getDimensions().length != 2) {
					throw new IllegalArgumentException(
							"Only two dimensions are supported since now");
				}

				final ImgPlus<BitType> thinned = thinnedValue.getImgPlus();
				final ImgPlus<BitType> source = sourceValue.getImgPlus();

				final RandomAccess<BitType> thinnedAccess = thinned
						.randomAccess();

				final IterableInterval<Neighborhood<BitType>> neighborhoods = new RectangleShape(
						1, true)
						.neighborhoods(Views.interval(
								Views.extendValue(thinned, new BitType(false)),
								thinned));

				final Cursor<Neighborhood<BitType>> neighborhoodCursor = neighborhoods
						.cursor();

				final int[][] posses = new int[2][2];

				int idx = 0;
				while (neighborhoodCursor.hasNext()) {
					neighborhoodCursor.fwd();

					thinnedAccess.setPosition(neighborhoodCursor);

					if (!thinnedAccess.get().get())
						continue;

					final Cursor<BitType> oneNeighborhood = neighborhoodCursor
							.get().cursor();

					int ctr = 0;
					while (oneNeighborhood.hasNext()) {
						oneNeighborhood.fwd();

						if (oneNeighborhood.get().get()) {
							ctr++;

							if (ctr > 1)
								break;
						}
					}

					if (ctr == 1 && idx < 2) {
						thinnedAccess.localize(posses[idx++]);
					}
				}

				// now we need to determine head and tail. We use a very very
				// simple heuristic here.
				// the one which has more active pixels in the 25 neighborhood
				// is the head

				final Labeling<String> res = new NativeImgLabeling<String, UnsignedByteType>(
						new ArrayImgFactory<UnsignedByteType>().create(source,
								new UnsignedByteType()));

				final Sum<BitType, DoubleType> sumOp = new Sum<BitType, DoubleType>();

				final RandomAccess<Neighborhood<BitType>> ra = new RectangleShape(
						2, true).neighborhoodsRandomAccessibleSafe(
						Views.interval(
								Views.extendValue(source, new BitType(false)),
								source)).randomAccess(source);

				ra.setPosition(posses[0]);

				final double tmp = sumOp.compute(ra.get().cursor(),
						new DoubleType()).get();

				ra.setPosition(posses[1]);

				// default
				int headIdx = 0, tailIdx = 1;

				// switch if default is wrong
				if (sumOp.compute(ra.get().cursor(), new DoubleType()).get() > tmp) {
					headIdx = 1;
					tailIdx = 0;
				}

				final RandomAccess<LabelingType<String>> resAccess = res
						.randomAccess();

				resAccess.setPosition(posses[headIdx]);
				resAccess.get().setLabel("Head");
				resAccess.setPosition(posses[tailIdx]);
				resAccess.get().setLabel("Tail");

				return m_fac.createCell(res, new DefaultLabelingMetadata(
						resAccess.numDimensions(),
						new DefaultLabelingColorTable()));
			}

			@Override
			protected void addSettingsModels(
					final List<SettingsModel> settingsModels) {
				//
			}
		};
	}

	@Override
	protected TwoValuesToCellNodeDialog<ImgPlusValue<BitType>, ImgPlusValue<BitType>> createNodeDialog() {
		return new TwoValuesToCellNodeDialog<ImgPlusValue<BitType>, ImgPlusValue<BitType>>() {

			@Override
			public void addDialogComponents() {
				// Nothing to do here since now ... add dialog component if
				// needed
			}

			@Override
			protected String getFirstColumnSelectionLabel() {
				return "Thinned Larva BitMask";
			}

			@Override
			protected String getSecondColumnSelectionLabel() {
				return "Source Larva BitMask";
			}
		};
	}
}
