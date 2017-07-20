# @OpService ops
# @ImgPlus beads
# @OUTPUT ImgPlus thresholded
# @OUTPUT ImgPlus points
# @OUTPUT ImgPlus psf

from net.imglib2.algorithm.labeling.ConnectedComponents import StructuringElement
from net.imglib2.roi import Regions;
from net.imglib2.roi.labeling import LabelRegions;

from java.util import ArrayList;

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
xSize=beads.dimension(0);
ySize=beads.dimension(1);
zSize=beads.dimension(2);

# create an empty image to store the points
points=ops.create().img([xSize, ySize, zSize])

# otsu threshold to find the beads 
thresholded = ops.threshold().otsu(beads)

# call connected components to label each connected region
labeling=ops.labeling().cca(thresholded, StructuringElement.FOUR_CONNECTED)

# get the index image (each object will have a unique gray level)
labelingIndex=labeling.getIndexImg()

# get the collection of regions and loop through them
regions=LabelRegions(labeling)

for region in regions:
	
	# get the center of mass of the region 
	center=region.getCenterOfMass();

	# place a point at the bead centroid
	randomAccess= points.randomAccess()
	randomAccess.setPosition([long(center.getFloatPosition(0)), long(center.getFloatPosition(1)), long(center.getFloatPosition(2))])
	randomAccess.get().setReal(255.0)

# convert images to float
beads32=ops.convert().float32(beads);
points32=ops.convert().float32(points);

# use "PSF Distilling" (reverse deconvolution) to solve for PSF
psf=ops.deconvolve().richardsonLucy(beads32, points32, None, None, None, None, None, 30, False, True)
