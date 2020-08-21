
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class FamilyShortCodeBusiReqData extends BaseReqData
{

    List<FamilyMebShortCodeData> shortCodeDataList = new ArrayList<FamilyMebShortCodeData>();

    public void addShortCodeDataList(FamilyMebShortCodeData shortCodeData)
    {
        this.shortCodeDataList.add(shortCodeData);
    }

    public List<FamilyMebShortCodeData> getShortCodeDataList()
    {
        return shortCodeDataList;
    }

    public void setShortCodeDataList(List<FamilyMebShortCodeData> shortCodeDataList)
    {
        this.shortCodeDataList = shortCodeDataList;
    }
}
