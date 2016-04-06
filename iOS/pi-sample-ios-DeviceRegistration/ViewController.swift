//   © Copyright 2015 IBM Corp.
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.



import UIKit
import PresenceInsightsSDK


class ViewController: UIViewController, UITextFieldDelegate {

    //hard coding the tenantID, orgID, username, password, baseURL
    //This information can be found in your Presence Insights UI/Dashboard
    let tenantID = ""
    let orgID = ""
    let username = ""
    let passwd = ""
    let baseURL = ""

    override func viewDidLoad() {
        super.viewDidLoad()
        self.deviceName.delegate = self
        self.deviceType.delegate = self
        self.unencryptedDataValue.delegate = self
        self.unencryptedDataKey.delegate = self
        self.datakey.delegate = self
        self.datavalue.delegate = self
        //creating a PIAdapter object with bm information
        piAdapter = PIAdapter(tenant: tenantID,
            org: orgID,
            baseURL: baseURL,
            username: username,
            password: passwd)
        //piAdapter.enableLogging()
        deviceType.userInteractionEnabled = false
        
        //initializing device to see if it's registered when the app is loaded.
        //if regsitered switch the registered switch to on.
        // note: i do not pull the encrypted data and unecnrypted data in the fields when the device is populated.
        device = PIDevice(name: " ")
        piAdapter.getDeviceByDescriptor(device.descriptor) { (rdevice, NSError) -> () in
            if((rdevice.name?.isEmpty) == nil){
            }
            else{
                //using UI thread to make teh necessary changes.
                dispatch_async(dispatch_get_main_queue(), { () -> Void in
                    self.RegisterSwitch.setOn(true, animated: true)
                    self.deviceName.text = rdevice.name
                    self.deviceType.text = rdevice.type
                    self.alert("Status", messageInput: "The Device is currently registered. Device Name and Type will popular based on PI information")
                })
                
                print("Device is already registered")
            }
        }

    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    
    
    //initializing piAdapter object and PIdevice
    var piAdapter : PIAdapter!
    var device : PIDevice!
    
    
    @IBOutlet weak var deviceName: UITextField!
    @IBOutlet weak var datakey: UITextField!
    @IBOutlet weak var datavalue: UITextField!
    @IBOutlet weak var unencryptedDataKey: UITextField!
    @IBOutlet weak var unencryptedDataValue: UITextField!
    @IBOutlet weak var deviceType: UITextField!
    
    
    //pop up alert and display BM information
    @IBAction func bmAction(sender: UIButton) {
        alert("BM Information", messageInput: "Username: \(username) \n Password: \(passwd) \n Tenant ID: \(tenantID) \n Org ID: \(orgID)")
        
    }
    
    //UI selection for which device Type exist in the user's PI
    @IBAction func DeviceTypeAction() {
        //gets org information
        piAdapter.getOrg { (org, NSError) -> () in
            print(org.registrationTypes);
            //grab the different device types
            let Types = org.registrationTypes
            print("TEST")
            print(Types.count)
            //need this dispatch to change from backend thread to UI thread
            dispatch_async(dispatch_get_main_queue(), { () -> Void in
                let alert = UIAlertController(title: "Select Device Type", message: "", preferredStyle: UIAlertControllerStyle.Alert)
                for Type in Types{
                    alert.addAction(UIAlertAction(title: "\(Type)", style: UIAlertActionStyle.Default, handler:{ (UIAlertAction) in
                        self.deviceType.text = Type
                        print(Type)
                    }))
                }
                alert.addAction(UIAlertAction(title: "cancel", style: UIAlertActionStyle.Default, handler: nil))
                self.presentViewController(alert, animated: true, completion: nil)
            })
            
        }
        
    }
    
    
    
    //Update action
    @IBAction func Action(sender: UIButton) {
        //adding device name to the device object
        device = PIDevice(name: deviceName.text!)
        device.type = deviceType.text
        
        
        //adding device type to the device object.
        //NOTE: the type has to exist in the Presence Insights. Default types are : "Internal" and "External"
        
        //method to add encrypted data
        // ("VALUE", key: "KEY_NAME")
        
        //checks to see if key or value is empty and throw error as necessary
        if( MissingText(datavalue, key: datakey) ==  false){
            device.addToDataObject(datavalue.text!, key: datakey.text!)
        }
        
        //checks to see if key or value is empty and throw error as necessary for unencrypted data
        if( MissingText(unencryptedDataValue, key: unencryptedDataKey) ==  false){
            device.addToUnencryptedDataObject(unencryptedDataValue.text!, key: unencryptedDataKey.text!)
        }
        
        
        //when the user flicks the switch to green
        //checking to see if the device is registered before updating the device.
        if RegisterSwitch.on {
            
            //set device register to true if the light is on
            device.registered=true
            //device is registered and will update.
            piAdapter.updateDevice(device, callback: { (newDevice, NSError) -> () in
                dispatch_async(dispatch_get_main_queue(), { () -> Void in
                    if(NSError == nil){
                        self.alert("Success", messageInput: "Successfully Updated the Device Information")
                    }
                    else{
                        self.alert("Error", messageInput: "\(NSError)")
                    }
                })
            })
            
            
            
        }
        else {
            //if the device is not registered, will alert saying must register device
            alert("Error", messageInput: "Must Register Device Before Updating")
        }
    }
    
    //function to make keyboard disappear when pressing return.
    func textFieldShouldReturn(textField: UITextField) -> Bool {
        self.view.endEditing(true)
        return false
    }
    
    @IBOutlet weak var RegisterSwitch: UISwitch!
    
    //Register Switch Action
    @IBAction func switchAction(sender: AnyObject) {
        
        
        //adding device name to the device object
        device = PIDevice(name: deviceName.text!)
        device.type = deviceType.text
        
        
        //adding device type to the device object.
        //NOTE: the type has to exist in the Presence Insights. Default types are : "Internal" and "External"
        
        //checks to see if key or value is empty
        if( MissingText(datavalue, key: datakey) ==  false){
            device.addToDataObject(datavalue.text!, key: datakey.text!)
        }
        
        //        checks to see if key or value is empty
        if( MissingText(unencryptedDataValue, key: unencryptedDataKey) ==  false){
            device.addToUnencryptedDataObject(unencryptedDataValue.text!, key: unencryptedDataKey.text!)
        }
        
        
        
        if RegisterSwitch.on {
            //PI Device Register SDK call
            piAdapter.registerDevice(device, callback: { (newDevice, NSError) -> () in
                // newDevice is of type PIDevice.
                dispatch_async(dispatch_get_main_queue(), { () -> Void in
                    if(NSError == nil){
                        self.alert("Success", messageInput: "Successfully Registered the Device")
                    }
                    else{
                        self.alert("Error", messageInput: "\(NSError)")
                    }
                })
            })
            
        }
            
        else {
            //PI Device unregister SDK call
            piAdapter.unregisterDevice(device, callback: { (newDevice, NSError) -> () in
                dispatch_async(dispatch_get_main_queue(), { () -> Void in
                    if(NSError == nil){
                        self.alert("Success", messageInput: "Successfully Unregistered the Device")
                    }
                    else{
                        self.alert("Error", messageInput: "\(NSError)")
                    }
                })
            })
        }
        
    }
    
    //function to easily create alert messages
    func alert(titleInput : String , messageInput : String){
        let alert = UIAlertController(title: titleInput, message: messageInput, preferredStyle: UIAlertControllerStyle.Alert)
        alert.addAction(UIAlertAction(title: "ok", style: UIAlertActionStyle.Default, handler: nil))
        self.presentViewController(alert, animated: true, completion: nil)
        
    }
    
    //function to check if both or none of the data value and key exist.
    func MissingText (value: UITextField, key : UITextField) -> Bool{
        if ( (key.text! == "" && value.text! == "")){
            print("here")
            return true
        }
        else{
            print("test")
            return false
        }
        
    }

    

    
   
    
}

