package ecs.components;

import com.artemis.Component;
import com.badlogic.gdx.math.MathUtils;

public class MovementDirection extends Component {
	public Direction currentDirection = Direction.DOWN;

	public static enum Direction {
		UP(0, 1), DOWN(0, -1), RIGHT(1, 0), LEFT(-1, 0);

		private int dx, dy;

		private Direction(int dx, int dy) {
			this.dx = dx;
			this.dy = dy;
		}

		public int getDX() {
			return dx;
		}

		public int getDY() {
			return dy;
		}

		static public Direction getRandomNext() {
			return Direction.values()[MathUtils.random(Direction.values().length - 1)];
		}

		public Direction getOpposite() {
			if (this == LEFT)
				return RIGHT;
			else if (this == RIGHT)
				return LEFT;
			else if (this == UP)
				return DOWN;
			else {
				return UP;
			}
		}
	}
}
