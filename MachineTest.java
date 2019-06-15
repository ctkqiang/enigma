//package enigma;
//
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.HashSet;
//
//import static junit.framework.TestCase.assertEquals;
package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import java.util.*;

import static enigma.TestUtils.*;


public class MachineTest {
    Alphabet defaultAlphabet = new CharacterRange('A', 'Z');
    Permutation[] perms = new Permutation[12];
    String[] cyc = new String[]{"(AELTPHQXRU) "
            + "(BKNW) (CMOY) (DFG) (IV) (JZ) (S)",
        "(FIXVYOMW) (CDKLHUP) (ESZ) (BJ) (GR) (NT) (A) (Q)",
        "(ABDHPEJT) (CFLVMZOYQIRWUKXSG) (N)",
        "(AEPLIYWCOXMRFZBSTGJQNH) (DV) (KU)",
        "(AVOLDRWFIUQ)(BZKSMNHYC) (EGTJPX)",
        "(AJQDVLEOZWIYTS) (CGMNHFUX) (BPRK)",
        "(ANOUPFRIMBZTLWKSVEGCJYDHXQ)",
        "(AFLSETWUNDHOZVICQ) (BKJ) (GXY) (MPR)",
        "(ALBEVFCYODJWUGNMQTZSKPR) (HIX)",
        "(AFNIRLBSQWVXGUZDKMTPCOYJHE)",
        "(AE) (BN) (CK) (DQ) (FU) (GY) (HW) "
                + "(IJ) (LO) (MP) (RX) (SZ) (TV)",
        "(AR) (BD) (CO) (EJ) (FN) (GT) "
                + "(HK) (IV) (LM) (PW) (QZ) (SX) (UY)"};
    String[] rotornames = new String[]{"I", "II", "III", "IV",
        "V", "VI", "VII", "VIII", "BETA", "GAMMA", "B", "C"};
    Rotor[] allRotors = new Rotor[12];
    String[] notches = new String[]{"Q", "E", "V",
        "J", "Z", "ZM", "ZM", "ZM"};

    @Test
    public void machineTest() {
        for (int i = 0; i < 12; i++) {
            perms[i] = new Permutation(cyc[i], defaultAlphabet);
        }
        for (int i = 0; i < 12; i++) {
            if (i < 8) {
                allRotors[i] = new MovingRotor
                (rotornames[i], perms[i], notches[i]);
            } else if (i < 10) {
                allRotors[i] = new FixedRotor
                (rotornames[i], perms[i]);
            } else {
                allRotors[i] = new Reflector(rotornames[i], perms[i]);
            }
        }
        Collection<Rotor> callRotors = new HashSet<>(Arrays.asList(allRotors));
        Machine machine1 = new Machine(defaultAlphabet, 5, 3, callRotors);
        Machine machine2 = new Machine(defaultAlphabet, 5, 3, callRotors);
        String[] machine1Rotors = new String[]{"B", "Beta", "III", "IV", "I"};
        String[] machine2Rotors = new String[]{"B", "Beta", "I", "II", "III"};
        machine1.insertRotors(machine1Rotors);
        machine1.setRotors("AXLE");
        machine1.setPlugboard(new Permutation("(HQ) (EX) "
                + "(IP) (TR) (BY)", defaultAlphabet));
        assertEquals(16, machine1.convert(5));
        assertEquals(21, machine1.convert(17));
        assertEquals(15, machine1.convert(14));
        assertEquals(16, machine1.convert(12));
        assertEquals("SOK", machine1.convert("his"));
        assertEquals("OILPUBKJZPISFXDW", machine1.convert("shoulder Hiawatha"));
        assertEquals("BHCNSCXNUOAATZXSRCFYDGU",
                machine1.convert("Took the camera of rosewood"));
        assertEquals("FLPNXGXIXTYJUJRCAUGEUNCFMKUF",
                machine1.convert("Made of sliding folding rosewood"));

    }


    @Test
    public void testDoubleStep() {
        Alphabet ac = new CharacterRange('A', 'D');
        Rotor one = new Reflector("R1", new Permutation("(AC) (BD)", ac));
        Rotor two = new MovingRotor("R2", new Permutation("(ABCD)", ac), "C");
        Rotor three = new MovingRotor("R3", new Permutation("(ABCD)", ac), "C");
        Rotor four = new MovingRotor("R4", new Permutation("(ABCD)", ac), "C");
        String setting = "AAA";
        Rotor[] machineRotors = {one, two, three, four};
        String[] rotors = {"R1", "R2", "R3", "R4"};
        Machine mach = new Machine(ac, 4, 3,
                new ArrayList<>(Arrays.asList(machineRotors)));
        mach.insertRotors(rotors);
        mach.setRotors(setting);

        assertEquals("AAAA", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("AAAB", getSetting(ac, machineRotors));
    }

    /** Helper method to get the String representation
     * of the current Rotor settings */
    private String getSetting(Alphabet alph, Rotor[] machineRotors) {
        String currSetting = "";
        for (Rotor r : machineRotors) {
            currSetting += alph.toChar(r.setting());
        }
        return currSetting;
    }

    @Test
    public void machine2Test() {
        for (int i = 0; i < 12; i++) {
            perms[i] = new Permutation(cyc[i], defaultAlphabet);
        }
        for (int i = 0; i < 12; i++) {
            if (i < 8) {
                allRotors[i] = new MovingRotor(rotornames[i], perms[i],
                        notches[i]);
            } else if (i < 10) {
                allRotors[i] = new FixedRotor(rotornames[i], perms[i]);
            } else {
                allRotors[i] = new Reflector(rotornames[i], perms[i]);
            }
        }
        Collection<Rotor> callRotors = new HashSet<>(Arrays.asList(allRotors));
        Machine machine1 = new Machine(defaultAlphabet, 5, 3, callRotors);
        Machine machine2 = new Machine(defaultAlphabet, 5, 3, callRotors);
        String[] machine2Rotors = new String[]{"B", "Beta", "I", "II", "III"};
        machine2.insertRotors(machine2Rotors);
        machine2.setRotors("AAAA");
        machine2.setPlugboard(new Permutation("(AQ) (EP)", defaultAlphabet));
        assertEquals("IHBDQQMTQZ", machine2.convert("Helloworld"));
    }


}
