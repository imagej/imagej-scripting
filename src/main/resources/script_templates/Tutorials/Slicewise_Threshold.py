# @OpService ops
# @net.imagej.Dataset inputData
# @UIService ui

from net.imagej.ops import Ops
from net.imagej import ImgPlus
from net.imglib2 import FinalDimensions
from net.imglib2.type.logic import BitType


# first take a look at the size and type of each dimension
for d in range(inputData.numDimensions()):
	print "axis d: type: "+str(inputData.axis(d).type())+" length: "+str(inputData.dimension(d))

# create the otsu op
otsu=ops.op(Ops.Threshold.Otsu, inputData);

# create memory for the thresholded image
thresholded=ops.create().img(inputData.getImgPlus(), BitType())

# call slice wise thresholde axis to process, in this case [0,1] means process the 
# first two axes (x and y)
ops.slicewise(thresholded, inputData.getImgPlus(), otsu, [0,1])

# create an ImgPlus using the thresholded img, copy meta data from the input
thresholdedPlus=ImgPlus(thresholded, inputData.getImgPlus(), True)

ui.show("thresholded", thresholdedPlus)
