<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%
/*
org.mrb.bean.MRBCaller c = (org.mrb.bean.MRBCaller)session.getAttribute("caller");

String allres = "";
String allcats = "";
String who = "";

if(c == null) {
    response.sendRedirect("/mrb/Controller");
} else {
    allres = (String)session.getAttribute("allres");
    allcats = (String)session.getAttribute("allcats");
    
    who = c.getName();
    
    if(who.equalsIgnoreCase("public"))
        who = "";
    
}
*/
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>crezoo &mdash; Welcome</title>
    <jsp:include page="Header"/>
</head>
<body>
<div id="wrap">
	<div id="panel-left">
		<jsp:include page="PanelLeft"/>
	</div>
	<div id="panel-right">
		<jsp:include page="PanelRightTop"/>
	</div>
	<div id="clear-foot">&nbsp;</div>
</div>
<jsp:include page="Foot"/>
</body>
</html>