package ru.tusur.ShaurmaWebSiteProject.backend.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Product;
import ru.tusur.ShaurmaWebSiteProject.backend.model.ProductType;
import ru.tusur.ShaurmaWebSiteProject.backend.model.ProductTypeEntity;
import ru.tusur.ShaurmaWebSiteProject.backend.model.UserDetails;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.ProductRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.ProductTypeEntityRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.security.DelegatingPasswordEncoder;
import ru.tusur.ShaurmaWebSiteProject.backend.security.Roles;
import ru.tusur.ShaurmaWebSiteProject.backend.service.CustomUserDetailsService;
import ru.tusur.ShaurmaWebSiteProject.ui.security.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Set;


@EnableWebSecurity
@Configuration
class SecurityConfig extends VaadinWebSecurity {

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    ProductRepo productRepo;

    @Autowired
    ProductTypeEntityRepo productTypeEntityRepo;

    @Autowired
    DelegatingPasswordEncoder delegatingPasswordEncoder;

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new CustomDao();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(delegatingPasswordEncoder.passwordEncoder());
        return provider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth ->
                auth
                        .requestMatchers(antMatchers("/")).permitAll()
//                        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/themes/*")).permitAll()
        );

        super.configure(http);
        setLoginView(http, LoginView.class);
        http.formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer.defaultSuccessUrl("/"));



        UserDetails userDetails = new UserDetails();
        userDetails.setRoles(Roles.ADMIN);
        userDetails.setEmail("admin@admin.admin");
        userDetails.setTransientPassword("admin@admin.admin");
        userDetails.setUsername("admin@admin.admin");
        customUserDetailsService.store(userDetails);

        ProductTypeEntity productType = new ProductTypeEntity();
        productType.setName("Шаурма");

        ProductTypeEntity productType1 = new ProductTypeEntity();
        productType1.setName("Кебаб");
        productTypeEntityRepo.save(productType);
        productTypeEntityRepo.save(productType1);

        Product product = new Product();
        product.setName("Шаверма из кота");
        product.setPrice(new BigDecimal("350.99"));
        product.setPreviewUrl("src/main/resources/META-INF/resources/images/564008-shaurma-35.jpg");
        product.setRank(1);
        product.setProductType(productType);
//        product.setProductType(ProductType.SHAURMA);
        product.setDescription("rrr");
//        product.setContentMap(new HashMap<String, Float>(){{
//            put("Лаваш", 150F);
//            put("Жопа кота", 200F);
//        }});
        productRepo.save(product);
        Product product2 = new Product();
        product2.setName("Шаверма из кота V2");
        product2.setPrice(new BigDecimal("350.99"));
        product2.setPreviewUrl("src/main/resources/META-INF/resources/images/564008-shaurma-35.jpg");
        product2.setRank(2);
        product2.setProductType(productType);
//        product2.setProductType(ProductType.SHAURMA);
        product2.setDescription("rrr");
//        product2.setContentMap(new HashMap<String, Float>(){{
//            put("Лаваш", 250F);
//            put("Жопа кота", 300F);
//        }});
        productRepo.save(product2);
        Product product3 = new Product();
        product3.setName("Шаверма из кота V3");
        product3.setPrice(new BigDecimal("350.99"));
        product3.setPreviewUrl("");
        product3.setRank(3);
        product3.setProductType(productType);
//        product3.setProductType(ProductType.SHAURMA);
        product3.setDescription("rrr");
//        product3.setContentMap(new HashMap<String, Float>(){{
//            put("Лаваш", 250F);
//            put("Жопа кота", 300F);
//        }});
        productRepo.save(product3);
        Product product4 = new Product();
        product4.setName("Шаверма из кота V4");
        product4.setPrice(new BigDecimal("299.99"));
        product4.setPreviewUrl("");
//        product4.setProductType(ProductType.SHAURMA);
        product4.setProductType(productType);
        product4.setRank(4);
        product4.setDescription("rrr");
//        product4.setContentMap(new HashMap<String, Float>(){{
//            put("Лаваш", 250F);
//            put("Жопа кота", 300F);
//        }});
        productRepo.save(product4);
        Product product5 = new Product();
        product5.setName("Шаверма из кота V5");
        product5.setPrice(new BigDecimal("350"));
        product5.setPreviewUrl("");
        product5.setRank(5);
//        product5.setProductType(ProductType.SHAURMA);
        product5.setProductType(productType);
        product5.setDescription("rrr");
//        product5.setContentMap(new HashMap<String, Float>(){{
//            put("Лаваш", 250F);
//            put("Жопа кота", 300F);
//        }});
        productRepo.save(product5);

        Product product6 = new Product();
        product6.setName("DONNER_KEBAB из кота V1");
        product6.setPrice(new BigDecimal("350"));
        product6.setPreviewUrl("");
        product6.setRank(5);
        product6.setProductType(productType1);
//        product6.setProductType(ProductType.DONNER_KEBAB);
        product6.setDescription("rrr");
//        product5.setContentMap(new HashMap<String, Float>(){{
//            put("Лаваш", 250F);
//            put("Жопа кота", 300F);
//        }});
        productRepo.save(product6);


    }

}