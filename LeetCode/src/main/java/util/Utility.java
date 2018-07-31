//com.sql.jdbc.myDriver is an external library ,need to include in your imports
//and classpath
package main.java.util;
//STEP 1. Import required packages
import main.java.beans.Country;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class Utility {


    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/db";

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "";

    List<Country> countries;

    public  void readCSVFile(){
        BufferedReader br =null;
        try {
            //backslash still ok
             String filePath = "C:\\Users\\Saurabh Agrawal\\IdeaProjects\\LeetCode\\src\\main\\resources\\factbook.csv";
             br= new BufferedReader(new FileReader(filePath));
             countries = new ArrayList<Country>();
             String line = null;
             String[]  cols = null;
             StringTokenizer st = null;
            //Map<String,String> countryMap= new HashMap<String,String>();
             line=br.readLine();
             st= new StringTokenizer(line,";"); int count=0;
             while(st.hasMoreTokens())
                 System.out.println(count++ +"->" +st.nextToken());
            line=br.readLine();
             while ((line=br.readLine())!=null){
            //System.out.println(line);
            cols=line.split(";",-1);
            System.out.println(cols[0]+" "+cols[25]+ " "+cols[37]);
            countries.add(new Country(cols[0],Long.parseLong(cols[25]),Double.parseDouble(cols[37])));
            }
            //System.out.println(countryMap);

        }catch(FileNotFoundException e){
          e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            if(br!=null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void insertRows(){
        Connection conn = null;
        Statement stmt = null;

        //STEP 2: Register JDBC driver
        try {
            Class.forName("com.mysql.jdbc.Driver");
            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            System.out.println("Connected database successfully...");
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql;
            for (Country country:countries){
                sql = "INSERT INTO COUNTRY VALUES("+country.getCountryName()+"," +country.getPopulation()+"," +country.getMilitaryExpenditure()+");";
                stmt.executeUpdate(sql);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            if(stmt!=null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(conn!=null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void main(String args[]){
        Utility util = new Utility();
        util.readCSVFile();
        util.insertRows();
    }

}
