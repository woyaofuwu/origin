
package com.asiainfo.veris.crm.order.soa.group.changememelement;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;

public class ChangeSwbsimMemElement extends ChangeMemElement
{

    protected void regTrade() throws Exception
    {
        super.regTrade();

        IData data = bizData.getTrade();

        data.put("RSRV_STR1", reqData.getGrpUca().getUserId()); // 集团USER_ID
        data.put("RSRV_STR2", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId())); // 关系类型编码
        data.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber()); // 集团SERIAL_NUMBER
        data.put("RSRV_STR8", reqData.getUca().getCustomer().getCustName());
        data.put("RSRV_STR9", reqData.getUca().getAcctId());

    }

    // @chenyi 验证测试在做
    /**
     * 验证函数
     * 
     * @param pd
     * @param step
     *            当前步骤
     * @param data
     *            数据
     * @return (true:正确 false:错误)
     * @author xiajj
     */
    // public boolean validchk(PageData pd, TradeData td, String chkFlag, IData data) throws Exception {
    // super.validchk(pd, td, chkFlag, data);
    //        
    // CSAppEntity dao = new CSAppEntity(pd);
    // String user_id = td.getMemUserInfo().getString("USER_ID");
    //        
    // if (chkFlag.equals("BaseInfo")) {
    //            
    // IData svcfax = new DataMap();
    // svcfax.put("USER_ID", user_id);
    // svcfax.put("SERVICE_ID", "46");
    // IDataset usersvcfax = dao.queryListByCodeCode("TF_F_USER_SVC", "SEL_BY_SERVICE_ID_AFTER_TRADE", svcfax);
    // if (usersvcfax != null && usersvcfax.size() > 0) {
    // }else{
    // common.warn("用户未开通传真服务功能，不能加入集团！");
    // }
    // }
    // return true;
    // }
}
