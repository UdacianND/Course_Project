import course_project.entity.Role;

public class Main {
    public static void main(String[] args) {
        Role  role = Role.valueOf("AMIN");
        System.out.println(role.name());
    }
}
