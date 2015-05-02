package com.Normangorman.HelloWorldGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

/**
 * Created by Ben on 26/04/2015.
 */
public class Barrier extends GameObject {
    public static final int width = 44;
    public static final int height = 32;
    private static final Sound damageSound = Gdx.audio.newSound(Gdx.files.internal("sounds/barrier_damage_sound.wav"));

    public int health = 5;

    public Barrier(float x, float y) {
        super(x,y,44,32);
        this.setAnimation(new SpriteAnimation("images/barrier_44x32.png", 44, 32));
    }

    public void handleCollision(GameObject other) {
        if (other instanceof Projectile) {
            health--;
            damageSound.play();
            if (health == 0)
                this.setDead(true);
            else
                this.animation.forceNextFrame();
        }
    }
}
