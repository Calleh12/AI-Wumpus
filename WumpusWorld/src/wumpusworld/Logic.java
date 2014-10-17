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
     * if there is only one possible wumpus in the world it is safe to assume
     * that it the wumpus is at the possible wumpus location.
     * 
     * @return position of wumpus.
     * @throws Exception 
     */
    public Pos countPossibleWumpus() throws Exception
    {
        String solve = "locate(pwumpus,X,Y).";
        SolveInfo info = m_Engine.solve(solve);
        int count = 0;
        
        while(info.isSuccess())
        {
            count++;
            if(!info.hasOpenAlternatives())
                break;
            info = m_Engine.solveNext();
        }
        
        Pos pos = new Pos(-1,1);
        if(count == 1)
        {
            Term tx = info.getVarValue("X");
            Term ty = info.getVarValue("Y");
            
            String wumpus = "add_wumpus(" + tx.toString() + "," + ty.toString() + ").";
            
            info = m_Engine.solve(wumpus);
            removeLocationsWith("pwumpus");
        }
        
        return pos;
    }
    
    public void addLocation(String p_Type, int p_X, int p_Y) throws Exception
    {
        String add = "add_" + p_Type + "(" + appendPos(p_X, p_Y) + ",A,B).";
        SolveInfo info = m_Engine.solve(add);
                
        while(info.isSuccess())
        {
                String x = info.getVarValue("A").toString();
                String y = info.getVarValue("B").toString();

                System.out.println(info.toString() + "\n, added to ("+x+","+y+")");
            
             if(!info.hasOpenAlternatives())
                break;
            info = m_Engine.solveNext();
        }
    }
    
    public void removeLocationsWith(String p_Type) throws Exception
    {
        String remove = "retract(at(" + p_Type + ",X,Y)).";
        m_Engine.solve(remove);
    }
    
    public void removeLocation(int p_X, int p_Y) throws Exception
    {
        String remove = "retract(at(O," + appendPos(p_X, p_Y) + ")).";
        m_Engine.solve(remove);
    }
    
    public boolean locate(String p_Type, int p_X, int p_Y) throws Exception
    {
	String locate = "locate(" + appendType(p_Type, p_X, p_Y) + ").";
	SolveInfo info = m_Engine.solve(locate);
        
        if(info.isSuccess())
            return true;
        
        return false;
    }
    
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
            
    public ArrayList<Square> locateAround(int p_X, int p_Y) throws Exception
    {        
        String around = "locatearound(" + p_X + "," + p_Y + ", What, A,B).";
        SolveInfo info = m_Engine.solve(around);
        
        
        ArrayList<Square> squares = new ArrayList<Square>();
        while(info.isSuccess())
        {
            Square square = new Square();
           
            square.pos.x = Integer.parseInt(info.getVarValue("A").toString());
            square.pos.y = Integer.parseInt(info.getVarValue("B").toString());
            square.type = info.getVarValue("What").toString();
            squares.add(square);
            
            if(!info.hasOpenAlternatives())
                break;
            info = m_Engine.solveNext();
            
        }
        
        return squares;
    }
    
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
    
    public String locateWhat(int p_X, int p_Y) throws Exception
    {
        String type = "";
        String locate = "locate(What," + p_X + "," + p_Y + ").";
        SolveInfo info = m_Engine.solve(locate);
        
        if(info.isSuccess())
        {
            type = info.getVarValue("What").toString();
        }
        
        return type;
    }
    
    public boolean possibleDangerIn(int p_X, int p_Y) throws Exception
    {
        String danger = "locate(What," + p_X + "," + p_Y + ").";
        SolveInfo info = m_Engine.solve(danger);
 
        while(info.isSuccess())
        {
            String what = info.getVarValue("What").toString();

            if(what.compareTo("p_wumpus") == 0)
                return true;
            else if(what.compareTo("p_pit") == 0)
                return true;
            else if(what.compareTo("wumpus") == 0)
                return true;
            else if(what.compareTo("pit") == 0)
                return true;
            
            if(!info.hasOpenAlternatives())
                break;
            info = m_Engine.solveNext();
        }
        
        return false;
    }
    
    public SolveInfo solve(String p_Query) throws Exception
    {
        return m_Engine.solve(p_Query);
    }
    
    public void possibleLocations(String p_Type, int p_X, int p_Y) throws Exception
    {
	
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
