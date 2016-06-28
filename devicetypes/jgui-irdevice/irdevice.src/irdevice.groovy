/**
 *  IrDevice
 *
 *  Copyright 2016 James Guistwite
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */


import groovy.json.JsonSlurper
import groovy.json.JsonBuilder

metadata {
	definition (name: "IrDevice", namespace: "jgui.irdevice", author: "James Guistwite") {
		capability "Switch"
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
			standardTile("switchTile", "device.switch", width: 1, height: 1,
    	         canChangeIcon: true) {
    		state "off", label: '${name}', action: "switch.on",
   		       icon: "st.switches.switch.off", backgroundColor: "#ffffff"
  		  state "on", label: '${name}', action: "switch.off",
      		    icon: "st.switches.switch.on", backgroundColor: "#0090D0"
		}

        main "switchTile"
        details(["switchTile"])
	}
}

// parse events into attributes
def parse(String description) {
}

// handle commands
def on() {
  def deviceKey = device.deviceNetworkId;
  def json = "{\"networkId\":\"${networkId}\",\"state\":\"on\"}"
  sendEvent(name: "irCommand", value: json)
  sendEvent(name: "switch", value:"on")
}

def off() {
  def deviceKey = device.deviceNetworkId;
  def json = "{\"networkId\":\"${networkId}\",\"state\":\"off\"}"
  sendEvent(name: "irCommand", value: json)
  sendEvent(name: "switch", value:"off")
}