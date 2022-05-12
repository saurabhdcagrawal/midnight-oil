package main.java.customDS;
//Not important
import java.util.*;

public class UserWebsitePattern {

    public List<String> mostVisitedPattern(String[] username, int[] timestamp, String[] website) {
        Map<String, List<WebClick>> userActivity = new HashMap();
        Map<String, Integer> patternCount = new HashMap<String, Integer>();

        for (int i = 0; i < username.length; i++) {
            List<WebClick> webList = null;
            if (userActivity.containsKey(username[i])) {
                webList = userActivity.get(username[i]);
            } else {
                webList = new ArrayList<WebClick>();
                userActivity.put(username[i], webList);
            }

            webList.add(new WebClick(timestamp[i], website[i]));
        }

        for (String user : userActivity.keySet()) {
            List<WebClick> webList = userActivity.get(user);
            Collections.sort(webList, new WebClickComparator());
            //no patterns generated if the size of list is less than 3
            if (webList.size() < 3)
                continue;
            Set<String> triplets = generatePatternTriplets(webList);
            System.out.println(user);
            for (String pattern : triplets) {
                System.out.println(pattern);
                patternCount.put(pattern, patternCount.getOrDefault(pattern, 0) + 1);
            }
        }

        System.out.println(patternCount);

        int maxCount = Integer.MIN_VALUE;
        String maxPattern = null;


        for (String p : patternCount.keySet()) {
            if (patternCount.get(p) > maxCount) {
                maxCount = patternCount.get(p);
                maxPattern = p;
            } else if (patternCount.get(p) == maxCount) {
                maxPattern = maxPattern.compareTo(p) < 0 ? maxPattern : p;
            }
        }


        return Arrays.asList(maxPattern.split(","));
    }

    //a b c d
    public Set<String> generatePatternTriplets(List<WebClick> webList) {
        Set<String> triplets = new HashSet<String>();
        for (int i = 0; i < webList.size() - 2; i++) {
            for (int j = i + 1; j < webList.size(); j++) {
                for (int k = j + 1; k < webList.size(); k++) {
                    String pattern = webList.get(i).getWebsite() + "," + webList.get(j).getWebsite() + "," + webList.get(k).getWebsite();
                    triplets.add(pattern);
                }
            }
        }
        return triplets;
    }
}


    class WebClick{

        public int timestamp;
        public String website;

        public WebClick(){};

        public WebClick(int ts, String webs){
            this.timestamp=ts;
            this.website=webs;
        }

        public String getWebsite(){
            return website;
        }

    }

    class WebClickComparator implements Comparator<WebClick>{
        @Override
        public int compare(WebClick o1, WebClick o2){

            return o1.timestamp-o2.timestamp;
        }
    }

