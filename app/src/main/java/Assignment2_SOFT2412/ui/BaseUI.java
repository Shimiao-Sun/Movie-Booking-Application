package Assignment2_SOFT2412.ui;

import Assignment2_SOFT2412.data.Database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseUI {

    protected Scanner s;
    protected Database db;
    protected PrintStream printStream;

    protected String mode = "";

    protected String today;
    protected String nextWeekDate;

    protected BaseUI(Database db, Scanner s, PrintStream ps) {
        this.db = db;
        this.s = s;
        this.printStream = ps;
    }

    public void setDefaultDate() {
        this.today = "2021-11-01";
        this.nextWeekDate = "2021-11-07";
    }

    public void setDate() {
        Date dateNow = new Date();
        Date nextWeek = null;
        try {
            SimpleDateFormat df1 = new SimpleDateFormat("u");
            String weekname = df1.format(dateNow);
            if (weekname.equals("1")) {
                nextWeek = addDate(dateNow, 6);
            } else if (weekname.equals("2")) {
                nextWeek = addDate(dateNow, 5);
            } else if (weekname.equals("3")) {
                nextWeek = addDate(dateNow, 4);
            } else if (weekname.equals("4")) {
                nextWeek = addDate(dateNow, 3);
            } else if (weekname.equals("5")) {
                nextWeek = addDate(dateNow, 2);
            } else if (weekname.equals("6")) {
                nextWeek = addDate(dateNow, 1);
            } else {
                nextWeek = addDate(dateNow, 0);
            }
            SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd ");
            this.today = df2.format(dateNow);
            this.nextWeekDate = df2.format(nextWeek);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Date addDate(Date date, long day) throws ParseException {
        long time = date.getTime();
        day = day * 24 * 60 * 60 * 1000;
        time += day;
        return new Date(time);
    }

    protected void printFancy(String msg) {
        printStream.println("[" + mode + "] " + msg);
    }

//    returns the matched option
    protected String askOptions(String msg, String... options) {
        String optionString =  List.of(options).stream()
                .map(String::toLowerCase)
                .collect(Collectors.joining("/"));

        while (true) {
            this.printFancy(msg + " (" + optionString + "): ");
            String line = null;
            while (true) {
                line = s.nextLine().toLowerCase().strip();
                if (!line.equals("")) break;
            }

            String finalLine = line;
            if (List.of(options).stream().map(String::toLowerCase).anyMatch(s->s.equals(finalLine))) return line;
        }
    }

    protected String getLine(String msg) {
        this.printFancy(msg);
        return  s.nextLine().strip();
    }

    protected String getLine(String msg, int seconds) {
        if (seconds < 0) return getLine(msg);

//        if possible, figure how to read in from the scanner;
        this.printFancy(msg);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        long startTime = System.currentTimeMillis();
        try {
            while ((System.currentTimeMillis() - startTime) < seconds * 1000
                    && !in.ready()) {
//                System.out.println(System.currentTimeMillis() - startTime);
            }
            if (in.ready()) return in.readLine();
            else return null;
        } catch (IOException e) {
            return null;
        }



    }

    protected double getDouble(String msg) {
        while (true) {
            try {
                this.printFancy(msg);
                return s.nextDouble();
            } catch (InputMismatchException e) {

            }
        }

    }

    protected boolean getBoolean(String msg) {
        return askOptions(msg, "yes", "no").equals("yes");
    }

    protected int getInt(String msg) {
        while (true) {
            try {
                this.printFancy(msg);
                return s.nextInt();
            } catch (InputMismatchException e) {

            }
        }
    }

    public void filterMovies() {
        String filterType = askOptions("What would you like to filter by?",
                "screen size", "price", "classification", "cinema");

        ResultSet rs;
        switch (filterType) {
            case "screen size":
                String screenSize = askOptions("Which screen size would you like to filter by?", "bronze", "silver", "gold");
                rs= db.getSessionsFilterScreenSize( screenSize,today, nextWeekDate);
                db.printAll(printStream, rs);
                break;
            case "price":
                double price = getDouble("What is your maximum budget?");
                rs= db.getSessionsFilterPrice( price,today, nextWeekDate);
                db.printAll(printStream,rs);
                break;
            case "classification":
                String classif = askOptions("Which classification would you like to filter by?", "G", "PG", "M", "MA15+", "R18+");
                rs= db.getSessionsFilterClassification( classif,today, nextWeekDate);
                db.printAll(printStream,rs);
                break;
            case "cinema":
                String cinema = getLine("Which cinema would you like to filter by?");
                rs= db.getSessionsFilterCinema( cinema,today, nextWeekDate);
                db.printAll(printStream,rs);
                break;
        }
    }

    public void listMovies() {
        printFancy("Here's a list of available movies:");

        ResultSet rs= db.getSessions(today, nextWeekDate);
        db.printAll(printStream, rs);
    }

    public abstract void start() throws Exception;



}
