<%
    org.tgdb.form.FormDataManager fdm = (org.tgdb.form.FormDataManager)request.getAttribute("formdata");
    org.tgdb.TgDbCaller caller = (org.tgdb.TgDbCaller)session.getAttribute("caller");
%>  
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="test.css" />
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <title>IMSR File Generator</title>
    </head>
    <body>         
        <h1>IMSR File Generator</h1>
        
        <form action="Controller" method="post">
        
        <hr id="ruler">  
            <table class="data">
                <tr>
                    <th class="data">MMMDb ID</th>
                    <th class="data">Strain Designation</th>
                    <th class="data">Strain Type</th>
                    <th class="data">Strain State</th>
                    <th class="data">MGI Allele ID</th>
                    <th class="data">Allele Symbol</th>
                    <th class="data">Allele Name</th>
                    <th class="data">Mutation Type</th>
                    <th class="data">Chromosome</th>
                    <th class="data">MGI Gene ID</th>
                    <th class="data">Gene Symbol</th>
                    <th class="data">Gene Name</th>
                </tr>
                <m:iterate-collection collection="modelsdto">
                    <tr class="#?alt#">
                        <%--<td width="13%">#:getAccNr#&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;#:getDesignation#&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;#:getMutationTypes#&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;#:getMutationStates#&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;#:getAllMgiid#&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;#:getAllSymbol#&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;#:getAllName#&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;#:getAllMutationabbrs#&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;#:getGeneChromosome#&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;#:getGeneMgiid#&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;#:getGeneSymbol#&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;#:getGeneName#</td>--%>
                        <td>#:getAccNr#</td>
                        <td>#:getDesignation#</td>
                        <td>#:getMutationTypes#</td>
                        <td>#:getMutationStates#</td>
                        <td>#:getAllMgiid#</td>
                        <td>#:getAllSymbol#</td>
                        <td>#:getAllName#</td>
                        <td>#:getAllMutationabbrs#</td>
                        <td>#:getGeneChromosome#</td>
                        <td>#:getGeneMgiid#</td>
                        <td>#:getGeneSymbol#</td>
                        <td>#:getGeneName#</td>
                    </tr>
                </m:iterate-collection>
            </table>
       </form>
    </body>
</html>
