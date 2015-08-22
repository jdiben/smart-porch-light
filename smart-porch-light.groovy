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
definition(
    name: "Smart Porch Light",
    namespace: "jdiben",
    author: "Joseph DiBenedetto",
    description: "Turn on your porch light, dimmed, at sunset and increase to full brightness when someone arrives. After a number of minutes, dim the light back to its original level. Optionally, set the light to turn off at a specified time while still turning on when someone arrives. This app runs from sunset to sunrise.",
    category: "Convenience",
	iconUrl: "http://apps.shiftedpixel.com/porchlight/porchlight.png",
	iconX2Url: "http://apps.shiftedpixel.com/porchlight/porchlight@2x.png",
	iconX3Url: "http://apps.shiftedpixel.com/porchlight/porchlight@2x.png"
)


preferences {
	section("Control these switches...") {
		input "switches", "capability.switchLevel", title: "Switches?", required: true, multiple: true;
		input "brightnessLevelPresence", "number", title: "Brightness Level (1-100)?", required:false, defaultValue:100 //Select brightness
	}
	
	section("When these people arrive") {
		input "presence", "capability.presenceSensor", title:"Who?", multiple: true;
	}
	
	section("Turn off after") {
		input "autoOffMinutes", "number", title: "Minutes", required: false, defaultValue:5;
	}
	
	section("Default") {
		input "brightnessLevelDefault", "number", title: "Brightness Level (1-100)?", required:false, defaultValue:10 //Select brightness
		input "timeDefaultEnd", "time", title: "Turn Off", required: false, description: "Leave blank to turn off at sunrise";
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}";
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	state.inDefault = true;
	state.enabled = true;
	
	lightReset();
	
	log.debug('inDefault: ' + state.inDefault);
	log.debug('enabled: ' + state.enabled);
	
	subscribe(location, "sunset", sunsetHandler);
	subscribe(location, "sunrise", sunriseHandler);
	
	if (brightnessLevelDefault != null && timeDefaultEnd != null) {
		schedule(timeDefaultEnd, defaultOff);
		log.debug('Off is scheduled');
	}
	
	subscribe(presence, "presence", presenceHandler);
}

def sunriseHandler() {
	state.enabled = false;
	defaultOff();
	log.debug('Sunrise');
}

def sunsetHandler() {
	state.enabled = true;
	log.debug('Sunset');
	if (brightnessLevelDefault != null) {
		lightSet(brightnessLevelDefault);
	}
}

def presenceHandler(evt) {
	
	log.debug('inDefault: ' + state.inDefault);
	log.debug('enabled: ' + state.enabled);
	
	if(evt.value == "present" && state.enabled) {
		log.debug('Someone arrived - turning light on');
		lightSet(brightnessLevelPresence);
	
		if (autoOffMinutes != null) {
			log.debug('Auto off is scheduled for ' + autoOffMinutes + ' minutes');
			if (autoOffMinutes < 1) {
				autoOffMinutes = 1;
			} else if (autoOffMinutes > 60) {
				autoOffMinutes = 60;
			}
			runIn(60 * autoOffMinutes, lightReset);
		}
	} else {
		log.debug('Someone left - take no action');
	}
}

def lightSet(level) {
	if (level > 100) level = 100;
	switches.setLevel(level);
	log.debug('brightness set to ' + level);
}

def lightReset() {
	def reset = 0;
	if (state.inDefault && brightnessLevelDefault != null) reset = brightnessLevelDefault;
	if (state.inDefault) {
		log.debug('Auto off executed - reset to default level');
		lightSet(reset);
	} else {
		switches.off();
		log.debug('Auto off executed - turn off');
	}
	log.debug('reset lights');
}

def defaultOn() {
	state.inDefault = true;
	lightReset();
	log.debug('Default - schedule started');
}

def defaultOff() {
	state.inDefault = false;
	switches.off();
	log.debug('Default - schedule ended');
}
