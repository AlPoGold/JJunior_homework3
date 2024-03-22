package org.example;


import java.sql.Connection;

@Table(name = "Students")
public class Student {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name="second_name")
    private String secondName;

    @Column(name="age")
    private int age;


    public static void main(String[] args) {
        Connection connection = TableService.run(Student.class);
        Student student1 = new Student(1, "Ivan", "Ivanov", 21);
        Student student2 = new Student(2, "Petr", "Petrov", 19);
        Student student3 = new Student(3, "Vasilii", "Vasiliev", 24);
        TableService.saveObject(student1, connection);
        TableService.saveObject(student2, connection);
        TableService.saveObject(student3, connection);
        TableService.showTable(connection);


        TableService.updateObject(3, "first_name", "Semen", connection);
        TableService.showTable(connection);


        Student student = TableService.findStudentById(2, connection);
        System.out.println(student);

        TableService.close(connection);



    }

    public Student(int id, String firstName, String secondName, int age) {
        this.id = id;
        this.firstName = firstName;
        this.secondName = secondName;
        this.age = age;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", secondName='" + secondName + '\'' +
                ", age=" + age +
                '}';
    }
}
