#@ OpService ops
#@ UIService ui
#@ String (visibility = MESSAGE, value ="<b>[ Deconvolution settings ]</b>", required = false) decon_msg
#@ Integer (label = "Grid image X dimension size:", min = 0, value = 150) x_dim
#@ Integer (label = "Grid image Y dimension size:", min = 0, value = 100) y_dim
#@ Integer (label = "Number of iterations", min = 0, value = 15) iterations
#@ String (visibility = MESSAGE, value ="<b>[ Show results settings ]</b>", required = false) show_msg
#@ Boolean (label = "Show input image:", value = true) show_grid
#@ Boolean (label = "Show gradient kernel", value = true) show_kernel
#@ Boolean (label = "Show convolved grid:", value = true) show_conv
#@ Boolean (label = "Show RL result:", value = true) show_rl
#@ Boolean (label = "Show RLTV result:", value = true) show_rltv
#@ Boolean (label = "Show RL non-circulant reuslt:", value = true) show_rl_nc
#@ Boolean (label = "Show RL accelerated non-circulant result:", value = true) show_rl_acc_nc

import net.imglib2.type.numeric.real.FloatType

// Richardson-Lucy deconvolution with a simulated grid image.
//
// This script utilizes the ImageJ Ops implementation of Richardson-Lucy (RL) and
// Richardson-Lucy Total Variation (RLTV) deconvolution. Specifically this script
// template creates a simulated grid image with sharp edges, convolves the grid
// image with a simple gradient kernel and performs the various RL deconvolution
// strategies such as accelerated processing and non-circulant deconvolution.
//
// See https://imagej.net/libs/imagej-ops/deconvolution for more information on
// RL/RLTV deconvolution with ImageJ Ops.
//
// Arguments
//    * `x_dim`: The grid image X dimension size.
//    * `y_dim`: The grid image Y dimension size.
//    * `iterations`: The number of deconvolution iterations to perform (default = 10).
//
// Returns
//   * `grid_img`: The simulated grid image.
//   * `kernel`: The gradient kernel used to convolve the grid image.
//   * `conv_img`: The convolved grid image.
//   * `rl_decon_img`: The Richardson-Lucy deconvolution result.
//   * `rltv_decon_img`: The Richardson-Lucy Total Variation deconvolution result.
//   * `rl_nc_decon_img`: The Richardson-Lucy non-circulant deconvolution result.
//   * `rl_acc_nc_decon_img`: The Richardson-Lucy accelerated non-circulant deconvolution
//      result.

// create a sample grid image with the given X and Y dimensions
// where each pixel is assigned the value (x² * y)
grid_img = ops.run("create.img", [x_dim, y_dim], new FloatType())
formula = "p[0]^2 * p[1]"
ops.image().equation(grid_img, formula)
if (show_grid) {
  ui.show("input grid image", grid_img)
}

// create a gradient kernel and convolve the grid image
kernel = ops.run("create.img", [20,20], new  FloatType())
ops.image().equation(kernel, "p[0]")
if (show_kernel) {
  ui.show("gradient kernel", kernel)
}

// convolve the grid image with the gradient kernel
conv_img= ops.filter().convolve(grid_img, kernel)
if (show_conv) {
  ui.show("convolved grid image", conv_img)
}

// deconvolve with Richardson-Lucy (RL)
if (show_rl) {
  rl_decon_img = ops.deconvolve().richardsonLucy(conv_img, kernel, iterations)
  ui.show("Richardson-Lucy", rl_decon_img)
}

// deconvolve with Richardson-Lucy Total Variation (RLTV)
if (show_rltv) {
  rltv_decon_img = ops.deconvolve().richardsonLucyTV(conv_img, kernel, iterations, 0.002)
  ui.show("Richardson-Lucy TV", rltv_decon_img)
}

// deconvolve with non-circulant Richardson-Lucy
if (show_rl_nc) {
  // create the output image (empty) for the deconvlution Op
  rl_nc_decon_img = ops.create().img(conv_img, new FloatType())
  ops.deconvolve().richardsonLucy(rl_nc_decon_img,
                                  conv_img,
                                  kernel,
                                  null,
                                  null,
                                  null,
                                  null,
                                  null,
                                  iterations,
                                  true,
                                  false)
  ui.show("Ricahrdson-Lucy non-circulant", rl_nc_decon_img)
}

// deconvolve with accelerated non-circulalant Richardson Lucyi
if (show_rl_acc_nc) {
  rl_acc_nc_decon_img = ops.create().img(conv_img, new FloatType())
  ops.deconvolve().richardsonLucy(rl_acc_nc_decon_img,
                                  conv_img,
                                  kernel,
                                  null,
                                  null,
                                  null,
                                  null,
                                  null,
                                  iterations,
                                  true,
                                  true)
  ui.show("Richardson-Lucy accelerated non-circulant", rl_acc_nc_decon_img)
}
