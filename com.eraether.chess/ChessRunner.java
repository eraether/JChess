/*@Author Eugene Raether
 * 
 * Approx.  1450 LOC
 * 
 */
public class ChessRunner {
	public static void main(String[] args) {
		Game game = createDefaultGame();
		ChessFrame frame = new ChessFrame();
		frame.setGame(game);
		frame.setVisible(true);
	}

	private static Game createDefaultGame() {
		ChessBoard board = new ChessBoard();
		// Checkmate / Stalemate scenario
		// board.placePiece(new ChessPiece(0, ChessPiece.Type.KING), 1, 0);
		// board.placePiece(new ChessPiece(1, ChessPiece.Type.QUEEN), 6, 1);
		// board.placePiece(new ChessPiece(1, ChessPiece.Type.QUEEN), 6, 2);
		// board.placePiece(new ChessPiece(1, ChessPiece.Type.BISHOP), 4, 3);
		// board.placePiece(new ChessPiece(1, ChessPiece.Type.KING), 7, 7);

		// castling test
		// board.placePiece(new ChessPiece(0, ChessPiece.Type.KING), 0, 4);
		// board.placePiece(new ChessPiece(0, ChessPiece.Type.ROOK), 0, 0);
		// board.placePiece(new ChessPiece(0, ChessPiece.Type.ROOK), 0, 7);
		// board.placePiece(new ChessPiece(0, ChessPiece.Type.KNIGHT), 2, 0);
		//
		// board.placePiece(new ChessPiece(1, ChessPiece.Type.KING), 7, 4);
		// board.placePiece(new ChessPiece(1, ChessPiece.Type.ROOK), 7, 0);
		// board.placePiece(new ChessPiece(1, ChessPiece.Type.ROOK), 7, 7);

		// promotion test
		// board.placePiece(new ChessPiece(0, ChessPiece.Type.KING), 0, 4);
		// board.placePiece(new ChessPiece(1, ChessPiece.Type.KING), 7, 4);
		// board.placePiece(new ChessPiece(0, ChessPiece.Type.PAWN), 6, 7);

		// En Passant

		// board.placePiece(new ChessPiece(0, ChessPiece.Type.KING), 0, 4);
		// board.placePiece(new ChessPiece(1, ChessPiece.Type.KING), 7, 4);
		// for (int y = 0; y < 8; y++) {
		// board.placePiece(new ChessPiece(0, ChessPiece.Type.PAWN), 1, y);
		// board.placePiece(new ChessPiece(1, ChessPiece.Type.PAWN), 6, y);
		// }
		// board.placePiece(new ChessPiece(0, ChessPiece.Type.PAWN), 3, 0);
		// board.placePiece(new ChessPiece(1, ChessPiece.Type.PAWN), 1, 0);
		// board.placePiece(new ChessPiece(0, ChessPiece.Type.PAWN), 4, 1);
		// board.placePiece(new ChessPiece(1, ChessPiece.Type.PAWN), 6, 0);
		// board.placePiece(new ChessPiece(1, ChessPiece.Type.PAWN), 6, 1);

		// default Board
		//add instant replay once game ends
		//can't castle while in check
		//look at castling FAQ
		//can't move through a square that is under attack
		board.generateDefaultBoard();
		Game game = new Game(board);
		game.getChessBoard();
		return game;
	}
}