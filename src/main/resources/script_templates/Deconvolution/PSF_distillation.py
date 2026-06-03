#@ OpEnvironment ops
#@ ImgPlus (label = "Input image:", autofill = false) beads
#@ String (visibility = MESSAGE, value ="<b>[ Point Spread Function (PSF) distilling settings ]</b>", required = false) psf_msg
#@ String (label="Global threshold method:", choices={"huang", "ij1", "intermodes", "isoData", "li", "maxEntropy", "maxLikelihood", "mean", "minError", "minimum", "moments", "otsu", "percentile", "renyiEntropy", "rosin", "shanbhag", "triangle", "yen"}, style="listBox") method
#@ Integer (label = "Number of iterations", min = 0, value = 30) iterations
#@output ImgPlus threshold_image
#@output ImgPlus points
#@output ImgPlus psf

from net.imglib2.algorithm.labeling.ConnectedComponents import StructuringElement
from net.imglib2.roi.labeling import LabelRegions
from net.imglib2.type.logic import BitType
from net.imglib2.type.numeric.real import FloatType
from net.imglib2.type.numeric.complex import ComplexFloatType
from net.imglib2.type.numeric.integer import UnsignedByteType

# Point Spread Function (PSF) distillation from sub-resolution bead images.
#
# This script utilizes a SciJava Ops implementation of Richardson-Lucy
# deconvolution to distill an PSF from an image of sub-resolution fluorescent
# beads. It automates bead segmentation via global thresholding and connected
# components, maps their integer centroids, and extracts an averaged, low-noise
# PSF by  reversing the deconvolution process (deconvolving the bead image with
# the centroid map).
#
# Arguments
#    * `beads`: A 3D (x,y,z) image dataset containing sub-resolution fluorescent
#      beads.
#    * `method`: The global thresholding method used to segment individual beads
#      from the background.
#    * `iterations`: The number of Richardson-Lucy iterations to perform for the
#      distillation (default = 30).
#
# Returns
#    * `threshold_image`: The binary mask image resulting from the chosen
#      threshold method.
#    * `points`: An image containing single-voxel points (intensity 255) at the
#      center of mass for each bead.
#    * `psf`: The distilled, averaged PSF.

# create an empty unsigned 8-bit image to store the points
points = ops.op("create.img").input(beads, UnsignedByteType()).apply()

# otsu threshold to find the beads
thresholded = ops.op("create.img").input(beads, BitType()).apply()
ops.op("threshold.{}".format(method)).input(beads).output(thresholded).compute()

# call connected components to label each connected region
labeling = ops.op("labeling.cca").input(thresholded, StructuringElement.FOUR_CONNECTED).apply()

# get the index image (each object will have a unique gray level)
label_img = labeling.getIndexImg()

# get the collection of regions and loop through them
regions = LabelRegions(labeling)
random_access = points.randomAccess()
for region in regions:

	# get the center of mass of the region
	center = region.getCenterOfMass()

	# place a point at the bead centroid
	random_access.setPosition([long(center.getFloatPosition(0)), long(center.getFloatPosition(1)), long(center.getFloatPosition(2))])
	random_access.get().setReal(255.0)

# convert images to float
beads_f32 = ops.op("convert.float32").input(beads).apply()
points_f32 = ops.op("convert.float32").input(points).apply()

# use "PSF Distilling" (reverse deconvolution) to solve for PSF
image = beads_f32
kernel = points_f32

# psf = ops.op("create.img").input(image, FloatType()).apply()
psf = ops.op("deconvolve.richardsonLucy").input(beads_f32,
                                          points_f32,
                                          FloatType(),
                                          ComplexFloatType(),
                                          iterations,
                                          False,
                                          True).apply()
