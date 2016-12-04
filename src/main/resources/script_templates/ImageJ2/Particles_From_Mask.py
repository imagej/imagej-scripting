# @ImageJ ij
# @Dataset data
# @Dataset mask

# This script identify all the particles from a mask and create label regions over which you can iterate.
# The second part of the script display all the detected regions into the IJ1 RoiManager.


from net.imagej.axis import Axes
 
from net.imglib2.algorithm.labeling.ConnectedComponents import StructuringElement
from net.imglib2.roi.labeling import LabelRegions
 
from ij.gui import PointRoi
from ij.plugin.frame import RoiManager


def get_roi_manager(new=False):
    rm = RoiManager.getInstance()
    if not rm:
        rm = RoiManager()
    if new:
        rm.runCommand("Reset")
    return rm


# Identify particles
img = mask.getImgPlus()
labeled_img = ij.op().run("cca", img, StructuringElement.EIGHT_CONNECTED)

# Create label regions from particles
regions = LabelRegions(labeled_img)
region_labels = list(regions.getExistingLabels())
 
print("%i regions/particles detected" % len(region_labels))
 
# Now use IJ1 RoiManager to display the detected regions 
rm = get_roi_manager(new=True)

for label in region_labels:
  
    region = regions.getLabelRegion(label)

  	# Get the center of mass of the region
    center = region.getCenterOfMass()
    x = center.getDoublePosition(0)
    y = center.getDoublePosition(1)
  
    roi = PointRoi(x, y)
    if center.numDimensions() >= 3:
        z = center.getDoublePosition(2)
        roi.setPosition(int(z))
      
    rm.addRoi(roi)
 
    # You can also iterate over the `data` pixel by LabelRegion
 
    cursor = region.localizingCursor()
    dataRA = data.randomAccess()
    while cursor.hasNext():
        cursor.fwd()
        dataRA.setPosition(cursor)
     
        x = cursor.getDoublePosition(0)
        y = cursor.getDoublePosition(1)
 
        # Pixel of `data`
        pixel = dataRA.get()
 
        # Do whatever you want here
        # print(x, y, pixel)