package data.scripts.ungprules.impl.other;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.econ.MonthlyReport;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_CampaignTag;
import ungp.scripts.campaign.everyframe.UNGP_CampaignPlugin;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;
import ungp.scripts.campaign.specialist.intel.UNGP_SpecialistIntel;

public class UNGPDX_FinancialObligation extends UNGP_BaseRuleEffect implements UNGP_CampaignTag {
    private float debtPercent;
    private float debtFlat;

    public UNGPDX_FinancialObligation() {
    }

    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
        debtPercent = (1 + getValueByDifficulty(0, difficulty));
        debtFlat = getValueByDifficulty(2, difficulty);
    }

    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return difficulty.getLinearValue(2f, 3f);
        if (index == 1) return difficulty.getLinearValue(10000f, 15000f);

        return 0;
    }

    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return getPercentString(getValueByDifficulty(index, difficulty));
        if (index == 1) return String.format("%.0f", getValueByDifficulty(index, difficulty));
        return super.getDescriptionParams(index, difficulty);
    }

    public void advanceInCampaign(float amount, UNGP_CampaignPlugin.TempCampaignParams params) {
        if (params.isOneDayPassed()) {
            CampaignFleetAPI player = Global.getSector().getPlayerFleet();
            //Ok no, nodes confuse me. I'm helpless here. Maybe someday.
            int debt = (int) new MonthlyReport().getNode(MonthlyReport.LAST_MONTH_DEBT).upkeep;


            if (debt == 0) return;

            float stackDebtPercent = debt * (debtPercent * 0.01F);

            float debtToCount = Math.min(stackDebtPercent, debtFlat);

                player.getCargo().getCredits().subtract(debtToCount);
                UNGP_SpecialistIntel.RuleMessage message = new UNGP_SpecialistIntel.RuleMessage(this.rule, this.rule.getExtra1(), new String[]{"" + debtToCount});
                message.send();
        }
    }

    public boolean addIntelTips(TooltipMakerAPI imageTooltip) {
        imageTooltip.addPara("Debt: " + new MonthlyReport().getNode(MonthlyReport.LAST_MONTH_DEBT).upkeep, 0.0F);
        return true;
    }
}
