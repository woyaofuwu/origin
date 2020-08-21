
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeimspasswd;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DESUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;

public class ChangeIMSpasswdBean extends MemberBean
{
    protected ChangeIMSpasswdReqData reqData = null;

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author xiajj
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        insTradeDataForIms();

        infoRegDataOther();
    }

    protected void chkTradeBefore(IData map) throws Exception
    {

    }

    protected BaseReqData getReqData() throws Exception
    {
        return new ChangeIMSpasswdReqData();
    }

    /**
     * 处理台帐Other子表的数据,用于发指令
     * 
     * @param Datas
     * @author liaoyi
     * @throws Exception
     */
    public void infoRegDataOther() throws Exception
    {
        IDataset dataset = new DatasetList();
        IData dataOther = new DataMap();
        boolean ecfetionTag = reqData.getECFETION_TAG();

        dataOther.put("USER_ID", reqData.getUca().getUserId());
        dataOther.put("RSRV_VALUE_CODE", "HSS");
        dataOther.put("RSRV_VALUE", "IMS密码重置HSS服务");

        dataOther.put("RSRV_STR9", "8172");// 用于服务开通，service_id
        dataOther.put("RSRV_STR10", "passwd");// HSS_SP_TEMPLATE_ID 101用于一号通，100用于多媒体电话
        dataOther.put("OPER_CODE", "08");// 用于服务开通，修改用08

        // 如果用户为企业飞信用户，服务编码修改为企业飞信的
//        if (ecfetionTag)
//        {
//            dataOther.put("RSRV_STR9", ECFetionConstants.HSS_SERVICE_ID);
//        }

        //dataOther.put("RSRV_STR12", "221");// HSS_SP_SIFC
        //dataOther.put("RSRV_STR20", "101");// HSS_SPIFC_TEMPLATE_ID

        dataOther.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        dataOther.put("START_DATE", getAcceptTime());
        dataOther.put("END_DATE", SysDateMgr.getTheLastTime());
        dataOther.put("INST_ID", SeqMgr.getInstId());
        dataset.add(dataOther);

        addTradeOther(dataset);
    }

    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (ChangeIMSpasswdReqData) getBaseReqData();
    }

    /**
     * impu台帐表密码修改
     * 
     * @author lixin
     * @param td
     * @throws Exception
     */
    public void insTradeDataForIms() throws Exception
    {
        IData impuData = reqData.getIMPUINFO();

        if (IDataUtil.isNotEmpty(impuData))
        {
            IDataset dataset = new DatasetList();

            // 暂时不加密，跟老系统保持一致，加密密码字段长度不够用
            String ims_passwd = reqData.getUser_passwd2(); // 根据userId 随机码 加密
            
            if(StringUtils.isNotBlank(ims_passwd)){
            	ims_passwd = DESUtil.encrypt(ims_passwd);//加密。 服开再解密
            }
            impuData.put("IMS_PASSWORD", ims_passwd); // IMS门户网站密码
            impuData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            dataset.add(impuData);
            addTradeImpu(dataset);
        }
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        reqData.setIMPUINFO(map.getData("IMPUINFO"));
        reqData.setECFETION_TAG(map.getBoolean("ECFETION_TAG"));
        reqData.setUser_passwd2(map.getString("USER_PASSWD2"));
    }

    protected void makUca(IData map) throws Exception
    {
        makUcaForMebNormal(map);
    }

    protected void regTrade() throws Exception
    {
        super.regTrade();
    }

    protected String setTradeTypeCode() throws Exception
    {
        // 业务类型--IMS成员鉴权密码重置
        return "3611";
    }
}
