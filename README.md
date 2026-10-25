# Simple Physics

A small Java/libGDX desktop physics demo for creating and interacting with simple physics objects.

## Object settings

- Select **Create square** or **Create circle**, then click in the simulation area to place an object.
- Set the square side length or circle radius before creating objects. Enter a value with the keyboard or adjust it using the arrows.
- Enable **Is static** to make newly created objects immovable.

## Physics settings

- Toggle **Gravity** to turn gravity on or off.
- Select **Apply impulse**, then drag from a dynamic object to apply an impulse. The arrow previews the impulse direction and magnitude.

## Camera settings

- Select **Follow**, then click an object to track it with the camera. If the followed object is removed, Follow turns off.
- Select **Reset camera** to return the view to the world origin and turn off Follow.
- Drag with the middle mouse button over the simulation to pan the camera.
- Scroll the mouse wheel over the simulation to zoom. Over the settings panel, the wheel scrolls the settings instead.

Objects more than 1,000 world units from the origin are removed automatically.
