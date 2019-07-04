#@ OpService ops
#@ UIService ui
#@ ImgPlus img
#@ Integer sxy
#@ Integer sz
#@ Integer numIterations
#@OUTPUT ImgPlus deconvolved

# convert to float (TODO: make sure deconvolution op works on other types)
img_float=ops.convert().float32(img)

# create and show the gaussian kernel
if img_float.numDimensions()==3:
	psf=ops.create().kernelGauss([sxy, sxy, sz])
elif img_float.numDimensions()==2:
	psf=ops.create().kernelGauss([sxy, sxy])

ui.show(psf)

# deconvolve
out=ops.create().img(img_float);
deconvolved=ops.deconvolve().richardsonLucyTV(out,img_float, psf, numIterations, 0.01)
