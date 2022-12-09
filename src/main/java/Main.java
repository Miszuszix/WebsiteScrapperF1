import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args)throws SQLException {
        String url = "https://www.formula1.com/en/results.html";
        String url_connect = "jdbc:mysql://localhost:3307/formla1";
        String user = "masi";
        String password = "toto";

        LocalTime startTime = LocalTime.now();
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(url_connect, user, password);
            System.out.println("Udało się");
        } catch (SQLException e) {
            System.out.println("Nie udało się");
        }
        int year = 1950;
        int year_champion;
        boolean created = false;
        boolean createOrDrop;
        Scanner scanner = new Scanner(System.in);
        int number_of_races;
//        File file = new File("odpowiedzi.txt");
//        FileWriter writer = new FileWriter("odpowiedzi.txt");

        Statement statement = connection.createStatement();

        System.out.println("Jeśli chcesz stworzyć bazę danych - wpisz true");
        System.out.println("Jeśli chcesz usunąć bazę danych - wpisz false");
        createOrDrop =  scanner.nextBoolean();

        if(createOrDrop){
            for(int i = 0; i < 72; i++){
                String createTable = "CREATE TABLE SEZON" + year +
                        " (id_wyscigu int NOT NULL PRIMARY KEY AUTO_INCREMENT, data_wyscigu varchar(11)," +
                        " nazwa_wyscigu varchar(20), imie varchar(20), nazwisko varchar(20), winner_car varchar(30)," +
                        " okrążenia varchar(4), czas varchar(11))";
                String createChampions = "CREATE TABLE champions (year int NOT NULL PRIMARY KEY, name varchar(20)," +
                        " surname varchar(20), nationality varchar(3), team varchar(30), points int)";
                statement.execute(createTable);
                if(!created){
                    statement.execute(createChampions);
                    created = true;
                }

                WebsiteChampions websiteChampions = new WebsiteChampions(url, year);
                Elements championslist = websiteChampions.getDoc().getElementsByTag("tbody");
                Elements champion = championslist.get(0).getElementsByTag("tr");
                String championName = champion.get(0).getElementsByClass("hide-for-tablet").text();//imie
                String championSurname = champion.get(0).getElementsByClass("hide-for-mobile").text();//nazwisko
                String championNationality = champion.get(0).getElementsByClass("dark semi-bold uppercase").text();//narodowość
                String championCar = champion.get(0).getElementsByClass("grey semi-bold uppercase ArchiveLink").text();//autko
                String championPoints = champion.get(0).getElementsByClass("dark bold").text();//punkty

                PreparedStatement champions = connection.prepareStatement("INSERT INTO champions(year, name, surname, nationality, team, points) VALUES(?, ?, ?, ?, ?, ?)");
                champions.setString(1, String.valueOf(year));
                champions.setString(2, String.valueOf(championName));
                champions.setString(3, String.valueOf(championSurname));
                champions.setString(4, String.valueOf(championNationality));
                champions.setString(5, String.valueOf(championCar));
                champions.setString(6, String.valueOf(championPoints));

//                String insertIntoChampions = "INSERT INTO champions(year, name, surname, nationality, team, points)" +
//                        " VALUES(" + year + ", '" +
//                        championName + "', '" + championSurname + "', '" + championNationality + "', '" + championCar +
//                        "', " + championPoints + ")";
//                statement.execute(insertIntoChampions);
                champions.execute();

                WebsiteRaces websiteRaces = new WebsiteRaces(url, year);
                Elements raceslist = websiteRaces.getDoc().getElementsByTag("tbody");
                Elements races = raceslist.get(0).getElementsByTag("tr");
                List<Elements> race = new ArrayList<>();
                List<String> races_names = new ArrayList<>();
                List<String> winners_names_and_surnames = new ArrayList<>();
                List<String> winners_names = new ArrayList<>();
                List<String> winners_surnames = new ArrayList<>();
                List<String> race_date = new ArrayList<>();
                List<String> winner_car = new ArrayList<>();
                List<String> race_laps = new ArrayList<>();
                List<String> winner_time = new ArrayList<>();
                for (Element e:races) {
                    race.add(e.getElementsByClass("dark bold"));
                    race_date.add(e.getElementsByClass("dark hide-for-mobile").text());
                    winner_car.add(e.getElementsByClass("semi-bold uppercase ").text());
                    race_laps.add(e.getElementsByClass("bold hide-for-mobile").text());
                    winner_time.add(e.getElementsByClass("dark bold hide-for-tablet").text());
                }
                for (Elements e: race) {
                    winners_names_and_surnames.add(e.get(1).getElementsByClass("hide-for-tablet").text());
                    winners_names_and_surnames.add(e.get(1).getElementsByClass("hide-for-mobile").text());
                    winners_names.add(e.get(1).getElementsByClass("hide-for-tablet").text());
                    winners_surnames.add(e.get(1).getElementsByClass("hide-for-mobile").text());
                }
                for (Elements e: race) {
                    races_names.add(e.get(0).getElementsByTag("a").text());
                }

                number_of_races = races_names.size();

                for(int j = 0; j < number_of_races; j++){
                    PreparedStatement season = connection.prepareStatement("INSERT INTO sezon" + year + " (data_wyscigu, nazwa_wyscigu, imie, nazwisko, winner_car, okrążenia, czas) VALUES (?, ?, ?, ?, ?, ?, ?)");
                    season.setString(1, String.valueOf(race_date.get(j)));
                    season.setString(2, String.valueOf(races_names.get(j)));
                    season.setString(3, String.valueOf(winners_names.get(j)));
                    season.setString(4, String.valueOf(winners_surnames.get(j)));
                    season.setString(5, String.valueOf(winner_car.get(j)));
                    season.setString(6, String.valueOf(race_laps.get(j)));
                    season.setString(7, String.valueOf(winner_time.get(j)));

//                    String insertIntoTable = "INSERT INTO sezon" + year +
//                            " (data_wyscigu, nazwa_wyscigu, imie, nazwisko, winner_car, okrążenia, czas)" +
//                            " VALUES ('" + race_date.get(j) +
//                            "', '"  + races_names.get(j) + "', '" + winners_names.get(j) + "', '" + winners_surnames.get(j) +
//                            "', '"  + winner_car.get(j) + "', '" + race_laps.get(j) + "', '" + winner_time.get(j) +  "')";
//                    statement.execute(insertIntoTable);
                    season.execute();
                }
                System.out.println("Utworzono rok: " + year);
                year++;
            }
            System.out.println("Utworzono bazę danych");
        }else{
            String dropDatabase = "DROP DATABASE formla1";
            statement.execute(dropDatabase);
            System.out.println("Usunięto bazę danych");
        }
        LocalTime endTime = LocalTime.now();
        System.out.println("Program wykonał swoje zadanie w " + Math.abs(endTime.getSecond() - startTime.getSecond()) + " sekund/y");
    }
}