
package com.asiainfo.veris.crm.order.soa.group.destroygroupuser;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMoList;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser.DestroyAdcGroupUserReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser.DestroyGroupUser;

public class DestroyAdcGroupUser extends DestroyGroupUser
{

    protected DestroyAdcGroupUserReqData reqData = null;

    protected static boolean isOWE = false;// 是否欠费注销

    public DestroyAdcGroupUser() throws Exception
    {
    }

    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);

        String isOweDel = map.getString("DESTROY_ATTR", "");
        if ("OWEFEE".equals(isOweDel))
        {// 集团产品欠费注销
            isOWE = true;
        }
    }

    /**
     * 作用: 生成其它台帐数据（生成台帐后）
     * 
     * @author liaolc 2014-03-13
     * @throws Exception
     */
    @Override
    public void actTradeSub() throws Exception
    {

        super.actTradeSub();

        setUnRegTradeGrpPlatSvc();
        setUnRegGrpMolist();
        
        //处理EC客户信息
        actTrade();
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new DestroyAdcGroupUserReqData();
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (DestroyAdcGroupUserReqData) getBaseReqData();
    }

    /**
     * 作用:处理集团用户上行业务指令信息
     * 
     * @author liaolc 2014-03-17 15:43
     * @throws Exception
     */
    public void setUnRegGrpMolist() throws Exception
    {

        IData params = new DataMap();
        params.put("USER_ID", this.reqData.getUca().getUserId());
        IDataset molist = UserGrpMoList.getGrpMolist(params);

        IData data = null;

        for (int j = 0; j < molist.size(); j++)
        {
            data = molist.getData(j);
            data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());// 标识删除

        }

        addTradeGrpMolist(molist);
    }

    /**
     * 作用:设置集团用户注销时的平台服务表
     * 
     * @author liaolc 2014-03-13 15:40
     * @throws Exception
     */
    private void setUnRegTradeGrpPlatSvc() throws Exception
    {
        String strUserId = reqData.getUca().getUserId();
        String productId = reqData.getUca().getProductId();
        String SerialNumber = reqData.getUca().getSerialNumber();
        IDataset userattrs = UserGrpPlatSvcInfoQry.getUserGrpPlatSvcByUserId(strUserId);
        IData data = null;
        for (int i = 0; i < userattrs.size(); i++)
        {
            data = userattrs.getData(i);
            data.put("END_DATE", getAcceptTime());
            data.put("IS_NEED_PF", "1");// J2EE新增IS_NEED_PF字段表示是否走服务开通，1或者是空： 走服务开通发指令,0：不走服务开通不发指令
            data.put("RSRV_TAG3", "0");// J2EE修改 0 默认值没意义，1 只ADC平台，2 只行业网关，
            // 以后有只发一个平台的产品时，可与 in_mode_code绑定用,服开暂时没取RSRV_TAG3字段;现在用的主表in_mode_code字段,字段值为P 只向网关发送数据 值为G 只向ADC发送数据 其他值
            // adc平台和网关都发送.
            data.put("OPER_STATE", "02"); // 02－终止：终止本业务，J2EE按接口规范
            data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());// 标识为删除

            // if ("6413".equals(productId)) // 企信通彩信版注销时----J2EE没查到这个产品已经删除该逻辑
            // {
            // delGrpOutsn(data.getString("BIZ_IN_CODE"));
            // crateOutnetSnreg(SerialNumber, data.getString("BIZ_IN_CODE", ""));
            // }

        }

        addTradeGrpPlatsvc(userattrs);
    }

    protected String setTradeTypeCode() throws Exception
    {
        if (isOWE)
        {
            return "3152"; // 集团产品欠费注销
        }
        return super.setTradeTypeCode();
    }
    
    /**
     * 处理EC客户信息
     * @throws Exception
     */
    public void actTrade() throws Exception{
        if (("9230").equals(reqData.getUca().getProductId())){
        	//直接存台账 
        	IData trade = bizData.getTrade();
        	trade.put("RSRV_STR1", reqData.getUca().getCustGroup().getProvinceCode());//EC归属省编码
        	IData qryParam = new DataMap();
			qryParam.put("CUST_MANAGER_ID", reqData.getUca().getCustGroup().getCustManagerId());
			IDataset maninfos = Dao.qryByCode("TF_F_CUST_MANAGER_STAFF", "SEL_BY_PK", qryParam, Route.CONN_CRM_CEN);
			if (maninfos.size()>0) {
				trade.put("RSRV_STR2", maninfos.getData(0).getString("SERIAL_NUMBER",""));//EC客户经理电话
			}
        }
    }
}
