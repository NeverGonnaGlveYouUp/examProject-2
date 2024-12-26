package ru.tusur.ShaurmaWebSiteProject.ui.templates;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility.*;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.ConstraintViolationException;
import org.vaadin.lineawesome.LineAwesomeIcon;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Order;
import ru.tusur.ShaurmaWebSiteProject.backend.model.OrderContentToProductOption;
import ru.tusur.ShaurmaWebSiteProject.backend.model.OrderState;
import ru.tusur.ShaurmaWebSiteProject.backend.model.UserDetails;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.OrderContentRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.OrderContentToProductOptionRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.OrderRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.UserDetailsRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.security.Roles;
import ru.tusur.ShaurmaWebSiteProject.backend.security.SecurityService;
import ru.tusur.ShaurmaWebSiteProject.backend.service.ShopCartService;
import ru.tusur.ShaurmaWebSiteProject.ui.components.Layout;
import ru.tusur.ShaurmaWebSiteProject.ui.components.i18n.UploadExamplesI18N;
import ru.tusur.ShaurmaWebSiteProject.ui.list.MyComponentList;
import ru.tusur.ShaurmaWebSiteProject.ui.list.OrderHistoryListItem;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.MainLayout;
import ru.tusur.ShaurmaWebSiteProject.ui.utils.ImageResourceUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@PageTitle("Профиль")
@RolesAllowed(value = {Roles.ADMIN, Roles.USER})
@Route(value = "Профиль", layout = MainLayout.class)
public class ProfileView extends Main {
    private final SecurityService securityService;
    private final ShopCartService shopCartService;
    private final OrderContentToProductOptionRepo orderContentToProductOptionRepo;
    private UserDetails userDetails;
    private final UserDetailsRepo userDetailsRepo;
    private final OrderRepo orderRepo;
    private final OrderContentRepo orderContentRepo;
    private final TabSheet tabSheet = new TabSheet();

    public ProfileView(SecurityService securityService, UserDetailsRepo userDetailsRepo, OrderRepo orderRepo, OrderContentRepo orderContentRepo, ShopCartService shopCartService, OrderContentToProductOptionRepo orderContentToProductOptionRepo) {
        this.shopCartService = shopCartService;
        this.orderContentToProductOptionRepo = orderContentToProductOptionRepo;
        addClassNames(BoxSizing.BORDER, Display.FLEX, FlexDirection.COLUMN, FlexDirection.Breakpoint.Medium.ROW, Margin.Horizontal.AUTO, MaxWidth.SCREEN_LARGE);
        getStyle().set("display", "inline");
        this.securityService = securityService;
        userDetails = securityService.getAuthenticatedUser();
        this.userDetailsRepo = userDetailsRepo;
        this.orderRepo = orderRepo;
        this.orderContentRepo = orderContentRepo;
        Tab profileData = tabSheet.add("Данные профиля", new LazyComponent(this::createDataForm));
        profileData.getStyle().setDisplay(Style.Display.FLEX).setJustifyContent(Style.JustifyContent.CENTER);
        Tab history = tabSheet.add("История", new LazyComponent(this::createHistory));
        history.getStyle().setDisplay(Style.Display.FLEX).setJustifyContent(Style.JustifyContent.CENTER);
        this.add(tabSheet);
    }

    public class LazyComponent extends Div {
        public LazyComponent(
                SerializableSupplier<? extends Component> supplier) {
            addAttachListener(e -> {
                if (getElement().getChildCount() == 0) {
                    add(supplier.get());
                }
            });
        }
    }

    public Component createHistory() {
        Layout layout = new Layout(createHistoryList());
        layout.addClassNames(BoxSizing.BORDER, MaxWidth.SCREEN_MEDIUM, Padding.LARGE);
        layout.getStyle().set("width", "max-content").set("place-self", "center");
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        return layout;
    }

    public Component createHistoryList(){
        MyComponentList list = new MyComponentList();
        list.addClassNames("divide-y");
        list.setGap(Layout.Gap.MEDIUM);

        H3 title = new H3("История заказов");
        title.addClassNames(FontSize.XLARGE, Margin.Top.XLARGE);

        List<Order> orders = orderRepo.findAllByUserDetailsAndOrderState(userDetails, OrderState.DELIVERED);
//        AtomicInteger counter = new AtomicInteger(0);
        orders.stream().sorted((o1, o2) ->Boolean.compare(o2.isFeatured(), o1.isFeatured())).forEach(order -> {
            order.setOrderContents(new HashSet<>(orderContentRepo.findAllByOrder(order)));
            order.getOrderContents().forEach(orderContent -> orderContent.setOrderContentToProductOption(orderContentToProductOptionRepo.findByOrderContent(orderContent).orElse(null)));
            list.add(new LazyComponent(() -> new OrderHistoryListItem(order, order.hashCode(), orderRepo, shopCartService)));
        });

        Section section = new Section(title, list);
        section.addClassNames(BoxSizing.BORDER, Padding.Horizontal.LARGE);
        return section;
    }

    public Component createDataForm() {
        Layout layout = new Layout(createPublicInformation(), createContactInformation(), createPassword());
        layout.addClassNames(BoxSizing.BORDER, MaxWidth.SCREEN_MEDIUM, Padding.LARGE);
        layout.getStyle().set("width", "max-content").set("place-self", "center");
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        return layout;
    }

    public Component createPublicInformation() {
        Avatar avatar = new Avatar(userDetails.getUsername());
        avatar.addThemeVariants(AvatarVariant.LUMO_XLARGE);
        avatar.setImageResource(ImageResourceUtils.getImageResource(userDetails.getAvatarUrl()));

//        Button uploadButton = new Button("Загрузить");
//        uploadButton.addClickListener(event -> openUploadDialog(avatarLayout));
//        uploadButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
//
//        Upload upload = new Upload();
//        upload.setDropAllowed(false);
//        upload.setMaxFiles(1);
//        upload.setUploadButton(uploadButton);

//        Button delete = new Button("Удалить");
//        delete.addClickListener(event -> {
//            userDetails.setAvatarUrl("");
//            avatarLayout.removeAll();
//            avatarLayout.add(avatar);
//        });
//        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);

        Layout avatarLayout = new Layout(avatar);
        avatarLayout.addClassNames(Margin.Bottom.XSMALL, Margin.Top.MEDIUM);
        avatarLayout.setAlignItems(Layout.AlignItems.CENTER);
        avatarLayout.setGap(Layout.Gap.LARGE);
        avatarLayout.addClickListener(event -> openUploadDialog(avatarLayout));

        TextField username = new TextField("Имя пользователя");
        username.setValue(userDetails.getUsername());
        username.setPrefixComponent(LineAwesomeIcon.USER_TAG_SOLID.create());

        Button saveButton = new Button("Сохранить");
        saveButton.getStyle().setMarginTop("36px");
        saveButton.addClassNames(AlignItems.CENTER, Background.PRIMARY,
                BorderRadius.MEDIUM, Display.FLEX, Height.MEDIUM, JustifyContent.CENTER, TextColor.PRIMARY_CONTRAST);
        saveButton.addClickListener(event -> {
            //dirty but easy solution todo remove this and make in somewhat more sane way
            List<String> allUsernames = userDetailsRepo.findAll().stream().map(userDetails1 -> userDetails.getUsername()).toList();
            if (Objects.equals(username.getValue(), "")) username.setErrorMessage("Имя пользователя не может быть пустым");
            //checks if username exists
            else if (allUsernames.contains(username.getValue()))username.setErrorMessage("Имя пользователя уже занято");
            else {
                userDetails.setUsername(username.getValue());
                userDetailsRepo.save(userDetails);
            }
        });

        Layout layout = new Layout(avatarLayout, username, saveButton);
        layout.setColumnGap(Layout.Gap.MEDIUM);
        return layout;
    }

    public Component createContactInformation() {
        EmailField email = new EmailField("Email");
        email.setPrefixComponent(LineAwesomeIcon.ENVELOPE.create());
        email.setValue(userDetails.getEmail());

        Button saveButton = new Button("Сохранить");
        saveButton.getStyle().setMarginTop("36px");
        saveButton.addClassNames(AlignItems.CENTER, Background.PRIMARY,
                BorderRadius.MEDIUM, Display.FLEX, Height.MEDIUM, JustifyContent.CENTER, TextColor.PRIMARY_CONTRAST);
        saveButton.addClickListener(event -> {
            //dirty but easy solution todo remove this and make in somewhat more sane way
            try {
                userDetails.setEmail(email.getValue());
                userDetailsRepo.save(userDetails);
            } catch (ConstraintViolationException constraintViolationException){
                email.setErrorMessage("Невалидный email");
            }
        });

        Layout layout = new Layout(email, saveButton);
        layout.setFlexDirection(Layout.FlexDirection.ROW);
        layout.setGap(Layout.Gap.MEDIUM);
        layout.setJustifyContent(Layout.JustifyContent.END);
        return layout;
    }

    public Component createPassword() {
        H2 title = new H2("Пароль");
        title.addClassNames(FontSize.XLARGE, Margin.Top.XLARGE);
        title.setId(title.getText().replace(" ", "-").toLowerCase());

        TextField currentPassword = new TextField("Текущий пароль");
        TextField newPassword = new TextField("Новый пароль");
        TextField confirmPassword = new TextField("Подтвердите пароль");

        Layout layout = new Layout(title, currentPassword, newPassword, confirmPassword);
        layout.setFlexDirection(Layout.FlexDirection.COLUMN);
        return layout;
    }

    private void openUploadDialog(Layout imageLayout) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Загрузка изображения");
        VerticalLayout verticalLayout1 = new VerticalLayout();
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.addSucceededListener(event -> {
            String fileName = event.getFileName();
            InputStream inputStream = buffer.getInputStream();
            try {
                Files.write(Path.of("src/main/resources/META-INF/resources/images/" + fileName), buffer.getInputStream().readAllBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            StreamResource imageResource = new StreamResource(fileName, (InputStreamFactory) () -> inputStream);

            userDetails.setAvatarUrl("src/main/resources/META-INF/resources/images/" + fileName);
            userDetails = userDetailsRepo.save(userDetails);
            imageLayout.removeAll();
            UI.getCurrent().access(() -> {
                Avatar avatar = new Avatar(userDetails.getUsername());
                avatar.addThemeVariants(AvatarVariant.LUMO_XLARGE);
                avatar.getStyle().set("min-width", "-webkit-fill-available");
                avatar.setImageResource(ImageResourceUtils.getImageResource(userDetails.getAvatarUrl()));
                imageLayout.removeAll();
                imageLayout.add(avatar);
            });
            dialog.close();
        });
        UploadExamplesI18N i18n = new UploadExamplesI18N();
        H4 title = new H4("Загрузить изображение");
        Paragraph hint = new Paragraph("Размер файла должен быть меньше или равен 5 МБ. Принимаются только файлы \".png\", \".jpeg\" и \".jpg\"...");
        upload.setI18n(i18n);
        upload.setWidthFull();
        upload.setAcceptedFileTypes(".png", ".jpeg", ".jpg");
        upload.setMaxFileSize(5 * 1024 * 1024);
        upload.setMaxFiles(1);
        verticalLayout1.setWidthFull();
        verticalLayout1.add(title, hint, upload);
        dialog.add(verticalLayout1);
        dialog.open();
    }

}