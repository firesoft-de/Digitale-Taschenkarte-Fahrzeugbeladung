/*  Diese App stellt die Beladung von BOS Fahrzeugen in digitaler Form dar.
    Copyright (C) 2017  David Schlossarczyk

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    For the full license visit https://www.gnu.org/licenses/gpl-3.0.*/

package dresden.de.digitaleTaschenkarteBeladung.fragments;


import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import dresden.de.digitaleTaschenkarteBeladung.R;
import dresden.de.digitaleTaschenkarteBeladung.daggerDependencyInjection.CustomApplication;
import dresden.de.digitaleTaschenkarteBeladung.data.EquipmentItem;
import dresden.de.digitaleTaschenkarteBeladung.data.ImageItem;
import dresden.de.digitaleTaschenkarteBeladung.data.TrayItem;
import dresden.de.digitaleTaschenkarteBeladung.util.PreferencesManager;
import dresden.de.digitaleTaschenkarteBeladung.util.Util;
import dresden.de.digitaleTaschenkarteBeladung.viewmodels.ItemViewModel;

/**
 * {@link DetailFragment} bietet eine Detailansicht für ein einzelnes Item
 */
public class DetailFragment extends Fragment {

    ItemViewModel viewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    PreferencesManager pManager;


    //Marker um zu prüfen ob die Bitmap bereits modifiziert wurde
    boolean modifyBitmap = false;
    ImageItem imageItem;
    TrayItem trayItem;
    EquipmentItem equipmentItem;


    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Anweisung an Dagger, dass hier eine Injection vorgenommen wird ??
        ((CustomApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        viewModel = ViewModelProviders.of(this,viewModelFactory)
                .get(ItemViewModel.class);

        if (this.getArguments() != null) {

            Bundle args = this.getArguments();
            int itemID = args.getInt(ItemFragment.BUNDLE_TAG_DETAIL);

            viewModel.getItem(itemID).observe(this, new Observer<EquipmentItem>() {
                @Override
                public void onChanged(@Nullable EquipmentItem equipmentItem) {
                    insertData(equipmentItem);
                }
            });

        }
        else {
            throw new IllegalArgumentException("Keine Argumente weitergegeben!");
        }

        //Elevation der Cards setzen
        Drawable drawable = getResources().getDrawable(android.R.drawable.dialog_holo_light_frame);

        CardView card = view.findViewById(R.id.card1);
        card.setBackground(drawable);

        card = view.findViewById(R.id.card2);
        card.setBackground(drawable);

        //Farbe des Positionstextes an die Einstellung anpassen
        TextView positiontext = view.findViewById(R.id.detailPosition);

        positiontext.setTextColor(pManager.getPositionTextColor());

        return view;
    }

    /**
     * Füllt die GUI mit den zum gegebenen Item verfügbaren Informationen
     * @param item Das Item, dass angezeigt werden soll.
     */
    private  void insertData(EquipmentItem item) {

        Activity activity = getActivity();

        TextView tvName = activity.findViewById(R.id.detailName);
        TextView tvDescription = activity.findViewById(R.id.detailDescription);
        TextView tvSetName = activity.findViewById(R.id.detailSetName);
        TextView tvPosition = activity.findViewById(R.id.detailPosition);
        TextView tvNotes = activity.findViewById(R.id.detailAdditionalNotes);
        TextView tvSetNameStatic = activity.findViewById(R.id.detailSetNameStatic);
        TextView tvNotesStatic = activity.findViewById(R.id.detailAdditionalNotesStatic);
        TextView tvCountStatic = activity.findViewById(R.id.detailCountStatic);
        TextView tvCount = activity.findViewById(R.id.detailCount);


        ImageView imageView = activity.findViewById(R.id.detailItemImage);

        equipmentItem = item;

        tvName.setText(item.getName());

        String desc = item.getDescription();

        //Beschreibungsfeld bedarfsabhängig anzeigen
        if (!desc.equals("")) {
            tvDescription.setText(desc);
        }
        else {
            tvDescription.setVisibility(View.GONE);
        }

        //Ausstattungssatz bedarfsabhängig anzeigen
        String setName = item.getSetName();

        if (!setName.equals("")) {
            tvSetName.setText(setName);
            tvSetName.setVisibility(View.VISIBLE);
            tvSetNameStatic.setVisibility(View.VISIBLE);}
        else {
            tvSetName.setVisibility(View.GONE);
            tvSetNameStatic.setVisibility(View.GONE);
        }

        //Hinweise bedarfsabhängig anzeigen
        String additionNotes = item.getAdditionalNotes();
        int length = additionNotes.length();

        if (length == 0) {
            tvNotes.setVisibility(View.GONE);
            tvNotesStatic.setVisibility(View.GONE);
        }
        else {
            tvNotes.setText(additionNotes);
            tvNotes.setVisibility(View.VISIBLE);
            tvNotesStatic.setVisibility(View.VISIBLE);

            //Ein unbekannter Fehler führt zum Abschneiden der letzten Zeile. Als Workaround/Hotfix wird hier manuell eine weitere Zeile eingefügt.
            //tvNotes.append("\nx");

        }

        // Anzahl anzeigen
        if (item.getCount() == 0 || item.getCount() == -1) {
            tvCount.setVisibility(View.GONE);
            tvCountStatic.setVisibility(View.GONE);
        }
        else {
            tvCount.setText(String.valueOf(item.getCount()));
        }

        // Position anzeigen
        String position = item.getPosition();

        // Kodierte Position splitten und jeweils in eine neue Zeile schreiben
        String[] positionParts = position.split(" - ");
        tvPosition.setText(positionParts[0]);

        // Neue Zeilen einfügen
        for (int i = 1; i< positionParts.length; i++) {
            tvPosition.append("\n\n" + positionParts[i]);
        }

        // Positionsbild einfügen
        // Im folgenden müssen das Imageitem und das Trayitem abgerufen werden, da in beiden eine benötigte Information liegt (Bildpfad und Positionsmarkierung).
        // Da diese beiden Abfragen parallel durchgeführt werden, wird eine von beiden früher fertig sein und entsprechend muss auf die andere gewartet werden.
        // Dazu wird die Variable modifyBitmap auf true gesetzt. Die Abfrage die zuerst fertig ist, setzt sie auf true. Die zweite Abfrage weiß dann, dass sofort mit der Bitmap Modifikation begonnen werden kann.
        viewModel.getImageByCatID(item.getCategoryId()).observe(this, new Observer<ImageItem>() {
            @Override
            public void onChanged(@Nullable ImageItem imageItemX) {
                if (imageItemX != null) {
                    imageItem = imageItemX;
                    if (!modifyBitmap) {
                        //Trayitem wurde noch nicht geladen. Marker auf true setzen und warten bis Trayitem geladen wurden (LiveItem!)
                        modifyBitmap = true;
                    }
                    else {
                        //Wenn das Trayitem schon geladen wurden, kann direkt die Bildbearbeitung beginnen
                        modifyBitmap();
                    }
                }
            }
        });

        viewModel.getTrayItem(item.getCategoryId()).observe(this, new Observer<TrayItem>() {
            @Override
            public void onChanged(@Nullable TrayItem trayItemX) {
                if (trayItemX != null) {
                    trayItem = trayItemX;

                    if (!modifyBitmap) {
                        //ImageItem noch nicht geladen. Marker auf true setzen und warten bis ImageItem geladen wurden
                        modifyBitmap = true;
                    }
                    else {
                        //Wenn das ImageItem schon geladen wurde direkt die Bildbearbeitung beginnen

                        modifyBitmap();
                    }
                }
            }
        });
    }

    @Override
    public void onDetach() {
        imageItem = null;
        equipmentItem = null;
        trayItem = null;
        super.onDetach();
    }


    /**
     * Zeigt eine Bitmap im Fragment an und fügt zu dieser, falls vorhanden, die Positionsmarkierung hinzu
     */
    private void modifyBitmap() {

        //Prüfen ob ein Bild hinterlegt ist
        if (imageItem != null && !imageItem.getPath().equals("-1")) {

            //ImageView initalisieren und das Bild vom Dauerspeicher abrufen
            ImageView imageView = getActivity().findViewById(R.id.detailItemImage);
            Bitmap image = Util.openImage(imageItem, getContext());

            //Bitmap in eine modfizierbare Bitmap umwandeln
            Bitmap workBitmap = image.copy(Bitmap.Config.ARGB_8888, true);

            //Prüfen ob Koordinaten hinterlegt sind und damit eine Position eingezeichnet werden kann und prüfen ob im Item eine Position hinterlegt ist
            if (trayItem.getPositionCoordinates() != null && equipmentItem.getPositionIndex() > -1) {

                int coordinates[] = new int[0];
                try {
                    coordinates = trayItem.getCoordinates(equipmentItem.getPositionIndex());
                } catch (NumberFormatException e) {
                    // Es tritt ein Fehler beim Umwandeln des Positionsstrings in der Datenbank in eine Zahl auf.
                    e.printStackTrace();
                    Snackbar.make(getActivity().findViewById(R.id.MainFrame),"Es ist ein Fehler beim Abrufen der Markierungsdaten aufgetreten. Markierungen werden wahrscheinlich nicht richtig angezeigt!",Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }

                if (coordinates[0] != -1) {
                    //Canvas initailiseren
                    Canvas canvas = new Canvas(workBitmap);

                    Rect rectangle = new Rect(coordinates[0], coordinates[1], coordinates[2], coordinates[3]);
                    Paint painter = new Paint();
                    painter.setStyle(Paint.Style.STROKE);
                    painter.setStrokeWidth(5);

                    if (pManager != null) {
                        painter.setColor(pManager.getPositionMarkColor());
                    }
                    else {
                        painter.setColor(getResources().getColor(R.color.position_image_highlight));
                    }

                    canvas.drawRect(rectangle, painter);
                }
            }
            imageView.setImageBitmap(workBitmap);
            modifyBitmap = false;
        }
    }
}
