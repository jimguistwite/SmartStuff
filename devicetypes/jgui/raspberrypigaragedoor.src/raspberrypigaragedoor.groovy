/**
 *  RaspberryPiGarageDoor
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
	definition (name: "RaspberryPiGarageDoor", namespace: "jgui", author: "James Guistwite") {
		capability "Garage Door Control"
		capability "Polling"
		capability "Refresh"
        capability "Contact Sensor"

        command "setIoState"
        command "actuate"
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
    	standardTile("switchTile", "device.switch", width: 1, height: 1, canChangeIcon: true) {
    		state "doorClosed", label: "Closed", action: "open", icon: "st.doors.garage.garage-closed", backgroundColor: "#79b821"
            state "doorOpen", label: "Open", action: "close", icon: "st.doors.garage.garage-open", backgroundColor: "#ffa81e"
		}
      standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
			state("default", label:'refresh', action:"polling.poll", icon:"st.secondary.refresh-icon")
        }
	
        main "switchTile"
        details(["switchTile","refresh"])
	}
}

// this should be a refresh response or actuate response
def parse(String message) {
}

def setIoState(String state) {
  log.debug "GD: set state ${state}"
  if ("LOW" == state) {
     sendEvent(name: "switch", value: "doorOpen", isStateChange:true)
  }
  else if ("HIGH" == state) {
     sendEvent(name: "switch", value: "doorClosed", isStateChange:true)
  } 
}


def actuate() {
  def pin = device.deviceNetworkId + ".relay"
  def json = "{\"command\":\"toggle\",\"pinName\":\"${pin}\"}"
  log.debug "gpio send json refresh request ${json}"
  sendEvent(name: "garageDoorCommand", value: json, isStateChange: true)
}

// handle commands
def open() {
  actuate();
}

def close() {
  actuate();
}

def poll() {
  refresh();
}

def refresh() {
  def pin = device.deviceNetworkId + ".sensor"
  def json = "{\"command\":\"refresh\",\"pinName\":\"${pin}\"}"
  log.debug "gpio send json refresh request ${json}"
  sendEvent(name: "garageDoorCommand", value: json, isStateChange: true)
}