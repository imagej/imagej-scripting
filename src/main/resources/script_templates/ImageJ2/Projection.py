# @Dataset data
# @String(label="Dimension to Project", choices={"X", "Y", "Z", "TIME", "CHANNEL"}) projected_dimension
# @String(label="Projection Type", choices={"Max","Mean","Median","Min", "StdDev", "Sum"}) projection_type
# @OUTPUT Dataset output
# @OpService ops
# @DatasetService ds

# Do a projection of a stack. The projection is done along a specified axis.
# The commin use case of this script is to do a maximum Z projection.
 
from net.imagej.axis import Axes
from net.imagej.ops import Ops

# Select which dimension to project
dim = data.dimensionIndex(getattr(Axes, projected_dimension))

if dim == -1:
    raise Exception("%s dimension not found." % projected_dimension)

if data.dimension(dim) < 2:
    raise Exception("%s dimension has only one frame." % projected_dimension)

# Write the output dimensions
new_dimensions = [data.dimension(d) for d in range(0, data.numDimensions()) if d != dim]

# Create the output image
projected = ops.create().img(new_dimensions)

# Create the op and run it
proj_op = ops.op(getattr(Ops.Stats, projection_type), data)
ops.transform().project(projected, data, proj_op, dim)

# Create the output Dataset
output = ds.create(projected)
