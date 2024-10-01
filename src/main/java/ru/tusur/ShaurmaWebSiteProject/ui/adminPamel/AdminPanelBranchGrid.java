package ru.tusur.ShaurmaWebSiteProject.ui.adminPamel;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;
import lombok.Setter;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Branch;
import ru.tusur.ShaurmaWebSiteProject.backend.model.BranchProductKey;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Product;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.BranchProductRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.BranchRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.security.Roles;

import java.util.List;
import java.util.stream.Collectors;


@Route(value = "grid-branches", layout = AdminPrefixPage.class)
@RolesAllowed(value = {Roles.ADMIN})
@PageTitle("Панель администратора - таблица филиалов")
public class AdminPanelBranchGrid extends VerticalLayout {
    public static final String name = "Филиалы";
//    private final LinkedList<Pair<Long, Long>> branchIdProductNumPairs = new LinkedList<>();

    public AdminPanelBranchGrid(BranchRepo branchRepo, BranchProductRepo branchProductRepo) {
        BranchesWithProducts branchesWithProducts = new BranchesWithProducts(branchRepo, branchProductRepo);
        TreeGrid<TreeObject> grid = new TreeGrid<>();
        grid.setItems(branchesWithProducts.getBranches(), branchesWithProducts::getProducts);
        grid.addComponentHierarchyColumn(treeObject -> createTreeObjectLabel(treeObject, branchProductRepo));
        add(grid);
    }

    private static class HideProductCheckbox extends Checkbox {
        public HideProductCheckbox(BranchProductKey branchProductKey, BranchProductRepo branchProductRepo) {
            this.setValue(branchProductRepo.findById(branchProductKey).isHide());
            this.addValueChangeListener(event -> {
                branchProductRepo.findByIdThenSetHide(
                        branchProductKey,
                        event.getValue()
                );
            });
            this.setTooltipText("Скрыт ли этот продукт");
        }
    }

    private Component createTreeObjectLabel(TreeObject item, BranchProductRepo branchProductRepo) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        if (item instanceof BranchProduct branchProduct) {
            Product product = branchProduct.productTreeObject.product;
            Branch branch = branchProduct.branchTreeObject.branch;
            horizontalLayout.add(
                    new Span(product.getName()),
                    new HideProductCheckbox(
                            new BranchProductKey(product.getId(), branch.getId()),
                            branchProductRepo
                    ));
        } else if (item instanceof BranchTreeObject branchTreeObject) {
            Branch branch = branchTreeObject.branch;
            horizontalLayout.add(branch.getAddress() + ": " + branch.getPhoneNumber());
        }
        return horizontalLayout;
    }

    private class BranchesWithProducts {
        List<Branch> branches;
        private final BranchProductRepo branchProductRepo;

        public BranchesWithProducts(BranchRepo branchRepo, BranchProductRepo branchProductRepo) {
            this.branches = branchRepo.findAll();
            this.branchProductRepo = branchProductRepo;
        }

        public List<TreeObject> getBranches() {
            return branches.stream()
                    .map(BranchTreeObject::new)
                    .distinct()
                    .collect(Collectors.toList());
        }

        public List<TreeObject> getProducts(TreeObject treeObject) {
            List<TreeObject> treeObjects = List.of();
            if (treeObject instanceof BranchTreeObject branchTreeObject) {
                treeObjects = branchProductRepo.findAllByBranch(branchTreeObject.branch)
                        .stream()
                        .distinct()
                        .map(BranchProduct::new)
                        .map(productTreeObject -> (TreeObject) productTreeObject)
                        .toList();
            }
            return treeObjects;
        }
    }


    @Getter
    @Setter
    public static class Pair<A, B> {
        private A a;
        private B b;

        public Pair(A a, B b) {
            this.a = a;
            this.b = b;
        }
    }


    public abstract class TreeObject {
    }

    public class ProductTreeObject extends TreeObject {
        public Product product;

        public ProductTreeObject(Product product) {
            this.product = product;
        }
    }

    public class BranchTreeObject extends TreeObject {
        public Branch branch;

        public BranchTreeObject(Branch branch) {
            this.branch = branch;
        }
    }

    @Getter
    public class BranchProduct extends TreeObject {
        private final BranchTreeObject branchTreeObject;
        private final ProductTreeObject productTreeObject;

        public BranchProduct(ru.tusur.ShaurmaWebSiteProject.backend.model.BranchProduct branchProduct) {
            this.branchTreeObject = new BranchTreeObject(branchProduct.getBranch());
            this.productTreeObject = new ProductTreeObject(branchProduct.getProduct());
        }
    }
}
