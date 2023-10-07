package data.scripts.ungprules.impl.member;

import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.DModManager;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import ungp.api.rules.UNGP_MemberBuffRuleEffect;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;

public class UNGPDX_Metalize extends UNGP_MemberBuffRuleEffect {
    private final float terrainResist = 7f;
    private final float calcArmor = 30f;
    private final float weaponRepair = 9f;
    private final float weaponDurability = 6f;
    private final float engineRepair = 8f;
    private final float engineDurability = 9f;
    private final float weaponFlux = 2f;
    private final float dmodMult = 1f;

    @Override
    public void applyPlayerFleetMemberInCampaign(FleetMemberAPI mem) {
        int dmods = DModManager.getNumDMods(mem.getStats().getVariant());

        mem.getStats().getDynamic().getStat(Stats.CORONA_EFFECT_MULT).modifyMult(this.buffID, 1 + (terrainResist * dmods) * 0.01f);
        mem.getStats().getEffectiveArmorBonus().modifyFlat(this.buffID, calcArmor * dmods);
        mem.getStats().getCombatWeaponRepairTimeMult().modifyMult(this.buffID, 1 + ((weaponRepair * dmods) * 0.01f));
        mem.getStats().getWeaponDamageTakenMult().modifyMult(this.buffID, 1 - ((weaponDurability * dmods) * 0.01f));
        mem.getStats().getCombatEngineRepairTimeMult().modifyMult(this.buffID, 1 + ((engineRepair * dmods) * 0.01f));
        mem.getStats().getEngineDamageTakenMult().modifyMult(this.buffID, 1 - ((engineDurability * dmods) * 0.01f));
        mem.getStats().getBallisticWeaponFluxCostMod().modifyMult(this.buffID, 1 - ((weaponFlux * dmods) * 0.01f));
        mem.getStats().getEnergyWeaponFluxCostMod().modifyMult(this.buffID, 1 - ((weaponFlux * dmods) * 0.01f));
        mem.getStats().getMissileWeaponFluxCostMod().modifyMult(this.buffID, 1 - ((weaponFlux * dmods) * 0.01f));
        mem.getStats().getDynamic().getStat(Stats.DMOD_EFFECT_MULT).modifyMult(this.buffID, 1 - ((dmodMult * dmods) * 0.01f));
    }

    @Override
    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return getPercentString(terrainResist);
        if (index == 1) return "" + String.format("%.0f", calcArmor);
        if (index == 2) return getPercentString(weaponRepair);
        if (index == 3) return getPercentString(weaponDurability);
        if (index == 4) return getPercentString(engineRepair);
        if (index == 5) return getPercentString(engineDurability);
        if (index == 6) return getPercentString(weaponFlux);
        if (index == 7) return getPercentString(dmodMult);
        return null;
    }
}