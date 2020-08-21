
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.specqry;

import java.text.NumberFormat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.SundryQry;

public class SundryQrySVC extends CSBizService
{
    /**
     * 获取业务区数据，按AREA_CODE排序
     * 
     * @param pd
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset initCityArea(IData input) throws Exception
    {
        return SundryQry.initCityArea(true);
    }

    /**
     * 执行改号业务查询sql
     * 
     * @param pd
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset queryChangeSnInfo(IData input) throws Exception
    {
        return SundryQry.queryChangeSnInfo(input, this.getPagination());
    }

    /**
     * 根据代理商编号获取代理商缴纳的押金
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryChannelDeposit(IData input) throws Exception
    {
        String departCode = input.getString("DEPART_CODE");
        IDataset qryResult = SundryQry.queryChannelDeposit(departCode);

        IDataset depositInfos = new DatasetList();

        if (qryResult != null && qryResult.size() > 0)
        {
            IData info = qryResult.getData(0);
            String depositSum = info.getString("MONEY");
            String departid = info.getString("DEPART_ID");
            int mobileStock = 0;
            double mobileMoney = 0;

            /*
             * 这段需要替换为资源查询 原查询：TuxedoHelper.callTuxSvc(pd, "QRM_IGetResInfo",param); IData mobileShop =
             * queryBean.queryMobileShopInfo(pd, departid); if(!mobileShop.isEmpty()){ mobileStock =
             * mobileShop.getInt("MOBILE_STOCK"); mobileMoney = Long.parseLong(mobileShop.getString("MOBILE_MONEY")); }
             */
            double depositPlus = Long.parseLong(depositSum) - mobileMoney;

            NumberFormat format = NumberFormat.getInstance();
            format.setMaximumFractionDigits(2);
            format.setMinimumFractionDigits(2);

            IData depositInfo = new DataMap();
            depositInfo.put("MOBILE_STOCK", mobileStock);
            depositInfo.put("MOBILE_MONEY", format.format(mobileMoney / 100));
            depositInfo.put("DEPOSIT_SUM", format.format(Double.parseDouble(depositSum) / 100));
            depositInfo.put("DEPOSIT_PLUS", format.format(depositPlus / 100));

            depositInfos.add(depositInfo);
        }

        return depositInfos;
    }

    /**
     * 执行营业过户用户清单查询sql
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryChgCustOwnerInfo(IData input) throws Exception
    {
        return SundryQry.queryChgCustOwnerInfo(input, this.getPagination());
    }

    /**
     * 执行营业新开户用户查询
     * 
     * @param pd
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset queryOpenUserInfo(IData input) throws Exception
    {
        String cityCode = input.getString("CITY_CODE");
        String startDate = input.getString("START_DATE");
        String endDate = input.getString("END_DATE");
        String sStaffId = input.getString("START_STAFF_ID");
        String eStaffId = input.getString("END_STAFF_ID");

        return SundryQry.queryOpenUserInfo(cityCode, startDate, endDate, sStaffId, eStaffId, this.getPagination());
    }

    /**
     * 执行营业员特殊补卡业务清单查询sql
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset querySpeCompCardInfo(IData input) throws Exception
    {
        return SundryQry.querySpeCompCardInfo(input, this.getPagination());
    }

    /**
     * 执行特殊号码处理查询
     * 
     * @param pd
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset querySpecProNumber(IData input) throws Exception
    {
        return SundryQry.querySpecProNumber(input, this.getPagination());
    }

    /**
     * 执行新业务产品资料查询
     * 
     * @param pd
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset querySpServiceTrade(IData input) throws Exception
    {
        return SundryQry.querySpServiceTrade(input, this.getPagination());
    }

    /**
     * 执行手机缴费通定制信息查询
     * 
     * @param pd
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset queryUserBankInfo(IData input) throws Exception
    {
        return SundryQry.queryUserBankInfo(input, this.getPagination());
    }
}
