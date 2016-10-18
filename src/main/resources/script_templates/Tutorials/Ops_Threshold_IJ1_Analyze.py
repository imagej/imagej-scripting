# @OpService ops
# @Dataset inputData
# @Double sigma
# @OUTPUT Dataset logFiltered
# @OUTPUT Dataset thresholded

# Run this tutorial using the C0Z16 image generated in the 'Crop Confocal Series' tutorial.

# To generate the C0Z16 image, do the following:
# Go to 'file>Open Samples>Confocal Series' and make sure confocal-series.tif is the active image and 
# run the Crop Confocal Series tutorial.

from net.imglib2.img.display.imagej import ImageJFunctions
from ij import IJ
from java.lang import Integer

# create a log kernel
logKernel=ops.create().kernelLog(inputData.numDimensions(), sigma);

# convolve with log kernel
logFiltered=ops.filter().convolve(inputData, logKernel)

# display log filter result
logFiltered.setName("log")

# otsu threshold and display
thresholded = ops.threshold().otsu(logFiltered)
thresholded.setName("thresholded")

# convert to imagej1 imageplus so we can run analyze particles
impThresholded=ImageJFunctions.wrap(thresholded, "wrapped")

# convert to mask and analyze particles
IJ.run(impThresholded, "Convert to Mask", "")
IJ.run(impThresholded, "Analyze Particles...", "display add")
