package com.example.liusu.travelapp.functiontwo;

public class Database {
    private CharSequence MBS[] = {"Marina","marina","Bay","bay","Sands","sands","Marina Bay Sands","marina bay sands"};
    private CharSequence SF[] = {"Singapore","singapore","Flyer","flyer","Singapore Flyer","singapore flyer"};
    private CharSequence VC[] = {"Vivo","vivo","City","city","Vivo City","vivo city"};
    private CharSequence RWS[] = {"Resort","resort","World","world","Sentosa","sentosa","Resort World Sentosa","resort world sentosa"};
    private CharSequence BTRT[] = {"Buddha","buddla","Tooth","tooth","Relic","relic","Temple","temple","Buddha Tooth Relic Temple","buddha toothe relic temple"};
    private CharSequence SZ[] = {"Zoo","zoo","Singapore Zoo","singapore zoo"};
    private CharSequence GBTB[] = {"gardens", "bay", "Gardens by the Bay", "gardens by the bay"};
    private CharSequence GMRB[] = {"g-max", "bungy", "G-Max Reverse Bungy", "gmax reverse bungy"};
    private CharSequence FCP[] = {"fort", "canning", "park", "Fort Canning Park", "fort canning park"};

    private CharSequence data[][] = {MBS,SF,VC,RWS,BTRT,SZ, GBTB, GMRB, FCP};

    public CharSequence[][] getData(){
        return data;
    }
    public String[] getList(){
        return new String[] {MBS[MBS.length-2].toString(),  SF[SF.length-2].toString(),VC[VC.length-2].toString(),RWS[RWS.length-2].toString(),BTRT[BTRT.length-2].toString(),SZ[SZ.length-2].toString(),GBTB[GBTB.length-2].toString(),GMRB[GMRB.length-2].toString(),FCP[FCP.length-2].toString()};
    }
}
