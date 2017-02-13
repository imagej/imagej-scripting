# @OpService ops
# @ImgPlus inputData
# @Double sigma
# @OUTPUT ImgPlus logFiltered
# @OUTPUT ImgPlus thresholded
# @OUTPUT ImgPlus labelingIndex

# Run this tutorial using the C0Z12 image generated in the 'Crop Confocal Series' tutorial.

# To generate the C0Z12 image, do the following:
# Go to 'file>Open Samples>Confocal Series' and make sure confocal-series.tif is the active image and 
# run the Crop Confocal Series tutorial.

from net.imglib2.algorithm.labeling.ConnectedComponents import StructuringElement
from net.imglib2.roi import Regions;
from net.imglib2.roi.labeling import LabelRegions;

# create a log kernel
logKernel=ops.create().kernelLog(inputData.numDimensions(), sigma);

logFiltered=ops.filter().convolve(inputData, logKernel)

# otsu threshold and display
thresholded = ops.threshold().otsu(logFiltered)

# call connected components to label each connected region
labeling=ops.labeling().cca(thresholded, StructuringElement.FOUR_CONNECTED)

# get the index image (each object will have a unique gray level)
labelingIndex=labeling.getIndexImg()

# get the collection of regions and loop through them
regions=LabelRegions(labeling)
for region in regions:
	# get the size of the region
	size=region.size()

	# get the intensity by "sampling" the intensity of the input image at the region pixels
	intensity=ops.stats().mean(Regions.sample(region, inputData)).getRealDouble()

	print "size",size,"intensity",intensity
	
	