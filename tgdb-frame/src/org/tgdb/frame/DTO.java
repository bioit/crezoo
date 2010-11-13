package org.tgdb.frame;

import org.apache.log4j.Logger;

public class DTO {
    
    protected static Logger logger = Logger.getLogger(DTO.class);
    
    public DTO() {}
    
//    public void updatePrivileges() {
//        logger.warn("---------------------------------------->Caller#hasPrivilege: Override method");
//    }

    public boolean exists(String value) {

        if(value != null && value.length() > 0)
            return true;
        else
            return false;
    }
    
    public String processURl(String url) {
        try{
            //split url
            String[] url_bits = url.split("&");
            //clean url
            url = "";
            for(int i = 0; i < url_bits.length; i++) {
                //add &amp;
                if(i!=0) url += "&amp;";
                //remove amp;
                url += url_bits[i].replace("amp;", "");
            }
        } catch (Exception e) {
           logger.error("---------------------------------------->DTO#processURl: Failed due to " + e.getMessage() + "\n", e); 
        }
        return url;
    }
    
}
