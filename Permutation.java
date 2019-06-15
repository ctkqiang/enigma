package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Jennifer Hu
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        String temp = cycles.trim();
        temp = temp.replace("(", " ");
        temp = temp.replace(")", " ");
        _cycles = temp.split(" ");
    }





    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        String[] addedCycle = new String[_cycles.length + 1];
        for (int i = 0; i < _cycles.length; i++) {
            addedCycle[i] = _cycles[i];
        }
        addedCycle[_cycles.length + 1] = cycle;
        _cycles = addedCycle;

    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char n = _alphabet.toChar(wrap(p));
        char newChar = '0';
        for (int i = 0; i < _cycles.length; i++) {
            for (int j = 0; j < _cycles[i].length(); j++) {
                if (_cycles[i].charAt(j) == n) {
                    if (j != _cycles[i].length() - 1) {
                        newChar = _cycles[i].charAt(j + 1);
                    } else {
                        newChar = _cycles[i].charAt(0);
                    }
                    return _alphabet.toInt(newChar);
                }
            }
        }
        return p;
    }


    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char n = _alphabet.toChar(wrap(c));
        char newChar = '0';
        for (int i = 0; i < _cycles.length; i++) {
            for (int j = 0; j < _cycles[i].length(); j++) {
                if (_cycles[i].charAt(j) == n) {
                    if (j != 0) {
                        newChar = _cycles[i].charAt(j - 1);
                    } else {
                        newChar = _cycles[i].charAt(_cycles[i].length() - 1);
                    }
                    return _alphabet.toInt(newChar);
                }
            }
        }
        return c;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int x = _alphabet.toInt(p);
        return _alphabet.toChar(permute(x));
    }

    /** Return the result of applying the inverse of this permutation to C. */
    int invert(char c) {
        int index = _alphabet.toInt(c);
        return _alphabet.toChar(invert(index));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        int charcount = 0;
        boolean derang = false;
        for (int i = 0; i < _cycles.length; i++) {
            charcount += _cycles[i].length();
        }
        if (charcount == _alphabet.size()) {
            derang = true;
        }
        return derang;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** String array of cycles. */
    private String [] _cycles;
}
