package org.example;

import java.sql.*;

public class StudentDB {
    public static void main(String[] args) {
        try(Connection conn = DriverManager.getConnection("jdbc:h2:mem:test")){

            createTable(conn);
            insertDataToTable(conn);
            showTable(conn);
            findStudentByID(conn,5);
            findStudentByID(conn,15);
            updateAgeStudent(conn, 3, 56);
            showTable(conn);



        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void updateAgeStudent(Connection conn, int id, int newAge) {
        try(PreparedStatement pstm = conn.prepareStatement("""
                UPDATE Students set age=$2
                WHERE id=$1""")){
            pstm.setInt(1, id);
            pstm.setInt(2, newAge);
            pstm.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void findStudentByID(Connection conn, int id) {
        try(Statement stm = conn.createStatement()){
            ResultSet rs = stm.executeQuery("SELECT first_name, second_name, age FROM Students WHERE id=" + String.valueOf(id));
            if(rs.next()){
                String firstName = rs.getString(1);
                String secondName = rs.getString(2);
                int age = rs.getInt(3);
                System.out.println(firstName + " "+ secondName + " " + age);
            }else{
                System.out.println("No student with id: "+ id);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void showTable(Connection conn) {
        try(Statement stm = conn.createStatement()){
            ResultSet resultSet = stm.executeQuery("""
                    SELECT * FROM Students""");
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();

            while(resultSet.next()){
                for (int i = 1; i < columnCount+1; i++) {
                    System.out.print(resultSet.getObject(i).toString() +" ");
                }
                System.out.println();
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void insertDataToTable(Connection conn) {
        try(Statement stm = conn.createStatement()){
            stm.executeUpdate("""
                    INSERT INTO Students(id, first_name, second_name, age) VALUES
                    (1, 'Olga', 'Philipenko', 21),
                    (2, 'Philip', 'Olgovich', 20),
                    (3, 'Kapi', 'Johns', 34),
                    (4, 'Jeremy', 'Richards', 30),
                    (5, 'Katherina', 'Boringova', 29),
                    (6, 'Konstantin', 'Philipenko', 21),
                    (7, 'Pavel', 'Nichienko', 18)""");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void createTable(Connection conn) {
        try(Statement stm = conn.createStatement()){
            stm.execute("""
                    CREATE TABLE Students(
                    id INT,
                    first_name VARCHAR(100),
                    second_name VARCHAR(100),
                    age INT)""");
        }catch(SQLException e){
            e.printStackTrace();
        }

    }
}
