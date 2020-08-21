
package com.asiainfo.veris.crm.iorder.web.person.view360;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

public abstract class MySaleActiveTab extends PersonBasePage {
    /**
     * 客户资料综合查询 - 营销活动信息查询
     * 
     * @param cycle
     * @throws Exception
     * @author huanghui@asiainfo.com
     * @date 2014-08-15
     */
    public void queryInfo(IRequestCycle cycle) throws Exception {
        IDataset QYSaleActives;   // 签约类营销活动
        IDataset NOQYSaleActives; // 非签约类营销活动

        IData inParam = getData();
        String userId = inParam.getString("USER_ID", "");
        String queryAll = inParam.getString("QUERY_ALL", "false"); // 如果前台勾选查询所有记录，queryAll为true，默认不勾选为false
        // 非签约类营销活动查询参数
        inParam.put("QRY_TYPE", "2");
        inParam.put("ALL_FLAG", queryAll);

        if (StringUtils.isNotBlank(userId)) {
            if ("true".equals(queryAll)) {
                NOQYSaleActives = new DatasetList();
                // 查询所有签约类营销活动：没有CAMPN_TYPE过滤，所以需要把结果集里CAMPN_TYPE = YX04 || YX07筛选出来
                QYSaleActives = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryUserSaleActiveInfoAll", inParam);
                filterNOQYSaleActives(QYSaleActives, NOQYSaleActives);
            } else {
                // 查询有效签约类营销活动：CAMPN_TYPE = YX02 || YX03 || YX08 || YX09 || YX11 || YX12 || YX13
                QYSaleActives = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryUserSaleActiveInfo", inParam);
                // 查询有效非签约类营销活动：CAMPN_TYPE = YX04 || YX07
                NOQYSaleActives = CSViewCall.call(this, "SS.QuerySaleActiveSVC.querySaleActive", inParam);
            }

            IData ajaxMap = new DataMap();
            if (IDataUtil.isNotEmpty(QYSaleActives)) {
                for (Object obj : QYSaleActives) {
                    IData active = (IData) obj;
                    String packageDesc = active.getString("PACKAGE_DESC", "");
                    active.put("PACKAGE_DESC", StringUtils.isNotBlank(packageDesc) ? packageDesc : active.getString("PACKAGE_NAME"));
                }
                ajaxMap.put("QY_COMMON", QYSaleActives);
                setQYSaleActives(QYSaleActives);
            }
            if (IDataUtil.isNotEmpty(NOQYSaleActives)) {
                for (Object obj : NOQYSaleActives) {
                    IData active = (IData) obj;
                    String packageDesc = active.getString("PACKAGE_DESC", "");
                    active.put("PACKAGE_DESC", StringUtils.isNotBlank(packageDesc) ? packageDesc : active.getString("PACKAGE_NAME"));
                }
                ajaxMap.put("NOQY_COMMON", NOQYSaleActives);
                setNOQYSaleActives(NOQYSaleActives);
            }
            setAjax(ajaxMap);
        }
    }

    // 从数据集合{A}过滤掉CAMPN_TYPE = YX04 || YX07的营销活动，存入另一个数据集合{B}
    private void filterNOQYSaleActives(IDataset saleActivesA, IDataset saleActivesB) {
        if (IDataUtil.isNotEmpty(saleActivesA)) {
            for (int i = saleActivesA.size() - 1; i >= 0; i--) {
                String campnType = saleActivesA.getData(i).getString("CAMPN_TYPE");
                if (SaleActiveConst.CAMPN_TYPE_FQYLB.equals(campnType) || SaleActiveConst.CAMPN_TYPE_FQYGJ.equals(campnType)) {
                    saleActivesB.add(saleActivesA.getData(i));
                    saleActivesA.remove(i);
                }
            }
        }
    }

    /**
     * 查询营销活动详细信息
     * @param cycle
     * @throws Exception
     */
    public void querySaleActiveDetails(IRequestCycle cycle) throws Exception {
        IData param = getData();
        IData saleActive = new DataMap(param.getString("PARAMS_STR"));

        String jumpTag = param.getString("JUMP_TAG", "");
        if (!"1".equals(jumpTag)) {
            saleActive.put("EPARCHY_CODE", param.getString("EPARCHY_CODE"));
            saleActive.put("NORMAL_USER_CHECK", param.getString("NORMAL_USER_CHECK"));

            querySaleActiveTradeAndUpdateInfo(saleActive);
            querySaleActiveDepositAndGiftInfo(saleActive);
            querySaleActiveGoodsInfoByRelaTradeId(saleActive);
            querySaleActiveDiscntGoodsAndDepositDetails(saleActive);

            setAjax(saleActive);
        }
        setSaleActive(saleActive);
    }

    // 查询营销活动受理和更新信息
    private void querySaleActiveTradeAndUpdateInfo(IData saleActive) throws Exception {
        IData inParam = new DataMap();
        inParam.put("TRADE_ID", saleActive.getString("RELATION_TRADE_ID"));
        inParam.put("CANCEL_TAG", "0"); // 返销标记：0-正向业务受理
        inParam.put(Route.ROUTE_EPARCHY_CODE, saleActive.getString("EPARCHY_CODE"));

        // 通过RELATION_TRADE_ID查主台账表TF_BH_TRADE获取受理部门信息
        IData tradeHisInfo = CSViewCall.callone(this, "CS.TradeHistoryInfoQrySVC.queryTradeHistoryInfos", inParam);
        if (IDataUtil.isNotEmpty(tradeHisInfo)) {
            String tradeDepartId = tradeHisInfo.getString("TRADE_DEPART_ID");
            String tradeDepartName = StaticUtil.getStaticValue(getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_NAME", tradeDepartId, "未知");
            saleActive.put("TRADE_DEPART_NAME", tradeDepartName);
        } else {
            saleActive.put("TRADE_DEPART_NAME", "未知");
        }

        String tradeStaffId = saleActive.getString("TRADE_STAFF_ID");
        String updateStaffId = saleActive.getString("UPDATE_STAFF_ID");
        String updateDepartId = saleActive.getString("UPDATE_DEPART_ID");
        String tradeStaffName = StaticUtil.getStaticValue(getVisit(), "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", tradeStaffId, "无");
        String updateStaffName = StaticUtil.getStaticValue(getVisit(), "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", updateStaffId, "无");
        String updateDepartName = StaticUtil.getStaticValue(getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_NAME", updateDepartId, "未知");
        saleActive.put("TRADE_STAFF_NAME", tradeStaffName);
        saleActive.put("UPDATE_STAFF_NAME", updateStaffName);
        saleActive.put("UPDATE_DEPART_NAME", updateDepartName);
    }

    // 查询营销活动实物终端信息
    private void querySaleActiveGoodsInfoByRelaTradeId(IData saleActive) throws Exception {
        IData inParam = new DataMap();
        inParam.put("RELATION_TRADE_ID", saleActive.getString("RELATION_TRADE_ID"));
        inParam.put(Route.ROUTE_EPARCHY_CODE, saleActive.getString("EPARCHY_CODE"));

        IData saleGoodsInfo = CSViewCall.callone(this, "CS.UserSaleGoodsQrySVC.getByRelationTradeId", inParam);
        if (IDataUtil.isNotEmpty(saleGoodsInfo)) {
            String deviceModel = saleGoodsInfo.getString("DEVICE_MODEL");
            String deviceBrand = saleGoodsInfo.getString("DEVICE_BRAND");
            String deviceName = StringUtils.isBlank(deviceModel) ? deviceBrand : deviceBrand + " " + deviceModel;
            if (StringUtils.isNotBlank(deviceName)) {
                String devicePrice = saleGoodsInfo.getString("RSRV_STR6", "");
                String salePrice = saleGoodsInfo.getString("RSRV_NUM5", "");
                saleActive.put("IMEI", saleGoodsInfo.getString("RES_CODE"));
                saleActive.put("DEVICE_PRICE", StringUtils.isBlank(devicePrice) ? 0 : Integer.parseInt(devicePrice) / 100);
                saleActive.put("SALE_PRICE", StringUtils.isBlank(salePrice) ? 0 : Integer.parseInt(salePrice) / 100);
                saleActive.put("DEVICE_NAME", deviceName);
                saleActive.put("HAS_DEVICE", 1);
            } else {
                saleActive.put("HAS_DEVICE", 0);
            }
        } else {
            saleActive.put("HAS_DEVICE", 0);
        }
    }

    // 查询营销活动预存款和赠送款总量及余量
    private void querySaleActiveDepositAndGiftInfo(IData saleActive) throws Exception {
        IData inParam = new DataMap();
        inParam.put("USER_ID", saleActive.getString("USER_ID"));
        inParam.put("SERIAL_NUMBER", saleActive.getString("SERIAL_NUMBER"));
        inParam.put("RELATION_TRADE_ID", saleActive.getString("RELATION_TRADE_ID"));
        inParam.put(Route.ROUTE_EPARCHY_CODE, saleActive.getString("EPARCHY_CODE"));

        IData depositGiftInfo = CSViewCall.callone(this, "SS.GetUser360ViewSVC.querySaleActiveDepositAndGiftInfo", inParam);
        if (IDataUtil.isNotEmpty(depositGiftInfo)) {
            saleActive.put("DEPOSIT_SUM", depositGiftInfo.getDouble("DEPOSIT_SUM"));
            saleActive.put("DEPOSIT_USED", depositGiftInfo.getDouble("DEPOSIT_USED"));
            saleActive.put("DEPOSIT_REMAINED", depositGiftInfo.getDouble("DEPOSIT_REMAINED"));
            saleActive.put("DEPOSIT_USED_PERCENT", depositGiftInfo.getString("DEPOSIT_USED_PERCENT"));
            saleActive.put("GIFT_SUM", depositGiftInfo.getDouble("GIFT_SUM"));
            saleActive.put("GIFT_USED", depositGiftInfo.getDouble("GIFT_USED"));
            saleActive.put("GIFT_REMAINED", depositGiftInfo.getDouble("GIFT_REMAINED"));
            saleActive.put("GIFT_USED_PERCENT", depositGiftInfo.getString("GIFT_USED_PERCENT"));
        }
    }

    // 查询营销活动优惠、实物和预存详细信息
    private void querySaleActiveDiscntGoodsAndDepositDetails(IData saleActive) throws Exception {
        IData inParam = new DataMap();
        inParam.put("USER_ID", saleActive.getString("USER_ID"));
        inParam.put("SERIAL_NUMBER", saleActive.getString("SERIAL_NUMBER"));
        inParam.put("PACKAGE_ID", saleActive.getString("PACKAGE_ID"));
        inParam.put("RELATION_TRADE_ID", saleActive.getString("RELATION_TRADE_ID"));
        inParam.put("INST_ID", saleActive.getString("INST_ID"));
        inParam.put("NORMAL_USER_CHECK", saleActive.getString("NORMAL_USER_CHECK"));

        IData saleActiveDetails = CSViewCall.callone(this, "SS.QuerySaleActiveSVC.queryActiveDetail", inParam);
        if (IDataUtil.isNotEmpty(saleActiveDetails)) {
            if (saleActiveDetails.containsKey("DISCNTS")) {
                saleActive.put("DISCNTS", saleActiveDetails.getDataset("DISCNTS"));
            }
            if (saleActiveDetails.containsKey("GOODS")) {
                saleActive.put("GOODS", saleActiveDetails.getDataset("GOODS"));
            }
            if (saleActiveDetails.containsKey("DEPOSITS")) {
                saleActive.put("DEPOSITS", saleActiveDetails.getDataset("DEPOSITS"));
            }
        }
    }

    public abstract void setQYSaleActives(IDataset QYSaleActives);

    public abstract void setNOQYSaleActives(IDataset NOQYSaleActives);

    public abstract void setSaleActive(IData saleActive);

}
