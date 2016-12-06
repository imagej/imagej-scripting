# @String(label="Threshold Method", required=true, choices={'otsu', 'huang'}) method_threshold
# @Dataset data
# @OUTPUT Dataset output
# @ImageJ ij

# Apply an automatic threshold from a given method.
thresholded = ij.op().run("threshold.%s" % method_threshold, data)

# Create output
output = ij.dataset().create(thresholded)