import java.awt.Point;

class Tile {
	private int xPosition;
	private int yPosition;

	public Tile(int xPosition, int yPosition) {
		setXPosition(xPosition);
		setYPosition(yPosition);
	}

	private void setXPosition(int xPosition) {
		this.xPosition = xPosition;
	}

	private void setYPosition(int yPosition) {
		this.yPosition = yPosition;
	}

	public int getX() {
		return xPosition;
	}

	public int getY() {
		return yPosition;
	}

	public Point getDelta(Tile other) {
		return new Point(getX() - other.getX(), getY() - other.getY());
	}

	public String toString() {
		return "(" + xPosition + "," + yPosition + ")";
	}

	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (!(other instanceof Tile))
			return false;
		Tile cast = (Tile) other;
		return getX() == cast.getX() && getY() == cast.getY();
	}
}