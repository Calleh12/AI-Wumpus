%
% Robin Karlsson, rokc09.
%

%%%%%%%%%%%%%%%%%%%%
% Facts 
at(player,1,1).

adj(0, [0,1]).
adj(1, [1,0]).
adj(2, [0,-1]).
adj(3, [-1,0]).

danger(p_pit,3).
danger(pit,2).
danger(p_wumpus,1).
danger(wumpus, 0).
%%%%%%%%%%%%%%%%%%%%

/**
* Updates the player position.
* Needs a player position to be initilized otherwize it cant retract and assert it.
*/
updatePlayer(X,Y,A,B) :- locate(player,A,B), retract(at(player,A,B)), assert(at(player,X,Y)).
/**
* Checks if the given coordinates is inside the map.
*/
inside_map(X,Y) :- (X>0, Y>0), (X<5, Y<5).
/**
* Just passthrough to at-facts.
*/
locate(O,X,Y) :- at(O,X,Y).
/**
* Checks all the adjacents squares to given square and see if the coordinates is
* inside and locate what is specified.
*/
locatearound(X,Y,O,A,B) :- adj(0, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B), locate(O,A,B);
			adj(1, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B), locate(O,A,B);
			adj(2, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B), locate(O,A,B);
			adj(3, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B), locate(O,A,B).
/**
* Just looks at the adjacents squares
*/
adjacent(X,Y,A,B) :- adj(0, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B);
			adj(1, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B);
			adj(2, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B);
			adj(3, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B).
/**
* Locates the adjacent squares for a given coordinate and checks if the specified 
* type is there and that the player is not standing in that square.
*/
find_adjacent(X,Y,A,B,O) :- 
			not(locate(visited,X,Y)), adj(0, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B), locate(O,A,B), locate(player,R,T), (R =\= A; T =\= B);
			not(locate(visited,X,Y)), adj(1, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B), locate(O,A,B), locate(player,R,T), (R =\= A; T =\= B);
			not(locate(visited,X,Y)), adj(2, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B), locate(O,A,B), locate(player,R,T), (R =\= A; T =\= B);
			not(locate(visited,X,Y)), adj(3, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B), locate(O,A,B), locate(player,R,T), (R =\= A; T =\= B).

/**
* Adds a fact, its location and checks if the given coordinates is inside the map and 
* that the square is not visited.
*/
add_location(O,X,Y) :- inside_map(X,Y), not(locate(O,X,Y)), assert(at(O,X,Y)).
/**
* Adds a stench to a location and its potential wumpuses.
* Also checks if there could possibly exists a real wumpus by checking if a possible wumpus 
* has two connecting stenches. Overdone with the help of the player as I don't know how to keep count. 
*/
add_stench(X,Y,A,B) :- add_location(stench,X,Y),(
			A is X+1, B is Y, not(locate(visited,A,B)), add_location(p_wumpus,A,B);
			A is X-1, B is Y, not(locate(visited,A,B)), add_location(p_wumpus,A,B);
			A is X, B is Y+1, not(locate(visited,A,B)), add_location(p_wumpus,A,B);
			A is X, B is Y-1, not(locate(visited,A,B)), add_location(p_wumpus,A,B);
			A is X+1, B is Y, find_adjacent(A,B,Z,W, stench), add_location(wumpus,A,B);
			A is X-1, B is Y, find_adjacent(A,B,Z,W, stench), add_location(wumpus,A,B);
			A is X, B is Y+1, find_adjacent(A,B,Z,W, stench), add_location(wumpus,A,B);
			A is X, B is Y-1, find_adjacent(A,B,Z,W, stench), add_location(wumpus,A,B)).

/**
* look at add_stench
*/
add_breeze(X,Y,A,B) :- add_location(breeze,X,Y); 
			A is X+1, B is Y, not(locate(visited,A,B)), add_location(p_pit,A,Y);
			A is X-1, B is Y, not(locate(visited,A,B)), add_location(p_pit,A,Y);
			A is X, B is Y+1, not(locate(visited,A,B)), add_location(p_pit,X,B);
			A is X, B is Y-1, not(locate(visited,A,B)), add_location(p_pit,X,B);
			A is X+1, B is Y, find_adjacent(A,B,Z,W,breeze), add_location(pit,A,B);
			A is X-1, B is Y, find_adjacent(A,B,Z,W,breeze), add_location(pit,A,B);
			A is X, B is Y+1, find_adjacent(A,B,Z,W,breeze), add_location(pit,A,B);
			A is X, B is Y-1, find_adjacent(A,B,Z,W,breeze), add_location(pit,A,B).
/**
* Passthrough to add location
*/	
add_pit(X,Y,A,B) :- add_location(pit,X,Y).
/**
* Adds a visited square at given coordinates.
* Also removes pit/wumpus if nto standing in a breeze/stench in its adjacent squares.
* If there is a not a possible pit/wumpus here remove it.
* As with finding the real wumpus and a pit, the same method is also used for 
* finding if a adjacent square could possible hold a pit/wumpus, if not remove 
* the type.
*/		
add_visited(X,Y,A,B) :- add_location(visited,X,Y);
			remove_p_pit(X,Y,A,B); 
			remove_p_wumpus(X,Y,A,B);
			locate(p_wumpus, X,Y), retract(at(p_wumpus, X,Y));
			locate(p_pit, X,Y), retract(at(p_pit, X,Y));
			locate(wumpus,A,B), retract(at(p_wumpus,Z,W));
			A is X+1, B is Y, find_adjacent(A,B,Z,W,breeze), retract(at(p_wumpus,A,B)); 
			A is X-1, B is Y, find_adjacent(A,B,Z,W,breeze), retract(at(p_wumpus,A,B));
			A is X, B is Y+1, find_adjacent(A,B,Z,W,breeze), retract(at(p_wumpus,A,B)); 
			A is X, B is Y-1, find_adjacent(A,B,Z,W,breeze), retract(at(p_wumpus,A,B));
			A is X+1, B is Y, find_adjacent(A,B,Z,W,stench), retract(at(p_pit,A,B)); 
			A is X-1, B is Y, find_adjacent(A,B,Z,W,stench), retract(at(p_pit,A,B));
			A is X, B is Y+1, find_adjacent(A,B,Z,W,stench), retract(at(p_pit,A,B)); 
			A is X, B is Y-1, find_adjacent(A,B,Z,W,stench), retract(at(p_pit,A,B)).
			%locatearound(X,Y,pit,Z,W), remove_p_pit(X,Y,A,B).
/**
* Removing adjacent p_pits if not standing in a breeze
*/
remove_p_pit(X,Y,A,B) :- 
			A is X+1, B is Y, not(locate(breeze,X,Y)), retract(at(p_pit,A,B)); 
			A is X-1, B is Y, not(locate(breeze,X,Y)), retract(at(p_pit,A,B));
			A is X, B is Y+1, not(locate(breeze,X,Y)), retract(at(p_pit,A,B)); 
			A is X, B is Y-1, not(locate(breeze,X,Y)), retract(at(p_pit,A,B)).
	
/**
* Check remove_p_pit
*/		
remove_p_wumpus(X,Y,A,B) :- 
			A is X+1, B is Y, not(locate(stench,X,Y)), retract(at(p_wumpus,A,B)); 
			A is X-1, B is Y, not(locate(stench,X,Y)), retract(at(p_wumpus,A,B));
			A is X, B is Y+1, not(locate(stench,X,Y)), retract(at(p_wumpus,A,B)); 
			A is X, B is Y-1, not(locate(stench,X,Y)), retract(at(p_wumpus,A,B)).
/**
* Remove all of a single type.
*/
removeAll(What) :- retract(at(What,X,Y)).

/**
* Gives the direction of which the GxGy square is located, could need a check first to see if the 
* GxGy node is adjacent.
*/
moveDir(X,Y,Gx,Gy,D) :- A is Gx-X, B is Gy-Y, adj(D,[A,B]).
/**
* With a direction it will look at a adjacent square and if that square is inside
* the map. The coordinates for the square can be fetched from A,B.
*/
look(X,Y,D,A,B) :- adj(D, [Z,W]), A is Z+X, B is W+Y, inside_map(A,B).


/**
* check to see if something is of danger, e.g. wumpus and not breeze.
*/
locateDanger(What,X,Y,D) :- locate(What,X,Y), danger(What,D).


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%&&&&&&&&&&&&&
%&&&&&&&&&&&&& Testing area &&&&&&&&&&&&&&&&&
%&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

			
%findPit(X,Y,A,B) :- not(locate(visited,X,Y)), locatearound(X,Y,breeze,A,B), locate(player,Z,W), (Z =\= A, W =\= B), locate(breeze,A,B), locate(visited,A,B).
%, locate(player,Z,W), (Z =\= A, W =\= B)
%findWumpus(X,Y,A,B) :- not(locate(visited,X,Y)), locatearound(X,Y,stench,A,B), locate(player,Z,W), (Z =\= A, W =\= B), locate(stench,A,B), locate(visited,A,B).

%test_stench(X,Y,A,B) :-	A is X, B is Y+1, findWumpus(A,B,Z,W), add_location(wumpus,A,B).

/*			
*/

%whichIsCloser(X,Y,A,B,P,D,C,V) :- adj(0, [Z,W]), C is X+Z, V is Y+W, inside_map(C,V), isPlayerCloser(A,B,C,V,D,P);
%			adj(1, [Z,W]), C is X+Z, V is Y+W, inside_map(C,V), isPlayerCloser(A,B,C,V,D,P);
%			adj(2, [Z,W]), C is X+Z, V is Y+W, inside_map(C,V), isPlayerCloser(A,B,C,V,D,P);
%			adj(3, [Z,W]), C is X+Z, V is Y+W, inside_map(C,V), isPlayerCloser(A,B,C,V,D,P).

%diff(X,Y,A,B,C,V) :- Z is X-A, W is Y-B, C is Z, V is W.
%sq(X,Y,A,B) :- A is X*X, B is Y*Y.
%calcDist(X,Y,A,B,D) :- diff(X,Y,A,B,Z,W), sq(Z,W,C,V), D is sqrt(C+V).
%isPlayerCloser(X,Y,A,B,D,P):- calcDist(X,Y,A,B,D), at(player,Z,W), calcDist(Z,W,A,B,P), P =< D.

%closer(X,Y,A,B,Z,W,R) :- calcDist(X,Y,Z,W,D), calcDist(A,B,Z,W,C), min(C,D,M), R is M.
%min(A, B, Min) :- A =< B -> Min = A ; Min = B.

%adjcent([X,Y]) :- inside_map(X,Y), (adjc([X,Y],[A,B]), locate(pit, X,Y)).

%adjc([X,Y],[A,B]) :- ((X+1) =:= A), Y =:= B.

%aRight([X,Y],[A,B]) :- ((X+1) =:= A, Y =:= B).

%adjX([X,Y],[A,B]) :- ((X+1) =:= A, Y =:= B); ((X-1) =:= A, Y=:=B).
%adjY([X,Y],[A,B]) :- (X =:= A, (Y+1) =:= B); (X=:=A, (Y-1) =:= B).

%adj([X,Y],[A,B]) :- (x(A) , x(B)) -> (adjX([X,Y],[A,B]) ; adjY([X,Y],[A,B])).

%locate_adj(O,[X,Y],[A,B]) :- (adj([X,Y],[A,B]), locate(O,A,B).