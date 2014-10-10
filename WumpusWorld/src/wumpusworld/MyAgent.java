package wumpusworld;

import alice.tuprolog.*;
import java.io.*;
/**
 * Contans starting code for creating your own Wumpus World agent.
 * Currently the agent only make a random decision each turn.
 * 
 * @author Johan Hagelbäck
 */
public class MyAgent implements Agent
{
    private World w;
    private Logic m_Logic;
    /**
     * Creates a new instance of your solver agent.
     * 
     * @param world Current world state 
     */
    public MyAgent(World world)
    {
        w = world;
	m_Logic = new Logic();
    }
    /**
     * Asks your solver agent to execute an action.
     */
    public void doAction()
    {
	try
	{
	    //Location of the player
	    int cX = w.getPlayerX();
	    int cY = w.getPlayerY();

	    //Basic action:
	    //Grab Gold if we can.
	    if (w.hasGlitter(cX, cY))
	    {
		w.doAction(World.A_GRAB);
		return;
	    }

	    //Basic action:
	    //We are in a pit. Climb up.
	    if (w.isInPit())
	    {
		w.doAction(World.A_CLIMB);
		return;
	    }
	    
	    m_Logic.addVisited(cX, cY);
	    //Test the environment
	    if (w.hasBreeze(cX, cY))
	    {
		System.out.println("I am in a Breeze");
		m_Logic.addRule(cX, cY, "breeze");
		m_Logic.addPosibleDanger(cX, cY, "breezy");
	    }
	    if (w.hasStench(cX, cY))
	    {
		System.out.println("I am in a Stench");
		m_Logic.addRule(cX, cY, "stench");
	    }
	    if (w.hasPit(cX, cY))
	    {
		System.out.println("I am in a Pit");
		m_Logic.addRule(cX, cY, "pit");
	    }
	    if (w.hasWumpus(cY, cY))
	    {
		System.out.println("I am at the Wumpus!");
		m_Logic.addRule(cX, cY, "wumpus");
	    }
	    if (w.getDirection() == World.DIR_RIGHT)
	    {
		System.out.println("I am facing Right");
	    }
	    if (w.getDirection() == World.DIR_LEFT)
	    {
		System.out.println("I am facing Left");
	    }
	    if (w.getDirection() == World.DIR_UP)
	    {
		System.out.println("I am facing Up");
	    }
	    if (w.getDirection() == World.DIR_DOWN)
	    {
		System.out.println("I am facing Down");
	    }

	    //Random move actions
	    int rnd = (int)(Math.random() * 5);
	    if (rnd == 0) 
	    {
		w.doAction(World.A_TURN_LEFT);
		return;
	    }
	    if (rnd == 1)
	    {
		w.doAction(World.A_TURN_RIGHT);
		return;
	    }
	    if (rnd >= 2)
	    {
		w.doAction(World.A_MOVE);
		return;
	    }
	}
	
	catch(Exception e)
	{
	    System.out.println("Something went wrong, error: " + e.getMessage());
	}
    }
}
