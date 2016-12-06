# @Dataset data
# @Float(label="Sigma 1 (pixel)", required=true, value=4.2, stepSize=0.1) sigma1
# @Float(label="Sigma 2 (pixel)", required=true, value=1.25, stepSize=0.1) sigma2
# @OUTPUT Dataset output

# @ImageJ ij

# Run a DOG filter on all the frames along the TIME axis.
# After the filtering step the image is clipped to match the input type.

from net.imagej.axis import Axes

# Convert data to float 32
converted = ij.op().convert().float32(data.getImgPlus())
 
# Allocate output memory (wait for hybrid CF version of slice)
dog = ij.op().create().img(converted)
 
# Create the op
dog_op = ij.op().op("filter.dog", converted, sigma1, sigma2)
 
# Setup the fixed axis
t_dim = data.dimensionIndex(Axes.TIME)
fixed_axis = [d for d in range(0, data.numDimensions()) if d != t_dim]
 
# Run the op
ij.op().slice(dog, converted, dog_op, fixed_axis)
 
# Clip image to the input type
clipped = ij.op().create().img(dog, data.getImgPlus().firstElement())
clip_op = ij.op().op("convert.clip", data.getImgPlus().firstElement(), dog.firstElement())
ij.op().convert().imageType(clipped, dog, clip_op)

# Create output Dataset
output = ij.dataset().create(clipped)