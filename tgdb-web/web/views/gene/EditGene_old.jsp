<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<%--
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="gene" scope="request" type="org.tgdb.model.modelmanager.GeneDTO" />
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <link rel="stylesheet" type="text/css" href="test.css" />
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <title>Edit Gene</title>       
    </head>
    <body>
        <h1>Edit gene</h1>
        <form action="Controller" method="post">
        <input type="hidden" name="gaid" value="<jsp:getProperty name='gene' property='gaid'/>">
            <p>
                <table>
                    <tr>
                        <td>Gene Name</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="text" name="name" size="35" value='<jsp:getProperty name="gene" property="name"/>'/>
                        </td>
                    </tr>
                    <tr>
                        <td>MGI Gene ID</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="text" name="mgiid" size="35" value='<jsp:getProperty name="gene" property="mgiid"/>'/>
                        </td>
                    </tr>
                    <tr>
                        <td>Gene Symbol</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="text" name="genesymbol" size="35" value='<jsp:getProperty name="gene" property="genesymbol"/>'/>
                        </td>
                    </tr>
                    <tr>
                        <td>Gene Expression</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="text" name="geneexpress" size="35" value='<jsp:getProperty name="gene" property="geneexpress"/>'/>
                        </td>
                    </tr>
                    <tr>
                        <td>GENE Database ID</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="text" name="idgene" size="35" value='<jsp:getProperty name="gene" property="idgene"/>'/>
                        </td>
                    </tr>
                    <tr>
                        <td>ENSEMBL Database ID</td>
                    </tr>
                    <tr>
                        <td>
                            <input type="text" name="idensembl" size="35" value='<jsp:getProperty name="gene" property="idensembl"/>'/>
                        </td>
                    </tr>
                    <tr>
                        <td>Comment</td>
                    </tr>
                    <tr>
                        <td><textarea type="text" cols="35" rows="6" name="comm"><jsp:getProperty name="gene" property="comm"/></textarea></td>                        
                    </tr>                    
                </table>
            </p>
            <p>
                <m:save/>
            </p>
        </form> 
    </body>
</html>

