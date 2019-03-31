package hu.bme.vzqixx.punctuations;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class PunctuatedIndexJoin {
    static StopWatch timer = new StopWatch();

    public static void main(String[] args) {
        if (args.length < 6) {
            printUsage();
            System.exit(-1);
        }

        PunctuationMode mode = PunctuationMode.valueOf(args[0]);
        int sIdx = Integer.valueOf(args[1]);
        int rIdx = Integer.valueOf(args[2]);
        File path = new File(args[3]);
        File streamingRelationFile = new File(path, args[4]);
        File persistentRelationFile = new File(path, args[5]);

        try (
                PrintStream outputStream = DevNull.out; //System.out;
                Stream<String> sStream = Files.lines(streamingRelationFile.toPath());

        ) {
            // convert the r persistent relation to its memory representation
            HashMap<String, String> rMap = new HashMap<>();
            try (Stream<String> rStream = Files.lines(persistentRelationFile.toPath())) {
                rStream.forEach(rLine -> {
                    String fields[] = rLine.split(Pattern.quote(Configuration.SEPARATOR));
                    String key = fields[rIdx];
                    rMap.put(key, rLine);
                });
            }

            // do the actual join
            timer.reset();
            long averageItemCount = punctuatedIndexJoin(mode, sStream, sIdx, rMap, rIdx, outputStream);

            System.out.println(timer.read()+" "+averageItemCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printUsage() {
        System.out.println("Usage: " + PunctuatedIndexJoin.class.getName() + "punctuationMode punctuationOnFieldPositionInS punctuationOnFieldPositionInR path streamingRelation persistentRelation");
        System.out.println("   punctuationMode can be: NONE, TAIL, HEAD_AND_TAIL");
    }

    /**
     * Compute the left outer join of s and r.
     *
     * @param mode the way we have punctuations embedded in the stream
     * @param s a streaming relation, left side of the join
     * @param sIdx zero-based index in s of the field for the equi-join
     * @param r a persistent relation, right side of the join
     * @param rIdx zero-based index in r of the field for the equi-join
     */
    public static long punctuatedIndexJoin(PunctuationMode mode, Stream<String> s, int sIdx, HashMap<String, String> r, int rIdx, PrintStream out) {
        Map<String, String> I = new HashMap<>(); // initially empty index for join lookup on r
        if (!mode.hasHeadPunctuation) {
            // we don't have head punctuations, so we need to preload the index
            timer.start();
            I.putAll(r);
            timer.pause();
        }

        final long[] i = new long[3]; // 0: counter, 1: sumSamples; 2: numSamples=0;
        s.forEachOrdered(sLine -> {
            String[] sRecord = sLine.split(Pattern.quote(Configuration.SEPARATOR));
            timer.start();
            if (PunctuationHelper.isHeadPunctuation(sRecord)) {
                String pattern = PunctuationHelper.getPunctuationPattern(sRecord, sIdx);
                // load records of R to index I by the punctuation s
                I.put(pattern, r.get(pattern));
                if (Configuration.DO_PRINT) {
                    out.println(sLine); // propagate punctuation
                }
            } else if (PunctuationHelper.isTailPunctuation(sRecord)) {
                String pattern = PunctuationHelper.getPunctuationPattern(sRecord, sIdx);
                // discard records from index I by the punctuation s
                I.remove(pattern);
                if (Configuration.DO_PRINT) {
                    out.println(sLine); // propagate punctuation
                }
            } else {
                String sJoinAttribute = sRecord[sIdx];
                String rLine = I.get(sJoinAttribute);
                if (Configuration.DO_PRINT) {
                    if (rLine != null) {
                        out.println(sLine + Configuration.SEPARATOR + rLine);
                    } else {
                        // no match, behave as outer join
                        out.println(sLine + Configuration.SEPARATOR + Configuration.OUTER_JOIN_NULL_MARK);
                    }
                }
            }
            timer.pause(); // exclude string slitting from time measurements
            i[0]++; // 0: counter, 1: sumSamples; 2: numSamples=0;
            if (i[0] % Configuration.ITERATIONS_PER_SAMPLE == 0) {
                i[1]+=I.size();
                i[2]++;
            }
        });
        if (i[2]>0) {
            return i[1]/i[2];
        } else {
            return -1;
        }
    }
}
