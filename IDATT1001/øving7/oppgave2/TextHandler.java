import java.util.*;

public class TextHandler {
    String s;
    String[] bad;

    public TextHandler(String s) {
        this.s = s;
        this.bad = new String[] { ".", ",", "!", "?", " " };
    }

    public int amountWords() {
        return this.s.split(" ").length;
    }
    
    public double avgWordLen() {
        int totalWords = this.s.split(" ").length;
        int totalLength = 0;
        for (String str : this.s.split("")) {
            if(!Arrays.stream(this.bad).anyMatch(str::equals)) {
                totalLength++;
            }
        }
        return (double) totalLength / (double) totalWords;
    }

    public double avgPeriodLen() {
        //StringBuilder delimeters = new StringBuilder();
        //for (String delim : this.bad) {
        //    if (delim.equals("?"))
        //        delimeters.append("\\?");
        //    else
        //        delimeters.append(delim + "|");
        //}
        //delimeters.deleteCharAt(delimeters.length()-1);  // fjern siste regex OR 
        //delimeters.deleteCharAt(delimeters.length()-1);  // fjern uønsket mellomrom
        //delimeters.deleteCharAt(delimeters.length()-1);  // fjern uønsket regex OR '|'
        String delimeters = "\\.|,|!|\\?";
        String[] intervals = this.s.split(delimeters);
        int amountIntervals = intervals.length;
        int totalWords = 0;

        for (String interval : intervals) {
            String[] words = interval.split(" ");
            for (String word : words) {
                if (!word.equals(" "))
                    totalWords++;
            }
        }

        return (double) totalWords / (double) amountIntervals;
    }

    public String searchAndReplace(String toReplace, String replacement) {
        StringBuilder newS = new StringBuilder();
        for (String str : this.s.split(" ")) {
            if (!str.equals(toReplace)) {
                newS.append(str + " ");
            } else {
                newS.append(replacement + " ");
            }
        }
        return newS.toString();

    }

    @Override
    public String toString() {
        return this.s;
    }

    public String toBIG() {
        return this.s.toUpperCase();
    }
}
