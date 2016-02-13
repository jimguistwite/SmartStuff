/**
 *  MyHomeAdapter
 *
 *  Copyright 2016 James Guistwite
 *
 */

import groovy.json.JsonSlurper

metadata {
	definition (name: "MyHomeAdapter", namespace: "jgui", author: "James Guistwite") {
		capability "Polling"
		capability "Refresh"
		capability "Switch"
        attribute "x10code", "string"
        command "getclock"
	}

    preferences {
       input "deviceId", "number", title: "X10 Device ID",
              description: "Device ID of X10 Device.", defaultValue: 651,
              required: true, displayDuringSetup: true
    }


	simulator {
	}



	tiles {
        standardTile("switchTile", "device.switch", width: 2, height: 2,
                     canChangeIcon: true) {
            state "off", label: '${name}', action: "switch.on",
              icon: "st.switches.switch.off", backgroundColor: "#ffffff"
            state "on", label: '${name}', action: "switch.off",
              icon: "st.switches.switch.on", backgroundColor: "#E60000"
        }
        valueTile("Clock", "device.clock", decoration: "flat", canChangeIcon: true) {
          state "clock", label:'${currentValue}', action:"getclock"
        }
	}
    
    main "switchTile"
    details(["switchTile","Clock"])
}


def updated() {
    log.info "MyHome. ${textVersion()}. ${textCopyright()}"
	log.info "$device.displayName updated with settings: ${settings.inspect()}"
}

// parse events into attributes
def parse(String message) {
   def msg = parseLanMessage(message)
    log.debug "headers ${msg.headers}"
        def slurper = new JsonSlurper()
		def result = slurper.parseText(msg.body)
        log.debug "timestamp is " + result.timestamp
        def ev = [
            name:   "clock",
            value:  result.timestamp
        ]
        return createEvent(ev)

}

private parseHttpHeaders(String headers) {
    def lines = headers.readLines()
    def status = lines.remove(0).split()

    def result = [
        protocol:   status[0],
        status:     status[1].toInteger(),
        reason:     status[2]
    ]

    return result
}

private getCallBackAddress() {
 	device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")
}

def getclock() { 
    log.debug "Preference Value " + settings.deviceId
    log.debug "API Server URL " + getApiServerUrl()
    log.debug "Callback address " + getCallBackAddress()
	log.debug "Executing 'getclock'"
    new physicalgraph.device.HubAction([
        method: "GET",
        path: "/myhome/clock",
        headers: [
        	Accept: "application/json",
            HOST: "10.0.0.215:9000",
        ]])
}

// handle commands
def poll() {
	log.debug "Executing 'poll'"
	// TODO: handle 'poll' command
}

def refresh() {
	log.debug "Executing 'refresh'"
	// TODO: handle 'refresh' command
}

def on() {
	log.debug "Executing 'on'"
	// TODO: handle 'on' command
}

def off() {
	log.debug "Executing 'off'"
	// TODO: handle 'off' command
}

private def textVersion() {
    return "Version 0.0.1 (01/30/2016)"
}

private def textCopyright() {
    return "Copyright (c) 2016 JimGuistwite.com"
}
