
package com.asiainfo.veris.crm.order.soa.person.busi.changephone.order.trade;

import java.util.List;

import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ImpuTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.simcard.WriteCardBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAltsnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changephone.order.requestdata.ChangePhonePreRegisterRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.SimCardBean;

public class ChangePhonePreRegisterReginTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        ChangePhonePreRegisterRequestData changePhoneReqData = (ChangePhonePreRegisterRequestData) btd.getRD();
        // 激活
        if (!"02".equals(changePhoneReqData.getOpr_code()))
        {
            geneTradeRes(btd);
            genPlatTrade(btd);
            geneTradeDiscnt(btd);
            geneTcsi(btd);
        }
        // 取消
        else
        {
            removeTradeDiscnt(btd);
        }
        geneMain(btd);
    }

    private void geneMain(BusiTradeData btd) throws Exception
    {
        ChangePhonePreRegisterRequestData changePhoneReqData = (ChangePhonePreRegisterRequestData) btd.getRD();

        MainTradeData tradeData = btd.getMainTradeData();
        tradeData.setRsrvStr6(changePhoneReqData.getNewSerialNum());
        tradeData.setRsrvStr7(changePhoneReqData.getOldSerialNum());
        tradeData.setRsrvStr8(changePhoneReqData.getPsptId());
    }

    protected String genePlatDealTag(String svc_id, BusiTradeData btd) throws Exception
    {

        ChangePhonePreRegisterRequestData changePhoneReqData = (ChangePhonePreRegisterRequestData) btd.getRD();
        String serial_number = changePhoneReqData.getOldSerialNum();

        IData pParam = new DataMap();
        pParam.put("SUBSYS_CODE", "CSM");
        pParam.put("PARAM_ATTR", "8000");
        pParam.put("PARAM_CODE", "PLAT_CODE_ALT");
        pParam.put("PARA_CODE1", svc_id);
        pParam.put("EPARCHY_CODE", "ZZZZ");
        IDataset dsParam = CommparaInfoQry.getCommparaByCode1("CSM", "8000", "PLAT_CODE_ALT", svc_id, null);

        if ((dsParam == null) || dsParam.size() < 1)
        {
            return "-";
        }
        String platCommId = dsParam.getData(0).getString("PARA_CODE2", "");
        IData pReg = new DataMap();
        pReg.put("SERIAL_NUMBER", serial_number);
        pReg.put("RELA_TYPE", "2");

        IDataset dsReg = UserAltsnInfoQry.selPlatInfoBySn(pReg);
        if ((dsReg == null) || dsReg.size() < 1)
        {
            return "-";
        }
        for (int i = 0; i < dsReg.size(); i++)
        {
            if (platCommId.equals(dsReg.getData(i).getString("PLAT_SVC_ID", "")))
                return "+";
        }
        return "-";
    }

    private void geneTcsi(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        ChangePhonePreRegisterRequestData changePhoneReqData = (ChangePhonePreRegisterRequestData) btd.getRD();
        String serial_number = changePhoneReqData.getUca().getUser().getSerialNumber();

        OtherTradeData otherTD = new OtherTradeData();
        otherTD.setRsrvValueCode("CHG_PHONE_TCSI_TAG");
        otherTD.setStartDate(SysDateMgr.getSysTime());
        otherTD.setUserId(changePhoneReqData.getUca().getUser().getUserId());
        otherTD.setEndDate(SysDateMgr.END_TIME_FOREVER);
        otherTD.setRsrvValue("改号签约标记");
        otherTD.setModifyTag("0");
        otherTD.setInstId(SeqMgr.getInstId());

        btd.add(serial_number, otherTD);

    }

    private void geneTradeDiscnt(BusiTradeData btd) throws Exception
    {
        ChangePhonePreRegisterRequestData changePhoneReqData = (ChangePhonePreRegisterRequestData) btd.getRD();
        String serial_number = changePhoneReqData.getUca().getUser().getSerialNumber();

        //IDataset userdiscntInfos = UserDiscntInfoQry.getAllValidDiscntByUserId(changePhoneReqData.getUca().getUser().getUserId());
        IDataset commInfos = CommparaInfoQry.getCommNetInfo("CSM", "8000", "ALT_ZERO_DISCNT");
        if (!IDataUtil.isEmpty(commInfos))
        {
            
            String discnt_code = commInfos.getData(0).getString("PARA_CODE1", "");
            if (!StringUtils.isBlank(discnt_code)) {
                DiscntTradeData discntTD1 = new DiscntTradeData();
                
                discntTD1.setUserId(changePhoneReqData.getUca().getUserId());
                
                discntTD1.setUserIdA("-1");
                discntTD1.setProductId("-1");
                discntTD1.setPackageId("-1");
                discntTD1.setElementId(discnt_code);
                discntTD1.setRelationTypeCode("");
                discntTD1.setSpecTag("0");     
                //discntTD1.setCampnId(userdiscntInfos.getData(0).getString("CAMPN_ID"));
                discntTD1.setInstId(SeqMgr.getInstId());
                discntTD1.setStartDate(SysDateMgr.getSysTime());
                discntTD1.setEndDate(SysDateMgr.END_TIME_FOREVER);
                discntTD1.setModifyTag("0");
                discntTD1.setRemark("改号激活落地方资费归0新增优惠");
                btd.add(serial_number, discntTD1);
            }
            
            

        }
    }
    
    private void removeTradeDiscnt(BusiTradeData btd) throws Exception
    {
        String discnt_code = "";
        IDataset commInfos = CommparaInfoQry.getCommNetInfo("CSM", "8000", "ALT_ZERO_DISCNT");
        if (!IDataUtil.isEmpty(commInfos))
        {
            discnt_code = commInfos.getData(0).getString("PARA_CODE1", "");
            if (!StringUtils.isEmpty(discnt_code)) {
                // TODO Auto-generated method stub
                ChangePhonePreRegisterRequestData changePhoneReqData = (ChangePhonePreRegisterRequestData) btd.getRD();
                DiscntTradeData discntTD = new DiscntTradeData();
                String serial_number = changePhoneReqData.getUca().getUser().getSerialNumber();

                IDataset userdiscntInfos = UserDiscntInfoQry.getAllValidDiscntByUserId(changePhoneReqData.getUca().getUser().getUserId());

               if (!IDataUtil.isEmpty(userdiscntInfos))
                {
                   for (int i = 0; i < userdiscntInfos.size(); i++)
                   {
                       IData userdiscntInfo = userdiscntInfos.getData(i);
                       if (StringUtils.equals(discnt_code, userdiscntInfo.getString("DISCNT_CODE"))) {
                           discntTD.setUserId(userdiscntInfo.getString("USER_ID"));
                           discntTD.setUserIdA(userdiscntInfo.getString("USER_ID_A", "-1"));
                           discntTD.setProductId(userdiscntInfo.getString("PRODUCT_ID"));
                           discntTD.setPackageId(userdiscntInfo.getString("PACKAGE_ID"));
                           discntTD.setElementId(userdiscntInfo.getString("DISCNT_CODE"));
                           discntTD.setSpecTag(userdiscntInfo.getString("SPEC_TAG"));
                           discntTD.setRelationTypeCode(userdiscntInfo.getString("RELATION_TYPE_CODE"));
                           discntTD.setCampnId(userdiscntInfo.getString("CAMPN_ID"));
                           discntTD.setInstId(SeqMgr.getInstId());
                           discntTD.setStartDate(userdiscntInfo.getString("START_DATE"));
                           discntTD.setEndDate(SysDateMgr.getLastDateThisMonth());
                           discntTD.setModifyTag("1");
                           discntTD.setRemark("改号激活落地方资费归0删除优惠");
                           btd.add(serial_number, discntTD);
                       }
                   }

                }

            }
        }
    }

    private void geneTradeRes(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        ChangePhonePreRegisterRequestData changePhoneReqData = (ChangePhonePreRegisterRequestData) btd.getRD();
        ResTradeData resTD = new ResTradeData();
        String serial_number = changePhoneReqData.getOldSerialNum();




        String imsi = "";
/*        changePhoneReqData.getUca().getUser().setSerialNumber(serial_number);
        changePhoneReqData.getUca().getUser().setUserId(user_id);*/
        IDataset resInfo = UserResInfoQry.getUserResInfoByUserId(btd.getRD().getUca().getUserId());
        IData resOld = new DataMap();
        if (IDataUtil.isEmpty(resInfo))
        {
            // 没有取到落地方号码的资源信息：
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有取到落地方号码的资源信息！");
        }
        else
        {
            for (int i = 0; i < resInfo.size(); i++)
            {
                if ("1".equals(resInfo.getData(i).getString("RES_TYPE_CODE", "")))
                {
                    resOld.putAll(resInfo.getData(i));
                }
            }
        }
        imsi = resOld.getString("IMSI");
        resTD.setUserId(changePhoneReqData.getUca().getUserId());
        resTD.setUserIdA(resOld.getString("USER_ID_A", "-1"));
        resTD.setResCode(resOld.getString("RES_CODE"));
        resTD.setResTypeCode("1");
        resTD.setInstId(resOld.getString("INST_ID"));
        resTD.setImsi(imsi);
        resTD.setKi(resOld.getString("KI"));
        resTD.setStartDate(resOld.getString("START_DATE"));
        resTD.setEndDate(SysDateMgr.getSysTime());
        resTD.setModifyTag("1");
        resTD.setRemark(resOld.getString("REMARK"));
        resTD.setUserIdA("-1");

        btd.add(serial_number, resTD);

        ResTradeData resTD1 = new ResTradeData();
        IData newImsiInfo = getNewSimInfo(serial_number,imsi);
        resTD1.setImsi(newImsiInfo.getString("IMSI"));
        resTD1.setUserId(changePhoneReqData.getUca().getUserId());
        resTD1.setUserIdA(resOld.getString("USER_ID_A", "-1"));
        resTD1.setResCode(newImsiInfo.getString("SIM_CARD_NO"));
        resTD1.setResTypeCode("1");
        resTD1.setInstId(SeqMgr.getInstId());

        IDataset userVolte = UserSvcInfoQry.getSvcUserId(btd.getRD().getUca().getUserId(), "190");
        if(IDataUtil.isNotEmpty(userVolte)){
            ImpuTradeData impuTD = new ImpuTradeData();
            impuTD.setInstId(SeqMgr.getInstId());
            impuTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
            impuTD.setUserId(changePhoneReqData.getUca().getUserId());
            impuTD.setTelUrl("+86"+serial_number);
            impuTD.setSipUrl(imsi + "@ims.mnc0"+imsi.substring(3, 5)+".mcc460.3gppnetwork.org");
            impuTD.setImpi(imsi + "@ims.mnc0"+imsi.substring(3, 5)+".mcc460.3gppnetwork.org");
            impuTD.setStartDate(SysDateMgr.getSysTime());
            impuTD.setEndDate(SysDateMgr.getTheLastTime());
            impuTD.setRsrvStr2(getStrToChar("+86"+serial_number));
            impuTD.setRsrvStr4("VoLTE");
            impuTD.setRsrvStr5("+86"+serial_number+"@hain.ims.mnc000.mcc460.3gppnetwork.org");
            btd.add(serial_number, impuTD);
        }
        resTD1.setKi(resOld.getString("KI", ""));
        resTD1.setStartDate(SysDateMgr.getSysTime());
        resTD1.setEndDate(SysDateMgr.END_TIME_FOREVER);
        resTD1.setModifyTag("0");
        resTD1.setRemark(changePhoneReqData.getRemark());
        btd.add(serial_number, resTD1);
    }

    private String getStrToChar(String strTel) {
        String tmp = strTel.toString();
        tmp = tmp.replaceAll("\\+", "");
        char[] c = tmp.toCharArray();
        String str2 = "";
        for(int i=c.length-1; i>=0; i--){
            
            str2 += String.valueOf(c[i]);
            str2 += ".";
        }
        str2 += "e164.arpa";
        return str2;
    }
    

    private void genPlatTrade(BusiTradeData btd) throws Exception
    {
        ChangePhonePreRegisterRequestData changePhoneReqData = (ChangePhonePreRegisterRequestData) btd.getRD();
        UcaData uca = changePhoneReqData.getUca();
        List<PlatSvcTradeData> platSvcTradeDatas = changePhoneReqData.getUca().getUserPlatSvcs();

        for (int i = 0; i < platSvcTradeDatas.size(); i++)
        {
            PlatSvcTradeData pstd = platSvcTradeDatas.get(i);
            PlatSvcTradeData newPlatSvcTradeData = pstd.clone();
            if ("+".equals(genePlatDealTag(pstd.getElementId(), btd)))
            {
                continue;
            }
            newPlatSvcTradeData.setOprSource("08");
            newPlatSvcTradeData.setOperTime(btd.getRD().getAcceptTime());
            newPlatSvcTradeData.setEndDate(SysDateMgr.getSysTime());
            newPlatSvcTradeData.setBizStateCode("E");
            newPlatSvcTradeData.setActiveTag("1");// 被动
            newPlatSvcTradeData.setModifyTag("1");
            newPlatSvcTradeData.setOperCode("07");
            newPlatSvcTradeData.setIsNeedPf("1");
            btd.add(uca.getSerialNumber(), newPlatSvcTradeData);
        }
    }
   
    private IData getNewSimInfo(String serialNumber,String oldImsi) throws Exception {
        //先跨区写卡回写 ki,opc, data表移入idle表
/*            IData resultInfo = new DataMap();

            WriteCardBean writeBean = (WriteCardBean) BeanManager.createBean(WriteCardBean.class);
            IData oldSimInfo = writeBean.getSimCardInfoByImsi(oldImsi);

            if(IDataUtil.isEmpty(oldSimInfo)){
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取原IMSI信息为空");
            }
            String ki=oldSimInfo.getString("KI","121212121");
            if("".equals(ki)){
                ki = "121212121";
            }
            String opc=oldSimInfo.getString("OPC","123123123123");
            if("".equals(opc)){
                opc = "123123123123";
            }*/
            String ki="B48FC941829B67D825992BD51D32FFCE";
            String opc="B48FC941829B67D825992BD51D32FFCE";
            WriteCardBean bean = BeanManager.createBean(WriteCardBean.class);
            IData resInfo = bean.getSpeSimInfo(serialNumber, "", "", "1").getData(0);

            if(IDataUtil.isEmpty(resInfo)){
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取换卡个性化信息失败");
            }
            // 回写白卡资料
            String newImsi = resInfo.getString("IMSI");
            String newIccid = resInfo.getString("SIM_CARD_NO");

            SimCardBean cardBean = (SimCardBean) BeanManager.createBean(SimCardBean.class);
            IData writeInputParams = new DataMap();
            writeInputParams.put("KI", ki);
            writeInputParams.put("OPC", opc);
            writeInputParams.put("IMSI", newImsi);
            writeInputParams.put("ICCID", newIccid);
            cardBean.remoteWriteUpdateByIntf(writeInputParams);
            //获取新IMSI信息
/*            IData simInfo = writeBean.getSimCardInfoByImsi(newImsi);
            if(IDataUtil.isEmpty(simInfo)){
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取新IMSI信息为空");
            }*/

            return resInfo;        
    }

}
