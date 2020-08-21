
package com.asiainfo.veris.crm.iorder.web.person.sundryquery.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

import java.math.BigDecimal;
import java.util.Iterator;

public abstract class OnNetSaleActivePenaltyQuery extends PersonBasePage {
    public void checkSaleActive(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IData svcParam = new DataMap();
        svcParam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        svcParam.put("TRADE_ID", data.getString("RELATION_TRADE_ID"));
        svcParam.put("PRODUCT_ID", data.getString("PRODUCT_ID"));
        svcParam.put("PACKAGE_ID", data.getString("PACKAGE_ID"));
        svcParam.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        IDataset checkResult = CSViewCall.call(this, "SS.SaleActiveEndCheckSVC.checkSaleActiveEnd", svcParam);
        this.setAjax(checkResult);
    }

    public void loadActiveDetailInfo(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IData svcParam = new DataMap();
        svcParam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        svcParam.put("USER_ID", data.getString("USER_ID"));
        svcParam.put("PRODUCT_ID", data.getString("PRODUCT_ID"));
        svcParam.put("PACKAGE_ID", data.getString("PACKAGE_ID"));
        svcParam.put("RELATION_TRADE_ID", data.getString("RELATION_TRADE_ID"));
        svcParam.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        IDataset saleActives = CSViewCall.call(this, "SS.SaleActiveEndSVC.querySaleActiveDetialInfos", svcParam);
        if (IDataUtil.isNotEmpty(saleActives)) {
            IData elementTradeData = saleActives.getData(0);
            setServs(elementTradeData.getDataset("SALE_SERVICE"));
            setDiscnts(elementTradeData.getDataset("SALE_DISCNT"));
            setGoods(elementTradeData.getDataset("SALE_GOODS"));
            setDeposits(elementTradeData.getDataset("SALE_DEPOSIT"));

            IData result = new DataMap();
            IDataset saleGoods = elementTradeData.getDataset("SALE_GOODS");
            if (IDataUtil.isEmpty(saleGoods)) {
                saleGoods = new DatasetList();
            }
            result.put("SALE_GOODS", saleGoods);

            //获取营销活动应收违约金
            IDataset refundMoneyData = CSViewCall.call(this, "SS.SaleActiveEndSVC.calculateSaleActiveEndOnNetReturnMoney", svcParam);
            result.put("REFUND_MONEY", refundMoneyData.getData(0));
            result.put("ALERT_INFO", elementTradeData.getString("ALERT", ""));
            this.setAjax(result);
        }
    }

    public void loadBaseTradeInfo(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IData svcParam = new DataMap();
        svcParam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        svcParam.put("USER_ID", data.getString("USER_ID"));
        svcParam.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        IDataset saleActives = CSViewCall.call(this, "SS.SaleActiveEndSVC.queryCanEndOnNetSaleActives", svcParam);
        //REQ201412260003 @ chenxy3 @ 2015-01-27 
        //紧急修改用户确认问题。
        if (saleActives != null && saleActives.size() > 0) {
            IData inparam = new DataMap();
            String staff = getVisit().getStaffId();
            boolean priv = StaffPrivUtil.isFuncDataPriv(staff, "ONNET_SALEACTIVE_END_PROD");
            IDataset saleActives1 = new DatasetList();
            //没有权限的工号，不可操作6888配置里的活动，仅可操作6888配置之外的活动；有权限的工号，可操作所有活动
            if (!priv) {
                for (Iterator it1 = saleActives.iterator(); it1.hasNext(); ) {
                    String flag = "1";
                    IData id1 = (IData) it1.next();
                    String product = id1.getString("PRODUCT_ID");
                    inparam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
                    inparam.put("PRODUCT_ID", product);
                    inparam.put("PARAM_CODE", "6888");//经查，最原始的代码使用的6889，但三代后6889已经被占用，改为6888
                    IDataset commparaValue = CSViewCall.call(this, "SS.SaleActiveEndSVC.getCommparaInfoSVC", inparam);
                    if (commparaValue != null && commparaValue.size() > 0) {
                        flag = "0";
                    } else {
                        flag = "1";
                    }

                    id1.put("PRODT_FLAG", flag);
                    saleActives1.add(id1);
                }
                setInfos(saleActives1);
            } else {
                for (Iterator it1 = saleActives.iterator(); it1.hasNext(); ) {
                    IData id1 = (IData) it1.next();
                    id1.put("PRODT_FLAG", "1");
                    saleActives1.add(id1);
                }
                setInfos(saleActives1);
            }
        } else {
            setInfos(saleActives);
        }
    }

   

    public abstract void setInfos(IDataset infos);

    public abstract void setDiscnts(IDataset discnts);

    public abstract void setServs(IDataset servs);

    public abstract void setDeposits(IDataset deposits);

    public abstract void setGoods(IDataset goods);

}
