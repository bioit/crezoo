<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; home</title>
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
            <span class="header_01">Home</span>
        
    <table>
        <tr>
            <td class="text_cell">
                Welcome to the CreZOO database, the European virtual repository of Cre and other targeted conditional driver strains. CreZOO is being developed in the context of the <a href="http://www.creline.org/" target="_blank">CREATE consortium</a>, a core of major European and international mouse database holders and research groups involved in conditional mutagenesis. Its aim is to capture and disseminate extant and new information on Cre driver strains. CreZOO also aims to contribute data to the <a href="http://www.creline.org/search_cre_mice" target="_blank">CREATE portal</a> for worldwide access of related information.
 
All transgenic strains carry detailed information on the promoter, specificity (using Adult Mouse Anatomy terms and Theiler Stages) and expressed gene(s) including IDs and direct links where available. Allele details are also presented, in addition to strain, background and availability (in the form of live mice, cryopreserved embryos or sperm etc) information (including <a href="http://www.emmanet.org/" target="_blank">EMMA</a>, <a href="http://www.informatics.jax.org/" target="_blank">MGI</a>, <a href="http://www.mmrrc.org/" target="_blank">MMRRC</a> etc hyperlinks where available). Handling and genotyping details (in the form of documents or hyperlinks) together with all relevant publications are clearly presented with PMID(s) and direct <a href="http://www.ncbi.nlm.nih.gov/pubmed" target="_blank">PubMed</a> links. 

For a collection of other online related resources please click <a href="http://bioit.fleming.gr/mugen/Controller?workflow=OnlineResources" target="_blank">here</a> or visit the <a href="http://bioit.fleming.gr/mrb/" target="_blank">MRB|Mouse Resource Browser</a> project.
 
Funded and supported by: <a href="http://www.creline.org/" target="_blank">CREATE</a>
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
