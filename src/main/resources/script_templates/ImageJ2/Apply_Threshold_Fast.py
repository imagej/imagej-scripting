# @String(label="Threshold Method", required=true, choices={'otsu', 'huang'}) method_threshold
# @Dataset data
# @OUTPUT Dataset output
# @OpService ops
# @DatasetService ds

# Apply an automatic threshold from a given method.
thresholded = ops.run("threshold.%s" % method_threshold, data)

# Create output
output = ds.create(thresholded)
