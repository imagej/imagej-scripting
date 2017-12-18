/* This script contains a couple of useful utilities for
 * manipulating line ROIs and point coordinates on an image,
 * as well as an internal test to demonstrate their utility.
 */

// Set up test image:
newImage("test1", "RGB black", 256, 256, 1);
makeRectangle(50, 50, 156, 156);
setForegroundColor(0, 255, 255);
run("Fill", "slice");
makeRectangle(120,120,16,16);
setForegroundColor(0, 0, 0);
run("Fill", "slice");
makeRectangle(60,126,50,4);
run("Fill", "slice");
makeRectangle(126,146,4,50);
setForegroundColor(120, 120, 120);
run("Fill", "slice");

// Line selection and rotation:
makeLine(50, 50, 206, 206);

// Test rotations with 45 degree rotations:
waitForUser("Check");
rotateCurrentLineSelection(45);
waitForUser("Check");
rotateCurrentLineSelection(45);
waitForUser("Check");
rotateCurrentLineSelection(45);
waitForUser("Translate");
translateCurrentLineSelection(78,0);

// Functions required to do the rotation:
function rotateCurrentLineSelection(theta){
	getSelectionCoordinates( x, y );
	getDimensions(w,h,ch,sl,fr);
	p1 = rotatePoint(theta,x[0],y[0],h,w);
	p2 = rotatePoint(theta,x[1],y[1],h,w);
	makeLine(p1[0],p1[1],p2[0],p2[1]);
}

function translateCurrentLineSelection(deltx,delty){
	getSelectionCoordinates(x,y);
	x = newArray(x[0]+deltx,x[1]+deltx);
	y = newArray(y[0]+delty,y[1]+delty);
	makeLine(x[0],y[0],x[1],y[1]);
}

function rotatePoint(theta,x,y,h,w){
	// Translate to centre origin cartesian and radians
	x1 = x-(w/2); 
	y1 = -(y-(h/2));
	theta = theta*PI/180;
	// Rotate point	
	x2 = (x1*cos(theta))-(y1*sin(theta));
	y2 = (x1*sin(theta))+(y1*cos(theta));
	// Translate back to imageJ coordinates
	x2 = x2 + (w/2); 
	y2 = (w/2)-y2;
	point = newArray(x2,y2);
	return point;
}