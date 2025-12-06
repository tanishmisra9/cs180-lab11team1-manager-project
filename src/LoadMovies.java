package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility to load movie showings from movies.txt into the database.
 *
 * Format: movieName,yyyy-MM-dd HH:mm,rows,cols,price
 *
 * <p>Purdue University -- CS18000 -- Fall 2025</p>
 *
 * @author CS180 Team 1
 * @version Nov 19, 2025
 */
public class LoadMovies {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        System.out.println("Loading movies from movies.txt...\n");

        ReservationDatabase db = ReservationDatabase.loadDatabase();
        int count = 0;

        try (BufferedReader br = new BufferedReader(new FileReader("data/movies.txt"))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                // Skip empty lines or comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split(",");

                if (parts.length != 5) {
                    System.out.println("Skipping invalid line: " + line);
                    continue;
                }

                try {
                    String movieName = parts[0].trim();
                    LocalDateTime showTime = LocalDateTime.parse(parts[1].trim(), DATE_TIME_FORMAT);
                    int rows = Integer.parseInt(parts[2].trim());
                    int cols = Integer.parseInt(parts[3].trim());
                    double price = Double.parseDouble(parts[4].trim());

                    Auditorium auditorium = new Auditorium(rows, cols, price, movieName, showTime);
                    db.addAuditorium(auditorium);

                    System.out.printf("Added: %s - %s (%dx%d @ $%.2f)\n",
                            movieName, showTime.format(DATE_TIME_FORMAT), rows, cols, price);
                    count++;

                } catch (Exception e) {
                    System.out.println("Error processing line: " + line);
                    System.out.println("Error: " + e.getMessage());
                }
            }

            db.saveData();
            System.out.println("\n" + count + " movies loaded successfully!");
            System.out.println("Database saved to db.dat");

        } catch (IOException e) {
            System.out.println("Error reading movies.txt: " + e.getMessage());
            System.out.println("Make sure movies.txt exists in the current directory.");
        }
    }
}