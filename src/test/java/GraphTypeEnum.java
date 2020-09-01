public enum GraphTypeEnum {
    DENSITY("density", "Proton Density [p/cc]"),
    SPEED("speed", "Bulk Speed [km/s]"),
    BZ_GSM("bz_gsm", "Bz [nT]");

    String dbKey;
    String printName;

    GraphTypeEnum(String dbKey, String printName) {
        this.dbKey = dbKey;
        this.printName = printName;
    }
}