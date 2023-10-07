package data.scripts.ungprules.impl.member;

import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import ungp.api.rules.UNGP_MemberBuffRuleEffect;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;

public class UNGPDX_ScaryDmod extends UNGP_MemberBuffRuleEffect {
    private float dmodMult;

    @Override
    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
        dmodMult = 1f + getValueByDifficulty(0, difficulty) * 0.01f;
    }

    @Override
    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return difficulty.getLinearValue(25f, 30f);
        return 0;
    }

    @Override
    public void applyPlayerFleetMemberInCampaign(FleetMemberAPI member) {
        member.getStats().getDynamic().getStat(Stats.DMOD_EFFECT_MULT).modifyMult(buffID, dmodMult);
    }

    @Override
    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return getPercentString(getValueByDifficulty(index, difficulty));
        return null;
    }
}