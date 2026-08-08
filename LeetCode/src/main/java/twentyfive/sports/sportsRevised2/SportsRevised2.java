package main.java.twentyfive.sports.sportsRevised2;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/*
 * ============================================================
 * API
 * ============================================================
 *
 * GET
 * /v1/leagues/{leagueId}/seasons/{seasonId}/players/
 * {playerId1}/compare/{playerId2}?statistic=GOALS
 *
 * Example:
 *
 * GET
 * /v1/leagues/nfl/seasons/2025/players/
 * 20/compare/30?statistic=TOUCHDOWNS
 *
 *
 * Context:
 *
 * leagueId
 * seasonId
 * playerId1
 * playerId2
 * statistic
 *
 *
 * The important idea:
 *
 * We are no longer comparing players globally.
 *
 * We are comparing:
 *
 * player + league + season + statistic
 *
 * if no league or season dont need build key
 * ============================================================
 */


/*
 * ============================================================
 * LEAGUE
 * ============================================================
 */
/*class League {

    private final String leagueId;
    private final String leagueName;
    private final Sport sport;


    public League(
            String leagueId,
            String leagueName,
            Sport sport) {

        this.leagueId = leagueId;
        this.leagueName = leagueName;
        this.sport = sport;
    }


    public String getLeagueId() {
        return leagueId;
    }


    public Sport getSport() {
        return sport;
    }
}
*/

/*
 * ============================================================
 * SEASON
 * ============================================================
 */
/*
class Season {

    private final String seasonId;
    private final String leagueId;
    private final String name;


    public Season(
            String seasonId,
            String leagueId,
            String name) {

        this.seasonId = seasonId;
        this.leagueId = leagueId;
        this.name = name;
    }


    public String getSeasonId() {
        return seasonId;
    }


    public String getLeagueId() {
        return leagueId;
    }
}
*/

/*
 * ============================================================
 * SPORT
 * ============================================================
 */
enum Sport {

    SOCCER,
    BASKETBALL,
    TENNIS,
    FOOTBALL
}


/*
 * ============================================================
 * STATISTIC
 * ============================================================
 */
enum Statistic {

    // Soccer
    GOALS,
    ASSISTS,
    CORNERS,
    FOULS,
    PENALTY,
    YELLOW_CARD,
    RED_CARD,

    // Basketball
    POINTS,
    REBOUNDS,

    // Tennis
    ACES,
    DOUBLE_FAULTS,

    // Football
    TOUCHDOWNS,
    RUSHING_YARDS
}


/*
 * ============================================================
 * PLAYER STATISTICS
 * ============================================================
 *
 * Statistics are now scoped to:
 *
 * playerId
 * leagueId
 * seasonId
 * sport
 *
 * Example:
 *
 * Player 20
 * NFL
 * 2025
 * TOUCHDOWNS = 15
 *
 *
 * The same player can have:
 *
 * Player 20
 * NFL
 * 2024
 * TOUCHDOWNS = 11
 *
 * Therefore season/league are part of the context.
 *
 */
class PlayerStatistics {

    private final int playerId;

    private final String leagueId;

    private final String seasonId;

    private final Sport sport;


    /*
     * Statistic → value
     */
    private final Map<Statistic, Integer> playerStats =
            new ConcurrentHashMap<>();


    /*
     * Timestamp of the latest update.
     */
    private long lastUpdated;


    /*
     * Used when multiple pieces of state need to be
     * updated atomically.
     *
     * Example:
     *
     * playerStats
     * +
     * lastUpdated
     *
     */
    private final ReentrantReadWriteLock lock =
            new ReentrantReadWriteLock();


    public PlayerStatistics(
            int playerId,
            String leagueId,
            String seasonId,
            Sport sport,
            Map<Statistic, Integer> playerStats) {

        this.playerId = playerId;
        this.leagueId = leagueId;
        this.seasonId = seasonId;
        this.sport = sport;

        this.playerStats.putAll(playerStats);

        this.lastUpdated =
                System.currentTimeMillis();
    }


    public int getPlayerId() {
        return playerId;
    }


    public String getLeagueId() {
        return leagueId;
    }


    public String getSeasonId() {
        return seasonId;
    }


    public Sport getSport() {
        return sport;
    }


    /*
     * Simple read.
     *
     * ConcurrentHashMap.get() is already thread-safe.
     *
     * No ReadWriteLock needed when reading one independent
     * statistic.
     */
    public Integer getPlayerStat(
            Statistic statistic) {

        Integer statisticValue =
                playerStats.get(statistic);


        if (statisticValue == null) {

            throw new StatisticNotFoundException(
                    "Statistic not found "
                            + statistic
            );
        }


        return statisticValue;
    }


    /*
     * ========================================================
     * SINGLE STATISTIC UPDATE
     * ========================================================
     *
     * compute() gives us atomic read-modify-write for
     * one map key.
     *
     * Example:
     *
     * GOALS = 10
     *
     * compute()
     *
     * GOALS = 11
     */
    public void incrementPlayerStat(
            Statistic statistic) {

        playerStats.compute(
                statistic,
                (key, current) ->
                        current == null
                                ? 1
                                : current + 1
        );
    }


    /*
     * ========================================================
     * MULTIPLE STATE UPDATE
     * ========================================================
     *
     * Now we update:
     *
     * 1. statistic
     * 2. timestamp
     *
     * Both should happen as one business operation.
     *
     * Therefore use the broader write lock.
     */
    public void incrementPlayerStatMultipleStates(
            Statistic statistic) {

        lock.writeLock().lock();

        try {

            Integer current =
                    playerStats.get(statistic);


            if (current == null) {
                current = 0;
            }


            playerStats.put(
                    statistic,
                    current + 1
            );


            lastUpdated =
                    System.currentTimeMillis();

        }
        finally {

            lock.writeLock().unlock();
        }
    }


    /*
     * Read lock is useful if we need a consistent snapshot
     * involving multiple pieces of state.
     */
    public Integer getPlayerStatReentrant(
            Statistic statistic) {

        lock.readLock().lock();

        try {

            Integer statisticValue =
                    playerStats.get(statistic);


            if (statisticValue == null) {

                throw new StatisticNotFoundException(
                        "Statistic not found "
                                + statistic
                );
            }


            return statisticValue;
        }
        finally {

            lock.readLock().unlock();
        }
    }
}


/*
 * ============================================================
 * REPOSITORY
 * ============================================================
 *
 * Previously:
 *
 * playerId → PlayerStatistics
 *
 *
 * Now:
 *
 * leagueId + seasonId + playerId
 *             ↓
 *       PlayerStatistics
 *
 *
 * Because the same player can have different statistics
 * in different leagues/seasons.
 *
 */
class PlayerStatisticsRepository {

    /*
     * Composite key:
     *
     * leagueId + seasonId + playerId
     */
    private final Map<String, PlayerStatistics>
            playerStatisticsData =
            new ConcurrentHashMap<>();


    private String buildKey(
            String leagueId,
            String seasonId,
            int playerId) {

        return leagueId
                + ":"
                + seasonId
                + ":"
                + playerId;
    }


    /*
     * ========================================================
     * GET
     * ========================================================
     */
    public PlayerStatistics getPlayerStatistics(
            String leagueId,
            String seasonId,
            int playerId) {

        String key =
                buildKey(
                        leagueId,
                        seasonId,
                        playerId
                );


        PlayerStatistics playerStatistics =
                playerStatisticsData.get(key);


        if (playerStatistics == null) {

            throw new PlayerNotFoundException(
                    "Player " + playerId
                            + " not found for league "
                            + leagueId
                            + " season "
                            + seasonId
            );
        }


        return playerStatistics;
    }


    /*
     * ========================================================
     * SAVE
     * ========================================================
     */
    public void savePlayerStatistics(
            PlayerStatistics playerStatistics) {

        String key =
                buildKey(
                        playerStatistics.getLeagueId(),
                        playerStatistics.getSeasonId(),
                        playerStatistics.getPlayerId()
                );


        playerStatisticsData.putIfAbsent(
                key,
                playerStatistics
        );
    }
}


/*
 * ============================================================
 * SPORT STATISTICS REGISTRY
 * ============================================================
 *
 * This answers:
 *
 * "Is this statistic valid for this sport?"
 *
 */
class SportStatisticsRegistry {

    private static final Map<Sport, java.util.Set<Statistic>>
            SUPPORTED_STATISTICS =
            Map.of(

                    Sport.SOCCER,
                    java.util.Set.of(
                            Statistic.GOALS,
                            Statistic.ASSISTS,
                            Statistic.CORNERS,
                            Statistic.FOULS,
                            Statistic.PENALTY,
                            Statistic.YELLOW_CARD,
                            Statistic.RED_CARD
                    ),

                    Sport.BASKETBALL,
                    java.util.Set.of(
                            Statistic.POINTS,
                            Statistic.REBOUNDS,
                            Statistic.ASSISTS
                    ),

                    Sport.TENNIS,
                    java.util.Set.of(
                            Statistic.ACES,
                            Statistic.DOUBLE_FAULTS
                    ),

                    Sport.FOOTBALL,
                    java.util.Set.of(
                            Statistic.TOUCHDOWNS,
                            Statistic.RUSHING_YARDS
                    )
            );


    public static boolean isSupported(
            Sport sport,
            Statistic statistic) {

        return SUPPORTED_STATISTICS
                .getOrDefault(sport, java.util.Set.of())
                .contains(statistic);
    }
}


/*
 * ============================================================
 * COMPARISON STRATEGY
 * ============================================================
 *
 * We are keeping Strategy statistic-based.
 *
 * Why?
 *
 * GOALS       → higher is better
 * POINTS      → higher is better
 * TOUCHDOWNS  → higher is better
 *
 * RED_CARD    → lower is better
 * FOULS       → lower is better
 *
 * The sport itself isn't determining the comparison algorithm.
 *
 */
interface ComparisonStrategy {

    int determineWinner(
            int player1Id,
            int player2Id,
            int player1Value,
            int player2Value);
}


/*
 * Higher value wins.
 */
class HigherIsBetterStrategy
        implements ComparisonStrategy {

    @Override
    public int determineWinner(
            int player1Id,
            int player2Id,
            int player1Value,
            int player2Value) {

        if (player1Value == player2Value) {
            return -1;
        }


        return player1Value > player2Value
                ? player1Id
                : player2Id;
    }
}


/*
 * Lower value wins.
 */
class LowerIsBetterStrategy
        implements ComparisonStrategy {

    @Override
    public int determineWinner(
            int player1Id,
            int player2Id,
            int player1Value,
            int player2Value) {

        if (player1Value == player2Value) {
            return -1;
        }


        return player1Value < player2Value
                ? player1Id
                : player2Id;
    }
}


/*
 * ============================================================
 * STRATEGY FACTORY
 * ============================================================
 */
class ComparisonStrategyFactory {

    public static ComparisonStrategy
    getStrategy(Statistic statistic) {

        switch (statistic) {

            case GOALS:
            case ASSISTS:
            case POINTS:
            case REBOUNDS:
            case ACES:
            case TOUCHDOWNS:
            case RUSHING_YARDS:

                return new HigherIsBetterStrategy();


            case RED_CARD:
            case FOULS:
            case YELLOW_CARD:
            case DOUBLE_FAULTS:

                return new LowerIsBetterStrategy();


            default:

                throw new IllegalArgumentException(
                        "No comparison strategy for "
                                + statistic
                );
        }
    }
}


/*
 * ============================================================
 * COMPARISON RESULT
 * ============================================================
 */
class PlayerComparisonResult {

    private final int player1Id;
    private final int player2Id;

    private final String leagueId;
    private final String seasonId;

    private final Statistic statistic;

    private final int player1StatisticValue;
    private final int player2StatisticValue;

    /*
     * -1 means tie.
     */
    private final int winner;


    public PlayerComparisonResult(
            int player1Id,
            int player2Id,
            String leagueId,
            String seasonId,
            Statistic statistic,
            int player1StatisticValue,
            int player2StatisticValue,
            int winner) {

        this.player1Id = player1Id;
        this.player2Id = player2Id;

        this.leagueId = leagueId;
        this.seasonId = seasonId;

        this.statistic = statistic;

        this.player1StatisticValue =
                player1StatisticValue;

        this.player2StatisticValue =
                player2StatisticValue;

        this.winner = winner;
    }


    @Override
    public String toString() {

        return "league="
                + leagueId
                + ", season="
                + seasonId
                + ", statistic="
                + statistic
                + ", player1="
                + player1StatisticValue
                + ", player2="
                + player2StatisticValue
                + ", winner="
                + winner;
    }
}


/*
 * ============================================================
 * COMPARISON SERVICE
 * ============================================================
 */
class PlayerComparisonService {

    private final PlayerStatisticsRepository
            playerStatisticsRepository;


    public PlayerComparisonService(
            PlayerStatisticsRepository playerStatisticsRepository) {

        this.playerStatisticsRepository =
                playerStatisticsRepository;
    }


    /*
     * ========================================================
     * COMPARE TWO PLAYERS
     * ========================================================
     *
     * Context:
     *
     * leagueId
     * seasonId
     * playerId1
     * playerId2
     * statistic
     *
     */
    public PlayerComparisonResult comparePlayers(
            String leagueId,
            String seasonId,
            int playerId1,
            int playerId2,
            Statistic statistic) {


        /*
         * Same player should not be compared with itself.
         */
        if (playerId1 == playerId2) {

            throw new IllegalArgumentException(
                    "Players must be different"
            );
        }


        /*
         * Both repository calls are independent.
         *
         * Therefore they can run concurrently.
         */
        CompletableFuture<PlayerStatistics>
                player1StatisticsFuture =
                CompletableFuture.supplyAsync(
                        () ->
                                playerStatisticsRepository
                                        .getPlayerStatistics(
                                                leagueId,
                                                seasonId,
                                                playerId1
                                        )
                );


        CompletableFuture<PlayerStatistics>
                player2StatisticsFuture =
                CompletableFuture.supplyAsync(
                        () ->
                                playerStatisticsRepository
                                        .getPlayerStatistics(
                                                leagueId,
                                                seasonId,
                                                playerId2
                                        )
                );


        /*
         * Wait for both.
         */
        CompletableFuture.allOf(
                player1StatisticsFuture,
                player2StatisticsFuture
        ).join();


        /*
         * Get actual results.
         */
        PlayerStatistics player1Statistics =
                player1StatisticsFuture.join();

        PlayerStatistics player2Statistics =
                player2StatisticsFuture.join();


        /*
         * Both statistics should belong to the same sport.
         */
        if (player1Statistics.getSport()
                != player2Statistics.getSport()) {

            throw new IllegalArgumentException(
                    "Players must belong to the same sport"
            );
        }


        /*
         * Validate statistic for sport.
         */
        if (!SportStatisticsRegistry.isSupported(
                player1Statistics.getSport(),
                statistic)) {

            throw new IllegalArgumentException(
                    "Statistic "
                            + statistic
                            + " is not supported for "
                            + player1Statistics.getSport()
            );
        }


        /*
         * Get values.
         */
        int player1Value =
                player1Statistics.getPlayerStat(
                        statistic
                );

        int player2Value =
                player2Statistics.getPlayerStat(
                        statistic
                );


        /*
         * Select comparison strategy based on metric.
         *
         * GOALS → HigherIsBetterStrategy
         * RED_CARD → LowerIsBetterStrategy
         */
        ComparisonStrategy strategy =
                ComparisonStrategyFactory
                        .getStrategy(statistic);


        /*
         * Let strategy determine the winner.
         */
        int winner =
                strategy.determineWinner(
                        playerId1,
                        playerId2,
                        player1Value,
                        player2Value
                );


        return new PlayerComparisonResult(
                playerId1,
                playerId2,
                leagueId,
                seasonId,
                statistic,
                player1Value,
                player2Value,
                winner
        );
    }
}


/*
 * ============================================================
 * EXCEPTIONS
 * ============================================================
 */
class PlayerNotFoundException
        extends RuntimeException {

    public PlayerNotFoundException(
            String message) {

        super(message);
    }
}


class StatisticNotFoundException
        extends RuntimeException {

    public StatisticNotFoundException(
            String message) {

        super(message);
    }
}


/*
 * ============================================================
 * MAIN
 * ============================================================
 */
public class SportsRevised2 {

    public static void main(String[] args) {

        PlayerStatisticsRepository
                playerStatisticsRepository =
                new PlayerStatisticsRepository();


        /*
         * ----------------------------------------------------
         * NFL 2025
         * ----------------------------------------------------
         */
        Map<Statistic, Integer> player1Stats =
                new HashMap<>();

        player1Stats.put(
                Statistic.TOUCHDOWNS,
                15
        );


        Map<Statistic, Integer> player2Stats =
                new HashMap<>();

        player2Stats.put(
                Statistic.TOUCHDOWNS,
                12
        );


        PlayerStatistics player1Statistics =
                new PlayerStatistics(
                        20,
                        "NFL",
                        "2025",
                        Sport.FOOTBALL,
                        player1Stats
                );


        PlayerStatistics player2Statistics =
                new PlayerStatistics(
                        30,
                        "NFL",
                        "2025",
                        Sport.FOOTBALL,
                        player2Stats
                );


        playerStatisticsRepository
                .savePlayerStatistics(
                        player1Statistics
                );


        playerStatisticsRepository
                .savePlayerStatistics(
                        player2Statistics
                );


        /*
         * ----------------------------------------------------
         * Comparison Service
         * ----------------------------------------------------
         */
        PlayerComparisonService comparisonService =
                new PlayerComparisonService(
                        playerStatisticsRepository
                );


        /*
         * Compare:
         *
         * NFL
         * 2025
         * Player 20
         * Player 30
         * TOUCHDOWNS
         */
        PlayerComparisonResult result =
                comparisonService.comparePlayers(
                        "NFL",
                        "2025",
                        20,
                        30,
                        Statistic.TOUCHDOWNS
                );


        System.out.println(result);
    }
}


/*
 * ============================================================
 * CONTROLLER
 * ============================================================
 *
 * REST:
 *
 * GET
 * /v1/leagues/{leagueId}/seasons/{seasonId}/players/
 * {playerId1}/compare/{playerId2}?statistic=GOALS
 *
 *
 * Spring code:
 *
 *
 * @RestController
 * @RequestMapping("/v1/leagues")
 * public class PlayerComparisonController {
 *
 *     private final PlayerComparisonService comparisonService;
 *
 *
 *     public PlayerComparisonController(
 *             PlayerComparisonService comparisonService) {
 *
 *         this.comparisonService = comparisonService;
 *     }
 *
 *
 *     @GetMapping(
 *         "/{leagueId}/seasons/{seasonId}/players/"
 *         + "{playerId1}/compare/{playerId2}"
 *     )
 *     public PlayerComparisonResult comparePlayers(
 *
 *             @PathVariable String leagueId,
 *
 *             @PathVariable String seasonId,
 *
 *             @PathVariable int playerId1,
 *
 *             @PathVariable int playerId2,
 *
 *             @RequestParam Statistic statistic) {
 *
 *
 *         return comparisonService.comparePlayers(
 *                 leagueId,
 *                 seasonId,
 *                 playerId1,
 *                 playerId2,
 *                 statistic
 *         );
 *     }
 * }
 *
 *
 * ============================================================
 * FINAL REQUEST
 * ============================================================
 *
 * GET
 * /v1/leagues/NFL/seasons/2025/players/
 * 20/compare/30?statistic=TOUCHDOWNS
 *
 *
 * Means:
 *
 * Compare player 20 vs player 30
 *
 * in:
 *
 * League = NFL
 * Season = 2025
 *
 * using:
 *
 * Statistic = TOUCHDOWNS
 *
 *
 * ============================================================
 * DESIGN
 * ============================================================
 *
 * League
 *     ↓
 * Season
 *     ↓
 * PlayerStatistics
 *     ├── playerId
 *     ├── leagueId
 *     ├── seasonId
 *     ├── sport
 *     └── Map<Statistic, Integer>
 *
 *
 * Repository key:
 *
 * (leagueId, seasonId, playerId)
 *              ↓
 *       PlayerStatistics
 *
 *
 * SportStatisticsRegistry
 *     ↓
 * Which statistics are valid?
 *
 *
 * ComparisonStrategy
 *     ↓
 * How should this metric be compared?
 *
 *
 * ============================================================
 * IMPORTANT INTERVIEW POINT
 * ============================================================
 *
 * We added League + Season because statistics are now scoped
 * to a particular competition and time period.
 *
 * We did NOT introduce Strategy for League or Season because
 * they don't change the comparison algorithm.
 *
 * Statistic determines comparison behavior:
 *
 * GOALS       → higher is better
 * POINTS      → higher is better
 * RED_CARD    → lower is better
 *
 * If the interviewer later says that different leagues have
 * different comparison rules, THEN we could make Strategy
 * league-based.
 *
 */
