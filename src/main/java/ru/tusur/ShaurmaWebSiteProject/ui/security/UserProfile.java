package ru.tusur.ShaurmaWebSiteProject.ui.security;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import org.jetbrains.annotations.NotNull;
import ru.tusur.ShaurmaWebSiteProject.backend.model.UserDetails;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.UserDetailsRepo;
import ru.tusur.ShaurmaWebSiteProject.backend.security.Roles;
import ru.tusur.ShaurmaWebSiteProject.backend.security.SecurityService;
import ru.tusur.ShaurmaWebSiteProject.ui.components.i18n.UploadExamplesI18N;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.Header;
import ru.tusur.ShaurmaWebSiteProject.ui.mainPage.MainPage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RoutePrefix(value = "account")
@CssImport(value = "vaadin-app-layout.css", themeFor = "vaadin-app-layout")
public class UserProfile extends AppLayout implements Header {

    SecurityService securityService;

    public UserProfile(SecurityService securityService, UserDetailsRepo userDetailsRepo) {
        this.securityService = securityService;

        DrawerToggle drawerToggle = new DrawerToggle();
        addToNavbar(drawerToggle, getMyTitle("Профиль"), getMyAvatarInMyNavBar(securityService));
        SideNav nav = getSideNav();
        Scroller scroller = new Scroller(nav);
        scroller.setClassName(LumoUtility.Padding.SMALL);
        UI.getCurrent().getPage().retrieveExtendedClientDetails(details -> {
            if(details.getWindowInnerWidth() >= 800) addClassName("PCLayout");
            else removeClassName("PCLayout");
        });

//        UI.getCurrent().getPage().retrieveExtendedClientDetails(details -> {
//            windowWidth = details.getWindowInnerWidth();
//        });
//        UI.getCurrent().getPage().addBrowserWindowResizeListener(event -> {
//            windowWidth = event.getWidth();
//        });

        addToDrawer(getSideNav());
    }

    private SideNav getSideNav() {
        SideNav nav = new SideNav();
        SideNavItem toMain = new SideNavItem("На главную", MainPage.class, VaadinIcon.ARROW_LEFT.create());
        LinkedList<SideNavItem> list = new LinkedList<>(List.of(
                new SideNavItem(Main.name, Main.class, VaadinIcon.USER.create()),
                new SideNavItem(PersonalDetails.name, PersonalDetails.class, VaadinIcon.USER_CARD.create()),
                new SideNavItem(Featured.name, Featured.class, VaadinIcon.USER_HEART.create()),
                new SideNavItem(History.name, History.class, VaadinIcon.RECORDS.create())));
        list.add(toMain);
        list.forEach(nav::addItem);
        return nav;
    }

    @RolesAllowed(value = {Roles.USER, Roles.ADMIN})
    @PageTitle("Профиль - Главная")
    @Route(value = "main", layout = UserProfile.class)
    public static class Main extends VerticalLayout {
        public final static String name = "Главная";
        UserDetails userDetails;

        Main(SecurityService securityService){
            userDetails = securityService.getAuthenticatedUser();
            addClassNames(LumoUtility.Gap.MEDIUM);
            getStyle().setPosition(Style.Position.RELATIVE);
            add(getUserCell());
        }

        private Div getUserCell(){
            Div div = new Div();
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            VerticalLayout verticalLayout = new VerticalLayout();
            Avatar avatar = new Avatar(userDetails.getUsername());
            avatar.setColorIndex((userDetails.getUsername().hashCode() % 7) - 1);
            avatar.setImage(userDetails.getAvatarUrl());
            Span span1 = new Span(Optional.of(userDetails.getUsername()).orElse("username - not present"));
            Span span2 = new Span("email: " + Optional.of(userDetails.getEmail()).orElse("email - not present"));
            Icon icon = new Icon(VaadinIcon.ANGLE_RIGHT);

            div.add(horizontalLayout);
            div.addClassNames("userCell", "trans03s");
            div.setWidth("100%");

            horizontalLayout.addClassNames(LumoUtility.FlexWrap.WRAP, LumoUtility.AlignSelf.BASELINE);
            horizontalLayout.addClickListener(event -> UI.getCurrent().navigate(PersonalDetails.class));
            horizontalLayout.add(avatar, verticalLayout, icon);
            horizontalLayout.getStyle()
                    .setPaddingLeft("16px")
                    .setPaddingRight("16px")
                    .setPaddingTop("8px")
                    .setPaddingBottom("8px")
                    .setBorder("1px");

            verticalLayout.add(span1, span2);
            verticalLayout.getStyle().setFlexGrow("4").setPadding("0px");
            verticalLayout.setSpacing(false);

            span1.getStyle()
                    .setPadding("0px")
                    .setMargin("0px")
                    .setFontWeight("bold")
                    .setFontSize("14px");

            span2.addClassNames(LumoUtility.FontSize.XSMALL, LumoUtility.FontWeight.THIN);
            span2.getStyle()
                    .setPadding("0px")
                    .setMargin("0px")
                    .setFontWeight("thin")
                    .setFontSize("12px");;

            icon.addClassName(LumoUtility.AlignSelf.BASELINE);
            return div;
        }
    }

    @RolesAllowed(value = {Roles.USER, Roles.ADMIN})
    @PageTitle("Профиль - Личные данные")
    @Route(value = "details", layout = UserProfile.class)
    @CssImport(value = "vaadin-menu-bar-button.css", themeFor = "vaadin-menu-bar-button")
    public static class PersonalDetails extends VerticalLayout implements Header {
        public final static String name = "Личные данные";
        private final SecurityService securityService;
        private final UserDetailsRepo userDetailsRepo;
        private Avatar avatar;
        private Icon checkIcon;
        private Span passwordStrengthText;
        private Button saveButton;

        PersonalDetails(SecurityService securityService, UserDetailsRepo userDetailsRepo){
            this.securityService = securityService;
            this.userDetailsRepo = userDetailsRepo;
            addClassNames(LumoUtility.Gap.MEDIUM);
            getStyle().setPosition(Style.Position.RELATIVE);
            add(getUserDetailsInputForms());

        }

        private Div getUserDetailsInputForms(){
            final UserDetails userDetails = securityService.getAuthenticatedUser();
            Div div = new Div();
            FormLayout formLayout = new FormLayout();
            HorizontalLayout horizontalLayout = new HorizontalLayout();
            VerticalLayout verticalLayout = new VerticalLayout();
            avatar = new Avatar(userDetails.getUsername());
            avatar.setColorIndex((userDetails.getUsername().hashCode() % 7) - 1);
            avatar.setImage(userDetails.getAvatarUrl());
            avatar.addThemeVariants(AvatarVariant.LUMO_XLARGE);
            MenuBar menuBar = new MenuBar();
            menuBar.addClassName("trans03s");
            menuBar.getStyle().set("border-radius", "30px");
            menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
            MenuItem menuItem = menuBar.addItem(avatar);
            SubMenu subMenu = menuItem.getSubMenu();
            Dialog dialog = attachUploadDialog();
            ComponentEventListener<ClickEvent<MenuItem>> listener = e -> {
                if (Objects.equals(e.getSource().getText(), "Изменить фотографию")){
                    dialog.open();
                } else if(Objects.equals(e.getSource().getText(), "Удалить фотографию")){
                    avatar.setImage(null);
                    userDetails.setAvatarUrl(null);
                }
            };
            subMenu.addItem("Изменить фотографию", listener);
            subMenu.addItem("Удалить фотографию", listener).addClassName(LumoUtility.TextColor.WARNING);



            Span span1 = new Span(Optional.of(userDetails.getUsername()).orElse("username - not present"));
            Span span2 = new Span("email: " + Optional.of(userDetails.getEmail()).orElse("email - not present"));

            verticalLayout.add(span1, span2);
            verticalLayout.getStyle().setFlexGrow("4").setPadding("0px");
            verticalLayout.setSpacing(false);
            horizontalLayout.addClassNames(LumoUtility.FlexWrap.WRAP, LumoUtility.AlignSelf.BASELINE);
            horizontalLayout.addClickListener(_ -> UI.getCurrent().navigate(PersonalDetails.class));
            horizontalLayout.add(menuBar, verticalLayout);
            horizontalLayout.getStyle()
                    .setPaddingLeft("0px")
                    .setPaddingRight("16px")
                    .setPaddingTop("8px")
                    .setPaddingBottom("8px")
                    .setBorder("1px");
            span1.getStyle()
                    .setPadding("0px")
                    .setMargin("0px")
                    .setFontWeight("bold")
                    .setFontSize("14px");
            span2.addClassNames(LumoUtility.FontSize.XSMALL, LumoUtility.FontWeight.THIN);
            span2.getStyle()
                    .setPadding("0px")
                    .setMargin("0px")
                    .setFontWeight("thin")
                    .setFontSize("12px");

            TextField nameField = new TextField();
            nameField.setLabel("Имя пользователя");
            nameField.setValue(userDetails.getUsername());
            nameField.setErrorMessage("Введите имя пользователя");

            EmailField emailField = new EmailField();
            emailField.setLabel("Почта");
            emailField.getElement().setAttribute("name", "email");
            emailField.setValue(userDetails.getEmail());
            emailField.setClearButtonVisible(true);
            emailField.setErrorMessage("Введите корректный адрес электронной почты");

            saveButton = new Button("Сохранить");
            saveButton.addClickListener(event -> {
                final UserDetails userDetailsAnon = securityService.getAuthenticatedUser();
                userDetailsAnon.setUsername(nameField.getValue());
                userDetailsAnon.setEmail(emailField.getValue());
                userDetailsRepo.save(userDetailsAnon);
                avatar.setImage(userDetails.getAvatarUrl());
                Notification notification = Notification.show("Данные обновлены!");
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setPosition(Notification.Position.BOTTOM_END);
                notification.setDuration(5000);
            });
            nameField.addValueChangeListener(event -> saveButton.setEnabled(!userDetails.getUsername().equals(event.getValue())));
            emailField.addValueChangeListener(event -> {
                String email = emailField.getValue();
                if (!email.contains("@")) {
                    emailField.setErrorMessage("Адресс электронной почты должен содержать символ '@'. Похоже, вы его пропустили.");
                    return;
                }
                if (email.endsWith("@") || email.startsWith("@")) {
                    emailField.setErrorMessage("Это не полный адресс. Введите его целеком, вместе с той часью, которая находится слева от символа '@'.");
                    return;
                }
                if (email.length() < 5) {
                    emailField.setErrorMessage("Проверьте правильность почты");
                    return;
                }
                if (!email.contains(".")) {
                    emailField.setErrorMessage("Проверьте правильность почты");
                    return;
                }
                saveButton.setEnabled(!userDetails.getEmail().equals(event.getValue()));
            });
            saveButton.setEnabled(false);

            formLayout.add(horizontalLayout, nameField, emailField, saveButton);
            div.add(formLayout);
            return div;
        }

        private PasswordField getPasswordChangeForm(){
            UserDetails userDetails = securityService.getAuthenticatedUser();
            PasswordField passwordField = new PasswordField();
            passwordField.setLabel("Пароль");
            passwordField.setValue(userDetails.getPassword());
            checkIcon = VaadinIcon.CHECK.create();
            checkIcon.setVisible(false);
            checkIcon.getStyle().set("color", "var(--lumo-success-color)");
            passwordField.setSuffixComponent(checkIcon);
            Div passwordStrength = new Div();
            passwordStrengthText = new Span();
            passwordStrength.add(new Text("Надежность пароля: "), passwordStrengthText);
            passwordField.setHelperComponent(passwordStrength);
            passwordField.setValueChangeMode(ValueChangeMode.EAGER);
            passwordField.addValueChangeListener(e -> {
                String password = e.getValue();
                updateHelper(password);
            });
            passwordField.setErrorMessage("Введите корректный адрес электронной почты");
            updateHelper("");
            return passwordField;
        }

        private void updateHelper(String password) {
            if (password.length() > 9) {
                passwordStrengthText.setText("strong");
                passwordStrengthText.getStyle().set("color",
                        "var(--lumo-success-color)");
                checkIcon.setVisible(true);
            } else if (password.length() > 5) {
                passwordStrengthText.setText("moderate");
                passwordStrengthText.getStyle().set("color", "#e7c200");
                checkIcon.setVisible(false);
            } else {
                passwordStrengthText.setText("weak");
                passwordStrengthText.getStyle().set("color",
                        "var(--lumo-error-color)");
                checkIcon.setVisible(false);
            }
        }

        private @NotNull Dialog attachUploadDialog() {
            UserDetails userDetails = securityService.getAuthenticatedUser();
            Dialog dialog = new Dialog();
            dialog.setHeaderTitle("Загрузка аватара");
            VerticalLayout verticalLayout1 = new VerticalLayout();
            MemoryBuffer buffer = new MemoryBuffer();
            Upload upload = new Upload(buffer);
            upload.addSucceededListener(event -> {
                String filePathName = "src/main/resources/META-INF/resources/images/" +  event.getFileName();
                InputStream inputStream = buffer.getInputStream();
                try {
                    Files.write(Path.of(filePathName), buffer.getInputStream().readAllBytes());
                    StreamResource imageResource = new StreamResource(event.getFileName(), (InputStreamFactory) () -> inputStream);
                    userDetails.setAvatarUrl(filePathName);
                    saveButton.setEnabled(true);
                    avatar.setImageResource(imageResource);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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
            return dialog;
        }

    }

    @RolesAllowed(value = {Roles.USER, Roles.ADMIN})
    @PageTitle("Профиль - Избранное")
    @Route(value = "featured", layout = UserProfile.class)
    public static class Featured extends VerticalLayout {
        public final static String name = "Избранное";
        Featured(){

        }
    }

    @RolesAllowed(value = {Roles.USER, Roles.ADMIN})
    @PageTitle("Профиль - История")
    @Route(value = "history", layout = UserProfile.class)
    public static class History extends VerticalLayout {
        public final static String name = "История";
        History(){

        }
    }
}
