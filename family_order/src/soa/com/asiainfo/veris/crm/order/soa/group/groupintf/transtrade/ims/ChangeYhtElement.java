
package com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade.ims;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.FuncrightException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class ChangeYhtElement
{
    /**
     * 一号通成员变更
     * 
     * @param pd
     * @param idata
     * @return
     * @throws Throwable
     */
    public IDataset changeYhtMemElement(IData idata, IDataset userInfos, IDataset prodAttr) throws Exception
    {
        String user_id = "";
        if (null != userInfos && userInfos.size() > 0)
        {
            user_id = userInfos.getData(0).getString("USER_ID_A");
        }
        else
        {
            user_id = idata.getString("USER_ID_A");
        }

        IData memUserInfo = UcaInfoQry.qryUserInfoByUserId(user_id);
        if (IDataUtil.isEmpty(memUserInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_137);
        }

        IDataset relat = RelaUUInfoQry.qryUU("", user_id, "S2", null, null);
        if (IDataUtil.isEmpty(relat))
        {
            CSAppException.apperr(GrpException.CRM_GRP_695, user_id);// 【%s】没有订购一号通业务！
        }

        String userIdA = relat.getData(0).getString("USER_ID_A");
        IData grpUser = UcaInfoQry.qryUserInfoByUserId(userIdA);
        if (IDataUtil.isEmpty(grpUser))
        {
            CSAppException.apperr(GrpException.CRM_GRP_76);// 获取集团用户资料无信息!
        }

        // 设置产品参数
        IDataset productParamDataset = setProductParam(userInfos, prodAttr, user_id, userIdA, memUserInfo.getString("EPARCHY_CODE"));

        IData conmmitData = new DataMap();
        conmmitData.put("USER_ID", userIdA);
        conmmitData.put("SERIAL_NUMBER", memUserInfo.getString("SERIAL_NUMBER"));
        conmmitData.put("MEM_ROLE_B", relat.getData(0).getString("ROLE_CODE_B", "1"));
        conmmitData.put("PRODUCT_ID", "8016");
        conmmitData.put("PRODUCT_PARAM_INFO", productParamDataset);
        conmmitData.put("IS_NEED_TRANS", false);
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

    public IDataset setProductParam(IDataset data, IDataset pardata, String userId, String userIdA, String eparchyCode) throws Exception
    {
        // 新数据
        IDataset newZyhtds = new DatasetList();
        IDataset newByhtds = new DatasetList();

        String serial_number_a = "";
        String tag = "1";
        String zpause = "1";
        String bpause = "1";

        IDataset oldproductparam = UserAttrInfoQry.getUserProductAttrByUserIdAndUserIdA(userId, userIdA, "P");
        for (int i = 0; oldproductparam != null && i < oldproductparam.size(); i++)
        {
            IData tmp = oldproductparam.getData(i);
            if ("CNTRX_MEMB_ONE_RTYPE".equals(tmp.getString("ATTR_CODE")))
                tag = tmp.getString("ATTR_VALUE", "1");
            if ("CallingActivated".equals(tmp.getString("ATTR_CODE")))
                zpause = tmp.getString("ATTR_VALUE", "1");
            if ("CalledActivated".equals(tmp.getString("ATTR_CODE")))
                bpause = tmp.getString("ATTR_VALUE", "1");
        }

        // 传入参数
        for (int i = 0; i < pardata.size(); i++)
        {
            IData tmp = pardata.getData(i);
            if ("CNTRX_MEMB_ONE_RTYPE".equals(tmp.getString("ATTR_CODE")))
                tag = tmp.getString("ATTR_VALUE", "1");
            if ("CallingActivated".equals(tmp.getString("ATTR_CODE")))
                zpause = tmp.getString("ATTR_VALUE", "1");
            if ("CalledActivated".equals(tmp.getString("ATTR_CODE")))
                bpause = tmp.getString("ATTR_VALUE", "1");
        }

        for (int i = 0; i < data.size(); i++)
        {
            IData dataUserInfo = data.getData(i);
            String serial_number_b = dataUserInfo.getString("SERIAL_NUMBER_B");
            if (serial_number_b == null || "".equals(serial_number_b))
            {
                continue;
            }

            dataUserInfo.put("SERIAL_NUMBER", serial_number_b);

            if ("".equals(serial_number_a))
            {
                serial_number_a = dataUserInfo.getString("SERIAL_NUMBER_A");
            }
            else if (!serial_number_a.equals(dataUserInfo.getString("SERIAL_NUMBER_A")))
            {
                CSAppException.apperr(ParamException.CRM_PARAM_504);// 传入了不一致的SERIAL_NUMBER_A！
            }
            if ("".equals(userId))
            {
                userId = dataUserInfo.getString("USER_ID_A");
            }
            else if (!userId.equals(dataUserInfo.getString("USER_ID_A")))
            {
                CSAppException.apperr(ParamException.CRM_PARAM_505);// 传入了不一致的USER_ID_A！
            }

            String userid = querySNInfo(serial_number_a, userIdA, dataUserInfo.getString("SERIAL_NUMBER"), eparchyCode);

            if (dataUserInfo.getString("RELATION_TYPE_CODE").equals("S6"))
            {// 把S6转化成主叫副号
                IData data1 = new DataMap();
                data1.put("SERIAL_NUMBER", dataUserInfo.getString("SERIAL_NUMBER"));
                data1.put("MAIN_FLAG_CODE", dataUserInfo.getString("SHOW_MAIN"));
                data1.put("ZUSERID", userid);
                data1.put("tag", dataUserInfo.getString("MODIFY_TAG"));
                newZyhtds.add(data1);
            }
            if (dataUserInfo.getString("RELATION_TYPE_CODE").equals("S7"))
            {// 把S7转化成被叫副号
                IData data2 = new DataMap();
                data2.put("BSERIAL_NUMBER", dataUserInfo.getString("SERIAL_NUMBER"));
                data2.put("Z_TAG_VAL", tag);
                data2.put("BUSERID", userid);
                data2.put("tag", dataUserInfo.getString("MODIFY_TAG"));
                newByhtds.add(data2);
            }
        }

        IData oldpamData = getOldYhtInfo(userIdA);
        IData oldZyhtds = oldpamData.getData("pam_oldzyht");
        IData oldByhtds = oldpamData.getData("pam_oldbyht");
        IDataset zyhtds = compareZyhtDataset(newZyhtds, oldZyhtds);
        IDataset byhtds = compareByhtDatasetN(newByhtds, oldByhtds);

        IData paramData = new DataMap();
        boolean bZyhtExist = false;
        boolean bByhtExist = false;
        for (int a = 0; a < zyhtds.size(); a++)
        {
            if (!"1".equals(zyhtds.getData(a).getString("tag", "")))
            {
                bZyhtExist = true;
                break;
            }
        }
        for (int b = 0; b < byhtds.size(); b++)
        {
            if (!"1".equals(byhtds.getData(b).getString("tag", "")))
            {
                bByhtExist = true;
                break;
            }
        }
        if (!bZyhtExist || !bByhtExist)
        {
            CSAppException.apperr(GrpException.CRM_GRP_697);// 删除所有主被叫号码请选择退订业务！
        }

        paramData.put("zyht", zyhtds); // zyhtds.toString()
        paramData.put("byht", byhtds); // byhtds.toString()
        paramData.put("Z_TAG_VAL", tag); // 同振0；顺振1

        paramData.put("zpause", zpause);
        paramData.put("bpause", bpause);

        IDataset productParamDataset = new DatasetList();
        IData mapData = new DataMap();
        mapData.put("PRODUCT_ID", "8016");
        mapData.put("PRODUCT_PARAM", IDataUtil.iData2iDataset(paramData, "ATTR_CODE", "ATTR_VALUE"));
        productParamDataset.add(mapData);
        return productParamDataset;
    }

    /**
     * 查询号码个人用户信息
     * 
     * @author liuzz
     * @throws Throwable
     */
    public String querySNInfo(String strMebSnA, String userIdA, String strMebSn, String eparchyCode) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", strMebSn);

        IData userInfos = UserInfoQry.getUserInfoBySN(strMebSn);
        if (IDataUtil.isEmpty(userInfos))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_472, strMebSn);// 该服务号码[%s]用户信息不存在！
        }

        String strEparchCode = userInfos.getString("EPARCHY_CODE");

        boolean ctFlag = false;
        // 判断是否可以拨打国内长途 14
        boolean result = RouteInfoQry.isChinaMobile(strMebSnA);
        if (result)
        {
            // 手机号码是否订购拨打长途服务
            IDataset ds = UserSvcInfoQry.qrySvcInfoByUserIdSvcId(userIdA, "14");
            if (IDataUtil.isNotEmpty(ds))
            {
                ctFlag = true;
            }
        }
        else
        {// 为固定电话时只产生了虚拟三户资料，是没有14服务，暂时不判断
            ctFlag = true;
        }

        // 没有长途拨打权限的不能加外地号码作为副号码
        if (!ctFlag)
        {
            if (!eparchyCode.equals(strEparchCode))
            {
                CSAppException.apperr(FuncrightException.CRM_FUNCRIGHT_9, strMebSnA, strMebSn);// 没有长途拨打权限的主号码[%s]，不能加该外地号码[%s]作为副号码！
            }
        }
        String userid = userInfos.getString("USER_ID", "");
        return userid;
    }

    /**
     * 提取成员融合一号通原号码信息
     * 
     * @author luoyong
     * @throws Throwable
     */
    public IData getOldYhtInfo(String userIdA) throws Exception
    {

        IDataset dsUu = RelaUUInfoQry.qryUU(userIdA, "", "S6", null, null);
        // 主叫一号通
        IData s6Map = new DataMap();
        if (dsUu != null && dsUu.size() > 0)
        {
            for (int i = 0; i < dsUu.size(); i++)
            {
                IData oldUuData = dsUu.getData(i);
                oldUuData.put("X_TAG", "0");
                oldUuData.put("MAIN_FLAG_CODE", oldUuData.getString("RSRV_TAG1", ""));
                oldUuData.put("SERIAL_NUMBER", oldUuData.getString("SERIAL_NUMBER_B", ""));
                oldUuData.put("ZUSERID", oldUuData.getString("USER_ID_B", ""));
                oldUuData.put("START_DATE", oldUuData.getString("START_DATE", ""));

                s6Map.put(oldUuData.getString("USER_ID_B", ""), oldUuData);
            }

        }

        // 被叫一号通
        IDataset dsUu2 = RelaUUInfoQry.qryUU(userIdA, "", "S7", null, null);
        IData s7Map = new DataMap();
        if (dsUu2 != null && dsUu2.size() > 0)
        {

            for (int i = 0; i < dsUu2.size(); i++)
            {
                IData oldUuData = dsUu2.getData(i);
                oldUuData.put("X_TAG", "0");
                oldUuData.put("Z_TAG_VAL", oldUuData.getString("RSRV_TAG1", ""));
                oldUuData.put("BSERIAL_NUMBER", oldUuData.getString("SERIAL_NUMBER_B", ""));
                oldUuData.put("BUSERID", oldUuData.getString("USER_ID_B", ""));
                oldUuData.put("START_DATE", oldUuData.getString("START_DATE", ""));

                s7Map.put(oldUuData.getString("USER_ID_B", ""), oldUuData);
            }
        }
        // 绑定变更前数据
        IData oldpamData = new DataMap();
        oldpamData.put("pam_oldzyht", s6Map);// 主叫数据
        oldpamData.put("pam_oldbyht", s7Map);// 被叫数据
        return oldpamData;
    }

    /**
     * 对比主叫一号通新老号码信息
     */
    public IDataset compareZyhtDataset(IDataset newValue, IData oldValue) throws Exception
    {
        if (IDataUtil.isEmpty(newValue))
        {
            return new DatasetList();
        }
        if (IDataUtil.isEmpty(oldValue))
        {
            return newValue;
        }

        int size = oldValue.entrySet().size();
        int count_add = 0;

        IDataset resultSet = new DatasetList();

        for (int i = 0; i < newValue.size(); i++)
        {
            boolean isfound = false;
            IData newValueColumn = (IData) newValue.get(i);

            String zUserId = newValueColumn.getString("ZUSERID");
            if (oldValue.containsKey(zUserId))
            {
                IData oldValueColumn = oldValue.getData(zUserId);
                newValueColumn.put("START_DATE", oldValueColumn.getString("START_DATE"));
                isfound = true;
                break;
            }

            String state = newValueColumn.getString("tag", "");
            if ((state.equals("1") || state.equals("2")) && isfound)
            {
                if (state.equals("1"))
                {
                    size--;
                }
                resultSet.add(newValueColumn);
            }
            else if (state.equals("0") && !isfound)
            {
                resultSet.add(newValueColumn);
                count_add++;
            }

        }

        if (size + count_add > 1)
        {
            CSAppException.apperr(GrpException.CRM_GRP_698);// 不能添加两个及两个以上主叫一号通号码!
        }

        return resultSet;
    }

    /**
     * 对比被叫一号通新老号码信息
     */
    public IDataset compareByhtDatasetN(IDataset newValue, IData oldValue) throws Exception
    {
        if (IDataUtil.isEmpty(newValue))
        {
            return new DatasetList();
        }
        if (IDataUtil.isEmpty(oldValue))
        {
            return newValue;
        }

        IDataset resultSet = new DatasetList();
        for (int i = 0; i < newValue.size(); i++)
        {
            boolean isfound = false;
            IData newValueColumn = (IData) newValue.get(i);
            String bUserId = newValueColumn.getString("BUSERID");
            if (oldValue.containsKey(bUserId))
            {
                IData oldValueColumn = oldValue.getData(bUserId);

                boolean isAddDel = false; // 是否为变更，即同时有新增和删除。该情况isfound不能为true，否则state状态会由Add变成Exist
                if ("0".equals(newValueColumn.getString("tag")) || "2".equals(newValueColumn.getString("tag")))
                {
                    for (int g = 0; g < newValue.size(); g++)
                    {
                        IData newValueColumn2 = (IData) newValue.get(g);
                        if ("1".equals(newValueColumn2.getString("tag")) && newValueColumn.getString("BUSERID").equals(newValueColumn2.getString("BUSERID")))
                        {
                            isAddDel = true;
                        }
                    }
                }

                if (!isAddDel)
                {
                    newValueColumn.put("START_DATE", oldValueColumn.getString("START_DATE"));
                    isfound = true;
                }
            }

            String state = newValueColumn.getString("tag", "");
            if (state.equals("0"))
            {
                if (!isfound)
                {
                    resultSet.add(newValueColumn);

                }
                else
                {
                    newValueColumn.put("tag", "2");
                    resultSet.add(newValueColumn);
                }
            }
            else if (state.equals("1") || state.equals("2"))
            {
                if (isfound)
                {
                    resultSet.add(newValueColumn);
                }
            }
        }

        return resultSet;
    }

}
