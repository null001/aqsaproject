package com.rabita.shabab.aqsa2;

/**
 * Created by macbookair on 8/19/16.
 */
public class navDrawerItem {

    String title;
    int icon;
    int count =0;
    boolean isConuntVisble = false;
    public navDrawerItem(String title)
    {
        this.title = title;

    }

    public navDrawerItem(String title,int icon)
    {
        this.title = title;
        this.icon = icon;
    }
    public navDrawerItem(String title,int icon,int count,boolean isCountVisible)
    {
        this.title = title;
        this.icon = icon;
        this.count = count;
        this.isConuntVisble =isCountVisible;

    }

    String getTitle ()
    {
        return  title;
    }
    int getIcon ()
    {
        return icon;
    }
    int getCount()
    {
        return count;
    }
    boolean getIscountVisible()
    {
        return isConuntVisble;
    }
    void setTitle(String title)
    {
        this.title = title;
    }
    void setIcon(int icon)
    {
        this.icon = icon;
    }
    void setCount(int count)
    {
        this.count = count;
    }
    void setIsCountVisible(boolean isConuntVisble)
    {
        this.isConuntVisble = isConuntVisble;
    }


}
