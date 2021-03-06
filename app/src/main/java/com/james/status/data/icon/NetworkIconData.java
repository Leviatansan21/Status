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

import android.Manifest;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import com.james.status.R;
import com.james.status.data.IconStyleData;

import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import james.signalstrengthslib.SignalStrengths;

public class NetworkIconData extends IconData {

    private TelephonyManager telephonyManager;
    private NetworkListener networkListener;
    private boolean isRegistered;

    public NetworkIconData(Context context) {
        super(context);
        telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
    }

    @Override
    public void register() {
        if (networkListener == null) {
            networkListener = new NetworkListener(this);
            telephonyManager.listen(networkListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        }
        isRegistered = true;
    }

    @Override
    public void unregister() {
        isRegistered = false;
    }

    @Override
    public String getTitle() {
        return getContext().getString(R.string.icon_network);
    }

    @Override
    public String[] getPermissions() {
        return new String[]{Manifest.permission.READ_PHONE_STATE};
    }

    @Override
    public int getIconStyleSize() {
        return 5;
    }

    @Override
    public List<IconStyleData> getIconStyles() {
        List<IconStyleData> styles = super.getIconStyles();

        styles.addAll(
                Arrays.asList(
                        new IconStyleData(
                                getContext().getString(R.string.icon_style_default),
                                IconStyleData.TYPE_VECTOR,
                                R.drawable.ic_signal_0,
                                R.drawable.ic_signal_1,
                                R.drawable.ic_signal_2,
                                R.drawable.ic_signal_3,
                                R.drawable.ic_signal_4
                        ),
                        new IconStyleData(
                                getContext().getString(R.string.icon_style_boxy),
                                IconStyleData.TYPE_VECTOR,
                                R.drawable.ic_icons8_signal_box_0,
                                R.drawable.ic_icons8_signal_box_1,
                                R.drawable.ic_icons8_signal_box_2,
                                R.drawable.ic_icons8_signal_box_2,
                                R.drawable.ic_icons8_signal_box_3
                        ),
                        new IconStyleData(
                                getContext().getString(R.string.icon_style_square),
                                IconStyleData.TYPE_VECTOR,
                                R.drawable.ic_signal_square_0,
                                R.drawable.ic_signal_square_1,
                                R.drawable.ic_signal_square_2,
                                R.drawable.ic_signal_square_3,
                                R.drawable.ic_signal_square_4
                        ),
                        new IconStyleData(
                                getContext().getString(R.string.icon_style_retro),
                                IconStyleData.TYPE_VECTOR,
                                R.drawable.ic_signal_retro_0,
                                R.drawable.ic_signal_retro_1,
                                R.drawable.ic_signal_retro_2,
                                R.drawable.ic_signal_retro_3,
                                R.drawable.ic_signal_retro_4
                        ),
                        new IconStyleData(
                                getContext().getString(R.string.icon_style_circle),
                                IconStyleData.TYPE_VECTOR,
                                R.drawable.ic_network_circle_0,
                                R.drawable.ic_network_circle_1,
                                R.drawable.ic_network_circle_2,
                                R.drawable.ic_network_circle_3,
                                R.drawable.ic_network_circle_4
                        ),
                        new IconStyleData(
                                getContext().getString(R.string.icon_style_curved),
                                IconStyleData.TYPE_VECTOR,
                                R.drawable.ic_network_curved_0,
                                R.drawable.ic_network_curved_1,
                                R.drawable.ic_network_curved_2,
                                R.drawable.ic_network_curved_3,
                                R.drawable.ic_network_curved_4
                        ),
                        new IconStyleData(
                                getContext().getString(R.string.icon_style_clip),
                                IconStyleData.TYPE_VECTOR,
                                R.drawable.ic_number_clip_0,
                                R.drawable.ic_number_clip_1,
                                R.drawable.ic_number_clip_2,
                                R.drawable.ic_number_clip_3,
                                R.drawable.ic_number_clip_4
                        )
                )
        );

        styles.removeAll(Collections.singleton(null));
        return styles;
    }

    @Override
    public String[] getIconNames() {
        return new String[]{
                getContext().getString(R.string.icon_network_no_connection),
                getContext().getString(R.string.icon_network_1_bar),
                getContext().getString(R.string.icon_network_2_bars),
                getContext().getString(R.string.icon_network_3_bars),
                getContext().getString(R.string.icon_network_4_bars)
        };
    }

    private static class NetworkListener extends PhoneStateListener {

        private SoftReference<NetworkIconData> reference;

        private NetworkListener(NetworkIconData iconData) {
            reference = new SoftReference<>(iconData);
        }

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);

            NetworkIconData icon = null;
            if (reference != null) icon = reference.get();

            if (icon != null && icon.isRegistered)
                icon.onIconUpdate((int) Math.round(SignalStrengths.getFirstValid(signalStrength)));
        }
    }
}
