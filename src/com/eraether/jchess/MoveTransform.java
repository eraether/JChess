package com.eraether.jchess;



public class MoveTransform extends Move {
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