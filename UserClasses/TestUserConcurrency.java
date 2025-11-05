package UserClasses;
import java.util.concurrent.ConcurrentHashMap;

public class TestUserConcurrency {
    public static void main(String[] args) throws InterruptedException {
        UserAccountManager manager = new UserAccountManager();

        // Task: create 5 users
        Runnable createUsers = () -> {
            for (int i = 0; i < 5; i++) {
                String username = "user" + i;
                boolean success = manager.createUser(username, "password", false);
                System.out.println(Thread.currentThread().getName() + " created " + username + ": " + success);
            }
        };

        // Simulate two threads creating users concurrently
        Thread t1 = new Thread(createUsers, "Thread-1");
        Thread t2 = new Thread(createUsers, "Thread-2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Total users: " + manager.getUserCount());
    }
}
