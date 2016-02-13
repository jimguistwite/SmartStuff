/**
 *  RaspberryPiX10DeviceHandler
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
	definition (name: "RaspberryPiX10DeviceHandler", namespace: "jgui", author: "James Guistwite") {
		capability "Polling"
		capability "Refresh"
		capability "Switch"

        command "setX10State"
	}

	simulator {
		// TODO: define status and reply messages here
	}

    preferences {
    }

	tiles {
    	standardTile("switchTile", "device.switch", width: 1, height: 1,
    	         canChangeIcon: true) {
    		state "off", label: '${name}', action: "switch.on",
   		       icon: "st.switches.switch.off", backgroundColor: "#ffffff"
  		  state "on", label: '${name}', action: "switch.off",
      		    icon: "st.switches.switch.on", backgroundColor: "#0090D0"
		}
      standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
			state("default", label:'refresh', action:"polling.poll", icon:"st.secondary.refresh-icon")
        }
	
        main "switchTile"
        details(["switchTile","refresh"])
	}
}

// parse events into attributes
def parse(String message) {
}

def getHouseCodeAndUnit() {
  def items = device.deviceNetworkId.tokenize('.')
  return items.get(items.size - 1)
}

def refresh() {
  def hcu = getHouseCodeAndUnit()
  def json = "{\"command\":\"refresh\",\"houseCodeUnit\":\"${hcu}\"}"
  //log.debug "x10dh send json refresh request ${json}"
  sendEvent(name: "x10command", value: json)
}

def on() {
  def hcu = getHouseCodeAndUnit()
  def json = "{\"command\":\"update\",\"houseCodeUnit\":\"${hcu}\",\"state\":\"on\"}"
  //log.debug "x10dh send json update ${json}"
  sendEvent(name: "x10command", value: json)
  sendEvent(name: "switch", value:"on")
}
  
def off() {
  def hcu = getHouseCodeAndUnit()
  def json = "{\"command\":\"update\",\"houseCodeUnit\":\"${hcu}\",\"state\":\"off\"}"
  //log.debug "x10dh send json update ${json}"
  sendEvent(name: "x10command", value: json)
  sendEvent(name: "switch", value:"off")
}

/**
 * Locally set the state of an X10 switch in the smartthings device state.
 */
def setX10State(onoff) {
  def lastState = device.currentValue("switch")
  //log.debug "state was ${lastState} and will be ${onoff}"
  sendEvent(name: "switch", value: onoff)
}

def updated() {
  log.info "${device.displayName} has device id ${device.deviceNetworkId}"
}

def poll() {
  refresh();
}
