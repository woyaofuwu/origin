
package com.asiainfo.veris.crm.order.web.frame.csview.group.creategroupuser;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationbbinfo.RelationBBInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcompinfo.ProductCompInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcomprelainfo.ProductCompRelaInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class BBoss extends GroupBasePage
{
    /*
     * @description 处理ESOP过来的数据
     * @author xunyl
     * @date 2013-09-20
     */
    protected IData dealEsopDatas(IRequestCycle cycle) throws Exception
    {
        // 1- 获取EOS对象
        String eos = getData().getString("EOS");
        IDataset eosInfoList = new DatasetList(eos);
        IData eosInfo = eosInfoList.getData(0);

        // 2- 获取接口标志并做相应处理
        String ibsysid = eosInfo.getString("IBSYSID", "");
        if (!"".equals(ibsysid))
        {
            IData inData = new DataMap();
            String nodeId = eosInfo.getString("NODE_ID", "");
            inData.put("NODE_ID", nodeId);
            inData.put("IBSYSID", ibsysid);
            inData.put("OPER_CODE", "11");
            IData httpResult = CSViewCall.callone(this, "SS.ESOPQcsGrpBusiIntfSvc.getEosInfo", inData);
            if (IDataUtil.isEmpty(httpResult))
                CSViewException.apperr(GrpException.CRM_GRP_508, "接口返回数据为空");

            eosInfo.putAll(httpResult);
        }
        String groupid = eosInfo.getString("GROUP_ID");
        // 将eos放入缓存
        String key = CacheKey.getBBossESOPInfoKey(getVisit().getStaffId(), "EOS_" + groupid);
        SharedCache.set(key, eosInfoList, 1200);

        setEOS(eosInfoList);
        // 3- 返回数据
        return eosInfo;

    }

    /**
     * @author zhujm 2009-03-06
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        // 1- 处理ESOP的数据集
        String eos = getData().getString("EOS");
        String ibSysId = "";
        if (!StringUtils.isEmpty(eos) && !"{}".equals(eos))
        {
            IDataset eosInfoList = new DatasetList(eos);
            if (!eosInfoList.isEmpty())
            {
                IData eosInfo = eosInfoList.getData(0);
                ibSysId = eosInfo.getString("IBSYSID", "");
            }
        }

        // 2- 设置集团编号
        String groupId = getData().getString("GROUP_ID");
        setGroupId(groupId);

        // 3- 设置客户编号
        String custId = getData().getString("CUST_ID");
        setCustId(custId);

        // 4-获取商品编号
        String baseProductId = getData().getString("GRP_PRODUCT_ID");

        // 5- 获取产品控制信息
        IData productCtrlInfo = AttrBizInfoIntfViewUtil.qryCrtUsProductCtrlInfoByProductId(this, baseProductId);
        
        //5.1-设置业务类型
        String tradeTypeCode = productCtrlInfo.getData("TradeTypeCode").getString("ATTR_VALUE");
        setTradeTypeCode(tradeTypeCode);
        
        //5.2-设置集团业务路由
        String grpUserEparchyCode = getData().getString("GRP_USER_EPARCHYCODE");
        setGrpUserEparchyCode(grpUserEparchyCode);
        
        // 6- 封装商品基本信息
        IData goodInfo = new DataMap();// 商品信息
        goodInfo.put("BASE_PRODUCT", baseProductId);

        // 7- 封装商产品信息
        IData productGoodInfos = new DataMap();// 商产品信息
        productGoodInfos.put("GOOD_INFO", goodInfo);
        productGoodInfos.put("PRODUCT_CTRL_INFO", productCtrlInfo);
        setProductGoodInfos(productGoodInfos);

        // 8- 查询可选产品和必选产品
        if (!"".equals(ibSysId))
        {
            queryOrderProducts(cycle);
        }
        else
        {
            queryProducts(cycle);
        }

        //9 - 设置组合包
        String offerName = UpcViewCall.queryOfferNameByOfferId(this, "P", baseProductId);
        setOfferName(offerName);
        
        
        //10- 设置业务开展模式
        IDataset bizModeList =  qryBizMode(baseProductId);
        setBizModeList(bizModeList);
        
        //11-设置套餐生效方式
        IDataset payModeList = qryPayMode(baseProductId);
        setPayModeList(payModeList);
        

    }

    public boolean initMulti(String productId) throws Exception
    {

        IDataset result = AttrBizInfoIntfViewUtil.qryAttrBizInfosByIdAndIdTypeAttrObjAttrCode(this, "1", "B", "MULTI", productId);
        if (IDataUtil.isEmpty(result))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * @author hudie
     * @作用 初始化操作类型 是否预受理
     */
    public void initOperTypesCrt(IRequestCycle cycle) throws Exception
    {
        // 1- 设置商品操作类型
        IDataset operTypes = new DatasetList();
        IData poOperType = new DataMap();
        IData proOperTypes = new DataMap();

        poOperType.put("OPER_TYPE", GroupBaseConst.MERCH_STATUS.MERCH_ADD.getValue());
        poOperType.put("OPER_NAME", "新增商品订购");
        operTypes.add(poOperType);
        setOperTypes(operTypes);

        // 2- 设置产品操作类型
        // 2-1 获取产品编号
        String baseProductId = getData().getString("GRP_PRODUCT_ID");

        // 2-2 根据产品编号判断该商品是否为需要预受理商品
        boolean needAHeadB = AttrBizInfoIntfViewUtil.qryBBossBusiNeedAHeadTagByGrpProductId(this, baseProductId);
        // 2-3 设置预受理标志
        if (!needAHeadB)
        {
            setProdOpType("1");
            proOperTypes.put("PRO_OPER_TYPE", GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_ADD.getValue());
            proOperTypes.put("PRO_OPER_NAME", "新增产品订购");
            setProOperTypes(proOperTypes);
        }
        else
        {
            setProdOpType("10");
            proOperTypes.put("PRO_OPER_TYPE", GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PREDEAL.getValue());
            proOperTypes.put("PRO_OPER_NAME", "产品预受理");
            setProOperTypes(proOperTypes);
        }

    }
    /*
     *11-获取套餐生效方式
     */
    private IDataset qryPayMode(String OfferCode) throws Exception
    {
        IDataset payModeList = new DatasetList();
        IDataset payModeDataset = AttrBizInfoIntfViewUtil.qryAttrBizInfosByIdAndIdTypeAttrObj(this, OfferCode, "B","OPNPYMDS");
        if (IDataUtil.isNotEmpty(payModeDataset))
        {
        	int iSize = payModeDataset.size();
            for (int i = 0; i < iSize; i++)
            {
            	IData map = new DataMap();
                IData tmp = payModeDataset.getData(i);
                map.put("ATTR_VALUE", tmp.getString("ATTR_VALUE"));
                map.put("ATTR_NAME", tmp.getString("ATTR_NAME"));
                payModeList.add(map);
            }
        }
        return payModeList;
    }
 
    
    /*
     * 获取 业务开展模式
     */
    private IDataset qryBizMode(String productId) throws Exception
    {
        IDataset bizModeList = new DatasetList();
        String bizModes = AttrBizInfoIntfViewUtil.qryBBossBizCodeByGrpProductId(this, productId);
        if (StringUtils.isBlank(bizModes))
        {
            // 如果没有配置业务开展模式，默认为 5 ，本省受理，本省支付
            IData tmp = new DataMap();
            tmp.put("BIZ_MODE", "5");
            tmp.put("MODE_NAME", "本省受理，本省支付");

            bizModeList.add(tmp);
        }
        else
        {
            String[] payList = bizModes.split(",");
            for (int i = 0; i < payList.length; i++)
            {
                IData tmp = new DataMap();
                tmp.put("BIZ_MODE", payList[i]);
                String modeName = StaticUtil.getStaticValue("BBOSS_BIZ_MODE", payList[i]);
                tmp.put("MODE_NAME", modeName);
                bizModeList.add(tmp);
            }
        }
        return bizModeList;
    }

    /**
     * 查询ESOP订单可选的和必选的产品
     * 
     * @author liaoyi
     * @version 创建时间：2012-09-27 15:32:11
     */
    public void queryOrderProducts(IRequestCycle cycle) throws Exception
    {
        // 1- 获取ESOP数据
        IData eosInfo = dealEsopDatas(cycle);

        // 2- 获取ESOP已选产品
        IDataset productsOperCodeList = IDataUtil.getDatasetSpecl("RSRV_STR14", eosInfo);// 产品操作代码
        IDataset subProductsList = IDataUtil.getDatasetSpecl("PRSRV_STR10", eosInfo);// 产品
        if (null != subProductsList && subProductsList.size() > 0)
        {

            if (subProductsList.size() != productsOperCodeList.size())
            {
                CSViewException.apperr(GrpException.CRM_GRP_602);
            }
            IDataset dsProducts = new DatasetList();

            for (int kk = 0; kk < subProductsList.size(); kk++)
            {
                IData d = new DataMap();
                String productId = (String) subProductsList.get(kk).toString();
                IData temp = ProductInfoIntfViewUtil.qryProductInfoByProductId(this, productId);
                if (IDataUtil.isNotEmpty(temp))
                {
                    d.putAll(temp);
                }
                d.put("PRODUCT_ID_B", productId);
                d.put("PRODUCT_INDEX", kk);
                // d.put("IS_EXIST", "true");
                dsProducts.add(d);
            }
            setProducts(dsProducts);
            initOperTypesCrt(cycle);
        }
        else
        {
            queryProducts(cycle);
        }

        // 3- 操作类型

    }

    /**
     * 查已经选择的产品和可选择的产品
     * 
     * @author shixb
     * @version 创建时间2009-5-11 下午09:13:52
     */
    public void queryProducts(IRequestCycle cycle) throws Exception
    {
        queryUserProducts(cycle);
    }

    /**
     * 查询用户可选的和必选的产品
     * 
     * @author shixb
     * @version 创建时间2009-5-11 下午08:52:48
     */
    public void queryUserProducts(IRequestCycle cycle) throws Exception
    {
        // 获取商品编号
        String baseProductId = getData().getString("GRP_PRODUCT_ID");

        // 根据商品编号获取产品信息
        IDataset ds = ProductCompRelaInfoIntfViewUtil.qryProductCompRelaInfosByProductIdARelationTypeCodeAndForceTag(this, baseProductId, "4", null);
        //特殊业务，产品列表过滤(爱流量统付业务，受理只保留爱流量基础产品)
        selectBaseProduct(baseProductId,ds);
        IDataset fstOpen = AttrBizInfoIntfViewUtil.qryAttrBizInfosByIdAndIdTypeAttrObjAttrCode(this, "1", "B", "FSTOPEN", baseProductId);
        if (IDataUtil.isNotEmpty(fstOpen))
        {
            for (int i = 0; i < ds.size(); i++)
            {
                IData d = ds.getData(i);
                d.put("FORCE_TAG", "0");
                for (int j = 0; j < fstOpen.size(); j++)
                {
                    if (fstOpen.getData(j).getString("ATTR_VALUE", "").equals(d.getString("PRODUCT_ID_B")))
                    {
                        d.put("FORCE_TAG", "1");
                    }
                }
            }
            ds = DataHelper.filter(ds, "FORCE_TAG=1");
        }

        IDataset mustSelect = new DatasetList();

        IDataset users = queryUsers(cycle);

        IDataset ds_user = new DatasetList();
        IData d_user;

        for (int i = 0; i < ds.size(); i++)
        {
            IData d = ds.getData(i);
            initOperTypesCrt(cycle);
            // 初始化操作类型
            if (initMulti(d.getString("PRODUCT_ID_B")))
            {
                d.put("CAN_MANY", "muti");
            }
            for (int j = 0; j < users.size(); j++)
            {
                IData user = users.getData(j);
                if (user.getString("PRODUCT_ID").equals(d.getString("PRODUCT_ID_B")))
                {
                    if ("".equals(d.getString("PRODUCT_INDEX", "")))
                    {
                        d.put("PRODUCT_INDEX", "1");
                        d.put("IS_EXIST", "true");
                        d.put("USER_ID", user.getString("USER_ID"));
                    }
                    else
                    {
                        d_user = new DataMap();
                        d_user.putAll(d);
                        d_user.put("USER_ID", user.getString("USER_ID"));
                        d_user.put("PRODUCT_INDEX", (d.getInt("PRODUCT_INDEX") + 1));
                        ds_user.add(d);
                    }
                }
            }
            // 1代表必选产品，0为非必选产品
            if ("1".equals(d.getString("FORCE_TAG")))
                mustSelect.add(d);

            if ("".equals(d.getString("PRODUCT_INDEX", "")))
            {
                d.put("PRODUCT_INDEX", "1");
            }
        }

        ds.addAll(ds_user);
        setProducts(ds);
        setGrpCompixProduct(mustSelect.toString());
    }

    /**
     * 查询用户信息
     * 
     * @author shixb
     * @version 创建时间䣺2009-5-12 下午07:33:11
     */
    public IDataset queryUsers(IRequestCycle cycle) throws Exception
    {
        // 获取商品编号
        String baseProductId = getData().getString("GRP_PRODUCT_ID");
        IDataset users = UCAInfoIntfViewUtil.qryGrpUserInfoByCustIdAndProId(this, getData().getString("CUST_ID"), baseProductId, false);
        // BBOSS的产品关系类型
        String relationTypeCode = ProductCompInfoIntfViewUtil.qryRelationTypeCodeStrByProductId(this, baseProductId);
        // 排除已经 组成其他BBOSS的商品的用户
        IDataset temp = new DatasetList();
        for (int i = 0; i < users.size(); i++)
        {
            String userIdB = ((IData) users.get(i)).getString("USER_ID");
            IDataset expt = RelationBBInfoIntfViewUtil.qryRelaBBInfoByUserIdBRelaTypeCode(this, userIdB, relationTypeCode, Route.CONN_CRM_CG);
            if (IDataUtil.isEmpty(expt))
            {
                temp.add(users.get(i));
            }
        }

        return temp;
    }
    
    /**
     * @description 爱流量统付产品受理时只能选择基础产品进行订购
     * @author xunyl
     * @date 2016-07-06
     */
    private void selectBaseProduct(String merchProductId,IDataset productInfoList)throws Exception{
        //1- 如果产品列表为空，则直接退出
        if(IDataUtil.isEmpty(productInfoList)){
            return;
        }
        
        //2- 根据本地商品编号找到集团商品编号
        IData inparam = new DataMap();
        inparam.put("ID", "1");
        inparam.put("ID_TYPE", "B");
        inparam.put("ATTR_CODE", merchProductId);
        inparam.put("ATTR_OBJ", "PRO");
        IDataset attrBizInfoList = CSViewCall.call(this, "CS.AttrBizInfoQrySVC.getBizAttr", inparam);
        if(IDataUtil.isEmpty(attrBizInfoList)){
            return;
        }
        
        //3- 如果为爱流量统付产品，只保留基础产品(集团产品编号为"9001201")    
        IData attrBizInfo = attrBizInfoList.getData(0);
        String merchSpecCode = attrBizInfo.getString("ATTR_VALUE","");
        if(StringUtils.equals("010190012", merchSpecCode)){
            for(int i=0;i<productInfoList.size();i++){
                IData productInfo = productInfoList.getData(i);
                String productId = productInfo.getString("PRODUCT_ID_B","");
                inparam.clear();
                inparam.put("ID", "1");
                inparam.put("ID_TYPE", "B");
                inparam.put("ATTR_CODE", productId);
                inparam.put("ATTR_OBJ", "PRO");
                attrBizInfoList =CSViewCall.call(this, "CS.AttrBizInfoQrySVC.getBizAttr", inparam);
                if(IDataUtil.isEmpty(attrBizInfoList)){                 
                    return;
                }
                attrBizInfo = attrBizInfoList.getData(0);
                String productSpecCode = attrBizInfo.getString("ATTR_VALUE","");
                if(!StringUtils.equals("9001201", productSpecCode)){
                    productInfoList.remove(i);
                    i--;
                }
            }
        }
    }
    public abstract void setOfferName(String offerName);// 组合包
    
    public abstract void setPayModeList(IDataset  payModeList);//套餐生效方式
    
    public abstract void setBizModeList(IDataset canSelectProducts);// 可以选择的业务开展模式

    public abstract void setTradeTypeCode(String tradeTypeCode);// 保存业务类型
    
    public abstract void setGrpUserEparchyCode(String grpUserEparchCode);// 保存集团路由
    
    public abstract void setCondition(IData info);

    public abstract void setCustId(String custId);// 保存客户编号

    public abstract void setEOS(IDataset EOS);

    public abstract void setGroupId(String groupId);// 上产品信息，升级前是存放在tradeData中的

    public abstract void setGrpCompixProduct(String str);

    public abstract void setInfo(IData info);

    public abstract void setOperTypes(IDataset OperTypes);

    public abstract void setProdOpType(String productOperType);

    public abstract void setProductGoodInfos(IData productGoodInfos);// 上产品信息，升级前是存放在tradeData中的

    public abstract void setProducts(IDataset products);// 必选的产品

    public abstract void setProOperTypes(IData proOperTypes);

    /*
     * @description 传递商产品信息至产品参数页面
     * @author xunyl
     * @date 2013-06-29
     */
    public void transProductGoodInfos(IRequestCycle cycle) throws Throwable
    {
    	
        // 1- 获取集团编号(该集团编号作为商产品信息的key值，用来保证缓存信息的唯一性)
        String groupId = getData().getString("GROUP_ID");

        // 2- 获取商产品信息        String productGoodInfos = getData().getString("productGoodInfos");

        // 3- 将商产品信息保存至缓存中
        String key = CacheKey.getBossProductInfoKey(getVisit().getStaffId(), groupId);
        SharedCache.set(key, productGoodInfos, 1200);
    }
}
