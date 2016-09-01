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

	int objectiveState;
	int powerPillsEaten;
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
	}

	public void reset() {
		closestObjective = null;
	}

	public void update(ConnectedSet cs, int[] pix){
		System.out.println("PowerPills: " + powerPills.size());
		if (cs.isPacMan()) {
			agent.update(cs, pix);
		} else if (cs.powerPill()) {
			ConnectedSet powerPill = new ConnectedSet(cs);
			if (!powerPills.contains(powerPill)) {
				powerPills.add(powerPill);
			}
		} else if (cs.ghostLike()) {
			if (cs.edible()) {
				objectiveState = 1;
			} else {
				if (objectiveState == 1) {
					powerPillsEaten = 4 - powerPills.size();
					if (powerPillsEaten == 4) {
						objectiveState = 2;
					} else {
						objectiveState = 0;
					}
				}
			}
		}
		
		System.out.println("Pills eaten = " + powerPillsEaten);
		
		// 0-> Comer power pill 
		// 	1-> Comer fantasma
		//  2 -> Comer demás pastilas
		if (objectiveState == 0) {
			//Estado 0 -> Busca la pastilla
			System.out.println("State #0 - " + powerPills.size());
			// keep track of the position of the closest powerPill
			Iterator<ConnectedSet> it = powerPills.iterator();
			while (it.hasNext()) {
				ConnectedSet currentPill = it.next();
				tmp.set(currentPill.x, currentPill.y);
				if (closestObjective == null) {						
					closestObjective = new Vector2d(tmp);
				} else if (tmp.dist(agent.cur) < closestObjective.dist(agent.cur)) {
					closestObjective.set(tmp);
				}
			}
			
			if (closestObjective != null && closestObjective.dist(agent.cur) < 2) {
				ConnectedSet closestSet = new ConnectedSet(closestObjective, MsPacInterface.pill);
				if (powerPills.contains(closestSet)) {
					powerPills.remove(closestSet);
					objectiveState = 1;
				}
			}
		} else if (objectiveState == 1) {
			System.out.println("State #1 - " + powerPills.size());
			if (cs.edible()) {
				
				tmp.set(cs.x, cs.y);
				if (closestObjective == null) {
					closestObjective = new Vector2d(tmp);
				}
				
				closestObjective.set(tmp);
							
				if (tmp.dist(agent.cur) < closestObjective.dist(agent.cur)) {
					closestObjective.set(tmp);
				}
			}
		} else if (objectiveState == 2) {
			System.out.println("State #2");
			if (cs.pill()) {
				tmp.set(cs.x, cs.y);
				if (closestObjective == null) {
					closestObjective = new Vector2d(tmp);
				} else if (tmp.dist(agent.cur) < closestObjective.dist(agent.cur)) {
					closestObjective.set(tmp);
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
