# imagej-scripting

ImageJ-specific applications of the SciJava script languages.

This repo contains example scripts in various scripting languages. All the scripts placed under [`src/main/resources/script_templates`](src/main/resources/script_templates) will be available in the ImageJ Script Editor under the `Templates` menu entry.


## Tests

Because most of those scripts are available in the ImageJ Script Editor, they should be tested each time they are modified and/or an ImageJ component changes its version.

To do so each script author is invited to write a test for his scripts. See the [`test/`](src/test/java/net/imagej/scripting) folder for examples.

Most of the tests are very simple and quick to write since they only make sure the script can run without error and test for an eventual output to be not null.
