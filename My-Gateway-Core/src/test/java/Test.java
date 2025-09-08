
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static constant.BasicConstant.DATE_DEFAULT_FORMATTER;

public class Test {

    @org.junit.Test
    public void testDate() {
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_DEFAULT_FORMATTER)) + "---TraceId: " + UUID.randomUUID());
    }


}
