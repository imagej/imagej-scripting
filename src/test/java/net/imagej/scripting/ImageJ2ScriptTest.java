/*-
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2015 - 2024 Board of Regents of the University of
 * 			Wisconsin-Madison.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

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
		assertConstant(output, 0);
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
		assertConstant(output, 0);
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
		assertConstant(output, 0);
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
		assertConstant(output, 0);
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
		assertConstant(output, 0);
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
		assertConstant(output, 0);
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
		assertConstant(output, 0);
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
		assertConstant(output, 0);
	}
}
