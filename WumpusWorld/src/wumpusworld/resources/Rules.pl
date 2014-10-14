%if sats
%breeze(X) -> true;false.

inside_map(X,Y) :- (X>0, Y>0), (X<5, Y<5).
locate(O,X,Y) :- at(O,X,Y).
add_location(O,X,Y) :- inside_map(X,Y), not(locate(visited,X,Y)), not(locate(O,X,Y)), assert(at(O,X,Y)).

locatearound(X,Y,O,A,B) :- adj(0, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B), locate(O,A,B);
			adj(1, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B), locate(O,A,B);
			adj(2, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B), locate(O,A,B);
			adj(3, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B), locate(O,A,B).
			
adjcent(X,Y,A,B,I) :- adj(I, [Z,W]), A is X+Z, B is Y+W, inside_map(A,B).

adj(0, [1,0]).	
adj(1, [0,1]).	
adj(2, [-1,0]).	
adj(3, [0,-1]).

add_stench(X,Y,A,B) :- add_location(stench,X,Y);
			A is X+1, add_location(p_wumpus,A,Y);
			A is X-1, add_location(p_wumpus,A,Y);
			B is Y+1, add_location(p_wumpus,X,B);
			B is Y-1, add_location(p_wumpus,X,B).
				
add_breeze(X,Y,A,B) :- add_location(breeze,X,Y); 
				A is X+1, add_location(p_pit,A,Y);
				A is X-1, add_location(p_pit,A,Y);
				B is Y+1, add_location(p_pit,X,B);
				B is Y-1, add_location(p_pit,X,B).
				
add_pit(X,Y,A,B) :- add_location(pit,X,Y); 
				A is X+1, add_location(breeze,A,Y);
				A is X-1, add_location(breeze,A,Y);
				B is Y+1, add_location(breeze,X,B);
				B is Y-1, add_location(breeze,X,B).
				
add_wumpus(X,Y) :- add_location(wumpus,X,Y); 
				A is X+1, add_location(stench,A,Y);
				A is X-1, add_location(stench,A,Y);
				B is Y+1, add_location(stench,X,B);
				B is Y-1, add_location(stench,X,B).
				
add_visited(X,Y,A,B) :- add_location(visited,X,Y);
			locate(pwumpus, X,Y), retract(at(pwumpus, X,Y));
			locate(ppit, X,Y), retract(at(ppit, X,Y)).
			
lookahead(X,Y,D,A,B) :- dir(D, [Z,W]), A is Z+X, B is W+Y, inside_map(A,B).

dir(0, [0,1]).
dir(1, [1,0]).
dir(2, [0,-1]).
dir(3, [-1,0]).

x(X) :- (X>0) -> true , (X<5) -> true.


%adjcent([X,Y]) :- inside_map(X,Y), (adjc([X,Y],[A,B]), locate(pit, X,Y)).

%adjc([X,Y],[A,B]) :- ((X+1) =:= A), Y =:= B.

%aRight([X,Y],[A,B]) :- ((X+1) =:= A, Y =:= B).

%adjX([X,Y],[A,B]) :- ((X+1) =:= A, Y =:= B); ((X-1) =:= A, Y=:=B).
%adjY([X,Y],[A,B]) :- (X =:= A, (Y+1) =:= B); (X=:=A, (Y-1) =:= B).

%adj([X,Y],[A,B]) :- (x(A) , x(B)) -> (adjX([X,Y],[A,B]) ; adjY([X,Y],[A,B])).

%locate_adj(O,[X,Y],[A,B]) :- (adj([X,Y],[A,B]), locate(O,A,B).