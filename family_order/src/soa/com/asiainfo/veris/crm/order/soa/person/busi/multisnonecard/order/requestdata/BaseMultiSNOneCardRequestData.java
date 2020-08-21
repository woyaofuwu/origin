
package com.asiainfo.veris.crm.order.soa.person.busi.multisnonecard.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class BaseMultiSNOneCardRequestData extends BaseReqData
{
    private String oper_type;

    private String service_type;

    private String deputy_sn_input;

    private String cooper_area;

    private String cooper_net;

    private String max_fee;

    private String sum_fee;

    private String auth_serial_number;

    private String vaild_date;

    private String invaild_date;

    private String imsi;

    private String serialNumberO;

    private String imsiNew;

    public String getAuth_serial_number()
    {
        return auth_serial_number;
    }

    public String getCooper_area()
    {
        return cooper_area;
    }

    public String getCooper_net()
    {
        return cooper_net;
    }

    public String getDeputy_sn_input()
    {
        return deputy_sn_input;
    }

    public String getImsi()
    {
        return imsi;
    }

    public String getImsiNew()
    {
        return imsiNew;
    }

    public String getInvaild_date()
    {
        return invaild_date;
    }

    public String getMax_fee()
    {
        return max_fee;
    }

    public String getOper_type()
    {
        return oper_type;
    }

    public String getSerialNumberO()
    {
        return serialNumberO;
    }

    public String getService_type()
    {
        return service_type;
    }

    public String getSum_fee()
    {
        return sum_fee;
    }

    public String getVaild_date()
    {
        return vaild_date;
    }

    public void setAuth_serial_number(String authSerialNumber)
    {
        auth_serial_number = authSerialNumber;
    }

    public void setCooper_area(String cooperArea)
    {
        cooper_area = cooperArea;
    }

    public void setCooper_net(String cooperNet)
    {
        cooper_net = cooperNet;
    }

    public void setDeputy_sn_input(String deputySnInput)
    {
        deputy_sn_input = deputySnInput;
    }

    public void setImsi(String imsi)
    {
        this.imsi = imsi;
    }

    public void setImsiNew(String imsiNew)
    {
        this.imsiNew = imsiNew;
    }

    public void setInvaild_date(String invaildDate)
    {
        invaild_date = invaildDate;
    }

    public void setMax_fee(String maxFee)
    {
        max_fee = maxFee;
    }

    public void setOper_type(String operType)
    {
        oper_type = operType;
    }

    public void setSerialNumberO(String serialNumberO)
    {
        this.serialNumberO = serialNumberO;
    }

    public void setService_type(String serviceType)
    {
        service_type = serviceType;
    }

    public void setSum_fee(String sumFee)
    {
        sum_fee = sumFee;
    }

    public void setVaild_date(String vaildDate)
    {
        vaild_date = vaildDate;
    }

}
