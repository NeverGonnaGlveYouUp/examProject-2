package ru.tusur.ShaurmaWebSiteProject.ui.list;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.jetbrains.annotations.NotNull;
import org.vaadin.lineawesome.LineAwesomeIcon;
import ru.tusur.ShaurmaWebSiteProject.backend.model.LikeState;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Likes;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Review;
import ru.tusur.ShaurmaWebSiteProject.backend.model.UserDetails;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.LikesRepo;
import ru.tusur.ShaurmaWebSiteProject.ui.components.Badge;
import ru.tusur.ShaurmaWebSiteProject.ui.components.Layout;
import ru.tusur.ShaurmaWebSiteProject.ui.utils.BadgeVariant;
import ru.tusur.ShaurmaWebSiteProject.ui.utils.ImageResourceUtils;
import ru.tusur.ShaurmaWebSiteProject.ui.utils.StarsUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class ProductReviewListItem extends com.vaadin.flow.component.html.ListItem {
    private final UserDetails userDetails;
    private final UserDetails userDetailsFromReview;
    private final Review review;
    private final LikesRepo likesRepo;


    public ProductReviewListItem(UserDetails user, Review review, LikesRepo likesRepo) {
        this.userDetails = user;
        this.userDetailsFromReview = review.getUserDetails();
        this.review = review;
        this.likesRepo = likesRepo;
        createContent(review);
    }

    private void createContent(Review review) {

        Layout userNameStars = new Layout();
        userNameStars.setAlignItems(Layout.AlignItems.START);
        userNameStars.setFlexDirection(Layout.FlexDirection.COLUMN);
        userNameStars.add(new Span(userDetailsFromReview.getUsername()), createStars());

        Layout head = new Layout();
        head.setFlexDirection(Layout.FlexDirection.ROW);
        head.setJustifyContent(Layout.JustifyContent.BETWEEN);
        head.setAlignItems(Layout.AlignItems.STRETCH);
        head.setAlignItems(Layout.AlignItems.CENTER);
        head.add(userNameStars, createTimeStampAndModOptions());

        Layout secondary = new Layout();
        secondary.setFlexDirection(Layout.FlexDirection.ROW);
        secondary.setGap(Layout.Gap.SMALL);
        secondary.addClassName(LumoUtility.AlignSelf.STRETCH);

        if (userDetails == null) secondary.add(createTextArea());
        else secondary.add(createReviewReactions(review), createTextArea());

        Layout mainLayout = new Layout();
        mainLayout.setAlignItems(Layout.AlignItems.START);
        mainLayout.setFlexDirection(Layout.FlexDirection.COLUMN);
        mainLayout.addClassNames(LumoUtility.Margin.Top.LARGE, LumoUtility.AlignSelf.STRETCH);
        mainLayout.add(new HorizontalLayout(createAvatar(), head), secondary);

        this.getStyle().set("list-style-type", "none");
        this.add(mainLayout);
    }

    private Layout createTimeStampAndModOptions() {
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        Span span = new Span(formatter.format(review.getDate()));

        Layout layout = new Layout();
        layout.setAlignItems(Layout.AlignItems.STRETCH);
        layout.setAlignItems(Layout.AlignItems.CENTER);
        layout.setGap(Layout.Gap.SMALL);

        if (userDetails == null) layout.add(span);
        else layout.add(span, getButton());

        return layout;
    }

    private static @NotNull Button getButton() {
        Button button = new Button();
        button.setIcon(LineAwesomeIcon.EXCLAMATION_CIRCLE_SOLID.create());
        button.addClassNames(LumoUtility.IconSize.SMALL);
        button.addClickListener(event -> {
        });
        return button;
    }

    private TextArea createTextArea() {
        TextArea textArea = new TextArea();
        textArea.setValue(review.getContent());
        textArea.setHeightFull();
        textArea.setWidthFull();
        textArea.getStyle().setMarginTop("8px");
        textArea.setReadOnly(true);
        return textArea;
    }

    private Avatar createAvatar() {
        Avatar avatar = new Avatar(userDetailsFromReview.getAvatarUrl());
        avatar.addThemeVariants(AvatarVariant.LUMO_LARGE);
        if (userDetails != null) {
            avatar.setImageResource(ImageResourceUtils.getImageResource(userDetails.getAvatarUrl()));
        }
        return avatar;
    }

    private Layout createReviewReactions(Review review) {
        AtomicReference<Optional<Likes>> likeOfUser = new AtomicReference<>(likesRepo.findByUserDetailsAndReview(userDetails, review));
        List<Likes> allLikes = likesRepo.findAllByReview(review);
        AtomicReference<Integer> rate = new AtomicReference<>(0);
        allLikes.forEach(value -> rate.getAndUpdate(integer -> integer + value.getLikes().getAnInt()));
        boolean likeStateBoolean = likeOfUser.get().map(likes -> likes.getLikes() != LikeState.LIKE).orElse(true);
        LikeState likeState = likeOfUser.get().map(Likes::getLikes).orElseThrow();

        Button rateUp = new Button();
        Span counter = new Span(String.valueOf(rate.get()));
        Button rateDown = new Button();
        Layout counterIndicator = new Layout();
        Icon indicator = new Icon();
        if (!likeStateBoolean && likeState.equals(LikeState.LIKE)) {
            indicator.setIcon(VaadinIcon.ARROW_UP);
            indicator.setColor("green");
        } else if (likeStateBoolean && likeState.equals(LikeState.DISLIKE)) {
            indicator.setIcon(VaadinIcon.ARROW_DOWN);
            indicator.setColor("red");
        } else indicator.setVisible(false);

        counterIndicator.setFlexDirection(Layout.FlexDirection.ROW);
        counterIndicator.setAlignItems(Layout.AlignItems.CENTER);
        counterIndicator.add(counter, indicator);
        counterIndicator.setGap(Layout.Gap.SMALL);

        rateUp.setTooltipText("Этот коментарий полезен");
        rateUp.setIcon(LineAwesomeIcon.THUMBS_UP.create());
        rateUp.setEnabled(likeStateBoolean);
        rateUp.addClickListener(_ -> {
            counter.setText(String.valueOf(rate.updateAndGet(integer -> integer + 1)));
            likeOfUser.get().ifPresentOrElse(likes -> {
                if (likes.getLikes() == LikeState.NEUTRAL) {
                    likes.setLikes(LikeState.LIKE);
                    rateDown.setEnabled(true);
                    rateUp.setEnabled(false);
                    indicator.setVisible(true);
                    indicator.setIcon(VaadinIcon.ARROW_UP);
                    indicator.setColor("green");
                } else if (likes.getLikes() == LikeState.DISLIKE) {
                    likes.setLikes(LikeState.NEUTRAL);
                    indicator.setVisible(false);
                    rateDown.setEnabled(true);
                    rateUp.setEnabled(true);
                }
                likesRepo.save(likes);
            }, () -> {
                Likes newLike = new Likes();
                newLike.setReview(review);
                newLike.setUserDetails(userDetails);
                newLike.setLikes(LikeState.LIKE);
                rateDown.setEnabled(true);
                rateUp.setEnabled(false);
                likesRepo.save(newLike);
                likeOfUser.set(Optional.of(newLike));
                indicator.setVisible(true);
                indicator.setIcon(VaadinIcon.ARROW_UP);
                indicator.setColor("green");
            });
        });

        rateDown.setTooltipText("Этот коментарий не полезен");
        rateDown.setIcon(LineAwesomeIcon.THUMBS_DOWN.create());
        rateDown.setEnabled(!likeStateBoolean);
        rateDown.addClickListener(_ -> {
            counter.setText(String.valueOf(rate.updateAndGet(integer -> integer - 1)));
            likeOfUser.get().ifPresentOrElse(likes -> {
                if (likes.getLikes() == LikeState.NEUTRAL) {
                    likes.setLikes(LikeState.DISLIKE);
                    rateDown.setEnabled(false);
                    rateUp.setEnabled(true);
                    indicator.setVisible(true);
                    indicator.setIcon(VaadinIcon.ARROW_DOWN);
                    indicator.setColor("red");
                } else if (likes.getLikes() == LikeState.LIKE) {
                    likes.setLikes(LikeState.NEUTRAL);
                    rateDown.setEnabled(true);
                    rateUp.setEnabled(true);
                    indicator.setVisible(false);
                }
                likesRepo.save(likes);
            }, () -> {
                Likes newLike = new Likes();
                newLike.setReview(review);
                newLike.setUserDetails(userDetails);
                newLike.setLikes(LikeState.DISLIKE);
                rateDown.setEnabled(false);
                rateUp.setEnabled(true);
                likesRepo.save(newLike);
                likeOfUser.set(Optional.of(newLike));
                indicator.setVisible(true);
                indicator.setIcon(VaadinIcon.ARROW_DOWN);
                indicator.setColor("red");
            });
        });

        Layout reactionsLayout = new Layout();
        reactionsLayout.addClassNames(LumoUtility.Margin.Bottom.XSMALL, LumoUtility.Margin.Top.SMALL);
        reactionsLayout.setAlignItems(Layout.AlignItems.CENTER);
        reactionsLayout.setFlexDirection(Layout.FlexDirection.COLUMN);
        reactionsLayout.setGap(Layout.Gap.SMALL);
        reactionsLayout.add(rateUp, counterIndicator, rateDown);
        return reactionsLayout;
    }

    private Layout createStars() {
        int stars = review.getGrade();
        String count = String.valueOf(stars);

        Badge badge = new Badge();
        badge.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.Margin.Start.XSMALL);
        badge.addThemeVariants(BadgeVariant.CONTRAST, BadgeVariant.SMALL, BadgeVariant.PILL);
        badge.setText(count);

        Layout rating = new Layout(StarsUtils.getStars(stars));
        rating.addClassNames(LumoUtility.TextColor.PRIMARY);

        Layout reviewLayout = new Layout(rating, badge);
        reviewLayout.addClassNames(LumoUtility.Margin.Bottom.XSMALL, LumoUtility.Margin.Top.XSMALL);
        reviewLayout.setAlignItems(Layout.AlignItems.CENTER);
        reviewLayout.setGap(Layout.Gap.SMALL);

        return reviewLayout;
    }

}