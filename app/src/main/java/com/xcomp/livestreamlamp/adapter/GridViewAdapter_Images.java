package com.xcomp.livestreamlamp.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.stfalcon.frescoimageviewer.ImageViewer;
import com.xcomp.livestreamlamp.Entity.ImageModel;
import com.xcomp.livestreamlamp.Entity.SessionModel;
import com.xcomp.livestreamlamp.R;
import com.xcomp.livestreamlamp.WebserviceGeneralManage.WebserviceInfors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class GridViewAdapter_Images extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int ITEM_VIEW_TYPE_SESSION = 0;
    public static final int ITEM_VIEW_TYPE_IMAGE = 1;

    private final Activity mContext;
    private ArrayList<ImageModel> imageModels;
    private ArrayList<SessionModel> sessionModels;
    private int itemCount = 0;
    Random r = new Random();

    public class ImageModelViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTakenTime;
        public ImageView thumbnail;

        public ImageModelViewHolder(View view) {
            super(view);

            thumbnail = (ImageView) view.findViewById(R.id.imageview);
            textViewTakenTime = (TextView) view.findViewById(R.id.textview_taken_time);
        }
    }

    public class SessionModelViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewStartTime;
        public TextView textViewEndTime;
        public TextView textViewSessionInfors;

        public SessionModelViewHolder(View view) {
            super(view);

            textViewStartTime = (TextView) view.findViewById(R.id.textview_start_time);
            textViewEndTime = (TextView) view.findViewById(R.id.textview_end_time);
            textViewSessionInfors = (TextView) view.findViewById(R.id.textview_session_infors);
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.grid_view_item_image_layout, parent, false);
//
//        return new ImageModelViewHolder(itemView);
        View itemView;
        if(viewType == ITEM_VIEW_TYPE_SESSION) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.grid_view_item_session_layout, parent, false);
            return new SessionModelViewHolder(itemView);
        }
        else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.grid_view_item_image_layout, parent, false);
            return new ImageModelViewHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

//        final ImageModel imageModel = this.imageModels.get(position);
//
//        ImageModelViewHolder viewHolder =  (ImageModelViewHolder) holder;
//        viewHolder.textViewTakenTime.setText(imageModel.getCreatedDate());
//
//        final String imageLink = WebserviceInfors.base_host_service + WebserviceInfors.get_image + imageModel.getImageName();
//        Picasso.get()
//                .load(imageLink)
//                .into(viewHolder.thumbnail, new com.squareup.picasso.Callback(){
//
//                    @Override
//                    public void onSuccess() {
//                        //do smth when picture is loaded successfully
////                         Toast.makeText(mContext, "Image loaded", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onError(Exception e) {
//                        //do smth when there is picture loading error
////                         Toast.makeText(mContext, "Image load fail", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//        viewHolder.thumbnail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new ImageViewer.Builder(GridViewAdapter_Images.this.mContext, Arrays.asList(imageLink))
//                        .setStartPosition(0)
//                        .show();
//            }
//        });
        int viewType = calculateViewTypeForPosition(position);


//        if(viewType == ITEM_VIEW_TYPE_SESSION) {
        if(holder instanceof SessionModelViewHolder) {
            SessionModel sessionModel = this.getSessionModelForPosition(position);
            if (sessionModel != null) {
                SessionModelViewHolder viewHolder =  (SessionModelViewHolder) holder;
                viewHolder.textViewStartTime.setText("Bắt đầu: " + sessionModel.getStart_time());
                viewHolder.textViewEndTime.setText("Kết thúc: " + sessionModel.getEnd_time());
                viewHolder.textViewSessionInfors.setText("Thông tin: đọc được " + sessionModel.getPage_count() + " trang sách");
            }
        }
        else if(holder instanceof ImageModelViewHolder) {
            ImageModel imageModel = this.getImageModelForPosition(position);
            if(imageModel != null) {
                ImageModelViewHolder viewHolder = (ImageModelViewHolder) holder;
                viewHolder.textViewTakenTime.setText(imageModel.getCreatedDate());
                final String imageLink = WebserviceInfors.base_host_service + WebserviceInfors.get_cut_image + imageModel.getImageName();
                Glide.with(this.mContext)
                        .load(imageLink)
                        .into(viewHolder.thumbnail);
                viewHolder.thumbnail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new ImageViewer.Builder(mContext, Arrays.asList(imageLink))
                                .setStartPosition(0)
                                .show();
                    }
                });
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = calculateViewTypeForPosition(position);
        Log.e("GirdViewAdapter_Images", "getItemViewType: " + viewType + "position: " + position);
        return viewType;
    }

    private int calculateViewTypeForPosition(int currentPosition) {
        int sessionPosition = 0;
        if(currentPosition == 0) {
            return ITEM_VIEW_TYPE_SESSION;
        }
        else {
            while (currentPosition > 0) {
                currentPosition -= 1;
                if(currentPosition == 0) {
                    if(sessionModels.get(sessionPosition).getImageModelsCount() > 0) {
                        return ITEM_VIEW_TYPE_IMAGE;
                    }
                    else {
                        return ITEM_VIEW_TYPE_SESSION;
                    }
                }
                else if(currentPosition < sessionModels.get(sessionPosition).getImageModelsCount()){
                    return ITEM_VIEW_TYPE_IMAGE;
                }
                else {
                    currentPosition -= sessionModels.get(sessionPosition).getImageModelsCount();
                    sessionPosition += 1;
                }
            }
        }

        return ITEM_VIEW_TYPE_SESSION;
    }

    private SessionModel getSessionModelForPosition(int position) {
        int sessionCount = 0;
        if (position == 0) {
            return sessionModels.get(0);
        }
        else {
            while (position > 0) {
                position -= 1;
                position -= sessionModels.get(sessionCount).getImageModelsCount();
                sessionCount += 1;
                if(position == 0) {
                    return sessionModels.get(sessionCount);
                }
            }
        }
        return null;
    }

    private ImageModel getImageModelForPosition(int position) {
        int sessionCount = 0;
        while (position > 0) {
            position -= 1;
            if (position < sessionModels.get(sessionCount).getImageModelsCount()) {
                return  sessionModels.get(sessionCount).getImageModels().get(position);
            }
            position -= sessionModels.get(sessionCount).getImageModelsCount();
            sessionCount += 1;
        }
        return null;
    }
    // 1
    public GridViewAdapter_Images(Activity context) {
        this.mContext = context;
        this.imageModels = null;
        this.sessionModels = null;
    }

    public void setImageModelList(ArrayList<ImageModel> dataList) {
        this.imageModels = dataList;
        notifyDataSetChanged();
    }

    public void setSessionModelList(ArrayList<SessionModel> dataList) {
        this.sessionModels = dataList;
        itemCount = this.sessionModels.size();
        for(SessionModel sessionModel: this.sessionModels) {
            itemCount += sessionModel.getImageModelsCount();
        }
        notifyDataSetChanged();
    }
    // 2
    @Override
    public int getItemCount() {

        return itemCount;
//        if(sessionModels != null) {
//            return sessionModels.size();
//        }
//        return 0;
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

//    // 4
//    @Override
//    public Object getItem(int position) {
//        return null;
//    }
//
//    // 5
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        // 2
//        if (convertView == null) {
//            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
//            convertView = layoutInflater.inflate(R.layout.grid_view_item_image_layout, null);
//
//            float heightInDp = 100;
//            int height = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightInDp, mContext.getResources().getDisplayMetrics()));
//            convertView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, height));
//        }
//
//        // 3
//        final ImageView imageView = (ImageView)convertView.findViewById(R.id.imageview);
//        final TextView nameTextView = (TextView)convertView.findViewById(R.id.textview_taken_time);
//
//
//        // 4
////        imageView.setImageResource(book.getImageResource());
//        nameTextView.setText(imageModel.getCreatedDate());
//
//        final String imageLink = WebserviceInfors.base_host_service + WebserviceInfors.get_image + imageModel.getImageName();
//        Log.e("Adapter: ", imageLink );
//        Log.e("Imagename: ", imageModel.getImageName() );
//
//
//        return convertView;
//
//    }
}