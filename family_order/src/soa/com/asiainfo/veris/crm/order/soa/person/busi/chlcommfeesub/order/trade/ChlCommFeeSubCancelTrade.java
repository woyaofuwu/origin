/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.chlcommfeesub.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.chlcommfeesub.order.requestdata.ChlCommFeeSubCancelRequestData;

/**
 * @CREATED by gongp@2014-4-15 修改历史 Revision 2014-4-15 下午03:03:32
 */
public class ChlCommFeeSubCancelTrade extends BaseTrade implements ITrade
{

    /*
     * (non-Javadoc)
     */
    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        // TODO Auto-generated method stub

        ChlCommFeeSubCancelRequestData reqData = (ChlCommFeeSubCancelRequestData) bd.getRD();

        String phoneSub = reqData.getPhoneSub();// 话音补贴
        String newTrade = reqData.getNewTrade();// 新业务补贴
        String custType = reqData.getCustType();// 客户类型

        IDataset paramDatas = CommparaInfoQry.getCommparaAllCol("CSM", "655", "CHL_COMM", "0898");

        if (IDataUtil.isEmpty(paramDatas))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未配置渠道通讯费补贴管理参数!");
        }

        IData paramData = paramDatas.getData(0);

        if ("1".equals(phoneSub))
        {
            String rsrvStr6 = null;
            if ("0".equals(custType))
            {
                rsrvStr6 = paramData.getString("PARA_CODE3");// 1402
            }
            else if ("1".equals(custType) || "2".equals(custType))
            {
                rsrvStr6 = paramData.getString("PARA_CODE2");// 1401
            }
            if (rsrvStr6 != null)
            {
                IDataset dataset = UserOtherInfoQry.getUserOtherByRsrvstr6(reqData.getUca().getUserId(), rsrvStr6, "CHNL");
                if (dataset.size() > 0)
                {
                    genUserOtherTrade(bd, reqData, dataset.getData(0));
                }
            }
        }

        if ("1".equals(newTrade))
        {
            IDataset dataset = UserOtherInfoQry.getUserOtherByRsrvstr7(reqData.getUca().getUserId(), paramData.getString("PARA_CODE4"), "CHNL");// 1403
            if (dataset.size() > 0)
            {
                genUserOtherTrade(bd, reqData, dataset.getData(0));
            }
        }

        if ("0".equals(custType))
        {
            if ("1".equals(phoneSub))
            {
                genUserDiscntTrade(bd, reqData, paramData.getString("PARA_CODE3"));// 1402
            }
        }
        else if ("1".equals(custType) || "2".equals(custType))
        {
            if ("1".equals(phoneSub))
            {
                genUserDiscntTrade(bd, reqData, paramData.getString("PARA_CODE2"));// 1401
            }
        }

        if ("1".equals(newTrade))
        {
            genUserDiscntTrade(bd, reqData, paramData.getString("PARA_CODE4"));// 1403
            genUserDiscntTrade(bd, reqData, paramData.getString("PARA_CODE5"));// 4807
        }
    }

    private void genUserDiscntTrade(BusiTradeData bd, ChlCommFeeSubCancelRequestData reqData, String discnt_code) throws Exception
    {

        List<DiscntTradeData> list = reqData.getUca().getUserDiscntByDiscntId(discnt_code);

        if (list.size() < 1)
        {
            return;
        }

        DiscntTradeData discntTD = list.get(0);

        discntTD.setModifyTag("1");
        discntTD.setEndDate(SysDateMgr.getLastDateThisMonth());

        bd.add(reqData.getUca().getSerialNumber(), discntTD);

    }

    private void genUserOtherTrade(BusiTradeData bd, ChlCommFeeSubCancelRequestData reqData, IData userOtherData) throws Exception
    {

        OtherTradeData otherTD = new OtherTradeData(userOtherData);

        otherTD.setModifyTag("1");
        otherTD.setEndDate(SysDateMgr.getLastDateThisMonth());

        bd.add(reqData.getUca().getSerialNumber(), otherTD);
    }

}
