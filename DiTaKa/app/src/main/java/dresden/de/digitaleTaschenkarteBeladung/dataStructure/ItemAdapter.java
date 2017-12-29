package dresden.de.digitaleTaschenkarteBeladung.dataStructure;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import dresden.de.digitaleTaschenkarteBeladung.R;
import dresden.de.digitaleTaschenkarteBeladung.data.EquipmentItem;

public class ItemAdapter extends ArrayAdapter<EquipmentItem> {

    private static  final String LOG_TAG="ItemAdapter_LOG";


    public ItemAdapter(Activity context, ArrayList<EquipmentItem> items) {

        super(context,0,items);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View listItemView = convertView;

            if (listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            }

            EquipmentItem currentItem = getItem(position);

            TextView tvItemName = (TextView) listItemView.findViewById(R.id.listItem_tv_name);

            tvItemName.setText(currentItem.getName());

            TextView tvItemPosition = (TextView) listItemView.findViewById(R.id.listItem_tv_position);
            tvItemPosition.setText(currentItem.getPosition());

            //Weiterpfeil ausblenden
            ImageView continueArrow = (ImageView) listItemView.findViewById(R.id.imageView2);
//            continueArrow.setVisibility(View.GONE);

            //ImageView ausblenden
            ImageView previewImage = (ImageView) listItemView.findViewById(R.id.imageView);
            previewImage.setVisibility(View.GONE);

            return listItemView;

    }


}