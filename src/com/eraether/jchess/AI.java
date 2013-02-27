package com.eraether.jchess;
import java.util.ArrayList;
import java.util.List;


class AI {
	private int player;

	public AI(int player) {
		setPlayer(player);
	}

	private void setPlayer(int player) {
		this.player = player;
	}

	public int getPlayer() {
		return this.player;
	}

	// (4,2)->(3,3)
	public Move generateMove(Game game) {
		// System.out.println("AI "+getPlayer()+": Calling generate move.");
		ArrayList<Move> moves = game.generateAllValidMoves();
		MultiMap<Integer, Move> rankedMoves = new MultiMap<Integer, Move>();
		for (Move move : moves) {
			rankedMoves.put(this.generateHeuristicForMove(game, move), move);
		}
		if (rankedMoves.isEmpty())
			return null;
		List<Integer> keySet = rankedMoves.keySet();
		int highest = keySet.get(0);
		for (int x = 0; x < keySet.size(); x++) {
			if (keySet.get(x) > highest)
				highest = keySet.get(x);
		}
		List<Move> highestRankedMoves = rankedMoves.get(highest);
		Move highestMove = highestRankedMoves.get((int) (0 * highestRankedMoves
				.size()));

		System.out.println(highestMove + " generated with weight :" + highest);
		return highestMove;
	}

	private int getLowestUnitCost(ArrayList<ChessPiece> pieces) {
		int lowest = getScoreForPiece(pieces.get(0));
		for (ChessPiece piece : pieces) {
			if (this.getScoreForPiece(piece) < lowest)
				lowest = this.getScoreForPiece(piece);
		}
		return lowest;
	}

	private int getHighestUnitCost(ArrayList<ChessPiece> pieces) {
		int highest = 0;
		for (ChessPiece piece : pieces) {
			if (this.getScoreForPiece(piece) > highest)
				highest = this.getScoreForPiece(piece);
		}
		return highest;
	}

	public ArrayList<ChessPiece> getChessPiecesFromTiles(ArrayList<Tile> tiles,
			ChessBoard board) {
		ArrayList<ChessPiece> pieces = new ArrayList<ChessPiece>();
		for (Tile t : tiles) {
			if (board.hasPieceAtLocation(t.getX(), t.getY()))
				pieces.add(board.getPiece(t));
		}
		return pieces;
	}

	private int generateHeuristicForMove(Game game, Move move) {
		return generateHeuristicForMove(game, move, 0, game.getCurrentPlayer());
	}

	private int generateHeuristicForMove(Game game, Move move, int level,
			int currentPlayer) {
		game.evaluateMove(move);
		if (game.discoverCheckMate()) {
			game.undoMove();
			return 100;
		}
		if (game.discoverStaleMate()) {
			game.undoMove();
			return 10;
		}
		Tile finalTile = move.getFinalTile();
		ArrayList<Tile> tiles = game.getTilesAttacking(finalTile);
		int lowestUnitCost = 0;
		if (tiles.size() != 0)
			lowestUnitCost = this.getLowestUnitCost(getChessPiecesFromTiles(
					tiles, game.getChessBoard()));
		int currentScore = lowestUnitCost
				- getScoreForPiece(game.getChessBoard().getPiece(
						move.getFinalTile())) + 1;
		if (move.getType() == Move.Type.MOVE) {
		} else if (move.getType() == Move.Type.ATTACK) {
			currentScore += getScoreForPiece(game.getChessBoard().getPiece(
					move.getEndingTile()));
		} else if (move.getType() == Move.Type.CASTLE) {
			CompoundMove combo = (CompoundMove) move;
		} else if (move.getType() == Move.Type.TRANSFORM) {
			// MoveTransform transform = (MoveTransform) move;
			// return getScoreForPiece(transform.getTransformation());
		} else if (move.getType() == Move.Type.EN_PASSANT) {
			// return 2;
		} else if (move.getType() == Move.Type.MOVE_PROMOTE) {
			// CompoundMove comboMove = (CompoundMove) move;
			// Move normalMove = comboMove.getMoveA();
			// MoveTransform transform = (MoveTransform) comboMove.getMoveB();
			// return evaluateMove(game, normalMove)
			// + evaluateMove(game, transform);
		}
		if (level < 4) {
			int rating = generateHeuristicForMove(game, move, level + 1,
					currentPlayer);
			currentScore += game.getCurrentPlayer() == currentPlayer ? rating
					: -rating;
		}
		game.undoMove();
		return currentScore;
		//
		// if (move.getType() == Move.Type.ATTACK) {
		// rankedMoves.put(
		// getScoreForPiece(game.getChessBoard().getPiece(
		// move.endingPosition)), move);
		// attacks.add(move);
		// } else if (move.getType() == Move.Type.MOVE_PROMOTE) {
		//
		// }
	}

	private int getScoreForPiece(ChessPiece piece) {
		return getScoreForPiece(piece.getType());
	}

	private int getScoreForPiece(ChessPiece.Type piece) {
		if (piece == null)
			return 0;

		switch (piece) {
		case PAWN:
			return 1;
		case KNIGHT:
			return 4;
		case BISHOP:
			return 5;
		case ROOK:
			return 10;
		case KING:
			return 4;
		case QUEEN:
			return 20;
		default:
			return 0;
		}
	}
}