
package com.asiainfo.veris.crm.order.soa.person.busi.vipsimbak.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.res.ResParaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.vipsimbak.VipSimBakQueryBean;
import com.asiainfo.veris.crm.order.soa.person.busi.vipsimbak.order.requestdata.VipSimBakActReqData;

public class VipSimBakActTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub

        VipSimBakActReqData reqData = (VipSimBakActReqData) btd.getRD();

        String inModeCode = CSBizBean.getVisit().getInModeCode();

        if (!"0".equals(inModeCode))
        {
            VipSimBakQueryBean bean = BeanManager.createBean(VipSimBakQueryBean.class);

            IData params = new DataMap();
            params.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
            params.put("USER_ID", reqData.getUca().getUser().getUserId());

            String simCardNo2 = reqData.getSimCardNo2();
            IDataset vipsimBakResult = bean.getVipSimBakActInfo(params); // 查询备卡信息
            IData vipSimBakInfo = new DataMap();
            boolean flag = false;

            if (StringUtils.isNotBlank(simCardNo2))
            { // 存在要激活的备卡
                for (int i = 0; i < vipsimBakResult.size(); i++)
                {
                    IDataset simcarddataset = vipsimBakResult.getData(i).getDataset("VIP_BAK_INFO");
                    if (simCardNo2.equals(simcarddataset.getData(0).getString("SIM_CARD_NO")))
                    {
                        flag = true;
                        vipSimBakInfo = vipsimBakResult.getData(i);
                    }
                }
            }
            else
            {
                flag = true;
                vipSimBakInfo = vipsimBakResult.getData(0);
            }
            if (!flag)
            {
                CSAppException.apperr(CrmCardException.CRM_CARD_1000);
            }

            reqData.setOldResCode(vipSimBakInfo.getString("RES_CODE"));
            reqData.setNewSimCardNo((vipSimBakInfo.getDataset("VIP_BAK_INFO")).getData(0).getString("SIM_CARD_NO"));
        }

        stringTableTradeMain(btd);

        geneResTrade(btd);

        // 调用资源备卡激活接口
        IDataset backupActivates = ResCall.backupCardActivate(reqData.getOldResCode(), reqData.getNewSimCardNo());
        IData backActivateInfo = backupActivates.getData(0);
        if (!StringUtils.equals("0", backActivateInfo.getString("X_RESULTCODE")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, backActivateInfo.getString("X_RESULTINFO"));
        }
    }

    /**
     * 功能说明：生成资源台帐
     * 
     * @param btd
     * @throws Exception
     */
    protected void geneResTrade(BusiTradeData btd) throws Exception
    {

        VipSimBakActReqData reqData = (VipSimBakActReqData) btd.getRD();

        IDataset userResInfoList = UserResInfoQry.getUserResource(reqData.getUca().getUser().getUserId());

        if (userResInfoList == null || userResInfoList.size() <= 0)
        {
            // common.error("550205:获取用户有效SIM卡信息无数据!");
            CSAppException.apperr(CrmCardException.CRM_CARD_20);
        }

        ResTradeData resTdDel = new ResTradeData(userResInfoList.getData(0));
        resTdDel.setResTypeCode("1");
        resTdDel.setResCode(reqData.getOldResCode());

        resTdDel.setRemark(reqData.getRemark());
        resTdDel.setUserIdA("-1");
        resTdDel.setModifyTag("1");
        resTdDel.setEndDate(reqData.getAcceptTime());
        btd.add(btd.getRD().getUca().getSerialNumber(), resTdDel);
        // ---------------------------------------------------------------
        ResTradeData resTdAdd = new ResTradeData();

        IData simInfo = this.getSimCardInfo(reqData.getNewSimCardNo());
        String resTypeCode = simInfo.getString("RES_TYPE_CODE");
//        String simTypeCode = resTypeCode.substring(1);
        
        IDataset reSet = ResCall.qrySimCardTypeByTypeCode(resTypeCode);
        if (IDataUtil.isNotEmpty(reSet))
        {
            if(StringUtils.equals("01", reSet.getData(0).getString("NET_TYPE_CODE"))){
                resTdAdd.setRsrvTag3("1");// 4G卡
            }else{
                resTdAdd.setRsrvTag3("0");// 23G卡
            }
        }
        else
        {
            resTdAdd.setRsrvTag3("0");// 23G卡
        }

        resTdAdd.setResTypeCode("1");
        resTdAdd.setResCode(reqData.getNewSimCardNo());
        resTdAdd.setInstId(SeqMgr.getInstId());

        resTdAdd.setImsi(simInfo.getString("IMSI"));
        resTdAdd.setKi(simInfo.getString("KI"));
        resTdAdd.setRsrvStr1(simInfo.getString("RES_KIND_CODE"));
        resTdAdd.setRsrvStr2(resTypeCode);
        resTdAdd.setRemark(reqData.getRemark());

        resTdAdd.setUserId(reqData.getUca().getUser().getUserId());
        resTdAdd.setUserIdA("-1");
        resTdAdd.setModifyTag("0");
        resTdAdd.setStartDate(reqData.getAcceptTime());
        resTdAdd.setEndDate(SysDateMgr.END_DATE_FOREVER);

        btd.add(btd.getRD().getUca().getSerialNumber(), resTdAdd);

    }

    protected IData getSimCardInfo(String simCardNo) throws Exception
    {

        IDataset simCardInfo = ResCall.getSimCardInfo("0", simCardNo, "", "");

        if (simCardInfo.size() > 0)
        {
            return (IData) simCardInfo.get(0);
        }
        else
        {
            return new DataMap();
        }

    }

    /**
     * 功能说明：设置台帐某个字段的数据
     * 
     * @param btd
     * @throws Exception
     */
    protected void stringTableTradeMain(BusiTradeData btd) throws Exception
    {

        MainTradeData mainList = btd.getMainTradeData();

        mainList.setOlcomTag(BofConst.OLCOM_TAG_SEND);
        //将对TF_F_CUST_VIPSIMBAK表资料的修改，移到action中处理,在rsrvStr10中记录新SIM卡号
        VipSimBakActReqData reqData = (VipSimBakActReqData) btd.getRD();
        mainList.setRsrvStr10(reqData.getNewSimCardNo());
    }

}
