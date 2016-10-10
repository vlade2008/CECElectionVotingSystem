package com.example.xtian.cecelectionvotingsystem;

/**
 * Created by Xtian on 7/21/2015.
 */
public class DBconnect {

    public static String id;

    public static void main(String[] args) {
//        try {
//
//            Class.forName("com.mysql.jdbc.Driver");
//            Connection con = DriverManager.getConnection(Config.url, Config.user, Config.pass);
//
//            String result = "Database Connection success\n";
//            Statement st = con.createStatement();
//            ResultSet rs = st.executeQuery("select * from administrator");
//            while (rs.next()){
//
//                System.out.println(rs.getString("id"));
//                 id = rs.getString("id");
//
//            }
//
//        }
//        catch (Exception e){
//            e.printStackTrace();
//
//        }
//    }
//        String connect(){
//            String connect_database = id;
//            return connect_database;
//        }

        float num1 = 200;
        int num2 = 1;
        float total_num2_percentage;
        int percentage;

        total_num2_percentage = num2 *100;

        percentage = (int) (total_num2_percentage/num1);



        String  count;
        count = "null";
        //if ()

        System.out.println("Percage is "+ percentage);
    }

    }

