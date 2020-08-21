
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.mobileoperation.order.buildrequestdata;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.interboss.mobileoperation.order.requestdata.CrossRegSynRequestData;

public class BuildCrossRegSynRequestData extends BaseBuilder implements IBuilder
{

    private static Logger logger = Logger.getLogger(BuildCrossRegSynRequestData.class);

    public void buildBusiRequestData(IData data, BaseReqData brd) throws Exception
    {

        CrossRegSynRequestData reqData = (CrossRegSynRequestData) brd;
        reqData.setScoreChanged(data.getString("SCORE_VALUE", "0"));// 积分变动值
        String brandCode = data.getString("SCORE_TYPE_CODE");// 积分类型 0－全球通积分；1－动感地带
        if ("G010".endsWith(brandCode))
        {
            reqData.setRsrvStr2("b"); // 动感地带
        }
        else if ("G001".endsWith(brandCode))
        {
            reqData.setRsrvStr2("e"); // 全球通
        }
        reqData.setYearId("ZZZZ");// 修改积分用 参数
        reqData.setVipTypeCode("0");// 固定为个人大客户类型
        reqData.setVipClassId(codeTransfer(data.getString("CLASS_LEVEL")));// 大客户级别
        reqData.setMobilenum(data.getString("MOBILENUM"));

    }

    /**
     * 大客户卡类型转换大客户等级
     * 
     * @param cycle
     * @throws Exception
     */
    private String codeTransfer(String value)
    {
        String vip_id = "";
        if ("1".equals(value))// 钻卡
        {
            vip_id = "4";
        }
        else if ("2".equals(value))// 金卡
        {
            vip_id = "3";

        }
        else if ("3".equals(value))// 银卡
        {
            vip_id = "2";
        }
        else
        {
            vip_id = "0";
        }
        return vip_id;
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new CrossRegSynRequestData();
    }

}
