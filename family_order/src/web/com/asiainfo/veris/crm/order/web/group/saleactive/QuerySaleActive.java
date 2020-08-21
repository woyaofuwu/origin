
package com.asiainfo.veris.crm.order.web.group.saleactive;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.FeeUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class QuerySaleActive extends GroupBasePage
{

    public abstract void setIsprint(int isprint);

    public abstract void setCondition(IData condition);

    public abstract void setUserinfo(IData userinfo);

    public abstract void setActiveInfos(IDataset activeInfos);

    public abstract void setCustinfo(IData custinfo);

    public abstract void setDiscntInfos(IDataset discntInfos);

    public abstract void setDiscntInfo(IData discntInfo);

    public abstract void setGoodsInfos(IDataset goodsInfos);

    public abstract void setGoodsInfo(IData goodsInfo);

    public abstract void setReturnInfos(IDataset returnInfos);

    public abstract void setReturnInfo(IData returnInfo);

    public abstract void setInfosCount(long infoCount);

    /**
     * 查询营销活动信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void getSaleActiveInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        // CS.UserSaleActiveInfoQrySVC.qrySaleActiveBySnStaffDeptCampnSDateEDate
        IData inparam = new DataMap();
        String serial_number = data.getString("cond_GROUP_SERIAL_NUMBER");
        if (serial_number.length() == 8)
        {
            serial_number = "898" + serial_number;
        }
        inparam.put("SERIAL_NUMBER", serial_number);
        inparam.put("CAMPN_TYPE", data.getString("cond_CAMPN_TYPE"));
        inparam.put("STAFF_ID", data.getString("cond_STAFF_ID"));
        inparam.put("DEPART_ID", data.getString("cond_DEPART_ID"));
        inparam.put("START_DATE", data.getString("cond_START_DATE"));
        inparam.put("END_DATE", data.getString("cond_END_DATE"));
        Pagination page = getPagination("PageNav");

        IDataOutput saleactiveOutput = CSViewCall.callPage(this, "CS.UserSaleActiveInfoQrySVC.qrySaleActiveBySnStaffDeptCampnSDateEDate", inparam, getPagination("PageNav"));
        long tt = saleactiveOutput.getDataCount();
        IDataset saleactives = saleactiveOutput.getData();
        if (IDataUtil.isNotEmpty(saleactives))
        {
            for (int i = 0, size = saleactives.size(); i < size; i++)
            {
                IData saleactive = saleactives.getData(i);

                String oper_fee = dataMoneyDeal(saleactive.getString("OPER_FEE"));
                saleactive.put("OPER_FEE", oper_fee);

                String foregift = dataMoneyDeal(saleactive.getString("FOREGIFT"));
                saleactive.put("FOREGIFT", foregift);

                String advance_pay = dataMoneyDeal(saleactive.getString("ADVANCE_PAY"));
                saleactive.put("ADVANCE_PAY", advance_pay);

                // 判断是否是无返还类活动
                IDataset dataset = CSViewCall.call(this, "CS.CommparaInfoQrySVC.queryCommparaByParaCode1ParaAttr", saleactive);
                boolean isReturn = (Boolean) dataset.get(0);
                if (isReturn)
                { // 无返还类
                    saleactive.put("ACTIVE_TYPE", "约定在网类");
                }
                else
                { // 返还类
                    saleactive.put("ACTIVE_TYPE", "返还/约定消费类");
                }
            }
            setActiveInfos(saleactives);
            
        }
        setInfosCount(tt);
        String loginStaffId = getVisit().getStaffId();
        // 判断导出权限
        if (StaffPrivUtil.isFuncDataPriv(loginStaffId, "CRM_QUERYSALEACTIVEEXPORT"))
        {
            data.put("EXPORT", "true");
        }
        else
        {
            data.put("EXPORT", "false");
        }

        // 判断打印权限
        if (StaffPrivUtil.isFuncDataPriv(loginStaffId, "SALEAVTIVE_EXPORTBTN"))
        {
            inparam.clear();
            inparam.put("STAFF_ID", loginStaffId);
            /*IDataset ds = CSViewCall.call(this, "CS.StaffInfoQrySVC.isStaffHavePrint", inparam);
            int isPrint = ds.getData(0).getInt("COUNT_NUM", 0);*/
            int isPrint = 1;// 服务不存在，简单修改成打印，具体后面怎么操作看用户怎么提要求
            data.put("PRINT", isPrint > 0 ? "true" : "false");
        }
        else
        {
            data.put("PRINT", "false");
        }
        setCondition(data);
    }

    /**
     * 根据营销包编码查询用户在该包下活动信息
     * 
     * @author sungq3
     * @date 2014-08-01
     * @param cycle
     * @throws Exception
     */
    public void queryUserOtherInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        // 获取客户综合信息
        IData userCustData = queryUserCust(data);
        setUserinfo(userCustData.getData("USER_INFO", new DataMap()));
        setCustinfo(userCustData.getData("CUST_INFO", new DataMap()));

        // 根据营销包编码查询用户在该包下优惠活动
        IDataset discnts = queryUserDiscnt(data);
        setDiscntInfos(discnts);

        // 根据营销包编码查询用户在该包下实物信息
        IDataset goods = queryUserGoods(data);
        setGoodsInfos(goods);

        // 获取用户返还信息
        IDataset userRets = queryUserReturns(data);
        setReturnInfos(userRets);
    }

    /**
     * 获取客户综合信息
     * 
     * @param data
     * @throws Exception
     */
    public IData queryUserCust(IData data) throws Exception
    {
        IData retData = new DataMap();

        // 查询集团用户信息
        IData userData = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, data.getString("USER_ID"));
        if (IDataUtil.isNotEmpty(userData))
        {
            retData.put("USER_INFO", userData);

            // 查询集团客户信息
            IData custData = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, userData.getString("CUST_ID"), false);
            if (IDataUtil.isEmpty(custData))
            {
                IDataset custDataset = CSViewCall.call(this, "CS.CustomerInfoQrySVC.getCustInfoByCustID", userData);
                if (IDataUtil.isNotEmpty(custDataset))
                {
                    custData = custDataset.getData(0);
                }
            }
            retData.put("CUST_INFO", custData);
        }
        return retData;
    }

    /**
     * 根据营销包编码查询用户在该包下优惠活动
     * 
     * @author sungq3
     * @date 2014-08-01
     * @param data
     * @throws Exception
     */
    public IDataset queryUserDiscnt(IData data) throws Exception
    {
        return CSViewCall.call(this, "CS.UserDiscntInfoQrySVC.getByUIdPkId", data);
    }

    /**
     * 根据营销包编码查询用户在该包下实物信息
     * 
     * @author sungq3
     * @date 2014-08-01
     * @param data
     * @throws Exception
     */
    public IDataset queryUserGoods(IData data) throws Exception
    {
        IDataset userSaleGoods = CSViewCall.call(this, "CS.UserSaleGoodsQrySVC.getByRelationTradeId", data);
        for (int i = 0, size = userSaleGoods.size(); i < size; i++)
        {
            IData userSaleGood = userSaleGoods.getData(i);
            if (!"".equals(userSaleGood.getString("USER_ID", "")))
            {
                IData userData = UCAInfoIntfViewUtil.qryMebUserInfoByUserIdAndRoute(this, data.getString("USER_ID"), getTradeEparchyCode(), false);
                if (IDataUtil.isEmpty(userData))
                {
                    userSaleGood.put("SERIAL_NUMBER", "");
                }
                else
                {
                    userSaleGood.put("SERIAL_NUMBER", userData.getString("SERIAL_NUMBER"));
                }
            }
            else
            {
                userSaleGood.put("SERIAL_NUMBER", "");
            }
            if ("1".equals(userSaleGood.getString("RSRV_TAG1", "")))
            {
                userSaleGood.put("RSRV_TAG1", "赠送");
            }
            else
            {
                userSaleGood.put("RSRV_TAG1", "");
            }
        }
        return userSaleGoods;
    }

    /**
     * 获取用户返还信息
     * 
     * @author sungq3
     * @date 2014-08-01
     * @param data
     * @throws Exception
     */
    public IDataset queryUserReturns(IData data) throws Exception
    {
        // 返回值
        IDataset rets = new DatasetList();

        IData inparam = new DataMap();
        inparam.put("X_GETMODE", "2");
        inparam.put("USER_ID", data.getString("USER_ID"));
        inparam.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

        IDataset actReturns = CSViewCall.call(this, "CS.UserSaleActiveInfoQrySVC.queryUserDiscntAction", inparam);// 调用帐务接口获取话费返还信息
        // IDataset actReturns = new DatasetList();

        // 获取userId对应的serial_number
        String serial_number = "";
        IData userData = UCAInfoIntfViewUtil.qryMebUserInfoByUserIdAndRoute(this, data.getString("USER_ID"), getTradeEparchyCode(), false);
        if (IDataUtil.isNotEmpty(userData))
        {
            serial_number = userData.getString("SERIAL_NUMBER");
        }

        // 一条活动信息可能对应多条话费返还信息
        if (IDataUtil.isNotEmpty(actReturns))
        {
            // 界面传入的单条活动ID，根据它去取账务接口返回的多条话费返还信息
            String relationId = data.getString("RELATION_TRADE_ID", "");
            String packageId = data.getString("PACKAGE_ID", "");

            for (int i = 0, size = actReturns.size(); i < size; i++)
            {
                IData actReturn = actReturns.getData(i);
                // 账务接口返回的活动ID
                String outerTradeId = actReturn.getString("OUTER_TRADE_ID", "");
                if (relationId.equals(outerTradeId))
                {
                    IData ret = new DataMap();

                    // 返款总月份数
                    int months = actReturn.getInt("MONTHS");

                    ret.put("PACKAGE_ID", packageId); // 营销包编码
                    ret.put("SERIAL_NUMBER", serial_number); // 用户服务号码
                    ret.put("X_MONTHS", Integer.valueOf(months).toString()); // 需返还月数
                    ret.put("X_USETAG", actReturn.getString("X_USETAG")); // 返还状态
                    ret.put("X_LIMIT_MONEY", Double.parseDouble(actReturn.getString("LIMIT_MONEY")) / 100); // 每月返还话费额(分转元)

                    String startDate = actReturn.getString("START_DATE");
                    startDate = SysDateMgr.getDateForSTANDYYYYMMDD(startDate);
                    ret.put("START_CYCLE_ID", startDate); // 开始时间

                    String endDate = SysDateMgr.getAddMonthsNowday(months, startDate);
                    endDate = SysDateMgr.addDays(endDate, -1);
                    ret.put("END_CYCLE_ID", endDate); // 结束时间

                    // 获取已返还月数
                    int left_months = (data.getInt("MONEY", 0) - data.getInt("LEFT_MONEY", 0)) / data.getInt("LIMIT_MONEY", 1);
                    ret.put("X_LEFT_MONTHS", Integer.valueOf(left_months).toString());

                    rets.add(ret);
                }
            }
        }
        return rets;
    }

    /**
     * 金额处理 分转化成元
     * 
     * @param str
     * @throws Exception
     */
    private String dataMoneyDeal(String str) throws Exception
    {
        String string = null;
        if (str == null || "".equals(str))
        {
            str = "0";
        }
        string = FeeUtils.Fen2Yuan(str);
        return string;
    }
}
