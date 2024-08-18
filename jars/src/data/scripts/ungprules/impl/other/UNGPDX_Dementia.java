package data.scripts.ungprules.impl.other;

import com.fs.starfarer.api.Global;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_CampaignTag;
import ungp.scripts.campaign.everyframe.UNGP_CampaignPlugin;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;
import ungp.scripts.campaign.specialist.intel.UNGP_SpecialistIntel;

public class UNGPDX_Dementia extends UNGP_BaseRuleEffect implements UNGP_CampaignTag {

    public UNGPDX_Dementia() {
    }

    public void applyGlobalStats() {
        if (!Global.getSector().getPersistentData().containsKey(this.buffID)) {
            if (Global.getSector().getPlayerStats().getLevel() < (Global.getSettings().getLevelupPlugin().getMaxLevel() - 4)) {
                Global.getSector().getPlayerStats().setLevel(Global.getSector().getPlayerStats().getLevel() + 4);
                Global.getSector().getPlayerStats().addXP(Global.getSector().getPlayerStats().getXP() + Global.getSettings().getLevelupPlugin().getXPForLevel(Global.getSector().getPlayerStats().getLevel()));
            }
            Global.getSector().getPersistentData().put(this.buffID, true);
        }

    }

    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
    }

    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        return 0;
    }

    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return "4";
        if (index == 1) return "" + Global.getSettings().getLevelupPlugin().getPointsAtLevel(1) * 4;
        if (index == 2) return "" + Global.getSettings().getLevelupPlugin().getStoryPointsPerLevel() * 4;
        if (index == 3) return "" + Global.getSettings().getLevelupPlugin().getMaxLevel();

        return null;
    }

    public void advanceInCampaign(float amount, UNGP_CampaignPlugin.TempCampaignParams params) {
        if (params.isOneDayPassed() && !Global.getSector().getPersistentData().containsKey(this.buffID + "1")) {
            if (Global.getSector().getPlayerStats().getLevel() >= Global.getSettings().getLevelupPlugin().getMaxLevel()) {
                Global.getSector().getPlayerStats().addPoints(Global.getSettings().getLevelupPlugin().getPointsAtLevel(1) * 4);
                Global.getSector().getPlayerStats().addStoryPoints(Global.getSettings().getLevelupPlugin().getStoryPointsPerLevel() * 4);

                UNGP_SpecialistIntel.RuleMessage message = new UNGP_SpecialistIntel.RuleMessage(this.rule, this.rule.getExtra1());
                message.send();

                Global.getSector().getPersistentData().put(this.buffID + "1", true);
            }
        }
    }
}