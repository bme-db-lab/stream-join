package hu.bme.vzqixx.punctuations;

public enum PunctuationMode {
    NONE(false, false)
    , TAIL(false, true)
    , HEAD_AND_TAIL(true, true)
    ;

    public final boolean hasHeadPunctuation;
    public final boolean hasTailPunctuation;

    PunctuationMode(boolean hasHeadPunctuation, boolean hasTailPunctuation) {
        this.hasHeadPunctuation=hasHeadPunctuation;
        this.hasTailPunctuation=hasTailPunctuation;
    }

    public boolean isNonePunctuationMode() {
        return NONE.equals(this);
    }

}
