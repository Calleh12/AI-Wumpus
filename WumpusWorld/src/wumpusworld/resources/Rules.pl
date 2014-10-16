%if sats
%breeze(X) -> true;false.
updatePlayer(X,Y,A,B) :- locate(player,A,B), retract(at(player,A,B)), assert(at(player,X,Y)).
at(player,1,1).
inside_map(X,Y) :- (X>0, Y>0), (X<5, Y<5).
locate(O,X,Y) :- at(O,X,Y).
add_location(O,X,Y) :- inside_map(X,Y), not(locate(O,X,Y)), assert(at(O,X,Y)).

locatearound(X,Y,O,A,B) :- adj(0, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B), locate(O,A,B);
			adj(1, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B), locate(O,A,B);
			adj(2, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B), locate(O,A,B);
			adj(3, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B), locate(O,A,B).
			
adjcent(X,Y,A,B,I) :- adj(I, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B).

adj(0, [0,1]).
adj(1, [1,0]).
adj(2, [0,-1]).
adj(3, [-1,0]).

%at(visited,1,1).
%at(player,2,1).
%at(stench,2,1).
%at(p_wumpus,3,1).
%at(p_wumpus,2,2).
%at(visited,2,1).
			
findPit(X,Y,A,B) :- not(locate(visited,X,Y)), locatearound(X,Y,breeze,A,B), locate(player,Z,W), (Z =\= A, W =\= B), locate(breeze,A,B), locate(visited,A,B).
findWumpus(X,Y,A,B) :- not(locate(visited,X,Y)), locatearound(X,Y,stench,A,B), locate(player,Z,W), (Z =\= A, W =\= B), locate(stench,A,B), locate(visited,A,B).

%test_stench(X,Y,A,B) :- A is X+1, B is Y, add_location(p_wumpus,A,B).

add_stench(X,Y,A,B) :- add_location(stench,X,Y);
			A is X+1, B is Y, not(locate(visited,A,B)), add_location(p_wumpus,A,B);
			A is X-1, B is Y, not(locate(visited,A,B)), add_location(p_wumpus,A,B);
			A is X, B is Y+1, not(locate(visited,A,B)), add_location(p_wumpus,A,B);
			A is X, B is Y-1, not(locate(visited,A,B)), add_location(p_wumpus,A,B);
			A is X+1, B is Y, findWumpus(A,B,Z,W), add_location(wumpus,A,B);
			A is X-1, B is Y, findWumpus(A,B,Z,W), add_location(wumpus,A,B);
			A is X, B is Y+1, findWumpus(A,B,Z,W), add_location(wumpus,A,B);
			A is X, B is Y-1, findWumpus(A,B,Z,W), add_location(wumpus,A,B).
				
add_breeze(X,Y,A,B) :- add_location(breeze,X,Y); 
			A is X+1, B is Y, not(locate(visited,A,B)), add_location(p_pit,A,Y);
			A is X-1, B is Y, not(locate(visited,A,B)), add_location(p_pit,A,Y);
			A is X, B is Y+1, not(locate(visited,A,B)), add_location(p_pit,X,B);
			A is X, B is Y-1, not(locate(visited,A,B)), add_location(p_pit,X,B);
			A is X+1, B is Y, findPit(A,B,Z,W), add_location(pit,A,B);
			A is X-1, B is Y, findPit(A,B,Z,W), add_location(pit,A,B);
			A is X, B is Y+1, findPit(A,B,Z,W), add_location(pit,A,B);
			A is X, B is Y-1, findPit(A,B,Z,W), add_location(pit,A,B).
				
add_pit(X,Y,A,B) :- add_location(pit,X,Y); 
			A is X+1, add_location(breeze,A,Y);
			A is X-1, add_location(breeze,A,Y);
			B is Y+1, add_location(breeze,X,B);
			B is Y-1, add_location(breeze,X,B).
			
removeAll(What) :- retract(at(What,X,Y)).
				
add_wumpus(X,Y,A,B) :- add_location(wumpus,X,Y); 
			A is X+1, add_location(stench,A,Y);
			A is X-1, add_location(stench,A,Y);
			B is Y+1, add_location(stench,X,B);
			B is Y-1, add_location(stench,X,B).
				
add_visited(X,Y,A,B) :- add_location(visited,X,Y);
			remove_p_pit(X,Y,A,B); 
			remove_p_wumpus(X,Y,A,B);
			locate(p_wumpus, X,Y), retract(at(p_wumpus, X,Y));
			locate(p_pit, X,Y), retract(at(p_pit, X,Y));
			locate(wumpus,A,B), retract(at(p_wumpus,Z,W));
			locatearound(X,Y,pit,Z,W), remove_p_pit(X,Y,A,B).
			
remove_p_pit(X,Y,A,B) :- 
			A is X+1, B is Y, not(locate(breeze,X,Y)), retract(at(p_pit,A,B)); 
			A is X-1, B is Y, not(locate(breeze,X,Y)), retract(at(p_pit,A,B));
			A is X, B is Y+1, not(locate(breeze,X,Y)), retract(at(p_pit,A,B)); 
			A is X, B is Y-1, not(locate(breeze,X,Y)), retract(at(p_pit,A,B)).
			
remove_p_wumpus(X,Y,A,B) :- 
			A is X+1, B is Y, not(locate(stench,X,Y)), retract(at(p_wumpus,A,B)); 
			A is X-1, B is Y, not(locate(stench,X,Y)), retract(at(p_wumpus,A,B));
			A is X, B is Y+1, not(locate(stench,X,Y)), retract(at(p_wumpus,A,B)); 
			A is X, B is Y-1, not(locate(stench,X,Y)), retract(at(p_wumpus,A,B)).


look(X,Y,D,A,B) :- dir(D, [Z,W]), A is Z+X, B is W+Y, inside_map(A,B).
dir(0, [0,1]).
dir(1, [1,0]).
dir(2, [0,-1]).
dir(3, [-1,0]).

danger(p_pit).
danger(pit).
danger(p_wumpus).
danger(wumpus).

locateDanger(What,X,Y) :- locate(What,X,Y), danger(What).

moveDir(X,Y,Gx,Gy,D) :- A is Gx-X, B is Gy-Y, dir(D,[A,B]).

x(X) :- (X>0) -> true , (X<5) -> true.


%adjcent([X,Y]) :- inside_map(X,Y), (adjc([X,Y],[A,B]), locate(pit, X,Y)).

%adjc([X,Y],[A,B]) :- ((X+1) =:= A), Y =:= B.

%aRight([X,Y],[A,B]) :- ((X+1) =:= A, Y =:= B).

%adjX([X,Y],[A,B]) :- ((X+1) =:= A, Y =:= B); ((X-1) =:= A, Y=:=B).
%adjY([X,Y],[A,B]) :- (X =:= A, (Y+1) =:= B); (X=:=A, (Y-1) =:= B).

%adj([X,Y],[A,B]) :- (x(A) , x(B)) -> (adjX([X,Y],[A,B]) ; adjY([X,Y],[A,B])).

%locate_adj(O,[X,Y],[A,B]) :- (adj([X,Y],[A,B]), locate(O,A,B).