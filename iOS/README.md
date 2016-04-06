# IBM Presence Insights Device Registration 

The purpose of this application is to show how to leverage Presence Insights SDK (device registration) for IOS(swift). This application demonstrates how to register and unregister devices with encrypted and unencrypted data. When the application starts, it will check to see if the device is already registered.

You can use the IOS device or XCode simulator to run this application.

1. Open PIDeviceRegistration.xcodeproj.
2. Edit the ViewController.swift file and update the Bluemix credentials, tenantID, orgID, baseURL, username, and password. 
Note that all of this information can be found in your Presence Insights Dashboard.
3. Click **Play** to build and run the application.

The ViewController.swift appliction is a great way to see how objects are initialized and implemented. The application will perform an initial check to see if the device is already registered. If the device is registered, it will alert the user and populate the device name and type.

The sample application contains the following fields and options:

* **Device Name** - Text Field
	- Type in the name you want to register the device as.
* **Device Type** - Selection
	- It grabs the device types so the user can select instead of having them type.
* **Encrypted Data Key** - Text Field
	- Encrypted Device Key value
* **Encrypted Data Value** - Text Field
	- Encrypted Device Data value
* **Unencrypted Data Key** - Text Field
    - Unencrypted Device Key value
* **Unencrypted Data Value** - Text Field
    - Unencrypted Device Data value
	
**Note:** If Key or Value is empty, it will throw an error. Will pass if both exist or empty.

* **UI Switch**
   - When the switch is set to **On**, it registers the device.
   - When the switch is set to **Off**, it unregisters the device. 
* **Update**
  Checks to see if the switch is set to **On**. If so, the device is registered and updated.



Copyright 2015 IBM Corp.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
