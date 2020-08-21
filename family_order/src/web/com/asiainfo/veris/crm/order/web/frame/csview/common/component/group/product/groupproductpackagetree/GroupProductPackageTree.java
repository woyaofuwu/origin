
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.product.groupproductpackagetree;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.web.BaseTempComponent;

public abstract class GroupProductPackageTree extends BaseTempComponent
{
    //
    // // private final static String SCRIPT =
    // // "com/linkage/saleserv/core/components/group/product/groupproductpackagetree/GroupProductPackageTree.script";
    //
    // // private final static String SCRIPT_ATTRIBUTE = GroupProductPackageTree.class.getName();
    //
    //
    // // protected void getStaticTreeData(BizContext bizContext) throws Exception {
    // // /** ������ */
    // // TreeItem root1 = new TreeItem("root", null, null, null);
    // //
    // // IData productInfo = ProductUtil.getProductInfo(bizContext,getMainProductId());
    // //
    // // if (productInfo==null) return;
    // //
    // // TreeItem node0 = new TreeItem("node", root1, productInfo.getString("PRODUCT_NAME"), null);
    // //
    // // TreeItem node1 = new TreeItem("node", node0,"���Ų�Ʒ", null);
    // //
    // // IDataset mainProductPackages = ProductUtil.getPackageByProduct(bizContext,
    // getMainProductId(),getRouteEparchyCode());
    // //
    // // setPackageNode(node1,mainProductPackages,true,true);
    // //
    // // //-----------------------���Ÿ��Ӳ�Ʒ----------------------------------------
    // // setPlusProducts(getPlusProducts()==null?new DatasetList():getPlusProducts()) ;
    // //
    // // if (getPlusProducts().size() >0){
    // // TreeItem node2 = new TreeItem("node", node0,"���Ÿ��Ӳ�Ʒ", null);
    // //
    // // for (int i = 0; i < getPlusProducts().size(); i++) {
    // // IData plusProduct = getPlusProducts().getData(i);
    // //
    // // IData plusProductInfo = ProductUtil.getProductInfo(bizContext,plusProduct.getString("PRODUCT_ID"));
    // //
    // // TreeItem nodetmp0 = new TreeItem("node", node2, plusProductInfo.getString("PRODUCT_NAME"), null);
    // //
    // // IDataset plusProductPackages = ProductUtil.getPackageByProduct(bizContext,
    // // plusProduct.getString("PRODUCT_ID"),getRouteEparchyCode());
    // //
    // // setPackageNode(nodetmp0,plusProductPackages,true,true);
    // //
    // // }
    // // }
    // //
    // // //-----------------------���ų�Ա���Ʒ----------------------------------
    // //
    // //
    // // if (getMebProductId()!=null && getMebProductId().length()>0){
    // // TreeItem node3 = new TreeItem("node", node0,"��Ա��Ʒ", null);
    // //
    // // IData memberProductInfo = ProductUtil.getProductInfo(bizContext,getMebProductId());
    // //
    // // IDataset memberProductPackages ;
    // //
    // // memberProductPackages = ProductUtil.getPackageByProduct(bizContext, getMebProductId(),getRouteEparchyCode());
    // //
    // // setPackageNode(node3,memberProductPackages,false,false);
    // // }
    // //
    // // // if (ProductCompInfoQry.ifGroupCustomize(bizContext, getMainProductId())){
    // // // //���ſɶ��� ����Ҫѡ���Ա�ɶ��Ƶ�Ԫ��
    // // //
    // // // TreeItem node32 = new TreeItem("node", node0,"��Ա��Ʒ", null);
    // // //
    // // // //��ȡ��Ա��Ʒ��Ĭ��ѡ�еİ�
    // // // IDataset mebProducts = ProductUtil.getMebProduct(bizContext,getMainProductId());
    // // //
    // // // for (int i = 0; i < mebProducts.size(); i++) {
    // // // IData product = (IData)mebProducts.get(i);
    // // // TreeItem nodetmp1 = new TreeItem("node", node32, product.getString("PRODUCT_NAME"), null);
    // // //
    // // //
    // // // IDataset memberPackages;
    // // // memberPackages = ProductUtil.getPackageByProduct(bizContext,product.getString("PRODUCT_ID"));
    // // //
    // // // setPackageNode(nodetmp1,memberPackages);
    // // // }
    // // // }
    // //
    // // //----------------------��Ա���Ӳ�Ʒ----------------------------------------
    // //
    // // //��ӳ�Ա���Ӳ�Ʒ��
    // //
    // // setMebPlusProducts(getMebPlusProducts()==null?new DatasetList():getMebPlusProducts()) ;
    // //
    // // if (getMebPlusProducts().size() >0){
    // // TreeItem node4 = new TreeItem("node", node0, "��Ա���Ӳ�Ʒ", null);
    // //
    // // for (int i = 0; i < getMebPlusProducts().size(); i++) {
    // // IData plusProduct = getMebPlusProducts().getData(i);
    // //
    // // IData plusProductInfo = ProductUtil.getProductInfo(bizContext,plusProduct.getString("PRODUCT_ID"));
    // //
    // // if (plusProductInfo==null) continue;
    // //
    // // TreeItem nodetmp2 = new TreeItem("node", node4, plusProductInfo.getString("PRODUCT_NAME"), null);
    // //
    // // IDataset plusProductPackages;
    // // // if (ProductCompInfoQry.ifGroupCustomize(bizContext, getMainProductId())){
    // // // //���Ŷ���
    // // // IData tmp = new DataMap();
    // // // tmp.put("PRODUCT_ID", plusProduct.getString("PRODUCT_ID"));
    // // // tmp.put("USER_ID", getGrpUserId());
    // // // plusProductPackages = PkgInfoQry.getPackageByProductFromGrpPck(bizContext, tmp);
    // // // }
    // // // else{
    // // // plusProductPackages = ProductUtil.getPackageByProduct(bizContext, plusProduct.getString("PRODUCT_ID"));
    // // // }
    // //
    // // plusProductPackages = ProductUtil.getPackageByProduct(bizContext,
    // // plusProduct.getString("PRODUCT_ID"),getRouteEparchyCode());
    // //
    // // setPackageNode(nodetmp2,plusProductPackages,false,false);
    // //
    // // }
    // // }
    // //
    // // treeItem = root1;
    // //
    // // }
    // //
    // // /**
    // // * ����ڵ�ҵ���Ʒ�ڵ���
    // // * @param node
    // // * @param packages
    // // * @throws Exception
    // // */
    // // private void setPackageNode(TreeItem node,IDataset packages,boolean needServParam,boolean verifyEleMinMax)
    // throws
    // // Exception{
    // //
    // // for (int j = 0; j < packages.size(); j++) {
    // // IData productPackage = packages.getData(j);
    // //
    // // productPackage.put("NEED_SERV_PARAM", needServParam?"true":"false");
    // // productPackage.put("VERIFY_ELE_MIN_MAX", verifyEleMinMax?"true":"false");
    // //
    // // String tmpCheckboxAction = getCheckboxAction()==null?"":getCheckboxAction();
    // // tmpCheckboxAction = TreeFactory.getStrByReplace(tmpCheckboxAction, productPackage);
    // // TreeNodeParam checkbox = new TreeNodeParam("groupproductpackage_checkbox_" +
    // // productPackage.getString("PRODUCT_ID") + "_" + productPackage.getString("PACKAGE_ID"),
    // // productPackage.getString("PACKAGE_ID"), "", tmpCheckboxAction);
    // //
    // // // if ("1".equals(productPackage.getString("FORCE_TAG"))){
    // // // checkbox.setDisabled(true);
    // // // }
    // //
    // // String tmpTextAction = getTextAction()==null?"":getTextAction();
    // // tmpTextAction = TreeFactory.getStrByReplace(tmpTextAction, productPackage);
    // //
    // // TreeItem nod_type = new TreeItem("node_" + productPackage.getString("PRODUCT_ID") + "^" +
    // // productPackage.getString("PACKAGE_ID"), node,
    // productPackage.getString("PACKAGE_NAME"),tmpTextAction,checkbox);
    // //
    // // }
    // // }
    //
    protected void cleanupAfterRender(IRequestCycle cycle)
    {

        super.cleanupAfterRender(cycle);
        if (!cycle.isRewinding())
        {

            setMebPlusProducts(null);
            setGrpUserId(null);
            setRouteEparchyCode(null);
            setPlusProducts(null);
            setMebProductId(null);
        }
    }

    public abstract String getCheckboxAction();

    public abstract String getClassName();

    public abstract String getGrpUserId();

    public abstract String getMainProductId();

    public abstract IDataset getMebPlusProducts();

    public abstract String getMebProductId();

    public abstract String getMethod();

    public abstract IDataset getPlusProducts();

    public abstract String getPrivForPack();

    public abstract String getRouteEparchyCode();

    public abstract String getTextAction();

    public abstract IData getTreeParams();

    public void loadGrpPackageTrees(IRequestCycle cycle) throws Exception
    {

    }

    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle)
    {

        if (cycle.isRewinding())
            return;

        // Util.renderScript(cycle, SCRIPT, SCRIPT_ATTRIBUTE);
        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/group/groupproductpackagetree/GroupProductPackageTree.js");
        // body.includeExternalScript("scripts/component/group/product/groupproductpackagetree/GroupProductPackageTree.js",
        // true);
        // body.addInitializationScript(writer, "groupProductPackageTreeInit();");
        StringBuilder init_script = new StringBuilder();
        init_script.append("$(document).ready(function(){\r\n");
        init_script.append("\t groupProductPackageTreeInit();\r\n");
        init_script.append("});\r\n");

        getPage().addScriptBeforeBodyEnd("_init", init_script.toString());
        try
        {

            IData params = new DataMap();

            params.put("PRIV_FOR_PACK", this.getPrivForPack() == null ? "" : this.getPrivForPack());
            String route = getRouteEparchyCode();
            // route = (route == null || route.trim().length() == 0) ? bizContext.getVisit().getRouteEparchyCode() :
            // route;
            // setRouteEparchyCode(route);

            params.put("MAIN_PRODUCT_ID", getMainProductId());
            params.put("EPARCHY_CODE", route);
            params.put("METHOD_NAME", getMethod());
            params.put("PLUS_PRODUCTS", this.getPlusProducts() == null || this.getPlusProducts().equals("") ? new DatasetList() : getPlusProducts());
            params.put("MEB_PRODUCT_ID", this.getMebProductId() == null ? "" : getMebProductId());
            params.put("MEB_PLUS_PRODUCTS", this.getMebPlusProducts() == null || this.getMebPlusProducts().equals("") ? new DatasetList() : getMebPlusProducts());
            params.put("CHECKBOX_ACTION", this.getCheckboxAction() == null ? "" : getCheckboxAction());
            params.put("TEXT_ACTION", this.getTextAction() == null ? "" : getTextAction());
            IData treeparams = getTreeParams();
            if (treeparams != null)
                params.putAll(treeparams);
            setTreeParams(params);

            // getStaticTreeData(getPageData());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        super.renderComponent(writer, cycle);
    }

    public abstract void setGrpUserId(String grpUserId);

    public abstract void setMebPlusProducts(IDataset mebPlusProducts);

    public abstract void setMebProductId(String mebProductId);

    public abstract void setPlusProducts(IDataset plusProducts);

    public abstract void setRouteEparchyCode(String routeEparchyCode);

    public abstract void setTreeParams(IData treeparams);
}
