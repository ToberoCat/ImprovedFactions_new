package io.github.toberocat.core.utility.language;

import org.jetbrains.annotations.NotNull;

/**
 * This class is used to replace a char from a .lang message with a string. E.g: {faction_name} => name of your faction
 */
public class Parseable {

    private String parse;
    private String to;

    /**
     * Create a new parseable
     *
     * @param parse The string sequence that needs to be replaced
     * @param to    The sequence the old should be replaced with
     */
    public Parseable(String parse, String to) {
        this.parse = parse;
        this.to = to;
    }

    public static Parseable of(@NotNull String from, String to) {
        return new Parseable("{" + from + "}", to);
    }

    /**
     * Create a new parseable
     *
     * @param parse The string sequence that needs to be replaced
     * @param to    The sequence the old should be replaced with
     */
    public Parseable(String parse, int to) {
        this.parse = parse;
        this.to = String.valueOf(to);
    }

    /**
     * Create a new parseable
     *
     * @param parse The string sequence that needs to be replaced
     * @param to    The sequence the old should be replaced with
     */
    public Parseable(String parse, double to) {
        this.parse = parse;
        this.to = String.valueOf(to);
    }

    /**
     * Create a new parseable
     *
     * @param parse The string sequence that needs to be replaced
     * @param to    The sequence the old should be replaced with
     */
    public Parseable(String parse, boolean to) {
        this.parse = parse;
        this.to = String.valueOf(to);
    }


    public String getParse() {
        return parse;
    }

    public void setParse(String parse) {
        this.parse = parse;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
