
package com.asiainfo.veris.crm.order.web.frame.csview.group.changeuserelement;

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

    private IDataset products = new DatasetList(); // 暂存所有产品的定制信息 ESOP

    protected String userId;

    protected void cleanupAfterRender(IRequestCycle cycle)
    {

        super.cleanupAfterRender(cycle);
        this.userId = null;

    }

    public abstract String getGrpProductTreeSelected();

    /**
     * @return productId
     */
    public String getuserId()
    {
        return userId;
    }

    /**
     * @author 动力100显示界面
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        this.productId = getData().getString("GRP_PRODUCT_ID", "");
        setProductId(productId);
        this.userId = getData().getString("GRP_USER_ID");

        setBrandCode(ProductInfoIntfViewUtil.qryBrandCodeStrByProductId(this, productId));

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
     * @version 创建时间：2009-5-11 下午08:54:28
     */
    public void queryCanSelectProducts(IRequestCycle cycle) throws Exception
    {

        String packageId = productId;

        IData inparams = new DataMap();
        inparams.put("PRODUCT_ID", packageId);
        inparams.put("RELATION_TYPE_CODE", "1");
        inparams.put("FORCE_TAG", "0");// 0可选，1必选

        IDataset ds = ProductCompRelaInfoIntfViewUtil.qryProductCompRelaInfosByProductIdARelationTypeCodeAndForceTag(this, packageId, "4", "");
        
        if(IDataUtil.isNotEmpty(ds))
        {
            for(int i = ds.size()-1; i >= 0; i--) 
            {
                IData comRel = ds.getData(i);
                String forceTag = comRel.getString("FORCE_TAG","0");
                if(!forceTag.equals("0"))
                    ds.remove(i);
            }
        }
        
        queryExistAndNew(cycle, ds);

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
                        pro.put("IS_EXIST_UU", "true");
                    }
                }

            }
        }

        setCanSelectProducts(ds);
    }

    public void queryExistAndNew(IRequestCycle cycle, IDataset ds) throws Exception
    {

        // 现在已经存在UU关系的动力100子用户
        String relationTypeCodeString = ProductCompInfoIntfViewUtil.qryRelationTypeCodeStrByProductId(this, productId);
        IDataset relationUUs = RelationUUInfoIntfViewUtil.qryGrpRelaUUInfosByUserIdAAndRelationTypeCode(this, getData().getString("GRP_USER_ID"), relationTypeCodeString);

        if (IDataUtil.isEmpty(relationUUs))
        {
            CSViewException.apperr(GrpException.CRM_GRP_254);
        }

        IDataset users = new DatasetList();
        for (int i = 0, size = relationUUs.size(); i < size; i++)
        {
            IData d = relationUUs.getData(i);
            IData temp = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, d.getString("USER_ID_B"));
            d.put("REMOVE_TAG", "0");
            d.put("USER_ID", d.getString("USER_ID_B"));
            d.put("PRODUCT_ID", temp.getString("PRODUCT_ID"));
            d.put("OPEN_DATE", temp.getString("OPEN_DATE"));
            d.put("SERIAL_NUMBER", temp.getString("SERIAL_NUMBER"));
            users.add(d);
        }

        IDataset temp = new DatasetList();
        IData temp1 = null;

        for (int i = 0; i < ds.size(); i++)
        {
            IData d = ds.getData(i);
            d.put("IS_EXIST", "false");
            for (int j = 0; j < users.size(); j++)
            {
                IData user = users.getData(j);
                if (user.getString("PRODUCT_ID").equals(d.getString("PRODUCT_ID_B")))
                {

                    if ("true".equals(d.getString("IS_EXIST_UU")))
                    {

                        temp1 = new DataMap();
                        temp1.putAll(d);
                        temp1.put("USER_ID", user.getString("USER_ID"));
                        temp1.put("USER_ID_A", this.getuserId());
                        temp1.put("OPEN_DATE", user.getString("OPEN_DATE"));
                        temp1.put("SERIAL_NUMBER", user.getString("SERIAL_NUMBER"));
                        temp1.remove("DISCNT_CODE");
                        IDataset product_distinct = CSViewCall.call(this, "CS.DiscntInfoQrySVC.getUserSingleProductDisForGrp", temp1); // TF_F_USER_DISCNT
                        if (IDataUtil.isNotEmpty(product_distinct))
                        {
                            StringBuilder temp_dis_code = new StringBuilder();
                            StringBuilder temp_dis_name = new StringBuilder();
                            for (int z = 0; z < product_distinct.size(); z++)
                            {
                                IData dist = product_distinct.getData(z);
                                temp_dis_code.append(" [" + dist.getString("DISCNT_CODE"));
                                temp_dis_code.append("]");
                                temp_dis_name.append(" [" + dist.getString("DISCNT_NAME"));
                                temp_dis_name.append("  " + dist.getString("START_DATE") + "--");
                                temp_dis_name.append(" " + dist.getString("END_DATE"));
                                temp_dis_name.append("]");
                            }
                            temp1.put("DISCNT_CODE", temp_dis_code.toString());
                            temp1.put("DISCNT_NAME", temp_dis_name.toString());
                            temp.add(temp1);
                        }
                    }
                    else
                    {
                        d.put("IS_EXIST_UU", "true");
                        d.put("IS_EXIST", "true");
                        d.put("USER_ID", user.getString("USER_ID"));
                        d.put("OPEN_DATE", user.getString("OPEN_DATE"));
                        d.put("SERIAL_NUMBER", user.getString("SERIAL_NUMBER"));
                        d.put("USER_ID_A", this.getuserId());
                        IDataset product_distinct = CSViewCall.call(this, "CS.DiscntInfoQrySVC.getUserSingleProductDisForGrp", d); // TF_F_USER_DISCNT
                        if (product_distinct != null && product_distinct.size() > 0)
                        {
                            StringBuilder temp_dis_code = new StringBuilder();
                            StringBuilder temp_dis_name = new StringBuilder();
                            for (int z = 0; z < product_distinct.size(); z++)
                            {
                                IData dist = product_distinct.getData(z);
                                temp_dis_code.append(" [" + dist.getString("DISCNT_CODE"));
                                temp_dis_code.append("]");
                                temp_dis_name.append(" [" + dist.getString("DISCNT_NAME"));
                                temp_dis_name.append("  " + dist.getString("START_DATE") + "--");
                                temp_dis_name.append(" " + dist.getString("END_DATE"));
                                temp_dis_name.append("]");
                            }
                            d.put("DISCNT_CODE", temp_dis_code.toString());
                            d.put("DISCNT_NAME", temp_dis_name.toString());
                        }
                    }
                }
            }
        }
        ds.addAll(temp);

        // 用户是否订购了新的动力100下的产品
        IDataset new_users = queryUsers(cycle);
        IData new_user = null;
        IData d = null;
        temp = new DatasetList();
        temp1 = null;
        int max = new_users.size();
        for (int x = 0; x < ds.size(); x++)
        {
            d = ds.getData(x);
            for (int y = 0; y < max; y++)
            {
                new_user = new_users.getData(y);
                if (new_user.getString("PRODUCT_ID").equals(d.getString("PRODUCT_ID_B")))
                {
                    if ("".equals(d.getString("USER_ID", "")))
                    {
                        d.put("IS_EXIST_UU", "false");
                        d.put("IS_EXIST", "true");
                        d.put("USER_ID", new_user.getString("USER_ID"));
                        d.put("OPEN_DATE", new_user.getString("OPEN_DATE"));
                        d.put("SERIAL_NUMBER", new_user.getString("SERIAL_NUMBER"));
                    }
                    else
                    {
                        temp1 = new DataMap();
                        temp1.putAll(d);
                        temp1.put("USER_ID", new_user.getString("USER_ID"));
                        temp1.put("OPEN_DATE", new_user.getString("OPEN_DATE"));
                        temp1.put("SERIAL_NUMBER", new_user.getString("SERIAL_NUMBER"));
                        temp1.put("IS_EXIST_UU", "false");
                        temp1.remove("DISCNT_CODE");
                        temp1.remove("DISCNT_NAME");
                        temp.add(temp1);
                    }
                }
            }
        }
        ds.addAll(temp);
        ds.removeAll(DataHelper.filter(ds, "IS_EXIST=false"));

    }

    /**
     * 查已经选择的产品和可选择的产品
     * 
     * @author hud
     * @version 创建时间：2009-5-11 下午09:13:52
     */
    public void queryProducts(IRequestCycle cycle) throws Exception
    {
        queryUserProducts(cycle);
        queryCanSelectProducts(cycle);
    }

    /**
     * 查询用户必选的产品
     * 
     * @author hud
     * @version 创建时间：2009-5-11 下午08:52:48
     */
    public void queryUserProducts(IRequestCycle cycle) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("PRODUCT_ID", productId);
        inparams.put("RELATION_TYPE_CODE", "1");
        inparams.put("FORCE_TAG", "1");// 0可选，1必选

        IDataset ds = ProductCompRelaInfoIntfViewUtil.qryProductCompRelaInfosByProductIdARelationTypeCodeAndForceTag(this, productId, "4", "");

        if(IDataUtil.isNotEmpty(ds))
        {
            for(int i = ds.size()-1; i >= 0; i--)
            {
                IData comRel = ds.getData(i);
                String forceTag = comRel.getString("FORCE_TAG","0");
                if(forceTag.equals("0"))
                    ds.remove(i);
            }
        }
        
        setGrpCompixProduct(ds.toString());

        queryExistAndNew(cycle, ds);

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
                        pro.put("IS_EXIST_UU", "true");
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
     * @version 创建时间：2009-5-12 下午07:33:11
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

        // 动力100的产品关系类型
        String relationTypeCode = ProductCompInfoIntfViewUtil.qryRelationTypeCodeStrByProductId(this, productId);
        // 排除已经 组成动力100的产品 对应的用户
        IDataset temp = new DatasetList();
        for (int i = 0; i < users.size(); i++)
        {
            String userIdB = ((IData) users.get(i)).getString("USER_ID");
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

    /**
     * @param productId
     *            要设置的 productId
     */
    public void setgetuserId(String getuserId)
    {
        this.userId = getuserId;
    }

    public abstract void setGrpCompixProduct(String str);

    public abstract void setGrpProductTreeSelected(String grpProductTreeSelected);// 已选择的产品

    public abstract void setInfo(IData info);

    public abstract void setProducts(IDataset products);// 必选的产品

    public abstract void setUserInfos(IDataset userInfos);

}
