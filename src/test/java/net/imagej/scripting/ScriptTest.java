package net.imagej.scripting;

import org.junit.After;
import org.junit.Before;
import org.scijava.Context;

/**
 *
 * @author Hadrien Mary
 */
public interface ScriptTest {

    /**
     * Subclasses can override to create a context with different services.
     *
     * @return
     */
    Context createContext();

    /**
     * Sets up a SciJava context.
     */
    @Before
    public void setUp();

    /**
     * Disposes of the {@link OpService} that was initialized in {@link #setUp()}.
     */
    @After
    public void cleanUp();

}
