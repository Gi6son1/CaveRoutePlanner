Progress Diary:

17/02/2025
  Today I created the project in andorid studio. I added the PNG of the LL cave survey (which took ages to read and kept returning null because even though the filename ended in .jpg, it was, indeed a TIF file).
  After converting it to a jpg online and putting it back into the project, I was able to render the image on the screen. 
  The next job was to learn how Canvas worked, since this is the class thhat allows me to draw lines and dots on the screen using coordinates.
  After fiddling around with overlaying canvas over the image, I gave up.
  https://developer.android.com/reference/kotlin/android/graphics/Canvas

18/02/2025
  Today I'm trying a different approach, the user will need to pinch and zoom the map, so what I'm going to try instead is focus on this for nopw, then try and attach a canvas to it afterwards. 
  I found this https://stackoverflow.com/questions/14004424/android-zoomable-scrollable-imageview-with-overlays, which tells me that I could use TileView, a library for scrolling images.

19/02/2025
  So, I found out that the TileView library is not to my suiting. The issue is that, in order to render an image onto a screen, with POIs and useful scrolling, the library requires the PNG image to be transformed into a series of tiles, which can only be done via bash scripts.
  Because of this, and I researched, it will be hard for a user to turn an image into a bunch of tiles, save them locally, and then transform them into a tilemap. Therefore this method has been made redundant, because I want a user to be able to simply import an image and       mark it up into a workable map, without the   tile map conversion.

  As a fix, I decided to implement a transformable modifier to the box (which includes the image and the canvas(which holds the graph), allowing it to be zoomed). See below for documentation which I used:
    https://developer.android.com/reference/kotlin/androidx/compose/foundation/gestures/package-summary#(androidx.compose.ui.Modifier).transformable(androidx.compose.foundation.gestures.TransformableState,kotlin.Boolean,kotlin.Boolean)
    Thorugh this, I was able to limit the zooming so that the survey doesn't become smaller than the screen, however I am still yet able to restrict the panning so that the edges of the survey don't leave the screen.
    I've sone some research, and can possibly find a solution:
    https://github.com/Tlaster/Zoomable
    This is a library that allows an object to be panned and zoomed without leaving the boundaries of the object, although, I am unsure if matchParentSize() applies to anything else but boxes.
    After, trying there was an error with the zoomable state - even if I tried zooming with a basic object, it still failed, suggesting that the library is too outdated to use.

20/02/2025
  I have Success! I've been able to line up the canvas over the top of the survey in such a way that the coordinates of points and lines on the survey line are displayed correclty on the canvas, and any transformations done to the survey don't impact the points on the canvas, and they will still line up perfectly no matter     what.

23/02/2025
  Took a few days off due to wanting to play videogames and I felt I had good progress. Now I'm back, and today I made some big strides. Using a graph points and trial and error, I manually filled in the cave survey with coordinates of junctions, paths and extra data about the areas i.e. if it's in water or hard to traverse, or altitude. I also took the liberty of using the distance bar in the survey to figure out approximately how many pixels in a meter, it's roughly 14.6, meaning that I now have all the distances for all the lines in the survey, and I should be good to move onto pathfinding between points. 
  
  One thing to note is that although I used the graph to find the points, I made sure that I referenced the points with the pixels in teh survey bitmap instead of the percentage from the top left, since if I decide to not use canvas anymore, all the work I've done today will still be valid since it takes it's place from the survey's point of view, not the canvas. The pathfinding should also be unaffected by the frontend, meaning the work I do tomorrow is also stable.
  
  If I can't do canvas anymore for any future reason, I need to remember that Maxim told me that I could be able to split the survey bitmap into multiple tiles for the tileView library as a backup plan, and since I now have all the bit coordinates, I should be able to still use the information I created today to transfer locations of points and lines into the newly split tiles using some a few cheeky calculations.
  
  Successful day today. Yipee!
