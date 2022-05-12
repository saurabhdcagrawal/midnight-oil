package main.java.customDS;

import java.util.*;

class Twitter {
    int tweetNumber=1;
    final int MAX_TWEETS=10;
    Map<Integer,Map<Integer,Integer>> userFollowMap;
    Map<Integer,List<Tweet>> tweetDB;

    public Twitter() {
        tweetDB= new HashMap();
        userFollowMap= new HashMap<>();
    }

    public void postTweet(int userId, int tweetId) {
        List<Tweet> tweetList=tweetDB.get(userId);
        if(tweetList==null){
            tweetList= new ArrayList<Tweet>();
            tweetDB.put(userId,tweetList);
        }
        Tweet currTweet= new Tweet(tweetId,tweetNumber);
        tweetList.add(currTweet);
        tweetNumber++;

    }

    public List<Integer> getNewsFeed(int userId) {
        List<Integer> result= new ArrayList<Integer>();
        PriorityQueue<Tweet> pq = new PriorityQueue<Tweet>((a, b)->Integer.compare(b.tweetNumber,a.tweetNumber));

        if(tweetDB.get(userId)!=null)
            pq.addAll(tweetDB.get(userId));
        Map<Integer,Integer> followerMap=userFollowMap.get(userId);
        if(followerMap!=null){
            for(Integer follower: followerMap.keySet()){
                List<Tweet> tweetList=tweetDB.get(follower);
                if(tweetList!=null){
                    pq.addAll(tweetList);
                }

            }
        }
        int i=1;
        while(pq.size()>0 && i<=MAX_TWEETS){
            Tweet curr= (Tweet)pq.poll();
            result.add(curr.tweetId);
            i++;
        }

        return result;
    }

    public void follow(int followerId, int followeeId) {
        Map<Integer,Integer> followerMap=userFollowMap.get(followerId);
        if(followerMap==null){
            followerMap= new HashMap<Integer,Integer>();
            userFollowMap.put(followerId,followerMap);
        }
        followerMap.put(followeeId,followeeId);
    }

    public void unfollow(int followerId, int followeeId) {
        Map<Integer,Integer> followerMap=userFollowMap.get(followerId);
        if(followerMap!=null)
            followerMap.remove(followeeId);
    }
}

class Tweet{

    Integer tweetId;
    Integer tweetNumber;
    public Tweet(){}
    public Tweet(int id, int number){
        this.tweetId=id;
        this.tweetNumber=number;
    }


}
/**
 * Your Twitter object will be instantiated and called as such:
 * Twitter obj = new Twitter();
 * obj.postTweet(userId,tweetId);
 * List<Integer> param_2 = obj.getNewsFeed(userId);
 * obj.follow(followerId,followeeId);
 * obj.unfollow(followerId,followeeId);
 */