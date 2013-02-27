package com.eraether.jchess;
import java.util.ArrayList;


class AIHandler {
	private ArrayList<AI> aiList = new ArrayList<AI>();

	public AIHandler() {
	}

	public void addAI(AI ai) {
		aiList.add(ai);
	}

	public boolean removeAI(int player) {
		return aiList.remove(getAIForPlayer(player));
	}

	public AI getAIForPlayer(int player) {
		for (AI ai : aiList) {
			if (ai.getPlayer() == player)
				return ai;
		}
		return null;
	}
}