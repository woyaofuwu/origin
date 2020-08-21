
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.usergrppackage;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.elementinfo.ElementInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.packageelement.PackageElementInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productpackage.ProductPkgInfoIntfViewUtil;

public abstract class UserGrpPackage extends CSBizTempComponent
{

    @Override
    protected void cleanupAfterRender(IRequestCycle cycle)
    {

        super.cleanupAfterRender(cycle);
        if (!cycle.isRewinding())
        {
            setElementList(null);
            setForcePkgList(null);
            setSourceElementList(null);
            setGrpPackageList(null);
            setOldGrpPackageList(null);
            setPackageInfo(null);
            setTreeParam(null);
        }
    }

    public void dealGrpPakcageInfo(String grpProductId, String eparchyCode, String grpUserId) throws Exception
    {

        // 组件初始化
        IData treeparam = new DataMap();
        if (StringUtils.isBlank(grpProductId))
        {
            treeparam.put("INIT_TAG", "false");
            setTreeParam(treeparam);
            return;
        }

        treeparam.put("METHOD_NAME", "loadGroupUserGrpPackageTree");
        treeparam.put("MAIN_PRODUCT_ID", grpProductId);
        treeparam.put("EPARCHY_CODE", eparchyCode);
        treeparam.put("INIT_TAG", "true");
        setTreeParam(treeparam);

        IDataset mebPkgList = ProductPkgInfoIntfViewUtil.qryMebForcePackageByGrpProId(this, grpProductId, eparchyCode);
        setForcePkgList(mebPkgList);

        IDataset elementList = new DatasetList();
        if (StringUtils.isNotBlank(grpUserId))
        {
            IData inparam = new DataMap();
            inparam.put("USER_ID", grpUserId);
            elementList = CSViewCall.call(this, "CS.UserGrpPkgInfoQrySVC.getUserGrpPackageForGrp", inparam);
            IDataset userGrpPackageList = new DatasetList();
            for (int i = 0; i < elementList.size(); i++)
            {
                IData temp = elementList.getData(i);
                IData userGrpPackage = new DataMap();
                userGrpPackage.put("PRODUCT_ID", temp.getString("PRODUCT_ID"));
                userGrpPackage.put("ELEMENT_ID", temp.getString("ELEMENT_ID"));
                userGrpPackage.put("PACKAGE_ID", temp.getString("PACKAGE_ID"));
                userGrpPackage.put("ELEMENT_FORCE_TAG", temp.getString("FORCE_TAG"));
                userGrpPackage.put("ELEMENT_NAME", temp.getString("ELEMENT_NAME"));
                userGrpPackage.put("ELEMENT_TYPE_CODE", temp.getString("ELEMENT_TYPE_CODE"));
                userGrpPackageList.add(userGrpPackage);
            }
            setGrpPackageList(userGrpPackageList);
            setOldGrpPackageList(userGrpPackageList);

        }
        else
        {
            IData inparam = new DataMap();
            inparam.put("PRODUCT_ID", grpProductId);
            inparam.put(Route.USER_EPARCHY_CODE, eparchyCode);
            elementList = CSViewCall.call(this, "CS.ProductInfoQrySVC.getMebProductForceElements", inparam);
            IDataset userGrpPackageList = new DatasetList();
            for (int i = 0; i < elementList.size(); i++)
            {
                IData temp = elementList.getData(i);
                IData userGrpPackage = new DataMap();
                userGrpPackage.put("PRODUCT_ID", temp.getString("PRODUCT_ID"));
                userGrpPackage.put("ELEMENT_ID", temp.getString("ELEMENT_ID"));
                userGrpPackage.put("PACKAGE_ID", temp.getString("PACKAGE_ID"));
                userGrpPackage.put("ELEMENT_FORCE_TAG", temp.getString("ELEMENT_FORCE_TAG"));
                userGrpPackage.put("ELEMENT_NAME", temp.getString("ELEMENT_NAME"));
                userGrpPackage.put("ELEMENT_TYPE_CODE", temp.getString("ELEMENT_TYPE_CODE"));
                userGrpPackageList.add(userGrpPackage);
            }
            setGrpPackageList(userGrpPackageList);
        }

    }

    /**
     * 为bboss修改的特殊逻辑 bboss在定制了资费后，需要第二次打开后，能够还原出第一次选择后的数据
     * 
     * @param sourceElementList
     *            传入组件内做了变动的元素
     * @param oldElementList
     *            变动前的数据
     * @return 定制信息内 已选区展示的定制信息集
     * @throws Exception
     */
    public IDataset dealUserGrpPakcageElementList(IDataset sourceElementList, IDataset oldElementList) throws Exception
    {
        IDataset result = new DatasetList();
        if (IDataUtil.isNotEmpty(oldElementList))
        {
            result.addAll(oldElementList);
        }

        if (IDataUtil.isEmpty(sourceElementList))
            return result;

        int srclen = sourceElementList.size();
        for (int i = 0; i < srclen; i++)
        {
            IData sourceElement = sourceElementList.getData(i);
            String elementId = sourceElement.getString("ELEMENT_ID", "");
            String modifyTag = sourceElement.getString("MODIFY_TAG", "");
            String elementTypeCode = sourceElement.getString("ELEMENT_TYPE_CODE", "");
            String packageId = sourceElement.getString("PACKAGE_ID", "");
            if (modifyTag.equals("0"))
            {
                IData addData = new DataMap();
                addData.put("PRODUCT_ID", sourceElement.getString("PRODUCT_ID"));
                addData.put("ELEMENT_ID", elementId);
                addData.put("PACKAGE_ID", packageId);
                addData.put("ELEMENT_FORCE_TAG", "0");//PackageElementInfoIntfViewUtil.qryForceTagStrByPackageIdAndElementIdElementTypeCode(getVisit(), packageId, elementId, elementTypeCode)
                addData.put("ELEMENT_NAME", ElementInfoIntfViewUtil.qryElementNameStrByElementIdAndElementTypeCode(this, elementId, elementTypeCode));
                addData.put("ELEMENT_TYPE_CODE", elementTypeCode);
                result.add(addData);
            }
            else if (modifyTag.equals("1"))
            {
                int resultLen = result.size();
                for (int k = 0; k < resultLen; k++)
                {
                    IData resultData = result.getData(k);
                    String tempElementId = resultData.getString("ELEMENT_ID", "");
                    String tempElementTypeCode = resultData.getString("ELEMENT_TYPE_CODE", "");
                    String tempPackageId = resultData.getString("PACKAGE_ID", "");
                    if (tempPackageId.equals(packageId) && tempElementTypeCode.equals(elementTypeCode) && tempElementId.equals(elementId))
                    {
                        result.remove(k);
                        break;
                    }
                }
            }
        }

        return result;
    }

    public abstract IDataset getElementList();

    public abstract String getGrpProductId();

    public abstract String getGrpUserEparchyCode();

    public abstract IDataset getOldGrpPackageList();

    public abstract IDataset getSourceElementList();

    public abstract String getUserId();

    public void loadGrpPackageTrees(IRequestCycle cycle) throws Exception
    {

    }

    @Override
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {

        if (cycle.isRewinding())
            return;
        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/group/usergrppackage/UserGrpPackage.js");
        IData inpara = this.getPage().getData();
        String tag = "";
        if (IDataUtil.isNotEmpty(inpara))
        {
            tag = inpara.getString("TAG", "");
        }
        // tag = 0 产品包刷新元素信息
        if (tag.equals("0"))
        {
            String productId = inpara.getString("PRODUCT_ID");
            String packageId = inpara.getString("PACKAGE_ID");
            String grpUserEparchyCode = inpara.getString("GRP_USER_EPARCHYCODE");
            IData inparam = new DataMap();
            inparam.put("PACKAGE_ID", packageId);
            inparam.put("PRODUCT_ID", productId);
            inparam.put("EPARCHY_CODE", grpUserEparchyCode);
            setPackageInfo(inparam);
            IDataset elementList = CSViewCall.call(this, "CS.PackageSVC.getPackageElements", inparam);
            setElementList(elementList);

        }
        else if (tag.equals("1"))
        {
            String productId = inpara.getString("GRP_PRODUCT_ID", "");
            String eparchyCode = inpara.getString("GRP_USER_EPARCHYCODE", "");
            String userId = inpara.getString("GRP_USER_ID", "");
            dealGrpPakcageInfo(productId, eparchyCode, userId);
        }
        else
        {
            dealGrpPakcageInfo(getGrpProductId(), getGrpUserEparchyCode(), getUserId());

            IDataset sourceList = getSourceElementList();
            if (IDataUtil.isNotEmpty(sourceList))
            {
                setGrpPackageList(dealUserGrpPakcageElementList(sourceList, getOldGrpPackageList()));
            }
        }

    }

    public abstract void setElementList(IDataset paramset);

    public abstract void setForcePkgList(IDataset forcePkgList);

    public abstract void setGrpPackageList(IDataset paramset);

    public abstract void setOldGrpPackageList(IDataset paramset);

    public abstract void setPackageInfo(IData packageInfo);

    public abstract void setSourceElementList(IDataset elemnetList);

    public abstract void setTreeParam(IData param);

}
