# @File(label="Directory of the images sequence", style="directory") images_sequence_dir
# @String(label="Image File Extension", required=false, value=".tif") image_extension
# @String(label="Type of Axis for the stack", required=false, value="TIME", choices={"TIME", "Z", "CHANNEL"}) axis_type
# @OUTPUT Dataset output

# @DatasetService ds

# This script takes a directory as a parameter, find all the files ending with ".tif" in the directory.
# Sort them and stack them to create a 3D dataset.

import os

from net.imagej.axis import Axes
from net.imglib2.view import Views

# Find image files
images_sequence_dir = str(images_sequence_dir)
fnames = []
for fname in os.listdir(images_sequence_dir):
    if fname.endswith(image_extension):
        fnames.append(os.path.join(images_sequence_dir, fname))
fnames = sorted(fnames)

if len(fnames) < 1:
    raise Exception("Not image files found in %s" % images_sequence_dir)

# Open and stack images
stack = []
for fname in fnames:
    data = ds.open(fname)

    # Discard image with others than 2 dimensions
    if data.numDimensions() == 2:
        stack.append(data)

output = Views.stack(stack)
output = ds.create(output)

# Set the third axis to the correct type
output.axis(2).setType(getattr(Axes, axis_type))

# Set the name of the dataset to the directory name
output.setName(os.path.basename(images_sequence_dir) + image_extension)
