#@ OpEnvironment ops
#@ UIService ui
#@ String (visibility = MESSAGE, value ="<b>[ Deconvolution settings ]</b>", required = false) decon_msg
#@ Integer (label = "Grid image X dimension size:", min = 0, value = 150) x_ax
#@ Integer (label = "Grid image Y dimension size:", min = 0, value = 100) y_ax
#@ Integer (label = "Number of iterations", min = 0, value = 15) iterations
#@ String (visibility = MESSAGE, value ="<b>[ Show results settings ]</b>", required = false) show_msg
#@ Boolean (label = "Show input image:", value = true) show_grid
#@ Boolean (label = "Show gradient kernel", value = true) show_kernel
#@ Boolean (label = "Show convolved grid:", value = true) show_conv
#@ Boolean (label = "Show RL result:", value = true) show_rl
#@ Boolean (label = "Show RLTV result:", value = true) show_rltv
#@ Boolean (label = "Show RL non-circulant reuslt:", value = true) show_rl_nc
#@ Boolean (label = "Show RL accelerated non-circulant result:", value = true) show_rl_acc_nc

// Richardson-Lucy deconvolution with a simulated grid image.
//
// This script utilizes the ImageJ Ops implementation of Richardson-Lucy (RL) and
// Richardson-Lucy Total Variation (RLTV) deconvolution. Specifically this script
// template creates a simulated grid image with sharp edges, convolves the grid
// image with a simple gradient kernel and performs the various RL deconvolution
// strategies such as accelerated processing and non-circulant deconvolution.
//
// See https://imagej.net/imaging/deconvolution for more information on RL/RLTV
// deconvolution.
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
//     result.

import net.imglib2.img.array.ArrayImgs
import net.imglib2.FinalDimensions
import net.imglib2.type.numeric.real.FloatType
import net.imglib2.type.numeric.complex.ComplexFloatType

// create a sample grid image with the given X and Y dimensions
// where each pixel is assigned the value (x XOR (2 * y))
// grid_shape = new FinalDimensions([x_ax, y_ax] as long[])
// grid_img = ops.op("create.img").input(grid_shape, new FloatType()).apply()
grid_img = ops.op("create.img").input([x_ax, y_ax] as long[]).apply()
grid_cursor = grid_img.localizingCursor()
pos = new long[2]
while (grid_cursor.hasNext()) {
  grid_cursor.fwd()
  grid_cursor.localize(pos)
  x = pos[0]
  y = pos[1]
  grid_cursor.get().setReal(x ^ (2 * y))
}
if (show_grid) {
  ui.show("input grid image", grid_img)
}

// create a gradient kernel and convolve the grid image
kernel_shape = new FinalDimensions([20, 20] as long[])
kernel_img = ops.op("create.img").input(kernel_shape, new FloatType()).apply()
kernel_cursor = kernel_img.localizingCursor()
while (kernel_cursor.hasNext()) {
  kernel_cursor.fwd()
  kernel_cursor.localize(pos)
  x = pos[0]
  kernel_cursor.get().setReal(x)
}
if (show_kernel) {
  ui.show("gradient kernel", kernel_img)
}

// convolve the grid image with the gradient kernel
conv_img = ops.op("filter.convolve").input(grid_img, kernel_img).apply()
if (show_conv) {
  ui.show("convolved grid image", conv_img)
}

// deconvolve with Richardson-Lucy (RL)
if (show_rl) {
  rl_decon_img = ops.op("deconvolve.richardsonLucy").input(conv_img,
                                                           kernel_img,
                                                           new FloatType(),
                                                           new ComplexFloatType(),
                                                           iterations).apply()
  ui.show("Richardson-Lucy", rl_decon_img)
}

// deconvolve with Richardson-Lucy Total Variation (RLTV)
if (show_rltv) {
  rltv_decon_img = ops.op("deconvolve.richardsonLucyTV").input(conv_img,
                                                               kernel_img,
                                                               new FloatType(),
                                                               new ComplexFloatType(),
                                                               iterations,
                                                               false,
                                                               false,
                                                               0.002f).apply()
  ui.show("Richardson-Lucy TV", rltv_decon_img)
}

// deconvolve with non-circulant Richardson-Lucy
if (show_rl_nc) {
  rl_nc_decon_img = ops.op("deconvolve.richardsonLucy").input(conv_img,
                                             kernel_img,
                                             new FloatType(),
                                             new ComplexFloatType(),
                                             iterations,
                                             true,
                                             false).apply()
  ui.show("Ricahrdson-Lucy non-circulant", rl_nc_decon_img)
}

// deconvolve with accelerated non-circulalant Richardson Lucy
if (show_rl_acc_nc) {
  rl_acc_nc_decon_img = ops.op("deconvolve.richardsonLucy").input(conv_img,
                                             kernel_img,
                                             new FloatType(),
                                             new ComplexFloatType(),
                                             iterations,
                                             true,
                                             true).apply()
  ui.show("Richardson-Lucy accelerated non-circulant", rl_acc_nc_decon_img)
}
