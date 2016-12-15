# @Float(label="Rotation angle (in degree)", required=true, value=90, stepSize=0.1) angle
# @Dataset data
# @OUTPUT Dataset output
# @OpService ops
# @DatasetService ds

# This script rotates all the frame of a stack along the TIME axis to a given angle.
# I found this script over complicated for what it is supposed to do. I hope a simpler way to do this kind of 
# transformation will be avaiable one day. At that time the script would have to be updated.

import math
from net.imagej.axis import Axes
from net.imglib2.interpolation.randomaccess import LanczosInterpolatorFactory
from net.imglib2.realtransform import AffineTransform2D
from net.imglib2.realtransform import RealViews
from net.imglib2.util import Intervals
from net.imglib2.view import Views

 
def get_axis(axis_type):
    return {
        'X': Axes.X,
        'Y': Axes.Y,
        'Z': Axes.Z,
        'TIME': Axes.TIME,
        'CHANNEL': Axes.CHANNEL,
    }.get(axis_type, Axes.Z)
     
 
def crop_along_one_axis(ops, data, intervals, axis_type):
    """Crop along a single axis using Views.
 
    Parameters
    ----------
    intervals : List with two values specifying the start and the end of the interval.
    axis_type : Along which axis to crop. Can be ["X", "Y", "Z", "TIME", "CHANNEL"]
    """
 
    axis = get_axis(axis_type)
    interval_start = [data.min(d) if d != data.dimensionIndex(axis) else intervals[0] for d in range(0, data.numDimensions())]
    interval_end = [data.max(d) if d != data.dimensionIndex(axis) else intervals[1] for d in range(0, data.numDimensions())]
 
    interval = interval_start + interval_end
    interval = Intervals.createMinMax(*interval)
 
    output = ops.run("transform.crop", data, interval, True)
 
    return output
    
 
# Get the center of the images so we do the rotation according to it
center = [int(round((data.max(d) / 2 + 1))) for d in range(2)]
 
# Convert angles to radians
angle_rad = angle * math.pi / 180
 
# Build the affine transformation
affine = AffineTransform2D()
affine.translate([-p for p in center])
affine.rotate(angle_rad)
affine.translate(center)
 
# Get the interpolator
interpolator = LanczosInterpolatorFactory()
 
# Iterate over all frame in the stack
axis = Axes.TIME
output = []
for d in range(data.dimension(axis)):
 
    # Get the current frame
    frame = crop_along_one_axis(ops, data, [d, d], "TIME")
 
    # Get the interpolate view of the frame
    extended = ops.run("transform.extendZeroView", frame)
    interpolant = ops.run("transform.interpolateView", extended, interpolator)
 
    # Apply the transformation to it
    rotated = RealViews.affine(interpolant, affine)
     
    # Set the intervals
    rotated = ops.transform().offset(rotated, frame)
 
    output.append(rotated)
 
output = Views.stack(output)

# Create output Dataset
output = ds.create(output)
