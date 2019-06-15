package enigma;

import static enigma.EnigmaException.*;

/** An Alphabet consisting of the Unicode characters in a certain range in
 *  order.
 *  @author P. N. Hilfinger
 */
class CharacterRange extends Alphabet {

    /** An alphabet consisting of all characters between FIRST and LAST,
     *  inclusive. */
    CharacterRange(char first, char last) {
        _first = Character.toUpperCase(first);
        _last = Character.toUpperCase(last);
        if (_first > _last) {
            throw error("empty range of characters");
        }
    }

    @Override
    int size() {
        return _last - _first + 1;
    }

    @Override
    boolean contains(char ch) {
        return ch >= _first && ch <= _last;
    }

    @Override
    char toChar(int index) {
        if (!contains((char) (_first + index))) {
            throw error("character index out of range");
        }
        return (char) (_first + index);
    }

    @Override
    int toInt(char ch) {
        if (!contains(ch)) {
            throw error("character out of range");
        }
        return ch - _first;
    }

    /** Range of characters in this Alphabet. */
    private char _first, _last;

}
