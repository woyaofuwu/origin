/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.familytrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

/**
 * @CREATED by gongp@2014-5-21 修改历史 Revision 2014-5-21 下午03:35:57
 */
public class FamilyUnionPaySVC extends CSBizService
{

    private static final long serialVersionUID = 5502421746441250576L;

    /**
     * 副号码校验
     * 
     * @param input
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-18
     */
    public IData checkBySerialNumber(IData input) throws Exception
    {

        FamilyUnionPayBean bean = BeanManager.createBean(FamilyUnionPayBean.class);

        return bean.checkBySerialNumber(input);
    }

    /**
     * 校验主副号码是否可以办理统一账户付费业务 new SS.FamilyUnionPaySVC.familyUnionPayCheck old :ITF_CRM_FamilyUnionPayCheck
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-19
     */
    public IDataset familyUnionPayCheck(IData input) throws Exception
    {

        IDataUtil.chkParam(input, "X_TAG"); // 校验类型：1-校验主号 2-校验副号
        IDataUtil.chkParam(input, "SERIAL_NUMBER"); // 服务号码

        String xTag = input.getString("X_TAG", "").trim();
        if (!"1".equals(xTag) && !"2".equals(xTag))
        {
            // common.error("接口校验号码是否可以办理统一付费业务时X_TAG不为1或2！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_823);
        }
        String serialNumber = input.getString("SERIAL_NUMBER", "");

        UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
        IDataset dataset = new DatasetList();
        // -----------校验主号----------------
        if ("1".equals(xTag))
        {

            input.put("USER_ID", ucaData.getUser().getUserId());
            input.put("USER_STATE_CODESET", ucaData.getUser().getUserStateCodeset());

            dataset = this.loadChildTradeInfo(input);

        }
        // ----------校验副号------------------
        if ("2".equals(xTag))
        {
            FamilyUnionPayBean bean = BeanManager.createBean(FamilyUnionPayBean.class);
            //之前传的是"" 查不出来用户信息  如果为校验副号码  需要传入主号码MIAN_SERIAL_NUMBER  add by  20170604
            String mianSn = input.getString("MIAN_SERIAL_NUMBER", "");
            bean.checkOtherSerialBusiLimits(ucaData.getUser().toData(),mianSn, "0");
            // ---------集团统付号码作为统一付费业务副卡时，要进行界面提示-----------
            IDataset ds = PayRelaInfoQry.getPayRelatInfoByUserIdNow2(ucaData.getUserId());
            if (IDataUtil.isNotEmpty(ds))
            {
                // common.error("889930", "该号码["+serialNumber+"]已办理集团统付业务，请确认！");
                CSAppException.apperr(FamilyException.CRM_FAMILY_824, serialNumber);
            }
        }

        return dataset;
    }

    public IData familyUnionPayMebMgr(IData data) throws Exception
    {

        IDataUtil.chkParam(data, "SERIAL_NUMBER_B"); // 副号
        IDataUtil.chkParam(data, "MODIFY_TAG"); // 操作类型:0-新增；1-删除
        IDataUtil.chkParam(data, "X_TAG"); // 发起类型:1-主号发起；2-副号发起

        String xTag = data.getString("X_TAG", "").trim();
        String modifyTag = data.getString("MODIFY_TAG", "").trim();
        if (!"1".equals(xTag) && !"2".equals(xTag))
        {
            // common.error("X_TAG参数传值不正确！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_726);
        }

        if ("1".equals(xTag))
        {
            IDataUtil.chkParam(data, "SERIAL_NUMBER");
        }
        else if ("2".equals(xTag))
        {
            if (!"1".equals(modifyTag))
            {
                // common.error("副号发起取消统一付费关系时MODIFY_TAG传值不为1！");
                CSAppException.apperr(FamilyException.CRM_FAMILY_727);
            }
            String serialNumberB = data.getString("SERIAL_NUMBER_B", "");

            UcaData ucaData = UcaDataFactory.getNormalUca(serialNumberB);

            String userIdB = ucaData.getUserId();

            IDataset uuIdbInfos = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userIdB, "56", "2");
            ;

            if (IDataUtil.isEmpty(uuIdbInfos))
            {
                // common.error("889955", "获取成员["+serialNumberB+"]的统一付费关系无数据！");
                CSAppException.apperr(FamilyException.CRM_FAMILY_821, serialNumberB);
            }

            String userIdA = uuIdbInfos.getData(0).getString("USER_ID_A", "-1");

            IDataset uuIdaInfos = RelaUUInfoQry.getSEL_USER_ROLEA(userIdA, "1", "56");

            if (IDataUtil.isEmpty(uuIdaInfos))
            {
                // common.error("889956", "获取成员["+serialNumberB+"]的统一付费关系主号无数据！");
                CSAppException.apperr(FamilyException.CRM_FAMILY_729, serialNumberB);
            }

            data.put("SERIAL_NUMBER", uuIdaInfos.getData(0).getString("SERIAL_NUMBER_B", ""));
            data.put("REMARK", "副号[" + serialNumberB + "]发起取消统一付费关系");

        }

        IData numberBInfo = new DataMap();

        numberBInfo.put("SERIAL_NUMBER_B", data.getString("SERIAL_NUMBER_B"));
        numberBInfo.put("MODIFY_TAG", data.getString("MODIFY_TAG"));

        IDataset memberDatas = new DatasetList();
        memberDatas.add(numberBInfo);

        data.put("MEMBER_DATAS", memberDatas);
        data.put("INTF_FLAG", "2");
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());

        return CSAppCall.call("SS.FamilyUnionPayRegSVC.tradeReg", data).getData(0);

    }

    /**
     * 统一账户付费关系管理，响应主号码开通、关闭统一付费关系请求
     * 
     * @param data
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-19
     */
    public IDataset familyUnionPayMgr(IData data) throws Exception
    {

        IDataUtil.chkParam(data, "SERIAL_NUMBER"); // 服务号码
        IDataUtil.chkParam(data, "MODIFY_TAG"); // 操作类型:0-新增；1-删除

        data.put("INTF_FLAG", "1");
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());

        return CSAppCall.call("SS.FamilyUnionPayRegSVC.tradeReg", data);

    }

    /**
     * 统一付费查询接口
     * 
     * @param data
     * @return
     * @throws Exception
     * @author zhouwu
     * @date 2014-07-03 16:21:12
     */
    public IDataset familyUnionPayQuery(IData data) throws Exception
    {
        FamilyUnionPayBean bean = BeanManager.createBean(FamilyUnionPayBean.class);
        return bean.familyUnionPayQuery(data);
    }

    /**
     * 统一付费关系查询
     * 
     * @param input
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-26
     */
    public IData getFamilyUnionPayInfos(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");

        IData returnData = new DataMap();
        IDataset uuMembers = new DatasetList();
        IData memberInfo = new DataMap();

        // -------获取查询号码的家庭统付关系角色-------------
        IDataset ds = RelaUUInfoQry.getRelaUUInfoByRol(userId, "56");
        if (IDataUtil.isNotEmpty(ds))
        {
            memberInfo = ds.getData(0);

            String roleCodeB = memberInfo.getString("ROLE_CODE_B", "");
            String userIdA = memberInfo.getString("USER_ID_A", "");

            // ----查询号码是主号则列出所有成员信息---
            if ("1".equals(roleCodeB))
            {
                uuMembers = RelaUUInfoQry.queryAllUnionPayMembers(userIdA, "56", "2");
            }
            else
            {// ----查询号码是副号码则只列出主号码信息---
                uuMembers = RelaUUInfoQry.getSEL_USER_ROLEA(userIdA, "1", "56");
            }
        }
        // -------------翻译家庭统付UU关系角色-------------
        if (IDataUtil.isNotEmpty(uuMembers))
        {

            for (int i = 0, size = uuMembers.size(); i < size; i++)
            {
                IData each = uuMembers.getData(i);

                String roleCodeBTmp = each.getString("ROLE_CODE_B", "");
                String userIdB = each.getString("USER_ID_B", "");

                if ("1".equals(roleCodeBTmp))
                {
                    each.put("ROLE_CODE_B", "主卡");
                }
                else
                {
                    each.put("ROLE_CODE_B", "副卡");
                }

                // ---------获取家庭网短号-----------
                IDataset scDs = RelaUUInfoQry.getUserRelationByUR(userIdB, "45");

                if (IDataUtil.isNotEmpty(scDs))
                {
                    IData scDt = scDs.getData(0);
                    String shortCode = scDt.getString("SHORT_CODE", "");
                    if (shortCode.trim().length() > 0)
                    {
                        each.put("SHORT_CODE", shortCode);
                    }
                }
            }
        }
        if (IDataUtil.isNotEmpty(memberInfo))
        {
            String roleCodeBTmp = memberInfo.getString("ROLE_CODE_B", "");
            if ("1".equals(roleCodeBTmp))
            {
                memberInfo.put("ROLE_CODE_B", "主卡");
            }
            else
            {
                memberInfo.put("ROLE_CODE_B", "副卡");
            }
        }

        returnData.put("QRY_MEMBER_LIST", uuMembers);
        returnData.put("COND_MEMBER_INFO", memberInfo);

        return returnData;
    }

    /**
     * 统一付费主卡号码校验与查询
     * 
     * @param input
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-21
     */
    public IDataset loadChildTradeInfo(IData input) throws Exception
    {

        FamilyUnionPayBean bean = BeanManager.createBean(FamilyUnionPayBean.class);

        return bean.loadChildTradeInfo(input);

    }

    @Override
    public void setTrans(IData input) throws Exception
    {

        // IBOSS接口入参转换,避免找不到路由
        if ("4".equals(getVisit().getInModeCode()))
        {
            if (StringUtils.isNotBlank(input.getString("SERIAL_NUMBER")))
            {
                return;
            }
            else
            {
                input.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER_B"));
                return;
            }
        }
    }

}
