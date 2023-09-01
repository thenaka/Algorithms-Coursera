import java.util.Arrays;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;

public class BaseballElimination {
    private int teamCount;
    private String[] teams;
    private String[] vertices;
    private int[] wins;
    private int[] losses;
    private int[] remaining;
    private int[][] gamesVersus;
    private Bag<String> eliminators;

    /**
     * Create a max flow baseball elimination from the given file.
     *
     * @param filename the file used to generate the max flow.
     * @throws IllegalArgumentException if cannot open {@code name} as
     *                                  a file or URL
     * @throws IllegalArgumentException if {@code filename} is {@code null}
     */
    public BaseballElimination(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename must not be null.");
        }

        eliminators = new Bag<String>();
        readFile(filename);
    }

    /**
     * Get the number of teams.
     *
     * @return the number of teams.
     */
    public int numberOfTeams() {
        return teamCount;
    }

    /**
     * Get an iterator for each team.
     *
     * @return an iterator for each team.
     */
    public Iterable<String> teams() {
        return Arrays.asList(teams);
    }

    /**
     * Get the number of wins for the given team.
     *
     * @param team the team to return the number of wins.
     * @return the wins for the given team.
     * @exception IllegalArgumentException if {@code team} is {@code null} or cannot
     *                                     be found in {@code teams}.
     */
    public int wins(String team) {
        ValidateTeamIsNotNull(team);
        return wins[getTeamNumber(team)];
    }

    /**
     * Get the number of wins for the given team.
     *
     * @param team the team to return the number of losses.
     * @return the number of losses for the given team.
     * @exception IllegalArgumentException if {@code team} is {@code null} or cannot
     *                                     be found in {@code teams}.
     */
    public int losses(String team) {
        ValidateTeamIsNotNull(team);
        return losses[getTeamNumber(team)];
    }

    /**
     * Get the number of games remaining for the given team.
     *
     * @param team the team to get the number of games remaining.
     * @return the number of remaining games for the given team.
     * @exception IllegalArgumentException if {@code team} is {@code null} or cannot
     *                                     be found in {@code teams}.
     */
    public int remaining(String team) {
        ValidateTeamIsNotNull(team);
        return remaining[getTeamNumber(team)];
    }

    /**
     * Get the number of games remaining between team1 and team2.
     *
     * @param team1 the first team to return remaining games.
     * @param team2 the second team to return remaining games.
     * @return the games remaining between team1 and team2.
     * @exception IllegalArgumentException if {@code team1} or {@code team2} is
     *                                     {@code null} or if either team cannot
     *                                     be found in {@code teams}.
     */
    public int against(String team1, String team2) {
        ValidateTeamIsNotNull(team1);
        ValidateTeamIsNotNull(team2);
        return gamesVersus[getTeamNumber(team1)][getTeamNumber(team2)];
    }

    /**
     * Get if the given team is eliminated from winning the division.
     *
     * @param team the team to check for elimination.
     * @return true if the team is eliminated, otherwise false.
     * @exception IllegalArgumentException if {@code team} is {@code null} or cannot
     *                                     be found in {@code teams}.
     */
    public boolean isEliminated(String team) {
        ValidateTeamIsNotNull(team);
        if (isTriviallyEliminated(team)) {
            return true;
        }
        setupFlowNetwork(team);

        return false;
    }

    /**
     * Get the subset R of teams that eliminate the given team.
     *
     * @param team the team that is eliminated.
     * @return the subset R of teams that eliminate the given team, null if not
     *         eliminated.
     * @exception IllegalArgumentException if {@code team} is {@code null} or cannot
     *                                     be found in {@code teams}.
     */
    public Iterable<String> certificateOfElimination(String team) {
        ValidateTeamIsNotNull(team);
        isEliminated(team);
        return eliminators.isEmpty() ? null : eliminators;
    }

    private void readFile(String filename) {
        In input = new In(filename);

        teamCount = input.readInt();
        teams = new String[teamCount];

        wins = new int[teamCount];
        losses = new int[teamCount];

        remaining = new int[teamCount];
        gamesVersus = new int[teamCount][teamCount];

        for (int i = 0; i < teamCount; i++) {
            teams[i] = input.readString();

            wins[i] = input.readInt();
            losses[i] = input.readInt();
            remaining[i] = input.readInt();

            for (int j = 0; j < teamCount; j++) {
                gamesVersus[i][j] = input.readInt();
            }
        }
    }

    private void setupFlowNetwork(String team) {
        // Flow network will have one vertex for each team and a vertex for the games
        // between each team.
        // For the following example:
        //
        // Team Wins Losses Remaining Versus
        // 0 Atlanta        83 71 8     0 1 6 1
        // 1 Philadelphia   80 79 3     1 0 0 2
        // 2 New_York       78 78 6     6 0 0 0
        // 3 Montreal       77 82 3     1 2 0 0

        int vertexCount = teamCount - 1;
        for (int i = teamCount - 2; i > 0; i--) {
            vertexCount += i;
        }
        vertices = new String[vertexCount];

        int teamNumber = getTeamNumber(team);
        int vertexIndex = 0;
        int teamIndex = 0;

        // Fill the first vertices with the team names
        while (teamIndex < teamCount) {
            if (teamIndex == teamNumber) {
                teamIndex++;
                continue;
            }
            vertices[vertexIndex++] = teams[teamIndex++];
        }

        // Fill the remaining vertices with the teams' games versus each other
        for (int i = 0; i < teamCount - 1; i++) {
            for (int j = i; j < teamCount - 1; j++) {
                if (i == j) {
                    continue; // skip team's games against themself
                }
                vertices[vertexIndex++] = vertices[i] + "-" + vertices[j];
            }
        }

        FlowNetwork flowNetwork = new FlowNetwork(vertices.length + 2);

        // source vertex = vertices.length
        // target vertex = vertices.length + 1




        // FordFulkerson maxFlow = new FordFulkerson(null, teamCount, teamCount)

    }

    private int getTeamNumber(String team) {
        for (int i = 0; i < teamCount; i++) {
            if (teams[i].equals(team)) {
                return i;
            }
        }
        throw new IllegalArgumentException(team + " not found");
    }

    private void ValidateTeamIsNotNull(String team) {
        if (team == null) {
            throw new IllegalArgumentException("Team name cannot be null");
        }
    }

    private boolean isTriviallyEliminated(String team) {
        int teamNumber = getTeamNumber(team);
        int totalPossible = wins(team) + remaining(team);
        for (int i = 0; i < teamCount; i++) {
            if (teamNumber == i) {
                continue;
            }
            if (totalPossible < wins[i]) {
                eliminators.add(teams[i]);
            }
        }
        return eliminators.size() > 0;
    }

    public static void main(String[] args) {
        BaseballElimination baseballElimination = new BaseballElimination("teams/teams4.txt");
        baseballElimination.isEliminated("Atlanta");
    }
}