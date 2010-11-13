<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <link rel="stylesheet" type="text/css" href="test.css" />
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Search keyword <a href="Controller?workflow=SearchKeyword&search.keyword.info=true"><m:img name="info" title="information about search"/></a></h1>
        <%--<a href="Controller?workflow=SearchKeyword&search.keyword.info=true">
            <m:img name="info" title="information about search"/>
        </a>--%>
    
        <form method="get" action="Controller">    
        <m:hide-block name="search.keyword.info">
            <p>
            Type a keyword and press the "Search" button.<br>
            Once the search returns you should see the "Search results" page. The list of this page contains all genes and mutant mice that are somehow relevant to the keyword you have typed. Whether it is the name of the gene or strain or another piece of information associated with a gene or strain (e.g. comments, references etc.).<br>
            To see more details about an item of this list you must click on its name.
            </p>
        </m:hide-block>
    
        
            <table class=block>
                <tr class=block>
                    <th class=block>
                        Keyword search
                    </th>
                </tr>
                <tr class=block>
                    <td>
                        Name:<br>
                        <input type="text" name="keyword">
                        <p>
                            <input type="submit" name="search" value="Search">
                        </p>
                    </td>
                </tr>
            </table>
        
        
        </form>
    </body>
</html>
