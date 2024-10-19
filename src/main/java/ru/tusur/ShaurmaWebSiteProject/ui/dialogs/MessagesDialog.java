package ru.tusur.ShaurmaWebSiteProject.ui.dialogs;

import com.vaadin.flow.component.Unit;
import ru.tusur.ShaurmaWebSiteProject.ui.list.MessageLinkListItem;
import ru.tusur.ShaurmaWebSiteProject.ui.list.MyComponentList;
import ru.tusur.ShaurmaWebSiteProject.ui.mainLayout.HomeView;

import java.time.LocalDateTime;

public class MessagesDialog extends NativeDialog {

    public MessagesDialog() {
        setAriaLabel("Сообщения");
        setMaxWidth(30, Unit.REM);

        // TODO: Mobile positioning
        // Position
        setRight(0.5f, Unit.REM);
        setTop(3.5f, Unit.REM);

        // Links
        MyComponentList list = new MyComponentList(
                new MessageLinkListItem("Sarah Anderson", "Hi John! Found a quicker way for you to access project files. Check 'Resources' for a shortcut to key documents!",
                        LocalDateTime.now().minusMinutes(12), HomeView.class),
                new MessageLinkListItem("Daniel Parker", "Hey John! Noticed an error in your report's data. Double-check Section 3 for accuracy before finalizing.",
                        LocalDateTime.now().minusMinutes(36), HomeView.class),
                new MessageLinkListItem("Rachel Hughes", "Hello John! Seeking your input for the upcoming presentation. Please review slides 5 to 10 and share your thoughts.",
                        LocalDateTime.now().minusMinutes(42), HomeView.class),
                new MessageLinkListItem("Andrew Murphy", "Hey John! Let's discuss the client proposal in the 'Ideas' section. Your insights are valuable!",
                        LocalDateTime.now().minusMinutes(56), HomeView.class)
        );
        add(list);
    }

}