package com.Normangorman.HelloWorldGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;

/**
 * Created by Ben on 25/04/2015.
 */
public class Projectile extends GameObject {
    public static final float animationSpeed = 0.1f;
    private GameObject owner;

    private final Sound deathSound = Gdx.audio.newSound(Gdx.files.internal("sounds/bullet_death_sound.wav"));

    public Projectile(GameObject owner, float x, float y, int width, int height) {
        super(x,y,width,height);
        this.owner = owner;
    }

    @Override
    public void setAnimation(SpriteAnimation s) {
        s.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        s.setSpeed(Projectile.animationSpeed);
        super.setAnimation(s);
    }

    public void setOwner(GameObject o) {
        owner = o;
    }

    public GameObject getOwner() {
        return owner;
    }

    public void playDeathSound() {
        deathSound.play();
    }
}
