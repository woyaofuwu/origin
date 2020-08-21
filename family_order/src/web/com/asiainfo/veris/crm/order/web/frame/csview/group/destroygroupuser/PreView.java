
package com.asiainfo.veris.crm.order.web.frame.csview.group.destroygroupuser;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.multitableinfo.UserProductElementInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationbbinfo.RelationBBInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.discntinfo.DiscntInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.packageelement.PackageElementInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBaseView;

public abstract class PreView extends GroupBasePage
{
    public void initial(IRequestCycle cycle) throws Exception
    {
        String userId = getData().getString("GRP_USER_ID");// 集团用户ID
        String productId = getData().getString("GRP_PRODUCT_ID", "");
        // 查询集团三户信息
        IData grpUCA = UCAInfoIntfViewUtil.qryGrpUCAInfoByUserId(this, userId);
        if (IDataUtil.isNotEmpty(grpUCA))
        {
            setGrpCustInfo(grpUCA.getData("GRP_CUST_INFO"));
            setGrpUserInfo(grpUCA.getData("GRP_USER_INFO"));
            setGrpAcctInfo(grpUCA.getData("GRP_ACCT_INFO"));
        }
        
        String brandCode = ProductInfoIntfViewUtil.qryBrandCodeStrByProductId(this, productId);
        
        //如果是bboss产品，有商品和产品的分别，资费一般挂在产品下面，为了不影响之前的流程，这部分元素信息不展示在界面上
        if("BOSG".equals(brandCode))
        {
        	IDataset relaBBInfoList = RelationBBInfoIntfViewUtil.qryRelaBBInfoByRoleCodeBForGrp(this,userId, "90", "0");

            // 6- 循环BB关系数据，添加产品信息
            for (int i = 0; i < relaBBInfoList.size(); i++)
            {
                // 获取子产品的用户编号
                String userIdB = relaBBInfoList.getData(i).getString("USER_ID_B");
                
                IDataset bossProductElements = UserProductElementInfoIntfViewUtil.qryGrpUserElementInfosByUserId(this, userIdB);
                IDataset dctDataset = DataHelper.filter(bossProductElements, "ELEMENT_TYPE_CODE=D");
                for (int j = 0, jSize = dctDataset.size(); j < jSize; j++) {
                	String discntId = dctDataset.getData(j).getString("ELEMENT_ID");
                	String packageId = dctDataset.getData(j).getString("PACKAGE_ID");
        			String cancelTag = PackageElementInfoIntfViewUtil.qryCancelTagStrByPackageIdAndElementIdElementTypeCode(this, packageId, discntId, "D");
        			if (!"".equals(cancelTag) && "4".equals(cancelTag)) {
        				CSViewException.apperr(GrpException.CRM_GRP_890, discntId);
        			}
                }
                
            }
            
        	
        }

        IDataset productElements = UserProductElementInfoIntfViewUtil.qryGrpUserElementInfosByUserId(this, userId);
        IDataset dctDataset = DataHelper.filter(productElements, "ELEMENT_TYPE_CODE=D");
        for (int j = 0, jSize = dctDataset.size(); j < jSize; j++) {
        	String discntId = dctDataset.getData(j).getString("ELEMENT_ID");
        	String packageId = dctDataset.getData(j).getString("PACKAGE_ID");
			String cancelTag = PackageElementInfoIntfViewUtil.qryCancelTagStrByPackageIdAndElementIdElementTypeCode(this, packageId, discntId, "D");
			if (!"".equals(cancelTag) && "4".equals(cancelTag)) {
				CSViewException.apperr(GrpException.CRM_GRP_890, discntId);
			}
        }
        IDataset dctDatasetAdd = DataHelper.filter(productElements, "PACKAGE_ID=91300002,ELEMENT_TYPE_CODE=D");
        for (int i = 0, jSize = dctDatasetAdd.size(); i < jSize; i++) {
			String DiscntId = dctDatasetAdd.getData(i).getString(
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
        IData elemment = new DataMap();
        GroupBaseView.processProductElements(this, productElements, elemment);
        setUserProduct(elemment);
        
        //add by chenzg@20180705-begin-REQ201804280001集团合同管理界面优化需求--
        IData infoData = new DataMap();
        IData iparam = new DataMap();
        iparam.put("SUBSYS_CODE", "CSM");
        iparam.put("PARAM_ATTR", "839");
        iparam.put("PARAM_CODE", productId);
        iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        IDataset resultSet = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaByParamattr", iparam);
        if (IDataUtil.isNotEmpty(resultSet)){
        	infoData.put("AUDIT_INFO_SHOW","false");
        }else{
        	infoData.put("AUDIT_INFO_SHOW","true");
        }
        this.setInfo(infoData);
        //add by chenzg@20180705-end-REQ201804280001集团合同管理界面优化需求----
        
    }

    public abstract void setGrpAcctInfo(IData grpAcctInfo);

    public abstract void setGrpCustInfo(IData grpCustInfo);

    public abstract void setGrpUserInfo(IData grpUserInfo);

    public abstract void setInfo(IData info);

    public abstract void setUserProduct(IData info);

}
