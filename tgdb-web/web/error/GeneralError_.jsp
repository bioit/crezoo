<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="exception" scope="request" class="java.lang.Exception"/>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="test.css" />
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="m" %>
        <title>Error page</title>
    </head>
    <body>
    <div id="centered">
        <table class=login>
            <tr>
                <td>
                    <m:img name="logo"/>
                </td>
                <td>
                    <m:img name="error32"/>
                </td>
                <td>
                    <table>
                        <tr>
                            <td colspan=2><h1>Error</h1></td>
                        </tr>
                        <tr>
                            <td>
                            <%
                                if (exception!=null)
                                    out.println(exception.getMessage());
                            %>
                            </td>
                        </tr>
                        <tr>
                            <td>
                            <%
                                if (exception!=null && exception.getCause()!=null)
                                    out.println(exception.getCause().getMessage());
                            %>
                            </td>
                        </tr>
                        <tr>
                            <td colspan=2 align=right>
                                <form action="Controller">
                                    
                                    <m:back/>
                                </form>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
        
        
        <a href="error/GeneralError.jsp?error.hide=true"><m:img name="nav_close"/></a>
        <m:hide-block name="error.hide">
        <pre>
        <%
            String time = new java.util.Date()+" ("+System.currentTimeMillis()+" ms)";
            out.println("Time="+time+"\n---\n");
            
            out.println("URL="+request.getRequestURL());
            
            
            org.tgdb.TgDbCaller caller = (org.tgdb.TgDbCaller)session.getAttribute("caller"); 
            if (caller!=null)
                out.println("Caller: usr="+caller.getUsr()+"\nname="+caller.getName()+"\nid="+caller.getId()+"\n---\n");
            
            java.io.PrintWriter pw = new java.io.PrintWriter(out);
            exception.printStackTrace(pw);
            
            
            
        %>
        </pre>
        </m:hide-block>
        
    </div>
    </body>
</html>
