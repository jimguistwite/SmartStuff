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

  preferences {
        input name: "device1", type: "text", title: "Device 1", description: "Enter Location and device", required: true, displayDuringSetup: true
        input name: "device2", type: "text", title: "Device 2", description: "Enter Location and device", required: false
        input name: "device3", type: "text", title: "Device 3", description: "Enter Location and device", required: false
        input name: "device4", type: "text", title: "Device 4", description: "Enter Location and device", required: false
        input name: "device5", type: "text", title: "Device 5", description: "Enter Location and device", required: false

   }

	tiles {
	  standardTile("switchTile", "device.switch", width: 1, height: 1, canChangeIcon: true) {
   		state "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
        state "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#0090D0"
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
  doit("poweron", "on")
}
def off() {
  doit("poweroff", "off");
}

def tocommand(s, onoff) {
  def locAndDev = s.tokenize(':')
  return [location: locAndDev[0], device: locAndDev[1], action: onoff]
}

def doit(onoff, newstate) {
  def c2 = [];
  if (device1 != null) c2.add(tocommand(device1, onoff))
  if (device2 != null) c2.add(tocommand(device2, onoff))
  if (device3 != null) c2.add(tocommand(device3, onoff))
  if (device4 != null) c2.add(tocommand(device4, onoff))
  if (device5 != null) c2.add(tocommand(device5, onoff))

 // def commands = device.deviceNetworkId.tokenize(',').collect {
 //     def locAndDev = it.tokenize(':')
 //     return [location: locAndDev[0], device: locAndDev[1], action: onoff]
 // }
  def json = new groovy.json.JsonBuilder()
  json commands: c2
  sendEvent(name: "irCommand", value: json.toString())
  sendEvent(name: "switch", value:newstate)
}
