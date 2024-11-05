package ru.tusur.ShaurmaWebSiteProject.backend.config;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.apache.catalina.util.ResourceSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithms;
import ru.tusur.ShaurmaWebSiteProject.backend.model.*;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.*;
import ru.tusur.ShaurmaWebSiteProject.backend.security.DelegatingPasswordEncoder;
import ru.tusur.ShaurmaWebSiteProject.backend.security.Roles;
import ru.tusur.ShaurmaWebSiteProject.backend.service.CustomUserDetailsService;
import ru.tusur.ShaurmaWebSiteProject.ui.security.LoginView;

import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.util.*;

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
    ReviewRepo reviewRepo;

    @Autowired
    LikesRepo likesRepo;

    @Autowired
    DelegatingPasswordEncoder delegatingPasswordEncoder;

//    get jwt key
    @Value("${jwt.auth.secret}")
    private String authSecret;

    private final Random random = new Random();

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new CustomDao();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(delegatingPasswordEncoder.passwordEncoder());
        return provider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth ->
                auth.requestMatchers(antMatchers("/", "/icons/**", "/line-awesome/**", "/components/**")).permitAll()
        );

        super.configure(http);
        setLoginView(http, LoginView.class);
//        http.formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer.defaultSuccessUrl("/"));

        //JWT Auth
        setStatelessAuthentication(http, new SecretKeySpec(Base64.getDecoder().decode(authSecret), JwsAlgorithms.HS256), "ru.tusur.ShaurmaWebSiteProject");


        UserDetails userDetails = new UserDetails();
        userDetails.setRole(Roles.ADMIN);
        userDetails.setEmail("admin@admin.admin");
        userDetails.setTransientPassword("admin@admin.admin");
        userDetails.setUsername("admin@admin.admin");
        customUserDetailsService.store(userDetails);

        UserDetails userDetails1 = new UserDetails();
        userDetails1.setRole(Roles.ADMIN);
        userDetails1.setEmail("admin1@admin1.admin");
        userDetails1.setTransientPassword("admin1@admin1.admin");
        userDetails1.setUsername("admin1@admin1.admin");
        customUserDetailsService.store(userDetails1);

        ProductTypeEntity productType = new ProductTypeEntity();
        productType.setName("Шаурма");

        ProductTypeEntity productType1 = new ProductTypeEntity();
        productType1.setName("Кебаб");
        productTypeEntityRepo.save(productType);
        productTypeEntityRepo.save(productType1);

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

        for (int i = 0; i < 10; i++) {
            Product product = new Product();
            product.setName("Шаверма из кота V" + i);
            product.setPrice(new BigDecimal("350.99"));
            product.setPreviewUrl("src/main/resources/META-INF/resources/images/img.png");
            product.setRank(i);
            product.setProductType(productType);
            product.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
            productRepo.save(product);
            HashSet<Review> reviews = new HashSet<>();
            for (int j = 0; j < 7; j++) {
                Review review = new Review();
                review.setBranch(j%2==0?branch:branch1);
                review.setProduct(product);
                review.setGrade(random.nextInt(5));
                review.setUserDetails(j%2==0?userDetails:userDetails1);
                review.setContent("Lorem ipsum dolor sit amet, consectetur adipiscing elit");
                reviews.add(review);
                reviewRepo.save(review);
                HashSet<Likes> likesHashSet = new HashSet<>();
                for (int k = 0; k <= 1; k++) {
                    Likes likes = new Likes();
                    likes.setLikes(k%5==0?LikeState.DISLIKE:LikeState.LIKE);
                    likes.setReview(review);
                    likes.setUserDetails(k==1?userDetails:userDetails1);
                    likesHashSet.add(likes);
                    likesRepo.save(likes);
                }
                review.setLikes(likesHashSet);
                reviewRepo.save(review);
            }
            product.setReviews(reviews);
            productRepo.save(product);

            Product product1 = new Product();
            product1.setName("DONNER_KEBAB из кота V" + i);
            product1.setPrice(new BigDecimal("350"));
            product1.setPreviewUrl("");
            product1.setRank(i);
            product1.setProductType(productType1);
            product1.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
            productRepo.save(product1);
            HashSet<Review> reviews1 = new HashSet<>();
            for (int l = 0; l < 7; l++) {
                Review review = new Review();
                review.setBranch(l%2==0?branch:branch2);
                review.setProduct(product1);
                review.setGrade(random.nextInt(5));
                review.setUserDetails(l%2==0?userDetails:userDetails1);
                review.setContent("Lorem ipsum dolor sit amet, consectetur adipiscing elit");
                reviews1.add(review);
                reviewRepo.save(review);
            }
            product1.setReviews(reviews1);
            productRepo.save(product1);

            ProductOption productOption = new ProductOption();
            productOption.setPrice(new BigDecimal("33"));
            productOption.setName("Сыр L");
            productOption.setMass(30);
            productOption.setProductSet(Set.of(product, product1));
            productOptionRepo.save(productOption);

            ProductOption productOption1 = new ProductOption();
            productOption1.setPrice(new BigDecimal("66"));
            productOption1.setName("Сыр XL");
            productOption1.setMass(60);
            productOption1.setProductSet(Set.of(product, product1));
            productOptionRepo.save(productOption1);

            ProductOption productOption2 = new ProductOption();
            productOption2.setPrice(new BigDecimal("99"));
            productOption2.setName("Сыр XXL");
            productOption2.setMass(90);
            productOption2.setProductSet(Set.of(product, product1));
            productOptionRepo.save(productOption2);

            ProductOption productOption3 = new ProductOption();
            productOption3.setPrice(new BigDecimal("10"));
            productOption3.setName("Халапеньо L");
            productOption3.setMass(10);
            productOption3.setProductSet(Set.of(product, product1));
            productOptionRepo.save(productOption3);

            ProductOption productOption4 = new ProductOption();
            productOption4.setPrice(new BigDecimal("20"));
            productOption4.setName("Халапеньо XL");
            productOption4.setMass(20);
            productOption4.setProductSet(Set.of(product, product1));
            productOptionRepo.save(productOption4);

            ProductOption productOption5 = new ProductOption();
            productOption5.setPrice(new BigDecimal("40"));
            productOption5.setName("Лук");
            productOption5.setMass(35);
            productOption5.setProductSet(Set.of(product, product1));
            productOptionRepo.save(productOption5);

            BranchProduct branchProduct = new BranchProduct();
            branchProduct.setHide(false);
            branchProduct.setProduct(product);
            branchProduct.setBranch(branch);
            branchProduct.setId(new BranchProductKey(product.getId(), branch.getId()));
            branchProductRepo.save(branchProduct);

            BranchProduct branchProduct1 = new BranchProduct();
            branchProduct1.setHide(false);
            branchProduct1.setProduct(product1);
            branchProduct1.setBranch(branch);
            branchProduct1.setId(new BranchProductKey(product1.getId(), branch.getId()));
            branchProductRepo.save(branchProduct1);

        }



//        Order order1 = new Order();
//        Order order = new Order();
//
//        Payment payment = new Payment();
//        payment.setPaymentType(PaymentType.CARD);
//        payment.setPaymentState(PaymentState.NO_PAYMENT);
//        paymentRepo.save(payment);
//
//        Payment payment1 = new Payment();
//        payment1.setPaymentType(PaymentType.SBP);
//        payment1.setPaymentState(PaymentState.PAYMENT_DONE);
//        paymentRepo.save(payment1);
//
//        order.setOrderState(OrderState.DELIVERED);
//        order.setSum(new BigDecimal("700"));
//        order.setUserDetails(userDetails);
//        order.setPayment(payment);
//
//        order1.setOrderState(OrderState.CANCELLED_BY_USER);
//        order1.setSum(new BigDecimal("750"));
//        order1.setUserDetails(userDetails);
//        order1.setPayment(payment1);
//        orderRepo.save(order1);
//        orderRepo.save(order);
//
//        OrderContent orderContent2 = new OrderContent();
//        orderContent2.setProduct(product3);
//        orderContent2.setNum(4);
//        orderContent2.setOrder(order);
//        orderContent2.setBranch(branch);
//        orderContent2.setId(new OrderContentKey(product3.getId(), order.getId()));
//        orderContentRepo.save(orderContent2);
//
//        OrderContent orderContent3 = new OrderContent();
//        orderContent3.setProduct(product4);
//        orderContent3.setNum(1);
//        orderContent3.setOrder(order1);
//        orderContent3.setBranch(branch2);
//        orderContent3.setId(new OrderContentKey(product4.getId(), order1.getId()));
//        orderContentRepo.save(orderContent3);
//
//
//        OrderContent orderContent = new OrderContent();
//        orderContent.setProduct(product);
//        orderContent.setNum(1);
//        orderContent.setBranch(branch);
//        orderContent.setOrder(order);
//        orderContent.setId(new OrderContentKey(product.getId(), order.getId()));
//        orderContentRepo.save(orderContent);
//
//        OrderContent orderContent1 = new OrderContent();
//        orderContent1.setProduct(product4);
//        orderContent1.setNum(1);
//        orderContent1.setOrder(order);
//        orderContent1.setBranch(branch);
//        orderContent1.setId(new OrderContentKey(product4.getId(), order.getId()));
//        orderContentRepo.save(orderContent1);
    }
}