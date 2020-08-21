package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.WlwBusiHelperGrp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMember;
import com.asiainfo.veris.crm.order.soa.group.internetofthings.GeneIotInstIdBean;
import com.asiainfo.veris.crm.order.soa.group.internetofthings.GrpWlwInstancePfQuery;
import com.asiainfo.veris.crm.order.soa.group.internetofthings.WlwGrpConstants;
import com.asiainfo.veris.crm.order.soa.script.rule.iot.IotCheck;


public class CreateWlwGroupMember extends CreateGroupMember
{
    
    private static final Logger logger = Logger.getLogger(CreateWlwGroupMember.class);
    
    private IData paramData = new DataMap();
    private String strApplyTypeA = "";
    private String strApplyTypeB = "";
    /**
     * @author yanwu
     * @return
     * @throws Exception
     */
    private IData getParamData() throws Exception
    {
        String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        IData paramData = reqData.cd.getProductParamMap(baseMemProduct);
        if (IDataUtil.isEmpty(paramData))
        {
            return null;
        }
        return paramData;
    }

    public void actTradeBefore() throws Exception
    {
        IDataset svcDatas = reqData.cd.getSvc();
        for (int row = svcDatas.size() - 1; row >= 0; row--)
        {
            IData map = svcDatas.getData(row);
            map.put("OPER_CODE", "06"); // 操作码, 06-订购

            if ("99011000".equals(map.getString("ELEMENT_ID")))
            {
                int size = UserSvcInfoQry.getUserSvcInfoByUserSvcId(reqData.getUca().getUserId(), "99011002");
                if (size > 0)
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "订购的短信标准和个人订购的短信优先级2冲突.");
                }
            }
            else if ("99011002".equals(map.getString("ELEMENT_ID")))
            {
                int size = UserSvcInfoQry.getUserSvcInfoByUserSvcId(reqData.getUca().getUserId(), "99011000");
                if (size > 0)
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "订购的短信优先级2和个人订购的短信标准冲突.");
                }
            }
        }
        
        super.actTradeBefore();
        
        //@author yanwu begin
        paramData = getParamData();
        if ( IDataUtil.isNotEmpty(paramData) )
        {
            strApplyTypeA = paramData.getString("APPLY_TYPE_A");
            strApplyTypeB = paramData.getString("APPLY_TYPE_B");
        }
        //@author yanwu end 
        
    }

    /**
     * 其它台帐处理
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        String tradeId = getTradeId();
        String tradeTypeCode = getTradeTypeCode();
        //物联网测试期资费时间处理
        
        WlwBusiHelperGrp.dealTestDiscnt(reqData.getUca(),bizData.getTradeDiscnt(),bizData.getTradeAttr());
                
        WlwBusiHelperGrp.dealNbIotSmsDiscnt(reqData.getUca(),bizData.getTradeDiscnt());
    	
    	String productId = reqData.getBaseMebProductId();
    	boolean hasTestDiscnt = false;
    	if("20171215".equals(productId))
    	{
    		IDataset infos = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "3997", 
					"20171215", "OPENCLOSEOPERTYPE","0","0898");
    		
    		if(IDataUtil.isNotEmpty(infos))
    		{
    			IData dataMap = WlwBusiHelperGrp.dealNbIotGrpMebTradeSvc(reqData.getUca(),bizData.getTradeDiscnt(),
            			bizData.getTradeSvc(),bizData.getTradeAttr());
        		
        		if(IDataUtil.isNotEmpty(dataMap))
        		{
        			IDataset svcsSet =  dataMap.getDataset("WLW_TRADE_SVC");
        			IDataset attrsSet = dataMap.getDataset("WLW_TRADE_SVCATTR");
        			hasTestDiscnt = dataMap.getBoolean("HAS_TEST_DISCNT");
        			this.addTradeSvc(svcsSet);
        			this.addTradeAttr(attrsSet);
        		}
    		}
    		
    	}
    	
    	
        // 服务，资费， 产品 实例转换
        GeneIotInstIdBean.geneProdInstId(reqData, tradeId,tradeTypeCode,bizData);
        // 生成包实例
        GeneIotInstIdBean.genePkgInstId(reqData, tradeId, tradeTypeCode,bizData,hasTestDiscnt);
        
        //@author yanwu begin
        //infoRegDataOther();
        /*IData userData = reqData.getUca().getUser().toData();
        if( IDataUtil.isNotEmpty(userData) ) {
        	userData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        	userData.put("RSRV_STR9", strApplyTypeA);
        	userData.put("RSRV_STR10", strApplyTypeB);

            super.addTradeUser(userData);
        }*/
        //@author yanwu end 
        
        infoRegTradeAttr();
        
        regTradeTestDiscnt();
        
        dealTradeDiscntAttr();
    }
    /**
     * @author yanwu 
     * @date 2015-08-01 
     */
    /*@Override
    public void setTradeUser(IData map) throws Exception
    {
    	String strPsw = map.getString("USER_PASSWD", ""); 
    	super.setTradeUser(map);
    	map.put("USER_PASSWD", strPsw); // 用户密码
    	map.put("RSRV_STR9", strApplyTypeA); 
    	map.put("RSRV_STR10", strApplyTypeB); 
    }*/
    
    /**
     *@author yanwu
     *@date 2015-08-01 
     */
    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        
        if( logger.isDebugEnabled() ){
            logger.debug("-----------------------yanwu---------------------2222" + map.toString());
        }
        String strBatid = map.getString("BATCH_ID");
        if( !"".equals(strBatid) ){
            IData pam = map.getData("ALL_PARAM");
            if( IDataUtil.isNotEmpty(pam) ){
                strApplyTypeA = pam.getString("APPLY_TYPE_A");
                strApplyTypeB = pam.getString("APPLY_TYPE_B");
            }
            if( logger.isDebugEnabled() ){
                logger.debug("-----------------------yanwu--------------------------3333" + strApplyTypeA + strApplyTypeB);
            }
        } 
    }
    
    /**
     * @author yanwu
     * @date 2015-09-08
     * 处理Other信息
     * 
     * @throws Exception
     */
    /*public void infoRegDataOther() throws Exception
    {
        IData otherData = new DataMap();
        otherData.put("USER_ID", reqData.getUca().getUserId());
        otherData.put("RSRV_VALUE_CODE", "TRADE_APP_TYPE");
        otherData.put("RSRV_VALUE", reqData.getUca().getSerialNumber());
        otherData.put("RSRV_NUM1", getTradeTypeCode());
        otherData.put("RSRV_STR1", strApplyTypeA);
        otherData.put("RSRV_STR11", strApplyTypeB);
        otherData.put("RSRV_STR29", reqData.getUca().getCustId());
        otherData.put("RSRV_STR30", reqData.getGrpUca().getSerialNumber());
        otherData.put("REMARK", "REQ201507100002 关于增加M2M、物联卡产品行业应用类型的需求");
        otherData.put("INST_ID", SeqMgr.getInstId());
        otherData.put("START_DATE", SysDateMgr.getSysTime());
        otherData.put("END_DATE", SysDateMgr.getTheLastTime());
        otherData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        super.addTradeOther(otherData);
    }*/
    
    /**
     *@author yanwu
     *@date 2015-09-15 
     */
    @Override
    public void setTradeRelation(IData map) throws Exception
    {
        super.setTradeRelation(map);
        
        map.put("RSRV_STR4", strApplyTypeA); 
        map.put("RSRV_STR5", strApplyTypeB); 
    }
    
    /**
     * @author yanwu
     * @date 2016-01-27
     */
    @Override
    public void setTradeProduct(IData map) throws Exception
    {
        super.setTradeProduct(map);
        
        String strBrandCode = map.getString("BRAND_CODE");
        if("PWLW".equals(strBrandCode) || "WLWG".equals(strBrandCode))
        {
            String productId = map.getString("PRODUCT_ID");
            IDataset ids = UserProductInfoQry.getUserProductByUserIdProductId(reqData.getUca().getUserId(), productId);
            if(IDataUtil.isNotEmpty(ids)){
                
                String firstTimeNextAcct = SysDateMgr.getFirstDayOfNextMonth() + SysDateMgr.getFirstTime00000();
                map.put("START_DATE", firstTimeNextAcct);
            }
        }
    }
    
    /**
     * 
     * @throws Exception
     */
    private void infoRegTradeAttr() throws Exception
    {
    	String userId = reqData.getGrpUca().getUserId();
        String serialNumber = reqData.getUca().getSerialNumber();
        IData subData = GrpWlwInstancePfQuery.queryGrpWlwSubsIdByUserId(userId);
        String groupCode = "";
        if(IDataUtil.isNotEmpty(subData))
        {
        	groupCode = subData.getString("SUBS_ID","");
        }
        
        //新增个人智能网语音通信服时，对该服务属性做处理
        boolean svcExist1019 = false;//99011019 个人智能网语音通信服
        boolean svcExist1020 = false; //99011020 语音通信服务
        boolean svcExist1024 = false;//99011024 车联网专用数据通信服务
        boolean svcExist1025 = false;//99011025 车联网数据通信服务
        
        boolean svcExist1005 = false;//99011005	4G_GPRS(大流量)
        boolean svcExist1012 = false;//99011012 4G_GPRS(中小流量)
        boolean svcExist1021 = false;//99011021 物联网专用数据通信服务(可选)
        boolean svcExist1022 = false;//99011022 通用流量4G_GPRS服务
                
        boolean svcExist1028 = false;//99011028 数据通信服务(和对讲)
        boolean svcExist1029 = false;//99011029 物联网专用数据通信服务(和对讲)
        
        boolean svcExist6926 = false; //99646926 用户策略服务产品
        boolean svcExist6927 = false;//99646927 用户策略服务产品
        
        boolean svcExist1030 = false;//99011030	机卡绑定(网络校验)产品-机器卡
        boolean svcExist1031 = false;//99011031	机卡绑定(网络校验)产品-和对讲
        boolean svcExist1032 = false;//99011032	机卡绑定(网络校验)产品-车联网
        
        boolean svcExist1041= false; //99011041 数据通信服务 机器卡-2020 
        boolean svcExist1042= false; //99011042 物联网专用数据通信服务 机器卡-2020 
        
        IDataset tradeSvcs = bizData.getTradeSvc();
        if(IDataUtil.isNotEmpty(tradeSvcs))
        {
        	for(int i=0; i < tradeSvcs.size(); i++)
        	{
        		IData svc = tradeSvcs.getData(i);
        		String serviceId = svc.getString("SERVICE_ID","");
        		if("99011019".equals(serviceId)) {
        			svcExist1019 = true;
        		} else if("99011020".equals(serviceId)) {
        			svcExist1020 = true;
        		} else if("99011024".equals(serviceId)) {
        			svcExist1024 = true;
        		} else if("99011025".equals(serviceId)) {
        			svcExist1025 = true;
        		} else if("99011005".equals(serviceId)) {
        			svcExist1005 = true;
        		} else if("99011012".equals(serviceId)) {
        			svcExist1012 = true;
        		} else if("99011021".equals(serviceId)) {
        			svcExist1021 = true;
        		} else if("99011022".equals(serviceId)) {
        			svcExist1022 = true;
        		} else if("99011028".equals(serviceId)) {
        			svcExist1028 = true;
        		} else if("99011029".equals(serviceId)) {
        			svcExist1029 = true;
        		} else if("99646926".equals(serviceId)) {
        			svcExist6926 = true;
        		} else if("99646927".equals(serviceId)) {
        			svcExist6927 = true;
        		} else if("99011030".equals(serviceId)) {
        			svcExist1030 = true;
        		} else if("99011031".equals(serviceId)) {
        			svcExist1031 = true;
        		} else if("99011032".equals(serviceId)) {
        			svcExist1032 = true;
        		} else if("99011041".equals(serviceId)) {
        			svcExist1041 = true;
        		} else if("99011042".equals(serviceId)) {
        			svcExist1042 = true;
        		}
        	}
        }
        
        
        if(svcExist1019) //个人智能网语音通信服
        {
        	IDataset tradeAttrs = bizData.getTradeAttr();
            if(IDataUtil.isNotEmpty(tradeAttrs))
            {
            	for(int i=0; i < tradeAttrs.size(); i++)
            	{
            		IData param = tradeAttrs.getData(i);
            		String modifyTag = param.getString("MODIFY_TAG","");
            		String attrCode = param.getString("ATTR_CODE","");
            		if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && !"".equals(attrCode))
            		{
            			IDataset infos = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "3997", 
            					"99011019", attrCode,"0","0898");
            			if(IDataUtil.isNotEmpty(infos))
            			{
            				param.put("REMARK", "WLW_SVC_ATTR");
            			}
            			if("GroupCode".equals(attrCode))
            			{
                			param.put("ATTR_VALUE", groupCode);
                		}
            			else if("PhoneNumber".equals(attrCode))
            			{
            				param.put("ATTR_VALUE", serialNumber);
                		}
            		}
            	}
            }
        }
        
        if(svcExist1020){//语音通信服务
        	IDataset tradeAttrs = bizData.getTradeAttr();
            if(IDataUtil.isNotEmpty(tradeAttrs))
            {
            	for(int i=0; i < tradeAttrs.size(); i++)
            	{
            		IData param = tradeAttrs.getData(i);
            		String modifyTag = param.getString("MODIFY_TAG","");
            		String attrCode = param.getString("ATTR_CODE","");
            		if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && !"".equals(attrCode))
            		{
            			IDataset infos = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "3997", 
            					"99011020", attrCode,"0","0898");
            			if(IDataUtil.isNotEmpty(infos))
            			{
            				param.put("REMARK", "WLW_SVC_ATTR");
            			}
            		}
            	}
            }
        }
        
        //车联网专用数据通信服务 //车联网数据通信服务 //99011005	4G_GPRS(大流量)
        //99011012 4G_GPRS(中小流量)
        //99011021 物联网专用数据通信服务(可选)
        //99011022 通用流量4G_GPRS服务
        //数据通信服务(和对讲)
        //99011029 物联网专用数据通信服务(和对讲)
        //99646926 用户策略服务产品
        //99011033	区域限制服务功能产品-机器卡车联网
        //99011034	区域限制服务功能产品-和对讲
        if(svcExist1024 || svcExist1025 || svcExist1005 || svcExist1012 || svcExist1021 || svcExist1022
        		|| svcExist1028 || svcExist1029 || svcExist6926 || svcExist6927 || svcExist1030 || svcExist1031 || svcExist1032
        		|| svcExist1041 || svcExist1042){
        	
        	IDataset tradeAttrs = bizData.getTradeAttr();
            if(IDataUtil.isNotEmpty(tradeAttrs))
            {
            	for(int i=0; i < tradeAttrs.size(); i++)
            	{
            		IData param = tradeAttrs.getData(i);
            		String modifyTag = param.getString("MODIFY_TAG","");
            		String attrCode = param.getString("ATTR_CODE","");
            		if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && !"".equals(attrCode))
            		{
            			if("OperType".equals(attrCode) ||
            					"ServiceBillingType".equals(attrCode) || 
            					"ServiceCode".equals(attrCode) ||
            					"ServiceEndDateTime".equals(attrCode) ||
            					"ServiceStartDateTime".equals(attrCode) ||
            					"ServiceUsageState".equals(attrCode) ||
            					"usrSessionPolicyCode".equals(attrCode) ||
            					"NotificationCycle".equals(attrCode) ||
            					"TerminalType".equals(attrCode) ||
            					"SessionPolicyStartDateTime".equals(attrCode) ||
            					"SessionPolicyEndDateTime".equals(attrCode) ||
            					"IMEI".equals(attrCode))
            			{
            				param.put("REMARK", "WLW_SVC_ATTR");
            			}
            			else if("usrSessionPolicyCode_Sb".equals(attrCode) ||
            					"NotificationCycle_Sb".equals(attrCode) ||
            					"TerminalType_Sb".equals(attrCode) ||
            					"SessionPolicyStartDateTime_Sb".equals(attrCode) ||
            					"SessionPolicyEndDateTime_Sb".equals(attrCode)||
            					"OperType_Sb".equals(attrCode))
            			{
            				param.put("REMARK", "WLW_SVC_ATTR_SB");
            			}
            			else if("usrSessionPolicyCode_Sb2".equals(attrCode) ||
            					"NotificationCycle_Sb2".equals(attrCode) ||
            					"TerminalType_Sb2".equals(attrCode) ||
            					"SessionPolicyStartDateTime_Sb2".equals(attrCode) ||
            					"SessionPolicyEndDateTime_Sb2".equals(attrCode)||
            					"OperType_Sb2".equals(attrCode))
            			{
            				param.put("REMARK", "WLW_SVC_ATTR_SB2");
            			}
            		}
            	}
            }
        }
        
    }

	@Override
	protected void setTradeAttr(IData map) throws Exception {
		//----add by chenzg@20171211---REQ201711150003关于新增物联卡本省折扣需求--begin----
		if(IDataUtil.isNotEmpty(map)){
			String instType = map.getString("INST_TYPE", "");
			String attrCode = map.getString("ATTR_CODE", "");
			if("D".equals(instType)&&"20171211".equals(attrCode)){
				/*判断工号是否有本省折扣权限*/
				String tradeStaffId = CSBizBean.getVisit().getStaffId();
				if(!StaffPrivUtil.isPriv(tradeStaffId, "PRIV_WLWATTR_DISCOUNT0898", "1")){
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "您没有本省折扣权限，不能填本省折扣，请确认!");
				}
			}
		}
		//----add by chenzg@20171211---REQ201711150003关于新增物联卡本省折扣需求--end------
		super.setTradeAttr(map);
	}
	
	/**
	 * 物联网将订购了测试期优惠保存到TF_INTERNETOFTHING_BOOK表
	 * @throws Exception
	 */
	private void regTradeTestDiscnt() throws Exception
	{
		IDataset tradeDiscnts = bizData.getTradeDiscnt();
		if(IDataUtil.isNotEmpty(tradeDiscnts))
		{
			for(int i=0; i < tradeDiscnts.size(); i++)
         	{
				IData tradeDiscnt = tradeDiscnts.getData(i);
         		String discntCode = tradeDiscnt.getString("DISCNT_CODE","");
         		String endDate = tradeDiscnt.getString("END_DATE","");
         		IData data = WlwGrpConstants.GRP_TEST_DISCNT_CONFIG.getData(discntCode);
         		if(IDataUtil.isNotEmpty(data))//有订购测试期套餐
         		{
         			this.insertIotBook(endDate);
         			break;
         		}
         	}
		}
	}
	
	/**
	 * 
	 * @param endDate
	 * @throws Exception
	 */
	private void insertIotBook(String endDate) throws Exception
	{
		 IData bookData = new DataMap();
         bookData.put("INST_ID", SeqMgr.getInstId());
         bookData.put("USER_ID", reqData.getUca().getUserId());
         bookData.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
         bookData.put("OLD_STATE_CODE", "0");
         bookData.put("NEW_STATE_CODE", "1");
         bookData.put("TRAN_DATE", endDate);
         bookData.put("DEAL_TAG", "0");// 默认为未处理
         bookData.put("EXC_TIME", SysDateMgr.getSysTime());
         bookData.put("RESULT_CODE", "");
         bookData.put("RESULT_INFO", "");
         bookData.put("TRADE_ID", reqData.getTradeId());
         bookData.put("RSRV_STR1", reqData.getOrderId());
         //bookData.put("RSRV_STR2", strCMQ + ", " + strAT);
         bookData.put("UPDATE_TIME", SysDateMgr.getSysTime());
         bookData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
         bookData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
         Dao.insert("TF_F_INTERNETOFTHINGS_BOOK", bookData, Route.CONN_CRM_CEN);
	}
	
	/**
	 * NB套餐、长周期套餐的折后价处理
	 * @throws Exception
	 */
	private void dealTradeDiscntAttr() throws Exception
	{
		IDataset tradeDiscnts = bizData.getTradeDiscnt();
		IDataset tradeAttrs = bizData.getTradeAttr();
		if(IDataUtil.isNotEmpty(tradeDiscnts))
		{
			for(int i=0; i < tradeDiscnts.size(); i++)
         	{
				IData tradeDiscnt = tradeDiscnts.getData(i);
         		String discntCode = tradeDiscnt.getString("DISCNT_CODE","");
         		String modifyTag = tradeDiscnt.getString("MODIFY_TAG","");
         		String instId = tradeDiscnt.getString("INST_ID","");
         		if(BofConst.MODIFY_TAG_ADD.equals(modifyTag))
         		{
         			IData discntConfig = IotCheck.DISCNT_CONFIG_MAP.get(discntCode);
             		if(IDataUtil.isNotEmpty(discntConfig))
             		{
             			String paramCode = discntConfig.getString("PARAM_CODE","");
             			String paraCode2 = discntConfig.getString("PARA_CODE2","");
             			//长周期套餐
             			if("I00010101005".equals(paraCode2) || "I00010101006".equals(paraCode2)
             					|| "I00010101013".equals(paraCode2) || "I00010101014".equals(paraCode2)) 
             			{
                 			String month = discntConfig.getString("PARA_CODE24","");//月数
             				//先获取填写的费用
             				String filterStr1 = "RELA_INST_ID=" + instId + ",ATTR_CODE=BottomPrice,MODIFY_TAG=0,INST_TYPE=D";
             				if(IDataUtil.isNotEmpty(tradeAttrs))
             				{
             					String muLuJia = "";//填写的费用
             					String firstMoney = "";//第一个月的费用
             					String lastMoney = "";//最后一个月的费用
             					IDataset filterDatas = DataHelper.filter(tradeAttrs,  filterStr1);
             					if(IDataUtil.isNotEmpty(filterDatas))
             					{
             						IData filterData = filterDatas.getData(0);
             						muLuJia = filterData.getString("ATTR_VALUE","");//填写的费用
             					}
             					if(StringUtils.isNotBlank(month) && StringUtils.isNotBlank(muLuJia))
             					{
             						BigDecimal bottomPriceDec = new BigDecimal(muLuJia);
                            		BigDecimal bigMonth = new BigDecimal(month);
                            		//每个月的平均价格,也是第一个月的费用
                					BigDecimal bigAverage = bottomPriceDec.divide(bigMonth,2,BigDecimal.ROUND_HALF_UP);
                					//转化成分
                					BigDecimal big100Average = bigAverage.multiply(new BigDecimal(100)).setScale(0);
                					firstMoney = big100Average.toString();//第一个月的费用
                					
                					//总月份减掉1,再用总的费用减去平均价格乘以减掉1后的月份数
                					//总月份减去1后
                					BigDecimal bigRemainMonth = bigMonth.subtract(new BigDecimal(1));
                					//平均价格乘以总月份减去1后的月份数
                					BigDecimal bigRemainMoney = bigAverage.multiply(bigRemainMonth);
                					//最后一个月的费用
                					BigDecimal bigLastMoney = bottomPriceDec.subtract(bigRemainMoney);
                					//转化成分
                					BigDecimal big100LastMoney = bigLastMoney.multiply(new BigDecimal(100)).setScale(0);
                					lastMoney = big100LastMoney.toString();
             					}
             					
             					if(StringUtils.isNotBlank(firstMoney) && StringUtils.isNotBlank(lastMoney))
             					{
             						String filterFirst= "RELA_INST_ID=" + instId + ",ATTR_CODE=20200511,MODIFY_TAG=0,INST_TYPE=D";
             						String filterLast = "RELA_INST_ID=" + instId + ",ATTR_CODE=20200512,MODIFY_TAG=0,INST_TYPE=D";
             						IDataset firstDatas = DataHelper.filter(tradeAttrs,  filterFirst);
             						IDataset lastDatas = DataHelper.filter(tradeAttrs,  filterLast);
             						if(IDataUtil.isNotEmpty(firstDatas))
             						{
             							IData firstData = firstDatas.getData(0);
                 						firstData.put("ATTR_VALUE", firstMoney);
             						}
             						if(IDataUtil.isNotEmpty(lastDatas))
             						{
             							IData lastData = lastDatas.getData(0);
             							lastData.put("ATTR_VALUE", lastMoney);
             						}
             					}
             				}
             			}
             			//NB-iot包年套餐
             			else if("I00011100002".equals(paraCode2)) 
             			{
             				if("84068842".equals(paramCode) || "84068843".equals(paramCode))
             				{
             					String year = discntConfig.getString("PARA_CODE24","");//年数
                 				//先获取填写的费用
                 				String filterStr1 = "RELA_INST_ID=" + instId + ",ATTR_CODE=BottomPrice,MODIFY_TAG=0,INST_TYPE=D";
                 				if(IDataUtil.isNotEmpty(tradeAttrs))
                 				{
                 					String muLuJia = "";//填写的费用
                 					String allMoney = "";//总费用
                 					IDataset filterDatas = DataHelper.filter(tradeAttrs,  filterStr1);
                 					if(IDataUtil.isNotEmpty(filterDatas))
                 					{
                 						IData filterData = filterDatas.getData(0);
                 						muLuJia = filterData.getString("ATTR_VALUE","");//填写的费用
                 					}
                 					if(StringUtils.isNotBlank(year) && StringUtils.isNotBlank(muLuJia))
                 					{
                 						BigDecimal bottomPriceDec = new BigDecimal(muLuJia);
                                		BigDecimal bigYear = new BigDecimal(year);
                                		//总费用
                    					BigDecimal bigAllMoney = bottomPriceDec.multiply(bigYear);
                    					//转化成分
                    					BigDecimal big100AllMoney = bigAllMoney.multiply(new BigDecimal(100)).setScale(0);
                    					allMoney = big100AllMoney.toString();
                 					}
                 					if(StringUtils.isNotBlank(allMoney))
                 					{
                 						String filterStr2 = "RELA_INST_ID=" + instId + ",ATTR_CODE=20200513,MODIFY_TAG=0,INST_TYPE=D";
                 						IDataset allDatas = DataHelper.filter(tradeAttrs,  filterStr2);
                 						if(IDataUtil.isNotEmpty(allDatas))
                 						{
                 							IData allData = allDatas.getData(0);
                 							allData.put("ATTR_VALUE", allMoney);
                 						}
                 					}
                 				}
             				}
             			}
             		}
         		}
         	}
		}
	}
	
}
