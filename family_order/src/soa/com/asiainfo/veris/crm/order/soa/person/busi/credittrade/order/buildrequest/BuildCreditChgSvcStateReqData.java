package com.asiainfo.veris.crm.order.soa.person.busi.credittrade.order.buildrequest;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TradeSvcStateParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: BuildCreditChgSvcStateReqData.java
 * @Description: 信控停开机专用
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-9-9 下午8:36:03
 */
public class BuildCreditChgSvcStateReqData extends BaseBuilder  implements IBuilder {

	 //重写父类方法
	 public BaseReqData getBlankRequestDataInstance()
	 {
	      return new BaseReqData();
	 }
	 
	// 请求数据组织方法，实现父类抽象方法
    public void buildBusiRequestData(IData idata, BaseReqData basereqdata) throws Exception
    {

    }

    // 重构父类的构建UcaData方法，如果有user_id传入，则用userId来查询用户资料，提高信控相关业务查询用户资料的效率
    // 根据serial_number无法走分区，根据user_id可以走分区，速度提升。
    public UcaData buildUcaData(IData param) throws Exception
    {
        // 设置三户资料对象
        String sn = param.getString("SERIAL_NUMBER");
        UcaData uca = DataBusManager.getDataBus().getUca(sn);
        if (uca == null)
        {
            String userId = param.getString("USER_ID", "");
            if (StringUtils.isNotBlank(userId))
            {
                uca = UcaDataFactory.getUcaByUserId(userId);
            }
            else
            {
                uca = UcaDataFactory.getNormalUca(sn);
            }
        }
        return uca;
    }

    // 重写父类的此方法
    //对于信控的业务，不走bre的规则执行，因为bre里面有很多-1的规则，而且这些规则和信控的有冲突，所以单独在代码中写死
    public void checkBefore(IData input, BaseReqData reqData) throws Exception
    {
        UcaData uca = reqData.getUca();
        IDataset userSvcStateInfos = new DatasetList();
        List<SvcStateTradeData> usetSvcStateList = uca.getUserSvcsState();//这里取了之后在登记台账的地方也可以使用的,可以提高效率
        int size = usetSvcStateList.size();
        for (int j = 0; j < size; j++)
        {
        	if(StringUtils.equals("1",usetSvcStateList.get(j).getMainTag()))//只加入主体服务的状态数据
        	{
        		userSvcStateInfos.add(usetSvcStateList.get(j).toData());
        	}
        }
        
    	 String userId = uca.getUserId();
         String tradeTypeCode = reqData.getTradeType().getTradeTypeCode();
         String acceptMonth = SysDateMgr.getSysDate("MM");
         // 检查是否存在有未完成的服务状态变更工单
         IDataset unFinishInfos = TradeInfoQry.queryTradeByUserAndAcceptMonth(userId, acceptMonth);
         if (IDataUtil.isNotEmpty(unFinishInfos))
         {
             // 存在，且和当前的业务类型一致，则报错。
             if (StringUtils.equals(tradeTypeCode, unFinishInfos.get(0, "TRADE_TYPE_CODE").toString()))
             {
            	 String errorInfo = "办理[" + tradeTypeCode + "]业务，用户有未完工的该业务，不需要再处理！";
            	 throwsException("214325",errorInfo);
             }
         }

         if (IDataUtil.isEmpty(userSvcStateInfos))
         {
        	 String errorInfo = "没有获取到用户[" + userId + "]服务状态相关资料！";
        	 throwsException("115002",errorInfo);
         }

         // 查询本次业务的服务状态变更参数
         String brandCode = uca.getBrandCode();
         String productId = uca.getProductId();
         String eparchyCode = CSBizBean.getTradeEparchyCode();
         IDataset svcStateParam = TradeSvcStateParamInfoQry.querySvcStateParamByKey(tradeTypeCode, brandCode, productId, eparchyCode);
         if (IDataUtil.isEmpty(svcStateParam))
         {
        	 StringBuilder errorSb = new StringBuilder();
        	 errorSb.append("没有获取到业务类型[").append(tradeTypeCode).append("]对应的服务状态变更参数！");
        	 errorSb.append("用户编号[").append(uca.getUserId()).append("]");
        	 throwsException("115999",errorSb.toString());
         }

         // 比较看是否有主体服务的变化
         for (int i = 0; i < svcStateParam.size(); i++)
         {
             String paramOldStatecode = svcStateParam.get(i, "OLD_STATE_CODE").toString();
             if (!StringUtils.equals("%", paramOldStatecode))
             {
                 String paramSvcId = svcStateParam.get(i, "SERVICE_ID").toString();
                 String paramNewStateCode = svcStateParam.get(i, "NEW_STATE_CODE").toString();
                 for (int j = 0; j < userSvcStateInfos.size(); j++)
                 {
                     String userSvcId = userSvcStateInfos.get(j, "SERVICE_ID").toString();
                     String userStateCode = userSvcStateInfos.get(j, "STATE_CODE").toString();

                     if (StringUtils.equals(paramSvcId, userSvcId) && StringUtils.equals(userStateCode, paramNewStateCode))
                     {
                    	 StringBuilder errorSb = new StringBuilder();
                    	 errorSb.append("办理[").append(tradeTypeCode).append("]业务，用户状态已经是该状态或存在未完工的变更到该状态的工单，不需要再开通！");
                    	 errorSb.append("用户编号[").append(uca.getUserId()).append("]");
                    	 errorSb.append("服务编码[").append(paramSvcId).append("]");
                    	 errorSb.append("状态[").append(userStateCode).append("]");
                    	 throwsException("115040",errorSb.toString());
                     }

                     if (StringUtils.equals(paramSvcId, userSvcId) && StringUtils.equals(userStateCode, paramOldStatecode))
                     {
                         return;// 满足条件直接返回false
                     }
                 }
             }
         }
         
         
         // 如果走到这里 那说明就是不满足了
         String errorInfo = "办理[" + tradeTypeCode + "]业务,用户原服务状态不满足业务办理条件,不能生成服务状态台帐！";
         throwsException("115003",errorInfo);
    }
    
    /**
     * 由于不支持制定错误编码抛出异常，所以只能模拟规则返回错误的方式来抛错
     * @param errorCode
     * @param errorInfo
     * @throws Exception
     */
    private void throwsException(String errorCode,String errorInfo)throws Exception
    {
    	IDataset errorInfos = new DatasetList();
    	
    	IData errorData = new DataMap();
    	errorData.put("TIPS_CODE", errorCode);
    	errorData.put("TIPS_INFO", errorInfo);
    	errorInfos.add(errorData);
    	
    	IData checkData = new DataMap();
    	checkData.put("TIPS_TYPE_ERROR", errorInfos);
    	
    	CSAppException.appError(errorCode, errorInfo);
    }
}
