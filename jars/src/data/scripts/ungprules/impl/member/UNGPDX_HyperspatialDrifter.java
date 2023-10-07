package data.scripts.ungprules.impl.member;

import com.fs.starfarer.api.fleet.FleetMemberAPI;
import ungp.api.rules.UNGP_MemberBuffRuleEffect;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;

public class UNGPDX_HyperspatialDrifter extends UNGP_MemberBuffRuleEffect {
    private float bonus;

    @Override
    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
        bonus = getValueByDifficulty(0, difficulty);
    }

    @Override
    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return difficulty == UNGP_SpecialistSettings.Difficulty.OMEGA ? 2 : 1;
        return 0;
    }

    @Override
    public void applyPlayerFleetMemberInCampaign(FleetMemberAPI member) {
        member.getStats().getMaxBurnLevel().modifyFlat(buffID, bonus);
    }


    @Override
    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return getFactorString(getValueByDifficulty(index, difficulty));
        return null;
    }
}
