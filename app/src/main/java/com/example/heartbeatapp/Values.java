package com.example.heartbeatapp;

public class Values {
    int bpm;
    int spo2;

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public Values(int bpm, int spo2) {
        this.bpm = bpm;
        this.spo2 = spo2;
    }

    public void setSpo2(int spo2) {
        this.spo2 = spo2;
    }

    public int getBpm() {
        return bpm;
    }

    public int getSpo2() {
        return spo2;
    }
}
