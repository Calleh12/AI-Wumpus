/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wumpusworld;

import alice.tuprolog.*;
import java.util.ArrayList;
import java.util.List;

enum Danger
{
    PIT(1),
    WUMPUS(2);
    
    private int danger;
    
    Danger(int p_Danger)
    {
        danger = p_Danger;
    }
    
    public int getValue()
    {
        return danger;
    }
}

class Pos
{
    public int x;
    public int y;

    public Pos()
    {
        x = -1;
        y = -1;
    }
}


class Square
{
    public Pos pos;
    public String type;
    
    public Square()
    {
        pos = new Pos();
        type = "unkown";
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
        if(info.isSuccess())
            count = 1;
        
        while(info.hasOpenAlternatives())
        {
            count++;
            info = m_Engine.solveNext();
        }
        
        Pos pos = new Pos();
        pos.x = -1;
        pos.y = -1;
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
        
//        if(info.isSuccess())
//        {
//            String x = info.getVarValue("A").toString();
//            String y = info.getVarValue("B").toString();
//
//            System.out.println(info.toString() + "\n, added possible wumpus to ("+x+","+y+")");
//        }
        
//        while(info.hasOpenAlternatives())
//        {
//            info = m_Engine.solveNext();
//            if(info.isSuccess())
//            {
//                String x = info.getVarValue("A").toString();
//                String y = info.getVarValue("B").toString();
//
//                System.out.println(info.toString() + "\n, added possible wumpus to ("+x+","+y+")");
//            }
//        }
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
    
    public Pos lookAhead(int p_X, int p_Y, int p_Dir) throws Exception
    {
        String look = "lookahead("+p_X +","+ p_Y +","+ p_Dir +",X,Y).";
        SolveInfo info = m_Engine.solve(look);
        
        Pos pos = new Pos();
        if(info.isSuccess())
        {
            Term tx = info.getVarValue("X");
            String sx = tx.toString();
            pos.x = Integer.parseInt(sx);
            Term ty = info.getVarValue("Y");
            String sy = ty.toString();
            pos.y = Integer.parseInt(sy);
        }
        
        return pos;
    }
    
    public ArrayList<Square> locateAround(int p_X, int p_Y) throws Exception
    {
        String around = "locatearound(" + p_X + "," + p_Y + ", What, A,B).";
        
        ArrayList<Square> squares = new ArrayList<Square>();
        
        SolveInfo info = m_Engine.solve(around);
        if(info.isSuccess())
        {
            Square square = new Square();
            square.pos.x = Integer.parseInt(info.getVarValue("A").toString());
            square.pos.y = Integer.parseInt(info.getVarValue("B").toString());
            square.type = info.getVarValue("What").toString();
            squares.add(square);
        }
        
        while(info.hasOpenAlternatives())
        {
            Square square = new Square();
            info = m_Engine.solveNext();
            if(info.isSuccess())
            {
                square.pos.x = Integer.parseInt(info.getVarValue("A").toString());
                square.pos.y = Integer.parseInt(info.getVarValue("B").toString());
                square.type = info.getVarValue("What").toString();
                squares.add(square);
            }
        }
        
        return squares;
    }
    
    public boolean possibleDangerAhead(int p_X, int p_Y) throws Exception
    {
        String danger = "locate(What," + p_X + "," + p_Y + ").";
        SolveInfo info = m_Engine.solve(danger);
        
        if(info.isSuccess())
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
        }
        
        return false;
    }
    
    public SolveInfo query(String p_Query) throws Exception
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
