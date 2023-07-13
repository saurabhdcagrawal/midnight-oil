package main.java.intvie;

import java.util.ArrayList;
import java.util.List;

public class PlotPoints {

    static String[][] graph;
    public static int getXBound(List<int[]> points){
        int max=0;
        for(int i=0;i<points.size();i++){
            max=Math.max(max,points.get(i)[0]);
        }
        return max;
    }
    public static int getYBound(List<int[]> points){
        int max=0;
        for(int i=0;i<points.size();i++){
            max=Math.max(max,points.get(i)[1]);
        }
        return max;
    }

    public static void drawGraph(List<int[]>points){
        int m=getYBound(points)+2;
        int n=getXBound(points)+2;
        graph= new String[m][n];
        System.out.println("bounds x "+m+" bounds y "+n);
        for(int i=0;i<m;i++){
            for(int j=0;j<n;j++) {
                graph[i][j] = " ";
            }
        }


        for(int i=0;i<m;i++){
            graph[i][0]="+";
            graph[i][n-1]="+";
        }
        for(int j=0;j<n;j++){
            graph[0][j]="+";
            graph[m-1][j]="+";
        }

        for(int i=0;i<points.size();i++){
            int[] point_given=points.get(i);
            int x=point_given[0];
            int y=point_given[1];
            graph[m-y-1][x]="*";
        }       

        for(int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                    System.out.print(graph[i][j]);
                    if(j!=n-1)
                        System.out.print("-----");
            }
            System.out.println("");
        }

    }

    public static void main(String[] args) {
        ArrayList<int[]> points = new ArrayList<int[]>();
        //(1,2), (2, 3), (3, 1), (4, 6), (5, 8)} {(2,6),{1,9}

        points.add(new int[]{1, 2});
        points.add(new int[]{2, 3});
        points.add(new int[]{3, 1});
        points.add(new int[]{4, 6});
        points.add(new int[]{5, 8});
        drawGraph(points);


    }
}
