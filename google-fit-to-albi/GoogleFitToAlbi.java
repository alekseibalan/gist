import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GoogleFitToAlbi implements BiConsumer<LocalDate, List<String>> {

  private final ZoneId zoneId;
  public final List<String> result;

  public GoogleFitToAlbi(ZoneId zoneId) {
    this.zoneId = zoneId;
    this.result = new ArrayList<>();
    result.add("Type,Date,Time,Value1,Value2,Unit,Category,Notes");
  }

  @Override
  public void accept(LocalDate localDate, List<String> strings) {
    List<String> header = Arrays.asList((strings.get(0) + ",-").split(","));
    int indexTime = header.indexOf("Start time");
    int indexSystolic = header.indexOf("Average systolic blood pressure (mmHg)");
    int indexDiastolic = header.indexOf("Average diastolic blood pressure (mmHg)");
    int indexWeight = header.indexOf("Average weight (kg)");
    for (int i = 1; i < strings.size(); i++) {
      List<String> line = Arrays.asList((strings.get(i) + ",-").split(","));
      assert line.size() == header.size();
      OffsetTime offsetTime = OffsetTime.parse(line.get(indexTime));
      ZonedDateTime dateTime = ZonedDateTime.of(localDate, offsetTime.toLocalTime(), offsetTime.getOffset())
          .withZoneSameInstant(zoneId);
      String date = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE);
      String time = dateTime.format(DateTimeFormatter.ISO_LOCAL_TIME);
      String dataSystolic = line.get(indexSystolic);
      String dataDiastolic = line.get(indexDiastolic);
      String dataWeight = line.get(indexWeight);
      assert !dataSystolic.isEmpty() ^ dataDiastolic.isEmpty();
      if (!dataSystolic.isEmpty()) {
        double systolic = Double.parseDouble(dataSystolic);
        double diastolic = Double.parseDouble(dataDiastolic);
        result.add(String.format("Blood Pressure,%s,%s,%.0f,%.0f,mmHg,,", date, time, systolic, diastolic));
      }
      if (!dataWeight.isEmpty()) {
        double weight = Double.parseDouble(dataWeight);
        result.add(String.format("Weight,%s,%s,%.1f,,kg,,", date, time, weight));
      }
    }
  }

  public List<String> readFolder(String path) throws IOException {
    Pattern pattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})\\.csv");
    Path folder = Paths.get(path);
    List<String> files = Files.list(folder).map(p -> p.getFileName().toString()).sorted().collect(Collectors.toList());
    for (String file : files) {
      Matcher matcher = pattern.matcher(file);
      if (!matcher.matches()) continue;
      int year = Integer.parseInt(matcher.group(1));
      LocalDate localDate = LocalDate.of(year, Integer.parseInt(matcher.group(2)),
          Integer.parseInt(matcher.group(3)));
      accept(localDate, Files.readAllLines(folder.resolve(file)));
    }
    return result;
  }

  public static void main(String[] args) throws IOException {
    List<String> list = new GoogleFitToAlbi(ZoneId.of("America/New_York")).readFolder("Daily activity metrics");
    String data = list.stream().map(s -> s + "\n").collect(Collectors.joining());
    LocalDateTime dateTime = LocalDateTime.now();
    String file = String.format("healthtracker_export_%04d%02d%02d_%02d%02d.csv", dateTime.getYear(),
        dateTime.getMonthValue(), dateTime.getDayOfMonth(), dateTime.getHour(), dateTime.getMinute());
    Files.writeString(Paths.get(file), data);
  }
}
