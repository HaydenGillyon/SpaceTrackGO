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

/**
 * Holds the data that the Event List activity's RecyclerView shows, and keeps it updated when
 * changes occur such as the user pressing refresh or selecting a different data type.
 */
public class SpaceDataAdapter extends RecyclerView.Adapter<SpaceDataAdapter.ViewHolder> {
  /**
   * Holds a view containing data on a space event while it is within the user's focus.
   */
  public class ViewHolder extends RecyclerView.ViewHolder {
    public ImageView spaceImage;
    public TextView itemHeading;
    public TextView itemDesc;

    /**
     * Constructs the ViewHolder by setting its access to the different layout components of a data
     * item's view.
     * @param itemView view that defines the layout of each data item in the RecyclerView
     */
    public ViewHolder(View itemView) {
      super(itemView);
      spaceImage = (ImageView) itemView.findViewById(R.id.spaceImage);
      itemHeading = (TextView) itemView.findViewById(R.id.itemHeading);
      itemDesc = (TextView) itemView.findViewById(R.id.itemDesc);
    }
  }

  /**
   * Listens for clicks on the RecyclerView to determine which item was selected and open its
   * hyperlink in a web browser.
   */
  class ItemOnClickListener implements OnClickListener {
    @Override
    public void onClick(final View view) {
      int itemPosition = recyclerView.getChildLayoutPosition(view);
      SpaceData item = spaceDataItems.get(itemPosition);
      openWebPage(item.getHyperlink());
    }
  }

  /**
   * Listens for long clicks on the RecyclerView to determine which item was selected and save this
   * value for use when responding to context menu actions.
   */
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

  /**
   * Constructs the SpaceDataAdapter by assigning the space data items that it initially holds.
   * @param spaceDataItems
   */
  public SpaceDataAdapter(List<SpaceData> spaceDataItems) {
    this.spaceDataItems = spaceDataItems;
  }

  /**
   * Sends a share intent for the space data item that was selected with a long click. This will
   * be called if the user chooses to share an item using the context menu which must be accessed
   * with a long click, so the variable longClickPosition will be set.
   * @param context the application context
   */
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

  /**
   * Sends an implicit intent to view the webpage found at a URL. This will open a browser on the
   * device in most cases.
   * @param url uniform resource locator (URL) to be viewed
   */
  public void openWebPage(URL url) {
    Uri webpage;
    webpage = Uri.parse(url.toString());
    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
    Context context = recyclerView.getContext();
    if (intent.resolveActivity(context.getPackageManager()) != null) {
      context.startActivity(intent);
    }
  }

  /**
   * Attaches the SpaceDataAdapter to the RecyclerView.
   * @param recyclerView the RecyclerView to be attached
   */
  @Override
  public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
    this.recyclerView = recyclerView; // Obtain reference to the RecyclerView
  }

  /**
   * Inflates the layout of the space data item view into a new ViewHolder and places this inside
   * the parent ViewGroup. This enables the ViewHolders to represent the data from space events.
   * @param parent ViewGroup to house the created ViewHolder
   * @param viewType the view type of the new View created within the ViewHolder
   * @return the created ViewHolder
   */
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

  /**
   * Binds the data from a space data object to a ViewHolder within the SpaceDataAdapter's
   * RecyclerView.
   * @param holder the ViewHolder to have the space data bound to it
   * @param position the number of the space data item stored within the adapter to be shown
   */
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

  /**
   * Returns the number of space data items the adapter is holding.
   * @return the count of items within the spaceDataItems list
   */
  @Override
  public int getItemCount() {
    return spaceDataItems.size();
  }
}
