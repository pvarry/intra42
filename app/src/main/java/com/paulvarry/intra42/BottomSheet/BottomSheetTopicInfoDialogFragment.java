package com.paulvarry.intra42.BottomSheet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.goebl.david.Response;
import com.google.gson.Gson;
import com.paulvarry.intra42.ApiService;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.Messages;
import com.paulvarry.intra42.api.Votes;
import com.paulvarry.intra42.oauth.ServiceGenerator;

import java.text.DateFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class BottomSheetTopicInfoDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private static String INTENT_JSON = "json";
    private static String INTENT_UP = "up_vote";
    private static String INTENT_DOWN = "down_vote";
    private static String INTENT_TROLL = "troll_vote";
    private static String INTENT_REPORT = "report_vote";
    private AppClass app;
    private Messages message;

    private TextView textViewUp;
    private TextView textViewDown;
    private TextView textViewReport;
    private TextView textViewTroll;
    private ImageButton imageButtonUp;
    private ImageButton imageButtonDown;
    private ImageButton imageButtonReport;
    private ImageButton imageButtonTroll;
    private ProgressBar progressBarUp;
    private ProgressBar progressBarDown;
    private ProgressBar progressBarReport;
    private ProgressBar progressBarTroll;
    private TextView massageWithoutRendering;

    private Button buttonReply;
    private Button buttonShare;
    private Button buttonEdit;
    private Button buttonDelete;

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    public static BottomSheetTopicInfoDialogFragment newInstance(Messages message) {
        BottomSheetTopicInfoDialogFragment fragment = new BottomSheetTopicInfoDialogFragment();
        Bundle args = new Bundle();
        args.putString(INTENT_JSON, message.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Gson gson = ServiceGenerator.getGson();
            message = gson.fromJson(getArguments().getString(INTENT_JSON), Messages.class);
        }

        app = (AppClass) getActivity().getApplication();
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sheet_topic_info, null);
        dialog.setContentView(contentView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        TextView textViewMessage = (TextView) contentView.findViewById(R.id.textViewMessage);

        textViewUp = (TextView) contentView.findViewById(R.id.textViewUp);
        textViewDown = (TextView) contentView.findViewById(R.id.textViewDown);
        textViewReport = (TextView) contentView.findViewById(R.id.textViewReport);
        textViewTroll = (TextView) contentView.findViewById(R.id.textViewTroll);
        imageButtonUp = (ImageButton) contentView.findViewById(R.id.imageButtonUp);
        imageButtonDown = (ImageButton) contentView.findViewById(R.id.imageButtonDown);
        imageButtonReport = (ImageButton) contentView.findViewById(R.id.imageButtonReport);
        imageButtonTroll = (ImageButton) contentView.findViewById(R.id.imageButtonTroll);
        progressBarUp = (ProgressBar) contentView.findViewById(R.id.progressBarUp);
        progressBarDown = (ProgressBar) contentView.findViewById(R.id.progressBarDown);
        progressBarReport = (ProgressBar) contentView.findViewById(R.id.progressBarReport);
        progressBarTroll = (ProgressBar) contentView.findViewById(R.id.progressBarTroll);

        buttonReply = (Button) contentView.findViewById(R.id.buttonReply);
        buttonShare = (Button) contentView.findViewById(R.id.buttonShare);
        buttonEdit = (Button) contentView.findViewById(R.id.buttonEdit);
        buttonDelete = (Button) contentView.findViewById(R.id.buttonDelete);
        View viewEdit = contentView.findViewById(R.id.viewEdit);
        View viewDelete = contentView.findViewById(R.id.viewDelete);

        TextView textViewCreated = (TextView) contentView.findViewById(R.id.textViewCreated);
        TextView textViewUpdated = (TextView) contentView.findViewById(R.id.textViewUpdated);
        TextView textViewView = (TextView) contentView.findViewById(R.id.textViewView);

        massageWithoutRendering = (TextView) contentView.findViewById(R.id.massageWithoutRendering);

        textViewMessage.setText(message.content.replace('\n', ' '));
        textViewUp.setText(String.valueOf(message.votesCount.upvote));
        textViewDown.setText(String.valueOf(message.votesCount.downvote));
        textViewReport.setText(String.valueOf(message.votesCount.problem));
        textViewTroll.setText(String.valueOf(message.votesCount.trollvote));

        if (message.userVotes.upvote != null)
            imageButtonUp.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorTintCheck));
        if (message.userVotes.downvote != null)
            imageButtonDown.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorTintCross));
        if (message.userVotes.trollvote != null)
            imageButtonTroll.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorTintCross));
        if (message.userVotes.problem != null)
            imageButtonReport.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorTintCross));
        imageButtonUp.setOnClickListener(this);
        imageButtonDown.setOnClickListener(this);
        imageButtonTroll.setOnClickListener(this);
        imageButtonReport.setOnClickListener(this);

        buttonReply.setOnClickListener(this);
        buttonShare.setOnClickListener(this);

        if (app != null && app.me != null && app.me.equals(message.author)) {
            buttonEdit.setOnClickListener(this);
            buttonDelete.setOnClickListener(this);
        } else {
            buttonEdit.setVisibility(View.GONE);
            viewEdit.setVisibility(View.GONE);
            buttonDelete.setVisibility(View.GONE);
            viewDelete.setVisibility(View.GONE);
        }

        DateFormat timeFormatter =
                DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.LONG, Locale.getDefault());

        textViewCreated.setText(timeFormatter.format(message.createdAt));
        textViewUpdated.setText(timeFormatter.format(message.updatedAt));
        textViewView.setText(String.valueOf(message.readings));

        massageWithoutRendering.setText(message.content);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == imageButtonUp)
            vote(progressBarUp, imageButtonUp, message.userVotes.upvote, Votes.KIND_UP);
        else if (v == imageButtonDown)
            vote(progressBarDown, imageButtonDown, message.userVotes.downvote, Votes.KIND_DOWN);
        else if (v == imageButtonTroll)
            vote(progressBarTroll, imageButtonTroll, message.userVotes.trollvote, Votes.KIND_TROLL);
        else if (v == imageButtonReport)
            vote(progressBarReport, imageButtonReport, message.userVotes.problem, Votes.KIND_PROBLEM);
        else if (v == buttonShare) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, message.content);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } else if (v == buttonEdit)
            onEdit(message.content);
        else if (v == buttonReply)
            onReply();
        else if (v == buttonDelete)
            onDelete();
    }

    private void vote(final ProgressBar progressBar, final ImageButton imageButton, Integer vote, String kind) { // update this
        progressBar.setVisibility(View.VISIBLE);
        imageButton.setVisibility(View.GONE);
        ApiService api = app.getApiService();
        Call<Votes> call;
        if (vote != null)
            call = api.destroyVote(vote);
        else
            call = api.createVote(app.me.id, message.id, kind);
        call.enqueue(new Callback<Votes>() {
            @Override
            public void onResponse(Call<Votes> call, retrofit2.Response<Votes> response) {
                if (response.isSuccessful())
                    Toast.makeText(getContext(), "Success\nDon't forget to refresh", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), "Error: " + response.message() + "\nDon't forget to refresh", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                imageButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<Votes> call, Throwable t) {
                Toast.makeText(getContext(), "Failed: " + t.getMessage() + "\nDon't forget to refresh", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                imageButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void onDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.delete);
        builder.setMessage(R.string.are_you_sure_to_delete_message);
        builder.setCancelable(true);

        // Set up the buttons
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ApiService api = app.getApiService();
                Call<Messages> call = api.destroyMessage(message.id);
                call.enqueue(new Callback<Messages>() {
                    @Override
                    public void onResponse(Call<Messages> call, retrofit2.Response<Messages> response) {
                        if (response.isSuccessful())
                            Toast.makeText(getContext(), "Success\nDon't forget to refresh", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getContext(), "Error: " + response.message() + "\nDon't forget to refresh", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Messages> call, Throwable t) {
                        Toast.makeText(getContext(), "Failed: " + t.getMessage() + "\nDon't forget to refresh", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog a = builder.create();
        Window w = a.getWindow();
        if (w != null)
            w.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        a.show();
    }

    private void onReply() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.reply);

        // Set up the input
        final EditText input = new EditText(getContext());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
        input.setSingleLine(false);
        input.setHint(R.string.message);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        input.setLayoutParams(lp);

        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ApiService api = app.getApiService();
                Call<Messages> call = api.createMessageReply(message.id, app.me.id, input.getText().toString());
                call.enqueue(new Callback<Messages>() {
                    @Override
                    public void onResponse(Call<Messages> call, retrofit2.Response<Messages> response) {
                        if (response.isSuccessful())
                            Toast.makeText(getContext(), "Success\nDon't forget to refresh", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getContext(), "Error: " + response.message() + "\nDon't forget to refresh", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Messages> call, Throwable t) {
                        Toast.makeText(getContext(), "Failed: " + t.getMessage() + "\nDon't forget to refresh", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog a = builder.create();
        a.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        a.show();
        input.requestFocus();
    }

    private void onEdit(final String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.edit_message);

        // Set up the input
        final EditText input = new EditText(getContext());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setSingleLine(false);
        input.setText(str);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            lp.setMarginStart(100);
        }
        input.setLayoutParams(lp);

        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ApiService api = app.getApiService();
                Call<Messages> call = api.updateMessage(message.id, input.getText().toString());
                call.enqueue(new Callback<Messages>() {
                    @Override
                    public void onResponse(Call<Messages> call, retrofit2.Response<Messages> response) {
                        if (response.isSuccessful())
                            Toast.makeText(getContext(), "Success\nDon't forget to refresh", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getContext(), "Error: " + response.message() + "\nDon't forget to refresh", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Messages> call, Throwable t) {
                        Toast.makeText(getContext(), "Failed: " + t.getMessage() + "\nDon't forget to refresh", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog a = builder.create();
        a.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        a.show();
        input.requestFocus();
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void response(Activity activity, final Response<String> r) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (r.isSuccess())
                    Toast.makeText(getContext(), R.string.success, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), r.getErrorBody().toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
}