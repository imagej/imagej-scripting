
package net.imagej.scripting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import io.scif.SCIFIOService;
import io.scif.img.ImgUtilityService;
import io.scif.img.converters.PlaneConverterService;
import io.scif.services.DatasetIOService;
import io.scif.services.InitializeService;
import io.scif.xml.XMLService;

import net.imagej.Dataset;
import net.imagej.ImageJService;
import net.imagej.ops.OpService;
import net.imglib2.IterableInterval;
import net.imglib2.type.numeric.RealType;

import org.junit.After;
import org.junit.Before;
import org.scijava.Context;
import org.scijava.cache.CacheService;
import org.scijava.plugin.Parameter;
import org.scijava.script.ScriptService;
import org.scijava.ui.UIService;
import org.scijava.widget.WidgetService;

/**
 * @author Hadrien Mary
 */
public abstract class AbstractScriptTest implements ScriptTest {

	@Parameter
	protected Context context;

	@Parameter
	protected ScriptService scriptService;

	/**
	 * Subclasses can override to create a context with different services.
	 *
	 * @return
	 */
	@Override
	public Context createContext() {
		return new Context(ScriptService.class, DatasetIOService.class,
			ImageJService.class, CacheService.class, WidgetService.class,
			ImgUtilityService.class, PlaneConverterService.class,
			InitializeService.class, XMLService.class, SCIFIOService.class,
			UIService.class);
	}

	/**
	 * Sets up a SciJava context.
	 */
	@Before
	@Override
	public void setUp() {
		createContext().inject(this);
	}

	/**
	 * Disposes of the {@link OpService} that was initialized in {@link #setUp()}.
	 */
	@After
	@Override
	public synchronized void cleanUp() {
		if (context != null) {
			context.dispose();
			context = null;
			scriptService = null;
		}
	}

	/** Asserts that a particular chunk of samples have expected values. */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void assertSamplesEqual(
		final Dataset image, final int offset,
		final double[] expected)
	{
		final Class<?> imageType = image.firstElement().getClass();
		assertTrue(RealType.class.isAssignableFrom(imageType));
		assertSamplesEqual((IterableInterval) image, offset, expected);
	}

	/** Asserts that a particular chunk of samples have expected values. */
	public <T extends RealType<T>> void assertSamplesEqual(
		final IterableInterval<T> image, final int offset,
		final double[] expected)
	{
		int i = -offset;
		for (final T sample : image) {
			if (i >= expected.length) break;
			if (i >= 0) assertEquals(expected[i], sample.getRealDouble(), 0);
			i++;
		}
	}

	/** Asserts that all image samples are the same particular value. */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void assertConstant(final Dataset image, final double value) {
		final Class<?> imageType = image.firstElement().getClass();
		assertTrue(RealType.class.isAssignableFrom(imageType));
		assertConstant((IterableInterval) image, value);
	}

	/** Asserts that all image samples are the same particular value. */
	public <T extends RealType<T>> void assertConstant(
		final IterableInterval<T> image, final double value)
	{
		for (final T sample : image) {
			assertEquals(value, sample.getRealDouble(), 0);
		}
	}
}
