
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQueryHelp;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.CParamQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class TBChecklimitTradeBySaleActive extends BreBase implements IBREScript
{
    private static final long serialVersionUID = -5292726269122756865L;

    private static Logger logger = Logger.getLogger(TBChecklimitTradeBySaleActive.class);

    /**
     * 业务受理前条件判断：活动与业务的限制！业务受理前特殊限制：该用户办理了
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBChecklimitTradeBySaleActive() >>>>>>>>>>>>>>>>>>");
        }

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */
        String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        String strUserId = databus.getString("USER_ID");
        String struserbKd = strUserId;
        String serialNumber = databus.getString("SERIAL_NUMBER");
        IData qryParam = new DataMap();
        if (serialNumber.startsWith("KD_"))
        {
            String strSn = serialNumber.replace("KD_", "");

            IDataset userInfos = UserInfoQry.getAllUserInfoBySn(strSn);
            if (IDataUtil.isNotEmpty(userInfos))
            {
            	struserbKd = userInfos.getData(0).getString("USER_ID", "");
            }
        }

        IDataset commparaDs = BreQryForCommparaOrTag.getCommpara("CSM", 173, strTradeTypeCode, CSBizBean.getUserEparchyCode());// ParamQry.getCommpara(pd,

        if (IDataUtil.isNotEmpty(commparaDs))
        {
            IDataset ds = CParamQry.getLimitActives(struserbKd, strTradeTypeCode);
            
            if (IDataUtil.isNotEmpty(ds))
            {
                // 拆机业务最后一个月不做限制
                boolean blnLastMonth = false;
                if ("605".equals(strTradeTypeCode) || "615".equals(strTradeTypeCode) || "624".equals(strTradeTypeCode) 
                		|| "625".equals(strTradeTypeCode) || "614".equals(strTradeTypeCode)|| "616".equals(strTradeTypeCode) 
                		|| "631".equals(strTradeTypeCode)|| "601".equals(strTradeTypeCode)|| "1605".equals(strTradeTypeCode)
                		|| "192".equals(strTradeTypeCode) || "3806".equals(strTradeTypeCode)|| "603".equals(strTradeTypeCode)) 	                                                                                                                     
                {
                    blnLastMonth = true;
                    String endDate = "";
                    for (int i = 0, iSize = ds.size(); i < iSize; i++)
                    {
                        String flag = ds.getData(i).getString("FLAG");
                        if(StringUtils.equals("1", flag))
                        {
                            blnLastMonth = false;
                            endDate = ds.getData(i).getString("END_DATE");
                            break;
                        }
                    }
                    
                    //预约拆机可以提前3个月预约拆机
                    if("1605".equals(strTradeTypeCode))
                    {
                    	blnLastMonth = true;
                    	for(int i = 0 ; i < ds.size() ; i++)
                    	{
                    		endDate = ds.getData(i).getString("END_DATE");
                            //取3个月的月底
                            String now_end = SysDateMgr.getSysDate();
                         	now_end = SysDateMgr.addMonths(now_end, 2);//只能在结束前3个月的时候可以预约,由于预约是失效后的次月，所以这里取从本月算起3个月的最后一天
                         	now_end = SysDateMgr.getDateLastMonthSec(now_end);
                         	if(SysDateMgr.compareTo(now_end, endDate) < 0)
                         	{
                         		blnLastMonth = false;
                         		break;
                         	}
                    	}
                    }
                    /**
                     * REQ201611020002 魔百和拆机优化
                     * chenxy3 2016-11-22 
                     * 魔百和拆机最后一个月允许拆机
                     * */
                    if("3806".equals(strTradeTypeCode)){
                    	blnLastMonth = true;
                    	for(int k = 0 ; k < ds.size() ; k++)
                    	{
                    		String thisMonFlag=ds.getData(k).getString("THISMONFLAG"); 
                    		if("-1".equals(thisMonFlag)){
                    			blnLastMonth = false;
                    			break;
                    		}  
                    	}
                    	
                    }
                    	 
                    //特殊拆机不需要限制
                    if("615".equals(strTradeTypeCode))
                    	 blnLastMonth = true;
                    
                    if("601".equals(strTradeTypeCode))
                    {
                    	String changeUpDownTag = databus.getString("CHANGE_UP_DOWN_TAG","");//降档，升档标志，1=升档，2=降档，0=不变，3=产品变了，速率不变,默认-1表示未传标记位，为查询时
                    	
                    	if("".equals(changeUpDownTag) || "1".equals(changeUpDownTag)) //初始状态和升档不需要再处理
                    		blnLastMonth = true;
                    	
                    	//判断预约时间是否大于结束时间，预约时间大于也允许
                    	if("0".equals(changeUpDownTag) || "2".equals(changeUpDownTag) || "3".equals(changeUpDownTag))
                    	{
                    		
                    		String specialSaleFlag = databus.getString("SPECIAL_SALE_FLAG","");
                    		
                    		if("1".equals(specialSaleFlag))
                        		blnLastMonth = true;
                    		
                    		String bookDate = databus.getString("BOOKING_DATE","");
                    		if(bookDate != null && !"".equals(bookDate))
                    		{
                    			for (int i = 0, iSize = ds.size(); i < iSize; i++)
                                {
                                    String flag = ds.getData(i).getString("FLAG");
                                    if(StringUtils.equals("0", flag))
                                    {
                                        endDate = ds.getData(i).getString("END_DATE");
                                        break ;
                                    }
                                }
                    			if(endDate == null && "".equals(endDate))
                    				endDate = ds.getData(0).getString("END_DATE");
                    			if(SysDateMgr.compareTo(bookDate, endDate) > 0)
                    			{
                    				blnLastMonth = true;
                    			}
                    		}
                    	}
                    }
                }
                
                if("601".equals(strTradeTypeCode)){
                //add by xuzh5 只有主产品发生改变时 规则拦截 2018-9-20 10:15:44
            	qryParam.clear();
                qryParam.put("USER_ID", strUserId);
                qryParam.put("PARA_CODE1", strTradeTypeCode);
                IDataset ds2 = Dao.qryByCode("TD_S_COMMPARA", "SEL_DATA_BY_PRODUCT_ID", qryParam);
                String productIdNew = databus.getString("PRODUCT_ID");
                String productIdOld="";
                IDataset ds3 = Dao.qryByCode("TD_S_COMMPARA", "SEL_DISNCT_BY_USER", qryParam);
                if(IDataUtil.isNotEmpty(ds3)){
                	productIdOld=ds3.getData(0).getString("PRODUCT_ID");
                }
                //end by xuzh5 只有主产品发生改变时 规则拦截 2018-9-20 10:15:44
                if (IDataUtil.isEmpty(ds2))
                {
                    if (!blnLastMonth)
                    {
                    	if(!productIdNew.equals(productIdOld))//add by xuzh5 只有主产品发生改变时 规则拦截 2018-9-20 10:15:44
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 6080, "业务受理前特殊限制：该用户办理了[" + ds.getData(0).getString("PRODUCT_NAME", "") + "]活动,不能办理该业务[" + strTradeTypeCode + "]!");
                    }
                }
                }else{
                // 查询看用户的产品是不是不收限制
                qryParam.clear();
                qryParam.put("USER_ID", strUserId);
                qryParam.put("PARA_CODE1", strTradeTypeCode);
                IDataset ds2 = Dao.qryByCode("TD_S_COMMPARA", "SEL_DATA_BY_PRODUCT_ID", qryParam);
                if (IDataUtil.isEmpty(ds2))
                {
                    if (!blnLastMonth)
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 6080, "业务受理前特殊限制：该用户办理了[" + ds.getData(0).getString("PRODUCT_NAME", "") + "]活动,不能办理该业务[" + strTradeTypeCode + "]!");
                    }
                }
            }
            }
        }
        
        
        /**
         * 业务受理前条件判断：优惠与业务的限制！业务受理前特殊限制：该用户含有XX优惠
         */
        //REQ201511020017 关于增加预约拆机服务的开发需求 by songlm 20151228
        //GPON宽带预约拆机，需要判断是否是宽带包年套餐，如果是，则只能包年套餐的最后一个月内预约拆机，否则不予办理
        //查看当前的业务是否存在拦截规则
        IDataset commpara532 = BreQryForCommparaOrTag.getCommpara("CSM", 532, strTradeTypeCode, CSBizBean.getUserEparchyCode());
        //如果存在拦截规则
        if (IDataUtil.isNotEmpty(commpara532))
        {
        	qryParam.clear();
        	qryParam.put("USER_ID", strUserId);
        	qryParam.put("PARAM_CODE", strTradeTypeCode);
        	//判断是否用户含有拦截规则不允许的条件
        	IDataset ds = Dao.qryByCode("TD_S_CPARAM", "SEL_TRADETYPE_LIMIT_DISCNT", qryParam);
        	if (IDataUtil.isNotEmpty(ds))
        	{
        		//GPON宽带预约拆机业务最后一个月不做限制,宽带拆机最后一个月允许拆机,有多条的情况需要考虑
        		boolean blnLastMonth = false;
        		if ("1605".equals(strTradeTypeCode) || "605".equals(strTradeTypeCode))	                                                                                                                     
        		{
        			blnLastMonth = true;
        			for (int i = 0, iSize = ds.size(); i < iSize; i++)
        			{
        				String flag = ds.getData(i).getString("FLAG","");
        				if(StringUtils.equals("1", flag))
        				{
        					blnLastMonth = false;
        					break;
        				}
        			}
        		}else if ("601".equals(strTradeTypeCode))//宽带产品变更
        		{
        			//宽带优化提升需求，可以随时变更，前提是升档，但这个要在用户选择了产品后再判断，这里就不判断了
        			blnLastMonth=true;
        		}else if ("615".equals(strTradeTypeCode))//宽带特殊拆机
        		{
        			//特殊拆机是对有营销活动和包年套餐的用户使用的，允许办理，界面上在进行进一步判断
        			blnLastMonth=true;
                }else if ("681".equals(strTradeTypeCode))//无手机宽带产品变更
                {
                    //无手机宽带产品变更，允许办理，界面上在进行进一步判断
                    blnLastMonth=true;
                }
                
                //预约拆机3个月内可以取消（含无手机宽带）
                if ("1605".equals(strTradeTypeCode)||"1685".equals(strTradeTypeCode))
                {
                    blnLastMonth = true;
                    for (int i = 0, iSize = ds.size(); i < iSize; i++)
                    {
                        String endDate = ds.getData(i).getString("END_DATE");
                        String now_end = SysDateMgr.getSysDate();
                        now_end = SysDateMgr.addMonths(now_end, 2);//只能在结束前3个月的时候可以预约,由于预约是失效后的次月，所以这里取从本月算起3个月的最后一天
                        now_end = SysDateMgr.getDateLastMonthSec(now_end);
                        if(SysDateMgr.compareTo(now_end, endDate) < 0)
                        {
                            blnLastMonth = false;
                            break ;
                        }
                    }
                } 
                
                //无手机宽带立即拆机最后一个月才允许办理
                if ("685".equals(strTradeTypeCode))
                {
                    blnLastMonth = true;
                    for (int i = 0, iSize = ds.size(); i < iSize; i++)
                    {
                        String endDate = ds.getData(i).getString("END_DATE");
                        String now_end = SysDateMgr.getSysDate();
                        now_end = SysDateMgr.addMonths(now_end, 0);//只能在结束前1个月的时候可以预约
                     	now_end = SysDateMgr.getDateLastMonthSec(now_end);
                     	if(SysDateMgr.compareTo(now_end, endDate) < 0)
                     	{
                     		blnLastMonth = false;
                     		break ;
                     	}
        			}
        		}
        		
                if ("687".equals(strTradeTypeCode))
                {
                    blnLastMonth=true;
                }
                
        		if (!blnLastMonth)
        		{
                    String strDiscntName = BreQueryHelp.getNameByCode("DiscntName", ds.getData(0).getString("DISCNT_CODE", ""));
        			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 6081, "业务受理前特殊限制：该用户含有[" +strDiscntName+ "]优惠,不能办理该业务！请使用“无手机宽带特殊拆机”功能进行拆机！");
        		}
        		
        		  //无手机宽带特殊拆机，随时可以拆，最后一个月不允许办理，提示其到宽带拆机界面办理
                if ("687".equals(strTradeTypeCode))
                {
                    blnLastMonth = true;
                    for (int i = 0, iSize = ds.size(); i < iSize; i++)
                    {
                        String endDate = ds.getData(i).getString("END_DATE");
                        String now_end = SysDateMgr.getSysDate();
                        now_end = SysDateMgr.addMonths(now_end, 0);//只能在结束前1个月的时候可以预约
                        now_end = SysDateMgr.getDateLastMonthSec(now_end);
                        if(SysDateMgr.compareTo(now_end, endDate) >= 0)
                        {
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 6083, "业务受理前特殊限制：使用“宽带特殊拆机”功能进行拆机不允许在最后一个月办理，请到“无手机宽带拆机”正常办理！");
                        }
                    }
                } 
        	}
        }
        
        //如果存在预约拆机登记，不允许某些业务
        //先判断是否是需要做处理的业务类型
        IDataset commpara533 = BreQryForCommparaOrTag.getCommpara("CSM", 533, strTradeTypeCode, CSBizBean.getUserEparchyCode());
        if (IDataUtil.isNotEmpty(commpara533))
        {
        	//判断是否存在GPON宽带预约拆机
        	IData tempParam = new DataMap();
        	tempParam.put("USER_ID", strUserId);
            IDataset destoryInfos = Dao.qryByCode("TF_F_USER_GPON_DESTROY", "SEL_DESTORY_ORDER_BY_USERID", tempParam);
            
            //如果存在预约拆机记录，且不是GPON预约拆机业务，则拦截
            if( (IDataUtil.isNotEmpty(destoryInfos)) )//kangyt 2016-4-18 修改
            //if( (IDataUtil.isNotEmpty(destoryInfos)) && (!"1605".equals(strTradeTypeCode)) )
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 6082, "业务受理前特殊限制：该用户含有宽带预约拆机记录,不能办理该业务!");
            }
            
            if (!serialNumber.startsWith("KD_"))
            {
                String kdSerialNumber = "KD_" + serialNumber;

                IDataset userInfos = UserInfoQry.getAllUserInfoBySn(kdSerialNumber);
                if (IDataUtil.isNotEmpty(userInfos))
                {
                	String kdUserId = userInfos.getData(0).getString("USER_ID", "");
                	IData tempParam2 = new DataMap();
                    tempParam2.put("USER_ID", kdUserId);
                    IDataset destoryInfos2 = Dao.qryByCode("TF_F_USER_GPON_DESTROY", "SEL_DESTORY_ORDER_BY_USERID", tempParam2);

                    //如果存在预约拆机记录，且不是GPON预约拆机业务，则拦截
                    if( (IDataUtil.isNotEmpty(destoryInfos2)) )//kangyt 2016-4-18 修改
                    //if( (IDataUtil.isNotEmpty(destoryInfos2)) && (!"1605".equals(strTradeTypeCode)) )
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 6082, "业务受理前特殊限制：该用户含有宽带预约拆机记录,不能办理该业务!");
                    }
                }
            }
            
        }
        //end
        

        if (logger.isDebugEnabled())
        {
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBChecklimitTradeBySaleActive() " + bResult + "<<<<<<<<<<<<<<<<<<<");
        }

        return bResult;
    }

}
