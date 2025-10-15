#@ OpService ops
#@ UIService ui
#@ Img (label = "Input image:", autofill = false) img
#@ Integer (label="Iterations", value=15) iterations
#@ Float (label="Numerical Aperture", style="format:0.00", min=0.00, value=1.45) numerical_aperture
#@ Integer (label="Emission Wavelength (nm)", value=457) wavelength
#@ Float (label="Refractive Index (immersion)", style="format:0.00", min=0.00, value=1.5) ri_immersion
#@ Float (label="Refractive Index (sample)", style="format:0.00", min=0.00, value=1.4) ri_sample
#@ Float (label="Lateral spacing (μm/pixel)", style="format:0.0000", min=0.0000, value=0.065) lateral_spacing
#@ Float (label="Axial spacing (μm/pixel)", style="format:0.0000", min=0.0000, value=0.1) axial_spacing
#@ Float (label="Particle/sample Position (μm)", style="format:0.0000", min=0.0000, value=0) p_z
#@ Float (label="Regularization factor", style="format:0.00000", min=0.00000, value=0.002) reg_factor
#@ Boolean (label = "Show PSF:", value = false) show_psf
#@output Img result

# Richardson-Lucy Total Variation deconvolution with a simulated point spread function.
#
# This script utilizes an ImageJ Ops implementation of Richardson-Lucy Total Variation (RLTV)
# deconvolution as described by Dey et al. 2006 and a simulated point spread function (PSF)
# using the Gibson-Lanni model.
#
# Arguments
#    * `img`: A 3-dimensional image with known lateral (x and y) and axial (z) spacing.
#    * `iterations`: The number of iterations to perform (default = 15).
#    * `numerical_aperture`: The numerical aperature (NA) of the objective used.
#    * `wavelength`: The emission wavelength in nanometers (nm) of the image.
#    * `ri_immersion`: The refractive index of immersion medium (air, oil, etc...).
#    * `ri_sample`: The refractive index of the sample, a measured value (default = 1.4)
#    * `lateral_spacing`: The X and Y pixel spacing in μm/pixel of the image.
#    * `axial_spacing`: The Z spacing in μm/pixel of the image.
#    * `p_z`: The position of the sample from the coverslip.
#    * `reg_factor`: The regularization factor.
#    * `show_psf`: Optionally display the simulated PSF.
#
# Returns
#    * `result`: The deconvolved data.
#    * `PSF`: The simulated PSF (optional).
#
# Reference
#
# https://doi.org/10.1002/jemt.20294

from net.imglib2 import FinalDimensions
from net.imglib2.type.numeric.real import FloatType

# convert input image to float
img = ops.convert().float32(img)

# use image dimensions for PSF size
psf_size = FinalDimensions(img.dimensionsAsLongArray())

# convert the input parameters to meters (m)
wavelength = float(wavelength) * 1E-9
lateral_spacing *= 1E-6
axial_spacing *= 1E-6
p_z *= 1E-6

# create the synthetic PSF
psf = ops.create().kernelDiffraction(
    psf_size,
    numerical_aperture,
    wavelength,
    ri_sample,
    ri_immersion,
    lateral_spacing,
    axial_spacing,
    p_z,
    FloatType()
    )

# deconvolve image
result = ops.deconvolve().richardsonLucyTV(img, psf, iterations, reg_factor)

# optionally show the PSF
if show_psf:
    ui.show("PSF", psf)
