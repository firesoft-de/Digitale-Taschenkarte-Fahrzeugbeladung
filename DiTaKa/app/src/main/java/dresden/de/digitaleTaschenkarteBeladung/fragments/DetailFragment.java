package dresden.de.digitaleTaschenkarteBeladung.fragments;


import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import dresden.de.digitaleTaschenkarteBeladung.R;
import dresden.de.digitaleTaschenkarteBeladung.daggerDependencyInjection.ApplicationForDagger;
import dresden.de.digitaleTaschenkarteBeladung.data.EquipmentItem;
import dresden.de.digitaleTaschenkarteBeladung.data.ImageItem;
import dresden.de.digitaleTaschenkarteBeladung.data.TrayItem;
import dresden.de.digitaleTaschenkarteBeladung.util.Util;
import dresden.de.digitaleTaschenkarteBeladung.viewmodels.ItemViewModel;

/**
 * {@link DetailFragment} bietet eine Detailansicht für ein einzelnes Item
 */
public class DetailFragment extends Fragment {

    ItemViewModel viewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

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
        ((ApplicationForDagger) getActivity().getApplication())
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

        return view;
    }

    /**
     * Bestückt das Fragment
     * @param item
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
            TextView tvDescriptionStatic = activity.findViewById(R.id.detailDescriptionStatic);
            tvDescriptionStatic.setVisibility(View.GONE);
        }

        //Ausstattungssatz bedarfsabhängig anzeigen
        String setName = item.getMSetName();

        if (setName != ""  &&  setName != null) {
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
        }

        String position = item.getPosition();

        String[] positionParts = position.split(" - ");

        tvPosition.setText(positionParts[0]);

        for (int i = 1; i< positionParts.length; i++) {

            tvPosition.append("\n\n" + positionParts[i]);

        }

        viewModel.getImageByCatID(item.getCategoryId()).observe(this, new Observer<ImageItem>() {
            @Override
            public void onChanged(@Nullable ImageItem imageItem) {
                drawBitmap(imageItem);
            }
        });

        viewModel.getTrayItem(item.getCategoryId()).observe(this, new Observer<TrayItem>() {
            @Override
            public void onChanged(@Nullable TrayItem trayItemX) {
                if (trayItemX != null) {
                    trayItem = trayItemX;

                    if (!modifyBitmap) {
                        modifyBitmap = true;
                    }
                    else {
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

    private void drawBitmap(ImageItem imageItem) {
        if (imageItem != null) {
            this.imageItem = imageItem;
            if (!modifyBitmap) {
                modifyBitmap = true;
            }
            else {
                modifyBitmap();
            }
        }
    }

    //Zeigt die Bitmap an und modifziert sie ggf.
    private void modifyBitmap() {

        //Prüfen ob ein Bild hinterlegt ist
        if (imageItem != null) {

            //ImageView initalisieren und das Bild vom Dauerspeicher abrufen
            ImageView imageView = getActivity().findViewById(R.id.detailItemImage);
            Bitmap image = Util.openImage(imageItem, getContext());

            //Bitmap in eine modfizierbare Bitmap umwandeln
            Bitmap workBitmap = image.copy(Bitmap.Config.ARGB_8888, true);

            //Prüfen ob Koordinaten hinterlegt sind und damit eine Position eingezeichnet werden kann
            if (trayItem.getPositionCoordinates() != null) {

                //Koordinaten abrufen
                int left = trayItem.getPositionCoordinates().get(equipmentItem.getPositionIndex() * 4);
                int top = trayItem.getPositionCoordinates().get(equipmentItem.getPositionIndex() * 4 + 1);
                int right = trayItem.getPositionCoordinates().get(equipmentItem.getPositionIndex() * 4 + 2);
                int bottom = trayItem.getPositionCoordinates().get(equipmentItem.getPositionIndex() * 4 + 3);

                //Prüfen ob für diese Position keine Koordinaten hinterlegt sind. Die Koordinaten wären in diesem Fall alle 0
                if (left == 0 && top == 0 && right == 0 && bottom == 0 ) {
                    //Nichts tun
                }
                else {
                    //Canvas initailiseren
                    Canvas canvas = new Canvas(workBitmap);

                    Rect rectangle = new Rect(left, top, right, bottom);
                    Paint painter = new Paint();
                    painter.setStyle(Paint.Style.STROKE);
                    painter.setStrokeWidth(5);
                    painter.setColor(getResources().getColor(R.color.position_image_highlight));

                    canvas.drawRect(rectangle, painter);
                }

            }

            imageView.setImageBitmap(workBitmap);
            modifyBitmap = false;

        }

    }

}
