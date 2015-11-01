package com.example.liusu.travelapp;

/**
 * Created by liusu on 22/10/15.
 */
public class Database {
    private CharSequence MBS[] = {"Marina","marina","Bay","bay","Sands","sands","Marina Bay Sands","marina bay sands"};
    private CharSequence SF[] = {"Singapore","singapore","Flyer","flyer","Singapore Flyer","singapore flyer"};
    private CharSequence VC[] = {"Vivo","vivo","City","city","Vivo City","vivo city"};
    private CharSequence RWS[] = {"Resort","resort","World","world","Sentosa","sentosa","Resort World Sentosa","resort world sentosa"};
    private CharSequence BTRT[] = {"Buddha","buddla","Tooth","tooth","Relic","relic","Temple","temple","Buddha Tooth Relic Temple","buddha toothe relic temple"};
    private CharSequence SZ[] = {"Zoo","zoo","Singapore Zoo","singapore zoo"};
    private CharSequence data[][] = {MBS,SF,VC,RWS,BTRT,SZ};

    public CharSequence[][] getData(){
        return data;
    }
}
