# @DatasetIOService ds
# @UIService ui
# @File file

# load the dataset
dataset = ds.open(file.getAbsolutePath())

# display the dataset
ui.show(dataset)
