package br.edu.ufersa.simplephysics;

import br.edu.ufersa.simplephysics.screen.ExampleScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class Main extends Game {
    @Override
    public void create() {
        setScreen(new ExampleScreen());
    }

    @Override
    public void dispose() {
        Screen currentScreen = getScreen();
        super.dispose();
        if (currentScreen != null) currentScreen.dispose();
    }
}
