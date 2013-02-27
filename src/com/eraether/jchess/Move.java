package com.eraether.jchess;
public class Move {
	public enum Type {
		MOVE, ATTACK, TRANSFORM, CASTLE, MOVE_PROMOTE, EN_PASSANT
	};

	public Type type;
	public Tile startingPosition;
	public Tile endingPosition;

	public Move(Type type, Tile a, Tile b) {
		setType(type);
		setStartingPosition(a);
		setEndingPosition(b);
	}

	private void setType(Type type) {
		this.type = type;
	}

	private void setStartingPosition(Tile a) {
		this.startingPosition = a;
	}

	private void setEndingPosition(Tile b) {
		this.endingPosition = b;
	}

	public Type getType() {
		return type;
	}

	public Tile getStartingTile() {
		return startingPosition;
	}

	public Tile getEndingTile() {
		return endingPosition;
	}

	public Tile getFinalTile() {
		return endingPosition;
	}

	public String toString() {
		return getType() + " (" + getStartingTile() + ") -> ("
				+ getEndingTile() + ")";
	}

	public boolean isCompound() {
		return false;
	}
}