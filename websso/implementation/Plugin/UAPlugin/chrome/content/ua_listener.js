
if (typeof Cc == "undefined") {
	var Cc = Components.classes;
}
if (typeof Ci == "undefined") {
        var Ci = Components.interfaces;
}
if (typeof CCIN == "undefined") {
	function CCIN(cName, ifaceName){
		return Cc[cName].createInstance(Ci[ifaceName]);
	}
}
if (typeof CCSV == "undefined") {
	function CCSV(cName, ifaceName){
		if (Cc[cName])
			// if fbs fails to load, the error can be _CC[cName] has no properties
			return Cc[cName].getService(Ci[ifaceName]);
		else
			dumpError("CCSV fails for cName:" + cName);
	};
}

function TracingListener() {
}

TracingListener.prototype =
{
    originalListener: null,
    receivedData: null,   //will be an array for incoming data.

	 //For the listener this is step 1.
    onStartRequest: function(request, context) {
    	this.receivedData = []; //initialize the array

	//Pass on the onStartRequest call to the next listener in the chain -- VERY IMPORTANT
	this.originalListener.onStartRequest(request, context);
    },

	//This is step 2. This gets called every time additional data is available
    onDataAvailable: function(request, context, inputStream, offset, count)
    {

		var binaryInputStream = CCIN("@mozilla.org/binaryinputstream;1",
				"nsIBinaryInputStream");
		binaryInputStream.setInputStream(inputStream);

		var storageStream = CCIN("@mozilla.org/storagestream;1",
				"nsIStorageStream");
		//8192 is the segment size in bytes, count is the maximum size of the stream in bytes
		storageStream.init(8192, count, null); 

		var binaryOutputStream = CCIN("@mozilla.org/binaryoutputstream;1",
				"nsIBinaryOutputStream");
		binaryOutputStream.setOutputStream(storageStream.getOutputStream(0));

		// Copy received data as they come.
		var data = binaryInputStream.readBytes(count);

		this.receivedData.push(data);

		binaryOutputStream.writeBytes(data, count);

		//Pass it on down the chain
		this.originalListener.onDataAvailable(request,
				context,
				storageStream.newInputStream(0),
				offset,
				count);
    },

	onStopRequest: function(request, context, statusCode)
	{
		var responseSource = this.receivedData.join();

		//alert("Date: "+ request.getResponseHeader("Date")); 
		try{
			var authString = request.getResponseHeader("WWW-Authenticate");
		}catch(err){
		}
		if(authString && authString.indexOf("OpenID:session") != -1){
			var prefManager = Components.classes["@mozilla.org/preferences-service;1"]
				.getService(Components.interfaces.nsIPrefBranch);

			var activeID = prefManager.getCharPref("extensions.uaplugin.activeID");
			var activeSessionID =  prefManager.getCharPref("extensions.uaplugin.activeSessionID");  

			alert("Found request for OpenID authentication");
			//alert("Value: "+request.getRequestHeader("Request-URI"));
            alert(""+request.name);

			var newreq = new XMLHttpRequest();
			newreq.open('GET', request.name, true);
			newreq.setRequestHeader("Authorization", "OpenID:session " +
				"user-id=\"" + activeID + "\", session-id=\"" + activeSessionID + "\"");	
			newreq.onreadystatechange = function ( ) {
				ChallengeResponse(newreq);
			};

			newreq.send(null);

		
		}

		//Pass it on down the chain
		this.originalListener.onStopRequest(request,
				context,
				statusCode);
    },

	QueryInterface: function (aIID) {
        if (aIID.equals(Ci.nsIStreamListener) ||
            aIID.equals(Ci.nsISupports)) {
            return this;
        }
        throw Components.results.NS_NOINTERFACE;
    },

    readPostTextFromRequest : function(request, context) {
        try
        {
	        var is = request.QueryInterface(Ci.nsIUploadChannel).uploadStream;
	        if (is)
	        {
	            var ss = is.QueryInterface(Ci.nsISeekableStream);
	            var prevOffset;
	            if (ss)
	            {
	                prevOffset = ss.tell();
	                ss.seek(Ci.nsISeekableStream.NS_SEEK_SET, 0);
	            }

	            // Read data from the stream..
		    var charset = "UTF-8";
		    var text = this.readFromStream(is, charset, true);

	            if (ss && prevOffset == 0)
	                ss.seek(Ci.nsISeekableStream.NS_SEEK_SET, 0);

	            return text;
	        }
		else {
			dump("Failed to Query Interface for upload stream.\n");
		}
	    }
	    catch(exc)
	    {
			dumpError(exc);
	    }

	    return null;
	},

	readFromStream : function(stream, charset, noClose)	{

	    var sis = CCSV("@mozilla.org/binaryinputstream;1",
                            "nsIBinaryInputStream");
	    sis.setInputStream(stream);

	    var segments = [];
	    for (var count = stream.available(); count; count = stream.available())
	        segments.push(sis.readBytes(count));

	    if (!noClose)
	        sis.close();

	    var text = segments.join("");
	    return text;
	},

	HandleR1 : function(){

	}

}

httpRequestObserver = {

	observe: function(request, aTopic, aData){
		if (typeof Cc == "undefined") {
			var Cc = Components.classes;
		}
		if (typeof Ci == "undefined") {
			var Ci = Components.interfaces;
		}
	    if (aTopic == "http-on-examine-response") {
	    	request.QueryInterface(Ci.nsIHttpChannel);

			var newListener = new TracingListener();
			request.QueryInterface(Ci.nsITraceableChannel);
			newListener.originalListener = request.setNewListener(newListener);
		}
	},

	QueryInterface: function(aIID){
		if (typeof Cc == "undefined") {
			var Cc = Components.classes;
		}
		if (typeof Ci == "undefined") {
			var Ci = Components.interfaces;
		}
		if (aIID.equals(Ci.nsIObserver) ||
		aIID.equals(Ci.nsISupports)) {
			return this;
		}

		throw Components.results.NS_NOINTERFACE;

	},
};

function ChallengeResponse(req){
	if (req.readyState == 4) {
		if(req.status == 200){
			alert("GET OK");
			dump(req.responseText);
		}else{
			alert("REQ NOT OK");
			dump("Error loading page\n");
		}
	}       
}

var observerService = Cc["@mozilla.org/observer-service;1"]
    .getService(Ci.nsIObserverService);

observerService.addObserver(httpRequestObserver,
    "http-on-examine-response", false);
