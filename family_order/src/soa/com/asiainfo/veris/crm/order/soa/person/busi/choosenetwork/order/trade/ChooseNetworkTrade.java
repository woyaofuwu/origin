/**
 *
 */

package com.asiainfo.veris.crm.order.soa.person.busi.choosenetwork.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.choosenetwork.InsertChooseNetwork;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.choosenetwork.order.requestdata.ChooseNetworkReqData;

public class ChooseNetworkTrade extends BaseTrade implements ITrade
{

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
    	ChooseNetworkReqData chooseNetworkRD = (ChooseNetworkReqData) btd.getRD();
        stringTableTradeOther(btd);
        tcsOneCardmuilNumber(chooseNetworkRD,btd);
    }

    protected void stringTableTradeOther(BusiTradeData btd) throws Exception
    {
        ChooseNetworkReqData chooseNetworkRD = (ChooseNetworkReqData) btd.getRD();

        String userId = chooseNetworkRD.getUca().getUserId();

        String modifyTag = (StringUtils.equals(chooseNetworkRD.getOperType(), "01")||StringUtils.equals(chooseNetworkRD.getOperType(), "03")) ? BofConst.MODIFY_TAG_ADD : BofConst.MODIFY_TAG_DEL;
        OtherTradeData otherTradeData = new OtherTradeData();
        if (StringUtils.equals(BofConst.MODIFY_TAG_ADD, modifyTag))
        {
            otherTradeData.setModifyTag(modifyTag);

            IDataset resDataset = UserResInfoQry.getUserResInfoByUserId(userId);
            otherTradeData = new OtherTradeData();

            if (IDataUtil.isNull(resDataset))
            {
                CSAppException.apperr(CrmCardException.CRM_CARD_145);
            }
            else
            {
                for (int i = 0; i < resDataset.size(); i++)
                {
                    if (StringUtils.equals("1", resDataset.getData(i).getString("RES_TYPE_CODE")))
                    {
                        otherTradeData.setRsrvStr7(resDataset.getData(i).getString("IMSI"));
                    }
                }
            }

            otherTradeData.setUserId(userId);
            otherTradeData.setRsrvValueCode("ROAM");
            otherTradeData.setRsrvValue("国漫优选");
            otherTradeData.setRsrvDate10(SysDateMgr.getSysDate());
            otherTradeData.setRsrvStr2(chooseNetworkRD.getCooperArea());
            otherTradeData.setRsrvStr3(StringUtils.equals(chooseNetworkRD.getOperType(), "01") ? chooseNetworkRD.getCooperNet() : "");
            // pageData.getString("OPER_TYPE").equals("01") ? pageData.getString("COOPER_NET")
            // :pageData.getString("selectCooper")
            otherTradeData.setRsrvStr4(chooseNetworkRD.getOperType());
            otherTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            otherTradeData.setStartDate(SysDateMgr.getSysTime());
            otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
            otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());

            otherTradeData.setInstId(SeqMgr.getInstId());
        }
        else
        {
            String rsrvValueCode = "ROAM";
            IDataset dataset = UserOtherInfoQry.getUserOtherInfo(userId, rsrvValueCode);
            if (IDataUtil.isNull(dataset)||dataset.size()==0)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "为获取到用户订购信息！");
            }
            otherTradeData = new OtherTradeData(dataset.getData(0));
            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
            otherTradeData.setRsrvStr4(chooseNetworkRD.getOperType());
            otherTradeData.setEndDate(chooseNetworkRD.getAcceptTime());
        }

        btd.add(chooseNetworkRD.getUca().getSerialNumber(), otherTradeData);

        return;
    }
    public boolean tcsOneCardmuilNumber(ChooseNetworkReqData reqData,BusiTradeData btd) throws Exception
    {
    	 List<OtherTradeData> otherTD = btd.getTradeDatas(TradeTableEnum.TRADE_OTHER);
    	 IData data = new DataMap();
    	 for (int i = 0; i < otherTD.size(); i++)
         {
             String kindId = "BIP2B145_T2001111_0_0";
             String routeType = "00";
             String routeValue = "000";
             String provinceCode = "8981";
             String opr = otherTD.get(i).getRsrvStr4();
             String serialNumber = btd.getRD().getUca().getSerialNumber();
             String provinceA = "8981";
             String tradeCityCode = CSBizBean.getTradeEparchyCode();
             String outGroupId = "852";
             String outNetType = otherTD.get(i).getRsrvStr3();
             String efftt = otherTD.get(i).getStartDate();
             String imsi = otherTD.get(i).getRsrvStr7();
             data = IBossCall.HKOneCardMuilNumber(imsi,serialNumber,  opr,  routeType,  routeValue,  provinceCode,  kindId,  provinceA,  tradeCityCode,  outGroupId,  outNetType,  efftt);

             //add by zhaohj3 start
             String resultcode="";
  			 String restltdesc="";

             /**
              *
              * 国漫优选反馈结果插表
              */
             if ("0000".equals(data.getString("X_RSPCODE")))
             {
     			resultcode="0";
     			restltdesc="SUCCESS";
     		  } else {
     			resultcode="1000";
     			restltdesc= "FAILED";
     		  }

            if ("6".equals(CSBizBean.getVisit().getInModeCode())) {
            	boolean result=InsertChooseNetwork.insertData(opr,serialNumber,imsi,resultcode,restltdesc);
            	return result;
            }
             //add by zhaohj3 end
         }

        if ("0000".equals(data.getString("X_RSPCODE")))
        {
            return true;
        }
        else
        {
            CSAppException.apperr(BizException.CRM_BIZ_5, data.getString("X_RESULTCODE"), data.getString("X_RESULTINFO"));
            return false;
        }
    }
}
