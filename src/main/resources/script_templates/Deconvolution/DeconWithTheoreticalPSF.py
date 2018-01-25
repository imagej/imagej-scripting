# @OpService ops
# @UIService ui
# @ImgPlus img
# @Integer numIterations(value=30)
# @Float numericalAperture(value=1.4)
# @Float wavelength(value=550)
# @Float riImmersion(value=1.5)
# @Float riSample(value=1.4)
# @Float xySpacing(value=62.9)
# @Float zSpacing(value=160)
# @OUTPUT ImgPlus psf
# @OUTPUT ImgPlus deconvolved

from net.imglib2 import FinalDimensions
from net.imglib2.type.numeric.real import FloatType;

# convert to float (TODO: make sure deconvolution op works on other types)
imgF=ops.convert().float32(img)

# make psf same size as image
psfSize=FinalDimensions([img.dimension(0), img.dimension(1), img.dimension(2)]);

# add border in z direction
borderSize=[0,0,psfSize.dimension(2)/2];

wavelength=wavelength*1E-9
xySpacing=xySpacing*1E-9
zSpacing=zSpacing*1E-9

# currently there is a bug when generating a PSF with wavelength < 545 nm
# (this has been fixed, but the fix has not yet been distributed)
if wavelength<545E-09:
	wavelength = 545E-09;

riImmersion = 1.5;
riSample = 1.4;
xySpacing = 62.9E-9;
zSpacing = 160E-9;
depth = 0;

psf = ops.create().kernelDiffraction(psfSize, numericalAperture, wavelength,
				riSample, riImmersion, xySpacing, zSpacing, depth, FloatType());

deconvolved = ops.deconvolve().richardsonLucy(imgF, psf, borderSize, None,
					None, None, None, numIterations, True, True);
