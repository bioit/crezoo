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

    <h1>Search Mutants</h1>
    
    <form method="get" action="Controller">
    
        <table class=block>
        <tr class=block>
            <th class=block>
                Projects 
                <a href="Controller?workflow=SearchModel&search.model.project=true"><m:img name="info"/></a>
            </th>
        </tr>
        <m:hide-block name="search.model.project">
        <tr>
            <td>
                This option searches all mutants in a project.
            </td>
        </tr>
        </m:hide-block>
        <tr class=block>
            <td class=block>
                <m:iterate-collection collection="projects">
                    <a href="?project=#:getName#">#:getName#</a><br>
                </m:iterate-collection>
                <!--
                <input type=radio name="project" value="Default">Default<br>
                <input type=radio name="project" value="test">Test
                -->
            </td>
        </tr>
        </table>
        <br>
        <table class=block>
        <tr class=block>
            <th class=block>
                Gene search
            </th>
        </tr>
        <tr class=block>
            <td>
                Name:<br>
                <input type="text" name="gene">
                <p>
                    <input id="button" type="submit" name="search" value="Search"
                </p>
            </td>
        </tr>
        </table>
        
        <br>
        <table class=block>
        <tr class=block>
            <th class=block>
                Model (VariableSet, Phenotype)
            </th>
        </tr>
        <tr class=block>
            <td>
                <table>
                <tr><th>Variable Set</th><th>Project</th></tr>
                <%
                    java.util.Collection varsets = (java.util.Collection)request.getAttribute("varsets");
                    java.util.Iterator i = varsets.iterator();
                    while (i.hasNext())
                    {
                        org.tgdb.model.modelmanager.VariableSetProjectDTO dto;
                        dto = (org.tgdb.model.modelmanager.VariableSetProjectDTO)i.next();
                        
                        String projectNames = "";
                        for (int j=0;j<dto.getProjects().length;j++)
                        {
                            org.tgdb.project.projectmanager.ProjectDTO prj;
                            prj = dto.getProjects()[j];
                            projectNames += prj.getName();
                            if (j!=0)
                                projectNames += ", ";
                        }
                        out.println("<tr><td><a href=\"Controller?vsname="+dto.getName()+"\" >"+dto.getName()+"</a></td><td>"+projectNames+"</td></tr>");   
                    }
                %>
                </table>
            </td>
        </tr>
        </table>
        
        
        <br>
        <table class=block>
        <tr class=block>
            <th class=block>
                Research Applications
            </th>
        </tr>
        <tr class=block>
            <td>
                <table>
                    <m:iterate-collection collection="rapps">
                    <tr>
                        <td><a href="Controller?raname=#:getName#">#:getName#</a></td>
                        <td>#:getProjectName#</td>
                    </tr>
                    </m:iterate-collection>
                </table>
            </td>
        </tr>
        </table>
    </form>
    </body>
</html>
