package com.example.heartbeatapp;

public class HistoryUser implements Comparable<HistoryUser>{
        public String notify,time;

    public HistoryUser(String notify, String time) {
        this.notify = notify;
        this.time = time;
    }

    @Override
    public int compareTo(HistoryUser o) {
        if(time==o.time)
        return 0;
        else if(time.compareTo(o.time)<0) return 1;
        else return -1;
    }
}
