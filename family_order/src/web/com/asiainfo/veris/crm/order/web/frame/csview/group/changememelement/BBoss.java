
package com.asiainfo.veris.crm.order.web.frame.csview.group.changememelement;

import org.apache.tapestry.IRequestCycle;

import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationbbinfo.RelationBBInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userattrinfo.UserAttrInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.usergrpmerchmebinfo.UserGrpMerchMebInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userproductinfo.UserProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcompinfo.ProductCompInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcomprelainfo.ProductCompRelaInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productmebinfo.ProductMebInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class BBoss extends GroupBasePage
{
    protected IData productGoodInfos = new DataMap();// 商产品信息

    protected String userId;

    public String getMerchpMebState(IData d) throws Exception
    {
        String mebUserId = d.getString("MEB_USER_ID");
        String ecUserId = d.getString("USER_ID");
        String mebSerialNumber = getData().getString("MEB_SERIAL_NUMBER");
        String mebEparchyCode = getData().getString("MEB_EPARCHY_CODE");
        if (StringUtils.isNotBlank(mebUserId) && StringUtils.isNotBlank(ecUserId))
        {
            IData temp = UserGrpMerchMebInfoIntfViewUtil.qryUserGrpMerchMebInfoByEcUserIdAndUserIdSerialNumber(this, ecUserId, mebUserId, mebSerialNumber, mebEparchyCode);

            if (IDataUtil.isNotEmpty(temp))
            {
                return temp.getString("STATUS", "A");
            }
        }

        return "N";
    }

    /**
     * 查出产品受理时添加的参数
     * 
     * @author hud
     * @version
     */
    public IDataset getUserAttrByUserIdInstId(String userIdA, String productId) throws Exception
    {
        String mebUserId = getData().getString("MEB_USER_ID");
        IDataset Insts = getUserProductInst(mebUserId, userIdA, productId);
        if (Insts == null || Insts.size() == 0)
        {
            return new DatasetList();
        }
        return UserAttrInfoIntfViewUtil.qryUserAttrInfosByUserIdAndInstTypeRelaInstId(this, mebUserId, "P", Insts.getData(0).getString("INST_ID"), getData().getString("MEB_EPARCHY_CODE"));
    }

    /**
     * 查出产品的INST_ID
     * 
     * @author hud
     * @version
     */
    public IDataset getUserProductInst(String userId, String userIdA, String productId) throws Exception
    {

        IDataset productBList = ProductMebInfoIntfViewUtil.qryProductMebInfosByProductId(this, productId);
        if (IDataUtil.isNotEmpty(productBList))
        {
            String productIdB = productBList.getData(0).getString("PRODUCT_ID_B", "");
            return UserProductInfoIntfViewUtil.qryUserProductInfsByUserIdAndUserIdAProductId(this, userId, userIdA, productIdB, getData().getString("MEB_EPARCHY_CODE"));
        }
        return null;
    }

    /**
     * @author hud
     * @version 创建时间：2009-8-13 下午08:43:06
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
    	IData inparam = getData();
        this.productId = getData().getString("GRP_PRODUCT_ID", "");
        this.userId = getData().getString("GRP_USER_ID");// 集团用户编号
      //获取是否免密标志，为空则免密，免密就要上传凭证信息，反之则不传 by zhuwj
   	 String authCheckMode = inparam.getString("cond_CHECK_MODE", "");
   	 System.out.print("authCheckMode"+authCheckMode);
        // 获取产品控制信息
        IData productCtrlInfo = AttrBizInfoIntfViewUtil.qryChgMbProductCtrlInfoByProductId(this, productId);

        // 传递参数
        setCheckMode(authCheckMode);//传递是否免密信息给下一个界面
        setMemEparchCode(getData().getString("MEB_EPARCHY_CODE"));
        setGrpUserId(getData().getString("GRP_USER_ID"));
        setGrpSn(getData().getString("GRP_SN"));
        setMemSerialNumber(getData().getString("MEB_SERIAL_NUMBER"));
        setMemUserId(getData().getString("MEB_USER_ID"));

        // 将商品信息和产品控制信息封装到productGoodInfos中,其中productGoodInfos包括五个组成部分(商品信息，产品信息，产品资费信息，

        // 产品属性信息,产品控制信息)
        IData goodInfo = new DataMap();// 商品信息
        goodInfo.put("BASE_PRODUCT", productId);
        productGoodInfos.put("GOOD_INFO", goodInfo);
        productGoodInfos.put("PRODUCT_CTRL_INFO", productCtrlInfo);
        setProductGoodInfos(productGoodInfos);

        //9 - 设置组合包
        String offerName = UpcViewCall.queryOfferNameByOfferId(this, "P", productId);
        setOfferName(offerName);
        
        queryUserProducts(cycle);
    }

    /**
     * 根据配置查能够添加成员的产品
     * 
     * @author hud
     * @version 创建时间：2009-8-24 上午11:48:01
     */
    public IDataset queryMemberCanProducts(String productId) throws Exception
    {
        return ProductMebInfoIntfViewUtil.qryProductMebInfosByProductIdStaffIdPriv(this, productId, getVisit().getStaffId());
    }

    /**
     * 查询成员加入的产品
     * 
     * @author hud
     * @version 创建时间：2009-8-18 上午11:21:05
     */
    public IDataset queryMemberProducts(IRequestCycle cycle, String userId, String relationTypeCode) throws Exception
    {
        String mebUserId = getData().getString("MEB_USER_ID");
        String roleCodeB = "1";// 0代表商产品关系，1代表产品与成员间关系
        String routeId = getData().getString("MEB_EPARCHY_CODE");
        return RelationBBInfoIntfViewUtil.getBBByUserIdAB(this, userId, mebUserId, roleCodeB, relationTypeCode, routeId);
    }

    /**
     * 查出集团用户已经订购的产品
     * 
     * @author hud
     * @version 创建时间：2009-5-11 下午08:52:48
     */
    public void queryUserProducts(IRequestCycle cycle) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("PRODUCT_ID", productId);
        inparams.put("RELATION_TYPE_CODE", "1");// 包产品关系

        IDataset ds = ProductCompRelaInfoIntfViewUtil.qryProductCompRelaInfosByProductIdARelationTypeCodeAndForceTag(this, productId, "4", null);

        IDataset users = new DatasetList();// 查集团用户

        // 现在已经存在UU关系的BBOSS子用户
        String relationTypeCode = ProductCompInfoIntfViewUtil.qryRelationTypeCodeStrByProductId(this, productId);
        // ROLE_CODE_B=0子产品跟商品的role_code_b关系
        IDataset relationBBInfoList = RelationBBInfoIntfViewUtil.qryRelaBBInfoByRoleCodeBForGrp(this, userId, relationTypeCode, "0");
        if (IDataUtil.isEmpty(relationBBInfoList))
        {
            CSViewException.apperr(GrpException.CRM_GRP_194, userId);
        }

        for (int i = 0; i < relationBBInfoList.size(); i++)
        {
            IData d = relationBBInfoList.getData(i);
            IData temp = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, d.getString("USER_ID_B"), false);
            if (IDataUtil.isEmpty(temp))
            {
                CSViewException.apperr(GrpException.CRM_GRP_207, d.getString("USER_ID_B"));
            }
            if ("BOSG".equals(temp.getString("BRAND_CODE")))
            {
                users.add(temp);
            }
        }

        IDataset userProducts = new DatasetList();// 用户订购的产品

        IData same = new DataMap();

        for (int i = 0; i < ds.size(); i++)
        {
            IData d = ds.getData(i);
            for (int j = 0; j < users.size(); j++)
            {
                IData user = users.getData(j);
                if (user.getString("PRODUCT_ID").equals(d.getString("PRODUCT_ID_B")))
                {
                    if ("".equals(d.getString("USER_ID", "")))
                    {
                        d.put("USER_ID", user.getString("USER_ID"));
                        d.put("PRODUCT_SPEC_CODE", user.getString("PRODUCT_ID"));
                        d.put("PRODUCT_ID", user.getString("PRODUCT_ID"));
                        relationTypeCode = ProductCompInfoIntfViewUtil.qryRelationTypeCodeStrByProductId(this, user.getString("PRODUCT_ID"));
                        IDataset members = queryMemberProducts(cycle, d.getString("USER_ID"), relationTypeCode);
                        if (members != null && members.size() > 0)
                        {
                            d.put("IS_EXIST", "true");
                            d.put("MEB_USER_ID", getData().getString("MEB_USER_ID"));
                            d.put("PRODUCT_STATUS", (getMerchpMebState(d)).equals("N") ? "暂停" : "正常");
                            d.put("PRODUCT_STATUS_CODE", getMerchpMebState(d));
                            // 设置成员参数
                            d.put("MEB_OPER_CODE", "EXIST");// 默认的操作类型

                            d.put("MEM_EPARCHY_CODE", getData().getString("MEB_EPARCHY_CODE"));
                            IDataset userAttr = getUserAttrByUserIdInstId(user.getString("USER_ID"), user.getString("PRODUCT_ID"));
                            // 初始化设置产品参数

                            productGoodInfos.put(user.getString("USER_ID"), IDataUtil.hTable2STable(userAttr, "ATTR_CODE", "ATTR_VALUE", "ATTR_VALUE"));
                            setProductGoodInfos(productGoodInfos);
                        }

                        IDataset memProducts = queryMemberCanProducts(user.getString("PRODUCT_ID"));// 查产品配置中，能添加成员的产品

                        if (memProducts != null && memProducts.size() > 0)// 如果可以添加成员
                        {
                            userProducts.add(d);// 只添加用户已经订购的产品
                        }
                    }
                    else
                    {
                        same = new DataMap();
                        same.putAll(d);
                        same.remove("IS_EXIST");
                        same.remove("MEB_USER_ID");
                        same.remove("PRODUCT_STATUS");
                        same.remove("MEB_OPER_CODE");
                        same.remove("MEM_EPARCHY_CODE");
                        same.put("USER_ID", user.getString("USER_ID"));
                        same.put("PRODUCT_SPEC_CODE", user.getString("PRODUCT_ID"));
                        same.put("PRODUCT_ID", user.getString("PRODUCT_ID"));
                        relationTypeCode = ProductCompInfoIntfViewUtil.qryRelationTypeCodeStrByProductId(this, user.getString("PRODUCT_ID"));

                        IDataset members = queryMemberProducts(cycle, same.getString("USER_ID"), relationTypeCode);
                        if (members != null && members.size() > 0)
                        {
                            same.put("IS_EXIST", "true");
                            same.put("MEB_USER_ID", getData().getString("MEB_USER_ID"));
                            same.put("PRODUCT_STATUS", (getMerchpMebState(same)).equals("N") ? "暂停" : "正常");
                            // 设置成员参数
                            same.put("MEB_OPER_CODE", "EXIST");// 默认的操作类型

                            same.put("MEM_EPARCHY_CODE", getData().getString("MEB_EPARCHY_CODE"));
                            IDataset userAttr = getUserAttrByUserIdInstId(user.getString("USER_ID"), user.getString("PRODUCT_ID"));
                            // 初始化设置产品参数

                            productGoodInfos.put(user.getString("USER_ID"), IDataUtil.hTable2STable(userAttr, "ATTR_CODE", "ATTR_VALUE", "ATTR_VALUE"));
                            setProductGoodInfos(productGoodInfos);
                        }
                        IDataset memProducts = queryMemberCanProducts(user.getString("PRODUCT_ID"));// 查产品配置中，能添加成员的产品

                        if (memProducts != null && memProducts.size() > 0)// 如果可以添加成员
                        {
                            userProducts.add(same);// 只添加用户已经订购的产品
                        }
                    }
                }
            }
        }

        setProducts(userProducts);
    }
    public abstract void setCheckMode(String CheckMode);
    
    public abstract void setGrpSn(String grpSn);

    public abstract void setGrpUserId(String grpUserId);

    public abstract void setInfo(IData info);

    public abstract void setMebOpers(IDataset mebOpers);// 设置成员操作类型

    public abstract void setMemEparchCode(String memEparchCode);

    public abstract void setMemSerialNumber(String memSerialNumber);

    public abstract void setMemUserId(String memUserId);

    public abstract void setProductGoodInfos(IData productGoodInfos);// 设置商产品信息

    public abstract void setProducts(IDataset products);// 必选的产品

    public abstract void setOfferName(String offerName);// 组合包
    /*
     * @description 传递商产品信息至产品参数页面
     * @author xunyl
     * @date 2013-06-29
     */
    public void transProductGoodInfos(IRequestCycle cycle) throws Throwable
    {
        // 1- 获取集团编号(该集团编号作为商产品信息的key值，用来保证缓存信息的唯一性)
        String mebUserId = getData().getString("MEB_USER_ID");

        // 2- 判断产品用户状态是否正常，非正常状态不能添加成员
        IData returnData = new DataMap();
        returnData.put("result", "true");
        String productUserId = getData().getString("PRODUCT_USER_ID");

        IData productUserInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, productUserId);
        String userState = productUserInfo.getString("RSRV_STR5", "A");
        if (!"A".equals(userState) && !"F".equals(userState))
        {
            returnData.put("result", "false");
            setAjax(returnData);
            return;
        }

        // 3- 获取商产品信息
        String productGoodInfos = getData().getString("productGoodInfos");

        // 4- 将商产品信息保存至缓存中(这里是借用集团侧的key,将集团侧的groupId换成了成员侧的mebUserId)
        String key = CacheKey.getBossProductInfoKey(getVisit().getStaffId(), mebUserId);
        SharedCache.set(key, productGoodInfos, 1200);

        // 5- 添加结果
        setAjax(returnData);
    }

}
