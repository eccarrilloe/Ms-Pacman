package pacman;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;

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
	public PriorityQueue<ConnectedSet> powerPills;

	Collection<ConnectedSet> ghosts;
	Agent agent;
	Vector2d closestObjective;
	Vector2d tmp;

	int objectiveState;
	static int nFeatures = 13;
	double[] vec;

	static HashMap<Integer, Integer> ghostLut = new HashMap<Integer, Integer>();
	static {
		// map these into positions of ghost rather than anything else -
		ghostLut.put(MsPacInterface.blinky, 0);
		ghostLut.put(MsPacInterface.inky, 1);
		ghostLut.put(MsPacInterface.pinky, 2);
		ghostLut.put(MsPacInterface.sue, 3);
		ghostLut.put(MsPacInterface.victims, 4);
	}

	public GameState() {
		agent = new Agent();
		tmp = new Vector2d();
		vec = new double[nFeatures];
		objectiveState = 0;
		powerPills =  new PriorityQueue<ConnectedSet>();
	}

	public void reset() {
		closestObjective = null;
	}

	public void update(ConnectedSet cs, int[] pix){
		if (cs.isPacMan()) {
			agent.update(cs, pix);
		} else if (cs.powerPill()) {
			ConnectedSet powerPill = new ConnectedSet(cs);
		//	System.out.println("Power Pills 1 " + powerPills);
			if (powerPills != null && !powerPills.contains(powerPill)) {
				powerPills.add(powerPill);
				System.out.println("pill  " +powerPill);
				System.out.println("Power Pills 2 " + powerPills);
			}
		} else if (cs.ghostLike()) {
		//	System.out.println("Fntasmas azules :D :D :D ");
			if (cs.fg == MsPacInterface.victims) {
			//	System.out.println("Fantmas MAS azules");
				objectiveState = 1;
			} else {
				objectiveState = 0;
			}
		}// 0-> Comer power pill 
		// 	1-> Comer fantasma
		//  2 -> Comer demás pastilas
		if (objectiveState == 0) {
			//Estado 0 -> Busca la pastilla
			//System.out.println("Entro al estado 0");
			if (powerPills != null && powerPills.size() > 0) {
				// keep track of the position of the closest powerPill
				Iterator<ConnectedSet> it = powerPills.iterator();
				while (it.hasNext()) {
					ConnectedSet currentPill = it.next();
					tmp.set(currentPill.x, currentPill.y);
					if (closestObjective == null) {						
						closestObjective = new Vector2d(tmp);
					} else if (tmp.dist(agent.cur) < closestObjective.dist(agent.cur)) {
						
						objectiveState = 1;
						closestObjective.set(tmp);
						
					}
				}
				
			}
		} else if (objectiveState == 1) {
			//System.out.println("Entro al estado 1");
			if (cs.fg == MsPacInterface.victims) {
				// update the state of the ghost distance
			//	System.out.println("Entro al Estado 1 - -- - - Fantasmas azules");
				tmp.set(cs.x, cs.y);
				
				if (closestObjective == null) {
					closestObjective = new Vector2d(tmp);
				}
			//	System.out.println("TEEEEEEEEEEEEEEEEEEMP");
				closestObjective.set(tmp);
				powerPills.poll();
				System.out.println("Power Pills 3 " + powerPills);
							
				if (tmp.dist(agent.cur) < closestObjective.dist(agent.cur)) {
					closestObjective.set(tmp);
				}
				if (powerPills.isEmpty()){
					objectiveState = 2;
				}
			} else {
			//	objectiveState = 0;
			}
		} else if (objectiveState == 2) {
			System.out.println("Entro al estado 2");
			//Busca pildoras normales
			if (cs.pill()) {
				// keep track of the position of the closest powerPill
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
