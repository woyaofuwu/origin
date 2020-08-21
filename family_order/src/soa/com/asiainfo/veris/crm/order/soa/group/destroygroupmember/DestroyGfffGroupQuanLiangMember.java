
package com.asiainfo.veris.crm.order.soa.group.destroygroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyGroupMember;

public class DestroyGfffGroupQuanLiangMember extends DestroyGroupMember
{

    public DestroyGfffGroupQuanLiangMember()
    {

    }

    /**
     * 生成登记信息
     * 
     * @throws Exception
     */
    public void actTradeBefore() throws Exception
    {

        super.actTradeBefore();

    }

    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception
    {

        super.actTradeSub();

        infoRegDataSpecial();
        
    }

    /**
     * 
     * @throws Exception
     */
    private void infoRegDataSpecial() throws Exception
    {
        String userid = reqData.getUca().getUserId();
        
        IData mebcenpayParam = new DataMap();
        mebcenpayParam.put("USER_ID", userid);
        mebcenpayParam.put("MP_GROUP_CUST_CODE", reqData.getGrpUca().getUserId());
        mebcenpayParam.put("PRODUCT_OFFER_ID", reqData.getGrpUca().getProductId());
        mebcenpayParam.put("OPER_TYPE", "3");//指定用户，全量统付
        
        IDataset mebCenPayInfoDataset = UserGrpInfoQry.queryMebCenPayInfoByUserIdAll(mebcenpayParam);
        if (IDataUtil.isNotEmpty(mebCenPayInfoDataset))
        {
            for (int i = 0, sizeI = mebCenPayInfoDataset.size(); i < sizeI; i++)
            {
                IData mebCenpay = mebCenPayInfoDataset.getData(i);

                mebCenpay.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                mebCenpay.put("END_DATE", SysDateMgr.getSysTime());
            }
        }
        
        this.addTradeMebCenpay(mebCenPayInfoDataset);
        
    }
    
    
    protected void initReqData() throws Exception
    {
        super.initReqData();

    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

    }

    protected void regTrade() throws Exception
    {
        super.regTrade();
    }

}
