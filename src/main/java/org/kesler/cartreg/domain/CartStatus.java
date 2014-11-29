package org.kesler.cartreg.domain;

/**
 * Статусы картриджей
 */
public enum CartStatus {
    NONE("Не определен"),
    NEW("Новый"),
    FILLED("Заправлен"),
    INSTALLED("Установлен"),
    EMPTY("Пустой"),
    DEFECT("Неисправен"),
    WITHDRAW("Списан");

    private String desc;

    CartStatus(String desc) {
        this.desc = desc;
    }

    public String getDesc() { return desc; }
}
