package data.scripts.ungprules.impl.combat;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_CombatTag;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;

public class UNGPDX_Jinxed extends UNGP_BaseRuleEffect implements UNGP_CombatTag {
    private float malfunctionChance;

    @Override
    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
        malfunctionChance = getValueByDifficulty(0, difficulty) * 0.001f;
    }

    @Override
    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return difficulty.getLinearValue(25f, 50f);

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
        ship.getMutableStats().getWeaponMalfunctionChance().modifyFlat(buffID, malfunctionChance);
        ship.getMutableStats().getEngineMalfunctionChance().modifyFlat(buffID, malfunctionChance * 0.1f);
    }

    @Override
    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) {
            if(difficulty == UNGP_SpecialistSettings.Difficulty.GAMMA) return "rare";
            if(difficulty == UNGP_SpecialistSettings.Difficulty.BETA) return "occasional";
            if(difficulty == UNGP_SpecialistSettings.Difficulty.ALPHA) return "common";
            if(difficulty == UNGP_SpecialistSettings.Difficulty.OMEGA) return "constant";
        }
        return null;
    }
}
