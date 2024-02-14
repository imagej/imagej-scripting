#@ OpService ops
#@ UIService ui

import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory
import net.imglib2.util.Util

// create the sample image
base = ops.run("create.img", [150, 100], new FloatType())
formula = "p[0]^2 * p[1]"
ops.image().equation(base, formula)

ui.show("input image", base);

// create kernel
kernel_big = ops.run("create.img", [20,20], new  FloatType())

ops.image().equation(kernel_big, "p[0]")

// convolve with large kernel
convolved_big = ops.filter().convolve(base, kernel_big)

ui.show("convolved", convolved_big);

// deconvolve with Richardson Lucy
base_deconvolved_big=ops.create().img(convolved_big, new FloatType())
base_deconvolved_big = ops.deconvolve().richardsonLucy(base_deconvolved_big, convolved_big, kernel_big, null, new OutOfBoundsConstantValueFactory<>(Util.getTypeFromInterval(kernel_big).createVariable()), 10)

// deconvolve with non-circulant Richardson Lucy
base_deconvolved_big_noncirc=ops.create().img(convolved_big, new FloatType())
base_deconvolved_big_noncirc = ops.deconvolve().richardsonLucy(base_deconvolved_big_noncirc, convolved_big, kernel_big, null, null, null, null, null, 50, true, false)

// deconvolve with accelerated non-circulalant Richardson LUcy
base_deconvolved_big_acc_noncirc=ops.create().img(convolved_big, new FloatType())
base_deconvolved_big_acc_noncirc = ops.deconvolve().richardsonLucy(base_deconvolved_big_acc_noncirc, convolved_big, kernel_big, null, null, null, null, null, 50, true, true)

ui.show("RL",base_deconvolved_big)
ui.show("RLNon-Circ",base_deconvolved_big_noncirc)
ui.show("RL Acc/Non-Circ",base_deconvolved_big_acc_noncirc)
