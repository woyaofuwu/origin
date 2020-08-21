
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bboss.BbossMemDataUdrInfoQry;

public class CommparaInfoQrySVC extends CSBizService
{
    /**
     * 序列化
     */
    private static final long serialVersionUID = 1L;

    public static IDataset getCommparaInfoByBizCode(IData input) throws Exception
    {
        String subsysCode = input.getString("SUBSYS_CODE");
        String paramAttr = input.getString("PARAM_ATTR");
        String paramCode = input.getString("PARAM_CODE");
        return CommparaInfoQry.getCommparaAllCol(subsysCode, paramAttr, paramCode, "ZZZZ");
    }

    /**
     * 根据paracode1查TD_S_COMMPARA
     * 
     * @author xunyl
     * @version 创建时间：2013-03-20
     */
    public static IDataset getCommparaInfoByParacode1AndAttr(IData inparams) throws Exception
    {
        String subsysCode = inparams.getString("SUBSYS_CODE");
        String paramAttr = inparams.getString("PARAM_ATTR");
        String paraCode1 = inparams.getString("PARA_CODE1");
        String eparchyCodes = inparams.getString("EPARCHY_CODE");
        return CommparaInfoQry.getCommparaByAttrCode1(subsysCode, paramAttr, paraCode1, eparchyCodes, null);
    }

    /**
     * 获取产品反馈属性
     * 
     * @data 2013-9-14
     * @author liaolc
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset getCommparaInfoByProdcuIdOrServicId(IData input) throws Exception
    {
        String subsysCode = input.getString("SUBSYS_CODE");
        String paramAttr = input.getString("PARAM_ATTR");
        String paramCode = input.getString("PARAM_CODE");
        // return CommparaInfoQry.getCommparaInfoByProdcuIdOrServicId(subsysCode, paramAttr, paramCode);
        CSAppException.apperr(CrmCommException.CRM_COMM_1, "方法不存在了");
        return null;
    }

    public static IDataset getPayItemsParam(IData input) throws Exception
    {
        String subsysCode = input.getString("SUBSYS_CODE");
        String paramAttr = input.getString("PARAM_ATTR");
        String paramCode = input.getString("PARAM_CODE");
        return CommparaInfoQry.getPayItemsParam(subsysCode, paramAttr, paramCode, null);
    }

    /**
     * 获得TD_S_COMMPARA表中配置的产品的信息，以及产品对应的其他信息。
     * 
     * @data 2013-3-25
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset getProductAndOther(IData inparams) throws Exception
    {
        String sql = inparams.getString("sql");
        String paramAttr = inparams.getString("PARAM_ATTR");
        String paramCode = inparams.getString("PARAM_ATTR");

        CSAppException.apperr(CrmCommException.CRM_COMM_1, "方法不存在了");
        return null;
    }

    /**
     * 查询销户原因
     * 
     * @return
     */
    public static IDataset queryDestroyReason(IData input) throws Exception
    {
        String type = input.getString("TYPE");
        String parentId = input.getString("PARENT_ID");
        String eparchyCode = input.getString("EPARCHY_CODE");
        return CommparaInfoQry.queryDestroyReason(type, parentId, eparchyCode);
    }

    /**
     * 获取commpara表参数
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getCommpara(IData idata) throws Exception
    {
        String subsysCode = idata.getString("SUBSYS_CODE");
        String paramAttr = idata.getString("PARAM_ATTR");
        String paramCode = idata.getString("PARAM_CODE");
        String eparchyCode = idata.getString("EPARCHY_CODE");
        IDataset output = CommparaInfoQry.getCommparaAllCol(subsysCode, paramAttr, paramCode, eparchyCode);
        return output;
    }

    /**
     * 获取commpara表参数
     * 
     * @param idata
     * @return
     * @throws Exception
     */
    public IDataset getCommparaByParser(IData idata) throws Exception
    {
        String subsysCode = idata.getString("SUBSYS_CODE");
        String paramAttr = idata.getString("PARAM_ATTR");
        String paramCode = idata.getString("PARAM_CODE");
        String eparchyCode = idata.getString("EPARCHY_CODE");
        IDataset output = CommparaInfoQry.getCommparaAllColByParser(subsysCode, paramAttr, paramCode, eparchyCode);
        return output;
    }

    /**
     * 获取commpara表信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getCommparaInfo(IData input) throws Exception
    {

        String subsysCode = input.getString("SUBSYS_CODE");
        String paramAttr = input.getString("PARAM_ATTR");
        String paramCode = input.getString("PARAM_CODE");
        String eparchyCode = input.getString("EPARCHY_CODE");
        // IDataset output = CommparaInfoQry.getCommparaInfo(subsysCode, paramAttr, paramCode, eparchyCode);
        CSAppException.apperr(CrmCommException.CRM_COMM_1, "方法不存在了");
        return null;
    }

    /**
     * 获取集团业务特殊开机commpara表参数
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getCommparaInfoBy16(IData input) throws Exception
    {
        String subsysCode = input.getString("SUBSYS_CODE");
        String paramAttr = input.getString("PARAM_ATTR");
        String paramCode = input.getString("PARAM_CODE");
        String paraCode1 = input.getString("PARA_CODE1");
        String paraCode6 = input.getString("PARA_CODE6");
        String eparchyCode = input.getString("EPARCHY_CODE");

        IDataset data = CommparaInfoQry.getCommparaInfoBy16(subsysCode, paramAttr, paramCode, paraCode1, paraCode6, eparchyCode, this.getPagination());

        return data;
    }

    public IDataset getCommparaInfoBy5(IData input) throws Exception
    {
        String subsysCode = input.getString("SUBSYS_CODE");
        String paramAttr = input.getString("PARAM_ATTR");
        String paramCode = input.getString("PARAM_CODE");
        String paraCode1 = input.getString("PARA_CODE1");
        String eparchyCode = input.getString("EPARCHY_CODE");

        IDataset data = CommparaInfoQry.getCommparaInfoBy5(subsysCode, paramAttr, paramCode, paraCode1, eparchyCode, this.getPagination());

        return data;
    }

    /**
     * 获取集团业务特殊开机commpara表参数
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getCommparaInfoBy6(IData input) throws Exception
    {
        String subsysCode = input.getString("SUBSYS_CODE");
        String paramAttr = input.getString("PARAM_ATTR");
        String paramCode = input.getString("PARAM_CODE");
        String paraCode1 = input.getString("PARA_CODE1");
        String paraCode4 = input.getString("PARA_CODE4");
        String eparchyCode = input.getString("EPARCHY_CODE");

        IDataset data = CommparaInfoQry.getCommparaInfoBy6(subsysCode, paramAttr, paramCode, paraCode1, paraCode4, eparchyCode, this.getPagination());

        return data;
    }

    /**
     * liaolc 获取commpara表参数
     * 
     * @param input
     *            getPagination()
     * @return
     * @throws Exception
     */

    public IDataset getCommparaInfoByAttr(IData input) throws Exception
    {

        String subsysCode = input.getString("SUBSYS_CODE");
        String paramAttr = input.getString("PARAM_ATTR");
        String eparchyCode = input.getString("EPARCHY_CODE");

        IDataset data = CommparaInfoQry.getOnlyByAttr(subsysCode, paramAttr, eparchyCode);

        return data;
    }

    /**
     * ADC获取敏感词汇 根据param_attr，subsys_code，PARAM_CODE查询参数
     * 
     * @author liaolc
     * @version 创建时间：2014-3-14
     */
    public IDataset getCommparaInfoByAttrAndParam(IData input) throws Exception
    {
        String paramCode = input.getString("PARAM_CODE");
        String sybsysCode = input.getString("SYBSYS_CODE");
        String paramAttr = input.getString("PARAM_ATTR");

        // IDataset output = CommparaInfoQry.getCommparaInfoByAttrAndParam(paramCode,sybsysCode, paramAttr);
        CSAppException.apperr(CrmCommException.CRM_COMM_1, "方法不存在了");
        return null;
    }

    /**
     * 根据param_attr，subsys_code，PARAM_CODE4查询参数
     * 
     * @author weixb3
     * @param input
     *            getPagination()
     * @return
     * @throws Exception
     */

    public IDataset getCommparaInfoByAttrAndParam4(IData input) throws Exception
    {
        String sybsysCode = input.getString("SYBSYS_CODE");
        String paramAttr = input.getString("PARAM_ATTR");
        String paraCode4 = input.getString("PARA_CODE4");

        IDataset data = CommparaInfoQry.getCommparaInfoByParacode4AndAttr(sybsysCode, paramAttr, paraCode4, "ZZZZ");
        return data;
    }

    /**
     * 根据paracode4和param_attr查TD_S_COMMPARA
     * 
     * @author weixb3
     * @version 创建时间：2013-3-14
     */
    public IDataset getCommparaInfoByParacode4AndAttr(IData input) throws Exception
    {
        String subsysCode = input.getString("SUBSYS_CODE");
        String paramAttr = input.getString("PARAM_ATTR");
        String paraCode4 = input.getString("PARA_CODE4");
        String eparchyCode = input.getString("EPARCHY_CODE");

        IDataset output = CommparaInfoQry.getCommparaInfoByParacode4AndAttr(subsysCode, paramAttr, paraCode4, eparchyCode);
        return output;
    }

    /**
     * 集团业务办理时，查询是否需要录入发展人信息
     * 
     * @data 2013-3-28
     * @author weixb3
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset getGpDeployerVisibleConfig(IData inparams) throws Exception
    {
        String tradeTypeCode = inparams.getString("TRADE_TYPE_CODE");
        String eparchyCode = inparams.getString("EPARCHY_CODE");
        // return CommparaInfoQry.getGpDeployerVisibleConfig(tradeTypeCode, eparchyCode);
        CSAppException.apperr(CrmCommException.CRM_COMM_1, "方法不存在了");
        return null;
    }

    /**
     * 获取commpara表参数
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getTacctInfo(IData idata) throws Exception
    {
        CSAppException.apperr(CrmCommException.CRM_COMM_1, "方法不存在了");
        return null;
    }

    /**
     * 查询工单属性 modify by fanti3 2014-8-15
     * 
     * @data 2013-3-28
     * @author weixb3
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset orderDetailQry(IData inparams) throws Exception
    {
        String productOrderNumber = inparams.getString("PRODUCTORDERNUMBER");

        return PoTradePlusInfoQry.qryPoTradePlusInfoByProductOrderNo(productOrderNumber, getPagination());
    }

    /**
     * 查询成员工单
     * 
     * @data 2013-3-28
     * @author weixb3
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset orderMemQry(IData inparams) throws Exception
    {
        String productOrderNumber = inparams.getString("PRODUCTORDERNUMBER");

        return BbossMemDataUdrInfoQry.qryBBossMemUdrByProductOrderNo(productOrderNumber, getPagination());
    }

    /**
     * 查询订单已经填写的反馈属性
     * 
     * @data 2013-4-15
     * @author weixb3
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset queryBbossOrderExtendsRecord(IData inparams) throws Exception
    {
        String productOrderNumber = inparams.getString("PRODUCTORDERNUMBER");
        String acceptMonth = inparams.getString("ACCEPT_MONTH");
        CSAppException.apperr(CrmCommException.CRM_COMM_1, "方法不存在了");
        return null;
    }

    /**
     * 查询BBOSS反馈属性,连接tf_b_potradeplus表
     * 
     * @data 2013-3-28
     * @author weixb3
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset queryBbossOrderOpenExtends(IData inparams) throws Exception
    {
        String productOrderNumber = inparams.getString("PRODUCTORDERNUMBER");
        String acceptMonth = inparams.getString("ACCEPT_MONTH");
        String eparchyCode = inparams.getString("EPARCHY_CODE");
        String poproductSpecNumber = inparams.getString("POPRODUCTSPECNUMBER");
        CSAppException.apperr(CrmCommException.CRM_COMM_1, "方法不存在了");
        return null;
    }

    /**
     * 根据td_s_commpara属性组配置的主属性ID，查询下发的属性组值
     * 
     * @data 2013-3-28
     * @author weixb3
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset queryCharacterGroup(IData inparams) throws Exception
    {
        String productSpecCharacterNumber = inparams.getString("PRODUCTSPECCHARACTERNUMBER");
        String productOrderNumber = inparams.getString("PRODUCTORDERNUMBER");
        CSAppException.apperr(CrmCommException.CRM_COMM_1, "方法不存在了");
        return null;
    }

    /**
     * 根据产品、包判断是否是无返还类活动
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public boolean queryCommparaByParaCode1ParaAttr(IData inparams) throws Exception
    {
        String productId = inparams.getString("PRODUCT_ID");
        String packageId = inparams.getString("PACKAGE_ID");
        return CommparaInfoQry.queryCommparaByParaCode1ParaAttr(productId, packageId);
    }

    public IDataset queryCommParaInfosByCode1(IData inparams) throws Exception
    {
        String eparchyCode = inparams.getString("EPARCHY_CODE");
        String subsysCode = inparams.getString("SUBSYS_CODE");
        String paramAttr = inparams.getString("PARAM_ATTR");
        String paraCode1 = inparams.getString("PARA_CODE1");
        // return CommparaInfoQry.queryCommParaInfosByCode1(subsysCode, paramAttr, paraCode1, eparchyCode);
        CSAppException.apperr(CrmCommException.CRM_COMM_1, "方法不存在了");
        return null;
    }

    public IDataset queryCommParaInfosByCode1Code2(IData inparams) throws Exception
    {
        String eparchyCode = inparams.getString("EPARCHY_CODE");
        String subsysCode = inparams.getString("SUBSYS_CODE");
        String paramAttr = inparams.getString("PARAM_ATTR");
        String paraCode1 = inparams.getString("PARA_CODE1");
        String paraCode2 = inparams.getString("PARA_CODE2");
        // return CommparaInfoQry.queryCommParaInfosByCode1Code2(subsysCode, paramAttr, paraCode1, paraCode2,
        // eparchyCode);
        CSAppException.apperr(CrmCommException.CRM_COMM_1, "方法不存在了");
        return null;
    }

    /**
     * 获取产品反馈属性
     * 
     * @data 2013-3-28
     * @author weixb3
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset queryExtendsParams(IData inparams) throws Exception
    {
        String eparchyCode = inparams.getString("EPARCHY_CODE");
        String productSpecNumber = inparams.getString("PRODUCTSPECNUMBER");
        CSAppException.apperr(CrmCommException.CRM_COMM_1, "方法不存在了");
        return null;
    }
    
    public IDataset getEnableCommparaInfoByCode12(IData inparams) throws Exception
    {
    	String subsysCode = inparams.getString("SUBSYS_CODE");
    	String paramAttr = inparams.getString("PARAM_ATTR");
    	String paramCode = inparams.getString("PARAM_CODE");
    	String paramCode1 = inparams.getString("PARA_CODE1");
    	String paramCode2 = inparams.getString("PARA_CODE2");
        String eparchyCode = inparams.getString("EPARCHY_CODE");

        return CommparaInfoQry.getEnableCommparaInfoByCode12(subsysCode,paramAttr,paramCode,paramCode1,paramCode2,eparchyCode);
    }
    
}
