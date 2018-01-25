// @OpService ops
// @UIService ui
// @ConvertService convert
// @DatasetService data

import net.imglib2.type.numeric.real.FloatType;
import net.imagej.ops.Op
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory
import net.imagej.ops.deconvolve.RichardsonLucyF
import net.imglib2.util.Util

// create the sample image
base = ops.run("create.img", [150, 100], new FloatType())
formula = "p[0]^2 * p[1]"
ops.image().equation(base, formula)

ui.show(base);

// create kernel
kernel_small = ops.run("create.img", [3,3], new FloatType())
kernel_big = ops.run("create.img", [20,20], new  FloatType())

ops.image().equation(kernel_small, "p[0]")
ops.image().equation(kernel_big, "p[0]")

// convolve with large and small kernel
convolved_small = ops.filter().convolve(base, kernel_small)
convolved_big = ops.filter().convolve(base, kernel_big)

ui.show(convolved_small);
ui.show(convolved_big);

base_deconvolved = ops.run(RichardsonLucyF.class, convolved_small, kernel_small, null, new OutOfBoundsConstantValueFactory<>(Util.getTypeFromInterval(kernel_small).createVariable()), 10)

// 50 iterations richardson lucy
base_deconvolved_big = ops.run(RichardsonLucyF.class, convolved_big, kernel_big, 50);

// 50 iterations non-circulant richardson lucy
base_deconvolved_big_noncirc = ops.run(RichardsonLucyF.class, convolved_big, kernel_big, null, null,null, null, null,50,true,false );

// 50 iterations non-circulant accelerated richardson lucy
base_deconvolved_big_acc_noncirc = ops.run(RichardsonLucyF.class, convolved_big, kernel_big, null, null,null, null, null, 50, true, true)

ui.show("RL",base_deconvolved_big)
ui.show("RLNon-Circ",base_deconvolved_big_noncirc)
ui.show("RL Acc/Non-Circ",base_deconvolved_big_acc_noncirc)