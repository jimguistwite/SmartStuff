/**
 *  irCommandSequence
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
metadata {
	definition (name: "irCommandSequence", namespace: "jgui", author: "James Guistwite") {
		capability "Momentary"
        capability "Switch"
	}

  preferences {
        input name: "cmd1", type: "text", title: "Command 1", description: "Enter Location device and action", required: true, displayDuringSetup: true
        input name: "cmd2", type: "text", title: "Command 2", description: "Enter Location device and action", required: false
        input name: "cmd3", type: "text", title: "Command 3", description: "Enter Location device and action", required: false
        input name: "cmd4", type: "text", title: "Command 4", description: "Enter Location device and action", required: false
        input name: "cmd5", type: "text", title: "Command 5", description: "Enter Location device and action", required: false
        input name: "cmd6", type: "text", title: "Command 6", description: "Enter Location device and action", required: false
        input name: "cmd7", type: "text", title: "Command 7", description: "Enter Location device and action", required: false
        input name: "cmd8", type: "text", title: "Command 8", description: "Enter Location device and action", required: false
        input name: "cmd9", type: "text", title: "Command 9", description: "Enter Location device and action", required: false
   }

	simulator {
	}

	tiles {
	  standardTile("switchTile", "device.switch", width: 1, height: 1, canChangeIcon: true) {
        state "go", label: '${name}', action: "push", icon: "st.Electronics.electronics13", backgroundColor: "#0090D0"
	  }

      main "switchTile"
      details(["switchTile"])
	}

}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"

}

/**
 * Be a switch also so this can appear in the Routines
 */
def on() {
  push();
}

def off() {
}

// handle commands
def push() {
 def c2 = [];
  if (cmd1 != null) c2.add(tocommand(cmd1))
  if (cmd2 != null) c2.add(tocommand(cmd2))
  if (cmd3 != null) c2.add(tocommand(cmd3))
  if (cmd4 != null) c2.add(tocommand(cmd4))
  if (cmd5 != null) c2.add(tocommand(cmd5))
  if (cmd6 != null) c2.add(tocommand(cmd6))
  if (cmd7 != null) c2.add(tocommand(cmd7))
  if (cmd8 != null) c2.add(tocommand(cmd8))
  if (cmd9 != null) c2.add(tocommand(cmd9))

  def json = new groovy.json.JsonBuilder()
  json commands: c2
  log.debug("IR: send event " + json)
  sendEvent(name: "irCommand", value: json, isStateChange: true)
}

def tocommand(s) {
  def locdevaction = s.tokenize(':')
  return [location: locdevaction[0], device: locdevaction[1], action: locdevaction[2]]
}
