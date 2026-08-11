package main.java.twentyfive.sports.sportsRevised2;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

enum GameStatus{
    SCHEDULED,
    LIVE,
    ENDED
}

class Team{
    int teamId;
    String teamName;
}

class GameSnapshot{
    int gameId;
    int homeTeamId;
    int awayTeamId;
    int homeScore;
    int awayScore;
    long lastUpdatedTimestamp;
    GameStatus status;

    public GameSnapshot(int gameId, int homeTeamId, int awayTeamId, int homeScore, int awayScore, GameStatus status, long lastUpdatedTimestamp){
        this.gameId=gameId;
        this.homeTeamId=homeTeamId;
        this.awayTeamId=awayTeamId;
        this.homeScore=homeScore;
        this.awayScore=awayScore;
        this.status=status;
        this.lastUpdatedTimestamp=lastUpdatedTimestamp;
    }
}

class Game{

    int gameId;
    int homeTeamId;
    int awayTeamId;
    int homeScore;
    int awayScore;
    GameStatus status;
    //Domain facing Business Data
    LocalDateTime gameDate;
    //System metaData
    long lastUpdatedTimestamp;
    ReentrantReadWriteLock lock= new ReentrantReadWriteLock();

    public Game(int gameId, int homeTeamId, int awayTeamId, LocalDateTime gameDate){
        this.gameId=gameId;
        this.homeTeamId=homeTeamId;
        this.awayTeamId=awayTeamId;
        gameDate=this.gameDate;
        status=GameStatus.SCHEDULED;
        lastUpdatedTimestamp=System.currentTimeMillis();
    }

    public void startGame(){
        lock.writeLock().lock();
        try{
            if(status!=GameStatus.SCHEDULED)
                throw new IllegalStateException("Game already started "+gameId);
            status=GameStatus.LIVE;
            lastUpdatedTimestamp=System.currentTimeMillis();
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    public void endGame(){
        lock.writeLock().lock();
        try{
            if(status!=GameStatus.LIVE)
                throw new IllegalStateException("Game already ended "+gameId);
            status=GameStatus.ENDED;
            lastUpdatedTimestamp=System.currentTimeMillis();
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    public GameSnapshot getGameSnapshot(){
        lock.readLock().lock();
        try{
            return new GameSnapshot(this.gameId,this.homeTeamId,this.awayTeamId,this.homeScore,this.awayScore,this.status,this.lastUpdatedTimestamp);
        }
        finally {
            lock.readLock().unlock();
        }
    }

    public void updateScore(int homeScoreDelta, int awayScoreDelta){
        lock.writeLock().lock();
        try{
            homeScore+=homeScoreDelta;
            awayScore+=awayScoreDelta;
            lastUpdatedTimestamp=System.currentTimeMillis();
        }
        finally {
            lock.writeLock().unlock();
        }
    }



}



public class GameService {
    ConcurrentHashMap<Integer,Game> games= new ConcurrentHashMap<>();
    public void createGame(int gameId, int homeTeamId, int awayTeamId, LocalDateTime matchDate){
        LocalDateTime matchDateModified= LocalDateTime.of(2026,10,10,18,0);
        Game game= games.putIfAbsent(gameId, new Game(gameId,homeTeamId,awayTeamId,matchDate));
        if(game!=null){
           throw new IllegalArgumentException("Game already exists "+gameId);
        }
    }

    public void startGame(int gameId) {
        Game game = games.get(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game does not exist " + gameId);
        }
        game.startGame();

    }
    public void endGame(int gameId){
        Game game = games.get(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game does not exist " + gameId);
        }
        game.endGame();
    }

    public void updateScore(int gameId, int homeTeamDeltaScore, int awayTeamDeltaScore){
        Game game= games.get(gameId);
        if(game==null){
            throw new IllegalArgumentException("Game does not exist "+gameId);
        }
        game.updateScore(homeTeamDeltaScore,awayTeamDeltaScore);
    }

    public GameSnapshot getGameSnapShot(int gameId){
        Game game= games.get(gameId);
        if(game==null){
            throw new IllegalArgumentException("Game does not exist "+gameId);
        }
        return game.getGameSnapshot();
    }



}
