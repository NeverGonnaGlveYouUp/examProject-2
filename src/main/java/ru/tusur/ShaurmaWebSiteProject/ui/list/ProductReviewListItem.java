package ru.tusur.ShaurmaWebSiteProject.ui.list;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIcon;
import ru.tusur.ShaurmaWebSiteProject.backend.model.LikeState;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Likes;
import ru.tusur.ShaurmaWebSiteProject.backend.model.Review;
import ru.tusur.ShaurmaWebSiteProject.backend.model.UserDetails;
import ru.tusur.ShaurmaWebSiteProject.backend.repo.LikesRepo;
import ru.tusur.ShaurmaWebSiteProject.ui.components.Layout;
import ru.tusur.ShaurmaWebSiteProject.ui.utils.ImageResourceUtils;

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

        Layout secondary = new Layout();
        secondary.setFlexDirection(Layout.FlexDirection.ROW);
        secondary.addClassNames(LumoUtility.Margin.Bottom.XSMALL, LumoUtility.Margin.Top.SMALL);
        secondary.setAlignItems(Layout.AlignItems.CENTER);
        secondary.setGap(Layout.Gap.SMALL);
        if (userDetails == null) {
            secondary.add(createTextArea());
        } else {
            secondary.add(createReviewReactions(review), createTextArea());
        }

        Layout mainLayout = new Layout();
        mainLayout.setAlignItems(Layout.AlignItems.START);
        mainLayout.setFlexDirection(Layout.FlexDirection.COLUMN);
        mainLayout.add(new HorizontalLayout(createAvatar(), userNameStars), secondary);

        this.add(mainLayout);
    }

    private TextArea createTextArea() {
        TextArea textArea = new TextArea();
        textArea.setValue(review.getContent());
        textArea.setHeightFull();
        textArea.setWidthFull();
        textArea.getStyle().setMarginTop(LumoUtility.Margin.Top.SMALL);
        textArea.setReadOnly(true);

        return textArea;
    }

    private Avatar createAvatar() {
        Avatar avatar = new Avatar(userDetailsFromReview.getAvatarUrl());
        avatar.addThemeVariants(AvatarVariant.LUMO_SMALL);
        if (userDetails != null) {
            avatar.setImageResource(ImageResourceUtils.getImageResource(userDetails.getAvatarUrl()));
        }
        return avatar;
    }

    private Layout createReviewReactions(Review review) {

        AtomicReference<Optional<Likes>> likeOfUser = new AtomicReference<>(likesRepo.findByUserDetailsAndReview(userDetails, review));
        List<Likes> allLikes = likesRepo.findAllByReview(review);
        AtomicReference<Integer> rate = new AtomicReference<>(0);
        allLikes.forEach(value -> rate.getAndUpdate(integer -> integer+value.getLikes().getAnInt()));

        Button rateUp = new Button();
        Span counter = new Span(String.valueOf(rate.get()));
        Button rateDown = new Button();


        rateUp.setIcon(LineAwesomeIcon.CARET_UP_SOLID.create());
        rateUp.setEnabled((likeOfUser.get().map(likes -> likes.getLikes() != LikeState.LIKE).orElse(true)));
        rateUp.addClickListener(_ -> {
            counter.setText(String.valueOf(rate.updateAndGet(integer -> integer + 1)));
            likeOfUser.get().ifPresentOrElse(likes -> {
                if(likes.getLikes() == LikeState.NEUTRAL){
                    likes.setLikes(LikeState.LIKE);
                    rateDown.setEnabled(true);
                    rateUp.setEnabled(false);
                } else if (likes.getLikes() == LikeState.DISLIKE){
                    likes.setLikes(LikeState.NEUTRAL);
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
            });
        });

        rateDown.setIcon(LineAwesomeIcon.CARET_DOWN_SOLID.create());
        rateDown.setEnabled((likeOfUser.get().map(likes -> likes.getLikes() != LikeState.DISLIKE).orElse(true)));
        rateDown.addClickListener(_ -> {
            counter.setText(String.valueOf(rate.updateAndGet(integer -> integer - 1)));
            likeOfUser.get().ifPresentOrElse(likes -> {
                if(likes.getLikes() == LikeState.NEUTRAL){
                    likes.setLikes(LikeState.DISLIKE);
                    rateDown.setEnabled(false);
                    rateUp.setEnabled(true);
                } else if (likes.getLikes() == LikeState.LIKE){
                    likes.setLikes(LikeState.NEUTRAL);
                    rateDown.setEnabled(true);
                    rateUp.setEnabled(true);
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
            });
        });


        Layout reactionsLayout = new Layout();
        reactionsLayout.addClassNames(LumoUtility.Margin.Bottom.XSMALL, LumoUtility.Margin.Top.SMALL);
        reactionsLayout.setAlignItems(Layout.AlignItems.CENTER);
        reactionsLayout.setFlexDirection(Layout.FlexDirection.COLUMN);
        reactionsLayout.setGap(Layout.Gap.SMALL);
        reactionsLayout.add(rateUp, counter, rateDown);
        return reactionsLayout;
    }

    private Layout createStars() {
        int stars = review.getGrade();
        String count = String.valueOf(stars);

        Span starsText = new Span(count + " звезд");
        starsText.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.Margin.Start.XSMALL);

        Layout rating = new Layout(getStars(stars));
        rating.addClassNames(LumoUtility.TextColor.PRIMARY);

        Layout reviewLayout = new Layout(rating, starsText);
        reviewLayout.addClassNames(LumoUtility.Margin.Bottom.XSMALL, LumoUtility.Margin.Top.XSMALL);
        reviewLayout.setAlignItems(Layout.AlignItems.CENTER);
        reviewLayout.setGap(Layout.Gap.SMALL);

        return reviewLayout;
    }

    private Div getStars(int stars) {
        Div div = new Div();
        for (int i = 0; i < 5; i++) {
            if (i < stars) {
                div.add(createStar());
            } else if (stars - i < 1 && stars - i > 0) {
                div.add(createHalfStar());
            }
        }
        return div;
    }


    private Component createStar() {
        SvgIcon star = LineAwesomeIcon.STAR_SOLID.create();
        star.addClassNames(LumoUtility.IconSize.SMALL);
        return star;
    }

    private Component createHalfStar() {
        SvgIcon star = LineAwesomeIcon.STAR_HALF_SOLID.create();
        star.addClassNames(LumoUtility.IconSize.SMALL);
        return star;
    }

}

