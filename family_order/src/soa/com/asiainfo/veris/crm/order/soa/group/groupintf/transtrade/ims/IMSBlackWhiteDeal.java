
package com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade.ims;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackWhiteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class IMSBlackWhiteDeal
{
    /*
     * @description 集团黑白名单变更
     * @author liuzz
     * @date 2014/05/30
     */
    public static IDataset OperGrpBWList(IData data) throws Exception
    {
        String strMebSn = data.getString("SERIAL_NUMBER", "");
        String strGrpUserID = data.getString("USER_ID_A", "");
        String bizCode = data.getString("BIZ_CODE", "");
        String operCode = data.getString("OPER_CODE", "");
        String userTypeCode = data.getString("USER_TYPE_CODE", "");

        IData param = new DataMap();
        param.put("USER_ID", strGrpUserID);
        // 查询集团信息
        UcaData grpUcaData = UcaDataFactory.getNormalUcaByUserIdForGrp(param);

        IDataset bwList = UserBlackWhiteInfoQry.getBlackWhitedataByGSSAndUserType(strMebSn, strGrpUserID, "817", userTypeCode, bizCode, grpUcaData.getUserEparchyCode());
        if ("0".equals(operCode))
        {
            if (IDataUtil.isNotEmpty(bwList))
            {
                CSAppException.apperr(CustException.CRM_CUST_79, strMebSn);// [%s]已经在黑/白名单中，请不要重复新增！
            }
        }
        else if ("1".equals(operCode))
        {
            if (IDataUtil.isEmpty(bwList))
            {
                CSAppException.apperr(CustException.CRM_CUST_68, strMebSn);// ["+serialnumber+"]不在黑/白名单中，不能进行删除操作！
            }
            IData bwInfo = bwList.getData(0);
            if (!bizCode.equals(bwInfo.getString("BIZ_CODE", "")))
            {
                CSAppException.apperr(GrpException.CRM_GRP_734, strMebSn);// ["+serialnumber+"]呼入呼出属性不符，不能进行删除操作！
            }
            data.put("USER_TYPE_CODE", bwInfo.getString("USER_TYPE_CODE", ""));
            data.put("BIZ_CODE", bwInfo.getString("BIZ_CODE", ""));
        }

        IData tempData = new DataMap();
        IDataset bwLists = new DatasetList();
        tempData.put("tag", data.getString("OPER_CODE", ""));
        tempData.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
        tempData.put("USER_TYPE_CODE", data.getString("USER_TYPE_CODE", ""));
        tempData.put("USER_TYPE", "U");
        tempData.put("BIZ_CODE", data.getString("BIZ_CODE", ""));
        bwLists.add(tempData);

        IData conmmitData = new DataMap();
        conmmitData.put("GRP_USER_ID", strGrpUserID);
        conmmitData.put("USER_ID", strGrpUserID);
        conmmitData.put("USER_TYPE_CODE", userTypeCode);
        conmmitData.put("BIZ_CODE", bizCode);
        conmmitData.put("BW_LISTS", bwLists);
        conmmitData.put(Route.USER_EPARCHY_CODE, grpUcaData.getUserEparchyCode()); // 用户归属地州设置为交易地州

        // 调用后台服务,提交数据
        IDataset dataset = new DatasetList();
        try
        {
            dataset = CSAppCall.call("SS.ImsBlackWhiteMemberBeanSVC.crtTrade", conmmitData);
        }
        catch (Throwable e)
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, Utility.getBottomException(e).getMessage());
        }
        return dataset;
    }

    /*
     * @description 多媒体桌面电话成员级个人黑白名单管理
     * @author liuzz
     * @date 2014/05/30
     */
    public static IDataset OperMebBWList(IData data) throws Exception
    {
        IDataset bwList = data.getDataset("BW_LIST_INFO");
        String userId = data.getString("USER_ID", "");
        String operCode = data.getString("OPER_CODE", "");
        if (IDataUtil.isEmpty(bwList))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_66, "BW_LIST_INFO");// 接口参数检查，BW_LIST_INFO不存在，不做操作！
        }
        if ("".equals(userId))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_155);// USER_ID是必填参数
        }
        if ("".equals(operCode))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_182);// 操作代码OPER_CODE不能传空
        }

        IData memUserInfo = UcaInfoQry.qryUserInfoByUserId(userId);
        if (IDataUtil.isEmpty(memUserInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_82);// 获取成员用户资料无信息
        }

        IDataset relat = RelaUUInfoQry.qryUU("", userId, "S1", null, null);
        if (IDataUtil.isEmpty(relat))
        {
            CSAppException.apperr(GrpException.CRM_GRP_135, userId);// 该成员用户[user_id: %s]还没有订购多媒体桌面电话的成员产品！
        }
        String userIdA = relat.getData(0).getString("USER_ID_A");
        IData grpUser = UcaInfoQry.qryUserInfoByUserId(userIdA);
        if (IDataUtil.isEmpty(grpUser))
        {
            CSAppException.apperr(GrpException.CRM_GRP_76);// 获取集团用户资料无信息!
        }

        IDataset elementList = setProductParam(bwList, userId, operCode, memUserInfo.getString("EPARCHY_CODE"));

        IData conmmitData = new DataMap();
        conmmitData.put("USER_ID", userIdA);
        conmmitData.put("SERIAL_NUMBER", memUserInfo.getString("SERIAL_NUMBER"));
        conmmitData.put("MEM_ROLE_B", relat.getData(0).getString("ROLE_CODE_B", "1"));
        conmmitData.put("PRODUCT_ID", "2222");
        conmmitData.put("ELEMENT_INFO", elementList);
        // 调用后台服务,提交数据
        IDataset dataset = new DatasetList();
        try
        {
            dataset = CSAppCall.call("CS.ChangeMemElementSvc.changeMemElement", conmmitData);
        }
        catch (Throwable e)
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, Utility.getBottomException(e).getMessage());
        }
        return dataset;
    }

    public static IDataset setProductParam(IDataset bwList, String userId, String operCode, String eparchyCode) throws Exception
    {
        // 新黑白名单数据
        IDataset whiteData = new DatasetList();
        IDataset blackData = new DatasetList();

        for (int i = 0; i < bwList.size(); i++)
        {
            IData bwInfo = bwList.getData(i);
            String sn = bwInfo.getString("SERIAL_NUMBER", "");
            String bizCode = bwInfo.getString("BIZ_CODE", "");
            String userTypeCode = bwInfo.getString("USER_TYPE_CODE", "");
            String serviceId = "10122814";
            if ("IW".equals(userTypeCode))
            {
                serviceId = "10122815";
            }
            IDataset memberBW = UserBlackWhiteInfoQry.getBlackWhitedataByGSSAndUserType(sn, userId, serviceId, userTypeCode, bizCode, eparchyCode);
            if ("0".equals(operCode))
            {
                if (IDataUtil.isNotEmpty(memberBW))
                {
                    CSAppException.apperr(CustException.CRM_CUST_79, sn);
                }
            }
            else if ("1".equals(operCode))
            {
                if (IDataUtil.isEmpty(memberBW))
                {
                    CSAppException.apperr(CustException.CRM_CUST_68, sn);// ["+serialnumber+"]不在黑/白名单中，不能进行删除操作！
                }
                if (!bizCode.equals(memberBW.getData(0).getString("BIZ_CODE", "")))
                {
                    CSAppException.apperr(GrpException.CRM_GRP_734);// ["+serialnumber+"]呼入呼出属性不符，不能进行删除操作！
                }
            }
            IData temp = new DataMap();
            temp.put("tag", operCode);
            temp.put("SERIAL_NUMBER", sn);
            temp.put("BIZ_CODE", bizCode);
            if ("IW".equals(userTypeCode))
            {
                whiteData.add(temp);
            }
            else if ("IB".equals(userTypeCode))
            {
                blackData.add(temp);
            }
        }
        IDataset elementList = new DatasetList();
        if (IDataUtil.isNotEmpty(blackData))
        {
            IData blackServData = new DataMap();
            blackServData.put("PRODUCT_ID", "222201");
            blackServData.put("ELEMENT_TYPE_CODE", "S");
            blackServData.put("PACKAGE_ID", "10122801");
            blackServData.put("ELEMENT_ID", "10122814");

            IDataset Svcset = UserSvcInfoQry.getSvcUserId(userId, "10122814");
            if (Svcset != null && Svcset.size() > 0)
            {
                blackServData.put("MODIFY_TAG", "2");
                blackServData.put("START_DATE", Svcset.getData(0).getString("START_DATE").substring(0, 10)); // 从user_svc表取值
                blackServData.put("END_DATE", Svcset.getData(0).getString("END_DATE").substring(0, 10));
                blackServData.put("INST_ID", Svcset.getData(0).getString("INST_ID")); // 从user_svc表取值
            }
            else
            {
                blackServData.put("START_DATE", SysDateMgr.getSysDate());
                blackServData.put("END_DATE", SysDateMgr.getTheLastTime());
                blackServData.put("MODIFY_TAG", "0");
                blackServData.put("INST_ID", "");
            }
            IData blData = new DataMap();
            blData.put("BLACK_LIST", blackData);
            blData.put("SERVICE_ID", "10122814");

            IDataset attrData = new DatasetList();
            IData tmpMap = new DataMap();
            tmpMap.put("PARAM_VERIFY_SUCC", "true");
            attrData.add(0, tmpMap);
            attrData.add(1, blData);

            blackServData.put("ATTR_PARAM", attrData);
            elementList.add(blackServData);
        }

        if (IDataUtil.isNotEmpty(whiteData))
        {
            IData whiteServData = new DataMap();
            whiteServData.put("PRODUCT_ID", "222201");
            whiteServData.put("ELEMENT_TYPE_CODE", "S");
            whiteServData.put("PACKAGE_ID", "10122801");
            whiteServData.put("ELEMENT_ID", "10122815");

            IDataset Svcset = UserSvcInfoQry.getSvcUserId(userId, "10122815");
            if (Svcset != null && Svcset.size() > 0)
            {
                whiteServData.put("MODIFY_TAG", "2");
                whiteServData.put("START_DATE", Svcset.getData(0).getString("START_DATE").substring(0, 10)); // 从user_svc表取值
                whiteServData.put("END_DATE", Svcset.getData(0).getString("END_DATE").substring(0, 10));
                whiteServData.put("INST_ID", Svcset.getData(0).getString("INST_ID")); // 从user_svc表取值
            }
            else
            {
                whiteServData.put("START_DATE", SysDateMgr.getSysDate());
                whiteServData.put("END_DATE", SysDateMgr.getTheLastTime());
                whiteServData.put("MODIFY_TAG", "0");
                whiteServData.put("INST_ID", "");
            }
            IData blData = new DataMap();
            blData.put("WHITE_LIST", whiteData);
            blData.put("SERVICE_ID", "10122815");

            IDataset attrData = new DatasetList();
            attrData.add(blData);

            whiteServData.put("ATTR_PARAM", attrData);
            elementList.add(whiteServData);
        }
        return elementList;
    }
}
