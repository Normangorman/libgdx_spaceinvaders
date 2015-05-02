package com.Normangorman.HelloWorldGame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
/**
 * Created by Ben on 28/04/2015.
 */
public class MainMenuScreen implements Screen {
    private HelloWorldGame game;
    private Stage stage;
    private Skin skin;

    private TextureRegion alien1Sprite;
    private TextureRegion alien2Sprite;
    private TextureRegion alien3Sprite;
    private TextureRegion mothershipSprite;

    private ShaderProgram shader;
    private float iGlobalTime;
    private Texture tex;
    private SpriteBatch batch;

    public MainMenuScreen(HelloWorldGame g) {
        game = g;
        stage = new Stage();
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        iGlobalTime = 0f;

        Texture menuSpritesSpritesheet = new Texture("images/menu_sprites_32x32.png");
        TextureRegion[][] menuSprites = TextureRegion.split(menuSpritesSpritesheet, 32, 32);

        alien1Sprite = menuSprites[0][0];
        alien2Sprite = menuSprites[0][1];
        alien3Sprite = menuSprites[0][2];
        mothershipSprite = menuSprites[0][3];

        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(Gdx.files.internal("shaders/vertshader.glsl"), Gdx.files.internal("shaders/starnest_frag.glsl"));
        if (!shader.isCompiled()) {
            System.err.println(shader.getLog());
            System.exit(0);
        }

        batch = new SpriteBatch(1000, shader);
        batch.setShader(shader);

        tex = new Texture(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Pixmap.Format.RGBA8888);

        shader.begin();
        shader.setUniformf("iResolution", new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        shader.end();

    }

    @Override
    public void resize(int width, int height) {
        stage.clear();

        //System.out.printf("resize called with with=%d, height=%d", width, height);
        float w = (float)width;
        float h = (float)height;

        // Title
        Label title = new Label("Space\nInvaders", skin);
        float titleWidth = 320f;
        float titleHeight = 80f;
        title.setWidth(titleWidth);
        title.setHeight(titleHeight);
        title.setStyle(new Label.LabelStyle(skin.getFont("title-font"), new Color(1f, 1f, 1f, 1f)));
        title.setAlignment(Align.bottomLeft, Align.center);
        title.setPosition(0 - titleWidth, h - titleHeight - 100f);

        // Scores
        Image alien1Image = new Image(alien1Sprite);
        Image alien2Image = new Image(alien2Sprite);
        Image alien3Image = new Image(alien3Sprite);
        Image mothershipImage = new Image(mothershipSprite);

        Label alien1Label = new Label("= 40 points", skin);
        Label alien2Label = new Label("= 20 points", skin);
        Label alien3Label = new Label("= 10 points", skin);
        Label mothershipLabel = new Label("= ?? points", skin);

        Table scoreTable = new Table();
        float hPadding = 5f;
        float vPadding = 10f;
        scoreTable.add(alien1Image).padRight(hPadding);
        scoreTable.add(alien1Label);
        scoreTable.row().padTop(vPadding);
        scoreTable.add(alien2Image).padRight(hPadding);
        scoreTable.add(alien2Label);
        scoreTable.row().padTop(vPadding);
        scoreTable.add(alien3Image).padRight(hPadding);
        scoreTable.add(alien3Label);
        scoreTable.row().padTop(vPadding);
        scoreTable.add(mothershipImage).padRight(hPadding);
        scoreTable.add(mothershipLabel);

        scoreTable.setWidth(w / 2f);
        scoreTable.setHeight(200f);
        scoreTable.setPosition(w / 2f - scoreTable.getWidth() / 2f, title.getY() - 10f - scoreTable.getHeight());
        Color c = scoreTable.getColor();
        c.a = 0f;
        scoreTable.setColor(c); // Set the alpha to 0 in preparation for the animation.

        // Play button
        final Label playButton = new Label("Play!", skin);
        playButton.setPosition(w, scoreTable.getY() - 10f - playButton.getHeight());
        playButton.addListener(new InputListener() {
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                playButton.setColor(0f, 1f, 0f, 1f);
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                playButton.setColor(1f, 1f, 1f, 1f);
            }

            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.startGame();
                return true;
            }
        });

        // Animations
        Action titleMove = moveTo(w / 2f - titleWidth / 2f, title.getY(), 0.5f, Interpolation.pow3);
        titleMove.setTarget(title);

        Action scoreTableFadeIn = alpha(1.0f, 0.5f);
        scoreTableFadeIn.setTarget(scoreTable);

        Action playButtonMove = moveTo(w / 2f - playButton.getWidth() / 2f, playButton.getY(), 0.5f, Interpolation.pow3);
        playButtonMove.setTarget(playButton);

        final Action playButtonBlink = forever(sequence(alpha(0f), delay(0.8f), alpha(1f), delay(1.2f)));
        playButtonBlink.setTarget(playButton);

        Action seqActions = sequence(titleMove, playButtonMove, scoreTableFadeIn, new RunnableAction() {
            // This is done so the play button only starts blinking after entry.
            @Override
            public void run() {
                playButton.addAction(playButtonBlink);
            }
        });

        stage.addActor(title);
        stage.addActor(scoreTable);
        stage.addActor(playButton);
        stage.addAction(seqActions);
        //stage.setDebugAll(true);
    }

    @Override
    public void render(float dt) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        iGlobalTime += dt;

        float mouseX = (float)Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - (float)Gdx.input.getY();
        //System.out.printf("mouseX: %f, mouseY: %f\n", mouseX, mouseY);

        batch.begin();
            shader.setUniformf("iGlobalTime", iGlobalTime);
            shader.setUniformf("iMouse", new Vector2(mouseX, mouseY));
            batch.draw(tex, 0, 0);
        batch.end();

        stage.act(dt);
        stage.draw();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    // Necessary so all the methods of the Screen interface are implemented.
    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}
}
