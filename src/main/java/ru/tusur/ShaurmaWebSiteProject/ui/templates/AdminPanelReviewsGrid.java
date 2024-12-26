package ru.tusur.ShaurmaWebSiteProject.ui.templates;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.grid.ColumnRendering;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIcon;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Review;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.LikesRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.ReviewRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.security.Roles;
import ru.tusur.ShaurmaWebSiteProject.ui.components.Layout;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.MainLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


@Route(value = "Панель администратора - таблица отзывов", layout = MainLayout.class)
@RolesAllowed(value = {Roles.ADMIN})
@PageTitle("Панель администратора - таблица отзывов")
public class AdminPanelReviewsGrid extends Main {

    public static final String name = "Отзывы";
    private final LikesRepo likesRepo;
    private final Grid<Review> reviewGrid = new Grid<>(Review.class, false);
    private final List<Review> reviews = new ArrayList<>();
    private final Div hint = new Div();
    private final ReviewRepo reviewRepo;
    private Review reviewInEditing;
    private Section sidebar;

    public AdminPanelReviewsGrid(ReviewRepo reviewRepo, LikesRepo likesRepo) {
        this.reviewRepo = reviewRepo;
        this.likesRepo = likesRepo;
        reviews.addAll(reviewRepo.findAll());
        reviewInEditing = reviews.getFirst();
        createGrid();
        addClassNames(LumoUtility.Display.FLEX, LumoUtility.Height.FULL, LumoUtility.Overflow.HIDDEN, Layout.FlexDirection.ROW.getClassName());
        Layout layout = new Layout(createHat(), reviewGrid, hint);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        layout.getStyle().setWidth("-webkit-fill-available");
        add(createSidebar(), layout);
        closeSidebar();
    }

    private void createGrid() {
        SimpleDateFormat sdf = new SimpleDateFormat();
        reviewGrid.getStyle().setMargin("8px").setHeight("500px");
        reviewGrid.addColumn(Review::getUserDetails).setHeader("Имя пользователя").setSortable(true).setResizable(true);
        reviewGrid.addColumn(review -> sdf.format(review.getDate())).setHeader("Время").setSortable(true).setResizable(true);
        reviewGrid.addColumn(Review::getGrade).setHeader("Оценка").setSortable(true);
        reviewGrid.addColumn(
                review ->
                        likesRepo.findAllByReview(review).size())
                .setHeader("Количество лайков").setSortable(true);
        reviewGrid.addColumn(
                // i hate this thing
                review ->
                        likesRepo.findAllByReview(review)
                                .stream()
                                .mapToInt(value -> value.getLikes().getAnInt()).sum() / (double)likesRepo.findAllByReview(review).size())
                .setHeader("Среднее лайков/дизлайков").setSortable(true).setResizable(true);
        reviewGrid.addColumn(
                new ComponentRenderer<>(Layout::new, ((layout, review) -> {
                    layout.setFlexDirection(Layout.FlexDirection.COLUMN);
                    layout.setGap(Layout.Gap.SMALL);
                    Span span = new Span(review.getProduct().getName());
                    Span span1 = new Span(review.getBranch().getAddress());
                    layout.add(span, span1);
                }))).setHeader("Источник").setSortable(true).setResizable(true);
        reviewGrid.addColumn(
                new ComponentRenderer<>(Checkbox::new, (checkbox, review) -> {
                    checkbox.setValue(review.isHide());
                    checkbox.setReadOnly(true);
                })).setHeader("Скрыто").setSortable(true).setComparator(Review::isHide);
        reviewGrid.setItems(reviews);
        reviewGrid.setColumnRendering(ColumnRendering.LAZY);
        refreshGrid();
        hint.setVisible(false);
        hint.setText("Отзывов нет");
        hint.getStyle().set("padding", "var(--lumo-size-l)")
                .set("text-align", "center").set("font-style", "italic")
                .set("color", "var(--lumo-contrast-70pct)");
    }

    private MenuItem createMenuItem(MenuBar menu, String iconName, String tooltipText, ComponentEventListener<ClickEvent<MenuItem>> listener, String label) {
        SvgIcon icon = new SvgIcon(iconName);
        MenuItem menuItem = menu.addItem(icon, tooltipText, listener);
        if (label != null) {
            menuItem.setAriaLabel(label);
            menuItem.add(new Text(label));
        }
        return menuItem;
    }

    private Layout createHat() {
        Layout layout = new Layout();
        layout.setFlexDirection(Layout.FlexDirection.ROW);
        layout.setGap(Layout.Gap.SMALL);
        layout.getStyle().setMargin("8px");

        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_ICON);
        menuBar.getStyle().setPaddingTop("35px");
        createMenuItem(menuBar, "line-awesome/svg/" + LineAwesomeIcon.BARS_SOLID.getSvgName() + ".svg", "Открыть форму добавки", _ -> toggleSidebar(), "Форма");

        layout.add(menuBar);

        return layout;
    }

    private void refreshGrid() {
        if (!reviews.isEmpty()) {
            reviewGrid.setVisible(true);
            hint.setVisible(false);
            reviewGrid.getDataProvider().refreshAll();
        } else {
            reviewGrid.setVisible(false);
            hint.setVisible(true);
        }
    }

    private void toggleSidebar() {
        if (this.sidebar.isEnabled()) {
            closeSidebar();
        } else {
            openSidebar();
        }
    }

    private void openSidebar() {
        this.sidebar.setEnabled(true);
        this.sidebar.addClassNames(LumoUtility.Border.RIGHT);
        this.sidebar.getStyle().remove("margin-inline-start");
        this.sidebar.addClassNames("start-0");
        this.sidebar.removeClassName("-start-full");
    }

    private void closeSidebar() {
        this.sidebar.setEnabled(false);
        this.sidebar.removeClassName(LumoUtility.Border.RIGHT);
        // Desktop
        this.sidebar.getStyle().set("margin-inline-start", "-25rem");
        // Mobile
        this.sidebar.addClassNames("-start-full");
        this.sidebar.removeClassName("start-0");
    }

    private Section createSidebar() {
        AtomicReference<Review> reviewAtomicReference = new AtomicReference<>();
        H2 title = new H2("Редактирование");
        title.addClassNames(LumoUtility.FontSize.MEDIUM);

        Button close = new Button(LineAwesomeIcon.TIMES_SOLID.create(), e -> closeSidebar());
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.setAriaLabel("Закрыть");
        close.setTooltipText("Закрыть");

        Layout header = new Layout(title, close);
        header.addClassNames(LumoUtility.Padding.End.MEDIUM, LumoUtility.Padding.Start.LARGE, LumoUtility.Padding.Vertical.SMALL);
        header.setAlignItems(Layout.AlignItems.CENTER);
        header.setJustifyContent(Layout.JustifyContent.BETWEEN);

        TextField usernameField = new TextField("Имя пользователя");
        usernameField.setPlaceholder("Имя пользователя");
        usernameField.setSuffixComponent(getForwardComponent());
        usernameField.setReadOnly(true);

        TextArea contentField = new TextArea();
        contentField.setLabel("Содержание отзыва");
        contentField.setReadOnly(true);
        contentField.getStyle().setMarginTop("8px");

        TextArea reasonField = new TextArea();
        reasonField.setEnabled(false);
        reasonField.setLabel("Причина сокрытия");
        reasonField.setPlaceholder("Причина сокрытия отзыва");
        reasonField.getStyle().setMarginTop("8px");

        Checkbox checkboxForReviewHide = new Checkbox("Скрыть отзыв");
        checkboxForReviewHide.addValueChangeListener(event -> {
            reasonField.setEnabled(event.getValue());
        });

//        Layout checkboxForReviewHideLayout = new Layout(usernameField, getForwardComponent());
//        checkboxForReviewHideLayout.setGap(Layout.Gap.SMALL);
//        checkboxForReviewHideLayout.setFlexDirection(Layout.FlexDirection.ROW);

        Button updateProductButton = new Button("Сохранить");
        updateProductButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        updateProductButton.addClickListener(event -> {
            Review o = reviewAtomicReference.get();
            if (!reviews.isEmpty()) {
                reviewGrid.setVisible(true);
                hint.setVisible(false);
                reviews.remove(o);
                o.setHide(checkboxForReviewHide.getValue());
                o.setReason(reasonField.getValue());
                reviews.add(reviewRepo.save(o));
                reviewGrid.getDataProvider().refreshItem(o);
            } else {
                reviewGrid.setVisible(false);
                hint.setVisible(true);
            }
            if (o.equals(reviewInEditing)) {
                reviewInEditing = null;
                reviewRepo.save(o);
            } else reviewRepo.save(o);
        });

        Layout form = new Layout(usernameField, contentField, reasonField, checkboxForReviewHide, updateProductButton);
        form.addClassNames(LumoUtility.Padding.Horizontal.LARGE);
        form.setFlexDirection(Layout.FlexDirection.COLUMN);
        reviewGrid.addItemDoubleClickListener(event -> openSidebar());
        reviewGrid.addSelectionListener(event -> {
            if (event.getFirstSelectedItem().isPresent())
                reviewAtomicReference.set(event.getFirstSelectedItem().get());
            usernameField.setValue(reviewAtomicReference.get().getUserDetails().getUsername());
            contentField.setValue(reviewAtomicReference.get().getContent());
            reasonField.setValue(reviewAtomicReference.get().getReason());
            checkboxForReviewHide.setValue(reviewAtomicReference.get().isHide());
        });
        this.sidebar = new Section(header, form);
        this.sidebar.addClassNames("backdrop-blur-sm", "bg-tint-90", LumoUtility.Border.RIGHT,
                LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.Position.ABSOLUTE, "lg:static", "bottom-0", "top-0",
                "transition-all", "z-10");
        this.sidebar.setWidth(25, Unit.REM);
        return this.sidebar;
    }

    private Component getForwardComponent(){
        SvgIcon icon = LineAwesomeIcon.SHARE_SQUARE.create();
        icon.addClickListener(event -> {
            //todo add forward to userGrid
        });
        return icon;
    }

}
