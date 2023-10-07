package data.scripts.ungprules.impl.other;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_CampaignTag;
import ungp.scripts.campaign.everyframe.UNGP_CampaignPlugin;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;

public class UNGPDX_SPXPDebuff extends UNGP_BaseRuleEffect implements UNGP_CampaignTag {
    private float xpDrain;

    public UNGPDX_SPXPDebuff() {
    }

    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
        xpDrain = getValueByDifficulty(0, difficulty) * 0.01f;
    }

    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return difficulty.getLinearValue(0.5f, 1f);

        return 0;
    }

    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return "" + getPercentString(getValueByDifficulty(index, difficulty));

        return super.getDescriptionParams(index, difficulty);
    }

    public void advanceInCampaign(float amount, UNGP_CampaignPlugin.TempCampaignParams params) {
        MutableCharacterStatsAPI playerStats = Global.getSector().getPlayerStats();

        if(params.isOneDayPassed() && playerStats.getBonusXp() > 0){
            playerStats.setBonusXp((long) (playerStats.getBonusXp() - playerStats.getBonusXp() * xpDrain));
        }
    }
}