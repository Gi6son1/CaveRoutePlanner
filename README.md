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
  Took a few days off due to wanting to play videogames and I felt I had good progress. Now I'm back, and today I made some big strides. Using a graph points and trial and error, I manually filled in the cave survey with coordinates of junctions, paths and extra data about the areas i.e. if it's in water or hard to traverse, or altitude. I also took the liberty of using the distance bar in the survey to figure out approximately how many pixels in a meter, it's roughly 14.6, meaning that I now have all the distances for all the lines in the survey, and I should be good to move onto pathfinding between points. All of this data can be outputted for debugging purposes onto the canvas using https://developer.android.com/develop/ui/compose/quick-guides/content/video/draw-text-compose#:~:text=In%20Compose%2C%20you%20can%20draw,%2C%20alignment%2C%20and%20other%20properties.
  
  One thing to note is that although I used the graph to find the points, I made sure that I referenced the points with the pixels in teh survey bitmap instead of the percentage from the top left, since if I decide to not use canvas anymore, all the work I've done today will still be valid since it takes it's place from the survey's point of view, not the canvas. The pathfinding should also be unaffected by the frontend, meaning the work I do tomorrow is also stable.
  
  If I can't do canvas anymore for any future reason, I need to remember that Maxim told me that I could be able to split the survey bitmap into multiple tiles for the tileView library as a backup plan, and since I now have all the bit coordinates, I should be able to still use the information I created today to transfer locations of points and lines into the newly split tiles using some a few cheeky calculations.
  
  Successful day today. Yipee!

25/02/2025
  Today I worked on the pathfinding algorithm. I decided to use Dijkstra'a algorithm because it means that it will always find the best route to a node instead of a best guess like A*. It also means that depending on where the person sets their orignal location, which will probably be entrance, I'll be able to preload all best routes so that when the user selects a node to travel to, the best route has already been calculated instead of being recalculated everytime. 
  I used: https://medium.com/@chetanshingare2991/finding-the-shortest-route-graph-algorithms-in-kotlin-dijkstras-and-bellman-ford-77ff80c6d412 to help me find the kotlin version of the algorithm.

  Tomorrow, I'm going to focus on tweaking this algorithm so that it displays which edge was the closest distance edge for each node, meaning that I should be able to then find paths, as the algorithm currenlty only returns the final distances to each node. I also need to add in the functionality to change the costs depending onn the requirements given i.e. altitude or hard traversal or water, but this can be done easily tomorrow; but for now I indend to add some cost that gradually increases the weight depending on the distance. i.e. hasWater doesn't increase the weight by a static amount because it will become less relevant for larger distances, but instead it will increase the orignal weight by, say 1.5 or 1.2 or whatever feels better after testing.

  Goodbye for now

26/02/2025
  Today, I did a bit more work on the pathfinder. It works, Yipeee. I've made a few changes to the algorithm so that it now, instead of returning a list of distances to all the points, it instead returns a map of reachable nodes from the start node, with a value of the edge from that node that is the quickest edge to take to get closer to that start node. I've also made sure that depending on the requirements i.e. no water or no hard traversals or high altitude only, the pathfinding changes depending on those requirements. I then was able to use that map and through backtracking edges and nodes, generate a sequenced lists of edges to take for that route from start to goal, along with a total distance for the journey.
  Next, I'll focus on how to visually show the route on the canvas, which shouldn't be too difficult, and then set up buttons to be used in the demo that demmonstrate the dynamic pathfinding on the interactable survey map

10/03/2025
  After my break from coding to do other things like touching grass and completing my other module's assignment, I got back to work today. As of now, the demo is completed. I've implemented a Route class to hold in the current route, and at what stage of the route the user is at. I then added code to go through the chosen route, and draw lines on the canvas of where the route is. For the demo I've also created buttons and sliders for use in the demo, to show how the pathfinding changes based on location and requirements.
  
  In order for the objects to save upon recomposition, and to even recompose the screen when those variables change, I made a bunch of the classes parcelizable https://developer.android.com/kotlin/parcelize meaning that I could use the rememberSaveable method on them, which essentially transforms the data structures and objects into a format that can be easily saved i.e. an object into a bunch of lists. 

  I can continue to work on the demo tomorrow, although I may need to do some other prep first  i.e. do some more UI designs. Also, the next natural step now is to put my focus into the feature of touching a coordinate on the canvas and it finding the closest vertex to it, so that users can actually use the app.

11/03/2025
  We have more success! After my last entry, I couldn't sleep and proceeded to spend until 8am coding to find out how to detect taps on the survey along with coordinates. Safe to say that because I'm using jetpack compose, it means that I don't have to do matrix calculations at all, which makes my code both more readable and simpler to write. I also spent the rest of today (after getting a few hours of shut-eye) finishing the task, and now the result is the user can long press on the survey (even after rotating, scaling, panning) and a shortest path will be drawn from the entrance to the closest node that user touched (the dynamic requiremetn path changing still works). Legendary. I used these to help https://developer.android.com/develop/ui/compose/touch-input/pointer-input/tap-and-press and https://developer.android.com/reference/kotlin/androidx/compose/ui/layout/OnGloballyPositionedModifier to convert the coordinates of the tap into float values to state-hoist up to the parent composable.

  Another task completed, is that I've made it so that the user can move the survey around on the screen, but there's now limitations on the movement i.e. cannot pan off screen (and can only pan when the survey has been zoomed in at least a bit), cannot zoom too far in or out. I used this to partly help https://developer.android.com/reference/kotlin/androidx/compose/foundation/gestures/package-summary#(androidx.compose.ui.input.pointer.PointerInputScope).detectTransformGestures(kotlin.Boolean,kotlin.Function4) https://stackoverflow.com/questions/68919900/screen-width-and-height-in-jetpack-compose . One thing I still haven't mastered yet was the centroid-based scaling (zooming on the location of the finger zoom gesture) and for the moment, it only zooms with the centre of the survey as the zoom point. I aim to fix this, but it's not that deep. I'm putting aside for now. 

  I have my demo ready. 

  12/03/2025 - 8am
  I did it again. I've added another feature now, being a user can progress the current path bar by using the volume buttons on the phone. I used this for assistence: https://stackoverflow.com/questions/68861171/onkeyevent-modifier-doesnt-work-in-jetpack-compose/68863985#68863985

  I need sleep, may God have mercy on my soul

  16/03/2025
  Some good work was completed today. After many hours of problem solving, once a user select a point and the first waypoint route in the section has been identified, the view focuses in on that point only. This was pretty tricky to figure out, due to my reluctance to try and matrix transformations (sorry Maxim, I'm grateful for the suggestion, but my secondary school days are behind me, and I don't want to revisit those years again), but I caved in and decided to try it out. I decdied to focus on one thing at a time for this. I started with rotation, determining the angle that the first point in the waypoint made with the last point, before adding some extra specific angles to get the orientation I want, then setting that as the new rotation. The same applies for zoom. 
  
  For offset, this is where it gets tricky because for that I had to do some rotation matrix work. I specifically used this formula https://en.wikipedia.org/wiki/Rotation_matrix for 2D work. I first calculated the centroid of the specific path section as a whole with respect to the box composable, then I found what this offset is compared to the centre point of the box (because we want to translate it from the middle for easier understanding). Then use the rotation matrix to find out, after the new angle calculated, what position this offset would be in now, before then applying the calculated zoom. Now the offset has been completed.

  My next task now is to focus on some frontend (using my figma designs), because I have a good amount of backend functionality, and before I work on other features, I have to make my work look pretty...

  I've also decided that since I want my work to be as close to business standards as possible, I'll start using SonarLint https://medium.com/@mohibshaikh19/sonarlint-integration-with-android-studio-75c20b5cbd16 , an extension that I used when I wrote software for VISA. This means that any code that I write is checked for vulnerabilities or potential errors or just better practice. This will increase my learning of the business standards for kotlin, which is great. 

19/03/2025
Both today and yesterday, I started work on the UI, in this case when a user chooses a location on the map, a little pinpoint shows up, with a path from the source to the destination. I added a button to start the journey, which changes the layout and also buttons for setting the pinpoint as the new source, changing travel details, the hoke button, and also a cave exit button which takes you to the nearest exit.

I split up the UI layouts into a pre journey layout (as described) and an in journey layout. The in journey layout allows the user to cancel the trip, do a cave exit from current location, move to next or previous stage, cancel trip, and see details about current route and path.

The UI is very basic (mainly just getting the functionality working first) and I'll properly develop it later when I have time. I'll make sure to add action check dialogs snd also little action confirmation messages for some things as well.

29/03/2025
Summarising my work today, I added custom composables for UI elements, like Icon and Text buttons, and implemented them into the UI, I also added proper navigation icons into the route once started, so the user has a arrow Icon, and the current path has a green arrow at the end to show direction. I also added a source icon in addition to the destination icon (as per Maxim's suggestion) and it looks pretty good!

30/03/2025
Bigger strides today.  I fully completed the UI for the navigation and survey screen, fitting with my UI design. I implemented time estimation for route and paths and this is outputted in the journey data. I implemented the emergency exit feature and all the action check dialogs required for this, as well as the travel conditions dialog. For increasing the number of cavers, I used a pretty good library: https://github.com/marcauberer/compose-number-picker which gives a basic number picker, just what I need.

Essentially, this section is completed. The next thing I'll need to work on will be the database, where the survey will be saved and read in an appropriate way. From there, I can create the home screen which displays these surveys, and then can work on the implemetnation that allows a new navigatable survey to be created.

31/03/2025
Today, I worked on the database. Prior to implementation, I decided to write up a schema so that I knew how to structure my database. For starteres, I needed a Cave table, which wwould store info on the cave that the survey was about. Survey has it's own table, and edges and nodes are stored in different tables (because otherwise, I'd be storing all of these inside a single column in the survey table and it would be a shitshow in terms of storage). 

To implement this, I used a combination of :
Blackboard materials from my android module,
https://stackoverflow.com/questions/45988446/how-to-create-a-table-with-a-two-or-more-foreign-keys-using-android-room, 
https://medium.com/androiddevelopers/7-pro-tips-for-room-fbadea4bfbd1, my previous Quizel app database implementations
https://github.com/Gi6son1/Quizel-App/blob/main/app/src/main/java/com/cs31620/quizel/datasource, 
with debugging assistence from GenAI and https://stackoverflow.com/questions/51340694/sqlite-requires-having-a-unique-constraint-android-room-annotations, https://stackoverflow.com/questions/29341380/sqlite-foreign-key-constraint-failed-code-787, https://stackoverflow.com/questions/76840478/sqliteconstraintexception-foreign-key-constraint-failed-code-787,

I bashed my head on the keyboard many times this day, because it was very finnicky to implement. But in the end I was able to store cave info, surveys, and nodes and edges in differrent tables, and then link them all together when reading them. I still have to adapt this system to the current one because it is practically separate still, however now that I have the functionality pretty much solved, it should be a case of "joining up the wires" and replacing the old survey reading system with the new one.

I hope the Kotlin gods take it easy on me

01/04/2025
Good progress made today:
I finishhed off the database implementation, replacing the old system with the new one. And now the exammple ll cave survey is stored on the DB, and can be loaded into the navigation screen.

I also started work on the landing page, where the list of caves will be. After making a card to hold the cave data, I turned it into a button which I used to select a cave from the list of caves read from the DB (currently only one). I also used my previous project work for inspiration on how I'd join up the cave list screen and the navigation screen, https://github.com/Gi6son1/Quizel-App/blob/main/app/src/main/java/com/cs31620/quizel/MainActivity.kt and now it's set up so that when a cave in the list is tapped on, the corresponding survey is read from the DB and the user can navigate with it, and the home button in the navigation screen sends the user back to the main menu if tapped. Bosh. Still need to make it look prettier since it's pretty basic, but functionality is there, and I should soon be able to work on the create your own navigateable survey feature. For the expandable list I used this: https://mrugendrathatte.medium.com/expandable-list-in-compose-b5ebdd768f37

05/04/2025
I am a god. Turns out to work out the sensor fusion stuff, I looking in the wrong place. I looked for libraries that would do this for me, HOWEVER, in android studio, there's stuff that already exists that uses sensor fusion. There's a class callled the Sensor Manager https://developer.android.com/reference/android/hardware/SensorManager.html that allows you to register a specific sensor to be monitored. 
I started out by looking at this tutorial https://www.kodeco.com/10838302-sensors-tutorial-for-android-getting-started/page/2 that walks you through how to use the accelarometor and magnetometer to read in sensor imput about the phones magnetic inputs and combines them with the accelarometer to be able to find the Z axis rotation, with respect to true north. 
I decided to use this one the direction arrow instead the entire survey box because I realised that 
a) the sensor input didn't have any filtering which means that the rotation would be very jittery, so adding that jitter rotation on a smaller object would make the experience better for the user.
b) Rotating the screen is great, but the direction arrow  doesnt' rotate and actually it less confusing to the user if the arrow is rotated instead of the map, so that they can the rotate it towards the correct direction (which would aligh their phone straight towards the correct direction)
This gave a good base compass. I then started looking at filters to apply to the input data to smooth it out, and in my search, I discovered that another sesnor RotationVector https://source.android.com/docs/core/interaction/sensors/sensor-types#rotation_vector, https://developer.android.com/develop/sensors-and-location/sensors/sensors_motion#sensors-motion-rotate actually already combines the magnetometer, gyroscope, and accelerometer, with smoothing and filtering, to provide a better sense of direction. Using the same technique from the tutorial (and the example code in the rotationVector page) to turn the data into z-axis direction, I was able to greatly improve the compass feature, and now it's mint!

Enough procrastinating, I should probably work on the survey creation feature now, but it's just so long! And I can't be bothered.

18/04/2025

Work OVER! Completed!

I used these to help me:
https://stackoverflow.com/questions/68840221/kotlin-how-to-convert-image-uri-to-bitmap
https://medium.com/@everydayprogrammer/implement-android-photo-picker-in-android-studio-3562a85c85f1
https://developer.android.com/training/data-storage/shared/photopicker

https://developer.android.com/develop/ui/compose/touch-input/pointer-input/understand-gestures#add-custom
https://developer.android.com/reference/kotlin/androidx/compose/ui/input/pointer/AwaitPointerEventScope

https://m3.material.io/components/segmented-buttons/overview

https://stackoverflow.com/questions/17839388/creating-a-scaled-bitmap-with-createscaledbitmap-in-android

https://stackoverflow.com/questions/28753285/how-can-i-create-static-method-for-enum-in-kotlin

https://developer.android.com/develop/ui/compose/components/snackbar

https://stackoverflow.com/questions/78343979/collecting-flow-from-room-the-correct-way

https://dpw-developer.medium.com/simple-steps-to-saving-loading-and-deleting-bitmaps-in-android-storage-using-java-a974b9d97c4a

https://stackoverflow.com/questions/15662258/how-to-save-a-bitmap-on-internal-storage
