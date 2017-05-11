
package net.imagej.scripting;

import io.scif.services.DatasetIOService;

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

import net.imagej.Dataset;

import org.junit.Assert;
import org.junit.Test;
import org.scijava.plugin.Parameter;
import org.scijava.script.ScriptModule;

/**
 * @author Hadrien Mary
 */
public class ImageJ2ScriptTest extends AbstractScriptTest {

	@Parameter
	private DatasetIOService datasetIOService;

	@Test
	public void testDOGScript() throws InterruptedException, ExecutionException,
		IOException, URISyntaxException, FileNotFoundException, ScriptException
	{

		final Map<String, Object> parameters = new HashMap<>();

		final String testPath =
			"8bit-signed&pixelType=int8&axes=X,Y,Z,Channel,Time&lengths=10,10,3,2,10.fake";
		final Dataset data = datasetIOService.open(testPath);
		parameters.put("data", data);
		parameters.put("sigma1", 4.0);
		parameters.put("sigma2", 1.0);

		final File scriptFile = new File(getClass().getResource(
			"/script_templates/ImageJ2/Apply_DOG_Filtering.py").toURI());
		final ScriptModule m = scriptService.run(scriptFile, true, parameters)
			.get();

		final Dataset output = (Dataset) m.getOutput("output");
		Assert.assertNotNull(output);
	}

	@Test
	public void testMaskScript() throws InterruptedException, ExecutionException,
		IOException, URISyntaxException, FileNotFoundException, ScriptException
	{

		final Map<String, Object> parameters = new HashMap<>();

		final String testPath =
			"8bit-signed&pixelType=int8&axes=X,Y,Z,Channel,Time&lengths=10,10,3,2,10.fake";
		final Dataset data = datasetIOService.open(testPath);
		parameters.put("data", data);

		final Dataset mask = datasetIOService.open(testPath);
		parameters.put("mask", mask);

		final File scriptFile = new File(getClass().getResource(
			"/script_templates/ImageJ2/Apply_Mask.py").toURI());
		final ScriptModule m = scriptService.run(scriptFile, true, parameters)
			.get();

		final Dataset output = (Dataset) m.getOutput("output");
		Assert.assertNotNull(output);
	}

	@Test
	public void testThresholdScript() throws InterruptedException,
		ExecutionException, IOException, URISyntaxException, FileNotFoundException,
		ScriptException
	{

		final Map<String, Object> parameters = new HashMap<>();

		final String testPath =
			"8bit-signed&pixelType=int8&axes=X,Y,Z,Channel,Time&lengths=10,10,3,2,10.fake";
		final Dataset data = datasetIOService.open(testPath);
		parameters.put("data", data);

		parameters.put("method_threshold", "otsu");
		parameters.put("relative_threshold", 1.0);

		final File scriptFile = new File(getClass().getResource(
			"/script_templates/ImageJ2/Apply_Threshold.py").toURI());
		final ScriptModule m = scriptService.run(scriptFile, true, parameters)
			.get();

		final Dataset output = (Dataset) m.getOutput("output");
		Assert.assertNotNull(output);
	}

	@Test
	public void testFastThresholdScript() throws InterruptedException,
		ExecutionException, IOException, URISyntaxException, FileNotFoundException,
		ScriptException
	{

		final Map<String, Object> parameters = new HashMap<>();

		final String testPath =
			"8bit-signed&pixelType=int8&axes=X,Y,Z,Channel,Time&lengths=10,10,3,2,10.fake";
		final Dataset data = datasetIOService.open(testPath);
		parameters.put("data", data);

		parameters.put("method_threshold", "otsu");

		final File scriptFile = new File(getClass().getResource(
			"/script_templates/ImageJ2/Apply_Threshold.py").toURI());
		final ScriptModule m = scriptService.run(scriptFile, true, parameters)
			.get();

		final Dataset output = (Dataset) m.getOutput("output");
		Assert.assertNotNull(output);
	}

	@Test
	public void testCropScript() throws InterruptedException, ExecutionException,
		IOException, URISyntaxException, FileNotFoundException, ScriptException
	{

		final Map<String, Object> parameters = new HashMap<>();

		final String testPath =
			"8bit-signed&pixelType=int8&axes=X,Y,Z,Channel,Time&lengths=10,10,3,2,10.fake";
		final Dataset data = datasetIOService.open(testPath);
		parameters.put("data", data);

		final File scriptFile = new File(getClass().getResource(
			"/script_templates/ImageJ2/Crop.py").toURI());
		final ScriptModule m = scriptService.run(scriptFile, true, parameters)
			.get();

		final Dataset output = (Dataset) m.getOutput("output");
		Assert.assertNotNull(output);
	}

	@Test
	public void testRotateScript() throws InterruptedException,
		ExecutionException, IOException, URISyntaxException, FileNotFoundException,
		ScriptException
	{

		final Map<String, Object> parameters = new HashMap<>();

		final String testPath =
			"8bit-signed&pixelType=int8&axes=X,Y,Time&lengths=10,10,10.fake";
		final Dataset data = datasetIOService.open(testPath);
		parameters.put("data", data);

		parameters.put("angle", 0.0);

		final File scriptFile = new File(getClass().getResource(
			"/script_templates/ImageJ2/Rotate_Stack.py").toURI());
		final ScriptModule m = scriptService.run(scriptFile, true, parameters)
			.get();

		final Dataset output = (Dataset) m.getOutput("output");
		Assert.assertNotNull(output);
	}

	@Test
	public void testSubtractFirstFrameScript() throws InterruptedException,
		ExecutionException, IOException, URISyntaxException, FileNotFoundException,
		ScriptException
	{

		final Map<String, Object> parameters = new HashMap<>();

		final String testPath =
			"8bit-signed&pixelType=int8&axes=X,Y,Time&lengths=10,10,10.fake";
		final Dataset data = datasetIOService.open(testPath);
		parameters.put("data", data);

		final File scriptFile = new File(getClass().getResource(
			"/script_templates/ImageJ2/Subtract_First_Image_Stack.py").toURI());
		final ScriptModule m = scriptService.run(scriptFile, true, parameters)
			.get();

		final Dataset output = (Dataset) m.getOutput("output");
		Assert.assertNotNull(output);
	}

	@Test
	public void testStackDirectoryImagesScript() throws InterruptedException,
		ExecutionException, IOException, URISyntaxException, FileNotFoundException,
		ScriptException
	{

		// Create a temp directory and store some 2D images inside
		final Path tempDir = Files.createTempDirectory("temp_images_sequence");
		final String testPath =
			"8bit-signed&pixelType=int8&axes=X,Y&lengths=10,10.fake";
		String fname;
		Dataset data;
		for (int i = 0; i < 5; i++) {
			data = datasetIOService.open(testPath);
			fname = Paths.get(tempDir.toString(), "image_" + i + ".tif").toString();
			datasetIOService.save(data, fname);
		}

		final Map<String, Object> parameters = new HashMap<>();
		parameters.put("images_sequence_dir", tempDir.toString());
		parameters.put("image_extension", ".tif");
		parameters.put("axis_type", "TIME");

		final File scriptFile = new File(getClass().getResource(
			"/script_templates/ImageJ2/Stack_Directory_Images.py").toURI());
		final ScriptModule m = scriptService.run(scriptFile, true, parameters)
			.get();

		final Dataset output = (Dataset) m.getOutput("output");
		Assert.assertNotNull(output);
	}
}
