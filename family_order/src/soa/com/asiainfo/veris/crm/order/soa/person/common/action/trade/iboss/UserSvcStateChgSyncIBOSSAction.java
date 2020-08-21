package com.asiainfo.veris.crm.order.soa.person.common.action.trade.iboss;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ResException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: UserSvcStateChgSyncIBOSSAction.java
 * @Description: 如果用户订购有行车卫士，用户服务状态变更同步给行车卫士平台
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-9-18 下午9:15:10
 */
public class UserSvcStateChgSyncIBOSSAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        UcaData ucaData = btd.getRD().getUca();
        String userId = ucaData.getUserId();
        String tradeTypeCode = btd.getTradeTypeCode();
        List<PlatSvcTradeData> planSvcTradeDataList = ucaData.getUserPlatSvcs();
        if (planSvcTradeDataList!=null && planSvcTradeDataList.size()>0)
        {
            boolean bFind = false;
            for (int i = 0, count = planSvcTradeDataList.size(); i < count; i++)
            {
                PlatSvcTradeData planSvcTradeData = planSvcTradeDataList.get(i);
                 if ((StringUtils.equals("3455", planSvcTradeData.getElementId()) || StringUtils.equals("3456", planSvcTradeData.getElementId()))
                         && (StringUtils.equals("A", planSvcTradeData.getBizStateCode()) || StringUtils.equals("N", planSvcTradeData.getBizStateCode())))
                {
                     // 处理局数据不存在的情况
                     PlatOfficeData officeData = null;
                     try
                     {
                         officeData = PlatOfficeData.getInstance(planSvcTradeData.getElementId());
                     }
                     catch (Exception e)
                     {

                     }

                     if (officeData!=null && StringUtils.equals("04", officeData.getBizTypeCode()))
                     {
                         bFind = true;
                         break;
                     }
                }
            }
            //如果用户订购有行车卫士，同步给行车卫士平台
            if (bFind)
            {
                //同步给行车卫士平台
                IData inparams = new DataMap();
                inparams.put("ROUTETYPE", "00");
                inparams.put("ROUTEVALUE", "000");
                inparams.put("KIND_ID", "BIP6B901_T9898989_0_0");
                inparams.put("MSG_TYPE", "SyncOrderRelationReq");//业务标识
                inparams.put("TRANSACTION_ID", btd.getTradeId());//操作流水
                if (StringUtils.equals("192", tradeTypeCode) || StringUtils.equals("7240", tradeTypeCode))
                {
                    inparams.put("ACTION_ID", "2");//服务状态管理动作代码
                }
                else if (StringUtils.equals("133", tradeTypeCode))
                {
                    inparams.put("ACTION_ID", "4");//服务状态管理动作代码
                }
                else if (StringUtils.equals("131", tradeTypeCode))
                {
                    inparams.put("ACTION_ID", "3");//服务状态管理动作代码
                }else {
                    return;
                }

                //获取imsi
                List<ResTradeData> resTradeDataList = ucaData.getUserAllRes();
                if (resTradeDataList == null || resTradeDataList.size()==0)
                {
                    CSAppException.apperr(ResException.CRM_RES_6);
                }
                String imsi = "";
                for (int i = 0, count=resTradeDataList.size(); i < count; i++)
                {
                    ResTradeData resTradeData = resTradeDataList.get(i);
                    if (StringUtils.equals("1", resTradeData.getResTypeCode()))
                    {
                        imsi = resTradeData.getImsi();
                        break;
                    }
                }

                //获取主号码
                IDataset relaUUDataset = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, "88", "2");
                if (IDataUtil.isEmpty(relaUUDataset))
                {
                    return;
                }
                String serialNumberA  = relaUUDataset.getData(0).getString("SERIAL_NUMBER_A");
                inparams.put("FFEUUER_ID", StringUtils.substring(serialNumberA, 2));//主号码
                inparams.put("FEEUSER_ID", StringUtils.substring(serialNumberA, 2));//主号码
                inparams.put("PROVINCE_ID", "8981");// 
                inparams.put("DESTUSER_ID", ucaData.getSerialNumber());//从号码
                inparams.put("SPSERVICE_ID","2" );//行车卫士平台中该服务的服务代码
                inparams.put("DESTUSER_IMSI", imsi);//终端IMSI   

                IDataset returnDaset = IBossCall.callHttpIBOSS7("IBOSS", inparams);
                if(IDataUtil.isEmpty(returnDaset))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用IBOSS接口报错.KIND_ID=BIP6B901_T9898989_0_0！");
                }
                IData tmpData = returnDaset.getData(0);
                if (!StringUtils.equals("0", tmpData.getString("RESULT"))) {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "同步给行车卫士平台信息出错，错误编码【" + tmpData.getString("RESULT")+ "】!");
                }
            }
        }
    }
}
