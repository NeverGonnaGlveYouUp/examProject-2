package ru.tusur.ShaurmaWebSiteProject.ui.security;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import ru.tusur.ShaurmaWebSiteProject.backend.service.CustomUserDetailsService;
import ru.tusur.ShaurmaWebSiteProject.ui.templates.MainProductView;

import java.util.Collections;

@AnonymousAllowed
@Route("login")
@PageTitle("Вход")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    @Autowired
    CustomUserDetailsService customUserDetailsService;
    LoginForm loginForm;
    LoginI18n.Form i18nForm;
    LoginI18n i18n;
    Button signup;
    LoginI18n.ErrorMessage errorMessage;

    public LoginView(){

        i18n = LoginI18n.createDefault();
        i18nForm = i18n.getForm();
        errorMessage = new LoginI18n.ErrorMessage();

        i18nForm.setTitle("Привет, с возвращением");
        i18nForm.setUsername("Имя пользователя");
        i18nForm.setPassword("Пароль");
        i18nForm.setSubmit("Войти");
        i18nForm.setForgotPassword("Востановить пароль");
        errorMessage.setMessage("Проверьте, что вы ввели исправьте имя пользователя и пароль и повторите попытку");
        errorMessage.setTitle("Неверное имя пользователя или пароль");
        errorMessage.setPassword("Требуется пароль");
        errorMessage.setUsername("Требуется имя пользователя");
        i18n.setErrorMessage(errorMessage);

        loginForm  = new LoginForm();
        signup = new Button("Создать аккаунт", buttonClickEvent -> UI.getCurrent().navigate(SigninView.class));

        setClassName("login-view");
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        loginForm.setI18n(i18n);
        loginForm.setAction("login");
        loginForm.addLoginListener(loginEvent ->
                {
                    try {
                        customUserDetailsService.checkCredentials(
                                loginEvent.getUsername(),
                                loginEvent.getPassword()
                        );
                        UI.getCurrent().navigate(MainProductView.class);
                    } catch (AuthException e) {
                        loginForm.setError(true);
                    }
                }
        );
//        loginForm.addClassName("yellow_submit_button");
        add(
                new H1("PitaMaster"),
                loginForm,
                signup
        );
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent){
        if(!beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .getOrDefault("error", Collections.emptyList())
                .isEmpty()){
            loginForm.setError(true);
        }
    }

}