package org.gdhote.gdhotecodegroup.pixcha.viewmodel;

import org.gdhote.gdhotecodegroup.pixcha.model.FeedPost;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserUploadsViewModel extends ViewModel {

    private MutableLiveData<List<FeedPost>> userFeedPost;
    private List<FeedPost> feedPostList;

    public void init() {
        userFeedPost = new MutableLiveData<>();
    }

    public MutableLiveData<List<FeedPost>> getUserFeedPost() {
        return userFeedPost;
    }

    public void setUserFeedPost(List<FeedPost> userFeedPost) {
        this.userFeedPost.setValue(userFeedPost);
        this.feedPostList = userFeedPost;
    }

    public List<FeedPost> getFeedPostList() {
        return feedPostList;
    }
}
