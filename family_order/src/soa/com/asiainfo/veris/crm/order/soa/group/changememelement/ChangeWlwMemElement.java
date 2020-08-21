
package com.asiainfo.veris.crm.order.soa.group.changememelement;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCallIntf;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.WlwBusiHelperGrp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;
import com.asiainfo.veris.crm.order.soa.group.internetofthings.GeneIotInstIdBean;
import com.asiainfo.veris.crm.order.soa.group.internetofthings.GrpWlwInstancePfQuery;
import com.asiainfo.veris.crm.order.soa.group.internetofthings.WlwGrpConstants;
import com.asiainfo.veris.crm.order.soa.script.rule.iot.IotCheck;

public class ChangeWlwMemElement extends ChangeMemElement
{
	private static final Logger logger = Logger.getLogger(ChangeWlwMemElement.class);
    private static final String memIotSvcId = "9014";

    public void actTradeBefore() throws Exception
    {
        IDataset svcDatas = reqData.cd.getSvc();
        for (int row = svcDatas.size() - 1; row >= 0; row--)
        {
            IData map = svcDatas.getData(row);
            String modifyTag = map.getString("MODIFY_TAG");
            String elementId = map.getString("ELEMENT_ID");
            
            if ("0".equals(map.getString("MODIFY_TAG")))
            {
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
            
            if("99011000".equals(elementId) || "99011002".equals(elementId))
            {
            	if("2".equals(modifyTag))
            	{
            		CSAppException.apperr(CrmCommException.CRM_COMM_103, "物联网短信服务属性不允许做修改变更.");
            	}
            }
        }
        
        super.actTradeBefore();
        
        
        
        
    }

    /**
     * 其它台帐处理
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        regLimitDiscountAttr();
        
        // 登记物联网成员主体服务
        infoRegTradeSvc();
        
        // 登记物联网成员主体产品
        infoRegTradeProduct();

        dealDelDiscnt();
        
        String tradeId = getTradeId();
      //cancel_mode为7时套餐已经生效的套餐不能取消
        WlwBusiHelperGrp.checkTradeCancelMode(reqData.cd.getDiscnt());
        //物联网测试期资费时间处理
        WlwBusiHelperGrp.dealTestDiscnt(reqData.getUca(),bizData.getTradeDiscnt(),bizData.getTradeAttr());
        
        WlwBusiHelperGrp.dealNbIotSmsDiscnt(reqData.getUca(),bizData.getTradeDiscnt());
    	
        String tradeTypeCode = getTradeTypeCode();
        // 服务，资费， 产品 实例转换
        GeneIotInstIdBean.geneProdInstId(reqData, tradeId,tradeTypeCode,bizData);
        // 生成包实例
        GeneIotInstIdBean.genePkgInstId(reqData, tradeId, tradeTypeCode,bizData,false);
        
        infoRegTradeAttr();
        
		regTradeTestDiscnt();
		
		dealTradeDiscntAttr();
    }
    
    /*
     * 登记物联网成员主体服务
     */
    public void infoRegTradeSvc() throws Exception
    {
        IDataset userSvc = UserSvcInfoQry.getSvcUserId(reqData.getUca().getUserId(), memIotSvcId);
        if (userSvc.isEmpty())
        {
            return;
        }

        IDataset svcDatas = new DatasetList();
        for (int i = 0, size = userSvc.size(); i < size; i++)
        {
            IData map = new DataMap();
            map.putAll(userSvc.getData(i));
            map.put("ELEMENT_ID", map.getString("SERVICE_ID")); // 调用addTradeSvc竟然要传ELEMENT_ID,没辙
            map.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            map.put("OPER_CODE", "08"); // 2，08-用户信息变更
            svcDatas.add(map);
        }

        if (svcDatas.size() > 0)
        {
            addTradeSvc(svcDatas);
        }
    }
    
    /*
     * 登记物联网成员主体产品
     */
    public void infoRegTradeProduct() throws Exception
    {
        String strBrandCode = reqData.getGrpUca().getBrandCode();
        /*if("20005013".equals(productId) || "20005014".equals(productId)
        || "20005015".equals(productId) || "20005016".equals(productId)){*/
        if("PWLW".equals(strBrandCode) || "WLWG".equals(strBrandCode))
        {
            String productId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
            IDataset userProduct = UserProductInfoQry.getUserProductByUserIdEnd(reqData.getUca().getUserId(), productId);
            if(IDataUtil.isNotEmpty(userProduct))
            {
                IDataset ProductDatas = new DatasetList();
                IData map = userProduct.getData(0);
                map.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                ProductDatas.add(map);
                
                if(IDataUtil.isNotEmpty(ProductDatas))
                {
                    addTradeProduct(ProductDatas);
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
			String modTag = map.getString("MODIFY_TAG", "");
			if("D".equals(instType)&&"20171211".equals(attrCode)){
				/*判断工号是否有本省折扣权限*/
				String tradeStaffId = CSBizBean.getVisit().getStaffId();
				if(!StaffPrivUtil.isPriv(tradeStaffId, "PRIV_WLWATTR_DISCOUNT0898", "1")){
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "您没有本省折扣权限，不能修改本省折扣，请确认!");
				}
			}
		}
		//----add by chenzg@20171211---REQ201711150003关于新增物联卡本省折扣需求--end------
		super.setTradeAttr(map);
	}
    
    private void infoRegTradeAttr() throws Exception
    {
    	IDataset svcTrades = bizData.getTradeSvc();
    	boolean svcAdd1019 = false;//99011019 个人智能网语音通信服务新增
    	boolean svcModify1019 = false;//99011019 个人智能网语音通信服务修改
    	boolean svcDel1019 = false;//99011019 个人智能网语音通信服务删除
    	
    	boolean svcAdd1020 = false;//99011020 语音通信服务(可选)
    	boolean svcDel1020 = false;//99011020 语音通信服务(可选)
    	
    	boolean svcDel1005 = false;//99011005 4G_GPRS(大流量)
    	boolean svcAdd1005 = false;//99011005 4G_GPRS(大流量)
    	
    	boolean svcDel1012 = false;//99011012 4G_GPRS(中小流量
    	boolean svcAdd1012 = false;//99011012 4G_GPRS(中小流量
    	
    	boolean svcDel1021 = false;//99011021 物联网专用数据通信服务(可选)
    	boolean svcAdd1021 = false;//99011021 物联网专用数据通信服务(可选)
    	
    	boolean svcDel1022 = false;//99011022 通用流量4G_GPRS服务
    	boolean svcAdd1022 = false;//99011022 通用流量4G_GPRS服务
    	
    	boolean svcDel1028 = false;//99011028 数据通信服务(和对讲)
    	boolean svcAdd1028 = false;//99011028 数据通信服务(和对讲)
    	
        boolean svcDel1029 = false;//99011029 物联网专用数据通信服务(和对讲)
        boolean svcAdd1029 = false;//99011029 物联网专用数据通信服务(和对讲)
        
        boolean svcDel1024= false;//99011024 车联网专用数据通信服务
        boolean svcAdd1024 = false;//99011024 车联网专用数据通信服务
        
		boolean svcDel1025= false;//99011025 车联网数据通信服务
        boolean svcAdd1025 = false;//99011025 车联网数据通信服务
        
        boolean svcDel6926 = false; //99646926 用户策略服务产品
        boolean svcAdd6926 = false; //99646926 用户策略服务产品
        
        boolean svcDel6927 = false;//99646927 用户策略服务产品
        boolean svcAdd6927 = false;//99646927 用户策略服务产品
        
        //boolean svcDel1030 = false;//99011030	机卡绑定(网络校验)产品-机器卡
        boolean svcAdd1030 = false;//99011030	机卡绑定(网络校验)产品-机器卡
        
        //boolean svcDel1031 = false;//99011031	机卡绑定(网络校验)产品-和对讲
        boolean svcAdd1031 = false;//99011031	机卡绑定(网络校验)产品-和对讲
        
        //boolean svcDel1032 = false;//99011032	机卡绑定(网络校验)产品-车联网
        boolean svcAdd1032 = false;//99011032	机卡绑定(网络校验)产品-车联网
                
        boolean svcDel1041 = false; //99011041 数据通信服务 机器卡-2020 
        boolean svcAdd1041 = false;//99011041 数据通信服务 机器卡-2020
        
        boolean svcDel1042= false; //99011042 物联网专用数据通信服务 机器卡-2020 
        boolean svcAdd1042= false; //99011042 物联网专用数据通信服务 机器卡-2020 
        
    	if(IDataUtil.isNotEmpty(svcTrades))
    	{
    		for(int i=0; i < svcTrades.size(); i++)
        	{
        		IData svc = svcTrades.getData(i);
        		String serviceId = svc.getString("SERVICE_ID","");
        		String modifyTag = svc.getString("MODIFY_TAG","");
        		if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && "99011019".equals(serviceId)) {
        			svcAdd1019 = true;
        		} else if(BofConst.MODIFY_TAG_UPD.equals(modifyTag) && "99011019".equals(serviceId)) {
        			svcModify1019 = true;
        		} else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "99011019".equals(serviceId)) {
        			svcDel1019 = true;
        		} else if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && "99011020".equals(serviceId)) {
        			svcAdd1020 = true;
        		} else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "99011020".equals(serviceId)) {
        			svcDel1020 = true;
        		} else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "99011005".equals(serviceId)) {
        			svcDel1005 = true;
        		} else if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && "99011005".equals(serviceId)) {
        			svcAdd1005 = true;
        		} else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "99011012".equals(serviceId)) {
        			svcDel1012 = true;
        		} else if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && "99011012".equals(serviceId)) {
        			svcAdd1012 = true;
        		} else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "99011021".equals(serviceId)) {
        			svcDel1021 = true;
        		} else if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && "99011021".equals(serviceId)) {
        			svcAdd1021 = true;
        		} else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "99011022".equals(serviceId)) {
        			svcDel1022 = true;
        		} else if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && "99011022".equals(serviceId)) {
        			svcAdd1022 = true;
        		} else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "99011024".equals(serviceId)) {
        			svcDel1024 = true;
        		} else if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && "99011024".equals(serviceId)) {
        			svcAdd1024 = true;
        		} else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "99011025".equals(serviceId)) {
                	svcDel1025 = true;
        		} else if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && "99011025".equals(serviceId)) {
        			svcAdd1025 = true;
        		} else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "99011028".equals(serviceId)) {
        			svcDel1028 = true;
        		} else if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && "99011028".equals(serviceId)) {
        			svcAdd1028 = true;
        		} else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "99011029".equals(serviceId)) {
        			svcDel1029 = true;
        		} else if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && "99011029".equals(serviceId)) {
        			svcAdd1029 = true;
        		} else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "99646926".equals(serviceId)) {
        			svcDel6926 = true;
        		} else if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && "99646926".equals(serviceId)) {
        			svcAdd6926 = true;
        		} else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "99646927".equals(serviceId)) {
        			svcDel6927 = true;
        		} else if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && "99646927".equals(serviceId)) {
        			svcAdd6927 = true;
        		} else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "99011030".equals(serviceId)) {
        			//svcDel1030 = true;
        		} else if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && "99011030".equals(serviceId)) {
        			svcAdd1030 = true;
        		} else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "99011031".equals(serviceId)) {
        			//svcDel1031 = true;
        		} else if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && "99011031".equals(serviceId)) {
        			svcAdd1031 = true;
        		} else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "99011032".equals(serviceId)) {
        			//svcDel1032 = true;
        		} else if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && "99011032".equals(serviceId)) {
        			svcAdd1032 = true;
        		} else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "99011041".equals(serviceId)) {
        			svcDel1041 = true;
        		} else if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && "99011041".equals(serviceId)) {
        			svcAdd1041 = true;
        		} else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "99011042".equals(serviceId)) {
        			svcDel1042 = true;
        		} else if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && "99011042".equals(serviceId)) {
        			svcAdd1042 = true;
        		}        	        
        	}
    	}
    	
    	String userId = reqData.getGrpUca().getUserId();
    	String serialNumber = reqData.getUca().getSerialNumber();
    	IData subData = GrpWlwInstancePfQuery.queryGrpWlwSubsIdByUserId(userId);
        String groupCode = "";
        if(IDataUtil.isNotEmpty(subData))
        {
        	groupCode = subData.getString("SUBS_ID","");
        }
        
        //个人智能网语音通信服务新增
    	if(svcAdd1019){
    		IDataset tradeAttrs = bizData.getTradeAttr();
            if(IDataUtil.isNotEmpty(tradeAttrs))
            {
            	for(int i=0; i < tradeAttrs.size(); i++)
            	{
            		IData param = tradeAttrs.getData(i);
            		String modifyTag = param.getString("MODIFY_TAG","");
            		String attrCode = param.getString("ATTR_CODE","");
            		if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) 
            				&& !"".equals(attrCode))
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
    	
    	//个人智能网语音通信服务删除
    	if(svcDel1019){
    		IDataset tradeAttrs = bizData.getTradeAttr();
            if(IDataUtil.isNotEmpty(tradeAttrs))
            {
            	for(int i=0; i < tradeAttrs.size(); i++)
            	{
            		IData param = tradeAttrs.getData(i);
            		String modifyTag = param.getString("MODIFY_TAG","");
            		String attrCode = param.getString("ATTR_CODE","");
            		if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && !"".equals(attrCode))
            		{
            			IDataset infos = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "3997", 
            					"99011019", attrCode,"1","0898");
            			if(IDataUtil.isNotEmpty(infos))
            			{
            				param.put("REMARK", "NO_WLW_SVC_ATTR");
            			}
            			if("PhoneNumber".equals(attrCode))
            			{
                			param.put("ATTR_VALUE", serialNumber);
                		}
            		}
            	}
            }
    	}
    	
    	boolean hasSVC = false;
    	//个人智能网语音通信服务修改
    	if(svcModify1019){
    		IDataset tradeAttrs = bizData.getTradeAttr();
            if(IDataUtil.isNotEmpty(tradeAttrs))
            {
            	for(int i=0; i < tradeAttrs.size(); i++)
            	{
            		IData param = tradeAttrs.getData(i);
            		String modifyTag = param.getString("MODIFY_TAG","");
            		String attrCode = param.getString("ATTR_CODE","");
            		if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) 
            				&& !"".equals(attrCode))
            		{
            			IDataset infos = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "3997", 
            					"99011019", attrCode,"2","0898");
            			if(IDataUtil.isNotEmpty(infos))
            			{
            				param.put("REMARK", "WLW_SVC_ATTR");
            			}
            		} 
            		else if(BofConst.MODIFY_TAG_UPD.equals(modifyTag)
            				&& "GroupCode".equals(attrCode))
            		{
            			param.put("REMARK", "NO_WLW_SVC_ATTR");
            			param.put("ATTR_VALUE", groupCode);
            			
            		} else if(BofConst.MODIFY_TAG_UPD.equals(modifyTag)
            				&& "UserFlag".equals(attrCode))
            		{
            			param.put("REMARK", "NO_WLW_SVC_ATTR");
            		}
            		else if(BofConst.MODIFY_TAG_UPD.equals(modifyTag)
            				&& "PhoneNumber".equals(attrCode))
            		{
            			param.put("ATTR_VALUE", serialNumber);
            			hasSVC = true;
            		}
            	}
            }
            
            //PhoneNumber没有做修改，要对PhoneNumber的属性登记一下台账
            String userIdB = reqData.getUca().getUserId();
            if(!hasSVC){
            	IDataset userAttrs = UserAttrInfoQry.getUserAttrByUserInstType(userIdB, "PhoneNumber");
            	if (IDataUtil.isNotEmpty(userAttrs))
                {
                    IDataset dataSet = new DatasetList();
                    IData data = new DataMap();
                    data = userAttrs.getData(0);
                    data.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                    dataSet.add(data);
                    addTradeAttr(dataSet);
                }
            }
    	}
    	
    	//新增语音通信服务(可选)
    	if(svcAdd1020)
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
            					"99011020", attrCode,"0","0898");
            			if(IDataUtil.isNotEmpty(infos))
            			{
            				param.put("REMARK", "WLW_SVC_ATTR");
            			}
            		}
            	}
            }
    	}
    	
    	//删除
    	if(svcDel1020)
    	{
    		IDataset tradeAttrs = bizData.getTradeAttr();
            if(IDataUtil.isNotEmpty(tradeAttrs))
            {
            	for(int i=0; i < tradeAttrs.size(); i++)
            	{
            		IData param = tradeAttrs.getData(i);
            		String modifyTag = param.getString("MODIFY_TAG","");
            		String attrCode = param.getString("ATTR_CODE","");
            		if(BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            		{
            			if("CLIPStatus".equals(attrCode))
            			{
            				param.put("ATTR_VALUE", "02");
            			}
            			else if("IntRoamStatus".equals(attrCode))
            			{
            				param.put("ATTR_VALUE", "02");
            			}
            			else if("OCSIPROV".equals(attrCode))
            			{
            				param.put("ATTR_VALUE", "02");
            			}
            			else if("TCSIPROV".equals(attrCode))
            			{
            				param.put("ATTR_VALUE", "02");
            			} 
            			else if("CallsWaiting".equals(attrCode))
            			{
            				param.put("ATTR_VALUE", "02");
            			}
            		}
            	}
            }
    	}
    	    	
    	//删除 4G_GPRS(大流量)\4G_GPRS(中小流量\物联网专用数据通信服务(可选)\通用流量4G_GPRS服务
    	// 数据通信服务(和对讲)\ 物联网专用数据通信服务(和对讲)
         //99011024 车联网专用数据通信服务  99011025 车联网数据通信服务
    	//99646926 用户策略服务产品
    	if(svcDel1005 || svcDel1012 || svcDel1021 || svcDel1022 
    			|| svcDel1028 || svcDel1029 || svcDel1024 || svcDel1025 || svcDel6926
    			|| svcDel6927 || svcDel1041 || svcDel1042)
    	{
    		IDataset tradeAttrs = bizData.getTradeAttr();
            if(IDataUtil.isNotEmpty(tradeAttrs))
            {
            	for(int i=0; i < tradeAttrs.size(); i++)
            	{
            		IData param = tradeAttrs.getData(i);
            		String modifyTag = param.getString("MODIFY_TAG","");
            		String attrCode = param.getString("ATTR_CODE","");
            		if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && ("OperType".equals(attrCode) 
            				|| "OperType_Sb".equals(attrCode) || "OperType_Sb2".equals(attrCode)))
            		{
            			String attrValue = param.getString("ATTR_VALUE","");
            			if(StringUtils.isNotEmpty(attrValue))
            			{
            				String[] attrValues = attrValue.split("\\|");
            				String newAttrValue = "";
            				for(int j=0;j<attrValues.length;j++)
            				{
            					newAttrValue = newAttrValue + "02" + "|";
            				}
            				if(StringUtils.isNotEmpty(newAttrValue))
            				{
            					newAttrValue = newAttrValue.substring(0, newAttrValue.length() -1);
            					param.put("ATTR_VALUE", newAttrValue);
            				}
            			}
            			
            		}
            	}
            }
    	}
    	
    	//99011005 4G_GPRS(大流量)
    	//99011012 4G_GPRS(中小流量
    	//99011021 物联网专用数据通信服务(可选)
    	//99011022 通用流量4G_GPRS服务
    	//99011028 数据通信服务(和对讲)
    	//99011029 物联网专用数据通信服务(和对讲) 
    	//99646926 用户策略服务产品
        if(svcAdd1005 || svcAdd1012 || svcAdd1021 || svcAdd1022 || svcAdd1028 || svcAdd1029
        		|| svcAdd1024 || svcAdd1025 || svcAdd6926 || svcAdd6927 || svcAdd1030 || svcAdd1031
        		|| svcAdd1032 || svcAdd1041 || svcAdd1042){
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
    
    /**
     * @author chenzg
     * @date 2018-01-10
     */
    @Override
	protected void setTradeDiscnt(IData map) throws Exception {
		if(IDataUtil.isNotEmpty(map)){
			String productId = map.getString("PRODUCT_ID", "");
			String packageId = map.getString("PACKAGE_ID", "");
			String modTag = map.getString("MODIFY_TAG", "");
			String discntCode = map.getString("DISCNT_CODE","");
			String cancelMode = checkTradeCancelMode(map);
			//20171215 NB-IOT产品_成员产品，流量包、短信包优惠不可以取消
			if(!"7".equals(cancelMode)&&"1".equals(modTag) && "20171215".equals(productId) && ("41003605".equals(packageId)||"41003606".equals(packageId)) ){
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "NB-IOT产品下的流量包、短信包优惠不能取消!");
			}
			
			if(!"7".equals(cancelMode)&&"1".equals(modTag) && ("20005014".equals(productId) || "20161125".equals(productId)))
			{
				if("70000012".equals(packageId) || "70000013".equals(packageId))
				{
					IDataset commInfos = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "4005", 
        					"D",discntCode, "0898");
					if(IDataUtil.isNotEmpty(commInfos))
					{
						CSAppException.apperr(CrmCommException.CRM_COMM_103, "长期通用、定向流量产品包下的优惠不能取消!");
					}
				}
			}
		}
		super.setTradeDiscnt(map);
	}

    
    /**
     * 折扣率不允许变更
     * @throws Exception
     */
    private void regLimitDiscountAttr() throws Exception
    {
    	IDataset tradeAttrs = bizData.getTradeAttr();
		IDataset discntInfos = bizData.getTradeDiscnt();

    	//REQ201909160021_关于行业应用卡成员变更界面套餐变更优化的需求
    	String product_id=reqData.getGrpProductId();
         IDataset dataset = CommparaInfoQry.getCommparaByCodeCode1("CSM","9006","WLWchange","9006");
         logger.error("regLimitDiscountAttr=============product_id ==========:"+product_id);
         if(IDataUtil.isNotEmpty(dataset)){
             String paraCode20 = dataset.first().getString("PARA_CODE20","0");
             logger.error("regLimitDiscountAttr=============paraCode20 ==========:"+paraCode20);
             if(paraCode20.contains(product_id)){
            	 return;
             }
    	}
         




    	if(IDataUtil.isNotEmpty(discntInfos)){
    		for(int i=0; i < discntInfos.size(); i++)
         	{
    			IData discnt = discntInfos.getData(i);
        		String modifyTag = discnt.getString("MODIFY_TAG","");
        		if(BofConst.MODIFY_TAG_UPD.equals(modifyTag)){
        			if(IDataUtil.isNotEmpty(tradeAttrs)){
        				for(int j=0; j < tradeAttrs.size(); j++)
        	        	{
        					IData attr = tradeAttrs.getData(j);
        					String attrCode = attr.getString("ATTR_CODE","");
        	         		String modTag = attr.getString("MODIFY_TAG","");
        	         		if(BofConst.MODIFY_TAG_DEL.equals(modTag) && "20171211".equals(attrCode)){
        	         			if(attr.getString("RELA_INST_ID").equals(discnt.getString("INST_ID")))
                                {
        	         				//本省折扣
        	         				CSAppException.apperr(CrmCommException.CRM_COMM_103, "物联网优惠本省折扣属性不允许做修改变更!");
                                }
        	         		}
        					
        	        	}
        			}
        		}
         	}
    	}
    	
    	if(IDataUtil.isNotEmpty(tradeAttrs))
        {
         	for(int i=0; i < tradeAttrs.size(); i++)
         	{
         		IData attrTrade = tradeAttrs.getData(i);
         		String attrCode = attrTrade.getString("ATTR_CODE","");
         		String modifyTag = attrTrade.getString("MODIFY_TAG","");
         		String relaInstId = attrTrade.getString("RELA_INST_ID","");
         		if(BofConst.MODIFY_TAG_UPD.equals(modifyTag) && 
         				("Discount".equals(attrCode) || "20171211".equals(attrCode) || "20120706".equals(attrCode)))
    			{
         			CSAppException.apperr(CrmCommException.CRM_COMM_103, "物联网优惠折扣属性不允许做修改变更.");
         		}
         		
         		if("Discount".equals(attrCode) || "20171211".equals(attrCode) || "20120706".equals(attrCode)){
         			
         			String filterStr1 = "RELA_INST_ID=" + relaInstId + ",ATTR_CODE=" + attrCode + ",MODIFY_TAG=0";
         			String filterStr2 = "RELA_INST_ID=" + relaInstId + ",ATTR_CODE=" + attrCode + ",MODIFY_TAG=1";
         			IDataset filterData1 = DataHelper.filter(tradeAttrs,  filterStr1);
         			IDataset filterData2 = DataHelper.filter(tradeAttrs,  filterStr2);
         			
         			if(IDataUtil.isNotEmpty(filterData1) && IDataUtil.isNotEmpty(filterData2))
         			{
         				CSAppException.apperr(CrmCommException.CRM_COMM_103, "物联网优惠折扣属性不允许做修改变更!");
         			}														  
         		}
         	}
        }
    }
    
    /**
     * REQ201805150009关于取消流量用尽关停产品的优化
     * @throws Exception
     */
    private void dealDelDiscnt() throws Exception{
    	
    	IDataset svcTrades = bizData.getTradeSvc();
        IDataset discntInfos = bizData.getTradeDiscnt();
        IDataset attrs = bizData.getTradeAttr();
        
        boolean svcExists = false;
        if(IDataUtil.isNotEmpty(svcTrades)){
        	for(int i=0; i < svcTrades.size(); i++)
        	{
        		IData svc = svcTrades.getData(i);
        		String svcId = svc.getString("SERVICE_ID","");
        		String modifyTag = svc.getString("MODIFY_TAG","");
        		if(BofConst.MODIFY_TAG_DEL.equals(modifyTag)){
        			IDataset commInfos = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "4001", 
        					"WLW_SVCCLOSE",svcId, "0898");
        			if(IDataUtil.isNotEmpty(commInfos)){
        				svcExists = true;
        				break;
        			}
        		}
        	}
        }
        
        if(IDataUtil.isNotEmpty(discntInfos) && svcExists){
        	for(int i=0; i < discntInfos.size(); i++)
        	{
        		IData discnt = discntInfos.getData(i);
        		String discntCode = discnt.getString("DISCNT_CODE","");
        		String modifyTag = discnt.getString("MODIFY_TAG","");
        		if("20171113".equals(discntCode) && BofConst.MODIFY_TAG_DEL.equals(modifyTag)){
        			discnt.put("END_DATE", getAcceptTime());
        			if(IDataUtil.isNotEmpty(attrs)){
        				for(int j=0; j < attrs.size(); j++)
        	        	{
        					IData attr = attrs.getData(j);
        					if(attr.getString("RELA_INST_ID").equals(discnt.getString("INST_ID")))
                            {
                                attr.put("END_DATE",getAcceptTime());
                            }
        	        	}
        			}
        		}
        	}
        }
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
         		String modifyTag = tradeDiscnt.getString("MODIFY_TAG","");
         		if(BofConst.MODIFY_TAG_ADD.equals(modifyTag))
         		{
         			IData data = WlwGrpConstants.GRP_TEST_DISCNT_CONFIG.getData(discntCode);
             		if(IDataUtil.isNotEmpty(data))//有订购测试期套餐
             		{
             			this.insertIotBook(endDate);
             			break;
             		}
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
	
	//cancel_mode为7时生效的套餐不能取消
	public static String checkTradeCancelMode(IData idata) throws Exception{
				String modifyTag = idata.getString("MODIFY_TAG");
				String cancelMode = "";
				if(StringUtils.isNotBlank(modifyTag)&&"1".equals(modifyTag)){
					String offerCode = idata.getString("ELEMENT_ID","");
                	String offerType = idata.getString("ELEMENT_TYPE_CODE","");
                	IDataset offerList=UpcCallIntf.queryOfferIdByOfferCodeAndOfferType(offerType,offerCode);
                	if(offerList!=null && offerList.size()>0){ 
                		String offerId = offerList.getData(0).getString("OFFER_ID","");
                		if(StringUtils.isNotBlank(offerId)){
                			String groupId = idata.getString("PACKAGE_ID","");
                			IDataset pemSet = UpcCallIntf.queryGroupComEnableModeByGroupIdOfferId(groupId,offerId);
                			if(pemSet!=null&&pemSet.size()>0){
                				cancelMode = pemSet.getData(0).getString("CANCEL_MODE","");
                				if(StringUtils.isNotBlank(cancelMode)&&"7".equals(cancelMode)){
                            			String startDate = idata.getString("START_DATE");
                            			long now =System.currentTimeMillis();
                            			String path = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
                            			Pattern pt = Pattern.compile(path);
                            			Matcher flag = pt.matcher(startDate);
                            			SimpleDateFormat sdf = null;
                            			if(flag.matches()){
                            				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            			}else{
                            				sdf = new SimpleDateFormat("yyyy-MM-dd");
                            			}
                            			
                            			long start =sdf.parse(startDate).getTime();
                            			if(start<now){
                            				CSAppException.apperr(BizException.CRM_BIZ_5, "套餐已经生效，不能取消");
                            			}
                            	}
                			}
                		}
                	}
				}
				return cancelMode;
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
