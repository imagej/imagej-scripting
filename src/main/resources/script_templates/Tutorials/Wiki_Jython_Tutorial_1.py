'''Image selection using the GenericDialog class

This code is part of the Jython tutorial at the ImageJ wiki.
http://imagej.net/Jython_Scripting#Image_selection_using_the_GenericDialog_class
'''

# The module __future__ contains some useful functions:
# https://docs.python.org/2/library/__future__.html
from __future__ import with_statement, division
# This imports the function random from the module random.
from random import random
# Next we import Java Classes into Jython.
# This is how we can acces the ImageJ API:
# https://imagej.nih.gov/ij/developer/api/allclasses-noframe.html
from ij import IJ, WindowManager
from ij.gui import GenericDialog

# A function is created with the def keyword.
# This function does not need any parameters.
def create_test_image():
    # Python uses indentation to create code blocks

    # Local variables are assigned.
    # We can assign the same value to more than one variable.
    image_width = image_height = 512
    box_width = box_height = 128
    offset_x = offset_y = 192
    counts = 64
    stdv = 16
    # The build in function int() is used to convert float to int.
    # The variable random contains a function that is called by adding parentheses.
    offset_x = int(2 * random() * offset_x)
    offset_y = int(2 * random() * offset_y)
    # We can define a function inside a function.
    # Outside of create_test_image() this function is not available.
    # By adding an asterisk to a parameter, all given parameters are combined to a tuple.
    def make_title(*to_concat):
        prefix = 'TestImage'
        # To create a tuple with a single entry the comma is necessary.
        # The 2 tuples are concatenated by using the + operator.
        to_join = (prefix,) + to_concat
        # We create a generator that converts every singe entry of the tuple to a string.
        strings_to_join = (str(arg) for arg in to_join)
        # The string ',' has a join method to concatenate values of a tuple with the string as seperator.
        # The result is a string.
        return ','.join(strings_to_join)
    def check_existence(title):
        if WindowManager.getIDList() is None:
            return False
        image_titles = (WindowManager.getImage(id).getTitle() for id in WindowManager.getIDList())
        return title in image_titles
    # To negate an expression put not in front of it.
    if not check_existence(make_title(offset_x, offset_y)):
        # The following code has been created by using the Recorder of ImageJ, set to output Java code.
        # By removing the semicolons, the code can be used in Jython.
        # The parameters can be modified by using variables and string concatenation.
        imp = IJ.createImage(make_title(offset_x, offset_y), "8-bit black", image_width, image_height, 1)
        # The build in function str() is used to convert int to string.
        IJ.run(imp, "Add...", "value=" + str(counts))
        imp.setRoi(offset_x , offset_y, box_width, box_height)
        IJ.run(imp, "Add...", "value=" + str(counts))
        IJ.run(imp, "Select None", "")
        IJ.run(imp, "Add Specified Noise...", "standard=" + str(stdv));
        # We don't want to confirm when closing one of the newly created images.
        imp.changes = False
        imp.show()

# This function uses parameters.
# A default value is given to the third parameter.
def create_selection_dialog(image_titles, defaults, title='Select images for processing'):
    gd = GenericDialog(title)
    # The build in function enumerate() returns two values:
    # The index and the value stored in the tuple/list.
    for index, default in enumerate(defaults):
        # for each loop we add a new choice element to the dialog.
        gd.addChoice('Image_'+ str(index + 1), image_titles, image_titles[default])
    gd.showDialog()
    if gd.wasCanceled():
        return None
    # This function returns a list.
    # _ is used as a placeholder for values we don't use.
    # The for loop is used to call gd.getNextChoiceIndex() len(defaults) times.
    return [gd.getNextChoiceIndex() for _ in defaults]

# It's best practice to create a function that contains the code that is executed when running the script.
# This enables us to stop the script by just calling return.
def run_script():
    while WindowManager.getImageCount() < 10:
        create_test_image()

    image_titles = [WindowManager.getImage(id).getTitle() for id in WindowManager.getIDList()]
    # range(3) will create the list [0, 1, 2].
    selected_indices = create_selection_dialog(image_titles, range(3))
    # The script stops if the dialog has ben canceld (None was returned from create_selection_dialog).
    if selected_indices is None:
        print('Script was canceld.')
        return
    # We have to get the corresponding IMagePlus objects.
    selected_imps = [WindowManager.getImage(id) for id in [WindowManager.getIDList()[index] for index in selected_indices]]
    # The previous line can be split into 2 lines:
    # selected_ids = [WindowManager.getIDList()[index] for index in selected_indices]
    # selected_imps = [WindowManager.getImage(id) for id in selected_ids]

    for imp in selected_imps:
        # Strings can be formated using the % operator:
        # http://www.learnpython.org/en/String_Formatting
        IJ.log('The image \'%s\' has been selected.' % imp.getTitle())

# If a Jython script is run, the variable __name__ contains the string '__main__'.
# If a script is loaded as module, __name__ has a different value.
if __name__ in ['__builtin__','__main__']:
    run_script()
