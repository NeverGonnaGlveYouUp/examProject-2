package ru.tusur.ShaurmaWebSiteProject.ui.mainPage;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import ru.tusur.ShaurmaWebSiteProject.backend.model.*;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.BranchProductRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.ProductTypeEntityRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.security.Roles;
import ru.tusur.ShaurmaWebSiteProject.backend.security.SecurityService;
import ru.tusur.ShaurmaWebSiteProject.backend.service.ProductService;
import ru.tusur.ShaurmaWebSiteProject.ui.components.LazyContainer;
import ru.tusur.ShaurmaWebSiteProject.ui.components.MainPageProductRepresentation;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.LazyPlaceholder;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.MainLayout;

import java.util.LinkedList;
import java.util.List;

@AnonymousAllowed
@Route(value = "/", layout = MainLayout.class)
@PageTitle("PitaMaster")
public class MainPage extends MainLayout implements LazyPlaceholder {
    private final LinkedList<Div> linkedList = new LinkedList<>(List.of(new Div(new Button("btn1-MainLayout")), new Div(new Button("btn2-MainLayout")), new Div(new Button("btn3-MainLayout")), new Div(new Button("btn4-MainLayout"))));
    private final SecurityService securityService;
    private final ProductService productService;
    private final ProductTypeEntityRepo productTypeEntityRepo;
    private final BranchProductRepo branchProductRepo;
    private final VerticalLayout verticalLayout = new VerticalLayout();

    //Stateless auth
//    public MainPage(){
//    }

    //State auth
    public MainPage(SecurityService securityService,
                    ProductService productService,
                    ProductTypeEntityRepo productTypeEntityRepo,
                    BranchProductRepo branchProductRepo) {
        super(securityService);
        this.securityService = securityService;
        this.productService = productService;
        this.productTypeEntityRepo = productTypeEntityRepo;
        this.branchProductRepo = branchProductRepo;

        addToNavbar(getMyNavBar(securityService, linkedList));
        lazyLoadProductsRepresentations();
        lazyLoadAdminButton();

        setContent(verticalLayout);
    }

    private void lazyLoadAdminButton() {
        try {
            if (this.securityService.getAuthenticatedUser().getRole().equals(Roles.ADMIN)) {
                verticalLayout.add(new LazyContainer<>(
                        lazyPlaceholder(),
                        (div, ui) -> {
                            ui.access(() -> {
                                div.removeAll();
                                div.add(setupAdminPanelButton(securityService));
                            });
                        }
                ));
            }
        } catch (Exception _) {
        }
    }

    private void lazyLoadProductsRepresentations() {
        List<ProductTypeEntity> allProductTypeEntities = productTypeEntityRepo.findAll();

        allProductTypeEntities.forEach(productType -> {
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            H3 title = new H3(productType.getName());

//            Branch branch = branchProductRepo.findById(new BranchProductKey(1L, 1L)).getBranch();
//
//            List<BranchProduct> products = productService.findAllByBranch(branch, productType);
            List<Product> products = productService.findByProductTypeOrderByRankAsc(productType);

            products.forEach(product -> {
                horizontalLayout.add(
                        new LazyContainer<>(
                                lazyPlaceholder(),
                                (div, ui) -> {
                                    ui.access(() -> {
                                        div.removeAll();
                                        MainPageProductRepresentation mainPageProductRepresentation = new MainPageProductRepresentation(product);
                                        div.add(mainPageProductRepresentation);
                                    });
                                })
                );
            });
            verticalLayout.add(title, horizontalLayout);
        });
    }
}