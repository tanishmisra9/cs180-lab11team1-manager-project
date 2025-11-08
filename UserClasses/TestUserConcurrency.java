package UserClasses;

public class TestUserConcurrency {
    
    private static final String DATA_PATH = "Data/users.txt";
    public static void main(String[] args) throws InterruptedException {
        UserAccountManager manager = new UserAccountManager();

        System.out.println("Working directory: " + System.getProperty("user.dir"));

        manager.boot(DATA_PATH); // Load user data base

        // Task: create 5 users
        Runnable createUsers = () -> {
            for (int i = 0; i < 5; i++) {

                String username = Thread.currentThread().getName() + "_user" + i + "_" + System.currentTimeMillis();

                UserType type = UserType.REGULAR;

                boolean success = manager.createUser(username, "password", false, type);
                System.out.println(Thread.currentThread().getName() + " created " + username + " (" + type + "): " + success);                
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
        manager.saveToFile(DATA_PATH);
    }
}
