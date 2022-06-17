#@ OpService ops
#@ ImgPlus beads
#@output ImgPlus thresholded
#@output ImgPlus points
#@output ImgPlus psf

from net.imglib2.algorithm.labeling.ConnectedComponents import StructuringElement
from net.imglib2.roi.labeling import LabelRegions
from net.imglib2.type.numeric.real import FloatType

# reference
# (note. there used to be good references on the Huygens web site, but that seems only accessible by password now)
# http://www.leica-microsystems.com/science-lab/measuring-the-3d-sted-psf-with-a-new-type-of-fluorescent-beads/

# assumptions
# 1. assuming a 3D (x,yz) dataset
# 2. assuming the image contains sub-resolution beads
#    note.  It is expected some beads may "clump" into larger objects,
#    the script may still work under these conditions, but the cleaner the
#    the bead image the better the PSF.
# 3. We use integer locations for bead centroids.  In reality beads are centered
#    at sub-resolution locations.  However as long as we have enough beads the error
#    in position should "even out".

# get dimensions of bead image
xSize = beads.dimension(0)
ySize = beads.dimension(1)
zSize = beads.dimension(2)

# create an empty image to store the points
points = ops.create().img([xSize, ySize, zSize])

# otsu threshold to find the beads
thresholded = ops.threshold().otsu(beads)

# call connected components to label each connected region
labeling = ops.labeling().cca(thresholded, StructuringElement.FOUR_CONNECTED)

# get the index image (each object will have a unique gray level)
labelingIndex = labeling.getIndexImg()

# get the collection of regions and loop through them
regions = LabelRegions(labeling)

randomAccess = points.randomAccess()
for region in regions:

	# get the center of mass of the region
	center = region.getCenterOfMass()

	# place a point at the bead centroid
	randomAccess.setPosition([long(center.getFloatPosition(0)), long(center.getFloatPosition(1)), long(center.getFloatPosition(2))])
	randomAccess.get().setReal(255.0)

# convert images to float
beads32 = ops.convert().float32(beads)
points32 = ops.convert().float32(points)

# use "PSF Distilling" (reverse deconvolution) to solve for PSF
image = beads32
kernel = points32
borderSize = None
obfInput = None
obfKernel = None
outType = FloatType()
fftType = None
maxIterations = 30
nonCirculant = False
accelerate = True # The default is False.
psf = ops.create().img(image, outType)
ops.deconvolve().richardsonLucy(psf, beads32, points32, borderSize, obfInput, obfKernel, outType, fftType, maxIterations, nonCirculant, accelerate)
