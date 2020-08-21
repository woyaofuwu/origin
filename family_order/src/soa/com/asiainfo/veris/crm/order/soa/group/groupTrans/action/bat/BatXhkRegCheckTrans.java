package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.Utility;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.group.bat.xhkregcheck.XhkRegCheckBean;

public class BatXhkRegCheckTrans implements ITrans
{
    @Override
    public void transRequestData(IData batData) throws Exception
    {
        // 初始化数据

        // 校验请求参数
        checkRequestDataSub(batData);

        // 根据条件判断调用服务
        setSVC(batData);
    }

    protected void checkRequestDataSub(IData batData) throws Exception
    {
        String mainSn = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码(学护卡号码)
        IData param = new DataMap();
        String batchId = IDataUtil.chkParam(batData, "BATCH_ID");

        IData userGrpInfo = UcaInfoQry.qryUserMainProdInfoBySn(mainSn);        
        //学护卡成员订购trade_type_code 3744 td_s_tradetype
        //IData resultInfo = UCAInfoIntfViewUtil.qryMebUCAAndAcctDayInfoBySn(this, mainSn, true, false);
        if (IDataUtil.isEmpty(userGrpInfo))
        {// 网外号码           
            param.put("BATCH_ID", batchId);
            param.put("SERIAL_NUMBER", mainSn);
            param.put("DEAL_RESULT", "不符合：查询不到成员服务号码["+mainSn+"]的用户资料！");
            param.put("DEAL_DESC", "不符合：查询不到成员服务号码["+mainSn+"]的用户资料！");
            XhkRegCheckBean.updateBatDealByBatchIdSn(param);
            //CSAppException.apperr(BatException.CRM_BAT_79);
            //Utility.error("不符合：查询不到成员服务号码["+mainSn+"]的用户资料！");
            Utility.abort("不符合：查询不到成员服务号码["+mainSn+"]的用户资料！");
        }else{  
            String rst = "ok";
            IData oweFee = AcctCall.getOweFeeByUserId(userGrpInfo.getString("USER_ID"));
            if(null != oweFee && !oweFee.getString("ACCT_BALANCE").equals("")){
                if(oweFee.getInt("ACCT_BALANCE") < 0){
                    rst = "不符合：成员服务号码["+mainSn+"]欠费！"+String.format("%1$3.2f", oweFee.getDouble("ACCT_BALANCE", 0.0) / 100.0);
                    Utility.abort(rst);
                }
            }else{
                rst = "不符合：成员服务号码["+mainSn+"]查询账务无数据！";
                Utility.abort(rst);
            }
            param.put("BATCH_ID", batchId);
            param.put("SERIAL_NUMBER", mainSn);
            param.put("DEAL_RESULT", rst);
            param.put("DEAL_DESC", rst);
            XhkRegCheckBean.updateBatDealByBatchIdSn(param);            
        }
    }

    // 根据条件判断调用服务
    protected void setSVC(IData batData) throws Exception
    {
        String svcName = "";
        IData svcData = batData.getData("svcData", new DataMap());

        svcName = "SS.XhkRegCheckSVC.xhkRegCheck";

        svcData.put("REAL_SVC_NAME", svcName);
    }
}
