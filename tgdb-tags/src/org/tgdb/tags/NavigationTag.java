package org.tgdb.tags;

import org.tgdb.frame.Navigator;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class NavigationTag extends BodyTagSupport {
    private String workflow;
    private Navigator navigator;
    private boolean showText;
    private int viewed;
    private int max;
    
    public NavigationTag() {
        showText = false;
    }
    
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }
    
    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }
    
    public void setShowText(boolean showText) {
        this.showText = showText;
    }
    
    @Override
    public int doStartTag() throws JspException {
        String data = "";
        try {
            boolean isLast = false;
            boolean isFirst = false;
            boolean onePage = false;
            navigator = (Navigator)pageContext.getSession().getAttribute("navigator");
            int currentStart = 0;
            int currentStop = 0;
            if(navigator != null) {
                max = navigator.getPageManager().getMax();
                
                currentStart = navigator.getPageManager().getStart();
                
                viewed = navigator.getPageManager().getViewed();
                
                if(max == 0)
                    currentStart = 0;
                
                if(currentStart == 1)
                    isFirst = true;
                currentStop = navigator.getPageManager().getStop();
                
                if(currentStop >= max) {
                    currentStop = max;
                    isLast = true;
                }
                if(currentStop < navigator.getPageManager().getDelta())
                    onePage = true;
                
            }
            
            // Added style info in css, p.navtext
            data += "<p class=\"navtext\">";
            
            if(isFirst && !onePage) {
//                data += "<img src=\"images/icons/navigate_beginning_disabled.png\" border=\"none\">\n";
//                data += "<img src=\"images/icons/navigate_left_disabled.png\" border=\"none\">\n";
            } else if(!onePage) {
//                data += "<a href=\"Controller?workflow="+workflow+"&first\"><img src=\"images/icons/navigate_beginning.png\" border=\"none\" title=\"First\"></a>\n";
//                data += "<a href=\"Controller?workflow="+workflow+"&prev\"><img src=\"images/icons/navigate_left.png\" border=\"none\" title=\"Previous\"></a>\n";
                data += "<a href=\"Controller?workflow="+workflow+"&amp;first\">Start</a>\n";
                data += "<a href=\"Controller?workflow="+workflow+"&amp;prev\">Previous</a>\n";
            }
            
            if(isLast && !onePage) {
//                data += "<img src=\"images/icons/navigate_right_disabled.png\" border=\"none\">\n";
//                data += "<img src=\"images/icons/navigate_last_disabled.png\" border=\"none\">\n";
            } else if(!onePage){
//                data += "<a href=\"Controller?workflow="+workflow+"&next\"><img src=\"images/icons/navigate_next.png\" border=\"none\" title=\"Next\"></a>\n";
//                data += "<a href=\"Controller?workflow="+workflow+"&last\"><img src=\"images/icons/navigate_end.png\" border=\"none\" title=\"Last\"></a>\n";
                data += "<a href=\"Controller?workflow="+workflow+"&amp;next\">Next</a>\n";
                data += "<a href=\"Controller?workflow="+workflow+"&amp;last\">End</a>\n";
            }
            
            if(onePage) {
//                data += "<img src=\"images/icons/navigate_beginning_disabled.png\" border=\"none\">\n";
//                data += "<img src=\"images/icons/navigate_left_disabled.png\" border=\"none\">\n";                
//                data += "<img src=\"images/icons/navigate_right_disabled.png\" border=\"none\">\n";
//                data += "<img src=\"images/icons/navigate_end_disabled.png\" border=\"none\">\n";                
            }
            
            
            // If the text should be shown
            if (showText)
            {    
                data += "&nbsp;";
                // If only one page will be displayed, show a simpler version
                if (onePage)    
                    data += "displaying "+viewed+" out of "+max;
                else
                    data += "displaying "+currentStart+"-"+currentStop+" out of "+max;
            }
            data += "&nbsp;";
            data += "</p>";
            
            // Print the table on the page
            pageContext.getOut().print(data);
            
        } catch (Exception e) {
            throw new JspTagException(e.getMessage());
        }
        
        return SKIP_BODY;
    }
}
