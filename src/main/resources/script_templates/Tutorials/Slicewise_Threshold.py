# @OpService ops
# @Dataset data
# @OUTPUT ImgPlus thresholdedPlus

# To run this tutorial, go to 'File>Open Samples>Confocal Series' and make sure
# that confocal-series.tif is the active image

from net.imagej.ops import Ops
from net.imagej import ImgPlus
from net.imglib2.type.logic import BitType
from net.imagej.axis import Axes


# first take a look at the size and type of each dimension
for d in range(data.numDimensions()):
	print "axis "+str(d)+": type: "+str(data.axis(d).type())+" length: "+str(data.dimension(d))
	
xDim = data.dimensionIndex(Axes.X)
yDim = data.dimensionIndex(Axes.Y)
zDim = data.dimensionIndex(Axes.Z)
cDim = data.dimensionIndex(Axes.CHANNEL) # Not used, just for reference

# create the otsu op
otsu=ops.op(Ops.Threshold.Otsu, data.getImgPlus())

# create memory for the thresholded image
thresholded=ops.create().img(data.getImgPlus(), BitType())

# call threshold op slice-wise for the defined axes, in this case [xDim,yDim] means process the 
# first two axes (x and y)
ops.slicewise(thresholded, data.getImgPlus(), otsu, [xDim,yDim])

# try again with [xDim, yDim, zDim] is the result different? Why?

# create an ImgPlus using the thresholded img, copy meta data from the input
thresholdedPlus=ImgPlus(thresholded, data.getImgPlus(), True)

# set a new name to avoid confusion
thresholdedPlus.setName("Thresholded")
