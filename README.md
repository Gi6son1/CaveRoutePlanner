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
  https://developer.android.com/reference/kotlin/androidx/compose/foundation/gestures/package-summary#(androidx.compose.ui.Modifier).transformable(androidx.compose.foundation.gestures.TransformableState,kotlin.Boolean,kotlin.Boolean)
