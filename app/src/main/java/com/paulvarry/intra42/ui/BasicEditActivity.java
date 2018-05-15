package com.paulvarry.intra42.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.paulvarry.intra42.R;

public abstract class BasicEditActivity extends BasicThreadActivity {

    protected MenuItem menuItemSave;
    protected MenuItem menuItemDelete;

    protected ProgressDialog progressDialog;

    Callback callBackDelete = new Callback() {
        @Override
        public void succeed() {
            show(getString(R.string.user_expertise_deleted));
            setResult(RESULT_OK);
            finish();
        }

        @Override
        public void failed() {
            failed(getString(R.string.error));
        }

        @Override
        public void message(String msg) {
            show(msg);
        }

        @Override
        public void failed(String msg) {
            show(msg);
            setResult(RESULT_CANCELED);
            finish();
        }

        void show(final String str) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.cancel();
                    progressDialog = new ProgressDialog(BasicEditActivity.this);
                    Toast.makeText(app, str, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    Callback callBackSave = new Callback() {
        @Override
        public void succeed() {
            show(getString(R.string.done));
            setResult(RESULT_OK);
            finish();
        }

        @Override
        public void failed() {
            failed(getString(R.string.error));
        }

        @Override
        public void message(String msg) {
            show(msg);
        }

        @Override
        public void failed(String msg) {
            show(msg);
            setResult(RESULT_CANCELED);
            finish();
        }

        void show(final String str) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.cancel();
                    progressDialog = new ProgressDialog(BasicEditActivity.this);
                    Toast.makeText(app, str, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setActionBarToggle(ActionBarToggle.CROSS);

        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onBackPressed() {

        if (haveUnsavedData()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.edit_unsaved_changes_title);
            builder.setMessage(R.string.edit_unsaved_changes_content);

            builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onSave(callBackSave);
                }
            });
            builder.setNegativeButton(R.string.discard_changes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    BasicEditActivity.super.onBackPressed();
                }
            });
            builder.setNeutralButton(R.string.continue_editing, null);

            builder.show();
        } else
            super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_edit_activity, menu);

        menuItemSave = menu.findItem(R.id.actionSave);
        menuItemDelete = menu.findItem(R.id.actionDelete);

        if (isCreate()) {
            menuItemDelete.setVisible(false);
        } else {
            menuItemDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    progressDialog.show();
                    onDelete(callBackDelete);
                    return true;
                }
            });
        }

        menuItemSave.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                progressDialog.show();
                onSave(callBackSave);
                return true;
            }
        });

        return true;
    }

    protected void hideActionBarIcon() {
        menuItemSave.setEnabled(false);
        menuItemDelete.setEnabled(false);
    }

    protected void showActionBarIcon() {
        menuItemSave.setEnabled(true);
        menuItemDelete.setEnabled(true);
    }

    protected abstract boolean isCreate();

    protected abstract boolean haveUnsavedData();

    protected abstract void onSave(Callback callBack);

    protected abstract void onDelete(Callback callBack);

    protected interface Callback {

        /**
         * Show Toast with Success and close
         */
        void succeed();

        /**
         * Show Toast with Error and close
         */
        void failed();

        /**
         * Just show the massage
         */
        void message(String msg);

        /**
         * Show Toast with Error and close
         */
        void failed(String msg);
    }
}
