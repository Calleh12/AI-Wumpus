/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wumpusworld;

import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Theory;

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
    
    public void addRule(int p_X, int p_Y, String p_Type) throws Exception
    {
	String theory = p_Type + "("+p_X+","+p_Y+").";
	m_Engine.addTheory(new Theory(theory));
    }
    
    public void addVisited(int p_X, int p_Y) throws Exception
    {
	SolveInfo info = m_Engine.solve("visited" + "("+p_X+","+p_Y+").");
	if(!info.isSuccess())
	{
	    String theory = "visited" + "("+p_X+","+p_Y+").";
	    m_Engine.addTheory(new Theory(theory));
	}
    }
}
