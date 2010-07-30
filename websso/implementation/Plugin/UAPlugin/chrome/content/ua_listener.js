
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
				SessionResponse(newreq);
			};

			newreq.send(null);

		
		}else if(authString && authString.indexOf("OpenID:challenge") != -1){
			var userID_regex = /user-id="([^"]*)"/;
			var sessionID_regex = /session-id="([^"]*)"/; 
			var challenge_regex = /challenge="([^"]*)"/; 
			var signed_regex = /signed="([^"]*)"/; 
			
			var mymatch = userID_regex.exec(authString);
            var userID = mymatch[1];
			
			mymatch = sessionID_regex.exec(authString);
            var sessionID = mymatch[1];

			var prefManager = Components.classes["@mozilla.org/preferences-service;1"]
				.getService(Components.interfaces.nsIPrefBranch);

			var activeID = prefManager.getCharPref("extensions.uaplugin.activeID");
			var activeSessionID =  prefManager.getCharPref("extensions.uaplugin.activeSessionID");  
			
			if(sessionID != activeSessionID || userID != activeID){
				alert("Error, OpenID:challenge not for active user, feature not yet supported");
			}else{

				mymatch = challenge_regex.exec(authString);
				var challenge = mymatch[1];

				mymatch = signed_regex.exec(authString);
				var signedString = mymatch[1];
				var signedArray = mymatch[1].split(/,/);

				var signedAlertString="";

				for(var i = 0; i < signedArray.length; i++){
					signedAlertString += "signed[" + i + "] = " + signedArray[i] + "\n";
				}

				alert("UserID: " + userID + "\nSessionID: " + sessionID + "\nChallenge: " + challenge + "\nSigned: " + signedString + "\n" + signedAlertString);

				var key = "batman";
				var toSign = "" + userID + sessionID + challenge + key;
				var signedVal = SHA256(toSign);

				alert("signature: " + signedVal);

				var newreq2 = new XMLHttpRequest();
				newreq2.open('GET', request.name, true);
				newreq2.setRequestHeader("Authorization", "OpenID:challenge " +
						"user-id=\"" + activeID + "\", session-id=\"" + sessionID + "\", challenge=\"" + challenge +"\", signature=\"" + signedVal+"\"");	
				newreq2.onreadystatechange = function ( ) {
					ChallengeResponse(newreq2);
				};  
			
				newreq2.send(null);
			}

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

function SessionResponse(req){
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

/**
 *
 *  Secure Hash Algorithm (SHA256)
 *  http://www.webtoolkit.info/
 *
 *  Original code by Angel Marin, Paul Johnston.
 *
 **/

function SHA256(s){

	var chrsz   = 8;
	var hexcase = 0;

	function safe_add (x, y) {
		var lsw = (x & 0xFFFF) + (y & 0xFFFF);
		var msw = (x >> 16) + (y >> 16) + (lsw >> 16);
		return (msw << 16) | (lsw & 0xFFFF);
	}

	function S (X, n) { return ( X >>> n ) | (X << (32 - n)); }
	function R (X, n) { return ( X >>> n ); }
	function Ch(x, y, z) { return ((x & y) ^ ((~x) & z)); }
	function Maj(x, y, z) { return ((x & y) ^ (x & z) ^ (y & z)); }
	function Sigma0256(x) { return (S(x, 2) ^ S(x, 13) ^ S(x, 22)); }
	function Sigma1256(x) { return (S(x, 6) ^ S(x, 11) ^ S(x, 25)); }
	function Gamma0256(x) { return (S(x, 7) ^ S(x, 18) ^ R(x, 3)); }
	function Gamma1256(x) { return (S(x, 17) ^ S(x, 19) ^ R(x, 10)); }

	function core_sha256 (m, l) {
		var K = new Array(0x428A2F98, 0x71374491, 0xB5C0FBCF, 0xE9B5DBA5, 0x3956C25B, 0x59F111F1, 0x923F82A4, 0xAB1C5ED5, 0xD807AA98, 0x12835B01, 0x243185BE, 0x550C7DC3, 0x72BE5D74, 0x80DEB1FE, 0x9BDC06A7, 0xC19BF174, 0xE49B69C1, 0xEFBE4786, 0xFC19DC6, 0x240CA1CC, 0x2DE92C6F, 0x4A7484AA, 0x5CB0A9DC, 0x76F988DA, 0x983E5152, 0xA831C66D, 0xB00327C8, 0xBF597FC7, 0xC6E00BF3, 0xD5A79147, 0x6CA6351, 0x14292967, 0x27B70A85, 0x2E1B2138, 0x4D2C6DFC, 0x53380D13, 0x650A7354, 0x766A0ABB, 0x81C2C92E, 0x92722C85, 0xA2BFE8A1, 0xA81A664B, 0xC24B8B70, 0xC76C51A3, 0xD192E819, 0xD6990624, 0xF40E3585, 0x106AA070, 0x19A4C116, 0x1E376C08, 0x2748774C, 0x34B0BCB5, 0x391C0CB3, 0x4ED8AA4A, 0x5B9CCA4F, 0x682E6FF3, 0x748F82EE, 0x78A5636F, 0x84C87814, 0x8CC70208, 0x90BEFFFA, 0xA4506CEB, 0xBEF9A3F7, 0xC67178F2);
		var HASH = new Array(0x6A09E667, 0xBB67AE85, 0x3C6EF372, 0xA54FF53A, 0x510E527F, 0x9B05688C, 0x1F83D9AB, 0x5BE0CD19);
		var W = new Array(64);
		var a, b, c, d, e, f, g, h, i, j;
		var T1, T2;

		m[l >> 5] |= 0x80 << (24 - l % 32);
		m[((l + 64 >> 9) << 4) + 15] = l;

		for ( var i = 0; i<m.length; i+=16 ) {
			a = HASH[0];
			b = HASH[1];
			c = HASH[2];
			d = HASH[3];
			e = HASH[4];
			f = HASH[5];
			g = HASH[6];
			h = HASH[7];

			for ( var j = 0; j<64; j++) {
				if (j < 16) W[j] = m[j + i];
				else W[j] = safe_add(safe_add(safe_add(Gamma1256(W[j - 2]), W[j - 7]), Gamma0256(W[j - 15])), W[j - 16]);

				T1 = safe_add(safe_add(safe_add(safe_add(h, Sigma1256(e)), Ch(e, f, g)), K[j]), W[j]);
				T2 = safe_add(Sigma0256(a), Maj(a, b, c));

				h = g;
				g = f;
				f = e;
				e = safe_add(d, T1);
				d = c;
				c = b;
				b = a;
				a = safe_add(T1, T2);
			}

			HASH[0] = safe_add(a, HASH[0]);
			HASH[1] = safe_add(b, HASH[1]);
			HASH[2] = safe_add(c, HASH[2]);
			HASH[3] = safe_add(d, HASH[3]);
			HASH[4] = safe_add(e, HASH[4]);
			HASH[5] = safe_add(f, HASH[5]);
			HASH[6] = safe_add(g, HASH[6]);
			HASH[7] = safe_add(h, HASH[7]);
		}
		return HASH;
	}

	function str2binb (str) {
		var bin = Array();
		var mask = (1 << chrsz) - 1;
		for(var i = 0; i < str.length * chrsz; i += chrsz) {
			bin[i>>5] |= (str.charCodeAt(i / chrsz) & mask) << (24 - i%32);
		}
		return bin;
	}

	function Utf8Encode(string) {
		string = string.replace(/\r\n/g,"\n");
		var utftext = "";

		for (var n = 0; n < string.length; n++) {

			var c = string.charCodeAt(n);

			if (c < 128) {
				utftext += String.fromCharCode(c);
			}
			else if((c > 127) && (c < 2048)) {
				utftext += String.fromCharCode((c >> 6) | 192);
				utftext += String.fromCharCode((c & 63) | 128);
			}
			else {
				utftext += String.fromCharCode((c >> 12) | 224);
				utftext += String.fromCharCode(((c >> 6) & 63) | 128);
				utftext += String.fromCharCode((c & 63) | 128);
			}

		}

		return utftext;
	}

	function binb2hex (binarray) {
		var hex_tab = hexcase ? "0123456789ABCDEF" : "0123456789abcdef";
		var str = "";
		for(var i = 0; i < binarray.length * 4; i++) {
			str += hex_tab.charAt((binarray[i>>2] >> ((3 - i%4)*8+4)) & 0xF) +
				hex_tab.charAt((binarray[i>>2] >> ((3 - i%4)*8  )) & 0xF);
		}
		return str;
	}

	s = Utf8Encode(s);
	return binb2hex(core_sha256(str2binb(s), s.length * chrsz));

}


var observerService = Cc["@mozilla.org/observer-service;1"]
    .getService(Ci.nsIObserverService);

observerService.addObserver(httpRequestObserver,
    "http-on-examine-response", false);

