import java.util.Scanner;
import java.io.*;  

public class Cache {

    // print a visual representation of the cache
    public static void printCache(byte[][] d, boolean[] v, int[] t, int w, int h) {
        System.out.print(" ");
        for(int i = 0; i < 16; i++) {
            System.out.printf("|%S", Integer.toHexString(i));
        }
        System.out.print("| valid | tag |\n");

        System.out.println("   _____________________________________________");
        String s = "";
        // print data 
        for(int i = 0; i < h; i++) {
            System.out.print(i + " ");
            for(int j = 0; j < w; j++) {
                // make sure our data is positive
                if(d[i][j] < 0) {
                    System.out.print("| ");
                }
                else {
                    s = Character.toString(d[i][j]);

                    // print visibile characters such as letters/numbers
                    if (s.charAt(0) > 32){
                        System.out.printf("|%s", s);
                    }
                    // replace invisible chars with space
                    else {
                        System.out.print("| ");
                   } 
                }
            }
            System.out.printf("|%5b|", v[i]);
            String hex = Integer.toHexString(t[i]);
            // print tag
            for(int k = 0; k < 7-hex.length(); k++) {
                System.out.print("0");
            }
            System.out.println(hex.toUpperCase() + "|");
        }
            System.out.println("   ‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾");
            System.out.println();
    }

    // fills a row with data from file
    public static void fillRow(int tag, byte data[][], int i) {
        try {
            RandomAccessFile f = new RandomAccessFile("input1.txt", "r");   // open a file 
            f.seek(tag*16);                                                 // go to address
            f.read(data[i]);                                                // fill the respective row with the data
            f.close();
        }
        catch(Exception ex) {
            System.out.println("Error");
            System.exit(1);
        }
    }

    // check only valid hex chars are entered
    public static boolean isHex(String s) {
        for(int i = 0; i < 20; i++) {
            char c = (char) (i + 103);
            if(s.toLowerCase().contains(Character.toString(c))) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);  // created scanner object for interactive user input
        int h = 10;                          // height of the cache
        int w = 16;                          // width of the cache

        boolean isValidTag = false;          // check if tag is in cache
        boolean isOpenSpace = false;         // check if there's open space for a new memory location
        int random = 0;                      // random variable for evictions
        int hits = 0;                        // number of hits
        int iterations = 0;                  // number of iterations
        String str = "";                     // store the user input
        
        byte[][] data = new byte[h][w];      // space to hold cache data
        boolean[] validity = new boolean[h]; // space to check if an address has been accessed
        int[] tags = new int[h];             // space to hold the tag of the address

        while(true) {
            System.out.print("Enter a memory address (ex. 3ADEB73F) or quit (q): ");

            str = s.next();
            if(str.equals("q") || str.equals("Q")) {
                break;
            }
            else if(str.length() > 8 || !isHex(str)) {
                System.out.println("Please enter less than 8 hex digits (0-f). Do not include the '0x'.");
                continue;
            }

            System.out.println();

            int address = Integer.parseInt(str, 16);                            // convert input to int 
            int tag = address / 16;                                             // get tag from address
            int index = address % 16;                                           // get index from address
            int saveH = 0;                                                      // save the row where we found a hit

            // search through tags to find if n is in the cache
            for(int i = 0; i < h; i++) {
                if(tags[i] == tag && validity[i]) {
                    isValidTag = true;
                    isOpenSpace = true;
                    saveH = i;
                    hits++;
                    break;
                }
            }

            // if not in the cache add it
            if(!isValidTag) {
                for(int i = 0; i < h; i ++) {
                    if(!validity[i]) {
                        tags[i] = tag;
                        validity[i] = true;
                        isOpenSpace = true;
                        fillRow(tag, data, i);
                        break;
                    }
                }
            }

            // if no open space in the cache, eviction 
            if(!isOpenSpace) {
                random = (int)(Math.random() * h);
                tags[random] = tag;
                fillRow(tag, data, random);
            }

            printCache(data, validity, tags, w, h);

            // prints info regarding hits/misses/evictions/data etc.
            if(isValidTag && isOpenSpace) {
                System.out.println("Cache Hit!");
                if(data[saveH][index] < 0 || Character.toString(data[saveH][index]).charAt(0) < 32){
                    System.out.println("Data at 0x" + str + ": A Space, an invisible character, or nothing");
                }
                else {
                    System.out.println("Data at 0x" + str + ": " + Character.toString(data[saveH][index]));
                }
            }
            else if(!isValidTag) {
                System.out.println("Cache Miss!");
                if (!isOpenSpace) {
                    System.out.println("Eviction.");
                }
            }

            // reset
            isOpenSpace = false;
            isValidTag = false;
            iterations++;

            double hitRate = ( (double)hits/ (double)iterations) * 100;
            System.out.println();
            System.out.println("Hit Rate: " + hitRate);
            System.out.println();

        }

        s.close();
    }
}

