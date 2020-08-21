package com.asiainfo.veris.crm.order.soa.person.busi.serviceMaintain.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.*;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserImpuInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.ChangeSvcStateComm;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.requestdata.ChangeSvcStateReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.requestdata.EmergencyOpenReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.serviceMaintain.order.requestdata.ServiceMaintainReqData;

import java.util.List;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: ServiceMaintainTrade.java
 * @Description:
 * @version: v1.0.0
 * @author: chencn
 * @date: 2019-8-29 下午2:01:45
 */

public class ServiceMaintainTrade extends BaseTrade implements ITrade
{

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // 创建基础服务维护台账信息
        createBaseServiceTradeInfo(btd);
    }

    private void createBaseServiceTradeInfo(BusiTradeData btd) throws Exception{
        // 获取业务请求对象
        ServiceMaintainReqData serviceMaintainReqData = (ServiceMaintainReqData)btd.getRD();
        // 获取三户数据
        UcaData ucaData = btd.getRD().getUca();
        // 获取请求对象中的数据
        String serviceID = serviceMaintainReqData.getBaseServiceID();
        // 获取操作类型
        String operateType = serviceMaintainReqData.getOperateType();
        // 新增用户基础功能服务。
        SvcTradeData svcParam = new SvcTradeData();
        svcParam.setUserId(ucaData.getUserId());
        svcParam.setUserIdA("-1");
        svcParam.setProductId(ucaData.getProductId());
        svcParam.setPackageId("-1");
        svcParam.setElementId(serviceID);
        // 是否为主体服务  0：是
        svcParam.setMainTag("0");
        svcParam.setInstId(SeqMgr.getInstId());
        svcParam.setStartDate(SysDateMgr.getSysTime());

        if (operateType.equals("0")) { // 开通服务
            svcParam.setModifyTag(BofConst.MODIFY_TAG_ADD);
            svcParam.setEndDate(SysDateMgr.END_DATE_FOREVER);
            btd.add(ucaData.getSerialNumber(), svcParam);
        }else {// 退订服务, 取用户服务资料数据。
            // 记录用户服务（TF_B_TRADE_SVC）台账信息
            svcParam.setModifyTag(BofConst.MODIFY_TAG_DEL);
            svcParam.setEndDate(SysDateMgr.getSysTime());
            btd.add(ucaData.getSerialNumber(), svcParam);

            // 构造用户服务（TF_B_TRADE_SVCSTATE）台账信息
            SvcStateTradeData svcStateTradeData = new SvcStateTradeData();
            svcStateTradeData.setUserId(ucaData.getUserId());
            svcStateTradeData.setServiceId(serviceID);
            svcStateTradeData.setMainTag("0");
            svcStateTradeData.setStateCode("0");
            svcStateTradeData.setInstId(SeqMgr.getInstId());
            svcStateTradeData.setStartDate(SysDateMgr.getSysTime());
            svcStateTradeData.setEndDate(SysDateMgr.getSysTime());
            svcStateTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
            btd.add(ucaData.getSerialNumber(), svcStateTradeData);

            // 根据UserId能查询到有效的IMPU资料
            String userID = btd.getRD().getUca().getUserId();
            IDataset impuIds = UserImpuInfoQry.queryUserImpuInfo(userID);
            createImpuInfo(btd);
        }
    }

    private void delImpuInfo (BusiTradeData btd,IData data) throws Exception
    {
        UcaData uca = btd.getRD().getUca();
        ImpuTradeData impuTD = new ImpuTradeData(data);
        impuTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
        impuTD.setEndDate(SysDateMgr.getSysTime());
        btd.add(uca.getSerialNumber(), impuTD);
    }

    private void createImpuInfo (BusiTradeData btd) throws Exception
    {
        UcaData uca = btd.getRD().getUca();
        String imsi = "";
        String url2 = "@hain.ims.mnc000.mcc460.3gppnetwork.org";
        String usim = "0";//0 sim 卡 1 USIM卡
        String url1 = "";

        List<ResTradeData> resTrades = btd.getTradeDatas(TradeTableEnum.TRADE_RES);

        if(CollectionUtils.isNotEmpty(resTrades) && resTrades.size() > 0)
        {
            for(int i = 0 ; i < resTrades.size(); i++)
            {
                if(BofConst.MODIFY_TAG_ADD.equals(resTrades.get(i).getModifyTag()) && "1".equals(resTrades.get(i).getResTypeCode()))
                {
                    imsi = resTrades.get(i).getImsi();

                    break;
                }
            }

        }

        if(StringUtils.isBlank(imsi))
        {
            IDataset ids = BofQuery.queryUserAllValidRes(uca.getUserId(),uca.getUser().getEparchyCode());

            if(IDataUtil.isNotEmpty(ids))
            {
                for(int i = 0 ; i < ids.size(); i++)
                {
                    if("1".equals(ids.getData(i).getString("RES_TYPE_CODE")))
                    {
                        imsi = ids.getData(i).getString("IMSI");
                        break;
                    }
                }
            }
        }


        IDataset simInfos = ResCall.getSimCardInfo("1", "", imsi, "", "00");
        if(IDataUtil.isNotEmpty(simInfos))
        {
            IDataset reSet = ResCall.qrySimCardTypeByTypeCode(simInfos.getData(0).getString("RES_TYPE_CODE"));
            if (IDataUtil.isNotEmpty(reSet))
            {
                String typeCode = reSet.getData(0).getString("NET_TYPE_CODE");
                if("01".equals(typeCode))
                {
                    usim = "1";
                }
            }
        }

        //判断是否为USIM卡
        if(!"1".equals(usim))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_3001);
        }

        if(StringUtils.isNotBlank(imsi) && imsi.length() >= 5)
        {
            url1 = imsi + "@ims.mnc0"+imsi.substring(3, 5)+".mcc460.3gppnetwork.org";
        }

        String strTel = "+86"+uca.getSerialNumber();

        ImpuTradeData impuTD = new ImpuTradeData();
        impuTD.setInstId(SeqMgr.getInstId());
        impuTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
        impuTD.setUserId(uca.getUserId());
        impuTD.setTelUrl(strTel);
        impuTD.setSipUrl(url1);
        impuTD.setImpi(url1);
        impuTD.setStartDate(SysDateMgr.getSysTime());
        impuTD.setEndDate(SysDateMgr.getSysTime());
        impuTD.setRsrvStr2(getStrToChar(strTel));
        impuTD.setRsrvStr5(strTel+url2);
        btd.add(uca.getSerialNumber(), impuTD);
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
}
