package ru.tusur.ShaurmaWebSiteProject;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;
import ru.tusur.ShaurmaWebSiteProject.ui.components.ChatMessage;

@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
@Theme("vaadin+")
@EnableCaching
@EnableScheduling
@Push
public class ShaurmaWebSiteProjectApplication extends SpringBootServletInitializer implements AppShellConfigurator {
    public static void main(String[] args) {
        SpringApplication.run(ShaurmaWebSiteProjectApplication.class, args);
    }

    @Bean
    UnicastProcessor<ChatMessage> publisher() {
        return UnicastProcessor.create();
    }

    @Bean
    Flux<ChatMessage> messages(UnicastProcessor<ChatMessage> publisher) {
        return publisher.replay(30).autoConnect();
    }

}
