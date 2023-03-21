package lib;
// Written by Nathaniel Levison, March 2023
// License for this file at /LICENSE.txt

// Import Dependencies
import java.awt.Color;
import java.util.Random;
public class Netscape {
    public static Color col(String s) {
        int seed = 0; for(char c : s.toCharArray()) seed += c;
        Random r = new Random(new Random(seed).nextInt(0,99999999));
        return new Color(r.nextInt(0, 255),r.nextInt(0, 255),r.nextInt(0, 255));
    }
    /*
    public static Color strToCol(String s) {
        // Replace all nonvalid hexadecimal characters with 0’s
        s = s.replaceAll("[^0-9a-fA-F]", "0");
        // Pad out to the next total number of characters divisible by 3
        int len = s.length();
        int padLen = len + (3 - len % 3) % 3;
        s = String.format("%-" + padLen + "s", s).replaceAll(" ", "0");
        // Split into three equal groups, with each component representing the corresponding colour component of an RGB colour
        String r = s.substring(0, padLen / 3);
        String g = s.substring(padLen / 3, 2 * padLen / 3);
        String b = s.substring(2 * padLen / 3);
        // Truncate each of the arguments from the right down to two characters.
        r = r.substring(Math.max(0, r.length() - 2));
        g = g.substring(Math.max(0, g.length() - 2));
        b = b.substring(Math.max(0, b.length() - 2));
        // Convert to Color object
        int red = Integer.parseInt(r, 16);
        int green = Integer.parseInt(g, 16);
        int blue = Integer.parseInt(b, 16);
        return new Color(red, green, blue);
    }
    */
}
