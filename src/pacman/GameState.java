package pacman;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

import games.math.Vector2d;

/**
 * User: Simon Date: 09-Mar-2007 Time: 11:33:00 The purpose of this is to
 * capture the state of the game to give to a decision making agent.
 * <p/>
 * The state is based on analysing a screen image, and may give incorrect
 * readings at any given instant - for example, power pills flash, but no
 * account is taken of this, so if a power pill is still in the game, but the
 * screen was captured while it was in the 'blinked off' state, then the
 * GameState will indicate that there is no power pill at that location (in
 * fact, the power pills flash in unison, so it could appear that there were no
 * power pills, when in fact they were all present in the true game state.
 */
public class GameState implements Drawable {
	// might as well have separate collections for each item?

	static int strokeWidth = 5;
	static Stroke stroke = new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	Collection<ConnectedSet> pills;
	ArrayList<ConnectedSet> powerPills;

	Collection<ConnectedSet> ghosts;
	Agent agent;
	Vector2d closestObjective;
	Vector2d tmp;

	int iterations = 0;
	int objectiveState;
	int powerPillsEaten;
	int nTest;
	static int nFeatures = 13;
	double[] vec;

	static HashMap<Integer, Integer> ghostLut = new HashMap<Integer, Integer>();
	static {
		// map these into positions of ghost rather than anything else -
		ghostLut.put(MsPacInterface.blinky, 0);
		ghostLut.put(MsPacInterface.inky, 1);
		ghostLut.put(MsPacInterface.pinky, 2);
		ghostLut.put(MsPacInterface.sue, 3);
		ghostLut.put(MsPacInterface.edible, 4);
	}

	public GameState() {
		agent = new Agent();
		tmp = new Vector2d();
		vec = new double[nFeatures];
		powerPills =  new ArrayList<>();
		objectiveState = 0;
		powerPillsEaten = 0;
		iterations = 0;
		nTest = 0;
	}

	public void reset() {
		closestObjective = null;
	}

	public void update(ConnectedSet cs, int[] pix){
		if (cs.isPacMan()) {
			agent.update(cs, pix);
		} else if (cs.powerPill()) {
			ConnectedSet powerPill = new ConnectedSet(cs);
			if (!powerPills.contains(powerPill)) {
				powerPills.add(powerPill);
			}
		}
		
		/**
		 * Pruebas Realizadas:
		 * nTest = 1 -> Ir hasta la esquina superior izquierda
		 * nTest = 2 -> Perseguir a Inky
		 * nTest = 3 -> Cruzar un tunel repetidamente
		 */
		nTest = 3;
		if (nTest == 1) {				
			closestObjective = new Vector2d(100.0, 100.0);
		} else if (nTest == 2) {
			if (cs.ghostLike() && cs.fg == MsPacInterface.inky) {
				tmp.set(cs.x, cs.y);
				if (closestObjective == null) {						
					closestObjective = new Vector2d(tmp);
				} else if (tmp.dist(agent.cur) < closestObjective.dist(agent.cur)) {
					closestObjective.set(tmp);
				}
			}
		} else if (nTest == 3) {
			Vector2d rightTunnel = new Vector2d(200.0, 160.0);
			if (objectiveState == 0) {
				closestObjective = rightTunnel;
				
				if (Math.abs(agent.cur.x - closestObjective.x) < 5 && Math.abs(agent.cur.y - closestObjective.y) < 5) {
					objectiveState = 1;
				}
			}
		}	
	}

	public void draw(Graphics gg, int w, int h) {
		// To change body of implemented methods use File | Settings | File
		// Templates.
		Graphics2D g = (Graphics2D) gg;

		if (agent != null) {
			agent.draw(g, w, h);
		}
		if (closestObjective != null && agent != null) {
			g.setStroke(stroke);
			g.setColor(Color.cyan);
			g.drawLine((int) closestObjective.x, (int) closestObjective.y, (int) agent.cur.x, (int) agent.cur.y);
		}
	}
}
