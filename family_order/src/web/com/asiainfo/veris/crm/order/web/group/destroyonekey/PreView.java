
package com.asiainfo.veris.crm.order.web.group.destroyonekey;

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
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userblackwhiteinfo.UserblackwhiteIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcompinfo.ProductCompInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBaseView;

public abstract class PreView extends GroupBasePage
{
    public void initial(IRequestCycle cycle) throws Exception
    {

        String userId = getData().getString("GRP_USER_ID");// 集团用户ID
        String productId = getData().getString("GRP_PRODUCT_ID", "");
        IData attrValuedata = AttrBizInfoIntfViewUtil.qryAttrBizInfoByIdAndIdTypeAttrObjAttrCode(this,"1","B","PREDESTORY",productId);
        if(IDataUtil.isNotEmpty(attrValuedata))
        {
            CSViewException.apperr(GrpException.CRM_GRP_880);
        }
        // 查询集团三户信息
        IData grpUCA = UCAInfoIntfViewUtil.qryGrpUCAInfoByUserId(this, userId);
        if (IDataUtil.isNotEmpty(grpUCA))
        {
            setGrpCustInfo(grpUCA.getData("GRP_CUST_INFO"));
            setGrpUserInfo(grpUCA.getData("GRP_USER_INFO"));
            setGrpAcctInfo(grpUCA.getData("GRP_ACCT_INFO"));
        }
        IDataset productElements = UserProductElementInfoIntfViewUtil.qryGrpUserElementInfosByUserId(this, userId);
        IData elemment = new DataMap();
        GroupBaseView.processProductElements(this, productElements, elemment);
        setUserProduct(elemment);

        IDataset dctDataset = DataHelper.filter(productElements, "ELEMENT_TYPE_CODE=D");
        for (int j = 0, jSize = dctDataset.size(); j < jSize; j++) {
            String discntId = dctDataset.getData(j).getString("ELEMENT_ID");
            String packageId = dctDataset.getData(j).getString("PACKAGE_ID");
            String cancelTag = UpcViewCall.getCancelTagByElementTypeCodeAndElementId(this, "D",discntId);
            if (!"".equals(cancelTag) && "4".equals(cancelTag)) {
                CSViewException.apperr(GrpException.CRM_GRP_890, discntId);
            }
        }

        // 查询成员信息
        String relationTypeCode = ProductCompInfoIntfViewUtil.qryRelationTypeCodeStrByProductId(this, productId);

        String brandCode = ProductInfoIntfViewUtil.qryBrandCodeStrByProductId(this, productId);

        String mebCount = "0";

        if ("BOSG".equals(brandCode))
        {
            mebCount = RelationBBInfoIntfViewUtil.qryCountByUserIdAAndRelationTypeCodeAllCrm(this, userId, relationTypeCode);
        }
        else if ("ADCG".equals(brandCode) || "MASG".equals(brandCode))
        {
           //adc,mas 产品因为有异网号码，需要查询 tf_f_user_blackwhite表的数据
            IDataset mebCountIdata = UserblackwhiteIntfViewUtil.qryUserblackwhiteCountByEcUserId(this, userId);
            mebCount = mebCountIdata.getData(0).getString("USER_COUNT");
        }
        else
        {
            mebCount = RelationUUInfoIntfViewUtil.qryCountByUserIdAAndRelationTypeCodeAllCrm(this, userId, relationTypeCode);

        }

        setMebCount(mebCount);
        
        //add by chenzg@20180705-begin-REQ201804280001集团合同管理界面优化需求--
        IData infoData = new DataMap();
        IData iparam = new DataMap();
        iparam.put("SUBSYS_CODE", "CSM");
        iparam.put("PARAM_ATTR", "839");
        iparam.put("PARAM_CODE", productId);
        iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        IDataset resultSet = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaByParamattr", iparam);
        if (IDataUtil.isNotEmpty(resultSet)){
        	infoData.put("MEB_VOUCHER_FILE_SHOW","false");
        }else{
        	infoData.put("MEB_VOUCHER_FILE_SHOW","true");
        }
        //add by chenzg@20180705-end---REQ201804280001集团合同管理界面优化需求--
        this.setInfo(infoData);
    }

    public abstract void setGrpAcctInfo(IData grpAcctInfo);

    public abstract void setGrpCustInfo(IData grpCustInfo);

    public abstract void setGrpUserInfo(IData grpUserInfo);

    public abstract void setInfo(IData info);

    public abstract void setUserProduct(IData info);

    public abstract void setMebCount(String mebCount);
}
