package data.scripts.ungprules.impl.other;

import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_CampaignTag;
import ungp.scripts.campaign.everyframe.UNGP_CampaignPlugin;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;

public class UNGPDX_EmptyRuleLmao extends UNGP_BaseRuleEffect implements UNGP_CampaignTag {

    public UNGPDX_EmptyRuleLmao() {
    }

    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
    }

    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        return 0;
    }

    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return "Does nothing.";

        return null;
    }

    public void advanceInCampaign(float amount, UNGP_CampaignPlugin.TempCampaignParams params) {

    }
}