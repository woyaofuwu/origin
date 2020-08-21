
package com.asiainfo.veris.crm.order.web.group.bat.batbbossmember;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.bbossattrinfo.BBossAttrInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcompinfo.ProductCompInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcomprelainfo.ProductCompRelaInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productmebinfo.ProductMebInfoIntfViewUtil;

public abstract class BatBbossMember extends CSBasePage
{
    /**
     * 初始化方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        setCondition(getData());
    }

    /**
     * 查询用户资费信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void qryUserDiscnt(IRequestCycle cycle) throws Exception
    {
        // 批量类型
        String batchOperType = getData().getString("BATCH_OPER_TYPE", "");
        // 产品用户userId
        String userId = getData().getString("USER_ID");
        // 省内产品ID(merchp)
        String productId = getData().getString("PRODUCT_ID");

        boolean isNeedParam = false;

        if ("BATDELBBOSSMEMBER".equals(batchOperType)) // 成员删除
        {
            //setMessage("注销该产品的成员，表格中仅填写手机号码即可！");
            setCanAccept("true");
            return;
        }
        else if (batchOperType.matches("BATMODBBOSSMEMBER|BATPASBBOSSMEMBER|BATCONBBOSSMEMBER")) // 成员变更
        {
            IDataset bizAttrList = AttrBizInfoIntfViewUtil.qryAttrBizInfosByIdAndIdTypeAttrObjAttrCode(this, "1", "B", "MOPER", productId);

            if (IDataUtil.isEmpty(bizAttrList))
            {
                setMessage("该产品的成员无法做成员暂停、恢复、修改！");
                setCanAccept("false");
                return;
            }

            String oper = bizAttrList.getData(0).getString("ATTR_VALUE");

            if (StringUtils.isBlank(oper))
                return;

            String[] operArray = oper.split(",");

            // 遍历操作类型
            for (int i = 0, row = operArray.length; i < row; i++)
            {
                String operType = operArray[i];

                if ("BATPASBBOSSMEMBER".equals(batchOperType) && "3".equals(operType))
                {
                    setMessage("暂停该产品的成员，表格中仅填写手机号码即可！");
                    setCanAccept("true");
                    return;
                }
                else if ("BATCONBBOSSMEMBER".equals(batchOperType) && "4".equals(operType))
                {
                    setMessage("恢复该产品的成员，表格中仅填写手机号码即可！");
                    setCanAccept("true");
                    return;
                }
                else if ("6".equals(operType))
                {
                    isNeedParam = true;
                    setMessage("变更成员属性，表格中新的属性值即可！");
                    setCanAccept("true");
                    return;
                }
            }

            if (isNeedParam == false)
            {
                setMessage("该产品的成员不支持此类型的批量操作！");
                setCanAccept("false");
                return;
            }
        }

        // 成员新增
        if ("BATADDBBOSSMEMBER".equals(batchOperType))
        {
            // 查询集团定制优惠信息
            IData svcData = new DataMap();
            IData merchNumInfo = AttrBizInfoIntfViewUtil.qryAttrBizInfoByIdAndIdTypeAttrObjAttrCode(this, "1", "B", "PRO", productId);
            if (IDataUtil.isEmpty(merchNumInfo))
            {
                setMessage("表格中不需要填写参数，仅填写手机号和操作类型！");
                setCanAccept("true");
                return;
            }
            String merchId = merchNumInfo.getString("ATTR_VALUE");
            svcData.put("USER_ID", userId);

            IDataset userDiscntList = CSViewCall.call(this, "CS.UserGrpPkgInfoQrySVC.qryGrpCustomizeDiscntByUserId", svcData);

            setUserDiscntList(userDiscntList);

            IDataset bbossAttrList = BBossAttrInfoIntfViewUtil.qryBBossAttrInfosByProductIdAndOperTypeBizType(this, merchId, "1", "2");// bizType
            // 1表示集团;
            // 2表示成员
            if (IDataUtil.isEmpty(bbossAttrList))
            {
                setMessage("表格中不需要填写参数，仅填写手机号和操作类型！");
                setCanAccept("true");
                return;
            }

            IDataset paramList = new DatasetList();

            // 遍历属性信息
            for (int i = 0, row = bbossAttrList.size(); i < row; i++)
            {
                IData bbossAttrData = bbossAttrList.getData(i);

                IData paramData = new DataMap();
                paramData.put("PARAM_NAME", bbossAttrData.getString("ATTR_NAME"));
                paramData.put("PARAM_CODE", bbossAttrData.getString("ATTR_CODE"));
                paramData.put("EXECL_NAME", bbossAttrData.getString("RSRV_STR1"));

                paramList.add(paramData);
            }

            setCanAccept("true");
            setParamList(paramList);
        }
    }

    /**
     * 查询用户产品信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void qryUserProduct(IRequestCycle cycle) throws Exception
    {
        // BBOSS商品的ID
        String productId = getData().getString("PRODUCT_ID");
        String custid = getData().getString("CUST_ID");

        // 查询商品下面的产品信息
        IData svcData = new DataMap();

        svcData.put("PRODUCT_ID", productId);
        svcData.put("RELATION_TYPE_CODE", "1");

        IDataset productList = ProductCompRelaInfoIntfViewUtil.qryProductCompRelaInfosByProductIdARelationTypeCodeAndForceTag(this, productId, "4", null);

        // 查询用户产品信息
        svcData.clear();
        svcData.put("CUST_ID", custid);

        IDataset userList = UCAInfoIntfViewUtil.qryGrpUserInfoByCustId(this, custid, false);

        IDataset userProductList = new DatasetList();

        // 循环商品下面的产品信息
        for (int i = 0, iRow = productList.size(); i < iRow; i++)
        {
            IData productData = productList.getData(i);

            for (int j = 0, jRow = userList.size(); j < jRow; j++)
            {
                IData userData = userList.getData(j);

                if (productData.getString("PRODUCT_ID_B", "").equals(userData.getString("PRODUCT_ID")))
                {

                    // 判断是否可以添加成员
                    IDataset mebProductList = ProductMebInfoIntfViewUtil.qryProductMebInfosByProductIdPriv(this, productData.getString("PRODUCT_ID_B", ""));

                    if (IDataUtil.isNotEmpty(mebProductList))
                    {
                        IData addProductData = (IData) Clone.deepClone(productData);

                        addProductData.put("USER_ID", userData.getString("USER_ID"));
                        addProductData.put("SERIAL_NUMBER", userData.getString("SERIAL_NUMBER"));
                        addProductData.put("PRODUCT_SPEC_CODE", userData.getString("PRODUCT_ID"));
                        addProductData.put("PRODUCT_ID", userData.getString("PRODUCT_ID"));

                        userProductList.add(addProductData);
                    }
                }
            }
        }

        setUserProductList(userProductList);
    }

    public abstract void setCanAccept(String canAccept);

    public abstract void setCondition(IData condition);

    public abstract void setMessage(String message);

    public abstract void setParamList(IDataset paramList);

    public abstract void setUserDiscntList(IDataset userDiscntList);

    public abstract void setUserProductList(IDataset userProductList);
}
