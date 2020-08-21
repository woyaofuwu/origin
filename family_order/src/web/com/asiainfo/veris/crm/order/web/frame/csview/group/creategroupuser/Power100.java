
package com.asiainfo.veris.crm.order.web.frame.csview.group.creategroupuser;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcompinfo.ProductCompInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcomprelainfo.ProductCompRelaInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class Power100 extends GroupBasePage
{

    private IDataset users;

    private IDataset products = new DatasetList(); // 暂存所有产品的定制信息 ESOP

    public abstract String getGrpProductTreeSelected();

    /**
     * @author hud 2009-08-10
     */
    public void initial(IRequestCycle cycle) throws Exception
    {

        this.productId = getData().getString("GRP_PRODUCT_ID");
        setProductId(productId);
        setBrandCode(ProductInfoIntfViewUtil.qryBrandCodeStrByProductId(this, productId));

        queryPackages(cycle);

        // 查询可选产品和必选产品
        IDataset eos = null;
        String eosStr = getData().getString("EOS");
        if (!StringUtils.isEmpty(eosStr) && !"{}".equals(eosStr))
        {
            eos = new DatasetList(eosStr);
        }

        if (null != eos && eos.size() > 0)
        {
            IData param = eos.getData(0);
            IData inData = new DataMap();
            inData.put("X_TRANS_CODE", "ITF_EOS_QcsGrpBusi");
            inData.put("X_SUBTRANS_CODE", "GetEosInfo");
            inData.put("NODE_ID", param.getString("NODE_ID", ""));
            inData.put("IBSYSID", param.getString("IBSYSID", ""));
            inData.put("SUB_IBSYSID", param.getString("SUB_IBSYSID", ""));
            inData.put("OPER_CODE", "16");
            // ESOP接口
            IDataset httpResultSet = CSViewCall.call(this, "SS.ESOPQcsGrpBusiIntfSvc.getEosInfo", inData);
            if (IDataUtil.isEmpty(httpResultSet))
                CSViewException.apperr(GrpException.CRM_GRP_508, "接口返回数据为空");
            IData httpResult = httpResultSet.getData(0);
            if (!"0".equals(httpResult.getString("X_RSPCODE")))
            {
                CSViewException.apperr(GrpException.CRM_GRP_508, httpResult.getString("X_RSPDESC"));
            }
            products = httpResult.getDataset("PRODUCTS");
        }
        getData().put("PRODUCT_NAME", ProductInfoIntfViewUtil.qryProductNameStrByProductId(this, productId));
        queryProducts(cycle);
        setInfo(getData());
    }

    /**
     * 查用户可以选择的产品，根据选择的资费包
     * 
     * @author hud
     * @version 创建时间：2009-08-20 下午08:54:28
     */
    public void queryCanSelectProducts(IRequestCycle cycle) throws Exception
    {
        if ("1".equals(getData().getString("REFESH")))
        {
            users = queryUsers(cycle);
        }
        String packageId = productId;

        IData inparams = new DataMap();
        inparams.put("PRODUCT_ID", packageId);
        inparams.put("RELATION_TYPE_CODE", "1");
        inparams.put("FORCE_TAG", "0");// 0可选，1必选

        IDataset ds = ProductCompRelaInfoIntfViewUtil.qryProductCompRelaInfosByProductIdARelationTypeCodeAndForceTag(this, packageId, "4", "");
        if(IDataUtil.isNotEmpty(ds)){
            for(int i = ds.size()-1; i >= 0; i--) 
            {
                IData comRel = ds.getData(i);
                String forceTag = comRel.getString("FORCE_TAG","0");
                if(!forceTag.equals("0"))
                    ds.remove(i);
            }
        }
        
        IDataset temp = new DatasetList();
        IData temp1 = null;

        for (int i = 0, size = ds.size(); i < size; i++)
        {
            IData d = ds.getData(i);
            for (int j = 0, sizes = users.size(); j < sizes; j++)
            {
                IData user = users.getData(j);

                if (user.getString("PRODUCT_ID").equals(d.getString("PRODUCT_ID_B")))
                {
                    if ("true".equals(d.getString("IS_EXIST")))
                    {
                        temp1 = new DataMap();
                        d.put("IS_MANY", "true");// 同一产品多个用户
                        temp1.putAll(d);
                        temp1.put("USER_ID", user.getString("USER_ID"));
                        temp1.put("OPEN_DATE", user.getString("OPEN_DATE"));
                        temp1.put("SERIAL_NUMBER", user.getString("SERIAL_NUMBER"));
                        temp1.put("EPARCHY_CODE", user.getString("EPARCHY_CODE"));
                        temp.add(temp1);
                    }
                    else
                    {
                        d.put("IS_EXIST", "true");
                        d.put("USER_ID", user.getString("USER_ID"));
                        d.put("OPEN_DATE", user.getString("OPEN_DATE"));
                        d.put("SERIAL_NUMBER", user.getString("SERIAL_NUMBER"));
                        d.put("EPARCHY_CODE", user.getString("EPARCHY_CODE"));
                    }
                }
            }
        }

        temp.addAll(ds);

        IDataset result = new DatasetList();
        IDataset temp2 = null;
        for (int x = 0; x < temp.size(); x++)
        {
            String product_id = temp.getData(x).getString("PRODUCT_ID_B");
            temp2 = DataHelper.filter(result, "PRODUCT_ID_B=" + product_id);
            if (temp2 != null && temp2.size() > 0)
            {
                continue;
            }
            result.addAll(DataHelper.filter(temp, "PRODUCT_ID_B=" + product_id));
        }
        // ESOP add
        if (null != products && products.size() > 0)
        {
            for (int j = 0; j < result.size(); j++)
            {
                IData pro = result.getData(j);
                for (int x = 0; x < products.size(); x++)
                {
                    String product_id_b = products.getData(x).getString("ATTR_VALUE", "");
                    if (pro.getString("PRODUCT_ID_B", "").equals(product_id_b))
                    {
                        pro.put("IS_CHECK", "true");
                    }
                }

            }
        }

        setCanSelectProducts(result);

    }

    /**
     * 查询资费包信息
     * 
     * @author hud
     * @version 创建时间：2009-08-20 下午08:55:04
     */
    public void queryPackages(IRequestCycle cycle) throws Exception
    {
        IDataset ds = ProductCompRelaInfoIntfViewUtil.qryProductCompRelaInfosByProductIdARelationTypeCode(this, productId, "0");
        setPackages(ds);

    }

    /**
     * 查已经选择的产品和可选择的产品
     * 
     * @author hud
     * @version 创建时间：2009-08-20 下午09:13:52
     */
    public void queryProducts(IRequestCycle cycle) throws Exception
    {

        users = queryUsers(cycle);
        queryUserProducts(cycle); // 必选产品
        queryCanSelectProducts(cycle); // 可选产品

    }

    /**
     * 查询用户必选的产品
     * 
     * @author hud
     * @version 创建时间：2009-08-20 下午08:52:48
     */
    public void queryUserProducts(IRequestCycle cycle) throws Exception
    {
        if ("1".equals(getData().getString("REFESH")))
        {
            users = queryUsers(cycle);
        }
        String packageId = productId;

        IData inparams = new DataMap();
        inparams.put("PRODUCT_ID", packageId);
        inparams.put("RELATION_TYPE_CODE", "1");
        inparams.put("FORCE_TAG", "1");// 0可选，1必选

        IDataset ds = ProductCompRelaInfoIntfViewUtil.qryProductCompRelaInfosByProductIdARelationTypeCodeAndForceTag(this, packageId, "4", "");

        if(IDataUtil.isNotEmpty(ds)){
            for(int i = ds.size()-1; i >= 0; i--)
            {
                IData comRel = ds.getData(i);
                String forceTag = comRel.getString("FORCE_TAG","0");
                if(forceTag.equals("0"))
                    ds.remove(i);
            }
        }
        setGrpCompixProduct(ds.toString());

        IDataset temp = new DatasetList();
        IData temp1 = null;

        for (int i = 0, size = ds.size(); i < size; i++)
        {
            IData d = ds.getData(i);
            for (int j = 0, sizes = users.size(); j < sizes; j++)
            {
                IData user = users.getData(j);
                if (user.getString("PRODUCT_ID").equals(d.getString("PRODUCT_ID_B")))
                {
                    if ("true".equals(d.getString("IS_EXIST")))
                    {
                        temp1 = new DataMap();
                        d.put("IS_MANY", "true");// 同一产品多个用户
                        temp1.putAll(d);
                        temp1.put("USER_ID", user.getString("USER_ID"));
                        temp1.put("OPEN_DATE", user.getString("OPEN_DATE"));
                        temp1.put("SERIAL_NUMBER", user.getString("SERIAL_NUMBER"));
                        temp1.put("EPARCHY_CODE", user.getString("EPARCHY_CODE"));
                        temp.add(temp1);
                    }
                    else
                    {
                        d.put("IS_EXIST", "true");
                        d.put("USER_ID", user.getString("USER_ID"));
                        d.put("OPEN_DATE", user.getString("OPEN_DATE"));
                        d.put("SERIAL_NUMBER", user.getString("SERIAL_NUMBER"));
                        d.put("EPARCHY_CODE", user.getString("EPARCHY_CODE"));
                    }
                }
            }
        }
        ds.addAll(temp);
        // ESOP add
        if (null != products && products.size() > 0)
        {
            for (int j = 0; j < ds.size(); j++)
            {
                IData pro = ds.getData(j);
                for (int x = 0; x < products.size(); x++)
                {
                    String product_id_b = products.getData(x).getString("ATTR_VALUE", "");
                    if (pro.getString("PRODUCT_ID_B", "").equals(product_id_b))
                    {
                        pro.put("IS_CHECK", "true");
                    }
                }

            }
        }

        setProducts(ds);

    }

    /**
     * 查询用户信息
     * 
     * @author hud
     * @version 创建时间：2009-08-20 下午07:33:11
     */
    public IDataset queryUsers(IRequestCycle cycle) throws Exception
    {

        String custId = getData().getString("CUST_ID", "");
        // 客户下 集团用户
        IDataset users = UCAInfoIntfViewUtil.qryGrpUserInfoByCustId(this, custId, false);
        if (productId == null)
        {
            productId = getData().getString("PRODUCT_ID");
        }

        // 动力100的产品关系类型 92
        String relationTypeCode = ProductCompInfoIntfViewUtil.qryRelationTypeCodeStrByProductId(this, productId);
        // 排除已经 组成其他动力100的产品 对应的用户
        IDataset temp = new DatasetList();
        for (int i = 0, size = users.size(); i < size; i++)
        {
            String userIdB = users.getData(i).getString("USER_ID");
            IDataset expt = RelationUUInfoIntfViewUtil.qryGrpRelaUUInfosByUserIdBAndRelationTypeCode(this, userIdB, relationTypeCode, false);
            if (IDataUtil.isEmpty(expt))
            {
                temp.add(users.getData(i));
            }
        }

        // 动力100下产品对应的用户列表
        return temp;
    }

    public abstract void setBrandCode(String brandCode);// 品牌标识

    public abstract void setCanSelectProducts(IDataset canSelectProducts);// 可以选择的产品

    public abstract void setGrpCompixProduct(String str);

    public abstract void setGrpProductTreeSelected(String grpProductTreeSelected);// 已选择的产品

    public abstract void setInfo(IData info);

    public abstract void setPackages(IDataset packages);// 资费包

    public abstract void setProducts(IDataset products);// 必选的产品

    public abstract void setTemp(IData info);

    public abstract void setUserInfos(IDataset userInfos);
}
