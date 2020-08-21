
package com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserImpuInfoQry;
import com.asiainfo.veris.crm.order.soa.group.common.query.CustManagerTJNumQry;
import com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade.ims.CentrexTeamDeal;
import com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade.ims.ChangeGrpUserParam;
import com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade.ims.ChangeIMSMemParam;
import com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade.ims.ChangeYhtElement;
import com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade.ims.CreateIMSGrpMem;
import com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade.ims.IMSBlackWhiteDeal;

public class GrpIMSDealIntf extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 集团成员产品变更
     * 
     * @param pd
     * @param idata
     * @return
     * @throws Throwable
     */
    public static IDataset changeYhtMemElement(IData idata) throws Exception
    {
        // 返回结果
        IDataset resultInfos = new DatasetList();
        IData result = new DataMap();
        result.put("X_RESULTINFO", "OK");
        result.put("X_RESULTCODE", "0");

        try
        {
            IDataset USER_INFOS = idata.getDataset("USER_INFOS");
            IDataset PRODUCT_ATTR = idata.getDataset("PRODUCT_ATTR");

            if (PRODUCT_ATTR.size() == 0 || PRODUCT_ATTR == null)
            {
                CSAppException.apperr(ParamException.CRM_PARAM_65);
            }
            String zTag = String.valueOf(PRODUCT_ATTR.get(0, "ATTR_VALUE"));// 振动方式
            if (zTag == null || zTag.equals("") || (!zTag.equals("0") && !zTag.equals("1")))
            {
                CSAppException.apperr(ParamException.CRM_PARAM_68);
            }
            ChangeYhtElement bean = new ChangeYhtElement();
            IDataset tradeResult = bean.changeYhtMemElement(idata, USER_INFOS, PRODUCT_ATTR);

            result.put("ORDER_ID", tradeResult.getData(0).getString("ORDER_ID"));
            result.put("TRADE_ID", tradeResult.getData(0).getString("TRADE_ID"));
            resultInfos.add(result);
            return resultInfos;
        }
        catch (Exception e)
        {
            result.put("X_LAST_RESULTINFO", e.getMessage());
            result.put("X_RESULTINFO", e.getMessage());
            result.put("X_RESULTCODE", "99");
            resultInfos.add(result);
            return resultInfos;
        }
    }

    /*
     * @description Centrex业务修改集团用户参数及资费
     * @author liuzz
     * @date 2014/05/30
     */
    public static IDataset OperGrpUserAttr(IData data) throws Exception
    {
        // 返回结果
        IDataset resultInfos = new DatasetList();
        IData result = new DataMap();
        result.put("X_RESULTINFO", "OK");
        result.put("X_RESULTCODE", "0");
        try
        {
            IDataset tradeResult = ChangeGrpUserParam.OperGrpUserAttr(data);
            result.put("ORDER_ID", tradeResult.getData(0).getString("ORDER_ID"));
            result.put("TRADE_ID", tradeResult.getData(0).getString("TRADE_ID"));
            resultInfos.add(result);
            return resultInfos;
        }
        catch (Exception e)
        {
            result.put("X_LAST_RESULTINFO", e.getMessage());
            result.put("X_RESULTINFO", e.getMessage());
            result.put("X_RESULTCODE", "99");

            resultInfos.add(result);
            return resultInfos;
        }
    }

    /*
     * @description 修改成员用户的资费
     * @author liuzz
     * @date 2014/05/30
     */
    public static IDataset OperModifyMebDiscnt(IData data) throws Exception
    {
        // 返回结果
        IDataset resultInfos = new DatasetList();
        IData result = new DataMap();
        result.put("X_RESULTINFO", "OK");
        result.put("X_RESULTCODE", "0");
        try
        {
            IDataset tradeResult = ChangeIMSMemParam.OperModifyMebDiscnt(data);
            result.put("ORDER_ID", tradeResult.getData(0).getString("ORDER_ID"));
            result.put("TRADE_ID", tradeResult.getData(0).getString("TRADE_ID"));
            resultInfos.add(result);
            return resultInfos;
        }
        catch (Exception e)
        {
            result.put("X_LAST_RESULTINFO", e.getMessage());
            result.put("X_RESULTINFO", e.getMessage());
            result.put("X_RESULTCODE", "99");

            resultInfos.add(result);
            return resultInfos;
        }
    }

    /*
     * @description 成员个性化参数
     * @author liuzz
     * @date 2014/05/30
     */
    public static IDataset OperModifyMebAttr(IData data) throws Exception
    {
        // 返回结果
        IDataset resultInfos = new DatasetList();
        IData result = new DataMap();
        result.put("X_RESULTINFO", "OK");
        result.put("X_RESULTCODE", "0");
        try
        {
            IDataset tradeResult = ChangeIMSMemParam.OperModifyMebAttr(data);
            result.put("ORDER_ID", tradeResult.getData(0).getString("ORDER_ID"));
            result.put("TRADE_ID", tradeResult.getData(0).getString("TRADE_ID"));
            resultInfos.add(result);
            return resultInfos;
        }
        catch (Exception e)
        {
            result.put("X_LAST_RESULTINFO", e.getMessage());
            result.put("X_RESULTINFO", e.getMessage());
            result.put("X_RESULTCODE", "99");

            resultInfos.add(result);
            return resultInfos;
        }
    }

    /*
     * @description 融合产品集团成员新增
     * @author liuzz
     * @date 2014/05/30
     */
    public static IDataset OperAddGrpMeb(IData data) throws Exception
    {
        // 返回结果
        IDataset resultInfos = new DatasetList();
        IData result = new DataMap();
        result.put("X_RESULTINFO", "OK");
        result.put("X_RESULTCODE", "0");
        try
        {
            IDataset tradeResult = CreateIMSGrpMem.OperAddGrpMeb(data);
            result.put("ORDER_ID", tradeResult.getData(0).getString("ORDER_ID"));
            result.put("TRADE_ID", tradeResult.getData(0).getString("TRADE_ID"));
            resultInfos.add(result);
            return resultInfos;
        }
        catch (Exception e)
        {
            result.put("X_LAST_RESULTINFO", e.getMessage());
            result.put("X_RESULTINFO", e.getMessage());
            result.put("X_RESULTCODE", "99");

            resultInfos.add(result);
            return resultInfos;
        }
    }

    /*
     * @description 融合产品集团成员退订
     * @author liuzz
     * @date 2014/05/30
     */
    public static IDataset OperDelGrpMeb(IData data) throws Exception
    {
        String strMebSn = data.getString("SERIAL_NUMBER", "");
        String strGrpUserID = data.getString("USER_ID_A", "");

        IData param = new DataMap();
        param.put("USER_ID", strGrpUserID);
        // 查询集团信息
        UcaData grpUcaData = UcaDataFactory.getNormalUcaByUserIdForGrp(param);
        String productId = grpUcaData.getProductId();
        if (!"2222".equals(productId) && !"8001".equals(productId) && !"8016".equals(productId) && !"6130".equals(productId))
        {
            CSAppException.apperr(GrpException.CRM_GRP_71);
        }
        // 查询成员用户信息
        UcaData mebUcaData = UcaDataFactory.getNormalUcaForGrp(strMebSn);

        // 查询集团成员关系是否存在
        IDataset grpRelat = RelaUUInfoQry.qryUU(strGrpUserID, mebUcaData.getUserId(), "", null, null);
        if (IDataUtil.isEmpty(grpRelat))
        {
            CSAppException.apperr(GrpException.CRM_GRP_129, mebUcaData.getUserId(), strGrpUserID);
        }
        IData conmmitData = new DataMap();
        conmmitData.put("USER_ID", strGrpUserID);
        conmmitData.put("SERIAL_NUMBER", strMebSn);
        conmmitData.put("PRODUCT_ID", productId);

        // 返回结果
        IDataset resultInfos = new DatasetList();
        IData result = new DataMap();
        result.put("X_RESULTINFO", "OK");
        result.put("X_RESULTCODE", "0");
        try
        {
            IDataset tradeResult = CSAppCall.call("CS.DestroyGroupMemberSvc.destroyGroupMember", conmmitData);
            result.put("ORDER_ID", tradeResult.getData(0).getString("ORDER_ID"));
            result.put("TRADE_ID", tradeResult.getData(0).getString("TRADE_ID"));
            resultInfos.add(result);
            return resultInfos;
        }
        catch (Throwable e)
        {
            result.put("X_LAST_RESULTINFO", Utility.getBottomException(e).getMessage());
            result.put("X_RESULTINFO", Utility.getBottomException(e).getMessage());
            result.put("X_RESULTCODE", "99");

            resultInfos.add(result);
            return resultInfos;
        }

    }

    /*
     * @description 集团黑白名单变更
     * @author liuzz
     * @date 2014/05/30
     */
    public static IDataset OperGrpBWList(IData data) throws Exception
    {
        // 返回结果
        IDataset resultInfos = new DatasetList();
        IData result = new DataMap();
        result.put("X_RESULTINFO", "OK");
        result.put("X_RESULTCODE", "0");
        try
        {
            IDataset tradeResult = IMSBlackWhiteDeal.OperGrpBWList(data);
            result.put("ORDER_ID", tradeResult.getData(0).getString("ORDER_ID"));
            result.put("TRADE_ID", tradeResult.getData(0).getString("TRADE_ID"));
            resultInfos.add(result);
            return resultInfos;
        }
        catch (Exception e)
        {
            result.put("X_LAST_RESULTINFO", e.getMessage());
            result.put("X_RESULTINFO", e.getMessage());
            result.put("X_RESULTCODE", "99");

            resultInfos.add(result);
            return resultInfos;
        }
    }

    /*
     * @description 多媒体桌面电话成员级个人黑白名单管理
     * @author liuzz
     * @date 2014/05/30
     */
    public static IDataset OperMebBWList(IData data) throws Exception
    {
        // 返回结果
        IDataset resultInfos = new DatasetList();
        IData result = new DataMap();
        result.put("X_RESULTINFO", "OK");
        result.put("X_RESULTCODE", "0");
        try
        {
            IDataset tradeResult = IMSBlackWhiteDeal.OperMebBWList(data);
            result.put("ORDER_ID", tradeResult.getData(0).getString("ORDER_ID"));
            result.put("TRADE_ID", tradeResult.getData(0).getString("TRADE_ID"));
            resultInfos.add(result);
            return resultInfos;
        }
        catch (Exception e)
        {
            result.put("X_LAST_RESULTINFO", e.getMessage());
            result.put("X_RESULTINFO", e.getMessage());
            result.put("X_RESULTCODE", "99");

            resultInfos.add(result);
            return resultInfos;
        }
    }

    /*
     * @description IMS用户密码重置
     * @author liuzz
     * @date 2014/05/30
     */
    public static IDataset ModifyImsMebPassword(IData data) throws Exception
    {

        // 返回结果
        IDataset resultInfos = new DatasetList();
        IData result = new DataMap();

        try
        {
            String userIdB = data.getString("USER_ID_B", "");
            if ("".equals(userIdB))
            {
                CSAppException.apperr(ParamException.CRM_PARAM_66, "USER_ID_B");
            }
            String newPassWord = data.getString("NEW_PASSWORD", "");
            if ("".equals(newPassWord))
            {
                CSAppException.apperr(ParamException.CRM_PARAM_66, "NEW_PASSWORD");
            }

            IData memUserInfo = UcaInfoQry.qryUserInfoByUserId(userIdB);

            if (IDataUtil.isEmpty(memUserInfo))
            {
                CSAppException.apperr(GrpException.CRM_GRP_82);
            }

            IDataset grpRelat = RelaUUInfoQry.qryUU("", userIdB, "S1", null, null);
            if (IDataUtil.isEmpty(grpRelat))
            {
                CSAppException.apperr(GrpException.CRM_GRP_614, userIdB);
            }

            IData param = new DataMap();
            param.clear();
            param.put("USER_ID", userIdB);
            IDataset impuInfo = UserImpuInfoQry.queryUserImpuInfo(userIdB);

            IData conParams = new DataMap();
            conParams.put("SERIAL_NUMBER", memUserInfo.getString("SERIAL_NUMBER"));
            conParams.put("IMPUINFO", impuInfo.getData(0));
            conParams.put("USER_ID", grpRelat.getData(0).getString("USER_ID_A")); // 集团用户ID
            conParams.put(Route.USER_EPARCHY_CODE, memUserInfo.getString("EPARCHY_CODE"));
            conParams.put(Route.ROUTE_EPARCHY_CODE, memUserInfo.getString("EPARCHY_CODE"));
            conParams.put("USER_PASSWD2", newPassWord);

            IDataset tradeResult = CSAppCall.call("CS.ChangeIMSpasswdSVC.changeIMSpasswd", conParams);
            result.put("ORDER_ID", tradeResult.getData(0).getString("ORDER_ID"));
            result.put("TRADE_ID", tradeResult.getData(0).getString("TRADE_ID"));
            result.put("X_RESULTINFO", "OK");
            result.put("X_RESULTCODE", "0");
            resultInfos.add(result);
            return resultInfos;
        }
        catch (Throwable e)
        {
            result.put("X_LAST_RESULTINFO", Utility.getBottomException(e).getMessage());
            result.put("X_RESULTINFO", Utility.getBottomException(e).getMessage());
            result.put("X_RESULTCODE", "99");

            resultInfos.add(result);
            return resultInfos;
        }
    }

    /*
     * @description 修改集团群组
     * @author liuzz
     * @date 2014/05/30
     */
    public static IDataset OperGrpTeam(IData data) throws Exception
    {
        // 返回结果
        IDataset resultInfos = new DatasetList();
        IData result = new DataMap();
        result.put("X_RESULTINFO", "OK");
        result.put("X_RESULTCODE", "0");
        try
        {
            IDataset tradeResult = CentrexTeamDeal.OperGrpTeam(data);
            result.put("ORDER_ID", tradeResult.getData(0).getString("ORDER_ID"));
            result.put("TRADE_ID", tradeResult.getData(0).getString("TRADE_ID"));
            resultInfos.add(result);
            return resultInfos;
        }
        catch (Exception e)
        {
            result.put("X_LAST_RESULTINFO", e.getMessage());
            result.put("X_RESULTINFO", e.getMessage());
            result.put("X_RESULTCODE", "99");

            resultInfos.add(result);
            return resultInfos;
        }
    }

    /*
     * @description 修改成员群组
     * @author liuzz
     * @date 2014/05/30
     */
    public static IDataset OperTeamMeb(IData data) throws Exception
    {
        // 返回结果
        IDataset resultInfos = new DatasetList();
        IData result = new DataMap();
        result.put("X_RESULTINFO", "OK");
        result.put("X_RESULTCODE", "0");
        try
        {
            IDataset tradeResult = CentrexTeamDeal.OperTeamMeb(data);
            result.put("ORDER_ID", tradeResult.getData(0).getString("ORDER_ID"));
            result.put("TRADE_ID", tradeResult.getData(0).getString("TRADE_ID"));
            resultInfos.add(result);
            return resultInfos;
        }
        catch (Exception e)
        {
            result.put("X_LAST_RESULTINFO", e.getMessage());
            result.put("X_RESULTINFO", e.getMessage());
            result.put("X_RESULTCODE", "99");

            resultInfos.add(result);
            return resultInfos;
        }
    }

    /*
     * @description 客户经理业务办理登记
     * @author liuzz
     * @date 2014/05/30
     */
    public static IDataset CustMgrTjNum(IData data) throws Exception
    {
        IData result = new DataMap();
        IDataset resultInfos = new DatasetList();
        try
        {

            // 获取客户经理手机号
            String serialNumber = data.getString("SERIAL_NUMBER", ""); // 客户经理号码
            // 获取成员列表
            IDataset regNumbers = null;
            try
            {
                regNumbers = data.getDataset("REG_NUMBER");
            }
            catch (Exception e)
            {
            }
            if (regNumbers == null)
            {
                regNumbers = new DatasetList();
                String regNumber = data.getString("REG_NUMBER", "");
                if (!"".equals(regNumber))
                {
                    regNumbers.add(regNumber);
                }
            }
            // 客户经理手机号为空
            if (serialNumber.trim().length() == 0)
            {
                result.put("X_CHECK_INFO", "1");
                result.put("X_RESULTCODE", "430001");
                result.put("X_RESULTINFO", "客户经理号码为空");
                resultInfos.add(result);
                return resultInfos;
            }
            // 无登记成员号码
            if (regNumbers.size() == 0)
            {
                result.put("X_CHECK_INFO", "1");
                result.put("X_RESULTCODE", "430002");
                result.put("X_RESULTINFO", "登记号码为空");
                resultInfos.add(result);
                return resultInfos;
            }

            // 一、判断是否是客户经理号码
            IData param = new DataMap();
            param.put("CUST_SERIAL_NUMBER", serialNumber);
            IDataset manageInfo = CustManagerTJNumQry.getCustManagerBySerialNumber(param);
            if (IDataUtil.isEmpty(manageInfo))
            {
                result.put("X_CHECK_INFO", "2");
                result.put("X_RESULTCODE", "430003");
                result.put("X_RESULTINFO", "号码不是客户经理的号码");
                resultInfos.add(result);
                return resultInfos;
            }
            String custManagerId = manageInfo.getData(0).getString("CUST_MANAGER_ID", "");
            // 二、验证是否是集团客户经理
            IData staff = UStaffInfoQry.qryStaffInfoByPK(custManagerId);
            if (IDataUtil.isEmpty(staff))
            {
                result.put("X_CHECK_INFO", "3");
                result.put("X_RESULTCODE", "430004");
                result.put("X_RESULTINFO", "号码不是某一集团的客户经理");
                resultInfos.add(result);
                return resultInfos;
            }
            param.clear();
            param.put("STAFF_ID", staff.getString("STAFF_ID"));
            int custManageCount = CustManagerTJNumQry.queryGroupCustManagerByStaffId(param);
            if (custManageCount <= 0)
            {
                result.put("X_CHECK_INFO", "3");
                result.put("X_RESULTCODE", "430004");
                result.put("X_RESULTINFO", "号码不是某一集团的客户经理");
                resultInfos.add(result);
                return resultInfos;
            }

            IData newParam = new DataMap();

            newParam.put("MANGER_STAFF_ID", custManagerId);
            newParam.put("MANAGER_NAME", manageInfo.getData(0).getString("CUST_MANAGER_NAME", ""));
            newParam.put("IN_MODE", "1");
            newParam.put("ACTIVE_ID", " ");
            newParam.put("ACTIVE_NAME", "");
            newParam.put("IN_DATE", SysDateMgr.getSysDate());
            newParam.put("IN_STAFF_ID", custManagerId);
            newParam.put("IN_DEPART_ID", manageInfo.getData(0).getString("DEPART_ID", ""));
            newParam.put("REMARK", "短信接口添加");
            IData map = new DataMap();

            // 三、循环号码处理
            for (int i = 0; i < regNumbers.size(); i++)
            {
                String tjNum = regNumbers.get(i).toString();
                if (tjNum == null || tjNum.trim().length() == 0)
                {
                    continue;
                }

                // 1、验证是否有效号码
                newParam.put("TJNUMBER", tjNum);
                int count = CustManagerTJNumQry.queryUserBySerialNumber(newParam);
                if (count == 0)
                {
                    result = new DataMap();
                    result.put("DEAL_NUMBER", tjNum);
                    result.put("DEAL_CODE", "430005");
                    result.put("DEAL_INFO", "号码不合法");
                    resultInfos.add(result);
                    continue;
                }
                // 2、验证是否集团成员号码
                IDataset groupMember = CustManagerTJNumQry.queryGbmBySerialNumber(newParam);
                if (IDataUtil.isEmpty(groupMember))
                {
                    result = new DataMap();
                    result.put("DEAL_NUMBER", tjNum);
                    result.put("DEAL_CODE", "430006");
                    result.put("DEAL_INFO", "号码不属于集团成员");
                    resultInfos.add(result);
                    continue;
                }
                // 3、排重，同一号码一个月最多只能推荐2次
                count = map.getInt(tjNum, 0);
                if (count == 0)
                {
                    IDataset manageTjNum = CustManagerTJNumQry.countCustManagerTjNum(newParam);
                    if (IDataUtil.isNotEmpty(manageTjNum))
                    {
                        count = manageTjNum.size();
                    }
                }
                if (count >= 2)
                {
                    result = new DataMap();
                    result.put("DEAL_NUMBER", tjNum);
                    result.put("DEAL_CODE", "430007");
                    result.put("DEAL_INFO", "号码本月已经推荐2次");
                    resultInfos.add(result);
                    continue;
                }

                String logId = SeqMgr.getTJNumBizCode();
                newParam.put("LOG_ID", logId);
                boolean isSuccess = CustManagerTJNumQry.createInfosByParam(newParam);
                if (isSuccess)
                {
                    map.put(tjNum, ++count);
                    result = new DataMap();
                    result.put("DEAL_NUMBER", tjNum);
                    result.put("DEAL_CODE", "0");
                    result.put("DEAL_INFO", "成功登记");
                    resultInfos.add(result);
                    continue;
                }
                else
                {
                    result = new DataMap();
                    result.put("DEAL_NUMBER", tjNum);
                    result.put("DEAL_CODE", "430008");
                    result.put("DEAL_INFO", "接口插入数据失败");
                    resultInfos.add(result);
                    continue;
                }
            }
            return resultInfos;
        }
        catch (Exception e)
        {
            result.put("X_LAST_RESULTINFO", e.getMessage());
            result.put("X_RESULTINFO", e.getMessage());
            result.put("X_RESULTCODE", "99");

            resultInfos.add(result);
            return resultInfos;
        }
    }

}
