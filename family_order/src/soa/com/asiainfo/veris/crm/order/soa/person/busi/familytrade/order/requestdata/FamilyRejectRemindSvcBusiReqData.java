
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

public class FamilyRejectRemindSvcBusiReqData extends BaseReqData
{

    private String rejectMode;

    private List<UcaData> mebUcaList = new ArrayList<UcaData>();

    public void addMebUca(UcaData mebUca)
    {
        this.mebUcaList.add(mebUca);
    }

    public List<UcaData> getMebUcaList()
    {
        return mebUcaList;
    }

    public String getRejectMode()
    {
        return rejectMode;
    }

    public void setMebUcaList(List<UcaData> mebUcaList)
    {
        this.mebUcaList = mebUcaList;
    }

    public void setRejectMode(String rejectMode)
    {
        this.rejectMode = rejectMode;
    }

}
