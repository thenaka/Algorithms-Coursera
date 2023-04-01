import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Topological;

public class WordNet {
    private ST<Integer, Bag<String>> synsets; // index->string
    private ST<String, Bag<Integer>> reverseSynsets; // string->index
    private Digraph hypernyms;

    public WordNet(String synsetsFile, String hypernymsFile) {
        if (synsetsFile == null) {
            throw new IllegalArgumentException("synsetsFile cannot be null.");
        }
        if (hypernymsFile == null) {
            throw new IllegalArgumentException("hypernymsFile cannot be null.");
        }

        readSynsets(synsetsFile);
        readHypernyms(hypernymsFile);
        isDag();
    }

    private void readSynsets(String synsetsFile) {
        this.synsets = new ST<Integer, Bag<String>>();
        this.reverseSynsets = new ST<String, Bag<Integer>>();

        In synsetStream = new In(synsetsFile);
        while (synsetStream.hasNextLine()) {
            String[] synsetParts = synsetStream.readLine().split(",");
            int id = Integer.parseInt(synsetParts[0]);
            String[] synsetSplit = synsetParts[1].split(" ");

            if (!this.synsets.contains(id)) {
                this.synsets.put(id, new Bag<String>());
            }
            for (String synset : synsetSplit) {
                this.synsets.get(id).add(synset);

                if (!this.reverseSynsets.contains(synset)) {
                    this.reverseSynsets.put(synset, new Bag<Integer>());
                }
                this.reverseSynsets.get(synset).add(id);
            }
        }
    }

    private void readHypernyms(String hypernymsFile) {
        this.hypernyms = new Digraph(synsets.size());
        In hypernymsStream = new In(hypernymsFile);
        while (hypernymsStream.hasNextLine()) {
            String[] hypernymParts = hypernymsStream.readLine().split(",");
            if (hypernymParts.length < 2) {
                continue;
            }

            int v = Integer.parseInt(hypernymParts[0]);
            for (int i = 1; i < hypernymParts.length; i++) {
                int w = Integer.parseInt(hypernymParts[i]);
                this.hypernyms.addEdge(v, w);
            }
        }
    }

    private void isDag() {
        Topological topo = new Topological(hypernyms);
        if (!topo.hasOrder()) {
            throw new IllegalArgumentException("The given hypernyms are not a DAG.");
        }
    }

    /**
     * Returns all WordNet nouns.
     * 
     * @return all WordNet nouns.
     */
    public Iterable<String> nouns() {
        return this.reverseSynsets.keys();
    }

    /**
     * Is the word a WordNet noun?
     * 
     * @param word noun to search for.
     * @return true if the word is a noun.
     */
    public boolean isNoun(String word) {
        return this.reverseSynsets.contains(word);
    }

    /**
     * Get the distance between nounA and nounB via a shortest ancestral path.
     * 
     * @param nounA noun to find the distance.
     * @param nounB noun to find the distance.
     * @return
     */
    public int distance(String nounA, String nounB) {
        SAP sap = new SAP(this.hypernyms);
        Iterable<Integer> aNouns = reverseSynsets.get(nounA);
        Iterable<Integer> bNouns = reverseSynsets.get(nounB);
        return sap.length(aNouns, bNouns);
    }

    /**
     * Get a synset that is the common ancestor of nounA and nounB in a shortest
     * ancestral path.
     * 
     * @param nounA noun to find the common ancestor.
     * @param nounB noun to find the common ancestor.
     * @return the common ancestor of noun! and nounB.
     */
    public String sap(String nounA, String nounB) {
        SAP sap = new SAP(this.hypernyms);
        Iterable<Integer> aNouns = reverseSynsets.get(nounA);
        Iterable<Integer> bNouns = reverseSynsets.get(nounB);

        String commonAncestor = "";
        for (String s : this.synsets.get(sap.ancestor(aNouns, bNouns))) {
            commonAncestor = s;
        }
        return commonAncestor;
    }

    public static void main(String[] args) {
        WordNet wn = new WordNet("synsets.txt", "hypernyms.txt");
        assert wn.isNoun("football");
        assert wn.isNoun("defect");
        assert wn.distance("football", "defect") == 12;
        assert wn.sap("football", "defect").equals("abstraction");
    }
}
