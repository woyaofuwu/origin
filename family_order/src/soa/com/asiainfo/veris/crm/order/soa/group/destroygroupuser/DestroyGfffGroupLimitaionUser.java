
package com.asiainfo.veris.crm.order.soa.group.destroygroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser.DestroyGroupUser;

/**
 * 集团流量自由充产品、指定用户，限量统付（流量池）
 * 
 * @author 
 */
public class DestroyGfffGroupLimitaionUser extends DestroyGroupUser
{
    public DestroyGfffGroupLimitaionUser()
    {

    }
    
    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        
        checkGrpEsop();
        
        infoGrpCenPay();
        
    }
    
    /**
     * 
     * @throws Exception
     */
    private void checkGrpEsop() throws Exception 
    {
    	IDataset eos = reqData.getEos();
        if (IDataUtil.isEmpty(eos))
        {
        	CSAppException.apperr(GrpException.CRM_GRP_909);
        }        
    }

    private void infoGrpCenPay() throws Exception
    {
        String userid = reqData.getUca().getUserId();

        IDataset grpCenPayInfoDataset = UserGrpInfoQry.queryGrpCenPayInfo(userid);

        if (IDataUtil.isNotEmpty(grpCenPayInfoDataset))
        {
            for (int i = 0, sizeI = grpCenPayInfoDataset.size(); i < sizeI; i++)
            {
                IData grpCenpay = grpCenPayInfoDataset.getData(i);

                grpCenpay.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                //grpCenpay.put("END_DATE", SysDateMgr.getSysTime());
                grpCenpay.put("END_DATE", SysDateMgr.getLastDateThisMonth());
            }
        }
        
        this.addTradeGrpCenpay(grpCenPayInfoDataset);

    }
    
    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        
        // makReqDataElement();
        
    }
    
//    private void makReqDataElement() throws Exception{
//        
//        IDataset userDiscntList = reqData.cd.getDiscnt();
//        
//        if (IDataUtil.isNotEmpty(userDiscntList)){
//            for (int i = 0, iSize = userDiscntList.size(); i < iSize; i++)
//            {
//                IData userDiscntData = userDiscntList.getData(i); // 取每个元素
//                if ("D".equals(userDiscntData.getString("ELEMENT_TYPE_CODE")) &&
//                        "73440002".equals(userDiscntData.getString("PACKAGE_ID"))
//                        && TRADE_MODIFY_TAG.DEL.getValue().equals(userDiscntData.getString("MODIFY_TAG"))){ // 优惠
//                    userDiscntData.put("END_DATE", SysDateMgr.getLastDateThisMonth());
//                }
//            }
//        }
//    }
  
    @Override
    protected void setTradeDiscnt(IData map) throws Exception
    {
    	super.setTradeDiscnt(map);
    	IDataset packageInfos = UPackageElementInfoQry.getPackageElementInfoByPackageId("73440002");
    	if (IDataUtil.isNotEmpty(DataHelper.filter(packageInfos, "ELEMENT_ID="+map.getString("DISCNT_CODE"))) && TRADE_MODIFY_TAG.DEL.getValue().equals(map.getString("MODIFY_TAG"))){ // 优惠
    		map.put("END_DATE", SysDateMgr.getLastDateThisMonth());
        }
    }
    
    @Override
    protected void setTradeAttr(IData map) throws Exception
    {
    	if ("7342".equals(map.getString("ATTR_CODE")) &&
                TRADE_MODIFY_TAG.DEL.getValue().equals(map.getString("MODIFY_TAG"))){ // 属性
    		map.put("END_DATE", SysDateMgr.getLastDateThisMonth());
        }
    }
    
    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();

        IData data = bizData.getTrade();
        data.put("EXEC_TIME", SysDateMgr.getTheLastTime());
    }
    
    @Override
    protected void chkTradeAfter() throws Exception
    {
        super.chkTradeAfter();
        
        IData orderData = bizData.getOrder();
        orderData.put("EXEC_TIME", SysDateMgr.getTheLastTime());
    }
    
}
