// @OpService ops
// @ImgPlus inputData
// @OUTPUT ImgPlus(label="Filtered") filtered
// @OUTPUT ImgPlus(label="Convolved") result

// This script takes an input image, applies a gaussian filter
// to it, and then performs convolution with a custom kernel.

// Convert the input image
img32 = ops.convert().int32(inputData)

// Apply the gaussian filter
filtered=ops.filter().gauss(img32, [4.0,4.0])

// Create the kernel
kernel=ops.create().kernel([[0,1,0],[1,-4,1],[0,1,0]])

// Perform convolution
result=ops.filter().convolve(filtered, kernel)
