<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; about</title>
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
            <span class="header_01">About</span>

    <table>
        <tr>
            <td class="text_cell">
                The CreZOO database is being developed & curated in the context of the <a href="http://www.creline.org/" target="_blank" title="CREATE">CREATE consortium</a>.
                <br/><br/>
                <b>Submission &amp; Curation</b>
                <br/>
                &nbsp;&nbsp;&nbsp;CreZOO's data collection is compiled and manually curated through extensive literature surveys and other online resources. The information is collected and assessed for accuracy and completeness, including genetic, strain, allelic and expression features.
                <br/>
                &nbsp;&nbsp;&nbsp;CreZOO aims to present data in standardised formats, resolving issues pertinent to nomenclature and referential integrity.
                <br/>
                &nbsp;&nbsp;&nbsp;Overall, gene/promoter, allelic and strain designation is assigned according to <a href="http://www.informatics.jax.org/mgihome/nomen/strains.shtml" target="_blank">rules and guidelines for mouse genes and strains</a> given by MGI, while anatomical expression patterns and developmental stages are displayed using the <a href="http://www.informatics.jax.org/searches/AMA_form.shtml" target="_blank">Adult Mouse Anatomy (MA)</a> terminology given by MGI and <a href="http://genex.hgu.mrc.ac.uk/Databases/Anatomy/Diagrams/" target="_blank">Theiler stages</a> as described at Edinburgh (<a href="http://genex.hgu.mrc.ac.uk/" target="_blank">EMAP</a>) respectively.
                <br/><br/>
                <b>Technical Specifications</b>
                <br/>
                &nbsp;&nbsp;&nbsp;CreZOO is a J2EE application based on the EJB 2.1 specification and the MVC2 architecture and the front end of a relational, fully normalized PostgreSQL (version 8.1.3) database.
                <br/><br/>
                <b>CreZOO Scientific Director</b>
                <br/>
                <a href="mailto:aidinis@fleming.gr">Dr. Aidinis Vassilis</a>
                <br/><br/>
                <b>CreZOO curator</b>
                <br/>
                <a href="mailto:chandras@fleming.gr">Dr. Christina Chandras</a>
                <br/><br/>
                <b>CreZOO database developer</b>
                <br/>
                <a href="mailto:zouberakis@fleming.gr">Michalis Zouberakis</a>
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
