package br.edu.ufersa.simplephysics.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;

public final class ScrollablePanel {
    private final Stage stage;
    private final Viewport viewport;
    private final ScrollPane scrollPane;
    private final InputProcessor inputProcessor;

    public ScrollablePanel(
        Stage stage,
        Viewport viewport,
        Actor content,
        Skin skin,
        InputProcessor fallbackInputProcessor
    ) {
        this.stage = stage;
        this.viewport = viewport;
        scrollPane = createScrollPane(content, skin);
        inputProcessor = createInputProcessor(fallbackInputProcessor);
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    public InputProcessor getInputProcessor() {
        return inputProcessor;
    }

    private ScrollPane createScrollPane(
        Actor content,
        Skin skin
    ) {
        NinePatchDrawable scrollTrack = new NinePatchDrawable(
            (NinePatchDrawable) skin.getDrawable("scrollbar-track")
        );
        NinePatchDrawable scrollKnob = new NinePatchDrawable(
            (NinePatchDrawable) skin.getDrawable("scrollbar-knob")
        );
        scrollTrack.setMinWidth(10);
        scrollKnob.setMinWidth(10);

        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        scrollPaneStyle.vScroll = scrollTrack;
        scrollPaneStyle.vScrollKnob = scrollKnob;

        ScrollPane pane = new ScrollPane(content, scrollPaneStyle);
        pane.setScrollingDisabled(true, false);
        pane.setFadeScrollBars(false);
        return pane;
    }

    private InputProcessor createInputProcessor(InputProcessor fallbackInputProcessor) {
        return new InputAdapter() {
            @Override
            public boolean scrolled(float amountX, float amountY) {
                if (isPointerOverViewport()) {
                    stage.setScrollFocus(scrollPane);
                    stage.scrolled(amountX, amountY);
                    return true;
                }

                stage.setScrollFocus(null);
                return fallbackInputProcessor.scrolled(amountX, amountY);
            }
        };
    }

    private boolean isPointerOverViewport() {
        int screenX = Gdx.input.getX();
        int screenYFromBottom = Gdx.graphics.getHeight() - Gdx.input.getY();
        return screenX >= viewport.getScreenX()
            && screenX < viewport.getScreenX() + viewport.getScreenWidth()
            && screenYFromBottom >= viewport.getScreenY()
            && screenYFromBottom < viewport.getScreenY() + viewport.getScreenHeight();
    }
}
