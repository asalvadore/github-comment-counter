package com.tzachz.commentcounter.server;

import com.google.common.collect.Lists;
import com.tzachz.commentcounter.GHCommentBuilder;
import com.tzachz.commentcounter.Commenter;
import com.tzachz.commentcounter.apifacade.jsonobjects.GHRepo;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: tzachz
 * Date: 17/08/13
 * Time: 18:36
 */
public class LeaderBoardViewTest {

    private GHCommentBuilder commentBuilder = new GHCommentBuilder();

    private GHRepo repo = new GHRepo("my-repo");

    @Test
    public void singleCommentAlwaysChosen() throws Exception {
        Commenter commenter = new Commenter("user1");
        commenter.addComment(commentBuilder.createComment("user1", "some-url", "body"), repo);
        LeaderBoardView view = new LeaderBoardView(Lists.newArrayList(commenter), "org1", true, "today");
        assertThat(view.getRecords().get(0).getSampleComment(), equalTo("body"));
    }

    @Test
    public void fewCommentsRandomChosen() throws Exception {
        Commenter commenter = new Commenter("user1");
        commenter.addComment(commentBuilder.createComment("user1", "some-url", "body1"), repo);
        commenter.addComment(commentBuilder.createComment("user1", "some-url", "body2"), repo);
        commenter.addComment(commentBuilder.createComment("user1", "some-url", "body3"), repo);
        int body1Chosen = 0;
        for (int i = 0; i < 100; i++) {
            LeaderBoardView view = new LeaderBoardView(Lists.newArrayList(commenter), "org1", true, "today");
            body1Chosen += view.getRecords().get(0).getSampleComment().equals("body1") ? 1 : 0;
        }
        assertThat(body1Chosen, is(both(greaterThan(10)).and(lessThan(60))));
    }

    @Test
    public void commentersSortedByScore() throws Exception {
        Commenter commenterScore6 = new Commenter("user1");
        commenterScore6.addComment(commentBuilder.createComment("user1", "url"), repo);
        commenterScore6.addComment(commentBuilder.createComment("user1", "url"), new GHRepo("anotherRepo"));
        Commenter commenterScore5 = new Commenter("user2");
        for (int i = 0; i < 5; i++) {
            commenterScore5.addComment(commentBuilder.createComment("user2", "url"), repo);
        }
        LeaderBoardView view = new LeaderBoardView(Lists.newArrayList(commenterScore6, commenterScore5), "org1", true, "today");
        assertThat(view.getRecords().get(0).getUsername(), is("user1"));
    }
}