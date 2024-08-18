package data.scripts.ungprules.impl.combat;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_CombatTag;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;

public class UNGPDX_ThinInsulator extends UNGP_BaseRuleEffect implements UNGP_CombatTag {
    private float overloadMult;

    @Override
    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
        overloadMult = 1f + getValueByDifficulty(0, difficulty) * 0.01f;
    }

    @Override
    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return difficulty.getLinearValue(17f, 18f);

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
        ship.getMutableStats().getOverloadTimeMod().modifyMult(buffID, overloadMult);
    }

    @Override
    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return getPercentString(getValueByDifficulty(index, difficulty));
        return null;
    }
}
