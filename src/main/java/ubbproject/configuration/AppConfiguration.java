package ubbproject.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan({"ubbproject.repository","ubbproject.service","ubbproject.ui"})
public class AppConfiguration {


}
