package data.scripts.ungprules.impl.fleet;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_PlayerFleetTag;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;

import java.util.List;


public class UNGPDX_Celebrity extends UNGP_BaseRuleEffect implements UNGP_PlayerFleetTag {

    public UNGPDX_Celebrity() {
    }

    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {

        return 0;
    }

    public void applyPlayerFleetStats(CampaignFleetAPI fleet) {
        List<FleetMemberAPI> members = fleet.getFleetData().getMembersListCopy();

        for (FleetMemberAPI member : members) {
            if (!member.isFlagship()) {
                if (member.getVariant().hasTag("ship_unique_signature") && !member.getVariant().hasTag("ship_unique_signature_natural")) {
                    member.getVariant().removeTag("ship_unique_signature");
                } else if (member.getVariant().hasTag("ship_unique_signature") && member.getVariant().hasTag("ship_unique_signature_natural")) {
                    member.getVariant().removeTag("ship_unique_signature_natural");
                }
            } else {
                if (!member.getVariant().hasTag("ship_unique_signature") && !member.getVariant().hasTag("ship_unique_signature_natural")) {
                    member.getVariant().addTag("ship_unique_signature");
                } else if (member.getVariant().hasTag("ship_unique_signature") && !member.getVariant().hasTag("ship_unique_signature_natural")) {
                    member.getVariant().addTag("ship_unique_signature_natural");
                }
            }
        }
    }

    public void unapplyPlayerFleetStats(CampaignFleetAPI fleet) {
        List<FleetMemberAPI> members = fleet.getFleetData().getMembersListCopy();

        for (FleetMemberAPI member : members) {
            if (member.getVariant().hasTag("ship_unique_signature") && !member.getVariant().hasTag("ship_unique_signature_natural")) {
                member.getVariant().removeTag("ship_unique_signature");
            } else if (member.getVariant().hasTag("ship_unique_signature") && member.getVariant().hasTag("ship_unique_signature_natural")) {
                member.getVariant().removeTag("ship_unique_signature_natural");
            }
        }
    }

    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return "notorious.";

        return null;
    }
}