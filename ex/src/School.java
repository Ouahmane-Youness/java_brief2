import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class School


{
    private List<Student> students = new ArrayList<>();



    public void addStudent(Student student)
    {
        this.students.add(student);
    }

    public void displayAllStudents()
    {
        if(students.isEmpty())
        {
            System.out.println("no students to print");
        }
        for(Student student : students)
        {
            System.out.println(student);
        }
    }


    public void totalStudents()
    {
        System.out.println(students.size());
    }


    public void studentsByAge()
    {
        for(Student st : students)
        {
            if(st.getAge() >= 10)
            {
                System.out.println(st);
            }
        }
    }

    public void updateStudent(String name, String newName, int newAge)
    {
        for(Student student : students)
        {
            if(Objects.equals(student.getName(), "name"))
            {
                student.setName(newName);
                student.setAge(newAge);
            }
        }
    }


}
