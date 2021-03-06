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

package com.james.status.data.preference;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import com.james.status.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatRadioButton;
import me.jfenn.androidutils.DimenUtils;

public class FontPreferenceData extends BasePreferenceData<String> {

    private String preference;
    private List<String> items;

    private String selectedPreference;

    public FontPreferenceData(Context context, Identifier<String> identifier, OnPreferenceChangeListener<String> listener, String... items) {
        super(context, identifier, listener);

        this.items = new ArrayList<>(Arrays.asList(items));
        preference = getIdentifier().getPreferenceValue(context, "");
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    @Override
    public void onClick(View v) {
        ScrollView scrollView = new ScrollView(getContext());

        RadioGroup group = new RadioGroup(getContext());
        int vPadding = DimenUtils.dpToPx(12);
        group.setPadding(0, vPadding, 0, vPadding);

        AppCompatRadioButton normalButton = (AppCompatRadioButton) LayoutInflater.from(getContext()).inflate(R.layout.item_dialog_radio_button, group, false);
        normalButton.setId(0);
        normalButton.setText(R.string.font_default);
        normalButton.setChecked(preference == null || preference.length() == 0);
        group.addView(normalButton);

        for (int i = 0; i < items.size(); i++) {
            String item = items.get(i);

            AppCompatRadioButton button = (AppCompatRadioButton) LayoutInflater.from(getContext()).inflate(R.layout.item_dialog_radio_button, group, false);
            button.setId(i + 1);
            button.setText(item.replace(".ttf", ""));
            button.setTag(item);
            try {
                button.setTypeface(Typeface.createFromAsset(getContext().getAssets(), item));
            } catch (Exception e) {
                continue;
            }
            button.setChecked(preference != null && preference.equals(item));
            group.addView(button);
        }

        group.setOnCheckedChangeListener((group1, checkedId) -> {
            for (int i = 0; i < group1.getChildCount(); i++) {
                RadioButton child = (RadioButton) group1.getChildAt(i);
                child.setChecked(child.getId() == checkedId);
                if (child.getId() == checkedId)
                    selectedPreference = (String) child.getTag();
            }
        });

        scrollView.addView(group);

        new AlertDialog.Builder(getContext())
                .setTitle(getIdentifier().getTitle())
                .setView(scrollView)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    FontPreferenceData.this.preference = selectedPreference;

                    getIdentifier().setPreferenceValue(getContext(), selectedPreference);
                    onPreferenceChange(selectedPreference);
                    selectedPreference = null;
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> selectedPreference = null)
                .show();
    }
}
