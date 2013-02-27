class CompoundMove extends Move {
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

class MoveTransform extends Move {
	private ChessPiece.Type transformation;

	public MoveTransform(Type type, Tile a, Tile b,
			ChessPiece.Type transformation) {
		super(type, a, b);
		setTransformation(transformation);
	}

	public void setTransformation(ChessPiece.Type transformation) {
		this.transformation = transformation;
	}

	public ChessPiece.Type getTransformation() {
		return transformation;
	}
}

class Move {
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