# IBM Presence Insights Device Registration 

## Overview

The purpose of this application is to show how to leverage Presence Insights SDK (device registration) for Android. This application demonstrates how to register and unregister devices with encrypted and unencrypted data.

## Running this code

1. Open the project in Android Studio. 
2. Connect an Android device and install the application.
3. Add your tenant code, org code, Presence Insights username and password into the settings.
4. Register, update, or unregister your device using the app main screen.

The sample application contains the following fields and options:

* **Register**
	- Registers the device if all the appropriate field are set. If the device is already registered it will update the device instead.
* **Unregister**
	- Unregisters the device.
* **Name** - Text Field
	- Type in the name you want to register the device as.
* **Registration Type** - Selection
	- Select a valid registration type.
* **Encrypted Data Key** - Text Field
	- Encrypted Device Key value
* **Encrypted Data Value** - Text Field
	- Encrypted Device Data value
* **Unencrypted Data Key** - Text Field
    - Unencrypted Device Key value
* **Unencrypted Data Value** - Text Field
    - Unencrypted Device Data value
	
**Note:** The "+" buttons underneath the Encrypted and Unenecrypted labels will add encrypted and unencrypted rows for additional data.

===

Copyright 2016 IBM Corp.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.