# Simple Physics

A Java/libGDX desktop demo for creating and interacting with physics objects.

## Object settings

- Select **Create square** or **Create circle**, then click in the simulation to add an object.
- Set the square side length or circle radius before creating objects. Enter a value or adjust it with the arrow buttons.
- Enable **Is static** to make newly created objects immovable.

## Physics settings

- Toggle **Gravity** to enable or disable gravity.
- Toggle **Reduce velocity** to gradually slow dynamic objects to a stop. While enabled, it counteracts gravity.
- Select **Apply impulse**, then drag from a dynamic object to apply an impulse. Use **Impulse scaling** to choose linear or exponential scaling.
- Select **Drag objects**, then drag a dynamic object to move it. Static objects cannot be moved with this tool.
- Select **Create revolute joint**, then drag from an anchor point on one object to an anchor point on another to connect them.

## Camera settings

- Select **Follow**, then click an object to track it. Follow turns off if the object is removed.
- Select **Reset camera** to return to the world origin and turn off Follow.
- Drag with the middle mouse button over the simulation to pan the camera.
- Scroll over the simulation to zoom, or over the settings panel to scroll the settings.

Objects more than 1,000 world units from the origin are removed automatically.
