package com.paulvarry.intra42;

import com.paulvarry.intra42.api.AccessToken;
import com.paulvarry.intra42.api.Announcements;
import com.paulvarry.intra42.api.Campus;
import com.paulvarry.intra42.api.Cursus;
import com.paulvarry.intra42.api.Events;
import com.paulvarry.intra42.api.EventsUsers;
import com.paulvarry.intra42.api.ExpertisesUsers;
import com.paulvarry.intra42.api.Locations;
import com.paulvarry.intra42.api.Messages;
import com.paulvarry.intra42.api.Notions;
import com.paulvarry.intra42.api.Projects;
import com.paulvarry.intra42.api.ProjectsUsers;
import com.paulvarry.intra42.api.ScaleTeams;
import com.paulvarry.intra42.api.Slots;
import com.paulvarry.intra42.api.Subnotions;
import com.paulvarry.intra42.api.Tags;
import com.paulvarry.intra42.api.Teams;
import com.paulvarry.intra42.api.Topics;
import com.paulvarry.intra42.api.User;
import com.paulvarry.intra42.api.UserLTE;
import com.paulvarry.intra42.api.Votes;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiService {

    @FormUrlEncoded
    @POST("/oauth/token")
    Call<AccessToken> getNewAccessToken(
            @Field("code") String code,
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("redirect_uri") String redirectUri,
            @Field("grant_type") String grantType);

    @FormUrlEncoded
    @POST("/oauth/token")
    Call<AccessToken> getRefreshAccessToken(
            @Field("refresh_token") String refreshToken,
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("redirect_uri") String redirectUri,
            @Field("grant_type") String grantType);


    @GET("/v2/notions")
    Call<List<Notions>> getNotions(@Query("page") Integer page);

    @GET("/v2/tags/{tag_id}/notions?sort=name")
    Call<List<Notions>> getNotionsTag(@Path("tag_id") int tagId, @Query("page") Integer page);

    @GET("/v2/cursus/{cursus_id}/notions?sort=name")
    Call<List<Notions>> getNotionsCursus(@Path("cursus_id") int cursusId, @Query("page") Integer page);

    /* Subnotions */
    @GET("/v2/notions/{id}/subnotions")
    Call<List<Subnotions>> getSubnotions(@Path("id") int notionId, @Query("page") Integer page);

    @GET("/v2/notions/{slug}/subnotions")
    Call<List<Subnotions>> getSubnotions(@Path("slug") String notionSlug, @Query("page") Integer page);

    /* Topics */
    @GET("v2/topics?sort=-write_at")
    Call<List<Topics>> getTopics(@Query("page") Integer page);

    @GET("v2/topics?sort=-write_at")
    Call<List<Topics>> getTopicsSearch(@Query("search[name]") String name);

    @GET("v2/cursus/{cursus_id}/topics?sort=-write_at")
    Call<List<Topics>> getTopicsSearch(@Path("cursus_id") int cursus, @Query("search[name]") String name);

    @GET("v2/topics/unread?sort=-write_at")
    Call<List<Topics>> getTopicsUnread(@Query("page") Integer page);

    @GET("/v2/tags/{tag_id}/topics?sort=-write_at")
    Call<List<Topics>> getTopicsTag(@Path("tag_id") int tagsId, @Query("page") int page);

    @GET("/v2/topics/{id}")
    Call<Topics> getTopic(@Path("id") int id);

    @GET("/v2/topics/{id}/messages")
    Call<List<Messages>> getTopicMessages(@Path("id") int id);

    @GET("/v2/me/topics/{id}/votes")
    Call<List<Votes>> getTopicVotesMe(@Path("id") int id);

    @POST("/v2/topics/{id}/messages")
    Call<Messages> createTopicReply(@Path("id") int topicId, @Query("message[author_id]") int authorId, @Query("message[content]") String content);

    @POST("/v2/topics")
    Call<Topics> createTopic(@Query("topic[name]") String name,
                             @Query("topic[kind]") String kind,
                             @Query("topic[language_id]") int languageId,
                             @Query("topic[messages_attributes][content]") String content,
                             @Query("topic[tag_ids]") String tagsIds,
                             @Query("topic[cursus_ids]") String cursusIds);

    @PUT("/v2/topics/{id}")
    Call<Topics> updateTopic(@Path("id") int topicId,
                             @Query("topic[name]") String name,
                             @Query("topic[kind]") String kind,
                             @Query("topic[language_id]") int languageId,
                             @Query("topic[messages_attributes][content]") String content,
                             @Query("topic[tag_ids]") String tagsIds,
                             @Query("topic[cursus_ids]") String cursusIds);

    /* Users */
    @GET("/v2/users?sort=login")
    Call<List<UserLTE>> getUsers(@Query("page") int page);

    @GET("/v2/campus/{campus_id}/users?sort=login")
    Call<List<UserLTE>> getUsersCampus(@Path("campus_id") int campus, @Query("page") int page);

    @GET("/v2/users/{id}")
    Call<User> getUser(@Path("id") int id);

    @GET("/v2/users/{slug}")
    Call<User> getUser(@Path("slug") String slug);

    @GET("/v2/users")
    Call<List<UserLTE>> getUsersSearch(@Query("search[login]") String slug);

    @GET("/v2/me")
    Call<User> getUserMe();

    @GET("/v2/users/{id}/topics")
    Call<List<Topics>> getUserTopics(@Path("id") int id, @Query("page") int page);

    @GET("/v2/users/{slug}/topics")
    Call<List<Topics>> getUserTopics(@Path("slug") String slug, @Query("page") int page);

    @GET("/v2/users/{id}/expertises_users?sort=-value")
    Call<List<ExpertisesUsers>> getUserExpertises(@Path("id") int userId, @Query("page") int page);

    @GET("/v2/users/{id}/expertises_users?sort=-value")
    Call<List<ExpertisesUsers>> getUserExpertises(@Path("id") String userSlug, @Query("page") int page);

    /* Events */
    @GET("/v2/campus/{campus_id}/cursus/{cursus_id}/events?sort=begin_at")
    Call<List<Events>> getEvent(@Path("campus_id") int campus, @Path("cursus_id") int cursus, @Query("range[end_at]") String rangeEnd, @Query("page") int page);

    @GET("/v2/events")
    Call<List<Events>> getEvent(@Query("range[end_at]") String rangeEnd, @Query("page") int page);

    @GET("/v2/cursus/{cursus_id}/events")
    Call<List<Events>> getEventCursus(@Path("cursus_id") int cursus, @Query("range[end_at]") String rangeEnd, @Query("page") int page);

    @GET("/v2/campus/{campus_id}/events")
    Call<List<Events>> getEventCampus(@Path("campus_id") int campus, @Query("range[end_at]") String rangeEnd, @Query("page") int page);

    @GET("/v2/events")
    Call<List<Events>> getEventCreatedAt(@Query("range[created_at]") String rangeCreated, @Query("page") int page);

    @GET("/v2/campus/{campus_id}/cursus/{cursus_id}/events")
    Call<List<Events>> getEventCreatedAt(@Path("campus_id") int campus, @Path("cursus_id") int cursus, @Query("range[created_at]") String rangeCreated, @Query("page") int page);

    @GET("/v2/cursus/{cursus_id}/events")
    Call<List<Events>> getEventCreatedAtCursus(@Path("cursus_id") int cursus, @Query("range[created_at]") String rangeCreated, @Query("page") int page);

    @GET("/v2/campus/{campus_id}/events")
    Call<List<Events>> getEventCreatedAtCampus(@Path("campus_id") int campus, @Query("range[created_at]") String rangeCreated, @Query("page") int page);

    /* Event User */
    @GET("/v2/events_users")
    Call<List<EventsUsers>> getEventsUsers(@Query("filter[user_id]") int user, @Query("filter[event_id]") int event);

    @POST("/v2/events_users")
    Call<List<EventsUsers>> createEventsUsers(@Query("events_user[event_id]") int eventId, @Query("events_user[user_id]") int userId);

    @DELETE("/v2/events_users/{id}")
    Call<List<EventsUsers>> deleteEventsUsers(@Path("id") int eventUser);

    /* Scale Teams */
    @GET("/v2/me/scale_teams?sort=begin_at")
    Call<List<ScaleTeams>> getScaleTeamsMe(@Query("page") int page);

    @GET("/v2/me/scale_teams?sort=begin_at")
    Call<List<ScaleTeams>> getScaleTeamsMe(@Query("range[created_at]") String rangeCreated, @Query("page") int page);

    @GET("/v2/me/scale_teams?sort=begin_at")
    Call<List<ScaleTeams>> getScaleTeamsMeBegin(@Query("range[begin_at]") String rangeCreated, @Query("page") int page);

    /* Projects */
    @GET("/v2/projects?sort=name")
    Call<List<Projects>> getProjects();

    @GET("/v2/projects?sort=name")
    Call<List<Projects>> getProjects(@Query("page") int page);

    @GET("/v2/projects?sort=name")
    Call<List<Projects>> getProjectsSearch(@Query("search[name]") String slug);

    @GET("v2/cursus/{cursus_id}/projects?sort=name")
    Call<List<Projects>> getProjectsSearch(@Path("cursus_id") int cursus, @Query("search[name]") String slug);

    @GET("/v2/projects/{project_id}")
    Call<Projects> getProject(@Path("project_id") int projectId);

    @GET("/v2/projects/{project_id}")
    Call<Projects> getProject(@Path("project_id") String projectSlug);

    @GET("/v2/projects/{project_id}/users?sort=login")
    Call<List<UserLTE>> getProjectUsers(@Path("project_id") int projectId, @Query("page") int page);

    @GET("/v2/projects/{project_id}/users?sort=login")
    Call<List<UserLTE>> getProjectUsers(@Path("project_id") String projectSlug, @Query("page") int page);

    @POST("/v2/projects/{project_id}/register")
    Call<Projects> createProjectRegister(@Path("project_id") int projectId);

    /* Projects Users */
    @GET("/v2/projects_users/{id}")
    Call<ProjectsUsers> getProjectsUsers(@Path("id") int projectUser);

    @GET("/v2/projects_users?page[size]=1")
    Call<List<ProjectsUsers>> getProjectsUsers(@Query("filter[project_id]") int projectId, @Query("filter[user_id]") int userId);

    @GET("/v2/projects/{project_id}/projects_users?page[size]=1")
    Call<List<ProjectsUsers>> getProjectsUsers(@Path("project_id") String projectSlug, @Query("filter[user_id]") int userId);

    @GET("/v2/projects/{project_id}/projects_users?page[size]=1")
    Call<List<ProjectsUsers>> getProjectsUsers(@Path("project_id") String projectSlug, @Query("filter[user_id]") String login);

    @GET("/v2/projects/{project_id}/projects_users?page[size]=1")
    Call<List<ProjectsUsers>> getProjectsUsers(@Path("project_id") int project_id, @Query("filter[user_id]") String login);

    /* Teams */
    @GET("/v2/users/{user_id}/projects/{project_id}/teams?sort=-created_at")
    Call<List<Teams>> getTeams(@Path("user_id") int userId, @Path("project_id") int projectId, @Query("page") int page);

    @GET("/v2/users/{user_id}/projects/{project_id}/teams?sort=-created_at")
    Call<List<Teams>> getTeams(@Path("user_id") int userId, @Path("project_id") String projectSlug, @Query("page") int page);

    @GET("/v2/users/{login}/projects/{project_slug}/teams?sort=-created_at")
    Call<List<Teams>> getTeams(@Path("login") String login, @Path("project_slug") String projectSlug, @Query("page") int page);

    @GET("/v2/users/{login}/projects/{project_id}/teams?sort=-created_at")
    Call<List<Teams>> getTeams(@Path("login") String login, @Path("project_id") int projectId, @Query("page") int page);

    /* Slots */
    @GET("/v2/me/slots?sort=begin_at&filter[future]=true")
    Call<List<Slots>> getSlotsMe(@Query("page") int page);

    @POST("/v2/slots")
    Call<List<Slots>> createSlot(@Query("slot[user_id]") int userId, @Query("slot[begin_at]") String beginAt, @Query("slot[end_at]") String endAt);

    @DELETE("/v2/slots/{id}")
    Call<Slots> destroySlot(@Path("id") int id);

    /* Votes */
    @POST("/v2/votes")
    Call<Votes> createVote(@Query("vote[user_id]") int userId, @Query("vote[message_id]") int messageId, @Query("vote[kind]") String kind);

    @DELETE("/v2/votes/{id}")
    Call<Votes> destroyVote(@Path("id") int id);

    /* Messages */
    @DELETE("/v2/messages/{id}")
    Call<Messages> destroyMessage(@Path("id") int id);

    @POST("/v2/messages/{id}/messages")
    Call<Messages> createMessageReply(@Path("id") int id, @Query("message[author_id]") int authorId, @Query("message[content]") String content);

    @PUT("/v2/messages/{id}")
    Call<Messages> updateMessage(@Path("id") int id, @Query("message[content]") String content);

    /* Cursus */
    @GET("/v2/cursus?sort=name")
    Call<List<Cursus>> getCursus();

    @GET("/v2/cursus?sort=name")
    Call<List<Cursus>> getCursus(@Query("page[size]") int pageSize, @Query("page[number]") int pageNumber);

    /* Cursus */
    @GET("/v2/campus?sort=name")
    Call<List<Campus>> getCampus();

    @GET("/v2/campus?sort=name")
    Call<List<Campus>> getCampus(@Query("page[size]") int pageSize, @Query("page[number]") int pageNumber);

    /* Tags */
    @GET("/v2/tags")
    Call<List<Tags>> getTags(@Query("page[size]") int pageSize, @Query("page[number]") int pageNumber);

    @GET("/v2/tags/{id}")
    Call<Tags> getTag(@Path("id") int id);

    /* Locations */
    @GET("/v2/campus/{campus_id}/locations?filter[active]=true")
    Call<List<Locations>> getLocations(@Path("campus_id") int campus, @Query("page[size]") int pageSize, @Query("page[number]") int pageNumber);

    @GET("/v2/users/{id_user}/locations?sort=-end_at")
    Call<List<Locations>> getLastLocations(@Path("id_user") String user);

    @GET("/v2/locations?sort=-begin_at&page[size]=5")
    Call<List<Locations>> getLocationsHost(@Query("filter[host]") String host);

    /* Announcements */
    @GET("/v2/announcements?sort=begin_at")
    Call<List<Announcements>> getAnnouncements(@Query("range[created_at]") String rangeCreated, @Query("page") int page);

    /* Other */

    @GET
    Call<ResponseBody> getOther(@Url String path);
}