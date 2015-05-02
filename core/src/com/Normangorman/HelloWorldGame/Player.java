package com.Normangorman.HelloWorldGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Ben on 23/04/2015.
 */
public class Player extends GameObject {
    private static final float moveSpeed = 140f;
    private static final float bulletSpeed = 250f;
    private static final float initialX = Gdx.graphics.getWidth() / 2f;
    private static final float initialY = 16f + 10f;
    private static final Sound deathSound = Gdx.audio.newSound(Gdx.files.internal("sounds/player_death_sound.wav"));
    private static final SpriteAnimation aliveAnimation = new SpriteAnimation("images/player_26x16.png", 26, 16);
    private static final SpriteAnimation deathAnimation = new SpriteAnimation("images/player_death_26x16.png", 26, 16);

    public int lives;
    public int score;

    private boolean readyToShoot;
    private boolean dying; // actual death is delayed to give the death animation time to complete.

    public Player() {
        super(initialX, initialY, 26, 16);
        lives = 3;
        score = 0;
        readyToShoot = false;
        dying = true;

        setAnimation(aliveAnimation);
        deathAnimation.setSpeed(0.5f);
        deathAnimation.setLooping(false);
    }

    @Override
    public void update(float dt) {
        readyToShoot = false;

        if (!dying) {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
                velocity.x = -Player.moveSpeed;
            else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
                velocity.x = Player.moveSpeed;

            if (Gdx.input.isKeyPressed(Input.Keys.SPACE))
                readyToShoot = true;

            super.update(dt);
            velocity.scl(0);
        }
        else {
            this.animation.update(dt); // the death animation.
        }
    }

    public void handleCollision(GameObject other) {
        if (other instanceof Projectile) {
            die();
        }
    }

    public void die() {
        lives--;
        Gdx.app.log("Player", "Player died!");
        deathSound.play();
        dying = true;
        this.setAnimation(deathAnimation);
    }

    public void reset() {
        readyToShoot = false;
        dying = false;
        position.x = initialX;
        position.y = initialY;
        setAnimation(aliveAnimation);
    }

    public void addToScore(int amount) {
        score += amount;
    }

    public Projectile shoot() {
        Projectile bullet = new Projectile(this, this.position.x, this.position.y + 16f, 4, 8);
        bullet.velocity.y = bulletSpeed;
        bullet.setAnimation(new SpriteAnimation("images/player_bullet_4x8.png", 4, 8));
        return bullet;
    }

    public boolean isReadyToShoot() { return readyToShoot; }
    public boolean isDying() { return dying; }
}
