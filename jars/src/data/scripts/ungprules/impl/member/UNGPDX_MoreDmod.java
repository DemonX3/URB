package data.scripts.ungprules.impl.member;

import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import ungp.api.rules.UNGP_MemberBuffRuleEffect;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;

public class UNGPDX_MoreDmod extends UNGP_MemberBuffRuleEffect {
    private float destructionMult;
    private float dmodRates;

    @Override
    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
        destructionMult = 1f + getValueByDifficulty(0, difficulty) * 0.01f;
        dmodRates = 1f + getValueByDifficulty(1, difficulty) * 0.01f;
    }

    @Override
    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return difficulty.getLinearValue(30f, 40f);
        if (index == 1) return difficulty.getLinearValue(15f, 10f);
        return 0;
    }

    @Override
    public void applyPlayerFleetMemberInCampaign(FleetMemberAPI member) {
        member.getStats().getBreakProb().modifyMult(buffID, destructionMult);
        member.getStats().getDynamic().getMod(Stats.DMOD_ACQUIRE_PROB_MOD).modifyMult(buffID, dmodRates);
    }


    @Override
    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return getPercentString(getValueByDifficulty(index, difficulty));
        if (index == 1) return getPercentString(getValueByDifficulty(index, difficulty));

        return null;
    }
}
