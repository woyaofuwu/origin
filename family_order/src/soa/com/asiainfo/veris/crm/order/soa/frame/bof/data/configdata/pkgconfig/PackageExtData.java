
package com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.pkgconfig;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class PackageExtData extends BaseTradeData
{

    private String packageId;

    private String extDesc;

    private String enableTag;

    private String startAbsoluteDate;

    private String startOffset;

    private String startUnit;

    private String endEnableTag;

    private String endAbsoluteDate;

    private String endOffset;

    private String endUnit;

    private String cancelTag;

    private String cancelAbsoluteDate;

    private String cancelOffset;

    private String cancelUnit;

    private String startDate;

    private String endDate;

    private String rightCode;

    private String eparchyCode;

    private String remark;

    private String condFactor1;

    private String condFactor2;

    private String condFactor3;

    private String tagSet1;

    private String tagSet2;

    private String tagSet3;

    private String rsrvInfo1;

    private String rsrvInfo2;

    private String rsrvInfo3;

    private String rsrvInfo4;

    private String rsrvInfo5;

    private String rsrvStr1;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvStr4;

    private String rsrvStr5;

    private String rsrvStr6;

    private String rsrvStr7;

    private String rsrvStr8;

    private String rsrvStr9;

    private String rsrvStr10;

    private String rsrvStr11;

    private String rsrvStr12;

    private String rsrvStr13;

    private String rsrvStr14;

    private String rsrvStr15;

    private String rsrvStr16;

    private String rsrvStr17;

    private String rsrvStr18;

    private String rsrvStr19;

    private String rsrvStr20;

    private String rsrvStr21;

    private String rsrvStr22;

    private String rsrvStr23;

    private String rsrvStr24;

    private String rsrvStr25;

    private String rsrvDate1;

    private String rsrvDate2;

    private String rsrvDate3;

    public PackageExtData()
    {

    }

    public PackageExtData(IData data)
    {
        this.packageId = data.getString("PACKAGE_ID");
        this.extDesc = data.getString("EXT_DESC");
        this.enableTag = data.getString("ENABLE_TAG");
        this.startAbsoluteDate = data.getString("START_ABSOLUTE_DATE");
        this.startOffset = data.getString("START_OFFSET");
        this.startUnit = data.getString("START_UNIT");
        this.endEnableTag = data.getString("END_ENABLE_TAG");
        this.endAbsoluteDate = data.getString("END_ABSOLUTE_DATE");
        this.endOffset = data.getString("END_OFFSET");
        this.endUnit = data.getString("END_UNIT");
        this.cancelTag = data.getString("CANCEL_TAG");
        this.cancelAbsoluteDate = data.getString("CANCEL_ABSOLUTE_DATE");
        this.cancelOffset = data.getString("CANCEL_OFFSET");
        this.cancelUnit = data.getString("CANCEL_UNIT");
        this.startDate = data.getString("START_DATE");
        this.endDate = data.getString("END_DATE");
        this.rightCode = data.getString("RIGHT_CODE");
        this.eparchyCode = data.getString("EPARCHY_CODE");
        this.remark = data.getString("REMARK");
        this.condFactor1 = data.getString("COND_FACTOR1");
        this.condFactor2 = data.getString("COND_FACTOR2");
        this.condFactor3 = data.getString("COND_FACTOR3");
        this.tagSet1 = data.getString("TAG_SET1");
        this.tagSet2 = data.getString("TAG_SET2");
        this.tagSet3 = data.getString("TAG_SET3");
        this.rsrvInfo1 = data.getString("RSRV_INFO1");
        this.rsrvInfo2 = data.getString("RSRV_INFO2");
        this.rsrvInfo3 = data.getString("RSRV_INFO3");
        this.rsrvInfo4 = data.getString("RSRV_INFO4");
        this.rsrvInfo5 = data.getString("RSRV_INFO5");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvStr4 = data.getString("RSRV_STR4");
        this.rsrvStr5 = data.getString("RSRV_STR5");
        this.rsrvStr6 = data.getString("RSRV_STR6");
        this.rsrvStr7 = data.getString("RSRV_STR7");
        this.rsrvStr8 = data.getString("RSRV_STR8");
        this.rsrvStr9 = data.getString("RSRV_STR9");
        this.rsrvStr10 = data.getString("RSRV_STR10");
        this.rsrvStr11 = data.getString("RSRV_STR11");
        this.rsrvStr12 = data.getString("RSRV_STR12");
        this.rsrvStr13 = data.getString("RSRV_STR13");
        this.rsrvStr14 = data.getString("RSRV_STR14");
        this.rsrvStr15 = data.getString("RSRV_STR15");
        this.rsrvStr16 = data.getString("RSRV_STR16");
        this.rsrvStr17 = data.getString("RSRV_STR17");
        this.rsrvStr18 = data.getString("RSRV_STR18");
        this.rsrvStr19 = data.getString("RSRV_STR19");
        this.rsrvStr20 = data.getString("RSRV_STR20");
        this.rsrvStr21 = data.getString("RSRV_STR21");
        this.rsrvStr22 = data.getString("RSRV_STR22");
        this.rsrvStr23 = data.getString("RSRV_STR23");
        this.rsrvStr24 = data.getString("RSRV_STR24");
        this.rsrvStr25 = data.getString("RSRV_STR25");
        this.rsrvDate1 = data.getString("RSRV_DATE1");
        this.rsrvDate2 = data.getString("RSRV_DATE2");
        this.rsrvDate3 = data.getString("RSRV_DATE3");
    }

    public String getCancelAbsoluteDate()
    {
        return this.cancelAbsoluteDate;
    }

    public String getCancelOffset()
    {
        return this.cancelOffset;
    }

    public String getCancelTag()
    {
        return this.cancelTag;
    }

    public String getCancelUnit()
    {
        return this.cancelUnit;
    }

    public String getCondFactor1()
    {
        return this.condFactor1;
    }

    public String getCondFactor2()
    {
        return this.condFactor2;
    }

    public String getCondFactor3()
    {
        return this.condFactor3;
    }

    public String getEnableTag()
    {
        return this.enableTag;
    }

    public String getEndAbsoluteDate()
    {
        return this.endAbsoluteDate;
    }

    public String getEndDate()
    {
        return this.endDate;
    }

    public String getEndEnableTag()
    {
        return this.endEnableTag;
    }

    public String getEndOffset()
    {
        return this.endOffset;
    }

    public String getEndUnit()
    {
        return this.endUnit;
    }

    public String getEparchyCode()
    {
        return this.eparchyCode;
    }

    public String getExtDesc()
    {
        return this.extDesc;
    }

    public String getPackageId()
    {
        return this.packageId;
    }

    public String getRemark()
    {
        return this.remark;
    }

    public String getRightCode()
    {
        return this.rightCode;
    }

    public String getRsrvDate1()
    {
        return this.rsrvDate1;
    }

    public String getRsrvDate2()
    {
        return this.rsrvDate2;
    }

    public String getRsrvDate3()
    {
        return this.rsrvDate3;
    }

    public String getRsrvInfo1()
    {
        return this.rsrvInfo1;
    }

    public String getRsrvInfo2()
    {
        return this.rsrvInfo2;
    }

    public String getRsrvInfo3()
    {
        return this.rsrvInfo3;
    }

    public String getRsrvInfo4()
    {
        return this.rsrvInfo4;
    }

    public String getRsrvInfo5()
    {
        return this.rsrvInfo5;
    }

    public String getRsrvStr1()
    {
        return this.rsrvStr1;
    }

    public String getRsrvStr10()
    {
        return this.rsrvStr10;
    }

    public String getRsrvStr11()
    {
        return this.rsrvStr11;
    }

    public String getRsrvStr12()
    {
        return this.rsrvStr12;
    }

    public String getRsrvStr13()
    {
        return this.rsrvStr13;
    }

    public String getRsrvStr14()
    {
        return this.rsrvStr14;
    }

    public String getRsrvStr15()
    {
        return this.rsrvStr15;
    }

    public String getRsrvStr16()
    {
        return this.rsrvStr16;
    }

    public String getRsrvStr17()
    {
        return this.rsrvStr17;
    }

    public String getRsrvStr18()
    {
        return this.rsrvStr18;
    }

    public String getRsrvStr19()
    {
        return this.rsrvStr19;
    }

    public String getRsrvStr2()
    {
        return this.rsrvStr2;
    }

    public String getRsrvStr20()
    {
        return this.rsrvStr20;
    }

    public String getRsrvStr21()
    {
        return this.rsrvStr21;
    }

    public String getRsrvStr22()
    {
        return this.rsrvStr22;
    }

    public String getRsrvStr23()
    {
        return this.rsrvStr23;
    }

    public String getRsrvStr24()
    {
        return this.rsrvStr24;
    }

    public String getRsrvStr25()
    {
        return this.rsrvStr25;
    }

    public String getRsrvStr3()
    {
        return this.rsrvStr3;
    }

    public String getRsrvStr4()
    {
        return this.rsrvStr4;
    }

    public String getRsrvStr5()
    {
        return this.rsrvStr5;
    }

    public String getRsrvStr6()
    {
        return this.rsrvStr6;
    }

    public String getRsrvStr7()
    {
        return this.rsrvStr7;
    }

    public String getRsrvStr8()
    {
        return this.rsrvStr8;
    }

    public String getRsrvStr9()
    {
        return this.rsrvStr9;
    }

    public String getStartAbsoluteDate()
    {
        return this.startAbsoluteDate;
    }

    public String getStartDate()
    {
        return this.startDate;
    }

    public String getStartOffset()
    {
        return this.startOffset;
    }

    public String getStartUnit()
    {
        return this.startUnit;
    }

    public String getTableName()
    {
        return "TD_B_PACKAGE_EXT";
    }

    public String getTagSet1()
    {
        return this.tagSet1;
    }

    public String getTagSet2()
    {
        return this.tagSet2;
    }

    public String getTagSet3()
    {
        return this.tagSet3;
    }

    public void setCancelAbsoluteDate(String cancelAbsoluteDate)
    {
        this.cancelAbsoluteDate = cancelAbsoluteDate;
    }

    public void setCancelOffset(String cancelOffset)
    {
        this.cancelOffset = cancelOffset;
    }

    public void setCancelTag(String cancelTag)
    {
        this.cancelTag = cancelTag;
    }

    public void setCancelUnit(String cancelUnit)
    {
        this.cancelUnit = cancelUnit;
    }

    public void setCondFactor1(String condFactor1)
    {
        this.condFactor1 = condFactor1;
    }

    public void setCondFactor2(String condFactor2)
    {
        this.condFactor2 = condFactor2;
    }

    public void setCondFactor3(String condFactor3)
    {
        this.condFactor3 = condFactor3;
    }

    public void setEnableTag(String enableTag)
    {
        this.enableTag = enableTag;
    }

    public void setEndAbsoluteDate(String endAbsoluteDate)
    {
        this.endAbsoluteDate = endAbsoluteDate;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setEndEnableTag(String endEnableTag)
    {
        this.endEnableTag = endEnableTag;
    }

    public void setEndOffset(String endOffset)
    {
        this.endOffset = endOffset;
    }

    public void setEndUnit(String endUnit)
    {
        this.endUnit = endUnit;
    }

    public void setEparchyCode(String eparchyCode)
    {
        this.eparchyCode = eparchyCode;
    }

    public void setExtDesc(String extDesc)
    {
        this.extDesc = extDesc;
    }

    public void setPackageId(String packageId)
    {
        this.packageId = packageId;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setRightCode(String rightCode)
    {
        this.rightCode = rightCode;
    }

    public void setRsrvDate1(String rsrvDate1)
    {
        this.rsrvDate1 = rsrvDate1;
    }

    public void setRsrvDate2(String rsrvDate2)
    {
        this.rsrvDate2 = rsrvDate2;
    }

    public void setRsrvDate3(String rsrvDate3)
    {
        this.rsrvDate3 = rsrvDate3;
    }

    public void setRsrvInfo1(String rsrvInfo1)
    {
        this.rsrvInfo1 = rsrvInfo1;
    }

    public void setRsrvInfo2(String rsrvInfo2)
    {
        this.rsrvInfo2 = rsrvInfo2;
    }

    public void setRsrvInfo3(String rsrvInfo3)
    {
        this.rsrvInfo3 = rsrvInfo3;
    }

    public void setRsrvInfo4(String rsrvInfo4)
    {
        this.rsrvInfo4 = rsrvInfo4;
    }

    public void setRsrvInfo5(String rsrvInfo5)
    {
        this.rsrvInfo5 = rsrvInfo5;
    }

    public void setRsrvStr1(String rsrvStr1)
    {
        this.rsrvStr1 = rsrvStr1;
    }

    public void setRsrvStr10(String rsrvStr10)
    {
        this.rsrvStr10 = rsrvStr10;
    }

    public void setRsrvStr11(String rsrvStr11)
    {
        this.rsrvStr11 = rsrvStr11;
    }

    public void setRsrvStr12(String rsrvStr12)
    {
        this.rsrvStr12 = rsrvStr12;
    }

    public void setRsrvStr13(String rsrvStr13)
    {
        this.rsrvStr13 = rsrvStr13;
    }

    public void setRsrvStr14(String rsrvStr14)
    {
        this.rsrvStr14 = rsrvStr14;
    }

    public void setRsrvStr15(String rsrvStr15)
    {
        this.rsrvStr15 = rsrvStr15;
    }

    public void setRsrvStr16(String rsrvStr16)
    {
        this.rsrvStr16 = rsrvStr16;
    }

    public void setRsrvStr17(String rsrvStr17)
    {
        this.rsrvStr17 = rsrvStr17;
    }

    public void setRsrvStr18(String rsrvStr18)
    {
        this.rsrvStr18 = rsrvStr18;
    }

    public void setRsrvStr19(String rsrvStr19)
    {
        this.rsrvStr19 = rsrvStr19;
    }

    public void setRsrvStr2(String rsrvStr2)
    {
        this.rsrvStr2 = rsrvStr2;
    }

    public void setRsrvStr20(String rsrvStr20)
    {
        this.rsrvStr20 = rsrvStr20;
    }

    public void setRsrvStr21(String rsrvStr21)
    {
        this.rsrvStr21 = rsrvStr21;
    }

    public void setRsrvStr22(String rsrvStr22)
    {
        this.rsrvStr22 = rsrvStr22;
    }

    public void setRsrvStr23(String rsrvStr23)
    {
        this.rsrvStr23 = rsrvStr23;
    }

    public void setRsrvStr24(String rsrvStr24)
    {
        this.rsrvStr24 = rsrvStr24;
    }

    public void setRsrvStr25(String rsrvStr25)
    {
        this.rsrvStr25 = rsrvStr25;
    }

    public void setRsrvStr3(String rsrvStr3)
    {
        this.rsrvStr3 = rsrvStr3;
    }

    public void setRsrvStr4(String rsrvStr4)
    {
        this.rsrvStr4 = rsrvStr4;
    }

    public void setRsrvStr5(String rsrvStr5)
    {
        this.rsrvStr5 = rsrvStr5;
    }

    public void setRsrvStr6(String rsrvStr6)
    {
        this.rsrvStr6 = rsrvStr6;
    }

    public void setRsrvStr7(String rsrvStr7)
    {
        this.rsrvStr7 = rsrvStr7;
    }

    public void setRsrvStr8(String rsrvStr8)
    {
        this.rsrvStr8 = rsrvStr8;
    }

    public void setRsrvStr9(String rsrvStr9)
    {
        this.rsrvStr9 = rsrvStr9;
    }

    public void setStartAbsoluteDate(String startAbsoluteDate)
    {
        this.startAbsoluteDate = startAbsoluteDate;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public void setStartOffset(String startOffset)
    {
        this.startOffset = startOffset;
    }

    public void setStartUnit(String startUnit)
    {
        this.startUnit = startUnit;
    }

    public void setTagSet1(String tagSet1)
    {
        this.tagSet1 = tagSet1;
    }

    public void setTagSet2(String tagSet2)
    {
        this.tagSet2 = tagSet2;
    }

    public void setTagSet3(String tagSet3)
    {
        this.tagSet3 = tagSet3;
    }

    public IData toData()
    {
        IData data = new DataMap();
        data.put("PACKAGE_ID", this.packageId);
        data.put("EXT_DESC", this.extDesc);
        data.put("ENABLE_TAG", this.enableTag);
        data.put("START_ABSOLUTE_DATE", this.startAbsoluteDate);
        data.put("START_OFFSET", this.startOffset);
        data.put("START_UNIT", this.startUnit);
        data.put("END_ENABLE_TAG", this.endEnableTag);
        data.put("END_ABSOLUTE_DATE", this.endAbsoluteDate);
        data.put("END_OFFSET", this.endOffset);
        data.put("END_UNIT", this.endUnit);
        data.put("CANCEL_TAG", this.cancelTag);
        data.put("CANCEL_ABSOLUTE_DATE", this.cancelAbsoluteDate);
        data.put("CANCEL_OFFSET", this.cancelOffset);
        data.put("CANCEL_UNIT", this.cancelUnit);
        data.put("START_DATE", this.startDate);
        data.put("END_DATE", this.endDate);
        data.put("RIGHT_CODE", this.rightCode);
        data.put("EPARCHY_CODE", this.eparchyCode);
        data.put("REMARK", this.remark);
        data.put("COND_FACTOR1", this.condFactor1);
        data.put("COND_FACTOR2", this.condFactor2);
        data.put("COND_FACTOR3", this.condFactor3);
        data.put("TAG_SET1", this.tagSet1);
        data.put("TAG_SET2", this.tagSet2);
        data.put("TAG_SET3", this.tagSet3);
        data.put("RSRV_INFO1", this.rsrvInfo1);
        data.put("RSRV_INFO2", this.rsrvInfo2);
        data.put("RSRV_INFO3", this.rsrvInfo3);
        data.put("RSRV_INFO4", this.rsrvInfo4);
        data.put("RSRV_INFO5", this.rsrvInfo5);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_STR4", this.rsrvStr4);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("RSRV_STR6", this.rsrvStr6);
        data.put("RSRV_STR7", this.rsrvStr7);
        data.put("RSRV_STR8", this.rsrvStr8);
        data.put("RSRV_STR9", this.rsrvStr9);
        data.put("RSRV_STR10", this.rsrvStr10);
        data.put("RSRV_STR11", this.rsrvStr11);
        data.put("RSRV_STR12", this.rsrvStr12);
        data.put("RSRV_STR13", this.rsrvStr13);
        data.put("RSRV_STR14", this.rsrvStr14);
        data.put("RSRV_STR15", this.rsrvStr15);
        data.put("RSRV_STR16", this.rsrvStr16);
        data.put("RSRV_STR17", this.rsrvStr17);
        data.put("RSRV_STR18", this.rsrvStr18);
        data.put("RSRV_STR19", this.rsrvStr19);
        data.put("RSRV_STR20", this.rsrvStr20);
        data.put("RSRV_STR21", this.rsrvStr21);
        data.put("RSRV_STR22", this.rsrvStr22);
        data.put("RSRV_STR23", this.rsrvStr23);
        data.put("RSRV_STR24", this.rsrvStr24);
        data.put("RSRV_STR25", this.rsrvStr25);
        data.put("RSRV_DATE1", this.rsrvDate1);
        data.put("RSRV_DATE2", this.rsrvDate2);
        data.put("RSRV_DATE3", this.rsrvDate3);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
