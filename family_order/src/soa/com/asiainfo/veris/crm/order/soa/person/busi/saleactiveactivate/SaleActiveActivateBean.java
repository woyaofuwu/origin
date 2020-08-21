
package com.asiainfo.veris.crm.order.soa.person.busi.saleactiveactivate;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleDepositTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleGoodsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgExtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;

public class SaleActiveActivateBean extends CSBizBean
{
    private void ActivateActiveOnlyAcct(String serialNumber, String resCode, String activeDate, String activeActionCode) throws Exception
    {
        IData activeParam = new DataMap();
        UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
        String tradeId = SeqMgr.getTradeId();
        String syncId = SeqMgr.getSyncIncreId();
        activeParam.put("SYNC_SEQUENCE", syncId);
        activeParam.put("TRADE_ID", tradeId);
        activeParam.put("BATCH_ID", tradeId);
        activeParam.put("PRIORITY", "0");
        activeParam.put("CHARGE_ID", tradeId);
        activeParam.put("PAYMENT_OP", "16000");
        activeParam.put("TRADE_TYPE_CODE", "7044");
//        activeParam.put("ACCT_ID", "0");
        activeParam.put("ACCT_ID", uca.getAcctId());
        activeParam.put("USER_ID", uca.getUserId());
        activeParam.put("CHANNEL_ID", "15000");
        activeParam.put("RECV_FEE", "0");
        activeParam.put("PAYMENT_ID", "102");
        activeParam.put("WRITEOFF_MODE", "1");
        activeParam.put("PAY_FEE_MODE_CODE", "0");
        activeParam.put("NET_TYPE_CODE", "00");
        activeParam.put("OUTER_TRADE_ID", tradeId);
        activeParam.put("RSRV_INFO3", tradeId);
        activeParam.put("X_SEQUENCEID", syncId);
        activeParam.put("RECV_EPARCHY_CODE", getTradeEparchyCode());
        activeParam.put("RECV_CITY_CODE", getVisit().getCityCode());
        activeParam.put("RECV_DEPART_ID", getVisit().getDepartId());
        activeParam.put("RECV_STAFF_ID", getVisit().getStaffId());
        activeParam.put("RECV_TIME", SysDateMgr.getSysTime());
        activeParam.put("CANCEL_TAG", "0");
        activeParam.put("DEAL_TAG", "0");
        activeParam.put("MODIFY_TAG", "0");
        activeParam.put("RSRV_INFO1", resCode);
        activeParam.put("RSRV_INFO2", "g3");
        activeParam.put("START_DATE", activeDate);
        activeParam.put("ACTION_CODE", activeActionCode);
        activeParam.put("PAYMENT_REASON_CODE", "0");
        activeParam.put("ACT_TAG", "4");
        Dao.insert("TI_A_SYNC_RECV", activeParam);

        activeParam.clear();
        activeParam.put("SYNC_SEQUENCE", syncId);
        activeParam.put("SYNC_TYPE", "0");
        activeParam.put("TRADE_ID", tradeId);
        activeParam.put("STATE", "0");
        Dao.insert("TI_B_SYNCHINFO", activeParam);
    }

    public IDataset activateActives(IData params) throws Exception
    {
        String serialNumber = params.getString("SERIAL_NUMBER");
        String resCode = params.getString("IMEI");

        if (!isNeedBindAcitve(serialNumber, resCode))
        {
            IData activeParam = getActiveActivateInfo(serialNumber, resCode, SysDateMgr.getSysTime());
            String activeDate = activeParam.getString("ACTIVE_DATE", SysDateMgr.getSysTime());
            String activeActionCode = activeParam.getString("ACTIVE_ACTIONCODE");
            ActivateActiveOnlyAcct(serialNumber, resCode, activeDate, activeActionCode);
        }
        else
        {
            IData svcParam = new DataMap();
            svcParam.put("SERIAL_NUMBER", serialNumber);
            svcParam.put("TERMINAL_ID", resCode);
            svcParam.put("PRODUCT_ID", params.getString("PRODUCT_ID"));
            svcParam.put("PACKAGE_ID", params.getString("PACKAGE_ID"));
            svcParam.put("ACTIVATE_ACTIVE_4_GROUP", "1");
            CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", svcParam);
        }

        return null;
    }

    private IData getActiveActivateInfo(String serialNumber, String resCode, String activeDate) throws Exception
    {
        UcaData uca = UcaDataFactory.getNormalUca(serialNumber);

        String relationTradeId = "";
        List<SaleGoodsTradeData> userSaleGoods = uca.getUserSaleGoods();
        if (CollectionUtils.isNotEmpty(userSaleGoods))
        {
            for (int index = 0, size = userSaleGoods.size(); index < size; index++)
            {
                SaleGoodsTradeData saleGoodsData = userSaleGoods.get(index);
                if (resCode.equals(saleGoodsData.getResCode()))
                {
                    relationTradeId = saleGoodsData.getRelationTradeId();
                    break;
                }
            }
        }

        IData returnData = new DataMap();

        List<SaleDepositTradeData> userSaleDepositList = uca.getUserSaleDepositByRelationTradeId(relationTradeId);
        if (CollectionUtils.isEmpty(userSaleDepositList))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有对应的用户预存资料！");
        }
        returnData.put("ACTIVE_ACTIONCODE", userSaleDepositList.get(0).getADiscntCode());

        SaleActiveTradeData userSaleActiveData = uca.getUserSaleActiveByRelaTradeId(relationTradeId);

        String packageId = userSaleDepositList.get(0).getPackageId();
        IDataset pkgExtDataset = PkgExtInfoQry.queryPackageExtInfo(packageId, uca.getUser().getEparchyCode());

        if (CollectionUtils.isEmpty(pkgExtDataset))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有没有获取到包扩展信息！");
        }

        String pkgTagSet2 = SaleActiveUtil.getPackageExtTagSet2(packageId, pkgExtDataset);
        String activeTag = StringUtils.defaultIfEmpty(pkgTagSet2, "000000").substring(0, 1);
        String maxActiveDate = activeDate;
        if (activeTag.equals("1"))
        {
            String activeType = StringUtils.defaultIfEmpty(pkgTagSet2, "000000").substring(2, 3);
            if (activeType.equals("0")) // 如果是0则为手机，需判顺延，如果为1则为3G上网本，不需要判顺延
            {
                int iresult = activeDate.compareTo(userSaleActiveData.getStartDate()); // 拿激活时间与该活动的开始时间做比较，哪个大取哪个
                if (iresult < 0)
                {
                    maxActiveDate = userSaleActiveData.getStartDate();
                }
                returnData.put("ACTIVE_DATE", maxActiveDate);
            }
        }
        return returnData;
    }

    private boolean isNeedBindAcitve(String serialNumber, String resCode) throws Exception
    {
        UcaData uca = UcaDataFactory.getNormalUca(serialNumber);

        boolean needBindActive = true;
        String packageId = "";
        List<SaleGoodsTradeData> userSaleGoods = uca.getUserSaleGoods();
        if (CollectionUtils.isNotEmpty(userSaleGoods))
        {
            for (int index = 0, size = userSaleGoods.size(); index < size; index++)
            {
                SaleGoodsTradeData saleGoodsData = userSaleGoods.get(index);
                if (resCode.equals(saleGoodsData.getResCode()))
                {
                    needBindActive = false;
                    packageId = saleGoodsData.getPackageId();
                    break;
                }
            }
        }

        if (!needBindActive)
        {
            IDataset pkgExtDataset = PkgExtInfoQry.queryPackageExtInfo(packageId, uca.getUser().getEparchyCode());
            String pkgTagSet2 = SaleActiveUtil.getPackageExtTagSet2(packageId, pkgExtDataset);
            String bindActiveTag = StringUtils.defaultIfEmpty(pkgTagSet2, "000000000").substring(1, 2);
            
            if ("1".equals(bindActiveTag)) // 第二位:0或者空不处理; 1:表示如果已经存在该礼包，还需要绑定集团下发的礼包
            {
                needBindActive = true;
            }
        }

        return needBindActive;
    }

    public IDataset queryActivePackages(String eparchyCode) throws Exception
    {
        return CommparaInfoQry.getOnlyByAttr("CSM", "2003", eparchyCode);
    }

    public IDataset queryActives(IData params, Pagination pagination) throws Exception
    {
        return SaleActiveInfoQry.querySaleActiveDataDown(params, pagination);
    }

    public void updateActiveIntfInfo(IData data) throws Exception
    {
        IData param = new DataMap();
        param.put("DEALFLAG", "1");
        param.put("RECORDID", data.getString("RECORDID"));
        Dao.save("TI_R_USRPACKAGEDATA_DOWN", param);
    }

}
