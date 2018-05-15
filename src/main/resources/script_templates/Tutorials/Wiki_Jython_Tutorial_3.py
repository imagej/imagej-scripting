#@ File(label='Choose a directory', style='directory') import_dir
#@ String(label='File types', value='tif;png') file_types
#@ String(label='Filter', value='') filters
#@ Boolean(label='Recursive search', value=True) do_recursive

'''A batch opener using os.walk()

This code is part of the Jython tutorial at the ImageJ wiki.
http://imagej.net/Jython_Scripting#A_batch_opener_using_os.walk.28.29
'''
 
# We do only include the module os,
# as we can use os.path.walk()
# to access functions of the submodule.
import os
from java.io import File
 
from ij import IJ
 
def batch_open_images(path, file_type=None, name_filter=None, recursive=False):
    '''Open all files in the given folder.
    :param path: The path from were to open the images. String and java.io.File are allowed.
    :param file_type: Only accept files with the given extension (default: None).
    :param name_filter: Only accept files that contain the given string (default: None).
    :param recursive: Process directories recursively (default: False).
    '''
       # Converting a File object to a string.
    if isinstance(path, File):
        path = path.getAbsolutePath()
 
    def check_type(string):
        '''This function is used to check the file type.
        It is possible to use a single string or a list/tuple of strings as filter.
        This function can access the variables of the surrounding function.
        :param string: The filename to perform the check on.
        '''
        if file_type:
            # The first branch is used if file_type is a list or a tuple.
            if type(file_type) in [list, tuple]:
                for file_type_ in file_type:
                    if string.endswith(file_type_):
                        # Exit the function with True.
                        return True
                    else:
                        # Next iteration of the for loop.
                        continue
            # The second branch is used if file_type is a string.
            elif isinstance(file_type, string):
                if string.endswith(file_type):
                    return True
                else:
                    return False
            return False
        # Accept all files if file_type is None.
        else:
            return True
 
    def check_filter(string):
        '''This function is used to check for a given filter.
        It is possible to use a single string or a list/tuple of strings as filter.
        This function can access the variables of the surrounding function.
        :param string: The filename to perform the filtering on.
        '''
        if name_filter:
            # The first branch is used if name_filter is a list or a tuple.
            if type(name_filter) in [list, tuple]:
                for name_filter_ in name_filter:
                    if name_filter_ in string:
                        # Exit the function with True.
                        return True
                    else:
                        # Next iteration of the for loop.
                        continue
            # The second branch is used if name_filter is a string.
            elif isinstance(name_filter, string):
                if name_filter in string:
                    return True
                else:
                    return False
            return False
        else:
        # Accept all files if name_filter is None.
            return True
 
    # We collect all files to open in a list.
    path_to_images = []
    # Replacing some abbreviations (e.g. $HOME on Linux).
    path = os.path.expanduser(path)
    path = os.path.expandvars(path)
    # If we don't want a recursive search, we can use os.listdir().
    if not recursive:
        for file_name in os.listdir(path):
            full_path = os.path.join(path, file_name)
            if os.path.isfile(full_path):
                if check_type(file_name):
                    if check_filter(file_name):
                        path_to_images.append(full_path)
    # For a recursive search os.walk() is used.
    else:
        # os.walk() is iterable.
        # Each iteration of the for loop processes a different directory.
        # the first return value represents the current directory.
        # The second return value is a list of included directories.
        # The third return value is a list of included files.
        for directory, dir_names, file_names in os.walk(path):
            # We are only interested in files.
            for file_name in file_names:
                # The list contains only the file names.
                # The full path needs to be reconstructed.
                full_path = os.path.join(directory, file_name)
                # Both checks are performed to filter the files.
                if check_type(file_name):
                    if check_filter(file_name):
                        # Add the file to the list of images to open.
                        path_to_images.append(full_path)
    # Create the list that will be returned by this function.
    images = []
    for img_path in path_to_images:
        # IJ.openImage() returns an ImagePlus object or None.
        imp = IJ.openImage(img_path)
        # An object equals True and None equals False.
        if imp:
            images.append(imp)
    return images
 
def split_string(input_string):
    '''Split a string to a list and strip it
    :param input_string: A string that contains semicolons as separators.
    '''
    string_splitted = input_string.split(';')
    # Remove whitespace at the beginning and end of each string
    strings_striped = [string.strip() for string in string_splitted]
    return strings_striped
 
if __name__ == '__main__':
    '''Run the batch_open_images() function using the Scripting Parameters.
    '''
    images = batch_open_images(import_dir,
                               split_string(file_types),
                               split_string(filters),
                               do_recursive
                              )
    for image in images:
        # Call the toString() method of each ImagePlus object.
        print(image)