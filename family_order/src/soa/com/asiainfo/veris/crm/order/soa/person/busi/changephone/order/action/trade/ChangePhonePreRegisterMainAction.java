
package com.asiainfo.veris.crm.order.soa.person.busi.changephone.order.action.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.ChangePhoneException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PreTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changephone.order.requestdata.ChangePhonePreRegisterRequestData;

public class ChangePhonePreRegisterMainAction implements ITradeAction
{

    private Object convertIdType(String psptTypeCode, String direct) throws Exception
    {
        // TODO Auto-generated method stub

        IData params = new DataMap();

        IDataset dsCommonParam = CommparaInfoQry.getCommNetInfo("CSM", "8000", "PSPT_IBOSS_ALT");

        for (int i = 0; i < dsCommonParam.size(); i++)
        {
            if ("+".equals(direct))
            {
                String str = "," + dsCommonParam.getData(0).getString("PARA_CODE1", "") + ",";
                if (str.contains("," + psptTypeCode + ","))
                    return dsCommonParam.getData(0).getString("PARA_CODE3", "");
            }
            else
            {
                if (psptTypeCode.equals(dsCommonParam.getData(0).getString("PARA_CODE3", "")))
                    return dsCommonParam.getData(0).getString("PARA_CODE1", "");
            }
        }
        CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_2908, psptTypeCode);

        return "";

    }

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        List<MainTradeData> mainList = btd.getTradeDatas(TradeTableEnum.TRADE_MAIN);

        ChangePhonePreRegisterRequestData changePhoneRD = (ChangePhonePreRegisterRequestData) btd.getRD();

        mainList.get(0).setRemark(btd.getRD().getRemark());
        mainList.get(0).setRsrvStr2("GH");
        mainList.get(0).setRsrvStr3(changePhoneRD.getOldSerialNum());
        mainList.get(0).setRsrvStr4(changePhoneRD.getNewSerialNum());
        mainList.get(0).setRsrvStr5(changePhoneRD.getPsptId());
        mainList.get(0).setRsrvStr6(changePhoneRD.getPsptTypeCode());

        String new_province = changePhoneRD.getNewProvince();
        String old_province = changePhoneRD.getOldProvince();
        if ("A".equals(new_province) && "A".equals(old_province))
        {

        }
        else if ("B".equals(new_province) && "B".equals(old_province))
        {
            this.provinceInDeal(btd);
            // IDataset dataset = CSAppCall.call(this, "SS.ChangePhonePreRegisterRegSVC.tradeReg",btd.getRD().);

        }
        else
        {
            this.provinceOutDeal(btd);
        }

    }

    private void provinceInDeal(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        ChangePhonePreRegisterRequestData changePhoneReqData = (ChangePhonePreRegisterRequestData) btd.getRD();

        IData param = new DataMap();
        param.put("OLD_ID_VALUE", changePhoneReqData.getOldSerialNum());
        param.put("NEW_ID_VALUE", changePhoneReqData.getNewSerialNum());

        String newEparchyForRoute = "";
        if ("A".equals(changePhoneReqData.getNewProvince()))
        {
            param.put("WH_HANDLE", "01"); // 01:新归属地接收业务申请
            newEparchyForRoute = changePhoneReqData.getOldEparchy();
        }
        else
        {
            param.put("WH_HANDLE", "02");
            newEparchyForRoute = changePhoneReqData.getNewEparchy();
        }
        // param.put ( "CHANNEL" , pd.getData().getString("IN_MODE_CODE" , "" ) ) ;

        String channel = btd.getMainTradeData().getInModeCode();
        if (channel.equals("2"))
        {
            channel = "02";
        }
        else if (channel.equals("5"))
        {
            channel = "04";
        }
        else if (channel.equals("1"))
        {
            channel = "07";
        }
        else if (channel.equals("0"))
        {
            channel = "08";
        }
        else if (channel.equals("L"))
        {
            channel = "03";
        }
        else if (channel.equals("K"))
        {
            channel = "01";
        }
        else
        {
            channel = "08"; // 默认营业厅接入
        }
        param.put("CHANNEL", channel);

        param.put("ID_CARD_TYPE", convertIdType(changePhoneReqData.getPsptTypeCode(), "+"));
        param.put("ID_CARD_NUM", changePhoneReqData.getPsptId());
        param.put("OPR_CODE", "01"); // 01-预申请
        param.put("RESERVE", "");

        IData moveInfo = new DataMap(changePhoneReqData.getMoveInfo());

        param.put("BIZ_INFO", moveInfo.getString("BIZ_INFO"));
        param.put("MOVED", moveInfo.getString("MOVED"));

        // 框架要求特殊处理

        // String result = HttpHelper.callHttpSvc(pd, "ITF_CRM_AltSnPreRegister", param).toString();

        // 框架要求特殊处理 的恢复

        IData resultMap = null;
        IData pPreUpdate = new DataMap();
        pPreUpdate.put("RSRV_STR7", resultMap.getString("BIZ_ORDER_RESULT") + "|" + resultMap.getString("RSPCODE")); // "X_RESULTCODE",
        // ""
        // )
        // )
        // ;
        pPreUpdate.put("RSRV_STR8", resultMap.getString("BIZ_ORDER_RSP_DESC")); // X_RESULTINFO", "" ) ) ;
        // pPreUpdate.put ( "TRADE_ID" , data.getString ( "TRADE_ID", "" ) ) ;
        // daoPreUpdate.executeUpdateByCodeCode("TF_B_PRE_TRADE", "UPD_STATUS_BY_ID",pPreUpdate );

        PreTradeData preTD = new PreTradeData();

        if (!"0000".equals(resultMap.getString("BIZ_ORDER_RESULT")))
        {
            // 预申请失败则把把tf_b_PRE_TRADE表数据删掉
            IData statusUpdate = new DataMap();
            // statusUpdate.put ( "TRADE_ID" , data.getString ( "TRADE_ID", "" ) ) ;//预申请发起方

            // deletedata.executeUpdateByCodeCode("TF_B_PRE_TRADE", "DEL_BY_TRADE_ID",statusUpdate );

            String content = "您好，很抱歉，您不符合改号业务办理条件，详情请垂询10086。中国移动";
        }
        // sms( pd , td , param.getString("NEW_ID_VALUE", "" ) , content , "改号预受理提醒") ;
        // 2013-03-21 在“改号业务预申请”界面提交后有拦截“”，然后在“改号业务受理状态查询”界面又可以查到状态为“预受理成功 ”，

        // common.error("落地方不符合改号业务办理条件:"+ resultMap.getString ( "BIZ_ORDER_RSP_DESC" )) ;
    }

    private void provinceOutDeal(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub

    }

}
