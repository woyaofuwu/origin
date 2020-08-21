
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.packageelement;

import com.ailk.biz.BizVisit;
import com.ailk.biz.util.StaticUtil;
import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.product.packageelement.PackageElementInfoIntf;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

public class PackageElementInfoIntfViewUtil
{
    /**
     * 通过包ID 和地州编码查询包下的元素信息 ,有元素权限过滤
     *
     * @param bc
     * @param packageId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset qryElementInfosByPackageIdAndEparchyCodeHasPriv(IBizCommon bc, String packageId, String eparchyCode) throws Exception
    {
        return PackageElementInfoIntf.qryElementInfosByPackageIdAndEparchyCodeHasPriv(bc, packageId, eparchyCode);
    }

    /**
     * 通过元素ID ，元素类型，元素所属的包 查询元素的必选标记
     *
     * @param visit
     * @param packageId
     * @param elementId
     * @param elemnetTypeCode
     * @return
     * @throws Exception
     */
    public static String qryForceTagStrByPackageIdAndElementIdElementTypeCode(BizVisit visit, String packageId, String elementId, String elemnetTypeCode) throws Exception
    {
        return StaticUtil.getStaticValue(visit, "TD_B_PACKAGE_ELEMENT", new String[]
        { "ELEMENT_ID", "PACKAGE_ID", "ELEMENT_TYPE_CODE" }, "FORCE_TAG", new String[]
        { elementId, packageId, elemnetTypeCode });
    }

    /**
     * 通过元素ID ，元素类型，元素所属的包 查询元素的取消方式
     *
     * @param visit
     * @param packageId
     * @param elementId
     * @param elemnetTypeCode
     * @return
     * @throws Exception
     */
    public static String qryCancelTagStrByPackageIdAndElementIdElementTypeCode(IBizCommon bc, String packageId, String elementId, String elemnetTypeCode) throws Exception
    {
        String cancelTag = "";
        IDataset reuslts = UpcViewCall.qryOfferFromGroupByGroupIdOfferId(bc, packageId, elemnetTypeCode, elementId);
        if (IDataUtil.isNotEmpty(reuslts))
        {
            cancelTag = reuslts.first().getString("CANCEL_TAG", ""); 
        }
        
        return cancelTag;
    }

}
