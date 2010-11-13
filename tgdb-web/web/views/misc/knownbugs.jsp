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

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <link rel="stylesheet" type="text/css" href="test.css" />
        <title>Known bugs</title>
    </head>
    <body>

    <h1>Known bugs</h1>
    
    <p>
        Problem in workflow handler, results in odd behaivour then using back buttons.
    </p>
    
    <p>
        Problem in workflow handler, results in lost internal model id. Failure while mining model data (No data loss, only runtime state!).
        <br>
        Workaround: Choose the model again from the menu.
    </p>
    
    <p>
        Problem in menu, results in unusable menu then session times out.
        <br>
        Workaround: Logout and login or user Reload in browser.
    </p>
    
    <p>
        then assigning a gene to a model, already assigned models show up in drop-down menu. Adding the same gene two times causes errors.
        This list will only contain genes that are not assigned to this model.
    
    </p>
    
    <p>
        Workflow "RemoveModelResource" not found   
    </p>
    
    
    </body>
</html>
