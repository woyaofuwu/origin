
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.checkmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CheckForGrpSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 通用规则
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset chk(IData input) throws Exception
    {
        IData idata = CheckForGrp.chk(input);

        IDataset result = IDataUtil.idToIds(idata);

        return result;
    }

    /**
     * 闭合群业务受理规则
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset chkCloseGrpMain(IData input) throws Exception
    {
        IData idata = CheckForGrp.chkCloseGrpMain(input);

        IDataset result = IDataUtil.idToIds(idata);

        return result;
    }

    /**
     * 集团融合计费新增
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset chkCrtGrpUnifiedBill(IData input) throws Exception
    {

        IData idata = CheckForGrp.chkCrtGrpUnifiedBill(input);

        IDataset result = IDataUtil.idToIds(idata);

        return result;

    }

    /**
     * 集团融合 计费新增
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset chkDstGrpUnifiedBill(IData input) throws Exception
    {

        IData idata = CheckForGrp.chkDstGrpUnifiedBill(input);

        IDataset result = IDataUtil.idToIds(idata);

        return result;

    }

    // 成员新增 批量
    public IDataset chkGrpBatMebOrder(IData input) throws Exception
    {

        IData idata = CheckForGrp.chkGrpBatMebOrder(input);

        IDataset result = IDataUtil.idToIds(idata);

        return result;
    }

    // 成员变更
    public IDataset chkGrpMebChg(IData input) throws Exception
    {

        IData idata = CheckForGrp.chkGrpMebChg(input);

        IDataset result = IDataUtil.idToIds(idata);

        return result;
    }

    // 成员注销
    public IDataset chkGrpMebDestory(IData input) throws Exception
    {

        IData idata = CheckForGrp.chkGrpMebDestory(input);

        IDataset result = IDataUtil.idToIds(idata);

        return result;
    }

    /**
     * 集团产品成员营销受理前条件判断
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset chkGrpMeberSale(IData input) throws Exception
    {
        IData idata = CheckForGrp.chkGrpMeberSale(input);

        IDataset result = IDataUtil.idToIds(idata);

        return result;

    }

    // 成员新增
    public IDataset chkGrpMebOrder(IData input) throws Exception
    {

        IData idata = CheckForGrp.chkGrpMebOrder(input);

        IDataset result = IDataUtil.idToIds(idata);

        return result;
    }

    /**
     * 成员付费关系变更业务受理规则
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset chkGrpMebPayMod(IData input) throws Exception
    {
        IData idata = CheckForGrp.chkGrpMebPayMod(input);

        IDataset result = IDataUtil.idToIds(idata);

        return result;
    }

    /**
     * 集团产品成员营销受理前条件判断
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset chkGrpMebSale(IData input) throws Exception
    {
        IData idata = CheckForGrp.chkGrpMebSale(input);

        IDataset result = IDataUtil.idToIds(idata);

        return result;

    }

    /**
     * 集团营销受理前条件判断
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset chkGrpSale(IData input) throws Exception
    {
        IData idata = CheckForGrp.chkGrpSale(input);

        IDataset result = IDataUtil.idToIds(idata);

        return result;

    }

    // 集团变更
    public IDataset chkGrpUserChg(IData input) throws Exception
    {

        IData idata = CheckForGrp.chkGrpUserChg(input);

        IDataset result = IDataUtil.idToIds(idata);

        return result;
    }

    // 集团销户
    public IDataset chkGrpUserDestroy(IData input) throws Exception
    {

        IData idata = CheckForGrp.chkGrpUserDestory(input);

        IDataset result = IDataUtil.idToIds(idata);

        return result;
    }

    // 集团开户
    public IDataset chkGrpUserOpen(IData input) throws Exception
    {

        IData idata = CheckForGrp.chkGrpUserOpen(input);

        IDataset result = IDataUtil.idToIds(idata);

        return result;

    }

    /**
     * 集团高级付费关系规则
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset chkPayRelaAdv(IData input) throws Exception
    {
        IData idata = CheckForGrp.chkPayRelaAdvChg(input);

        IDataset result = IDataUtil.idToIds(idata);

        return result;

    }

    /**
     * 普通V网短号码验证
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset chkVpmnShortCode(IData input) throws Exception
    {

        IData idata = CheckForGrp.chkVpmnShortCode(input);

        IDataset result = IDataUtil.idToIds(idata);

        return result;

    }

    /**
     * 普通V网升级融合V网受理条件判断
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset chkVpmnToCntrx(IData input) throws Exception
    {
        IData idata = CheckForGrp.chkVpmnToCntrx(input);

        IDataset result = IDataUtil.idToIds(idata);

        return result;

    }
}
