package br.edu.ufersa.simplephysics.ui;

import br.edu.ufersa.simplephysics.math.Vector2;
import br.edu.ufersa.simplephysics.physics.PhysicsWorld;
import br.edu.ufersa.simplephysics.screen.SimulationInputController;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import java.math.BigDecimal;
import java.math.RoundingMode;

public final class SimulationButtons {
    // Button creation is provided through static methods; instances are not needed.
    private SimulationButtons() {
    }

    // Creates a toggle button that selects the square-creation tool.
    public static TextButton createSquareButton(
        Skin skin,
        SimulationInputController inputController
    ) {
        return createToolButton(
            "Create square",
            SimulationInputController.Tool.CREATE_SQUARE,
            skin,
            inputController
        );
    }

    // Creates a toggle button that selects the circle-creation tool.
    public static TextButton createCircleButton(
        Skin skin,
        SimulationInputController inputController
    ) {
        return createToolButton(
            "Create circle",
            SimulationInputController.Tool.CREATE_CIRCLE,
            skin,
            inputController
        );
    }

    // Shows a prompt until a shape is selected, then displays that shape's size control.
    public static Table createShapeSizeControl(
        Skin skin,
        TextButton squareButton,
        TextButton circleButton,
        SimulationInputController inputController
    ) {
        Label title = new Label("Shape size", skin);
        Label.LabelStyle descriptionStyle = new Label.LabelStyle(skin.get(Label.LabelStyle.class));
        descriptionStyle.fontColor = Color.valueOf("a0a0a0");
        Label description = new Label("Select a shape below", descriptionStyle);
        Table prompt = new Table(skin);
        prompt.setBackground(skin.get(TextButton.TextButtonStyle.class).up);
        prompt.add(title).center();
        prompt.row();
        prompt.add(description).center();
        Table circleRadiusControl = createSizeControl(
            "Radius",
            inputController.getCircleRadius(),
            skin,
            inputController::setCircleRadius,
            inputController::getCircleRadius
        );
        Table squareSideLengthControl = createSizeControl(
            "Side length",
            inputController.getSquareSideLength(),
            skin,
            inputController::setSquareSideLength,
            inputController::getSquareSideLength
        );

        Table shapeSizeControl = new Table(skin);

        ChangeListener selectionListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean circleSelected = circleButton.isChecked();
                boolean squareSelected = squareButton.isChecked();
                shapeSizeControl.clearChildren();
                if (circleSelected) {
                    shapeSizeControl.add(circleRadiusControl).width(300).fillX();
                } else if (squareSelected) {
                    shapeSizeControl.add(squareSideLengthControl).width(300).fillX();
                } else {
                    shapeSizeControl.add(prompt).width(300).height(50).fill();
                }
            }
        };
        squareButton.addListener(selectionListener);
        circleButton.addListener(selectionListener);
        selectionListener.changed(null, null);

        return shapeSizeControl;
    }

    private static Table createSizeControl(
        String label,
        double initialValue,
        Skin skin,
        java.util.function.DoubleConsumer valueSetter,
        java.util.function.DoubleSupplier valueGetter
    ) {
        TextField valueField = new TextField(formatSize(initialValue), skin);
        valueField.setTextFieldFilter((textField, character) -> {
            if (!Character.isDigit(character) && character != '.') {
                return false;
            }

            String currentText = textField.getText();
            int start = textField.getCursorPosition();
            int end = start;
            if (textField.getSelectionStart() >= 0) {
                start = Math.min(textField.getCursorPosition(), textField.getSelectionStart());
                end = Math.max(textField.getCursorPosition(), textField.getSelectionStart());
            }
            String candidate = currentText.substring(0, start)
                + character
                + currentText.substring(end);
            return candidate.matches("[0-9]*\\.?[0-9]{0,2}");
        });
        valueField.setTextFieldListener((textField, character) -> {
            try {
                double value = Double.parseDouble(textField.getText());
                if (Double.isFinite(value) && value > 0) {
                    valueSetter.accept(roundSize(value));
                }
            } catch (NumberFormatException ignored) {
                // Intermediate input such as an empty field is not a complete value yet.
            }
        });
        valueField.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                if (!focused) {
                    valueField.setText(formatSize(valueGetter.getAsDouble()));
                }
            }
        });

        TextButton increaseButton = new TextButton("\u2191", skin);
        TextButton decreaseButton = new TextButton("\u2193", skin);
        increaseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateSizeField(valueField, valueSetter, valueGetter, 1.0);
            }
        });
        decreaseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateSizeField(valueField, valueSetter, valueGetter, -1.0);
            }
        });

        Table arrowButtons = new Table(skin);
        arrowButtons.add(increaseButton).width(36).height(24);
        arrowButtons.row();
        arrowButtons.add(decreaseButton).width(36).height(24);

        Table sizeControl = new Table(skin);
        sizeControl.add(new Label(label, skin)).left().expandX();
        sizeControl.add(valueField).width(135).height(50);
        sizeControl.add(arrowButtons).width(36).height(50).padLeft(6);
        return sizeControl;
    }

    private static void updateSizeField(
        TextField valueField,
        java.util.function.DoubleConsumer valueSetter,
        java.util.function.DoubleSupplier valueGetter,
        double delta
    ) {
        double updatedValue = roundSize(Math.max(0.1, valueGetter.getAsDouble() + delta));
        valueSetter.accept(updatedValue);
        valueField.setText(formatSize(updatedValue));
    }

    private static double roundSize(double value) {
        return BigDecimal.valueOf(value)
            .setScale(2, RoundingMode.HALF_UP)
            .doubleValue();
    }

    private static String formatSize(double value) {
        return BigDecimal.valueOf(roundSize(value))
            .stripTrailingZeros()
            .toPlainString();
    }

    // Creates a toggle button that enables or disables gravity in the physics world.
    public static TextButton createGravityButton(Skin skin, PhysicsWorld world) {
        TextButton button = createToggleButton("Gravity", skin);
        button.setChecked(true);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                world.gravity = button.isChecked()
                    ? new Vector2(0, -9.81)
                    : new Vector2(0, 0);
            }
        });
        return button;
    }

    // Creates a toggle button that enables or disables collisions in the physics world.
    public static TextButton createCollisionButton(Skin skin, PhysicsWorld world) {
        TextButton button = createToggleButton("Collisions", skin);
        button.setChecked(world.collisionsEnabled);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                world.collisionsEnabled = button.isChecked();
            }
        });
        return button;
    }

    // Controls whether newly created physics objects are static.
    public static TextButton createStaticButton(
        Skin skin,
        SimulationInputController inputController
    ) {
        TextButton button = createToggleButton("Is static", skin);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                inputController.setNewObjectStatic(button.isChecked());
            }
        });
        return button;
    }

    // Creates a toggle button that selects the impulse tool.
    public static TextButton createImpulseButton(
        Skin skin,
        SimulationInputController inputController
    ) {
        return createToolButton(
            "Apply impulse",
            SimulationInputController.Tool.APPLY_IMPULSE,
            skin,
            inputController
        );
    }

    // Creates a toggle button that lets the next simulation click choose the camera target.
    public static TextButton createFollowObjectButton(
        Skin skin,
        SimulationInputController inputController
    ) {
        TextButton button = createToggleButton("Follow", skin);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                inputController.setCameraFollowEnabled(button.isChecked());
            }
        });
        return button;
    }

    // Makes any supplied tools exclusive; an optional follow button clears the selected tool.
    public static void configureSimulationToolGroup(
        TextButton followObjectButton,
        TextButton... toolButtons
    ) {
        ButtonGroup<TextButton> toolGroup = new ButtonGroup<>();
        toolGroup.setMinCheckCount(0);
        toolGroup.setMaxCheckCount(1);
        for (TextButton toolButton : toolButtons) {
            toolGroup.add(toolButton);
        }

        if (followObjectButton != null) {
            followObjectButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (followObjectButton.isChecked()) {
                        toolGroup.uncheckAll();
                    }
                }
            });
        }
    }

    // Disables target buttons when the condition is active and any trigger button is checked.
    public static void updateDisabledButtons(
        TextButton[] triggerButtons,
        TextButton[] buttonsToDisable,
        boolean condition
    ) {
        boolean shouldDisable = condition;
        if (shouldDisable) {
            shouldDisable = false;
            for (TextButton triggerButton : triggerButtons) {
                if (triggerButton.isChecked()) {
                    shouldDisable = true;
                    break;
                }
            }
        }

        for (TextButton button : buttonsToDisable) {
            button.setDisabled(shouldDisable);
        }
    }

    // Updates the follow button label to show whether it is off, awaiting a target, or tracking.
    public static void updateFollowObjectButtonLabel(
        TextButton button,
        SimulationInputController inputController
    ) {
        if (!button.isChecked()) {
            button.setText("Follow: OFF");
        } else if (inputController.isFollowingObject()) {
            button.setText("Tracking");
        } else {
            button.setText("Select object");
        }
    }

    // Selects the given tool when checked and clears it only if it is still the selected tool.
    private static TextButton createToolButton(
        String label,
        SimulationInputController.Tool tool,
        Skin skin,
        SimulationInputController inputController
    ) {
        TextButton button = createToggleButton(label, skin);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (button.isChecked()) {
                    inputController.setSelectedTool(tool);
                } else if (inputController.getSelectedTool() == tool) {
                    inputController.setSelectedTool(SimulationInputController.Tool.NONE);
                }
            }
        });
        return button;
    }

    // Creates a button whose label reflects whether its toggle is on or off.
    private static TextButton createToggleButton(String label, Skin skin) {
        TextButton button = new TextButton(label + ": OFF", skin);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                button.setText(label + (button.isChecked() ? ": ON" : ": OFF"));
            }
        });
        return button;
    }
}
