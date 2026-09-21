package Assignment2_SOFT2412.data;

import org.postgresql.jdbc2.optional.SimpleDataSource;
import org.postgresql.util.PSQLException;

import javax.xml.transform.Result;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.*;
import java.text.SimpleDateFormat;

public class Database {

    private Connection c;

    public Database(Connection c) {
        this.c = c;
    }

    /**
     * Set connection to database server.
     */
    public static Connection connectToSql() {
        Connection c = null;

        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://home.xsourse.cc:5432/postgres", "postgres", "123");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    //    connects to test db
    public static Connection connectToSqlTest() {
        Connection c = null;

        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://home.xsourse.cc:8083/postgres", "postgres", "123");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    /**
     * modification functions for cinema staff with each table.
     *
     * @return rowsAffected will return how many rows have been changed after the sql executed. Will return 0 if an exception has been thrown.
     */
    public int addRowForMovie(String name, String classification) {

        int rowsAffected = 0;
        try {
            String sql = "INSERT INTO movie VALUES ( ?, ? );";
            PreparedStatement statement = null;
            statement = c.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, classification);
            rowsAffected = statement.executeUpdate();
            statement.close();

        } catch (Exception e) {
//            e.printStackTrace();
        }
        return rowsAffected;
    }

    public int addRowForCinema(String name) {

        int rowsAffected = 0;
        try {
            String sql = "INSERT INTO cinema VALUES (?);";
            PreparedStatement statement = null;
            statement = c.prepareStatement(sql);
            statement.setString(1, name);
            rowsAffected = statement.executeUpdate();
            statement.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowsAffected;
    }

    //    todo
    public int addRowForMovieShowing(String movie, String cinema, String dateRange) {

        int rowsAffected = 0;
        try {
//            String sql = "INSERT INTO movie_showing VALUES ( ?, ?, ?, ?::daterange, ?::float8::numeric::money, ? );";

            String sql = "INSERT INTO movie_showing(movie, cinema, available_dates) VALUES ( ?, ?, ?::daterange );";

            PreparedStatement statement = null;
            statement = c.prepareStatement(sql);
            statement.setString(1, movie);
            statement.setString(2, cinema);
            statement.setString(3, dateRange);
            rowsAffected = statement.executeUpdate();
            statement.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowsAffected;
    }

    public int addRowForBooking(Integer movieSession, String seat_location, String account) {

        int rowsAffected = 0;
        try {
            String sql = "INSERT INTO booking(movie_session, seat_location, account) VALUES ( ?, ?, ? );";
            PreparedStatement statement = null;
            statement = c.prepareStatement(sql);
            statement.setInt(1, movieSession);
            statement.setString(2, seat_location);
            statement.setString(3, account);
            rowsAffected = statement.executeUpdate();
            statement.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowsAffected;
    }

    public int deleteRowForMovieSession(int id) {

        int rowsAffected = 0;
        try {
            PreparedStatement statement = null;
            String sql = "DELETE FROM movie_session WHERE id = ?";
            statement = c.prepareStatement(sql);
            statement.setInt(1, id);
            rowsAffected = statement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowsAffected;
    }

    public int deleteRowForCinema(String cinema) {

        int rowsAffected = 0;
        try {
            String sql = "DELETE FROM cinema WHERE cinema_name = ?";
            PreparedStatement statement = null;
            statement = c.prepareStatement(sql);
            statement.setString(1, cinema);
            rowsAffected = statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowsAffected;
    }

    public int deleteRowForMovie(String name) {

        int rowsAffected = 0;
        try {

            int movie_session_id;
            PreparedStatement statement = null;

            String sql1 = "SELECT mse.id, movie_showing \n" +
                    "FROM movie_session mse LEFT OUTER JOIN movie_showing msh ON (mse.movie_showing = msh.id) \n" +
                    "WHERE msh.movie = ?";
            statement = c.prepareStatement(sql1);
            statement.setString(1, name);
            ResultSet rs_id = statement.executeQuery();

            while(rs_id.next()) {
                movie_session_id = rs_id.getInt("id");

                rowsAffected += deleteRowForBookingUsingMovieSession(movie_session_id);

                rowsAffected += deleteRowForMovieSession(movie_session_id);
            }

            rowsAffected += deleteRowForMovieShowingUsingMovie(name);

            String sql4 = "DELETE FROM movie WHERE name = ?";
            statement = c.prepareStatement(sql4);
            statement.setString(1, name);
            rowsAffected += statement.executeUpdate();

            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowsAffected;
    }

    public int deleteRowForMovieShowingUsingId(int id) {

        int rowsAffected = 0;
        try {
            String sql = "DELETE FROM movie_showing WHERE id = ?";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setInt(1, id);
            rowsAffected += statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowsAffected;
    }

    public int deleteRowForMovieShowingUsingMovie(String movie) {

        int rowsAffected = 0;
        try {
            String sql = "DELETE FROM movie_showing WHERE movie = ?";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, movie);
            rowsAffected += statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowsAffected;
    }

    public int deleteRowForBookingUsingMovieSession(int sessionId) {

        int rowsAffected = 0;
        try {
            String sql = "DELETE FROM booking WHERE movie_session = ?";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setInt(1, sessionId);
            rowsAffected += statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowsAffected;
    }

    public int modifyRowForMovie(String name, String classification) {

        int rowsAffected = 0;
        try {
            String sql = "UPDATE movie SET classification = ? WHERE name = ?";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, classification);
            statement.setString(2, name);
            rowsAffected = statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowsAffected;
    }

    // todo
    public int modifyRowForMovieShowing(int id, String movie, String cinema, String dateRange) {

        int rowsAffected = 0;
        try {
            String sql = "UPDATE movie_showing SET movie = ?, cinema = ?, available_dates = ?::daterange, WHERE id = ?;";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, movie);
            statement.setString(2, cinema);
            statement.setString(3, dateRange);
            statement.setInt(4, id);
            rowsAffected = statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowsAffected;
    }

    public int modifyRowForMovieSession(int id, String date, int seats, double price, String screenSize) {

        int rowsAffected = 0;
        try {
            String sql = "UPDATE movie_session SET showing_date = ?::date, seats_total = ?, price = ?::float8::numeric::money, screen_size = ? WHERE id = ?";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, date);
            statement.setInt(2, seats);
            statement.setDouble(3, price);
            statement.setString(4, screenSize);
            statement.setInt(5, id);
            rowsAffected = statement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowsAffected;
    }

//    public static int addRowForMovieSession(Connection c, int movieShwoing, String showing_date, int seatsTotal, int price, int screenSize) {
//
//    }

    /**
     * Get session status for all sessions from db for the next week
     */
    public ResultSet getSessions(String today, String nextWeekDate) {
        try {

            String sql = "SELECT id, movie, classification, synopsis, director, actor_cast, cinema, showing_date, booked_seats, seats_total, price, screen_size FROM movie_session_v WHERE showing_date between ?::date AND ?::date";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, today);
            statement.setString(2, nextWeekDate);
            return statement.executeQuery();
//            printAll(c, statement);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet getSummarySession() {
        try {
            String sql = "SELECT movie, classification, showing_date, seat_location, account FROM booking_v";
//            String sql = "SELECT movie_session, count(movie_session) AS booked, seats_total, (seats_total - count(movie_session)) AS available_seats\n" +
//                    "FROM public.movie_session mse LEFT OUTER JOIN public.movie_showing msh ON mse.movie_showing = msh.id \n" +
//                    "LEFT OUTER JOIN public.booking b ON mse.id = b.movie_session\n" +
//                    "GROUP BY movie_session, seats_total\n" +
//                    "ORDER BY movie_session";
            PreparedStatement statement = c.prepareStatement(sql);
            return statement.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void printAll(PrintStream ps, ResultSet rs) {
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int colume = rsmd.getColumnCount();
            for (int i = 1; i <= colume; i++) {
                String columnName = rsmd.getColumnName(i);
                ps.printf("%-20s", columnName);

            }
            ps.println("\n");
            while (rs.next()) {
                for (int j = 1; j <= colume; j++) {
                    String cval = rs.getString(j);
                    ps.printf("%-20s", cval);
                }
                ps.println("");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace(ps);
        }

    }

    public void saveAll(PrintStream ps, ResultSet rs) {
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int colume = rsmd.getColumnCount();
            FileWriter myWriter = new FileWriter("canceled_bookings.txt");
            for (int i = 1; i <= colume; i++) {
                String columnName = rsmd.getColumnName(i);
                ps.printf("%-30s", columnName);
                myWriter.write(String.format("%-30s", columnName));
            }

            ps.println("\n");
            myWriter.write("\n");
            while (rs.next()) {
                for (int j = 1; j <= colume; j++) {
                    String cval = rs.getString(j);
                    ps.printf("%-30s", cval);
                    myWriter.write(String.format("%-30s", cval));
                }
                ps.println("");
                myWriter.write("\n");
            }
            myWriter.close();
            ps.println("File saved as canceled_bookings.txt");
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace(ps);
        }

    }

    public ResultSet getSessionsFilterCinema(String cinema, String today, String nextWeekDate) {
        try {
            String sql = "SELECT id, movie, classification, synopsis, director, actor_cast, cinema, showing_date, booked_seats, seats_total, price, screen_size FROM movie_session_v WHERE cinema ILIKE ? AND showing_date between ?::date AND ?::date";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, cinema);
            statement.setString(2, today);
            statement.setString(3, nextWeekDate);
            return statement.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public ResultSet getSessionsFilterScreenSize(String screenSize, String today, String nextWeekDate) {
        try {
            String sql = "SELECT id, movie, classification, synopsis, director, actor_cast, cinema, showing_date, booked_seats, seats_total, price, screen_size FROM movie_session_v WHERE screen_size ILIKE ? AND showing_date between ?::date AND ?::date";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, screenSize);
            statement.setString(2, today);
            statement.setString(3, nextWeekDate);
            return statement.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public ResultSet getSessionsFilterClassification(String classification, String today, String nextWeekDate) {
        try {
            String sql = "SELECT id, movie, classification, synopsis, director, actor_cast, cinema, showing_date, booked_seats, seats_total, price, screen_size FROM movie_session_v WHERE classification ILIKE ? AND showing_date between ?::date AND ?::date";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, classification);
            statement.setString(2, today);
            statement.setString(3, nextWeekDate);
            return statement.executeQuery();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public ResultSet getSessionsFilterPrice(double maxPrice, String today, String nextWeekDate) {
        try {
            String sql = "SELECT id, movie, classification, synopsis, director, actor_cast, cinema, showing_date, booked_seats, seats_total, price, screen_size FROM movie_session_v WHERE price <= ?::float8::numeric::money AND showing_date between ?::date AND ?::date";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setDouble(1, maxPrice);
            statement.setString(2, today);
            statement.setString(3, nextWeekDate);

            return statement.executeQuery();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public int addAccount(String username, String password, double balance) {
        int rowsAffected = 0;
        try {
            String sql = "INSERT INTO account(username, password, balance) VALUES (?, ?, ?::float8::numeric::money);";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setDouble(3, balance);
            rowsAffected = statement.executeUpdate();
            statement.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowsAffected;
    }

    public ResultSet getAccount(String username) {
        try {
            String sql = "SELECT username, password, balance FROM account WHERE username = ?";
            PreparedStatement statement= c.prepareStatement(sql);
            statement.setString(1, username);

            return statement.executeQuery();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public ResultSet getAccountsBalance(String username) {
        try {
            String sql = "SELECT balance FROM account WHERE username = ?";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, username);

            return statement.executeQuery();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public ResultSet getGiftcardBalance(String serial) {
        try {
            String sql = "SELECT balance FROM giftcards WHERE serialNo = ?";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, serial);

            return statement.executeQuery();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public ResultSet modifyAccounts(String username, String password, boolean save, double balance) {
        try {
            String sql = "UPDATE account SET password = ?, save = ?, balance = ? WHERE username = ?";
            PreparedStatement statement= c.prepareStatement(sql);
            statement.setString(1, password);
            statement.setBoolean(2, save);
            statement.setDouble(3, balance);
            statement.setString(4, username);
            return statement.executeQuery();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public int modifyAccountsBalance(String username, double balance) {
        
        int rowsAffected = 0;
        try {
            String sql = "UPDATE account SET balance = ?::float8::numeric::money WHERE username = ?";
            PreparedStatement statement= c.prepareStatement(sql);
            statement.setDouble(1, balance);
            statement.setString(2, username);
            rowsAffected = statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowsAffected;
    }

    public ResultSet getMovieSessionPrice(int id) {
        try {
            String sql = "SELECT price FROM movie_session WHERE id = ?";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setInt(1, id);

            return statement.executeQuery();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public ResultSet getGiftCard(String cardNo) {
        try {
            String sql = "SELECT serialNo, redeemable FROM giftcards WHERE serialNo = ?";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, cardNo);

            return statement.executeQuery();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public int giftcardRedeemed(String serialNo) {

        int rowsAffected = 0;
        try {
            String sql = "UPDATE giftcards SET redeemable = ? WHERE  serialNo = ?";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setInt(1, 0);
            statement.setString(2, serialNo);
            rowsAffected = statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowsAffected;
    }

    public int addRowForGiftcard(String serialNo){
        int rowsAffected = 0;
        try {
            String sql = "INSERT INTO giftcards (serialNo, redeemable) VALUES (?, ?)";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, serialNo);
            statement.setInt(2, 1);
            rowsAffected = statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowsAffected;
    }

    public ResultSet getStaffAccount(String staffname) {
        try {
            String sql = "SELECT staffname, password FROM staffaccount WHERE staffname = ?";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, staffname);

            return statement.executeQuery();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public ResultSet getManagerAccount(String managername) {
        try {
            String sql = "SELECT managername, password FROM manageraccount WHERE managername = ?";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, managername);

            return statement.executeQuery();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public int addStaffAccount(String staffname, String password) {
        int rowsAffected = 0;
        try {
            String sql = "INSERT INTO staffaccount(staffname, password) VALUES (?, ?);";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, staffname);
            statement.setString(2, password);
            rowsAffected = statement.executeUpdate();
            statement.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowsAffected;
    }

    public int deleteStaffAccount(String staffname) {

        int rowsAffected = 0;
        try {
            String sql = "DELETE FROM staffaccount WHERE staffname = ?";
            PreparedStatement statement = null;
            statement = c.prepareStatement(sql);
            statement.setString(1, staffname);
            rowsAffected = statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowsAffected;
    }

    public ResultSet getBookings(String username) {
        try {
            String sql = "SELECT movie, classification, showing_date, seat_location FROM booking_v WHERE account = ?";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, username);

            return statement.executeQuery();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public int addMovieSession(int movieShowing, String showingDate, int seatsTotal, double price, String screenSize) {
        int rowsAffected = 0;
        try {
            String sql = "INSERT INTO movie_session(movie_showing, showing_date, seats_total, price, screen_size) VALUES ( ?, ?::daterange, ?, ?::float8::numeric::money, ? );";
            PreparedStatement statement = null;
            statement = c.prepareStatement(sql);
            statement.setInt(1, movieShowing);
            statement.setString(2, showingDate);
            statement.setInt(3, seatsTotal);
            statement.setDouble(4, price);
            statement.setString(5, screenSize);

            rowsAffected = statement.executeUpdate();
            statement.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rowsAffected;
    }

    public ResultSet getSavedCreditCardDetails(String username) {
        try {
            String sql = "SELECT saved_card_number, saved_card_name FROM account WHERE username = ?";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, username);

            return statement.executeQuery();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

//    returns true if the given user has credit card details saved
    public boolean userHasCreditCardSaved(String username) {
        try {
            String sql = "SELECT saved_card_number, saved_card_name FROM account WHERE username = ?";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, username);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getString("saved_card_number") != null && rs.getString("saved_card_name") != null;
            } else {
//                the user doesnt even exist!
                return false;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public int setUserSavedCreditDetails(String username, String cardNo, String cardName) {
        int rowsAffected = 0;
        try {
            String sql = "UPDATE account SET saved_card_number, saved_card_name WHERE  username = ?";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, cardNo);
            statement.setString(3, cardName);
            rowsAffected = statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowsAffected;
    }

    public ResultSet getCanceledTransaction(){

       try {         String sql = "SELECT * FROM canceled_booking";
                PreparedStatement statement = c.prepareStatement(sql);
                return statement.executeQuery();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return null;
    }

    public int addRowForCanceledBooking(Timestamp date_time, String account, String reason) {
        int rowsAffected = 0;
        try {
            String sql = "INSERT INTO canceled_booking(date_time, account, reason) VALUES ( ?, ?, ? );";
            PreparedStatement statement = c.prepareStatement(sql);
            statement.setTimestamp(1, date_time);
            statement.setString(2, account);
            statement.setString(3, reason);
            rowsAffected = statement.executeUpdate();
            statement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rowsAffected;
    }

}
