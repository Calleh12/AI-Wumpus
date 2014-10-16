package wumpusworld;

import alice.tuprolog.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

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
    private Pos goalPos;
    private Pos lastGoalPos;
    private int m_LastDir;
    /**
     * Creates a new instance of your solver agent.
     * 
     * @param world Current world state 
     */
    public MyAgent(World world)
    {
        w = world;
	m_Logic = new Logic();
        goalPos = new Pos(w.getPlayerX(),w.getPlayerY());
        lastGoalPos = new Pos(-1, -1);
        m_LastDir = -1;
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
            m_Logic.solve("updatePlayer("+cX+","+cY+",A,B).");
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
            
	    m_Logic.addLocation("visited", cX, cY);
            m_Logic.countPossibleWumpus();
//            ArrayList<String> whats = m_Logic.locateAllAt(cX, cY);            
//            if(whats.size() == 1)
//            {
//                if(whats.get(0).compareTo("visited") == 0)
//                {
//                    ArrayList<Square> squares = m_Logic.locateAround(cX, cY);
//                    int hej = 0;
//                }
//            }
            if(!w.wumpusAlive())
            {
                m_Logic.solve("removeAll(wumpus).");
            }
            int wumpusDir = -1;
            ArrayList<String> types;            
            if(goalPos.x == cX && goalPos.y == cY)
            {
                for(int i = 0; i < 4; i++)
                {
//                    Pos pos = m_Logic.look(cX, cY, i);
//                    if(pos.x == -1 || pos.y == -1)
//                        continue;
//                    types = m_Logic.locateAllAt(pos.x, pos.y);
//                    
//                    if(types.isEmpty() || types.size() == 1 && types.get(0).compareTo("visited") == 0)
//                    {
//                        goalPos = pos;
//                        break;
//                    }
//                    else
//                    {
//                        for (String type : types) 
//                        {
//                            String temp = type;
//                            if(temp.compareTo("p_wumpus") == 0)
//                            {
//                                break;
//                            }
//                            else if(temp.compareTo("wumpus") == 0)
//                            {
//                                wumpusDir = i;
//                                break;
//                            }
//                            else if(temp.compareTo("p_pit") == 0)
//                            {
//                                break;
//                            }
//                            else if(temp.compareTo("pit") == 0)
//                            {
//                                break;
//                            }
//                            else if(temp.compareTo("visited") == 0)
//                            {
//                                goalPos = pos;
//                            }
//                        }
//                    }
                }
            }
            
            Pos pos = m_Logic.look(cX, cY, w.getDirection());
            if(pos.x != -1 || pos.y != -1)
            {
                types = m_Logic.locateAllAt(pos.x, pos.y);


                if(types.isEmpty())// && m_LastDir != w.getDirection())// || types.size() == 1 && types.get(0).compareTo("visited") == 0)
                {
                    m_LastDir = w.getDirection();
                    w.doAction(World.A_MOVE);
                    return;
                    //goalPos = pos;
                }
                else
                {
                    for (String type : types) 
                    {
                        String temp = type;
                        if(temp.compareTo("p_wumpus") == 0)
                        {
                            break;
                        }
                        else if(temp.compareTo("wumpus") == 0)
                        {
                            w.doAction(World.A_SHOOT);
                            return;
                        }
                        else if(temp.compareTo("p_pit") == 0)
                        {
                            break;
                        }
                        else if(temp.compareTo("pit") == 0)
                        {
                            break;
                        }
                        else if(temp.compareTo("visited") == 0 && m_LastDir != w.getDirection())
                        {
                            m_LastDir = w.getDirection();
                            w.doAction(World.A_MOVE);
                            return;
                        }
                    }
                }
            }
            
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
            
//            int dir = m_Logic.moveDir(cX, cY, goalPos.x, goalPos.y);
//            if(wumpusDir != -1)
//            {
//                if(wumpusDir > w.getDirection())
//                    w.doAction(w.A_TURN_RIGHT);
//                if(wumpusDir < w.getDirection())
//                    w.doAction(World.A_TURN_LEFT);
//                if(wumpusDir == w.getDirection())
//                    w.doAction(World.A_SHOOT);
//                
//                return;
//            }
//            else if(dir == w.getDirection())
//            {
//                w.doAction(World.A_MOVE);
//            }
//            else if(dir < w.getDirection() && dir != -1)
//            {
//                w.doAction(World.A_TURN_LEFT);
//            }
//            else if(dir > w.getDirection() && dir != -1)
//            {
//                w.doAction(World.A_TURN_RIGHT);
//            }
//            Square square = m_Logic.lookAtWithDir(cX, cY, w.getDirection());
//            if(square.pos.x != -1 && square.pos.y != -1)
//            {
//                if(m_Logic.possibleDangerIn(square.pos.x, square.pos.y))
//                {
//                    int rnd = (int)(Math.random() * 2);
//                    if (rnd == 0) 
//                    {
//                        w.doAction(World.A_TURN_LEFT);
//                        return;
//                    }
//                    if (rnd == 1)
//                    {
//                        w.doAction(World.A_TURN_RIGHT);
//                        return;
//                    }
//                }
//                else
//                {
//                    w.doAction(w.A_MOVE);
//                    return;
//                }
//            }
//            else
//            {
//                //Failsafe against invalid squares
//                //Random move actions
//                int rnd = (int)(Math.random() * 2);
//                if (rnd == 0) 
//                {
//                    w.doAction(World.A_TURN_LEFT);
//                    return;
//                }
//                if (rnd == 1)
//                {
//                    w.doAction(World.A_TURN_RIGHT);
//                    return;
//                }
//            }
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
