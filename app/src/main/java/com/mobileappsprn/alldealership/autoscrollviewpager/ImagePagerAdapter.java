package com.mobileappsprn.alldealership.autoscrollviewpager;//package com.mobileappsprn.alldealership.autoscrollviewpager;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//
//import com.mobileappsprn.alldealership.R;
//
//import java.util.List;
//
///**
// * Created by sri on 18/07/16.
// */
//public class ImagePagerAdapter extends BaseAdapter {
//
//        private LayoutInflater inflater;
//        public List<Integer> imageResIdList;
//
//        public ImagePagerAdapter(Context context) {
//            super();
//            inflater = LayoutInflater.from(context);
//        }
//
//        @Override
//        public int getCount() {
//            return ListUtils.getSize(imageResIdList);
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return isEmpty(imageResIdList) ? null : imageResIdList.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder holder;
//            if (convertView == null) {
//                convertView = inflater.inflate(R.layout.image_list_item, null);
//                holder = new ViewHolder();
//                holder.imageView = (ImageView)convertView.findViewById(R.id.arrowIcon);
//                convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder)convertView.getTag();
//            }
//            holder.imageView.setImageResource(imageResIdList.get(position));
//            return convertView;
//        }
//
//        public List<Integer> getImageList() {
//            return imageResIdList;
//        }
//
//    public void setImageList(List<Integer> imageList) {
//        this.imageResIdList = imageList;
//    }
//
//    /**
//     * ViewHolder
//     *
//     * @author Trinea 2012-11-22
//     */
//    static class ViewHolder {
//
//        ImageView imageView;
//    }
//
//    private boolean isEmpty(List<Integer> imageList) {
//        return ListUtils.isEmpty(imageList);
//    }
//}
//}
