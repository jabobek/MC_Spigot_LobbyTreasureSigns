package cz.minicraft.minicraftlobbytreasure.managers;

public enum TreasureType {
    EASY,
    NORMAL,
    HARD;

    public static TreasureType fromInt(int i) {
        switch (i) {
            case 1:
                return EASY;
            case 2:
                return NORMAL;
            case 3:
                return HARD;
            default:
                return null;
        }
    }
}
