package com.Normangorman.HelloWorldGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.util.Random;

/**
 * Created by Ben on 25/04/2015.
 */
public class Mothership extends GameObject {
    private static final float moveSpeed = 80f;
    private static final int scoreValue = 100;
    private static final Random randomGen = new Random();
    private static Sound moveSound = Gdx.audio.newSound(Gdx.files.internal("sounds/mothership_move.wav"));
    private static Sound deathSound = Gdx.audio.newSound(Gdx.files.internal("sounds/mothership_death.wav"));
    private long moveSoundPlayingId;

    public Mothership() {
        super(0.0f, 0.0f, 48, 21);
        this.setAnimation(new SpriteAnimation("images/mothership_48x21.png", 48, 21));

        boolean whichSide = randomGen.nextBoolean();
        if (whichSide == false) {
            this.position.x = -48f;
            this.velocity.x = Mothership.moveSpeed;
        }
        else {
            this.position.x = Gdx.graphics.getWidth() + 48f;
            this.velocity.x = -1 * Mothership.moveSpeed;
        }
        this.position.y = Gdx.graphics.getHeight() - 48f;

        moveSoundPlayingId = moveSound.loop(0.25f);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        // Pan the playing sound in accordance with the ship's position on screen.
        float panAmount = (this.position.x / Gdx.graphics.getWidth()) * 2f - 1f;
        if (panAmount < -1f)
            panAmount = -1f;
        else if (panAmount > 1f)
            panAmount = 1f;

        moveSound.setPan(moveSoundPlayingId, panAmount, 0.25f);
    }

    // Called explicitly when the mothership goes offscreen.
    public void die() {
        this.setDead(true);
        moveSound.stop();
    }

    @Override
    public void handleCollision(GameObject other) {
        // Assume it's a player bullet
        deathSound.play();
        die();

        Player owner = (Player)((Projectile)other).getOwner();
        owner.addToScore(this.scoreValue);
    }
}
