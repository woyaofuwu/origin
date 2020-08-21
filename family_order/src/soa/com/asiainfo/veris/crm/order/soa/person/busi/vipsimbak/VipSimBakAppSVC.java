package com.asiainfo.veris.crm.order.soa.person.busi.vipsimbak;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.DevicePriceQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class VipSimBakAppSVC extends CSBizService
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static transient final Logger logger = Logger.getLogger(VipSimBakAppSVC.class);
    /**
     * 对新申请的sim卡进行校验
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public IDataset checkBakCard(IData params) throws Exception
    {

        IData checkResInfo = new DataMap();

        String serialNumber = params.getString("SERIAL_NUMBER");
        if (StringUtils.isBlank(serialNumber))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "校验手机号码出错！");
        }
        IData userInfos = UcaInfoQry.qryUserInfoBySn(serialNumber);

        IDataset dataset = ResCall.checkResourceForSim(params.getString("BAK_CARD_NO"),
                params.getString("SERIAL_NUMBER"), "3", "", "", "", "1", "0", "");

        // IDataset dataset = new DatasetList();
        // IData data = new DataMap();
        // data.put("SIM_TYPE_CODE", "Z");
        // data.put("CAPACITY_TYPE_CODE", "K");
        // data.put("RES_TYPE_CODE", "sd");
        // data.put("RES_KIND_CODE", "L");
        // dataset.add(data);
        if (dataset.size() > 0)
        {
            checkResInfo = (IData) dataset.get(0);
            String userId = userInfos.getString("USER_ID");
            String oldSimOPC = null;
            String newSimOPC = checkResInfo.getString("OPC", "");
            //判断老SIM卡走接口
            IDataset userReses = UserResInfoQry.queryUserSimInfo(userId, "1");
            String simNo = "";
            if (userReses != null && userReses.size() >= 0)
            {
                for (int t = 0; t < userReses.size(); t++)
                {
                    IData tempRes = (IData) userReses.get(t);
                    String resTypeCode = tempRes.getString("RES_TYPE_CODE", "");
                    if (resTypeCode.equals("1"))
                    {
                        simNo = tempRes.getString("RES_CODE", "");
                    }
                }
            }
            else
            {
                CSAppException.apperr(CrmUserException.CRM_USER_293);
            }

            if (simNo == null || simNo.equals(""))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_294);
            }
            
            
            
//            IDataset usimdata = UserResInfoQry.getRecordCountByUserId(userId, CSBizService.getVisit().getStaffEparchyCode());
//            IDataset usimdata= ResCall.checkResourceForSim(simNo,params.getString("SERIAL_NUMBER"), "3", "", "", "", "1", "0", "");
            IDataset usimdata = ResCall.getSimCardInfo("0",simNo, "", "");
            if (usimdata == null || usimdata.size() == 0)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "原SIM卡查询资源数据为空，请确认。");
            }
            String oldSimTypeCode = usimdata.getData(0).getString("RES_TYPE_CODE", "");
            String newSimCardType = checkResInfo.getString("RES_TYPE_CODE");
//            IDataset oldUsim4G = UserResInfoQry.checkUser4GUsimCard(oldSimTypeCode, CSBizService.getVisit()
//                    .getStaffEparchyCode());
//            IDataset newUsim4G = UserResInfoQry.checkUser4GUsimCard(newSimCardType, CSBizService.getVisit()
//                    .getStaffEparchyCode());
            IDataset oldUsim4G = ResCall.qrySimCardTypeByTypeCode(oldSimTypeCode); 
            IDataset newUsim4G = ResCall.qrySimCardTypeByTypeCode(newSimCardType); 
            if (null != usimdata && 0 < usimdata.size())
            {
                IData usimdataInfo = usimdata.getData(0);
                oldSimOPC = usimdataInfo.getString("OPC", "");
            }
            if (StringUtils.isNotBlank(oldSimOPC) && IDataUtil.isNotEmpty(oldUsim4G) && StringUtils.equals("01",oldUsim4G.getData(0).getString("NET_TYPE_CODE")))
            {
                // oldIsUSIM = true;
                checkResInfo.put("oldIsUSIM", "1");
            }
            if (StringUtils.isNotBlank(newSimOPC) && IDataUtil.isNotEmpty(newUsim4G)&& StringUtils.equals("01",newUsim4G.getData(0).getString("NET_TYPE_CODE")))
            {
                IDataset userSvc = UserSvcInfoQry.getUserSvcGprs(userId);
                if (userSvc == null || userSvc.size() == 0)
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "为了保证号码正常使用，办理USIM卡换卡业务前，请先开通GPRS功能。");
                }

                boolean lowDiscnt = false;
                String lowDiscntCode = "";
                IDataset userDiscntInfo = UserResInfoQry.queryUserDiscntFor4GCheck(userId);
                if (userDiscntInfo != null && userDiscntInfo.size() > 0)
                {
                    lowDiscnt = true;
                    for (int i = 0; i < userDiscntInfo.size(); i++)
                    {
                        IData userDiscnt = userDiscntInfo.getData(i);
                        lowDiscntCode += "".equals(lowDiscntCode) ? userDiscnt.getString("DISCNT_CODE", "") : ',' + userDiscnt
                                .getString("DISCNT_CODE", "");
                    }

                }

                if (lowDiscnt)
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "该号码已办理的优惠或预约优惠，与USIM卡换卡业务存在互斥，存在互斥的优惠编码为["
                            + lowDiscntCode + "]");
                }
                checkResInfo.put("newIsUSIM", "1");
            }

            checkResInfo.put("BOOK_DATE", SysDateMgr.getSysDate());
            checkResInfo.put("SIMCARD_NAME", checkResInfo.getString("RSRV_STR4", ""));
            checkResInfo.put("SIM_TYPE_CODE", checkResInfo.getString("RES_TYPE_CODE", ""));
            checkResInfo.put("CAPACITY_TYPE_CODE", checkResInfo.getString("CAPACITY_TYPE_CODE", ""));
        }

        checkResInfo.put("X_RESULTCODE", "0");

        checkResInfo.put("BAK_CARD_NO", checkResInfo.getString("SIM_CARD_NO"));
        String resKingName = "";
        IDataset oldResInfos = ResCall.getSimCardInfo("0", checkResInfo.getString("SIM_CARD_NO"), "", "");
        if (oldResInfos != null && oldResInfos.size() > 0)
        {
            resKingName = ((IData) (oldResInfos.get(0))).getString("RES_KIND_NAME", "");

        }
        else
        {
            resKingName = "";
        }
        IDataset vipInfos = CustVipInfoQry.queryVipInfoBySn(params.getString("SERIAL_NUMBER"),
                params.getString("REMOVE_TAG"));
        if(IDataUtil.isNotEmpty(vipInfos)){
            checkResInfo.put("VIP_TYPE_CODE",  vipInfos.getData(0).getString("VIP_TYPE_CODE"));
            checkResInfo.put("VIP_CLASS_ID",  vipInfos.getData(0).getString("VIP_CLASS_ID"));
        }
        
        checkResInfo.put("SALE_FEE_TAG", checkResInfo.getString("FEE_TAG", ""));
        String rsrvStr = checkResInfo.getString("RSRV_STR3");
        if ("1".equals(rsrvStr))
            checkResInfo.put("SALE_FEE_TAG", "1");
        
        String strSimTypeCode = checkResInfo.getString("RES_TYPE_CODE", "0").substring(1);// 对应老系统的simtypecode
        //判断白卡买断卡
        if (!"".equals(checkResInfo.getString("EMPTY_CARD_ID", "")) && !"U".equals(strSimTypeCode) && !"X".equals(strSimTypeCode))
        {
         // 如果SIM卡表中EMPTY_CARD_ID字段不为空，标明该卡由白卡写成，到白卡表中取卡类型 IData
            IData newEmptyCardInfo = ResCall.getEmptycardInfo(checkResInfo.getString("EMPTY_CARD_ID"), "", "").getData(0);// 资源接口
            
            String rsrvTag = newEmptyCardInfo.getString("RSRV_TAG1");
            if ("3".equals(rsrvTag))
                checkResInfo.put("SALE_FEE_TAG", "1");
        }
        checkResInfo.put("RES_KIND_NAME", resKingName);
        dataset.add(checkResInfo);
        return dataset;
    }

    /**
     * 获取sim卡的费用
     * 
     * @param param
     * @return
     * @throws Exception
     */

    public IDataset getDevicePrice(IData param) throws Exception
    {

        String userTag = param.getString("USER_TAG");
        Boolean tagCommonUser = false;
        Boolean tagVipUser = false;
        IDataset feeInfos = new DatasetList();
        String oldIsUSIM = param.getString("oldIsUSIM", "");
        String newIsUSIM = param.getString("newIsUSIM", "");

        String feeTag = param.getString("FEE_TAG", "");
        String creditFeeTag = param.getString("CREDIT_FEE_TAG", "");
        IData  userInfo = UcaInfoQry.qryUserInfoBySn(param.getString("SERIAL_NUMBER"));
        if(IDataUtil.isEmpty(userInfo)){
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"查询SIM卡费用出错！");
        }
        
        IData newSimInfo = ResCall.getSimCardInfo("0", param.getString("BAK_CARD_NO"), "", "0", userInfo.getString("NET_TYPE_CODE")).getData(0);
        
        String newResKingCode = newSimInfo.getString("RES_KIND_CODE");
        
        //3至5星级用户免费申请备卡
        if("NO".equals(creditFeeTag))
        {
        	return feeInfos;
        }
        
        if ("1".equals(userTag))
        {
            if ("NO".equals(feeTag) && !"1".equals(oldIsUSIM) && "1".equals(newIsUSIM))
            {
                // 使用2/3G卡用户换4G卡，免卡费
            }
            else
            {
                // 获取普通用户优惠
                IDataset commonClasses = ParamInfoQry.getCommparaByCode("CSM", "1750", "Z",
                        CSBizBean.getTradeEparchyCode());

                IDataset userDiscnts = UserDiscntInfoQry.getAllValidDiscntByUserId(param.getString("USER_ID"));

                for (int i = 0; i < userDiscnts.size(); i++)
                {

                    IData userDiscnt = (IData) userDiscnts.get(i);
                    String discntCode = userDiscnt.getString("DISCNT_CODE", "");

                    for (int j = 0; j < commonClasses.size(); j++)
                    {

                        IData userClass = (IData) commonClasses.get(j);
                        String paraCode1 = userClass.getString("PARA_CODE1", "TEMP");
                        String paraCode2 = userClass.getString("PARA_CODE2", "TEMP");
                        if (discntCode.equals(paraCode1))
                        {
                            if (!discntCode.equals(paraCode2))
                            {
                                tagCommonUser = true;
                                break;
                            }
                        }
                    }
                }
                if (tagCommonUser == true && feeTag.equals("NO"))
                {
                    // 只有当用户有优惠且当年无备卡记录时不收费
                }
                else
                {

//                    IData feeInfo = DevicePriceQry.getDevicePrice(CSBizBean.getTradeEparchyCode(), param.getString("RES_TYPE_CODE"),
//                            param.getString("PRODUCT_ID"), "142", param.getString("CAPACITY_TYPE_CODE"),
//                            param.getString("RES_KIND_CODE"));
                    IData feeInfo = DevicePriceQry.getDevicePrice(getTradeEparchyCode(), "", "142", newResKingCode, param.getString("RES_TYPE_CODE"));
                    feeInfos.add(feeInfo);
                }
            }
        }
        else
        {

            if ("NO".equals(feeTag) && !"1".equals(oldIsUSIM) && "1".equals(newIsUSIM))
            {
                // 使用2/3G卡用户换4G卡，免卡费
            }
            else
            {

                IDataset vipClasses = ParamInfoQry
                        .getCommparaByCode("CSM", "1750", "", CSBizBean.getTradeEparchyCode());

                String vipTypeCode = param.getString("VIP_TYPE_CODE");

                for (int i = 0; i < vipClasses.size(); i++)
                {

                    IData userClass = (IData) vipClasses.get(i);
                    String paramCode = userClass.getString("PARAM_CODE", "TEMP");
                    String paraCode1 = userClass.getString("PARA_CODE1", "TEMP");

                    if (vipTypeCode.equals(paramCode))
                    {
                        if (paraCode1.equals(param.getString("VIP_CLASS_ID")))
                        {
                            tagVipUser = true;
                            break;
                        }

                    }
                }
                if (tagVipUser == true && feeTag.equals("NO"))
                {
                    // 只有当用户有配置了相关参数且当年无备卡记录时不收费

                }
                else
                {
//                    IData feeInfo = DevicePriceQry.getDevicePrice(CSBizBean.getTradeEparchyCode(), param.getString("RES_TYPE_CODE"),
//                            param.getString("PRODUCT_ID"), "142", param.getString("CAPACITY_TYPE_CODE"),
//                            param.getString("RES_KIND_CODE"));
                    IData feeInfo = DevicePriceQry.getDevicePrice(getTradeEparchyCode(), "", "142", newResKingCode, param.getString("RES_TYPE_CODE"));
                    feeInfos.add(feeInfo);
                }
            }
        }
        return feeInfos;
    }

    private String getSimType(IData params) throws Exception
    {

        String simNo = "";
        String returnValue = "";

        IDataset userReses = UserResInfoQry.queryUserSimInfo(params.getString("USER_ID"), "1");

        if (userReses != null && userReses.size() >= 0)
        {
            for (int t = 0; t < userReses.size(); t++)
            {
                IData tempRes = (IData) userReses.get(t);
                String resTypeCode = tempRes.getString("RES_TYPE_CODE", "");
                if (resTypeCode.equals("1"))
                {
                    simNo = tempRes.getString("RES_CODE", "");
                }
            }
        }
        else
        {
            CSAppException.apperr(CrmUserException.CRM_USER_293);
        }

        if (simNo == null || simNo.equals(""))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_294);
        }
        else
        {
            // 调用资源接口，测试暂不调用
            // 获取老的资源信息start2

            // IData oldRes = new DataMap();
            // oldRes.put("RES_NO", simNo);
            // oldRes.put("TRADE_EPARCHY_CODE", pd.getContext().getEpachyId()); //路由
            // oldRes.put("RES_TRADE_CODE", "IGetSimCardInfo");
            // oldRes.put("X_GETMODE", "0");
            // oldRes.put("TRADE_CITY_CODE", td.getTradeCityCode());
            // oldRes.put("TRADE_DEPART_ID", td.getTradeDepartId());
            // oldRes.put("TRADE_STAFF_ID", td.getTradeStaffId());
            // IDataset oldResInfos = TuxedoHelper.callTuxSvc(pd, "QRM_IGetResInfo", oldRes);
            // IDataset oldResInfos = ResCall.getSimCardInfo("0", simNo, "", "", this.getVisit());
            // IDataset oldResInfos = new DatasetList();
            // IData data = new DataMap();
            // data.put("RES_KIND_NAME", "testResName");
            // oldResInfos.add(data);
            IDataset oldResInfos = ResCall.getSimCardInfo("0", simNo, "", "");
            if (oldResInfos != null && oldResInfos.size() > 0)
            {
                returnValue = ((IData) (oldResInfos.get(0))).getString("RES_TYPE_CODE", "");
            }
            else
            {
                returnValue = "";
            }

        }
        return returnValue;
    }

    /**
     * 返回大客户备卡申请信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getVipSimBakAppInfo(IData params) throws Exception
    {

        String NORMALSIMBAK_LIMIT = "";
        VipSimBakQueryBean bean = new VipSimBakQueryBean();
        String VIPSIMBAK_LIMIT = bean.getSysTagInfo("CS_NUM_VIPSIMBAKNOLIMIT", "TAG_NUMBER", "1");// 参数字段
        String NORMAL_USER_SIMBAK = bean.getSysTagInfo("CS_NORMALUSERSIMBAK", "TAG_CHAR", "0");// 参数字段
        if ("1".equals(NORMAL_USER_SIMBAK))
        {
            NORMALSIMBAK_LIMIT = bean.getSysTagInfo("CS_NORMALUSERSIMBAK", "TAG_NUMBER", "1");// 参数字段
        }
        //查3星级客户至5星级客户可享受备卡数目
        String SIMBAKNOLIMITCLASS3 = bean.getSysTagInfo("CS_NUM_SIMBAKNOLIMITCLASS3", "TAG_NUMBER", "1");// 参数字段
        String SIMBAKNOLIMITCLASS4 = bean.getSysTagInfo("CS_NUM_SIMBAKNOLIMITCLASS4", "TAG_NUMBER", "1");// 参数字段
        String SIMBAKNOLIMITCLASS5 = bean.getSysTagInfo("CS_NUM_SIMBAKNOLIMITCLASS5", "TAG_NUMBER", "1");// 参数字段
        
        String user_tag = "0";
        String simBak_Limit = "0";
        String strUserIdBiz = "";
        String vipClassId = "";

        // 判断用户是否已经办过手机支付业务
        String userId = UcaInfoQry.qryUserInfoBySn(params.getString("SERIAL_NUMBER")).getString("USER_ID");
        IDataset mobilePays = UserPlatSvcInfoQry.queryUserPlatByUserType(userId, "54");
        if (mobilePays.size() > 0)
        {
            // CSAppException.apperr(CrmCardException.CRM_CARD_256);
        }

        IDataset userSvcInfos = UserSvcInfoQry.getSvcUserIdPf(userId, "22");
        if (IDataUtil.isEmpty(userSvcInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "为了保证号码正常使用，办理USIM卡换卡业务前，请先开通GPRS功能。");
        }

        IDataset vipInfos = CustVipInfoQry.queryVipInfoBySn(params.getString("SERIAL_NUMBER"),
                params.getString("REMOVE_TAG"));

        IData vipInfo = new DataMap();

        if (vipInfos == null || vipInfos.size() < 1)
        {
            if ("0".equals(NORMAL_USER_SIMBAK))
            {
                CSAppException.apperr(CrmCardException.CRM_CARD_12);
            }
            else
            {
                simBak_Limit = NORMALSIMBAK_LIMIT;
                user_tag = "1";
                strUserIdBiz = params.getString("USER_ID");
            }
        }
        else
        {
            vipInfo = (IData) vipInfos.get(0);

            simBak_Limit = VIPSIMBAK_LIMIT;
            user_tag = "2";
            strUserIdBiz = vipInfo.getString("VIP_ID", "");
            vipClassId = vipInfo.getString("VIP_CLASS_ID", "");
        }

        params.put("VIP_ID", strUserIdBiz);

        IDataset simbakInfos = CustVipInfoQry.getSimBakInfos(params);

        params.put("START_DATE", SysDateMgr.getNowYear() + "-01" + "-01 00" + ":00:00");
        params.put("END_DATE", SysDateMgr.getLastDayOfThisYear());
        
        IDataset SimBakInfosByDate=CustVipInfoQry.getSimBakInfosByDate(params);

        vipInfo.put("FEE_TAG", SimBakInfosByDate.size() < 1 ? "NO" : "YES");

        vipInfo.put("RES_TYPE_CODE", getSimType(params));// 获取老sim卡资料的名称
        vipInfo.put("VIP_BAK_INFO", simbakInfos);

        int iNowVipBakNo = 0;
        if (!simbakInfos.isEmpty())
        {
            for (int i = 0; i < simbakInfos.size(); i++)
            {
                if ("0".equals(simbakInfos.get(i, "ACT_TAG")))
                    iNowVipBakNo++;
            }
        }

        vipInfo.put("VIPSIMBAK_NUMBER", iNowVipBakNo);
        vipInfo.put("OLD_SIM_BAK", simbakInfos);
        vipInfo.put("VIP_ID", strUserIdBiz);// 隐藏参数字段
        vipInfo.put("USER_TAG", user_tag);// 隐藏参数字段
        vipInfo.put("VIP_CLASS_ID", vipClassId);// 隐藏参数字段
        vipInfo.put("SIMBAK_LIMIT", simBak_Limit);// 隐藏参数字段
        
        //新增3至5星级客户可享受备卡服务，一年内，五星客户可享受不超过3个备用SIM卡服务，四星级客户可享受不超过2个备用SIM卡服务，三星级客户可享受不超过1个备用SIM卡服务。 
        UcaData uca = null;
        uca = UcaDataFactory.getNormalUca(params.getString("SERIAL_NUMBER", ""));
        String strCreditClass = uca.getUserCreditClass();
        int iCreditClass = Integer.parseInt(strCreditClass);
        vipInfo.put("CREDIT_FEE_TAG", "YES");//默认是收费
        if(3 == iCreditClass)
        {
        	if (Integer.parseInt(SIMBAKNOLIMITCLASS3) > Integer.parseInt(simBak_Limit))
        	{
        		simBak_Limit = SIMBAKNOLIMITCLASS3;
        	}
        	
        	//增加星级用户，备卡是否收费标志
        	if(SimBakInfosByDate.size() >= Integer.parseInt(SIMBAKNOLIMITCLASS3))
        	{
        		vipInfo.put("CREDIT_FEE_TAG", "YES");
        	}else
        	{
        		vipInfo.put("CREDIT_FEE_TAG", "NO");
        	}
        }
        else if(4 == iCreditClass)
        {
        	if (Integer.parseInt(SIMBAKNOLIMITCLASS4) > Integer.parseInt(simBak_Limit))
        	{
        		simBak_Limit = SIMBAKNOLIMITCLASS4;
        	}
        	
        	//增加星级用户，备卡是否收费标志
        	if(SimBakInfosByDate.size() >= Integer.parseInt(SIMBAKNOLIMITCLASS4))
        	{
        		vipInfo.put("CREDIT_FEE_TAG", "YES");
        	}else
        	{
        		vipInfo.put("CREDIT_FEE_TAG", "NO");
        	}
        }
        else if (iCreditClass >= 5)
        {
        	if (Integer.parseInt(SIMBAKNOLIMITCLASS5) > Integer.parseInt(simBak_Limit))
        	{
        		simBak_Limit = SIMBAKNOLIMITCLASS5;
        	}
        	
        	//增加星级用户，备卡是否收费标志
        	if(SimBakInfosByDate.size() >= Integer.parseInt(SIMBAKNOLIMITCLASS5))
        	{
        		vipInfo.put("CREDIT_FEE_TAG", "YES");
        	}else
        	{
        		vipInfo.put("CREDIT_FEE_TAG", "NO");
        	}
        }
        //end 新增3至5星级客户可享受备卡服务

        if (iNowVipBakNo >= Integer.parseInt(simBak_Limit))
        {
            CSAppException.apperr(CrmCardException.CRM_CARD_15, simBak_Limit);
        }

        IDataset returnDatas = new DatasetList();

        returnDatas.add(vipInfo);

        return returnDatas;
    }

}
