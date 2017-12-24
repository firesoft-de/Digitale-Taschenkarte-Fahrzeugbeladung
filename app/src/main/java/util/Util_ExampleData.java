package util;

import java.util.ArrayList;

import dresden.de.blueproject.dataStructure.EquipmentItem;
import dresden.de.blueproject.dataStructure.TrayItem;

public class Util_ExampleData {

    private static final String LOG_TAG="Util_ExampleData_LOG";

    public static ArrayList<TrayItem> dummyDataTray () {

        ArrayList<TrayItem> trays = new ArrayList<TrayItem>();

        trays.add(new TrayItem(0,"Geräteraum 1","Erster Geräteraum, Fahrerseite vorne"));
        trays.add(new TrayItem(1,"Geräteraum 2","Zweiter Geräteraum, Beifahrerseite vorne"));
        trays.add(new TrayItem(2,"Geräteraum 3","Dritter Geräteraum, Fahrerseite mittig"));
        trays.add(new TrayItem(3,"Geräteraum 4","Vierter Geräteraum, Beifahrerseite mittig"));

        return trays;

    }

    public static ArrayList<EquipmentItem> dummyDataEquipment() {

        ArrayList<EquipmentItem> items = new ArrayList<>();

        items.add(new EquipmentItem(0,"Hydraulikschere", "Die Hydraulikschere gehört zum hydraulischen Rettungsgerät und wird vor allem während der Fahrzeugrettung verwendet.",
                "Hydraulisches Rettungsgerät","Geräteraum 1 - Mitte - Ausziehfach Hydraulisches Rettungsgerät",0,new String[] {"hydraulik", "rettungsgerät","schere"}));

        items.add(new EquipmentItem(1,"Hydraulikspreizer", "Der Hydraulikspreizer gehört zum hydraulischen Rettungsgerät und wird vor allem während der Fahrzeugrettung verwendet.",
                "Hydraulisches Rettungsgerät","Geräteraum 1 - Mitte - Ausziehfach Hydraulisches Rettungsgerät",0,new String[] {"hydraulik", "rettungsgerät","spreizer"}));

        items.add(new EquipmentItem(2,"Atemschutzgerät", "Atemschutzgeräte werden zum Arbeiten in toxischen Atmosphären verwendet.",
                "PSA-Atemschutz","Geräteraum 4 - Mitte - Schnellausrüstungsgestell Atemschutz",3,new String[] {"atemschutz", "agt","ag"}));

        return items;
    }


}
