package data.scripts.ungprules.impl.fleet;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_PlayerFleetTag;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;
import ungp.scripts.utils.UNGP_BaseBuff;

import java.util.*;


public class UNGPDX_Pestilence extends UNGP_BaseRuleEffect implements UNGP_PlayerFleetTag {
    private float CR_PENALTY;

    public UNGPDX_Pestilence() {
    }

    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
        CR_PENALTY = getValueByDifficulty(0, difficulty) * 0.01f;
    }

    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return difficulty.getLinearValue(5f, 5f);

        return 0;
    }

    public void applyPlayerFleetStats(CampaignFleetAPI fleet) {
        List<String> mothBallers = new ArrayList();
        List<FleetMemberAPI> members = fleet.getFleetData().getMembersListCopy();

        for (FleetMemberAPI member : members) {
            if (member.isMothballed()) {
                String mothBaller = member.getId();
                if (!mothBallers.contains(mothBaller)) {
                    mothBallers.add(mothBaller);
                }
            }
        }

        int mothBalledAmount = mothBallers.size();
        String buffId = this.buffID;
        boolean needsSync = false;
        float crReduction = CR_PENALTY * mothBalledAmount;

        for (FleetMemberAPI member : members) {
            CRDeBuff buff = (CRDeBuff) member.getBuffManager().getBuff(buffId);
            if (buff != null) {
                buff.refresh();
                buff.setCrReduction(crReduction);
            } else {
                member.getBuffManager().addBuff(new CRDeBuff(buffId, crReduction));
                needsSync = true;
            }
        }

        if (needsSync) {
            fleet.forceSync();
        }
    }

    public void unapplyPlayerFleetStats(CampaignFleetAPI fleet) {
    }

    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return "" + getPercentString(getValueByDifficulty(index, difficulty));

        return super.getDescriptionParams(index, difficulty);
    }

    private class CRDeBuff extends UNGP_BaseBuff {
        private float crReduction;

        public CRDeBuff(String id, float crReduction) {
            super(id);
            this.crReduction = crReduction;
        }

        public void apply(FleetMemberAPI member) {
            UNGPDX_Pestilence.decreaseMaxCR(member.getStats(), this.id, this.crReduction, UNGPDX_Pestilence.this.rule.getName());
        }

        public void setCrReduction(float crReduction) {
            this.crReduction = crReduction;
        }
    }
}