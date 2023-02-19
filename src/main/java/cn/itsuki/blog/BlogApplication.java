package cn.itsuki.blog;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.format.Formatter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;

@Log4j2
@SpringBootApplication
public class BlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }


    @Bean
    ApplicationRunner applicationRunner(Environment environment) {
        String mode = environment.getProperty("mode");
        return args -> {
            log.info("Itsuki Server Run！mode: " + mode);
        };
    }

    /**
     * Create the date formatter that accepts both ISO date and ISO date time.
     *
     * @return the date formatter
     */
    @Bean
    public Formatter<Date> dateFormatter() {
        return new Formatter<Date>() {
            private final SimpleDateFormat isoDateTimeFormat =
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sssZ");

            @Override
            public Date parse(String text, Locale locale) throws ParseException {
                if (text.length() == 10) {
                    text += "T00:00:00.000Z";
                }

                return Date.from(Instant.parse(text));
            }

            @Override
            public String print(Date date, Locale locale) {
                return isoDateTimeFormat.format(date);
            }
        };
    }
}
