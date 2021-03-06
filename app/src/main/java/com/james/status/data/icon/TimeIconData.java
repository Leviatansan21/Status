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

package com.james.status.data.icon;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.format.DateFormat;

import com.james.status.R;
import com.james.status.data.PreferenceData;
import com.james.status.data.preference.BasePreferenceData;
import com.james.status.data.preference.FormatPreferenceData;
import com.james.status.receivers.IconUpdateReceiver;
import com.james.status.utils.StaticUtils;

import java.util.Calendar;
import java.util.List;

public class TimeIconData extends IconData<TimeIconData.TimeReceiver> {

    private Calendar calendar;
    private String format;

    public TimeIconData(Context context) {
        super(context);
    }

    @Override
    public void init(boolean isFirstInit) {
        calendar = Calendar.getInstance();
        format = PreferenceData.ICON_TEXT_FORMAT.getSpecificOverriddenValue(getContext(), DateFormat.is24HourFormat(getContext()) ? "HH:mm" : "h:mm a", getIdentifierArgs());

        if (!isFirstInit && calendar != null) {
            calendar.setTimeInMillis(System.currentTimeMillis());
            onTextUpdate(DateFormat.format(format, calendar).toString());
        }

        super.init(isFirstInit);
    }

    @Override
    public boolean canHazIcon() {
        return false;
    }

    @Override
    public boolean hasIcon() {
        return false;
    }

    @Override
    public boolean canHazText() {
        return true;
    }

    @Override
    public boolean hasText() {
        return true;
    }

    @Override
    public int getDefaultGravity() {
        return CENTER_GRAVITY;
    }

    @Override
    public TimeReceiver getReceiver() {
        return new TimeReceiver(this);
    }

    @Override
    public IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        return filter;
    }

    @Override
    public void register() {
        super.register();

        calendar.setTimeInMillis(System.currentTimeMillis());
        onTextUpdate(DateFormat.format(format, calendar).toString());
    }

    @Override
    public String getTitle() {
        return getContext().getString(R.string.icon_clock);
    }

    @Override
    public List<BasePreferenceData> getPreferences() {
        List<BasePreferenceData> preferences = super.getPreferences();

        preferences.add(new FormatPreferenceData(
                getContext(),
                new BasePreferenceData.Identifier<String>(
                        PreferenceData.ICON_TEXT_FORMAT,
                        getContext().getString(R.string.preference_time_format),
                        getIdentifierArgs()
                ),
                preference -> {
                    format = preference;
                    StaticUtils.updateStatusService(getContext(), true);
                }
        ));

        return preferences;
    }

    static class TimeReceiver extends IconUpdateReceiver<TimeIconData> {

        private TimeReceiver(TimeIconData iconData) {
            super(iconData);
        }

        @Override
        public void onReceive(TimeIconData icon, Intent intent) {
            icon.calendar.setTimeInMillis(System.currentTimeMillis());
            icon.onTextUpdate(DateFormat.format(icon.format, icon.calendar).toString());
        }
    }
}
