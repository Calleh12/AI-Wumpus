package wumpusworld;

import alice.tuprolog.*;
import java.io.*;
import java.util.ArrayList;

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
    ArrayList<String> moves = new ArrayList<String>();
    /**
     * Creates a new instance of your solver agent.
     * 
     * @param world Current world state 
     */
    public MyAgent(World world)
    {
        w = world;
	m_Logic = new Logic();
        
        moves.add(World.A_MOVE);
        moves.add(World.A_MOVE);
        moves.add(World.A_TURN_LEFT);
        moves.add(World.A_TURN_LEFT);
        moves.add(World.A_MOVE);
        moves.add(World.A_MOVE);
        moves.add(World.A_TURN_RIGHT);
        moves.add(World.A_MOVE);
        moves.add(World.A_MOVE);
        moves.add(World.A_TURN_RIGHT);
        moves.add(World.A_TURN_RIGHT);
        moves.add(World.A_MOVE);
        moves.add(World.A_MOVE);
        moves.add(World.A_MOVE);
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
	    
	    m_Logic.addLocation("visited", cX, cY);
	    //Test the environment
	    if (w.hasBreeze(cX, cY))
	    {
		System.out.println("I am in a Breeze");
		m_Logic.addLocation("breeze", cX, cY);
	    }
	    if (w.hasStench(cX, cY))
	    {
		System.out.println("I am in a Stench");
		m_Logic.addLocation("stench", cX, cY);
	    }
	    if (w.hasPit(cX, cY))
	    {
		System.out.println("I am in a Pit");
		m_Logic.addLocation("pit", cX, cY);
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
            
            m_Logic.countPossibleWumpus();
            Pos pos = m_Logic.lookAhead(cX, cY, w.getDirection());
            if(pos.x != -1 && pos.y != -1)
            {
                if(m_Logic.possibleDangerAhead(pos.x, pos.y).compareTo("p_wumpus") == 0)
                {
                    int rnd = (int)(Math.random() * 2);
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
                }
                else
                {
                    w.doAction(w.A_MOVE);
                    return;
                }
            }
            else
            {
                //Failsafe against invalid squares
                //Random move actions
                int rnd = (int)(Math.random() * 2);
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
            }
//            if(moves.size() > 0)
//            {
//                w.doAction(moves.get(0));
//                moves.remove(0);
//            }
	}
	
	catch(Exception e)
	{
	    System.out.println("Something went wrong, error: " + e.getMessage());
	}
    }
}
