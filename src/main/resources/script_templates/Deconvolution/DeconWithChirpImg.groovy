// @OpService ops
// @UIService ui
// @ConvertService convert
// @DatasetService data

// Author Chalkie/Brian Northan
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

// generate diffraction based psf
IJ.run("Diffraction PSF 3D", "index=1.520 numerical=1.42 wavelength=510 "
+ "longitudinal=0 image=10 slice=200 width,=512 height,=512 depth,=1 "
+ "normalization=[Sum of pixel values = 1] title=PSF")

// get the psf
psf=IJ.getImage()

// convert to imglib img
psf=ImageJFunctions.wrapFloat(psf)

// convolve chirp and psf
convolved=ops.filter().convolve(exponentialChirp, psf)
ui.show("convolved", convolved)

plotProfile()

// add noise
noisy=ops.filter().addPoissonNoise(noisy,convolved)
ui.show("noisy", noisy)

plotProfile()

// deconvolve
deconvolved=ops.deconvolve().richardsonLucy(noisy, psf,10)
ui.show("deconvolved", deconvolved)
plotProfile()

deconvolved_accelerated=ops.deconvolve().richardsonLucy(noisy, psf, null, null, null, null, null, 10, false, true)
ui.show("deconvolved accelerated", deconvolved_accelerated)
plotProfile()

deconvolved_rltv=ops.deconvolve().richardsonLucyTV(noisy, psf, 10, 0.01)
ui.show("deconvolved rltv", deconvolved_rltv)
plotProfile()

deconvolved_rltv_accelerated=ops.deconvolve().richardsonLucyTV(noisy, psf, null, null, null, null, null, 10, true, true, 0.01)
ui.show("deconvolved rltv accelerated", deconvolved_rltv_accelerated)
plotProfile()
