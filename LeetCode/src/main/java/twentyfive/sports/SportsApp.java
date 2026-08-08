package main.java.twentyfive.sports;

/* GET /v1/players/{player1Id}/compare/{player2Id}?statistic=GOALS */
//path variable playerId, pathvariable player2Id,  Requestparam

//GET /v1/players/compare?player1Id=20&player2Id=30&statistics=GOALS
//GET /v1/players/compare?playerId=20,30,40&statistics=GOALS
//RequestParam is List<String> here
//  throw new IllegalArgumentException(
//            "Players must be different"); & 400 bad request

//multiple players..compare each winning value and winner


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class PlayerComparisonService{

    main.java.twentyfive.sportsRevised.PlayerStatisticsRepository playerStatisticsRepository;
    public PlayerComparisonService(main.java.twentyfive.sportsRevised.PlayerStatisticsRepository playerStatisticsRepository){
        this.playerStatisticsRepository=playerStatisticsRepository;
    }

    public main.java.twentyfive.sportsRevised.PlayerComparisonResult comparePlayers(int playerId1, int playerId2, main.java.twentyfive.sportsRevised.Statistic statistic){
/*PlayerStatistics player1Statistics= playerStatisticsRepository.getPlayerStatistics(playerId1);
        PlayerStatistics player2Statistics= playerStatisticsRepository.getPlayerStatistics(playerId2); */
        //we can use completable future here..


        CompletableFuture<main.java.twentyfive.sportsRevised.PlayerStatistics> player1StatisticsFuture= CompletableFuture.supplyAsync(()->playerStatisticsRepository.getPlayerStatistics(playerId1));
        CompletableFuture<main.java.twentyfive.sportsRevised.PlayerStatistics> player2StatisticsFuture= CompletableFuture.supplyAsync(()->playerStatisticsRepository.getPlayerStatistics(playerId2));
        CompletableFuture.allOf(player1StatisticsFuture,player2StatisticsFuture).join();

        main.java.twentyfive.sportsRevised.PlayerStatistics player1Statistics= player1StatisticsFuture.join();
        main.java.twentyfive.sportsRevised.PlayerStatistics player2Statistics= player2StatisticsFuture.join();

        int player1Value=player1Statistics.getPlayerStat(statistic);
        int player2Value=player2Statistics.getPlayerStat(statistic);
        int winner=-1;
        if(player1Value!=player2Value){
            winner= player1Value>player2Value?playerId1:playerId2;
        }
        return  new main.java.twentyfive.sportsRevised.PlayerComparisonResult(playerId1,playerId2,statistic,player1Value,player2Value,winner);
    }

}


class PlayerComparisonResult{
    private final int player1Id;
    private final int player2Id;
    private final main.java.twentyfive.sportsRevised.Statistic statistic;
    private final int player1StatisticValue;
    private final int player2StatisticValue;
    private final int winner;

    public PlayerComparisonResult(int player1Id, int player2Id, main.java.twentyfive.sportsRevised.Statistic statistic, int player1StatisticValue, int player2StatisticValue, int winner){
        this.player1Id=player1Id;
        this.player2Id=player2Id;
        this.statistic=statistic;
        this.player1StatisticValue=player1StatisticValue;
        this.player2StatisticValue=player2StatisticValue;
        this.winner=winner;
    }
    @Override
    public String toString(){
        return (statistic+ " "+winner);
    }
}


class PlayerStatisticsRepository{
    //playerId,Stats
    //Map<Integer,PlayerStatistics> playerStatisticsData= new HashMap<>();

    Map<Integer, main.java.twentyfive.sportsRevised.PlayerStatistics> playerStatisticsData= new ConcurrentHashMap<>();


    //Do we need constructor here

    public main.java.twentyfive.sportsRevised.PlayerStatistics getPlayerStatistics(int playerId){
        main.java.twentyfive.sportsRevised.PlayerStatistics playerStatistics = playerStatisticsData.get(playerId);
        if(playerStatistics==null){
            throw new main.java.twentyfive.sportsRevised.PlayerNotFoundException("Player "+playerId+" not found in records");
        }
        return  playerStatistics;
    }


    public void savePlayerStatistics(int playerId, main.java.twentyfive.sportsRevised.PlayerStatistics playerStatistics){
        playerStatisticsData.put(playerId,playerStatistics);
    }

}

class PlayerNotFoundException extends RuntimeException{
    public PlayerNotFoundException (String message){
        super(message);
    }
}


class StatisticNotFoundException extends RuntimeException{
    public StatisticNotFoundException (String message){
        super(message);
    }
}


class Player{
    int playerId;
    String playerName;
    public Player(int playerId, String playerName){
        this.playerId=playerId;
        this.playerName=playerName;
    }
}


enum Statistic{
    GOALS,
    ASSISTS,
    CORNERS,
    FOULS,
    PENALTY,
    YELLOW_CARD,
    RED_CARD
}

class PlayerStatistics{
    int playerId;
    //Map<Statistic,Integer> playerStats= new HashMap<>();


    //// HashMap is not thread-safe for concurrent reads/writes.
    //// Concurrent updates can lead to race conditions and inconsistent state.
    //// ConcurrentHashMap protects the map structure.
    //// Individual map operations are atomic, however the whole
    //// read-modify-write operation is not atomic — similar to
    //// the idea of a Redis Lua script making multiple operations atomic.
    Map<main.java.twentyfive.sportsRevised.Statistic,Integer> playerStats= new ConcurrentHashMap<>();
    long lastUpdated;
    private final ReentrantReadWriteLock lock= new ReentrantReadWriteLock();



    public PlayerStatistics(int playerId, Map<main.java.twentyfive.sportsRevised.Statistic,Integer> playerStats){
        this.playerId=playerId;
        this.playerStats=playerStats;
    }

    public Integer getPlayerStat(main.java.twentyfive.sportsRevised.Statistic statistic){
        Integer statisticValue= playerStats.get(statistic);
        if(statisticValue==null){
            throw new main.java.twentyfive.sportsRevised.StatisticNotFoundException("Statistic not found "+statistic);
        }
        return statisticValue;
    }
    //new method for update..part 2
    //simplest concurrent and then make this synchronized
    //lock on entire object..this is coarse grained
    //even if we are modifying different statistic each time

    public synchronized void incrementPlayerStatWithSynchronized(main.java.twentyfive.sportsRevised.Statistic statistic){
        Integer statisticValue= playerStats.get(statistic);
        if(statisticValue==null){
            statisticValue=0;
        }
        playerStats.put(statistic,statisticValue+1);
    }

    //fine grained lock..so updates can independetly succeed


    public void incrementPlayerStat(main.java.twentyfive.sportsRevised.Statistic statistic){
        // compute takes key + function
        // (key, currentValue) -> newValue
        playerStats.compute(statistic,(key,current)->{
                return current==null?1:current+1;
            });
    }

    //part 3 of the problem.. now we have multiple states..we have to update timestamp
    //if we dont then..player b can update then player a can update
    //so we need a rentrant lock
    //now we dont needCompute Use compute() when you need atomicity for a single map entry.
    //Use a broader lock when the entire business operation—including multiple pieces of state—must be atomic.
    public void incrementPlayerStatMultipleStates(main.java.twentyfive.sportsRevised.Statistic statistic){
        // compute takes key + function
       // (key, currentValue) -> newValue
        lock.writeLock().lock();
        try{
            Integer current = playerStats.get(statistic);
            if(current==null)
                current=0;
            playerStats.put(statistic,current+1);
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    //read lock for writes wait for reads
    //read+read no lock
    //write+read.. read waits for write
    //read+write.. write waits for read
    //write+write ..second write waits for first
    public Integer getPlayerStatRentrant(main.java.twentyfive.sportsRevised.Statistic statistic){
        lock.readLock().lock();
        try {
            Integer statisticValue = playerStats.get(statistic);
            if (statisticValue == null) {
                throw new main.java.twentyfive.sportsRevised.StatisticNotFoundException("Statistic not found " + statistic);
            }
            //lets say if you had to return time;
            return statisticValue;
        }
        finally {
            lock.readLock().unlock();
        }
    }
}







public class SportsApp {
    public static void main (String[] args){
        main.java.twentyfive.sportsRevised.PlayerStatisticsRepository playerStatisticsRepository= new main.java.twentyfive.sportsRevised.PlayerStatisticsRepository();

        Map<main.java.twentyfive.sportsRevised.Statistic,Integer> player1Stats= new HashMap<>();
        player1Stats.put(main.java.twentyfive.sportsRevised.Statistic.GOALS,10);

        Map<main.java.twentyfive.sportsRevised.Statistic,Integer> player2Stats= new HashMap<>();
        player2Stats.put(main.java.twentyfive.sportsRevised.Statistic.GOALS,14);

        main.java.twentyfive.sportsRevised.PlayerStatistics player1Statistics= new main.java.twentyfive.sportsRevised.PlayerStatistics(1,player1Stats);
        main.java.twentyfive.sportsRevised.PlayerStatistics player2Statistics= new main.java.twentyfive.sportsRevised.PlayerStatistics(2,player2Stats);

        playerStatisticsRepository.savePlayerStatistics(1,player1Statistics);
        playerStatisticsRepository.savePlayerStatistics(2,player2Statistics);

        main.java.twentyfive.sportsRevised.PlayerComparisonService comparisonService= new main.java.twentyfive.sportsRevised.PlayerComparisonService(playerStatisticsRepository);
        main.java.twentyfive.sportsRevised.PlayerComparisonResult result=comparisonService.comparePlayers(1,2, main.java.twentyfive.sportsRevised.Statistic.GOALS);
        System.out.println(result.toString());
    }


}

/*
@RestController
@RequestMapping("/v1/players")
public class PlayerComparisonController {

    private final PlayerComparisonService comparisonService;

    public PlayerComparisonController(
            PlayerComparisonService comparisonService) {
        this.comparisonService = comparisonService;
    }

    @GetMapping("/{playerId1}/compare/{playerId2}")
    public PlayerComparisonResult comparePlayers(
            @PathVariable int playerId1,
            @PathVariable int playerId2,
            @RequestParam Statistic statistic) {

        return comparisonService.comparePlayers(
                playerId1,
                playerId2,
                statistic
        );
    }
}*/