
var objUAPlugin = {
	

	AddUser: function(){
		
		this.OpenPreferences();
	
	},

	EditUser: function(){
		this.OpenPreferences();
	},
	
	GetStatus: function(){

		return "UAPlugin OK";
  	}, 

	Populate: function(){

		var prefManager = Components.classes["@mozilla.org/preferences-service;1"]
                                .getService(Components.interfaces.nsIPrefBranch);

		var prefString = prefManager.getCharPref("extensions.uaplugin.activeID");
		
		if(prefString == "")
			return;

	 	// Get the menupopup element that we will be working with
        var menu = document.getElementById("UAPlugin-ActiveUserMenu");
    
        // Remove all of the items currently in the popup menu
        for(var i=menu.childNodes.length - 1; i >= 0; i--)
        {
            menu.removeChild(menu.childNodes.item(i));
        }
    
		
		var items = prefString.split("<NEXT>");

		var numItemsToAdd = items.length;

		for(var i=0; i<numItemsToAdd; i++)
        {
            // Create a new menu item to be added
            var tempItem = document.createElement("menuitem");
    
            // Set the new menu item's label
            tempItem.setAttribute("label", items[i]);
    
            // Add the item to our menu
            menu.appendChild(tempItem);
        }

	},

	GetDefaultID: function(){

        var prefManager = Components.classes["@mozilla.org/preferences-service;1"]
                                .getService(Components.interfaces.nsIPrefBranch);

		var prefString = prefManager.getCharPref("extensions.uaplugin.activeID");

		return prefString;

	},
	
 	OpenPreferences : function() {
		if (null == this._preferencesWindow || this._preferencesWindow.closed) {
			let instantApply =
				Application.prefs.get("browser.preferences.instantApply");
			let features =
				"chrome,titlebar,toolbar,centerscreen" +
				(instantApply.value ? ",dialog=no" : ",modal");

			this._preferencesWindow =
				window.openDialog(
					"chrome://uaplugin/content/options.xul",
					"UAPlugin Preferences Window", features);
		}
		
		this._preferencesWindow.focus();
	},

	ToolbarSetup: function(){
		
		this.Populate();
	}

};

objUAPlugin.ToolbarSetup();

