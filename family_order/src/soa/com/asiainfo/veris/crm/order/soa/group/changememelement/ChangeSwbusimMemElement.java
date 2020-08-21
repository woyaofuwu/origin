
package com.asiainfo.veris.crm.order.soa.group.changememelement;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;

public class ChangeSwbusimMemElement extends ChangeMemElement
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

    // @chenyi 函数验证 测试再调
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
    // IData svccall = new DataMap();
    // svccall.put("USER_ID", user_id);
    // svccall.put("SERVICE_ID", "0");
    // IDataset usersvccall = dao.queryListByCodeCode("TF_F_USER_SVC", "SEL_BY_SERVICE_ID_AFTER_TRADE", svccall);
    // if (usersvccall != null && usersvccall.size() > 0) {
    // IData svcgprs = new DataMap();
    // svcgprs.put("USER_ID", user_id);
    // svcgprs.put("SERVICE_ID", "22");
    // IDataset usersvcgprs = dao.queryListByCodeCode("TF_F_USER_SVC", "SEL_BY_SERVICE_ID_AFTER_TRADE", svcgprs);
    // if (usersvcgprs != null && usersvcgprs.size() > 0) {
    // }else{
    // common.warn("用户未开通GPRS功能，不能加入集团！");
    // }
    // }else{
    // common.warn("用户未开通语音通话功能，不能加入集团！");
    // }
    // }
    // return true;
    // }
}
