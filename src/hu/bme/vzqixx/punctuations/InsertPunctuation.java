package hu.bme.vzqixx.punctuations;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class InsertPunctuation {

    public static final String SEPARATOR="|";

    public static void main(String[] args) {
        if (args.length < 5) {
            printUsage();
            System.exit(0);
        }

        PunctuationMode mode = PunctuationMode.valueOf(args[0]);
        int punctuationOnFieldPosition = Integer.valueOf(args[1]);
        File path = new File(args[2]);
        File inputFile = new File(path, args[3]);
        File outputFile = new File(path, args[4]);

        try (
                PrintStream outputStream = new PrintStream(outputFile);
        ) {
            createPunctuatedStream(mode, punctuationOnFieldPosition, inputFile, outputStream, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printUsage() {
        System.out.println("Usage: " + InsertPunctuation.class.getName() + "punctuationMode punctuationOnFieldPosition path inputFile outputFile");
        System.out.println("   punctuationMode can be: NONE, TAIL, HEAD_AND_TAIL");
    }

    static void  createPunctuatedStream(PunctuationMode mode, int punctuationOnFieldPosition, File inputFile, PrintStream outputStream, boolean headerInFirstRow) throws  IOException {
        if (mode.isNonePunctuationMode()) {
            try (Stream<String> inputStream = Files.lines(inputFile.toPath())) {
                inputStream.forEachOrdered(s -> outputStream.println(s));
                return;
            }
        }

        HashSet<String> headPunctuationEmitted = new HashSet<>();
        HashMap<String, Integer> itemLastIndex = new HashMap<>();

        // iterate through and populate the itemLastIndex HashMap
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            if (headerInFirstRow) {
                // just consume the header row
                br.readLine();
            }
            for (int i=(headerInFirstRow?1:0); (line = br.readLine()) != null; i++) {
                String[] fields = line.split(Pattern.quote(SEPARATOR));
                String pattern = fields[punctuationOnFieldPosition];
                itemLastIndex.put(pattern, i);
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            if (headerInFirstRow && (line = br.readLine()) != null) {
                outputStream.println(line);
            }
            for (int i=(headerInFirstRow?1:0); (line = br.readLine()) != null; i++) {
                String[] fields = line.split(Pattern.quote(SEPARATOR));
                String pattern = fields[punctuationOnFieldPosition];
                if (mode.hasHeadPunctuation && !headPunctuationEmitted.contains(pattern)) {
                    outputStream.println(assemblePunctuation(PunctuationMode.headPunctuationMark, punctuationOnFieldPosition, fields.length, pattern));
                    headPunctuationEmitted.add(pattern);
                }
                outputStream.println(line);
                if (mode.hasTailPunctuation && itemLastIndex.getOrDefault(pattern, -1) == i) {
                    // this was the last occurence, so we emit tail punctuation
                    outputStream.println(assemblePunctuation(PunctuationMode.tailPunctuationMark, punctuationOnFieldPosition, fields.length, pattern));
                }
            }
        }
    }

    static String assemblePunctuation(String mark, int position, int length, String pattern) {
        return mark+SEPARATOR+pattern;
    }
}
