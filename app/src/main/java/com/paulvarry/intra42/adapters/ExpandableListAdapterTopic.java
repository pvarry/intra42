package com.paulvarry.intra42.adapters;

import android.content.Context;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.NewTopicActivity;
import com.paulvarry.intra42.activities.TopicActivity;
import com.paulvarry.intra42.activities.user.UserActivity;
import com.paulvarry.intra42.api.model.Messages;
import com.paulvarry.intra42.api.pack.Topic;
import com.paulvarry.intra42.bottomSheet.BottomSheetTopicInfoDialogFragment;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.BypassPicassoImageGetter;
import com.paulvarry.intra42.utils.DateTool;
import com.paulvarry.intra42.utils.Tag;
import com.paulvarry.intra42.utils.UserImage;
import com.plumillonforge.android.chipview.ChipView;
import com.squareup.picasso.Picasso;
import com.veinhorn.tagview.TagView;

import java.util.List;

import in.uncod.android.bypass.Bypass;

public class ExpandableListAdapterTopic extends BaseExpandableListAdapter {

    private final TopicActivity context;
    private List<Messages> messagesList;
    private Topic topic;

    public ExpandableListAdapterTopic(TopicActivity context, Topic topic) {

        this.context = context;
        this.topic = topic;
        this.messagesList = topic.messages;
    }


    /**
     * Gets the number of groups.
     *
     * @return the number of groups
     */
    @Override
    public int getGroupCount() {
        return messagesList.size();
    }

    /**
     * Gets the number of children in a specified group.
     *
     * @param groupPosition the position of the group for which the children
     *                      count should be returned
     * @return the children count in the specified group
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return messagesList.get(groupPosition).replies.size();
    }

    /**
     * Gets the data associated with the given group.
     *
     * @param groupPosition the position of the group
     * @return the data child for the specified group
     */
    @Override
    public Messages getGroup(int groupPosition) {
        return messagesList.get(groupPosition);
    }

    /**
     * Gets the data associated with the given child within the given group.
     *
     * @param groupPosition the position of the group that the child resides in
     * @param childPosition the position of the child with respect to other
     *                      children in the group
     * @return the data of the child
     */
    @Override
    public Messages getChild(int groupPosition, int childPosition) {
        return messagesList.get(groupPosition).replies.get(childPosition);
    }

    /**
     * Gets the ID for the group at the given position. This group ID must be
     * unique across groups. The combined ID (see
     * {@link #getCombinedGroupId(long)}) must be unique across ALL items
     * (groups and all children).
     *
     * @param groupPosition the position of the group for which the ID is wanted
     * @return the ID associated with the group
     */
    @Override
    public long getGroupId(int groupPosition) {
        return messagesList.get(groupPosition).id;
    }

    /**
     * Gets the ID for the given child within the given group. This ID must be
     * unique across all children within the group. The combined ID (see
     * {@link #getCombinedChildId(long, long)}) must be unique across ALL items
     * (groups and all children).
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child within the group for which
     *                      the ID is wanted
     * @return the ID associated with the child
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return getChild(groupPosition, childPosition).id;
    }

    /**
     * Indicates whether the child and group IDs are stable across changes to the
     * underlying data.
     *
     * @return whether or not the same ID always refers to the same object
     * //     * @see Adapter#hasStableIds()
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * Gets a View that displays the given group. This View is only for the
     * group--the Views for the group's children will be fetched using
     * {@link #getChildView(int, int, boolean, View, ViewGroup)}.
     *
     * @param groupPosition the position of the group for which the View is
     *                      returned
     * @param isExpanded    whether the group is expanded or collapsed
     * @param convertView   the old view to reuse, if possible. You should check
     *                      that this view is non-null and of an appropriate type before
     *                      using. If it is not possible to convert this view to display
     *                      the correct data, this method can create a new view. It is not
     *                      guaranteed that the convertView will have been previously
     *                      created.
     * @param parent        the parent that this view will eventually be attached to
     * @return the View corresponding to the group at the specified position
     */
    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final Messages message = getGroup(groupPosition);
        ViewHolderParent holder;

        if (convertView == null) {
            holder = new ViewHolderParent();

            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.expandable_list_view_topic_group, null);

            holder.imageViewProfile = convertView.findViewById(R.id.imageViewProfile);
            holder.textViewLogin = convertView.findViewById(R.id.textViewLogin);
            holder.textViewMessage = convertView.findViewById(R.id.textViewMessage);
            holder.textViewDate = convertView.findViewById(R.id.textViewDate);
            holder.tagView1 = convertView.findViewById(R.id.tagView1);
            holder.tagView2 = convertView.findViewById(R.id.tagView2);
            holder.viewUp = convertView.findViewById(R.id.viewUp);
            holder.viewDown = convertView.findViewById(R.id.viewDown);
            holder.imageButtonOption = convertView.findViewById(R.id.imageButtonOption);

            holder.linearLayoutInfoTopic = convertView.findViewById(R.id.linearLayoutInfoTopic);
            holder.textViewTitle = convertView.findViewById(R.id.textViewTitle);
            holder.chipViewTags = convertView.findViewById(R.id.chipViewTags);

            convertView.setTag(holder);
        } else
            holder = (ViewHolderParent) convertView.getTag();

        setHeader(holder, message);

        if (AppSettings.Advanced.getAllowMarkdownRenderer(context)) {
            Bypass bypass = new Bypass(context);
            CharSequence messageContent = bypass.markdownToSpannable(message.content, new BypassPicassoImageGetter(holder.textViewMessage, Picasso.with(context)));
            holder.textViewMessage.setText(messageContent);
            holder.textViewMessage.setMovementMethod(LinkMovementMethod.getInstance());
        } else
            holder.textViewMessage.setText(message.content);

        holder.textViewMessage.setTextIsSelectable(true);
        holder.textViewMessage.setFocusable(true);

        if (groupPosition == 0 &&
                topic.topic != null &&
                topic.topic.message != null && topic.topic.message.id == message.id) {
            holder.linearLayoutInfoTopic.setVisibility(View.VISIBLE);
            holder.textViewTitle.setText(topic.topic.name);

            Tag.setTagForum(context, topic.topic.tags, holder.chipViewTags);

        } else
            holder.linearLayoutInfoTopic.setVisibility(View.GONE);

        holder.imageButtonOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AppClass app = context.app;

                if (groupPosition == 0 && app != null && app.me != null && app.me.equals(message.author)) {
                    PopupMenu popup = new PopupMenu(context, view);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.menu_forum_message_item, popup.getMenu());
                    popup.show();

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {

                            switch (menuItem.getItemId()) {
                                case R.id.info:
                                    openBottomSheet(message);
                                    return true;
                                case R.id.edit:
                                    NewTopicActivity.openIt(context, topic.topic);
                                    return true;
                                case R.id.delete:

                                    return true;
                            }

                            return false;
                        }
                    });
                } else
                    openBottomSheet(message);

            }
        });

        holder.imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserActivity.openIt(context, message.author);
            }
        });

        return convertView;
    }

    /**
     * Gets a View that displays the data for the given child within the given
     * group.
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child (for which the View is
     *                      returned) within the group
     * @param isLastChild   Whether the child is the last child within the group
     * @param convertView   the old view to reuse, if possible. You should check
     *                      that this view is non-null and of an appropriate type before
     *                      using. If it is not possible to convert this view to display
     *                      the correct data, this method can create a new view. It is not
     *                      guaranteed that the convertView will have been previously
     *                      created.
     * @param parent        the parent that this view will eventually be attached to
     * @return the View corresponding to the child at the specified position
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Messages message = getChild(groupPosition, childPosition);
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.expandable_list_view_topic_item, null);

            holder.imageViewProfile = convertView.findViewById(R.id.imageViewProfile);
            holder.textViewLogin = convertView.findViewById(R.id.textViewLogin);
            holder.textViewMessage = convertView.findViewById(R.id.textViewMessage);
            holder.textViewDate = convertView.findViewById(R.id.textViewDate);
            holder.tagView1 = convertView.findViewById(R.id.tagView1);
            holder.tagView2 = convertView.findViewById(R.id.tagView2);
            holder.viewUp = convertView.findViewById(R.id.viewUp);
            holder.viewDown = convertView.findViewById(R.id.viewDown);
            holder.imageButtonOption = convertView.findViewById(R.id.imageButtonOption);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        setHeader(holder, message);

        if (AppSettings.Advanced.getAllowMarkdownRenderer(context)) {
            Bypass bypass = new Bypass(context);
            CharSequence messageContent = bypass.markdownToSpannable(message.content);
            holder.textViewMessage.setText(messageContent);
            holder.textViewMessage.setMovementMethod(LinkMovementMethod.getInstance());
        } else
            holder.textViewMessage.setText(message.content);

        holder.textViewMessage.setTextIsSelectable(true);
        holder.textViewMessage.setFocusable(true);
        holder.textViewMessage.setFocusableInTouchMode(false);
        holder.textViewMessage.setClickable(false);
        holder.textViewMessage.setLongClickable(false);

        holder.imageButtonOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottomSheet(message);
            }
        });

        holder.imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserActivity.openIt(context, message.author);
            }
        });

        return convertView;
    }

    private void setHeader(ViewHolder holder, Messages message) {
        UserImage.setImage(context, message.author, holder.imageViewProfile);
        holder.textViewLogin.setText(message.author.login);
        holder.textViewDate.setText(DateTool.getDurationAgo(message.createdAt));

        if (message.votesCount.upvote < 10)
            holder.viewUp.setVisibility(View.GONE);
        else if (message.votesCount.upvote < 25)
            holder.viewUp.setBackgroundColor(ContextCompat.getColor(context, R.color.topic_message_up_0));
        else if (message.votesCount.upvote < 50)
            holder.viewUp.setBackgroundColor(ContextCompat.getColor(context, R.color.topic_message_up_1));
        else
            holder.viewUp.setBackgroundColor(ContextCompat.getColor(context, R.color.topic_message_up_2));

        if (message.votesCount.downvote < 10)
            holder.viewDown.setVisibility(View.GONE);
        else if (message.votesCount.downvote < 25)
            holder.viewDown.setBackgroundColor(ContextCompat.getColor(context, R.color.topic_message_down_0));
        else if (message.votesCount.downvote < 50)
            holder.viewDown.setBackgroundColor(ContextCompat.getColor(context, R.color.topic_message_down_1));
        else
            holder.viewDown.setBackgroundColor(ContextCompat.getColor(context, R.color.topic_message_down_2));
    }

    private void openBottomSheet(Messages message) {
        BottomSheetDialogFragment bottomSheetDialogFragment = BottomSheetTopicInfoDialogFragment.newInstance(message);
        bottomSheetDialogFragment.show(context.getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
    }

    /**
     * Whether the child at the specified position is selectable.
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child within the group
     * @return whether the child is selectable.
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private static class ViewHolder {

        protected ImageView imageViewProfile;
        protected TextView textViewLogin;
        protected TextView textViewMessage;
        protected TextView textViewDate;
        protected TagView tagView1;
        protected TagView tagView2;
        protected View viewUp;
        protected View viewDown;
        protected ImageButton imageButtonOption;

    }

    private static class ViewHolderParent extends ViewHolder {
        protected LinearLayout linearLayoutInfoTopic;
        protected TextView textViewTitle;
        protected ChipView chipViewTags;
    }
}
