
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetspamchangespeed;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;
import org.apache.log4j.Logger;

public class WidenetSPAMChangeSpeedBean extends CSBizBean
{
    public static final Logger logger=Logger.getLogger(WidenetSPAMChangeSpeedBean.class);

    /**
     * 300M免费提速包，到期时，宽带速率降回原速率。
     * @return
     * @throws Exception
     */
    public IData revertFTTHrate() throws Exception
    {
        logger.debug("==WidenetSPAMChangeSpeedBean==revertFTTHrate==");
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0");
        //查询到期用户，依次调用接口降速（调用成功应该修改优惠表记录，标识降速成功）
        IDataset widenetSPAMChangeSpeedExpireList = qryWidenetSPAMChangeSpeedExpireList();
        if(IDataUtil.isNotEmpty(widenetSPAMChangeSpeedExpireList)) {
            for (int i = 0; i < widenetSPAMChangeSpeedExpireList.size(); i++) {
                IData changeSpeedExpireListData = widenetSPAMChangeSpeedExpireList.getData(i);
                logger.debug("==WidenetSPAMChangeSpeedBean==changeSpeedExpireListData=="+changeSpeedExpireListData);
                if(IDataUtil.isNotEmpty(changeSpeedExpireListData)) {
                    String serialNumber = changeSpeedExpireListData.getString("SERIAL_NUMBER");
                    IDataset kduserinfo = UserInfoQry.getUserinfo("KD_"+serialNumber);
                    if(IDataUtil.isNotEmpty(kduserinfo)) {
                        String kdUserId = kduserinfo.getData(0).getString("USER_ID");
                        String presRate = WideNetUtil.getWidenetUserRate(serialNumber);
                        String oldPresRate = "307200";
                        String tradeId = "20200630" + serialNumber;//给服开的tradeId不重要，可以区分就行
                        changeResMethod(tradeId, kdUserId, "KD_" + serialNumber, presRate, oldPresRate);//调用服开接口降速

                        //调用成功应该修改优惠表记录，标识降速成功
                        IData param = new DataMap();
                        param.put("USER_ID", changeSpeedExpireListData.getString("USER_ID"));
                        param.put("DISCNT_CODE", "80176874");
                        param.put("RSRV_TAG1", "Y");//表示降回原速率
                        Dao.executeUpdateByCodeCode("TF_F_USER_DISCNT", "UPD_TAG_BY_USERID_DISCNTCODE", param);
                    }
                }
            }
        }

        return result;
    }

    /**
     * 调用服开接口降速
     * @param TRADE_ID
     * @param USER_ID
     * @param SERIAL_NUMBER
     * @param PRES_RATE
     * @param OLD_PRES_RATE
     */
    private void changeResMethod(String TRADE_ID ,String USER_ID,String SERIAL_NUMBER ,String PRES_RATE,String OLD_PRES_RATE){
        IData inParam = new DataMap();
        inParam.put("TRADE_ID", TRADE_ID);
        inParam.put("USER_ID", USER_ID);
        inParam.put("SERIAL_NUMBER", SERIAL_NUMBER);
        inParam.put("PRES_RATE", PRES_RATE);
        inParam.put("OLD_PRES_RATE", OLD_PRES_RATE);
        try {
            CSAppCall.callNGPf("PF_ORDER_CHANGERES", inParam);
            logger.debug("==WidenetSPAMChangeSpeedBean==调用接口号码："+SERIAL_NUMBER);
        } catch (Exception e) {
            logger.error("==WidenetSPAMChangeSpeedBean==调用接口号码："+SERIAL_NUMBER + ";异常信息："+ e);
        }
    }

    /**
     * 查询到期用户
     * @return
     * @throws Exception
     */
    private IDataset qryWidenetSPAMChangeSpeedExpireList()throws Exception {
        IData param = new DataMap();
        return Dao.qryByCode("TF_F_USER_DISCNT", "QRY_WIDENET_SPAM_EXPIRE", param);
    }
}
