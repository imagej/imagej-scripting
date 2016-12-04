# @ImageJ ij
# @Dataset data
# @Dataset mask
# @OUTPUT Dataset output

# Given a mask (binary image) and a raw image, remove background pixel from raw by
# keeping only those in the mask (different from 0).

# Note : As specified by @stelfrich on Gitter, the particular case when foreground pixel
# are 1 and background pixels are 0 can be simpler to write with a multiplication of the two
# images.

from net.imglib2.util import Intervals

# Check dimensions are the same for 'data' and 'mask'

if not Intervals.equalDimensions(data, mask):
	raise Exception("Dimensions from input dataset does not match.")

# Create the cursors
output = data.duplicate() 
targetCursor = output.localizingCursor()
dataRA = data.randomAccess()
maskRA = mask.randomAccess()

# Iterate over each pixels of the datasets
while targetCursor.hasNext():
    targetCursor.fwd()
    dataRA.setPosition(targetCursor)
    maskRA.setPosition(targetCursor)
 
    if maskRA.get().get() == 0:
        targetCursor.get().set(0)
    else:
        targetCursor.get().set(dataRA.get())