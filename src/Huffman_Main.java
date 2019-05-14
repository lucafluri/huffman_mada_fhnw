import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class Huffman_Main {

    private static String inputPath = "ascii.txt";          // Ascii Text File to decode
    private static String tablePath = "dec_tab.txt";        // Path to Huffman Table
    private static String comprPath = "output.dat";         // Path to compressed File
    private static String decomprPath = "decompress.txt";   // Path to decompressed File

    public static void main(String[] args) throws IOException {

        compress(); // creates freq table and creates a huffman tree which is used to compress/encode the original text
                    // Both the Huffman table and the compressed text are stored.

        decompress(); // Decompresses the specified file with the specified table and saves the result.
    }

    /**
     * Reads a ASCII File and returns its contents as string
     * @param filepath of file
     * @return String of its contents
     * @throws IOException
     */
    public static String readASCIIFile(String filepath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filepath)));
    }

    /**
     * Creates a frequency table from given string
     * @param input String
     * @return int[] where index corresponds to character value and the element is its frequency
     */
    public static int[] createFreqTable(String input){
        System.out.println("Creating Frequency Table...");

        int[] freqTable = new int[256]; //ASCII Table Size = 256, index is used as char identifier and elements as frequency.
        for(char c : input.toCharArray()){
            if(c<256){ //only ascii characters!
                freqTable[c]++;
            }
        }
        return freqTable;
    }

    /**
     * Creates the Huffman Code and saves it to a file.
     * @param freqTable which has been generated before
     * @return TreeMap with characters and Huffman Code
     * @throws IOException
     */
    public static TreeMap<String, String> createHuffmanCode(int[] freqTable) throws IOException{
        System.out.println("Creating Huffman Tree...");

        PriorityQueue<Node> queue = new PriorityQueue<>((o1, o2) -> (o1.freq < o2.freq) ? -1 : 1); // sorted frequency table with (freq, char int)
        TreeMap<String, String> map = new TreeMap<>(); //Huffmancode table as treemap
        for(char i=0; i<freqTable.length; i++){
            if((freqTable[i] > 0) && queue.add(new Node(freqTable[i], String.valueOf(i)))); //saves every character and corresponding frequency that appears more than once in the text.
        } //frequency table generated

        Node left, right, top = null;

        while(queue.size()!=1){ //Huffman Tree Algorithm
            left = queue.poll();
            right = queue.poll(); //removes the 2 nodes with lowest frequency
            top = new Node(left, right); // creates a parent node with the combined frequency fo left and right nodes. -> see Node Class at bottom
            queue.add(top); //Adds Parent Node to queue.
            //Repeats until only one element is present in queue -> Algorithm finished.
        }
        //"Tree" finished, we have top node of tree

        getCodes(map, "", top); //We use the top node to recursively traverse the Tree and generate the codes.
        saveHuffman(map); //Save the generated Huffman Code

        return map;
    }

    /**
     * Fills recursively the Huffman Tree Map
     * @param map Huffman Tree Map
     * @param code current iteration of the code
     * @param top current top node
     */
    public static void getCodes(TreeMap<String, String> map, String code, Node top){
        if(top.left == null && top.right==null){ //This means the node is a leaf node and we have found a character.
            map.put(top.value, code); //Add the newly found character and huffman code pair to the map.
        }else{
            getCodes(map, code + "0", top.left); //If we go left we add a 0 to the code and call the function recursively
            getCodes(map, code + "1", top.right); //right -> +1
        }

    }

    /**
     * Saves the Huffman Table to file
     * @param table Huffman Table as a TreeMap
     * @throws IOException
     */
    public static void saveHuffman(TreeMap<String, String> table) throws IOException {
        String toWrite = "";
        TreeMap<String, String> map = (TreeMap) table.clone();

        System.out.println("Saving Huffman Tree to " + tablePath + "...");

        int size = map.size();
        for(int i = 0; i<size; i++){ //Saving Huffman Table according to Assignment
            toWrite += Integer.valueOf(map.firstEntry().getKey().toCharArray()[0]) + ":";
            toWrite += map.firstEntry().getValue() + "-"; //The last "-" is ignored for simplicity
            map.pollFirstEntry();
        }
        Files.write(Paths.get(tablePath), toWrite.getBytes()); //saves the Huffman Table to the specified file path
    }

    /**
     * Compresses  Text (Filepath specified as main class variables!)
     * @throws IOException
     */
    public static void compress() throws IOException{
        System.out.println("COMPRESSING:\nReading File " + inputPath + "...");
        String bitString = "";
        String text = readASCIIFile(inputPath);
        TreeMap<String, String> table = createHuffmanCode(createFreqTable(text)); //creates the huffman table
        System.out.println("Compressing Text...");

        float count = 0;
        float progr = 0;
        float oldprogr = 0;
        for(char c : text.toCharArray()){
            bitString += table.get(String.valueOf(c)); //Encodes each character of the text with the huffman table
            progr = (int) ((++count / text.toCharArray().length) * 100);
            if (progr % 10 == 0 && progr != oldprogr) {
                System.out.print((int) progr + "%\r");
            }
            oldprogr = progr;

        }
        bitString += "1";
        while(bitString.length() % 8 != 0){ // Appends a 1 and tailing 0s until divisible by 8
            bitString += "0";
        }

        //create byteArray
        byte[] byteArray = new byte[bitString.length()/8];
        for(int i = 0; i<bitString.length()/8;i++){ //saving bitstring as byte array
            byteArray[i] = (byte) Integer.parseInt(bitString.substring(i*8, (i+1)*8), 2); //parsing int base 2 -> binary number.
        }
        //System.out.println(byteArray);

        System.out.println("Saving Compressed Text to " + comprPath + "...\n");
        FileOutputStream fos = new FileOutputStream(comprPath);
        fos.write(byteArray);
        fos.close();

    }

    /**
     * Decompresses file specified as main class variable.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void decompress() throws FileNotFoundException, IOException{
        System.out.println("DECOMPRESSING:\nDecompressing " + comprPath + "...");
        File file = new File(comprPath);
        byte[] bFile = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(bFile);
        fis.close();

        String bitString = "";
        float count = 0;
        float progr = 0;
        float oldprogr = 0;
        for(byte b : bFile){ // Converting every byte from the bytearray to a  binarystring. and adding it to bitstring
            bitString += Integer.toBinaryString((b & 0xFF)+0x100).substring(1); // Wasted too much time here. Thanks Stackoverflow^^
            progr = (int) ((++count / bFile.length) * 100);
            if (progr % 10 == 0 && progr != oldprogr) {
                System.out.print((int) progr + "%\r");
            }
            oldprogr = progr;
        }

        bitString = bitString.substring(0, bitString.lastIndexOf("1")); //remove tailing 0s and 1

        String decTable = readASCIIFile(tablePath);
        System.out.println("Reading Huffman Table from " + tablePath + "...");
        //Parse String into TreeMap
        TreeMap<String, String> table = new TreeMap<>();
        String[] mappings = decTable.split("-");

        for(String mapping : mappings){ //Splits the saved huffman table and converts it back to a map.
            String c = String.valueOf((char) Integer.parseInt(mapping.split(":")[0]));
            String v = mapping.split(":")[1];
            table.put(c, v);
        }

        //System.out.println("read Table: " + table);

        String decoded = "";
        int a = 0;
        for(int i = 0; i<=bitString.length(); i++) { //Actual decompressing
            for(Map.Entry<String, String> entry: table.entrySet()){
                String substring = bitString.substring(a, i);
                if(entry.getValue().equals(substring)){ //If current substring exists as a value in the map, we add its key (=character) to the decompressed string. All mappings are completely unique and no code exists twice.
                    decoded += entry.getKey();
                    a = i; //adjust starting position of substring
                }
            }

        }

        //Write decoded File
        Files.write(Paths.get(decomprPath), decoded.getBytes());
        System.out.println("Decompressed File saved to " + decomprPath);

    }


}

/**
 * Tree Node Class
 */
class Node{
    Node left = null;
    Node right = null;
    String value;
    int freq;

    /**
     * Constructor for leaf nodes
     * @param _freq
     * @param _value
     */
    public Node(int _freq, String _value){ //Constructor for bottom Nodes
        this.freq = _freq;
        this.value = _value;
    }

    /**
     * Constructor for parent nodes
     * @param _left
     * @param _right
     */
    public Node(Node _left, Node _right){ //Constructor for all the parent Nodes with children
        if(_left.freq > _right.freq){ //Correct binary tree order
            this.right = _left;
            this.left = _right;
        }else {
            this.left = _left;
            this.right = _right;
        }

        this.value = _left.value + _right.value; //Each Parent Node has the sum of its childrens value as value (character)
        this.freq = _left.freq + _right.freq; //Sum of children's frequency
    }

    @Override
    public String toString(){
        return this.freq + ": " + this.value;
    }

}
