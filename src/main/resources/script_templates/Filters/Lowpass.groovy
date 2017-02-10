// @OpService ops
// @UIService ui
// @ImgPlus img
// @Integer radius
// @OUTPUT ImgPlus filtered

import net.imglib2.img.display.imagej.ImageJFunctions
import net.imglib2.util.Util
import net.imglib2.FinalDimensions

// perform fft of the input
inputFFT=ops.filter().fft(img)

// display fft (by default it will be a generalized log power spectrum)
// note that frequency 0,0 is at the origin and that the dimension in x dimensions is half that in y
// this stack overflow question explains the reason the first transformed dimensions is half that of the second
// http://stackoverflow.com/questions/17322449/fftw-size-of-output-array-for-2d-fourier-transform-of-3d-data
ImageJFunctions.show(inputFFT).setTitle("fft power spectrum")

// get a cursor so we can loop through the FFT
fftCursor=inputFFT.cursor()
// declare an array to hold the current position of the cursor
pos = new long[ inputFFT.numDimensions() ];

// define origin as 0,0
def origin=[0,0] as long[]

// define a 2nd 'origin' at bottom left of image.  This is a bit of a hack.  We want to draw a circle around the origin, since the origin
// is at 0,0 - the circle will 'reflect' to the bottom
def origin2=[0,inputFFT.dimension(1)] as long[]

// loop through all pixels
while (fftCursor.hasNext()) {
	
	fftCursor.fwd()
	fftCursor.localize( pos );

	// calculate distance from 0,0 and bottom left corner (so we can form the reflected semi-circle)
	dist = Util.distance( origin, pos )
	dist2 = Util.distance( origin2, pos )

	// if distance is above radius (cutoff frequency) set value of FFT to zero 
	if ( (dist>radius) & (dist2>radius)) {
		fftCursor.get().setZero()
	}

}

// show FFT after bandpass, shoudl have "semicircle" around 0,0
ImageJFunctions.show(inputFFT).setTitle("fft after bandpass")

// create Img memory for inverse FFT and compute inverse 
inverse=ops.create().img([img.dimension(0), img.dimension(1)] as long[])

// perform inverse and show
ops.filter().ifft(inverse, inputFFT)
ui.show("template inverse", inverse)
