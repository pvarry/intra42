package com.paulvarry.intra42.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.ServiceGenerator;
import com.paulvarry.intra42.api.model.Language;
import com.paulvarry.intra42.api.model.Topics;
import com.paulvarry.intra42.utils.BypassPicassoImageGetter;

import java.util.List;

import in.uncod.android.bypass.Bypass;

public class NewTopicActivity extends AppCompatActivity {

    private static final String INTENT_TOPIC = "intent_topic";

    //    CompletionViewTags completionViewTag;
//    CompletionViewCursus completionViewCursus;
    TextView textViewPreview;

    List<Topics.Kind> kind;
    List<Language> language;

    private boolean editExistingTopic = false;
    private Topics topic;
    private AppClass app;
    private EditText editTextTitle;
    private Spinner spinnerKind;
    private EditText editTextContent;
    private Spinner spinnerLanguage;
    private Button buttonCreate;
    private boolean edited = false;
    private TextWatcher textWatcherChange;
    private AdapterView.OnItemSelectedListener spinnerWatchEdit;

    public static void openIt(Context context) {
        Intent intent = new Intent(context, NewTopicActivity.class);
        context.startActivity(intent);
    }

    public static void openIt(Context context, Topics topic) {
        Intent intent = new Intent(context, NewTopicActivity.class);
        intent.putExtra(INTENT_TOPIC, topic.toString());
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_topic);

        Intent intent = getIntent();
        String extra = intent.getStringExtra(INTENT_TOPIC);
        if (extra != null && !extra.isEmpty()) {
            topic = ServiceGenerator.getGson().fromJson(extra, Topics.class);
            editExistingTopic = true;
        } else
            topic = new Topics();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        editTextTitle = findViewById(R.id.editTextTitle);
        spinnerKind = findViewById(R.id.spinnerKind);
//        completionViewTag = findViewById(R.id.completion_view_tags);
//        completionViewCursus = findViewById(R.id.completion_view_cursus);
        editTextContent = findViewById(R.id.editTextContent);
        spinnerLanguage = findViewById(R.id.spinnerLanguage);
        textViewPreview = findViewById(R.id.textViewPreview);
        buttonCreate = findViewById(R.id.buttonCreate);

        app = (AppClass) getApplication();


        kind = Topics.Kind.getListOfKind(NewTopicActivity.this);
        spinnerKind.setAdapter(new KindAdapter());

        language = Language.getListOfLanguage(NewTopicActivity.this);
        spinnerLanguage.setAdapter(new LanguageAdapter());

        if (editExistingTopic) {
            setTitle(R.string.forum_topic_edit_button);
            editTextTitle.setText(topic.name);
            editTextContent.setText(topic.message.content.markdown);

            buttonCreate.setText(R.string.save);

            for (int i = 0; i < kind.size(); i++) {
                if (kind.get(i).slug.contentEquals(topic.kind)) {
                    spinnerKind.setSelection(i);
                }
            }

            for (int i = 0; i < language.size(); i++) {
                if (language.get(i).equals(topic.language)) {
                    spinnerLanguage.setSelection(i);
                }
            }
        }

        char[] splitChar = {',', ';'};

//        ArrayAdapter<Tags> adapterTags = new ArrayAdapter<>(NewTopicActivity.this, android.R.layout.simple_list_item_1, CacheTags.get(app.cacheSQLiteHelper));
//        completionViewTag.setAdapter(adapterTags);
//        completionViewTag.allowDuplicates(false);
//        completionViewTag.allowCollapse(false);
//        completionViewTag.setThreshold(0);
//        completionViewTag.setSplitChar(splitChar);
//        completionViewTag.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Delete);
//
//        if (editExistingTopic)
//            for (Tags t : topic.tags)
//                completionViewTag.addObject(t);
//
//        ArrayAdapter<Cursus> adapterCursus = new ArrayAdapter<>(NewTopicActivity.this, android.R.layout.simple_list_item_1, CacheCursus.get(app.cacheSQLiteHelper));
//        completionViewCursus.setAdapter(adapterCursus);
//        completionViewCursus.allowDuplicates(false);
//        completionViewCursus.allowCollapse(false);
//        completionViewCursus.setThreshold(0);
//
//        completionViewCursus.setSplitChar(splitChar);
//        completionViewCursus.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Delete);

//        for (Cursus c : topic.)
//            completionViewCursus.addObject(c);

        Bypass bypass = new Bypass(NewTopicActivity.this);
        CharSequence messageContent = bypass.markdownToSpannable(editTextContent.getText().toString(), new BypassPicassoImageGetter(textViewPreview));
        textViewPreview.setText(messageContent);
        textViewPreview.setMovementMethod(LinkMovementMethod.getInstance());
        editTextContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                Bypass bypass = new Bypass(NewTopicActivity.this);
                CharSequence messageContent = bypass.markdownToSpannable(editable.toString(), new BypassPicassoImageGetter(textViewPreview));
                textViewPreview.setText(messageContent);
                textViewPreview.setMovementMethod(LinkMovementMethod.getInstance());
            }
        });

        setTextChangeListener();
    }

    @Override
    public void onBackPressed() {//TODO: hardcoded string

        if (edited) {
            new AlertDialog.Builder(this)
                    .setTitle("Keep editing")
                    .setMessage("Delete unsaved data")
                    .setPositiveButton("discard", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            NewTopicActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("keep editing", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();
        } else
            super.onBackPressed();
    }

    public void buttonActionTopic(View view) {
        if (editTextTitle.getText().toString().isEmpty())
            editTextTitle.setError(getString(R.string.forum_topic_error_empty));
        if (editTextContent.getText().toString().isEmpty())
            editTextContent.setError(getString(R.string.forum_topic_error_empty));

        if (!editTextTitle.getText().toString().isEmpty() &&
                !editTextContent.getText().toString().isEmpty() &&
                topic.language != null) {
            topic.language = new Language();
        }

        String name = editTextTitle.getText().toString();
        String kind = ((Topics.Kind) spinnerKind.getSelectedItem()).slug;
        String content = editTextContent.getText().toString();
        int language = (int) spinnerLanguage.getSelectedItemId();

//        List<Tags> tag = completionViewTag.getObjects();
//        List<Integer> tagInt = new ArrayList<>();
//        for (Tags t : tag) {
//            tagInt.add(t.id);
//        }
//        String tagString = tagInt.toString();
//
//        List<Cursus> cursus = completionViewCursus.getObjects();
//        List<Integer> cursusInt = new ArrayList<>();
//        for (Cursus c : cursus) {
//            cursusInt.add(c.id);
//        }
//        String cursusString = cursusInt.toString();

//        if (editExistingTopic) {
//            app.getApiService().updateTopic(topic.id, name, kind, language, content, tagString, cursusString).enqueue(new Callback<Topics>() {
//                @Override
//                public void onResponse(Call<Topics> call, Response<Topics> response) {
//                    if (response.isSuccessful())
//                        finish();
//                    else
//                        Toast.makeText(NewTopicActivity.this, response.message(), Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onFailure(Call<Topics> call, Throwable t) {
//                    Toast.makeText(NewTopicActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        } else {
//            app.getApiService().createTopic(name, kind, language, content, tagString, cursusString).enqueue(new Callback<Topics>() {
//                @Override
//                public void onResponse(Call<Topics> call, Response<Topics> response) {
//                    if (response.isSuccessful())
//                        finish();
//                    else
//                        Toast.makeText(NewTopicActivity.this, response.message(), Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onFailure(Call<Topics> call, Throwable t) {
//                    Toast.makeText(NewTopicActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
    }

    void setTextChangeListener() {

        if (textWatcherChange != null)
            return;

        textWatcherChange = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                edited = true;
                unSetTextChangeListener();
            }
        };

        editTextTitle.addTextChangedListener(textWatcherChange);
        editTextContent.addTextChangedListener(textWatcherChange);
//        completionViewTag.addTextChangedListener(textWatcherChange);
//        completionViewCursus.addTextChangedListener(textWatcherChange);

        spinnerWatchEdit = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                edited = true;
                unSetTextChangeListener();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                edited = true;
                unSetTextChangeListener();
            }
        };

        spinnerKind.setOnItemSelectedListener(spinnerWatchEdit);
        spinnerLanguage.setOnItemSelectedListener(spinnerWatchEdit);

        edited = false;
    }

    void unSetTextChangeListener() {
        editTextTitle.removeTextChangedListener(textWatcherChange);
        editTextContent.removeTextChangedListener(textWatcherChange);
//        completionViewTag.removeTextChangedListener(textWatcherChange);
//        completionViewCursus.removeTextChangedListener(textWatcherChange);

        spinnerKind.setOnItemSelectedListener(null);
        spinnerLanguage.setOnItemSelectedListener(null);
    }

    class KindAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        KindAdapter() {
            mInflater = LayoutInflater.from(NewTopicActivity.this);
        }

        @Override
        public int getCount() {
            return kind.size();
        }

        @Override
        public Topics.Kind getItem(int i) {
            return kind.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(android.R.layout.simple_spinner_item, null);
                holder = new ViewHolder();
                holder.spinnerValue = convertView.findViewById(android.R.id.text1);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.spinnerValue.setText(getItem(position).name);
            return convertView;
        }

        class ViewHolder {
            TextView spinnerValue; //spinner name
        }
    }

    class LanguageAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        LanguageAdapter() {
            mInflater = LayoutInflater.from(NewTopicActivity.this);
        }

        @Override
        public int getCount() {
            if (language != null)
                return language.size();
            return 0;
        }

        @Override
        public Language getItem(int i) {
            return language.get(i);
        }

        @Override
        public long getItemId(int i) {
            return getItem(i).id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(android.R.layout.simple_spinner_item, null);
                holder = new ViewHolder();
                holder.spinnerValue = convertView.findViewById(android.R.id.text1);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.spinnerValue.setText(getItem(position).name);
            return convertView;
        }

        class ViewHolder {
            TextView spinnerValue; //spinner name
        }
    }
}
