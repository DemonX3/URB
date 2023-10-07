package data.scripts.ungprules.impl.economy;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_EconomyTag;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;

public class UNGPDX_ColonySuffering extends UNGP_BaseRuleEffect implements UNGP_EconomyTag {

    public UNGPDX_ColonySuffering() {
    }
    private float hazardMult = 100f;

    @Override
    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {

    }

    @Override
    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        return 0;
    }

    @Override
    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return "+" + (int)hazardMult + "%";

        return null;
    }

    public void applyPlayerMarket(MarketAPI market) {
        market.getHazard().modifyFlat(buffID, hazardMult * 0.01f, this.rule.getName());
    }

    public void unapplyPlayerMarket(MarketAPI market) {
        market.getHazard().unmodify(buffID);
    }

    public void applyAllMarket(MarketAPI market) {

    }

    public void unapplyAllMarket(MarketAPI market) {

    }
}
