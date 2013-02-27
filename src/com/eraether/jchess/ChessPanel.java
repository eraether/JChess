package com.eraether.jchess;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JPanel;
import java.awt.Image;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.awt.event.*;
import javax.swing.event.*;

class ChessPanel extends JPanel implements MouseListener, MouseMotionListener,
		KeyListener, ComponentListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Game game;
	private AIHandler aiHandler;

	private int lastSelectedX;
	private int lastSelectedY;
	private boolean hasSelectedPiece = false;
	private ArrayList<Move> availableMoves = null;

	private BufferedImage[][] chessPieceImages;
	private BufferedImage[][] rescaledChessPieceImages;

	private int tileSize = 50;
	private ArrayList<Move> allAvailableMoves = null;
	private boolean playerUnderCheck = false;
	private int previousTileSize = tileSize;
	private boolean aiActive = true;
	private boolean startInstantReplay = false;
	private long lastReplayChange = 0;

	public ChessPanel() {
		cacheResources();
		initGUI();
		addListeners();
		setFocusable(true);
		requestFocusInWindow();
		initializeAI();
	}

	private AIHandler getAIHandler() {
		return aiHandler;
	}

	private void initializeAI() {
		aiHandler = new AIHandler();

		int amount = Integer.parseInt(JOptionPane.showInputDialog(
				"Enter # of AI Player", "0"));
		amount = Math.min(Math.abs(amount), 2);
		int start = (int) (Math.random() * 2);

		for (int x = 0; x < amount; x++) {
			int player = (x + start) % 2;
			this.getAIHandler().addAI(new AI(player));
		}
	}

	private void runAI() {
		AI ai = aiHandler.getAIForPlayer(game.getCurrentPlayer());
		if (ai == null)
			return;
		Move move = ai.generateMove(getGame());
		if (move != null)
			game.applyMove(move);
		recalculateAllAvailableMoves();
		try {
			if (aiHandler.getAIForPlayer(game.getCurrentPlayer()) != null)
				Thread.sleep(100);
		} catch (Exception e) {
		}
		;
	}

	private void runGameUpdate() {
		if (this.startInstantReplay) {

			if (System.currentTimeMillis() - this.lastReplayChange > 1000) {
				boolean b = getGame().redoMove();
				if (!b)
					this.stopInstantReplay();
				this.lastReplayChange = System.currentTimeMillis();
			}
		}
		if (this.aiActive)
			runAI();

	}

	private BufferedImage getImageForPiece(ChessPiece piece) {
		try {
			return rescaledChessPieceImages[piece.getType().ordinal()][piece
					.getPlayer()];
		} catch (Exception e) {
			return null;
		}
	}

	private void cacheResources() {
		chessPieceImages = new BufferedImage[ChessPiece.Type.values().length][2];
		chessPieceImages[ChessPiece.Type.PAWN.ordinal()][Game.PLAYER_ONE] = loadImage("pawn0.png");
		chessPieceImages[ChessPiece.Type.BISHOP.ordinal()][Game.PLAYER_ONE] = loadImage("bishop0.png");
		chessPieceImages[ChessPiece.Type.ROOK.ordinal()][Game.PLAYER_ONE] = loadImage("rook0.png");
		chessPieceImages[ChessPiece.Type.KING.ordinal()][Game.PLAYER_ONE] = loadImage("king0.png");
		chessPieceImages[ChessPiece.Type.QUEEN.ordinal()][Game.PLAYER_ONE] = loadImage("queen0.png");
		chessPieceImages[ChessPiece.Type.KNIGHT.ordinal()][Game.PLAYER_ONE] = loadImage("knight0.png");
		chessPieceImages[ChessPiece.Type.PAWN.ordinal()][Game.PLAYER_TWO] = loadImage("pawn1.png");
		chessPieceImages[ChessPiece.Type.BISHOP.ordinal()][Game.PLAYER_TWO] = loadImage("bishop1.png");
		chessPieceImages[ChessPiece.Type.ROOK.ordinal()][Game.PLAYER_TWO] = loadImage("rook1.png");
		chessPieceImages[ChessPiece.Type.KING.ordinal()][Game.PLAYER_TWO] = loadImage("king1.png");
		chessPieceImages[ChessPiece.Type.QUEEN.ordinal()][Game.PLAYER_TWO] = loadImage("queen1.png");
		chessPieceImages[ChessPiece.Type.KNIGHT.ordinal()][Game.PLAYER_TWO] = loadImage("knight1.png");

		rescaleImages(50);
	}

	private void rescaleImages(int newTileSize) {
		if (newTileSize <= 0)
			newTileSize = 1;
		rescaledChessPieceImages = new BufferedImage[chessPieceImages.length][chessPieceImages[0].length];
		for (int x = 0; x < chessPieceImages.length; x++) {
			for (int y = 0; y < chessPieceImages[0].length; y++) {
				BufferedImage original = chessPieceImages[x][y];
				BufferedImage dupe = new BufferedImage(newTileSize,
						newTileSize, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = dupe.createGraphics();

				g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BICUBIC);

				g2d.drawImage(original, 0, 0, newTileSize, newTileSize, null);
				g2d.dispose();
				rescaledChessPieceImages[x][y] = dupe;
			}
		}
	}

	private BufferedImage loadImage(String fileName) {
		String folder = "res/";
		
		try {
			InputStream in = this.getClass().getClassLoader().getResourceAsStream(folder+fileName);
			return ImageIO.read(in);
		} catch (Exception e) {

			System.out.print("Could not load image " + fileName + ": " + e);
		}
		;
		return null;
	}

	private void addListeners() {
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		addComponentListener(this);
	}

	private void initGUI() {

	}

	public int getTileSize() {
		return tileSize;
	}

	private void setTileSize(int tileSize) {
		this.tileSize = tileSize;
	}

	private int calculateOptimalTileSize(Dimension dim) {
		int min = Math.min(dim.width, dim.height);
		return min
				/ (int) (Math.max(this.getGame().getChessBoard().getHeight(),
						this.getGame().getChessBoard().getWidth()));
	}

	public void paintComponent(Graphics g) {
		setTileSize(calculateOptimalTileSize(getSize()));
		if (previousTileSize != getTileSize()) {
			this.rescaleImages(getTileSize());
		}
		this.previousTileSize = getTileSize();

		runGameUpdate();

		g.clearRect(0, 0, (int) getSize().getWidth(), (int) getSize()
				.getHeight());
		ChessBoard chessBoard = getGame().getChessBoard();

		// render board as alternating rectangles
		g.setColor(Color.BLACK);
		for (int x = 0; x < chessBoard.getHeight(); x++) {
			for (int y = 0; y < chessBoard.getWidth(); y++) {
				if ((x + y) % 2 == 0)
					g.setColor(new Color(255, 206, 158));
				else
					g.setColor(new Color(209, 139, 71));
				g.fillRect(getTileSize() * y, getTileSize() * x, getTileSize(),
						getTileSize());
			}
		}

		// render tile outline
		g.setColor(Color.BLACK);
		for (int x = 0; x < chessBoard.getHeight(); x++) {
			for (int y = 0; y < chessBoard.getWidth(); y++) {
				g.drawRect(getTileSize() * y, getTileSize() * x, getTileSize(),
						getTileSize());
			}
		}

		// render pieces
		Graphics2D g2d = (Graphics2D) g;
		for (int x = 0; x < chessBoard.getHeight(); x++) {
			for (int y = 0; y < chessBoard.getWidth(); y++) {
				if (!chessBoard.hasPieceAtLocation(x, y))
					continue;
				ChessPiece piece = chessBoard.getPiece(x, y);
				BufferedImage image = getImageForPiece(piece);
				if (image != null) {
					g.drawImage(image, y * getTileSize(), x * getTileSize(),
							null);
				} else
					g.drawRect(y * getTileSize(), x * getTileSize(),
							getTileSize(), getTileSize());
				// if (piece.getPlayer() == Game.PLAYER_ONE)
				// g.setColor(Color.red);
				// else
				// g.setColor(Color.black);
				// FontMetrics metrics = g.getFontMetrics();
				// g.drawOval(getTileSize() * y, getTileSize() * x,
				// getTileSize(), getTileSize());
				// renderCenteredText(g, getTileSize() * y, getTileSize() * x,
				// getTileSize(),
				// getTileSize(), piece.getType().toString());
			}
		}

		// if (availableMoves != null) {
		// for (Move m : availableMoves) {
		// if (m.getType() == Move.Type.MOVE)
		// g.setColor(new Color(0, 0, 255, 200));
		// else if (m.getType() == Move.Type.ATTACK)
		// g.setColor(new Color(255, 0, 0, 200));
		// else
		// g.setColor(new Color(255, 255, 255, 200));
		// g.fillRect(m.getEndingTile().getY() * getTileSize(), m
		// .getEndingTile().getX() * getTileSize(), getTileSize(),
		// getTileSize());
		// }
		// }

		if (allAvailableMoves != null && playerUnderCheck) {
			HashSet<Tile> activeTiles = new HashSet<Tile>();
			for (Move m : allAvailableMoves) {
				activeTiles.add(m.getStartingTile());
			}
			g.setColor(new Color(255, 20, 20, 255));
			int strokeSize = 3;
			g2d.setStroke(new BasicStroke(strokeSize, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_MITER));
			for (Tile t : activeTiles) {
				g.drawRect(t.getY() * getTileSize() + strokeSize / 2, t.getX()
						* getTileSize() + strokeSize / 2, getTileSize()
						- strokeSize, getTileSize() - strokeSize);
			}

		}
		if (allAvailableMoves != null && hasSelectedPiece) {
			for (Move m : allAvailableMoves) {
				if (m.getStartingTile().getX() == this.lastSelectedX
						&& m.getStartingTile().getY() == this.lastSelectedY) {
					if (m.getType() == Move.Type.MOVE)
						g.setColor(new Color(0, 0, 255, 200));
					else if (m.getType() == Move.Type.ATTACK)
						g.setColor(new Color(255, 0, 0, 200));
					else
						g.setColor(new Color(255, 255, 255, 200));
					g.fillRect(m.getEndingTile().getY() * getTileSize(), m
							.getEndingTile().getX() * getTileSize(),
							getTileSize(), getTileSize());
				}
			}
		}
		try {
			Thread.sleep(20);
		} catch (Exception e) {
		}
		;
		repaint();
	}

	private static void renderCenteredText(Graphics g, int startX, int startY,
			int width, int height, String text) {

		Rectangle2D rect2D = g.getFontMetrics().getStringBounds(text, g);
		int renderX = (int) (startX + (width - rect2D.getWidth()));
		int renderY = (int) (startY + (height - rect2D.getHeight()));
		g.drawString(text, renderX, renderY);
	}

	public void setGame(Game game) {
		this.game = game;
		recalculateAllAvailableMoves();
	}

	private Game getGame() {
		return game;
	}

	public Point snapMouseToBoard(int x, int y) {
		return new Point(y / getTileSize(), x / getTileSize());
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	private ChessPiece.Type promptForPieceType() {
		final JDialog dialog = new JDialog((JFrame) null, true);
		dialog.setLayout(new GridLayout(1, 1));
		JPanel panelHousing = new JPanel(new BorderLayout());
		JPanel radioButtonHousing = new JPanel(new GridLayout(4, 1));
		panelHousing.add(radioButtonHousing, BorderLayout.CENTER);
		ButtonGroup group = new ButtonGroup();
		Map<JRadioButton, ChessPiece.Type> map = new HashMap<JRadioButton, ChessPiece.Type>();
		for (int x = 0; x < ChessPiece.Type.values().length; x++) {
			ChessPiece.Type type = ChessPiece.Type.values()[x];
			if (type == ChessPiece.Type.PAWN || type == ChessPiece.Type.KING)
				continue;
			JRadioButton button = new JRadioButton();
			JLabel label = new JLabel(type.toString(), new ImageIcon(
					this.getImageForPiece(new ChessPiece(game
							.getCurrentPlayer(), type))), SwingConstants.LEFT);

			JPanel panel = new JPanel(new GridLayout(1, 2));
			panel.add(label);
			panel.add(button);
			radioButtonHousing.add(panel);
			button.setSelected(type == ChessPiece.Type.QUEEN);
			group.add(button);
			map.put(button, type);
		}
		JButton button = new JButton("Select");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				dialog.dispose();
			}
		});
		panelHousing.add(button, BorderLayout.SOUTH);
		dialog.add(panelHousing);
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dialog.setSize(400, 300);
		dialog.setTitle("Select pawn promotion");
		dialog.setVisible(true);

		for (JRadioButton b : map.keySet()) {
			if (b.isSelected())
				return map.get(b);
		}

		return ChessPiece.Type.QUEEN;
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		Point point = snapMouseToBoard(arg0.getX(), arg0.getY());
		if (availableMoves != null) {
			for (Move m : availableMoves) {
				if (m.getEndingTile().getX() == point.x
						&& m.getEndingTile().getY() == point.y) {
					if (m.getType() == Move.Type.MOVE_PROMOTE) {
						ChessPiece.Type type = promptForPieceType();
						CompoundMove move = (CompoundMove) m;
						MoveTransform transform = (MoveTransform) move
								.getMoveB();
						transform.setTransformation(type);
					}
					availableMoves = null;
					hasSelectedPiece = false;
					game.applyMove(m);
					this.recalculateAllAvailableMoves();
					return;
				}
			}

			hasSelectedPiece = false;
			availableMoves = null;
		}

		if (game.getChessBoard().isValidLocation(point.x, point.y)
				&& game.getChessBoard().hasPieceAtLocation(point.x, point.y)
				&& game.getChessBoard().getPiece(point.x, point.y).getPlayer() == game
						.getCurrentTurn()) {
			lastSelectedX = point.x;
			lastSelectedY = point.y;
			hasSelectedPiece = true;
			availableMoves = game.generateMovesForTile(game.getChessBoard()
					.getTile(lastSelectedX, lastSelectedY));
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getKeyChar() == '0') {
			this.getGame().undoMove();
			System.out.println("Undoing move.");
			pauseAI(true);
			stopInstantReplay();
		} else if (arg0.getKeyChar() == '1') {
			this.getGame().redoMove();
			System.out.println("Redoing move.");
			pauseAI(true);
			stopInstantReplay();
		} else if (arg0.getKeyChar() == 'r') {
			pauseAI(false);
		} else if (arg0.getKeyChar() == 'i') {
			if (startInstantReplay) {
				stopInstantReplay();
			} else {
				startInstantReplay();
			}
		}
		recalculateAllAvailableMoves();
	}

	private void stopInstantReplay() {
		startInstantReplay = false;
		System.out.println("Stopping instant replay");
	}

	private void startInstantReplay() {
		pauseAI(true);
		startInstantReplay = true;
		lastReplayChange = System.currentTimeMillis();
		getGame().undoAllMoves();
		System.out.println("Start instant replay");
	}

	private void pauseAI(boolean pause) {
		this.aiActive = !pause;
	}

	private void recalculateAllAvailableMoves() {

		if (this.game != null) {
			this.allAvailableMoves = this.game.generateAllValidMoves();
			this.playerUnderCheck = this.game.kingUnderCheck(this.game
					.getCurrentPlayer());
		}
	}

	private void clearAvailableMoveList() {
		this.playerUnderCheck = false;
		allAvailableMoves = null;
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}
}