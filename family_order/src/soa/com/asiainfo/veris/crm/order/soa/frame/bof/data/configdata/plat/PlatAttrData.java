
package com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat;

import java.io.Serializable;
import java.util.ArrayList;

import com.ailk.org.apache.commons.lang3.StringUtils;

public class PlatAttrData implements Serializable
{
    private String infoCode;

    private String serviceId;

    private ArrayList<String> supportOperCode;

    public PlatAttrData(String serviceId, String infoCode, String supportOperCode) throws Exception
    {

        this.serviceId = serviceId;
        this.infoCode = infoCode;
        if (!StringUtils.isBlank(supportOperCode))
        {
            this.supportOperCode = new ArrayList<String>();
            String[] operCodes = supportOperCode.split(",");
            int length = operCodes.length;
            for (int i = 0; i < length; i++)
            {
                this.supportOperCode.add(operCodes[i]);
            }
        }

    }

    public String getInfoCode()
    {

        return infoCode;
    }

    public String getServiceId()
    {

        return serviceId;
    }

    public ArrayList<String> getSupportOperCode()
    {

        return supportOperCode;
    }

}
