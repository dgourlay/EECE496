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
			<legend><h2>Sample 1:</h2></legend>
			<form action="ConsumerServlet" method="post" >
				<div>
					<label><span>URL:</span>
                                            <input type="text" name="openid_identifier" class="openid_identifier" />
                                            <input type="submit" name="login" value="Login" class="green"/>
                                        </label>
				</div>
			</form>
		</fieldset>
		<fieldset>
			<legend><h2>Sample 2: Using the Simple Registration extension</h2>(doc: <a href="http://code.google.com/p/openid4java/wiki/SRegHowTo">SRegHowTo</a>)</legend>
			<form action="ConsumerServlet" method="post">
				<div>
                                    <label><span>URL:</span>
					<input type="text" name="openid_identifier" class="openid_identifier" />
					<input type="submit" name="login" value="Login" class="green" />
                                    </label>
                                        <table id="sreg">
						<tr>
							<th>All:</th>
							<td>
								<input type="radio" name="all" value="" id="all" onclick="changeAll('')" /><label for="all">None</label>
								<input type="radio" name="all" value="0" id="all0" onclick="changeAll('0')" /><label for="all0">Optional</label>
								<input type="radio" name="all" value="1" id="all1" onclick="changeAll('1')" /><label for="all1">Required</label>
							</td>
						</tr>
						<tr>
							<th>Nickname:</th>
							<td>
								<input type="radio" name="nickname" value="" id="nickname" checked="checked" /><label for="nickname">None</label>
								<input type="radio" name="nickname" value="0" id="nickname0" /><label for="nickname0">Optional</label>
								<input type="radio" name="nickname" value="1" id="nickname1" /><label for="nickname1">Required</label>
							</td>
						</tr>
						<tr>
							<th>Email:</th>
							<td>
								<input type="radio" name="email" value="" id="email" checked="checked" /><label for="email">None</label>
								<input type="radio" name="email" value="0" id="email0" /><label for="email0">Optional</label>
								<input type="radio" name="email" value="1" id="email1" /><label for="email1">Required</label>
							</td>
						</tr>
						<tr>
							<th>Fullname:</th>
							<td>
								<input type="radio" name="fullname" value="" id="fullname" checked="checked" /><label for="fullname">None</label>
								<input type="radio" name="fullname" value="0" id="fullname0" /><label for="fullname0">Optional</label>
								<input type="radio" name="fullname" value="1" id="fullname1" /><label for="fullname1">Required</label>
							</td>
						</tr>
						<tr>
							<th>Date of birth:</th>
							<td>
								<input type="radio" name="dob" value="" id="dob" checked="checked" /><label for="dob">None</label>
								<input type="radio" name="dob" value="0" id="dob0" /><label for="dob0">Optional</label>
								<input type="radio" name="dob" value="1" id="dob1" /><label for="dob1">Required</label>
							</td>
						</tr>
						<tr>
							<th>Gender:</th>
							<td>
								<input type="radio" name="gender" value="" id="gender" checked="checked" /><label for="gender">None</label>
								<input type="radio" name="gender" value="0" id="gender0" /><label for="gender0">Optional</label>
								<input type="radio" name="gender" value="1" id="gender1" /><label for="gender1">Required</label>
							</td>
						</tr>
						<tr>
							<th>Postcode:</th>
							<td>
								<input type="radio" name="postcode" value="" id="postcode" checked="checked" /><label for="postcode">None</label>
								<input type="radio" name="postcode" value="0" id="postcode0" /><label for="postcode0">Optional</label>
								<input type="radio" name="postcode" value="1" id="postcode1" /><label for="postcode1">Required</label>
							</td>
						</tr>
						<tr>
							<th>Country:</th>
							<td>
								<input type="radio" name="country" value="" id="country" checked="checked" /><label for="country">None</label>
								<input type="radio" name="country" value="0" id="country0" /><label for="country0">Optional</label>
								<input type="radio" name="country" value="1" id="country1" /><label for="country1">Required</label>
							</td>
						</tr>
						<tr>
							<th>Language:</th>
							<td>
								<input type="radio" name="language" value="" id="language" checked="checked" /><label for="language">None</label>
								<input type="radio" name="language" value="0" id="language0" /><label for="language0">Optional</label>
								<input type="radio" name="language" value="1" id="language1" /><label for="language1">Required</label>
							</td>
						</tr>
						<tr>
							<th>Timezone:</th>
							<td>
								<input type="radio" name="timezone" value="" id="timezone" checked="checked" /><label for="timezone">None</label>
								<input type="radio" name="timezone" value="0" id="timezone0" /><label for="timezone0">Optional</label>
								<input type="radio" name="timezone" value="1" id="timezone1" /><label for="timezone1">Required</label>
							</td>
						</tr>
					</table>
				</div>
			</form>
		</fieldset>

	</div>

    <a href="protected01.jsp" >protected01.jsp</a>
</body>
</html>