package data.scripts.ungprules.impl.combat;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.DModManager;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sun.rmi.runtime.Log;
import ungp.api.rules.UNGP_BaseRuleEffect;
import ungp.api.rules.tags.UNGP_CombatTag;
import ungp.scripts.campaign.specialist.UNGP_SpecialistSettings;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class UNGPDX_ProsperousForging extends UNGP_BaseRuleEffect implements UNGP_CombatTag {
    private float FIRST_ROLL_CHANCE;
    private float SECOND_ROLL_CHANCE;
    private float EXTRA_LIMIT;
    private final String blacklistPath = Global.getSettings().getString("URB_antagonism_blacklist");

    Logger logAdd = Logger.getLogger("UNGPDX_ProsperousForging S-Mod Activity");

    public Set<String> getCSVSet(String path) {
        Set<String> csvSet = new LinkedHashSet<>();
        try {
            JSONArray blacklist = Global.getSettings().getMergedSpreadsheetDataForMod("id", path, "demonx3_ungp_rulepack");
            for (int i = 0; i < blacklist.length(); i++) {

                JSONObject row = blacklist.getJSONObject(i);
                String id = row.getString("id");

                csvSet.add(id);
            }
        } catch (IOException | JSONException ex) {
            logAdd.error(ex);
        }
        return csvSet;
    }

    Set<String> blacklist = getCSVSet(blacklistPath);

    public class Forged implements AdvanceableListener {
        CombatEngineAPI engine = Global.getCombatEngine();

        public ShipAPI ship;
        public HullModSpecAPI mod;
        public boolean SModded = false;
        public boolean SModdead = false;
        boolean listDone = false;
        boolean hullmodsListComplete = false;

        String[] hullmodsList = new String[0];
        String[] hullmodsPriority = new String[0];
        String[] hullmodsKnown = new String[0];

        List<HullModSpecAPI> specs = Global.getSettings().getAllHullModSpecs();
        String[] hullmodsAll = new String[specs.size()];

        int sMod1;
        int sMod2;
        int sMod3;
        boolean sModa1 = false;
        boolean sModa2 = false;
        boolean sModa3 = false;

        public Forged(ShipAPI ship) {
            this.ship = ship;
        }

        public String[] removeFromArray(String[] array, int index) {

            if (array == null || index < 0
                    || index >= array.length) {

                return array;
            }

            String[] anotherArray = new String[array.length - 1];
            for (int i = 0, k = 0; i < array.length; i++) {
                if (i == index) {
                    continue;
                }
                anotherArray[k++] = array[i];
            }

            return anotherArray;
        }

        public boolean hullmodCheck(String modID, ShipAPI ship) {
            boolean applicable = true;
            if (modID == null) return false;
            mod = Global.getSettings().getHullModSpec(modID);

            /*Logger logC = Logger.getLogger("UNGPDX_ProsperousForging hullmodCheck");
            logC.setLevel(Level.INFO);
            if (!mod.getEffect().isApplicableToShip(ship)) {
                logC.info(mod.getDisplayName() + " is not applicable to ship because: " + mod.getEffect().getUnapplicableReason(ship));
            } else if (mod.getUITags().contains("Logistics")) {
                logC.info(mod.getDisplayName() + " is a Logistics hullmod.");
            } else if (mod.isHidden()) {
                logC.info(mod.getDisplayName() + " is hidden.");
            } else if (mod.isHiddenEverywhere()) {
                logC.info(mod.getDisplayName() + " is hidden everywhere.");
            }*/

            if (blacklist.contains(mod.getId())
                    || mod.getCostFor(ship.getHullSize()) == 0
                    || mod.getUITags().contains("Logistics")
                    || mod.isHidden()
                    || mod.isHiddenEverywhere()
                    || !mod.getEffect().isApplicableToShip(ship)) {
                applicable = false;
            }
            return applicable;
        }

        public String[] hullmodAddToString(String[] hullmodSetOrigin, String[] hullmodSetAdd, String logInfo) {
            if (hullmodSetAdd.length == 0) {
                /*Logger logF = Logger.getLogger("UNGPDX_ProsperousForging hullmodList Fail " + logInfo);
                logF.setLevel(Level.INFO);
                logF.info("Failed to add hullmods into list to " + ship.getName() + ", " + ship.getHullSpec().getHullNameWithDashClass() + " due to lack of hullmods to add.");
                logF.info("List had " + hullmodSetOrigin.length + " hullmods, " + logInfo + " had " + hullmodSetAdd.length + " hullmods.");*/
                return hullmodSetOrigin;
            }
            String[] hullmodSetNew = new String[hullmodSetOrigin.length + hullmodSetAdd.length];

            int k = 0;

            for (String s : hullmodSetOrigin) {
                if (hullmodCheck(s, ship)) {
                    hullmodSetNew[k] = s;
                    k++;
                }
            }

            for (String s : hullmodSetAdd) {
                if (hullmodCheck(s, ship)) {
                    hullmodSetNew[k] = s;
                    k++;
                }
            }

            for (int h = 0; h < hullmodSetNew.length; h++) {
                if (hullmodSetNew[h] == null) {
                    hullmodSetNew = removeFromArray(hullmodSetNew, h);
                    h--;
                }
            }


            Logger logS = Logger.getLogger("UNGPDX_ProsperousForging hullmodList Success " + logInfo);
            logS.setLevel(Level.INFO);
            logS.info("Added " + hullmodSetNew.length + " hullmods into list to " + ship.getName() + ", " + ship.getHullSpec().getHullNameWithDashClass());
            logS.info("List had " + hullmodSetOrigin.length + " hullmods, " + logInfo + " had " + hullmodSetAdd.length + " hullmods.");
            return hullmodSetNew;
        }

        public void advance(float amount) {
            if (engine.isPaused()) return;

            if (ship.getFleetMember() != null) {
                if (ship.getFleetMember().getFleetCommander() != null) {
                    if (ship.getFleetMember().getFleetCommander().getFaction() != null) {
                        FactionAPI faction = ship.getFleetMember().getFleetCommander().getFaction();
                        hullmodsPriority = faction.getPriorityHullMods().toArray(new String[0]);
                        hullmodsKnown = faction.getKnownHullMods().toArray(new String[0]);
                    }
                }
            }

            while (!listDone) {
                for (int i = 0; i < specs.size(); i++)
                    hullmodsAll[i] = specs.get(i).getId();

                listDone = true;
            }

            if (!hullmodsListComplete) {
                hullmodsList = hullmodAddToString(hullmodsList, hullmodsPriority, "Priority");
                if (hullmodsList.length < 3)
                    hullmodsList = hullmodAddToString(hullmodsList, hullmodsKnown, "Known");
                if (hullmodsList.length < 3)
                    hullmodsList = hullmodAddToString(hullmodsList, hullmodsAll, "All");

                hullmodsListComplete = true;
            }
            //logAdd.setLevel(Level.INFO);

            if (!SModded && ship.isAlive() && hullmodsList.length >= 3) {
                while (!sModa1) {
                    sMod1 = new Random().nextInt(hullmodsList.length);
                    if (!ship.getVariant().getHullMods().contains(hullmodsList[sMod1])) {
                        sModa1 = true;
                    }
                }
                while (!sModa2 && EXTRA_LIMIT > 0f) {
                    sMod2 = new Random().nextInt(hullmodsList.length);
                    if (!ship.getVariant().getHullMods().contains(hullmodsList[sMod2])) {
                        sModa2 = true;
                    }
                }
                while (!sModa3 && EXTRA_LIMIT > 1.4f) {
                    sMod3 = new Random().nextInt(hullmodsList.length);
                    if (!ship.getVariant().getHullMods().contains(hullmodsList[sMod3])) {
                        sModa3 = true;
                    }
                }

                if (EXTRA_LIMIT > 1.4f) {
                    if (roll(FIRST_ROLL_CHANCE)) {
                        ship.getVariant().addPermaMod(hullmodsList[sMod1], true);
                        //logAdd.info("Added " + hullmodsList[sMod1] + " S-Mod to " + ship.getName() + ", " + ship.getHullSpec().getHullNameWithDashClass());

                        if (roll(SECOND_ROLL_CHANCE)) {
                            ship.getVariant().addPermaMod(hullmodsList[sMod2], true);
                            //logAdd.info("Added " + hullmodsList[sMod2] + " S-Mod to " + ship.getName() + ", " + ship.getHullSpec().getHullNameWithDashClass());

                            if (roll(SECOND_ROLL_CHANCE)) {
                                ship.getVariant().addPermaMod(hullmodsList[sMod3], true);
                                //logAdd.info("Added " + hullmodsList[sMod3] + " S-Mod to " + ship.getName() + ", " + ship.getHullSpec().getHullNameWithDashClass());
                            }
                        }
                    }
                } else if (EXTRA_LIMIT > 0f) {
                    if (roll(FIRST_ROLL_CHANCE))
                        ship.getVariant().addPermaMod(hullmodsList[sMod1], true);
                    //logAdd.info("Added " + hullmodsList[sMod1] + " S-Mod to " + ship.getName() + ", " + ship.getHullSpec().getHullNameWithDashClass());
                    if (roll(SECOND_ROLL_CHANCE))
                        ship.getVariant().addPermaMod(hullmodsList[sMod2], true);
                    //logAdd.info("Added " + hullmodsList[sMod2] + " S-Mod to " + ship.getName() + ", " + ship.getHullSpec().getHullNameWithDashClass());
                } else {
                    if (roll(FIRST_ROLL_CHANCE)) {
                        ship.getVariant().addPermaMod(hullmodsList[sMod1], true);
                        //logAdd.info("Added " + hullmodsList[sMod1] + " S-Mod to " + ship.getName() + ", " + ship.getHullSpec().getHullNameWithDashClass());
                    }
                }

                SModded = true;
            } else if (SModded &&
                    (!ship.isAlive() || engine.isCombatOver() || ship.isRetreating())
                    && !SModdead) {
                if (ship.getVariant().getPermaMods().contains(hullmodsList[sMod1]))
                    ship.getVariant().removePermaMod(hullmodsList[sMod1]);
                //logAdd.info("Removed " + hullmodsList[sMod1] + " S-Mod from " + ship.getName() + ", " + ship.getHullSpec().getHullNameWithDashClass());

                if (ship.getVariant().getPermaMods().contains(hullmodsList[sMod2]))
                    ship.getVariant().removePermaMod(hullmodsList[sMod2]);
                //logAdd.info("Removed " + hullmodsList[sMod2] + " S-Mod from " + ship.getName() + ", " + ship.getHullSpec().getHullNameWithDashClass());

                if (ship.getVariant().getPermaMods().contains(hullmodsList[sMod3]))
                    ship.getVariant().removePermaMod(hullmodsList[sMod3]);
                //logAdd.info("Removed " + hullmodsList[sMod3] + " S-Mod from " + ship.getName() + ", " + ship.getHullSpec().getHullNameWithDashClass());

                SModdead = true;
            } /*else {
                boolean logged = false;
                while(!logged){
                    logAdd.info("Dubious activity in " + ship.getName() + ", " + ship.getHullSpec().getHullNameWithDashClass());
                    logged = true;
                }
            }*/
        }
    }

    @Override
    public void updateDifficultyCache(UNGP_SpecialistSettings.Difficulty difficulty) {
        FIRST_ROLL_CHANCE = getValueByDifficulty(0, difficulty);
        SECOND_ROLL_CHANCE = getValueByDifficulty(1, difficulty);
        EXTRA_LIMIT = getValueByDifficulty(2, difficulty);
    }

    @Override
    public float getValueByDifficulty(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return 0.6f;
        if (index == 1) return difficulty.getLinearValue(0f, 0.3f);
        if (index == 2) return difficulty.getLinearValue(0f, 1f);

        return 0f;
    }

    @Override
    public void advanceInCombat(CombatEngineAPI engine, float amount) {

    }

    @Override
    public void applyEnemyShipInCombat(float amount, ShipAPI enemy) {
        if (enemy == null) return;

        if (enemy.isFighter() || enemy.isStation() || enemy.isStationModule()) return;

        if (enemy.hasListenerOfClass(UNGPDX_ProsperousForging.Forged.class)) return;
        enemy.addListener(new UNGPDX_ProsperousForging.Forged(enemy));
    }

    @Override
    public void applyPlayerShipInCombat(float amount, CombatEngineAPI engine, ShipAPI ship) {

    }

    @Override
    public String getDescriptionParams(int index, UNGP_SpecialistSettings.Difficulty difficulty) {
        if (index == 0) return "40%";
        if (index == 1) return getPercentString(getValueByDifficulty(index, difficulty) * 100);
        if (index == 2) return String.valueOf(String.format("%.0f", getValueByDifficulty(index, difficulty)));
        return null;
    }
}
