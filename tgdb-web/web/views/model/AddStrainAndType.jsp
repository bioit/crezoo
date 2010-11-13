<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="test.css" />
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <title>Strain States</title>
    </head>
    <body>         
        <h1>Strain States</h1>
         
         
        <form action="Controller" method="post">
        
            <p>
            Types<br>
            <m:checkbox collection="availabletypes" emptyOption="true" idGetter="getId" textGetter="getName" name="availabletypes"/> 
            </p>
            <p>
            States<br>
            <m:checkbox collection="availablestates" emptyOption="true" idGetter="getId" textGetter="getName" name="availablestates"/> 
            </p>
            <m:save saveName="add"/>
       
        </form>
    </body>
</html>
