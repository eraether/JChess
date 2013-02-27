package com.eraether.jchess;
import java.util.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ListIterator;

public class Game {
	public static final int PLAYER_ONE = 0;
	public static final int PLAYER_TWO = 1;
	private List<GameState> gameStateStack = new ArrayList<GameState>();
	private int activeFrame = 0;

	public Game(ChessBoard chessBoard) {
		this(new GameState(chessBoard, 0, 0));
	}

	public Game(GameState gameState) {
		this(createGameStateList(gameState));
	}

	public Game(List<GameState> gameStates) {
		setMoveStack(gameStates);
	}

	private void setMoveStack(List<GameState> moveStates) {
		this.gameStateStack = moveStates;
		setActiveFrame(this.gameStateStack.size() - 1);
	}

	private void setActiveFrame(int frame) {
		this.activeFrame = frame;
	}

	private int getActiveFrame() {
		return activeFrame;
	}

	private static List<GameState> createGameStateList(GameState state) {
		List<GameState> stateList = new ArrayList<GameState>();
		stateList.add(state);
		return stateList;
	}

	private boolean needTransformation(int player) {
		ArrayList<Tile> unitTiles = getChessBoard().getAllPiecesForPlayer(
				player);
		for (Tile t : unitTiles) {
			if (tileNeedsTransformation(t))
				return true;
		}
		return false;
	}

	private boolean tileNeedsTransformation(Tile t) {
		ChessPiece piece = this.getChessBoard().getPiece(t);
		if (piece == null || piece.getType() != ChessPiece.Type.PAWN)
			return false;

		if (piece.getPlayer() == Game.PLAYER_ONE
				&& t.getX() == getChessBoard().getHeight() - 1)
			return true;
		if (piece.getPlayer() == Game.PLAYER_TWO && t.getX() == 0)
			return true;
		return false;
	}

	public ArrayList<Tile> getTilesAttacking(Tile tile, int player) {
		ArrayList<Tile> attackingTiles = new ArrayList<Tile>();
		ArrayList<Tile> tiles = getChessBoard().getAllPiecesForPlayer(player);
		for (Tile t : tiles) {
			ArrayList<Move> attacks = this.generateAttacksForPiece(t);
			for (Move m : attacks) {
				if (m.getEndingTile().equals(tile))
					attackingTiles.add(m.getEndingTile());
			}
		}
		return attackingTiles;
	}

	public ArrayList<Tile> getTilesAttacking(Tile tile) {
		ChessPiece piece = this.getChessBoard().getPiece(tile);
		return getTilesAttacking(tile, getOpponent(piece.getPlayer()));
	}

	public boolean isTileUnderAttack(Tile tile, int player) {
		return getTilesAttacking(tile, player).size() != 0;
	}

	public boolean isTileUnderAttack(Tile tile) {
		// ChessPiece piece = this.getChessBoard().getPiece(tile);
		// ArrayList<Tile> tiles = getChessBoard().getAllPiecesForPlayer(
		// Game.getOpponent(piece.getPlayer()));
		// for (Tile t : tiles) {
		// ArrayList<Move> attacks = this.generateAttacksForPiece(t);
		// for (Move m : attacks) {
		// if (m.getEndingTile().equals(tile))
		// return true;
		// }
		// }
		// return false;
		return getTilesAttacking(tile).size() != 0;
	}

	public Move getPreviousMove() {
		return getStateStack().get(getActiveFrame()).getAppliedMove();
	}

	public boolean kingUnderCheck(int player) {
		Tile kingTile = getChessBoard().getKingTile(player);
		return isTileUnderAttack(kingTile);
	}

	// evaluate after switch turn
	public boolean discoverCheckMate() {
		if (!kingUnderCheck(getCurrentPlayer()))
			return false;

		ArrayList<Move> moves = getAllMovesForPlayer(getCurrentPlayer());
		boolean checkMate = true;
		// Tile kingTile = getChessBoard().getKingTile(getCurrentPlayer());
		for (Move m : moves) {
			// getStateStack().add(this.duplicateCurrentGameState());
			evaluateMove(m);
			boolean kingUnderAttack = kingUnderCheck(this.getOpponent());
			// getStateStack().remove(getStateStack().size() - 1);
			popState();
			if (!kingUnderAttack) {
				checkMate = false;
				break;
			}
		}
		return checkMate;
	}

	private ArrayList<Move> getAllMovesForPlayer(int player) {
		ArrayList<Tile> tiles = getChessBoard().getAllPiecesForPlayer(player);
		ArrayList<Move> moves = new ArrayList<Move>();
		for (Tile t : tiles) {
			moves.addAll(this.generateMovesForTile(t));
		}
		return moves;
	}

	private List<GameState> getStateStack() {
		return gameStateStack;
	}

	private GameState getCurrentState() {
		return getStateStack().get(getActiveFrame());
	}

	private void pushState(GameState state) {
		setActiveFrame(getActiveFrame() + 1);
		while (getStateStack().size() <= getActiveFrame())
			getStateStack().add(null);
		getStateStack().set(getActiveFrame(), state);
	}

	private GameState popState() {
		GameState current = getCurrentState();
		this.getStateStack().remove(getCurrentState());

		setActiveFrame(getActiveFrame() - 1);
		return current;
	}

	private GameState duplicateCurrentGameState() {
		ChessBoard currentBoard = getChessBoard();
		ChessBoard newBoard = new ChessBoard();
		for (int x = 0; x < currentBoard.getHeight(); x++) {
			for (int y = 0; y < currentBoard.getWidth(); y++) {
				if (currentBoard.hasPieceAtLocation(x, y)) {
					ChessPiece piece = currentBoard.getPiece(x, y);
					newBoard.placePiece(
							new ChessPiece(piece.getPlayer(), piece.getType(),
									piece.hasMoved()), x, y);
				}
			}
		}
		return new GameState(newBoard, getCurrentTurn(), getElapsedTurns());
	}

	public int getCurrentPlayer() {
		return getCurrentState().getCurrentTurn();
	}

	public void applyMove(Move move) {
		evaluateMove(move);
		boolean kingUnderCheckAfterMove = kingUnderCheck(this.getOpponent());
		if (kingUnderCheckAfterMove) {
			System.out.println("Need to get out of check!");
			this.popState();
			return;
		}
		System.out.println(move);

		if (kingUnderCheck(getCurrentPlayer()))
			System.out.println("Player " + this.getCurrentPlayer()
					+ "'s King is under check.");
		else if (discoverStaleMate())
			System.out.println("Stalemate.");

		if (discoverCheckMate())
			System.out.println("Checkmate.");

	}

	public boolean discoverStaleMate() {
		if (kingUnderCheck(getCurrentPlayer()))
			return false;

		ArrayList<Move> moves = getAllMovesForPlayer(getCurrentPlayer());
		boolean staleMate = true;
		for (Move m : moves) {
			evaluateMove(m);
			boolean kingUnderAttack = kingUnderCheck(this.getOpponent());
			popState();
			if (!kingUnderAttack) {
				staleMate = false;
				break;
			}
		}
		return staleMate;
	}

	public void evaluateMove(Move incomingMove) {
		// if (true)
		// throw (new RuntimeException());

		GameState state = this.duplicateCurrentGameState();
		state.setAppliedMove(incomingMove);
		pushState(state);
		ArrayList<Move> movesToBeEvaluated = new ArrayList<Move>();

		// if (incomingMove.getType() == Move.Type.CASTLE
		// || incomingMove.getType() == Move.Type.MOVE_PROMOTE
		// || incomingMove.getType() == Move.Type.EN_PASSANT) {
		if (incomingMove.isCompound()) {
			movesToBeEvaluated.add(((CompoundMove) incomingMove).getMoveA());
			movesToBeEvaluated.add(((CompoundMove) incomingMove).getMoveB());
		} else {
			movesToBeEvaluated.add(incomingMove);
		}

		for (Move move : movesToBeEvaluated) {

			if (move.type == Move.Type.MOVE || move.type == Move.Type.ATTACK) {
				getChessBoard().getPiece(move.getStartingTile()).move();
				getChessBoard()
						.move(move.startingPosition, move.endingPosition);
			}

			if (move.type == Move.Type.TRANSFORM) {
				MoveTransform transform = (MoveTransform) move;
				ChessPiece piece = getChessBoard().getPiece(
						move.getStartingTile());

				getChessBoard()
						.placePiece(
								new ChessPiece(piece.getPlayer(),
										transform.getTransformation(),
										piece.hasMoved()),
								move.getEndingTile().getX(),
								move.getEndingTile().getY());
			}
		}

		// if (!needTransformation(getCurrentPlayer()))
		switchTurns();
	}

	public ChessBoard getChessBoard() {
		return this.getCurrentState().getChessBoard();
	}

	public ArrayList<Move> generateAllValidMoves() {
		GameState state = this.duplicateCurrentGameState();
		state.setAppliedMove(this.getPreviousMove());

		Game game = new Game(state);
		ArrayList<Tile> tiles = game.getChessBoard().getAllPiecesForPlayer(
				getCurrentPlayer());
		ArrayList<Move> moves = new ArrayList<Move>();
		for (Tile t : tiles)
			moves.addAll(game.generateMovesForTile(t));
		ListIterator<Move> moveIterator = moves.listIterator();
		while (moveIterator.hasNext()) {
			Move m = moveIterator.next();
			game.evaluateMove(m);
			if (game.kingUnderCheck(game.getOpponent()))
				moveIterator.remove();
			game.popState();
		}
		return moves;
	}

	// Taking into account transformations, etc.
	// TODO: Complete this method
	public ArrayList<Move> generateValidMovesForTile(Tile t) {
		return generateMovesForTile(t);
	}

	public ArrayList<Move> generateMovesForTile(Tile t) {
		ChessPiece piece = getChessBoard().getPiece(t);
		if (piece == null)
			return null;
		ArrayList<Move> allMoves = generateNormalMovesForPiece(t);
		allMoves.addAll(generateAttacksForPiece(t));
		allMoves.addAll(generateCastleMovesForPiece(t));
		return allMoves;
	}

	public ArrayList<Move> generateCastleMovesForPiece(Tile t) {
		ChessPiece piece = getChessBoard().getPiece(t);
		if (piece.getType() != ChessPiece.Type.KING || piece.hasMoved()
				|| kingUnderCheck(piece.getPlayer()))
			return new ArrayList<Move>();

		// know king has not moved at this point
		ArrayList<Move> castleMoves = new ArrayList<Move>();
		ArrayList<Tile> rookTiles = getChessBoard()
				.getAllPiecesOfTypeForPlayer(piece.getPlayer(),
						ChessPiece.Type.ROOK);

		for (Tile rookTile : rookTiles) {
			ChessPiece rook = getChessBoard().getPiece(rookTile);
			if (rook.hasMoved())
				continue;
			// rooks have not moved

			ArrayList<Point> rightCollisionArray = walkUntilCollision(rookTile,
					0, 1, true);
			if (rightCollisionArray.isEmpty() == false) {
				ArrayList<Point> leftKingCollisionPoints = walkUntilCollision(
						t, 0, -1, false);

				Tile rightCollision = getChessBoard().getTile(
						rightCollisionArray.get(0).x,
						rightCollisionArray.get(0).y);
				if (rightCollision.equals(t)) {
					Move a = new Move(Move.Type.MOVE, t, getChessBoard()
							.getTile(t.getX(), t.getY() - 2));
					Move b = new Move(Move.Type.MOVE, rookTile, getChessBoard()
							.getTile(rookTile.getX(), t.getY() - 1));
					castleMoves.add(new CompoundMove(Move.Type.CASTLE, a, b));
				}
			}
			ArrayList<Point> leftCollisionArray = walkUntilCollision(rookTile,
					0, -1, true);
			if (leftCollisionArray.isEmpty() == false) {
				ArrayList<Point> rightKingCollisionPoints = walkUntilCollision(
						t, 0, 1, false);

				Tile leftCollision = getChessBoard().getTile(
						leftCollisionArray.get(0).x,
						leftCollisionArray.get(0).y);
				if (leftCollision.equals(t)) {
					Move a = new Move(Move.Type.MOVE, t, getChessBoard()
							.getTile(t.getX(), t.getY() + 2));
					Move b = new Move(Move.Type.MOVE, rookTile, getChessBoard()
							.getTile(rookTile.getX(), t.getY() + 1));
					castleMoves.add(new CompoundMove(Move.Type.CASTLE, a, b));
				}
			}
		}
		ListIterator<Move> moveIterator = castleMoves.listIterator();
		while (moveIterator.hasNext()) {
			Move m = moveIterator.next();
			Move kingMove = ((CompoundMove) m).getMoveA();
			Point delta = kingMove.getEndingTile().getDelta(
					kingMove.getStartingTile());
			Tile crossingTile = getChessBoard().getTile(t.getX() + delta.x / 2,
					t.getY() + delta.y / 2);
			Move intoFireMove = new Move(Move.Type.MOVE, t, crossingTile);

			ArrayList<Move> checkMoves = new ArrayList<Move>();
			checkMoves.add(m);
			checkMoves.add(intoFireMove);
			boolean underCheck = false;
			for (Move move : checkMoves) {
				evaluateMove(move);
				if (kingUnderCheck(piece.getPlayer()))
					underCheck = true;
				popState();
			}
			if (underCheck)
				moveIterator.remove();
		}
		return castleMoves;
	}

	public ArrayList<Move> generateNormalMovesForPiece(Tile t) {
		ChessPiece piece = getChessBoard().getPiece(t);
		ArrayList<Move> availableMoves = new ArrayList<Move>();

		if (piece.getType() == ChessPiece.Type.PAWN) {
			ArrayList<Point> availablePoints = new ArrayList<Point>();
			if (piece.getPlayer() == Game.PLAYER_TWO) {
				if (piece.hasMoved())
					availablePoints
							.addAll(walkUntilCollision(t, 1, 0, false, 1));
				else
					availablePoints
							.addAll(walkUntilCollision(t, 1, 0, false, 2));
			} else {
				if (piece.hasMoved())
					availablePoints.addAll(walkUntilCollision(t, -1, 0, false,
							1));
				else
					availablePoints.addAll(walkUntilCollision(t, -1, 0, false,
							2));
			}
			for (Point p : availablePoints) {
				Move move = new Move(Move.Type.MOVE, t, this.getChessBoard()
						.getTile(p.x, p.y));
				if (getChessBoard().inEndzone(p.x, p.y)) {
					for (int x = 0; x < ChessPiece.Type.values().length; x++) {
						if (x != ChessPiece.Type.PAWN.ordinal()
								|| x != ChessPiece.Type.KING.ordinal())
							availableMoves.add(new CompoundMove(
									Move.Type.MOVE_PROMOTE, move,
									new MoveTransform(Move.Type.TRANSFORM, move
											.getEndingTile(), move
											.getEndingTile(), ChessPiece.Type
											.values()[x])));
					}
				} else
					availableMoves.add(move);
			}

		}

		if (piece.getType() == ChessPiece.Type.KNIGHT) {
			availableMoves.add(new Move(Move.Type.MOVE, t, getChessBoard()
					.getTile(t.getX() - 2, t.getY() + 1)));
			availableMoves.add(new Move(Move.Type.MOVE, t, getChessBoard()
					.getTile(t.getX() - 2, t.getY() - 1)));
			availableMoves.add(new Move(Move.Type.MOVE, t, getChessBoard()
					.getTile(t.getX() + 2, t.getY() + 1)));
			availableMoves.add(new Move(Move.Type.MOVE, t, getChessBoard()
					.getTile(t.getX() + 2, t.getY() - 1)));
			availableMoves.add(new Move(Move.Type.MOVE, t, getChessBoard()
					.getTile(t.getX() - 1, t.getY() - 2)));
			availableMoves.add(new Move(Move.Type.MOVE, t, getChessBoard()
					.getTile(t.getX() + 1, t.getY() - 2)));
			availableMoves.add(new Move(Move.Type.MOVE, t, getChessBoard()
					.getTile(t.getX() - 1, t.getY() + 2)));
			availableMoves.add(new Move(Move.Type.MOVE, t, getChessBoard()
					.getTile(t.getX() + 1, t.getY() + 2)));
		}
		if (piece.getType() == ChessPiece.Type.KING) {
			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					availableMoves
							.add(new Move(Move.Type.MOVE, t, getChessBoard()
									.getTile(t.getX() + x, t.getY() + y)));
				}
			}
		}
		if (piece.getType() == ChessPiece.Type.ROOK
				|| piece.getType() == ChessPiece.Type.QUEEN) {
			// move up
			HashSet<Point> allEmptyLocations = new HashSet<Point>();

			allEmptyLocations.addAll(walkUntilCollision(t, 1, 0, false));
			allEmptyLocations.addAll(walkUntilCollision(t, -1, 0, false));
			allEmptyLocations.addAll(walkUntilCollision(t, 0, 1, false));
			allEmptyLocations.addAll(walkUntilCollision(t, 0, -1, false));

			for (Point p : allEmptyLocations) {
				availableMoves.add(new Move(Move.Type.MOVE, t, getChessBoard()
						.getTile(p.x, p.y)));
			}
		}
		if (piece.getType() == ChessPiece.Type.BISHOP
				|| piece.getType() == ChessPiece.Type.QUEEN) {

			HashSet<Point> allEmptyLocations = new HashSet<Point>();

			allEmptyLocations.addAll(walkUntilCollision(t, 1, 1, false));
			allEmptyLocations.addAll(walkUntilCollision(t, 1, -1, false));
			allEmptyLocations.addAll(walkUntilCollision(t, -1, 1, false));
			allEmptyLocations.addAll(walkUntilCollision(t, -1, -1, false));

			for (Point p : allEmptyLocations) {
				availableMoves.add(new Move(Move.Type.MOVE, t, getChessBoard()
						.getTile(p.x, p.y)));
			}
		}

		removeInvalidMoves(availableMoves);

		ListIterator<Move> moves = availableMoves.listIterator();
		while (moves.hasNext()) {
			Move m = moves.next();
			if (getChessBoard().hasPieceAtLocation(m.getEndingTile().getX(),
					m.getEndingTile().getY()))
				moves.remove();

		}
		return availableMoves;
	}

	private ArrayList<Point> walkUntilCollision(Tile t, int incX, int incY,
			boolean includeCollision, int maxSteps) {
		ArrayList<Point> collisions = walkUntilCollision(t, incX, incY,
				includeCollision);
		while (collisions.size() > maxSteps)
			collisions.remove(collisions.size() - 1);
		return collisions;
	}

	private ArrayList<Point> walkUntilCollision(Tile t, int incX, int incY,
			boolean includeCollision) {
		if (incX == 0 && incY == 0) {
			System.err.println("Infinite Loop - returning null");
			return null;
		}
		int currentX = t.getX();
		int currentY = t.getY();
		boolean collided = false;
		ArrayList<Point> points = new ArrayList<Point>();
		while (!collided) {
			currentX += incX;
			currentY += incY;
			if (!getChessBoard().isValidLocation(currentX, currentY))
				break;

			if (!getChessBoard().hasPieceAtLocation(currentX, currentY)) {
				if (!includeCollision)
					points.add(new Point(currentX, currentY));
			} else {
				if (includeCollision)
					points.add(new Point(currentX, currentY));
				break;
			}
		}
		return points;
	}

	private void removeInvalidMoves(ArrayList<Move> moves) {
		ListIterator<Move> iterator = moves.listIterator();
		while (iterator.hasNext()) {
			Move m = iterator.next();
			if (!getChessBoard().isValidLocation(m.getEndingTile().getX(),
					m.getEndingTile().getY()))
				iterator.remove();
		}
	}

	public ArrayList<Move> generateAttacksForPiece(Tile t) {
		ArrayList<Move> availableAttacks = new ArrayList<Move>();
		ChessPiece piece = getChessBoard().getPiece(t);
		if (piece.getType() == ChessPiece.Type.PAWN) {
			ArrayList<Point> availablePoints = new ArrayList<Point>();

			if (piece.getPlayer() == Game.PLAYER_TWO) {
				availablePoints.add(new Point(t.getX() + 1, t.getY() + 1));
				availablePoints.add(new Point(t.getX() + 1, t.getY() - 1));
			} else {

				availablePoints.add(new Point(t.getX() - 1, t.getY() + 1));
				availablePoints.add(new Point(t.getX() - 1, t.getY() - 1));
			}

			for (Point p : availablePoints) {
				Move move = new Move(Move.Type.ATTACK, t, this.getChessBoard()
						.getTile(p.x, p.y));
				if (getChessBoard().inEndzone(p.x, p.y)) {
					for (int x = 0; x < ChessPiece.Type.values().length; x++) {
						if (x != ChessPiece.Type.PAWN.ordinal()
								|| x != ChessPiece.Type.KING.ordinal())
							availableAttacks.add(new CompoundMove(
									Move.Type.MOVE_PROMOTE, move,
									new MoveTransform(Move.Type.TRANSFORM, move
											.getEndingTile(), move
											.getEndingTile(), ChessPiece.Type
											.values()[x])));
					}
				} else
					availableAttacks.add(move);

			}
			// En Passant
			Move move = this.getPreviousMove();
			if (move != null) {
				if (move.getType() == Move.Type.MOVE
						&& getChessBoard().getPiece(move.getEndingTile())
								.getType() == ChessPiece.Type.PAWN) {
					int dX = move.getEndingTile().getX()
							- move.getStartingTile().getX();
					int dY = move.getEndingTile().getY()
							- move.getStartingTile().getY();

					if (Math.abs(dX) >= 2) {
						Tile attackPoint = getChessBoard().getTile(
								move.getStartingTile().getX() + dX / 2,
								move.getStartingTile().getY());
						for (Point p : availablePoints) {
							Tile attackTile = getChessBoard().getTile(p.x, p.y);
							if (attackPoint.equals(attackTile)) {
								Move attack = new Move(Move.Type.ATTACK, t,
										move.getEndingTile());
								Move finalMove = new Move(Move.Type.MOVE,
										move.getEndingTile(), attackTile);
								availableAttacks
										.add(new CompoundMove(
												Move.Type.EN_PASSANT, attack,
												finalMove));
							}
						}
					}
				}
			}
		}

		if (piece.getType() == ChessPiece.Type.KNIGHT) {
			availableAttacks.add(new Move(Move.Type.ATTACK, t, getChessBoard()
					.getTile(t.getX() - 2, t.getY() + 1)));
			availableAttacks.add(new Move(Move.Type.ATTACK, t, getChessBoard()
					.getTile(t.getX() - 2, t.getY() - 1)));
			availableAttacks.add(new Move(Move.Type.ATTACK, t, getChessBoard()
					.getTile(t.getX() + 2, t.getY() + 1)));
			availableAttacks.add(new Move(Move.Type.ATTACK, t, getChessBoard()
					.getTile(t.getX() + 2, t.getY() - 1)));
			availableAttacks.add(new Move(Move.Type.ATTACK, t, getChessBoard()
					.getTile(t.getX() - 1, t.getY() - 2)));
			availableAttacks.add(new Move(Move.Type.ATTACK, t, getChessBoard()
					.getTile(t.getX() + 1, t.getY() - 2)));
			availableAttacks.add(new Move(Move.Type.ATTACK, t, getChessBoard()
					.getTile(t.getX() - 1, t.getY() + 2)));
			availableAttacks.add(new Move(Move.Type.ATTACK, t, getChessBoard()
					.getTile(t.getX() + 1, t.getY() + 2)));
		}
		if (piece.getType() == ChessPiece.Type.KING) {
			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					availableAttacks
							.add(new Move(Move.Type.ATTACK, t, getChessBoard()
									.getTile(t.getX() + x, t.getY() + y)));
				}
			}
		}
		if (piece.getType() == ChessPiece.Type.ROOK
				|| piece.getType() == ChessPiece.Type.QUEEN) {
			// move up
			HashSet<Point> allEmptyLocations = new HashSet<Point>();

			allEmptyLocations.addAll(walkUntilCollision(t, 1, 0, true));
			allEmptyLocations.addAll(walkUntilCollision(t, -1, 0, true));
			allEmptyLocations.addAll(walkUntilCollision(t, 0, 1, true));
			allEmptyLocations.addAll(walkUntilCollision(t, 0, -1, true));

			for (Point p : allEmptyLocations) {
				availableAttacks.add(new Move(Move.Type.ATTACK, t,
						getChessBoard().getTile(p.x, p.y)));
			}
		}
		if (piece.getType() == ChessPiece.Type.BISHOP
				|| piece.getType() == ChessPiece.Type.QUEEN) {

			HashSet<Point> allEmptyLocations = new HashSet<Point>();

			allEmptyLocations.addAll(walkUntilCollision(t, 1, 1, true));
			allEmptyLocations.addAll(walkUntilCollision(t, 1, -1, true));
			allEmptyLocations.addAll(walkUntilCollision(t, -1, 1, true));
			allEmptyLocations.addAll(walkUntilCollision(t, -1, -1, true));

			for (Point p : allEmptyLocations) {
				availableAttacks.add(new Move(Move.Type.ATTACK, t,
						getChessBoard().getTile(p.x, p.y)));
			}
		}

		removeInvalidMoves(availableAttacks);

		ListIterator<Move> moves = availableAttacks.listIterator();
		while (moves.hasNext()) {
			Move m = moves.next();
			if (!getChessBoard().hasPieceAtLocation(m.getEndingTile().getX(),
					m.getEndingTile().getY())
					|| getChessBoard().getPiece(m.getEndingTile().getX(),
							m.getEndingTile().getY()).getPlayer() == getChessBoard()
							.getPiece(t).getPlayer())
				moves.remove();

		}
		return availableAttacks;
	}

	public int getCurrentTurn() {
		return this.getCurrentState().getCurrentTurn();
	}

	private static int getOpponent(int player) {
		if (player == Game.PLAYER_ONE)
			return Game.PLAYER_TWO;
		return Game.PLAYER_ONE;
	}

	public int getOpponent() {
		return Game.getOpponent(getCurrentTurn());
	}

	public int getElapsedTurns() {
		return this.getCurrentState().getElapsedTurns();
	}

	public void switchTurns() {
		GameState currentState = this.getCurrentState();
		currentState.setCurrentTurn(getOpponent());
		if (this.getCurrentTurn() == Game.PLAYER_ONE)
			currentState.setElapsedTurns(currentState.getElapsedTurns() + 1);
	}

	public boolean undoMove() {
		if (this.getActiveFrame() > 0) {
			setActiveFrame(getActiveFrame() - 1);
			return true;
		}
		return false;
	}

	public boolean redoMove() {
		if (getActiveFrame() < getStateStack().size() - 1) {
			setActiveFrame(getActiveFrame() + 1);
			return true;
		}
		return false;
	}

	public void undoAllMoves() {
		setActiveFrame(0);
	}
}