
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

public class CreateWlanGroupUser extends CreateGroupUser
{

    /**
     * 构造函数
     */
    public CreateWlanGroupUser()
    {

    }

    public void actTradeSub() throws Exception
    {
        // 1- 继承基类处理
        super.actTradeSub();

        // 2- 登记other表，服务开通侧用
        infoRegDataOther();

    }

    /**
     * 其它台帐处理
     */
    public void infoRegDataOther() throws Exception
    {
        // 2- 根据产品编号获取产品参数
        IDataset productParamInfoList = reqData.cd.getProductParamList(reqData.getUca().getProductId());
        if (IDataUtil.isEmpty(productParamInfoList))
        {
            return;
        }
        IDataset lineDataset = new DatasetList();
        if (productParamInfoList != null && productParamInfoList.size() > 0)
        {
            for (int i = 0; i < productParamInfoList.size(); i++)
            {
                
                IData data = new DataMap();
                IData productData = productParamInfoList.getData(i);
                
                if("PAY_APMONEY".equals(productData.getString("ATTR_CODE", "")) 
                        || "PAY_WLMONEY".equals(productData.getString("ATTR_CODE", ""))
                        || "PAY_ONECOST".equals(productData.getString("ATTR_CODE", "")))
                {
                    data.put("RSRV_VALUE_CODE", "GRP_WLAN"); 
                    if("PAY_APMONEY".equals(productData.getString("ATTR_CODE", ""))){
                        data.put("RSRV_STR1", "AP月租费");
                        data.put("RSRV_VALUE", "PAY_APMONEY");
                    } else if("PAY_ONECOST".equals(productData.getString("ATTR_CODE", ""))){
                        data.put("RSRV_STR1", "一次性开通使用费用");
                        data.put("RSRV_VALUE", "PAY_ONECOST");
                    }
                    else
                    {
                        data.put("RSRV_STR1", "带宽月租费");
                        data.put("RSRV_VALUE", "PAY_WLMONEY");
                    }
                    data.put("RSRV_STR2", productData.getString("ATTR_VALUE", ""));
                    data.put("RSRV_STR3", "集团WLAN");
                    data.put("RSRV_STR4", "CrtUs");//操作类型
                    data.put("RSRV_STR5", "3560");//业务类型
                    data.put("RSRV_STR6", "7130");//产品参数
                    data.put("START_DATE", getAcceptTime());
                    data.put("END_DATE", SysDateMgr.getTheLastTime());
                    data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                    data.put("INST_ID", SeqMgr.getInstId());
                    
                    
                    lineDataset.add(data);
                }
            }
            addTradeOther(lineDataset);

        }
    }

    // 校验 等测试再修改 @chenyi

    // public boolean validchk(PageData pd, TradeData td, String chkFlag, IData data) throws Exception {
    // super.validchk(pd, td, chkFlag, data);
    //        
    // if (chkFlag.equals("BaseInfo")) {
    // //权限控制优惠价格
    //            
    // if (!pd.getContext().hasPriv("COMPANY_LED")){
    // IDataset datas = ParamQry.getCommpara(pd, "CSM", "556", "COMPANY_LED", pd.getContext().getEpachyId());
    // if (datas != null && datas.size() > 0) {
    // }else{
    // common.warn("您没有该产品的优惠权限，无法继续办理！");
    // }
    // }else if(!pd.getContext().hasPriv("PROVINCE_LED")){
    // IDataset datas = ParamQry.getCommpara(pd, "CSM", "556", "PROVINCE_LED", pd.getContext().getEpachyId());
    // if (datas != null && datas.size() > 0) {
    // }else{
    // common.warn("您没有该产品的优惠权限，无法继续办理！");
    // }
    // }else if(!pd.getContext().hasPriv("COM_TREE_LED")){
    // IDataset datas = ParamQry.getCommpara(pd, "CSM", "556", "COM_TREE_LED", pd.getContext().getEpachyId());
    // if (datas != null && datas.size() > 0) {
    // }else{
    // common.warn("您没有该产品的优惠权限，无法继续办理！");
    // }
    // }
    // }
    // return true;
    // }
}
