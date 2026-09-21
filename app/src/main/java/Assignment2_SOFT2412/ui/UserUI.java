package Assignment2_SOFT2412.ui;

import Assignment2_SOFT2412.data.CreditCard;
import Assignment2_SOFT2412.data.Database;
import com.google.common.base.CaseFormat;

import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.io.Console;
import java.util.Arrays;

public class UserUI extends BaseUI {

//    username of the currently logged in user; null if not logged in
    private String user = null;

    private double balance = 0.0;  // balance of current user

//    num seconds for timeout
    private int timeout = -1;

    public UserUI(Database db, Scanner s, PrintStream ps) {
        super(db,s,ps);
    }

    public boolean isLoggedIn() {
        return user != null;
    }


//    is the "main page"
//     after performing actions (e.g. register/login) user should be taken back here
    public void start() {
        super.mode = "guest";
        boolean shouldQuit = false;
        while (!shouldQuit) {

//        todo display user specific settings

            String option;

            if (isLoggedIn()) {
                option = super.askOptions("What would you like to do next?", "list movies",
                        "filter", "book", "view bookings",  "quit");
            } else {
                option = super.askOptions("What would you like to do next?", "list movies",
                        "filter", "book", "register", "login", "quit");
            }

            switch (option) {
                case "list movies":
                    listMovies();
                    break;
                case "filter":
                    filterMovies();
                    break;
                case "book":
                    bookSession();
                    break;
                case "register":
                    registerAccount();
                    break;
                case "login":
                    login();
                    break;
                case "view bookings":
                    listBookings();
                    break;
                case "quit":
                    shouldQuit = true;
                    break;
            }
        }
        
        super.printFancy("Goodbye!");

    }

    public void listBookings() {
        ResultSet rs = db.getBookings(user);
        db.printAll(printStream, rs);
    }



    private void bookSession() {

//        if not logged in ask user to register or login, before taking them back to the bookings page
        if (!isLoggedIn()) {
            super.printFancy("You need to register or login before making a booking!");
            return;
        }
//        int movieIdToBook;
//        while(true){
//            movieIdToBook = super.getInt("Which movie session would you like to watch (enter ID)?");
//            ResultSet price = db.getMovieSessionPrice(movieIdToBook);
//            if(price != null){
//                break;
//            }
//        }
        int movieIdToBook = super.getInt("Which movie session would you like to watch (enter ID)?");

//        Account exampleAccount = new Account("hans", "123");
//        Booking newBooking = new Booking(movieIdToBook, exampleAccount, conn);
        int child = super.getInt("Enter the number of child tickets you want to book:");
        int student = super.getInt("Enter the number of student tickets you want to book:");
        int adult = super.getInt("Enter the number of adult tickets you want to book:");
        int senior = super.getInt("Enter the number of senior tickets you want to book:");
        String seat_location = super.askOptions("Enter the seat area you want to choose:",
                "front", "middle", "rear");
//        todo fix this up; maybe add fields in the db
//        added multipliers,
        int numTickets = child + student + adult + senior;
        double multipliedTickets = child * 0.5 + student * 0.75 + adult + senior * 0.5;
        double TotalPrice = 0;
        ResultSet rs = db.getMovieSessionPrice(movieIdToBook);
        try{
            if(rs.next()){
                double SessionPrice = rs.getInt(1);
                TotalPrice = SessionPrice * multipliedTickets;
            } else {
                super.printFancy("The Movie ID is invalid!");
                return;
            }
        } catch(SQLException throwables){
            throwables.printStackTrace();
        }
//        String option = super.askOptions(
//                String.format("Please confirm you wish to book %d child ticket(s), %d student ticket(s), %d adult ticket(s), and %d senior tickets for movie session %d in seat location %s",child,student,adult,senior,movieIdToBook, seat_location),
//                "confirm", "cancel");

        String payment = super.askOptions("How would you like to pay? ", "credit card", "gift card", "cancel");

        switch (payment) {
            case "gift card":
                if (paywithGiftcard())  {
                    ResultSet user_bal = db.getAccountsBalance(this.user);
                    try{
                        if(user_bal.next()){
                            double balance = user_bal.getDouble(1);
                            if(balance >= TotalPrice){
                                double new_balance = balance - TotalPrice;
                                db.modifyAccountsBalance(this.user, new_balance);
                            for (int i = 0; i < numTickets; i++) {
                                db.addRowForBooking(movieIdToBook, seat_location,user);
                            }
                            super.printFancy("Booked successfully!");
                        
                            } else {
                                double overdue = TotalPrice - balance;
                                
                                super.printFancy("You do not have enough balance in your account! Excess amount to pay: " + Double.toString(overdue));
                                String ans = super.askOptions("Would you like to pay the amount by card", "y", "n");
                                if(ans == "n"){
                                    super.printFancy("Booking Failed! Returning to main page!");
                                    return;
                                }
                                // if (payWithCreditCard())
                            }
                        } else {
                            super.printFancy("User Balance was not fetched!");
                        }
                    } catch(SQLException throwables) {

                    }

                }
                break;
            case "credit card":
                if (payWithCreditCard()) {
//                    if payment succeeded, we book the tickets
                    for (int i = 0; i < numTickets; i++) {
                        db.addRowForBooking(movieIdToBook, seat_location,user);
                    }
                    super.printFancy("Booked successfully!");
                }
                break;
            case "cancel":
                super.printFancy("Booking cancelled!");
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//                SimpleDateFormat sqlTimeStampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                System.out.println(sqlTimeStampFormat.format(timestamp));
                db.addRowForCanceledBooking(timestamp, user, "user cancelled");
                break;
        }
    }

    private void askToSaveCreditCardDetails(String existingCardNo, String existingCardName, String newCardNo, String newCardName) {
        if ((existingCardNo == null && newCardNo != null) || (existingCardName == null && newCardName != null) || !existingCardNo.equals(newCardNo) || !existingCardName.equals(newCardName)) {
            if (super.getBoolean("Would you like to save your card number and details so you don't need to enter them again in future transactions?")) {
                db.setUserSavedCreditDetails(user, newCardNo,newCardName);
                super.printFancy("Your credit card details have been saved successfully!");
            }
        }
    }

//    returns true if payment successful, false otherwise
    public boolean payWithCreditCard() throws RuntimeException {
        int maxAttempts = 3;
        int noAttempts = 0;
        while (noAttempts < maxAttempts) {
            // get the details
            ResultSet rs= db.getSavedCreditCardDetails(user);
            String savedCardNo = null;
            String savedCardName = null;
            try {
                rs.next();
                savedCardNo = rs.getString("saved_card_number");
                savedCardName = rs.getString("saved_card_name");

            } catch (SQLException throwables) {
//                throwables.printStackTrace();
            }
//            first check if they have details saved
            if (savedCardNo != null && savedCardName != null) {
                boolean autofill = super.getBoolean("It looks like you have your credit card details saved from a previous transaction. Would you like to auto-fill these details?");
                if (autofill) {
                    return true;
                } else {
                    String creditCard = super.getLine("Enter your credit card number: ", timeout);
                    if (creditCard == null) {
                        db.addRowForCanceledBooking(new Timestamp(System.currentTimeMillis()), user, "credit card payment timeout");
                        super.printFancy("Timed out.");
                        return false;
                    }
                    String creditName = super.getLine("Enter the name associated with the credit card: ", timeout);
                    if (creditName == null) {
                        db.addRowForCanceledBooking(new Timestamp(System.currentTimeMillis()), user, "credit card payment timeout");
                        super.printFancy("Timed out.");
                        return false;
                    }
                    if (CreditCard.isValid(creditCard,creditName)) {
                        askToSaveCreditCardDetails(savedCardNo,savedCardName,creditCard,creditName);
                        return true;
                    }
                }

            } else {
                String creditCard = super.getLine("Enter your credit card number: ", timeout);
                if (creditCard == null) {
                    db.addRowForCanceledBooking(new Timestamp(System.currentTimeMillis()), user, "credit card payment timeout");
                    super.printFancy("Timed out.");
                    return false;
                }

                String creditName = super.getLine("Enter the name associated with the credit card: ", timeout);
                if (creditName == null) {
                    db.addRowForCanceledBooking(new Timestamp(System.currentTimeMillis()), user, "credit card payment timeout");
                    super.printFancy("Timed out.");
                    return false;
                }

                if (CreditCard.isValid(creditCard, creditName)) {
                    askToSaveCreditCardDetails(savedCardNo, savedCardName, creditCard, creditName);
                    return true;
                }
            }
            noAttempts++;
            super.printFancy("Invalid credit card details! Please try again.");
        }
        super.printFancy("Too many incorrect attempts!");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        db.addRowForCanceledBooking(timestamp, user, "credit card payment failed");
        return false;
    }



    private boolean paywithGiftcard() {
        int count = 0;
        while(count < 4){
            String serialNoGC = super.getLine("Enter the serial number of the giftcard: ", timeout);
            if (serialNoGC == null) {
                db.addRowForCanceledBooking(new Timestamp(System.currentTimeMillis()), user, "gift card payment timeout");
                super.printFancy("Timed out.");
                return false;
            }
            String serialNo = serialNoGC.substring(0, (serialNoGC.length() - 2));
            if(serialNo.length() != 16){
                super.printFancy("Serial number is invalid! Please try again.");
                count ++;
                continue;
            } else {
                try{
                    ResultSet rs = db.getGiftCard(serialNo);
                    if (rs.next()) {
                        boolean redeemable = rs.getBoolean(2);
                        // double balance = rs.getDouble(3);
                        if(!redeemable){
                            super.printFancy("Giftcard is not redeemable! Please try again.");
                            count ++;
                            continue;
                        } else {
                            int a = db.giftcardRedeemed(serialNo);
                            if(a != 1){
                                super.printFancy("Internal error in system! Please try again.");
                                count ++;
                                continue;
                            }
                            //add the balance of the giftcard to user balance
                            double giftcard_balance = 0;
                            double current_balance= 0;
                            ResultSet acc_bal = db.getAccountsBalance(this.user);
                            if(acc_bal.next()){
                                current_balance = acc_bal.getDouble(1);
                            } else {
                                ;
                            }
                            ResultSet gc = db.getGiftcardBalance(serialNo);
                            if(gc.next()){
                                giftcard_balance = gc.getDouble(1);
                            } else {
                                ;
                            }
                            current_balance += giftcard_balance;
                            db.modifyAccountsBalance(this.user, current_balance);

                            return true;
                        }
                    } else {
                        super.printFancy("Invalid gift card number!");
                    }

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        if(count > 4){
            super.printFancy("Too many incorrect attempts!");
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            db.addRowForCanceledBooking(timestamp, user, "gift card payment failed");
            return false;
        }


        return true;
    }

//    returns boolean whether successful or not
    public boolean registerAccount() {
        String username = super.getLine("Enter a username: ");
        String password = super.getLine("Enter a password: ");

//        remove this as not part of the spec ~Alex 01/11/21
//        String savePassword = super.askOptions("Remember password (You don't need password to log in in the future)?", "yes", "no");

        try {

            ResultSet rs =  db.getAccount(username);
            if (rs.next()) {
//                username is already taken
                throw new Exception();
            }else {
                db.addAccount(username, password, 0); // initial balance is 0
                user = username;
                super.printFancy("Registration complete!");
                return true;
            }

        } catch (Exception e) {
            super.printFancy("Failed to register! The username may already be taken.");
            return false;
        }

    }

    private void login() {
        String username = super.getLine("Enter your username: ");
        ResultSet rs =  db.getAccount( username);
        try {
            if (rs.next()) {
//                Console console = System.console();
//                if (console != null) {
//                    char[] pw = console.readPassword("Enter password: ");
//                    Arrays.fill(pw, '*'); //Overwrite the password in memory for security
//                    //String password=  super.getLine("Enter your password: ");
//                    String password = new String(pw);
                    String password = super.getLine("Enter your password: ");
                    // correct password stored in the db
                    String correctPassword = rs.getString(2);

                    if ( password.equals(correctPassword)) {
                    this.user = username;
                    super.mode="user " + this.user;
                    super.printFancy("Welcome " + username);
                    balance = getBalance();  // get the balance of current user after login
                } else {
                    super.printFancy("Wrong password!");
                }//                } else {
//                    System.out.println("Console is disabled. Quitting!");
//                }
//
            } else {
//                no username exists
//              String option = super.askOptions("What would you like to do?", "login", "register");
                super.printFancy("No user exists");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    private double getBalance() throws SQLException {
        ResultSet rs = db.getAccountsBalance(user);
        if(rs.next()){
            balance = rs.getDouble(1);
        }
        return balance;
    }

    private void addBalance(double amount){
        balance += amount;
        setBalanceToDb(balance);
    }

    private void subtractBalance(double amount){
        balance -= amount;
        setBalanceToDb(balance);
    }

    private void setBalanceToDb(double amount){
        balance = amount;
        db.modifyAccountsBalance(user, balance);
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

}
