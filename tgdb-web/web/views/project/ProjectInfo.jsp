<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html">
        <%@ taglib uri="/WEB-INF/lib/tgdb-tags.jar" prefix="arx" %>
        <link rel="stylesheet" type="text/css" href="test.css" />
        <title>JSP Page</title>
    </head>
    <body>

    <h1>Details for project '<jsp:getProperty name="project" property="name"/>'</h1>
    
    <arx:window name="prj.main" state="expanded" title="&nbsp;" workflow="ViewProjectInfo">
        <menu>
            <arx:hide privilege="PROJECT_ADM">
                <a href="Controller?workflow=EditProject&pid=<jsp:getProperty name="project" property="pid"/>">
                <arx:img name="edit"/></a>
            </arx:hide> 
        </menu>
        <body>
            <table class="block">
                <tr class="block">
                    <td class="block">
                        <b>Name</b><br> <jsp:getProperty name="project" property="name"/>
                    </td>
                    <td class="block">
                        <b>Status</b><br><jsp:getProperty name='project' property='status'/>
                    </td> 
                </tr>
                <tr>
                    <td class="block" colspan="2">
                        <b>Comment</b><br><jsp:getProperty name='project' property='comm'/>
                    </td>           
                </tr>
            </table> 
        </body>
    </arx:window>
    
    <br>
    
    <arx:window name="prj.users" title="Users assigned to project" workflow="ViewProjectInfo">
        <menu>
            <arx:hide privilege="PROJECT_ADM">
                <a href="Controller?workflow=CreateUser"><m:img name="add" title="Create a new user"/></a>
                <a href="Controller?workflow=ProjectAssignUser&pid=<jsp:getProperty name="project" property="pid"/>"><arx:img name="assign" title="Assign a user to this project"/></a>
            </arx:hide>
        </menu>
        <body>
        
            <table class="block_data">
                <tr>
                    <th class="block_data" width="45%">User</th>
                    <th class="block_data" width="45%">Role</th>
                    <arx:hide privilege="PROJECT_ADM">
                        <th class="block_data" width="10%">Unassign</th>
                    </arx:hide>
                </tr>
                <arx:iterate-collection collection="users">
                <tr class="data #?alt#">
                    <td width="45%"><a info="View user details" href="Controller?workflow=ViewUser&id=#:getId#">#:getName#</a></td>
                    <td width="45%">#:getRoleName#</td>
                    <arx:hide privilege="PROJECT_ADM">
                        <td width="10%"><a href="Controller?workflow=ProjectUnAssignUser&pid=<jsp:getProperty name="project" property="pid"/>&id=#:getId#"><arx:img name="unassign" title="Unassign user from the project"/></a></td>
                    </arx:hide>
                </tr>
                </arx:iterate-collection>
            </table>
        </body>
    </arx:window>
    
    
    <br>
    
    <arx:window name="prj.species" title="Species assigned to project" workflow="ViewProjectInfo">
        <menu>
            <arx:hide privilege="PROJECT_ADM">
                <a href="Controller?workflow=ProjectAssignSpecies&pid=<jsp:getProperty name="project" property="pid"/>"><arx:img name="assign" title="Assign a species to this project"/></a>
            </arx:hide>
        </menu>
        <body>
            <table class="block_data">
                <tr>
                    <th class="block_data" width="35%">Name</th>
                    <th class="block_data" width="55%">Comment</th>
                    <arx:hide privilege="PROJECT_ADM">
                        <th class="block_data" width="10%">Unassign</th>
                    </arx:hide>
                </tr>
                <arx:iterate-collection collection="species">
                <tr class="data #?alt#">
                    <td width="35%">#:getName#</td>
                    <td width="55%">#:getComm#</td>
                    <arx:hide privilege="PROJECT_ADM">
                        <td width="10%"><a href="Controller?workflow=ProjectUnAssignSpecies&pid=<jsp:getProperty name="project" property="pid"/>&sid=#:getSid#" onClick="return confirm('Unassign species?')"><arx:img name="unassign" title="Unassign species from project"/></a></td>
                    </arx:hide>
                </tr>
                </arx:iterate-collection>
            </table>
        </body>
    </arx:window>
    
    <br>
    
    <arx:window name="prj.res" title="Resources for project" workflow="ViewProjectInfo">
        <menu>       
        </menu>
        <body>
            <arx:resource-list categoryCreateWorkflow="AdminCreateResourceCategory" categories="categoriesAndResources" projectId='<%=(String)request.getAttribute("pid")%>' deleteWorkflow="RemoveProjectResource" editWorkflow="EditProjectResource" createLinkWorkflow="CreateProjectLinkResource" createFileWorkflow="CreateProjectFileResource"  categoryUnAssignWorkflow="#"/>  
        </body>
    </arx:window>
       
    <form action="Controller" method="post">
        <br>
        <arx:back/>
    </form>
    </body>
</html>