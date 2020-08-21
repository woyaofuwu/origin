
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

/**
 * @author Administrator
 */
public class OtherTradeData extends BaseTradeData
{
    private String departId;

    private String endDate;

    private String instId;

    private String modifyTag;

    private String processTag;

    private String remark;

    private String rsrvDate1;

    private String rsrvDate10;

    private String rsrvDate2;

    private String rsrvDate3;

    private String rsrvDate4;

    private String rsrvDate5;

    private String rsrvDate6;

    private String rsrvDate7;

    private String rsrvDate8;

    private String rsrvDate9;

    private String rsrvNum1;

    private String rsrvNum10;

    private String rsrvNum11;

    private String rsrvNum12;

    private String rsrvNum13;

    private String rsrvNum14;

    private String rsrvNum15;

    private String rsrvNum16;

    private String rsrvNum17;

    private String rsrvNum18;

    private String rsrvNum19;

    private String rsrvNum2;

    private String rsrvNum20;

    private String rsrvNum3;

    private String rsrvNum4;

    private String rsrvNum5;

    private String rsrvNum6;

    private String rsrvNum7;

    private String rsrvNum8;

    private String rsrvNum9;

    private String rsrvStr1;

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

    private String rsrvStr2;

    private String rsrvStr20;

    private String rsrvStr21;

    private String rsrvStr22;

    private String rsrvStr23;

    private String rsrvStr24;

    private String rsrvStr25;

    private String rsrvStr26;

    private String rsrvStr27;

    private String rsrvStr28;

    private String rsrvStr29;

    private String rsrvStr3;

    private String rsrvStr30;

    private String rsrvStr4;

    private String rsrvStr5;

    private String rsrvStr6;

    private String rsrvStr7;

    private String rsrvStr8;

    private String rsrvStr9;

    private String rsrvTag1;

    private String rsrvTag10;

    private String rsrvTag2;

    private String rsrvTag3;

    private String rsrvTag4;

    private String rsrvTag5;

    private String rsrvTag6;

    private String rsrvTag7;

    private String rsrvTag8;

    private String rsrvTag9;

    private String rsrvValue;

    private String rsrvValueCode;

    private String staffId;

    private String startDate;

    private String userId;

    private String operCode;

    private String isNeedPf;

    public OtherTradeData()
    {

    }

    public OtherTradeData(IData data)
    {
        this.departId = data.getString("DEPART_ID");
        this.endDate = data.getString("END_DATE");
        this.instId = data.getString("INST_ID");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.processTag = data.getString("PROCESS_TAG");
        this.remark = data.getString("REMARK");
        this.rsrvDate1 = data.getString("RSRV_DATE1");
        this.rsrvDate10 = data.getString("RSRV_DATE10");
        this.rsrvDate2 = data.getString("RSRV_DATE2");
        this.rsrvDate3 = data.getString("RSRV_DATE3");
        this.rsrvDate4 = data.getString("RSRV_DATE4");
        this.rsrvDate5 = data.getString("RSRV_DATE5");
        this.rsrvDate6 = data.getString("RSRV_DATE6");
        this.rsrvDate7 = data.getString("RSRV_DATE7");
        this.rsrvDate8 = data.getString("RSRV_DATE8");
        this.rsrvDate9 = data.getString("RSRV_DATE9");
        this.rsrvNum1 = data.getString("RSRV_NUM1");
        this.rsrvNum10 = data.getString("RSRV_NUM10");
        this.rsrvNum11 = data.getString("RSRV_NUM11");
        this.rsrvNum12 = data.getString("RSRV_NUM12");
        this.rsrvNum13 = data.getString("RSRV_NUM13");
        this.rsrvNum14 = data.getString("RSRV_NUM14");
        this.rsrvNum15 = data.getString("RSRV_NUM15");
        this.rsrvNum16 = data.getString("RSRV_NUM16");
        this.rsrvNum17 = data.getString("RSRV_NUM17");
        this.rsrvNum18 = data.getString("RSRV_NUM18");
        this.rsrvNum19 = data.getString("RSRV_NUM19");
        this.rsrvNum2 = data.getString("RSRV_NUM2");
        this.rsrvNum20 = data.getString("RSRV_NUM20");
        this.rsrvNum3 = data.getString("RSRV_NUM3");
        this.rsrvNum4 = data.getString("RSRV_NUM4");
        this.rsrvNum5 = data.getString("RSRV_NUM5");
        this.rsrvNum6 = data.getString("RSRV_NUM6");
        this.rsrvNum7 = data.getString("RSRV_NUM7");
        this.rsrvNum8 = data.getString("RSRV_NUM8");
        this.rsrvNum9 = data.getString("RSRV_NUM9");
        this.rsrvStr1 = data.getString("RSRV_STR1");
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
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr20 = data.getString("RSRV_STR20");
        this.rsrvStr21 = data.getString("RSRV_STR21");
        this.rsrvStr22 = data.getString("RSRV_STR22");
        this.rsrvStr23 = data.getString("RSRV_STR23");
        this.rsrvStr24 = data.getString("RSRV_STR24");
        this.rsrvStr25 = data.getString("RSRV_STR25");
        this.rsrvStr26 = data.getString("RSRV_STR26");
        this.rsrvStr27 = data.getString("RSRV_STR27");
        this.rsrvStr28 = data.getString("RSRV_STR28");
        this.rsrvStr29 = data.getString("RSRV_STR29");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvStr30 = data.getString("RSRV_STR30");
        this.rsrvStr4 = data.getString("RSRV_STR4");
        this.rsrvStr5 = data.getString("RSRV_STR5");
        this.rsrvStr6 = data.getString("RSRV_STR6");
        this.rsrvStr7 = data.getString("RSRV_STR7");
        this.rsrvStr8 = data.getString("RSRV_STR8");
        this.rsrvStr9 = data.getString("RSRV_STR9");
        this.rsrvTag1 = data.getString("RSRV_TAG1");
        this.rsrvTag10 = data.getString("RSRV_TAG10");
        this.rsrvTag2 = data.getString("RSRV_TAG2");
        this.rsrvTag3 = data.getString("RSRV_TAG3");
        this.rsrvTag4 = data.getString("RSRV_TAG4");
        this.rsrvTag5 = data.getString("RSRV_TAG5");
        this.rsrvTag6 = data.getString("RSRV_TAG6");
        this.rsrvTag7 = data.getString("RSRV_TAG7");
        this.rsrvTag8 = data.getString("RSRV_TAG8");
        this.rsrvTag9 = data.getString("RSRV_TAG9");
        this.rsrvValue = data.getString("RSRV_VALUE");
        this.rsrvValueCode = data.getString("RSRV_VALUE_CODE");
        this.staffId = data.getString("STAFF_ID");
        this.startDate = data.getString("START_DATE");
        this.userId = data.getString("USER_ID");
        this.operCode = data.getString("OPER_CODE");
        this.isNeedPf = data.getString("IS_NEED_PF");
    }

    public OtherTradeData clone()
    {
        OtherTradeData OtherTradeData = new OtherTradeData();
        OtherTradeData.setDepartId(this.getDepartId());
        OtherTradeData.setEndDate(this.getEndDate());
        OtherTradeData.setInstId(this.getInstId());
        OtherTradeData.setModifyTag(this.getModifyTag());
        OtherTradeData.setProcessTag(this.getProcessTag());
        OtherTradeData.setRemark(this.getRemark());
        OtherTradeData.setRsrvDate1(this.getRsrvDate1());
        OtherTradeData.setRsrvDate10(this.getRsrvDate10());
        OtherTradeData.setRsrvDate2(this.getRsrvDate2());
        OtherTradeData.setRsrvDate3(this.getRsrvDate3());
        OtherTradeData.setRsrvDate4(this.getRsrvDate4());
        OtherTradeData.setRsrvDate5(this.getRsrvDate5());
        OtherTradeData.setRsrvDate6(this.getRsrvDate6());
        OtherTradeData.setRsrvDate7(this.getRsrvDate7());
        OtherTradeData.setRsrvDate8(this.getRsrvDate8());
        OtherTradeData.setRsrvDate9(this.getRsrvDate9());
        OtherTradeData.setRsrvNum1(this.getRsrvNum1());
        OtherTradeData.setRsrvNum10(this.getRsrvNum10());
        OtherTradeData.setRsrvNum11(this.getRsrvNum11());
        OtherTradeData.setRsrvNum12(this.getRsrvNum12());
        OtherTradeData.setRsrvNum13(this.getRsrvNum13());
        OtherTradeData.setRsrvNum14(this.getRsrvNum14());
        OtherTradeData.setRsrvNum15(this.getRsrvNum15());
        OtherTradeData.setRsrvNum16(this.getRsrvNum16());
        OtherTradeData.setRsrvNum17(this.getRsrvNum17());
        OtherTradeData.setRsrvNum18(this.getRsrvNum18());
        OtherTradeData.setRsrvNum19(this.getRsrvNum19());
        OtherTradeData.setRsrvNum2(this.getRsrvNum2());
        OtherTradeData.setRsrvNum20(this.getRsrvNum20());
        OtherTradeData.setRsrvNum3(this.getRsrvNum3());
        OtherTradeData.setRsrvNum4(this.getRsrvNum4());
        OtherTradeData.setRsrvNum5(this.getRsrvNum5());
        OtherTradeData.setRsrvNum6(this.getRsrvNum6());
        OtherTradeData.setRsrvNum7(this.getRsrvNum7());
        OtherTradeData.setRsrvNum8(this.getRsrvNum8());
        OtherTradeData.setRsrvNum9(this.getRsrvNum9());
        OtherTradeData.setRsrvStr1(this.getRsrvStr1());
        OtherTradeData.setRsrvStr10(this.getRsrvStr10());
        OtherTradeData.setRsrvStr11(this.getRsrvStr11());
        OtherTradeData.setRsrvStr12(this.getRsrvStr12());
        OtherTradeData.setRsrvStr13(this.getRsrvStr13());
        OtherTradeData.setRsrvStr14(this.getRsrvStr14());
        OtherTradeData.setRsrvStr15(this.getRsrvStr15());
        OtherTradeData.setRsrvStr16(this.getRsrvStr16());
        OtherTradeData.setRsrvStr17(this.getRsrvStr17());
        OtherTradeData.setRsrvStr18(this.getRsrvStr18());
        OtherTradeData.setRsrvStr19(this.getRsrvStr19());
        OtherTradeData.setRsrvStr2(this.getRsrvStr2());
        OtherTradeData.setRsrvStr20(this.getRsrvStr20());
        OtherTradeData.setRsrvStr21(this.getRsrvStr21());
        OtherTradeData.setRsrvStr22(this.getRsrvStr22());
        OtherTradeData.setRsrvStr23(this.getRsrvStr23());
        OtherTradeData.setRsrvStr24(this.getRsrvStr24());
        OtherTradeData.setRsrvStr25(this.getRsrvStr25());
        OtherTradeData.setRsrvStr26(this.getRsrvStr26());
        OtherTradeData.setRsrvStr27(this.getRsrvStr27());
        OtherTradeData.setRsrvStr28(this.getRsrvStr28());
        OtherTradeData.setRsrvStr29(this.getRsrvStr29());
        OtherTradeData.setRsrvStr3(this.getRsrvStr3());
        OtherTradeData.setRsrvStr30(this.getRsrvStr30());
        OtherTradeData.setRsrvStr4(this.getRsrvStr4());
        OtherTradeData.setRsrvStr5(this.getRsrvStr5());
        OtherTradeData.setRsrvStr6(this.getRsrvStr6());
        OtherTradeData.setRsrvStr7(this.getRsrvStr7());
        OtherTradeData.setRsrvStr8(this.getRsrvStr8());
        OtherTradeData.setRsrvStr9(this.getRsrvStr9());
        OtherTradeData.setRsrvTag1(this.getRsrvTag1());
        OtherTradeData.setRsrvTag10(this.getRsrvTag10());
        OtherTradeData.setRsrvTag2(this.getRsrvTag2());
        OtherTradeData.setRsrvTag3(this.getRsrvTag3());
        OtherTradeData.setRsrvTag4(this.getRsrvTag4());
        OtherTradeData.setRsrvTag5(this.getRsrvTag5());
        OtherTradeData.setRsrvTag6(this.getRsrvTag6());
        OtherTradeData.setRsrvTag7(this.getRsrvTag7());
        OtherTradeData.setRsrvTag8(this.getRsrvTag8());
        OtherTradeData.setRsrvTag9(this.getRsrvTag9());
        OtherTradeData.setRsrvValue(this.getRsrvValue());
        OtherTradeData.setRsrvValueCode(this.getRsrvValueCode());
        OtherTradeData.setStaffId(this.getStaffId());
        OtherTradeData.setStartDate(this.getStartDate());
        OtherTradeData.setUserId(this.getUserId());
        OtherTradeData.setOperCode(this.getOperCode());
        OtherTradeData.setIsNeedPf(this.getIsNeedPf());
        return OtherTradeData;
    }

    public String getDepartId()
    {
        return departId;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getInstId()
    {
        return instId;
    }

    public String getIsNeedPf()
    {
        return isNeedPf;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getOperCode()
    {
        return operCode;
    }

    public String getProcessTag()
    {
        return processTag;
    }

    public String getRemark()
    {
        return remark;
    }

    public String getRsrvDate1()
    {
        return rsrvDate1;
    }

    public String getRsrvDate10()
    {
        return rsrvDate10;
    }

    public String getRsrvDate2()
    {
        return rsrvDate2;
    }

    public String getRsrvDate3()
    {
        return rsrvDate3;
    }

    public String getRsrvDate4()
    {
        return rsrvDate4;
    }

    public String getRsrvDate5()
    {
        return rsrvDate5;
    }

    public String getRsrvDate6()
    {
        return rsrvDate6;
    }

    public String getRsrvDate7()
    {
        return rsrvDate7;
    }

    public String getRsrvDate8()
    {
        return rsrvDate8;
    }

    public String getRsrvDate9()
    {
        return rsrvDate9;
    }

    public String getRsrvNum1()
    {
        return rsrvNum1;
    }

    public String getRsrvNum10()
    {
        return rsrvNum10;
    }

    public String getRsrvNum11()
    {
        return rsrvNum11;
    }

    public String getRsrvNum12()
    {
        return rsrvNum12;
    }

    public String getRsrvNum13()
    {
        return rsrvNum13;
    }

    public String getRsrvNum14()
    {
        return rsrvNum14;
    }

    public String getRsrvNum15()
    {
        return rsrvNum15;
    }

    public String getRsrvNum16()
    {
        return rsrvNum16;
    }

    public String getRsrvNum17()
    {
        return rsrvNum17;
    }

    public String getRsrvNum18()
    {
        return rsrvNum18;
    }

    public String getRsrvNum19()
    {
        return rsrvNum19;
    }

    public String getRsrvNum2()
    {
        return rsrvNum2;
    }

    public String getRsrvNum20()
    {
        return rsrvNum20;
    }

    public String getRsrvNum3()
    {
        return rsrvNum3;
    }

    public String getRsrvNum4()
    {
        return rsrvNum4;
    }

    public String getRsrvNum5()
    {
        return rsrvNum5;
    }

    public String getRsrvNum6()
    {
        return rsrvNum6;
    }

    public String getRsrvNum7()
    {
        return rsrvNum7;
    }

    public String getRsrvNum8()
    {
        return rsrvNum8;
    }

    public String getRsrvNum9()
    {
        return rsrvNum9;
    }

    public String getRsrvStr1()
    {
        return rsrvStr1;
    }

    public String getRsrvStr10()
    {
        return rsrvStr10;
    }

    public String getRsrvStr11()
    {
        return rsrvStr11;
    }

    public String getRsrvStr12()
    {
        return rsrvStr12;
    }

    public String getRsrvStr13()
    {
        return rsrvStr13;
    }

    public String getRsrvStr14()
    {
        return rsrvStr14;
    }

    public String getRsrvStr15()
    {
        return rsrvStr15;
    }

    public String getRsrvStr16()
    {
        return rsrvStr16;
    }

    public String getRsrvStr17()
    {
        return rsrvStr17;
    }

    public String getRsrvStr18()
    {
        return rsrvStr18;
    }

    public String getRsrvStr19()
    {
        return rsrvStr19;
    }

    public String getRsrvStr2()
    {
        return rsrvStr2;
    }

    public String getRsrvStr20()
    {
        return rsrvStr20;
    }

    public String getRsrvStr21()
    {
        return rsrvStr21;
    }

    public String getRsrvStr22()
    {
        return rsrvStr22;
    }

    public String getRsrvStr23()
    {
        return rsrvStr23;
    }

    public String getRsrvStr24()
    {
        return rsrvStr24;
    }

    public String getRsrvStr25()
    {
        return rsrvStr25;
    }

    public String getRsrvStr26()
    {
        return rsrvStr26;
    }

    public String getRsrvStr27()
    {
        return rsrvStr27;
    }

    public String getRsrvStr28()
    {
        return rsrvStr28;
    }

    public String getRsrvStr29()
    {
        return rsrvStr29;
    }

    public String getRsrvStr3()
    {
        return rsrvStr3;
    }

    public String getRsrvStr30()
    {
        return rsrvStr30;
    }

    public String getRsrvStr4()
    {
        return rsrvStr4;
    }

    public String getRsrvStr5()
    {
        return rsrvStr5;
    }

    public String getRsrvStr6()
    {
        return rsrvStr6;
    }

    public String getRsrvStr7()
    {
        return rsrvStr7;
    }

    public String getRsrvStr8()
    {
        return rsrvStr8;
    }

    public String getRsrvStr9()
    {
        return rsrvStr9;
    }

    public String getRsrvTag1()
    {
        return rsrvTag1;
    }

    public String getRsrvTag10()
    {
        return rsrvTag10;
    }

    public String getRsrvTag2()
    {
        return rsrvTag2;
    }

    public String getRsrvTag3()
    {
        return rsrvTag3;
    }

    public String getRsrvTag4()
    {
        return rsrvTag4;
    }

    public String getRsrvTag5()
    {
        return rsrvTag5;
    }

    public String getRsrvTag6()
    {
        return rsrvTag6;
    }

    public String getRsrvTag7()
    {
        return rsrvTag7;
    }

    public String getRsrvTag8()
    {
        return rsrvTag8;
    }

    public String getRsrvTag9()
    {
        return rsrvTag9;
    }

    public String getRsrvValue()
    {
        return rsrvValue;
    }

    public String getRsrvValueCode()
    {
        return rsrvValueCode;
    }

    public String getStaffId()
    {
        return staffId;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public String getTableName()
    {
        return "TF_B_TRADE_OTHER";
    }

    public String getUserId()
    {
        return userId;
    }

    public void setDepartId(String departId)
    {
        this.departId = departId;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setIsNeedPf(String isNeedPf)
    {
        this.isNeedPf = isNeedPf;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setOperCode(String operCode)
    {
        this.operCode = operCode;
    }

    public void setProcessTag(String processTag)
    {
        this.processTag = processTag;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setRsrvDate1(String rsrvDate1)
    {
        this.rsrvDate1 = rsrvDate1;
    }

    public void setRsrvDate10(String rsrvDate10)
    {
        this.rsrvDate10 = rsrvDate10;
    }

    public void setRsrvDate2(String rsrvDate2)
    {
        this.rsrvDate2 = rsrvDate2;
    }

    public void setRsrvDate3(String rsrvDate3)
    {
        this.rsrvDate3 = rsrvDate3;
    }

    public void setRsrvDate4(String rsrvDate4)
    {
        this.rsrvDate4 = rsrvDate4;
    }

    public void setRsrvDate5(String rsrvDate5)
    {
        this.rsrvDate5 = rsrvDate5;
    }

    public void setRsrvDate6(String rsrvDate6)
    {
        this.rsrvDate6 = rsrvDate6;
    }

    public void setRsrvDate7(String rsrvDate7)
    {
        this.rsrvDate7 = rsrvDate7;
    }

    public void setRsrvDate8(String rsrvDate8)
    {
        this.rsrvDate8 = rsrvDate8;
    }

    public void setRsrvDate9(String rsrvDate9)
    {
        this.rsrvDate9 = rsrvDate9;
    }

    public void setRsrvNum1(String rsrvNum1)
    {
        this.rsrvNum1 = rsrvNum1;
    }

    public void setRsrvNum10(String rsrvNum10)
    {
        this.rsrvNum10 = rsrvNum10;
    }

    public void setRsrvNum11(String rsrvNum11)
    {
        this.rsrvNum11 = rsrvNum11;
    }

    public void setRsrvNum12(String rsrvNum12)
    {
        this.rsrvNum12 = rsrvNum12;
    }

    public void setRsrvNum13(String rsrvNum13)
    {
        this.rsrvNum13 = rsrvNum13;
    }

    public void setRsrvNum14(String rsrvNum14)
    {
        this.rsrvNum14 = rsrvNum14;
    }

    public void setRsrvNum15(String rsrvNum15)
    {
        this.rsrvNum15 = rsrvNum15;
    }

    public void setRsrvNum16(String rsrvNum16)
    {
        this.rsrvNum16 = rsrvNum16;
    }

    public void setRsrvNum17(String rsrvNum17)
    {
        this.rsrvNum17 = rsrvNum17;
    }

    public void setRsrvNum18(String rsrvNum18)
    {
        this.rsrvNum18 = rsrvNum18;
    }

    public void setRsrvNum19(String rsrvNum19)
    {
        this.rsrvNum19 = rsrvNum19;
    }

    public void setRsrvNum2(String rsrvNum2)
    {
        this.rsrvNum2 = rsrvNum2;
    }

    public void setRsrvNum20(String rsrvNum20)
    {
        this.rsrvNum20 = rsrvNum20;
    }

    public void setRsrvNum3(String rsrvNum3)
    {
        this.rsrvNum3 = rsrvNum3;
    }

    public void setRsrvNum4(String rsrvNum4)
    {
        this.rsrvNum4 = rsrvNum4;
    }

    public void setRsrvNum5(String rsrvNum5)
    {
        this.rsrvNum5 = rsrvNum5;
    }

    public void setRsrvNum6(String rsrvNum6)
    {
        this.rsrvNum6 = rsrvNum6;
    }

    public void setRsrvNum7(String rsrvNum7)
    {
        this.rsrvNum7 = rsrvNum7;
    }

    public void setRsrvNum8(String rsrvNum8)
    {
        this.rsrvNum8 = rsrvNum8;
    }

    public void setRsrvNum9(String rsrvNum9)
    {
        this.rsrvNum9 = rsrvNum9;
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

    public void setRsrvStr26(String rsrvStr26)
    {
        this.rsrvStr26 = rsrvStr26;
    }

    public void setRsrvStr27(String rsrvStr27)
    {
        this.rsrvStr27 = rsrvStr27;
    }

    public void setRsrvStr28(String rsrvStr28)
    {
        this.rsrvStr28 = rsrvStr28;
    }

    public void setRsrvStr29(String rsrvStr29)
    {
        this.rsrvStr29 = rsrvStr29;
    }

    public void setRsrvStr3(String rsrvStr3)
    {
        this.rsrvStr3 = rsrvStr3;
    }

    public void setRsrvStr30(String rsrvStr30)
    {
        this.rsrvStr30 = rsrvStr30;
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

    public void setRsrvTag1(String rsrvTag1)
    {
        this.rsrvTag1 = rsrvTag1;
    }

    public void setRsrvTag10(String rsrvTag10)
    {
        this.rsrvTag10 = rsrvTag10;
    }

    public void setRsrvTag2(String rsrvTag2)
    {
        this.rsrvTag2 = rsrvTag2;
    }

    public void setRsrvTag3(String rsrvTag3)
    {
        this.rsrvTag3 = rsrvTag3;
    }

    public void setRsrvTag4(String rsrvTag4)
    {
        this.rsrvTag4 = rsrvTag4;
    }

    public void setRsrvTag5(String rsrvTag5)
    {
        this.rsrvTag5 = rsrvTag5;
    }

    public void setRsrvTag6(String rsrvTag6)
    {
        this.rsrvTag6 = rsrvTag6;
    }

    public void setRsrvTag7(String rsrvTag7)
    {
        this.rsrvTag7 = rsrvTag7;
    }

    public void setRsrvTag8(String rsrvTag8)
    {
        this.rsrvTag8 = rsrvTag8;
    }

    public void setRsrvTag9(String rsrvTag9)
    {
        this.rsrvTag9 = rsrvTag9;
    }

    public void setRsrvValue(String rsrvValue)
    {
        this.rsrvValue = rsrvValue;
    }

    public void setRsrvValueCode(String rsrvValueCode)
    {
        this.rsrvValueCode = rsrvValueCode;
    }

    public void setStaffId(String staffId)
    {
        this.staffId = staffId;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public IData toData()
    {
        IData data = new DataMap();

        data.put("DEPART_ID", this.departId);
        data.put("END_DATE", this.endDate);
        data.put("INST_ID", this.instId);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("PROCESS_TAG", this.processTag);
        data.put("REMARK", this.remark);
        data.put("RSRV_DATE1", this.rsrvDate1);
        data.put("RSRV_DATE10", this.rsrvDate10);
        data.put("RSRV_DATE2", this.rsrvDate2);
        data.put("RSRV_DATE3", this.rsrvDate3);
        data.put("RSRV_DATE4", this.rsrvDate4);
        data.put("RSRV_DATE5", this.rsrvDate5);
        data.put("RSRV_DATE6", this.rsrvDate6);
        data.put("RSRV_DATE7", this.rsrvDate7);
        data.put("RSRV_DATE8", this.rsrvDate8);
        data.put("RSRV_DATE9", this.rsrvDate9);
        data.put("RSRV_NUM1", this.rsrvNum1);
        data.put("RSRV_NUM10", this.rsrvNum10);
        data.put("RSRV_NUM11", this.rsrvNum11);
        data.put("RSRV_NUM12", this.rsrvNum12);
        data.put("RSRV_NUM13", this.rsrvNum13);
        data.put("RSRV_NUM14", this.rsrvNum14);
        data.put("RSRV_NUM15", this.rsrvNum15);
        data.put("RSRV_NUM16", this.rsrvNum16);
        data.put("RSRV_NUM17", this.rsrvNum17);
        data.put("RSRV_NUM18", this.rsrvNum18);
        data.put("RSRV_NUM19", this.rsrvNum19);
        data.put("RSRV_NUM2", this.rsrvNum2);
        data.put("RSRV_NUM20", this.rsrvNum20);
        data.put("RSRV_NUM3", this.rsrvNum3);
        data.put("RSRV_NUM4", this.rsrvNum4);
        data.put("RSRV_NUM5", this.rsrvNum5);
        data.put("RSRV_NUM6", this.rsrvNum6);
        data.put("RSRV_NUM7", this.rsrvNum7);
        data.put("RSRV_NUM8", this.rsrvNum8);
        data.put("RSRV_NUM9", this.rsrvNum9);
        data.put("RSRV_STR1", this.rsrvStr1);
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
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR20", this.rsrvStr20);
        data.put("RSRV_STR21", this.rsrvStr21);
        data.put("RSRV_STR22", this.rsrvStr22);
        data.put("RSRV_STR23", this.rsrvStr23);
        data.put("RSRV_STR24", this.rsrvStr24);
        data.put("RSRV_STR25", this.rsrvStr25);
        data.put("RSRV_STR26", this.rsrvStr26);
        data.put("RSRV_STR27", this.rsrvStr27);
        data.put("RSRV_STR28", this.rsrvStr28);
        data.put("RSRV_STR29", this.rsrvStr29);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_STR30", this.rsrvStr30);
        data.put("RSRV_STR4", this.rsrvStr4);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("RSRV_STR6", this.rsrvStr6);
        data.put("RSRV_STR7", this.rsrvStr7);
        data.put("RSRV_STR8", this.rsrvStr8);
        data.put("RSRV_STR9", this.rsrvStr9);
        data.put("RSRV_TAG1", this.rsrvTag1);
        data.put("RSRV_TAG10", this.rsrvTag10);
        data.put("RSRV_TAG2", this.rsrvTag2);
        data.put("RSRV_TAG3", this.rsrvTag3);
        data.put("RSRV_TAG4", this.rsrvTag4);
        data.put("RSRV_TAG5", this.rsrvTag5);
        data.put("RSRV_TAG6", this.rsrvTag6);
        data.put("RSRV_TAG7", this.rsrvTag7);
        data.put("RSRV_TAG8", this.rsrvTag8);
        data.put("RSRV_TAG9", this.rsrvTag9);
        data.put("RSRV_VALUE", this.rsrvValue);
        data.put("RSRV_VALUE_CODE", this.rsrvValueCode);
        data.put("STAFF_ID", this.staffId);
        data.put("START_DATE", this.startDate);
        data.put("USER_ID", this.userId);
        data.put("OPER_CODE", this.operCode);
        data.put("IS_NEED_PF", this.isNeedPf);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
