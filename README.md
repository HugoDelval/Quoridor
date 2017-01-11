# Quoridor
One semester Trinity project in software engineering - Quoridor Game with AI

This project is an Android game, it allows the user to play Quoridor against another player (on the same phone) or against a basic AI based on Min-Max

## MinMax evaluation functions

I based my work on several papers :

* https://project.dke.maastrichtuniversity.nl/games/files/bsc/Mertens_BSc-paper.pdf
* http://www.cs.huji.ac.il/~ai/projects/2012/Quoridor/files/report.pdf
* http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.100.5204&rep=rep1&type=pdf

Some simple features (without costful graph traveling) extracted from the previous papers:

* position (are we close from the finish line?)
* beenThere (each square has a weight incresing each time the pawn pass by it) -> easy and powerfull
* number of walls (if you have more walls, you can trap your oponent)
* number of walls in front of you

## MinMax pruning

We use a basic and efficient pruning function: we only try to put barriers around the pawns and around other barriers on the board. This allow us to go to a depth of 3 (in approx. 1sec), several papers only manage to get to a depth of 2 (they didn't use this type of pruning). It is a huge approximation but it works relatively well (try it out!).
