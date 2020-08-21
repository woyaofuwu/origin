
package com.asiainfo.veris.crm.order.soa.person.busi.vipserver;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQrySVC;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.AirportVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print.ReceiptNotePrintMgr;

public class AirportVipServerSVC extends CSBizService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 598810070326252442L;

    /**
     * 检验VIP卡号
     * 
     * @throws Exception
     */
    public void checkVipGrade(String vipTypeCode, String classId) throws Exception
    {
        if ((!"0".equalsIgnoreCase(vipTypeCode) && !"2".equalsIgnoreCase(vipTypeCode)) || (!"2".equalsIgnoreCase(classId) && !"3".equalsIgnoreCase(classId) && !"4".equalsIgnoreCase(classId)))
        {
            // common.error("549001", "客户等级不够!");
        }
    }

    public IDataset getAirportVipInfos(IData params) throws Exception
    {
        String SERIAL_NUMBER = params.getString("SERIAL_NUMBER");
        String USER_ID = params.getString("USER_ID");
        String userProductId = params.getString("PRODUCT_ID");
        IDataset initParams = new DatasetList();
        IData initparam = new DataMap();

        // 获得vip信息
        IDataset airportVipInfos = CustVipInfoQry.queryVipInfoBySn(SERIAL_NUMBER, "0");

        String VIP_NO = "";
        IData airportVipInfo = new DataMap();
        if (IDataUtil.isEmpty(airportVipInfos))
        {
            if (!"10007609".equals(userProductId) && !"10007633".equals(userProductId) && !"10007610".equals(userProductId) && !"10007634".equals(userProductId) && !"10007611".equals(userProductId) && !"10007635".equals(userProductId)
                    && !"10007612".equals(userProductId) && !"10007636".equals(userProductId))
            {
                CSAppException.apperr(CustException.CRM_CUST_169);
            }
        }
        else
        {
            airportVipInfo = airportVipInfos.getData(0);
            VIP_NO = airportVipInfo.getString("VIP_CARD_NO");
        }

        // 获取最后一次易登机服务记录
        IData service = new DataMap();
        IDataset dataset = new DatasetList();
        IDataset services = AirportVipInfoQry.queryServiceLast(SERIAL_NUMBER, VIP_NO);
        if (services != null && services.size() > 0)
        {
            service = services.getData(0);
            dataset.add(service);
            initparam.put("INFOS", dataset); // 最近一次服务信息

            // 加载最近一个服务的附加信息
            airportVipInfo.put("INDIV_INFO", service.getString("INDIV_INFO"));
            airportVipInfo.put("FEEDBACK", service.getString("FEEDBACK"));
            airportVipInfo.put("INNOVATION", service.getString("INNOVATION"));
            airportVipInfo.put("ADVICES", service.getString("ADVICES"));
            airportVipInfo.put("OTHERS", service.getString("OTHERS"));
        }

        String oldStartDate = SysDateMgr.getSysTime();
        String ifeeCount = "0";// 已使用的免费服务次数信息
        IDataset freeCountInfos = UserOtherInfoQry.getUserOther(USER_ID, "AREM");
        if (freeCountInfos != null && freeCountInfos.size() > 0)
        {
            IData freeCountInfo = freeCountInfos.getData(0);
            oldStartDate = freeCountInfo.getString("START_DATE", SysDateMgr.getSysTime());// 旧记录开始时间
            ifeeCount = freeCountInfo.getString("RSRV_STR1", "0");
        }

        // 获得用户可以使用的免费次数
        AirportVipServerBean bean = new AirportVipServerBean();
        String vip_type_code = airportVipInfo.getString("VIP_TYPE_CODE", "");
        String vip_class_id = airportVipInfo.getString("VIP_CLASS_ID", "");

        int totalFreeCount = bean.getTotalFreeCount(vip_type_code, vip_class_id, userProductId);
        int feeCount = Integer.parseInt(ifeeCount) > totalFreeCount ? totalFreeCount : Integer.parseInt(ifeeCount);

        // airportVipInfo.put("SCORE", "20000");//积分信息
        airportVipInfo.put("SCORE", bean.queryUserScore(USER_ID));// 积分信息
        airportVipInfo.put("TOTAL_FREECOUNT", totalFreeCount);// 客户可使用的免费次数
        airportVipInfo.put("FREE_COUNT", feeCount);// 已使用的免费服务次数
        airportVipInfo.put("NUM", AirportVipInfoQry.queryServiceNum(SERIAL_NUMBER));// 累计的服务次数
        airportVipInfo.put("OLD_START_DATE", oldStartDate);

        initparam.put("vipInfo", airportVipInfo);
        initparam.put("AIRPORT_INFOS", ParamInfoQry.getCommparaByCode("CSM", "121", "", CSBizBean.getTradeEparchyCode())); // 机场名称列表
        initparam.put("AIRPORT_SERVICE_TYPE", ParamInfoQry.getCommparaByCode("CSM", "123", "", CSBizBean.getTradeEparchyCode()));
        initParams.add(initparam);
        return initParams;
    }

    /**
     * 组织打印数据
     * 
     * @author chenzg
     * @date 2009-10-11
     * @param pd
     * @param td
     * @return
     * @throws Exception
     */
    public IDataset loadPrintData(IData params) throws Exception
    {
        IData airportInfo = null;
        String tradeId = params.getString("TRADE_ID");
        IDataset airportInfos = AirportVipInfoQry.queryVipAirServer(tradeId);
        if (IDataUtil.isEmpty(airportInfos))
        {
            return null;
        }
        else
        {
            airportInfo = airportInfos.getData(0);
        }
        // 获取品牌名称
        String brandName = UBrandInfoQry.getBrandNameByBrandCode(params.getString("BRAND_CODE"));
        // 获取剩余免费次数
        String iRestFreeTimes = airportInfo.getString("RSRV_STR3");
        // 证件类型
        String idcardTypeName = StaticUtil.getStaticValue("TD_S_PASSPORTTYPE", params.getString("PSPT_ID"));
        ;

        IData inparams = new DataMap();
        inparams.put("TRADE_TYPE_CODE", "360");
        inparams.put("TRADE_ID", tradeId);
        inparams.put("TRADE_DATE", SysDateMgr.getSysDate());
        inparams.put("TRADE_STAFF_NAME", getVisit().getStaffName());
        inparams.put("TRADE_STAFF_ID", getVisit().getStaffId());
        inparams.put("TRADE_DEPT_NAME", getVisit().getDepartName());
        inparams.put("TRADE_CITY_NAME", getVisit().getCityName());
        inparams.put("TRADE_TYPE", "全球通VIP俱乐部机场贵宾厅服务");
        inparams.put("TRADE_DETAIL", "VIP机场服务");
        inparams.put("LEVEL_NAME", airportInfo.getString("CLASS_ID"));
        inparams.put("ATTENDANTS", airportInfo.getString("FOLLOW_NUM"));
        inparams.put("CHECK_DESC", "");
        inparams.put("USERRANK", airportInfo.getString("CLASS_ID")); // 客户级别
        inparams.put("SERIAL_NUMBER", airportInfo.getString("SERIAL_NUMBER")); // 服务号码
        inparams.put("AIRPORTNAME", airportInfo.getString("AIRDROME_NAME")); // 机场名称
        inparams.put("PLANLINE", airportInfo.getString("PLANE_LINE")); // 航班
        inparams.put("PAY_SCORE", airportInfo.getString("CONSUME_SCORE")); // 应扣积分
        inparams.put("USE_TIMES", airportInfo.getString("RSRV_STR2")); // 本次扣除免费次数
        inparams.put("FREE_TIMES", (Integer.parseInt(iRestFreeTimes) > 0 ? iRestFreeTimes : 0)); // 剩余免费次数
        inparams.put("BRAND", brandName); // 用户品牌
        inparams.put("ID_CARD_TYPE", idcardTypeName); // 证件类型
        inparams.put("IDCARDNUM", params.getString("PSPT_ID")); // 证件号码
        inparams.put("VIP_NO", airportInfo.getString("VIP_NO")); // vip卡号
        inparams.put("CUST_NAME", params.getString("CUST_NAME")); // 客户名称

        inparams.put("UPDATE_DEPART_ID", getVisit().getDepartId());
        inparams.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        inparams.put("UPDATE_TIME", SysDateMgr.getSysTime());
        inparams.put("ACCEPT_DATE", SysDateMgr.getSysTime());
        inparams.put("ACCEPT_MONTH", inparams.getString("OPERATION_MONTH"));
        inparams.put("TRADE_CITY_CODE", getVisit().getCityCode());
        inparams.put("NOTE_TYPE", "1");
        inparams.put("PRT_FLAG", "1");
        ReceiptNotePrintMgr recePrintMgr = new ReceiptNotePrintMgr();
        IDataset result = recePrintMgr.printInterBossReceipt(inparams);
        return result;
    }
    
    public IDataset authVipUserInfo(IData params) throws Exception
    {
        String serialNumber = params.getString("SERIAL_NUMBER");
        String psptId = params.getString("PSPT_ID");
        UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
        String userPspt = ucaData.getCustomer().getPsptId();
        IData data = new DataMap(); 
        if(StringUtils.equals(psptId, userPspt)){
            data.put("RESULT_CODE", "0");
            data.put("RESULT_VALUE", "正确！");
        }else{
            data.put("RESULT_CODE", "1");
            data.put("RESULT_VALUE", "错误！");
        }
        IDataset rs = new DatasetList();
        rs.add(data);
        return rs;
    }

}
