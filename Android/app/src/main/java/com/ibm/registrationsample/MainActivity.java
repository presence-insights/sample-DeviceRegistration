/**
 * Copyright (c) 2016 IBM Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package com.ibm.registrationsample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ibm.json.java.JSONObject;
import com.ibm.pisdk.DeviceInfo;
import com.ibm.pisdk.PIAPIAdapter;
import com.ibm.pisdk.PIAPICompletionHandler;
import com.ibm.pisdk.PIAPIResult;
import com.ibm.pisdk.PIDeviceInfo;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Settings vars
    public static String TENANT;
    public static String ORG;
    public static String USERNAME;
    public static String PASSWORD;

    // Edit text vars
    public static ArrayList<EditText> dataArr = new ArrayList<>();
    public static ArrayList<EditText> clearArr = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Use default preference settings
        PreferenceManager.setDefaultValues(this, R.xml.preference, false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        TENANT = prefs.getString("pref_tenant", "");
        ORG = prefs.getString("pref_org", "");
        USERNAME = prefs.getString("pref_username", "");
        PASSWORD = prefs.getString("pref_password", "");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent  = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Register/update the device
    public void register(View view) {
        if (!checkVars(USERNAME, PASSWORD, TENANT, ORG)) {
            return;
        }

        PIAPIAdapter adapter = new PIAPIAdapter(this, USERNAME, PASSWORD, "https://presenceinsights.ibmcloud.com", TENANT, ORG);

        PIDeviceInfo device = loadJSON(new PIDeviceInfo(this));

        adapter.registerDevice(device, new PIAPICompletionHandler() {
            @Override
            public void onComplete(PIAPIResult result) {
                handleResult(result);
            }
        });
    }

    // Unregister the device
    public void unregister(View view) {
        if (!checkVars(USERNAME, PASSWORD, TENANT, ORG)) {
            return;
        }

        PIAPIAdapter adapter = new PIAPIAdapter(this, USERNAME, PASSWORD, "https://presenceinsights.ibmcloud.com", TENANT, ORG);

        DeviceInfo device = new PIDeviceInfo(this);

        adapter.unregisterDevice(device, new PIAPICompletionHandler() {
            @Override
            public void onComplete(PIAPIResult result) {
                handleResult(result);
            }
        });
    }

    // Check that the settings are non-empty
    public boolean checkVars(String user, String pass, String tenant, String org) {
        if (user.length() == 0) {
            Toast error = Toast.makeText(getApplicationContext(), "Error: Username must be set", Toast.LENGTH_SHORT);
            error.show();
            return false;
        }
        if (pass.length() == 0) {
            Toast error = Toast.makeText(getApplicationContext(), "Error: Password must be set", Toast.LENGTH_SHORT);
            error.show();
            return false;
        }
        if (tenant.length() == 0) {
            Toast error = Toast.makeText(getApplicationContext(), "Error: Tenant must be set", Toast.LENGTH_SHORT);
            error.show();
            return false;
        }
        if (org.length() == 0) {
            Toast error = Toast.makeText(getApplicationContext(), "Error: Org must be set", Toast.LENGTH_SHORT);
            error.show();
            return false;
        }

        return true;
    }

    // Load the JSON vars into the device
    public PIDeviceInfo loadJSON(PIDeviceInfo device) {
        // Name
        EditText name = (EditText) findViewById(R.id.name);
        String stringName = name.getText().toString();
        device.setName(stringName);

        // Blacklist
        RadioGroup blacklistGroup = (RadioGroup) findViewById(R.id.blacklist);
        int blacklistButtonSelected = blacklistGroup.getCheckedRadioButtonId();
        RadioButton blacklistButton = (RadioButton) findViewById(blacklistButtonSelected);

        if (blacklistButton != null) {
            Boolean blacklist = Boolean.parseBoolean(blacklistButton.getText().toString());
            device.setBlacklisted(blacklist);
        }

        // Registration type
        RadioGroup regGroup = (RadioGroup) findViewById(R.id.registrationType);
        int regButtonSelected = regGroup.getCheckedRadioButtonId();
        RadioButton regButton = (RadioButton) findViewById(regButtonSelected);

        if (regButton != null) {
            String regType = regButton.getText().toString();
            device.setType(regType);
        }

        device.setRegistered(true);

        // Encrypted Data
        JSONObject dataObject = new JSONObject();
        LinearLayout dataLayout = (LinearLayout) findViewById(R.id.dataLayout);
        // Get the children without IDs
        for (int i = 0; i < dataLayout.getChildCount(); i++) {
            LinearLayout childLayoutData = (LinearLayout) dataLayout.getChildAt(i);

            // Key
            TextInputLayout childTILKeyData = (TextInputLayout) childLayoutData.getChildAt(0);
            EditText childEditKeyData = (EditText) childTILKeyData.getChildAt(0);

            String stringKeyData = childEditKeyData.getText().toString();

            // Value
            TextInputLayout childTILValueData = (TextInputLayout) childLayoutData.getChildAt(1);
            EditText childEditValueData = (EditText) childTILValueData.getChildAt(0);

            String stringValueData = childEditValueData.getText().toString();

            if (stringKeyData.length() != 0) {
                dataObject.put(stringKeyData, stringValueData);
            }
        }

        device.setData(dataObject);


        // Unencrypted Data
        JSONObject clearObject = new JSONObject();
        LinearLayout clearLayout = (LinearLayout) findViewById(R.id.clearLayout);
        // Get the children without IDs
        for (int i = 0; i < clearLayout.getChildCount(); i++) {
            LinearLayout childLayoutClear = (LinearLayout) clearLayout.getChildAt(i);

            // Key
            TextInputLayout childTILKeyClear = (TextInputLayout) childLayoutClear.getChildAt(0);
            EditText childEditKeyClear = (EditText) childTILKeyClear.getChildAt(0);

            String stringKeyClear = childEditKeyClear.getText().toString();

            // Value
            TextInputLayout childTILValueClear = (TextInputLayout) childLayoutClear.getChildAt(1);
            EditText childEditValueClear = (EditText) childTILValueClear.getChildAt(0);

            String stringValueClear = childEditValueClear.getText().toString();

            if (stringKeyClear.length() != 0) {
                clearObject.put(stringKeyClear, stringValueClear);
            }
        }

        device.setUnencryptedData(clearObject);

        return device;
    }

    // Common code for register and unregister toast
    public void handleResult(PIAPIResult result) {

        JSONObject jsonResult = null;
        try {
            jsonResult = JSONObject.parse(result.getResultAsString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        int responseCode = result.getResponseCode();
        String message = "";

        if (responseCode != 200 && responseCode != 201) {
            String error = (String) jsonResult.get("message");
            message = "Response code: " + result.getResponseCode() + ", " + error;

            Toast alert = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
            alert.show();
            return;
        }

        String response = Integer.toString(responseCode);
        String registered = jsonResult.get("registered").toString();

        if (responseCode == 201) {
            message = "device created";
        } else if (registered.equals("true")) {
            message = "device updated";
        } else if (registered.equals("false")) {
            message = "device unregistered";
        }

        Toast alert = Toast.makeText(getApplicationContext(), "response code: " + response + ", " + message, Toast.LENGTH_SHORT);
        alert.show();
    }

    // Calls a new row for the encrypted layout
    public void addData(View view) {
        addRow(R.id.dataLayout);

        Button dataButton = (Button) findViewById(R.id.addDataRow);
        dataButton.setEnabled(false);
    }

    // Calls a new row for the unencrypted layout
    public void addClear(View view) {
        addRow(R.id.clearLayout);

        Button clearButton = (Button) findViewById(R.id.addClearRow);
        clearButton.setEnabled(false);
    }

    // Add a new key and value row
    public void addRow(int id) {
        LinearLayout linear = (LinearLayout)findViewById(id);

        // Add LinearLayout
        LinearLayout linearChild = new LinearLayout(this);
        linear.addView(linearChild);
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) linearChild.getLayoutParams();
        linearParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        linearParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        linearChild.setOrientation(LinearLayout.HORIZONTAL);
        linearChild.setLayoutParams(linearParams);

        // Add key
        TextInputLayout tilKey = new TextInputLayout(this);
        linearChild.addView(tilKey);
        tilKey.setHint("Key");
        LinearLayout.LayoutParams tilParams = (LinearLayout.LayoutParams) tilKey.getLayoutParams();
        tilParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        tilParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        tilParams.weight = 1;
        tilKey.setLayoutParams(tilParams);

        EditText editKey = new EditText(this);
        tilKey.addView(editKey);
        LinearLayout.LayoutParams editParams = (LinearLayout.LayoutParams) editKey.getLayoutParams();
        editParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        editParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        editKey.setLayoutParams(editParams);

        if (id == R.id.dataLayout) {
            editKey.addTextChangedListener(new DataTextWatcher(editKey));
            dataArr.add(editKey);
        } else if (id == R.id.clearLayout) {
            editKey.addTextChangedListener(new ClearTextWatcher(editKey));
            clearArr.add(editKey);
        }

        // Add value
        TextInputLayout tilValue = new TextInputLayout(this);
        linearChild.addView(tilValue);
        tilValue.setLayoutParams(tilParams);

        EditText editValue = new EditText(this);
        tilValue.addView(editValue);
        tilValue.setHint("Value");
        editValue.setLayoutParams(editParams);
    }

    // Text watcher to control encrypted row button
    private class DataTextWatcher implements TextWatcher {
        private EditText mEditText;

        public DataTextWatcher(EditText e) {
            mEditText = e;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            Button dataButton = (Button) findViewById(R.id.addDataRow);
            Boolean empty = false;
            for (int i = 0; i < dataArr.size(); i++) {
                if (dataArr.get(i).length() == 0) {
                    empty = true;
                    break;
                }
            }

            if (!empty) {
                dataButton.setEnabled(true);
            } else {
                dataButton.setEnabled(false);
            }
        }
    }

    // Text watcher to control unencrypted row button
    private class ClearTextWatcher implements TextWatcher {
        private EditText mEditText;

        public ClearTextWatcher(EditText e) {
            mEditText = e;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            Button clearButton = (Button) findViewById(R.id.addClearRow);
            Boolean empty = false;
            for (int i = 0; i < clearArr.size(); i++) {
                if (clearArr.get(i).length() == 0) {
                    empty = true;
                    break;
                }
            }

            if (!empty) {
                clearButton.setEnabled(true);
            } else {
                clearButton.setEnabled(false);
            }
        }
    }
}
