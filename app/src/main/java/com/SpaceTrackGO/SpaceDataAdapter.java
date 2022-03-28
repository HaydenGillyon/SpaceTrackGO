package com.SpaceTrackGO;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.net.URL;
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

  class ItemOnClickListener implements OnClickListener {
    @Override
    public void onClick(final View view) {
      int itemPosition = recyclerView.getChildLayoutPosition(view);
      SpaceData item = spaceDataItems.get(itemPosition);
      openWebPage(item.getHyperlink());
    }
  }

  class ItemOnLongClickListener implements OnLongClickListener {
    @Override
    public boolean onLongClick(final View view) {
      longClickPosition = recyclerView.getChildLayoutPosition(view);
      return false;
    }
  }

  private final OnClickListener onClickListener = new ItemOnClickListener();
  private final OnLongClickListener onLongClickListener = new ItemOnLongClickListener();
  private RecyclerView recyclerView;
  private List<SpaceData> spaceDataItems;
  private int longClickPosition;

  public SpaceDataAdapter(List<SpaceData> spaceDataItems) {
    this.spaceDataItems = spaceDataItems;
  }

  public void shareItem(Context context) {
    SpaceData spaceDataItem = spaceDataItems.get(longClickPosition);
    String heading;
    SpaceData.DataType dataType = spaceDataItem.getDataType();
    switch (dataType) {
      case CME:
        heading = context.getString(R.string.CME_heading);
        break;
      case GST:
        heading = context.getString(R.string.GST_heading);
        break;
      case FLR:
        heading = context.getString(R.string.FLR_heading);
        break;
      default:
        heading = "space data";
    }
    String shareText = "Look at this " + heading + " I found on SpaceTrackGO! Here's the"
        + " official report: " + spaceDataItem.getHyperlink();

    // Send share intent
    Intent sendIntent = new Intent(Intent.ACTION_SEND);
    sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
    sendIntent.setType("text/plain");
    Intent shareIntent = Intent.createChooser(sendIntent, null);
    if (shareIntent.resolveActivity(context.getPackageManager()) != null) {
      context.startActivity(shareIntent);
    }
  }

  public void openWebPage(URL url) {
    Uri webpage;
    webpage = Uri.parse(url.toString());
    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
    Context context = recyclerView.getContext();
    if (intent.resolveActivity(context.getPackageManager()) != null) {
      context.startActivity(intent);
    }
  }

  @Override
  public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
    this.recyclerView = recyclerView; // Obtain reference to the RecyclerView
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);

    View spaceDataView = inflater.inflate(R.layout.item_space, parent, false);
    spaceDataView.setOnClickListener(onClickListener);
    spaceDataView.setOnLongClickListener(onLongClickListener);
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
