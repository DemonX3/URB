package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;

import java.util.List;

public class URB_ModPlugin extends BaseModPlugin {

    @Override
    public void onApplicationLoad() {
        List<ShipHullSpecAPI> ships = Global.getSettings().getAllShipHullSpecs();

        for (ShipHullSpecAPI ship : ships) {
            if (ship.hasTag("derelict") && !ship.getHullId().equals("station_derelict_survey_mothership") && !ship.getHullId().equals("guardian"))
                ship.addTag("dx3_derelict");
        }
    }
}
