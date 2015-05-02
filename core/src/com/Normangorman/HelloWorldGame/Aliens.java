package com.Normangorman.HelloWorldGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

/**
 * Created by Ben on 29/04/2015.
 */
public class Aliens {
    protected static float alienInitX = Gdx.graphics.getWidth() / 2f - 11f * 32f / 2f;
    protected static float alienInitY = Gdx.graphics.getHeight() - 80f;
    protected static float alienGameOverY = 122f;
    protected static float alienMinX = 16f;
    protected static float alienMaxX = Gdx.graphics.getWidth() - 16f;
    protected static float alienShootChance = 0.002f;
    protected static float alienMoveSpeed = 16f;
    protected static float alienBulletSpeed = -200f;
    protected static Sound alienDeathSound = Gdx.audio.newSound(Gdx.files.internal("sounds/alien_death_sound.wav"));

    private static Sound alienReachedBottomSound = Gdx.audio.newSound(Gdx.files.internal("sounds/alien_reached_bottom_sound.wav"));

    private GameScreen gameScreen;

    private Aliens.Alien[][] aliens;
    private int numAliens;
    private GridPoint2 collisionAlienPosition; // save the position of the last alien collided with so that if handleCollision is called we know which Alien it is.
    private boolean aliensReachedBottom;

    private Sound[] alienMoveSounds;
    private int alienMoveSoundIndex;

    private final float alienInitUpdateDelay = 1.0f; // the reference point from which alienUpdateDelay is calculated.
    private float alienUpdateDelay = alienInitUpdateDelay; // this is variable and scales linearly with the number of dead aliens.
    private float alienUpdateTimer = alienUpdateDelay; // when this hits 0 the aliens are moved.

    private Random randomGen;

    private int currentMoveDirection; // -1 for left, 1 for right.
    private boolean moveDown; // this is set to true when the aliens reach either side of the screen.

    public Aliens(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        aliens = new Aliens.Alien[5][11];
        numAliens = 55;
        aliensReachedBottom = false;
        alienMoveSounds = new Sound[4];
        alienMoveSounds[0] = Gdx.audio.newSound(Gdx.files.internal("sounds/alien_move_1.wav"));
        alienMoveSounds[1] = Gdx.audio.newSound(Gdx.files.internal("sounds/alien_move_2.wav"));
        alienMoveSounds[2] = Gdx.audio.newSound(Gdx.files.internal("sounds/alien_move_3.wav"));
        alienMoveSounds[3] = Gdx.audio.newSound(Gdx.files.internal("sounds/alien_move_4.wav"));

        alienMoveSoundIndex = 0;
        randomGen = new Random();
        currentMoveDirection = 1;
        moveDown = false;

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 11; j++) {
                Aliens.Alien alien;
                float x = alienInitX + 32f * (float)j;
                float y = alienInitY - 32f * (float)i;

                if (i == 0)
                    alien = new Aliens.Alien1(x, y);
                else if (i == 1 || i == 2)
                    alien = new Aliens.Alien2(x, y);
                else
                    alien = new Aliens.Alien3(x, y);

                aliens[i][j] = alien;
            }
        }

    }

    public void update(float dt) {
        boolean changeDirection = false;

        // Prune dead aliens, make them shoot, and work out whether to change direction. Don't move them yet.
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 11; j++) {
                Aliens.Alien alien = aliens[i][j];
                if (alien == null)
                    continue;
                else if (alien.isDead()) {
                    aliens[i][j] = null;
                    numAliens--;
                    // Scales linearly with number of dead aliens, down to 0.25 of the initial value when none remaining.
                    alienUpdateDelay = alienInitUpdateDelay * (0.75f * ((float)numAliens/55f) + 0.25f);

                    if (numAliens == 0) {
                        Gdx.app.debug("Aliens", "All aliens dead!");
                    }
                    continue;
                }

                // Work out whether the alien can shoot (i.e. there are no aliens below it)
                boolean canShoot = true;
                if (i != 4)
                    for (int rowNum = i+1; rowNum < 5; rowNum++)
                        if (aliens[rowNum][j] != null)
                            canShoot = false;

                if (canShoot && randomGen.nextFloat() < alienShootChance) {
                    gameScreen.addProjectile(alien.shoot());
                }

                if (currentMoveDirection == -1 && alien.position.x <= alienMinX ||
                    currentMoveDirection == 1 && alien.position.x >= alienMaxX) {
                    moveDown = true;
                    currentMoveDirection *= -1;
                }
            }
        }

        // Update the frame timer. If it is 0, move all the aliens.
        alienUpdateTimer -= dt;
        if (alienUpdateTimer <= 0f) {
            alienUpdateTimer = alienUpdateDelay;

            moveAliens();
            moveDown = false;
        }
    }

    private void moveAliens() {
        alienMoveSounds[alienMoveSoundIndex].play();
        alienMoveSoundIndex = (alienMoveSoundIndex + 1) % 4;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 11; j++) {
                Aliens.Alien alien = aliens[i][j];
                if (alien == null)
                    continue;

                if (moveDown) {
                    alien.move(0);
                    if (alien.position.y <= alienGameOverY) {
                        aliensReachedBottom = true;
                        Aliens.alienReachedBottomSound.play();
                    }
                }
                else
                    alien.move(currentMoveDirection);
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 11; j++) {
                Aliens.Alien alien = aliens[i][j];
                if (alien != null)
                    alien.render(batch);
            }
        }
    }

    public boolean collides(GameObject other) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 11; j++) {
                Aliens.Alien alien = aliens[i][j];
                if (alien != null && alien.collides(other)) {
                    collisionAlienPosition = new GridPoint2(j, i);
                    return true;
                }
            }
        }
        return false;
    }

    public void handleCollision(GameObject other) {
        assert(collisionAlienPosition != null);
        int row = collisionAlienPosition.y;
        int col = collisionAlienPosition.x;

        Aliens.Alien alien = aliens[row][col];
        assert(alien != null);
        alien.handleCollision(other);

        collisionAlienPosition = null;
    }

    public boolean areAliensAtBottom() {
        return aliensReachedBottom;
    }

    public boolean areAllAliensDead() {
        return numAliens == 0;
    }

    class Alien extends GameObject {
        protected int scoreValue;

        public Alien(float x, float y, int width, int height) {
            super(x,y,width,height);
        }

        public Projectile shoot() {
            Projectile bullet = new Projectile(this, position.x, position.y - height, 3, 8);
            bullet.setAnimation(new SpriteAnimation("images/alien_bullet_4x8.png", 4, 8));
            bullet.velocity.y = Aliens.alienBulletSpeed;
            return bullet;
        }

        public void move(int direction) {
            position.x += direction * Aliens.alienMoveSpeed;
            if (direction == 0) {
                this.position.y -= Aliens.alienMoveSpeed * 2f;
            }

            animation.forceNextFrame();
        }

        @Override
        public void handleCollision(GameObject other) {
            if (other instanceof Projectile) {
                Gdx.app.log("Alien", "Alien died!");
                this.setDead(true);
                Aliens.alienDeathSound.play();

                GameObject owner = ((Projectile)other).getOwner();
                if (owner instanceof Player)
                    ((Player)owner).addToScore(this.getScoreValue());
            }
        }

        public int getScoreValue() {
            return this.scoreValue;
        }
    }

    class Alien1 extends Aliens.Alien {
        public Alien1(float x, float y) {
            super(x,y,16,16);
            setAnimation(new SpriteAnimation("images/alien1_16x16.png", 16, 16));
            this.scoreValue = 40;
        }
    }

    class Alien2 extends Aliens.Alien {
        public Alien2(float x, float y) {
            super(x, y, 22, 16);
            setAnimation(new SpriteAnimation("images/alien2_22x16.png", 22, 16));
            this.scoreValue = 20;
        }
    }

    class Alien3 extends Aliens.Alien {
        public Alien3(float x, float y) {
            super(x, y, 24, 16);
            setAnimation(new SpriteAnimation("images/alien3_24x16.png", 24, 16));
            this.scoreValue = 10;
        }
    }
}
