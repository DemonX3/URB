package data.scripts.ungprules.impl.other;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.MathUtils;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_CampaignTag;
import ungp.scripts.campaign.everyframe.UNGP_CampaignPlugin;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;
import ungp.scripts.campaign.specialist.intel.UNGP_SpecialistIntel;

import java.awt.*;

public class UNGPDX_Famine extends UNGP_BaseRuleEffect implements UNGP_CampaignTag {
    private float crewFood;

    public UNGPDX_Famine() {
    }

    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
        crewFood = getValueByDifficulty(0, difficulty);
    }

    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return difficulty.getLinearValue(0.0125f, 0.025f);

        return 0;
    }

    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return "" + getValueByDifficulty(index, difficulty);
        if (index == 1) return "" + getValueByDifficulty(0, difficulty) * 2;
        if (index == 2) return "10%";

        return super.getDescriptionParams(index, difficulty);
    }

    public void advanceInCampaign(float amount, UNGP_CampaignPlugin.TempCampaignParams params) {
        if (params.isOneMonthPassed()) {
            CampaignFleetAPI player = Global.getSector().getPlayerFleet();
            float crew = player.getCargo().getCrew() + player.getCargo().getMarines() * 2;
            float foodAmount = crew * crewFood;
            float crewToStarve = player.getCargo().getTotalCrew() * 0.1f;
            float rngDifference = MathUtils.getRandomNumberInRange(1f,2.5f);
            float organs = (crewToStarve * (rngDifference / 2));

            //If you are here to read my code, I pity you.
            //Meet the default shit coder activity:
            if (crew > 0) {
                if (player.getCargo().getCommodityQuantity("food") > foodAmount) { //If we have more food than required, EAT required.
                    if (foodAmount > 1) { //If we have more than 1 food, EAT.
                        player.getCargo().removeCommodity("food", foodAmount);
                        UNGP_SpecialistIntel.RuleMessage message = new UNGP_SpecialistIntel.RuleMessage(this.rule, this.rule.getExtra1(), new String[]{"" + (int)(foodAmount)});
                        message.send();
                    } else { //If we have less or equal, EAT. Wait why did I do this, I don't remember.
                        player.getCargo().removeCommodity("food", 1);
                        UNGP_SpecialistIntel.RuleMessage message = new UNGP_SpecialistIntel.RuleMessage(this.rule, this.rule.getExtra1(), new String[]{"" + 1});
                        message.send();
                    }
                } else { //If not? Oh well, organ time.
                    if (player.getCargo().getCommodityQuantity("food") > 0){ //Eat remaining food.
                        player.getCargo().removeCommodity("food", player.getCargo().getCommodityQuantity("food"));
                        UNGP_SpecialistIntel.RuleMessage message = new UNGP_SpecialistIntel.RuleMessage(this.rule, this.rule.getExtra1(), new String[]{"" + player.getCargo().getCommodityQuantity("food")});
                        message.send();
                    }
                    if (player.getCargo().getMarines() > 0 && player.getCargo().getCrew() > 0) { //Starvation when both types of crew exist.
                        player.getCargo().removeCrew((int) (crewToStarve * rngDifference));
                        player.getCargo().removeMarines((int) (crewToStarve / rngDifference));

                        UNGP_SpecialistIntel.RuleMessage message = new UNGP_SpecialistIntel.RuleMessage(this.rule, this.rule.getExtra2(), new String[]{"" + (int)(crewToStarve * rngDifference), "" + (int)(crewToStarve / rngDifference), "" + (int)organs});
                        message.send();
                    } else if (player.getCargo().getMarines() > 0 && player.getCargo().getCrew() == 0) { //Starvation when only marines exist (damn, no crew bitches?)
                        player.getCargo().removeMarines((int) (crewToStarve));

                        UNGP_SpecialistIntel.RuleMessage message = new UNGP_SpecialistIntel.RuleMessage(this.rule, this.rule.getExtra2(), new String[]{"no", "" + (int)(crewToStarve), "" + (int)organs});
                        message.send();
                    } else if (player.getCargo().getMarines() == 0 && player.getCargo().getCrew() > 0) { //Starvation when only crew exist (damn, marineless)
                        player.getCargo().removeCrew((int) (crewToStarve));

                        UNGP_SpecialistIntel.RuleMessage message = new UNGP_SpecialistIntel.RuleMessage(this.rule, this.rule.getExtra2(), new String[]{"" + (int)(crewToStarve), "no", "" + (int)organs});
                        message.send();
                    }
                    player.getCargo().addCommodity("organs", organs); // Involuntary organ donation post-mortem!
                }
            } //I'm 100% sure all this can be simplified into a very tidy little bit, but oh well.
        }
    }

    public boolean addIntelTips(TooltipMakerAPI imageTooltip) {
        Color highlight = Misc.getHighlightColor();
        CampaignFleetAPI player = Global.getSector().getPlayerFleet();
        float foodAmountTip = (player.getCargo().getCrew() + player.getCargo().getMarines() * 2) * crewFood;
        String crew;
        String marines;

        if (player.getCargo().getCrew() > 0) {crew = "" + player.getCargo().getCrew();}else{crew = "no";}
        if (player.getCargo().getMarines() > 0) {marines = "" + player.getCargo().getMarines();}else{marines = "no";}

        imageTooltip.addPara("Based on your fleet's crew complement of %s crewmembers and %s marines:", 0.0F,
                highlight,
                crew,
                marines);
        imageTooltip.addPara("Your crew and marines require %s food each month.", 1.0F,
                highlight,
                "" + foodAmountTip);
        imageTooltip.addPara("Your food stockpile of %s will last for another %s months.", 1.0F,
                highlight,
                "" + (int)(player.getCargo().getCommodityQuantity("food")),
                "" + (int)(player.getCargo().getCommodityQuantity("food") / foodAmountTip));
        return true;
    }
}