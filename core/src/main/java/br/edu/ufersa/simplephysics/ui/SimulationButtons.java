package br.edu.ufersa.simplephysics.ui;

import br.edu.ufersa.simplephysics.math.Vector2;
import br.edu.ufersa.simplephysics.physics.PhysicsWorld;
import br.edu.ufersa.simplephysics.screen.SimulationInputController;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public final class SimulationButtons {
    private SimulationButtons() {
    }

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
