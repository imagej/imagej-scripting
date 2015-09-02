# @OpService ops
# @UIService ui
# @net.imagej.Dataset data

from net.imagej.ops import Ops
from net.imagej.axis import Axes

# get the dimension to project
pDim = data.dimensionIndex(Axes.Z);

# generate the projected dimension
projectedDimensions=[data.dimension(x) for x in range(0, data.numDimensions()) if x!=pDim]
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
ops.image().project(maxProjection, data, maxOp, pDim)

# project again this time use sum projection
ops.image().project(sumProjection, data, sumOp, pDim)

# display the results
ui.show("max projection", maxProjection)
ui.show("sum projection", sumProjection)
