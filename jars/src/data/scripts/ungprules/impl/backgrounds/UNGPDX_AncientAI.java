package data.scripts.ungprules.impl.backgrounds;

import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import ungp.api.backgrounds.UNGP_BaseBackgroundPlugin;

import java.awt.*;

public class UNGPDX_AncientAI extends UNGP_BaseBackgroundPlugin {
    public UNGPDX_AncientAI() {
    }

    public Color getOverrideNameColor() {
        return new Color(190, 160, 110, 255);
    }

    public float getInheritBlueprintsFactor() {
        return 0.25F;
    }

    public void initCycleBonus() {
        this.addCycleBonus(1, new BackgroundBonus(BackgroundBonusType.CARGO_STACK, new Object[]{CargoAPI.CargoItemType.RESOURCES, "crew", 1f}));
        this.addCycleBonus(3, new BackgroundBonus(BackgroundBonusType.SKILL, new Object[]{"dx3_centuriesuntold", 1}));
        this.addCycleBonus(5, new BackgroundBonus(BackgroundBonusType.CARGO_STACK, new Object[]{CargoAPI.CargoItemType.RESOURCES, "beta_core", 2f}));
        this.addCycleBonus(7, new BackgroundBonus(BackgroundBonusType.SKILL, new Object[]{"gunnery_implants", 1}));
        this.addCycleBonus(7, new BackgroundBonus(BackgroundBonusType.SKILL, new Object[]{"gunnery_implants", 2}));
        this.addCycleBonus(9, new BackgroundBonus(BackgroundBonusType.CARGO_STACK, new Object[]{CargoAPI.CargoItemType.RESOURCES, "beta_core", 3f}));
        this.addCycleBonus(11, new BackgroundBonus(BackgroundBonusType.SKILL, new Object[]{"automated_ships", 1}));
        this.addCycleBonus(11, new BackgroundBonus(BackgroundBonusType.CARGO_STACK, new Object[]{CargoAPI.CargoItemType.SPECIAL, new SpecialItemData("dx3_derelict_bp_package", "dx3_derelict"), 1f}));
        this.addCycleBonus(16, new BackgroundBonus(BackgroundBonusType.SKILL, new Object[]{"neural_link", 1}));
        this.addCycleBonus(25, new BackgroundBonus(BackgroundBonusType.SKILL, new Object[]{"hypercognition", 1}));
        this.addCycleBonus(25, new BackgroundBonus(BackgroundBonusType.CARGO_STACK, new Object[]{CargoAPI.CargoItemType.SPECIAL, new SpecialItemData("ship_bp", "guardian"), 1f}));
        //this.addCycleBonus(25, new BackgroundBonus(BackgroundBonusType.CARGO_STACK, new Object[]{CargoAPI.CargoItemType.SPECIAL, new SpecialItemData("dx3_remnant_bp_package", "remnant"), 1f}));
    }
}
