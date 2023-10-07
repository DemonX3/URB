package data.scripts.ungprules.impl.combat;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.util.Misc;
import data.scripts.thedudes.AuraCircleAttemptOne;
import org.lazywizard.lazylib.combat.CombatUtils;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_CombatTag;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UNGPDX_Doomslayer extends UNGP_BaseRuleEffect implements UNGP_CombatTag {

    private float auraRange = 1000f;
    private float maneurabilityPenalty = 0.25f;
    private float maneurabilityPenaltyPhase = 0.75f;
    private float fluxDissipationPenalty = 0.05f;
    private float fluxDissipationPenaltyPhase = 0.1f;
    private float phaseUpkeepPenalty = 2f;
    Color color = new Color(25, 50, 200, 200);

    static List<ShipAPI> getShipsOfSide(FleetSide side, boolean includeAllies) {
        final List<ShipAPI> ships = new ArrayList<>();
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

    private List<ShipAPI> getShipsInRangeByList(List<ShipAPI> ships, ShipAPI shipRange, float range) {
        List<ShipAPI> newShips = new ArrayList<>();
        List<ShipAPI> shipsInRange = CombatUtils.getShipsWithinRange(shipRange.getLocation(), range);
        for (ShipAPI e : ships) {
            if (e != null) {
                if (e.isAlive()) {
                    if (shipsInRange.contains(e)) {
                        newShips.add(e);
                    }
                }
            }
        }
        return newShips;
    }

    @Override
    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
    }

    @Override
    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        return 0;
    }

    @Override
    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return "" + String.format("%.0f", auraRange) + "su";
        if (index == 1) return getPercentString(maneurabilityPenalty * 100);
        if (index == 2) return getPercentString(fluxDissipationPenalty * 100);
        if (index == 3) return "" + String.format("%.0f", phaseUpkeepPenalty);
        if (index == 4) return getPercentString(maneurabilityPenaltyPhase * 100);
        if (index == 5) return getPercentString(fluxDissipationPenaltyPhase * 100);
        return super.getDescriptionParams(index, difficulty);
    }

    @Override
    public void advanceInCombat(CombatEngineAPI engine, float amount) {
        if (engine.isPaused()) return;
        ShipAPI playerShip = engine.getPlayerShip();
        List<ShipAPI> enemies = getShipsOfSide(FleetSide.ENEMY, false);

        AuraCircleAttemptOne.AuraParams p = new AuraCircleAttemptOne.AuraParams();

        if (playerShip == null) return;
        if (!playerShip.isAlive()) return;

        List<ShipAPI> shipsInRange = CombatUtils.getShipsWithinRange(playerShip.getLocation(), auraRange);
        List<ShipAPI> shipsAlmostInRange = getShipsInRangeByList(enemies, playerShip, auraRange + 500f);

        if (!playerShip.getCustomData().containsKey("doomslayer_aura")) {
            p.color = Misc.setAlpha(color, 125);
            p.ship = playerShip;
            p.radius = auraRange;
            AuraCircleAttemptOne plugin = new AuraCircleAttemptOne(p);
            Global.getCombatEngine().addLayeredRenderingPlugin(plugin);
            playerShip.setCustomData("doomslayer_aura", plugin);
        } else if (playerShip.getCustomData().containsKey("doomslayer_aura")) {
            AuraCircleAttemptOne visual = (AuraCircleAttemptOne) playerShip.getCustomData().get("doomslayer_aura");
            if (shipsAlmostInRange.size() == 0) {
                visual.p.baseAlpha = 125;
            } else {
                visual.p.baseAlpha = 255;
            }
        }

        for (ShipAPI e : enemies) {
            if (e == null) return;
            if (!e.isAlive()) return;

            e.getMutableStats().getAcceleration().unmodify(this.buffID);
            e.getMutableStats().getDeceleration().unmodify(this.buffID);
            e.getMutableStats().getTurnAcceleration().unmodify(this.buffID);
            e.getMutableStats().getMaxTurnRate().unmodify(this.buffID);
            e.getMutableStats().getFluxDissipation().unmodify(this.buffID);
            e.getMutableStats().getPhaseCloakUpkeepCostBonus().unmodify(this.buffID);

        }

        for (ShipAPI e : shipsInRange) {
            if (enemies.contains(e)) {
                if (e == null) return;
                if (!e.isAlive()) return;

                if (!e.getHullSpec().isPhase()) {
                    e.getMutableStats().getAcceleration().modifyMult(this.buffID, 1 - (maneurabilityPenalty / 2f));
                    e.getMutableStats().getDeceleration().modifyMult(this.buffID, 1 - (maneurabilityPenalty));
                    e.getMutableStats().getTurnAcceleration().modifyMult(this.buffID, 1 - (maneurabilityPenalty / 2f));
                    e.getMutableStats().getMaxTurnRate().modifyMult(this.buffID, 1 - (maneurabilityPenalty));

                    e.getMutableStats().getFluxDissipation().modifyMult(this.buffID, 1 - fluxDissipationPenalty);
                } else {
                    e.getMutableStats().getAcceleration().modifyMult(this.buffID, 1 - (maneurabilityPenaltyPhase / 2f));
                    e.getMutableStats().getDeceleration().modifyMult(this.buffID, 1 - (maneurabilityPenaltyPhase));
                    e.getMutableStats().getTurnAcceleration().modifyMult(this.buffID, 1 - (maneurabilityPenaltyPhase / 2f));
                    e.getMutableStats().getMaxTurnRate().modifyMult(this.buffID, 1 - (maneurabilityPenaltyPhase));

                    e.getMutableStats().getFluxDissipation().modifyMult(this.buffID, 1 - fluxDissipationPenaltyPhase);
                    e.getMutableStats().getPhaseCloakUpkeepCostBonus().modifyMult(this.buffID, phaseUpkeepPenalty);
                }
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
