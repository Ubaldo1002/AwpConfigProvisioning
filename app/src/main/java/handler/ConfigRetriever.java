package handler;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigRetriever {

    private static final String PATIENT_FILE_NAME = "patients";
    private static final String TAG = ConfigRetriever.class.getName();

    // beacons.json variable names
    private static final String BEACONS = "beacons";
    private static final String BEACON_ID = "beaconId";

    public List<String> parseBeaconLocations(String jsonStrBeacons){
        List<String> beaconLocations = new ArrayList<String>();

        String beaconId = "";

        if (!jsonStrBeacons.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(jsonStrBeacons);
                JSONArray beacons = jsonObject.getJSONArray(BEACONS);
                for (int i=0; i< beacons.length(); ++i) {

                    JSONObject beacon = beacons.getJSONObject(i);

                    if(beacon.has(BEACON_ID)){
                        beaconId = beacon.getString(BEACON_ID);
                    }else {
                        Log.w(TAG, "missing json field " + BEACON_ID);
                        // continue processing the other beacons but stop processing this one.
                        break;
                    }

                   beaconLocations.add(beaconId);

                }
            } catch (JSONException e1) {
                Log.e("parseBeacon JSONError", e1.getMessage());
                e1.printStackTrace();
            } catch (Exception e1){
                Log.e("parseBeacon Error", e1.getMessage());
                e1.printStackTrace();
            }
        }

        return beaconLocations;
    }


}
