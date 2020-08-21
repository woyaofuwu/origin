
package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMember;

public class CreateTDbusinessTeleGroupMember extends CreateGroupMember
{

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();

        IData data = bizData.getTrade();

        data.put("RSRV_STR1", reqData.getGrpUca().getUserId()); // 集团USER_ID
        data.put("RSRV_STR2", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId())); // 关系类型编码
        data.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber()); // 集团SERIAL_NUMBER
        data.put("RSRV_STR8", reqData.getUca().getCustomer().getCustName());
        data.put("RSRV_STR9", reqData.getUca().getAccount().getAcctId());
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
     * @author lixiuyu
     */

    // @chenyi 校验 测试时候再改
    // public boolean validchk(PageData pd, TradeData td, String chkFlag, IData data) throws Exception {
    // super.validchk(pd, td, chkFlag, data);
    //        
    // CSAppEntity dao = new CSAppEntity(pd);
    // String user_id = td.getMemUserInfo().getString("USER_ID");
    // String serialNumber = td.getMemUserInfo().getString("SERIAL_NUMBER");
    //        
    // if (chkFlag.equals("BaseInfo")) {
    // /*//手机号码必须采取157198号段
    // if(!serialNumber.substring(0, 6).equals("157198")){
    // common.warn("用户手机号码不是157198号段，不能加入集团产品！");
    // }*/
    //            
    // IData svccall = new DataMap();
    // svccall.put("USER_ID", user_id);
    // svccall.put("SERVICE_ID", "0");
    // IDataset usersvccall = dao.queryListByCodeCode("TF_F_USER_SVC", "SEL_BY_SERVICE_ID_AFTER_TRADE", svccall);
    // if (usersvccall != null && usersvccall.size() > 0) {
    // }else{
    // common.warn("用户未开通语音通话功能，不能加入集团产品！");
    // }
    //
    // boolean flag = false;
    // IData info = new DataMap();
    // info.put("USER_ID", user_id);
    // IDataset discnts = UserDiscntQry.getByUserIdNow(pd,info);
    // if(null != discnts && discnts.size()>0) {
    // for(int i=0; i<discnts.size();i++) {
    // IData discnt = (IData) discnts.get(i);
    // String discnt_code = discnt.getString("DISCNT_CODE");
    // if("9433".equals(discnt_code) || "9434".equals(discnt_code) || "5920".equals(discnt_code) ||
    // ("1261".equals(discnt_code))) {
    // flag = true;
    // break;
    // }
    // }
    // }
    // if(!flag){
    // common.error("用户未订购TD无线座机（集团）18元套餐[9433]或TD无线座机（家庭）18元套[9434]或无线固话家庭套餐[5920]或神州行户户通座机套餐[1261]，不能加入集团产品！");
    // }
    //            
    //           
    // }
    // return true;
    // }

}
