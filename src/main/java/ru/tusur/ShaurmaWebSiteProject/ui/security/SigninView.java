package ru.tusur.ShaurmaWebSiteProject.ui.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import ru.tusur.ShaurmaWebSiteProject.backend.model.UserDetails;
import ru.tusur.ShaurmaWebSiteProject.backend.service.CustomUserDetailsService;


/**
 * This is the default (and only) view in this example.
 * <p>
 * It demonstrates how to create a form using Vaadin and the Binder. The backend
 * service and data class are in the <code>.data</code> package.
 */
@AnonymousAllowed
@PermitAll
@Route("signin")
@PageTitle("Регистрация")
public class SigninView extends VerticalLayout {

    private final PasswordField passwordField1;
    private final PasswordField passwordField2;

    private final CustomUserDetailsService customUserDetailsService;
    private final BeanValidationBinder<UserDetails> binder;

    /**
     * Flag for disabling first run for password validation
     */
    private boolean enablePasswordValidation;

    /**
     * We use Spring to inject the backend into our view
     */
    public SigninView(@Autowired CustomUserDetailsService customUserDetailsService) {

        this.customUserDetailsService = customUserDetailsService;

        /*
         * Create the components we'll need
         */

        H1 title = new H1("Давайте познокомимся");

        TextField usernameField = new TextField("Имя пользователя");

        EmailField emailField = new EmailField("E-Почта");

        passwordField1 = new PasswordField("Пароль");
        passwordField2 = new PasswordField("Повтор паролья");

        Span errorMessage = new Span();

        Button submitButton = new Button("Зарегистрироваться");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        /*
         * Build the visible layout
         */

        // Create a FormLayout with all our components. The FormLayout doesn't have any
        // logic (validation, etc.), but it allows us to configure Responsiveness from
        // Java code and its defaults looks nicer than just using a VerticalLayout.
        FormLayout formLayout = new FormLayout(title, usernameField, emailField, passwordField1, passwordField2, errorMessage, submitButton);

        formLayout.setMaxWidth("360px");
        formLayout.getStyle().set("margin", "0 auto");

        // Allow the form layout to be responsive. On device widths 0-490px we have one
        // column, then we have two. Field labels are always on top of the fields.
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("490px", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP)
        );

        formLayout.setColspan(title, 2);
        formLayout.setColspan(errorMessage, 1);
        formLayout.setColspan(submitButton, 1);

        // Add some styles to the error message to make it pop out
        errorMessage.getStyle().set("color", "var(--lumo-error-text-color)");
        errorMessage.getStyle().set("padding", "15px 0");

        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        // Add the form to the page
        add(formLayout);

        /*
         * Set up form functionality
         */

        /*
         * Binder is a form utility class provided by Vaadin. Here, we use a specialized
         * version to gain access to automatic Bean Validation (JSR-303). We provide our
         * data class so that the Binder can read the validation definitions on that
         * class and create appropriate validators. The BeanValidationBinder can
         * automatically validate all JSR-303 definitions, meaning we can concentrate on
         * custom things such as the passwords in this class.
         */
        binder = new BeanValidationBinder<UserDetails>(UserDetails.class);

        // Basic name fields that are required to fill in
        binder.forField(usernameField).asRequired().bind("username");

        // EmailField uses a Validator that extends one of the built-in ones.
        // Note that we use 'asRequired(Validator)' instead of
        // 'withValidator(Validator)'; this method allows 'asRequired' to
        // be conditional instead of always on. We don't want to require the email if
        // the user declines marketing messages.
        binder.forField(emailField).withValidator(this::emailValidator).bind("email");

        // Another custom validator, this time for passwords
        binder.forField(passwordField1).asRequired().withValidator(this::passwordValidator).bind("transientPassword");
        // We won't bind passwordField2 to the Binder, because it will have the same
        // value as the first field when correctly filled in. We just use it for
        // validation.

        usernameField.addValueChangeListener(e -> {
            if(usernameField.getValue().isEmpty()){
                usernameField.setErrorMessage("Вы пропустили это поле");
            }
        });

        emailField.addValueChangeListener(e -> {
            binder.validate();
            String email = emailField.getValue();
            if (!email.contains("@"))
                emailField.setErrorMessage("Адресс электронной почты должен содержать символ '@'. Похоже, вы его пропустили.");
            if (email.endsWith("@") || email.startsWith("@"))
                emailField.setErrorMessage("Это не полный адресс. Введите его целеком, вместе с той часью, которая находится слева от символа '@'.");
            if (email.length() < 5)
                emailField.setErrorMessage("Проверьте правильность почты");
            if (!email.contains("."))
                emailField.setErrorMessage("Проверьте правильность почты");
        });

        // The second field is not connected to the Binder, but we want the binder to
        // re-check the password validator when the field value changes. The easiest way
        // is just to do that manually.
        passwordField2.addValueChangeListener(e -> {

            // The user has modified the second field, now we can validate and show errors.
            // See passwordValidator() for how this flag is used.
            enablePasswordValidation = true;

            binder.validate();
        });

        // And finally the submit button
        submitButton.addClickListener(e -> {
            try {

                UserDetails detailsBean = new UserDetails();
                binder.writeBean(detailsBean);
                customUserDetailsService.store(detailsBean);
                showSuccess(detailsBean);

            } catch (ValidationException e1) {
                // validation errors are already visible for each field,
                // and bean-level errors are shown in the status label.

                // We could show additional messages here if we want, do logging, etc.

            } catch (CustomUserDetailsService.ServiceException ex) {

                // For some reason, the save failed in the back end.

                // First, make sure we store the error in the server logs (preferably using a
                // logging framework)
                ex.printStackTrace();

                // Notify, and let the user try again.
                errorMessage.setText(ex.getMessage());
            }
        });

        // A label where bean-level error messages go
        binder.setStatusLabel(errorMessage);


    }

    private void showSuccess(UserDetails detailsBean) {
        Notification notification = Notification.show("Готово, добро пожаловать " + detailsBean.getUsername());
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        UI.getCurrent().navigate(LoginView.class);
    }

    /**
     * Method to validate that:
     * <p>
     * 1) Password is at least 8 characters long
     * <p>
     * 2) Values in both fields match each other
     */
    private ValidationResult passwordValidator(String pass1, ValueContext ctx) {

        /*
         * Just a simple length check. A real version should check for password
         * complexity as well!
         */
        if (pass1 == null || pass1.length() < 8) {
            return ValidationResult.error("Пароль должен быть длиной не менее 8 символов");
        }

        if (!enablePasswordValidation) {
            // user hasn't visited the field yet, so don't validate just yet, but next time.
            enablePasswordValidation = true;
            return ValidationResult.ok();
        }

        String pass2 = passwordField2.getValue();

        if (pass1 != null && pass1.equals(pass2)) {
            return ValidationResult.ok();
        }

        return ValidationResult.error("Пароли не совпадают");
    }

    private ValidationResult emailValidator(String email, ValueContext ctx){
        return new EmailValidator("Проверьте правильность почты").apply(email, ctx);
    }


}

