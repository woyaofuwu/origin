
package com.asiainfo.veris.crm.order.soa.group.vpmn;

import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElementReqData;

public class AdvPayChgReqData extends ChangeMemElementReqData
{
    private String START_CYCLE_ID;

    private String END_CYCLE_ID;

    private String DEL_CYCLE_ID;

    private String OPER_TYPE;

    private String COMPLEMENT_TAG;

    private String LIMIT;

    private String LIMIT_TYPE;

    private String PAYITEM_CODE;

    private String ACCT_ID;

    public String getLIMIT()
    {
        return LIMIT;
    }

    public void setLIMIT(String lIMIT)
    {
        LIMIT = lIMIT;
    }

    public String getLIMIT_TYPE()
    {
        return LIMIT_TYPE;
    }

    public void setLIMIT_TYPE(String lIMIT_TYPE)
    {
        LIMIT_TYPE = lIMIT_TYPE;
    }

    public String getPAYITEM_CODE()
    {
        return PAYITEM_CODE;
    }

    public void setPAYITEM_CODE(String pAYITEM_CODE)
    {
        PAYITEM_CODE = pAYITEM_CODE;
    }

    public String getACCT_ID()
    {
        return ACCT_ID;
    }

    public void setACCT_ID(String aCCT_ID)
    {
        ACCT_ID = aCCT_ID;
    }

    public String getCOMPLEMENT_TAG()
    {
        return COMPLEMENT_TAG;
    }

    public void setCOMPLEMENT_TAG(String cOMPLEMENT_TAG)
    {
        COMPLEMENT_TAG = cOMPLEMENT_TAG;
    }

    public String getOPER_TYPE()
    {
        return OPER_TYPE;
    }

    public void setOPER_TYPE(String oPER_TYPE)
    {
        OPER_TYPE = oPER_TYPE;
    }

    public String getDEL_CYCLE_ID()
    {
        return DEL_CYCLE_ID;
    }

    public void setDEL_CYCLE_ID(String dEL_CYCLE_ID)
    {
        DEL_CYCLE_ID = dEL_CYCLE_ID;
    }

    public String getSTART_CYCLE_ID()
    {
        return START_CYCLE_ID;
    }

    public void setSTART_CYCLE_ID(String sTART_CYCLE_ID)
    {
        START_CYCLE_ID = sTART_CYCLE_ID;
    }

    public String getEND_CYCLE_ID()
    {
        return END_CYCLE_ID;
    }

    public void setEND_CYCLE_ID(String eND_CYCLE_ID)
    {
        END_CYCLE_ID = eND_CYCLE_ID;
    }

}
