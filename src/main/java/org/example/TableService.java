package org.example;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TableService {
    final static  String URL = "jdbc:h2:mem:test";
    public static <T> Connection run(Class<T> studentClass) {

        Connection conn = null;
        try{
            conn = DriverManager.getConnection(URL);
            conn.createStatement().execute(createTable(studentClass));
            return conn;

        }catch (SQLException e){
            System.out.println("Can't connect to DB");
        }
        return conn;
    }

    private static <T> String createTable(Class<T> studentClass) {
        StringBuilder sqlRequest = new StringBuilder();
        List<String> needColumns = new ArrayList<>();

        if(studentClass.isAnnotationPresent(Table.class)){
            sqlRequest.append("CREATE TABLE ");
            sqlRequest.append(studentClass.getAnnotation(Table.class).name());
        }

        Field[] fields  = studentClass.getDeclaredFields();
        for (Field field: fields
             ) {
            if(field.isAnnotationPresent(Id.class) && field.isAnnotationPresent(Column.class)){
                String str = "id INT";
                needColumns.add(str);
            } else if (field.isAnnotationPresent(Column.class) &&!field.isAnnotationPresent(Id.class)) {
                String str = field.getAnnotation(Column.class).name() + " ";
                String type = field.getType().getSimpleName();
                if(type.equals("String")) str+="VARCHAR(100)";
                else if(type.equals("int")) str+="INT";
                needColumns.add(str);
            }


        }
        if(!needColumns.isEmpty()){
            sqlRequest.append("(");
            for (String part: needColumns
            ) {
                sqlRequest.append(part).append(",");
            }
            sqlRequest.delete(sqlRequest.length()-1, sqlRequest.length());
            sqlRequest.append(")");

        }
        System.out.println(sqlRequest);
        return sqlRequest.toString();
    }

    public static <T> void saveObject(T student, Connection connection){
        try {
                Field[] fields = Student.class.getDeclaredFields();
                StringBuilder sqlRequest = new StringBuilder();
                sqlRequest.append("INSERT INTO ").append(student.getClass().getAnnotation(Table.class).name());
                sqlRequest.append("(");
                for (Field field: fields
                ) {
                    field.setAccessible(true);
                    sqlRequest.append(field.getDeclaredAnnotation(Column.class).name()).append(",");
                }
                sqlRequest.delete(sqlRequest.length()-1, sqlRequest.length());
                sqlRequest.append(")").append(" VALUES (");
                for (Field field: fields
                ) {
                    field.setAccessible(true);
                    Object objValue = null;
                    try {
                        objValue = field.get(student);
                        if(objValue instanceof String){
                            sqlRequest.append("'").append(objValue).append("',");
                        }else if(objValue instanceof Integer){
                            sqlRequest.append(objValue).append(",");
                        }

                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }

                sqlRequest.delete(sqlRequest.length()-1, sqlRequest.length());
                sqlRequest.append(")");
            System.out.println(sqlRequest);
                connection.createStatement().execute(sqlRequest.toString());
        } catch (SQLException e){
            System.out.println("ERROR");
        }





    }
    public static  <T> void updateObject(int id, String columnName, String newValue, Connection connection){
        try {
            String tableName = Student.class.getAnnotation(Table.class).name();
            String sql = "UPDATE " + tableName + " SET " + columnName + " = ? WHERE id = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setObject(1, newValue);
                pstmt.setObject(2, id);
                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println("Error in DB: " + e.getMessage());
        }

    }

    public static void showTable(Connection connection){

        try{

            if (connection != null) {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM Students");
                ResultSetMetaData rsmd = resultSet.getMetaData();
                StringBuilder sb = new StringBuilder();
                int count = rsmd.getColumnCount();
                int index = 0;
                String[] colNames = new String[count];
                for (int i = 1; i <= count; i++) {
                   colNames[index] = rsmd.getColumnName(i);
                   index++;
                }


                System.out.println("-----------------------------------------------------");
                System.out.printf("%-5s | %-15s | %-15s | %-3s%n", colNames);
                System.out.println("-----------------------------------------------------");
                int colIndex=1;
                while (resultSet.next()) {
                    int id = resultSet.getInt(colIndex);
                    colIndex++;
                    String firstName = resultSet.getString(colIndex);
                    colIndex++;
                    String secondName = resultSet.getString(colIndex);
                    colIndex++;
                    int age = resultSet.getInt(colIndex);
                    System.out.printf("%-5d | %-15s | %-15s | %-3d%n", id, firstName, secondName, age);
                    colIndex=1;
                }

                System.out.println("-----------------------------------------------------");
                System.out.println();

                resultSet.close();
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static void close(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("DataBase has been already closed");
        }
    }



}
