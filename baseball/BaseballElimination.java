import java.util.Arrays;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class BaseballElimination {
    private int teamCount;
    private String[] teams;
    private String[] teamsAndGames;
    private int[] wins;
    private int[] losses;
    private int[] remaining;
    private int[][] gamesVersus;
    private Bag<String> eliminators;

    /**
     * Create a max flow baseball elimination from the given file.
     *
     * @param filename the file used to generate the max flow.
     * @throws IllegalArgumentException if cannot open {@code filename} as
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
        validateTeamIsNotNull(team);
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
        validateTeamIsNotNull(team);
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
        validateTeamIsNotNull(team);
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
        validateTeamIsNotNull(team1);
        validateTeamIsNotNull(team2);
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
        validateTeamIsNotNull(team);

        eliminators = new Bag<String>();

        if (isTriviallyEliminated(team)) {
            return true;
        }

        FordFulkerson fordFulkerson = setupFlowNetwork(team);
        for (int i = 0; i < (teams.length - 1); i++) {
            if (fordFulkerson.inCut(i)) {
                eliminators.add(teamsAndGames[i]);
            }
        }

        return !eliminators.isEmpty();
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
        return !eliminators.isEmpty();
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
        validateTeamIsNotNull(team);
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

    private FordFulkerson setupFlowNetwork(String team) {
        teamsAndGames = new String[getTeamsAndGamesCount()];
        fillTeamsAndGames(team);

        FlowNetwork flowNetwork = new FlowNetwork(teamsAndGames.length + 2);

        connectFlowNetwork(team, flowNetwork);

        return new FordFulkerson(flowNetwork, teamsAndGames.length, teamsAndGames.length + 1);
    }

    private int getTeamsAndGamesCount() {
        // Number of vertices in the flow network example:
        // New_York 75 59 28 0 3 8 7 3
        // Baltimore 71 63 28 3 0 2 7 7
        // Boston 69 66 27 8 2 0 0 3
        // Toronto 63 72 27 7 7 0 0 3
        // Detroit 49 86 27 3 7 3 3 0
        //
        // Number of teams minus one: 4
        // Plus: +
        // Games against each other: 4 * 4 = 16
        // Minus the games against themselves: 16 - 4 = 12
        // Divide the remainder by two: 12 / 2 = 6
        // Equals: = 10 vertices

        int remainingTeams = teamCount - 1;
        int gamesVersusCount = ((remainingTeams * remainingTeams) - remainingTeams) / 2;
        int teamsAndGamesCount = remainingTeams + gamesVersusCount;

        return teamsAndGamesCount;
    }

    private void fillTeamsAndGames(String team) {
        int teamNumber = getTeamNumber(team);
        int vertexIndex = 0;
        int teamIndex = 0;

        // Fill the first vertices with the team names
        while (teamIndex < teamCount) {
            if (teamIndex == teamNumber) {
                teamIndex++;
                continue;
            }
            teamsAndGames[vertexIndex++] = teams[teamIndex++];
        }

        // Fill the remaining vertices with the teams' games versus each other
        for (int i = 0; i < teamCount - 1; i++) {
            for (int j = i; j < teamCount - 1; j++) {
                if (i == j) {
                    continue; // skip team's games against themself
                }
                teamsAndGames[vertexIndex++] = teamsAndGames[i] + "-" + teamsAndGames[j];
            }
        }
    }

    private void connectFlowNetwork(String team, FlowNetwork flowNetwork) {
        // Connect teams to the target with capacity of (wins + remaining - candidate
        // team wins)
        int candidateTeam = getTeamNumber(team);
        int remainingTeamCount = teamCount - 1;
        int targetVertex = teamsAndGames.length + 1;
        for (int i = 0; i < remainingTeamCount; i++) {
            flowNetwork.addEdge(new FlowEdge(i, targetVertex,
                    wins[candidateTeam] + remaining[candidateTeam] - wins[getTeamNumber(teamsAndGames[i])]));
        }

        // Connect flow of games versus to each team playing with infinite capacity
        for (int i = remainingTeamCount; i < teamsAndGames.length; i++) {
            String[] matchup = teamsAndGames[i].split("-");
            int team1 = getTeamsAndGamesNumber(matchup[0]);
            int team2 = getTeamsAndGamesNumber(matchup[1]);

            flowNetwork.addEdge(new FlowEdge(i, team1, Double.POSITIVE_INFINITY));
            flowNetwork.addEdge(new FlowEdge(i, team2, Double.POSITIVE_INFINITY));
        }

        // Connect flow from source to games versus with the capacity equal to the
        // number of games against
        int sourceVertex = teamsAndGames.length;
        for (int i = remainingTeamCount; i < teamsAndGames.length; i++) {
            String[] matchup = teamsAndGames[i].split("-");
            int team1 = getTeamNumber(matchup[0]);
            int team2 = getTeamNumber(matchup[1]);

            flowNetwork.addEdge(new FlowEdge(sourceVertex, i, gamesVersus[team1][team2]));
        }
    }

    private int getTeamNumber(String team) {
        for (int i = 0; i < teamCount; i++) {
            if (teams[i].equals(team)) {
                return i;
            }
        }
        throw new IllegalArgumentException(team + " not found");
    }

    private int getTeamsAndGamesNumber(String team) {
        for (int i = 0; i < (teamCount - 1); i++) {
            if (teamsAndGames[i].equals(team)) {
                return i;
            }
        }
        throw new IllegalArgumentException(team + " not found");
    }

    private void validateTeamIsNotNull(String team) {
        if (team == null) {
            throw new IllegalArgumentException("Team name cannot be null");
        }
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}