package data.scripts.ungprules.impl.combat;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.mission.FleetSide;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_CombatTag;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;

import java.util.ArrayList;
import java.util.List;

public class UNGPDX_MentalWarfare extends UNGP_BaseRuleEffect implements UNGP_CombatTag {
    private float DEGRADATION_MULT;

    static List<ShipAPI> getShipsOfSide(FleetSide side, boolean includeAllies)
    {
        final List<ShipAPI> ships = new ArrayList<>();
        for (ShipAPI ship : Global.getCombatEngine().getShips())
        {
            if (ship.getOwner() == side.ordinal())
            {
                if (ship.isShuttlePod() || (!includeAllies && ship.isAlly()))
                {
                    continue;
                }

                ships.add(ship);
            }
        }

        return ships;
    }

    @Override
    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
        DEGRADATION_MULT = getValueByDifficulty(0, difficulty);
    }

    @Override
    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return difficulty.getLinearValue(8f, 12f);

        return 0f;
    }

    @Override
    public void advanceInCombat(CombatEngineAPI engine, float amount) {
        if (Global.getCombatEngine().isPaused()) return;
        float TimeFlow = engine.getTimeMult().getModifiedValue();

        List<ShipAPI> enemyShips = getShipsOfSide(FleetSide.ENEMY, false);

        for(ShipAPI e : new ArrayList<ShipAPI>(enemyShips)){
            if (e.areSignificantEnemiesInRange())
                e.setTimeDeployed((float) (e.getTimeDeployedForCRReduction() + (amount * TimeFlow) * (DEGRADATION_MULT * 0.01)));
        }
    }

    @Override
    public void applyEnemyShipInCombat(float amount, ShipAPI enemy) {
        enemy.getMutableStats().getCRLossPerSecondPercent().modifyMult(buffID, (float) (1 + DEGRADATION_MULT * 0.01));
    }

    @Override
    public void applyPlayerShipInCombat(float amount, CombatEngineAPI engine, ShipAPI ship) {
    }

    @Override
    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return getPercentString(getValueByDifficulty(index, difficulty));
        return null;
    }
}
