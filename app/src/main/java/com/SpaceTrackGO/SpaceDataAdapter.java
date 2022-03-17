package com.SpaceTrackGO;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SpaceDataAdapter extends RecyclerView.Adapter<SpaceDataAdapter.ViewHolder> {
  public class ViewHolder extends RecyclerView.ViewHolder {
    public ImageView spaceImage;
    public TextView itemHeading;
    public TextView itemDesc;

    public ViewHolder(View itemView) {
      super(itemView);
      spaceImage = (ImageView) itemView.findViewById(R.id.spaceImage);
      itemHeading = (TextView) itemView.findViewById(R.id.itemHeading);
      itemDesc = (TextView) itemView.findViewById(R.id.itemDesc);
    }
  }

  private List<SpaceData> spaceDataItems;

  public SpaceDataAdapter(List<SpaceData> spaceDataItems) {
    this.spaceDataItems = spaceDataItems;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);

    View spaceDataView = inflater.inflate(R.layout.item_space, parent, false);
    return new ViewHolder(spaceDataView);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    SpaceData spaceDataItem = spaceDataItems.get(position);

    ImageView spaceImage = holder.spaceImage;
    TextView itemHeading = holder.itemHeading;
    TextView itemDesc = holder.itemDesc;

    Context context = spaceImage.getContext();

    int img;
    String heading;
    SpaceData.DataType dataType = spaceDataItem.getDataType();
    // Default if null (viewing saved data)
    dataType = dataType == null ? SpaceData.DataType.CME : dataType;
    switch (dataType) {
      case CME:
        img = R.drawable.cme_icon;
        heading = context.getString(R.string.CME_heading);
        break;
      case GST:
        img = R.drawable.gst_icon;
        heading = context.getString(R.string.GST_heading);
        break;
      case FLR:
        img = R.drawable.flr_icon;
        heading = context.getString(R.string.FLR_heading);
        break;
      default:
        img = R.drawable.cme_icon;  // Not possible
        heading = context.getString(R.string.CME_heading);
    }
    spaceImage.setImageDrawable(
        ContextCompat.getDrawable(context, img));

    String dateString;
    try {
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
      Date date = simpleDateFormat.parse(spaceDataItem.getDateAndTime());
      if (date == null) throw new ParseException("Date is null", 0);
      // Get locale specific date and time representation
      java.text.DateFormat dateFormat = DateFormat.getDateFormat(context);
      dateString = dateFormat.format(date);
    } catch (ParseException e) {
      e.printStackTrace();
      dateString = "";
      Toast.makeText(context, "A problem occurred when getting the date and time information.",
          Toast.LENGTH_SHORT).show();
    }
    String fullHeading = dateString + " " + heading;
    itemHeading.setText(fullHeading);
    itemDesc.setText(spaceDataItem.getDescription());
  }

  @Override
  public int getItemCount() {
    return spaceDataItems.size();
  }
}
