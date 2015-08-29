/**
 *  Porch Light
 *
 *  Copyright 2015 Joseph DiBenedetto
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
 
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

definition(
	name: "Smart Porch Light",
	namespace: "jdiben",
	author: "Joseph DiBenedetto",
	description: "Turn on your porch light, dimmed, at sunset and increase to full brightness when someone arrives or some other action is triggered. After a number of minutes the light will dim back to its original level. Optionally, set the light to turn off at a specified time while still turning on when someone arrives. This app runs from sunset to sunrise.\n\nSelect as many lights as you like. Additional triggers include motion detected, door knock, door open and app button. A door bell trigger will also be added in the future.\n\nOnly dimmable switches will work with this app.",
	category: "Convenience",
	iconUrl: "http://apps.shiftedpixel.com/porchlight/porchlight.png",
	iconX2Url: "http://apps.shiftedpixel.com/porchlight/porchlight@2x.png",
	iconX3Url: "http://apps.shiftedpixel.com/porchlight/porchlight@2x.png"
)

preferences {
	page(name: "mainSettingsPage")
    page(name: "scheduleSettingsPage")
}

def mainSettingsPage() {
	dynamicPage(
    	name: 		"mainSettingsPage", 
        title: 		"", 
        install: 	true, 
        uninstall: 	true
    ) {
    
    	//Lights/switches to use. Only allow dimmable lights
        section("Control these switches") {
            input (
                name:		"switches", 
                type:		"capability.switchLevel", 
                title: 		"Switches?", 
                required: 	true, 
                multiple: 	true
            )
        }

		//Presence Sensors (including phones)
        section("When these people arrive") {
            input (
                name:		"presence", 
                type:		"capability.presenceSensor", 
                title:		"Who?", 
                required:	false,
                multiple: 	true
            )
                    
            input (
            	name:			"brightnessLevelPresence", 
                type:			"number", 
                title: 			"Brightness Level (1-100)?", 
                required:		false, 
                defaultValue:	100
            )
        }

		//Motion sensors
        section("When motion is detected") {
            input (
            	name:		"motion", 
                type:		"capability.motionSensor", 
                title:		"Which?", 
                required: 	false, 
                multiple: 	true
            )
                    
            input (
            	name:			"brightnessLevelMotion", 
                type:			"number", 
                title: 			"Brightness Level (1-100)?", 
                required: 		false, 
                defaultValue:	100
            )
        }

		//Contact sensors
        section("When these doors are opened") {
            input (
            	name:		"contact", 
                type:		"capability.contactSensor", 
                title:		"Which?", 
                required: 	false, 
                multiple: 	true
            )
                    
            input (
            	name:			"brightnessLevelContact", 
                type:			"number", 
                title: 			"Brightness Level (1-100)?", 
                required: 		false, 
                defaultValue:	100
            )
        }

		//Vibration sensors to detect a knock
        section("When someone knocks on these doors") {
            input (
            	name:		"acceleration", 
                type:		"capability.accelerationSensor", 
                title:		"Which?", 
                required: 	false, 
                multiple: 	true
            )
                    
            input (
            	name:			"brightnessLevelAcceleration", 
                type:			"number", 
                title: 			"Brightness Level (1-100)?", 
                required: 		false, 
                defaultValue:	100
            )
        }

		//Enable a button overlay on the app icon to trigger lights
        section("When the app button is tapped") {
            input (
            	name:			"appButton", 
                type:			"bool", 
                title: 			"Tap to brighten lights?", 
                defaultValue: 	true
            )
                    
            input (
            	name:			"brightnessLevelTap", 
                type:			"number", 
                title: 			"Brightness Level (1-100)?", 
                required: 		false, 
                defaultValue:	100
            )
        }

		//Minutes after event is detected before lights are set to their standby levels
        section("Dim after") {
        	paragraph	"The number of minutes after an event is triggered before the lights are dimmed."
            input (
            	name:			"autoOffMinutes", 
                type:			"number", 
                title: 			"Minutes (0 - 30)", 
                required: 		false, 
                defaultValue:	5
            )
        }

        section("Standby Light Brightness") {
        	paragraph	"The brightness level that the lights will be set to at sunset and whenever an event times out."
            input (
            	name:			"brightnessLevelDefault", 
                type:			"number", 
                title: 			"Brightness Level (1-100)?", 
                required:		false, 
                defaultValue:	10
            )
        }

		//Open the scheduling page
        section {        
            href(
            	title: 			"Schedule",
                name: 			"toScheduleSettingsPage", 
                page: 			"scheduleSettingsPage", 
                description:	readableSchedule(), //Display a more readable schedule description
                state: 			"complete"
            )
        }

		//Enable certain events to output to hello home
        section("Use Hello Home") {
            input (
            	name:			"useHelloHome", 
                type:			"bool", 
                title: 			"Output events to Hello Home?", 
                defaultValue: 	true
            )
        }
    }
}
 
def scheduleSettingsPage() {
    dynamicPage(
    	name: 		"scheduleSettingsPage", 
    	install: 	false, 
        uninstall: 	false,
        nextPage: 	"mainSettingsPage"
    ) {
    	section("Schedule") {
        	paragraph	"By default, the app runs from sunset to sunrise. You can offset both sunset and sunrise by up to +/- 2 hours"
                        
            input (
            	name:			"sunsetOffset", 
                type:			"enum", 
                title: 			"Sunset Offset in minutes?", 
                options: 		['-120', '-90', '-60', '-30', '0', '30', '60', '90', '120'],
                defaultValue: 	"0"
            )
                        
            input (
            	name:			"sunriseOffset", 
                type:			"enum", 
                title: 			"Sunrise Offset in minutes?", 
                options: 		['-120', '-90', '-60', '-30', '0', '30', '60', '90', '120'],
                defaultValue: 	"0"
            )
        }

        section("Lights off override") {
        	paragraph	"By default, the lights will turn off at sunrise when the app goes to sleep. Here, you can override the time that those lights are turned off. This will cause the light to turn off when no one is around instead of just dimming. The lights will still come on when someone arrives until sunrise. Leave the time blank to keep the lights on until sunrise."
            input 		name:			"timeDefaultEnd", 
            			type:			"time", 
                        title: 			"Turn off at", 
                        required: 		false,
                        defaultValue:	null
        }
    }
}

def installed() {
	debug("Installed with settings: ${settings}")
	initialize()
}

def updated() {
	debug("Updated with settings: ${settings}")

	unsubscribe()
	initialize()
}

def initialize() {

	//Enable app on install. This should be updated to only enable the app on install after sunset.
	state.inDefault = true
	state.enabled = true
	lightReset()
	
    //Enable sunset and sunrise
	subscribe(location, "sunsetTime", sunsetTimeHandler)
	subscribe(location, "sunriseTime", sunriseTimeHandler)
	
	sunsetScheduler(location.currentValue("sunsetTime"))
	sunriseScheduler(location.currentValue("sunriseTime"))
	
    //Override the sunrise turn off if specified. We don't need to do this if the standby brightness is set to 0. In that case the lights will be turn off instead of dimmed so there is no need to schedule it
	if (brightnessLevelDefault != null && timeDefaultEnd != null) {
		schedule(timeDefaultEnd, timeOff)
		debug('Off is scheduled')
	}
    
    //Enable events 
	if (presence != null) subscribe(presence, "presence", presenceHandler)
	if (motion != null) subscribe(motion, "motion", motionHandler)
	if (contact != null) subscribe(contact, "contact", contactHandler)
	if (acceleration != null) subscribe(acceleration, "acceleration.active", accelerationHandler)
	if (appButton) subscribe(app, appTouchHandler)
}

def timeOff() {
	defaultOff()
    if (useHelloHome) sendNotificationEvent("Smart Porch Light has turned off your light" + plural(switches.size()) + " as scheduled.")
}

def sunsetTimeHandler(evt) {
	sunsetScheduler(evt.value)
}

def sunriseTimeHandler(evt) {
	sunriseScheduler(evt.value)
}

def sunriseScheduler(sunriseString) {
	def sunriseTime = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", sunriseString)
	
    def offset=sunriseOffset
    if (offset == null) offset = 0
	def timeBeforeSunrise = new Date(sunriseTime.time + (offset.toInteger() * 60 * 1000))
	
	debug("Scheduling sunrise for: $timeBeforeSunrise (sunrise is $sunriseTime)")
	runOnce(timeBeforeSunrise, sunriseHandler)
}

def sunsetScheduler(sunsetString) {
	def sunsetTime = Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", sunsetString)
	
    def offset=sunsetOffset
    if (offset == null) offset = 0
	def timeBeforeSunset = new Date(sunsetTime.time + (offset.toInteger() * 60 * 1000))
	
	debug("Scheduling sunset for: $timeBeforeSunset (sunset is $sunsetTime)")
	
	runOnce(timeBeforeSunset, sunsetHandler)
}

def sunsetHandler() {	
	debug('Sunset')

	state.enabled = true
	
	def output = "Smart Porch Light is now active"

	if (brightnessLevelDefault != null && brightnessLevelDefault > 0) {

		lightSet(brightnessLevelDefault)
		
		output = output + " and has turned your light" + plural(switches.size()) + " on to " + brightnessLevelDefault + "%"

	}
	
	if (useHelloHome) sendNotificationEvent(output + ".")
}

def sunriseHandler() {
	state.enabled = false
	defaultOff()
	debug('Sunrise')
	
	if (timeDefaultEnd == null) {
		if (useHelloHome) sendNotificationEvent("It's sunrise. Smart Porch Light has turned off your light" + plural(switches.size()) + ".")
	}
}

def presenceHandler(evt) {

	if(evt.value == "present" && state.enabled) {
		lightSet(brightnessLevelPresence)
	
		scheduleAutoOff()
		
		if (useHelloHome) sendNotificationEvent(evt.displayName + " arrived. Smart Porch Light has turned your light" + plural(switches.size()) + " on to " + brightnessLevelPresence + "%.")
		
	} else {
		debug('Someone left - take no action')
	}
}

def motionHandler(evt) {
	if (evt.value == "active") {
		lightSet(brightnessLevelMotion)
	
		scheduleAutoOff()
		
		if (useHelloHome) sendNotificationEvent(evt.displayName + " detected motion. Smart Porch Light has turned your light" + plural(switches.size()) + " on to " + brightnessLevelMotion + "%.")
	} 
}

def contactHandler(evt) {
	if (evt.value == "open") {
        def reset=100
        if (brightnessLevelContact != null) reset = brightnessLevelContact
        lightSet(reset)
        scheduleAutoOff()
        
        if (useHelloHome) sendNotificationEvent(evt.displayName + " opened.  Smart Porch Light has turned your light" + plural(switches.size()) + " on to " + brightnessLevelContact + "%.")
    }
}

def accelerationHandler() {
	def reset=100
    if (brightnessLevelAcceleration != null) reset = brightnessLevelAcceleration
    lightSet(reset)
    scheduleAutoOff()
        
    if (useHelloHome) sendNotificationEvent("Someone knocked on " + evt.displayName + ".  Smart Porch Light has turned your light" + plural(switches.size()) + " on to " + brightnessLevelAcceleration + "%.")
}

def appTouchHandler(evt) {
	def reset=100
    if (brightnessLevelTap != null) reset = brightnessLevelTap
    lightSet(reset)
    scheduleAutoOff()
}

def scheduleAutoOff() {
	//Schedule lights to dim after x minutes.
    //This is executed after every event and is reset to x minutes on all subsequent events if this schedule hasn't yet run.
	if (autoOffMinutes != null) {
		debug('Auto off is scheduled for ' + autoOffMinutes + ' minutes')
        
        //Make sure that a valid number was specified. Adjust number if needed
		if (autoOffMinutes < 1) {
			autoOffMinutes = 1
		} else if (autoOffMinutes > 30) {
			autoOffMinutes = 30
		}
		runIn(60 * autoOffMinutes, autoOff)
	}
}

def autoOff() {
	//Reset lights to default level
	lightReset()
	if (useHelloHome) {
		def output = "It's been "+ autoOffMinutes + " minute" + plural(autoOffMinutes) + " since the light" + plural(switches.size()) + " were turned on. "
		if (state.inDefault) {
			output += "Resetting your lights to " + brightnessLevelDefault + "%."
		} else {
			output += "Turning your lights off."
		}
		sendNotificationEvent(output)
	}
}

def lightSet(level) {
	//Don't allow values above 100% for brightness
	if (level > 100) {
    	level = 100
    } else if (level == null) {
    	level = 0
    }
    
    //Set lights to specified level
	switches.setLevel(level)
	debug('brightness set to ' + level)
}

def lightReset() {

	//set default "reset" to 0% brightness
	def reset = 0
    //If brightness level is set, use that instead of the default set above
	if (state.inDefault && brightnessLevelDefault != null) reset = brightnessLevelDefault
    
    //If the lights are set to dim, set light level to lights. If they're set to turn off, send "off" command.
    //This probably isn't necessary. Need to confirm setting light level to "0" is the same as turning off.
	if (state.inDefault) {
		debug('Auto off executed - reset to default level')
		lightSet(reset)
	} else {
		switches.off()
		debug('Auto off executed - turn off')
	}
	debug('reset lights')
}

def defaultOn() {
	//Enables app at sunset and turns lights on to default level
	state.inDefault = true
	lightReset()
	debug('Default - schedule started')
}

def defaultOff() {
	//Disable app at sunrise or when scheduled and turn lights off
	state.inDefault = false
	switches.off()

	debug('Default - schedule ended')
}

private readableSchedule() {

	//Create a more readable schedule description to display on the main settings page when setting on the schedule page are modified.

	def sunrise = (sunriseOffset == null) ? 0 : sunriseOffset.toInteger()
	def sunset = (sunsetOffset == null) ? 0 : sunsetOffset.toInteger()
    
    def output = "Active from\n"
    
    if (sunset != null && sunset !=0) output += sunset.abs() + " minutes" + ((sunset > 0) ? " after " : " before ")
    output += "sunset to"
    
    if (sunrise != null && sunrise !=0) output += " " + sunrise.abs() + " minutes" + ((sunrise > 0) ? " after" : " before")
    output += " sunrise."
        
    if (timeDefaultEnd != null) {
    	output += "\n\nStandby light" + plural(switches.size()) + " turn" + plural(switches.size(), true) + " off at "
       
        def outputFormat = new SimpleDateFormat("h:mm a")
        def inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")

        def date = inputFormat.parse(timeDefaultEnd)
        output += outputFormat.format(date)
    }

	return output
}

private plural(count, r = false) {
	//return an "s" to append to a word if "count" indicates zero or more than one
    //Is this really necessary? No, but it makes me happy.
	if ((count == 1 && !r) || (count != 1 && r)) {
    	return ''
    } else {
    	return 's'
    }
}

private debug(debugString) {
	//Enable debugging. Comment out line below to disable output.
	log.debug(debugString)
}

