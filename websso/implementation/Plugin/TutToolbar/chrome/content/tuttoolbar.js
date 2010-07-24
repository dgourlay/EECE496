var objTutorialToolbar = {
    ////////////////////////////////////////////////////////////////////////////
    // The ConvertTermsToURI() function converts an incoming string of search
    // terms to a safe value for passing into a URL.
    ////////////////////////////////////////////////////////////////////////////
    ConvertTermsToURI: function(terms)
    {
        // Create an array to hold each search term
        var termArray = new Array();
    
        // Split up the search term string based on the space character
        termArray = terms.split(" ");
    
        // Create a variable to hold our resulting URI-safe value
        var result = "";
    
        // Loop through the search terms
        for(var i=0; i<termArray.length; i++)
        {
            // All search terms (after the first one) are to be separated with a '+'
            if(i > 0)
                result += "+";
    
            // Encode each search term, using the built-in Firefox function
            // encodeURIComponent().
            result += encodeURIComponent(termArray[i]);
        }
    
        return result; // Return the result
    },
    
    ////////////////////////////////////////////////////////////////////////////
    // The KeyHandler() function checks to see if the key that was pressed
    // is the [Enter] key. If it is, a web search is performed.
    ////////////////////////////////////////////////////////////////////////////
    KeyHandler: function(event)
    {
        // Was the key that was pressed [ENTER]? If so, perform a web search.
        if(event.keyCode == event.DOM_VK_RETURN)
            this.Search(event, 'web');
    },
    
    ////////////////////////////////////////////////////////////////////////////
    // The LoadURL() function loads the specified URL in the browser.
    ////////////////////////////////////////////////////////////////////////////
    LoadURL: function(url)
    {
        // Set the browser window's location to the incoming URL
        window.content.document.location = url;
    
        // Make sure that we get the focus
        window.content.focus();
    },
    
    ////////////////////////////////////////////////////////////////////////////
    // The Populate() function places dynamically generated menu items inside
    // our toolbar's search box drop-down menu. Although this sample isn't very
    // practical, it demonstrates how dynamic menu population works.
    ////////////////////////////////////////////////////////////////////////////
    Populate: function()
    {
        // Get the menupopup element that we will be working with
        var menu = document.getElementById("TutTB-SearchTermsMenu");
    
        // Remove all of the items currently in the popup menu
        for(var i=menu.childNodes.length - 1; i >= 0; i--)
        {
            menu.removeChild(menu.childNodes.item(i));
        }
    
        // Specify how many items we should add to the menu
        var numItemsToAdd = 10;
    
        for(var i=0; i<numItemsToAdd; i++)
        {
            // Create a new menu item to be added
            var tempItem = document.createElement("menuitem");
    
            // Set the new menu item's label
            tempItem.setAttribute("label", "Dynamic Item Number " + (i+1));
    
            // Add the item to our menu
            menu.appendChild(tempItem);
        }
    },
    
    ////////////////////////////////////////////////////////////////////////////
    // The Search() function will perform a Google search for us. The two
    // parameters that get passed in are the event that triggered this function
    // call, and the type of search to perform.
    ////////////////////////////////////////////////////////////////////////////
    Search: function(event, type)
    {
        // This variable will hold the URL we will browse to
        var URL = "";
    
        // This variable will tell us whether our search box is empty or not
        var isEmpty = false;
    
        // Get a handle to our search terms box (the <menulist> element)
        var searchTermsBox = document.getElementById("TutTB-SearchTerms");
        
        // Get the value in the search terms box, trimming whitespace as necessary
        // See the TrimString() function farther down in this file for details
        // on how it works.
        var searchTerms = this.TrimString(searchTermsBox.value);
    
        if(searchTerms.length == 0) // Is the search terms box empty?
            isEmpty = true;         // If so, set the isEmpty flag to true
        else                        // If not, convert the terms to a URL-safe string
            searchTerms = this.ConvertTermsToURI(searchTerms);
    
        // Now switch on whatever the incoming type value is
        // If the search box is empty, we simply redirect the user to the appropriate
        // place at the Google website. Otherwise, we search for whatever they entered.
    
        switch(type)
        {
        // Build up the URL for an image search
        case "image":
            if(isEmpty) { URL = "http://images.google.com/"; }
            else        { URL = "http://images.google.com/images?q=" + searchTerms; }
            break;
    
    
        // Build up the URL for a web search
        case "web":
        default:
            if(isEmpty) { URL = "http://www.google.com/"; }
            else        { URL = "http://www.google.com/search?q=" + searchTerms; }
            break;
        }
        
        // Load the URL in the browser window using the LoadURL function
        this.LoadURL(URL);
    },
    
    ////////////////////////////////////////////////////////////////////////////
    // The TrimString() function will trim all leading and trailing whitespace
    // from the incoming string, and convert all runs of more than one
    // whitespace character into a single space. The new string gets returned.
    ////////////////////////////////////////////////////////////////////////////
    TrimString: function(string)
    {
        // If the incoming string is invalid, or nothing was passed in, return empty
        if (!string)
            return "";
    
        string = string.replace(/^\s+/, ''); // Remove leading whitespace
        string = string.replace(/\s+$/, ''); // Remove trailing whitespace
    
        // Replace all whitespace runs with a single space
        string = string.replace(/\s+/g, ' ');
    
        return string; // Return the altered value
    }

};

