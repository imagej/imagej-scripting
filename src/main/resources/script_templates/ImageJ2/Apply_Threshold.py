# @String(label="Threshold Method", required=true, choices={'otsu', 'huang'}) method_threshold
# @Float(label="Relative threshold", required=true, value=1, stepSize=0.1) relative_threshold
# @Dataset data
# @OUTPUT Dataset thresholded
# @ImageJ ij

# Apply an automatic threshold from a given method. The threshold value 'threshold_value'
# can be modulated by a relative parameter called 'relative_threshold' (if equal to 1 it does
# not modify 'threshold_value')

from net.imglib2.type.numeric.integer import UnsignedByteType

# Get the histogram
histo = ij.op().run("image.histogram", data)

# Get the threshold
threshold_value = ij.op().run("threshold.%s" % method_threshold, histo)

# Modulate 'threshold_value' by 'relative_threshold'
threshold_value = int(round(threshold_value.get() * relative_threshold))

# We should not have to do that...
threshold_value = UnsignedByteType(threshold_value)

# Apply the threshold
thresholded = ij.op().run("threshold.apply", data, threshold_value)

# Create output Dataset
thresholded = ij.dataset().create(thresholded)


