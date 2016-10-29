/**
 *  Raspberry Pi
 *
 *  Copyright 2016 James Guistwite
 *
 */
 
import groovy.json.JsonSlurper
import groovy.json.JsonBuilder

metadata {
	definition (name: "Raspberry Pi", namespace: "jgui", author: "James Guistwite") {
		capability "Polling"
		capability "Refresh"
        capability "Temperature Measurement"

        command "x10Refresh"
        command "x10Update"
        command "temperatureSensorRefresh"
        command "refreshInputState"
        command "toggleRelay"
        command "irCommand"
	}

	simulator {
		// TODO: define status and reply messages here
	}

    preferences {
    input("confIpAddr", "string", title:"IP Address",
        required:true, displayDuringSetup: true)
    input("confTcpPort", "number", title:"TCP Port",
        required:true, displayDuringSetup:true)
    input("confMac", "string", title:"Mac Address (upper, nocolon)",
        required:true, displayDuringSetup: true)

    }

	tiles {
      valueTile("Last Update", "device.lastupdate", decoration: "flat", canChangeIcon: false) {
          state "lastupdate", label:'Updated:\n${currentValue}'
      }
      valueTile("CPU Temp", "device.temperature", decoration: "flat", canChangeIcon: true) {
          state "temperature", label:'CPU Temp:\n${currentValue}'
      }
      standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
			state("default", label:'refresh', action:"polling.poll", icon:"st.secondary.refresh-icon")
      }
	 
      main "refresh"
      details(["Last Update","CPU Temp","refresh"])
	}
}

// parse events into attributes
def parse(String message) {
  //log.debug("Pi: received message " + message)
  def msg = parseLanMessage(message)
   log.debug('Pi: parse ' + msg.body)
   def slurper = new JsonSlurper()
   def result = slurper.parseText(msg.body)
   def events = []
   if ((result instanceof Map) && (result.status == "success")) {
      def zone = TimeZone.getTimeZone("EST")
      events << createEvent(name:"lastupdate",value: new Date().format("MM/dd\nhh:mm:ss",zone))
      if (result.deviceconfig != null) {
         log.debug "Pi: received device config"
         events << createEvent(name: "config", value: result.deviceconfig)
      }
      if (result.sysinfo != null) {
         def temp = result.sysinfo.get("CPU Temperature")
         log.debug "Pi: temp " + temp
         events << createEvent(name: "temperature", value: temp)
      }
      if (result.event != null) {
         if ("x10" == result.event.eventtype) {
             log.debug "Pi: code and func " + result.event.code + " " + result.event.function
             events << createEvent(name: result.event.code, value: result.event.function, isStateChange:true)
         }
         else if ("gpio" == result.event.eventtype) {
             log.debug "Pi: pin and state " + result.event.pin + " " + result.event.state
             events << createEvent(name: result.event.pin, value: result.event.state)
         }
      }
      if (result.temperature != null) {
        if (result.temperature instanceof Map) {
           log.debug "Pi: temperature response " + result.temperature
           def s = "{\"c\":${result.temperature.c},\"f\":${result.temperature.f}}"
           events << createEvent(name: result.temperature.sensor, value: s, isStateChange:true)
        }
        else if (result.temperature instanceof List) {
           result.temperature.each {
             log.debug "Pi: temperature item " + it
             def s = "{\"c\":${it.c},\"f\":${it.f}}"
             events << createEvent(name: it.sensor, value: s, isStateChange:true)
          }
        }
      }
      if (result.x10state != null) {
           if (result.x10state instanceof Map) {
             log.debug "Pi: code and func " + result.x10state.code + " " + result.x10state.status
             events << createEvent(name: result.x10state.code, value: result.x10state.status, isStateChange:true)
          }
          else if (result.x10state instanceof List) {
              result.x10state.each {
                  log.debug "Pi: code and func " + it.code + " " + it.status
                  events << createEvent(name: it.code, value: it.status, isStateChange:true)
              }
          }
      }
       if (result.gpiostate != null) {
           if (result.gpiostate instanceof Map) {
             log.debug "Pi: gpio state " + result.gpiostate.name + " " + result.gpiostate.state
             events << createEvent(name: result.gpiostate.name, value: result.gpiostate.state)
          }
          else if (result.gpiostate instanceof List) {
              result.gpiostate.each {
                  log.debug "Pi: gpio state " + it.name + " " + it.state
                  events << createEvent(name: it.name, value: it.state)
              }
          }
      }
  }
   else {
     log.debug "Pi: not processing message ${msg.body}"
   }
   //log.debug "Pi: parse returning events ${events}"
   return events
}

def irCommand(json) {
  def hostAddress = "${settings.confIpAddr}:${settings.confTcpPort}"
  log.debug("Pi: process irCommand ${json}")
	log.debug "posting ${json} to ${hostAddress}"
	new physicalgraph.device.HubAction([
        method: "POST",
        path: "/sendir",
        body: json,
        headers: [
        	Accept: "application/json",
            'Content-type': 'application/json',
            HOST: hostAddress
        ]])
}

def x10Update(code,state) {
    updateDNI()
    def hostAddress = "${settings.confIpAddr}:${settings.confTcpPort}"
	def json = new JsonBuilder()
	json.call("function":state,"housecodeunit":code)
	log.debug "posting " + json.toString()
	new physicalgraph.device.HubAction([
        method: "POST",
        path: "/x10",
        body: json.toString(),
        headers: [
        	Accept: "application/json",
            'Content-type': 'application/json',
            HOST: hostAddress
        ]])
}

def temperatureSensorRefresh(sensorName) {
 updateDNI()
    log.debug "Pi: temperature refresh for ${device} ${code} to ${state.hostAddress}"
    def hostAddress = "${settings.confIpAddr}:${settings.confTcpPort}"

	new physicalgraph.device.HubAction([
        method: "GET",
        path: "/temp/" + sensorName,
        headers: [
        	Accept: "application/json",
            HOST: hostAddress
        ]])
}


def refreshInputState(sensorName) {
 updateDNI()
    def hostAddress = "${settings.confIpAddr}:${settings.confTcpPort}"

	new physicalgraph.device.HubAction([
        method: "GET",
        path: "/gpiostate/" + sensorName,
        headers: [
        	Accept: "application/json",
            HOST: hostAddress
        ]])
}

def toggleRelay(pin) {
    updateDNI()
    def hostAddress = "${settings.confIpAddr}:${settings.confTcpPort}"
	def json = "{\"pin\":\"${pin}\"}"
	log.debug "posting ${json}"
	new physicalgraph.device.HubAction([
        method: "POST",
        path: "/gpiotoggle",
        body: json,
        headers: [
        	Accept: "application/json",
            'Content-type': 'application/json',
            HOST: hostAddress
        ]])
}




def x10Refresh(code) {
    updateDNI()
    log.debug "Pi: x10 refresh for ${code} to ${state.hostAddress}"
    def hostAddress = "${settings.confIpAddr}:${settings.confTcpPort}"

	new physicalgraph.device.HubAction([
        method: "GET",
        path: "/x10/" + code,
        headers: [
        	Accept: "application/json",
            HOST: hostAddress
        ]])

}


def refresh() {
    updateDNI()
    def hostAddress = "${settings.confIpAddr}:${settings.confTcpPort}"

    log.debug "Pi: posting refresh to ${hostAddress}"

    def actions = []

	actions << new physicalgraph.device.HubAction([
        method: "GET",
        path: "/sysinfo",
        headers: [
        	Accept: "application/json",
            HOST: hostAddress,
        ]])
	actions << new physicalgraph.device.HubAction([
        method: "GET",
        path: "/x10",
        headers: [
        	Accept: "application/json",
            HOST: hostAddress,
        ]])
    actions << new physicalgraph.device.HubAction([
        method: "GET",
        path: "/temp",
        headers: [
        	Accept: "application/json",
            HOST: hostAddress,
        ]])
    actions << new physicalgraph.device.HubAction([
        method: "GET",
        path: "/gpiostate",
        headers: [
        	Accept: "application/json",
            HOST: hostAddress,
        ]])

    return actions
/*
  works via the cloud.  request goes out through smartthings...
  try {
        httpGet("http://10.0.0.244:9000/sysinfo") { resp ->
        log.debug "response data: ${resp.data}"
        log.debug "response contentType: ${resp.contentType}"
    }
    } catch (e) {
        log.debug "something went wrong: $e"
    }
*/
}



private updateDNI() { 
    if (device.deviceNetworkId != state.dni) {
        device.deviceNetworkId = state.dni
    }
}

def updated() {
    log.info "Raspberry Pi DeviceHandler. ${textVersion()}. ${textCopyright()}"
	log.info "$device.displayName updated with settings: ${settings.inspect()}"
    log.trace "deviceNetworkId: ${device.deviceNetworkId}"
    log.trace "callback address: ${getCallBackAddress()}"
    
    state.hostAddress = "${settings.confIpAddr}:${settings.confTcpPort}"
    state.dni = settings.confMac//createDNI(settings.confIpAddr,settings.confTcpPort)

}

private def textVersion() {
    return "Version 0.0.1 (01/30/2016)"
}

private def textCopyright() {
    return "Copyright (c) 2016 JimGuistwite.com"
}

private getCallBackAddress() {
 	device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")
}


// handle commands
def poll() {
  log.debug "Pi: Poll"
  refresh()
}

// Creates Device Network ID in 'AAAAAAAA:PPPP' format
private String createDNI(ipaddr,port) { 
    logger.debug "createDNI(${ipaddr}  ${port})"

    def hexIp = ipaddr.tokenize('.').collect {
        String.format('%02X', it.toInteger())
    }.join()

    def hexPort = String.format('%04X', port.toInteger())

    return "${hexIp}:${hexPort}"
}
