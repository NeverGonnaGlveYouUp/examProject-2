package ru.tusur.ShaurmaWebSiteProject.backend.config;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.apache.commons.lang3.time.DateUtils;
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
    OrderContentToProductOptionRepo orderRepo;

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
    PromotionRepo promotionRepo;

    @Autowired
    ReviewRepo reviewRepo;

    @Autowired
    LikesRepo likesRepo;

    @Autowired
    DelegatingPasswordEncoder delegatingPasswordEncoder;

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

        UserDetails userDetails2 = new UserDetails();
        userDetails2.setRole(Roles.ADMIN);
        userDetails2.setEmail("user@user.user");
        userDetails2.setTransientPassword("user@user.user");
        userDetails2.setUsername("user@user.user");
        customUserDetailsService.store(userDetails2);

        ProductTypeEntity productType = new ProductTypeEntity();
        productType.setName("Шаурма");

        ProductTypeEntity productType1 = new ProductTypeEntity();
        productType1.setName("Кебаб");
        productTypeEntityRepo.save(productType);
        productTypeEntityRepo.save(productType1);

        Date date = new Date(2880000);

        Branch branch = new Branch();
        branch.setDeliveryStreets("Вершинина; Петропавловская; Фёдора Лыткина;");
        branch.setAddress("Вершинина, 38");
        branch.setPhoneNumber("222-333-44-55");
        branch.setOpenFrom(date);
        branch.setOpenTill(DateUtils.addHours(date, 6));
        branchRepo.save(branch);

        Branch branch1 = new Branch();
        branch1.setDeliveryStreets("Вершинина; Петропавловская; Фёдора Лыткина;");
        branch1.setAddress("Тверская, 81");
        branch1.setPhoneNumber("123-345-67-89");
        branch1.setOpenFrom(date);
        branch1.setOpenTill(DateUtils.addHours(date, 3));
        branchRepo.save(branch1);

        Branch branch2 = new Branch();
        branch2.setDeliveryStreets("Вершинина; Петропавловская; Фёдора Лыткина;");
        branch2.setAddress("Гоголя, 67");
        branch2.setPhoneNumber("666-123-66-11");
        branch2.setOpenFrom(date);
        branch2.setOpenTill(DateUtils.addHours(date, 8));
        branchRepo.save(branch2);

        Promotion promotion = new Promotion();
        promotion.setCondition("FREED300");
        promotion.setName("FREED300");
        promotion.setHide(false);
        promotion.setPromotionEffect(new BigDecimal("0"));
        promotion.setDescription(PromotionType.FREE_DELIVERY_BY_CODE.getDescription());
        promotion.setPromotionType(PromotionType.FREE_DELIVERY_BY_CODE);
        promotionRepo.save(promotion);

        Promotion promotion1 = new Promotion();
        promotion1.setCondition("FREED300");
        promotion1.setName("FREED300");
        promotion1.setHide(false);
        promotion1.setPromotionEffect(new BigDecimal("300"));
        promotion1.setDescription(PromotionType.CONSTANT_DISCOUNT_BY_CODE.getDescription());
        promotion1.setPromotionType(PromotionType.CONSTANT_DISCOUNT_BY_CODE);
        promotionRepo.save(promotion1);

        ProductOption productOption = new ProductOption();
        productOption.setPrice(new BigDecimal("33"));
        productOption.setName("Сыр L");
        productOption.setMass(30);
        productOptionRepo.save(productOption);

        ProductOption productOption1 = new ProductOption();
        productOption1.setPrice(new BigDecimal("66"));
        productOption1.setName("Сыр XL");
        productOption1.setMass(60);
        productOptionRepo.save(productOption1);

        ProductOption productOption2 = new ProductOption();
        productOption2.setPrice(new BigDecimal("99"));
        productOption2.setName("Сыр XXL");
        productOption2.setMass(90);
        productOptionRepo.save(productOption2);

        ProductOption productOption3 = new ProductOption();
        productOption3.setPrice(new BigDecimal("10"));
        productOption3.setName("Халапеньо L");
        productOption3.setMass(10);
        productOptionRepo.save(productOption3);

        ProductOption productOption4 = new ProductOption();
        productOption4.setPrice(new BigDecimal("20"));
        productOption4.setName("Халапеньо XL");
        productOption4.setMass(20);
        productOptionRepo.save(productOption4);

        ProductOption productOption5 = new ProductOption();
        productOption5.setPrice(new BigDecimal("40"));
        productOption5.setName("Лук");
        productOption5.setMass(35);
        productOptionRepo.save(productOption5);

        HashSet<Product> productHashSet = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            Product product = new Product();
            product.setName("Шаверма V" + i);
            product.setPrice(new BigDecimal("250.99").add(BigDecimal.valueOf(random.nextInt(3) * 25)));
            product.setPreviewUrl("src/main/resources/META-INF/resources/images/img.png");
            product.setRank(i);
            product.setProductType(productType);
            product.setMass(350);
            product.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
            productRepo.save(product);
            HashSet<Review> reviews = new HashSet<>();
            for (int j = 0; j < 7; j++) {
                Review review = new Review();
                review.setBranch(branch);
//                review.setBranch(j % 2 == 0 ? branch : branch1);
                review.setProduct(product);
                review.setGrade(random.nextInt(1, 6));
                review.setUserDetails(j % 2 == 0 ? userDetails : userDetails1);
                review.setContent("Lorem ipsum dolor sit amet, consectetur adipiscing elit");
                reviews.add(review);
                reviewRepo.save(review);
                HashSet<Likes> likesHashSet = new HashSet<>();
                for (int k = 0; k <= 1; k++) {
                    Likes likes = new Likes();
                    likes.setLikes(k % 5 == 0 ? LikeState.DISLIKE : LikeState.LIKE);
                    likes.setReview(review);
                    likes.setUserDetails(k == 1 ? userDetails : userDetails1);
                    likesHashSet.add(likes);
                    likesRepo.save(likes);
                }
                review.setLikes(likesHashSet);
                reviewRepo.save(review);
            }
            product.setReviews(reviews);
            productRepo.save(product);

            Product product1 = new Product();
            product1.setName("донер-кебаб V" + i);
            product1.setPrice(new BigDecimal("250.99").add(BigDecimal.valueOf(random.nextInt(3) * 25)));
            product1.setPreviewUrl("src/main/resources/META-INF/resources/images/1663705172_3-mykaleidoscope-ru-p-kebab-v-lavashe-yeda-krasivo-3-3345574924.jpg");
            product1.setRank(i);
            product1.setMass(390);
            product1.setProductType(productType1);
            product1.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
            productRepo.save(product1);

            productHashSet.add(product);
            productHashSet.add(product1);

            HashSet<Review> reviews1 = new HashSet<>();
            for (int l = 0; l < 7; l++) {
                Review review = new Review();
                review.setBranch(branch);
                review.setProduct(product1);
                review.setGrade(random.nextInt(1, 6));
                review.setUserDetails(l % 2 == 0 ? userDetails : userDetails1);
                review.setContent("Lorem ipsum dolor sit amet, consectetur adipiscing elit");
                reviews1.add(review);
                reviewRepo.save(review);
            }
            product1.setReviews(reviews1);
            productRepo.save(product1);

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
        productOption.setProductSet(productHashSet);
        productOptionRepo.save(productOption);
        productOption1.setProductSet(productHashSet);
        productOptionRepo.save(productOption1);
        productOption2.setProductSet(productHashSet);
        productOptionRepo.save(productOption2);
        productOption3.setProductSet(productHashSet);
        productOptionRepo.save(productOption3);
        productOption4.setProductSet(productHashSet);
        productOptionRepo.save(productOption4);
        productOption5.setProductSet(productHashSet);
        productOptionRepo.save(productOption5);
    }
}