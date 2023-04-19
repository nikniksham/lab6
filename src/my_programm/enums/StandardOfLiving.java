package my_programm.enums;

public enum StandardOfLiving {
    VERY_HIGH,
    HIGH,
    VERY_LOW,
    ULTRA_LOW,
    NIGHTMARE;

    public static StandardOfLiving getById(int id) {
        switch (id) {
            case 1: return VERY_HIGH;
            case 2: return HIGH;
            case 3: return VERY_LOW;
            case 4: return ULTRA_LOW;
            case 5: return NIGHTMARE;
            default: throw new RuntimeException("Уровень жизни может быть от 1 (круто) до 5 (не круто)");
        }
    }

    public static int getIdByName(StandardOfLiving standardOfLiving) {
        switch (standardOfLiving) {
            case VERY_HIGH: return 1;
            case HIGH: return 2;
            case VERY_LOW: return 3;
            case ULTRA_LOW: return 4;
            case NIGHTMARE: return 5;
            default: return 0;
        }
    }
}
