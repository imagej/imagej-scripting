#@ OpService ops
#@ Dataset data
#@ Dataset psf
#@ Integer(value=128) psfXSize
#@ Integer(value=128) psfYSize
#@ Float(value=0.01) psfBackgroundPercent
#@ Boolean(value=True) nonCirculant
#@ Boolean(value=True) acceleration
#@OUTPUT ImgPlus psf_
#@OUTPUT ImgPlus deconvolved_

# deconvolve an image, the key thing in this script is that the PSF is preprocessed so
# we get faster and better results.
# 1.  Crop PSF (smaller PSF -> faster results (assuming we extend image to avoid wrap around)
# 2.  Normalize PSF (required to keep sum of image signal constant)
# 3.  Subtract background from PSF (small background values can reduce deconvolution quality)

from net.imglib2.util import Intervals
from net.imagej.axis import Axes
from net.imglib2.type.numeric.real import FloatType

# crop PSF to desired size, this makes decon run faster with little effect on final quality
psfX = psf.dimension(data.dimensionIndex(Axes.X))
psfY = psf.dimension(data.dimensionIndex(Axes.Y))
psfZ = psf.dimension(2)

psf_=ops.transform().crop(psf.getImgPlus(), Intervals.createMinMax(psfX/2-psfXSize/2, psfY/2-psfYSize/2, 0,
	psfX/2+psfXSize/2-1, psfY/2+psfYSize/2-1, psfZ-1))

psf_=ops.convert().float32(psf_);

maxPSF=ops.stats().max(psf_).getRealFloat();
psfBackground=psfBackgroundPercent*maxPSF;

# subtract background from psf
for t in psf_:
	val=t.getRealFloat()-psfBackground;
	if val<0:
		val=0;
	t.setReal(val);

# normalize psf
sumpsf=ops.stats().sum(psf_);
sumpsf=FloatType(sumpsf.getRealFloat());

print sumpsf

psf_=ops.math().divide(psf_, sumpsf);

# convert image to 32 bit
img_=ops.convert().float32(data.getImgPlus());

# now deconvolve
deconvolved_=ops.deconvolve().richardsonLucy(img_, psf_, None, None, None, None, None, 30, nonCirculant, acceleration)
