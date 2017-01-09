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
        capability "Momentary"
        command "action1"
        command "action2"
        command "action3"
        command "action4"
        command "action5"
        command "action6"
        command "action7"
        command "action8"
        command "action9"
	}

	simulator {
		// TODO: define status and reply messages here
	}

  preferences {
        input name: "device1", type: "text", title: "Device 1", description: "Enter Location and device", required: true, displayDuringSetup: true
        input name: "action1", type: "text", title: "Action 1", description: "Action 1", required: false
        input name: "action2", type: "text", title: "Action 2", description: "Action 2", required: false
        input name: "action3", type: "text", title: "Action 3", description: "Action 3", required: false
        input name: "action4", type: "text", title: "Action 4", description: "Action 4", required: false
        input name: "action5", type: "text", title: "Action 5", description: "Action 5", required: false
        input name: "action6", type: "text", title: "Action 6", description: "Action 6", required: false
        input name: "action7", type: "text", title: "Action 7", description: "Action 7", required: false
        input name: "action8", type: "text", title: "Action 8", description: "Action 8", required: false
        input name: "action9", type: "text", title: "Action 9", description: "Action 9", required: false

   }

	tiles {
	  standardTile("switchTile", "device.switch", width: 1, height: 1, canChangeIcon: true) {
   		state "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
        state "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#0090D0"
	  }

      standardTile("action1Tile", "device.action1", width: 1, height: 1, canChangeIcon: true) {
        state "default", label: 'A1', action: "action1", icon: "st.Electronics.electronics13", backgroundColor: "#0275d8"
	  }
      standardTile("action2Tile", "device.action2", width: 1, height: 1, canChangeIcon: true) {
        state "default", label: 'A2', action: "action2", icon: "st.Electronics.electronics13", backgroundColor: "#5cb85c"
	  }
      standardTile("action3Tile", "device.action3", width: 1, height: 1, canChangeIcon: true) {
        state "default", label: 'A3', action: "action3", icon: "st.Electronics.electronics13", backgroundColor: "#5bc0de"
	  }
      standardTile("action4Tile", "device.action4", width: 1, height: 1, canChangeIcon: true) {
        state "default", label: 'A4', action: "action4", icon: "st.Electronics.electronics13", backgroundColor: "#0275d8"
	  }

      main "switchTile"
      details(["switchTile", "action1Tile", "action2Tile", "action3Tile", "action4Tile"])
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

def tocommand(s, action) {
  def locAndDev = s.tokenize(':')
  return [location: locAndDev[0], device: locAndDev[1], action: action]
}

def doit(action, newstate) {
  def c2 = [];
  if (device1 != null) c2.add(tocommand(device1, action))
  def json = new groovy.json.JsonBuilder()
  json commands: c2
  sendEvent(name: "irCommand", value: json.toString(), isStateChange: true)
  if (newstate != null) {
    sendEvent(name: "switch", value:newstate)
  }
}

def doaction(action) {
  def idx = action.indexOf(":")
  if (idx > 0) {
    action = action.substring(idx+1)
  }
  log.debug("do ${action}")
  doit(action, null);
}

def action1() { doaction(action1) }
def action2() { doaction(action2) }
def action3() { doaction(action3) }
def action4() { doaction(action4) }
def action5() { doaction(action5) }
def action6() { doaction(action6) }
def action7() { doaction(action7) }
def action8() { doaction(action8) }
def action9() { doaction(action9) }
