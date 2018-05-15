#@ Dataset ds
#@OUTPUT Dataset output
#@ OpService ops
#@ LogService log
#@ DatasetService datasetService

# This script translates individual slices in a stack according to single
# point ROIs (defined in the IJ1 ROIManager). If slices exist in between specified ROIs,
# a linear translation from one ROI to the next is applied.
# 1. Add point ROIs to the RoiManager; either one per slice or only in slices of
# interest. (Be carefull to set the correct Z/T position when adding the ROI.)
# 2. Run the script.

from net.imglib2.util import Intervals
from net.imglib2.view import Views

from ij.plugin.frame import RoiManager

# Initialize some variables
img = ds.getImgPlus()
rois = RoiManager.getInstance().getRoisAsArray()

images = []

total_x_shift = 0
total_y_shift = 0

# Iterate over regions of interest
for j, (start_roi, end_roi) in enumerate(zip(rois[:-1], rois[1:])):

	# Get Z or T positions defined by the position of the ROIs
	z_start = start_roi.getPosition()
	z_end = end_roi.getPosition()

	# Get X positions
	x_start = start_roi.getContainedPoints()[0].x
	x_end = end_roi.getContainedPoints()[0].x

	# Get Y positions
	y_start = start_roi.getContainedPoints()[0].y
	y_end = end_roi.getContainedPoints()[0].y

	# Calculate the linear translation for each frame in the Z/T axis
	x_shift = float(x_end - x_start) / (z_end - z_start)
	y_shift = float(y_end - y_start) / (z_end - z_start)

	# Iterate over each frame in Z/T
	for i, z in enumerate(range(z_start, z_end)):

		log.info("Processing frame %i/%i for ROIs #%i and #%i (%i total detected ROIs)" % (z_start + i, z_end, j, j + 1, len(rois)))

		# Compute the translation
		dx = int(x_shift + total_x_shift) * -1
		dy = int(y_shift + total_y_shift) * -1

		total_x_shift += x_shift
		total_y_shift += y_shift

		# Get only the frame cooresponding to the actual Z/T position
		intervals = Intervals.createMinMax(0, 0, z, ds.getWidth() - 1, ds.getHeight() - 1, z)
		single_frame = ops.transform().crop(img, intervals)

		# Pad the frame so the outisde of the image dimensions values are set to 0
		padded_frame = ops.transform().extendZero(single_frame)

		# Do the translation
		translated_frame = ops.transform().translate(padded_frame, [dx, dy])

		# Cleanup
		interval2d = Intervals.createMinMax(0, 0, ds.getWidth() - 1, ds.getHeight() - 1)
		translated_frame = Views.interval(translated_frame, interval2d)
		translated_frame = ops.transform().dropSingletonDimensions(translated_frame)

		images.append(translated_frame)

images = ops.run("transform.stackView", [images])
output = datasetService.create(images)
