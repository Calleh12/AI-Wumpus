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
    
    class Node
    {
        public int x;
        public int y;
        
        public Node previous;
        public int g;
        public double h;
        
        public Node(){}
        
        public Node(int p_X, int p_Y)
        {
            x = p_X;
            y = p_Y;
        }
        
        public double getF()
        {
            return g + h;
        }
    }
    
    private World w;
    private Logic m_Logic;
    private Pos goalPos;
    private Pos lastGoalPos;
    private int m_LastDir;
    private ArrayList<Node> m_Path;
    private ArrayList<ArrayList<String>> m_Map;
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
        m_Path = new ArrayList<>();
        m_Map = new ArrayList<ArrayList<String>>();
    }
    
    public ArrayList<Node> aStarPath(int p_Sx, int p_Sy, int p_Gx, int p_Gy, int p_Danger) throws Exception
    {
        ArrayList<Node> closed = new ArrayList<>();
        ArrayList<Node> open = new ArrayList<>();
        ArrayList<Node> path = new ArrayList<>();
        Node start = new Node(p_Sx, p_Sy);
        Node last = new Node(p_Sx, p_Sy);
        start.h = Math.abs(p_Sx-p_Gx) + Math.abs(p_Sy-p_Gy);;
        open.add(start);
        int currDepth = 0;
        int maxDepth = 16;
        
        ArrayList<String> types = new ArrayList<>();
        int gScore = 0;
        while(!open.isEmpty())
        {
            Node current = new Node();
            double fScore = 100000;
            for(Node node : open)
            {
                if(open.size() > 1 && !m_Logic.isAdjacent(node.x, node.y, last.x, last.y))
                    continue;
                
                if(fScore > node.getF())
                {
                    fScore = node.getF();
                    current = node;
                }
            }
            if(current.x == p_Gx && current.y == p_Gy)
            {
                return calculatePath(start, current);
            }
            
            open.remove(current);
            closed.add(current);
            for(int i = 0; i < 4; i++)
            {
                Pos pos = m_Logic.look(current.x, current.y, i);
                if(pos.x == -1 || pos.y == -1)
                    continue;
                boolean danger = false;
                for(int d = p_Danger; d > 0; d--)
                {
                    if(m_Logic.locateDanger(pos.x, pos.y, d))
                    {
                        danger = true;
                    }
                }
                if(danger)
                    continue;
                
                Node neighbour = new Node(pos.x, pos.y);
                boolean exists = false;
                for(Node nodes : closed)
                {
                    if(nodes.x == neighbour.x && nodes.y == neighbour.y)
                    {
                        exists = true;
                        break;
                    }
                }
                if(exists)
                    continue;
                
                int g = current.g + Math.abs(current.x-neighbour.x) + Math.abs(current.y-neighbour.y);
               
                for(Node nodes : open)
                {
                    if(nodes.x == neighbour.x && nodes.y == neighbour.y)
                    {
                        exists = true;
                        break;
                    }
                }
                
                if(!exists || g < neighbour.g)
                {
                    neighbour.previous = current;
                    neighbour.g = g;
                    neighbour.h = Math.sqrt(Math.pow((pos.x-p_Gx),2) + Math.pow((pos.y-p_Gy),2))*2;
                    if(!exists)
                        open.add(neighbour);
                }
            }
            last = current;
            if(currDepth > maxDepth)
                return null;
            currDepth++;
        }
        return null;
    }
    
    public ArrayList<Node> calculatePath(Node p_Start, Node p_Goal)
    {
        ArrayList<Node> nodes = new ArrayList<>();
        boolean done = false;
        Node temp = p_Goal;
        while(!done)
        {
            nodes.add(0,temp);
            
            temp = temp.previous;
            
            if(temp == p_Start)
                done = true;
        }
        
        return nodes;
    }
    
    public Pos findGoalPos() throws Exception
    {
        ArrayList<String> sTypes = new ArrayList<>();
        for(int i = 1; i < 5; i++)
        {
            for(int j = 1; j < 5; j++)
            {
                sTypes = m_Logic.locateAllAt(j, i);
                if(sTypes.isEmpty())
                {
                    return new Pos(j,i);
                }
            }
        }
        
        for(int i = 1; i < 5; i++)
        {
            for(int j = 1; j < 5; j++)
            {
                boolean visited = false;
                sTypes = m_Logic.locateAllAt(j, i);
                
                for(String type : sTypes)
                {
                    if(type.compareTo("visited") == 0)
                    {
                        visited = true;
                    }
                }
                
                if(!visited)
                    return new Pos(j,i);
            }
        }
        return new Pos(-1,-1);
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
            if(!w.wumpusAlive())
            {
                m_Logic.solve("removeAll(wumpus).");
            }
            
            if(cX == 1 && cY == 1)
            {
                Pos pos = m_Logic.look(cX, cY, w.getDirection());
                ArrayList<String> types = m_Logic.locateAllAt(pos.x, pos.y);
                for(String type : types)
                {
                    if(type.compareTo("p_wumpus") == 0)
                    {
                        w.doAction(World.A_SHOOT);
                        String s = "retract(at(p_wumpus,"+pos.x+","+pos.y+")).";
                        m_Logic.solve(s);
                        return;
                    }
                    
                    if(type.compareTo("p_pit") == 0)
                    {
                        if(!w.hasArrow())
                        {
                            w.doAction(World.A_MOVE);
                            return;
                        }
                    }
                }
            }
            
            Pos start = new Pos(cX,cY);
            
            ArrayList<String> sTypes = new ArrayList<>();
            m_Map.clear();
            
            int danger = 4;
            danger = pathFind(start, danger);
            
            int dir = m_Logic.moveDir(cX, cY, m_Path.get(0).x, m_Path.get(0).y);
            if(dir == w.getDirection())
            {
                ArrayList<String> whats = new ArrayList<>();
                whats = m_Logic.locateAllAt(m_Path.get(0).x, m_Path.get(0).y);
                for(String what : whats)
                {
                    if(what.compareTo("wumpus") == 0)
                    {
                        w.doAction(World.A_SHOOT);
                        return;
                    }
                    if(what.compareTo("p_wumpus") == 0)
                    {
                        m_Path.clear();
                        return;
                    }
                    if(danger >= 4 && what.compareTo("p_pit") == 0)
                    {
                        m_Path.clear();
                        return;
                    }
                }
                w.doAction(World.A_MOVE);
                m_Path.remove(0);
            }
            else if(dir == 3 && w.getDirection() == 0)
            {
                w.doAction(World.A_TURN_LEFT);
            }
            else if(dir == 0 && w.getDirection() == 3)
            {
                w.doAction(World.A_TURN_RIGHT);
            }
            else if(dir < w.getDirection())
            {
                w.doAction(World.A_TURN_LEFT);
            }
            else if(dir > w.getDirection())
            {
                w.doAction(World.A_TURN_RIGHT);
            }
	}
	
	catch(Exception e)
	{
	    System.out.println("Something went wrong, error: " + e.getMessage());
	}
    }

    private int pathFind(Pos start, int danger) throws Exception {
        ArrayList<String> sTypes;
        for(int i = 1; i < 5; i++)
        {
            for(int j = 1; j < 5; j++)
            {
                sTypes = m_Logic.locateAllAt(j, i);
                m_Map.add(sTypes);
            }
        }
        if(m_Path != null && m_Path.isEmpty())
        {
            Pos goal = findGoalPos();
            
            m_Path = aStarPath(start.x, start.y, goal.x, goal.y, danger);
            while(m_Path == null)
            {
                m_Path = aStarPath(start.x, start.y, goal.x, goal.y, danger--);
                if(danger <= 0)
                    break;
            }
        }
        return danger;
    }
}
