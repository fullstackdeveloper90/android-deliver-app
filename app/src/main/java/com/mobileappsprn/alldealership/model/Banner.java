package com.mobileappsprn.alldealership.model;

import java.io.Serializable;

/**
 * Created by Khushal on 12/04/16.
 */
public class Banner implements Serializable
{
    private String _id;

    private String sequence;

    private String target;

    private String image;

    private String type;

    public String get_id ()
    {
        return _id;
    }

    public void set_id (String _id)
    {
        this._id = _id;
    }

    public String getSequence ()
    {
        return sequence;
    }

    public void setSequence (String sequence)
    {
        this.sequence = sequence;
    }

    public String getTarget ()
    {
        return target;
    }

    public void setTarget (String target)
    {
        this.target = target;
    }

    public String getImage ()
    {
        return image;
    }

    public void setImage (String image)
    {
        this.image = image;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [_id = "+_id+", sequence = "+sequence+", target = "+target+", image = "+image+", type = "+type+"]";
    }

    /*@Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(image);
        dest.writeString(type);
        dest.writeString(sequence);
        dest.writeString(target);
    }

    private Banner(Parcel in){
        this._id = in.readString();
        this.image = in.readString();
        this.type = in.readString();
        this.sequence = in.readString();
        this.target = in.readString();
    }*/
}
