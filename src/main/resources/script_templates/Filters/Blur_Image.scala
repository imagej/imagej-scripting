#@ Dataset inputImg
#@ OpService ops
#@OUTPUT Dataset(label="Blurred") blurredImg

// A simple Scala script that blurs an input image with
// a Gaussian filter. Note that you must have an image
// open to run this script.
// It is the duty of the scripting framework to bind
// the input image and then display the blurred output
// image. It also binds the ImageJ OpService for filtering.

import net.imagej.ops.OpService
import net.imglib2.img.Img
import net.imglib2.`type`.numeric.RealType

// Scala is a compiled language with a very strict/powerful
// type system. We need to cast any parameters that are bound
// at runtime to their known type otherwise the Scala compiler
// will throw Type errors.

// Dataset extends Img[RealType[_]]. We cast directly to
// Img[RealType[_]] to narrow the underlying pixel type,
// which is required for the Gauss filter.
val img = inputImg.asInstanceOf[Img[T] forSome {type T <: RealType[T]}]

val blurredImg = ops.asInstanceOf[OpService].filter().gauss(img, 2)
