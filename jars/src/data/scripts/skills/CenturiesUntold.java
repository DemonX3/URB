package data.scripts.skills;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.*;
import com.fs.starfarer.api.fleet.MutableFleetStatsAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.skills.BaseSkillEffectDescription;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class CenturiesUntold {

    public static float FINDINGS_BONUS = 50f;
    public static float RARE_SALVAGE_RATES = 25f;

    public static class Level1 implements FleetStatsSkillEffect {
        public void apply(MutableFleetStatsAPI stats, String id, float level) {
            String desc = "Centuries Untold skill";
            stats.getDynamic().getStat(Stats.SALVAGE_VALUE_MULT_FLEET_INCLUDES_RARE).modifyFlat(id, RARE_SALVAGE_RATES * 0.01f, desc);
            stats.getDynamic().getStat(Stats.SALVAGE_VALUE_MULT_FLEET_NOT_RARE).modifyFlat(id, -(RARE_SALVAGE_RATES * 0.01f), desc);
        }

        public void unapply(MutableFleetStatsAPI stats, String id) {
            stats.getDynamic().getStat(Stats.SALVAGE_VALUE_MULT_FLEET_INCLUDES_RARE).unmodifyFlat(id);
            stats.getDynamic().getStat(Stats.SALVAGE_VALUE_MULT_FLEET_NOT_RARE).unmodifyFlat(id);
        }

        public String getEffectDescription(float level) {
            return "+" + (int) RARE_SALVAGE_RATES + "% rare items, such as blueprints, recovered from abandoned stations and other derelicts";
        }

        public String getEffectPerLevelDescription() {
            return null;
        }

        public ScopeDescription getScopeDescription() {
            return ScopeDescription.FLEET;
        }
    }

    public static class Level2 implements FleetStatsSkillEffect {
        public void apply(MutableFleetStatsAPI stats, String id, float level) {
        }

        public void unapply(MutableFleetStatsAPI stats, String id) {
        }

        public String getEffectDescription(float level) {
            return "+" + (int) FINDINGS_BONUS + "% resources recovered in Tech-Mining industries";
        }

        public String getEffectPerLevelDescription() {
            return null;
        }

        public ScopeDescription getScopeDescription() {
            return ScopeDescription.FLEET;
        }
    }

    public static class Level3 implements MarketSkillEffect {
        public void apply(MarketAPI market, String id, float level) {
        }

        public void unapply(MarketAPI market, String id) {
        }

        public String getEffectDescription(float level) {
            return "+" + (int) RARE_SALVAGE_RATES + "% rare items, such as blueprints, recovered from abandoned stations and other derelicts";
        }

        public String getEffectPerLevelDescription() {
            return null;
        }

        public ScopeDescription getScopeDescription() {
            return ScopeDescription.GOVERNED_OUTPOST;
        }
    }
    public static class Level4 implements MarketSkillEffect {
        public void apply(MarketAPI market, String id, float level) {
            market.getStats().getDynamic().getMod(Stats.TECH_MINING_MULT).modifyMult(id, 1f + (FINDINGS_BONUS * 0.01f));
        }

        public void unapply(MarketAPI market, String id) {
            market.getStats().getDynamic().getMod(Stats.TECH_MINING_MULT).unmodifyMult(id);
        }

        public String getEffectDescription(float level) {
            return "+" + (int) FINDINGS_BONUS + "% resources recovered in Tech-Mining industries";
        }

        public String getEffectPerLevelDescription() {
            return null;
        }

        public ScopeDescription getScopeDescription() {
            return ScopeDescription.GOVERNED_OUTPOST;
        }
    }
}
