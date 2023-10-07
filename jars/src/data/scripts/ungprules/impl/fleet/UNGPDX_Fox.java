package data.scripts.ungprules.impl.fleet;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BuffManagerAPI;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.loading.CampaignPingSpec;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_CampaignTag;
import ungp.scripts.campaign.everyframe.UNGP_CampaignPlugin;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;
import ungp.scripts.utils.UNGP_BaseBuff;

import java.awt.*;

public class UNGPDX_Fox extends UNGP_BaseRuleEffect implements UNGP_CampaignTag {
    private static final Color NOTICE_COLOR = new Color(61, 125, 159, 255);
    private static final float WAIT_DAY = 0.5f;
    private static final float SENSOR_FACTOR = 0.5f;

    private class FoxBuff extends UNGP_BaseBuff {
        public FoxBuff(String id) {
            super(id);
        }

        public void apply(FleetMemberAPI member) {
            member.getStats().getSensorProfile().modifyMult(buffID, SENSOR_FACTOR);
            member.getStats().getSensorStrength().modifyMult(buffID, 1f + SENSOR_FACTOR * 2);
        }

    }

    @Override
    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
    }

    @Override
    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        return 0;
    }

    @Override
    public void advanceInCampaign(float amount, UNGP_CampaignPlugin.TempCampaignParams params) {
        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        if (playerFleet != null) {
            float vel = playerFleet.getCurrBurnLevel();
            if ((double)vel < 0.01D) {
                Object mem = getDataInCampaign(0);
                if (mem == null) {
                    saveDataInCampaign(0, 0f);
                } else {
                    float elapsed = (float) mem;
                    if (elapsed >= WAIT_DAY) {
                        String buffId = buffID;
                        boolean needsSync = false;
                        for (FleetMemberAPI member : playerFleet.getFleetData().getMembersListCopy()) {
                            BuffManagerAPI.Buff test = member.getBuffManager().getBuff(buffId);
                            if (test instanceof UNGPDX_Fox.FoxBuff) {
                                FoxBuff buff = (FoxBuff) test;
                                buff.refresh();
                            } else {
                                member.getBuffManager().addBuff(new FoxBuff(buffId));
                                needsSync = true;
                            }
                        }
                        if (needsSync) {
                            playerFleet.forceSync();
                        }
                    } else {
                        float days = Global.getSector().getClock().convertToDays(amount);
                        elapsed += days;
                        saveDataInCampaign(0, elapsed);
                        if (elapsed >= WAIT_DAY) {
                            playerFleet.addFloatingText(rule.getExtra1(), NOTICE_COLOR, 1f);
                            CampaignPingSpec custom = new CampaignPingSpec();
                            custom.setColor(NOTICE_COLOR);
                            custom.setWidth(7);
                            custom.setRange(200);
                            custom.setDuration(3f);
                            custom.setAlphaMult(0.25f);
                            custom.setInFraction(0.1f);
                            custom.setDelay(0.15f);
                            custom.setNum(5);
                            Global.getSector().addPing(playerFleet, custom);
                            Global.getSoundPlayer().playUISound("ui_go_dark_on", 0.5f, 2f);
                        }
                    }
                }
            } else {
                clearDataInCampaign(0);
            }
        }
    }

    @Override
    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return WAIT_DAY + "";
        if (index == 1) return (int) (SENSOR_FACTOR * 4) + "";

        return super.getDescriptionParams(index, difficulty);
    }
}