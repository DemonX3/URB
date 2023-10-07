package data.scripts.ungprules.impl.combat;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_CombatTag;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class UNGPDX_ArtifactOfEvolution extends UNGP_BaseRuleEffect implements UNGP_CombatTag {
    private static final Logger log = Global.getLogger(UNGPDX_ArtifactOfEvolution.class);

    private float damageInputMultiplier;
    private float rangeBonus;
    private float damageOutputMultiplier;
    private float fluxGridEfficiency;
    private float engineEfficiency;
    private float shieldEfficiency;
    private float timeflowMultiplier;

    static{
        log.setLevel(Level.ALL);
    }

    @Override
    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
        damageInputMultiplier = 1f - getValueByDifficulty(0, difficulty) * 0.01f;
        rangeBonus = getValueByDifficulty(1, difficulty);
        damageOutputMultiplier = 1f + getValueByDifficulty(2, difficulty) * 0.01f;
        fluxGridEfficiency = 1f + getValueByDifficulty(3, difficulty) * 0.01f;
        engineEfficiency = 1f + getValueByDifficulty(4, difficulty) * 0.01f;
        shieldEfficiency = 1f + getValueByDifficulty(5, difficulty) * 0.01f;
        timeflowMultiplier = 1f + getValueByDifficulty(6, difficulty) * 0.01f;
    }

    @Override
    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return difficulty.getLinearValue(2f, 1f);
        if (index == 1) return difficulty.getLinearValue(3f, 1f);
        if (index == 2) return difficulty.getLinearValue(2f, 1f);
        if (index == 3) return difficulty.getLinearValue(2f, 1f);
        if (index == 4) return difficulty.getLinearValue(2f, 1f);
        if (index == 5) return difficulty.getLinearValue(0.5f, 0.5f);
        if (index == 6) return difficulty.getLinearValue(0.5f, 0.25f);

        return 0;
    }

    @Override
    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return getPercentString(getValueByDifficulty(index, difficulty));
        if (index == 1) return getPercentString(getValueByDifficulty(index, difficulty));
        if (index == 2) return getPercentString(getValueByDifficulty(index, difficulty));
        if (index == 3) return getPercentString(getValueByDifficulty(index, difficulty));
        if (index == 4) return getPercentString(getValueByDifficulty(index, difficulty));
        if (index == 5) return getPercentString(getValueByDifficulty(index, difficulty));
        if (index == 6) return getPercentString(getValueByDifficulty(index, difficulty));

        return super.getDescriptionParams(index, difficulty);
    }

    @Override
    public void advanceInCombat(CombatEngineAPI engine, float amount) {

    }

    @Override
    public void applyEnemyShipInCombat(float amount, ShipAPI enemy) {

    }

    @Override
    public void applyPlayerShipInCombat(float amount, CombatEngineAPI engine, ShipAPI ship) {

    }
}