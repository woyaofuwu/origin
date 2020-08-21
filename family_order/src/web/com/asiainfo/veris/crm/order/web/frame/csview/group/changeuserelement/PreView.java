
package com.asiainfo.veris.crm.order.web.frame.csview.group.changeuserelement;

import org.apache.tapestry.IRequestCycle;

import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.discntinfo.DiscntInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.packageelement.PackageElementInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBaseView;

public abstract class PreView extends GroupBasePage
{

    /**
     * 作用：页面的初始化
     *
     * @author luojh 2009-07-29
     * @param cycle
     * @throws Throwable
     */
    public void initial(IRequestCycle cycle) throws Throwable
    {
        IData preViewData = getData();

        String productId = preViewData.getString("PRODUCT_ID");// 集团产品ID
        String custId = preViewData.getString("CUST_ID");// 集团客户ID
        String uesrId = preViewData.getString("GRP_USER_ID");// 集团用户ID
        String tradeTypeCode = preViewData.getString("TRADE_TYPE_CODE");// 业务类型

        // 查询集团客户信息
        IData grpCustInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custId);
        setGrpCustInfo(grpCustInfo);

        // 获取缓存元素信息
        String cacheKey = CacheKey.getSelectElemtInfoKey(uesrId, productId, tradeTypeCode);
        IData elementData = (IData) SharedCache.get(cacheKey);

        IDataset selectedElementList = new DatasetList();
        IDataset grpPkgElementList = new DatasetList();



        if (IDataUtil.isNotEmpty(elementData))
        {
            String selectedElementStr = elementData.getString("SELECTED_ELEMENTS");// 集团已选择元素
            String grpPkgElementStr = elementData.getString("SELECTED_GRPPACKAGE_LIST");// 集团为成员定制元素

            if (StringUtils.isNotEmpty(selectedElementStr))
            {
                selectedElementList = new DatasetList(selectedElementStr);
                IDataset dctDatasetAdd = DataHelper.filter(selectedElementList, "ELEMENT_TYPE_CODE=D,MODIFY_TAG=2");
                for (int j = 0, jSize = dctDatasetAdd.size(); j < jSize; j++)
                {

                	String DiscntId = dctDatasetAdd.getData(0).getString("ELEMENT_ID");
                	if("9900101".equals(DiscntId) ||   "9900102".equals(DiscntId) ||
                			"9900103".equals(DiscntId) || "9900104".equals(DiscntId)||
                			"9900201".equals(DiscntId) || "9900202".equals(DiscntId)||
                			"9900203".equals(DiscntId) || 	"9900204".equals(DiscntId))
                	{
                		CSViewException.apperr(GrpException.CRM_GRP_843);
                	}
				}
                IDataset dctDatasetDel = DataHelper.filter(selectedElementList, "ELEMENT_TYPE_CODE=D,MODIFY_TAG=1");
                for (int j = 0, jSize = dctDatasetDel.size(); j < jSize; j++) {
                	String discntId = dctDatasetDel.getData(j).getString("ELEMENT_ID");
                	String packageId = dctDatasetDel.getData(j).getString("PACKAGE_ID");
					String cancelTag = PackageElementInfoIntfViewUtil.qryCancelTagStrByPackageIdAndElementIdElementTypeCode(this, packageId, discntId, "D");
					if (!"".equals(cancelTag) && "4".equals(cancelTag)) {
						CSViewException.apperr(GrpException.CRM_GRP_890, discntId);
					}
                }

				IDataset dctDatasetlkf = DataHelper.filter(selectedElementList,
						"PACKAGE_ID=91300002,ELEMENT_TYPE_CODE=D,MODIFY_TAG=1");
				for (int i = 0, jSize = dctDatasetlkf.size(); i < jSize; i++) {
					String DiscntId = dctDatasetlkf.getData(i).getString(
							"ELEMENT_ID");
					IDataset DiscntInfoDataset = DiscntInfoIntfViewUtil
							.qryDiscntInfoByDisCode(this, DiscntId);
					String DiscntMouths = DiscntInfoDataset.getData(0)
							.getString("MONTHS");
					String RsrvStr2 = DiscntInfoDataset.getData(0)
							.getString("RSRV_STR2");
					if ("12".equals(DiscntMouths) && "1".equals(RsrvStr2)) {
						CSViewException.apperr(GrpException.CRM_GRP_877);

					}
				}
            }
            if (StringUtils.isNotEmpty(grpPkgElementStr))
            {
                grpPkgElementList = new DatasetList(grpPkgElementStr);
            }
        }

        IData userElementData = new DataMap(); // 集团用户元素
        IData memberElementData = new DataMap();// 成员定制

        GroupBaseView.processProductElements(this, selectedElementList, userElementData);
        setUserProduct(userElementData);

        GroupBaseView.processProductElements(this, grpPkgElementList, memberElementData);
        setMemberProduct(memberElementData);
    }

    public abstract void setBbossMemberProduct(IData memberProduct); // BBOSS产品成员附加产品

    public abstract void setBbossUserProduct(IData userProduct); // BBOSS产品用户产品

    public abstract void setGrpCustInfo(IData grpCustInfo);// 集团客户信息

    public abstract void setGrpFeeList(String feeList);// 集团费用信息

    public abstract void setInfo(IData info);

    public abstract void setMemberProduct(IData memberProduct); // 成员附加产品

    public abstract void setUserProduct(IData userProduct); // 用户产品

}
