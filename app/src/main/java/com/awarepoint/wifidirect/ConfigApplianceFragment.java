package com.awarepoint.wifidirect;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by ureyes on 7/29/2015.
 */
public class ConfigApplianceFragment extends Fragment{

    private View mContentView = null;
    ProgressDialog progressDialog = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.config_appliance, container, false);
    }

    public void resetViews() {
        mContentView.findViewById(R.id.btn_getconfig).setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.txt_appliance_name);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.txt_appliance_user);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.txt_appliance_pass);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.txt_appliance_wifi_cert);
        view.setText(R.string.empty);

    }

    /**
     * Updates the UI with Configuration data
     *
     * @param config the device to be displayed
     */
    public void showDetails(String config) {

    }

}
