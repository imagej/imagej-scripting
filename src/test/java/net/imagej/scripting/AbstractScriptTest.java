package net.imagej.scripting;

import io.scif.SCIFIOService;
import io.scif.img.ImgUtilityService;
import io.scif.img.converters.PlaneConverterService;
import io.scif.services.DatasetIOService;
import io.scif.services.InitializeService;
import io.scif.xml.XMLService;
import net.imagej.ImageJService;

import org.scijava.script.ScriptService;
import org.scijava.Context;
import org.scijava.cache.CacheService;
import org.scijava.plugin.Parameter;

import org.junit.After;
import org.junit.Before;
import org.scijava.plugins.scripting.jython.JythonService;
import org.scijava.ui.UIService;
import org.scijava.widget.WidgetService;

/**
 *
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
        return new Context(ScriptService.class, JythonService.class, DatasetIOService.class,
                ImageJService.class, CacheService.class, WidgetService.class, ImgUtilityService.class,
                PlaneConverterService.class, InitializeService.class, XMLService.class,
                SCIFIOService.class, UIService.class);
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
}
