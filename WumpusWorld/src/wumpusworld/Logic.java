/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wumpusworld;

import alice.tuprolog.*;
import java.util.ArrayList;
import java.util.List;

class Pos
{
    public int x;
    public int y;

    public Pos(int p_X, int p_Y)
    {
        x = p_X;
        y = p_Y;
    }
}

class Square
{
    public Pos pos;
    public String type;
    
    public Square()
    {
        pos = new Pos(-1,1);
        type = "unknown";
    }
    
    public Square(Pos p_Pos, String p_Type)
    {
        pos = p_Pos;
        type = p_Type;
    }
}

/**
 *
 * @author rokc09
 */
public class Logic 
{
    private Prolog m_Engine;
    
    public Logic()
    {
	m_Engine = new Prolog();
	
	try
	{
	    Theory theory = new Theory(MyAgent.class.getResourceAsStream("resources/Rules.pl"));
	    m_Engine.addTheory(theory);
	}
	catch (Exception e)
	{
	    System.out.println("Error: " + e.getMessage());
	}
    }
    
    /**
     * Used for adding a location as a fact to prolog.
     * @param p_Type: what to add.
     * @param p_X: X position
     * @param p_Y: Y position
     * @throws Exception as tuprolog demands it.
     */
    public void addLocation(String p_Type, int p_X, int p_Y) throws Exception
    {
        String add = "add_" + p_Type + "(" + appendPos(p_X, p_Y) + ",A,B).";
        SolveInfo info = m_Engine.solve(add);
                
        while(info.isSuccess())
        {
                String x = info.getVarValue("A").toString();
                String y = info.getVarValue("B").toString();

             if(!info.hasOpenAlternatives())
                break;
            info = m_Engine.solveNext();
        }
    }
    /**
     * Used to look at a adjacent square with the direction.
     * @param p_X, current position X
     * @param p_Y, current position Y
     * @param p_Dir, current direction.
     * @return pos, the position of the "looked" at square.
     * @throws Exception 
     */
    public Pos look(int p_X, int p_Y, int p_Dir) throws Exception
    {
        String look = "look("+p_X +","+ p_Y +","+ p_Dir +",X,Y).";
        SolveInfo info = m_Engine.solve(look);
        
        Pos pos = new Pos(-1,-1);
        if(info.isSuccess())
        {
            pos.x = Integer.parseInt(info.getVarValue("X").toString());
            pos.y = Integer.parseInt(info.getVarValue("Y").toString());
        }
        
        return pos;
    }
    /**
     * Is these positions adjacents?
     * @param p_X, pos1.y
     * @param p_Y, pos1.y
     * @param p_A, pos2.x
     * @param p_B, pos2.y
     * @return true or false depending if they are adjacent
     * @throws Exception 
     */
    public boolean isAdjacent(int p_X, int p_Y, int p_A, int p_B) throws Exception
    {
        String adjacent = "adjacent(" + p_X + "," + p_Y + "," + p_A + "," + p_B + ").";
        SolveInfo info = m_Engine.solve(adjacent);
        
        if(info.isSuccess())
        {
            return true;
        }
        return false;
    }
    /**
     * It determines the direction to be headed for the GxGy pos.
     * @param p_X, from this pos.x
     * @param p_Y, from this pos.y
     * @param p_Gx, to this pos.x
     * @param p_Gy, to this pos.y
     * @return the direction to be headed.
     * @throws Exception 
     */
    public int moveDir(int p_X, int p_Y, int p_Gx, int p_Gy) throws Exception
    {
        String dir = "moveDir("+p_X +","+ p_Y +","+ p_Gx +","+p_Gy+", D).";
        SolveInfo info = m_Engine.solve(dir);
        
        if(info.isSuccess())
        {
           return Integer.parseInt(info.getVarValue("D").toString());
        }
        
        return -1;
    }
    /**
     * Locate all the types to the given position.
     * @param p_X
     * @param p_Y
     * @return returns what exists in the square.
     * @throws Exception 
     */
    public ArrayList<String> locateAllAt(int p_X, int p_Y) throws Exception
    {
        String around = "locate(What," + p_X + "," + p_Y + ").";
        
        ArrayList<String> what = new ArrayList<String>();
        SolveInfo info = m_Engine.solve(around);

        while(info.isSuccess())
        {
            String type = "";
            type = info.getVarValue("What").toString();
            what.add(type);
            
             if(!info.hasOpenAlternatives())
                break;
            info = m_Engine.solveNext();
        }
        
        return what;
    }
    /**
     * Checks the given square if the dangerlevel is present there.
     * @param p_X, pos.x
     * @param p_Y, pos.y
     * @param p_Danger, danger level: 4=p_pit, 3=pit, 2=p_wumpus, 1=wumpus
     * @return true if they are there.
     * @throws Exception 
     */
    public boolean locateDanger(int p_X, int p_Y, int p_Danger) throws Exception
    {
        String type = "";
        String danger = "locateDanger(What," + p_X + "," + p_Y + "," + p_Danger+").";
        SolveInfo info = m_Engine.solve(danger);
        
        if(info.isSuccess())
        {
            type = info.getVarValue("What").toString();
            return true;
        }
        
        return false;
    }
    /**
     * Just solves the query that is sent.
     * @param p_Query, query that needs to be solved.
     * @return SolveInfo.
     * @throws Exception 
     */
    public SolveInfo solve(String p_Query) throws Exception
    {
        return m_Engine.solve(p_Query);
    }
    
    public String appendType(String p_Type, int p_X, int p_Y)
    {
	return p_Type + "," + p_X + "," + p_Y;
    }
    
    public String appendPos(int p_X, int p_Y)
    {
	return p_X + "," + p_Y;
    }
}
