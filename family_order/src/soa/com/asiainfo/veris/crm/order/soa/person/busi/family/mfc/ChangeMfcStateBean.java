
package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class ChangeMfcStateBean extends GroupBean
{

    private String TradeTypeCode;//TRADE_TYPE_CODE

    private String userId;//虚拟家庭的USER_ID_A
    
    private String main_serial_number;//主号的号码MAIN_SERIAL_NUMBER
    
    private String serial_number_a;//虚拟家庭的号码SERIAL_NUMBER_A

    private String is_send_type = "0";//对账过来的标记 ：1
    
    private String product_code;//产品编码，MFC000001-全国亲情网，MFC000002-全国亲情网（自付版），MFC000003-5G家庭会员群组，MFC000004-G家庭套餐群组，MFC000005-5G融合套餐群组
                                //MFC000006-全国亲情网(支付宝版月包)，MFC000007-全国亲情网(支付宝版季包)，MFC000008-全国亲情网(支付宝版年包)
                                //MFC000009-全国亲情网(异网版月包)，MFC000010-全国亲情网(异网版季包)，MFC000011-全国亲情网(异网版年包) 
    
    private String product_offering_id;//群组编码，对应唯一的家庭网
    
    private String finish_time;//归档时间
    
    private String eff_time;//生效时间
    
    private String exp_time;//失效时间

    /**
     * 生成登记信息
     * 
     * @throws Exception
     */
    @Override
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }

    /**
     * 其它台帐处理-重点
     */
    @Override
    public void actTradeSub() throws Exception
    {
        infoRegDataUser();
        infoRegDataSvc();
//        insertTradeSMS();
    }

    /**
     * 处理用户的服务状态
     * 
     * @param data
     * @throws Exception
     */

    public void infoRegDataSvc() throws Exception
    {

        IDataset svcDatas = new DatasetList();

        IDataset svcState = new DatasetList();
        svcState = UserSvcStateInfoQry.getUserNowSvcStateByUserIdNow(userId, Route.CONN_CRM_CG);

        if (svcState.isEmpty())
        {
            return;
        }
        for (int i = 0, size = svcState.size(); i < size; i++)
        {
            IData temp1 = new DataMap();
            temp1.putAll(svcState.getData(i));
            IData temp = new DataMap();
            temp.putAll(svcState.getData(i));
            temp1.put("END_DATE", SysDateMgr.getSysTime());
            temp1.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            temp.put("START_DATE",SysDateMgr.getSysTime());
            temp.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
            temp.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            temp.put("INST_ID", SeqMgr.getInstId());// 新增一条记录时要新增instid

            if (TradeTypeCode.equals("2584"))
            {
                temp.put("STATE_CODE", "5");
            }
            else if (TradeTypeCode.equals("2585"))
            {
                temp.put("STATE_CODE", "0"); // 信控开机
            }
            else
            {
                return;
            }

            svcDatas.add(temp);
            svcDatas.add(temp1);
        }
        if (svcDatas.size() > 0)
        {
            addTradeSvcstate(svcDatas);
        }

    }

    /**
     * 信控处理用户信息
     * 
     * @param data
     * @throws Exception
     */

    public void infoRegDataUser() throws Exception
    {
        IDataset userDatas = new DatasetList();
        IData userInfos = UcaInfoQry.qryUserInfoByUserId(userId, Route.CONN_CRM_CG);
        if (userInfos.isEmpty())
        {
            return;
        }

        IData userInfo = userInfos;
        if (TradeTypeCode.equals("2584")) // stop 暂停; back 恢复
        {
            userInfo.put("USER_STATE_CODESET", "5"); // 信控停机
            userInfo.put("LAST_STOP_TIME", getAcceptTime());
        }
        else if (TradeTypeCode.equals("2585"))
        {
            userInfo.put("USER_STATE_CODESET", "0"); // 信控开机

        }
        else
        {
            return;
        }
        userInfo.put("MODIFY_TAG", "2");

        userDatas.add(userInfo);

        addTradeUser(userDatas);
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();
    }

    @Override
    protected void makInit(IData data) throws Exception
    {
        super.makInit(data);

        TradeTypeCode = data.getString("TRADE_TYPE_CODE");
        userId = data.getString("USER_ID_A");
        serial_number_a=data.getString("SERIAL_NUMBER_A");
        main_serial_number=data.getString("MAIN_SERIAL_NUMBER");
        is_send_type=data.getString("IS_SEND_TYPE","0");
        product_code=data.getString("PRODUCT_CODE");
        product_offering_id=data.getString("PRODUCT_OFFERING_ID");
        finish_time=data.getString("FINISH_TIME");
        eff_time=data.getString("EFF_TIME");
        exp_time=data.getString("EXP_TIME");
    }

    @Override
    protected void makUca(IData data) throws Exception
    {
        makUcaForGrpNormal(data);
    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        return TradeTypeCode;

    }
    @Override
    protected String setOrderTypeCode() throws Exception
    {
        return "0";
    }
     

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData tradeData = bizData.getTrade();

        tradeData.put("SUBSCRIBE_TYPE", "0");
        tradeData.put("RSRV_STR1",main_serial_number);
        tradeData.put("RSRV_STR6",is_send_type);
        tradeData.put("RSRV_STR2",product_code);
        tradeData.put("RSRV_STR3",product_offering_id);
    }

    @Override
    protected void regOrder() throws Exception
    {
        super.regOrder();
        IData tradeData = bizData.getTrade();

        tradeData.put("SUBSCRIBE_TYPE", "0");
        tradeData.put("RSRV_STR1",main_serial_number);
    }

}
