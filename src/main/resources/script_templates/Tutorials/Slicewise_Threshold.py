# @OpService ops
# @Dataset data
# @UIService ui

# To run this tutorial, go to 'file>Open Samples>Confocal Series' and make sure that
# confocal-series.tif is the active image

from net.imagej.ops import Ops
from net.imagej import ImgPlus
from net.imglib2 import FinalDimensions
from net.imglib2.type.logic import BitType
from net.imagej.axis import Axes


# first take a look at the size and type of each dimension
for d in range(data.numDimensions()):
	print "axis d: type: "+str(data.axis(d).type())+" length: "+str(data.dimension(d))

xDim = data.dimensionIndex(Axes.X)
yDim = data.dimensionIndex(Axes.Y)
zDim = data.dimensionIndex(Axes.Z)
cDim = data.dimensionIndex(Axes.CHANNEL)

# create the otsu op
otsu=ops.op(Ops.Threshold.Otsu, data)

# create memory for the thresholded image
thresholded=ops.create().img(data.getImgPlus(), BitType())

# call slice wise thresholde axis to process, in this case [0,1] means process the 
# first two axes (x and y)
ops.slicewise(thresholded, data.getImgPlus(), otsu, [xDim,yDim])

# try again with [xDim, yDim, zDim] is the result different??  Why??

# create an ImgPlus using the thresholded img, copy meta data from the input
thresholdedPlus=ImgPlus(thresholded, data.getImgPlus(), True)

ui.show("thresholded", thresholdedPlus)
