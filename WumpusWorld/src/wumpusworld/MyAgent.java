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
        m_Path = new ArrayList<>();
        m_Map = new ArrayList<ArrayList<String>>();
    }
    
    /**
     * A* pathfinding algorithm, psuedo code from wikipedia.
     * @param p_Sx, start X
     * @param p_Sy, start Y
     * @param p_Gx, goal X
     * @param p_Gy, goal Y
     * @param p_Danger, max danger level to search for.
     * @return
     * @throws Exception 
     */
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
                //if the openlist size is only 1 there is no need to check if it is adjacent.
                //it could be possible to remove all nonadjacent squares when a current has 
                //been selected.
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
                //look at its neighbour.
                Pos pos = m_Logic.look(current.x, current.y, i);
                //is it valid?
                if(pos.x == -1 || pos.y == -1)
                    continue;
                boolean danger = false;
                //does it have any danger? Lower p_Danger means skipping to check dangers.
                for(int d = p_Danger; d > 0; d--)
                {
                    if(m_Logic.locateDanger(pos.x, pos.y, d))
                    {
                        danger = true;
                    }
                }
                if(danger)
                    continue;
                
                //Create the neighbour node.
                Node neighbour = new Node(pos.x, pos.y);
                boolean exists = false;
                //Checks if the neighbour node is in the closed list
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
                //calculate the new g score for the neighbour.
                int g = current.g + 1;//Math.abs(current.x-neighbour.x) + Math.abs(current.y-neighbour.y);
               //checks the if the neighbour exists in the open list.
                for(Node nodes : open)
                {
                    if(nodes.x == neighbour.x && nodes.y == neighbour.y)
                    {
                        exists = true;
                        break;
                    }
                }
                //if the neighbour does not exists in any of the lists and
                // if the new Gscore is lower than the neighbour's gscore.
                if(!exists || g < neighbour.g)
                {
                    neighbour.previous = current;
                    neighbour.g = g;
                    neighbour.h = Math.sqrt(Math.pow((pos.x-p_Gx),2) + Math.pow((pos.y-p_Gy),2))*2;
                    if(!exists)
                        open.add(neighbour);
                }
            }
            //"lastCurrent" is used to determine if the nodes up for being current
            // is adjacent.
            last = current;
            //Check to break the pathfinding so it doens't get stuck and locks
            // the program.
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
            //always add the new node at the beginning of the list
            nodes.add(0,temp);
            //get the previous node from the temp.
            temp = temp.previous;
            //if the temp is equal to the start node then the path is recreated.
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
                //loops over the board, trying to find a unknown square.
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
                //loops over the board again but this time to find a nonvisited
                //square
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
    
     private int pathFind(Pos start, int danger) throws Exception {
//        ArrayList<String> sTypes;
//        for(int i = 1; i < 5; i++)
//        {
//            for(int j = 1; j < 5; j++)
//            {
//                //just debugging to see the map and how it is represented from prolog
//                sTypes = m_Logic.locateAllAt(j, i);
//                m_Map.add(sTypes);
//            }
//        }
        //If there exist a goal to be reached no need to search again. 
        if(m_Path == null || m_Path.isEmpty())
        {
            Pos goal = findGoalPos();
            
            m_Path = aStarPath(start.x, start.y, goal.x, goal.y, danger);
            while(m_Path == null)
            {
                //if a route to a square was not reached, decrease the dangerlevel 
                //and try again.
                m_Path = aStarPath(start.x, start.y, goal.x, goal.y, danger--);
                if(danger <= 0)
                    break;
            }
        }
        return danger;
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
            //update the player's position in KB.
            m_Logic.solve("updatePlayer("+cX+","+cY+",A,B).");
	    //Test the environment
	    if (w.hasBreeze(cX, cY))
	    {
                //conditions have changed so a new path needs to be found.
                m_Path.clear();
		System.out.println("I am in a Breeze");
                //query the KB to add a breeze, i.e. use add_breeze rule.
		m_Logic.addLocation("breeze", cX, cY);
	    }
	    if (w.hasStench(cX, cY))
	    {
		System.out.println("I am in a Stench");
		m_Logic.addLocation("stench", cX, cY);
                //Special case used for when starting in a stench.
                if(cX == 1 && cY == 1 && w.hasArrow())
                {
                    w.doAction(World.A_SHOOT);
                    Pos pos = m_Logic.look(cX, cY, w.getDirection());
                    String s = "retract(at(p_wumpus,"+pos.x+","+pos.y+")).";
                    SolveInfo info = m_Logic.solve(s);
                    return;
                }
                
                //conditions have changed so a new path needs to be found.
                m_Path.clear();
	    }
	    if (w.hasPit(cX, cY))
	    {
                //conditions have changed so a new path needs to be found.
                m_Path.clear();
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
            //always tries to add a location to be visited
            //Also calls the rule add_visited which does alot. :O
	    m_Logic.addLocation("visited", cX, cY);
            
            //m_Logic.countPossibleWumpus();
            //If the wumpus is no longer alive, remove all the facts regarding it 
            //from the KB:
            
            //Converts cX,cY into a position
            Pos start = new Pos(cX,cY);
            
            ArrayList<String> sTypes = new ArrayList<>();
            m_Map.clear();
            
            int danger = 4;
            //Due to refactoring, it takes two arguments and returns one of them.
            danger = pathFind(start, danger);
            //get the direction to the nearest node in path
            int dir = m_Logic.moveDir(cX, cY, m_Path.get(0).x, m_Path.get(0).y);
            //if the AI is looking to the node
            if(dir == w.getDirection())
            {
                ArrayList<String> whats = new ArrayList<>();
                whats = m_Logic.locateAllAt(m_Path.get(0).x, m_Path.get(0).y);
                //check what is at the square and if it is a wumpus, SHOOOT!!!
                for(String what : whats)
                {
                    if(what.compareTo("wumpus") == 0)
                    {
                        w.doAction(World.A_SHOOT);
                        if(!w.wumpusAlive())
                        {
                            m_Logic.solve("removeAll(wumpus).");
                            m_Logic.solve("removeAll(p_wumpus).");
                        }
                        return;
                    }
                }
                //otherwise just move forward and remove the node.
                w.doAction(World.A_MOVE);
                m_Path.remove(0);
            }
            else if(dir == 3 && w.getDirection() == 0)
            {
                //If the AI is looking up but needs to go to the left, turn left.
                w.doAction(World.A_TURN_LEFT);
            }
            else if(dir == 0 && w.getDirection() == 3)
            {
                //if the AI is looking left and needs to move up, turn right.
                w.doAction(World.A_TURN_RIGHT);
            }
            else if(dir < w.getDirection())
            {
                //if the direction needed to be headed is lower then what the AI
                //currently is looking, it needs to turn left.
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
}
