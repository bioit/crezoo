/*
 * BlockHideTag.java
 *
 * Created on December 5, 2005, 1:56 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.tgdb.tags;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 *
 * @author lami
 */
public class ImageTag extends BodyTagSupport {
    private String name, title;
    
    /** The xml document */
    private Document doc;
    
    private class Image {
        public String name;
        public String src;
        public String title;
        public String path;
    }
            
            
    
    /** Creates a new instance of BlockHideTag */
    public ImageTag() throws JspException {
        name = "";
        title = "";
    }
    
    private void parseXML() throws Exception
    {
        try
        {
            ServletContext context = pageContext.getServletContext();
            InputStream is = context.getResourceAsStream("/images.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(is);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Exception("ImageTag#parseXML: Error parsing XML. "+e.getMessage(), e);
        }
    }
    
    public String getPath()
    {
        NodeList nl = doc.getElementsByTagName("images");
        Element e = (Element)nl.item(0);
        return e.getAttribute("path");
    }
    
    public Image getImage(String name)
    {
        NodeList nl = doc.getElementsByTagName("image");
        
        
       
        for (int i=0;i<nl.getLength();i++)
        {
            Element e = (Element)nl.item(i);
            String tmp = e.getAttribute("name");
            if (name.equals(tmp))
            {                
                Image img = new Image();
                img.name = e.getAttribute("name");
                img.src = e.getAttribute("src");
                img.title = e.getAttribute("title");
                img.path = getPath();
                return img;
            }
        }
        
        
        Image img = new Image();
        img.name="unknown";
        img.src="unknown.png";
        img.title="Image not found";
        img.path=getPath();
        return img;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public int doStartTag() throws JspException {        
        try {            
            if (doc==null)
                parseXML();
            
            Image img = getImage(name);
            if (title==null || title.length()==0)
                title = img.title;
            
            pageContext.getOut().print("<img src=\""+img.path+"/"+img.src+"\" title=\""+title+"\">");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new JspException("Failed tag",e);
        }

        return SKIP_BODY;
    }       
}
