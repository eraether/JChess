package com.eraether.jchess;
import java.awt.GridLayout;

import javax.swing.JFrame;


class ChessFrame extends JFrame {
	private ChessPanel chessPanel;

	public ChessFrame() {
		initGUI();
	}

	private void initGUI() {
		chessPanel = new ChessPanel();
		setSize(500, 500);
		setLayout(new GridLayout(1, 1));
		add(chessPanel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Chess");
	}

	public void setGame(Game g) {
		this.chessPanel.setGame(g);
	}
}