package data.scripts.ungprules.impl.combat;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipCommand;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.util.IntervalUtil;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_CombatTag;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;

import java.awt.*;

public class UNGPDX_DazingPilotage extends UNGP_BaseRuleEffect implements UNGP_CombatTag {

    private final float interval = 7f;
    private final float aMimirChance = 0.01f;
    private float sleepChance;

    public class Dazing implements AdvanceableListener {
        IntervalUtil dazingInterval = new IntervalUtil(interval, interval);
        float sleepProgress = 0f;
        Color mimirColor = new Color(130, 211, 169, 255);
        boolean isAsleep = false;

        CombatEngineAPI engine = Global.getCombatEngine();

        public ShipAPI ship;

        public Dazing(ShipAPI ship) {
            this.ship = ship;
        }

        public void advance(float amount) {
            if (engine.isPaused()) return;

            float sleepDuration;
            if (ship.getHullSize().equals(ShipAPI.HullSize.FRIGATE)) sleepDuration = 1f;
            else if (ship.getHullSize().equals(ShipAPI.HullSize.DESTROYER)) sleepDuration = 2f;
            else if (ship.getHullSize().equals(ShipAPI.HullSize.CRUISER)) sleepDuration = 4f;
            else if (ship.getHullSize().equals(ShipAPI.HullSize.CAPITAL_SHIP)) sleepDuration = 5f;
            else sleepDuration = 1f;

            dazingInterval.advance(amount);
            if (isAsleep) sleepProgress += amount;

            //my sad logless display of values for debug
            //Global.getCombatEngine().addFloatingText(ship.getLocation(), "" + String.format("%.0f", dazingInterval.getElapsed()) + "/" + String.format("%.0f", sleepProgress), 30.0F, mimirColor, (CombatEntityAPI) null, 1.0F, 0.0F);

            if (dazingInterval.intervalElapsed()) {
                if (!isAsleep) {
                    if (UNGPDX_DazingPilotage.this.roll(sleepChance)) {
                        if (UNGPDX_DazingPilotage.this.roll(aMimirChance)) {
                            Global.getCombatEngine().addFloatingText(ship.getLocation(), "A mimir...", 30.0F, mimirColor, (CombatEntityAPI) null, 1.0F, 0.0F);
                        } else {
                            Global.getCombatEngine().addFloatingText(ship.getLocation(), "ZZZzzz...", 30.0F, mimirColor, (CombatEntityAPI) null, 1.0F, 0.0F);
                        }
                        isAsleep = true;
                        sleepProgress = 0f;
                    }
                }
            }

            if (sleepProgress >= sleepDuration || !isAsleep) {
                ship.getMutableStats().getShieldTurnRateMult().unmodify(buffID);
                ship.getMutableStats().getWeaponTurnRateBonus().unmodify(buffID);
                ship.getMutableStats().getBeamWeaponTurnRateBonus().unmodify(buffID);
                isAsleep = false;
            } else {
                ship.blockCommandForOneFrame(ShipCommand.USE_SYSTEM);
                ship.blockCommandForOneFrame(ShipCommand.TOGGLE_SHIELD_OR_PHASE_CLOAK);
                ship.blockCommandForOneFrame(ShipCommand.FIRE);
                ship.blockCommandForOneFrame(ShipCommand.PULL_BACK_FIGHTERS);
                ship.blockCommandForOneFrame(ShipCommand.VENT_FLUX);
                ship.blockCommandForOneFrame(ShipCommand.ACCELERATE);
                ship.blockCommandForOneFrame(ShipCommand.DECELERATE);
                ship.blockCommandForOneFrame(ShipCommand.TURN_LEFT);
                ship.blockCommandForOneFrame(ShipCommand.TURN_RIGHT);
                ship.blockCommandForOneFrame(ShipCommand.STRAFE_LEFT);
                ship.blockCommandForOneFrame(ShipCommand.STRAFE_RIGHT);
                ship.getMutableStats().getShieldTurnRateMult().modifyMult(buffID, 0f);
                ship.getMutableStats().getWeaponTurnRateBonus().modifyMult(buffID, 0f);
                ship.getMutableStats().getBeamWeaponTurnRateBonus().modifyMult(buffID, 0f);
                if (ship == engine.getPlayerShip())
                    Global.getCombatEngine().maintainStatusForPlayerShip(buffID, "graphics/urb/icons/hullsys/ungpdx_mimir.png", "Dozing off...", "Controls locked.", true);
            }
        }
    }

    @Override
    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
        sleepChance = getValueByDifficulty(0, difficulty) * 0.01f;
    }

    @Override
    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return difficulty.getLinearValue(9f, 9f);

        return 0;
    }

    @Override
    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return "7th";
        if (index == 1) return getPercentString(getValueByDifficulty(0, difficulty));
        if (index == 2) return "2/3/5/6";

        return super.getDescriptionParams(index, difficulty);
    }

    @Override
    public void advanceInCombat(CombatEngineAPI engine, float amount) {

    }

    @Override
    public void applyEnemyShipInCombat(float amount, ShipAPI enemy) {

    }

    @Override
    public void applyPlayerShipInCombat(float amount, CombatEngineAPI engine, ShipAPI player) {
        if (player.hasListenerOfClass(UNGPDX_DazingPilotage.Dazing.class)) return;
        player.addListener(new UNGPDX_DazingPilotage.Dazing(player));
    }
}
