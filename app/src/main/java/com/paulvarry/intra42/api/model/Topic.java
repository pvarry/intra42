package com.paulvarry.intra42.api.model;

import android.support.annotation.Nullable;

import com.paulvarry.intra42.ApiService;
import com.paulvarry.intra42.activity.TopicActivity;
import com.paulvarry.intra42.api.Messages;
import com.paulvarry.intra42.api.Topics;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;

public class Topic {

    public Topics topic;
    public List<Messages> messages;

    @Nullable
    static public Topic get(TopicActivity activity, ApiService service, int id) {
        Topic topic = new Topic();

        Call<Topics> callTopic = service.getTopic(id);
        Call<List<Messages>> callMessages = service.getTopicMessages(id);
        try {
            activity.setLoadingStatus("loading topic 1/2 ...");
            retrofit2.Response<Topics> retTopic = callTopic.execute();
            activity.setLoadingStatus("loading reply 2/2 ...");
            retrofit2.Response<List<Messages>> retMessages = callMessages.execute();
            activity.setLoadingStatus("finishing");
            if (!(retMessages.code() == 200 && retTopic.code() == 200))
                return null;
            topic.topic = retTopic.body();
            topic.messages = retMessages.body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return topic;
    }

    @Nullable
    static public Topic get(TopicActivity activity, ApiService service, Topics topics) {
        Topic topic = new Topic();

        Call<List<Messages>> callMessages = service.getTopicMessages(topics.id);
        try {
            activity.setLoadingStatus("loading reply 1/1 ...");
            retrofit2.Response<List<Messages>> retMessages = callMessages.execute();
            activity.setLoadingStatus("finishing");
            if (!(retMessages.code() == 200))
                return null;
            topic.topic = topics;
            topic.messages = retMessages.body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return topic;
    }
}
