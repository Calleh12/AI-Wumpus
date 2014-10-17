%if sats
%breeze(X) -> true;false.
updatePlayer(X,Y,A,B) :- locate(player,A,B), retract(at(player,A,B)), assert(at(player,X,Y)).
inside_map(X,Y) :- (X>0, Y>0), (X<5, Y<5).
locate(O,X,Y) :- at(O,X,Y).
add_location(O,X,Y) :- inside_map(X,Y), not(locate(O,X,Y)), assert(at(O,X,Y)).

locatearound(X,Y,O,A,B) :- adj(0, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B), locate(O,A,B);
			adj(1, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B), locate(O,A,B);
			adj(2, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B), locate(O,A,B);
			adj(3, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B), locate(O,A,B).
			
adjacent(X,Y,A,B) :- adj(0, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B);
			adj(1, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B);
			adj(2, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B);
			adj(3, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B).

adj(0, [0,1]).
adj(1, [1,0]).
adj(2, [0,-1]).
adj(3, [-1,0]).

/*
at(visited,1,1).
at(visited,2,1).
at(visited,1,2).
at(visited,1,3).
at(visited,2,3).
at(stench,3,3).
at(p_wumpus,3,4).
at(p_wumpus,4,3).
at(p_wumpus,3,2).
at(visited,3,3).
*/
/*
at(visited,1,1).
at(stench,2,1).
at(p_wumpus,2,2).
at(p_wumpus,3,1).
at(visited,2,1).
*/



at(player,1,1).
			
findPit(X,Y,A,B) :- not(locate(visited,X,Y)), locatearound(X,Y,breeze,A,B), locate(player,Z,W), (Z =\= A, W =\= B), locate(breeze,A,B), locate(visited,A,B).
%, locate(player,Z,W), (Z =\= A, W =\= B)
findWumpus(X,Y,A,B) :- not(locate(visited,X,Y)), locatearound(X,Y,stench,A,B), locate(player,Z,W), (Z =\= A, W =\= B), locate(stench,A,B), locate(visited,A,B).

test_stench(X,Y,A,B) :-	A is X, B is Y+1, findWumpus(A,B,Z,W), add_location(wumpus,A,B).
test_wumpus(X,Y,A,B) :- 
			not(locate(visited,X,Y)), adj(0, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B), locate(stench,A,B), locate(player,R,T), (R =\= A; T =\= B);
			not(locate(visited,X,Y)), adj(1, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B), locate(stench,A,B), locate(player,R,T), (R =\= A; T =\= B);
			not(locate(visited,X,Y)), adj(2, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B), locate(stench,A,B), locate(player,R,T), (R =\= A; T =\= B);
			not(locate(visited,X,Y)), adj(3, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B), locate(stench,A,B), locate(player,R,T), (R =\= A; T =\= B).

add_stench(X,Y,A,B) :- add_location(stench,X,Y);
			A is X+1, B is Y, not(locate(visited,A,B)), add_location(p_wumpus,A,B);
			A is X-1, B is Y, not(locate(visited,A,B)), add_location(p_wumpus,A,B);
			A is X, B is Y+1, not(locate(visited,A,B)), add_location(p_wumpus,A,B);
			A is X, B is Y-1, not(locate(visited,A,B)), add_location(p_wumpus,A,B);
			A is X+1, B is Y, test_wumpus(A,B,Z,W), add_location(wumpus,A,B);
			A is X-1, B is Y, test_wumpus(A,B,Z,W), add_location(wumpus,A,B);
			A is X, B is Y+1, test_wumpus(A,B,Z,W), add_location(wumpus,A,B);
			A is X, B is Y-1, test_wumpus(A,B,Z,W), add_location(wumpus,A,B).
				
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

danger(p_pit,3).
danger(pit,2).
danger(p_wumpus,1).
danger(wumpus, 0).


locateDanger(What,X,Y,D) :- locate(What,X,Y), danger(What,D).
%locateDanger(What,X,Y,D) :- locate(What,X,Y), danger(What,D), D >= 0, Z is D-1, locateDanger(What,X,Y,Z).

moveDir(X,Y,Gx,Gy,D) :- A is Gx-X, B is Gy-Y, dir(D,[A,B]).

%x(X) :- (X>0) -> true , (X<5) -> true.

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