package Assignment2_SOFT2412.data;

import netscape.javascript.JSObject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class CreditCard {
    //    maps from card number to owner name
    private static HashMap<String, String> validCards = null;

    private static String readFile(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();
            return everything;
        } finally {
            br.close();
        }
    }

    private static void initValidCards() {
        try {
            validCards = new HashMap<>();
            String fileContents = readFile("assets/credit_cards.json");
            JSONArray ja = new JSONArray(fileContents);
            for (int i = 0; i < ja.length(); i++) {
               JSONObject jo = ja.getJSONObject(i);
               String name = jo.getString("name");
               String number = jo.getString("number");
               validCards.put(number, name);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isValid(String number, String owner) {
        if (validCards == null) initValidCards();

        String ownerCard = validCards.get(number);
        if (ownerCard == null) return false;
        return ownerCard.equals(owner);
    }
}
