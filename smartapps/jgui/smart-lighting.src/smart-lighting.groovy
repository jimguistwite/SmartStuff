/**
 *  Smart Lighting
 *
 *  Copyright 2016 James Guistwite
 *
 */
definition(
    name: "Smart Lighting",
    namespace: "jgui",
    author: "James Guistwite",
    description: "My Smart Lighting Solution",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    section("Choose Lights:") {
        input "lights", "capability.switch", required: true, title: "Lights", multiple:true
    }
	section("And turn them off at...") {
		input name: "stopTime", title: "Turn Off Time?", type: "time"
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"
    subscribe(location, "sunset", sunsetHandler)
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def stopTimerCallback() {
	log.debug "Turning off switches"
	lights.off()
}

def initialize() {
	schedule(stopTime, "stopTimerCallback")
}

def sunsetHandler(evt) {
  log.debug("it is sunset.  turn on lights")
  lights.on();
}