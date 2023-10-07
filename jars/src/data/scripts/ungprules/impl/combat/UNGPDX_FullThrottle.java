package data.scripts.ungprules.impl.combat;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_CombatTag;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;

public class UNGPDX_FullThrottle extends UNGP_BaseRuleEffect implements UNGP_CombatTag {
    private float MASS_MULT;

    public class RammingSpeed implements AdvanceableListener {
        CombatEngineAPI engine = Global.getCombatEngine();

        private float defaultMass = 0f;
        private float modMass = 0f;

        public ShipAPI ship;

        public RammingSpeed(ShipAPI ship) {
            this.ship = ship;
            this.defaultMass = ship.getMass();
            this.modMass = ship.getMass() * MASS_MULT;
        }

        public void advance(float amount) {
            if (engine.isPaused()) return;

            if (ship.getVelocity().length() > ship.getMaxSpeed() * 0.9f && ship.isAlive()) {
                ship.setMass(modMass);
            } else {
                ship.setMass(defaultMass);
            }

            //My bootleg mass check for testing
            //if(ship == engine.getPlayerShip())
            //Global.getCombatEngine().maintainStatusForPlayerShip(buffID, "graphics/icons/hullsys/damper_field.png", "Full Throttle DEBUG", "Mass: " + String.format("%.0f", ship.getMass()), false);
        }
    }

    @Override
    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
        MASS_MULT = 1 + getValueByDifficulty(0, difficulty) * 0.01f;
    }

    @Override
    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return difficulty.getLinearValue(10f, 20f);

        return 0f;
    }

    @Override
    public void advanceInCombat(CombatEngineAPI engine, float amount) {

    }

    @Override
    public void applyEnemyShipInCombat(float amount, ShipAPI enemy) {

    }

    @Override
    public void applyPlayerShipInCombat(float amount, CombatEngineAPI engine, ShipAPI ship) {
        if (ship.hasListenerOfClass(UNGPDX_FullThrottle.RammingSpeed.class)
                || !ship.isAlive()) return;
        ship.addListener(new UNGPDX_FullThrottle.RammingSpeed(ship));
    }

    @Override
    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return "90%";
        if (index == 1) return getPercentString(getValueByDifficulty(0, difficulty));
        return null;
    }
}
