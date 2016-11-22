# @OpService ops
# @UIService ui
# @ImgPlus img
# @Integer sxy
# @Integer sz
# @Integer numIterations
# @OUTPUT ImgPlus deconvolved

# convert to float (TODO: make sure deconvolution op works on other types)
img_float=ops.convert().float32(img)

# create and show the gaussian kernel
psf=ops.create().kernelGauss([sxy, sxy, sz])
ui.show(psf)

# deconvolve
deconvolved=ops.deconvolve().richardsonLucyTV(img_float, psf, numIterations, 0.01);
