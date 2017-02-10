package net.imagej.scripting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.script.ScriptException;

import org.junit.Assert;
import org.junit.Test;
import org.scijava.plugin.Parameter;
import org.scijava.script.ScriptModule;

import io.scif.services.DatasetIOService;
import net.imagej.Dataset;
import net.imagej.ImgPlus;
import net.imglib2.type.numeric.RealType;

/**
 *
 * @author Hadrien Mary
 */
public class DeconvolutionScriptTest extends AbstractScriptTest {

    @Parameter
    protected DatasetIOService datasetIOService;

    @Test
    @SuppressWarnings("unchecked")
    public <T extends RealType<T>> void testDeconWithGaussian() throws InterruptedException, ExecutionException,
            IOException, URISyntaxException, FileNotFoundException, ScriptException {

        String testPath = "8bit-signed&pixelType=int8&axes=X,Y,Z,&lengths=100,100,50.fake";
        Dataset data = datasetIOService.open(testPath);

        Map<String, Object> parameters = new HashMap<>();

        parameters.put("img", data.getImgPlus());
        parameters.put("sxy", 2.0);
        parameters.put("sz", 6.0);
        parameters.put("numIterations", 3);

        File scriptFile = new File(
                getClass().getResource("/script_templates/Deconvolution/DeconWithGaussian.py").toURI());
        final ScriptModule m = scriptService.run(scriptFile, true, parameters).get();

        final ImgPlus<T> output1 = (ImgPlus<T>) m.getOutput("deconvolved");
        Assert.assertNotNull(output1);

    }
}
