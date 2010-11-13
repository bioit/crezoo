<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
    <title>crezoo &mdash; add specificity</title>
    <jsp:include page="/Header"/>
    <link rel="stylesheet" type="text/css" href="css/crezoo-tabs.css" />
    <script type="text/javascript" src="javascripts/validate.js"></script>

    <link rel="stylesheet" type="text/css" href="css/jquery.autocomplete.css" />
    <script type="text/javascript" src="javascripts/jquery.autocomplete.pack.js"></script>
    <script type="text/javascript">
	<!--
        $(document).ready(function() {
            //MA: Mouse Adult Gross Anatomy
            var ma = $('input#ma');

            ma.autocomplete('olsajax?uri=http://www.ebi.ac.uk/ontology-lookup/ajax.view?q=termautocomplete', {
                delay: 400,
				extraParams: {
					termname: function() { return $("input#ma").val(); },
					ontologyname: "MA"
				},
                minChars: 3,
                matchSubset: 1,
                matchContains: 1,
                parse: parseXML,
				width: 400,
				scrollHeight: 900,
				scroll: false,
				max: 1000,
                formatItem: formatItem,
                formatResult: formatResult
            }).result(function(event, item) {
				$('input#ma_id').replaceWith('<input type="hidden" id="ma_id" name="ma_id" value="'+item.value+'" />');
            });

            //EMAP: Mouse Gross Anatomy and Development
            var emap = $('input#emap');

            emap.autocomplete('olsajax?uri=http://www.ebi.ac.uk/ontology-lookup/ajax.view?q=termautocomplete', {
                delay: 400,
				extraParams: {
					termname: function() { return $("input#emap").val(); },
					ontologyname: "EMAP"
				},
                minChars: 3,
                matchSubset: 1,
                matchContains: 1,
                parse: parseXML,
				width: 400,
				scrollHeight: 900,
				scroll: false,
				max: 1000,
                formatItem: formatItem,
                formatResult: formatResult
            }).result(function(event, item) {
				$('input#emap_id').replaceWith('<input type="hidden" id="emap_id" name="emap_id" value="'+item.value+'" />');
            });

            // this function will parse xml and return as a array of string
            function parseXML(xml) {
                var results = [];
                $(xml).find('item').each(function() {
                    var text = $.trim($(this).find('name').text());
                    var value = $.trim($(this).find('value').text());
                    results[results.length] = { 'data': { text: text, value: value },
                        'result': text, 'value': value
                    };
                });
                return results;
            };

            function formatItem(data) {
                return " [" + data.value + "] " + data.text;
            };

            function formatResult(data) {
                return " [" + data.value + "] " + data.text;
            };
        });
	// -->
    </script>
    
    <script type="text/javascript">
            function init() { 
                define('exanatomy', 'string', 'Expression Anatomy', null, 500);
                define('excomm', 'string', 'Expression Comment', null, 500);
                define('exfilecomm', 'string', 'Expression Image Comment', null, 500);
            }        
    </script>
</head>
<body onload="init()">
<div id="wrap">
	<div id="panel-left">
		<jsp:include page="/PanelLeft"/>
	</div>
	<div id="panel-right">
		<jsp:include page="/PanelRightTop"/>        
        <div id="panel-right-rest">
        <form action="Controller" method="post" enctype="multipart/form-data">
            <span class="header_01">Add Specificity</span>
        <input type="hidden" name="eid" value='<%=(String)request.getParameter("eid")%>'/>
        <table>
            <tr>
                <td><b>Expression Anatomy</b></td>
            </tr>
            <tr>
                <td><input type="text" name="exanatomy" size="55" maxlength="500"/></td>
            </tr>
            <tr>
                <td><b>Expression Comment</b></td>
            </tr>
            <tr>
                <td><textarea cols="55" rows="4" name="excomm"></textarea></td>
            </tr>
            <tr>
                <td><b>Expression Image</b></td>
            </tr>
            <tr>
                <td><input type="file" name="exfile" /></td>
            </tr>
            <tr>
                <td><b>Expression Image Comment</b></td>
            </tr>
            <tr>
                <td><input type="text" name="exfilecomm" size="55" maxlength="500"/></td>
            </tr>
            <tr>
                <td><b>Site of Expression</b></td>
            </tr>
            <tr>
                <td><input id="ma" type="text" size="55" maxlength="500"/><input type="hidden" id="ma_id" name="ma_id" /></td>
            </tr>
            <tr>
                <td><b>Developmental Stage</b></td>
            </tr>
            <tr>
                <td><input id="emap" type="text" size="55" maxlength="500"/><input type="hidden" id="emap_id" name="emap_id" /></td>
            </tr>
        </table>
        <table>
            <tr>
                <td>
                    <p class="navtext">
                        <input type="submit" name="upload" value="Save" title="Save" onclick="validate();return returnVal;"/>
                        <input type="submit" name="back" value="Back" title="Back"/>
                    </p>
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