package data.scripts.ungprules.impl.combat;

import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_CombatTag;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;

public class UNGPDX_Mirage extends UNGP_BaseRuleEffect implements UNGP_CombatTag {

    @Override
    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {

    }

    @Override
    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        return 0f;
    }

    public class SuperCloak implements AdvanceableListener {
        public ShipAPI ship;
        boolean StateSaved = false;
        CollisionClass DefaultCollision;
        float AlphaMult = 1f;
        float Alpha = 1f;

        public SuperCloak(ShipAPI ship) {
            this.ship = ship;
        }

        public void advance(float amount) {
            if (!StateSaved) {
                AlphaMult = ship.getExtraAlphaMult();
                DefaultCollision = ship.getCollisionClass();
                StateSaved = true;
            }

            if (ship.isPhased()) {
                if (Alpha > 0f) Alpha = Alpha - 0.02f;
                if (Alpha < 0f) Alpha = 0f;
            } else {
                if (Alpha < 1f) Alpha = Alpha + 0.02f;
                if (Alpha > AlphaMult) Alpha = AlphaMult;
            }
            ship.setAlphaMult(Alpha);

        }
    }

    public class CloakLock implements AdvanceableListener {
        public ShipAPI ship;

        public CloakLock(ShipAPI ship) {
            this.ship = ship;
        }

        public void advance(float amount) {
            if (ship.getShipTarget() == null) return;
            if (ship.getShipTarget().isPhased()) {
                ship.setShipTarget(null);
            }
        }
    }

    @Override
    public void advanceInCombat(CombatEngineAPI engine, float amount) {

    }

    @Override
    public void applyEnemyShipInCombat(float amount, ShipAPI enemy) {
        if (enemy.hasListenerOfClass(UNGPDX_Mirage.SuperCloak.class)) return;
        enemy.addListener(new UNGPDX_Mirage.SuperCloak(enemy));
    }

    @Override
    public void applyPlayerShipInCombat(float amount, CombatEngineAPI engine, ShipAPI ship) {
        if (ship.hasListenerOfClass(UNGPDX_Mirage.CloakLock.class)) return;
        ship.addListener(new UNGPDX_Mirage.CloakLock(ship));
    }

    @Override
    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return "complete invisibility";
        if (index == 1) return "cannot be locked on";

        return null;
    }
}
