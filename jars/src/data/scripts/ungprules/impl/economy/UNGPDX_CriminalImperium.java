package data.scripts.ungprules.impl.economy;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.util.Misc;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_EconomyTag;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;

import java.util.List;

public class UNGPDX_CriminalImperium extends UNGP_BaseRuleEffect implements UNGP_EconomyTag {

    public UNGPDX_CriminalImperium() {
    }

    private float marketRange = 10f;
    private float accessibilityMult;

    @Override
    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
        marketRange = 10f;
        accessibilityMult = getValueByDifficulty(0, difficulty) * 0.01f;
    }

    @Override
    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return 10f;
        if (index == 1) return difficulty.getLinearValue(3f, 2f);

        return 0;
    }

    @Override
    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return String.format("%.0f", getValueByDifficulty(index, difficulty)) + " LY";
        if (index == 1) return getPercentString(getValueByDifficulty(index, difficulty));
        return super.getDescriptionParams(index, difficulty);
    }

    public void applyPlayerMarket(MarketAPI market) {

    }

    public void unapplyPlayerMarket(MarketAPI market) {

    }

    public void applyAllMarket(MarketAPI market) {
        List<MarketAPI> nearbyMarkets = Misc.getNearbyMarkets(market.getLocationInHyperspace(), marketRange);
        int hostileCount = 0;

        for (MarketAPI nearbyMarket : nearbyMarkets) {
            if (nearbyMarket.isPlayerOwned()
                    && (nearbyMarket.getFaction().isHostileTo(market.getFaction())
                    || Global.getSector().getPlayerFaction().isHostileTo(market.getFaction()))
                    && nearbyMarket.isFreePort()) {
                ++hostileCount;
            }
        }
        if (!market.isPlayerOwned()) {
            market.getAccessibilityMod().modifyFlat(this.buffID, (float) (-hostileCount) * accessibilityMult, this.rule.getName());
        }
    }

    public void unapplyAllMarket(MarketAPI market) {
        market.getAccessibilityMod().unmodify(this.buffID);
    }
}
