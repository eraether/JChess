package com.eraether.jchess;
public class GameState {
	private ChessBoard chessBoard;
	private int currentTurn;
	private int elapsedTurns;
	private Move appliedMove;

	public GameState(ChessBoard chessBoard, int currentTurn, int elapsedTurns) {
		this(chessBoard, currentTurn, elapsedTurns, null);
	}

	public GameState(ChessBoard chessBoard, int currentTurn, int elapsedTurns,
			Move appliedMove) {
		setChessBoard(chessBoard);
		setCurrentTurn(currentTurn);
		setElapsedTurns(elapsedTurns);
		setAppliedMove(appliedMove);
	}

	public void setAppliedMove(Move move) {
		this.appliedMove = move;
	}

	private void setChessBoard(ChessBoard chessBoard) {
		this.chessBoard = chessBoard;
	}

	public Move getAppliedMove() {
		return appliedMove;
	}

	public void setElapsedTurns(int elapsedTurns) {
		this.elapsedTurns = elapsedTurns;
	}

	public void setCurrentTurn(int currentTurn) {
		this.currentTurn = currentTurn;
	}

	public ChessBoard getChessBoard() {
		return chessBoard;
	}

	public int getElapsedTurns() {
		return elapsedTurns;
	}

	public int getCurrentTurn() {
		return currentTurn;
	}

}