package com.eraether.jchess;



public class CompoundMove extends Move {
	private Move a;
	private Move b;

	public CompoundMove(Type type, Move a, Move b) {
		super(type, a.getStartingTile(), a.getEndingTile());
		setMoveA(a);
		setMoveB(b);
	}

	private void setMoveA(Move a) {
		this.a = a;
	}

	private void setMoveB(Move b) {
		this.b = b;
	}

	public Move getMoveA() {
		return a;
	}

	public Move getMoveB() {
		return b;
	}

	public Tile getFinalTile() {
		return getMoveB().getEndingTile();
	}

	public boolean isCompound() {
		return true;
	}
}