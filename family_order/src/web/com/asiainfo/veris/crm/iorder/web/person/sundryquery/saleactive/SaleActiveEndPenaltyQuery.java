
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
import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;
import com.ailk.org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Iterator;

public abstract class SaleActiveEndPenaltyQuery extends PersonBasePage {
    private static final Logger log = Logger.getLogger(SaleActiveEndPenaltyQuery.class);

    public void checkSaleActive(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IData svcParam = new DataMap();
        svcParam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        svcParam.put("TRADE_ID", data.getString("RELATION_TRADE_ID"));
        svcParam.put("PRODUCT_ID", data.getString("PRODUCT_ID"));
        svcParam.put("PACKAGE_ID", data.getString("PACKAGE_ID"));
        svcParam.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        IDataset checkResult = CSViewCall.call(this, "SS.SaleActiveEndCheckSVC.checkSaleActiveEnd", svcParam);

        // 核对哪些营销活动需要进行提示
        IDataset checkIsNeedWarmResult = CSViewCall.call(this, "SS.SaleActiveEndCheckSVC.checkIsNeedWarm", svcParam);
        if (IDataUtil.isNotEmpty(checkIsNeedWarmResult)) {
            checkResult.getData(0).putAll(checkIsNeedWarmResult.getData(0));
        }
      //是否显示退还和目
        String backTermShow="0";
        IData inputParam = new DataMap();
        inputParam.put("QUERY_TAG", "8987");
        inputParam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        IDataset commparaInfos = CSViewCall.call(this, "SS.NewSvcRecomdInfoSVC.getParaInfo", inputParam);
        if(IDataUtil.isNotEmpty(commparaInfos)){
        	for(int i=0;i<commparaInfos.size();i++){
        		if(data.getString("PRODUCT_ID","").equals(commparaInfos.getData(i).getString("PARAM_CODE"))
        				&&data.getString("PACKAGE_ID","").equals(commparaInfos.getData(i).getString("PARA_CODE1"))){
        			backTermShow="1";
        			break;
        		}
        	}
        checkResult.getData(0).put("BACK_TERM_SHOW", backTermShow);}
    
        
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
        svcParam.put("PRODUCT_MODE", data.getString("PRODUCT_MODE"));
        //加是否归还摄像头标记
        svcParam.put("BACK_TERM", data.getString("BACK_TERM"));
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
//          IDataset refundMoneyData=CSViewCall.call(this, "SS.SaleActiveEndSVC.calculateSaleActiveReturnMoney", svcParam);
            IDataset refundMoneyData = CSViewCall.call(this, "SS.SaleActiveEndSVC.newcalculateSaleActiveReturnMoney", svcParam);

            result.put("REFUND_MONEY", refundMoneyData.getData(0));
            result.put("ALERT_INFO", elementTradeData.getString("ALERT", ""));

            if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "REFUNDPRICEFUNC")) {
                result.put("REFUND_PRICE_FUNC", "1");
            } else {
                result.put("REFUND_PRICE_FUNC", "0");
            }

            if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "REFUNDACTIVEFUNC")) {
                result.put("REFUND_ACTIVE_FUNC", "1");
            } else {
                result.put("REFUND_ACTIVE_FUNC", "0");
            }

            this.setAjax(result);
        }
    }

    public void loadBaseTradeInfo(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IData svcParam = new DataMap();
        svcParam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        svcParam.put("USER_ID", data.getString("USER_ID"));
        svcParam.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        IDataset saleActives = CSViewCall.call(this, "SS.SaleActiveEndSVC.queryCanEndSaleActives", svcParam);
        //REQ201412260003 @ chenxy3 @ 2015-01-27 
        //紧急修改用户确认问题。
        if (saleActives != null && saleActives.size() > 0) {
            IData inparam = new DataMap();
            String staff = getVisit().getStaffId();
            /**
             * REQ201607270001 关于终止吉祥号码营销活动包权限的优化需求
             * chenxy3
             * 统一规则，替代//REQ201412260003新增星火计划礼包提前终止专项办理界面
             * 有权限且才能终止配置6889里的规则
             * flag
             * */
            boolean priv = StaffPrivUtil.isFuncDataPriv(staff, "SALEACTIVE_END_PROD");
            IDataset saleActives1 = new DatasetList();
            if ("SUPERUSR".equalsIgnoreCase(staff)) {
                priv = true;
            }
            //1、要终止的列表循环
            for (Iterator it1 = saleActives.iterator(); it1.hasNext(); ) {
                String flag = "1";
                IData id1 = (IData) it1.next();
                if (!priv) {
                    String product = id1.getString("PRODUCT_ID");
                    inparam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
                    inparam.put("PRODUCT_ID", product);
                    inparam.put("PARAM_CODE", "6888");//经查，最原始的代码使用的6889，但三代后6889已经被占用，改为6888
                    IDataset commparaValue = CSViewCall.call(this, "SS.SaleActiveEndSVC.getCommparaInfoSVC", inparam);
                    if (commparaValue != null && commparaValue.size() > 0) {
                        for (int k = 0; k < commparaValue.size(); k++) {
                            IData commData = commparaValue.getData(k);
                            String paraCode1 = commData.getString("PARA_CODE1", "");
                            String packId = id1.getString("PACKAGE_ID");
                            if (("1".equals(paraCode1) || paraCode1.equals(packId))) {
                                flag = "0";
                                break;
                            }
                        }
                    }
                }
                id1.put("PRODT_FLAG", flag);
                saleActives1.add(id1);
            }
            setInfos(saleActives1);
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
