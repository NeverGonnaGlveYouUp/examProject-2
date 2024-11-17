package ru.tusur.ShaurmaWebSiteProject.console;

import jakarta.validation.constraints.Size;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.tusur.ShaurmaWebSiteProject.backend.model.BranchProduct;
import ru.tusur.ShaurmaWebSiteProject.backend.model.UserDetails;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.BranchProductRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.UserDetailsRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.security.DelegatingPasswordEncoder;

import java.util.List;
import java.util.logging.Logger;

import static java.lang.String.format;

@ShellComponent
public class MyShell implements PromptProvider {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    DelegatingPasswordEncoder delegatingPasswordEncoder;

    @Autowired
    UserDetailsRepo userDetailsRepo;

    @Autowired
    BranchProductRepo customBranchProductRepository;

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

    @ShellMethod(value = "returns list of BranchProduct", key = "find-all")
    public void findAll() {

        List<BranchProduct> all = customBranchProductRepository.getAll();
        all.forEach(branchProduct -> System.out.println(branchProduct.toString()));

    }

    // select --b 2 --p 1
    @ShellMethod(key = "select")
    public void selectEnt(
            @ShellOption(value = "--b")
            Long b_id,
            @ShellOption(value = "--p")
            Long p_id) {
        BranchProduct one = customBranchProductRepository.getOne(b_id, p_id);
        System.out.println(one.toString());
    }

    //create --h true --b 2 --p 1
    @ShellMethod(value = "creates BranchProduct by Long b_id, Long p_id", key = "create")
    public void createEnt(
            @ShellOption(value = "--h")
            boolean b,
            @ShellOption(value = "--b")
            Long b_id,
            @ShellOption(value = "--p")
            Long p_id) {
        customBranchProductRepository.create(b, b_id, p_id);
    }

    //update --h true --b 1 --p 1
    @ShellMethod(value = "update BranchProduct by boolean b, Long b_id, Long p_id", key = "update")
    public void updateEnt(
            @ShellOption(value = "--h")
            boolean b,
            @ShellOption(value = "--b")
            Long b_id,
            @ShellOption(value = "--p")
            Long p_id) {
        customBranchProductRepository.update(b, b_id, p_id);
    }
    //delete --b 1 --p 1

    @ShellMethod(key = "delete")
    public void removeOne(
            @ShellOption(value = "--b")
            Long b_id,
            @ShellOption(value = "--p")
            Long p_id) {
        customBranchProductRepository.delete(b_id, p_id);
    }

    //create-table
    @ShellMethod(key = "create-table")
    public void createTable() {
        String sql = """
                CREATE TABLE public.branch_product1 (
                	hide bool NOT NULL,
                	branch_id int8 NOT NULL,
                	product_id int8 NOT NULL
                )
                """;
        jdbcTemplate.execute(sql);
    }

    @Override
    public AttributedString getPrompt() {
        return new AttributedString(
                "Shell" + "==> ",
                AttributedStyle.DEFAULT.background(AttributedStyle.GREEN));
    }
}