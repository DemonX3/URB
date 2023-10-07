package data.scripts.ungprules.impl.combat;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.combat.listeners.HullDamageAboutToBeTakenListener;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_CombatTag;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;

import java.awt.*;

public class UNGPDX_SkeletonKing extends UNGP_BaseRuleEffect implements UNGP_CombatTag {

    @Override
    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
    }

    @Override
    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        return 0;
    }

    public class Wraith implements AdvanceableListener, HullDamageAboutToBeTakenListener {
        public ShipAPI ship;
        public boolean Wraith = true;
        public boolean Reincarnated = false;
        public boolean Active = false;

        public Wraith(ShipAPI ship) {
            this.ship = ship;
        }

        public boolean notifyAboutToTakeHullDamage(Object param, ShipAPI ship, Vector2f point, float damageAmount) {
            if (!Active) {
                float hull = ship.getHitpoints();
                if (damageAmount >= hull && Wraith) {
                    CombatEngineAPI engine = Global.getCombatEngine();
                    Reincarnated = true;
                    Active = true;
                    Wraith = false;

                    Color color1 = new Color(255, 139, 25, 168);
                    Color color2 = new Color(255, 94, 25, 168);
                    Color color3 = new Color(255, 36, 36, 181);

                    Global.getSoundPlayer().playSound("deathless_reincarnate", 1f, 1f, ship.getLocation(), Misc.ZERO);
                    engine.addFloatingText(ship.getLocation(), "My time is yet to come!", 30f, Color.red, (CombatEntityAPI) null, 0.0F, 0.0F);
                    MagicRender.objectspace(Global.getSettings().getSprite("graphics/urb/effects/ungpdx_circle1.png"), ship, new Vector2f(0f, 0f),
                            new Vector2f(0f, 0f), new Vector2f(15f, 15f), new Vector2f(750f, 750f),
                            0f, 70f, true,
                            color1, true, 0.05f, 0.3f, 0.1f, true);
                    MagicRender.objectspace(Global.getSettings().getSprite("graphics/urb/effects/ungpdx_circle1.png"), ship, new Vector2f(0f, 0f),
                            new Vector2f(0f, 0f), new Vector2f(15f, 15f), new Vector2f(700f, 700f),
                            0f, 70f, true,
                            color1, true, 0.05f, 0.4f, 0.1f, true);

                    MagicRender.objectspace(Global.getSettings().getSprite("graphics/urb/effects/ungpdx_circle1.png"), ship, new Vector2f(0f, 0f),
                            new Vector2f(0f, 0f), new Vector2f(15f, 15f), new Vector2f(650f, 650f),
                            45f, 40f, true,
                            color2, true, 0.05f, 0.5f, 0.1f, true);
                    MagicRender.objectspace(Global.getSettings().getSprite("graphics/urb/effects/ungpdx_circle1.png"), ship, new Vector2f(0f, 0f),
                            new Vector2f(0f, 0f), new Vector2f(15f, 15f), new Vector2f(600f, 600f),
                            45f, 40f, true,
                            color2, true, 0.05f, 0.6f, 0.1f, true);

                    MagicRender.objectspace(Global.getSettings().getSprite("graphics/urb/effects/ungpdx_circle1.png"), ship, new Vector2f(0f, 0f),
                            new Vector2f(0f, 0f), new Vector2f(5f, 5f), new Vector2f(550f, 550f),
                            0f, 20f, true,
                            color3, true, 0.05f, 0.7f, 0.1f, true);
                    MagicRender.objectspace(Global.getSettings().getSprite("graphics/urb/effects/ungpdx_circle1.png"), ship, new Vector2f(0f, 0f),
                            new Vector2f(0f, 0f), new Vector2f(5f, 5f), new Vector2f(500f, 500f),
                            0f, 20f, true,
                            color3, true, 0.05f, 0.8f, 0.1f, true);

                    return true;
                }
            }
            return Reincarnated;
        }

        public void advance(float amount) {
            if(Global.getCombatEngine().isPaused()) return;
            if (Active) {
                if (Reincarnated) {
                    ArmorGridAPI armorGrid = ship.getArmorGrid();
                    final float[][] grid = armorGrid.getGrid();
                    final float max = armorGrid.getMaxArmorInCell();

                    MutableShipStatsAPI stats = ship.getMutableStats();

                    if (ship.isCapital()) {
                        stats.getEntity().setHitpoints(stats.getEntity().getMaxHitpoints() / 2);
                        ship.getFluxTracker().setCurrFlux(ship.getMaxFlux() / 2);
                    } else {
                        stats.getEntity().setHitpoints(stats.getEntity().getMaxHitpoints());
                        ship.getFluxTracker().setCurrFlux(0);
                    }

                    for (WeaponAPI w : ship.getAllWeapons()) {
                        if (ship.isCapital()) {
                            if (w.usesAmmo() && w.getAmmo() < w.getMaxAmmo() / 2) {
                                w.setAmmo(w.getMaxAmmo() / 2);
                            }
                        } else {
                            if (w.usesAmmo() && w.getAmmo() < w.getMaxAmmo()) {
                                w.setAmmo(w.getMaxAmmo());
                            }
                        }
                    }
                    for (int x = 0; x < grid.length; x++) {
                        for (int y = 0; y < grid[0].length; y++) {
                            if (ship.isCapital()) {
                                if (grid[x][y] < max / 2) {
                                    armorGrid.setArmorValue(x, y, armorGrid.getMaxArmorInCell() / 2);
                                }
                            } else {
                                if (grid[x][y] < max) {
                                    armorGrid.setArmorValue(x, y, armorGrid.getMaxArmorInCell());
                                }
                            }
                        }
                    }
                    stats.getPeakCRDuration().modifyFlat(buffID, -1337 * 69);

                    Reincarnated = false;
                    Active = false;
                }
            }
        }
    }

    @Override
    public void advanceInCombat(CombatEngineAPI engine, float amount) {

    }

    @Override
    public void applyEnemyShipInCombat(float amount, ShipAPI enemy) {
        if (!enemy.getHullSpec().hasTag("ungpdx_deathless")) {
            if (enemy.isFighter() || enemy.isStation() || enemy.isStationModule()) return;
            if (enemy.isFrigate() || enemy.isCapital() && !enemy.getFleetMember().isFlagship()) return;
        }
        if (enemy.hasListenerOfClass(UNGPDX_SkeletonKing.Wraith.class)) return;
        enemy.addListener(new UNGPDX_SkeletonKing.Wraith(enemy));
    }

    @Override
    public void applyPlayerShipInCombat(float amount, CombatEngineAPI engine, ShipAPI ship) {
    }

    @Override
    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return "once";
        if (index == 1) return "hull, armor and ammunition";
        if (index == 2) return "emptied";
        if (index == 3) return "wiped";
        return null;
    }
}
