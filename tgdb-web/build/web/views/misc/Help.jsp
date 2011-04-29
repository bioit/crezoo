<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; help</title>
	<link rel="stylesheet" type="text/css" href="css/crezoo-tabs.css" />
    <jsp:include page="/Header"/>
</head>
<body>
<div id="wrap">
	<div id="panel-left">
		<jsp:include page="/PanelLeft"/>
	</div>
	<div id="panel-right">
		<jsp:include page="/PanelRightTop"/>
        <div id="panel-right-rest">
        <form action="Controller" method="post">
            <span class="header_01">Help</span>

    <table>
        <tr>
            <td class="text_cell">
                <b>How to find a Transgenic Mouse</b>
		<ol>
		    <li>Click on the Mice button on the menu which is available at all times.</li>
		    <li>A CreZOO Mouse Index page appears below from which you can select the one that is of interest to you.</li>
		    <li>To view detailed information about a particular transgenic mouse of interest, click on the respective Line Name and you will be redirected to the Detailed information page.</li> 
		    <li>The information is categorised and presented in three tabs named General, Gene & Allele Information and Documents. Upon clicking on the particular tab, respective information is displayed.</li>
		</ol>
		<b>How to find an Allele of interest and all related Transgenic Mice</b>
		<ol>
		    <li>Click on the Alleles button on the menu which is available at all times on the top bar.</li>
		    <li>A CreZOO Allele Index page appears from which you can select the one that is of interest to you. Official MGI Allele nomenclature is used, and MGI Allele IDs are provided where possible.</li>
		    <li>To view detailed information about a particular allele of interest, click on the respective Symbol and you will be redirected to a detailed Allele information page in addition to a list of Related Mice carrying the particular allele of choice.</li>
		</ol>
		<b>How to use the CreZOO search engine</b>
		<ol>
		    <li>Click inside the Search box on the very top right hand corner of CreZOO, which is available at all times.</li>
		    <li>Type the keyword in the text field and press Enter.</li>
		    <li>Upon completion of the search, a list with all results matching the keyword(s) will be displayed, including available mice, alleles and promoters. By clicking on the particular Name on the search results, you will be redirected to the respective page.</li>
		</ol>
            </td>
        </tr>
    </table>
    </form>
    </div>
</div>
<div id="clear-foot">&nbsp;</div>
</div>
<jsp:include page="/Foot"/>
</body>
</html>
