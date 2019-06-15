package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.error;

/**
 * Enigma simulator.
 *
 * @author Jennifer Hu
 */
public final class Main {

    /**
     * Process a sequence of encryptions and decryptions, as
     * specified by ARGS, where 1 <= ARGS.length <= 3.
     * ARGS[0] is the name of a configuration file.
     * ARGS[1] is optional; when present, it names an input file
     * containing messages.  Otherwise, input comes from the standard
     * input.  ARGS[2] is optional; when present, it names an output
     * file for processed messages.  Otherwise, output goes to the
     * standard output. Exits normally if there are no errors in the input;
     * otherwise with code 1.
     */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /**
     * Check ARGS and open the necessary files (see comment on main).
     */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /**
     * Return a Scanner reading from the file named NAMEX.
     * https://docs.oracle.com/javase/10/docs/api/java/util/Scanner.html
     */
    private Scanner getInput(String namex) {
        try {
            return new Scanner(new File(namex));
        } catch (IOException excp) {
            throw error("could not open %s", namex);
        }
    }

    /**
     * Return a PrintStream writing to the file named NAME1.
     */
    private PrintStream getOutput(String name1) {
        try {
            return new PrintStream(new File(name1));
        } catch (IOException excp) {
            throw error("could not open %s", name1);
        }
    }

    /**
     * Configure an Enigma machine from the contents of configuration
     * file _config and apply it to the messages in _input, sending the
     * results to _output.
     */
    private void process() {
        enigma = readConfig();
        String setting = _input.nextLine();
        if (setting.charAt(0) != '*') {
            throw error("Wrong setting format");
        }
        if (!setting.contains("*")) {
            throw new EnigmaException("Wrong setting format");
        } else {
            setUp(enigma, setting);
        }
        while (_input.hasNextLine()) {
            setting = _input.nextLine();
            if (setting.isEmpty()) {
                _output.println();
            } else if ((setting.contains("*"))) {
                setUp(enigma, setting);
            } else {
                printMessageLine(enigma.convert(setting));
            }
        }

    }

    /**
     * Return an Enigma machine configured from the contents of configuration
     * file _config.
     * "I" indicates its name
     * "M" indicates it's type, in this case, moving
     * "Q" indicates its notch position, in this case, at Q only.
     * (AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S) indicates it's cycles
     * USE READ ROTOR AS HELPER FUNCTION
     */
    private Machine readConfig() {
        try {
            String letters = _config.nextLine();
            _alphabet = new CharacterRange(letters.charAt(0),
                    letters.charAt(letters.length() - 1));
            if (letters.contains("(")) {
                throw new EnigmaException("Wrong configuration format for (");
            }
            if (letters.contains(")")) {
                throw new EnigmaException("Wrong configuration format for ) ");
            }
            if (letters.contains("*")) {
                throw new EnigmaException("Wrong configuration format for * ");
            }
            if (!_config.hasNextInt()) {
                throw new EnigmaException("Wrong configuration format");
            }
            if (!_config.hasNextInt()) {
                throw new EnigmaException("Wrong configuration format");
            }
            int rotators = _config.nextInt();
            int pawlnumber = _config.nextInt();
            if (pawlnumber > rotators) {
                throw error("Number of pawls "
                        + "cannot be greater than amount of rotors");
            }
            while (_config.hasNextLine()) {
                Rotor addedRotor = readRotor();
                allrotors.add(addedRotor);
            }
            return new Machine(_alphabet, rotators, pawlnumber, allrotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }

    }


    /**
     * Return a rotor, reading its description from _config.
     */
    private Rotor readRotor() {
        try {
            String currname = _config.next();
            if (currname.contains("(") || currname.contains(")")) {
                throw error("Not an acceptable name");
            }
            String typeNotch = _config.next();
            String cycle = "";
            while (_config.hasNext("([(][A-Z]+[)])+")) {
                cycle += _config.next();
            }
            Permutation perm = new Permutation(cycle, _alphabet);
            char canmove = typeNotch.charAt(0);
            if (_config.hasNextLine()) {
                _config.nextLine();
            }
            if (canmove == 'M' && typeNotch.length() == 1) {
                throw error("No notches given");
            }
            if (canmove == 'M') {
                return new MovingRotor(currname, perm, typeNotch.substring(1));
            } else if (canmove == 'R') {
                return new Reflector(currname, perm);
            } else {
                return new FixedRotor(currname, perm);
            }

        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }

    }

    /**
     * Set M according to the specification given on SETTINGS,
     * which must have the format specified in the assignment.
     */
    private void setUp(Machine M, String settings) {
        String[] rotorarray = new String[M.numRotors()];
        Scanner mach = new Scanner(settings);
        String temp = mach.next();
        if (temp.equals('*')) {
            mach.next();
        }
        for (int i = 0; i < M.numRotors(); i++) {
            String curr = mach.next();
            rotorarray[i] = curr;
        }
        M.insertRotors(rotorarray);
        M.setRotors(mach.next());
        String perm = "";
        for (int y = 0; y < M.numRotors(); y++) {
            if (mach.hasNext("([(][A-Z]+[)])+")) {
                perm += mach.next();
            }
        }

        for (int i = 0; i < rotorarray.length - 1; i++) {
            for (int j = i + 1; j < rotorarray.length; j++) {
                if (rotorarray[i].equals(rotorarray[j])) {
                    throw error("Repeated Rotor");
                }
            }
        }
        M.setPlugboard(new Permutation(perm, _alphabet));
        if (!M.getRotorsList()[0].reflecting()) {
            throw error("First Rotor should be a reflector");
        }
    }

    /**
     * Print MSG in groups of five (except that the last group may
     * have fewer letters).
     */
    private void printMessageLine(String msg) {
        for (int i = 0; i < msg.length(); i += 1) {
            if (i % 6 == 0) {
                msg = msg.substring(0, i) + " "
                        + msg.substring(i, msg.length());
            }
        }
        outmessage = msg.trim();
        _output.println(outmessage);
    }

    /**
     * Alphabet used in this machine.
     */
    private Alphabet _alphabet;

    /**
     * Rotor name.
     */
    private String name;

    /**
     * Source of input messages.
     */
    private Scanner _input;

    /**
     * Source of machine configuration.
     */
    private Scanner _config;

    /**
     * File for encoded/decoded messages.
     */
    private PrintStream _output;


    /**
     * What we are sending out.
     */
    private String outmessage;

    /**
     * Contains appropriate cycles after readRotor is called.
     */
    private String updated;

    /**
     * Holds current temp value of config.
     */
    private String pos;

    /**
     * Type and notches of current rotor.
     */
    private String notches;

    /**
     * Makes an array list of all of the Rotors..
     */
    private ArrayList<Rotor> allrotors = new ArrayList<>();

    /**
     * Initalizes machine.
     */
    private Machine enigma;
}
