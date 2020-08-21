package com.asiainfo.veris.crm.order.soa.person.busi.imslandline.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyRolesEnum;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ImpuTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WideNetTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.imslandline.order.requestdata.IMSLandLineRequestData;
import com.asiainfo.veris.crm.order.soa.person.common.query.broadband.WidenetTradeQuery;

public class IMSLandLineTrade extends BaseTrade implements ITrade
{
	public void createBusiTradeData(BusiTradeData btd) throws Exception
    {	
		//REQ201810190032  和家固话开户界面增加实名制校验—BOSS侧 by mqx end
        //新增权限，有该权限的工号可以在家庭产品开户界面、家庭IMS固话开户界面进行单位证件开户。
        //没有权限的工号进行单位证件开户时提示：该工号不具有和家固话单位证件开户权限（仅限自办厅工号申请） 
		this.verifyOrganizationPriv(btd);
		
		IMSLandLineRequestData reqData = (IMSLandLineRequestData) btd.getRD();
        List<ProductModuleData> selectedElements = reqData.getProductElements();
        
        //BUS201812190003 关于在固话开户界面新增“铁通迁转固话”标签的需求 by mengqx 20190123
        btd.getMainTradeData().setRsrvStr4(reqData.getIsTTtransfer());
        
        createTradeWidenet(btd, reqData);
        
        // 生成宽带用户
        createTradeUser(btd, reqData);
        
        // 生成虚拟用户
        createTradeVirtualUser(btd, reqData);
        
        // 生成虚拟产品台账
        createTradeProductVirtualUser(btd, reqData, reqData.getVirtualUserId());
        
        // 生成用户关系
        createTradeRelationUU(btd, reqData);
        
        // 宽带用户产品台账
        createTradeProduct(btd, reqData);

        // 虚拟用户绑定资费
        createTradeVirtualDiscnt(btd, reqData);
        
        // 生成付费关系
        createTradePayRelation(btd, reqData);
        
        // 生成资源关系
        createResTradeData(btd, reqData);
        
        // 构建产品
        ProductModuleCreator.createProductModuleTradeData(selectedElements, btd);
        
        //构建other表，存入终端串号
        createResIdOtherTradeData(btd, reqData);

        //追加主台账信息
        appendTradeMainData(btd, reqData);
        //start-wangsc10-20181116 REQ201809040031++关于开发和家固话（软终端）BOSS-杭研平台接口的需求-
        //在主台账中给预留字段赋值，服开根据此字段来判断是否自动完工，不需要走外线工单
        IData pageParam=btd.getRD().getPageRequestData();
        if(IDataUtil.isNotEmpty(pageParam))
        {
        	String OPEN_IMS=pageParam.getString("OPEN_IMS","");
        	if(OPEN_IMS!=null&&!OPEN_IMS.trim().equals(""))
        	{
        		if(OPEN_IMS.equals("OPEN_IMS"))
        		{
        			btd.getMainTradeData().setRsrvStr7("OPEN_IMS");
        		}
        	}
        }
        //end
        
        if("HAS_REAL_NAME_INFO".equals(pageParam.getString("HAS_REAL_NAME_INFO")))
        {
        	//REQ201810190032++和家固话开户界面增加实名制校验—BOSS侧   by mqx
            // 处理客户核心资料
            createCustomerTradeData(btd);

            // 处理客户个人资料
            createCustPersonTradeData(btd);
            
            IMSLandLineRequestData imsLandLineRD = (IMSLandLineRequestData) btd.getRD();
            CustomerTradeData customerTD = imsLandLineRD.getUca().getCustomer();
            String psptTypeCode = customerTD.getPsptTypeCode();
            if(psptTypeCode!=null&&psptTypeCode.trim().equals("E")){//营业执照
                createOtherTradeDataEnterprise(btd);
            }
            //REQ201810190032++和家固话开户界面增加实名制校验—BOSS侧   by mqx end
        }
        if(null != reqData.getMainProduct()){
        	if(null != reqData.getMainProduct().getProductId()){
	        	if("84018059".equals(reqData.getMainProduct().getProductId())){
	        		btd.getMainTradeData().setRsrvStr7("OPEN_IMS");
	        	}
        	}
        }
        btd.addOpenUserAcctDayData(reqData.getUca().getUserId(), "1");
        btd.addOpenUserAcctDayData(reqData.getVirtualUserId(), "1");
        
        //数值拆分需要
        //// impu台帐表录入
        actTradeIMSIMPU(btd, reqData);
        
        //１表示是宽带融合订单，在宽带开户里面已经做了预占操作，不需要再次做预占
        // 业务办理资格校验（CIP00003），和家固话办理资格校验入参无固话号码，不走以下逻辑
        if (!"1".equals(reqData.getIsMergeWideUserCreate()) && !StringUtils.equals(reqData.getPageRequestData().getString("CHECK_CUSTOMER_DO_SERVICE", ""), "1"))
        {
        	preSimCard(btd, reqData);
        }
    }
	
	/**
	 * 用户宽带台帐拼串
	 * <p>Title: createTradeWidenet</p>
	 * <p>Description: </p>
	 * <p>Company: AsiaInfo</p>
	 * @param btd
	 * @param reqData
	 * @throws Exception
	 * @author XUYT
	 * @date 2017-11-2 上午09:55:23
	 */
    private void createTradeWidenet(BusiTradeData<BaseTradeData> btd, IMSLandLineRequestData reqData) throws Exception
    {
    	WideNetTradeData wtd = new WideNetTradeData();
        String pwd = reqData.getUserPasswd();
        wtd.setUserId(reqData.getUca().getUserId());
        String roleCode = reqData.getFamilyImsRoleCode();
        if (StringUtils.isNotBlank(roleCode) && FamilyRolesEnum.IMS.getRoleCode().equals(roleCode)) {//家庭项目新增
            wtd.setStandAddress(reqData.getStandAddress());
            wtd.setDetailAddress(reqData.getDetailAddress());
            wtd.setStandAddressCode(reqData.getStandAddressCode());
            wtd.setAcctPasswd(reqData.getUsermPasswd());
            wtd.setContact(reqData.getContact());
            wtd.setContactPhone(reqData.getContactPhone());
            wtd.setPhone(reqData.getPhone());
            wtd.setInstId(SeqMgr.getInstId());
            wtd.setModifyTag(BofConst.MODIFY_TAG_ADD);
            wtd.setStartDate(reqData.getOpenDate());
            wtd.setEndDate(SysDateMgr.getTheLastTime());
            wtd.setRsrvStr1(pwd);
            wtd.setRsrvStr4(reqData.getAreaCode());//AreaCode
            wtd.setRsrvStr5("家庭项目固话psptId");//PsptId
            wtd.setRsrvStr2(reqData.getWideType());//WideType
            wtd.setRsrvNum1(reqData.getDeviceId());//DeviceId
        }else{
            String serialNum="KD_"+reqData.getNormalSerialNumber();
            IDataset dataset = WidenetInfoQry.getUserWidenetInfoBySerialNumber(serialNum);
            if(IDataUtil.isNotEmpty(dataset))
            {
                IData widenetdata = dataset.getData(0);
                wtd.setStandAddress(widenetdata.getString("STAND_ADDRESS", ""));
                wtd.setDetailAddress(widenetdata.getString("DETAIL_ADDRESS", ""));
                wtd.setStandAddressCode(widenetdata.getString("STAND_ADDRESS_CODE", ""));
                wtd.setAcctPasswd(reqData.getUsermPasswd());
                wtd.setContact(widenetdata.getString("CONTACT", ""));
                wtd.setContactPhone(widenetdata.getString("CONTACT_PHONE", ""));
                wtd.setPhone(widenetdata.getString("PHONE", ""));
                wtd.setInstId(SeqMgr.getInstId());
                wtd.setModifyTag(BofConst.MODIFY_TAG_ADD);
                wtd.setStartDate(reqData.getOpenDate());
                wtd.setEndDate(SysDateMgr.getTheLastTime());
                wtd.setRsrvStr1(pwd);
                wtd.setRsrvStr4(widenetdata.getString("RSRV_STR4", ""));//AreaCode
                wtd.setRsrvStr5(widenetdata.getString("RSRV_STR5", ""));//PsptId
                wtd.setRsrvStr2(widenetdata.getString("RSRV_STR2", ""));//WideType

                //设备号
                wtd.setRsrvNum1(widenetdata.getString("RSRV_NUM1", ""));//DeviceId
            }
            else
            {
                IDataset widenetMainTrades = TradeInfoQry.getMainTradeBySN(serialNum, "600");

                if (IDataUtil.isNotEmpty(widenetMainTrades))
                {
                    IData widenetMainTrade = widenetMainTrades.first();

                    IDataset widenetTrades = WidenetTradeQuery.queryWidenet(widenetMainTrade.getString("TRADE_ID"));

                    if (IDataUtil.isNotEmpty(widenetTrades))
                    {
                        IData widenetTrade = widenetTrades.first();

                        wtd.setStandAddress(widenetTrade.getString("STAND_ADDRESS",""));
                        wtd.setDetailAddress(widenetTrade.getString("DETAIL_ADDRESS",""));
                        wtd.setStandAddressCode(widenetTrade.getString("STAND_ADDRESS_CODE",""));
                        wtd.setAcctPasswd(reqData.getUsermPasswd());
                        wtd.setContact(widenetTrade.getString("CONTACT",""));
                        wtd.setContactPhone(widenetTrade.getString("CONTACT_PHONE",""));
                        wtd.setPhone(widenetTrade.getString("PHONE",""));
                        wtd.setInstId(SeqMgr.getInstId());
                        wtd.setModifyTag(BofConst.MODIFY_TAG_ADD);
                        wtd.setStartDate(reqData.getOpenDate());
                        wtd.setEndDate(SysDateMgr.getTheLastTime());
                        wtd.setRsrvStr1(pwd);
                        wtd.setRsrvStr4(widenetTrade.getString("RSRV_STR4",""));//AreaCode
                        wtd.setRsrvStr5(widenetTrade.getString("RSRV_STR5",""));//PsptId
                        wtd.setRsrvStr2(widenetTrade.getString("RSRV_STR2",""));//WideType
                        wtd.setRsrvNum1(widenetTrade.getString("RSRV_NUM1",""));//DeviceId
                    }
                    else
                    {
                        CSAppException.appError("201800021", "该用户不存在有效的宽带信息，不能办理该该业务！");
                    }
                }
                else
                {
                    CSAppException.appError("201800021", "该用户不存在有效的宽带信息，不能办理该该业务！");
                }
            }
        }

	
        btd.add(btd.getRD().getUca().getSerialNumber(), wtd);
    }   
    
    /**
     * 用户子台账表拼串
     * <p>Title: createTradeUser</p>
     * <p>Description: </p>
     * <p>Company: AsiaInfo</p>
     * @param btd
     * @param reqData
     * @throws Exception
     * @author XUYT
     * @date 2017-11-2 上午09:56:14
     */
    private void createTradeUser(BusiTradeData<BaseTradeData> btd, IMSLandLineRequestData reqData) throws Exception
    {
        UserTradeData userTD = reqData.getUca().getUser();
        String pwd = reqData.getUserPasswd();
        userTD.setUserPasswd(pwd);
        userTD.setOpenDate(reqData.getOpenDate());
        userTD.setInDate(reqData.getOpenDate());
        userTD.setUserTypeCode("0");
        userTD.setNetTypeCode("00");
        btd.add(btd.getRD().getUca().getSerialNumber(), userTD);
    }
     
    /**
     * 虚拟用户子台账表拼串
     * <p>Title: createTradeVirtualUser</p>
     * <p>Description: </p>
     * <p>Company: AsiaInfo</p>
     * @param btd
     * @param reqData
     * @throws Exception
     * @author XUYT
     * @date 2017-11-2 上午09:56:36
     */
    private void createTradeVirtualUser(BusiTradeData<BaseTradeData> btd, IMSLandLineRequestData reqData) throws Exception
    {
        UserTradeData userTD = new UserTradeData();
        String pwd = reqData.getUserPasswd();

        userTD.setUserId(reqData.getVirtualUserId());
        userTD.setCustId(reqData.getUca().getCustomer().getCustId());
        userTD.setUsecustId(reqData.getUca().getCustomer().getCustId());
        userTD.setSerialNumber("IMS_" + reqData.getNormalSerialNumber());
        userTD.setNetTypeCode(reqData.getUca().getUser().getNetTypeCode());
        userTD.setUserTypeCode(reqData.getUca().getUser().getUserTypeCode());
        userTD.setUserStateCodeset("0");
        userTD.setUserPasswd(pwd);
        userTD.setPrepayTag("0");
        userTD.setMputeMonthFee("0");
        userTD.setAcctTag("0");
        userTD.setDevelopCityCode(reqData.getUca().getUser().getCityCode());
        userTD.setDevelopDate(reqData.getOpenDate());
        userTD.setDevelopDepartId(CSBizBean.getVisit().getDepartId());
        userTD.setDevelopEparchyCode(CSBizBean.getUserEparchyCode());
        userTD.setDevelopStaffId(CSBizBean.getVisit().getStaffId());
        userTD.setInDate(reqData.getOpenDate());
        userTD.setInStaffId(CSBizBean.getVisit().getStaffId());
        userTD.setInDepartId(CSBizBean.getVisit().getDepartId());
        userTD.setOpenDepartId(CSBizBean.getVisit().getDepartId());
        userTD.setOpenStaffId(CSBizBean.getVisit().getStaffId());
        userTD.setOpenDate(reqData.getOpenDate());
        userTD.setOpenMode("0");
        userTD.setEparchyCode(CSBizBean.getUserEparchyCode());
        userTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        userTD.setCityCode(reqData.getUca().getUser().getCityCode());
        userTD.setRemoveTag("0");
        btd.add("IMS_" + reqData.getNormalSerialNumber(), userTD);
    }
     
    /**
     * 虚拟用户产品台账拼串
     * <p>Title: createTradeProductVirtualUser</p>
     * <p>Description: </p>
     * <p>Company: AsiaInfo</p>
     * @param btd
     * @param reqData
     * @param userId
     * @throws Exception
     * @author XUYT
     * @date 2017-11-2 上午09:57:06
     */
    private void createTradeProductVirtualUser(BusiTradeData<BaseTradeData> btd, IMSLandLineRequestData reqData, String userId) throws Exception
    {
        ProductTradeData productTD = new ProductTradeData();
        productTD.setUserId(userId);
        productTD.setUserIdA("-1");
        productTD.setProductId(reqData.getMainProduct().getProductId());
        productTD.setProductMode(reqData.getMainProduct().getProductMode());
        productTD.setBrandCode(reqData.getMainProduct().getBrandCode());
        productTD.setInstId(SeqMgr.getInstId());
        productTD.setStartDate(reqData.getOpenDate());
        productTD.setEndDate(SysDateMgr.getTheLastTime());
        productTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        productTD.setRsrvStr5(reqData.getIMSProductName());
        productTD.setMainTag("1");
        
        btd.add("IMS_" + reqData.getNormalSerialNumber(), productTD);
    }
   
    /**
     * 用户关系台账表拼串
     * <p>Title: createTradeRelationUU</p>
     * <p>Description: </p>
     * <p>Company: AsiaInfo</p>
     * @param btd
     * @param reqData
     * @throws Exception
     * @author XUYT
     * @date 2017-11-2 上午09:57:24
     */
    private void createTradeRelationUU(BusiTradeData<BaseTradeData> btd, IMSLandLineRequestData reqData) throws Exception
    {
        genTradeRelaInfoWide(reqData.getVirtualUserId(), reqData.getNormalUserId(), reqData.getNormalSerialNumber(), "1","MS" , btd, reqData);
        genTradeRelaInfoWide(reqData.getVirtualUserId(), reqData.getUca().getUserId(), reqData.getUca().getSerialNumber(), "2", "MS", btd, reqData);
    }
    
    /**
     * 用户关系台账表拼串实现类
     * <p>Title: genTradeRelaInfoWide</p>
     * <p>Description: </p>
     * <p>Company: AsiaInfo</p>
     * @param userIdVirtual
     * @param userId
     * @param serialNumber
     * @param roleCodeB
     * @param relationTypeCode
     * @param btd
     * @param reqData
     * @throws Exception
     * @author XUYT
     * @date 2017-11-2 上午09:57:48
     */
    private void genTradeRelaInfoWide(String userIdVirtual, String userId, String serialNumber, String roleCodeB, String relationTypeCode, BusiTradeData<BaseTradeData> btd, IMSLandLineRequestData reqData) throws Exception
    {

        RelationTradeData rtd = new RelationTradeData();

        rtd.setUserIdA(userIdVirtual);
        rtd.setUserIdB(userId);
        rtd.setSerialNumberA("-1");
        rtd.setInstId(SeqMgr.getInstId());
        rtd.setModifyTag(BofConst.MODIFY_TAG_ADD);
        rtd.setRelationTypeCode(relationTypeCode);
        rtd.setSerialNumberB(serialNumber);
        rtd.setRoleCodeA("0");
        rtd.setRoleCodeB(roleCodeB);// 2表示副卡
        rtd.setOrderno("0");
        rtd.setStartDate(reqData.getOpenDate());
        rtd.setEndDate(SysDateMgr.getTheLastTime());
        btd.add(reqData.getUca().getUser().getSerialNumber(), rtd);
    }
    
    /**
     * 构建产品
     * <p>Title: createTradeProduct</p>
     * <p>Description: </p>
     * <p>Company: AsiaInfo</p>
     * @param btd
     * @param reqData
     * @throws Exception
     * @author XUYT
     * @date 2017-11-2 上午09:58:06
     */
    private void createTradeProduct(BusiTradeData<BaseTradeData> btd, IMSLandLineRequestData reqData) throws Exception
    {
        ProductTradeData productTD = new ProductTradeData();
        productTD.setUserId(reqData.getUca().getUserId());
        productTD.setUserIdA("-1");
        productTD.setProductId(reqData.getMainProduct().getProductId());
        productTD.setProductMode(reqData.getMainProduct().getProductMode());
        productTD.setBrandCode(reqData.getMainProduct().getBrandCode());
        productTD.setInstId(SeqMgr.getInstId());
        productTD.setStartDate(reqData.getOpenDate());
        productTD.setEndDate(SysDateMgr.getTheLastTime());
        productTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        productTD.setRsrvStr5(reqData.getIMSProductName());
        productTD.setMainTag("1");

        btd.add(btd.getRD().getUca().getSerialNumber(), productTD);
    }
    
    /**
     * 虚拟套餐台账
     * <p>Title: createTradeVirtualDiscnt</p>
     * <p>Description: </p>
     * <p>Company: AsiaInfo</p>
     * @param btd
     * @param reqData
     * @throws Exception
     * @author XUYT
     * @date 2017-11-2 上午09:58:32
     */
    private void createTradeVirtualDiscnt(BusiTradeData<BaseTradeData> btd, IMSLandLineRequestData reqData) throws Exception
    {

        DiscntTradeData newDiscnt = new DiscntTradeData();
        newDiscnt.setUserId(reqData.getVirtualUserId());
        newDiscnt.setUserIdA("-1");
        newDiscnt.setProductId("-1");
        newDiscnt.setPackageId("-1");
        newDiscnt.setElementId(reqData.getLowDiscntCode());
        newDiscnt.setRelationTypeCode("MS");
        newDiscnt.setInstId(SeqMgr.getInstId());
        newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
        newDiscnt.setSpecTag("2");
        newDiscnt.setStartDate(reqData.getOpenDate());
        newDiscnt.setEndDate(SysDateMgr.getTheLastTime());
        newDiscnt.setRemark("IMS家庭固话转移套餐");
        btd.add("IMS_" + reqData.getNormalSerialNumber(), newDiscnt);
    }
    	
    /**
     * 	生成台帐付费表
     * <p>Title: createTradePayRelation</p>
     * <p>Description: </p>
     * <p>Company: AsiaInfo</p>
     * @param btd
     * @param reqData
     * @throws Exception
     * @author XUYT
     * @date 2017-11-2 上午09:59:14
     */
    private void createTradePayRelation(BusiTradeData<BaseTradeData> btd, IMSLandLineRequestData reqData) throws Exception
    {
        PayRelationTradeData payrelationTD = new PayRelationTradeData();
        payrelationTD.setUserId(reqData.getUca().getUserId());
        payrelationTD.setAcctId(reqData.getUca().getAcctId());
        payrelationTD.setPayitemCode("-1");
        payrelationTD.setAcctPriority("0");
        payrelationTD.setUserPriority("0");
        payrelationTD.setBindType("1");
        payrelationTD.setStartCycleId(SysDateMgr.getNowCycle());
        payrelationTD.setEndCycleId(SysDateMgr.getEndCycle20501231());
        payrelationTD.setActTag("1");
        payrelationTD.setDefaultTag("1");
        payrelationTD.setLimitType("0");
        payrelationTD.setLimit("0");
        payrelationTD.setComplementTag("0");
        payrelationTD.setInstId(SeqMgr.getInstId());
        payrelationTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(btd.getRD().getUca().getSerialNumber(), payrelationTD);
    }
    
    /**
     * 修改主台帐字段
     * <p>Title: appendTradeMainData</p>
     * <p>Description: </p>
     * <p>Company: AsiaInfo</p>
     * @param btd
     * @param reqData
     * @throws Exception
     * @author XUYT
     * @date 2017-11-2 上午09:59:34
     */
    private void appendTradeMainData(BusiTradeData<BaseTradeData> btd, IMSLandLineRequestData reqData) throws Exception
    {  	
    	//标记用户订购了魔百和业务
        if (StringUtils.isNotBlank(reqData.getMoProductId()))
        {
            //告知服开订购的魔百和业务
            btd.getMainTradeData().setRsrvStr3("0");
            btd.getMainTradeData().setRsrvStr9(reqData.getMoProductId());
            btd.getMainTradeData().setRsrvStr10(reqData.getMoPackageId());
        }
        btd.getMainTradeData().setSubscribeType("300");
        btd.getMainTradeData().setRsrvStr1(reqData.getNormalSerialNumber());
        btd.getMainTradeData().setRsrvStr2(reqData.getWideProductName());

    }
    
    /**
     * 数值拆分需要
     * <p>Title: createTTWideNetTradeData</p>
     * <p>Description: </p>
     * <p>Company: AsiaInfo</p>
     * @param btd
     * @param reqData
     * @throws Exception
     * @author XUYT
     * @date 2017-11-6 上午10:03:52
     */
    private void createOtherTradeData(BusiTradeData<BaseTradeData> btd, IMSLandLineRequestData reqData) throws Exception
    {    	
        OtherTradeData otherTradeData = new OtherTradeData();
        
        //第一条
        otherTradeData.setRsrvValueCode("CNTRX");
        otherTradeData.setRsrvValue("成员创建");
        otherTradeData.setUserId(reqData.getUca().getUserId());
        otherTradeData.setStartDate(reqData.getOpenDate());
        otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
        otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTradeData.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值 
        
        otherTradeData.setRsrvStr9("8171");
        otherTradeData.setOperCode("01");        
        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD); 
        btd.add(btd.getRD().getUca().getSerialNumber(), otherTradeData);
        
        OtherTradeData otherTradeData2 = new OtherTradeData();
        //第二条
        otherTradeData2.setRsrvValueCode("CNTRX");
        otherTradeData2.setRsrvValue("多媒体成员业务配置");
        otherTradeData2.setUserId(reqData.getUca().getUserId());
        otherTradeData2.setStartDate(reqData.getOpenDate());
        otherTradeData2.setEndDate(SysDateMgr.getTheLastTime());
        otherTradeData2.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTradeData2.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTradeData2.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值 
        
        otherTradeData2.setRsrvStr9("8171");
        otherTradeData2.setOperCode("03");        
        otherTradeData2.setModifyTag(BofConst.MODIFY_TAG_ADD); 
        btd.add(btd.getRD().getUca().getSerialNumber(), otherTradeData2);
        
        OtherTradeData otherTradeData3 = new OtherTradeData();
        //第三条
        otherTradeData3.setRsrvValueCode("HSS");
        otherTradeData3.setRsrvValue("创建HSS用户");
        otherTradeData3.setUserId(reqData.getUca().getUserId());
        otherTradeData3.setStartDate(reqData.getOpenDate());
        otherTradeData3.setEndDate(SysDateMgr.getTheLastTime());
        otherTradeData3.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTradeData3.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTradeData3.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值 
        
        otherTradeData3.setRsrvStr9("8172");
        otherTradeData3.setOperCode("01");         
        otherTradeData3.setModifyTag(BofConst.MODIFY_TAG_ADD); 
        btd.add(btd.getRD().getUca().getSerialNumber(), otherTradeData3);
        
        OtherTradeData otherTradeData4 = new OtherTradeData();
        //第四条
        otherTradeData4.setRsrvValueCode("ENUM");
        otherTradeData4.setRsrvValue("创建ENUM用户");
        otherTradeData4.setUserId(reqData.getUca().getUserId());
        otherTradeData4.setStartDate(reqData.getOpenDate());
        otherTradeData4.setEndDate(SysDateMgr.getTheLastTime());
        otherTradeData4.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTradeData4.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTradeData4.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值 
        
        otherTradeData4.setRsrvStr9("8173");
        otherTradeData4.setOperCode("01");         
        otherTradeData4.setModifyTag(BofConst.MODIFY_TAG_ADD); 
        btd.add(btd.getRD().getUca().getSerialNumber(), otherTradeData4);
    }
    
    /**
     * 数值拆分需要
     * <p>Title: actTradeIMSIMPU</p>
     * <p>Description: </p>
     * <p>Company: AsiaInfo</p>
     * @param btd
     * @param reqData
     * @throws Exception
     * @author XUYT
     * @date 2017-11-6 下午03:34:25
     */
    public void actTradeIMSIMPU(BusiTradeData<BaseTradeData> btd, IMSLandLineRequestData reqData) throws Exception
    {
    	ImpuTradeData impuTD = new ImpuTradeData();
    	
    	StringBuilder strImpi = new StringBuilder("");
        StringBuilder strTel = new StringBuilder("");
        StringBuilder strSip = new StringBuilder("");
    	String serialNumber = btd.getRD().getUca().getSerialNumber();
    	genImsIMPI(serialNumber, strImpi, "1");
    	genImsIMPU(serialNumber, strTel, strSip, "1");
    	
        
    	impuTD.setUserId(reqData.getUca().getUserId());
    	impuTD.setTelUrl(strTel.toString());
		impuTD.setSipUrl(strSip.toString());
		impuTD.setImpi(strImpi.toString());
		impuTD.setImsUserId(serialNumber);
		impuTD.setImsPassword(reqData.getUserPasswd());
		impuTD.setStartDate(reqData.getOpenDate());
		impuTD.setEndDate(SysDateMgr.getTheLastTime());
		impuTD.setRsrvStr1("0");
		
		String tmp = strTel.toString();
        tmp = tmp.replaceAll("\\+", "");
        char[] c = tmp.toCharArray();
        String str2 = "";
        for (int i = c.length - 1; i >= 1; i--)
        {

            str2 += String.valueOf(c[i]);
            str2 += ".";
        }
        str2 += "6.8.e164.arpa";
        String str3 = "";
        for (int i = 3; i >= 1; i--)
        {

            str3 += String.valueOf(c[i]);
            str3 += ".";
        }
        str3 += "6.8.e164.arpa";
        
        impuTD.setRsrvStr2(str2);
        impuTD.setRsrvStr3(str3);
        impuTD.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值 
    	impuTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
		btd.add(btd.getRD().getUca().getSerialNumber(), impuTD);
    }
    
    /**
     * IMPI
     * <p>Title: genImsIMPI</p>
     * <p>Description: </p>
     * <p>Company: AsiaInfo</p>
     * @param serialNumber
     * @param strImpi
     * @param telType
     * @throws Exception
     * @author XUYT
     * @date 2017-11-6 下午03:50:51
     */
    public static void genImsIMPI(String serialNumber, StringBuilder strImpi, String telType) throws Exception
    {

        String provice = getVisit().getProvinceCode();
        IDataset imsDomains = CommparaInfoQry.getCommparaAllCol("CGM", "9980", provice, "ZZZZ");
        String imsDomain = "";
        if (IDataUtil.isEmpty(imsDomains))
        {
            imsDomain = "@ims.hi.chinamobile.com";
        }
        else
        {
            imsDomain = imsDomains.getData(0).getString("PARAM_NAME", "");
        }

        if ("1".equals(telType))
        { // 固定终端（SIP硬终端或POTS话机）
            strImpi.append(serialNumber).append(imsDomain);
        }
        if ("2".equals(telType))
        { // 无卡PC客户端
            strImpi.append(serialNumber).append("_s").append(imsDomain);
        }
        if ("3".equals(telType))
        { // 签约IMS的CS手机（如签约一号通）
            strImpi.append(serialNumber).append(imsDomain);
        }
    }
    
    /**
     * IMPU
     * <p>Title: genImsIMPU</p>
     * <p>Description: </p>
     * <p>Company: AsiaInfo</p>
     * @param serialNumber
     * @param strTel
     * @param strSip
     * @param telType
     * @throws Exception
     * @author XUYT
     * @date 2017-11-6 下午03:51:14
     */
    public static void genImsIMPU(String serialNumber, StringBuilder strTel, StringBuilder strSip, String telType) throws Exception
    {

        String provice = getVisit().getProvinceCode();
        IDataset imsDomains = CommparaInfoQry.getCommparaAllCol("CGM", "9980", provice, "ZZZZ");
        String imsDomain = "";
        if (IDataUtil.isEmpty(imsDomains))
        {
            // imsDomain = "@ims." + getVisit().getProvinceCode() + ".chinamobile.com";
            imsDomain = "@ims.hi.chinamobile.com";
        }
        else
        {
            imsDomain = imsDomains.getData(0).getString("PARAM_NAME", "");
        }

        if ("1".equals(telType))
        { // 固定终端（SIP硬终端或POTS话机）
            strTel.append(serialNumber);
            strSip.append(serialNumber).append(imsDomain);
        }
        if ("2".equals(telType))
        { // 无卡PC客户端
            strTel.append(serialNumber);
            strSip.append(serialNumber).append("_s").append(imsDomain);
        }
        if ("3".equals(telType))
        { // 签约IMS的CS手机（如签约一号通）
            strTel.append(serialNumber);
            strSip.append(serialNumber).append(imsDomain);
        }
    }
    
    /**
     * 资源预占
     * <p>Title: preSimCard</p>
     * <p>Description: </p>
     * <p>Company: AsiaInfo</p>
     * @param bd
     * @throws Exception
     * @author XUYT
     * @date 2017-11-8 上午10:06:56
     */
    public void preSimCard(BusiTradeData<BaseTradeData> btd, IMSLandLineRequestData reqData) throws Exception
    {
        // 号码预占
        ResCall.resEngrossForMphone(btd.getRD().getUca().getSerialNumber());
    }
    
    /**
     * 资源台账处理
     * <p>Title: createResTradeData</p>
     * <p>Description: </p>
     * <p>Company: AsiaInfo</p>
     * @param btd
     * @throws Exception
     * @author XUYT
     * @date 2017-11-9 下午03:22:02
     */
    public void createResTradeData(BusiTradeData<BaseTradeData> btd, IMSLandLineRequestData reqData) throws Exception
    {
    	String serialNumber = btd.getRD().getUca().getSerialNumber();
        ResTradeData resTD = new ResTradeData();
        String inst_id = SeqMgr.getInstId();
        resTD.setUserId(reqData.getUca().getUserId());
        resTD.setUserIdA("-1");
        resTD.setResTypeCode("0");
        resTD.setResCode(serialNumber);
        resTD.setImsi("");
        resTD.setKi("");
        resTD.setInstId(inst_id);
        resTD.setStartDate(SysDateMgr.getSysTime());
        resTD.setEndDate(SysDateMgr.getTheLastTime());
        resTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        resTD.setRsrvStr1(reqData.getMainProduct().getBrandCode());
        btd.add(btd.getRD().getUca().getSerialNumber(), resTD);
    }
    
	/**
     * 构建终端串号OTHER表数据
     * <p>Title: createResIdOtherTradeData</p>
     * <p>Description: </p>
     * <p>Company: AsiaInfo</p>
     * @param btd
     * @param reqData
     * @throws Exception
     * @author WANGSC10
     * @date 2019-6-26 上午10:03:52
     */
    private void createResIdOtherTradeData(BusiTradeData<BaseTradeData> btd, IMSLandLineRequestData reqData) throws Exception
    {    	
    	if(null != reqData.getMainProduct()){
    		if(null != reqData.getMainProduct().getProductId()){
    			String activesaleId = reqData.getMoPackageId();//营销活动ID
    			String resId = reqData.getResId();//串号
    			if(reqData.getMainProduct().getProductId().equals("84018059") && resId != null){
        			OtherTradeData otherTradeData = new OtherTradeData();
        	        
        	        otherTradeData.setRsrvValueCode("RESID");
        	        otherTradeData.setRsrvValue(resId);
        	        otherTradeData.setUserId(reqData.getUca().getUserId());
        	        otherTradeData.setStartDate(reqData.getOpenDate());
        	        otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
        	        otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
        	        otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
        	        otherTradeData.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值 
        	        otherTradeData.setRsrvStr10(activesaleId);
        	        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD); 
        	        btd.add(btd.getRD().getUca().getSerialNumber(), otherTradeData);
        		}
    		}
    	}
    }
	
    /**
     * 客户资料表
     * //REQ201810190032++和家固话开户界面增加实名制校验—BOSS侧   by mqx
     * @author mengqx
     * @param btd
     * @throws Exception
     */
    public void createCustomerTradeData(BusiTradeData btd) throws Exception
    {
    	IMSLandLineRequestData rd = (IMSLandLineRequestData) btd.getRD();
        String serialNumber = rd.getUca().getUser().getSerialNumber();
        CustomerTradeData customerTD = rd.getUca().getCustomer().clone();

        btd.add(serialNumber, customerTD);
    }


    /**
     * 生成台帐个人客户表
     * //REQ201810190032++和家固话开户界面增加实名制校验—BOSS侧   by mqx
     * @author mengqx
     * @param btd
     * @throws Exception
     */
    public void createCustPersonTradeData(BusiTradeData btd) throws Exception
    {
    	IMSLandLineRequestData rd = (IMSLandLineRequestData) btd.getRD();
        String serialNumber = rd.getUca().getUser().getSerialNumber();
        CustPersonTradeData custpersonTD = rd.getUca().getCustPerson().clone();

        btd.add(serialNumber, custpersonTD);
    }

    /**
     * 营业执照类型信息
     * @param btd
     * @throws Exception
     */
    public void createOtherTradeDataEnterprise(BusiTradeData btd) throws Exception
    {
    	IMSLandLineRequestData imsLandLineRD = (IMSLandLineRequestData) btd.getRD();
        String serialNumber = imsLandLineRD.getUca().getUser().getSerialNumber();
        String agentDepartId = CSBizBean.getVisit().getDepartId();
        IData idRequestData = btd.getRD().getPageRequestData();
        String legalperson = idRequestData.getString("legalperson","").trim();//法人
        String startdate = idRequestData.getString("startdate","").trim();//成立日期
        String termstartdate = idRequestData.getString("termstartdate","").trim();//营业开始时间
        String termenddate = idRequestData.getString("termenddate","").trim();//营业结束时间
        OtherTradeData otherTD = new OtherTradeData();
        otherTD.setUserId(imsLandLineRD.getUca().getUserId());
        otherTD.setRsrvValueCode("ENTERPRISE");
        otherTD.setRsrvValue("营业执照");
        otherTD.setRsrvStr1(legalperson);
        otherTD.setRsrvStr2(startdate);
        otherTD.setRsrvStr3(termstartdate);
        otherTD.setRsrvStr4(termenddate);
        otherTD.setRsrvStr5(CSBizBean.getVisit().getStaffName());
        otherTD.setStartDate(SysDateMgr.getSysTime());
        otherTD.setEndDate(SysDateMgr.getTheLastTime());
        otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        otherTD.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTD.setDepartId(agentDepartId);
        otherTD.setInstId(SeqMgr.getInstId());
        btd.add(serialNumber, otherTD);
    }
    
    /**
     * REQ201810190032  和家固话开户界面增加实名制校验—BOSS侧  by mqx 20190108
     * 单位证件开户权限判断
     */
	private void verifyOrganizationPriv(BusiTradeData btd) throws Exception{
		IMSLandLineRequestData imsLandLineRD = (IMSLandLineRequestData) btd.getRD();
        CustomerTradeData customerTD = imsLandLineRD.getUca().getCustomer();
        String psptTypeCode = customerTD.getPsptTypeCode();
    	String staffId = getVisit().getStaffId();

		IDataset organizationPsptTypes = CommparaInfoQry.getCommNetInfo("CSM", "2019", "VERIFY_ORGANIZATION_PSPT_TYPE_CODE");
		if(!StaffPrivUtil.isFuncDataPriv(staffId, "OP_ORGANIZATION_PRIV")&&psptTypeCode!=null){
			if(IDataUtil.isNotEmpty(organizationPsptTypes)){
				for (int i = 0; i < organizationPsptTypes.size(); i++) {
					IData organizationPsptType = organizationPsptTypes.getData(i);
					if(psptTypeCode.equals(organizationPsptType.getString("PARA_CODE1"))){
						CSAppException.appError("201901080", "该工号不具有和家固话单位证件开户权限（仅限自办厅工号申请）");
					}
				}
			}
     	}
	}
}
