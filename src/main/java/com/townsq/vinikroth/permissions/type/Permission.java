package com.townsq.vinikroth.permissions.type;

public enum Permission {
    Nenhuma(0),
    Leitura(1),
    Escrita(2),;

    private final int value;

    Permission(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
