import java.util.ArrayList;
import java.util.List;

public class RacesRepository {
   private List<String> races_names;
   private List<String> winners_names;
   private List<String> winners_surnames;
   private List<String> winners_names_and_surnames;

    public RacesRepository(List<String> races_names, List<String> winners_names, List<String> winners_surnames, List<String> winners_names_and_surnames) {
        this.races_names = races_names;
        this.winners_names = winners_names;
        this.winners_surnames = winners_surnames;
        this.winners_names_and_surnames = winners_names_and_surnames;
    }

    public List<String> getRaces_names() {
        return races_names;
    }

    public List<String> getWinners_names() {
        return winners_names;
    }

    public List<String> getWinners_surnames() {
        return winners_surnames;
    }

    public List<String> getWinners_names_and_surnames() {
        return winners_names_and_surnames;
    }

    public String raceWinnerInfo(int raceNumber){
        return "The winner of the " + races_names.get(raceNumber) +
                " GP is " + winners_names.get(raceNumber) +
                " " +
                winners_surnames.get(raceNumber);
    }
}
