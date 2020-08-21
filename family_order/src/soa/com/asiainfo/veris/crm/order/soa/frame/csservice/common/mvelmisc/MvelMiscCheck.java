
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.mvelmisc;

import java.util.ArrayList;
import java.util.List;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.RegTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;

public final class MvelMiscCheck
{
    /**
     * *用于判断活动编码
     * 
     * @param rtd
     * @param packageId
     * @return
     * @throws Exception
     */
    public static boolean chkByActivePkgId(RegTradeData rtd, String packageId) throws Exception
    {
        boolean bResult = false;
        List<SaleActiveTradeData> saleActiveTradeDatas = rtd.getTradeDatas("TF_B_TRADE_SALE_ACTIVE");
        if (saleActiveTradeDatas != null && saleActiveTradeDatas.size() > 0)
        {
            for (Object obj : saleActiveTradeDatas)
            {
                SaleActiveTradeData saTd = (SaleActiveTradeData) obj;
                if (saTd.getPackageId().equals(packageId))
                {
                    bResult = true;
                    break;
                }
            }
        }
        return bResult;
    }

    /**
     * 原短信模板匹配，td_b_trade_sms.obj_type_code='3'，品牌匹配 原来配置(modifyTag:0-Like,1-Not Like，%只能放在最后)
     * 
     * @param rtd
     * @param brandCode
     * @param modifyTag
     * @return
     * @throws Exception
     */
    public static boolean chkByBrand(RegTradeData rtd, String brandCode, String modifyTag) throws Exception
    {
        String brand_code = rtd.getMainTradeData().getBrandCode();

        if ("%".equals(brandCode.substring(brandCode.length() - 1, brandCode.length())))
        {
            brandCode = brandCode.substring(0, brandCode.length() - 1);
            brand_code = brand_code.substring(0, brand_code.length() - 1);
        }

        if ("0".equals(modifyTag) && brand_code.equals(brandCode))
        {
            return true;
        }

        if ("1".equals(modifyTag) && !brand_code.equals(brandCode))
        {
            return true;
        }

        return false;
    }

    /**
     * 原短信模板匹配，td_b_trade_sms.obj_type_code='7'，营销活动类型判断
     * 
     * @param rtd
     * @param campnType
     * @return
     * @throws Exception
     */
    public static boolean chkByCampnType(RegTradeData rtd, String campnType) throws Exception
    {
        // 原逻辑中只有240业务才判断

        // 原来执行sql，TF_B_TRADE_SALE_ACTIVE-SEL_BY_FINISH，根据trade_id查询台帐，modify_tag!=9
        // 现在改成下面这个写法，从btd中获取台帐
        if ("240".equals(rtd.getMainTradeData().getTradeTypeCode()))
        {
            List<SaleActiveTradeData> saleactiveTradeDatas = rtd.getTradeDatas("TF_B_TRADE_SALE_ACTIVE");
            if (saleactiveTradeDatas != null && saleactiveTradeDatas.size() > 0)
            {
                if (campnType.equals(saleactiveTradeDatas.get(0).getCampnType()) && !"9".equals(saleactiveTradeDatas.get(0).getModifyTag()))
                {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean chkByCommparaForVip(RegTradeData rtd, String param_attr, String sendTag) throws Exception
    {
        String tradeId = rtd.getTradeId();
        String svc_level = rtd.getMainTradeData().getRsrvStr4();

        IDataset dataset = CommparaInfoQry.getCommparaAllCol("CSM", param_attr, svc_level, "ZZZZ");

        if (dataset.size() < 1)
        {
            return false;
        }
        else
        {
            return "sendTag".equals(dataset.getData(0).getString("PARA_CODE11"));
        }

    }

    /**
     * @Function: chkByDiscnt
     * @Description: 该函数的功能描述
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2013-9-30 上午11:36:29 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-9-30 lijm3 v1.0.0 修改原因
     */
    public static boolean chkByDiscnt(RegTradeData rtd, IData m, String modifyTag, String discntid) throws Exception
    {
        boolean bResult = false;
        String mod = m.getString("MODIFY_TAG");
        String discntcode = m.getString("DISCNT_CODE");

        if (modifyTag.equals(mod) && discntid.equals(discntcode))
        {
            bResult = true;
        }

        return bResult;
    }

    /**
     * 原短信模板匹配，td_b_trade_sms.obj_type_code='2'，优惠
     * 
     * @param rtd
     * @param discntCode
     * @param modifyTag
     * @return
     * @throws Exception
     */
    public static boolean chkByDisOper(RegTradeData rtd, String discntCode, String modifyTag) throws Exception
    {
        boolean bResult = false;
        List<DiscntTradeData> discntTradeDatas = rtd.getTradeDatas("TF_B_TRADE_DISCNT");
        if (discntTradeDatas != null && discntTradeDatas.size() > 0)
        {
            for (Object obj : discntTradeDatas)
            {
                DiscntTradeData dtd = (DiscntTradeData) obj;

                if (discntCode.equals(dtd.getDiscntCode()) && (modifyTag.equals(dtd.getModifyTag())))
                {
                    bResult = true;
                    break;
                }
            }
        }
        return bResult;
    }

    /**
     * @Function: chkByIsP
     * @Description: 该函数的功能描述
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2013-9-10 下午8:00:20 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-9-10 lijm3 v1.0.0 修改原因
     */
    public static boolean chkByIsP(RegTradeData rtd, IData m, String modifyTag, String product_id) throws Exception
    {
        boolean bResult = false;
        String mod = m.getString("MODIFY_TAG");
        String productId = m.getString("PRODUCT_ID");
        String role_id = m.getString("ROLE_CODE_B");

        if (modifyTag.equals(mod) && product_id.equals(productId))
        {
            bResult = true;
        }

        return bResult;
    }

    /**
     * @Function: chkByNotP
     * @Description: 不是参入产品走这个模版
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2013-9-10 下午7:57:42 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-9-10 lijm3 v1.0.0 修改原因
     */
    public static boolean chkByNotP(RegTradeData rtd, IData m, String modifyTag, String product_id) throws Exception
    {
        boolean bResult = false;
        String mod = m.getString("MODIFY_TAG");
        String productId = m.getString("PRODUCT_ID");
        String role_id = m.getString("ROLE_CODE_B");

        if (modifyTag.equals(mod) && !product_id.equals(productId))
        {
            bResult = true;
        }

        return bResult;
    }

    /**
     * 原短信模板匹配，td_b_trade_sms.obj_type_code='0'，产品
     * 
     * @param rtd
     * @param productId
     * @param modifyTag
     * @return
     * @throws Exception
     */
    public static boolean chkByPrdOper(RegTradeData rtd, String productId, String modifyTag) throws Exception
    {
        boolean bResult = false;
        List<ProductTradeData> productTradeDatas = rtd.getTradeDatas("TF_B_TRADE_PRODUCT");
        if (productTradeDatas != null && productTradeDatas.size() > 0)
        {
            for (Object obj : productTradeDatas)
            {
                ProductTradeData dtd = (ProductTradeData) obj;

                if ((productId.equals(dtd.getProductId()) && modifyTag.equals(dtd.getModifyTag())) || (productId.equals(dtd.getOldProductId()) && modifyTag.equals("1")))
                {
                    bResult = true;
                    break;
                }
            }
        }
        return bResult;
    }

    /**
     * 原短信模板匹配，process_tag_set td_b_trade_sms.obj_type_code='4'，校验台帐processTagSet的第N位是否等于某个值
     * td_b_trade_sms.obj_type_code='5'，校验台帐processTagSet的第N位是否不等于某个值 现在改为直接传入标记isEqu，==-等于，!=-不等于
     * 
     * @param rtd
     * @param processTagSet
     * @param modifyTag
     * @return
     * @throws Exception
     */
    public static boolean chkByProcTagSet(RegTradeData rtd, int processTagSetIndex, String modifyTag, String isEqu) throws Exception
    {
        boolean bResult = false;

        String process_tag_set = rtd.getMainTradeData().getProcessTagSet();

        // process_tag_set 足够长
        if (process_tag_set.length() >= processTagSetIndex)
        {
            if (modifyTag.equals(process_tag_set.substring(processTagSetIndex - 1, processTagSetIndex)))
            {
                bResult = true;
            }
        }
        // process_tag_set 默认每一位都为0
        else
        {
            if ("0".equals(modifyTag))
            {
                bResult = true;
            }
        }

        // 等于
        if ("==".equals(isEqu))
        {

        }
        // 不等于
        else if ("!=".equals(isEqu))
        {
            bResult = !bResult;
        }

        // 默认
        return bResult;
    }

    /**
     * 原短信模板匹配，td_b_trade_sms.obj_type_code='6'，台帐表产品 现在改为传入标记isEqu：==-等于，!=-不等于
     * 
     * @param rtd
     * @param key
     * @param value
     * @return
     * @throws Exception
     */
    public static boolean chkByProduct(RegTradeData rtd, String productId, String isEqu) throws Exception
    {
        boolean bResult = false;

        String product_id = rtd.getMainTradeData().getProductId();
        if (product_id.equals(productId))
        {
            bResult = true;
        }

        // 等于
        if ("==".equals(isEqu))
        {

        }
        // 不等于
        else if ("!=".equals(isEqu))
        {
            bResult = !bResult;
        }

        // 默认
        return bResult;
    }

    /**
     * 原短信模板匹配，td_b_trade_sms.obj_type_code='1'，服务
     * 
     * @param rtd
     * @param serviceId
     * @param modifyTag
     * @return
     * @throws Exception
     */
    public static boolean chkBySvcOper(RegTradeData rtd, String serviceId, String modifyTag) throws Exception
    {
        boolean bResult = false;
        List<SvcTradeData> svcTradeDatas = rtd.getTradeDatas("TF_B_TRADE_SVC");
        if (svcTradeDatas != null && svcTradeDatas.size() > 0)
        {
            for (Object obj : svcTradeDatas)
            {
                SvcTradeData dtd = (SvcTradeData) obj;

                if (serviceId.equals(dtd.getElementId()) && (modifyTag.equals(dtd.getModifyTag())))
                {
                    bResult = true;
                    break;
                }
            }
        }
        return bResult;
    }

    /**
     * 家庭账户短信根据业务编码、修改标识与TRADE_RELATION、TRADE_OTHER子台账表 匹配短信模板
     * 
     * @param rtd
     * @param tradeTypeCode
     * @param modifyTag
     * @return boolean
     * @throws Exception
     *             wangjx 2013-9-7
     */
    @SuppressWarnings(
    { "rawtypes", "unchecked" })
    public static boolean chkByTM(RegTradeData rtd, String tradeTypeCode, String modifyTag) throws Exception
    {
        boolean bResult = false;
        if (!tradeTypeCode.equals(rtd.getTradeTypeCode()))
        {
            return bResult;
        }

        if ("2".equals(modifyTag))// 修改只登记TRADE_OTHER
        {
            List<OtherTradeData> otherTradeDatas = rtd.getTradeDatas("TF_B_TRADE_OTHER");
            if (otherTradeDatas != null && otherTradeDatas.size() > 0)
            {
                for (Object obj : otherTradeDatas)
                {
                    OtherTradeData otherTD = (OtherTradeData) obj;

                    if ("*".equals(modifyTag) || modifyTag.equals(otherTD.getModifyTag()))
                    {
                        bResult = true;
                        break;
                    }
                }
            }
        }
        else
        {
            List<RelationTradeData> relaTradeDatas = rtd.getTradeDatas("TF_B_TRADE_RELATION");
            if (relaTradeDatas != null && relaTradeDatas.size() > 0)
            {
                for (Object obj : relaTradeDatas)
                {
                    RelationTradeData relaTD = (RelationTradeData) obj;

                    if ("*".equals(modifyTag) || modifyTag.equals(relaTD.getModifyTag()))
                    {
                        bResult = true;
                        break;
                    }
                }
            }
        }

        return bResult;
    }

    /**
     * 根据业务编码，操作标识查询TRADE_RELATION子台账表
     * 
     * @param rtd
     * @param tradeTypeCode
     * @param modifyTag
     * @return boolean
     * @throws Exception
     *             wangjx 2013-9-18
     */
    @SuppressWarnings(
    { "rawtypes", "unchecked" })
    public static boolean chkRelaByTM(RegTradeData rtd, String tradeTypeCode, String modifyTag) throws Exception
    {
        boolean bResult = false;
        if (!tradeTypeCode.equals(rtd.getTradeTypeCode()))
        {
            return bResult;
        }

        List<RelationTradeData> relaTradeDatas = rtd.getTradeDatas("TF_B_TRADE_RELATION");
        if (relaTradeDatas != null && relaTradeDatas.size() > 0)
        {
            for (Object obj : relaTradeDatas)
            {
                RelationTradeData relaTD = (RelationTradeData) obj;

                if ("*".equals(modifyTag) || modifyTag.equals(relaTD.getModifyTag()))
                {
                    bResult = true;
                    break;
                }
            }
        }

        return bResult;
    }

    /**
     * 根据业务编码，操作标识，角色编码查询是否存在TRADE_RELATION子台账表记录
     * 
     * @param rtd
     * @param tradeTypeCode
     * @param roleCode
     * @param modifyTag
     * @return boolean
     * @throws Exception
     *             wangjx 2013-9-26
     */
    @SuppressWarnings(
    { "rawtypes", "unchecked" })
    public static boolean chkRelaByTRM(RegTradeData rtd, String tradeTypeCode, String roleCode, String modifyTag) throws Exception
    {
        boolean bResult = false;
        if (!tradeTypeCode.equals(rtd.getTradeTypeCode()))
        {
            return bResult;
        }

        List<RelationTradeData> relaTradeDatas = rtd.getTradeDatas("TF_B_TRADE_RELATION");
        if (relaTradeDatas != null && relaTradeDatas.size() > 0)
        {
            for (Object obj : relaTradeDatas)
            {
                RelationTradeData relaTD = (RelationTradeData) obj;

                if (("*".equals(modifyTag) || modifyTag.equals(relaTD.getModifyTag())) && roleCode.equals(relaTD.getRoleCodeB()))
                {
                    bResult = true;
                    break;
                }
            }
        }

        return bResult;
    }

    /**
     * 根据业务编码，操作标识查询TRADE_SVC子台账表
     * 
     * @param rtd
     * @param tradeTypeCode
     * @param modifyTag
     * @return boolean
     * @throws Exception
     *             wangjx 2013-9-18
     */
    @SuppressWarnings(
    { "rawtypes", "unchecked" })
    public static boolean chkSvcByTM(RegTradeData rtd, String tradeTypeCode, String modifyTag) throws Exception
    {
        boolean bResult = false;
        if (!tradeTypeCode.equals(rtd.getTradeTypeCode()))
        {
            return bResult;
        }

        List<SvcTradeData> svcTradeDatas = rtd.getTradeDatas("TF_B_TRADE_SVC");
        if (svcTradeDatas != null && svcTradeDatas.size() > 0)
        {
            for (Object obj : svcTradeDatas)
            {
                SvcTradeData svcTD = (SvcTradeData) obj;

                if ("*".equals(modifyTag) || modifyTag.equals(svcTD.getModifyTag()))
                {
                    bResult = true;
                    break;
                }
            }
        }

        return bResult;
    }

    /**
     * 原短信模板匹配，td_b_trade_sms.obj_type_code='S'，splCheckBySql：TD_S_CPARAM-ExistsTradeDiscnt
     * 
     * @param rtd
     * @param discntCode
     * @param modifyTag
     * @param isRevolt
     *            0-不取反 1-取反
     * @return
     * @throws Exception
     */
    public static boolean existsTradeDiscnt(RegTradeData rtd, String discntCode, String modifyTag, String isRevolt) throws Exception
    {
        boolean bResult = false;

        // 只跟台帐有关，直接从btd中获取，减少数据库交互
        List<DiscntTradeData> discntTradeDatas = rtd.getTradeDatas("TF_B_TRADE_DISCNT");
        if (discntTradeDatas != null && discntTradeDatas.size() > 0)
        {
            for (Object obj : discntTradeDatas)
            {
                DiscntTradeData dtd = (DiscntTradeData) obj;

                if (discntCode.equals(dtd.getDiscntCode()) && ("*".equals(modifyTag) || modifyTag.equals(dtd.getModifyTag())))
                {
                    bResult = true;
                    break;
                }
            }
        }

        if ("1".equals(isRevolt))
        {
            bResult = !bResult;
        }

        return bResult;
    }

    /**
     * 原短信模板匹配，td_b_trade_sms.obj_type_code='S'，splCheckBySql：TD_S_CPARAM-ExistsTradeMultiDiscnt
     * 
     * @param rtd
     * @param paramCode
     * @param modifyTag
     * @param isRevolt
     *            0-不取反 1-取反
     * @return
     * @throws Exception
     */
    public static boolean existsTradeMultiDiscnt(RegTradeData rtd, String paramCode, String modifyTag, String isRevolt) throws Exception
    {
        boolean bResult = false;
        // TODO 原来获取的是tf_b_trade.TRADE_EPARCHY_CODE，是否应该为 tf_b_trade.EPARCHY_CODE
        String userEparchyCode = rtd.getMainTradeData().getEparchyCode();
        ;

        // 考虑2001传入
        IDataset dataset = ParamInfoQry.getCommparaByCode("CSM", "2001", paramCode, userEparchyCode);

        List<DiscntTradeData> discntTradeDatas = rtd.getTradeDatas("TF_B_TRADE_DISCNT");

        if (discntTradeDatas != null && discntTradeDatas.size() > 0)
        {
            for (Object obj : discntTradeDatas)
            {
                DiscntTradeData dtd = (DiscntTradeData) obj;
                if (("*".equals(modifyTag) || modifyTag.equals(dtd.getModifyTag())))
                {
                    for (int i = 0; i < dataset.size(); i++)
                    {
                        IData data = dataset.getData(i);
                        String paraCode1 = data.getString("PARA_CODE1");
                        if (paraCode1.equals(dtd.getDiscntCode()))
                        {
                            bResult = true;
                            break;
                        }
                    }

                }

                if (bResult)
                {
                    break;
                }
            }
        }

        if ("1".equals(isRevolt))
        {
            bResult = !bResult;
        }

        return bResult;
    }

    /**
     * 短信翻译 原编码KHG，新编码DISCNT_DESC TD_S_COMMPARA - SEL_BY_PK_2001_DISCNT 逻辑翻译, 修改了该逻辑，增加了eparchy_code处理
     * 
     * @param subsysCode
     * @param paramAttr
     * @param paraCode2
     * @param paraCode3
     * @return
     * @throws Exception
     */
    public static IDataset getCommPk2001Discnt(RegTradeData rtd, String subsysCode, String paramAttr, String paraCode2, String paraCode3) throws Exception
    {
        String trade_type_code = rtd.getTradeTypeCode();
        String acceptDate = rtd.getAcceptTime();
        String tempAcceptDate = SysDateMgr.transTime(acceptDate);
        String eparhcyCode = rtd.getMainTradeData().getEparchyCode();

        // 取优惠台账
        List<DiscntTradeData> discntTradeDatas = rtd.getTradeDatas("TF_B_TRADE_DISCNT");
        // 如果没有优惠台账，直接返回
        if (discntTradeDatas == null || discntTradeDatas.size() <= 0)
        {
            return null;
        }

        // 特定modify_tag判断
        List<DiscntTradeData> newDiscntTradeDatas = new ArrayList<DiscntTradeData>();
        for (int i = discntTradeDatas.size() - 1; i >= 0; i--)
        {
            if (paraCode2.equals(discntTradeDatas.get(i).getModifyTag()))
            {
                newDiscntTradeDatas.add(discntTradeDatas.get(i));
            }
        }

        // 如果没有优惠台账，直接返回
        if (newDiscntTradeDatas == null || newDiscntTradeDatas.size() <= 0)
        {
            return null;
        }

        // 获取参数配置，对原逻辑做了修改，增加了eparchy_code条件
        // IDataset commpara2001s = CommparaInfoQry.getCommPk2001Discnt2(subsysCode, paramAttr, paraCode3, eparhcyCode);
        Utility.error("方法不存在了");
        IDataset commpara2001s = new DatasetList();
        // 如果无参数配置，则直接返回
        if (IDataUtil.isEmpty(commpara2001s))
        {
            return null;
        }

        // 返回结果集
        IDataset returnDataset = new DatasetList();

        // 循环比较 参数 和 优惠台账
        for (int m = 0; m < commpara2001s.size(); m++)
        {
            IData commpara2001 = commpara2001s.getData(m);

            for (int n = 0; n < newDiscntTradeDatas.size(); n++)
            {
                String discntCode = newDiscntTradeDatas.get(n).getDiscntCode();
                if (commpara2001.getString("PARA_CODE1").equals(discntCode))
                {
                    String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode);
                    String discntExplain = UDiscntInfoQry.getDiscntExplainByDiscntCode(discntCode);

                    String discntStartDate = newDiscntTradeDatas.get(n).getStartDate();
                    String content1 = commpara2001.getString("PARA_CODE3");
                    content1 += "240".equals(trade_type_code) ? ",该套餐(营销活动)" : ",该套餐";
                    content1 += SysDateMgr.transTime(discntStartDate);
                    content1 += "生效,";
                    content1 += discntExplain;

                    commpara2001.put("PARA_CODE2", discntName);
                    commpara2001.put("PARA_CODE25", discntExplain);
                    commpara2001.put("ACCEPT_DATE", tempAcceptDate);
                    commpara2001.put("CONTENT1", content1);

                    returnDataset.add(commpara2001);
                }
            }
        }

        return returnDataset;
    }

    /**
     * 原短信模板匹配，td_b_trade_sms.obj_type_code='S'，splCheckBySql：TD_S_CPARAM-IsTradeOtherLike
     * 
     * @param rtd
     * @param rsrvValueCode
     * @param rsrvStr1
     * @param rsrvStr2
     * @param rsrvStr3
     * @param rsrvStr4
     * @param rsrvStr5
     * @param isRevolt
     * @return
     * @throws Exception
     */
    public static boolean isTradeOtherLike(RegTradeData rtd, String rsrvValueCode, String rsrvStr1, String rsrvStr2, String rsrvStr3, String rsrvStr4, String rsrvStr5, String modifyTag, String isRevolt) throws Exception
    {
        // 由于原sql中有条件rsrv_str1 like :RSRV_STR1，like不能翻译，所以还是从数据库获取台帐，而不从btd中获取
        boolean bResult = ParamInfoQry.isTradeOtherLike(rtd.getTradeId(), rsrvValueCode, rsrvStr1, rsrvStr2, rsrvStr3, rsrvStr4, rsrvStr5, modifyTag);

        if ("1".equals(isRevolt))
        {
            bResult = !bResult;
        }

        return bResult;
    }

    /**
     * 原短信模板匹配，td_b_trade_sms.obj_type_code='S'，splCheckBySql：TD_S_CPARAM-SEL_MULTI_COUNT
     * 
     * @param rtd
     * @param paraCode3
     * @param paraCode4
     * @return
     * @throws Exception
     */
    public static boolean selMultiCount(RegTradeData rtd, String paraCode3, String paraCode4, String isRevolt) throws Exception
    {
        String tradeId = rtd.getTradeId();
        String userId = rtd.getMainTradeData().getUserId();
        String tradeEparchyCode = CSBizBean.getTradeEparchyCode();

        boolean bResult = ParamInfoQry.selMultiCount(tradeId, userId, tradeEparchyCode, paraCode3, paraCode4);

        if ("1".equals(isRevolt))
        {
            bResult = !bResult;
        }

        return bResult;
    }

}
