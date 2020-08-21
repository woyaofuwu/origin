
package com.asiainfo.veris.crm.order.soa.person.busi.vipserver.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.vipserver.AirportVipServerBean;
import com.asiainfo.veris.crm.order.soa.person.busi.vipserver.order.requestdata.AirportVipServerReqData;

public class BuildAirportVipServerIntfReqData extends BaseBuilder implements IBuilder
{

    private String exemptScoreTag;

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {

        AirportVipServerReqData airportVipServerRqData = (AirportVipServerReqData) brd;
        UcaData ucaData = airportVipServerRqData.getUca();
        airportVipServerRqData.setServiceType(param.getString("SVC_LEVEL")); // 服务类型
        airportVipServerRqData.setAirDromeId(param.getString("AIRDROME_ID", "IBOSS")); // 机场ID
        airportVipServerRqData.setAirDromeName(param.getString("AIRDROME_NAME", "机场VIP外地服务"));// 机场名称
        airportVipServerRqData.setPlanLine(param.getString("PLANE_LINE")); // 航班编号

        airportVipServerRqData.setHandingChange(param.getString("HANDING_CHARGE")); // 手续费
        airportVipServerRqData.setServiceChange(param.getString("RSRV_STR1")); // 服务费
        airportVipServerRqData.setIndivInfo(param.getString("INDIV_INFO"));
        airportVipServerRqData.setInnovation(param.getString("INNOVATION"));
        airportVipServerRqData.setFeeBack(param.getString("FEEDBACK"));
        airportVipServerRqData.setAdvices(param.getString("ADVICES"));
        airportVipServerRqData.setOthers(param.getString("OTHERS"));
        airportVipServerRqData.setServiceContent(param.getString("PARA_CODE4"));

        // 已使用的免费服务次数
        // String ifeeCount = "0";
        String oldStartDate = SysDateMgr.getSysTime();
        IDataset freeCountInfos = UserOtherInfoQry.getUserOther(ucaData.getUserId(), "AREM");
        if (IDataUtil.isNotEmpty(freeCountInfos))
        {
            IData freeCountInfo = (IData) freeCountInfos.get(0);
            oldStartDate = freeCountInfo.getString("START_DATE", SysDateMgr.getSysTime());// 旧记录开始时间
            airportVipServerRqData.setOldStartDate(oldStartDate);
            String ifeeCount = freeCountInfo.getString("RSRV_STR1", "0");
            airportVipServerRqData.setFreeCount(Integer.parseInt(ifeeCount));
        }
        // 可使用的免费次数
        int totalFreeCount = getTotalFreeCount(brd);
        airportVipServerRqData.setTotalFreeCount(totalFreeCount); // 总共可使用的免费次数
        // airportVipServerRqData.setTotalFreeCount(Integer.parseInt(param.getString("TOTAL_FREECOUNT", "0")));

        AirportVipServerBean bean = new AirportVipServerBean();
        String score = bean.queryUserScore(ucaData.getUserId()); // 获得用户积分
        airportVipServerRqData.setScore(score);

        computeConsumeScore(param, brd);

    }

    private String calcConsumeMoney(IData param, int pepleCount) throws Exception
    {
        String consumeMoney = "0";
        String serviceType = param.getString("SVC_LEVEL", "");

        if ("1".equals(serviceType))
        {
            consumeMoney = (4000 * 0.015 * 1000 * pepleCount) + ""; // 单位厘
            if (consumeMoney != null)
            {
                consumeMoney = consumeMoney.substring(0, consumeMoney.indexOf("."));
            }
        }
        else if ("2".equals(serviceType))
        {
            consumeMoney = (8000 * 0.015 * 1000 * pepleCount) + ""; // 单位厘
            if (consumeMoney != null)
            {
                consumeMoney = consumeMoney.substring(0, consumeMoney.indexOf("."));
            }
        }
        else if ("3".equals(serviceType))
        {
            consumeMoney = (8000 * 0.015 * 1000 * pepleCount) + ""; // 单位厘
            if (consumeMoney != null)
            {
                consumeMoney = consumeMoney.substring(0, consumeMoney.indexOf("."));
            }
        }
        else if ("4".equals(serviceType))
        {
            consumeMoney = (8000 * 0.015 * 1000 * pepleCount) + ""; // 单位厘
            if (consumeMoney != null)
            {
                consumeMoney = consumeMoney.substring(0, consumeMoney.indexOf("."));
            }
        }

        return consumeMoney;
    }

    /**
     * 计算消耗积分
     * 
     * @author songzy
     * @param pd
     * @param td
     * @throws Exception
     */
    public void computeConsumeScore(IData param, BaseReqData brd) throws Exception
    {
        AirportVipServerReqData airportVipServerRqData = (AirportVipServerReqData) brd;

        // 页面参数
        // String serviceType = param.getString("commInfo_SERVICE_TYPE"); //服务类型
        int total_freecount = airportVipServerRqData.getTotalFreeCount(); // 客户可使用的免费次数
        int freecount = airportVipServerRqData.getFreeCount(); // 已使用的免费服务次数
        // int follow_num = Integer.parseInt(param.getString("commInfo_FOLLOW_NUMBER")); //随行人数
        int follow_num = 0;
        if (param.getString("PARA_CODE1", "").equalsIgnoreCase("06"))
        {
            follow_num = param.getInt("PARA_CODE2", 0);
        }
        int thisSvcCount = (follow_num + 1); // 本次服务次数
        int restFree = (total_freecount - freecount - thisSvcCount); // 剩余免费次数
        String rsrv_str2 = "0"; // 本次消耗的免费次数(对应tf_b_vipairdrome_service.rsrv_str2)
        String consumeScore = "0"; // 消耗积分
        // 本次业务完成后已使用的免费次数 (对应tf_f_user_other.rsrv_str1)
        int rsrv_str1 = ((freecount + thisSvcCount) > total_freecount) ? total_freecount : (freecount + thisSvcCount);
        if (restFree >= 0)
        {
            // consumeScore = "0";
            rsrv_str2 = String.valueOf(thisSvcCount);
        }
        else
        {
            rsrv_str2 = String.valueOf(total_freecount - freecount); // 扣积分和免费次数同时使用的情况（已使用免费次数和本次免费次数之和大于可使用免费次数）
            // 判断VIP用户是否可以免扣积分
            if (!"1".equals(this.getExemptScoreTag()))
            {
                consumeScore = param.getString("RSRV_STR2", "0");
            }
        }

        // 计算省间结算费
        String consumeValue = this.calcConsumeMoney(param, thisSvcCount); // 代扣金额
        airportVipServerRqData.setValueChange(consumeValue);
        airportVipServerRqData.setConsumeScore(consumeScore);
        if (Integer.parseInt(consumeScore) > Integer.parseInt(airportVipServerRqData.getScore()))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_163);
        }
        airportVipServerRqData.setFollowNuber(String.valueOf(follow_num)); // 随行人数
        airportVipServerRqData.setRestCount(restFree);
        airportVipServerRqData.setThisRsrvStr1(rsrv_str1); // 本次业务完成后已使用的免费次数
        airportVipServerRqData.setThisRsrvStr2(param.getString("RSRV_STR20", rsrv_str2));// 一级BOSS传入应扣免费次数，如果没有，则使用自己计算的本次使用的免费次数
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new AirportVipServerReqData();
    }

    public String getExemptScoreTag()
    {
        return exemptScoreTag;
    }

    // 可使用的免费次数
    private int getTotalFreeCount(BaseReqData brd) throws Exception
    {
        AirportVipServerReqData airportVipServerRqData = (AirportVipServerReqData) brd;

        UcaData ucaData = brd.getUca();
        String userProductId = ucaData.getProductId();

        IDataset airportVipInfos = CustVipInfoQry.queryVipInfoBySn(ucaData.getSerialNumber(), "0");
        IData airportVipInfo = new DataMap();
        if (IDataUtil.isNotEmpty(airportVipInfos))
        {
            airportVipInfo = airportVipInfos.getData(0);
        }
        String vip_type_code = airportVipInfo.getString("VIP_TYPE_CODE", "S");
        String vip_class_id = airportVipInfo.getString("VIP_CLASS_ID", "S");

        airportVipServerRqData.setVipId(airportVipInfo.getString("VIP_ID", "0"));
        airportVipServerRqData.setVipTypeCode(vip_type_code);
        airportVipServerRqData.setVipClassId(vip_class_id);
        airportVipServerRqData.setVipNo(airportVipInfo.getString("VIP_CARD_NO"));

        AirportVipServerBean bean = new AirportVipServerBean();
        this.setExemptScoreTag(airportVipInfo.getString("EXEMPT_SCORE_TAG"));
        int totalFreeCount = bean.getTotalFreeCount(vip_type_code, vip_class_id, userProductId);
        return totalFreeCount;
    }

    private IDataset getUserFreeCount(BaseReqData brd) throws Exception
    {
        IDataset freeCountInfos = UserOtherInfoQry.getUserOther(brd.getUca().getUserId(), "AREM");
        return freeCountInfos;
    }

    public void setExemptScoreTag(String exemptScoreTag)
    {
        this.exemptScoreTag = exemptScoreTag;
    }

}
