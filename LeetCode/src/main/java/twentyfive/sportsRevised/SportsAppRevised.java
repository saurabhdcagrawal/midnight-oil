package main.java.twentyfive.sportsRevised;


/*
 * ============================================================
 * SPORTS PLAYER COMPARISON — LLD
 * ============================================================
 *
 * 1. Compare two players:
 *
 * GET /v1/players/{player1Id}/compare/{player2Id}?statistic=GOALS
 *
 * Path variables:
 * - player1Id
 * - player2Id
 *
 * Request parameter:
 * - statistic
 *
 *
 * 2. Alternative API:
 *
 * GET /v1/players/compare?player1Id=20&player2Id=30&statistic=GOALS
 *
 * Here player1Id and player2Id are @RequestParam.
 *
 *
 * 3. Multiple players:
 *
 * GET /v1/players/compare?playerIds=20,30,40&statistic=GOALS
 *
 * Here playerIds can be represented as:
 *
 * @RequestParam List<Integer> playerIds
 *
 * We can compare multiple players and return:
 *
 * - winning statistic value
 * - all players having that winning value
 *
 *
 * 4. Validation:
 *
 * If the same player is passed twice:
 *
 * throw new IllegalArgumentException(
 *     "Players must be different"
 * );
 *
 * This can be mapped to HTTP 400 Bad Request.
 *
 *
 * 5. Multiple sports:
 *
 * Sport is kept inside PlayerStatistics for this simplified LLD.
 *
 * PlayerStatistics:
 *
 * playerId
 * sport
 * statistics
 *
 * This avoids introducing PlayerRepository only to retrieve the sport.
 *
 * ============================================================
 *
 * /v1/leagues/{leagueId}/seasons/{seasonId}/players/{playerId1}/compare/{playerId2}?statistic=GOALS
 * playerStatistics will have sport,league,season,playerStats i.e. Map<Statistics,Integer>
 getPlayerStatistics(String leagueId, String SeasonId,
 */


import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/*
 * ============================================================
 * PLAYER COMPARISON SERVICE
 * ============================================================
 *
 * Responsibility:
 *
 * - Fetch player statistics
 * - Validate business rules
 * - Compare requested statistic
 * - Return comparison result
 *
 */
class PlayerComparisonService {

    private final PlayerStatisticsRepository playerStatisticsRepository;


    public PlayerComparisonService(
            PlayerStatisticsRepository playerStatisticsRepository) {

        this.playerStatisticsRepository = playerStatisticsRepository;
    }


    /*
     * ========================================================
     * Compare two players
     * ========================================================
     *
     * Example:
     *
     * Player 1 → GOALS = 10
     * Player 2 → GOALS = 14
     *
     * Result:
     *
     * winner = player 2
     *
     *
     * We can fetch the two players concurrently because
     * the repository calls are independent.
     *
     */
    public PlayerComparisonResult comparePlayers(
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
         * ----------------------------------------------------
         * Fetch Player 1 asynchronously
         * ----------------------------------------------------
         *
         * supplyAsync()
         *     ↓
         * CompletableFuture<PlayerStatistics>
         *
         */
        CompletableFuture<PlayerStatistics> player1StatisticsFuture =
                CompletableFuture.supplyAsync(
                        () -> playerStatisticsRepository
                                .getPlayerStatistics(playerId1)
                );


        /*
         * ----------------------------------------------------
         * Fetch Player 2 asynchronously
         * ----------------------------------------------------
         */
        CompletableFuture<PlayerStatistics> player2StatisticsFuture =
                CompletableFuture.supplyAsync(
                        () -> playerStatisticsRepository
                                .getPlayerStatistics(playerId2)
                );


        /*
         * Wait for both independent operations.
         *
         * allOf() returns:
         *
         * CompletableFuture<Void>
         *
         * It does NOT return PlayerStatistics.
         *
         * Therefore we still call join() on each individual
         * future below to retrieve the actual result.
         */
        CompletableFuture.allOf(
                player1StatisticsFuture,
                player2StatisticsFuture
        ).join();


        /*
         * Extract actual PlayerStatistics from the futures.
         */
        PlayerStatistics player1Statistics =
                player1StatisticsFuture.join();

        PlayerStatistics player2Statistics =
                player2StatisticsFuture.join();


        /*
         * ----------------------------------------------------
         * Validate players belong to the same sport.
         * ----------------------------------------------------
         *
         * Since Sport is stored in PlayerStatistics,
         * we don't need a separate PlayerRepository for
         * this simplified design.
         */
        if (player1Statistics.getSport()
                != player2Statistics.getSport()) {

            throw new IllegalArgumentException(
                    "Players must belong to the same sport"
            );
        }


        /*
         * ----------------------------------------------------
         * Validate statistic belongs to the sport.
         * ----------------------------------------------------
         *
         * Example:
         *
         * SOCCER + GOALS       → valid
         * BASKETBALL + POINTS  → valid
         *
         * SOCCER + REBOUNDS    → invalid
         */
        if (!SportStatisticsRegistry.isSupported(
                player1Statistics.getSport(),
                statistic)) {

            throw new IllegalArgumentException(
                    "Statistic " + statistic +
                            " is not supported for sport " +
                            player1Statistics.getSport()
            );
        }


        /*
         * Get requested statistic.
         */
        int player1Value =
                player1Statistics.getPlayerStat(statistic);

        int player2Value =
                player2Statistics.getPlayerStat(statistic);


        /*
         * winner = -1 means tie.
         */
        /*int winner = -1;


        if (player1Value != player2Value) {

            winner = player1Value > player2Value
                    ? playerId1
                    : playerId2;
        }*/

        ComparisonStrategy strategy= ComparisonFactory.getComparisonStrategy(statistic);
        int winner=strategy.determineWinner(playerId1,playerId2,player1Value,player2Value);

        return new PlayerComparisonResult(
                playerId1,
                playerId2,
                statistic,
                player1Value,
                player2Value,
                winner
        );
    }
}


/*
 * ============================================================
 * PLAYER COMPARISON RESULT
 * ============================================================
 */
class PlayerComparisonResult {

    private final int player1Id;
    private final int player2Id;

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
            Statistic statistic,
            int player1StatisticValue,
            int player2StatisticValue,
            int winner) {

        this.player1Id = player1Id;
        this.player2Id = player2Id;

        this.statistic = statistic;

        this.player1StatisticValue =
                player1StatisticValue;

        this.player2StatisticValue =
                player2StatisticValue;

        this.winner = winner;
    }


    @Override
    public String toString() {

        return statistic + " winner=" + winner;
    }
}


/*
 * ============================================================
 * PLAYER STATISTICS REPOSITORY
 * ============================================================
 *
 * Responsibility:
 *
 * Store and retrieve PlayerStatistics.
 *
 *
 * Why ConcurrentHashMap?
 *
 * HashMap is not thread-safe for concurrent reads/writes.
 *
 * Concurrent updates can lead to race conditions and
 * inconsistent state.
 *
 * ConcurrentHashMap protects the map structure.
 *
 * Individual map operations are atomic.
 *
 * However:
 *
 * read → modify → write
 *
 * is NOT automatically atomic as a whole.
 *
 * Example:
 *
 * value = map.get(key)
 * value++
 * map.put(key, value)
 *
 * Two threads can interfere with each other.
 *
 * This is why we use compute() for atomic update of
 * a single map entry.
 *
 */
class PlayerStatisticsRepository {

    /*
     * playerId → PlayerStatistics
     */
    private final Map<Integer, PlayerStatistics>
            playerStatisticsData =
            new ConcurrentHashMap<>();


    /*
     * --------------------------------------------------------
     * READ
     * --------------------------------------------------------
     */
    public PlayerStatistics getPlayerStatistics(int playerId) {

        PlayerStatistics playerStatistics =
                playerStatisticsData.get(playerId);


        if (playerStatistics == null) {

            throw new PlayerNotFoundException(
                    "Player " + playerId +
                            " not found in records"
            );
        }


        return playerStatistics;
    }


    /*
     * --------------------------------------------------------
     * SAVE
     * --------------------------------------------------------
     *
     * putIfAbsent():
     *
     * Insert only if playerId does not already exist.
     */
    public void savePlayerStatistics(
            int playerId,
            PlayerStatistics playerStatistics) {

        playerStatisticsData.putIfAbsent(
                playerId,
                playerStatistics
        );
    }
}


/*
 * ============================================================
 * PLAYER NOT FOUND EXCEPTION
 * ============================================================
 */
class PlayerNotFoundException extends RuntimeException {

    public PlayerNotFoundException(String message) {
        super(message);
    }
}


/*
 * ============================================================
 * STATISTIC NOT FOUND EXCEPTION
 * ============================================================
 */
class StatisticNotFoundException extends RuntimeException {

    public StatisticNotFoundException(String message) {
        super(message);
    }
}


/*
 * ============================================================
 * SPORT
 * ============================================================
 */
enum Sport {

    SOCCER,
    BASKETBALL,
    TENNIS
}


/*
 * ============================================================
 * STATISTIC
 * ============================================================
 *
 * Initially we can use one enum for all sports.
 *
 * The registry below determines which statistic is valid
 * for which sport.
 *
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
    DOUBLE_FAULTS
}


/*
 * ============================================================
 * SPORT STATISTICS REGISTRY
 * ============================================================
 *
 * Responsibility:
 *
 * Determine which statistics are valid for each sport.
 *
 *
 * Map<Sport, Set<Statistic>>
 *
 * Example:
 *
 * SOCCER
 *     → GOALS
 *     → ASSISTS
 *     → CORNERS
 *
 * BASKETBALL
 *     → POINTS
 *     → REBOUNDS
 *     → ASSISTS
 *
 *
 * Set gives approximately O(1) contains() lookup.
 *
 */
class SportStatisticsRegistry {

    private static final Map<Sport, Set<Statistic>>
            SUPPORTED_STATISTICS =
            Map.of(

                    Sport.SOCCER,
                    Set.of(
                            Statistic.GOALS,
                            Statistic.ASSISTS,
                            Statistic.CORNERS,
                            Statistic.FOULS,
                            Statistic.PENALTY,
                            Statistic.YELLOW_CARD,
                            Statistic.RED_CARD
                    ),

                    Sport.BASKETBALL,
                    Set.of(
                            Statistic.POINTS,
                            Statistic.REBOUNDS,
                            Statistic.ASSISTS
                    ),

                    Sport.TENNIS,
                    Set.of(
                            Statistic.ACES,
                            Statistic.DOUBLE_FAULTS
                    )
            );


    public static boolean isSupported(
            Sport sport,
            Statistic statistic) {

        return SUPPORTED_STATISTICS
                .getOrDefault(sport, Set.of())
                .contains(statistic);
    }
}


/*
 * ============================================================
 * PLAYER STATISTICS
 * ============================================================
 *
 * Simplified design:
 *
 * PlayerStatistics contains:
 *
 * playerId
 * sport
 * statistics
 *
 * We keep sport here so that the comparison service does not
 * need a separate PlayerRepository just to find the sport.
 *
 */
class PlayerStatistics {

    private final int playerId;

    private final Sport sport;


    /*
     * Statistic → value
     *
     * Example:
     *
     * GOALS → 10
     * ASSISTS → 5
     */
    private final Map<Statistic, Integer> playerStats =
            new ConcurrentHashMap<>();


    /*
     * Timestamp of the latest update.
     */
    private long lastUpdated;


    /*
     * --------------------------------------------------------
     * ReentrantReadWriteLock
     * --------------------------------------------------------
     *
     * Used when multiple pieces of state must be updated
     * atomically as ONE business operation.
     *
     * Example:
     *
     * GOALS = 10
     * lastUpdated = T1
     *
     * After update:
     *
     * GOALS = 11
     * lastUpdated = T2
     *
     * We don't want another thread to observe:
     *
     * GOALS = 11
     * lastUpdated = T1
     *
     * Therefore the entire update needs a write lock.
     */
    private final ReentrantReadWriteLock lock =
            new ReentrantReadWriteLock();


    public PlayerStatistics(
            int playerId,
            Sport sport,
            Map<Statistic, Integer> playerStats) {

        this.playerId = playerId;
        this.sport = sport;

        /*
         * Copy values into our concurrent map.
         */
        this.playerStats.putAll(playerStats);

        this.lastUpdated = System.currentTimeMillis();
    }


    public Sport getSport() {
        return sport;
    }


    /*
     * ========================================================
     * SIMPLE READ
     * ========================================================
     *
     * ConcurrentHashMap.get() is thread-safe.
     *
     * We do NOT need a ReadWriteLock if we are only reading
     * one independent map value.
     */
    public Integer getPlayerStat(Statistic statistic) {

        Integer statisticValue =
                playerStats.get(statistic);


        if (statisticValue == null) {

            throw new StatisticNotFoundException(
                    "Statistic not found " + statistic
            );
        }


        return statisticValue;
    }


    /*
     * ========================================================
     * OPTION 1 — SYNCHRONIZED
     * ========================================================
     *
     * Simplest solution.
     *
     * synchronized locks the entire PlayerStatistics object.
     *
     * This is coarse-grained locking.
     *
     * Even if two threads update different statistics:
     *
     * Thread 1 → GOALS
     * Thread 2 → ASSISTS
     *
     * Thread 2 must wait for Thread 1.
     */
    public synchronized void
    incrementPlayerStatWithSynchronized(
            Statistic statistic) {

        Integer statisticValue =
                playerStats.get(statistic);


        if (statisticValue == null) {
            statisticValue = 0;
        }


        playerStats.put(
                statistic,
                statisticValue + 1
        );
    }


    /*
     * ========================================================
     * OPTION 2 — COMPUTE
     * ========================================================
     *
     * Fine-grained atomic update for ONE map entry.
     *
     * compute() takes:
     *
     * key + function
     *
     * (key, currentValue) -> newValue
     *
     *
     * Example:
     *
     * GOALS = 10
     *
     * compute(GOALS, ...)
     *
     * current = 10
     *
     * return 11
     *
     *
     * ConcurrentHashMap ensures the computation for that
     * individual key is atomic.
     *
     * This is preferred when ONLY the statistic value needs
     * to be updated.
     */
    public void incrementPlayerStat(
            Statistic statistic) {

        playerStats.compute(
                statistic,
                (key, current) -> {

                    return current == null
                            ? 1
                            : current + 1;
                }
        );
    }


    /*
     * ========================================================
     * OPTION 3 — MULTIPLE STATE UPDATE
     * ========================================================
     *
     * Now suppose we update:
     *
     * 1. playerStats
     * 2. lastUpdated
     *
     * These two changes must happen together.
     *
     * compute() only makes the update to ONE map entry atomic.
     *
     * It does NOT protect lastUpdated.
     *
     * Therefore we need a broader lock.
     *
     * Use:
     *
     * ReentrantReadWriteLock
     *
     * because the entire business operation needs to be
     * atomic.
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


            /*
             * Update timestamp as part of the same
             * critical section.
             */
            lastUpdated =
                    System.currentTimeMillis();

        }
        finally {

            lock.writeLock().unlock();
        }
    }


    /*
     * ========================================================
     * READ LOCK
     * ========================================================
     *
     * ReadWriteLock behavior:
     *
     * READ + READ
     * → both can proceed concurrently
     *
     * WRITE + READ
     * → read waits for write
     *
     * READ + WRITE
     * → write waits for read
     *
     * WRITE + WRITE
     * → second write waits for first
     *
     *
     * Use this when the read needs a consistent snapshot
     * involving multiple related pieces of state.
     */
    public Integer getPlayerStatReentrant(
            Statistic statistic) {

        lock.readLock().lock();

        try {

            Integer statisticValue =
                    playerStats.get(statistic);


            if (statisticValue == null) {

                throw new StatisticNotFoundException(
                        "Statistic not found " + statistic
                );
            }


            /*
             * If we also needed to return lastUpdated,
             * reading both values under the same read lock
             * gives us a consistent snapshot.
             */
            return statisticValue;
        }
        finally {

            lock.readLock().unlock();
        }
    }
}


/*
 * ============================================================
 * MAIN
 * ============================================================
 */
public class SportsAppRevised {

    public static void main(String[] args) {


        PlayerStatisticsRepository
                playerStatisticsRepository =
                new PlayerStatisticsRepository();


        /*
         * ----------------------------------------------------
         * Player 1
         * ----------------------------------------------------
         */
        Map<Statistic, Integer> player1Stats =
                new HashMap<>();

        player1Stats.put(
                Statistic.GOALS,
                10
        );


        /*
         * ----------------------------------------------------
         * Player 2
         * ----------------------------------------------------
         */
        Map<Statistic, Integer> player2Stats =
                new HashMap<>();

        player2Stats.put(
                Statistic.GOALS,
                14
        );


        /*
         * Both players are soccer players.
         */
        PlayerStatistics player1Statistics =
                new PlayerStatistics(
                        1,
                        Sport.SOCCER,
                        player1Stats
                );


        PlayerStatistics player2Statistics =
                new PlayerStatistics(
                        2,
                        Sport.SOCCER,
                        player2Stats
                );


        /*
         * Save statistics.
         */
        playerStatisticsRepository.savePlayerStatistics(
                1,
                player1Statistics
        );

        playerStatisticsRepository.savePlayerStatistics(
                2,
                player2Statistics
        );


        /*
         * Comparison service.
         */
        PlayerComparisonService comparisonService =
                new PlayerComparisonService(
                        playerStatisticsRepository
                );


        /*
         * Compare GOALS.
         */
        PlayerComparisonResult result =
                comparisonService.comparePlayers(
                        1,
                        2,
                        Statistic.GOALS
                );


        System.out.println(
                result.toString()
        );
    }
}


/*
 * ============================================================
 * CONTROLLER — CONCEPTUAL SPRING BOOT VERSION
 * ============================================================
 *
 * REST endpoint:
 *
 * GET /v1/players/{playerId1}/compare/{playerId2}
 *     ?statistic=GOALS
 *
 *
 * Path variables:
 *
 * @PathVariable int playerId1
 * @PathVariable int playerId2
 *
 *
 * Request parameter:
 *
 * @RequestParam Statistic statistic
 *
 *
 * If invalid player IDs / statistic / same players are passed,
 * service can throw IllegalArgumentException and the controller
 * can map it to HTTP 400 Bad Request.
 *
 *
 * Example:
 *
 * @RestController
 * @RequestMapping("/v1/players")
 * public class PlayerComparisonController {
 *
 *     private final PlayerComparisonService comparisonService;
 *
 *     public PlayerComparisonController(
 *             PlayerComparisonService comparisonService) {
 *
 *         this.comparisonService = comparisonService;
 *     }
 *
 *     @GetMapping("/{playerId1}/compare/{playerId2}")
 *     public PlayerComparisonResult comparePlayers(
 *             @PathVariable int playerId1,
 *             @PathVariable int playerId2,
 *             @RequestParam Statistic statistic) {
 *
 *         return comparisonService.comparePlayers(
 *                 playerId1,
 *                 playerId2,
 *                 statistic
 *         );
 *     }
 * }
 *
 *
 * ============================================================
 * Alternative endpoint:
 * ============================================================
 *
 * GET /v1/players/compare
 *     ?playerIds=20,30,40
 *     &statistic=GOALS
 *
 * Controller can accept:
 *
 * @RequestParam List<Integer> playerIds
 *
 * Then:
 *
 * comparePlayers(playerIds, statistic)
 *
 * can find:
 *
 * - highest statistic value
 * - all players having that value
 *
 * Example:
 *
 * Player 20 → 10 goals
 * Player 30 → 14 goals
 * Player 40 → 14 goals
 *
 * Result:
 *
 * winningValue = 14
 * winners = [30, 40]
 *
 * ============================================================
 */


/*
 * ============================================================
 * INTERVIEW SUMMARY
 * ============================================================
 *
 * HashMap
 * → Not thread-safe for concurrent access.
 *
 * ConcurrentHashMap
 * → Protects concurrent map operations.
 *
 * putIfAbsent()
 * → Atomic insert-if-absent.
 *
 * compute()
 * → Atomic read-modify-write for ONE map key.
 *
 * synchronized
 * → Simple coarse-grained locking.
 *
 * ReentrantReadWriteLock
 * → Useful when multiple pieces of state must be treated
 *   as one atomic business operation.
 *
 *
 * Example:
 *
 * compute()
 * → GOALS++
 *
 * ReentrantReadWriteLock
 * → GOALS++ AND lastUpdated = now
 *
 *
 * ReadWriteLock:
 *
 * read + read
 * → concurrent
 *
 * read + write
 * → write waits
 *
 * write + read
 * → read waits
 *
 * write + write
 * → second write waits
 *
 *
 * Multi-sport:
 *
 * PlayerStatistics
 * → playerId
 * → sport
 * → Map<Statistic, Integer>
 *
 * SportStatisticsRegistry
 * → Map<Sport, Set<Statistic>>
 *
 * This simplified design avoids a PlayerRepository because we
 * only need player identity + sport + statistics for this
 * particular comparison use case.
 *
 * If later the system needs player name, team, age, profile,
 * etc., we can introduce a separate Player entity and
 * PlayerRepository.
 *
 * ============================================================
 */

interface ComparisonStrategy{
    public int determineWinner(int playerId1,int playerId2, int player1Statistic, int player2Statistic);
}

class HigherIsBetter implements ComparisonStrategy{

    @Override
    public int determineWinner(int playerId1,int playerId2, int player1Statistic, int player2Statistic){
            int winner=-1;
            if(player1Statistic!=player2Statistic){
                winner=player1Statistic>player2Statistic?playerId1:playerId2;
            }
            return winner;
    }

}


class LowerIsBetter implements ComparisonStrategy{

    @Override
    public int determineWinner(int playerId1,int playerId2, int player1Statistic, int player2Statistic){
        int winner=-1;
        if(player1Statistic!=player2Statistic){
            winner=player1Statistic<player2Statistic?playerId1:playerId2;
        }
        return winner;
    }

}

 class ComparisonFactory {

    public static ComparisonStrategy getComparisonStrategy(Statistic statistic){

        switch (statistic){
            case GOALS:
            case ASSISTS:
            return  new HigherIsBetter();

            default:
                return  new LowerIsBetter();
        }

    }

}