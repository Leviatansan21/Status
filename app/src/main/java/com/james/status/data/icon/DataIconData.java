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
import android.telephony.TelephonyManager;

import com.james.status.R;

import java.lang.ref.SoftReference;

//HAH IT'S A ICON DATA FOR THE DATA ICON GET IT BECAUSE THEIR NAMES ARE THE SAME BUT REVERSED YEAH IT'S SO FUNNY HAHHAHAHAHAHAHAH I KNOW RIGHT IT'S REALLY HILARIOUS I'M LITERALLY DYING OF LAUGHTER
public class DataIconData extends IconData {

    private TelephonyManager telephonyManager;
    private DataListener dataListener;

    private boolean isRegistered;

    public DataIconData(Context context) {
        super(context);

        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        dataListener = new DataListener(this);
    }

    @Override
    public String[] getPermissions() {
        return new String[]{Manifest.permission.READ_PHONE_STATE};
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
    public void register() {
        if (dataListener != null)
            telephonyManager.listen(dataListener, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
        isRegistered = true;

        onDataChanged();
    }

    private void onDataChanged() {
        switch (telephonyManager.getDataState()) {
            case TelephonyManager.DATA_CONNECTED:
            case TelephonyManager.DATA_CONNECTING:
                if (telephonyManager.getDataState() != TelephonyManager.DATA_DISCONNECTED) {
                    switch (telephonyManager.getNetworkType()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            onTextUpdate("2G");
                            return;
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                            onTextUpdate("3G");
                            return;
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                            onTextUpdate("H");
                            return;
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            onTextUpdate("H+");
                            return;
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            onTextUpdate("4G");
                            return;
                    }
                }
        }

        onTextUpdate(null);
    }

    @Override
    public void unregister() {
        isRegistered = false;
    }

    @Override
    public String getTitle() {
        return getContext().getString(R.string.icon_data);
    }

    private static class DataListener extends PhoneStateListener {

        private SoftReference<DataIconData> reference;

        private DataListener(DataIconData iconData) {
            reference = new SoftReference<>(iconData);
        }

        @Override
        public void onDataConnectionStateChanged(int state, int networkType) {
            super.onDataConnectionStateChanged(state, networkType);

            DataIconData icon = reference.get();
            if (icon != null && icon.isRegistered)
                icon.onDataChanged();
        }
    }
}
