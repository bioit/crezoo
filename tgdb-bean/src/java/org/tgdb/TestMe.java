package org.tgdb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.tgdb.dto.OlsDTO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import uk.ac.ebi.ook.web.services.Query;
import uk.ac.ebi.ook.web.services.QueryService;
import uk.ac.ebi.ook.web.services.QueryServiceLocator;

public class TestMe {

    private static Map term_maps = new HashMap();
    private static String[] ontologies = {"EMAP", "MA"};

    public static void main(java.lang.String[] arg) {
//        doMe();
//        doMeToo();
//        doMeAsWell();
//        doHim("http://www.ebi.ac.uk/ontology-lookup/ajax.view?q=termautocomplete+termname=embryo+ontologyname=EMAP");
//        doHimToo("http://www.ebi.ac.uk/ontology-lookup/browse.do?ontName=EMAP");
//        doHimToo("http://zoub.org");
        doMice();
    }

    private static void doMe() {
        try {
            QueryService ols = (QueryService) new QueryServiceLocator();
            Query ols_query = ols.getOntologyQuery();
//            Map map = ols_query.getOntologyNames();
            Map map = ols_query.getAllTermsFromOntology("MA");
            Iterator i = map.keySet().iterator();
            int oid = 1;
            while(i.hasNext()) {
                String key = (String) i.next();
                String name =(String) map.get(key);
                OlsDTO dto = new  OlsDTO();
//                dto.setOid(oid);
                dto.setNamespace(key);
                dto.setName(name);
                System.out.println("name = " + dto.getName() + " + space = " + dto.getNamespace());
//                arr.add(dto);
                oid++;
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static void doMeToo() {
        try {
            QueryService ols = (QueryService) new QueryServiceLocator();
            Query ols_query = ols.getOntologyQuery();
//            Map map = ols_query.getOntologyNames();
             System.out.println(ols_query.getTermById("MA:0002405", "MA"));
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void doMeAsWell() {
        try {
            QueryService ols = (QueryService) new QueryServiceLocator();
            Query ols_query = ols.getOntologyQuery();
            for(int i = 0; i < ontologies.length; i++) {
                term_maps.put(ontologies[i], ols_query.getAllTermsFromOntology(ontologies[i]));
            }
            String ontology_name = "EMAP";
            Map map = (Map)term_maps.get(ontology_name);//ols_query.getAllTermsFromOntology(ontology_name);
            Iterator i = map.keySet().iterator();
            int oid = 1;
            while(i.hasNext()) {
                String key = (String) i.next();
                String name =(String) map.get(key);
                OlsDTO dto = new  OlsDTO();
//                dto.setOid(oid);
                dto.setNamespace(key);
                dto.setName(name);
                System.out.println("name = " + dto.getName() + " + space = " + dto.getNamespace());
//                arr.add(dto);
                oid++;
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static void doHim(String url) {
        try {
            InputStream nptstr = new URL(url.replace("+", "&")).openStream();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(nptstr);
            System.out.print(doc.getElementsByTagName("item").getLength());

            NodeList nl = doc.getElementsByTagName("item");

            for(int nli = 0; nli < nl.getLength(); nli++) {
                Element node = (Element) nl.item(nli);
                System.out.print("<item>");
                NodeList nl1 = node.getElementsByTagName("name");
                for(int nl1i = 0; nl1i < nl1.getLength(); nl1i++) {
                    System.out.print("<name>");
                    System.out.print(nl1.item(nl1i).getTextContent());
                    System.out.print("</name>");
                }
                NodeList nl2 = node.getElementsByTagName("value");
                for(int nl2i = 0; nl2i < nl2.getLength(); nl2i++) {
                    System.out.print("<value>");
                    System.out.print(nl2.item(nl2i).getTextContent());
                    System.out.print("</value>");
                }
                System.out.print("</item>");
            }
//            URLConnection conn = (new URL(url)).openConnection();
//            HttpURLConnection c = (HttpURLConnection)conn;
//            System.out.println("inputStream > " +  c.getInputStream().toString());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static void doHimToo(String url) {
        try {
            WebFile file   = new WebFile( url );
            String MIME    = file.getMIMEType( );
            Object content = file.getContent( );
            if ( MIME.equals( "text/html" ) && content instanceof String )
            {
                String html = (String)content;
                System.out.println(html);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean doMice() {
        boolean completed = false;
        try{

            BufferedReader input = new BufferedReader(new FileReader("C:/tmp/mice.csv"));

            String line = null;
            int line_type = 0;

            String [] tg_mouse = null;

            while ((line = input.readLine()) != null){
                tg_mouse = line.split("\t", -2);

                System.out.println("--------------MODEL DATA--------------");
                System.out.println("Line Name: \t" + tg_mouse[4]);
                System.out.println("Inducible: \t" + tg_mouse[7]);
                System.out.println("--------------ALLELE DATA--------------");
                System.out.println("Allele MGI ID: \t" + tg_mouse[0]);
                System.out.println("Allele Symbol: \t" + tg_mouse[1]);
                System.out.println("Allele Name: \t" + tg_mouse[2]);
                System.out.println("--------------STRAIN DATA--------------");
                System.out.println("Strain: \t" + tg_mouse[3]);
                System.out.println("MGI ID: \t" + tg_mouse[11]);
                System.out.println("EMMA ID: \t" + tg_mouse[12]);
                System.out.println("MMRRC ID: \t" + tg_mouse[13]);
                System.out.println("--------------PROMOTER DATA--------------");
                System.out.println("Promoter Name: \t" + tg_mouse[5]);
                System.out.println("--------------PUBMED--------------");
                System.out.println("Pubmed: \t" + tg_mouse[6]);
            }
            completed = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return completed;
    }
}
