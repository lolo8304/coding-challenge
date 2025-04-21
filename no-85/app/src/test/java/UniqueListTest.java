import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import timezone.TimezoneAbbr;
import timezone.UniqueList;

public class UniqueListTest {


    private TimezoneAbbr tz2;
    private TimezoneAbbr tz1;
    private TimezoneAbbr tz0;
    private TimezoneAbbr tz_2;
    private TimezoneAbbr tz_3;

    @BeforeEach
    void setup() {
        var zh2 = "CH;Europe/Zurich2;Büsingen;Canonical;+02:00;+03:00;CET;CEST;europe;";
        var zh1 = "CH;Europe/Zurich1;Büsingen;Canonical;+01:00;+02:00;CET;CEST;europe;";
        var zh0 = "CH;Europe/Zurich0;Büsingen;Canonical;+00:00;+01:00;CET;CEST;europe;";
        var zh_2 = "CH;Europe/Zurich_2;Büsingen;Canonical;-02:00;-01:00;CET;CEST;europe;";
        var zh_3 = "CH;Europe/Zurich_3;Büsingen;Canonical;-03:00;-02:00;CET;CEST;europe;";
        tz2 = TimezoneAbbr.fromTzLine(zh2).get();
        tz1 = TimezoneAbbr.fromTzLine(zh1).get();
        tz0 = TimezoneAbbr.fromTzLine(zh0).get();
        tz_2 = TimezoneAbbr.fromTzLine(zh_2).get();
        tz_3 = TimezoneAbbr.fromTzLine(zh_3).get();
        
    }
    
    @Test
    void compare_zones_ok() {
        // Arrange
        var tz2Sdt = tz2.timezoneOffsetSdt();
        var tz1Sdt = tz1.timezoneOffsetSdt();
        var tz_2Sdt = tz_2.timezoneOffsetSdt();

        // Action
        var sdt11Result = tz1Sdt.compareTo(tz1Sdt);
        var sdt21Result = tz2Sdt.compareTo(tz1Sdt);
        var sdt_22Result = tz_2Sdt.compareTo(tz2Sdt);

        assert sdt11Result == 0;
        assert sdt21Result > 0;
        assert sdt_22Result < 0;

    }

    @Test
    void compare_zh2_zh1_ok() {
        // Arrange
        var tz2Sdt = tz2.timezoneOffsetSdt();
        var tz1Sdt = tz1.timezoneOffsetSdt();

        // Action
        var sdt11Result = tz1Sdt.compareTo(tz1Sdt);
        var sdt21Result = tz2Sdt.compareTo(tz1Sdt);

        assert sdt11Result == 0;
        assert sdt21Result > 0;

    }

    @Test
    void list_all_zh_sorted_ok() {
        // Arrange
        var list = new UniqueList<>(TimezoneAbbr.getComparator());
        list.add(tz2);
        list.add(tz1);
        list.add(tz0);
        list.add(tz_2);
        list.add(tz_3);

        // Action
        var sortedList = new UniqueList<>(TimezoneAbbr.getComparator());
        sortedList.add(tz_3);
        sortedList.add(tz_2);
        sortedList.add(tz0);
        sortedList.add(tz1);
        sortedList.add(tz2);

        // Assert
        assert list.size() == 5;
        assert sortedList.size() == 5;

        for (int i = 0; i < list.size(); i++) {
            var tz = list.get(i);
            var tzSorted = sortedList.get(i);
            assert tz.compareTo(tzSorted) == 0;
        }
    }

}
