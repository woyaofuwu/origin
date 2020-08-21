
package com.asiainfo.veris.crm.order.soa.person.busi.plat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class PlatScoreExchangeSVC extends CSBizService
{
    public IDataset scoreExchangeCheck(IData data) throws Exception
    {
        String itemId = data.getString("ITEM_ID");
        IDataset giftInfos = CommparaInfoQry.queryScoreExchangePlat(itemId);
        if (IDataUtil.isEmpty(giftInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_463);
            return null;
        }
        else
        {
            IData gift = giftInfos.getData(0);
            String serviceId = gift.getString("SERVICE_ID");
            data.put("SERVICE_ID", serviceId);
            data.put("OPER_CODE", PlatConstants.OPER_ORDER);
            data.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);
          
            
            try
            {
                IDataset result = CSAppCall.call("SS.PlatRegSVC.tradeRegIntf", data); 
            	
            }
            catch (Exception e)
            { 
                String serviceName = PlatOfficeData.getInstance(serviceId).getServiceName();
                IDataset result = new DatasetList();
                IData map = new DataMap();
                String[] errorMessage;
                String errorCode;
                String errorInfo;
              //add by dujt 根据局数据类型区分标识 
            	IDataset PlatsvcInfos = PlatScoreExchangeSVC.querySpBizBySvcId(serviceId);
            	if (IDataUtil.isEmpty(PlatsvcInfos))
                {
            		
                    CSAppException.apperr(CrmCommException.CRM_COMM_680);
                    return null;
                }
            	else{
            		IData PlatsvcInfo = PlatsvcInfos.getData(0);
                	String BizTypeCodeStr = PlatsvcInfo.getString("BIZ_TYPE_CODE");
                	String SpCode = PlatsvcInfo.getString("SP_CODE");
                	String BizCode = PlatsvcInfo.getString("BIZ_CODE");
                	 data.put("BIZ_TYPE_CODE", BizTypeCodeStr);
                     data.put("SP_CODE", SpCode);
                     data.put("BIZ_CODE", BizCode);
            	} 
            	
                if (e.getMessage() != null)
                {
                    errorMessage = e.getMessage().split("●");
                    if (errorMessage != null && errorMessage.length <= 1)
                    {
                        errorMessage = e.getMessage().split("`");
                    }
                }
                else if (e.getCause() != null && e.getCause().getMessage() != null)
                {
                    errorMessage = e.getCause().getMessage().split("●");
                    if (errorMessage != null && errorMessage.length <= 1)
                    {
                        errorMessage = e.getCause().getMessage().split("`");
                    }
                }
                else if (e.getCause().getCause() != null && e.getCause().getCause().getMessage() != null)
                {
                    errorMessage = e.getCause().getCause().getMessage().split("●");
                    if (errorMessage != null && errorMessage.length <= 1)
                    {
                        errorMessage = e.getCause().getCause().getMessage().split("`");
                    }
                }
                else
                {
                    errorMessage = null;
                }

                if (errorMessage == null || errorMessage.length <= 1)
                {
                    // 截取不到报错
                    map.put("STATUS", "99");
                    map.put("DESC", "其他原因不可订购");
                    map.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
                    result.add(map);
                    return result;
                }
                errorCode = errorMessage[0];
                errorInfo = errorMessage[1];
                if ("0912".equals(errorCode) || "0984".equals(errorCode))
               // if (PlatException.CRM_PLAT_0912_6.toString().equals(errorCode) || PlatException.CRM_PLAT_0984.toString().equals(errorCode))
                {
                    this.switchFaultInfo(map, data.getString("BIZ_TYPE_CODE"),data.getString("SP_CODE"),data.getString("BIZ_CODE"));
                }
                else if ("0914".equals(errorCode))
               // else if (PlatException.CRM_PLAT_0914.toString().equals(errorCode))
                {
                     if ("23".endsWith(data.getString("BIZ_TYPE_CODE")))
                    //if ((serviceName.indexOf("飞信") != -1))
                    {
                        map.put("STATUS", "11");
                        map.put("DESC", "该用户未通过中国移动手机号码开通飞信业务，不可订购");
                    }
                    else
                    {
                        map.put("STATUS", "98");
                        map.put("DESC", "依赖关系不存在，不可订购" + errorInfo);
                    }
                }
                else
                {
                    map.put("STATUS", "99");
                    map.put("DESC", "其他原因不可订购，" + errorCode + ":" + errorInfo);
                    map.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
                }
                result.add(map);
                return result;
            }
            finally
            {
                SessionManager.getInstance().rollback();// 避免有插表，做回滚处理
            }
            IDataset result = new DatasetList();
            IData map = new DataMap();
            map.put("STATUS", "00");
            map.put("DESC", "可订购");
            map.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
            result.add(map);
            return result;
        }

    }

    private void switchFaultInfo(IData result, String BizTypeCodeStr ,String SpCode ,String BizCode)
    {

    	if ("801234".endsWith(SpCode)) 
        { 
    		if("110359".endsWith(BizCode)||"119257".endsWith(BizCode)||"119111".endsWith(BizCode)||"119256".endsWith(BizCode))
    		{
               result.put("STATUS", "01");
               result.put("DESC", "已开通凤凰周刊，不可重复订购（对应产品代号a3,a4）");
    		}
           else if("119112".endsWith(BizCode)||"119259".endsWith(BizCode)||"119258".endsWith(BizCode)||"110360".endsWith(BizCode))
           	     
            {
               result.put("STATUS", "02");
               result.put("DESC", "已开通凤凰观察，不可重复订购（对应产品代号b3,b4）");
            }
           else  //避免业务扩展 ，默认归属
        	{
        	   result.put("STATUS", "02");
               result.put("DESC", "已开通凤凰观察，不可重复订购（对应产品代号b3,b4）");
        	}
        }
        else if ("23".endsWith(BizTypeCodeStr)) 
        {
            result.put("STATUS", "12");
            result.put("DESC", "该用户已开通飞信会员产品，不可以订购");
        }
        else if ("65".endsWith(BizTypeCodeStr)) 
        {
            result.put("STATUS", "21");
            result.put("DESC", "该用户已开通手机导航产品，不可以订购");
        }
        else if ("28".endsWith(BizTypeCodeStr)) 
        {
            result.put("STATUS", "31");
            result.put("DESC", "该用户已开通游戏玩家产品，不可以订购");
        }
        else if ("60".endsWith(BizTypeCodeStr)) 
        {
            result.put("STATUS", "41");
            result.put("DESC", "该用户已开通同款阅读包产品，不可以订购");
      //关于下发近期积分商城相关省级配合改造的通知 by zhouyl5 20141016
  		}
        //else if(serviceName.indexOf("动漫") != -1){
        else{
  			result.put("STATUS", "51");
  			result.put("DESC", "该用户已开通同款漫赏包或动漫杂志类产品，不可订购");
  		}

    }
    
    public static IDataset querySpBizBySvcId(String serviceId) throws Exception
    {
        //IData param = new DataMap();
        //param.put("SERVICE_ID", serviceId);
        //return Dao.qryByCode("TD_B_PLATSVC", "SEL_SP_BIZ_BY_SVCID", param);
    	IDataset result = new DatasetList();
    	try{
        	result = UpcCall.querySpServiceAndProdByCond(null ,null ,null, serviceId);
        }catch(Exception e){
        	
        }
        
        return result;
    }
    
}
