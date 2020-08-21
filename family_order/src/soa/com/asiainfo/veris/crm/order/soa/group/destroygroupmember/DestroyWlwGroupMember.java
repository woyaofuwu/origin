
package com.asiainfo.veris.crm.order.soa.group.destroygroupmember;

import org.apache.commons.lang.StringUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyGroupMember;
import com.asiainfo.veris.crm.order.soa.group.internetofthings.GeneIotInstIdBean;

public class DestroyWlwGroupMember extends DestroyGroupMember
{
	
    public DestroyWlwGroupMember()
    {

    }
    
    protected void initReqData() throws Exception
    {
        super.initReqData();

    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        
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
        	String lastDateThisMonth = SysDateMgr.getLastDateThisMonth();//SysDateMgr.getFirstDayOfNextMonth4WEB();
        	map.put("END_DATE", lastDateThisMonth);
        }
    }
    
    @Override
	protected void setTradeRelation(IData map) throws Exception {
		super.setTradeRelation(map);
		
		String relaTypeCode = map.getString("RELATION_TYPE_CODE","");
		if("9A".equals(relaTypeCode))
		{
			String lastDateThisMonth = SysDateMgr.getLastDateThisMonth();
			map.put("END_DATE", lastDateThisMonth);
		}
		
	}

	@Override
	public void actTradeSub() throws Exception {
		super.actTradeSub();
		
		dealDelDiscnt();
		
		String tradeId = getTradeId();

        String tradeTypeCode = getTradeTypeCode();
        //处理服务的结束时间与被依赖的优惠的结束时间一致
        //this.specDealSvcEndDate();
        // 服务，资费， 产品 实例转换
        GeneIotInstIdBean.geneProdInstId(reqData, tradeId,tradeTypeCode,bizData);
        // 生成包实例
        GeneIotInstIdBean.genePkgInstId(reqData, tradeId, tradeTypeCode, bizData,false);
        
        infoRegTradeAttr();
	}
    
    private void infoRegTradeAttr() throws Exception
    {
    	IDataset svcTrades = bizData.getTradeSvc();
    	boolean svcDel1019 = false;//个人智能网语音通信服 99011019
    	boolean svcDel1020 = false;//语音通信服务 99011020
    	
    	boolean svcDel1005 = false;//99011005 4G_GPRS(大流量)
    	boolean svcDel1012 = false;//99011012 4G_GPRS(中小流量
    	boolean svcDel1021 = false;//99011021 物联网专用数据通信服务(可选)
    	boolean svcDel1022 = false;//99011022 通用流量4G_GPRS服务
    	
    	boolean svcDel1028 = false;//99011028 数据通信服务(和对讲)
        boolean svcDel1029 = false;//99011029 物联网专用数据通信服务(和对讲)
        
        boolean svcDel1024= false;//99011024 车联网专用数据通信服务
		boolean svcDel1025= false;//99011025 车联网数据通信服务
        
		boolean svcDel6926 = false; //99646926 用户策略服务产品
		boolean svcDel6927 = false;//99646927 用户策略服务产品
        
    	if(IDataUtil.isNotEmpty(svcTrades))
    	{
    		for(int i=0; i < svcTrades.size(); i++)
        	{
        		IData svc = svcTrades.getData(i);
        		String serviceId = svc.getString("SERVICE_ID","");
        		String modifyTag = svc.getString("MODIFY_TAG","");
        		if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "99011019".equals(serviceId)) {
        			svcDel1019 = true;
        		} else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "99011020".equals(serviceId)) {
        			svcDel1020 = true;
        		} else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "99011005".equals(serviceId)) {
        			svcDel1005 = true;
        		} else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "99011012".equals(serviceId)) {
        			svcDel1012 = true;
        		} else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "99011021".equals(serviceId)) {
        			svcDel1021 = true;
        		} else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "99011022".equals(serviceId)) {
        			svcDel1022 = true;
        		} else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "99011028".equals(serviceId)) {
        			svcDel1028 = true;
        		} else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "99011029".equals(serviceId)) {
        			svcDel1029 = true;
        		} else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "99011024".equals(serviceId)) {
        			svcDel1024 = true;
        		} else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "99011025".equals(serviceId)) {
        			svcDel1025 = true;
        		} else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "99646926".equals(serviceId)) {
        			svcDel6926 = true;
        		} else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag) && "99646927".equals(serviceId)) {
        			svcDel6927 = true;
        		}
        	}
    	}
    	
    	//个人智能网语音通信服
    	if(svcDel1019)
    	{
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
            		}
            	}
            }
    	}
    	
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
    	//99646926 用户策略服务产品
    	if(svcDel1005 || svcDel1012 || svcDel1021 || svcDel1022 || svcDel1028 || svcDel1029
    			|| svcDel1024 ||svcDel1025 || svcDel6926 || svcDel6927)
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
    	
    }
    /**
     * 特殊处理服务的结束时间，要求服务的结束时间要与被依赖的优惠的结束时间一致
     * REQ201804180018物联卡开户界面、流量共享规则的优化
     * 物联网产品（机器卡、NBIOT、和对讲、车联网）在注销集团产品时，对应的服务同依赖的优惠结束时间一起终止
     * （例如机器卡：通用流量4G_GPRS服务依赖于全网通用流量套餐，注销时全国通用流量套餐结束至月底对应的通用流量4G_GPRS服务也结束到月底）
     * @param reqData
     * @param bizData
     * @author chenzg
     * @date 2018-5-2
     */
    public void specDealSvcEndDate() throws Exception{
    	IDataset svcTrades = this.bizData.getTradeSvc();
    	IDataset discntTrades = this.bizData.getTradeDiscnt();
    	IDataset attrTrades = this.bizData.getTradeAttr();
    	if(IDataUtil.isNotEmpty(svcTrades)){
    		for(int i=0;i<svcTrades.size();i++){
    			IData eachSvc = svcTrades.getData(i);
    			String modifyTag = eachSvc.getString("MODIFY_TAG", "");
    			String serviceId = eachSvc.getString("SERVICE_ID", "");
    			String instId = eachSvc.getString("INST_ID", "");
    			if(!"1".equals(modifyTag)){
    				continue;
    			}
    			//取服务被依赖的元素
    			IDataset offerRels = new DatasetList();
    			IDataset offerRels1 = UpcCall.queryOfferRelByRelOfferIdAndRelType(serviceId, "S", "1");
    			IDataset offerRels2 = UpcCall.queryOfferRelByRelOfferIdAndRelType(serviceId, "S", "2");
    			offerRels.addAll(offerRels1);
    			offerRels.addAll(offerRels2);
    			if(IDataUtil.isEmpty(offerRels)){
    				continue;
    			}
    			//取被依赖的优惠
    			IDataset relDiscts = DataHelper.filter(offerRels, "OFFER_TYPE=D");
    			if(IDataUtil.isNotEmpty(relDiscts)){
    				for(int j=0;j<relDiscts.size();j++){
    					IData eachRel = relDiscts.getData(j);
    					String discntCode = eachRel.getString("OFFER_CODE");
    					//找优惠台帐是否有该优惠的结束台帐
    					IDataset filterTradeDscnts = DataHelper.filter(discntTrades, "MODIFY_TAG=1,DISCNT_CODE="+discntCode);
    					if(IDataUtil.isEmpty(filterTradeDscnts)){
    						continue;
    					}
    					//修改服务的结束时间
    					eachSvc.put("END_DATE", filterTradeDscnts.getData(0).getString("END_DATE"));
    					eachSvc.put("REMARK", "以["+discntCode+"]优惠结束时间按为准");
    					//修改服务属性的结束时间
    					IDataset filterAttrs = DataHelper.filter(attrTrades, "MODIFY_TAG=1,RELA_INST_ID="+instId);
    					if(IDataUtil.isNotEmpty(filterAttrs)){
    						for(int k=0;k<filterAttrs.size();k++){
    							filterAttrs.getData(k).put("END_DATE", eachSvc.getString("END_DATE"));
    						}
    					}
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
    
}
