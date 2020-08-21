package com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackWhiteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBaseReqData;
import com.asiainfo.veris.crm.order.soa.script.rule.iot.IotCheck;

public class WlwBusiHelper
{

	private static final Logger logger = Logger.getLogger(WlwBusiHelper.class);	
	
	//private static final Map<String, IData> configs = IotCheck.DISCNT_CONFIG_MAP;
    
    public static IData getWlwDiscnt(UcaData uca,String ... name) throws Exception
    {
    	IDataset discntList = BofQuery.queryUserAllValidDiscnt(uca.getUserId(), uca.getUserEparchyCode());

        return getWlwDiscnt(discntList,name);
    }
    
    public static IData getWlwDiscnt(IDataset discntList,String ... names) throws Exception
    {
        IData ouput = new DataMap();
        IDataset testDiscntList = new DatasetList();
        IDataset smsDiscntList = new DatasetList();
        IDataset flowDiscntList = new DatasetList();
        IDataset normalDiscntList = new DatasetList();
        
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
                        }
                    }
                }
                
            }
        }

        ouput.put("TEST_DISCNT", testDiscntList);
        ouput.put("NORMAL_DISCNT", normalDiscntList);
        ouput.put("SMS_DISCNT", smsDiscntList);
        ouput.put("FLOW_DISCNT", flowDiscntList);
        return ouput;
    }
    
    /**
     * 处理物联网测试期与正式期的生失效时间（新的）
     * 
     * @param bizData
     * @param reqData
     * @throws Exception
     */
    public static void dealTestDiscntTrade(UcaData uca, List<DiscntTradeData> tradeDiscnt,List<AttrTradeData> attrs) throws Exception
	{
    	 
    	IDataset tradeDiscntSet =  TradeInfoBuildHelper.listToDataset(tradeDiscnt);  
    	IDataset attrDiscntSet =  TradeInfoBuildHelper.listToDataset(attrs);
    	
    	
    	dealTestDiscnt(uca, tradeDiscntSet, attrDiscntSet);
    	
    	resetTradeData(tradeDiscnt, tradeDiscntSet,new BuildResetTradeData(){

			@Override
			public BaseTradeData getTradeDataObj(IData tradeItem) {
			    return new DiscntTradeData(tradeItem);
			}}); 
    	
    	resetTradeData(attrs, attrDiscntSet,new BuildResetTradeData(){

			@Override
			public BaseTradeData getTradeDataObj(IData tradeItem) {
			    return new AttrTradeData(tradeItem);
			}}); 
	}

	private static void resetTradeData(List tradeDiscnt, IDataset tradeDiscntSet,BuildResetTradeData reqData) 
	{
		
		for (Iterator iterator = tradeDiscnt.iterator(); iterator.hasNext();)
    	{
    	    iterator.next(); 
			iterator.remove(); 
		}
    	
    	for (Iterator iterator = tradeDiscntSet.iterator(); iterator.hasNext();) 
    	{
			IData item = (IData) iterator.next();
			
			tradeDiscnt.add(reqData.getTradeDataObj(item));
			
		}
	}
	
	 interface BuildResetTradeData 
	 {
		 BaseTradeData getTradeDataObj(IData tradeItem);
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
		IDataset discnts = getWlwDiscnt(uca,"ALL").getDataset("TEST_DISCNT");
		IData wlwDiscnt = getWlwDiscnt(tradeDiscnt,"ALL");
		discnts.addAll(wlwDiscnt.getDataset("TEST_DISCNT"));
		if(IDataUtil.isNotEmpty(discnts))
		{
    		
    		if(discnts.size()>1)
    		{
    		    CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户订购测试期优惠数量超过1个！");
    		}
    		
        	String testEndDate = discnts.first().getString("END_DATE");
        	
        	if("".equals(testEndDate))
        	{
        		return;//如果没有测试期资费，不用处理
        	}
        	
        	IDataset nomarlDiscnt = wlwDiscnt.getDataset("NORMAL_DISCNT");
        	
        	for (int i = 0; i < nomarlDiscnt.size(); i++)
            {
        	    processValidDate(nomarlDiscnt.getData(i),attrs,testEndDate);
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
     * 物联网流量年包与短信年包失效时间必须一致
     * 
     * @param bizData
     * @param reqData
     * @throws Exception
     */
    public static void dealNbIotSmsDiscntTrade(UcaData uca, List<DiscntTradeData> tradeDiscnt) throws Exception
    {
    	IDataset result =  TradeInfoBuildHelper.listToDataset(tradeDiscnt);
    	
    	dealNbIotSmsDiscnt(uca, result);
    	
    	resetTradeData(tradeDiscnt, result,new BuildResetTradeData(){

			@Override
			public BaseTradeData getTradeDataObj(IData tradeItem) {
			    return new DiscntTradeData(tradeItem);
			}}); 
        
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
                String startdate = SysDateMgr.addSecond(testEndDate, 2);
                
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
	

	public static IDataset buildBlackWhiteInfo(IDataset blackWhiteInfos,GroupBaseReqData reqData) throws Exception
	{ 
	    
	    for (Iterator iterator = blackWhiteInfos.iterator(); iterator.hasNext();)
        {
            IData data = (IData) iterator.next();
            
            String modifyTag = data.getString("MODIFY_TAG");
      
            if (TRADE_MODIFY_TAG.Add.getValue().equals(modifyTag))
            {
                data.put("INST_ID", SeqMgr.getInstId());
                data.put("BIZ_IN_CODE","0" );
                data.put("BIZ_CODE", "0" );
                data.put("BIZ_NAME", "物联网企业黑白名单" );
                data.put("REMARK", "物联网企业黑白名单设置" );
                data.put("EXPECT_TIME", SysDateMgr.getSysTime() );
                data.put("START_DATE", SysDateMgr.getSysTime() );
                data.put("END_DATE", SysDateMgr.getTheLastTime() ); 
                data.put("GROUP_ID",reqData.getUca().getCustGroup().getGroupId());
                data.put("USER_ID",reqData.getUca().getUserId());
                data.put("OPER_STATE", "01");// 加入 
            }
            else if (TRADE_MODIFY_TAG.DEL.getValue().equals(modifyTag))
            {
                String outSn = reqData.getUca().getSerialNumber();
                String grpUserId = reqData.getUca().getUserId();
                String serviceId = data.getString("SERVICE_ID");

               
                IDataset datas = UserBlackWhiteInfoQry.getBlackWhiteInfoByUserID(grpUserId);
                
                datas =  DataHelper.filter(datas, "SERVICE_ID="+serviceId+",SERIAL_NUMBER="+outSn);
                
                if (IDataUtil.isEmpty(datas))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据名单号码：["+outSn+"],服务编码["+serviceId+"],用户编码["+grpUserId+"]查询名单信息不存在");
                    
                }
                data = datas.getData(0);
                data.put("OPER_STATE", "02");// 退出
                data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());

                data.put("END_DATE", SysDateMgr.getSysTime());
            }

            data.put("IS_NEED_PF", "1");// // 1或者是空 走服务开通发指令,0：不走服务开通不发指令 
           
            
        } 
	    
      return blackWhiteInfos;
	}

	 private static IDataset getParamFormServiceParamInfo(IDataset serviceParamInfo,String oprTypeKey,String numKey) throws Exception
     {
	        IData blackParamMap = new DataMap(); 
	      
	        IDataset blackServiceNumbers = new DatasetList(); 
	        
	        IDataset result = new DatasetList(); 
	        
	        for (Iterator iterator = serviceParamInfo.iterator(); iterator.hasNext();)
	        {
	            IData serviceParamInfoItem = (IData) iterator.next();
	            
	            IDataset attrInfos = serviceParamInfoItem.getDataset("ATTR_INFO");
	            
	            for (Iterator iterator2 = attrInfos.iterator(); iterator2.hasNext();)
	            {
	                IData attrInfoItem = (IData) iterator2.next();
	                
	                String key= attrInfoItem.getString("ATTR_KEYADD","");
	                
	                String value= attrInfoItem.getString("ATTR_VALUEADD","");
	                
	                //处理操作标志
	                if (key.equals(oprTypeKey))
	                {
	                    if (value.equals("1"))
	                    { 
	                        blackParamMap.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
	                    }
	                    else   if ( value.equals("2"))
	                    { 
	                        blackParamMap.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
	                    }
	                    else
	                    {
	                        CSAppException.apperr(CrmCommException.CRM_COMM_103,"不支持的操作类型");
	                    }
	                }
	              
	                
	                if (key.equals(numKey))
	                {
	                    blackServiceNumbers.add(value);
	                } 
	            } 
	        }
	        
	        if (IDataUtil.isNotEmpty(blackParamMap) && IDataUtil.isEmpty(blackServiceNumbers))
	        {
	            CSAppException.apperr(CrmCommException.CRM_COMM_103,"名单号码不能为空");
	        }
	        
	       
	        //重新组装 
	        for (Iterator iterator2 = blackServiceNumbers.iterator(); iterator2.hasNext();)
	        {
	            String item = (String) iterator2.next();
	            if (StringUtils.isEmpty(blackParamMap.getString("MODIFY_TAG")))
	            {
	                CSAppException.apperr(CrmCommException.CRM_COMM_103,"名单操作类型不能为空");
	            }
	                IData paramItem= new DataMap(); 
	                paramItem.put("SERIAL_NUMBER", item); 
	                paramItem.put("MODIFY_TAG", blackParamMap.getString("MODIFY_TAG"));  
	                result.add(paramItem);
	            
	          
	        }
	        
	        return result;
	 }

    public static IDataset getBlackParamFormServiceParamInfo(IDataset serviceParamInfos) throws Exception
    {
    	if(logger.isDebugEnabled())
    	{
    		logger.debug("物联网黑白名单入参构建入参"+serviceParamInfos);
    	}
        
        /**
         * 格式：SERIAL_NUMBER : 名单号码
         *       USER_TYPE_CODE :名单类型  W 白名单   B 黑名单
         *       SERVICE_ID ：服务编码
         *       MODIFY_TAG :1新增  2删除
         */
        IDataset allParam = new DatasetList();
        for (Iterator iterator = serviceParamInfos.iterator(); iterator.hasNext();)
        {
            IData serviceParam = (IData) iterator.next();

            IDataset serviceParamInfo = serviceParam.getDataset("SERVICE_PARAM");

            if (IDataUtil.isNotEmpty(serviceParamInfo))
            {

                IDataset blackParam = getParamFormServiceParamInfo(serviceParamInfo, "BlackNumOperType", "BlackNum");
                IDataset whiteParam = getParamFormServiceParamInfo(serviceParamInfo, "SysSpecialNumOperType",
                                                                   "SysSpecialNum");

                IDataUtil.insertIDatasetItem(blackParam, "USER_TYPE_CODE", "B");
                IDataUtil.insertIDatasetItem(whiteParam, "USER_TYPE_CODE", "W");

                if(IDataUtil.isNotEmpty(blackParam))
				{
                	allParam.addAll(blackParam);
				}
                if(IDataUtil.isNotEmpty(whiteParam))
				{
                	allParam.addAll(whiteParam);
				}

                IDataUtil.insertIDatasetItem(allParam, "SERVICE_ID", IDataUtil.getMandaData(serviceParam, "SERVICE_ID"));  

            }
        }
        
        if(logger.isDebugEnabled())
    	{
        	logger.debug("物联网黑白名单入参构建结果"+allParam);
    	}

        return allParam;

    }
    
    public static IDataset getServiceParamInfos(IDataset serviceParamInfos) throws Exception
	{
		 IDataset allParam = new DatasetList();
		 for (Iterator iterator = serviceParamInfos.iterator(); iterator.hasNext();)
		 {
			 IData serviceParam = (IData) iterator.next();
			 IDataset serviceParamInfo = serviceParam.getDataset("SERVICE_PARAM");
	
			 if (IDataUtil.isNotEmpty(serviceParamInfo))
			 {
	
				 IDataset blackParam = getParamServiceParamInfo(serviceParamInfo, "BlackNumOperType", "BlackNum");
				 IDataset whiteParam = getParamServiceParamInfo(serviceParamInfo, "SysSpecialNumOperType","SysSpecialNum");
	
				 if(IDataUtil.isNotEmpty(blackParam))
				 {
					 allParam.addAll(blackParam);
				 }
				 if(IDataUtil.isNotEmpty(whiteParam))
				 {
					 allParam.addAll(whiteParam);
				 }
			 }
		 }
		 return allParam;
	 }
    
    private static IDataset getParamServiceParamInfo(IDataset serviceParamInfo,String oprTypeKey,String numKey) throws Exception
	{
		IDataset blackServiceNumbers = new DatasetList(); 
		IDataset result = new DatasetList(); 
		
		for (Iterator iterator = serviceParamInfo.iterator(); iterator.hasNext();)
		{
		    IData serviceParamInfoItem = (IData) iterator.next();
		    IDataset attrInfos = serviceParamInfoItem.getDataset("ATTR_INFO");
		    for (Iterator iterator2 = attrInfos.iterator(); iterator2.hasNext();)
		    {
		    	IData paramMap = new DataMap(); 
		        IData attrInfoItem = (IData) iterator2.next();
		        String key = attrInfoItem.getString("ATTR_KEYADD","");
		        String value = attrInfoItem.getString("ATTR_VALUEADD","");
		        
		        //处理操作标志
		        if (key.equals(oprTypeKey))
		        {
		            if (value.equals("1"))
		            { 
		            	paramMap.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
		            }
		            else   if ( value.equals("2"))
		            { 
		            	paramMap.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
		            }
		            else
		            {
		                CSAppException.apperr(CrmCommException.CRM_COMM_103,"不支持的操作类型");
		            }
		            paramMap.put("ATTR_CODE", oprTypeKey);
		            paramMap.put("ATTR_VALUE", value);
		            blackServiceNumbers.add(paramMap);
		        }
		        else if(key.equals(numKey))
		        {
		        	paramMap.put("ATTR_CODE", numKey);
			        paramMap.put("ATTR_VALUE", value);
		        	blackServiceNumbers.add(paramMap);
		        }
		    }
		}
		return blackServiceNumbers;
	 }
    
    /**
	 * 通过paramAttr加载配置
	 * 
	 * @author pengxin
	 * @param paramAttr
	 * @return
	 * @throws Exception
	 */
	public static IData loadConfigData(String paramAttr) throws Exception {
		IData configData = new DataMap();
		IDataset configList = CommparaInfoQry.getCommByParaAttr("CSM", paramAttr, "ZZZZ");
		for (int i = 0; i < configList.size(); i++) {
			IData config = configList.getData(i);
			configData.put(config.getString("PARAM_CODE"), config);
		}
		return configData;
	}
	
}
