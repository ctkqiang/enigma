package enigma;

import java.util.HashMap;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Jennifer Hu
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _numPawls = pawls;
        rotorsList = new Rotor[numRotors()];
        _allRotors = allRotors;

    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _numPawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        for (int i = 0; i < rotors.length; i++) {
            for (Rotor current: _allRotors) {
                if (rotors[i].toUpperCase()
                        .equals(current.name().toUpperCase())) {
                    rotorsList[i] = current;
                }
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 upper-case letters. The first letter refers to the
     *  leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        for (int i = 1; i < rotorsList.length; i += 1) {
            rotorsList[i].set(setting.charAt(i - 1));
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing

     *  the machine. */
    int convert(int c) {
        HashMap<String, Rotor> availrotors = new HashMap<String, Rotor>();
        for (Rotor current : _allRotors) {
            availrotors.put(current.name().toUpperCase(), current);
        }
        for (int i = 0; i < rotorsList.length; i += 1) {
            String name = rotorsList[i].name().toUpperCase();
            if (availrotors.containsKey(name)) {
                rotorsList[i] = availrotors.get(name);
            }
        }
        boolean[] convertible = new boolean[numRotors()];
        for (int i = rotorsList.length - 1; i > 1; i -= 1) {
            if (rotorsList[i].atNotch()) {
                if (rotorsList[i].rotates()) {
                    convertible[i] = true;
                }
                if (rotorsList[i - 1].rotates()) {
                    convertible[i - 1] = true;
                }
            }
        }
        convertible[numRotors() - 1] = true;

        for (int i = 0; i < convertible.length; i++) {
            if (convertible[i]) {
                rotorsList[i].advance();
            }
        }
        if (_plugboard != null) {
            int result = _plugboard.permute(c);
            for (int i = _numRotors - 1;  i >= 0; i--) {
                result = rotorsList[i].convertForward(result);
            }
            for (int i = 1; i < _numRotors; i++) {
                result = rotorsList[i].convertBackward(result);
            }
            result = _plugboard.permute(result);
            return result;
        } else {
            int result = c;
            for (int i = _numRotors - 1;  i >= 0; i--) {
                result = rotorsList[i].convertForward(result);
            }
            for (int i = 1; i < _numRotors; i++) {
                result = rotorsList[i].convertBackward(result);
            }
            return result;
        }

    }


    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        msg = msg.replaceAll(" ", "");
        msg = msg.toUpperCase();
        String result = "";
        for (int i = 0; i < msg.length(); i++) {
            char converted = _alphabet.toChar
                    (convert(_alphabet.toInt(msg.charAt(i))));
            result += converted;
        }
        return result;

    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number rotors. */
    private int _numRotors;

    /** Number of pawls. */
    private int _numPawls;

    /** Collection of rotors. */
    private Collection<Rotor> _allRotors;

    /** Array of rotors. */
    private Rotor[] rotorsList;

    /** Saves plugboard as permutation. */
    private Permutation _plugboard;

    /** /** Returns rotorsList. Allows for this to be
     * called in Main and thus able to be used.
     */

    Rotor[] getRotorsList() {
        return this.rotorsList;
    }




}
