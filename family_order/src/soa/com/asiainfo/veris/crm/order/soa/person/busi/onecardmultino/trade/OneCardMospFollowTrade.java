package com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.requestdata.OneCardMultiNoReqData;

public  class OneCardMospFollowTrade extends BaseTrade implements ITrade
{

    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
    	OneCardMultiNoReqData rd = (OneCardMultiNoReqData) bd.getRD();
        MainTradeData mtd = bd.getMainTradeData();
       
        String rsrvStr5 = "";
        String rsrvStr6 = "";
        String   category=rd.getCategory();
        String  useridB ="";
        
     
        if ("".equals(category)) {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到副号码类型订购关系！");
        }
            if ("1".equals(category))
            {
                 mtd.setSerialNumber(rd.getSerialNumber());//主号MSISDN
            mtd.setUserIdB(category);//副号码类型，字段的最后一位
            mtd.setRsrvStr3(rd.getFlag());//操作类型,06--新增 07--删除
            mtd.setRsrvStr4(rd.getMsisdn());//主号MSISDN
          
            IData snbInfo =new DataMap();
			 snbInfo = UcaInfoQry.qryUserInfoBySn(rd.getMsisdn());
			 useridB = snbInfo.getString("USER_ID");

                //            String snb = rd.getSerial_number_b();
                //            IData snbInfo = UcaInfoQry.qryUserInfoBySn(snb);
                //            String useridB = snbInfo.getString("USER_ID");
                IDataset userVolteB = UserSvcInfoQry.getSvcUserId(useridB, "190");
                if (IDataUtil.isNotEmpty(userVolteB))
                {
                    rsrvStr5 = "FOLLOW_VOLTE";
                    rsrvStr6 = "TRANS_PROV_MAIN";
                }
            }
            mtd.setRsrvStr5(rsrvStr5);
            mtd.setRsrvStr6(rsrvStr6);
        }
   

}
