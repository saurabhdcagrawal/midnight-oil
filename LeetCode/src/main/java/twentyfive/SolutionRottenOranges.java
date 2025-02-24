package main.java.twentyfive;

import java.util.LinkedList;
import java.util.Queue;

public class SolutionRottenOranges {

    public int orangesRotting(int[][] grid) {

        int m = grid.length;
        int n = grid[0].length;
        int freshOranges = 0;
        int timeElapsed = -1;
        Queue<int[]> q = new LinkedList<>();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 2)
                    q.offer(new int[]{i, j});
                else if (grid[i][j] == 1)
                    freshOranges++;
            }
        }

        //optimization.. return when no fresh oranges then 0 has to be returned as per
        //problem condition
        if (freshOranges == 0)
            return 0;
        q.offer(null);
        //q contains ({0,0};null)
        //if we intitalize timeElapsed=0, then timeElapsed will be 1 when first 2 is encountered
        //but that is start of time for us so that should be 0
        //hence to offset that we initialize timeElapsed to -1
        while (!q.isEmpty()) {
            int[] point = q.poll();
            if (point == null) {
                timeElapsed++;
                if (!q.isEmpty())
                    q.offer(null);
            } else {
                int i = point[0], j = point[1];
                int[] x_dir = {-1, 0, 1, 0};
                int[] y_dir = {0, 1, 0, -1};
                for (int k = 0; k < 4; k++) {
                    int new_i = i + x_dir[k];
                    int new_j = j + y_dir[k];
                    if (new_i >= 0 && new_i < m && new_j >= 0 && new_j < n && grid[new_i][new_j] == 1) {
                        grid[new_i][new_j] = 2;
                        freshOranges--;
                        q.offer(new int[]{new_i, new_j});
                    }
                }
            }
        }
        //freshOranges can be shielded by empty space. since there is no diagonal propogation
        //there can be a case when freshOranges can never be spoiled
        return freshOranges == 0 ? timeElapsed : -1;
    }
    //Time complexity : O(mn)+O(mn)= O(mn);
    //Space Complexity: O(mn) In worst case all elements of grid can be in Queue
    //making queue size mn
}