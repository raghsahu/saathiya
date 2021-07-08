package com.prometteur.sathiya.model.chatmodel;


public class Message{
    public String idSender;
    public String idReceiver;
    public String text;
    public String type;
    public long timestamp;

    /*public String getTimestamp()
    {
        *//*SimpleDateFormat dateFormatdef = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date=null;
        try {
            date.setTime(timestamp);
        } catch (Exception e) {
            e.printStackTrace();
        }*//*
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        return dateFormat.format(timestamp);
    }*/
}