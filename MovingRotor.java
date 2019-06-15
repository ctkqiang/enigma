package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Jennifer Hu
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
        _permutation = perm;
    }


    /**need to override method bc original rotates does
     not take into account the position of neighboring nothces.*/
    @Override
    boolean rotates() {
        return true;
    }



    /** checks if currently at notch.*/
    @Override
    boolean atNotch() {
        for (int i = 0; i < _notches.length(); i++) {
            if (alphabet().toInt(_notches.charAt(i)) == this.setting()) {
                return true;
            }
        }
        return false;
    }


    /** moves the setting up once.*/
    @Override
    void advance() {
        super.set(_permutation.wrap(super.setting() + 1));


    }

    /** Saves notches.*/
    private String _notches;

    /** Saves permutation.*/
    private Permutation _permutation;


}
