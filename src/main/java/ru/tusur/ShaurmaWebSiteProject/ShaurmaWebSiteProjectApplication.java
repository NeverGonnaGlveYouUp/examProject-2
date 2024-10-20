package ru.tusur.ShaurmaWebSiteProject;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude =  ErrorMvcAutoConfiguration.class)
@Theme("vaadin+")
@EnableCaching
public class ShaurmaWebSiteProjectApplication extends SpringBootServletInitializer implements AppShellConfigurator {
	public static void main(String[] args) {
		SpringApplication.run(ShaurmaWebSiteProjectApplication.class, args);
	}
}
