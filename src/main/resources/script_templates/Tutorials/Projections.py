#@ OpService ops
#@ UIService ui
#@ Dataset data
#@OUTPUT ImgPlus maxProjection
#@OUTPUT ImgPlus sumProjection


# Run this tutorial using the C0 image generated in the 'Crop Confocal Series' tutorial.

# To generate the C0 image, do the following:
# Go to 'file>Open Samples>Confocal Series' and make sure confocal-series.tif is the active image and
# run the Crop Confocal Series tutorial.

from net.imagej.ops import Ops
from net.imagej.axis import Axes

# get the dimension to project
pDim = data.dimensionIndex(Axes.Z)

# generate the projected dimension
projectedDimensions=[data.dimension(d) for d in range(0, data.numDimensions()) if d!=pDim]
print projectedDimensions

# create memory for projections
maxProjection=ops.create().img(projectedDimensions)
sumProjection=ops.create().img(projectedDimensions)

# use op service to get the max op
maxOp = ops.op(Ops.Stats.Max, data)
# use op service to get the sum op
sumOp = ops.op(Ops.Stats.Sum, sumProjection.firstElement(), data)

# call the project op passing
# maxProjection: img to put projection in
# image: img to project
# op: the op used to generate the projection (in this case "max")
# dimensionToProject: the dimension to project
ops.transform().project(maxProjection, data, maxOp, pDim)

# project again this time use sum projection
ops.transform().project(sumProjection, data, sumOp, pDim)
