import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AlbiToMediLog implements Consumer<List<String>> {

  public static final int AL_TYPE = 0;
  public static final int AL_DATE = 1;
  public static final int AL_TIME = 2;
  public static final int AL_VALUE1 = 3;
  public static final int AL_VALUE2 = 4;
  public static final int AL_NOTES = 7;
  public static final int ML_TIMESTAMP = 0;
  public static final int ML_COMMENT = 1;
  public static final int ML_TYPE = 2;
  public static final int ML_VALUE1 = 3;
  public static final int ML_VALUE2 = 4;
  public static final int ML_VALUE4 = 6;
  public static final int ML_PROFILE_ID = 8;
  public final List<String> result = new ArrayList<>();
  public final Set<String> timestamps = new HashSet<>();

  @Override
  public void accept(List<String> strings) {
    if (!"Type,Date,Time,Value1,Value2,Unit,Category,Notes".equals(strings.get(0))) throw new IllegalStateException();
    for (int i = 1; i < strings.size(); i++) {
      String[] al = (strings.get(i) + ",-").split(",");
      if (al.length != 9) throw new IllegalStateException();
      String[] ml = new String[11];
      Arrays.fill(ml, "");
      if (!timestamps.add(al[AL_DATE] + al[AL_TIME] + al[AL_TYPE])) continue;
      ml[ML_TIMESTAMP] = al[AL_DATE] + " " + al[AL_TIME];
      ml[ML_COMMENT] = "\"" + al[AL_NOTES] + "\"";
      ml[ML_VALUE1] = al[AL_VALUE1];
      ml[ML_VALUE2] = al[AL_VALUE2];
      ml[ML_PROFILE_ID] = "1";
      switch (al[AL_TYPE]) {
        case "Weight":
          ml[ML_TYPE] = "1";
          break;
        case "Blood Pressure":
          ml[ML_TYPE] = "2";
          ml[ML_VALUE4] = "0";
          break;
        default:
          throw new IllegalStateException();
      }
      result.add(String.join(",", ml));
    }

  }

  public String readFolder() throws IOException {
    Pattern pattern = Pattern.compile("healthtracker_export_(\\d{8})_(\\d{4})\\.csv");
    Path folder = Paths.get(".");
    List<String> files = Files.list(folder).map(p -> p.getFileName().toString()).sorted().collect(Collectors.toList());
    for (String file : files) {
      Matcher matcher = pattern.matcher(file);
      if (!matcher.matches()) continue;
      accept(Files.readAllLines(folder.resolve(file)));
    }
    return "timestamp,comment,type,value1,value2,value3,value4,attachment,profile_id,category_id,tags\n" +
        result.stream().map(s -> s + "\n").collect(Collectors.joining());
  }

  public static void main(String[] args) throws IOException {
    String data = new AlbiToMediLog().readFolder();
    Files.writeString(Paths.get("MediLog-Data.csv"), data);
  }
}
