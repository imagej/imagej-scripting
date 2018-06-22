#@ String(value='Please set some parameters.', visibility='MESSAGE') message
#@ Short(label='Image size', value=512, min=128, max=2048, stepSize=128, style="slider") img_size
#@ Double(label='Image amplitude', value=100) amplitude
#@ Short(label='Spacing', value=16, min=8) spacing

'''Using Scripting Parameters

This code is part of the Jython tutorial at the ImageJ wiki.
http://imagej.net/Jython_Scripting#Using_Scripting_Parameters
'''

# The parameters in front of this comment are populated before the script runs.
# Details on Script parameters can be found at
# http://imagej.net/Script_parameters

# The module __future__ contains some useful functions:
# https://docs.python.org/2/library/__future__.html
from __future__ import with_statement, division

# It's best practice to create a function that contains the code that is executed when running the script.
# This enables us to stop the script by just calling return.
def run_script():
    # We can use import inside of code blocks to limit the scope.
    import math
    from ij import IJ, ImagePlus
    from ij.process import FloatProcessor
    blank = IJ.createImage("Blank", "32-bit black", img_size, img_size, 1)
    # This create a list of lists. Each inner list represents a line.
    # pixel_matrix[0] is the first line where y=0.
    pixel_matrix = split_list(blank.getProcessor().getPixels(), wanted_parts=img_size)
    # This swaps x and y coordinates.
    # http://stackoverflow.com/questions/8421337/rotating-a-two-dimensional-array-in-python
    # As zip() creates tuples, we have to convert each one by using list().
    pixel_matrix = [list(x) for x in zip(*pixel_matrix)]
    for y in range(img_size):
        for x in range(img_size):
            # This function oszillates between 0 and 1.
            # The distance of 2 maxima in a row/column is given by spacing.
            val = (0.5 * (math.cos(2*math.pi/spacing*x) + math.sin(2*math.pi/spacing*y)))**2
            # When assigning, we multiply the value by the amplitude.
            pixel_matrix[x][y] = amplitude * val
    # The constructor of FloatProcessor works fine with a 2D Python list.
    crystal = ImagePlus("Crystal", FloatProcessor(pixel_matrix))
    # Crop without selection is used to duplicate an image.
    crystal_with_noise = crystal.crop()
    crystal_with_noise.setTitle("Crystal with noise")
    IJ.run(crystal_with_noise, "Add Specified Noise...", "standard=%d" % int(amplitude/math.sqrt(2)))
    # As this is a demo, we don't want to be ask to save an image on closing it.
    # In Python True and False start with capital letters.
    crystal_with_noise.changes = False
    crystal.show()
    crystal_with_noise.show()
    filtered = fft_filter(crystal_with_noise)
    # We create a lambda function to be used as a parameter of img_calc().
    subtract = lambda values: values[0] - values[1]
    # This is a short form for:
    # def subtract(values):
    #   return values[0] - values[1]

    # The first time we call img_calc with 2 images.
    difference = img_calc(subtract, crystal, filtered, title="Difference of 2")
    difference.show()
    # The first time we call img_calc with 3 images.
    minimum = img_calc(min, crystal, filtered, crystal_with_noise, title="Minimum of 3")
    minimum.show()
    for imp in (crystal, crystal_with_noise, filtered, difference, minimum):
        IJ.run(imp, "Measure", "")

# Functions can be defined after they are used.
# This is only possible if the main code is encapsulated into a function.
# The main function has to be called at the end of the script.

def img_calc(func, *imps, **kwargs):
    """Runs the given function on each pixel of the given list of images.
    An additional parameter, the title of the result, is passed as keyword parameter.
    We assume that each image has the same size. This is not checked by this function.
    """
    # If the keyword parameter is not passed, it is set to a default value.
    if not kwargs['title']:
        kwargs['title'] = "Result"
    # This is a 2D list: list[number of images][pixels per image] .
    pixels = [imp.getProcessor().getPixels() for imp in imps]
    # The function is called pixel by pixel.
    # zip(*pixels) rotates the 2D list: list[pixels per image][mumber of images].
    result = [func(vals) for vals in zip(*pixels)]
    # result is a 1D list and can be used to create an ImagePlus object.
    from ij import ImagePlus
    from ij.process import FloatProcessor
    return ImagePlus(kwargs['title'], FloatProcessor(img_size, img_size, result))

def split_list(alist, wanted_parts=1):
    """Split a list to the given number of parts."""
    length = len(alist)
    # alist[a:b:step] is used to get only a subsection of the list 'alist'.
    # alist[a:b] is the same as [a:b:1].
    # '//' is an integer division.
    # Without 'from __future__ import division' '/' would be an integer division.
    return [ alist[i*length // wanted_parts: (i+1)*length // wanted_parts]
             for i in range(wanted_parts) ]

def fft_filter(imp):
    """ Removing noise from an image by using a FFT filter
    This are operations copied from the ImageJ macro recorder.
    Jython does not complain when you forget to remove the semicolons.
    """
    from ij import IJ
    IJ.run(imp, "FFT", "");
    # No ImagePlus is returned by the FFT function of ImageJ.
    # We need to use the WindowManager to select the newly created image.
    from ij import WindowManager as wm
    fft = wm.getImage("FFT of " + imp.getTitle())
    IJ.run(fft, "Find Maxima...", "noise=64 output=[Point Selection] exclude");
    # Enlarging the point selectins from Find Maxima.
    IJ.run(fft, "Enlarge...", "enlarge=2");
    # Inverting the selection.
    IJ.run(fft, "Make Inverse", "");
    IJ.run(fft, "Macro...", "code=v=0");
    IJ.run(fft, "Inverse FFT", "");
    fft.changes = False
    fft.close()
    imp_filtered = wm.getImage("Inverse FFT of " + imp.getTitle())
    imp_filtered.setTitle("Filtered " + imp.getTitle())
    imp_filtered.changes = False
    return imp_filtered


# If a Jython script is run, the variable __name__ contains the string '__main__'.
# If a script is loaded as module, __name__ has a different value.
if __name__ in ['__builtin__','__main__']:
    run_script()
