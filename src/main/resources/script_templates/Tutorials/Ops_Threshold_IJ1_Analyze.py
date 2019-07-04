#@ OpService ops
#@ ImgPlus inputData
#@ Double sigma
#@OUTPUT ImgPlus logFiltered
#@OUTPUT ImgPlus thresholded

# Run this tutorial using the C0Z12 image generated in the 'Crop Confocal Series' tutorial.

# To generate the C0Z12 image, do the following:
# Go to 'file>Open Samples>Confocal Series' and make sure confocal-series.tif is the active image and
# run the Crop Confocal Series tutorial.

from net.imglib2.img.display.imagej import ImageJFunctions
from ij import IJ

# create a log kernel
logKernel=ops.create().kernelLog(inputData.numDimensions(), sigma);

logFiltered=ops.filter().convolve(ops.create().img(inputData), inputData, logKernel)

# otsu threshold and display
thresholded = ops.threshold().otsu(logFiltered)

# convert to imagej1 imageplus so we can run analyze particles
impThresholded=ImageJFunctions.wrap(thresholded, "wrapped")

# convert to mask and analyze particles
IJ.run(impThresholded, "Convert to Mask", "")
IJ.run(impThresholded, "Analyze Particles...", "display add")
