package com.bluebed.strip;

class Strip {
    public final StripType type;
    public final int length; // z
    public final int startZ;
    public final int endZ;

    public Strip(StripType type, int startZ, int length) {
        this.type = type;
        this.startZ = startZ;
        this.length = length;
        this.endZ = startZ + length - 1;
    }
}