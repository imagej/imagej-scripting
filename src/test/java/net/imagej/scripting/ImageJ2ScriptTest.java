package net.imagej.scripting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
public class ImageJ2ScriptTest extends AbstractScriptTest {

	@Parameter
	protected DatasetIOService datasetIOService;

	@Test
	public void testDOGScript() throws InterruptedException, ExecutionException, IOException, URISyntaxException,
			FileNotFoundException, ScriptException {

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
	public void testMaskScript() throws InterruptedException, ExecutionException, IOException, URISyntaxException,
			FileNotFoundException, ScriptException {

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
	public void testThresholdScript() throws InterruptedException, ExecutionException, IOException, URISyntaxException,
			FileNotFoundException, ScriptException {

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
	public void testFastThresholdScript() throws InterruptedException, ExecutionException, IOException,
			URISyntaxException, FileNotFoundException, ScriptException {

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
	public void testCropScript() throws InterruptedException, ExecutionException, IOException, URISyntaxException,
			FileNotFoundException, ScriptException {

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
	public void testParticlesFromMaskScript() throws InterruptedException, ExecutionException, IOException,
			URISyntaxException, FileNotFoundException, ScriptException {

		// This script calls IJ1 stuff (RoiManager) that draw some UI which is
		// not desirable in my
		// opinion for a test. So I just don't run it.
		// Map<String, Object> parameters = new HashMap<>();
		//
		// String testPath =
		// "8bit-signed&pixelType=int8&axes=X,Y,Z,Channel,Time&lengths=10,10,3,2,10.fake";
		// Dataset data = datasetIOService.open(testPath);
		// parameters.put("data", data);
		//
		// Dataset mask = datasetIOService.open(testPath);
		// parameters.put("mask", mask);
		//
		// File scriptFile = new
		// File(getClass().getResource("/script_templates/ImageJ2/Particles_From_Mask.py").toURI());
		// scriptService.run(scriptFile, true, parameters).get();
	}

	@Test
	public void testRotateScript() throws InterruptedException, ExecutionException, IOException, URISyntaxException,
			FileNotFoundException, ScriptException {

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
	public void testSubtractFirstFrameScript() throws InterruptedException, ExecutionException, IOException,
			URISyntaxException, FileNotFoundException, ScriptException {

		Map<String, Object> parameters = new HashMap<>();

		String testPath = "8bit-signed&pixelType=int8&axes=X,Y,Time&lengths=10,10,10.fake";
		Dataset data = datasetIOService.open(testPath);
		parameters.put("data", data);

		File scriptFile = new File(
				getClass().getResource("/script_templates/ImageJ2/Subtract_First_Image_Stack.py").toURI());
		final ScriptModule m = scriptService.run(scriptFile, true, parameters).get();

		final Dataset output = (Dataset) m.getOutput("output");
		Assert.assertNotNull(output);
	}

	@Test
	public void testStackDirectoryImagesScript() throws InterruptedException, ExecutionException, IOException,
			URISyntaxException, FileNotFoundException, ScriptException {

		// Create a temp directory and store some 2D images inside
		Path tempDir = Files.createTempDirectory("temp_images_sequence");
		String testPath = "8bit-signed&pixelType=int8&axes=X,Y&lengths=10,10.fake";
		String fname;
		Dataset data;
		for (int i = 0; i < 5; i++) {
			data = datasetIOService.open(testPath);
			fname = Paths.get(tempDir.toString(), "image_" + i + ".tif").toString();
			datasetIOService.save(data, fname);
		}

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("images_sequence_dir", tempDir.toString());
		parameters.put("image_extension", ".tif");
		parameters.put("axis_type", "TIME");

		File scriptFile = new File(
				getClass().getResource("/script_templates/ImageJ2/Stack_Directory_Images.py").toURI());
		final ScriptModule m = scriptService.run(scriptFile, true, parameters).get();

		final Dataset output = (Dataset) m.getOutput("output");
		Assert.assertNotNull(output);
	}

	@Test
	public <T extends RealType<T>> void testCropConfocalSeries() throws InterruptedException, ExecutionException,
			IOException, URISyntaxException, FileNotFoundException, ScriptException {

		Map<String, Object> parameters = new HashMap<>();

		String testPath = "8bit-signed&pixelType=int8&axes=X,Y,Channel,Z,&lengths=400,400,2,25.fake";
		Dataset data = datasetIOService.open(testPath);
		parameters.put("data", data);
		parameters.put("sigma1", 4.0);
		parameters.put("sigma2", 1.0);

		File scriptFile = new File(
				getClass().getResource("/script_templates/Tutorials/Crop_Confocal_Series.py").toURI());
		final ScriptModule m = scriptService.run(scriptFile, true, parameters).get();

		@SuppressWarnings("unchecked")
		final ImgPlus<T> output = (ImgPlus<T>) m.getOutput("c0");
		Assert.assertNotNull(output);
	}

	//@Test
	public void testFindTemplate() throws InterruptedException, ExecutionException, IOException, URISyntaxException,
			FileNotFoundException, ScriptException {

		Map<String, Object> parameters = new HashMap<>();

		String testPath1 = "8bit-signed&pixelType=int8&axes=X,Y,&lengths=400,400.fake";
		String testPath2 = "8bit-signed&pixelType=int8&axes=X,Y,&lengths=40,40.fake";

		Dataset data1 = datasetIOService.open(testPath1);
		Dataset data2 = datasetIOService.open(testPath2);

		parameters.put("image", data1);
		parameters.put("template", data2);

		File scriptFile = new File(getClass().getResource("/script_templates/Tutorials/Find_Template.py").toURI());
		scriptService.run(scriptFile, true, parameters).get();

	}

	@Test
	@SuppressWarnings("unchecked")
	public <T extends RealType<T>> void testCreateAndConvolvePoints() throws InterruptedException, ExecutionException,
			IOException, URISyntaxException, FileNotFoundException, ScriptException {

		Map<String, Object> parameters = new HashMap<>();

		parameters.put("xSize", 128);
		parameters.put("ySize", 128);
		parameters.put("zSize", 24);

		File scriptFile = new File(
				getClass().getResource("/script_templates/Tutorials/Create_And_Convolve_Points.py").toURI());
		final ScriptModule m = scriptService.run(scriptFile, true, parameters).get();

		final ImgPlus<T> output1 = (ImgPlus<T>) m.getOutput("phantom");
		Assert.assertNotNull(output1);

		final ImgPlus<T> output2 = (ImgPlus<T>) m.getOutput("convolved");
		Assert.assertNotNull(output2);

	}

	@Test
	@SuppressWarnings("unchecked")
	public <T extends RealType<T>> void testOpsThresholdMeasure() throws InterruptedException, ExecutionException,
			IOException, URISyntaxException, FileNotFoundException, ScriptException {

		String testPath = "8bit-signed&pixelType=int8&axes=X,Y,&lengths=100,100.fake";
		Dataset data = datasetIOService.open(testPath);

		Map<String, Object> parameters = new HashMap<>();

		parameters.put("inputData", data);
		parameters.put("sigma", 3);

		File scriptFile = new File(
				getClass().getResource("/script_templates/Tutorials/Ops_Threshold_Measure.py").toURI());
		final ScriptModule m = scriptService.run(scriptFile, true, parameters).get();

		final ImgPlus<T> output1 = (ImgPlus<T>) m.getOutput("logFiltered");
		Assert.assertNotNull(output1);

		final ImgPlus<T> output2 = (ImgPlus<T>) m.getOutput("thresholded");
		Assert.assertNotNull(output2);

	}

	@Test
	@SuppressWarnings("unchecked")
	public <T extends RealType<T>> void testProjections() throws InterruptedException, ExecutionException, IOException,
			URISyntaxException, FileNotFoundException, ScriptException {

		String testPath = "8bit-signed&pixelType=int8&axes=X,Y,Z,&lengths=100,100,100.fake";
		Dataset data = datasetIOService.open(testPath);

		Map<String, Object> parameters = new HashMap<>();

		parameters.put("data", data);

		File scriptFile = new File(getClass().getResource("/script_templates/Tutorials/Projections.py").toURI());
		final ScriptModule m = scriptService.run(scriptFile, true, parameters).get();

		final ImgPlus<T> output1 = (ImgPlus<T>) m.getOutput("maxProjection");
		Assert.assertNotNull(output1);

		final ImgPlus<T> output2 = (ImgPlus<T>) m.getOutput("sumProjection");
		Assert.assertNotNull(output2);

	}

	@Test
	@SuppressWarnings("unchecked")
	public <T extends RealType<T>> void testSimpleConvolution() throws InterruptedException, ExecutionException,
			IOException, URISyntaxException, FileNotFoundException, ScriptException {

		String testPath = "8bit-signed&pixelType=int8&axes=X,Y,&lengths=100,100.fake";
		Dataset data = datasetIOService.open(testPath);

		Map<String, Object> parameters = new HashMap<>();

		parameters.put("inputData", data);

		File scriptFile = new File(
				getClass().getResource("/script_templates/Tutorials/Simple_Convolution.groovy").toURI());
		final ScriptModule m = scriptService.run(scriptFile, true, parameters).get();

		final ImgPlus<T> output1 = (ImgPlus<T>) m.getOutput("filtered");
		Assert.assertNotNull(output1);

		final ImgPlus<T> output2 = (ImgPlus<T>) m.getOutput("result");
		Assert.assertNotNull(output2);

	}

	@Test
	@SuppressWarnings("unchecked")
	public <T extends RealType<T>> void testSlicewiseThreshold() throws InterruptedException, ExecutionException,
			IOException, URISyntaxException, FileNotFoundException, ScriptException {

		String testPath = "8bit-signed&pixelType=int8&axes=X,Y,Z,&lengths=100,100,100.fake";
		Dataset data = datasetIOService.open(testPath);

		Map<String, Object> parameters = new HashMap<>();

		parameters.put("data", data);

		File scriptFile = new File(
				getClass().getResource("/script_templates/Tutorials/Slicewise_Threshold.py").toURI());
		final ScriptModule m = scriptService.run(scriptFile, true, parameters).get();

		final ImgPlus<T> output1 = (ImgPlus<T>) m.getOutput("thresholdedPlus");
		Assert.assertNotNull(output1);

	}

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
