package com.github.smallinger.copperagebackport.block.shelf;

import net.minecraft.util.StringRepresentable;

/**
 * Represents the connection state of a shelf block in a chain.
 * Shelves can connect horizontally when powered to form chains up to 3 blocks.
 */
public enum SideChainPart implements StringRepresentable {
    UNCONNECTED("unconnected"),
    RIGHT("right"),
    CENTER("center"),
    LEFT("left");

    private final String name;

    SideChainPart(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.getSerializedName();
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public boolean isConnected() {
        return this != UNCONNECTED;
    }

    public boolean isConnectionTowards(SideChainPart part) {
        return this == CENTER || this == part;
    }

    public boolean isChainEnd() {
        return this != CENTER;
    }

    public SideChainPart whenConnectedToTheRight() {
        switch (this) {
            case UNCONNECTED:
            case LEFT:
                return LEFT;
            case RIGHT:
            case CENTER:
            default:
                return CENTER;
        }
    }

    public SideChainPart whenConnectedToTheLeft() {
        switch (this) {
            case UNCONNECTED:
            case RIGHT:
                return RIGHT;
            case CENTER:
            case LEFT:
            default:
                return CENTER;
        }
    }

    public SideChainPart whenDisconnectedFromTheRight() {
        switch (this) {
            case UNCONNECTED:
            case LEFT:
                return UNCONNECTED;
            case RIGHT:
            case CENTER:
            default:
                return RIGHT;
        }
    }

    public SideChainPart whenDisconnectedFromTheLeft() {
        switch (this) {
            case UNCONNECTED:
            case RIGHT:
                return UNCONNECTED;
            case CENTER:
            case LEFT:
            default:
                return LEFT;
        }
    }
}
