/*
 *    Copyright 2019 James Fenn
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.james.status.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.james.status.R;
import com.james.status.data.PreferenceData;

import java.io.File;
import java.util.List;

public class BackupCreatorDialog extends ThemedCompatDialog implements View.OnClickListener {

    private OnBackupChangedListener listener;
    private List<File> files;
    private File file;

    private EditText editText;

    public BackupCreatorDialog(Context context, List<File> files, File file) {
        super(context);
        setTitle(file != null ? R.string.preference_backups : R.string.action_new_backup);
        this.files = files;
        this.file = file;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_backup_creator);

        editText = findViewById(R.id.name);
        if (file != null)
            editText.setText(file.getName().substring(0, file.getName().length() - 4));
        else {
            String name = "backup";
            for (int i = 1; hasFile(name); i++) {
                name = "backup" + i;
            }

            file = new File(PreferenceData.getBackupsDir(), name + ".txt");
            editText.setText(name);
        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name = editText.getText().toString();

                String replaced = name.replaceAll("[^a-zA-Z0-9.-]", "_");
                if (!name.equals(replaced)) {
                    editText.setText(replaced);
                    return;
                }

                if (hasFile(name)) {
                    editText.setError(getContext().getString(R.string.error_name_exists));
                    file = null;
                    return;
                }

                file = new File(PreferenceData.getBackupsDir(), name + ".txt");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        TextView delete = findViewById(R.id.delete);
        delete.setText(file.exists() ? R.string.action_delete : R.string.action_cancel);
        delete.setOnClickListener(this);

        if (file.exists())
            findViewById(R.id.restore).setOnClickListener(this);
        else findViewById(R.id.restore).setVisibility(View.GONE);

        TextView save = findViewById(R.id.save);
        save.setText(file.exists() ? R.string.action_save_backup : R.string.action_create_backup);
        save.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                if (file != null) {
                    if (listener != null && PreferenceData.toFile(getContext(), file)) {
                        listener.onFileChanged(false);
                        dismiss();
                    }
                } else editText.setError(getContext().getString(R.string.error_name_exists));
                break;
            case R.id.restore:
                if (file != null && listener != null && file.exists() && PreferenceData.fromFile(getContext(), file)) {
                    listener.onFileChanged(true);
                    dismiss();
                }
                break;
            case R.id.delete:
                if (file != null && file.exists() && file.delete()) {
                    listener.onFileChanged(false);
                    dismiss();
                }
                break;
        }
    }

    public void setListener(OnBackupChangedListener listener) {
        this.listener = listener;
    }

    private boolean hasFile(String name) {
        for (File file : files) {
            String fileName = file.getName().substring(0, file.getName().length() - 4);
            if (fileName.equals(name))
                return true;
        }

        return false;
    }

    public interface OnBackupChangedListener {
        void onFileChanged(boolean isSettingsChanged);
    }
}
