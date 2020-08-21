
package com.asiainfo.veris.crm.order.web.frame.csview.group.querygroupinfo;

import java.util.Map;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.server.hessian.wade3tran.Wade3DataTran;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

/**
 * 2014-6_13 bboss业务冲抵
 * 
 * @author chenyi
 */
public abstract class BBossTask extends CSBasePage
{

    public void sendHttpStr(IRequestCycle cycle) throws Exception
    {

        IData param = new DataMap();
        IDataset merchOrderIdset = CSViewCall.call(this, "CS.SeqMgrSVC.getBBossMerchIdForGrp", param);
        String merchOrderId = merchOrderIdset.getData(0).getString("seq_BBOSS_merch_id");// 商品订单号

        IDataset productOderIdset = CSViewCall.call(this, "CS.SeqMgrSVC.getBBossProductIdForGrp", param);
        String productOderId = productOderIdset.getData(0).getString("seq_BBOSS_product_id");// 产品订单号

        String customerNumber = getData().getString("grpNum");// 全网集团编码
        String number400 = "4001" + productOderId.substring(10);// 400号码冲量时不允许重复,4001开头，后6位随机
        String flow400 = productOderId;// 400流水号

        // 组BBOSS业务冲抵的串
        String str = "{RSRV_STR2=[\"\"], PRODUCT_ORDER_CHARGE_VALUE=[[]], RSRV_STR1=[\"01114001\"], IN_MODE_CODE=[\"0\"], POATTACHMENT=[\"\"], RSRV_STR4=[\"\"], RSRV_STR3=[\"1\"], PR_MN_OPERATE_CODE=[\"\"], RSRV_STR8=[[\"158\"]], RSRV_STR9=[[[]]], KIND_ID=[\"BIP4B255_T4011004_0_0\"], ROUTE_EPARCHY_CODE=[\"0898\"], PSUBSCRIBE_ID=[\""
                + productOderId
                + "\"], X_TRANS_TYPE=[\"HTTP\"], ACTION_CV2=[[\"1\"]], CGROUP=[[\"\", \"\", \"\", \"\", \"\", \"\", \"\"]], PROVINCE_CODE=[\"HAIN\"], SI_BIZ_MODE=[\"5\"], TRADE_DEPART_ID=[\"36601\"], X_TRANS_CODE=[\"IBOSS\"], RSRV_STR14=[[\"1\"]], RSRV_STR15=[[\"4115011022\", \"4115017001\", \"4115017007\", \"4115017008\", \"4115017030\", \"4115017031\", \"4115017032\"]], ROUTEVALUE=[\"000\"], RSRV_STR16=[[\"VOICE\", \""
                + number400
                + "\", \""
                + flow400
                + "\", \"139@139.com\", \"0\", \"0\", \"3\"]], SOURCE_ID=[\"0\"], RSRV_STR17=[[\"业务承载方式\", \"400号码\", \"预占单流水号\", \"管理员邮箱\", \"网站上传短信白名单权限\", \"主叫号码判别规则\", \"目的地号码数量\"]], RSRV_STR18=[[\"1\", \"1\", \"1\", \"1\", \"1\", \"1\", \"1\"]], TRADE_EPARCHY_CODE=[\"0898\"], TRADE_CITY_CODE=[\"0898\"], RSRV_STR10=[[[]]], RSRV_STR11=[[[]]], PRSRV_STR10=[\"411501\"], ROUTETYPE=[\"00\"], PRODUCT_ORDER_CHARGE_CODE=[[]], EC_SERIAL_NUMBER=[\""
                + customerNumber
                + "\"], TRADE_DEPART_PASSWD=[\"\"], PROVINCE=[\"898\"], OPERA_TYPE=[\"1\"], SUBSCRIBE_ID=[\""
                + merchOrderId
                + "\"], TRADE_STAFF_ID=[\"SUPERUSR\"], CONTACTOR_NAME=[\"施卓\", \"施卓\"], CONTACTOR_TYPE=[\"2\", \"5\"], CONTACTOR_PHONE=[\"13907652424\", \"13907652424\"]}";

        Map map = Wade3DataTran.strToMap(str);

        IData httpStr = Wade3DataTran.wade3To4DataMap(map);
        IDataset result = CSViewCall.call(this, "CS.BBossTaskSVC.callIBOSS", httpStr);
        
        IData data = new DataMap();
        if (IDataUtil.isEmpty(result) || !("00".equals(result.getData(0).getString("X_RSPCODE", "")) || "99".equals(result.getData(0).getString("X_RSPCODE", ""))))
        {
            // 执行失败
            data.put("FLAG", "false");
        }
        else
        {
            data.put("FLAG", "true");
        }
        this.setAjax(data);

    }

    public abstract void setInfo(IData info);

}
