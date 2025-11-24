package src;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

//TODO implement thread safety
public class ClientDriver {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Client basic = new BasicClient();
        ClientService a = new ClientService(basic);
        a.connectToServer();        

        boolean isAdmin = false;

        System.out.println("Welcome to the ticketing client!");

        // Loops forever until terminal is closed
        fullBreak: while (true) {

            // Loops until a valid login is entered.
            loginBreak: while (true) {
                System.out.println("\nPlease enter your Username:");
                String username = sc.nextLine();
                System.out.println("\nPlease enter your Password:");
                String password = sc.nextLine();

                a.login(username, password);
                // TODO verify if successful
                // read from server, and get payload
                // TODO verify if admin
                boolean isValidLogin = true;
                if (isAdmin) {
                    break fullBreak;
                }

                if (isValidLogin) {
                    break loginBreak;
                } else {
                    System.out.println("\nUsername or Password is incorrect.");
                }

            } // end login while loop

            System.out.println("\nSuccess!");

            returnMovieSelectionBreak: while (true) {

                // Shows the available movies
                System.out.println("\nAvailable showings: ");
                // TODO get a list of available movies and print the list
                // In Server.java
                // Recieve payload (serverRequest = server.readObject())

                // Get an auditorium
                String[] tempMovieArray = new String[] { "Madagascar", "Godzilla", "Cars", "Toy Story" };
                for (String line : tempMovieArray) {
                    System.out.println(line);
                }
                List<String> movieArrayList = Arrays.asList(tempMovieArray);

                // Loops until a valid movie is selected
                String movieSelected;
                movieSelectionBreak: while (true) {
                    System.out.println("\nSelect movie: ");
                    movieSelected = sc.nextLine();

                    boolean foundMovie = false;

                    for (String movie : movieArrayList) {
                        if (movie.equalsIgnoreCase(movieSelected)) {
                            foundMovie = true;
                            break;
                        }
                    }

                    if (foundMovie) {
                        break movieSelectionBreak;
                    } else {
                        System.out.println("Please enter a valid movie!");
                    }
                } // end movieSelection while loop

                System.out.println("\nSuccess!");

                // Shows the available seats
                System.out.println("\nAvailable seating:\n(_ for available, X for taken!");
                // TODO get the available seating.
                // server will send an audtorium
                String[][] tempSeating = new String[4][];
                tempSeating[0] = new String[] { "empty", "empty", "JohnJacob43", "JaneJacob43", "empty", "BobFan",
                        "Hi" };
                tempSeating[1] = new String[] { "hey", "empty", "sup", "Doe", "empty" };
                tempSeating[2] = new String[] { "empty", "filled" };
                tempSeating[3] = new String[] { "Hello", "Joshua", "hi", "gettup" };

                // removes the names from the list (makes them "taken")
                for (int i = 0; i < tempSeating.length; i++) {
                    for (int j = 0; j < tempSeating[i].length; j++) {
                        if (!tempSeating[i][j].equals("empty")) {
                            tempSeating[i][j] = "taken";
                        }

                    }
                }

                seatSelectionPaymentBreak: while (true) {
                    System.out.print("|| ");
                    for (int i = 0; i < tempSeating.length; i++) {
                        for (int j = 0; j < tempSeating[i].length; j++) {
                            if (tempSeating[i][j].equals("empty")) {
                                System.out.print("_" + " | ");
                            } else {
                                System.out.print("X" + " | ");
                            }

                        }
                        System.out.print("||");
                        System.out.println();
                        System.out.print("|| ");
                    }
                    System.out.println("\nType \"exit\" to return to the movie list");

                    // loops until an available seat is selected, or exit back to movie selection.
                    String row;
                    String col;
                    seatSelectionBreak: while (true) {
                        System.out.println("\nNote: Start at 1 and not 0.");
                        System.out.println("\nEnter the Row of the seat you want: ");
                        row = sc.nextLine();
                        if (row.equalsIgnoreCase("exit")) {
                            continue returnMovieSelectionBreak;
                        }
                        System.out.println("\nEnter the Column of the seat you want: ");
                        col = sc.nextLine();
                        if (col.equalsIgnoreCase("exit")) {
                            continue returnMovieSelectionBreak;
                        }
                        // validate the row/col
                        int intRow = -1;
                        int intCol = -1;
                        try {
                            intRow = Integer.parseInt(row);
                        } catch (NumberFormatException nfe) {
                            System.out.println("\nError: Row must be a number! Please try again.");
                            continue seatSelectionBreak;
                        }
                        try {
                            intCol = Integer.parseInt(col);
                        } catch (NumberFormatException nfe) {
                            System.out.println("\nError: Column must be a number! Please try again.");
                            continue seatSelectionBreak;
                        }

                        int zeroBasedRow = intRow - 1;
                        int zeroBasedCol = intCol - 1;
                        if (zeroBasedRow < 0 || zeroBasedRow >= tempSeating.length) {
                            System.out.println("\nInvalid Row number: "
                                    + "The row is outside the seating boundaries. Please try again.");
                            continue seatSelectionBreak;
                        }

                        if (zeroBasedCol < 0 || zeroBasedCol >= tempSeating[zeroBasedRow].length) {
                            System.out.println("\nInvalid Column number: "
                                    + "The column is outside the seating boundaries for that row. Please try again.");
                            continue seatSelectionBreak;
                        }

                        String seatStatus = tempSeating[zeroBasedRow][zeroBasedCol];

                        if (seatStatus.equalsIgnoreCase("empty")) {
                            System.out.println("\nSeat (" + intRow + ", " + intCol + ") successfully selected.");
                            break seatSelectionBreak;
                        } else {
                            System.out.println("\nSeat (" + intRow + ", " + intCol + ") is already taken!"
                                    + " Please select an empty seat.");
                            continue seatSelectionBreak;
                        }

                    } // end seatSelection while loop

                    // displays the price of the selected seat
                    // (in GUI, it should just be a pop up with cancel and ok.)
                    // (if cancel is selected, close the popup.)
                    // (if okay is selected, then close the pop up,
                    // and make the window for entering a seat the payment screen.)

                    // TODO DISPLAY PRICE OF SELECTED SEAT
                    System.out.println("\nPrice of seat: ");

                    paymentScreenBreak: while (true) {
                        System.out.println("Enter 1 to proceed to payment. Enter 2 to go back to seat selection.");
                        String payScreenOption = sc.nextLine();
                        if (payScreenOption.equals("1")) {
                            // TODO check if they have enough money and remove money from account
                            break paymentScreenBreak;
                        } else if (payScreenOption.equals("2")) {
                            continue seatSelectionPaymentBreak;
                        } else {
                                 System.out.println("Error: Please enter a vamber (1 or 2)");
                            continue paymentScreenBreak;
                        }
                    } // end paymentScreen while loop

                    purchaseAnotherSeatBreak: while (true) {
                        //TODO reserve said seat as the current user's name. 

                        System.out.println("\nSuccess! Seat has been purchased.");
                        System.out.println("Would you like to purchase another seat? (1 for Yes, 2 for No)");
                        String anotherPurchase = sc.nextLine();
                        if (anotherPurchase.equals("1")) {
                            continue seatSelectionPaymentBreak;
                        } else if (anotherPurchase.equals("2")) {

                            anotherPurchaseBreak: while (true) {
                                System.out.println(
                                        "\nWould you like to purchase seats from another movie? (1 for Yes, 2 for No)");
                                String anotherMovie = sc.nextLine();
                                if (anotherMovie.equals("1")) {
                                    continue returnMovieSelectionBreak;
                                } else if (anotherMovie.equals("2")) {
                                    break fullBreak;
                                } else {
                                    System.out.println("Error: Please enter a valid number (1 or 2)");
                                    continue anotherPurchaseBreak;
                                }
                            }
                        } else {
                            System.out.println("Error: Please enter a valid number (1 or 2)");
                            continue purchaseAnotherSeatBreak;
                        }

                    }

                } // end seatSelectionPayment while loop

            } // end returnMovieSelection while loop

        } // end full while loop


        if (isAdmin) {

            System.out.println("\nSuccess!");
            String input;

            adminOuter:while(true) {
            // TODO

            // choose what to do while loop (selectOptions).
            // options include: reserve seat, edit existing auditorium, create new venue, add auditorium.
            // at any time, type "exit" to return to options menu
            selectOptions:while(true) {
                System.out.println("\n--- Selection what you wish to do: ---");
                System.out.println("1: Reserve seat");
                System.out.println("2: Edit existing auditorium");
                System.out.println("3: Create new venue");
                System.out.println("4: Add auditorium");
                System.out.println("Type 'exit' to return.");
                System.out.println("Enter selection: ");

                input = sc.nextLine().trim();

                switch (input) {
                    case "1": {
                        // TODO: Reserve seat logic

                        // if reserve seat is selected
                        // while loop select showing
                        // while loop select which seat
                        // while loop set seat to reserved (could be for out of order or actually booking)

                        selectShowing:while(true) {
                            
                        }
                    } break; // This breaks out of the 'switch', not the 'while'
                
                    case "2":
                        // TODO: Edit existing auditorium logic
                        break;

                    case "3":
                        // TODO: Create new venue logic
                        break;
                    case "4":
                        // TODO: Add auditorium logic
                        break;
                    case "exit":
                        System.out.println("Exiting options menu...");
                        break selectOptions; 
                    default:
                        // This handles any invalid input
                        System.out.println("Error: Invalid input. Please enter a number 1-4 or exit.");
                        break;
                }
            }
        
        // Code execution continues here after the loop is broken
        System.out.println("Returned to previous menu.");

            

            // if edit existing auditorium is selected
            // while loop editExisting
            // ask if they want to change movie name, reschedule the time, or cancel it

            // if a new venue is to be created
            // while loop createVenue
            // ask which auditorium to use (each is a 2D array of seats), (USE A PLACEHOLDER)
            // show existing venues and ask which time to schedule
            // check if time does not conflict and is in the future and then create it

            // if adding an auditorium
            // ask for row
            // ask for col
            // create an auditorium with that row and column

            //after this, return back to options menu and ask again

            }


        }


        System.out.println("\nThank you for using our service!");
        sc.close();
    } // end main

} // end class
