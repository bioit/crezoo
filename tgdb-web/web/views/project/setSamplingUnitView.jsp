<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <link rel="stylesheet" type="text/css" href="test.css" />
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>        
        <title>JSP Page</title>
    </head> 
    <body id="bodyChromo">

        <h1>Sampling unit settings</h1>
        <form action="Controller" method="post">    
            <table>             
                <tr>
                    <td>Sampling unit<br>
                        <m:su-combobox name="suid"/>                        
                    </td>
                </tr>
                <tr>
                    <td align="right">
                        <input id="button" type="submit" value="Set">
                    </td>
                </tr>
            </table>
        </form>    
    </body>
</html>
