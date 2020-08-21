
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.script.rule.iot.IotCheck;

/**
 * 
 * 
 * @author 
 *
 */
public class CheckDiscntBottomPriceAction implements ITradeAction
{
	@SuppressWarnings("unchecked")
	@Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        UcaData uca = btd.getRD().getUca();
        if(!"PWLW".equals(uca.getBrandCode()))
		{
            return;
        }
        
        List<DiscntTradeData> tradeDiscntList = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        List<AttrTradeData> tradeAttrList = btd.getTradeDatas(TradeTableEnum.TRADE_ATTR);
        
        if(CollectionUtils.isNotEmpty(tradeDiscntList))
        {
        	int size = tradeDiscntList.size();
            for (int i = 0; i < size; i++)
            {
            	DiscntTradeData dataDiscnt = tradeDiscntList.get(i);
            	String modifyTag = dataDiscnt.getModifyTag();
            	String elementId = dataDiscnt.getElementId();
            	String instId = dataDiscnt.getInstId();
            	if(BofConst.MODIFY_TAG_ADD.equals(modifyTag))
            	{
            		if(tradeAttrList != null && tradeAttrList.size() > 0)
            		{
            			String muLuJia = "";//目录价
        				String diJia = "";//折扣底线
        				String bottomPrice = ""; //折扣价
        				String approvalNum = "";//审批文号
            			for(AttrTradeData attrTrade: tradeAttrList)
    					{
    						String attrCode = attrTrade.getAttrCode();
    						String attrValue = attrTrade.getAttrValue();
    						String relaInstId = attrTrade.getRelaInstId();
    						if(StringUtils.isNotBlank(instId) && StringUtils.isNotBlank(relaInstId)
    								&& StringUtils.equals(instId, relaInstId))
    						{
    							if(StringUtils.isNotBlank(attrCode) 
    									&& StringUtils.equals("BottomPrice", attrCode))
    							{
    								bottomPrice = attrValue;
    							}
    							else if(StringUtils.isNotBlank(attrCode) 
    									&& StringUtils.equals("ApprovalNum", attrCode))
    							{
    								approvalNum = attrValue;
    							}
    						}
    					}
            			
            			IDataset configList = CommparaInfoQry.getCommparaAllCol("CSM", "9013", elementId,"0898");
        				if(IDataUtil.isNotEmpty(configList))
        				{
        					IData configParam = configList.getData(0);
        					if(IDataUtil.isNotEmpty(configParam))
        					{
        						muLuJia = configParam.getString("PARA_CODE22","");
        						diJia = configParam.getString("PARA_CODE23","");
        					}
        				}
        				
        				if(StringUtils.isNotBlank(bottomPrice))
        				{
        					boolean flag = isNumber(bottomPrice);
        					if(!flag)
        					{
        						String err = "优惠" + elementId + "填写的折扣价必须是数字并且格式必须是0.00!";
    							CSAppException.apperr(CrmCommException.CRM_COMM_103, err);
        					}
        					else 
        					{
        						int length = bottomPrice.indexOf(".");
        						if(length < 0)
        						{
        							String err = "优惠" + elementId + "填写的折扣价必须是数字并且格式必须是0.00!";
        							CSAppException.apperr(CrmCommException.CRM_COMM_103, err);
        						}
        					}
        				}
        				if(StringUtils.isNotBlank(bottomPrice) && StringUtils.isNotBlank(muLuJia))
        				{
        					BigDecimal muLuJiaDec = new BigDecimal(muLuJia);
        					BigDecimal bottomPriceDec = new BigDecimal(bottomPrice);
        					
        					int result = muLuJiaDec.compareTo(bottomPriceDec);
        					if(result == 1)//折扣价比目录价低
        					{
        						if(StringUtils.isBlank(approvalNum))
        						{
        							String err = "优惠" + elementId + "填写的折扣价低于目录价时,审批文号不能为空!";
        							CSAppException.apperr(CrmCommException.CRM_COMM_103, err);
        						}
        					}
        				}
            		}
            	}
            }
        }
        
        //NB套餐、长周期套餐的折后价处理
        if(CollectionUtils.isNotEmpty(tradeDiscntList))
        {
        	IDataset tradeAttrs = new DatasetList();
        	if(CollectionUtils.isNotEmpty(tradeAttrList)) 
        	{
                for (int i = 0; i < tradeAttrList.size(); i++)
                {
                    IData data = new DataMap();
                    AttrTradeData attrTradeData = tradeAttrList.get(i);
                    data.put("RELA_INST_ID", attrTradeData.getRelaInstId());
                    data.put("ATTR_CODE", attrTradeData.getAttrCode());
                    data.put("ATTR_VALUE", attrTradeData.getAttrValue());
                    data.put("MODIFY_TAG", attrTradeData.getModifyTag());
                    data.put("INST_TYPE", attrTradeData.getInstType());
                    tradeAttrs.add(data);
                }
            }
        	
        	int size = tradeDiscntList.size();
            for (int i = 0; i < size; i++)
            {
            	DiscntTradeData dataDiscnt = tradeDiscntList.get(i);
            	String modifyTag = dataDiscnt.getModifyTag();
            	String elementId = dataDiscnt.getElementId();
            	String instId = dataDiscnt.getInstId();
            	if(BofConst.MODIFY_TAG_ADD.equals(modifyTag))
            	{
            		IData discntConfig = IotCheck.DISCNT_CONFIG_MAP.get(elementId);
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
             						if(CollectionUtils.isNotEmpty(tradeAttrList))
             						{
             							for (int j = 0; j < tradeAttrList.size(); j++)
             			                {
             			                    AttrTradeData attrTradeData = tradeAttrList.get(j);
             			                    String relaInstId = attrTradeData.getRelaInstId();
             			                    String attrCode = attrTradeData.getAttrCode();
             			                    String modifyTags = attrTradeData.getModifyTag();
             			                    String instType = attrTradeData.getInstType();
             			                    if(instId.equals(relaInstId) && "20200511".equals(attrCode) 
             			                    		&& "0".equals(modifyTags) && "D".equals(instType))
             			                    {
             			                    	attrTradeData.setAttrValue(firstMoney);
             			                    }
             			                    else if(instId.equals(relaInstId) && "20200512".equals(attrCode) 
             			                    		&& "0".equals(modifyTags) && "D".equals(instType))
             			                    {
             			                    	attrTradeData.setAttrValue(lastMoney);
             			                    }
             			                }
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
                 						if(CollectionUtils.isNotEmpty(tradeAttrList))
                 						{
                 							for (int j = 0; j < tradeAttrList.size(); j++)
                 			                {
                 			                    AttrTradeData attrTradeData = tradeAttrList.get(j);
                 			                    String relaInstId = attrTradeData.getRelaInstId();
                 			                    String attrCode = attrTradeData.getAttrCode();
                 			                    String modifyTags = attrTradeData.getModifyTag();
                 			                    String instType = attrTradeData.getInstType();
                 			                    if(instId.equals(relaInstId) && "20200513".equals(attrCode) 
                 			                    		&& "0".equals(modifyTags) && "D".equals(instType))
                 			                    {
                 			                    	attrTradeData.setAttrValue(allMoney);
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
        
    }
	
	private boolean isNumber(String str) throws Exception
	{
		String regex = "^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){2})?$";
		Pattern pattern = Pattern.compile(regex);
		Matcher match = pattern.matcher(str);
		return match.matches();
	}
}
