package main.java.customDS;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class RankByVotes {

    public String rankTeams(String[] votes) {

            int teamSize=votes[0].length();

            Map<Character,int[]> rankMap = new HashMap();
            for(int i=0;i<votes.length;i++){
                for(int j=0;j<votes[i].length();j++){
                    char team= votes[i].charAt(j);
                    if(!rankMap.containsKey(team))
                        rankMap.put(team,new int[teamSize]);
                    rankMap.get(team)[j]++;
                }
            }

            System.out.println(rankMap);
            PriorityQueue<Character> pq = new PriorityQueue<Character>((a, b)->{
                for(int i=0;i<teamSize;i++){
                    if(rankMap.get(a)[i]!= rankMap.get(b)[i]){
                        return rankMap.get(b)[i]-rankMap.get(a)[i];
                    }
                }
                return a-b;
            });

            for(Character c:rankMap.keySet())
                pq.add(c);

            StringBuilder finalRank=new StringBuilder("");

            while(pq.size()>0)
                finalRank.append(pq.poll());

            return finalRank.toString();
        }

}
