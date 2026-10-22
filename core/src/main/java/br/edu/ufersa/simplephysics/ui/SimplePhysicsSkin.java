package br.edu.ufersa.simplephysics.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public final class SimplePhysicsSkin {

    // Palette.
    private static final Color BACKGROUND = Color.valueOf("242424");
    private static final Color PANEL = Color.valueOf("2d2d2d");
    private static final Color SIMULATION = Color.valueOf("fefefe");
    private static final Color GRID = Color.valueOf("afafaf");
    private static final Color AXIS = Color.valueOf("686868");

    // Derived UI colors.
    private static final Color TEXT = Color.valueOf("e6e6e6");
    private static final Color TEXT_DISABLED = Color.valueOf("6a6a6a");
    private static final Color BUTTON_UP = Color.valueOf("3a3a3a");
    private static final Color BUTTON_OVER = Color.valueOf("464646");
    private static final Color BUTTON_DOWN = Color.valueOf("262626");

    private static final int CORNER_RADIUS = 10;
    private static final int PATCH_SIZE = CORNER_RADIUS * 2 + 4;

    private static final String FONT_DIR = "fonts/";

    private SimplePhysicsSkin() {}

    public static Skin create() {
        Skin skin = new Skin();

        // Fonts.
        BitmapFont bodyFont = generateFont("FiraCode-Regular.ttf", 16, TEXT);
        BitmapFont buttonFont = generateFont("FiraCode-SemiBold.ttf", 16, TEXT);

        skin.add("default-font", bodyFont);
        skin.add("button-font", buttonFont);

        // Label style.
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = bodyFont;
        labelStyle.fontColor = TEXT;
        skin.add("default", labelStyle);

        // Panel background.
        skin.add("panel", rounded(skin, "panel-texture", PANEL, CORNER_RADIUS));

        // Button style.
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = rounded(skin, "button-up-texture", BUTTON_UP, CORNER_RADIUS);
        buttonStyle.over = rounded(skin, "button-over-texture", BUTTON_OVER, CORNER_RADIUS);
        buttonStyle.down = rounded(skin, "button-down-texture", BUTTON_DOWN, CORNER_RADIUS);
        buttonStyle.disabled = buttonStyle.down;

        buttonStyle.font = buttonFont;
        buttonStyle.fontColor = TEXT;
        buttonStyle.overFontColor = TEXT;
        buttonStyle.downFontColor = TEXT;
        buttonStyle.disabledFontColor = TEXT_DISABLED;

        skin.add("default", buttonStyle);

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = bodyFont;
        textFieldStyle.fontColor = TEXT;
        textFieldStyle.cursor = rounded(skin, "text-field-cursor", TEXT, 0);
        textFieldStyle.selection = rounded(skin, "text-field-selection", BUTTON_OVER, 4);
        textFieldStyle.background = rounded(skin, "text-field-background", BUTTON_UP, CORNER_RADIUS);
        textFieldStyle.focusedBackground = rounded(
            skin,
            "text-field-focused-background",
            BUTTON_OVER,
            CORNER_RADIUS
        );
        skin.add("default", textFieldStyle);

        return skin;
    }

    private static BitmapFont generateFont(String file, int size, Color color) {
        FreeTypeFontGenerator generator =
            new FreeTypeFontGenerator(Gdx.files.internal(FONT_DIR + file));

        try {
            FreeTypeFontParameter parameter = new FreeTypeFontParameter();
            parameter.size = size;
            parameter.color = color;
            parameter.characters += "\u2191\u2193";
            parameter.minFilter = Texture.TextureFilter.Linear;
            parameter.magFilter = Texture.TextureFilter.Linear;

            // Renders crisp text on HiDPI displays too.
            return generator.generateFont(parameter);
        } finally {
            generator.dispose();
        }
    }

    private static Drawable rounded(Skin skin, String textureName, Color color, int radius) {
        Texture texture = createRoundedTexture(PATCH_SIZE, radius, color);
        skin.add(textureName, texture);

        NinePatch patch = new NinePatch(
            new TextureRegion(texture),
            radius, radius, radius, radius
        );

        return new NinePatchDrawable(patch);
    }

    private static Texture createRoundedTexture(int size, int radius, Color color) {
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);

        try {
            pixmap.setBlending(Pixmap.Blending.None);

            pixmap.setColor(0, 0, 0, 0);
            pixmap.fill();

            pixmap.setColor(color);
            pixmap.fillRectangle(radius, 0, size - 2 * radius, size);
            pixmap.fillRectangle(0, radius, size, size - 2 * radius);

            pixmap.fillCircle(radius, radius, radius);
            pixmap.fillCircle(size - radius - 1, radius, radius);
            pixmap.fillCircle(radius, size - radius - 1, radius);
            pixmap.fillCircle(size - radius - 1, size - radius - 1, radius);

            Texture texture = new Texture(pixmap);
            texture.setFilter(
                Texture.TextureFilter.Linear,
                Texture.TextureFilter.Linear
            );

            return texture;
        } finally {
            pixmap.dispose();
        }
    }
}
