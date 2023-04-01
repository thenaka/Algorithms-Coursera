public class Outcast {
    private final WordNet wordnet;

    /**
     * Find outcast nouns for a given wordnet.
     * 
     * @param wordnet wordnet to check for outcasts.
     * @throws IllegalArgumentException if wordnet is null.
     */
    public Outcast(WordNet wordnet) {
        if (wordnet == null) {
            throw new IllegalArgumentException("Wordnet cannot be null.");
        }
        this.wordnet = wordnet;
    }

    /**
     * Given an array of WordNet nouns, return an outcast, the noun furthest away
     * from the others.
     * 
     * @param nouns words to find the outcast.
     * @return the noun that is the furthest distance from the others.
     */
    public String outcast(String[] nouns) {
        if (nouns == null) {
            throw new IllegalArgumentException("nouns must not be null.");
        }
        if (nouns.length == 1) {
            return nouns[0];
        }

        int outcast = -1;
        int currentSum = 0;
        int previousSum = -1;
        for (int i = 0; i < nouns.length; i++) {
            for (int j = 0; j < nouns.length; j++) {
                if (i != j) {
                    currentSum += wordnet.distance(nouns[i], nouns[j]);
                }
            }
            if (currentSum > previousSum) {
                outcast = i;
                previousSum = currentSum;
            }
        }

        return nouns[outcast];
    }

    public static void main(String[] args) {
        WordNet wn = new WordNet("synsets.txt", "hypernyms.txt");
        Outcast oc = new Outcast(wn);
        assert oc.outcast(new String[] { "zebra", "cat", "bear", "table" }).equals("table");
    }
}
