package data.scripts.thedudes;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseCombatLayeredRenderingPlugin;
import com.fs.starfarer.api.combat.CombatEngineLayers;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.EnumSet;
import org.lwjgl.opengl.GL11;

// racoon don't hire hitmen to eliminate me please i swear i'll learn how this rendering stuff truly works so i can write the exact same thing in a different way

public class AuraCircleAttemptOne extends BaseCombatLayeredRenderingPlugin {
    protected SpriteAPI atmosphereTex;
    public AuraCircleAttemptOne.AuraParams p;
    protected int segments;

    public AuraCircleAttemptOne(AuraCircleAttemptOne.AuraParams p) {
        this.p = p;
    }

    public float getRenderRadius() {
        return this.p.radius + 500.0F;
    }

    public EnumSet<CombatEngineLayers> getActiveLayers() {
        return EnumSet.of(CombatEngineLayers.BELOW_SHIPS_LAYER);
    }

    public void render(CombatEngineLayers layer, ViewportAPI viewport) {
        if (this.p.ship != null) {
            if (this.p.ship.isAlive()) {
                float x = this.p.ship.getLocation().x;
                float y = this.p.ship.getLocation().y;
                float r = this.p.radius;
                float tSmall = this.p.thickness;
                float a = 1.0F;
                if (this.p.ship.getOwner() == 0) {
                    if (this.p.ship.equals(Global.getCombatEngine().getPlayerShip())) {
                        a = this.p.playerAlpha;
                    } else {
                        a = this.p.allyAlpha;
                    }
                } else {
                    a = this.p.enemyAlpha;
                }

                a *= this.p.baseAlpha;
                if (layer == CombatEngineLayers.BELOW_SHIPS_LAYER) {
                    this.renderAtmosphere(x, y, r, 20.0F, a, this.segments, this.atmosphereTex, this.p.color, false);
                }

            }
        }
    }

    public void init(CombatEntityAPI entity) {
        super.init(entity);
        this.atmosphereTex = Global.getSettings().getSprite("combat", "corona_soft");
        float perSegment = 2.0F;
        this.segments = (int)(this.p.radius * 2.0F * 3.14F / perSegment);
        if (this.segments < 8) {
            this.segments = 8;
        }

    }

    public void advance(float amount) {
        if (this.p.ship != null) {
            this.entity.getLocation().set(this.p.ship.getLocation().x, this.p.ship.getLocation().y);
        }

    }

    private void renderAtmosphere(float x, float y, float radius, float thickness, float alphaMult, int segments, SpriteAPI tex, Color color, boolean additive) {
        float startRad = (float)Math.toRadians(0.0D);
        float endRad = (float)Math.toRadians(360.0D);
        float spanRad = Misc.normalizeAngle(endRad - startRad);
        float anglePerSegment = spanRad / (float)segments;
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0.0F);
        GL11.glRotatef(0.0F, 0.0F, 0.0F, 1.0F);
        GL11.glEnable(3553);
        tex.bindTexture();
        GL11.glEnable(3042);
        if (additive) {
            GL11.glBlendFunc(770, 1);
        } else {
            GL11.glBlendFunc(770, 771);
        }

        GL11.glColor4ub((byte)color.getRed(), (byte)color.getGreen(), (byte)color.getBlue(), (byte)((int)((float)color.getAlpha() * alphaMult)));
        float texX = 0.0F;
        float incr = 1.0F / (float)segments;
        GL11.glBegin(8);

        for(float i = 0.0F; i < (float)(segments + 1); ++i) {
            boolean last = i == (float)segments;
            if (last) {
                i = 0.0F;
            }

            float theta = anglePerSegment * i;
            float cos = (float)Math.cos((double)theta);
            float sin = (float)Math.sin((double)theta);
            float m1 = 1.0F;
            float m2 = 1.0F;
            float x1 = cos * radius * m1;
            float y1 = sin * radius * m1;
            float x2 = cos * (radius + thickness * m2);
            float y2 = sin * (radius + thickness * m2);
            GL11.glTexCoord2f(0.5F, 0.05F);
            GL11.glVertex2f(x1, y1);
            GL11.glTexCoord2f(0.5F, 0.95F);
            GL11.glVertex2f(x2, y2);
            texX += incr;
            if (last) {
                break;
            }
        }

        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public boolean isExpired() {
        return this.p.ship == null;
    }

    public static class AuraParams implements Cloneable {
        public float radius = 20.0F;
        public float thickness = 25.0F;
        public float baseAlpha = 1.0F;
        public float playerAlpha = 1.0F;
        public float allyAlpha = 0.5F;
        public float enemyAlpha = 0.5F;
        public ShipAPI ship = null;
        public Color color = new Color(100, 100, 255);

        public AuraParams() {
        }

        public AuraParams(ShipAPI ship, float radius, float thickness, Color color) {
            this.radius = radius;
            this.thickness = thickness;
            this.color = color;
            this.ship = ship;
        }

        protected AuraCircleAttemptOne.AuraParams clone() {
            try {
                return (AuraCircleAttemptOne.AuraParams)super.clone();
            } catch (CloneNotSupportedException var2) {
                return null;
            }
        }
    }
}