package data.scripts.ungprules.impl.member;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CampaignTerrainAPI;
import com.fs.starfarer.api.campaign.CampaignTerrainPlugin;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import ungp.api.rules.UNGP_MemberBuffRuleEffect;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;

public class UNGPDX_WishUponAStar extends UNGP_MemberBuffRuleEffect {
    private final float MANEURABILITY = 100f;
    private final int SPEED_BONUS = 10;
    private final float CR_COMPENSATION = 40f;

    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0)
            return getPercentString(MANEURABILITY);
        if (index == 1)
            return (SPEED_BONUS*6) + "/" + (SPEED_BONUS*5) + "/" + (SPEED_BONUS*3) + "/" + (SPEED_BONUS*2) + "su";
        if (index == 2)
            return getPercentString(CR_COMPENSATION);
        return super.getDescriptionParams(index, difficulty);
    }

    public void applyPlayerFleetStats(CampaignFleetAPI fleet) {
    }

    public void unapplyPlayerFleetStats(CampaignFleetAPI fleet) {
    }

    public void applyPlayerFleetMemberInCampaign(FleetMemberAPI member) {
        MutableShipStatsAPI stats = member.getStats();
        ShipAPI.HullSize hullSize = stats.getVariant().getHullSize();

        stats.getDynamic().getStat(Stats.CORONA_EFFECT_MULT).modifyPercent(this.buffID, -CR_COMPENSATION);
        stats.getAcceleration().modifyPercent(this.buffID, MANEURABILITY * 2f);
        stats.getDeceleration().modifyPercent(this.buffID, MANEURABILITY);
        stats.getTurnAcceleration().modifyPercent(this.buffID, MANEURABILITY * 2f);
        stats.getMaxTurnRate().modifyPercent(this.buffID, MANEURABILITY);
        switch (hullSize) {
            case FRIGATE:
                stats.getMaxSpeed().modifyFlat(this.buffID, SPEED_BONUS * 6);
                break;
            case DESTROYER:
                stats.getMaxSpeed().modifyFlat(this.buffID, SPEED_BONUS * 5);
                break;
            case CRUISER:
                stats.getMaxSpeed().modifyFlat(this.buffID, SPEED_BONUS * 3);
                break;
            case CAPITAL_SHIP:
                stats.getMaxSpeed().modifyFlat(this.buffID, SPEED_BONUS * 2);
                break;
        }
    }

    public boolean canApply(FleetMemberAPI member) {
        CampaignFleetAPI fleet = Global.getSector().getPlayerFleet();
        if((fleet.getContainingLocation() == null) || fleet != Global.getSector().getPlayerFleet()) return false;

        for (CampaignTerrainAPI terrain : fleet.getContainingLocation().getTerrainCopy()) {
            if (Terrain.CORONA.equals(terrain.getType()) || (Terrain.EVENT_HORIZON.equals(terrain.getType()))) {
                CampaignTerrainPlugin terrainPlugin = terrain.getPlugin();
                if (terrainPlugin.containsEntity(fleet)){
                    return true;
                }
            }
        }
        return false;
    }
}
