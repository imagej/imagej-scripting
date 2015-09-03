# @OpService ops
# @Dataset data
# @UIService ui
# @DisplayService display

# to run this tutorial run 'file->Open Samples->Confocal Series' and make sure that
# confocal-series.tif is the active image

from net.imglib2.util import Intervals
from net.imagej.axis import Axes

# first take a look at the size and type of each dimension
for d in range(data.numDimensions()):
	print "axis d: type: "+str(data.axis(d).type())+" length: "+str(data.dimension(d))

img=data.getImgPlus()

xLen = data.dimension(data.dimensionIndex(Axes.X))
yLen = data.dimension(data.dimensionIndex(Axes.Y))
zLen = data.dimension(data.dimensionIndex(Axes.Z))
cLen = data.dimension(data.dimensionIndex(Axes.CHANNEL))

# crop a channel
c0=ops.image().crop(img, Intervals.createMinMax(0, 0, 0,0,xLen-1, yLen-1, 0, zLen-1))
c0.setName("c0")

# crop both channels at z=12
z12=ops.image().crop(img, Intervals.createMinMax(0,0,0,12, xLen-1, yLen-1, cLen-1, 12))
z12.setName("z12")

# crop channel 0 at z=12
c0z12=ops.image().crop(img, Intervals.createMinMax(0,0,0,12, xLen-1, yLen-1, 0, 12))
c0z12.setName("c0z12")

# crop an roi at channel 0, z=12
roiC0z12=ops.image().crop(img, Intervals.createMinMax(150,150,0,12, 200, 200, 0, 12))
roiC0z12.setName("roiC0z12")

# display all the cropped images
ui.show(c0)
ui.show(z12)
ui.show(c0z12)
ui.show(roiC0z12)

