package com.eraether.jchess;
import java.util.ArrayList;

class ChessBoard {
	// column / row order
	private ChessPiece[][] board;

	public ChessBoard() {
		clearBoard();
	}

	public void clearBoard() {
		board = new ChessPiece[8][8];
	}

	public boolean inEndzone(int x, int y) {
		return x == 0 || x == getHeight() - 1;
	}

	public ChessPiece getPiece(Tile t) {
		return this.getPiece(t.getX(), t.getY());
	}

	public Tile getKingTile(int player) {
		ArrayList<Tile> pieces = getAllPiecesForPlayer(player);
		for (Tile t : pieces) {
			if (getPiece(t).getType() == ChessPiece.Type.KING)
				return t;
		}
		return null;
	}

	public ArrayList<Tile> getAllPiecesOfTypeForPlayer(int player,
			ChessPiece.Type type) {
		ArrayList<Tile> tiles = getAllPiecesForPlayer(player);
		ArrayList<Tile> validTiles = new ArrayList<Tile>();
		for (Tile t : tiles) {
			if (getPiece(t.getX(), t.getY()).getType() == type)
				validTiles.add(t);
		}
		return validTiles;
	}

	public ArrayList<Tile> getAllPiecesForPlayer(int player) {
		ArrayList<Tile> tiles = new ArrayList<Tile>();
		for (int x = 0; x < getHeight(); x++) {
			for (int y = 0; y < getWidth(); y++) {
				if (hasPieceAtLocation(x, y)
						&& this.getPiece(x, y).getPlayer() == player) {
					tiles.add(this.getTile(x, y));
				}
			}
		}
		return tiles;
	}

	public void move(Tile start, Tile end) {
		placePiece(this.getPiece(start.getX(), start.getY()), end.getX(),
				end.getY());
		removePiece(start.getX(), start.getY());
	}

	public void generateDefaultBoard() {
		// placePiece(new ChessPiece(0, ChessPiece.Type.PAWN), 0, 7);

		placePiece(new ChessPiece(0, ChessPiece.Type.ROOK), 7, 0);
		placePiece(new ChessPiece(0, ChessPiece.Type.KNIGHT), 7, 1);
		placePiece(new ChessPiece(0, ChessPiece.Type.BISHOP), 7, 2);
		placePiece(new ChessPiece(0, ChessPiece.Type.QUEEN), 7, 3);
		placePiece(new ChessPiece(0, ChessPiece.Type.KING), 7, 4);
		placePiece(new ChessPiece(0, ChessPiece.Type.BISHOP), 7, 5);
		placePiece(new ChessPiece(0, ChessPiece.Type.KNIGHT), 7, 6);
		placePiece(new ChessPiece(0, ChessPiece.Type.ROOK), 7, 7);

		for (int x = 0; x < 8; x++) {
			placePiece(new ChessPiece(0, ChessPiece.Type.PAWN), 6, x);
		}

		placePiece(new ChessPiece(1, ChessPiece.Type.ROOK), 0, 0);
		placePiece(new ChessPiece(1, ChessPiece.Type.KNIGHT), 0, 1);
		placePiece(new ChessPiece(1, ChessPiece.Type.BISHOP), 0, 2);
		placePiece(new ChessPiece(1, ChessPiece.Type.QUEEN), 0, 3);
		placePiece(new ChessPiece(1, ChessPiece.Type.KING), 0, 4);
		placePiece(new ChessPiece(1, ChessPiece.Type.BISHOP), 0, 5);
		placePiece(new ChessPiece(1, ChessPiece.Type.KNIGHT), 0, 6);
		placePiece(new ChessPiece(1, ChessPiece.Type.ROOK), 0, 7);

		for (int x = 0; x < 8; x++) {
			placePiece(new ChessPiece(1, ChessPiece.Type.PAWN), 1, x);
		}

	}

	public boolean isValidLocation(int xPosition, int yPosition) {
		if (xPosition < 0 || yPosition < 0 || xPosition >= getHeight()
				|| yPosition >= getWidth())
			return false;
		return true;
	}

	public int getHeight() {
		return board.length;
	}

	public int getWidth() {
		return board[0].length;
	}

	public void removePiece(int xPosition, int yPosition) {
		board[xPosition][yPosition] = null;
	}

	public void placePiece(ChessPiece piece, int xPosition, int yPosition) {
		board[xPosition][yPosition] = piece;
	}

	public boolean hasPieceAtLocation(int x, int y) {
		return board[x][y] != null;
	}

	public ChessPiece getPiece(int x, int y) {
		if (isValidLocation(x, y))
			return board[x][y];
		else
			return null;
	}

	public Tile getTile(int x, int y) {
		return new Tile(x, y);
	}

}