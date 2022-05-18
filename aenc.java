import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

// TO DO

// write executable script

public class aenc {

    // This function calculates and returns the bit of n at position k
    public static int kthBit(long n, long k) { 
        if ((n & (1 << (k - 1))) > 0) {
            return(1);
        } else {
            return(0);
        }
    }

    // This function orders the specificaiton using a bubble sort process; characters are ordered in terms of their ASCII values.
    public static String[] orderSpec(String[] specification) {

        // int array to store probabilities from specification
        int[] probs = new int[specification.length / 2];
        // string array to store characters from specification
        String[] chars = new String[specification.length / 2];

        // counter for index
        int indexCounter = 0;

        // loop fills up the probs array
        for (int x = 1; x < specification.length; x = x + 2) {
            probs[indexCounter] = Integer.parseInt(specification[x]);
            indexCounter = indexCounter + 1;
        }

        // counter for another index
        int indexCounterTwo = 0;

        // loop fills up the chars array
        for (int y = 0; y < specification.length; y = y + 2) {
            chars[indexCounterTwo] = specification[y];
            indexCounterTwo = indexCounterTwo + 1;
        }

        // two temporary variables used in the bubble sort loop below
        int temp = 0;
        String tempTwo = "";

        // bubble sort to shuffle around chars and probs equally
        for (int g = 0; g < Math.pow(probs.length, 2); g++) {
            for (int i = 1; i < probs.length; i++) {
                char charOne = chars[i - 1].charAt(0);
                char charTwo = chars[i].charAt(0);
                int ascii = (int) charOne;
                int ascii2 = (int) charTwo;
                if (ascii > ascii2) {
                    temp = probs[i];
                    tempTwo = chars[i];
                    probs[i] = probs[i - 1];
                    chars[i] = chars[i - 1];
                    probs[i - 1] = temp;
                    chars[i - 1] = tempTwo;
                }
            }
        }

        // new arrays for probabilities and characters with an extra space for the end of message symbol
        int[] newProbs = new int[probs.length + 1];
        String[] newChars = new String[chars.length + 1];

        // loop copies probs and chars into newProbs and newChars (Could've just used arraycopy but whatever)
        for (int q = 0; q < probs.length; q++) {
            newProbs[q] = probs[q];
            newChars[q] = chars[q];
            //System.out.println(chars[q] + ": " + Integer.toString(probs[q]));
        }

        // adds the end of message symbol
        newProbs[newProbs.length - 1] = 1;
        newChars[newChars.length - 1] = "§";

        // returns new chars (renders probabilities array useless but thats just how the development journey went)
        return(newChars);
 
    }

    // this function contains the logic of the arithmetic coding algorithm
    public static String arithmetic(Hashtable<String, Integer> probabilities, String[] msg, String[] chars) throws DidntWorkException {

        // empty string which will hold a binary representation of the tag to be output
        String tag = "";
        // end of message symbol
        String eom = "§";
        // s is rescale counter
        int s = 0;
        // l is the lower bound of the scale
        long l = 0;
        // u is the upper bound of the scale
        long u = Long.parseLong("4294967295");
        // this is will just keep the starting l value
        long initialL = l;
        // similiarly, this will keep the starting u value
        long intiialU = u;
        // stores the scale range
        long range = u - l + 1;
        // stores the initial range
        long initialRange = u - l + 1;
        // stores half the original range for rescaling operations
        long half = Long.parseLong("4294967296") / 2;
        //stores a quarter of the original range for rescaling operations
        long quarter = Long.parseLong("4294967296") / 4;

        // scale is an array of Interval objects (a class I wrote to store scale intervals)
        Interval[] scale = new Interval[chars.length];

        // array will store the lower bound for each character's cumulative probability
        int[] lBounds = new int[chars.length];
        // index initialised for later use
        int charIndex = -1;

        // this block builds up a scale of cumulative probabilities
        lBounds[0] = 0;
        Interval temp = new Interval(chars[0], lBounds[0], lBounds[0] + probabilities.get(chars[0]));
        scale[0] = temp;
        for (int oo = 1; oo < lBounds.length; oo++) {
            lBounds[oo] = lBounds[oo - 1] + probabilities.get(chars[oo - 1]);
            temp = new Interval(chars[oo], lBounds[oo], lBounds[oo] + probabilities.get(chars[oo]));
            scale[oo] = temp;
        }

        // I printed out a bunch of variables to see how things were being processed
        /*
        System.out.println("----------------------------------------------------------");
        System.out.println("Scale:");

        for (int j = 0; j < scale.length; j++) {
            System.out.println("Char: " + scale[j].character);
            System.out.println("Lower Cumulative Probability: " + scale[j].lower);
            System.out.println("Upper Cumulative Probability: " + scale[j].upper);    
        }

        System.out.println("----------------------------------------------------------");
        System.out.println("Chars:");

        for (int j = 0; j < chars.length; j++) {
            System.out.println("Char: " + chars[j]);
        }
        */

        // loop for each character in the message to be encoded
        for (int a = 0; a < msg.length; a++) {

            //System.out.println("----------------------------------------------------------");
            //System.out.println("Message Character: " + msg[a]);

            // loop finds the index of the message character in the characters array for later
            for (int b = 0; b < chars.length; b++) {
                if (Objects.equals(msg[a], chars[b])) {
                    charIndex = b;
                }
            }

            // prints the character and its respective probability
            //System.out.println("P(" + chars[charIndex] + "): " + probabilities.get(chars[charIndex]));

            // prints the character and its respective upper and lower cumulative probabilities
            //System.out.println("CP(" + chars[charIndex] + ").upper = " + scale[charIndex].upper);
            //System.out.println("CP(" + chars[charIndex] + ").lower = " + scale[charIndex].lower);

            // updates u and l in accordance with the integer arithmetic coding algorithm
            u = l + (long) Math.floor((double) ((scale[charIndex].upper * range) / 1000000)) - 1;
            l = l + (long) Math.floor((double) (scale[charIndex].lower * range) / 1000000);
            range = u - l + 1;

            // prints out the two new u and l values
            //System.out.println("Upper Bound: " + u);
            //System.out.println("Lower Bound: " + l);
            //System.out.println("Range: " + range);

            // implements the DO RESCALE function as described in the lecture notes except with integer scaling operations
            while(true) {
                // check if end of message character is being expanded and, if so, stop the rescale
                if (msg[a] == eom) {
                    break;
                }

                // prints the l and u values each iteration of the while loop
                //System.out.println("New U: " + u);
                //System.out.println("New L: " + l);

                // check if most significant bit of l and u is the same using the kthBit function shown above
                if (kthBit(l, 32) == kthBit(u, 32)) { // IF SAME MSB
                    // check if most significant bit of l is a 1 (it follows that the MSB of u is also 1 given the first check passed)
                    if (kthBit(l, 32) == 1) { // IF MSB IS ONE
                        //System.out.println("Mutual MSB is 1");
                        // appends the MSB to the binary tag
                        tag = tag + "1";
                        // check if extra bits needs to be sent (rescale counter)
                        if (s != 0) {
                            // appends the complement of the MSB to the tag s times
                            for (int ss = 0; ss < s; ss++) {
                                tag = tag + "0";
                            }
                        }
                        // resets rescale counter
                        s = 0;
                        // bitiwse rescaling that didnt work
                        //l = l << 1;
                        //u = u << 1;
                        //u = u | 1;
                        // integer rescaling operations for when the lower and upper bounds are both greater than half
                        l = 2 * (l - half);
                        u = 2 * (u - half);

                        // THROW AWAY ALL BITS ABOVE 32
                        while (Long.highestOneBit(u) > Long.parseLong("2147483648")) {
                            long highestOne = Long.highestOneBit(u);
                            //System.out.println("New U Highest Bit: " + highestOne);
                            u ^= highestOne;
                        }
                        while (Long.highestOneBit(l) > Long.parseLong("2147483648")) {
                            long highestTwo = Long.highestOneBit(l);
                            //System.out.println("New L Highest Bit: " + highestTwo);
                            l ^= highestTwo;
                        }
                        
                    } else { // MSB must be 0 since it was not 1
                        //System.out.println("Mutual MSB is 0");
                        // appends MSB to the binary tag
                        tag = tag + "0";
                        // check if extra bits need to be sent
                        if (s != 0) {
                            // appends the complement of the MSB to the tag s times
                            for (int sr = 0; sr < s; sr++) {
                                tag = tag + "1";
                            }
                        }
                        // resets the rescale counter
                        s = 0;
                        // bitwise operations that didnt work
                        //l = l << 1;
                        //u = u << 1;
                        //u = u | 1;
                        // integer rescaling operations for when the lower and upper bounds are both less than half
                        l = l * 2;
                        u = u * 2;

                        // THROW AWAY ALL BITS ABOVE 32
                        while (Long.highestOneBit(u) > Long.parseLong("2147483648")) {
                            long highestOne = Long.highestOneBit(u);
                            //System.out.println("New U Highest Bit: " + highestOne);
                            u ^= highestOne;
                        }
                        while (Long.highestOneBit(l) > Long.parseLong("2147483648")) {
                            long highestTwo = Long.highestOneBit(l);
                            //System.out.println("New L Highest Bit: " + highestTwo);
                            l ^= highestTwo;
                        }
                        
                    }

                    // prints out the current tag
                    //System.out.println("RESCALE A");
                    //System.out.println("Tag: " + tag);

                } else if ((kthBit(u, 32) != kthBit(l, 32)) && (kthBit(l, 31) == 1) && (kthBit(u, 31) != 1)) { // checks if MSB of lower and upper are unique plus 2MSB of lower is one and 2MSB of upper is zero (lower and upper are both between 25% and 75% of the scale) also known as rescale B
                    // bitwise operations that didnt work
                    //l = l - 1073741824; // 2 ^ 30 = 1073741824
                    //u = u + 1073741824; // 2 ^ 30 = 1073741824
                    //l = l << 1;
                    //u = u << 1;
                    //u = u | 1;
                    //l = 2 * (l - quarter);
                    //u = 2 * (u - quarter);
                    // integer rescaling operations for when l and u are between 25% and 75% of the scale
                    l = 2 * (l - quarter);
                    u = 2 * (u - quarter);
                    // increment rescale counter
                    s = s + 1;

                    // THROW AWAY ALL BITS ABOVE 32
                    while (Long.highestOneBit(u) > Long.parseLong("2147483648")) {
                        long highestOne = Long.highestOneBit(u);
                        u ^= highestOne;
                    }
                    while (Long.highestOneBit(l) > Long.parseLong("2147483648")) {
                        long highestTwo = Long.highestOneBit(l);
                        l ^= highestTwo;
                    }
                    
                    //System.out.println("RESCALE B");
                } else {
                    break;
                }
            }
            
            
        }

        // prints binary tag
        //System.out.println("Binary Tag: " + tag);
        // prints lower bound after all rescaling
        //System.out.println("Final L: " + Long.toBinaryString(l));
        
        // sends remaining bits needed from rescale B
        if (kthBit(l, 32) == 1) {
            // sends the MSB to the binary tag
            tag = tag + "1";
            // checks that there are remaining bits to be sent
            if (s != 0) {
                // sends the remaining s bits
                for (int st = 0; st < s; st++) {
                    tag = tag + "0";
                }
            }
        } else {
            // sends the MSB to the binary tag
            tag = tag + "0";
            // checks that there are remaining bits to be sent
            if (s != 0) {
                // sends the remaining s bits
                for (int su = 0; su < s; su++) {
                    tag = tag + "1";
                }
            }
        }

        // prints the binary tag after the extra bits from rescaling are accounted for
        //System.out.println("Tag after rescale accounted for: " + tag);

        // stores l as a binary string
        String bMinusOneBitsOfL = Long.toBinaryString(l);
        // adds 0 in front of binary string until at least 32 bits
        while (bMinusOneBitsOfL.length() < 32) {
            bMinusOneBitsOfL = "0" + bMinusOneBitsOfL;
        }

        // essentially cuts the Bth bit off the top of the binary string
        bMinusOneBitsOfL = bMinusOneBitsOfL.substring(1);
        
        // prints the last B-1 bits of l
        //System.out.println("Last B - 1 Bits of L: " + bMinusOneBitsOfL);

        // appends the last B-1 bits of l onto the binary tag
        tag = tag + bMinusOneBitsOfL;

        // while the tag length is less than 32 bits, add 0s in front of the tag
        while (tag.length() < 32) {
            tag = "0" + tag;
        }

        // while the tag length isn't divisible by 4, add 0s in front of the tag
        while ((tag.length() % 4) != 0) {
            tag = "0" + tag;
        }

        // prints the final tag
        //System.out.println("Final Tag: " + tag);

        // tagNibbles is an array of each 4 bits of the tag
        String[] tagNibbles = tag.split("(?<=\\G.{4})");
        // hex tag is initialised and will store the tag in hexadecimal form
        String hexTag = "";
        
        // loops through each element in tagNibbles
        for (int nib = 0; nib < tagNibbles.length; nib++) {
            // prints the current 4 bits
            //System.out.println("Tag Nibble: " + tagNibbles[nib]);
            // reverses the 4 bits
            String reversed = new StringBuilder(tagNibbles[nib]).reverse().toString();
            // prints the reverse of the 4 bits
            //System.out.println("Reversed Tag Nibble: " + reversed);
            // resets the tagNibbles element as its reverse
            tagNibbles[nib] = reversed;
            // stores the decimal form of the reversed 4 bits
            int dec = Integer.parseInt(tagNibbles[nib], 2);
            // stores the hexadecimal form of the decimal form of the reversed 4 bits
            String hexStr = Integer.toString(dec, 16);
            // appends the hexadecimal nibble to the hex tag
            hexTag = hexTag + hexStr;
        }

        // prints the final hex tag
        System.out.println("Final Hex Tag: " + hexTag);
        // returns the hex tag
        return(hexTag);

    }

    // this function splits the input into nicely sorted arrays to be used in the arithmetic function
    public static String encode(String[] _strArr) throws DidntWorkException {

        // endReached is a boolean flag to determine whether the for loop has reached the end symbol yet
        // specSize is initialised and later assigned to the size of the specification array
        boolean endReached = false;
        int specSize = 0;

        // this loop finds the array index of the end symbol
        for (int i = 0; i < _strArr.length; i++) {
            if (_strArr[i].equals("end")) {
                endReached = true;
                specSize = i;
            }
        }

        // int to store number of tokens after the end symbol
        int numOfTokens = 0;

        // loop counts how many tokens come after the end symbol
        for (int z = specSize + 1; z < _strArr.length; z++) {
            String[] tempStrArr = _strArr[z].split("(?!^)");
            for (int y = 0; y < tempStrArr.length; y++) {
                numOfTokens = numOfTokens + 1;
            }
        }

        // spec is string array of each token before the end token
        // chars is a string array of the input alphabet symbols
        // tokens is a string array of the tokens that come after the end symbol
        String[] spec = new String[specSize];
        String[] chars = new String[(specSize / 2) + 1];
        String[] tokens = new String[numOfTokens + 1];

        
        // prints spec
        //System.out.println("----Spec----");

        // also sets spec
        for (int j = 0; j < specSize; j++) {
            spec[j] = _strArr[j];
            //System.out.println(spec[j]);
        }

        // this will count up from zero so that the tokens array is filled up properly
        int alsoIndexCounter = 0;

        // fills up tokens array correctly with all tokens proceeding the end symbol
        for (int k = specSize + 1; k < _strArr.length; k++) {
            String[] tempArr = _strArr[k].split("(?!^)");
            for (int s = 0; s < tempArr.length; s++) {
                tokens[alsoIndexCounter] = tempArr[s];
                alsoIndexCounter = alsoIndexCounter + 1;
            }
        }
        tokens[tokens.length - 1] = "§";

        // hashtable dict is a key-value lookup table mapping chars to their respective probabilities
        Hashtable<String, Integer> dict = new Hashtable<String, Integer>();
        // token is a temp variable to hold the keys that will be added to the dictionary
        String token = null;
        int p = 0;
        // p is a temp variable to hold the probabilities that will be added to the dictionary
        int[] ps = new int[chars.length];

        // this will count up from zero so that the chars array is filled up properly
        int indexCounter = 0;

        int indexCounterToo = 0;

        // this loop fills the chars array with the chars and fills the dictionary with char:probability pairs
        for (int l = 0; l < specSize; l++) {
            if ((l % 2) == 0) {
                token = spec[l];
                chars[indexCounter] = token;
                indexCounter = indexCounter + 1;
            } else {
                p = Integer.parseInt(spec[l]);
                ps[indexCounterToo] = p;
                dict.put(token, p);
                indexCounterToo = indexCounterToo + 1;
            }
        }

        // adds the end symbol to the dictionary of probabilities
        dict.put("§", 1);

        // sorts the relative probabilities
        Arrays.sort(ps);

        // orders the characters by ASCII value using the orderSpec function from above
        chars = orderSpec(spec);

        // stores total cumulative frequency
        int total = 0;

        // calculates the total cumulative probability
        for (int ma = 0; ma < ps.length; ma++) {
            total = total + ps[ma];
        }

        // if the total cumulative frequency is not equal to 999999 then write Bad Source Input
        if (total != 999999) {
            return("Bad Source Input");
        }
 
        // calls the srithmetic function with the source alphabet dictionary and the data to be encoded
        String output = arithmetic(dict, tokens, chars);

        // return output
        return(output);

    }

    // main function takes two arguments (input file and output file [.txt])
    // parses input file to array of strings split between spaces then calls encode() on the string array
    public static void main(String args[]) throws FileNotFoundException, IOException, DidntWorkException {

        // null is a temporary variable to store each line read from the file as a string
        String line = null;

        try {

            // the buffered reader reads the text in from the file passed in from the command line
            // the string builder concatenates each line read by the buffered reader
            // the file writer is uesd to write output into a file
            BufferedReader in = new BufferedReader(new FileReader(new File(args[0])));
            StringBuilder sb = new StringBuilder();
            FileWriter myWriter = new FileWriter(new File(args[1]));

            // the while loop reads each line off the file and appends the lines to the string builder
            while((line = in.readLine()) != null) {
                sb.append(line.trim());
                sb.append(" ");
            }

            // contents stores the string builder's data in the string data type
            // the buffered reader is then closed
            // strArr is an array of strings defined as the input text split by whitespace
            String contents = sb.toString();
            System.out.println(contents);
            in.close();
            String[] strArr = contents.split("\\s+");

            // this is a simple check to see if the input is too short and thus Bad Input Source
            if (strArr.length < 3) {
                myWriter.write("Bad Source Input");
                throw new DidntWorkException("RAHHH");
            }

            /*
            for (int ig = 0; ig < strArr.length; ig++) {
                System.out.println("strArr[" + ig + "] = " + strArr[ig]);
            }
            */

            boolean result = Arrays.stream(strArr).anyMatch("end"::equals);
            if (!result) {
                System.out.println("No end!");
                myWriter.write("Bad Source Input");
            }

            // call encode on array of strings
            String output = encode(strArr);

            myWriter.write(output);

            // closes the file writer
            myWriter.close();

        } catch (DidntWorkException e) {
            // if an exception is thrown then throw it
            throw new DidntWorkException("Frick.");
        }

    }

}