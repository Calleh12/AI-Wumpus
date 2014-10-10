%if sats
%breeze(X) -> true;false.

%-----------------------
% Rules that add facts -
%-----------------------



locate(A, X,Y) :- at(A, X,Y).

at(player, 1,1).
at(wumpus, 1,2).



home(O, X, Y) :- wumpusAt(X,Y).
wumpusAt(1,1).

%adj([X,Y],[A,B]) :- x(A) -> ((X+1) =:= A; (X-1) =:= A); (Y+1) =:= B; (Y-1) =:= B.



%----------------------

%----------------------

x(X) :- (X>0) -> true , (X<5) -> true.

adjX([X,Y],[A,B]) :- ((X+1) =:= A, Y =:= B); ((X-1) =:= A, Y=:=B).
adjY([X,Y],[A,B]) :- (X =:= A, (Y+1) =:= B); (X=:=A, (Y-1) =:= B).

adj([X,Y],[A,B]) :- (x(A) , x(B)) -> (adjX([X,Y],[A,B]) ; adjY([X,Y],[A,B])).

breezy([X,Y],[A,B]) :- adj([X,Y],[A,B]), pit(A,B), assert(pit(A,B)).

