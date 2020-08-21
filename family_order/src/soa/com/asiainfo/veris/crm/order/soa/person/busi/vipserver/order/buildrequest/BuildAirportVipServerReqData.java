
package com.asiainfo.veris.crm.order.soa.person.busi.vipserver.order.buildrequest;

import java.util.Iterator;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.vipserver.AirportVipServerBean;
import com.asiainfo.veris.crm.order.soa.person.busi.vipserver.order.requestdata.AirportVipServerReqData;

public class BuildAirportVipServerReqData extends BaseBuilder implements IBuilder
{

    private String exemptScoreTag;

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {

        AirportVipServerReqData airportVipServerRqData = (AirportVipServerReqData) brd;
        airportVipServerRqData.setServiceType(param.getString("commInfo_SERVICE_TYPE")); // 服务类型
        airportVipServerRqData.setAirDromeId(param.getString("commInfo_AIRDROME_ID")); // 机场ID
        airportVipServerRqData.setPlanLine(param.getString("commInfo_PLANE_LINE")); // 航班编号
        String followNumber = param.getString("commInfo_FOLLOW_NUMBER");
        if (StringUtils.isNotBlank(followNumber) && Integer.parseInt(followNumber) > 1)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "最多带一个随员!");
        }
        airportVipServerRqData.setFollowNuber(followNumber); // 随行人数
        String handingChange = param.getString("commInfo_HANDING_CHARGE");
        if (StringUtils.isEmpty(handingChange))
        {
            handingChange = "0";
        }
        String serviceChange = param.getString("commInfo_SERVICE_CHARGE");
        if (StringUtils.isEmpty(serviceChange))
        {
            serviceChange = "0";
        }
        airportVipServerRqData.setHandingChange(handingChange); // 手续费
        airportVipServerRqData.setServiceChange(serviceChange); // 服务费
        airportVipServerRqData.setIndivInfo(param.getString("commInfo_INDIV_INFO"));
        airportVipServerRqData.setInnovation(param.getString("commInfo_INNOVATION"));
        airportVipServerRqData.setFeeBack(param.getString("commInfo_FEEDBACK"));
        airportVipServerRqData.setAdvices(param.getString("commInfo_ADVICES"));
        airportVipServerRqData.setOthers(param.getString("commInfo_OTHERS"));
        airportVipServerRqData.setServiceContent(param.getString("commInfo_SERVICE_CONTENT"));

        // 已使用的免费服务次数
        String ifeeCount = "0";
        String oldStartDate = SysDateMgr.getSysTime();
        IDataset freeCountInfos = getUserFreeCount(brd);
        if (freeCountInfos != null && freeCountInfos.size() > 0)
        {
            IData freeCountInfo = freeCountInfos.getData(0);
            oldStartDate = freeCountInfo.getString("START_DATE", SysDateMgr.getSysTime());// 旧记录开始时间
            ifeeCount = freeCountInfo.getString("RSRV_STR1", "0");
        }

        // 可使用的免费次数
        int totalFreeCount = getTotalFreeCount(brd);
        int feeCount = Integer.parseInt(ifeeCount) > totalFreeCount ? totalFreeCount : Integer.parseInt(ifeeCount);

        // airportVipServerRqData.setFreeCount(feeCount);
        // airportVipServerRqData.setTotalFreeCount(totalFreeCount);
        // airportVipServerRqData.setOldStartDate(oldStartDate);

        airportVipServerRqData.setFreeCount(Integer.parseInt(param.getString("FREE_COUNT", "0")));
        airportVipServerRqData.setTotalFreeCount(Integer.parseInt(param.getString("TOTAL_FREECOUNT", "0")));
        airportVipServerRqData.setOldStartDate(param.getString("OLD_START_DATE"));

        AirportVipServerBean bean = new AirportVipServerBean();
        String serialNumber = brd.getUca().getSerialNumber();
        String score = param.getString("SCORE");
        airportVipServerRqData.setScore(score);

        computeConsumeScore(param, brd);
        getAirDromeName(brd);
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
        String serviceType = param.getString("commInfo_SERVICE_TYPE"); // 服务类型
        int total_freecount = airportVipServerRqData.getTotalFreeCount(); // 客户可使用的免费次数
        int freecount = airportVipServerRqData.getFreeCount(); // 已使用的免费服务次数
        int follow_num = Integer.parseInt(param.getString("commInfo_FOLLOW_NUMBER")); // 随行人数
        int thisSvcCount = (follow_num + 1); // 本次服务次数
        int restFree = (total_freecount - freecount - thisSvcCount); // 剩余免费次数
        String rsrv_str2 = "0"; // 本次消耗的免费次数(对应tf_b_vipairdrome_service.rsrv_str2)
        String consumeScore = "0"; // 消耗积分
        // 本次业务完成后已使用的免费次数 (对应tf_f_user_other.rsrv_str1)
        int rsrv_str1 = ((freecount + thisSvcCount) > total_freecount) ? total_freecount : (freecount + thisSvcCount);
        if (restFree >= 0)
        {
            consumeScore = "0";
            rsrv_str2 = String.valueOf(thisSvcCount);
        }
        else
        {
            rsrv_str2 = String.valueOf(total_freecount - freecount); // 扣积分和免费次数同时使用的情况（已使用免费次数和本次免费次数之和大于可使用免费次数）
            // 判断VIP用户是否可以免扣积分
            if (!"1".equals(this.getExemptScoreTag()))
            {
                // 服务类别对应扣减积分信息
                IDataset paramset = ParamInfoQry.getCommparaByCode("CSM", "989", "", CSBizBean.getTradeEparchyCode());
                // 计算服务类型应扣的积分
                /**
                 * 1 第一类服务(国内) 600 2 第二类服务(国内) 1000 3 第一类服务(国际) 1500 4 第二类服务(国际) 2500
                 */
                if (paramset != null && paramset.size() > 0)
                {
                    for (Iterator it = paramset.iterator(); it.hasNext();)
                    {
                        IData data = (IData) it.next();
                        if (serviceType.equals(data.get("PARAM_CODE")))
                        {
                            consumeScore = String.valueOf(-1 * restFree * data.getInt("PARA_CODE1", 0));
                        }
                    }
                }
            }
        }

        airportVipServerRqData.setConsumeScore(consumeScore);
        if (Integer.parseInt(consumeScore) > Integer.parseInt(airportVipServerRqData.getScore()))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_163);
        }
        airportVipServerRqData.setRestCount(restFree);
        airportVipServerRqData.setThisRsrvStr1(rsrv_str1);
        airportVipServerRqData.setThisRsrvStr2(rsrv_str2);
    }

    private void getAirDromeName(BaseReqData brd) throws Exception
    {
        AirportVipServerReqData airportVipServerRqData = (AirportVipServerReqData) brd;

        String commInfoAirDromeId = airportVipServerRqData.getAirDromeId();
        UcaData ucaData = brd.getUca();
        IDataset airPortDromeInfos = ParamInfoQry.getCommparaByCode("CSM", "121", "", ucaData.getUser().getEparchyCode());
        for (int i = 0; i < airPortDromeInfos.size(); i++)
        {
            IData airPortInfo = airPortDromeInfos.getData(i);
            if (commInfoAirDromeId.equals(airPortInfo.getString("PARAM_CODE")))
            {
                airportVipServerRqData.setAirDromeName(airPortInfo.getString("PARAM_NAME"));
            }
        }
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
