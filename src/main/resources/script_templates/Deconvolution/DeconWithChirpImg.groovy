#@ OpService ops
#@ UIService ui
#@ ConvertService convert
#@ DatasetService data

// Author Daniel James White/Brian Northan
// Prerequisite install http://www.optinav.info/Diffraction-PSF-3D.htm

import ij.IJ
import net.imglib2.FinalDimensions
import net.imglib2.type.numeric.real.FloatType
import net.imagej.ImgPlus
import net.imglib2.img.Img
import net.imglib2.img.display.imagej.ImageJFunctions

def plotProfile() {
	IJ.resetMinAndMax()
	IJ.makeLine(0, 32, 511, 32)
	IJ.run("Plot Profile")
}

blank=ops.create().img(new FinalDimensions(512,512),new FloatType())
noisy=ops.create().img(new FinalDimensions(512,512),new FloatType())

formula = "50 * (Math.sin(2*Math.PI*0.1*Math.pow(3,p[0]/149.8)*p[0]/149.8 )+1)+1"
exponentialChirp = ops.image().equation(blank, formula)
ui.show(exponentialChirp)

plotProfile()

psfSize=new FinalDimensions(new long[]{512,512});

psf = ops.create().kernelDiffraction(psfSize, 1.42, 510e-9,
				1.5, 1.5, 32.5e-9, 150e-9, 0, new FloatType());

sumpsf=ops.stats().sum(psf);

// normalize psf
sumpsf=new FloatType(sumpsf.getRealFloat());
psf=ops.math().divide(psf, sumpsf);

// convolve chirp and psf
convolved=ops.filter().convolve(exponentialChirp, psf)
ui.show("convolved", convolved)

plotProfile()

// add noise
noisy=ops.filter().addPoissonNoise(noisy,convolved)
ui.show("noisy", noisy)

plotProfile()

// deconvolve using classical Richardson Lucy
deconvolved=ops.deconvolve().richardsonLucy(noisy, psf,10)
ui.show("deconvolved", deconvolved)
plotProfile()

// deconvolve using accelerated Richardson Lucy
deconvolved_accelerated=ops.create().img(noisy, new FloatType())
deconvolved_accelerated=ops.deconvolve().richardsonLucy(deconvolved_accelerated,noisy, psf, null, null, null, null, null, 10, false, true)
ui.show("deconvolved accelerated", deconvolved_accelerated)
plotProfile()

// deconvolve using Richardson Lucy with total variation
deconvolved_rltv=ops.create().img(noisy, new FloatType())
deconvolved_rltv=ops.deconvolve().richardsonLucyTV(deconvolved_rltv,noisy, psf, 10, 0.01)
ui.show("deconvolved rltv", deconvolved_rltv)
plotProfile()

// deconvolve using accelerated Richardson Lucy with total variation
deconvolved_rltv_accelerated=ops.create().img(noisy, new FloatType())
deconvolved_rltv_accelerated=ops.deconvolve().richardsonLucyTV(deconvolved_rltv_accelerated,noisy, psf, null, null, null, null, null, 10, false, true, 0.01)
ui.show("deconvolved rltv accelerated", deconvolved_rltv_accelerated)
plotProfile()
