package data.scripts.ungprules.impl.combat;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.campaign.DModManager;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_CombatTag;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UNGPDX_TheUnyielding extends UNGP_BaseRuleEffect implements UNGP_CombatTag {

    private float fluxGridEfficiency;
    private float hullRegeneration;
    private float commandDelay;
    private float curCommandDelay = 0;
    private boolean visualTriggered = false;
    Color color = new Color(157, 130, 246, 197);

    private List<ShipAPI> getShipsOfSide(FleetSide side, boolean includeAllies) {
        List<ShipAPI> ships = new ArrayList<>();
        for (ShipAPI ship : Global.getCombatEngine().getShips()) {
            if (ship.getOwner() == side.ordinal()) {
                if (ship.isShuttlePod() || (!includeAllies && ship.isAlly())) {
                    continue;
                }

                ships.add(ship);
            }
        }

        return ships;
    }

    private List<ShipAPI> getShipWithModules(ShipAPI ship) {
        List<ShipAPI> ships = new ArrayList<>();
        if (ship.isShipWithModules() && ship.getChildModulesCopy().size() > 0) {
            ships.addAll(ship.getChildModulesCopy());
        }
        ships.add(ship);

        return ships;
    }

    @Override
    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
        fluxGridEfficiency = 1f + getValueByDifficulty(0, difficulty) * 0.01f;
        hullRegeneration = getValueByDifficulty(2, difficulty) * 0.01f;
        commandDelay = getValueByDifficulty(3, difficulty);
    }

    @Override
    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return difficulty.getLinearValue(15f, 10f);
        if (index == 1) return 30f;
        if (index == 2) return difficulty == UNGP_SpecialistSettings.Difficulty.OMEGA ? 1.5f : 1f;
        if (index == 3) return difficulty.getLinearValue(30f, 60f);

        return 0;
    }

    @Override
    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return getPercentString(getValueByDifficulty(index, difficulty));
        if (index == 1) return getPercentString(getValueByDifficulty(index, difficulty));
        if (index == 2) return getPercentString(getValueByDifficulty(index, difficulty));
        if (index == 3) return String.valueOf(getValueByDifficulty(index, difficulty));
        return super.getDescriptionParams(index, difficulty);
    }

    @Override
    public void advanceInCombat(CombatEngineAPI engine, float amount) {
        ShipAPI playerShip = engine.getPlayerShip();
        if (engine.isCombatOver()) {
            curCommandDelay = 0;
            visualTriggered = false;

            return;
        }
        if (engine.isPaused()) return;

        if ((playerShip.isShuttlePod() && getShipsOfSide(FleetSide.PLAYER, false).size() <= 1) || engine.getTotalElapsedTime(false) <= 0.1f) {
            curCommandDelay = 0.1f;
        } else if (playerShip.isShuttlePod()) {
            curCommandDelay = commandDelay;
        } else if (curCommandDelay > 0) {
            curCommandDelay = curCommandDelay - amount;
        }

        for (ShipAPI p : getShipWithModules(playerShip)) {
            MutableShipStatsAPI stats = p.getMutableStats();
            float HullCur = stats.getEntity().getHitpoints();
            float HullMax = stats.getEntity().getMaxHitpoints();
            float HullSegment = 100f - HullCur / HullMax;

            int particleCount;
            if (p.getHullSize().equals(ShipAPI.HullSize.FRIGATE)) particleCount = 20;
            else if (p.getHullSize().equals(ShipAPI.HullSize.DESTROYER)) particleCount = 30;
            else if (p.getHullSize().equals(ShipAPI.HullSize.CRUISER)) particleCount = 40;
            else if (p.getHullSize().equals(ShipAPI.HullSize.CAPITAL_SHIP)) particleCount = 50;
            else particleCount = 15;

            if (curCommandDelay > 0) {
                Global.getCombatEngine().maintainStatusForPlayerShip(buffID, "graphics/icons/hullsys/damper_field.png", "The Unyielding", "Command transfer in progress: " + String.format("%.0f", curCommandDelay), true);
            }

            stats.getFluxDissipation().unmodify(buffID);
            stats.getFluxCapacity().unmodify(buffID);
            stats.getCRLossPerSecondPercent().unmodify(buffID);

            List<ShipAPI> playerShips = getShipsOfSide(FleetSide.PLAYER, false);
            for (ShipAPI ship : new ArrayList<ShipAPI>(playerShips)) {
                if (ship.isFighter() && ship != engine.getPlayerShip()) playerShips.remove(ship);
                if (ship.isStationModule()) playerShips.remove(ship);
            }

            if (visualTriggered && (curCommandDelay > 0 || playerShips.size() > 1)) {
                visualTriggered = false;

                if (engine.getTotalElapsedTime(false) >= 0.1f) {
                    Global.getSoundPlayer().playSound("unyielding_deactivate", 1f, 0.6f, p.getLocation(), Misc.ZERO);

                    triggerGFX(p, particleCount, color, p.getShieldRadiusEvenIfNoShield() * 1.5f, false);
                }
            } else if (!visualTriggered && playerShips.size() <= 1 && curCommandDelay <= 0) {
                visualTriggered = true;

                Global.getSoundPlayer().playSound("unyielding_activate", 1.2f, 0.4f, p.getLocation(), Misc.ZERO);

                triggerGFX(p, particleCount, color, p.getShieldRadiusEvenIfNoShield() * 1.2f, true);
            }

            if (playerShip.isShuttlePod() || !playerShip.isAlive() || playerShips.size() > 1) return;
            if (playerShips.get(0) != playerShip) return;


            if (curCommandDelay <= 0) {
                Global.getCombatEngine().maintainStatusForPlayerShip(buffID, "graphics/icons/hullsys/damper_field.png", "The Unyielding Flux", "Flux grid improved by " + String.format("%.0f", (fluxGridEfficiency - 1f) * 100) + "%", false);
                Global.getCombatEngine().maintainStatusForPlayerShip(buffID + "1", "graphics/icons/hullsys/damper_field.png", "The Unyielding Will", "Steady performance.", false);
            }

            if (curCommandDelay <= 0) {
                if (HullCur >= HullMax * 0.3f) {
                    stats.getMaxCombatHullRepairFraction().modifyFlat(buffID, 0f);
                    stats.getHullCombatRepairRatePercentPerSecond().modifyFlat(buffID, 0f);
                } else {
                    stats.getMaxCombatHullRepairFraction().modifyFlat(buffID, 1f);
                    stats.getHullCombatRepairRatePercentPerSecond().modifyFlat(buffID, hullRegeneration * HullSegment);
                }

                stats.getFluxDissipation().modifyMult(buffID, fluxGridEfficiency);
                stats.getFluxCapacity().modifyMult(buffID, fluxGridEfficiency);
                stats.getCRLossPerSecondPercent().modifyMult(buffID, 0);

                if (p.areSignificantEnemiesInRange()) {
                    p.setTimeDeployed(p.getTimeDeployedForCRReduction() - amount * p.getMutableStats().getTimeMult().getModifiedValue());
                }
            }
        }
    }

    public static void triggerGFX(CombatEntityAPI target, int particleCount, Color color, float radius, boolean isInverse) {
        float scaleFactor = Math.min(Misc.random.nextFloat() * 0.66f + 0.33f, 1.0f);
        Vector2f scale = new Vector2f(32f, 32f);
        scale.scale(scaleFactor);

        for (int i = 0; i < particleCount; ++i) {
            if (isInverse) {
                MagicRender.objectspace(Global.getSettings().getSprite("graphics/urb/effects/ungpdx_glass_particle.png"),
                        target,
                        MathUtils.getRandomPointInCircle(Misc.ZERO, radius / 2),
                        new Vector2f(-150f, 0f),
                        scale,
                        new Vector2f(-5f, -5f),
                        Misc.random.nextFloat() * 360f,
                        Misc.random.nextFloat() * 90f - 45f,
                        true,
                        color,
                        true,
                        Misc.random.nextFloat() * 0.5f,
                        0.0f,
                        Misc.random.nextFloat() * 1.5f,
                        true);
            } else {
                MagicRender.objectspace(Global.getSettings().getSprite("graphics/urb/effects/ungpdx_glass_particle.png"),
                        target,
                        new Vector2f(0f, 0f),
                        MathUtils.getRandomPointInCircle(Misc.ZERO, radius),
                        scale,
                        new Vector2f(-5f, -5f),
                        Misc.random.nextFloat() * 360f,
                        Misc.random.nextFloat() * 90f - 45f,
                        true,
                        color,
                        true,
                        Misc.random.nextFloat() * 0.5f,
                        0.0f,
                        Misc.random.nextFloat() * 1.5f,
                        true);
            }
        }
    }

    @Override
    public void applyEnemyShipInCombat(float amount, ShipAPI enemy) {

    }

    @Override
    public void applyPlayerShipInCombat(float amount, CombatEngineAPI engine, ShipAPI player) {

    }
}
