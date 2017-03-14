# @ImageJ ij
# @Dataset ds
# @OpService ops
# @DatasetService datasetService
# @OUTPUT Dataset final_dataset

import math

from net.imglib2.util import Intervals
from net.imagej.axis import Axes
from net.imglib2.view import Views

from ij.plugin.frame import RoiManager

# Initialize some variables
img = ds.getImgPlus()
rois = RoiManager.getInstance().getRoisAsArray()

images = []

total_x_shift = 0
total_y_shift = 0

# Iterate over regions of interest
for j, r in enumerate(range(len(rois)-1)):

	# Get two ROIs
	start = rois[r]
	end = rois[r+1]

	# Get Z or T positions defined by the position of the ROIs
	z_start = start.getPosition()
	z_end = end.getPosition()

	# Get X positions
	x_start = start.getContainedPoints()[0].x
	x_end = end.getContainedPoints()[0].x

	# Get Y positions
	y_start = start.getContainedPoints()[0].y
	y_end = end.getContainedPoints()[0].y

	# Calculate the linear translation for each frame in the Z/T axis
	x_shift = float(x_end - x_start) / (z_end - z_start)
	y_shift = float(y_end - y_start) / (z_end - z_start)

	# Iterate over each frame in Z/T
	for i, z in enumerate(range(z_start, z_end)):

		ij.log().info("Processing frame %i/%i for roi #%i" % (i + 1, z_end - z_start, j))

		# Compute the translation
		dx = int(x_shift + total_x_shift) * -1
		dy = int(y_shift + total_y_shift) * -1

		total_x_shift += x_shift
		total_y_shift += y_shift

		# Get only the frame cooresponding to the actual Z/T position
		intervals = Intervals.createMinMax(0, 0, z, ds.getWidth() - 1, ds.getHeight() - 1, z)
		single_frame = ops.transform().crop(img, intervals)

		# Pad the frame so the outisde of the image dimensions values are set to 0
		padded_frame = ops.run("transform.extendZeroView", single_frame)

		# Do the translation
		translated_frame = ops.transform().translate(padded_frame, [dx, dy, 0])

		# Cleanup
		translated_frame = Views.interval(translated_frame, intervals)
		translated_frame = ops.run("transform.dropSingletonDimensionsView", translated_frame)
	
		images.append(translated_frame)

images = ops.run("transform.stackView", [images])
final_dataset = datasetService.create(images)