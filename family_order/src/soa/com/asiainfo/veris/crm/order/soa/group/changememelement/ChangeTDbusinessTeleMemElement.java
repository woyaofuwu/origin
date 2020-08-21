
package com.asiainfo.veris.crm.order.soa.group.changememelement;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;

public class ChangeTDbusinessTeleMemElement extends ChangeMemElement
{
    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();

        IData data = bizData.getTrade();

        data.put("RSRV_STR1", reqData.getGrpUca().getUserId()); // 集团USER_ID
        data.put("RSRV_STR2", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId())); // 关系类型编码
        data.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber()); // 集团SERIAL_NUMBER
        data.put("RSRV_STR4", reqData.getUca().getCustomer().getCustName());

    }

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
    // String serialNumber = td.getMemUserInfo().getString("SERIAL_NUMBER");
    //        
    // if (chkFlag.equals("BaseInfo")) {
    // //手机号码必须采取157198号段
    // if(!serialNumber.substring(0, 6).equals("157198")){
    // common.warn("用户手机号码不是157198号段，不能加入集团！");
    // }
    //            
    // IData svccall = new DataMap();
    // svccall.put("USER_ID", user_id);
    // svccall.put("SERVICE_ID", "0");
    // IDataset usersvccall = dao.queryListByCodeCode("TF_F_USER_SVC", "SEL_BY_SERVICE_ID_AFTER_TRADE", svccall);
    // if (usersvccall != null && usersvccall.size() > 0) {
    // }else{
    // common.warn("用户未开通语音通话功能，不能加入集团！");
    // }
    // }
    // return true;
    // }
}
