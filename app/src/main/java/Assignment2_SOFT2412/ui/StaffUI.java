package Assignment2_SOFT2412.ui;

import Assignment2_SOFT2412.data.Database;
import org.postgresql.util.PSQLException;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Scanner;

public class StaffUI extends BaseUI {

    private String staff = null;
    private boolean manager = false;

    public StaffUI(Database db, Scanner s, PrintStream ps) {
        super(db, s, ps);
    }

    public boolean isLoggedIn() {
        return staff != null;
    }

    public boolean isManager() {
        return manager;
    }

    public void start() {
        super.mode = "staff";
        boolean shouldQuit = false;
        while (!shouldQuit) {

            String option;

            if (isLoggedIn() && isManager()) {
//                manager only
                option = super.askOptions("What would you like to do next?", "add movies",
                        "add movie showing", "add movie session", "delete movies", "modify movies", "summary bookings",
                        "show upcoming movies", "filter movies", "show canceled transaction", "add giftcard", "delete staff", "add staff", "quit");
            } else if (isLoggedIn()) {
//                regular staff
                option = super.askOptions("What would you like to do next?", "add movies",
                        "add movie showing", "add movie session", "delete movies", "modify movies", "summary bookings",
                        "show upcoming movies", "filter movies", "add giftcard", "quit");
            } else {
                option = super.askOptions("What would you like to do next?", "login as staff",
                        "login as manager", "quit");
            }

            switch (option) {
                case "add movies":
                    addMovies();
                    break;
                case "add movie showing":
                    addNewShows();
                    break;
                case "add movie session":
                    addNewSession();
                    break;
                case "delete movies":
                    deleteMovies();
                    break;
                case "modify movies":
                    modifyMovies();
                    break;
                case "summary bookings":
                    summaryMovieSessions();
                    break;
                case "show upcoming movies":
                    showUpcomingMovies();
                    break;
                case "filter movies":
                    super.filterMovies();
                    break;
                case "show canceled transaction":
                    showCanceledTransaction();
                    break;
                case "add giftcard":
                    addGiftCard();
                    break;
                case "delete staff":
                    deleteStaff();
                    break;
                case "add staff":
                    addStaff();
                    break;
                case "login as staff":
                    loginStaff();
                    break;
                case "login as manager":
                    loginManager();
                    break;
                case "quit":
                    shouldQuit = true;
                    break;
            }
        }

        super.printFancy("Goodbye!");
    }

    private void addMovies() {

        String movieName = super.getLine("Enter a name of movie: ");
        String classification = super.getLine("Enter the classification for this movie: ").toUpperCase();

        int rowsAffected = db.addRowForMovie(movieName, classification);
        if (rowsAffected > 0) {
            super.printFancy("Add successful!");
        } else {

            super.printFancy("Unable to add movie. Check the classification is correct.");
        }
    }

    private void addNewSession() {
        int movieShowingId = super.getInt("Enter the showing ID: ");
        String showingDate = super.getLine("Enter the session date (what day the movie will be shown): ");
        int seats = super.getInt("Enter the number of seats in this session: ");
        String screenSize = super.getLine("Enter the screen size in this session: ");
        double price = super.getDouble("Enter the price for this session: ");

        db.addMovieSession(movieShowingId, showingDate, seats,price,screenSize);
    }

    private void addNewShows() {
        String movieName = super.getLine("Enter a name of movie: ");
        String cinemaName = super.getLine("Enter a name of cinema: ");
        String dateRange = super.getLine("Enter the available dates for the movie: ");
        db.addRowForMovieShowing(movieName, cinemaName, dateRange);
        super.printFancy("Add successful!");
    }

    private void deleteMovies() {

        String movieName = super.getLine("Enter the name of movie you want to delete: ");
        int rowsAffected = db.deleteRowForMovie(movieName);
        if (rowsAffected == 0) {
            super.printFancy("No movie named " + movieName + " exists!");
        } else {
            super.printFancy("Delete successful!");
        }

    }

    private void modifyMovies() {

        String movieName = super.getLine("Enter the name of movie: ");
        String classification = super.getLine("Enter the new classification you want to cover for this movie: ");
        int rowsAffected = db.modifyRowForMovie(movieName, classification);
        if (rowsAffected == 0) {
            super.printFancy("No movie named " + movieName + " exists!");
        } else {
            super.printFancy("Change successful!");
        }
    }

    private void summaryMovieSessions() {
        ResultSet rs = db.getSummarySession();

        try {
            PrintStream file = new PrintStream("session-summary.txt");
            db.printAll(file, rs);
            file.close();

            super.printFancy("A summary of the movie sessions has been saved to session-summary.txt");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void showUpcomingMovies() {
        try {
            ResultSet rs = db.getSessions(today, nextWeekDate);

            PrintStream file = new PrintStream("upcoming-movie-summary.txt");
            db.printAll(file, rs);
            file.close();

            super.printFancy("A summary of the upcoming movies has been saved to upcoming-movie-summary.txt");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    private void loginStaff() {

        String staffname = super.getLine("Enter your staff account name: ");
        ResultSet rs = db.getStaffAccount(staffname);

        try {
            if (rs.next()) {
                String password = super.getLine("Enter your password: ");

                // correct password stored in the db
                String correctPassword = rs.getString(2);

                if (password.equals(correctPassword)) {
                    this.staff = staffname;
                    super.mode = "staff " + this.staff;

                    super.printFancy("Welcome " + staffname);
                } else {
                    super.printFancy("Wrong password!");
                }
            } else {
                super.printFancy("No staff exists");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void loginManager() {
        String managername = super.getLine("Enter your manager account name: ");
        ResultSet rs = db.getManagerAccount(managername);

        try {
            if (rs.next()) {
                String password = super.getLine("Enter your password: ");

                // correct password stored in the db
                String correctPassword = rs.getString(2);

                if (password.equals(correctPassword)) {
                    this.staff = managername;
                    this.manager = true;
                    super.mode = "manager " + this.staff;
                    super.printFancy("Welcome " + managername);
                } else {
                    super.printFancy("Wrong password!");
                }
            } else {
                super.printFancy("No manager exists");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void addStaff() {
        if (!this.manager) {
            super.printFancy("Only manager can have authority to add staff account!");
            return;
        }
        String staffname = super.getLine("Enter a staff account name you want to add: ");
        String password = super.getLine("Enter the password you want to set: ");
        db.addStaffAccount(staffname, password);
        super.printFancy("Add successful!");
    }

    private void deleteStaff() {
        if (!this.manager) {
            super.printFancy("Only manager can have authority to delete staff account!");
            return;
        }
        String staffname = super.getLine("Enter the staff account name you want to delete: ");
        int rowsAffected = db.deleteStaffAccount(staffname);
        if (rowsAffected == 0) {
            super.printFancy("No staff named " + staffname + " exists!");
        } else {
            super.printFancy("Delete successful!");
        }
    }

    public void showCanceledTransaction(){
        super.printFancy("Here is the list of canceled transactions: ");
        ResultSet rs = db.getCanceledTransaction();
//        db.printAll(printStream, rs);
        db.saveAll(printStream, rs);
    }

    public void addGiftCard(){
        String option;
        option = super.askOptions("Choose from the options to add gift card: ", "add manually", "import from txt file");
        switch (option) {
            case "add manually":
                addGiftCardManually();
                break;
            case "import from txt file":
                addGiftCardFromFile();
                break;
        }


    }

    public void addGiftCardManually(){
        String cardNumber = super.getLine("Enter the card number(length must be 16) you want to add: ");
//        String password = super.getLine("Enter the password you want to set: ");
        db.addRowForGiftcard(cardNumber);
        super.printFancy("Add successful!");
    }

    public void addGiftCardFromFile(){
        String fileName = super.getLine("Enter the file name(path) you want to import: \n(each line per card in the txt file)");
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                db.addRowForGiftcard(line);
            }
            super.printFancy("Add successful!");
        } catch (IOException e) {
            super.printFancy("Failed to read the file, please try again");
        }


    }

}
