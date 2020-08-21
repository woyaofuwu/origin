package com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCallIntf;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.script.rule.iot.IotCheck;


public class WlwBusiHelperGrp
{

	private static final Logger logger = Logger.getLogger(WlwBusiHelperGrp.class);	
	
	/**
	 * 
	 * @param uca
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static IData getWlwDiscnt(UcaData uca,String ... name) throws Exception
    {
    	IDataset discntList = BofQuery.queryUserAllValidDiscnt(uca.getUserId(), uca.getUserEparchyCode());

        return getWlwDiscnt(discntList,name);
    }
	
	/**
	 * 
	 * @param discntList
	 * @param names
	 * @return
	 * @throws Exception
	 */
	public static IData getWlwDiscnt(IDataset discntList,String ... names) throws Exception
    {
        IData ouput = new DataMap();
        IDataset testDiscntList = new DatasetList();
        IDataset smsDiscntList = new DatasetList();
        IDataset flowDiscntList = new DatasetList();
        IDataset normalDiscntList = new DatasetList();
        IDataset experienceDiscntList = new DatasetList();
        
        
        Map<String, IData>  configs = IotCheck.loadConfig("9013");
        
        for (int i = 0; i < names.length; i++)
        {
            String name = names[i];
            
            if (IDataUtil.isNotEmpty(discntList))
            {
                for (Iterator iterator = discntList.iterator(); iterator.hasNext();)
                {
                    IData discnt = (IData) iterator.next();
                  
                    IData configItem = (IData) configs.get(discnt.getString("DISCNT_CODE"));
                    if(IDataUtil.isNotEmpty(configItem))
                    {
                    	if(name.equals("ALL"))
                        {
                        	if (configItem.getString("PARA_CODE20","").endsWith("_TEST"))
                            {
                                testDiscntList.add(discnt);
                            }
                            if (configItem.getString("PARA_CODE20","").endsWith("_GPRS"))
                            {
                                flowDiscntList.add(discnt);
                                normalDiscntList.add(discnt);
                            }
                            if (configItem.getString("PARA_CODE20","").endsWith("_SMS"))
                            {
                                smsDiscntList.add(discnt);
                                normalDiscntList.add(discnt);
                            }
                            if(configItem.getString("PARA_CODE20","").endsWith("_EXPERIENCE"))
                            {
                            	experienceDiscntList.add(discnt);//体验产品
                            }
                        }
                        else
                        {
                        	if ((name + "_TEST").equals(configItem.getString("PARA_CODE20")))
                            {
                                testDiscntList.add(discnt);
                            }
                            if ((name + "_GPRS").equals(configItem.getString("PARA_CODE20")))
                            {
                                flowDiscntList.add(discnt);
                                normalDiscntList.add(discnt);
                            }
                            if ((name + "_SMS").equals(configItem.getString("PARA_CODE20")))
                            {
                                smsDiscntList.add(discnt);
                                normalDiscntList.add(discnt);
                            }
                            if(configItem.getString("PARA_CODE20","").endsWith("_EXPERIENCE"))
                            {
                            	experienceDiscntList.add(discnt);//体验产品
                            }
                        }
                    }
                }
                
            }
        }

        ouput.put("TEST_DISCNT", testDiscntList);
        ouput.put("NORMAL_DISCNT", normalDiscntList);
        ouput.put("SMS_DISCNT", smsDiscntList);
        ouput.put("FLOW_DISCNT", flowDiscntList);
        ouput.put("EXPERIENCE_DISCNT", experienceDiscntList);
        return ouput;
    }
	 
	/**
     * 处理物联网测试期与正式期的生失效时间
     * 
     * @param bizData
     * @param reqData
     * @throws Exception
     */
    public static void dealTestDiscnt(UcaData uca, IDataset tradeDiscnt,IDataset attrs) throws Exception
	{
    	IData userAllDiscnts = getWlwDiscnt(uca,"ALL");
    	IData wlwDiscnt = getWlwDiscnt(tradeDiscnt,"ALL");
    	
    	//NB-IOT体验产品
    	IDataset expTradeDiscnts = new DatasetList();
    	IDataset expUserDiscnts = userAllDiscnts.getDataset("EXPERIENCE_DISCNT");
    	if(IDataUtil.isNotEmpty(expUserDiscnts))
    	{
    		expTradeDiscnts.addAll(expUserDiscnts);
    	}
    	expTradeDiscnts.addAll(wlwDiscnt.getDataset("EXPERIENCE_DISCNT"));
    	
    	//测试期套餐
    	IDataset testTradeDiscnts = new DatasetList();
    	IDataset testUserDiscnts = userAllDiscnts.getDataset("TEST_DISCNT");
    	if(IDataUtil.isNotEmpty(testUserDiscnts))
    	{
    		testTradeDiscnts.addAll(testUserDiscnts);
    	}
    	testTradeDiscnts.addAll(wlwDiscnt.getDataset("TEST_DISCNT"));
    	
    	//BUG20190305093135限制物联网测试期产品套餐不能重复订购;
    	/*IDataset testDiscnts = new DatasetList();
    	IDataset discntover = getWlwDiscntover(uca,"ALL").getDataset("TEST_DISCNT");			//已过期的测试期产品
    	testDiscnts.addAll(testTradeDiscnts);
    	testDiscnts.addAll(discntover);
    	if(IDataUtil.isNotEmpty(testDiscnts) && testDiscnts.size() > 1)
		{
			 CSAppException.apperr(CrmCommException.CRM_COMM_103, "测试期产品不能重复订购!");
		}*/
    	
		
		//正常的套餐
		IDataset nomarlDiscnt = wlwDiscnt.getDataset("NORMAL_DISCNT");
		
		//体验产品
		if(IDataUtil.isNotEmpty(expTradeDiscnts))
		{
			if(checkTradeModifyTag(tradeDiscnt)){
				if(expTradeDiscnts.size()>1)
	    		{
	    		    CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户订购体验产品数量超过1个!");
	    		}
				
				if(IDataUtil.isNotEmpty(testTradeDiscnts) && testTradeDiscnts.size() > 1)
				{
					 CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户订购测试期优惠数量超过1个!");
				}
			}
			
			//订购了体验期产品,并且有订购测试期套餐,
			//则通过测试期套餐的结束时间来偏移体验期产品的时间
			if(IDataUtil.isNotEmpty(testTradeDiscnts))
			{
				//测试期的结束时间,即体验产品的开始时间
				String testEndDate = testTradeDiscnts.first().getString("END_DATE","");
				
				//根据测试期套餐的结束时间来偏移体验期产品的时间
				//if(StringUtils.isNotBlank(testEndDate) && IDataUtil.isNotEmpty(expTradeDiscnts))
				if(IDataUtil.isNotEmpty(expTradeDiscnts))
				{
					for (int i = 0; i < expTradeDiscnts.size(); i++)
		            {
		        	    processValidDate(expTradeDiscnts.getData(i),attrs,testEndDate);
		            }
				}
				
				//根据体验期产品的结束时间来偏移正常套餐的时间,如流量年包
				if(IDataUtil.isNotEmpty(expTradeDiscnts) && IDataUtil.isNotEmpty(nomarlDiscnt))
				{
					String expEndDate = expTradeDiscnts.first().getString("END_DATE");
					if("".equals(expEndDate))
					{
						return;
					}
					
					for (int i = 0; i < nomarlDiscnt.size(); i++)
		            {
		        	    processValidDate(nomarlDiscnt.getData(i),attrs,expEndDate);
		            }
				}
			}
			else 
			{
				//没有订购测试期套餐,只订购了体验期产品和流量年包
				//则通过体验期产品的结束时间来偏移流量年包的时间
				if(IDataUtil.isNotEmpty(expTradeDiscnts) && IDataUtil.isNotEmpty(nomarlDiscnt))
				{
					String expEndDate = expTradeDiscnts.first().getString("END_DATE");
					if("".equals(expEndDate))
					{
						return;
					}
					for (int i = 0; i < nomarlDiscnt.size(); i++)
		            {
		        	    processValidDate(nomarlDiscnt.getData(i),attrs,expEndDate);
		            }
				}
			}
			
		}
		else if(IDataUtil.isNotEmpty(testTradeDiscnts))//未订购体验产品时,从测试期套餐开始偏移计算
		{
			if(checkTradeModifyTag(tradeDiscnt)){
				if(testTradeDiscnts.size()>1)
	    		{
	    		    CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户订购测试期优惠数量超过1个!!");
	    		}
			}
			
			String testEndDate = testTradeDiscnts.first().getString("END_DATE");
        	
        	if("".equals(testEndDate))
        	{
        		return;//如果没有测试期资费，不用处理
        	}
        	
        	for (int i = 0; i < nomarlDiscnt.size(); i++)
            {
        	    processValidDate(nomarlDiscnt.getData(i),attrs,testEndDate);
            }
		}
		
	}
    
    private static void processValidDate(IData discnt,IDataset attrs, String testEndDate) throws Exception 
	{
        if (IDataUtil.isNotEmpty(discnt))
        {
        	IData elementdata = ProductInfoQry.getProductElementByPkForCG(discnt.getString("PACKAGE_ID"), "D",discnt.getString("DISCNT_CODE"));
            String endoffset = "";
            String end_enable_tag = "";
            String end_unit = "";
            if(IDataUtil.isNotEmpty(elementdata))
            {
                endoffset = elementdata.getString("END_OFFSET");
                end_enable_tag = elementdata.getString("END_ENABLE_TAG");
                end_unit = elementdata.getString("END_UNIT");
            }
            if(!"".equals(endoffset) && !"".equals(end_unit) && ("1".equals(end_enable_tag) || "0".equals(end_enable_tag)))
            {
                String startdate = SysDateMgr.addSecond(testEndDate, 1);
                
                if(logger.isDebugEnabled())
                {
                	logger.debug("偏移前资费开始时间START_DATE="+discnt.getString("START_DATE"));
                }
                discnt.put("START_DATE", startdate);//开始时间为测试期的结束时间+2s
                if(logger.isDebugEnabled())
                {
                	logger.debug("偏移后资费开始时间START_DATE="+discnt.getString("START_DATE"));
                }
                
                String enddate = SysDateMgr.endDateOffset(startdate, endoffset, end_unit);
                
                if(logger.isDebugEnabled())
                {
                	logger.debug("偏移前资费结束时间"+discnt.getString("END_DATE"));
                }
                discnt.put("END_DATE", enddate);
                if(logger.isDebugEnabled())
                {
                	logger.debug("偏移后资费结束时间"+discnt.getString("END_DATE"));
                }
                
                //资费参数的开始结束时间
                if(attrs != null && attrs.size() > 0)
                {
                    for(int j=0;j<attrs.size();j++)
                    { 
                        IData attr = attrs.getData(j);
                        if(attr.getString("RELA_INST_ID").equals(discnt.getString("INST_ID")))
                        {
                            attr.put("START_DATE", startdate);
                            attr.put("END_DATE",enddate);
                        }
                    }
                }
            }
        }
	}
    
    /**
     * 物联网流量年包与短信年包失效时间必须一致
     * 
     * @param bizData
     * @param reqData
     * @throws Exception
     */
    public static void dealNbIotSmsDiscnt(UcaData uca, IDataset tradeDiscnt) throws Exception
    {
    	// 获取流量年包，包含订购的和已订购的
    	IDataset flowDiscnts = getWlwDiscnt(uca,"NB").getDataset("FLOW_DISCNT");
    	flowDiscnts.addAll(getWlwDiscnt(tradeDiscnt,"NB").getDataset("FLOW_DISCNT"));
    	// 获取短信年包，只需要考虑订购的
    	IDataset smsDiscnts = getWlwDiscnt(tradeDiscnt,"NB").getDataset("SMS_DISCNT");
    	if (IDataUtil.isEmpty(flowDiscnts) || IDataUtil.isEmpty(smsDiscnts))
        {
             return;
        }
    	// 短信年包套餐的失效依赖于流量年包，且时间须与流量年包套餐的失效时间一致
        int compareTag = SysDateMgr.compareTo(smsDiscnts.first().getString("END_DATE"), flowDiscnts.first().getString("END_DATE"));
        if (compareTag < 0 && StringUtils.equals(smsDiscnts.first().getString("MODIFY_TAG"), "0"))
        {
            //订购时短信年包失效时间小于流量年包，直接报错
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "NB-IOT短信年包套餐的失效依赖于NB-IOT流量年包，且时间须与流量年包套餐的失效时间一致");
        }
        else
        {
            //其他情况，修改短信年包套餐与流量年包套餐的生失效时间一致
        	if(logger.isDebugEnabled())
        	{
        		logger.debug("偏移前短信年包开始时间START_DATE="+smsDiscnts.first().getString("START_DATE"));
        	}
        	smsDiscnts.first().put("START_DATE", flowDiscnts.first().getString("START_DATE"));
        	if(logger.isDebugEnabled())
        	{
        		logger.debug("偏移后短信年包开始时间START_DATE="+smsDiscnts.first().getString("START_DATE"));
        	}
        	
        	if(logger.isDebugEnabled())
        	{
        		logger.debug("偏移前短信年包结束时间END_DATE=" + smsDiscnts.first().getString("END_DATE"));
        	}
        	smsDiscnts.first().put("END_DATE", flowDiscnts.first().getString("END_DATE"));
        	
        	if(logger.isDebugEnabled())
        	{
        		logger.debug("偏移后短信年包结束时间END_DATE=" + smsDiscnts.first().getString("END_DATE"));
        	}
        }
    }
    
    /**
	 * NbIot服务、属性处理-集团侧使用
	 * @param uca
	 * @param tradeDiscnt
	 * @param tradeSvcs
	 * @param attrs
	 * @return
	 * @throws Exception
	 */
	public static IData dealNbIotGrpMebTradeSvc(UcaData uca, IDataset tradeDiscnt,
			IDataset tradeSvcs,IDataset attrs) throws Exception
    {
		//只考虑成员新增时订购的
		IDataset experienceDiscnts = getWlwDiscnt(tradeDiscnt,"NB").getDataset("EXPERIENCE_DISCNT");
		IDataset testDiscnts = getWlwDiscnt(tradeDiscnt,"NB").getDataset("TEST_DISCNT");
		IDataset flowDiscnts = getWlwDiscnt(tradeDiscnt,"NB").getDataset("FLOW_DISCNT");
		
		String exStartDate = "";//体验产品开始时间
		String exEndDate = "";//体验产品结束时间
		boolean isExFlag = false;//是否有体验产品
		if(IDataUtil.isNotEmpty(experienceDiscnts))
		{
			if(checkTradeModifyTag(tradeDiscnt)){
				if(experienceDiscnts.size()>1)
	    		{
	    		    CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户订购体验产品数量超过1个!");
	    		}
			}
			
			exStartDate = experienceDiscnts.first().getString("START_DATE");
			exEndDate = experienceDiscnts.first().getString("END_DATE");
			isExFlag = true;
		}
		
    	String testStartDate = "";//测试期套餐开始时间
    	String testEndDate = "";//测试期套的结束时间
    	boolean isTestFlag = false;//是否有测试套餐
    	if(IDataUtil.isNotEmpty(testDiscnts))
    	{
    		if(checkTradeModifyTag(tradeDiscnt)){
    			if(testDiscnts.size()>1)
        		{
        		    CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户订购测试期优惠数量超过1个！");
        		}
    		}
    		testStartDate = testDiscnts.first().getString("START_DATE");
    		testEndDate = testDiscnts.first().getString("END_DATE");
    		isTestFlag = true;
    	}
		
    	String startDate = "";
    	String endDate = "";
    	boolean isFlowFlag = false;//是否有流量套餐
    	//查找优惠流量套餐的开始时间和结束时间
    	if(IDataUtil.isNotEmpty(flowDiscnts))
    	{
    		startDate = flowDiscnts.first().getString("START_DATE");
    		endDate = flowDiscnts.first().getString("END_DATE");
    		isFlowFlag = true;
    	}
    	
    	//处理服务的时间
    	for(int i = 0,len = tradeSvcs.size(); i < len; i++)
    	{
    		IData tradeSvc = tradeSvcs.getData(i);
    		String serviceId = tradeSvc.getString("SERVICE_ID","");
    		if( (!StringUtils.equals("9014", serviceId)) && StringUtils.isNotBlank(serviceId) )
    		{
    			if(StringUtils.isNotBlank(startDate) 
    					&& StringUtils.isNotBlank(endDate)
    					&& isFlowFlag)//根据流量套餐的开始结束时间来处理服务对应的开始结束时间
    			{
    				tradeSvc.put("START_DATE", startDate);
    				tradeSvc.put("END_DATE", endDate);
    				//处理服务属性的时间
    				processNbIotAttrDate(tradeSvc,attrs,startDate,endDate);
    				
    			} else if(StringUtils.isNotBlank(testStartDate) 
    					&& StringUtils.isNotBlank(testEndDate)
    					&& isTestFlag && !isFlowFlag){ //有测试套餐,服务,没有正式套餐时,用测试套餐的时间处理服务的时间
    				tradeSvc.put("START_DATE", testStartDate);
    				tradeSvc.put("END_DATE", testEndDate);
    				//处理服务属性的时间
    				processNbIotAttrDate(tradeSvc,attrs,testStartDate,testEndDate);
    			}
    		}
    	}
		
    	IDataset resultSetSvc = new DatasetList();
    	IDataset resultSetAttr = new DatasetList();
    	//有体验产品,正式套餐时,则根据体验产品的开始时间、失效时间新增出一套对应的服务
    	if(isExFlag && isFlowFlag)
    	{
    		for(int i = 0,len = tradeSvcs.size(); i < len; i++)
    		{
    			IData tradeSvc = tradeSvcs.getData(i);
        		String serviceId = tradeSvc.getString("SERVICE_ID","");
        		if("1218301".equals(serviceId) || "1218302".equals(serviceId))
        		{
        			IData data = new DataMap();
        			data = (IData)Clone.deepClone(tradeSvc);
        			data.put("START_DATE", exStartDate);
        			data.put("END_DATE", exEndDate);
        			data.put("ELEMENT_ID", serviceId);
        			String newInstId = SeqMgr.getInstId();
        			data.put("INST_ID", newInstId);
        			resultSetSvc.add(data);
        			processNbIotTestDiscntAttr(tradeSvc,attrs,exStartDate,exEndDate,newInstId,resultSetAttr);
        		}
    		}
    	}
    	
    	if(isTestFlag && isFlowFlag)//有测试套餐,正式套餐时,才拆分出两个对应服务
    	{
    		//如果有测试期套的,则新增出一套生效、失效时间一样的服务和属性
    		for(int i = 0,len = tradeSvcs.size(); i < len; i++)
    		{
    			IData tradeSvc = tradeSvcs.getData(i);
        		String serviceId = tradeSvc.getString("SERVICE_ID","");
        		if("1218301".equals(serviceId) || "1218302".equals(serviceId))
        		{
        			IData data = new DataMap();
        			data = (IData)Clone.deepClone(tradeSvc);
        			data.put("START_DATE", testStartDate);
        			data.put("END_DATE", testEndDate);
        			data.put("ELEMENT_ID", serviceId);
        			String newInstId = SeqMgr.getInstId();
        			data.put("INST_ID", newInstId);
        			resultSetSvc.add(data);
        			processNbIotTestDiscntAttr(tradeSvc,attrs,testStartDate,testEndDate,newInstId,resultSetAttr);
        		}
    		}
    	}
    	
    	IData resultData = new DataMap();
    	resultData.put("HAS_TEST_DISCNT", false);
    	if(IDataUtil.isNotEmpty(resultSetSvc))
    	{
    		resultData.put("WLW_TRADE_SVC", resultSetSvc);
    		if((isTestFlag && isFlowFlag) || (isExFlag && isFlowFlag))
    		{
    			resultData.put("HAS_TEST_DISCNT", true);
    		}
    	}
    	if(IDataUtil.isNotEmpty(resultSetAttr))
    	{
    		resultData.put("WLW_TRADE_SVCATTR", resultSetAttr);
    	}
    	return resultData;
    }
	
	/**
	 * 处理NBiot的服务属性开始时间、结束时间
	 * @param svc
	 * @param attrs
	 * @param startDate
	 * @param endDate
	 * @throws Exception
	 */
	private static void processNbIotAttrDate(IData svc,IDataset attrs, String startDate,String endDate) throws Exception 
	{
        if (IDataUtil.isNotEmpty(svc) && IDataUtil.isNotEmpty(attrs))
        {
            for(int j=0,len = attrs.size();j<len;j++)
            { 
                IData attr = attrs.getData(j);
                if(attr.getString("RELA_INST_ID").equals(svc.getString("INST_ID")))
                {
                    attr.put("START_DATE", startDate);
                    attr.put("END_DATE",endDate);
                }
            }
        }
	}
	
	/**
	 * 以测试期套餐的开始时间、结束时间处理NBiot的新增服务属性的开始时间、结束时间
	 * @param svc
	 * @param attrs
	 * @param testStartDate
	 * @param testEndDate
	 * @param newInstId
	 * @param newAttrs
	 * @throws Exception
	 */
	private static void processNbIotTestDiscntAttr(IData svc,IDataset attrs, 
			String testStartDate,String testEndDate,String newInstId,IDataset newAttrs) throws Exception 
	{
        if (IDataUtil.isNotEmpty(svc) && IDataUtil.isNotEmpty(attrs))
        {
            for(int j=0,len = attrs.size();j<len;j++)
            { 
                IData attr = attrs.getData(j);
                if(attr.getString("RELA_INST_ID").equals(svc.getString("INST_ID")))
                {
                	IData data = new DataMap();
                	data = (IData)Clone.deepClone(attr);
                	data.put("START_DATE", testStartDate);
        			data.put("END_DATE", testEndDate);
        			data.put("INST_ID", SeqMgr.getInstId());
        			data.put("RELA_INST_ID", newInstId);
        			newAttrs.add(data);
                }
            }
        }
	}
	
	/**
	 * BUG20190305093135限制物联网测试期产品套餐不能重复订购问题
	 * @param uca
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static IData getWlwDiscntover(UcaData uca,String ... name) throws Exception
    {
    	IDataset discntList = BofQuery.queryUserAllValidDiscntover(uca.getUserId(), uca.getUserEparchyCode());

        return getWlwDiscnt(discntList,name);
    }
	
	//判断套餐里面是否有新增
	public static boolean checkTradeModifyTag(IDataset tradeDiscnt){
		if(tradeDiscnt != null&&tradeDiscnt.size()>0){
			for(int i=0;i<tradeDiscnt.size();i++){
				String modifyTag = tradeDiscnt.getData(i).getString("MODIFY_TAG");
				if(StringUtils.isNotBlank(modifyTag)&&"0".equals(modifyTag)){
					return true;
				}
			}
			
		}
		return false;
	}
	
	//cancel_mode为7时生效的套餐不能取消
	public static void checkTradeCancelMode(IDataset tradeDiscnt) throws Exception{
		if(tradeDiscnt != null&&tradeDiscnt.size()>0){
			for(int i=0;i<tradeDiscnt.size();i++){
				IData idata = tradeDiscnt.getData(i);
				String modifyTag = idata.getString("MODIFY_TAG");
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
                				String cancelMode = pemSet.getData(0).getString("CANCEL_MODE","");
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
			}
			
		}
	}
}