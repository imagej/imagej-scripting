package net.imagej.scripting;

import io.scif.services.DatasetIOService;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.script.ScriptException;

import net.imagej.Dataset;

import org.junit.Assert;
import org.junit.Test;
import org.scijava.plugin.Parameter;
import org.scijava.script.ScriptModule;

/**
 *
 * @author Hadrien Mary
 */
public class ImageJ2ScriptTest extends AbstractScriptTest {

    @Parameter
    protected DatasetIOService datasetIOService;

    @Test
    public void testDOGScript() throws InterruptedException, ExecutionException,
            IOException, URISyntaxException, FileNotFoundException, ScriptException {

        Map<String, Object> parameters = new HashMap<>();

        String testPath = "8bit-signed&pixelType=int8&axes=X,Y,Z,Channel,Time&lengths=10,10,3,2,10.fake";
        Dataset data = datasetIOService.open(testPath);
        parameters.put("data", data);
        parameters.put("sigma1", 4.0);
        parameters.put("sigma2", 1.0);

        File scriptFile = new File(getClass().getResource("/script_templates/ImageJ2/Apply_DOG_Filtering.py").toURI());
        final ScriptModule m = scriptService.run(scriptFile, true, parameters).get();

        final Dataset output = (Dataset) m.getOutput("output");
        Assert.assertNotNull(output);
    }

    @Test
    public void testMaskScript() throws InterruptedException, ExecutionException,
            IOException, URISyntaxException, FileNotFoundException, ScriptException {

        Map<String, Object> parameters = new HashMap<>();

        String testPath = "8bit-signed&pixelType=int8&axes=X,Y,Z,Channel,Time&lengths=10,10,3,2,10.fake";
        Dataset data = datasetIOService.open(testPath);
        parameters.put("data", data);

        Dataset mask = datasetIOService.open(testPath);
        parameters.put("mask", mask);

        File scriptFile = new File(getClass().getResource("/script_templates/ImageJ2/Apply_Mask.py").toURI());
        final ScriptModule m = scriptService.run(scriptFile, true, parameters).get();

        final Dataset output = (Dataset) m.getOutput("output");
        Assert.assertNotNull(output);
    }

    @Test
    public void testThresholdScript() throws InterruptedException, ExecutionException,
            IOException, URISyntaxException, FileNotFoundException, ScriptException {

        Map<String, Object> parameters = new HashMap<>();

        String testPath = "8bit-signed&pixelType=int8&axes=X,Y,Z,Channel,Time&lengths=10,10,3,2,10.fake";
        Dataset data = datasetIOService.open(testPath);
        parameters.put("data", data);

        parameters.put("method_threshold", "otsu");
        parameters.put("relative_threshold", 1.0);

        File scriptFile = new File(getClass().getResource("/script_templates/ImageJ2/Apply_Threshold.py").toURI());
        final ScriptModule m = scriptService.run(scriptFile, true, parameters).get();

        final Dataset output = (Dataset) m.getOutput("output");
        Assert.assertNotNull(output);
    }

    @Test
    public void testFastThresholdScript() throws InterruptedException, ExecutionException,
            IOException, URISyntaxException, FileNotFoundException, ScriptException {

        Map<String, Object> parameters = new HashMap<>();

        String testPath = "8bit-signed&pixelType=int8&axes=X,Y,Z,Channel,Time&lengths=10,10,3,2,10.fake";
        Dataset data = datasetIOService.open(testPath);
        parameters.put("data", data);

        parameters.put("method_threshold", "otsu");

        File scriptFile = new File(getClass().getResource("/script_templates/ImageJ2/Apply_Threshold.py").toURI());
        final ScriptModule m = scriptService.run(scriptFile, true, parameters).get();

        final Dataset output = (Dataset) m.getOutput("output");
        Assert.assertNotNull(output);
    }

    @Test
    public void testCropScript() throws InterruptedException, ExecutionException,
            IOException, URISyntaxException, FileNotFoundException, ScriptException {

        Map<String, Object> parameters = new HashMap<>();

        String testPath = "8bit-signed&pixelType=int8&axes=X,Y,Z,Channel,Time&lengths=10,10,3,2,10.fake";
        Dataset data = datasetIOService.open(testPath);
        parameters.put("data", data);

        File scriptFile = new File(getClass().getResource("/script_templates/ImageJ2/Crop.py").toURI());
        final ScriptModule m = scriptService.run(scriptFile, true, parameters).get();

        final Dataset output = (Dataset) m.getOutput("output");
        Assert.assertNotNull(output);
    }

    @Test
    public void testParticlesFromMaskScript() throws InterruptedException, ExecutionException,
            IOException, URISyntaxException, FileNotFoundException, ScriptException {

        // This script calls IJ1 stuff (RoiManager) that draw some UI which is not desirable in my
        // opinion for a test. So I just don't run it.
//        Map<String, Object> parameters = new HashMap<>();
//
//        String testPath = "8bit-signed&pixelType=int8&axes=X,Y,Z,Channel,Time&lengths=10,10,3,2,10.fake";
//        Dataset data = datasetIOService.open(testPath);
//        parameters.put("data", data);
//
//        Dataset mask = datasetIOService.open(testPath);
//        parameters.put("mask", mask);
//
//        File scriptFile = new File(getClass().getResource("/script_templates/ImageJ2/Particles_From_Mask.py").toURI());
//        scriptService.run(scriptFile, true, parameters).get();
    }

    @Test
    public void testRotateScript() throws InterruptedException, ExecutionException,
            IOException, URISyntaxException, FileNotFoundException, ScriptException {

        Map<String, Object> parameters = new HashMap<>();

        String testPath = "8bit-signed&pixelType=int8&axes=X,Y,Time&lengths=10,10,10.fake";
        Dataset data = datasetIOService.open(testPath);
        parameters.put("data", data);

        parameters.put("angle", 0.0);

        File scriptFile = new File(getClass().getResource("/script_templates/ImageJ2/Rotate_Stack.py").toURI());
        final ScriptModule m = scriptService.run(scriptFile, true, parameters).get();

        final Dataset output = (Dataset) m.getOutput("output");
        Assert.assertNotNull(output);
    }
    
        @Test
    public void testSubtractFirstFrameScript() throws InterruptedException, ExecutionException,
            IOException, URISyntaxException, FileNotFoundException, ScriptException {

        Map<String, Object> parameters = new HashMap<>();

        String testPath = "8bit-signed&pixelType=int8&axes=X,Y,Time&lengths=10,10,10.fake";
        Dataset data = datasetIOService.open(testPath);
        parameters.put("data", data);

        File scriptFile = new File(getClass().getResource("/script_templates/ImageJ2/Subtract_First_Image_Stack.py").toURI());
        final ScriptModule m = scriptService.run(scriptFile, true, parameters).get();

        final Dataset output = (Dataset) m.getOutput("output");
        Assert.assertNotNull(output);
    }
}
