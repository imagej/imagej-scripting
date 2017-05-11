
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
import net.imagej.ImgPlus;
import net.imglib2.type.numeric.RealType;

import org.junit.Assert;
import org.junit.Test;
import org.scijava.plugin.Parameter;
import org.scijava.script.ScriptModule;

/**
 * @author Hadrien Mary
 */
public class TutorialsScriptTest extends AbstractScriptTest {

	@Parameter
	private DatasetIOService datasetIOService;

	@Test
	public <T extends RealType<T>> void testCropConfocalSeries()
		throws InterruptedException, ExecutionException, IOException,
		URISyntaxException, FileNotFoundException, ScriptException
	{

		final Map<String, Object> parameters = new HashMap<>();

		final String testPath =
			"8bit-signed&pixelType=int8&axes=X,Y,Channel,Z,&lengths=400,400,2,25.fake";
		final Dataset data = datasetIOService.open(testPath);
		parameters.put("data", data);
		parameters.put("sigma1", 4.0);
		parameters.put("sigma2", 1.0);

		final File scriptFile = new File(getClass().getResource(
			"/script_templates/Tutorials/Crop_Confocal_Series.py").toURI());
		final ScriptModule m = scriptService.run(scriptFile, true, parameters)
			.get();

		@SuppressWarnings("unchecked")
		final ImgPlus<T> output = (ImgPlus<T>) m.getOutput("c0");
		final double[] expected = { 0.0, -128.0, -128.0, -128.0, -128.0, -128.0,
			-128.0, -128.0, -128.0, -128.0, -128.0, -128.0, -128.0, -128.0, -128.0,
			-128.0, -128.0, -128.0, -128.0, -128.0, -128.0, -88.0 };
		assertSamplesEqual(output, 19, expected);
	}

	// @Test
	public void testFindTemplate() throws InterruptedException,
		ExecutionException, IOException, URISyntaxException, FileNotFoundException,
		ScriptException
	{

		final Map<String, Object> parameters = new HashMap<>();

		final String testPath1 =
			"8bit-signed&pixelType=int8&axes=X,Y,&lengths=400,400.fake";
		final String testPath2 =
			"8bit-signed&pixelType=int8&axes=X,Y,&lengths=40,40.fake";

		final Dataset data1 = datasetIOService.open(testPath1);
		final Dataset data2 = datasetIOService.open(testPath2);

		parameters.put("image", data1);
		parameters.put("template", data2);

		final File scriptFile = new File(getClass().getResource(
			"/script_templates/Tutorials/Find_Template.py").toURI());
		scriptService.run(scriptFile, true, parameters).get();

	}

	@Test
	@SuppressWarnings("unchecked")
	public <T extends RealType<T>> void testCreateAndConvolvePoints()
		throws InterruptedException, ExecutionException, IOException,
		URISyntaxException, FileNotFoundException, ScriptException
	{

		final Map<String, Object> parameters = new HashMap<>();

		parameters.put("xSize", 128);
		parameters.put("ySize", 128);
		parameters.put("zSize", 24);

		final File scriptFile = new File(getClass().getResource(
			"/script_templates/Tutorials/Create_and_Convolve_Points.py").toURI());
		final ScriptModule m = scriptService.run(scriptFile, true, parameters)
			.get();

		final ImgPlus<T> output1 = (ImgPlus<T>) m.getOutput("phantom");
		final double[] expected1 = new double[] { 255.0, 0.0 };
		assertSamplesEqual(output1, 102432, expected1);

		final ImgPlus<T> output2 = (ImgPlus<T>) m.getOutput("convolved");
		final double[] expected2 = { -4.118816399056868E-9, -2.6888646864620114E-9,
			-7.762487319595834E-10, 1.5811233433637994E-9, 4.294427036199977E-9,
			7.265440249426547E-9, 1.0367559255541892E-8, 1.346087241671512E-8,
			1.6421539683619812E-8, 1.9126369821265143E-8, 2.14812700960465E-8,
			2.3422717987386932E-8, 2.490532402532608E-8, 2.5911424117452952E-8,
			2.6449438195186303E-8, 2.6547994025349908E-8, 2.6253854201740978E-8,
			2.561551504243198E-8, 2.470525828357495E-8, 2.357626804894153E-8,
			2.228407325333137E-8 };
		assertSamplesEqual(output2, 0, expected2);

	}

	@Test
	@SuppressWarnings("unchecked")
	public <T extends RealType<T>> void testOpsThresholdMeasure()
		throws InterruptedException, ExecutionException, IOException,
		URISyntaxException, FileNotFoundException, ScriptException
	{

		final String testPath =
			"8bit-signed&pixelType=int8&axes=X,Y,&lengths=100,100.fake";
		final Dataset data = datasetIOService.open(testPath);

		final Map<String, Object> parameters = new HashMap<>();

		parameters.put("inputData", data);
		parameters.put("sigma", 3);

		final File scriptFile = new File(getClass().getResource(
			"/script_templates/Tutorials/Ops_Threshold_Measure.py").toURI());
		final ScriptModule m = scriptService.run(scriptFile, true, parameters)
			.get();

		final ImgPlus<T> output1 = (ImgPlus<T>) m.getOutput("logFiltered");
		final double[] expected1 = { 1.5894572769070692E-8, -3.1789145538141383E-8,
			0.0, 3.1789145538141383E-8, 5.563100202721216E-8, 3.973643103449831E-8,
			1.5894572769070692E-8, -3.1789145538141383E-8, 0.0, 3.1789145538141383E-8,
			-5.563100202721216E-8, -1.2715658215256553E-7, -1.1126200405442432E-7,
			-4.7683716530855236E-8, 0.05621512606739998, 0.1999766081571579,
			0.44583258032798767, 0.6688981652259827, 0.5973716974258423,
			0.06918635219335556, -0.6852334141731262 };
		assertSamplesEqual(output1, 0, expected1);

		final ImgPlus<T> output2 = (ImgPlus<T>) m.getOutput("thresholded");
		final double[] expected2 = { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0,
			1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0 };
		assertSamplesEqual(output2, 0, expected2);

	}

	@Test
	@SuppressWarnings("unchecked")
	public <T extends RealType<T>> void testProjections()
		throws InterruptedException, ExecutionException, IOException,
		URISyntaxException, FileNotFoundException, ScriptException
	{

		final String testPath =
			"8bit-signed&pixelType=int8&axes=X,Y,Z,&lengths=100,100,100.fake";
		final Dataset data = datasetIOService.open(testPath);

		final Map<String, Object> parameters = new HashMap<>();

		parameters.put("data", data);

		final File scriptFile = new File(getClass().getResource(
			"/script_templates/Tutorials/Projections.py").toURI());
		final ScriptModule m = scriptService.run(scriptFile, true, parameters)
			.get();

		final ImgPlus<T> output1 = (ImgPlus<T>) m.getOutput("maxProjection");
		final double[] expected1 = { 99.0, 99.0, 99.0, -29.0, -29.0, -29.0, -29.0,
			-29.0, -29.0, -29.0, -29.0, -29.0, -29.0, -98.0 };
		assertSamplesEqual(output1, 17, expected1);

		final ImgPlus<T> output2 = (ImgPlus<T>) m.getOutput("sumProjection");
		final double[] expected2 = { 4950.0, 4950.0, 4950.0, -7850.0, -7850.0,
			-7850.0, -7850.0, -7850.0, -7850.0, -7850.0, -7850.0, -7850.0, -7850.0,
			-9800.0 };
		assertSamplesEqual(output2, 17, expected2);

	}

	@Test
	@SuppressWarnings("unchecked")
	public <T extends RealType<T>> void testSimpleConvolution()
		throws InterruptedException, ExecutionException, IOException,
		URISyntaxException, FileNotFoundException, ScriptException
	{

		final String testPath =
			"8bit-signed&pixelType=int8&axes=X,Y,&lengths=100,100.fake";
		final Dataset data = datasetIOService.open(testPath);

		final Map<String, Object> parameters = new HashMap<>();

		parameters.put("inputData", data);

		final File scriptFile = new File(getClass().getResource(
			"/script_templates/Tutorials/Simple_Convolution.groovy").toURI());
		final ScriptModule m = scriptService.run(scriptFile, true, parameters)
			.get();

		final ImgPlus<T> output1 = (ImgPlus<T>) m.getOutput("filtered");
		final double[] expected1 = { -2.0, -2.0, -2.0, -2.0, -2.0, -2.0, -2.0, -2.0,
			-2.0, -2.0, -3.0, -3.0, -5.0, -7.0, -11.0, -15.0, -22.0, -29.0, -39.0,
			-48.0, -59.0 };
		assertSamplesEqual(output1, 0, expected1);

		final ImgPlus<T> output2 = (ImgPlus<T>) m.getOutput("result");
		final double[] expected2 = { 4.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0,
			0.0, 4.0, 0.0, 5.0, 4.0, 11.0, 11.0, 22.0, 25.0, 40.0, 45.0, 61.0 };
		assertSamplesEqual(output2, 0, expected2);

	}

	@Test
	@SuppressWarnings("unchecked")
	public <T extends RealType<T>> void testSlicewiseThreshold()
		throws InterruptedException, ExecutionException, IOException,
		URISyntaxException, FileNotFoundException, ScriptException
	{

		final String testPath =
			"8bit-signed&pixelType=int8&axes=X,Y,Z,&lengths=100,100,100.fake";
		final Dataset data = datasetIOService.open(testPath);

		final Map<String, Object> parameters = new HashMap<>();

		parameters.put("data", data);

		final File scriptFile = new File(getClass().getResource(
			"/script_templates/Tutorials/Slicewise_Threshold.py").toURI());
		final ScriptModule m = scriptService.run(scriptFile, true, parameters)
			.get();

		final ImgPlus<T> output1 = (ImgPlus<T>) m.getOutput("thresholdedPlus");
		Assert.assertNotNull(output1);

	}

}
