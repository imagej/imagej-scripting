/*-
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2015 - 2022 Board of Regents of the University of
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

import static org.junit.Assert.assertEquals;

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

import org.junit.Test;
import org.scijava.plugin.Parameter;
import org.scijava.script.ScriptModule;

/**
 * @author Hadrien Mary
 */
public class DeconvolutionScriptTest extends AbstractScriptTest {

	@Parameter
	protected DatasetIOService datasetIOService;

	@Test
	@SuppressWarnings("unchecked")
	public <T extends RealType<T>> void testDeconWithGaussian()
		throws InterruptedException, ExecutionException, IOException,
		URISyntaxException, FileNotFoundException, ScriptException
	{

		final String testPath =
			"8bit-signed&pixelType=int8&axes=X,Y,Z,&lengths=100,100,50.fake";
		final Dataset data = datasetIOService.open(testPath);

		final Map<String, Object> parameters = new HashMap<>();

		parameters.put("img", data.getImgPlus());
		parameters.put("sxy", 2.0);
		parameters.put("sz", 6.0);
		parameters.put("numIterations", 3);

		final File scriptFile = new File(getClass().getResource(
			"/script_templates/Deconvolution/DeconWithGaussian.py").toURI());
		final ScriptModule m = scriptService.run(scriptFile, true, parameters)
			.get();

		final ImgPlus<T> output1 = (ImgPlus<T>) m.getOutput("deconvolved");
		assertEquals(3, output1.numDimensions());
		final double[] expected = { 378.56768798828125, 97.66587829589844,
			22.849470138549805, 1.997551679611206, 51.35069274902344,
			73.30968475341797, -193.34324645996094, -616.32958984375,
			-628.3247680664062, -180.4445037841797 };
		assertSamplesEqual(output1, 1000, expected);
	}
}
