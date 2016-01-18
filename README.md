# Open-Chord Battleships

## Open-Chord Battleship Implementation for HAW-Hamburg TTV Laboratory WS15/16

This is a University Project based on the Open-Chord Project, an open source implementation of the Chord distributed hash table (DHT)

* Open-Chord Website on Sourceforge: http://sourceforge.net/projects/open-chord/
* Open-Chord Manual: http://inet.haw-hamburg.de/teaching/ws-2014-15/technik-und-technologie-vernetzter-systeme/manual.pdf

This game is based on the original Battleship-Game where you shot at a coordinate and try to destroy all of the opponents ships on his battlefield.
The battlefield in this case is the Chord DHT. Each player spreads his part of the DHT into I intervalls and put S ships in it.

Specifically, the game process is as follows:
1. The player with the largest node key starts (owner of the key 2^160 -1). He sends a retrieve on a key of his choice.
2. The recipient of the retrieve checks whether a ship is placed at the same interval as the required key. If this is the case, the ship is destroyed, otherwise the "shot" went into the void.
3. The receiver notifies all participants (via broadcast) about the destruction attempt (routed key) and its output. Here, a ship can not be destroyed twice, that means any further shot on the same field is ineffective.
4. Thereafter the recipient of the last shot sends a retrieve on a key of his choice.
5. The player who correctly noted first that he has destroyed the last ship of another player has won.

German instructions: http://inet.haw-hamburg.de/teaching/ws-2015-16/technik-und-technologie-vernetzter-systeme/lab2

## Our Tactic

Our "tactic" is to shot at a random sector, which has not yet been shot at.
In the fact that a ship only covers one sector we can not develop a tactic to shot the enclosed sectors after a hit.
Besides we log every broadcast to know each number of ships of the other players and every sector that was shot so that we don't shoot it twice.