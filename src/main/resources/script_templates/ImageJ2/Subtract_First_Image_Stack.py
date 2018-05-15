#@ Dataset data
#@OUTPUT Dataset output
#@ OpService ops
#@ DatasetService ds

# Subtract the first frame of a stack to all the frames of the given stack along the TIME axis.
# It removes the static elements from a stack. Usefull when you are studying moving objects.

from net.imglib2.util import Intervals
from net.imagej.axis import Axes

# Convert input
converted = ops.convert().float32(data)

# Get the first frame (TODO: find a more convenient way !)
t_dim = data.dimensionIndex(Axes.TIME)
interval_start = []
interval_end = []
for d in range(0, data.numDimensions()):
    if d != t_dim:
        interval_start.append(0)
        interval_end.append(data.dimension(d) - 1)
    else:
        interval_start.append(0)
        interval_end.append(0)

intervals = interval_start + interval_end
intervals = Intervals.createMinMax(*intervals)

first_frame = ops.transform().crop(converted, intervals)

# Allocate output memory (wait for hybrid CF version of slice)
subtracted = ops.create().img(converted)

# Create the op
sub_op =  ops.op("math.subtract", first_frame, first_frame)

# Setup the fixed axis
fixed_axis = [d for d in range(0, data.numDimensions()) if d != t_dim]

# Run the op
ops.slice(subtracted, converted, sub_op, fixed_axis)

# Clip image to the input type
clipped = ops.create().img(subtracted, data.getImgPlus().firstElement())
clip_op = ops.op("convert.clip", data.getImgPlus().firstElement(), subtracted.firstElement())
ops.convert().imageType(clipped, subtracted, clip_op)

# Create output Dataset
output = ds.create(clipped)
