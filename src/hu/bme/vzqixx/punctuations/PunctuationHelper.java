package hu.bme.vzqixx.punctuations;

public class PunctuationHelper {
    public static String headPunctuationMark = "#";
    public static String tailPunctuationMark = "*";

    public static boolean isHeadPunctuation(String[] fields) {
        return isHeadTailPunctuationInternal(true, fields);
    }
    public static boolean isTailPunctuation(String[] fields) {
        return isHeadTailPunctuationInternal(false, fields);
    }

    /**
     *
     * @param fields
     * @param idx zero-based index of the join attribute in the streaming relation
     * @return
     */
    public static String getPunctuationPattern(String[] fields, int idx) {
        return fields[1]; // TODO: pattern is currently at a fixed location
    }

    protected static boolean isHeadTailPunctuationInternal(boolean lookForHead, String[] fields) {
        String mark = lookForHead?headPunctuationMark:tailPunctuationMark;
        return fields.length > 0 && fields.length == 2 && mark.equals(fields[0]);
    }
}
