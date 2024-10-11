package ru.tusur.ShaurmaWebSiteProject.console;

import jakarta.validation.constraints.Size;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.tusur.ShaurmaWebSiteProject.backend.model.UserDetails;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.UserDetailsRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.security.DelegatingPasswordEncoder;

import java.util.logging.Logger;

import static java.lang.String.format;

@ShellComponent
public class MyShell implements PromptProvider {

    @Autowired
    DelegatingPasswordEncoder delegatingPasswordEncoder;

    @Autowired
    UserDetailsRepo userDetailsRepo;

    Logger log = Logger.getLogger(MyShell.class.getName());

    //create-user --p 1234567890 --n userName --e asd@asd.asd --r ADMIN
    @ShellMethod(value = "create user with params", key = "create-user")
    public void createUser(
            @ShellOption(value = "--p")
            @Size(min = 8)
            String password,
            @ShellOption(value = "--n")
            String name,
            @ShellOption(value = "--e")
            String email,
            @ShellOption(value = "--r", defaultValue = "USER")
            String role
    ) {
        UserDetails user = new UserDetails();
        try {
            user.setPassword(delegatingPasswordEncoder.passwordEncoder().encode(password));
            user.setUsername(name);
            user.setRole("ROLE_" + role);
            user.setEmail(email);
            userDetailsRepo.save(user);
            log.info(format("User '%s' created", name));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public AttributedString getPrompt() {
        return new AttributedString(
                "Shell" + "==> ",
                AttributedStyle.DEFAULT.background(AttributedStyle.GREEN));
    }
}