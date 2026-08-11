package main.java.twentyfive.sports.sportsRevised2;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

enum MatchStatus{
    SCHEDULED,
    STARTED,
    ENDED
}

class Team{
    int teamId;
    String teamName;
}

class MatchSnapshot{
    public MatchSnapshot(int matchId, String matchTitle, int homeTeamId, int homeScore, int awayTeamId, int awayScore, long lastUpdated) {
        this.matchId = matchId;
        this.matchTitle = matchTitle;
        this.homeTeamId = homeTeamId;
        this.homeScore = homeScore;
        this.awayTeamId = awayTeamId;
        this.awayScore = awayScore;
        this.lastUpdated = lastUpdated;
    }

    int matchId;
    String matchTitle;
    int homeTeamId;
    int awayTeamId;
    int homeScore;
    int awayScore;
    long lastUpdated;

}
class Match{
    int matchId;
    String matchTitle;
    int homeTeamId;
    int awayTeamId;
    int homeScore;
    int awayScore;
    long lastUpdated;
    ReentrantReadWriteLock lock= new ReentrantReadWriteLock();
    MatchStatus status;

    public Match(int matchId, int homeTeamId, int awayTeamId) {
        this.matchId=matchId;
        this.homeTeamId=homeTeamId;
        this.awayTeamId=awayTeamId;
        status=MatchStatus.SCHEDULED;
    }

    public int startMatch(){
        lock.writeLock().lock();
        try {
            if(status!=MatchStatus.SCHEDULED){
                throw new IllegalStateException("Match already started");
            }
            status = MatchStatus.STARTED;
            return matchId;
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    public int endMatch(){
        lock.writeLock().lock();
        try {
            if(status!=MatchStatus.STARTED){
                throw new IllegalStateException("Match has not started");
            }
            status = MatchStatus.ENDED;
            return matchId;
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    public MatchSnapshot getMatchSnapshot(){
        lock.readLock().lock();
        try {

            return new MatchSnapshot(matchId,matchTitle,homeScore,awayScore,homeTeamId,awayTeamId,lastUpdated);
        }
        finally {
            lock.readLock().unlock();
        }
    }


    public void updateMatchScore(int homeDelta, int awayDelta, long timestamp){
        lock.writeLock().lock();
        try {
            homeScore+=homeDelta;
            awayScore+=awayDelta;
            lastUpdated=timestamp;
        }
        finally {
            lock.writeLock().unlock();
        }
    }

}

class MatchAlreadyExistsException extends RuntimeException {
    public MatchAlreadyExistsException(String message){
        super(message);
    }
}

public  class MatchService {

    ConcurrentHashMap<Integer,Match> matches= new ConcurrentHashMap<>();

    public void createMatch(int matchId, int homeTeamId, int awayTeamId){
            Match existing=matches.putIfAbsent(matchId, new Match(matchId,homeTeamId,awayTeamId));
            if(existing!=null){
                throw new IllegalArgumentException("Match already exists"+matchId);
            }
    }

    public void startMatch(int matchId){
        Match match = matches.get(matchId);
        if(match==null){
            throw new IllegalArgumentException("Match not found"+matchId);
        }
        match.startMatch();
    }
    public void endMatch(int matchId){
        Match match = matches.get(matchId);
        if(match==null){
            throw new IllegalArgumentException("Match not found"+matchId);
        }
        match.endMatch();
    }

    public void updateMatchScore(int matchId, int homeDelta, int awayDelta, long timestamp){
        Match match = matches.get(matchId);
        if(match==null){
            throw new IllegalArgumentException("Match not found"+matchId);
        }
        match.updateMatchScore(matchId,homeDelta,awayDelta);
    }

    public MatchSnapshot getMatchSnapshot(int matchId){
        Match match = matches.get(matchId);
        if(match==null){
            throw new IllegalArgumentException("Match not found"+matchId);
        }
        return match.getMatchSnapshot();
    }



}

