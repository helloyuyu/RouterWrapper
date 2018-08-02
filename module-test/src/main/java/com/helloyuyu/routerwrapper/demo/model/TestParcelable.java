package com.helloyuyu.routerwrapper.demo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author xjs
 * @date 2018/4/12 10:16
 */

public class TestParcelable implements Parcelable{
    private String name;
    private long extra;
    private Long aLong;

    protected TestParcelable(Parcel in) {
        name = in.readString();
        extra = in.readLong();
        if (in.readByte() == 0) {
            aLong = null;
        } else {
            aLong = in.readLong();
        }
    }

    public static final Creator<TestParcelable> CREATOR = new Creator<TestParcelable>() {
        @Override
        public TestParcelable createFromParcel(Parcel in) {
            return new TestParcelable(in);
        }

        @Override
        public TestParcelable[] newArray(int size) {
            return new TestParcelable[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeLong(extra);
        if (aLong == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(aLong);
        }
    }
}
