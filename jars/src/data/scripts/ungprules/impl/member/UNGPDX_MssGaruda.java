//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package data.scripts.ungprules.impl.member;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import ungp.api.rules.UNGP_MemberBuffRuleEffect;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings.Difficulty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UNGPDX_MssGaruda extends UNGP_MemberBuffRuleEffect {
    private static final float CDR_BONUS = 50f;
    private static final float RANGE_BONUS = 30f;
    private static final float FLUX_BONUS = 24f;
    private static final float CR_BONUS = 10f;

    public UNGPDX_MssGaruda() {
    }

    public float getValueByDifficulty(int index, Difficulty difficulty) {
        return 0.0F;
    }

    public String getDescriptionParams(int index, Difficulty difficulty) {
        if (index == 0) return this.getPercentString(CDR_BONUS);
        if (index == 1) return this.getPercentString(RANGE_BONUS);
        //if (index == 2) return this.getPercentString(FLUX_BONUS);
        if (index == 2) return this.getPercentString(CR_BONUS);

        return null;
    }

    public void applyPlayerFleetMemberInCampaign(FleetMemberAPI member) {
        MutableShipStatsAPI stats = member.getStats();
        stats.getSystemCooldownBonus().modifyMult(buffID, 1 + CDR_BONUS * 0.01f);
        stats.getVentRateMult().modifyMult(buffID, 1 + CDR_BONUS * 0.01f);
        stats.getBallisticWeaponRangeBonus().modifyMult(buffID, 1 + RANGE_BONUS * 0.01f);
        stats.getEnergyWeaponRangeBonus().modifyMult(buffID, 1 + RANGE_BONUS * 0.01f);
        stats.getMissileWeaponRangeBonus().modifyMult(buffID, 1 + RANGE_BONUS * 0.01f);
        //stats.addListener(new GarudaRangeModifier(RANGE_BONUS));
        //stats.getBallisticWeaponFluxCostMod().modifyMult(buffID, 1 + FLUX_BONUS * 0.01f);

        stats.getMaxCombatReadiness().modifyFlat(buffID, CR_BONUS * 0.01f, this.rule.getName());
    }

    public boolean addIntelTips(TooltipMakerAPI imageTooltip) {
        imageTooltip.addPara(this.rule.getExtra1(), 0.0f);
        List<String> names = new ArrayList();
        Iterator i$ = Global.getSettings().getAllShipHullSpecs().iterator();

        while(i$.hasNext()) {
            ShipHullSpecAPI hullSpec = (ShipHullSpecAPI)i$.next();
            if (isConquest(hullSpec) && !hullSpec.isDefaultDHull()) {
                names.add(hullSpec.getNameWithDesignationWithDashClass());
            }
        }

        imageTooltip.addPara(Misc.getAndJoined(names), Misc.getHighlightColor(), 5.0F);
        return true;
    }

    public boolean canApply(FleetMemberAPI member) {
        return member.isFlagship() && isConquest(member.getHullSpec());
    }

    public static boolean isConquest(ShipHullSpecAPI hullSpec) {
        return hullSpec.getHullId().contains("conquest") || hullSpec.getTags().contains("ungpdx_conquest");
    }

    /*public class GarudaRangeModifier implements WeaponBaseRangeModifier {
        public float mult;
        public GarudaRangeModifier(float mult) {
            this.mult = 1 + mult * 0.01f;
        }

        public float getWeaponBaseRangePercentMod(ShipAPI ship, WeaponAPI weapon) {
            return 0;
        }

        public float getWeaponBaseRangeMultMod(ShipAPI ship, WeaponAPI weapon) {
            float bonus = 0;
            if (weapon.getSize() != WeaponAPI.WeaponSize.LARGE) {
                bonus = mult;
            }

            if (bonus == 0f) return 0f;
            if (bonus < 0) bonus = 0;

            return bonus;
        }

        public float getWeaponBaseRangeFlatMod(ShipAPI ship, WeaponAPI weapon) {
            return 0f;
        }
    }*/
}
