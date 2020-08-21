package com.asiainfo.veris.crm.iorder.web.family.viewquery;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 *
 * @author zhangxi
 *
 */
public abstract class FamilySaleActiveTab extends PersonBasePage{

	/**
	 * 家庭营销活动查询
	 * @param cycle
	 * @throws Exception
	 */
	public void queryFamilyInfo(IRequestCycle cycle) throws Exception {

		IDataset QYSaleActives;   // 签约类营销活动
        IDataset NOQYSaleActives; // 非签约类营销活动

        IData inParam = getData();
        String userId = inParam.getString("USER_ID", "");
        String queryAll = inParam.getString("QUERY_ALL", "false");

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

    public abstract void setQYSaleActives(IDataset QYSaleActives);
    public abstract void setNOQYSaleActives(IDataset NOQYSaleActives);
    public abstract void setSaleActive(IData saleActive);

}
