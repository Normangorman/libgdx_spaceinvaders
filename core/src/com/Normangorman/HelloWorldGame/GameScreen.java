package com.Normangorman.HelloWorldGame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by Ben on 25/04/2015.
 */
public class GameScreen implements Screen {
    private float motherShipSpawnChance = 0.0015f;
    private float stateTransitionTimer; // used to create a delay when changing between states.
    private HelloWorldGame game;
    private Stage stage;
    private SpriteBatch batch;
    private SpriteAnimation playerLifeSprite;
    private BitmapFont font;
    private Random randomGen;

    private Mothership mothership;
    private Aliens aliens;
    private Player player;
    private Barrier[] barriers;
    private Projectile playerBullet;
    private List<Projectile> bullets;

    private enum GameState {
        PLAYING,
        PLAYER_DYING,
        PLAYER_WON,
        GAME_OVER,
        PAUSED
    }
    GameState currentGameState;
    private boolean paused;

    public GameScreen(HelloWorldGame game) {
        this.game = game;
        stage = new Stage();
        batch = new SpriteBatch();
        playerLifeSprite = new SpriteAnimation("images/player_26x16.png", 26, 16);
        font = new BitmapFont(Gdx.files.internal("fonts/PressStart2P.fnt"));
        randomGen = new Random();
        player = new Player();
        paused = false;

        reset();
    }

    private void reset() {
        currentGameState = GameState.PLAYING;
        player.reset();
        aliens = new Aliens(this);
        barriers = new Barrier[4];
        bullets = new ArrayList<Projectile>();
        if (mothership != null) {
            mothership.die(); // stop the music.
            mothership = null;
        }

        for (int i = 0; i < 4; i++) {
            float x = (int)(Gdx.graphics.getWidth() / 9f) * (1+i*2) + Barrier.width/2;
            float y = 72f;
            barriers[i] = new Barrier(x, y);
        }
    }

    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.P))
            paused = !paused;

        if (paused) {
            if (Gdx.input.isTouched())
                paused = false;
            else
                return;
        }

        switch(currentGameState) {
            case PLAYING:
                // Handle aliens
                aliens.update(dt);

                // Handle player
                player.update(dt);
                if (playerBullet == null && player.isReadyToShoot()) {
                    Gdx.app.debug("GameScreen:update", "Adding player bullet to game entities.");
                    playerBullet = player.shoot();
                }

                // Handle barriers
                for (int i = 0; i < 4; i++) {
                    Barrier b = barriers[i];
                    if (b == null)
                        continue;

                    if (b.isDead())
                        barriers[i] = null;
                }

                // Handle bullets
                Stack<Integer> indicesToRemove = new Stack();
                for (int i = 0; i < bullets.size(); i++) {
                    Projectile bullet = bullets.get(i);
                    bullet.update(dt);
                    if (bullet.isDead())
                        indicesToRemove.push(i);
                }
                while (!indicesToRemove.empty()) {
                    int i = indicesToRemove.pop();
                    bullets.remove(i);
                }

                // Handle player bullet
                if (playerBullet != null) {
                    playerBullet.update(dt);
                    if (playerBullet.isOffScreen()) {
                        Gdx.app.debug("GameScreen:update", "Nulling player bullet because it was offscreen.");
                        playerBullet = null;
                    }
                }

                // Handle mothership
                if (mothership == null) {
                    if (randomGen.nextFloat() < motherShipSpawnChance) {
                        Gdx.app.debug("GameScreen update", "Spawning a mothership.");
                        mothership = new Mothership();
                    }
                }
                else if (mothership.isDead())
                    mothership = null;
                else {
                    mothership.update(dt);
                    if (mothership.velocity.x > 0 && mothership.position.x >= Gdx.graphics.getWidth() + mothership.width/2f ||
                            mothership.velocity.x < 0 && mothership.position.x <= 0 - mothership.width/2f) {
                        mothership.die();
                        mothership = null;
                    }
                }

                handleCollisions();

                if (aliens.areAllAliensDead()) {
                    currentGameState = GameState.PLAYER_WON;
                    stateTransitionTimer = 1.5f;
                    break;
                }

                if (aliens.areAliensAtBottom()) {
                    player.die();
                }

                if (player.lives == 0) {
                    currentGameState = GameState.GAME_OVER;
                    stateTransitionTimer = 3f;
                }
                else if (aliens.areAliensAtBottom() || player.isDying()) {
                    currentGameState = GameState.PLAYER_DYING;
                    stateTransitionTimer = 1.5f;
                }

                break;

            case PLAYER_DYING:
                if (mothership != null) {
                    mothership.die();
                    mothership = null;
                }
                stateTransitionTimer -= dt;
                if (stateTransitionTimer <= 0) {
                    reset();
                }
                break;

            case PLAYER_WON:
                if (mothership != null) {
                    mothership.die();
                    mothership = null;
                }
                stateTransitionTimer -= dt;
                if (stateTransitionTimer <= 0) {
                    reset();
                }
                break;

            case GAME_OVER:
                if (mothership != null) {
                    mothership.die();
                    mothership = null;
                }
                stateTransitionTimer -= dt;
                if (stateTransitionTimer <= 0) {
                    game.startMenu();
                }
                break;
        }
    }

    public void render(float dt) {
        update(dt);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();

        if (mothership != null)
            mothership.render(batch);

        aliens.render(batch);

        player.render(batch);

        for (int i = 0; i < 4; i++) {
            Barrier b = barriers[i];
            if (b != null)
                b.render(batch);
        }

        if (currentGameState == GameState.PLAYING) {
            for (Projectile bullet : bullets)
                bullet.render(batch);

            if (playerBullet != null)
                playerBullet.render(batch);
        }

        float uiInitX = 10f;
        float uiInitY = Gdx.graphics.getHeight() - 10f;
        font.draw(batch, "Score: " + Integer.toString(player.score), uiInitX, uiInitY);
        font.draw(batch, "Lives:", Gdx.graphics.getWidth() / 2f, uiInitY );
        for (int i = 0; i < player.lives; i++) {
            playerLifeSprite.render(batch, Gdx.graphics.getWidth() / 2f + (float)(i+3) * 32f, uiInitY - 12f);
        }

        if (paused) {
            GlyphLayout pausedTextLayout = new GlyphLayout(font, "PAUSED");
            float textWidth = pausedTextLayout.width;
            float textHeight = pausedTextLayout.height;
            font.draw(batch, pausedTextLayout, Gdx.graphics.getWidth() / 2f - textWidth / 2f, Gdx.graphics.getHeight() / 2f - textHeight / 2f);
        }
        else if (currentGameState == GameState.GAME_OVER) {
            GlyphLayout gameOverTextLayout = new GlyphLayout(font, "GAME OVER!");
            float textWidth = gameOverTextLayout.width;
            float textHeight = gameOverTextLayout.height;
            font.draw(batch, gameOverTextLayout, Gdx.graphics.getWidth() / 2f - textWidth / 2f, Gdx.graphics.getHeight() / 2f - textHeight / 2f);
        }

        batch.end();
    }

    public void addProjectile(Projectile p) {
        bullets.add(p);
    }

    private void handleCollisions() {
        for (int i = 0; i < bullets.size(); i++) {
            Projectile bullet = bullets.get(i);

            if (bullet.collides(player)) {
                Gdx.app.debug("handleCollisions", "Bullet hit player.");
                bullet.setDead(true);
                player.handleCollision(bullet);
                continue;
            }

            boolean hitABarrier = false;
            for(Barrier b : barriers) {
                if (b != null && bullet.collides(b)) {
                    bullet.setDead(true);
                    Gdx.app.debug("handleCollisions", "Bullet hit barrier.");
                    b.handleCollision(bullet);
                    hitABarrier = true;
                }
            }
            if (hitABarrier)
                continue;
        }

        // Player bullet:
        if (playerBullet != null) {
            boolean hitSomething = false;
            if (aliens.collides(playerBullet)) {
                aliens.handleCollision(playerBullet);
                hitSomething = true;
            }

            if (mothership != null && playerBullet.collides(mothership)) {
                mothership.handleCollision(playerBullet);
                hitSomething = true;
            }

            for(Barrier b : barriers) {
                if (b != null && playerBullet.collides(b)) {
                    hitSomething = true;
                    b.handleCollision(playerBullet);
                    break;
                }
            }

            for(Projectile b : bullets) {
                if (playerBullet.collides(b)) {
                    hitSomething = true;
                    b.setDead(true);
                    playerBullet.playDeathSound();
                    break;
                }
            }

            if (hitSomething)
                playerBullet = null;
        }
    }


    @Override
    public void resize(int width, int height) {}

    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();
    }

    @Override
    public void hide() {}

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }
}
