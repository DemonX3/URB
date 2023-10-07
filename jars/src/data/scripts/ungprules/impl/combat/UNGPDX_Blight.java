package data.scripts.ungprules.impl.combat;

import com.fs.starfarer.api.combat.ArmorGridAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_CombatTag;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;

public class UNGPDX_Blight extends UNGP_BaseRuleEffect implements UNGP_CombatTag {
    private float ARMOR_DRAIN;
    private final float ARMOR_THRESHOLD_MULT = 0.3f;
    private final float ARMOR_THRESHOLD_FLAT = 500f;

    @Override
    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
        ARMOR_DRAIN = getValueByDifficulty(0, difficulty);
    }

    @Override
    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return difficulty.getLinearValue(0.2f, 0.2f);

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
        if (ship.isHulk()) return;

        ArmorGridAPI armorGrid = ship.getArmorGrid();
        final float[][] grid = armorGrid.getGrid();
        final float maxMult = armorGrid.getMaxArmorInCell() * ARMOR_THRESHOLD_MULT;
        final float maxFlat = ARMOR_THRESHOLD_FLAT;
        float max = Math.min(maxMult, maxFlat);

        float drainAmount = ARMOR_DRAIN * amount;

        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                if (grid[x][y] > max) {
                    float drain = grid[x][y] - drainAmount;
                    armorGrid.setArmorValue(x, y, drain);
                }
            }
        }
    }

    @Override
    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return getPercentString(getValueByDifficulty(index, difficulty));
        if (index == 1) return "" + String.format("%.0f", ARMOR_THRESHOLD_FLAT);
        if (index == 2) return getPercentString(ARMOR_THRESHOLD_MULT * 100f);

        return null;
    }
}
