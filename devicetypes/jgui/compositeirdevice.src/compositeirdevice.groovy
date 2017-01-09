/**
 *  CompositeIrDevice
 *
 *  Copyright 2017 James Guistwite
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
metadata {
	definition (name: "CompositeIrDevice", namespace: "jgui", author: "James Guistwite") {
		capability "Switch"
	}

  preferences {
        input name: "on_cmd1", type: "text", title: "On Command 1", description: "Enter Location device and action", required: true, displayDuringSetup: true
        input name: "on_cmd2", type: "text", title: "On Command 2", description: "Enter Location device and action", required: false
        input name: "on_cmd3", type: "text", title: "On Command 3", description: "Enter Location device and action", required: false
        input name: "on_cmd4", type: "text", title: "On Command 4", description: "Enter Location device and action", required: false
        input name: "on_cmd5", type: "text", title: "On Command 5", description: "Enter Location device and action", required: false
        input name: "off_cmd1", type: "text", title: "Off Command 1", description: "Enter Location device and action", required: true, displayDuringSetup: true
        input name: "off_cmd2", type: "text", title: "Off Command 2", description: "Enter Location device and action", required: false
        input name: "off_cmd3", type: "text", title: "Off Command 3", description: "Enter Location device and action", required: false
        input name: "off_cmd4", type: "text", title: "Off Command 4", description: "Enter Location device and action", required: false
        input name: "off_cmd5", type: "text", title: "Off Command 5", description: "Enter Location device and action", required: false
   }

	simulator {
	}

	tiles {
	          standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
            state "off", label: '${currentValue}', action: "switch.on",
                  icon: "st.switches.switch.off", backgroundColor: "#ffffff"
            state "on", label: '${currentValue}', action: "switch.off",
                  icon: "st.switches.switch.on", backgroundColor: "#79b821"
        }
}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}

// handle commands
def on() {
 def c2 = [];
 if (on_cmd1 != null) c2.add(tocommand(on_cmd1))
 if (on_cmd2 != null) c2.add(tocommand(on_cmd2))
 if (on_cmd3 != null) c2.add(tocommand(on_cmd3))
 if (on_cmd4 != null) c2.add(tocommand(on_cmd4))
 if (on_cmd5 != null) c2.add(tocommand(on_cmd5))

  def json = new groovy.json.JsonBuilder()
  json commands: c2
  log.debug("IR: send on event " + json)
  sendEvent(name: "irCommand", value: json, isStateChange: true)
}

def off() {
 def c2 = [];
 if (off_cmd1 != null) c2.add(tocommand(off_cmd1))
 if (off_cmd2 != null) c2.add(tocommand(off_cmd2))
 if (off_cmd3 != null) c2.add(tocommand(off_cmd3))
 if (off_cmd4 != null) c2.add(tocommand(off_cmd4))
 if (off_cmd5 != null) c2.add(tocommand(off_cmd5))

  def json = new groovy.json.JsonBuilder()
  json commands: c2
  log.debug("IR: send off event " + json)
  sendEvent(name: "irCommand", value: json, isStateChange: true)
}

def tocommand(s) {
  def locdevaction = s.tokenize(':')
  return [location: locdevaction[0], device: locdevaction[1], action: locdevaction[2]]
}
