import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class adec {

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
        }

        // adds the end of message symbol
        newProbs[newProbs.length - 1] = 1;
        newChars[newChars.length - 1] = "§";

        // returns new chars (renders probabilities array useless but thats just how the development journey went)
        return(newChars);
 
    }

    // this function converts a hexadecimal tag stored as a string array to a binary string
    public static String HexTagToBinaryTag(String[] hexTag) {

        // binary tag will store the outputted tag in binary
        String binaryTag = "";
        // binary nibble will store the reversed binary string equal to each hexadecimal character
        String binaryNibble = "";

        // this loop will iterate through each hexadecimal character in the tag
        for (int i = 0; i < hexTag.length; i++) {
            // this switch is a lazy but fast way to replace hexadecimal characters with their reversed binary counterparts
            switch (hexTag[i]) {
                case "0": // 0000
                    binaryNibble = "0000";
                    break;
                case "1": // 0001
                    binaryNibble = "1000"; 
                    break;
                case "2": // 0010
                    binaryNibble = "0100";
                    break;
                case "3": // 0011
                    binaryNibble = "1100"; 
                    break;
                case "4": // 0100
                    binaryNibble = "0010"; 
                    break;
                case "5": // 0101
                    binaryNibble = "1010"; 
                    break;
                case "6": // 0110
                    binaryNibble = "0110"; 
                    break;
                case "7": // 0111
                    binaryNibble = "1110"; 
                    break;
                case "8": // 1000
                    binaryNibble = "0001"; 
                    break;
                case "9": // 1001
                    binaryNibble = "1001"; 
                    break;
                case "A": // 1010
                    binaryNibble = "0101"; 
                    break;
                case "B": // 1011
                    binaryNibble = "1101"; 
                    break;
                case "C": // 1100
                    binaryNibble = "0011"; 
                    break;
                case "D": // 1101
                    binaryNibble = "1011"; 
                    break;
                case "E": // 1110
                    binaryNibble = "0111"; 
                    break;
                case "F": // 1111
                    binaryNibble = "1111"; 
                    break;
                default:
                    binaryNibble = "0000"; 
                    break;
            }
            // the binary nibble of this character is appended to the binary tag
            binaryTag = binaryTag + binaryNibble;
        }

        // after all characters have been converted, the binary tag is returned
        return(binaryTag);

    }

    // calculates the first 32 bits of the binary tag represented in decimal form
    public static long calculateFirst32BitsAsInteger(String binaryTag) {

        // string to hold the first 32 bits
        String first32BitsStr = "";
        // long to hold the output
        long result = 0;

        // for each of the first 32 bits, add them to the first32BitsString
        for (int i = 0; i < 32; i++) {
            first32BitsStr = first32BitsStr + binaryTag.charAt(i);
        }

        // result is set as the demical form of the first 32 bits of the tag
        result = Long.parseLong(first32BitsStr, 2);

        // this number is returned
        return(result);

    }

    public static String arithmetic(Hashtable<String, Integer> probabilities, String[] tag, String[] chars) throws DidntWorkException {
        
        // stores the hex tag in binary form
        String binaryTag = HexTagToBinaryTag(tag);
        // l is the lower bound of the scale
        long l = 0;
        // u is the upper bound of the scale
        long u = Long.parseLong("4294967295");
        // v is the first B bits of the tag
        long v = calculateFirst32BitsAsInteger(binaryTag);
        // m is the length of the binary tag
        long m = binaryTag.length();
        // output is the string of message characters established from decoding
        String output = "";
        // i is an index counter
        int i = 33;
        // range is the differece between u and l
        long range = u - l + 1;
        // stores half the intitial range
        long half = range / 2;
        // stores a quarter of the intitial range
        long quarter = range / 4;
        // instantiates newU and newL to store l' and u' as described in the lecture notes
        long newU;
        long newL;
        //an unnecessary line of code that i left anyway
        Boolean working = true;

        while (working) {

            // scale is an array of Interval objects (a class that I wrote to store intervals)
            Interval[] scale = new Interval[chars.length];

            // array will store the lower bound for each character's cumulative probability
            int[] lBounds = new int[chars.length];

            // this block builds up a scale of cumulative probabilities
            lBounds[0] = 0;
            // temp stores the cumulative frequency interval for each character
            Interval temp = new Interval(chars[0], lBounds[0], lBounds[0] + probabilities.get(chars[0]));
            scale[0] = temp;
            // this loop fills the lBounds array with the lower cumulative frequencies of each character, initialises temp, and then adds the interval to the scale
            for (int oo = 1; oo < lBounds.length; oo++) {
                lBounds[oo] = lBounds[oo - 1] + probabilities.get(chars[oo - 1]);
                temp = new Interval(chars[oo], lBounds[oo], lBounds[oo] + probabilities.get(chars[oo]));
                scale[oo] = temp;
            }

            /*
            System.out.println("----------------------------------------------------------");
            System.out.println("Scale:");

            for (int j = 0; j < scale.length; j++) {
                System.out.println("Char: " + scale[j].character);
                System.out.println("Lower Cumulative Probability: " + scale[j].lower);
                System.out.println("Upper Cumulative Probability: " + scale[j].upper);    
            }

            System.out.println("----------------------------------------------------------");
            */

            // for each character in the source alphabet
            for (int j = 0; j < chars.length; j++) {

                // u is updated to u' as it was in the encoding algorithm
                newU = l + (long) Math.floor((double) ((scale[j].upper * range) / 1000000)) - 1;
                // l is updated to l' as it was in the encoding algorithm
                newL = l + (long) Math.floor((double) (scale[j].lower * range) / 1000000);

                // the range variable is updated
                range = u - l + 1;

                // check if the current approximation of the tag is within the interval that u and l have been updated to zoom into
                if ((v >= newL) && (v < newU)) {

                    // check that the character is not the end of message symbol, §
                    if (chars[j] != "§") {

                        // add the charcter to the output string
                        output = output + chars[j];
                    }

                    // u is set as newU or u'
                    u = newU;
                    // l is set as newL or l'
                    l = newL;

                    // if the character is the end of message character then return the output string
                    if (chars[j] == "§") {
                        return(output);
                    }
                }
            }

            // start rescaling operations
            // while u is less than half the range or l is greater than half the range
            while ((u < half) || (l > half)) {

                // integer rescaling operations for when u is below half the range
                if (u < half) {
                    u = 2 * u;
                    l = 2 * l;
                    v = 2 * v;
                } else if (l > half) { // integer rescaling operations for when l is greater than half the range
                    u = 2 * (u - half);
                    l = 2 * (l - half);
                    v = 2 * (v - half);
                }

                // if there are more bits to be expanded and the next bit is one
                if ((i < m) && (binaryTag.charAt(i) == 1)) {
                    // add one to v
                    v = v + 1;
                }
                
                // add one to the bit counter
                i = i + 1;

            }

            // while u is below 75% of the range and l is above 25% of the range
            while ((u < (3 * quarter)) && (l > quarter)) {

                // integer rescaling operations for when l is greater than half the range
                u = 2 * (u - quarter);
                l = 2 * (l - quarter);
                v = 2 * (v - quarter);

                // if there are more bits to be expanded and the next bit is one
                if ((i < m) && (binaryTag.charAt(i) == 1)) {
                    // add one to v
                    v = v + 1;
                }
                
                // add one to the bit counter
                i = i + 1;

            }

        }

        // returns the output string
        return(output);

    }

    // this function splits the input into nicely sorted arryas to be used in the arithmetic function
    public static String decode(String[] _strArr) throws DidntWorkException {
        
        // endReached is a boolean flag to determine whether the for loop has reached the end symbol yet
        // specSize is initialised and later assigned to the size of the specification array
        boolean endReached = false;
        int specSize = 0;

        // this loop finds the array index of the end symbol and sets specSize as the number of elements before that point
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

        // also sets spec
        for (int j = 0; j < specSize; j++) {
            spec[j] = _strArr[j];
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
            in.close();
            String[] strArr = contents.split("\\s+");

            // this is a simple check to see if the input is too short and thus Bad Input Source
            if (strArr.length < 3) {
                myWriter.write("Bad Source Input");
                throw new DidntWorkException("RAHHH");
            }

            // call encode on array of strings
            String output = decode(strArr);

            // prints the final output of the arithemtic decoding algorithm
            System.out.println("Final Output: " + output);

            // writes the output to an external file passed in through the command line
            myWriter.write(output);

            // closes the file writer
            myWriter.close();

        } catch (DidntWorkException e) {
            // if an exception is thrown then throw it
            throw new DidntWorkException("Frick.");
        }

    }

}