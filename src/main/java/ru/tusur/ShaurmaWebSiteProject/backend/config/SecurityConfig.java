package ru.tusur.ShaurmaWebSiteProject.backend.config;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithms;
import ru.tusur.ShaurmaWebSiteProject.backend.model.*;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.*;
import ru.tusur.ShaurmaWebSiteProject.backend.security.DelegatingPasswordEncoder;
import ru.tusur.ShaurmaWebSiteProject.backend.security.Roles;
import ru.tusur.ShaurmaWebSiteProject.backend.service.CustomUserDetailsService;
import ru.tusur.ShaurmaWebSiteProject.ui.security.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.Date;
import java.util.Set;

@EnableWebSecurity
@Configuration
class SecurityConfig extends VaadinWebSecurity {

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    ProductRepo productRepo;

    @Autowired
    OrderContentRepo orderContentRepo;

    @Autowired
    OrderRepo orderRepo;

    @Autowired
    ProductTypeEntityRepo productTypeEntityRepo;

    @Autowired
    PaymentRepo paymentRepo;

    @Autowired
    ProductOptionRepo productOptionRepo;

    @Autowired
    BranchProductRepo branchProductRepo;

    @Autowired
    BranchRepo branchRepo;

    @Autowired
    DelegatingPasswordEncoder delegatingPasswordEncoder;

//    get jwt key
//    @Value("${jwt.auth.secret}")
//    private String authSecret;

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
        );

        super.configure(http);
        setLoginView(http, LoginView.class);
        http.formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer.defaultSuccessUrl("/"));

        //JWT Auth
//        setStatelessAuthentication(http, new SecretKeySpec(Base64.getDecoder().decode(authSecret), JwsAlgorithms.HS256), "ru.tusur.ShaurmaWebSiteProject");



        UserDetails userDetails = new UserDetails();
        userDetails.setRole(Roles.ADMIN);
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

        ProductOption productOption = new ProductOption();
        productOption.setPrise(new BigDecimal("33"));
        productOption.setName("сыр");
        productOption.setMass(30);
        productOption.setProductSet(Set.of(product3, product2, product4));

        ProductOption productOption1 = new ProductOption();
        productOption1.setPrise(new BigDecimal("66"));
        productOption1.setName("сыр X2");
        productOption1.setMass(60);
        productOption1.setProductSet(Set.of(product3, product2, product4));

        ProductOption productOption2 = new ProductOption();
        productOption2.setPrise(new BigDecimal("99"));
        productOption2.setName("сыр X3");
        productOption2.setMass(90);
        productOption2.setProductSet(Set.of(product3, product2, product4));
        productOptionRepo.save(productOption);
        productOptionRepo.save(productOption1);
        productOptionRepo.save(productOption2);

        Branch branch = new Branch();
        branch.setAddress("Улица Вершинина, 38");
        branch.setPhoneNumber("222-333-44-55");
        branch.setOpenFrom(new Date());
        branch.setOpenTill(new Date());
        branchRepo.save(branch);

        Branch branch1 = new Branch();
        branch1.setAddress("Тверская, 81");
        branch1.setPhoneNumber("123-345-67-89");
        branch1.setOpenFrom(new Date());
        branch1.setOpenTill(new Date());
        branchRepo.save(branch1);

        Branch branch2 = new Branch();
        branch2.setAddress("Улица Гоголя, 67");
        branch2.setPhoneNumber("666-123-66-11");
        branch2.setOpenFrom(new Date());
        branch2.setOpenTill(new Date());
        branchRepo.save(branch2);

        BranchProduct branchProduct = new BranchProduct();
        branchProduct.setHide(false);
        branchProduct.setProduct(product);
        branchProduct.setBranch(branch);
        branchProduct.setId(new BranchProductKey(product.getId(), branch.getId()));
        branchProductRepo.save(branchProduct);

        BranchProduct branchProduct1 = new BranchProduct();
        branchProduct1.setHide(false);
        branchProduct1.setProduct(product2);
        branchProduct1.setBranch(branch);
        branchProduct1.setId(new BranchProductKey(product2.getId(), branch.getId()));
        branchProductRepo.save(branchProduct1);

        BranchProduct branchProduct2 = new BranchProduct();
        branchProduct2.setHide(false);
        branchProduct2.setProduct(product3);
        branchProduct2.setBranch(branch);
        branchProduct2.setId(new BranchProductKey(product3.getId(), branch.getId()));
        branchProductRepo.save(branchProduct2);

        BranchProduct branchProduct3 = new BranchProduct();
        branchProduct3.setHide(false);
        branchProduct3.setProduct(product4);
        branchProduct3.setBranch(branch);
        branchProduct3.setId(new BranchProductKey(product4.getId(), branch.getId()));
        branchProductRepo.save(branchProduct3);

        BranchProduct branchProduct4 = new BranchProduct();
        branchProduct4.setHide(false);
        branchProduct4.setProduct(product5);
        branchProduct4.setBranch(branch);
        branchProduct4.setId(new BranchProductKey(product5.getId(), branch.getId()));
        branchProductRepo.save(branchProduct4);

        BranchProduct branchProduct5 = new BranchProduct();
        branchProduct5.setHide(false);
        branchProduct5.setProduct(product6);
        branchProduct5.setBranch(branch);
        branchProduct5.setId(new BranchProductKey(product6.getId(), branch.getId()));
        branchProductRepo.save(branchProduct5);

        BranchProduct branchProduct6 = new BranchProduct();
        branchProduct6.setHide(false);
        branchProduct6.setProduct(product4);
        branchProduct6.setBranch(branch2);
        branchProduct6.setId(new BranchProductKey(product4.getId(), branch2.getId()));
        branchProductRepo.save(branchProduct6);

        Order order1 = new Order();
        Order order = new Order();

        Payment payment = new Payment();
        payment.setPaymentType(PaymentType.CARD);
        payment.setPaymentState(PaymentState.NO_PAYMENT);
        paymentRepo.save(payment);

        Payment payment1 = new Payment();
        payment1.setPaymentType(PaymentType.SBP);
        payment1.setPaymentState(PaymentState.PAYMENT_DONE);
        paymentRepo.save(payment1);

        order.setOrderState(OrderState.DELIVERED);
        order.setSum(new BigDecimal("700"));
        order.setUserDetails(userDetails);
        order.setPayment(payment);

        order1.setOrderState(OrderState.CANCELLED_BY_USER);
        order1.setSum(new BigDecimal("750"));
        order1.setUserDetails(userDetails);
        order1.setPayment(payment1);
        orderRepo.save(order1);
        orderRepo.save(order);

        OrderContent orderContent2 = new OrderContent();
        orderContent2.setProduct(product3);
        orderContent2.setNum(4);
        orderContent2.setOrder(order);
        orderContent2.setBranch(branch);
        orderContent2.setId(new OrderContentKey(product3.getId(), order.getId()));
        orderContentRepo.save(orderContent2);


        OrderContent orderContent3 = new OrderContent();
        orderContent3.setProduct(product4);
        orderContent3.setNum(1);
        orderContent3.setOrder(order1);
        orderContent3.setBranch(branch2);
        orderContent3.setId(new OrderContentKey(product4.getId(), order1.getId()));
        orderContentRepo.save(orderContent3);


        OrderContent orderContent = new OrderContent();
        orderContent.setProduct(product2);
        orderContent.setNum(1);
        orderContent.setBranch(branch);
        orderContent.setOrder(order);
        orderContent.setId(new OrderContentKey(product2.getId(), order.getId()));
        orderContentRepo.save(orderContent);

        OrderContent orderContent1 = new OrderContent();
        orderContent1.setProduct(product4);
        orderContent1.setNum(1);
        orderContent1.setOrder(order);
        orderContent1.setBranch(branch);
        orderContent1.setId(new OrderContentKey(product4.getId(), order.getId()));
        orderContentRepo.save(orderContent1);
    }
}