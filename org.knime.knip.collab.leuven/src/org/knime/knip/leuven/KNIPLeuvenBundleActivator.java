package org.knime.knip.leuven;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core;
import org.knime.core.node.NodeLogger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * 
 * @author <a href="mailto:dietzc85@googlemail.com">Christian Dietz</a>
 * @author <a href="mailto:horn_martin@gmx.de">Martin Horn</a>
 * @author <a href="mailto:michael.zinsmaier@googlemail.com">Michael
 *         Zinsmaier</a>
 */
public class KNIPLeuvenBundleActivator implements BundleActivator {

	private static final NodeLogger LOGGER = NodeLogger
			.getLogger(KNIPLeuvenBundleActivator.class);

	private static boolean JavaCVLoaded = false;

	@Override
	public final void start(final BundleContext context) throws Exception {

		LOGGER.debug("Trying to load JavaCV libs");

		try {

			Loader.load(opencv_core.class);

		} catch (final UnsatisfiedLinkError e) {
			LOGGER.error("Could not load JavaCV");
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}

	}

	@Override
	public final void stop(final BundleContext context) throws Exception {
		// unused
	}

	public static final boolean JavaCVLoaded() {
		return JavaCVLoaded;
	}
}
