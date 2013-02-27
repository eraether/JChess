package com.eraether.jchess;

class ChessPiece {
	private int player;
	private Type type;
	private boolean hasMoved = false;

	public enum Type {
		QUEEN, KNIGHT, BISHOP, ROOK, KING, PAWN
	};

	public ChessPiece(int player, Type type) {
		this(player, type, false);
	}

	public ChessPiece(int player, Type type, boolean hasMoved) {
		setPlayer(player);
		setType(type);
		setHasMoved(hasMoved);
	}
	private void setHasMoved(boolean hasMoved) {
		this.hasMoved = hasMoved;
	}

	private void setPlayer(int player) {
		this.player = player;
	}

	private void setType(Type type) {
		this.type = type;
	}

	public int getPlayer() {
		return this.player;
	}

	public Type getType() {
		return this.type;
	}

	public void move() {
		hasMoved = true;
	}

	public boolean hasMoved() {
		return hasMoved;
	}

	@Override
	public String toString() {
		return "Player " + getPlayer() + "'s " + getType();
	}
}