<?xml version="1.0" encoding="UTF-8"?>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <title>RelyingPartyEX</title>
        <meta http-equiv="Content-Type" content="application/xhtml+xml; charset=UTF-8" />

        <link rel="stylesheet" type="text/css" href="style.css" />
        <script type="text/javascript">
            <!--
            function changeAll(v) {
                var inputs = document.getElementsByTagName("input");
                for (var i = 0; i < inputs.length; i++) {
                    if (inputs[i].value == v) {
                        inputs[i].checked = true;
                    }
                }
            }
            //-->
        </script>
    </head>
    <body>
        <div>
            <fieldset>
                <legend><h2>Test:</h2></legend>
                <form action="ConsumerServlet" method="get" >
                    <div>
                        <input type="submit" name="login" value="Go!" class="green"/>

                    </div>
                </form>
            </fieldset>

        </div>
    </body>
</html>