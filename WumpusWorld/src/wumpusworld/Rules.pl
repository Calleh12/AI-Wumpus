%if sats
%breeze(X) -> true;false.


adjacent([X,Y],[A,B]) :- or(((X+1) =:= A, Y =:= B), ((X-1) =:= A, Y =:= B)).
%, (X =:= A, Y+1 =:= B)).