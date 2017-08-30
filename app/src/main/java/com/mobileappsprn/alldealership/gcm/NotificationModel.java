package com.mobileappsprn.alldealership.gcm;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Khushal on 26/05/16.
 */
public class NotificationModel implements Parcelable{
    private String message;

    private String _id;

    private String id_customer;

    private String image;

    protected NotificationModel(Parcel in) {
        message = in.readString();
        _id = in.readString();
        id_customer = in.readString();
        image = in.readString();
    }

    public NotificationModel(String message, String image){
        this._id = "";
        this.id_customer = "";
        this.message = message;
        this.image = image;
    }

    public static final Creator<NotificationModel> CREATOR = new Creator<NotificationModel>() {
        @Override
        public NotificationModel createFromParcel(Parcel in) {
            return new NotificationModel(in);
        }

        @Override
        public NotificationModel[] newArray(int size) {
            return new NotificationModel[size];
        }
    };

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public String get_id ()
    {
        return _id;
    }

    public void set_id (String _id)
    {
        this._id = _id;
    }

    public String getId_customer ()
    {
        return id_customer;
    }

    public void setId_customer (String id_customer)
    {
        this.id_customer = id_customer;
    }

    public String getImage ()
    {
        return image;
    }

    public void setImage (String image)
    {
        this.image = image;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [message = "+message+", _id = "+_id+", id_customer = "+id_customer+", image = "+image+"]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeString(_id);
        dest.writeString(id_customer);
        dest.writeString(image);
    }
}
