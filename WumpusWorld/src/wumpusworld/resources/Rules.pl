%if sats
%breeze(X) -> true;false.

inside_map(X,Y) :- (X>0, Y>0), (X<5, Y<5).
locate(O,X,Y) :- inside_map(X,Y), at(O,X,Y).
add_location(O,X,Y) :- inside_map(X,Y), not(locate(O,X,Y)), assert(at(O,X,Y)).

x(X) :- (X>0) -> true , (X<5) -> true.


adjcent([X,Y]) :- inside_map(X,Y), (adjc([X,Y],[A,B]), locate(pit, X,Y)).

adjc([X,Y],[A,B]) :- ((X+1) =:= A), Y =:= B.

aRight([X,Y],[A,B]) :- ((X+1) =:= A, Y =:= B).

adjX([X,Y],[A,B]) :- ((X+1) =:= A, Y =:= B); ((X-1) =:= A, Y=:=B).
adjY([X,Y],[A,B]) :- (X =:= A, (Y+1) =:= B); (X=:=A, (Y-1) =:= B).

adj([X,Y],[A,B]) :- (x(A) , x(B)) -> (adjX([X,Y],[A,B]) ; adjY([X,Y],[A,B])).

%locate_adj(O,[X,Y],[A,B]) :- (adj([X,Y],[A,B]), locate(O,A,B).