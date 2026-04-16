package com.example.skynet;

public class Mesaj {
    public static final String COL_ODALAR = "odalar";
    public static final String COL_MESAJLAR = "mesajlar";
    public static final String FIELD_METIN = "metin";
    public static final String FIELD_GONDEREN = "gonderen";
    public static final String FIELD_TARIH = "tarih";
    public static final String FIELD_ODA_ADI = "odaAdi";
    public static final String FIELD_DUZENLENDI = "duzenlendi";

    private String gonderen;
    private String metin;
    private String id;
    private boolean duzenlendi;
    private long zaman;

    public Mesaj() { }

    public Mesaj(String gonderen, String metin, String id, boolean duzenlendi, long zaman) {
        this.gonderen = gonderen;
        this.metin = metin;
        this.id = id;
        this.duzenlendi = duzenlendi;
        this.zaman = zaman;
    }

    public String getGonderen() { return gonderen; }
    public String getMetin() { return metin; }
    public String getId() { return id; }
    public boolean isDuzenlendi() { return duzenlendi; }
    public long getZaman() { return zaman; }
}