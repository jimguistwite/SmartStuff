/**
 *  RaspberryPiTemperatureSensor
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
	definition (name: "RaspberryPiTemperatureSensor", namespace: "jgui", author: "James Guistwite") {
		capability "Polling"
		capability "Refresh"
		capability "Sensor"

        command "setTemp"
	}

	simulator {
	}

    preferences {
       input "sensorName", "string", title: "Sensor Name",
              description: "Name of Sensor.", defaultValue: "family",
              required: true, displayDuringSetup: true

       input "scale", "string", title: "Degree Scale",
              description: "Celsius or Fahrenheit", defaultValue: "celsius",
              type: "enum", options:"celsius,fahrenheit", required: true, displayDuringSetup: true
    }

	tiles {
        valueTile("Temp", "device.temperature", decoration: "flat", canChangeIcon: true) {
          state "temperature", label:'${currentValue}',
				backgroundColors:[
					[value: 31, color: "#153591"],
					[value: 44, color: "#1e9cbb"],
					[value: 59, color: "#90d2a7"],
					[value: 74, color: "#44b621"],
					[value: 84, color: "#f1d801"],
					[value: 95, color: "#d04e00"],
					[value: 96, color: "#bc2323"]
				]
        }
        standardTile("Refresh", "device.switch", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
            state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
        }
	}
    main "Temp"
    details(["Temp","Refresh"])
}

// parse events into attributes
def parse(String message) {
	
}

def setTemp(temp) {
  //log.debug "respTmp: setTemp from ${temp}"
   def data = new JsonSlurper().parseText(temp)
   if (settings.scale == "celsius") {
      sendEvent(name: "temperature", value:String.format("%3.1f\u00b0C", data.c))
   }
   else {
      sendEvent(name: "temperature", value:String.format("%3.1f\u00b0F", data.f))
   }
}

def poll() {
  refresh()
}


def refresh() {
  def json = "{\"command\":\"refresh\",\"sensorName\":\"${settings.sensorName}\"}"
  //log.debug "temperature send json refresh request ${json}"
  sendEvent(name: "tempSensorCommand", value: json, isStateChange: true)
}

def updated() {
    log.info "RaspberryPiTemperatureSensor. ${textVersion()}. ${textCopyright()}"
	log.info "$device.displayName updated with settings: ${settings.inspect()}"
}

private def textVersion() {
    return "Version 0.0.1 (01/30/2016)"
}

private def textCopyright() {
    return "Copyright (c) 2016 JimGuistwite.com"
}
