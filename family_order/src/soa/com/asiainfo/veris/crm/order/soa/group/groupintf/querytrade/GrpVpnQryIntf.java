
package com.asiainfo.veris.crm.order.soa.group.groupintf.querytrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ElemLimitInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSpecialPayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.group.groupunit.VpnUnit;

public class GrpVpnQryIntf
{
    /*
     * 集团v网批量业务发短信提醒和短信办理V网成员新增优化的需求,查询集团VPMN编码和VPMN名称,查询VPMN编码和VPMN名称
     */
    public static IDataset GetVpnNoBySn(IData data) throws Exception
    {
        String serial_number = IDataUtil.getMandaData(data, "SERIAL_NUMBER");// 成员手机号码
        String serial_number_in = IDataUtil.getMandaData(data, "SERIAL_NUMBER_IN");// 某V网的成员号码
        // 判断当前办理号码是否为VPMN成员
        // 成员手机号码
        UcaData memUcaData = new UcaData();
        IData uca = UCAQryIntf.getMemberUCAAndStateBySerialNumber(serial_number);
        memUcaData = (UcaData) uca.get("UCADATA");

        String memUserId = memUcaData.getUserId();
        String memEparchyCode = memUcaData.getUserEparchyCode();

        // 查询该vpn成员所属集团相关信息
        IDataset idsRelation = RelaUUInfoQry.getGrpRelaUUInfoByUserIdBAndRelaTypeCode(memUserId, "20", memEparchyCode);

        if (IDataUtil.isNotEmpty(idsRelation))
        {
            IDataset ds8000 = getInfosByProductId(idsRelation, "8000");
            if (IDataUtil.isNotEmpty(ds8000))
            {
                CSAppException.apperr(GrpException.CRM_GRP_713, "800001:当前办理号码[" + serial_number + "]已加入VPMN集团!");
            }

        }

        // 某V网的成员号码
        UcaData memInUcaData = new UcaData();
        uca.clear();
        uca = UCAQryIntf.getMemberUCAAndStateBySerialNumber(serial_number_in);

        memInUcaData = (UcaData) uca.get("UCADATA");

        String memInUserId = memInUcaData.getUserId();
        String memInEparchyCode = memInUcaData.getUserEparchyCode();

        // 查询该vpn成员所属集团相关信息
        IDataset idsRes = RelaUUInfoQry.getGrpRelaUUInfoByUserIdBAndRelaTypeCode(memInUserId, "20", memInEparchyCode);

        if (IDataUtil.isEmpty(idsRes))
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "800002:该号码[" + serial_number_in + "]不属于任何VPMN集团成员!");
        }
        IDataset ds8000 = getInfosByProductId(idsRes, "8000");
        String serialNumberA = ds8000.getData(0).getString("SERIAL_NUMBER_A");
        String userIdA = ds8000.getData(0).getString("USER_ID_A");
        // 查询VPN信息
        IDataset uservpns = UserVpnInfoQry.qryUserVpnByUserId(userIdA);
        if (IDataUtil.isEmpty(uservpns))
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "800003:该号码 [" + serial_number_in + "]VPN信息不存在，业务不能继续办理!");
        }
        IData uservpn = uservpns.getData(0);
        String vpnName = uservpn.getString("VPN_NAME", "");

        // 判读用户用户当前的个人套餐与目标VPMN套餐（3/5/8元套餐）不兼容时，业务终止
        IDataset tmpDatas = getElemLimit358ByUserId(memUserId, memEparchyCode);
        if (IDataUtil.isNotEmpty(tmpDatas))
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "800004:当前办理号码 [" + serial_number + "]个人套餐与目标VPMN套餐（3/5/8元套餐）不兼容，业务不能继续办理!");
        }

        // 返回
        IDataset ids = new DatasetList();

        IData dt = new DataMap();
        dt.put("SERIAL_NUMBER_A", serialNumberA);
        dt.put("VPN_NAME", vpnName);
        ids.add(dt);

        return ids;
    }

    /*
     * 获取集团V网名称和集团订购优惠编码
     */
    public static IDataset GetVpnNameGrpPackageBySn(IData data) throws Exception
    {
        String serial_number = IDataUtil.getMandaData(data, "SERIAL_NUMBER");// 成员手机号码
        String serial_number_in = IDataUtil.getMandaData(data, "SERIAL_NUMBER_IN");// 某V网的成员号码

        // 成员手机号码
        UcaData memUcaData = new UcaData();
        IData uca = UCAQryIntf.getMemberUCAAndStateBySerialNumber(serial_number);

        memUcaData = (UcaData) uca.get("UCADATA");

        String memUserId = memUcaData.getUserId();
        String memEparchyCode = memUcaData.getUserEparchyCode();

        // 查询该vpn成员所属集团相关信息
        IDataset idsRelation = RelaUUInfoQry.getGrpRelaUUInfoByUserIdBAndRelaTypeCode(memUserId, "20", memEparchyCode);

        if (IDataUtil.isNotEmpty(idsRelation))
        {
            IDataset ds8000 = getInfosByProductId(idsRelation, "8000");
            if (IDataUtil.isNotEmpty(ds8000))
            {
                CSAppException.apperr(GrpException.CRM_GRP_713, "800011:当前办理号码[" + serial_number + "]已加入VPMN集团!");
            }

        }

        // 某V网的成员号码
        UcaData memInUcaData = new UcaData();
        uca.clear();
        uca = UCAQryIntf.getMemberUCAAndStateBySerialNumber(serial_number_in);

        memInUcaData = (UcaData) uca.get("UCADATA");

        String memInUserId = memInUcaData.getUserId();
        String memInEparchyCode = memInUcaData.getUserEparchyCode();

        // 查询该vpn成员所属集团相关信息
        IDataset idsRes = RelaUUInfoQry.getGrpRelaUUInfoByUserIdBAndRelaTypeCode(memInUserId, "20", memInEparchyCode);

        if (IDataUtil.isEmpty(idsRes))
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "800012:该号码[" + serial_number_in + "]不属于任何VPMN集团成员!");
        }
        IDataset ds8000 = getInfosByProductId(idsRes, "8000");
        String serialNumberA = ds8000.getData(0).getString("SERIAL_NUMBER_A");
        String userIdA = ds8000.getData(0).getString("USER_ID_A");

        // 查询VPN信息
        IDataset uservpns = UserVpnInfoQry.qryUserVpnByUserId(userIdA);
        if (IDataUtil.isEmpty(uservpns))
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "800013:该号码 [" + serial_number_in + "]VPN信息不存在，业务不能继续办理!");
        }
        IData uservpn = uservpns.getData(0);
        String vpnName = uservpn.getString("VPN_NAME", "");
        int maxUsers = uservpn.getInt("MAX_USERS");

        // add by lixiuyu@20110907 判断集团用户数已达到最大用户数
        IData info = new DataMap();
        info.put("USER_ID_A", userIdA);
        info.put("RELATION_TYPE_CODE", "20");
        // modify by lixiuyu@20120208
        IDataset vpnRela = RelaUUInfoQry.getMebRelaCoutByUserIdA(userIdA, "20");
        int vpnAllNum = ((IData) vpnRela.get(0)).getInt("RECORDCOUNT");
        if (vpnAllNum + 1 > maxUsers)
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "800015:此集团[" + serialNumberA + "]用户数已达到最大用户数，业务不能继续办理!");
        }

        // 判断用户当月已办理两次VPMN成员新增
        String startDate = SysDateMgr.getFirstDayOfThisMonth();
        String endDate = SysDateMgr.getLastDateThisMonth();
        IDataset tradeDataset = TradeHistoryInfoQry.getInfosByUserIdTradeTypeCode("3034", memUserId, startDate, endDate, memEparchyCode);
        if (IDataUtil.isNotEmpty(tradeDataset) && tradeDataset.size() > 1)
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "800017:您本月办理VPMN成员新增业务已达2次，请于下月办理!");
        }

        // 获取集团订购的成员套餐编码和名称
        IDataset grpDataset = UserGrpPkgInfoQry.qryDiscntInfosByUserId(userIdA);
        if (IDataUtil.isEmpty(grpDataset))
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "800016:此集团[" + serialNumberA + "]未订购成员产品优惠包，业务不能继续办理!");
        }

        // 判读用户用户当前的个人套餐与目标VPMN套餐（3/5/8元套餐）兼容性
        boolean flag358 = false;
        IDataset tmpDatas = getElemLimit358ByUserId(memUserId, memEparchyCode);
        if (IDataUtil.isNotEmpty(tmpDatas))
        {
            flag358 = true;
        }

        // 判读用户用户当前的个人套餐与集团超值8元套餐（JDB）兼容性
        boolean flagJDB = false;
        IDataset dataset = getElemLimitByUserIdAndDiscntCode(memUserId, "686", memEparchyCode);
        if (IDataUtil.isNotEmpty(dataset))
        {
            flagJDB = true;
        }

        if (flag358 && !flagJDB)
        {
            IData data358 = idsRes.getData(0);
            data358.put("X_RESULTCODE", "800014");
            data358.put("X_RESULTINFO", "当前办理号码 [" + serial_number + "]个人套餐与目标VPMN套餐（3/5/8元套餐）不兼容，业务不能继续办理!");
            data358.put("VPN_NAME", vpnName);
            data358.put("DISCNT_LIST", grpDataset);

            return idsRes;
        }

        if (!flag358 && flagJDB)
        {
            IData dataJDB = idsRes.getData(0);
            dataJDB.put("X_RESULTCODE", "800018");
            dataJDB.put("X_RESULTINFO", "当前办理号码 [" + serial_number + "]个人套餐与集团超值8元套餐(VPMN JDB)不兼容，业务不能继续办理!");
            dataJDB.put("VPN_NAME", vpnName);
            dataJDB.put("DISCNT_LIST", grpDataset);

            return idsRes;
        }

        if (flag358 && flagJDB)
        {
            IData tmpdata = idsRes.getData(0);
            tmpdata.put("X_RESULTCODE", "800019");
            tmpdata.put("X_RESULTINFO", "当前办理号码 [" + serial_number + "]个人套餐与目标VPMN套餐（3/5/8元套餐）和集团超值8元套餐(VPMN JDB)都不兼容，业务不能继续办理!");
            tmpdata.put("VPN_NAME", vpnName);
            tmpdata.put("DISCNT_LIST", grpDataset);

            return idsRes;
        }

        // 返回
        IDataset ids = new DatasetList();

        IData dt = new DataMap();
        dt.put("SERIAL_NUMBER_A", serialNumberA);
        dt.put("VPN_NAME", vpnName);
        dt.put("DISCNT_LIST", grpDataset);
        ids.add(dt);

        return ids;
    }

    /*
     * 查询判断用户是否VPMN用户并且返回本月办理VPMN成员新增业务的次数
     */
    public static IDataset getCreateVpnCountMonth(IData data) throws Exception
    {
        IDataset ids = new DatasetList();
        String memSn = IDataUtil.getMandaData(data, "SERIAL_NUMBER");// 成员手机号码
        UcaData memUcaData = new UcaData();
        IData uca = UCAQryIntf.getMemberUCAAndStateBySerialNumber(memSn);
        memUcaData = (UcaData) uca.get("UCADATA");

        String memUserId = memUcaData.getUserId();
        String memEparchyCode = memUcaData.getUserEparchyCode();

        // 查询该vpn成员所属集团相关信息
        IDataset idsRes = RelaUUInfoQry.getGrpRelaUUInfoByUserIdBAndRelaTypeCode(memUserId, "20", memEparchyCode);

        if (IDataUtil.isEmpty(idsRes))
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "10:该号码[" + memSn + "]不属于任何VPMN集团成员!");
        }

        // 历史台账表
        String startDate = SysDateMgr.getFirstDayOfThisMonth();
        String endDate = SysDateMgr.getLastDateThisMonth();
        IDataset results = TradeHistoryInfoQry.getInfosByUserIdTradeTypeCode("3034", memUserId, startDate, endDate, memEparchyCode);
        // 返回
        IData dt = new DataMap();
        if (IDataUtil.isNotEmpty(results) && results.size() > 1)
        {
            dt.put("X_TAG", "2");
        }
        else
        {
            dt.put("X_TAG", "1");
        }
        ids.add(dt);

        return ids;
    }

    /**
     * 从一个集团用户信息集里查出指定productId的集合
     * 
     * @param dataset
     * @param productId
     * @return
     * @throws Exception
     */

    public static IDataset getInfosByProductId(IDataset source, String productId) throws Exception
    {
        IDataset ds = new DatasetList();
        for (int i = 0, cout = source.size(); i < cout; i++)
        {
            IData map = source.getData(i);
            String grpUserId = map.getString("USER_ID_A");
            IData grpData = new DataMap();
            grpData.put("USER_ID", grpUserId);
            UcaData grpUcaData = chkGroupUCAByUserId(grpData);
            if (productId.equals(grpUcaData.getProductId()))
            {
                ds.add(map);
            }
        }
        return ds;
    }

    /*
     * 集团VPMN产品编码和成员优惠编码
     */
    public static IDataset GetDiscntCodeBySn(IData data) throws Exception
    {
        // 1.成员手机号码
        String memSn = IDataUtil.getMandaData(data, "SERIAL_NUMBER");
        UcaData memUcaData = new UcaData();
        IData uca = UCAQryIntf.getMemberUCAAndStateBySerialNumber(memSn);
        memUcaData = (UcaData) uca.get("UCADATA");

        String memUserId = memUcaData.getUserId();
        String memEparchyCode = memUcaData.getUserEparchyCode();
        // 判读当前办理号码是否为VPMN成员
        IDataset idsRelation = RelaUUInfoQry.getGrpRelaUUInfoByUserIdBAndRelaTypeCode(memUserId, "20", memEparchyCode);
        if (IDataUtil.isNotEmpty(idsRelation))
        {
            String grpUserId = idsRelation.getData(0).getString("USER_ID_A");
            IData tmp = new DataMap();
            tmp.put("USER_ID", grpUserId);
            UcaData grpUcaData = chkGroupUCAByUserId(tmp);
            if ("8000".equals(grpUcaData.getProductId()))
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_208, memSn);
            }
        }

        // 2. 某V网的成员号码
        String snIn = IDataUtil.getMandaData(data, "SERIAL_NUMBER_IN");
        UcaData snInUcaData = new UcaData();
        uca.clear();
        uca = UCAQryIntf.getMemberUCAAndStateBySerialNumber(snIn);
        snInUcaData = (UcaData) uca.get("UCADATA");

        String snInUserId = snInUcaData.getUserId();
        String snInEparchyCode = snInUcaData.getUserEparchyCode();
        // 查询该vpn成员所属集团相关信息
        IDataset idsRes = RelaUUInfoQry.getGrpRelaUUInfoByUserIdBAndRelaTypeCode(snInUserId, "20", snInEparchyCode);

        if (IDataUtil.isEmpty(idsRes))
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "1:该号码[" + snIn + "]不属于任何VPMN集团成员!");

        }
        IDataset ds8000 = new DatasetList();
        for (int i = 0, cout = idsRes.size(); i < cout; i++)
        {
            IData map = idsRes.getData(i);
            String grpUserId = map.getString("USER_ID_A");
            IData grpData = new DataMap();
            grpData.put("USER_ID", grpUserId);
            UcaData grpUcaData = chkGroupUCAByUserId(grpData);
            if ("8000".equals(grpUcaData.getProductId()))
            {
                ds8000.add(map);
            }
        }
        if (IDataUtil.isEmpty(ds8000))
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "1:该号码[" + snIn + "]不属于任何VPMN集团成员!");
        }
        String serialNumberA = ds8000.getData(0).getString("SERIAL_NUMBER_A");
        String userIdA = ds8000.getData(0).getString("USER_ID_A");
        // 查询VPN信息
        IDataset vpnInfos = UserVpnInfoQry.qryUserVpnByUserId(userIdA);
        if (IDataUtil.isEmpty(vpnInfos))
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "1:该号码 [" + snIn + "]VPN信息不存在，业务不能继续办理");
        }
        String vpnName = vpnInfos.getData(0).getString("VPN_NAME", "");

        // 查询当月订购3/5/8元套餐,默认订购当月删除的套餐
        IDataset usrDiscnts = UserDiscntInfoQry.qryUserDiscntbyThisMonth(memUserId, "20", "800001", "80000102", null, memEparchyCode);
        IData discnt = new DataMap();
        String discntCode = "";
        if (IDataUtil.isNotEmpty(usrDiscnts))
        {
            discnt = (IData) usrDiscnts.get(0);
            discntCode = discnt.getString("DISCNT_CODE", "");
        }
        // 当月未订购3/5/8元套餐，默认为用户办理集团3元套餐（JDD）
        if (IDataUtil.isEmpty(discnt))
        {
            discntCode = "1285";
        }

        // 判断用户当前的个人套餐与目标VPMN套餐（3/5/8元套餐）不兼容时，默认为用户办理叠加集团超值8元套餐（JDB）
        IDataset tmpDatas = getElemLimit358ByUserId(memUserId, memEparchyCode);
        if (IDataUtil.isNotEmpty(tmpDatas))
        {
            discntCode = "686";
        }

        // 查询套餐名称
        String discntName = "";
        IData discntData = UDiscntInfoQry.getDiscntInfoByPk(discntCode);
        if (IDataUtil.isNotEmpty(discntData))
        {
            discntName = discntData.getString("DISCNT_NAME", "");
        }

        // 返回
        IDataset ids = new DatasetList();

        IData dt = new DataMap();
        dt.put("SERIAL_NUMBER_A", serialNumberA);
        dt.put("VPN_NAME", vpnName);
        dt.put("DISCNT_CODE", discntCode);
        dt.put("DISCNT_NAME", discntName);
        ids.add(dt);

        return ids;
    }

    /**
     * 查询集团vpn 统一付费
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getQryUsrSpePay(IData data) throws Exception
    {
        IDataset ids = new DatasetList();
        // 查询 方式
        String qryPayRelaTyp = IDataUtil.getMandaData(data, "PARA_CODE");

        // 成员用户手机号码
        if ("0".equals(qryPayRelaTyp))
        {
            String memSn = IDataUtil.getMandaData(data, "PARA_CODE2");
            UcaData memUcaData = new UcaData();
            IData uca = UCAQryIntf.getMemberUCAAndStateBySerialNumber(memSn);
            memUcaData = (UcaData) uca.get("UCADATA");

            String memUserId = memUcaData.getUserId();
            String memEparchyCode = memUcaData.getUserEparchyCode();
            ids = UserSpecialPayInfoQry.getUserSpecPayByUserId(memUserId, memEparchyCode);
            if (IDataUtil.isNotEmpty(ids))
            {
                IDataset ids2 = new DatasetList();
                for (int i = 0, cout = ids.size(); i < cout; i++)
                {
                    IData map = ids.getData(i);
                    String grpUserId = map.getString("USER_ID_A");
                    IData grpData = new DataMap();
                    grpData.put("USER_ID", grpUserId);

                    String custName = "";
                    try
                    {
                        UcaData grpUcaData = chkGroupUCAByUserId(grpData);
                        custName = grpUcaData.getCustomer().getCustName();
                    }
                    catch (Exception e)
                    {
                        continue; // 报错数据跳过
                    }

                    map.put("PARA_CODE14", custName); // 所属集团客户名称
                    map.put("PARA_CODE12", "");
                    map.put("PARA_CODE13", "");
                    IDataset vpnInfos = UserVpnInfoQry.qryUserVpnByUserId(grpUserId);
                    if (IDataUtil.isNotEmpty(vpnInfos))
                    {
                        IData vpnInfo = vpnInfos.getData(0);
                        map.put("PARA_CODE12", vpnInfo.getString("VPN_NO", ""));
                        map.put("PARA_CODE13", vpnInfo.getString("VPN_NAME", ""));
                    }
                    map.put("PARA_CODE1", memSn);
                    map.remove("USER_ID_A");
                    // IDataset uuInfos = RelaUUInfoQry.qryRelaUUInfoByUserIdAB(memUserId, grpUserId, memEparchyCode);
                    // if (IDataUtil.isNotEmpty(uuInfos)) // 存在有效的uu关系
                    // {
                    ids2.add(map);
                    // }
                }
                return ids2;
            }
        }
        else if ("1".equals(qryPayRelaTyp)) // vpmn编号和用户短号码
        {
            // 根据 vpnno 和 shortcode 查出 serialnumber
            String vpnNo = IDataUtil.getMandaData(data, "PARA_CODE1");
            String shorCode = IDataUtil.getMandaData(data, "PARA_CODE2");

            IDataset grpVpnInfo = UserVpnInfoQry.queryVpnInfoByVpnNo(vpnNo);
            if (IDataUtil.isEmpty(grpVpnInfo))
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_206, vpnNo);
            }
            String grpUserId = grpVpnInfo.getData(0).getString("USER_ID");

            IDataset memVpnInfos = UserVpnInfoQry.qryVpnMemByuserIdaAndShortCode(grpUserId, shorCode);
            if (IDataUtil.isEmpty(memVpnInfos))
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_207, grpUserId, shorCode);
            }
            String memSn = memVpnInfos.getData(0).getString("SERIAL_NUMBER"); // 成员手机号

            UcaData memUcaData = new UcaData();
            IData uca = UCAQryIntf.getMemberUCAAndStateBySerialNumber(memSn);
            memUcaData = (UcaData) uca.get("UCADATA");

            String memUserId = memUcaData.getUserId();
            String memEparchyCode = memUcaData.getUserEparchyCode();
            ids = UserSpecialPayInfoQry.getUserSpecPayByUserIdAB(memUserId, grpUserId, memEparchyCode);
            for (int i = 0, cout = ids.size(); i < cout; i++)
            {
                IData map = ids.getData(i);
                map.put("PARA_CODE14", ""); // 所属集团客户名称,ng接口传“”

                map.put("PARA_CODE12", "");
                map.put("PARA_CODE13", "");
                IDataset vpnInfos = UserVpnInfoQry.qryUserVpnByUserId(grpUserId);
                if (IDataUtil.isNotEmpty(vpnInfos))
                {
                    IData vpnInfo = vpnInfos.getData(0);
                    map.put("PARA_CODE12", vpnInfo.getString("VPN_NO", ""));
                    map.put("PARA_CODE13", vpnInfo.getString("VPN_NAME", ""));
                }
                map.put("PARA_CODE1", memSn);
            }

        }
        else
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_218, "PARA_CODE", qryPayRelaTyp);

        }

        return ids;
    }

    /*
     * 短号校验
     */
    public static IDataset getValidShortCode(IData data) throws Exception
    {
        IDataset ids = new DatasetList();
        IData dt = new DataMap();
        String shortCode = IDataUtil.getMandaData(data, "SHORT_CODE");
        String grpSn = IDataUtil.getMandaData(data, "SERIAL_NUMBER_A");
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", grpSn);
        UcaData grpUcaData = chkGroupUCABySerialNumber(inparam);
        String usrIdA = grpUcaData.getUserId();

        // add by lixiuyu@20110223 修改接口的输入参数
        IData inData = new DataMap();
        inData.put("USER_ID_A", usrIdA);
        inData.put("SHORT_CODE", shortCode);
        inData.put("EPARCHY_CODE", data.getString("EPARCHY_CODE"));
        IData resultData = VpnUnit.shortCodeValidateVpn(inData);
        boolean valid = true;
        String reStr = resultData.getString("RESULT", "");
        if ("false".equals(reStr))
        {
            valid = false;
        }
        dt.put("VALID_SHORT_RESULT", valid);
        ids.add(dt);
        return ids;
    }

    /**
     * 自动生成短号
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getAutoGenShortCode(IData data) throws Exception
    {
        String grpSn = IDataUtil.getMandaData(data, "SERIAL_NUMBER_A");
        String mebSn = IDataUtil.getMandaData(data, "SERIAL_NUMBER");

        String mebEparchyCode = Route.getCrmDefaultDb();

        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", grpSn);
        UcaData grpUcaData = chkGroupUCABySerialNumber(inparam);
        String usrIdA = grpUcaData.getUserId();

        // add by lixiuyu@20110223 修改接口的输入参数
        IData inData = new DataMap();
        inData.put("SERIAL_NUMBER", mebSn);
        inData.put("USER_ID_A", usrIdA);
        inData.put("EPARCHY_CODE", mebEparchyCode);

        String shortCode = VpnUnit.createShortCode(inData);

        IData retData = new DataMap();
        retData.put("SHORT_CODE", shortCode);

        return IDataUtil.idToIds(retData);
    }

    /*
     * vpn信息查询 外围接口
     */
    public static IDataset getUserVpnInfo(IData data) throws Exception
    {
        // X_GETMODE:0 取默认vpn信息; =1 取成员个性vpn信息
        String getMode = IDataUtil.getMandaData(data, "X_GETMODE");
        String serNumA = IDataUtil.getMandaData(data, "SERIAL_NUMBER_A");

        IDataset ids = new DatasetList();
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serNumA);
        UcaData grpUcaData = chkGroupUCABySerialNumber(inparam);
        String usrIdA = grpUcaData.getUserId();
        if ("1".equals(getMode))
        {
            String serNumB = IDataUtil.getMandaData(data, "SERIAL_NUMBER");
            UcaData memUcaData = new UcaData();
            IData uca = UCAQryIntf.getMemberUCAAndStateBySerialNumber(serNumB);
            memUcaData = (UcaData) uca.get("UCADATA");
            String usrIdB = memUcaData.getUserId();
            String memEparchCode = memUcaData.getUserEparchyCode();

            IData vpnInfo = UserVpnInfoQry.getMemberVpnByUserId(usrIdB, usrIdA, memEparchCode);
            ids.add(vpnInfo);
        }

        if ("0".equals(getMode))
        {
            ids = UserVpnInfoQry.qryUserVpnByUserId(usrIdA);

        }
        if (IDataUtil.isNotEmpty(ids))
        {
            int recodeNum = ids.size();
            for (int i = 0; i < recodeNum; i++)
            {
                IData info = ids.getData(0);
                info.put("X_RECORDNUM", recodeNum);
                info.put("MAX_POUT_NUM", info.getString("MAX_POUT_NUM", ""));
                info.put("OUTGRP", info.getString("OUTGRP", ""));
                info.put("OUTNETGRP_USE_TYPE", info.getString("OUTNETGRP_USE_TYPE", ""));
                info.put("OVER_RIGHT_TAG", info.getString("OVER_RIGHT_TAG", ""));
                info.put("SELL_DEPART", info.getString("SELL_DEPART", ""));
                info.put("NOT_BATCHFEE_TAG", info.getString("NOT_BATCHFEE_TAG", ""));
                info.put("PERFEE_PLAY_BACK", info.getString("PERFEE_PLAY_BACK", ""));
                info.put("CALL_DISP_MODE", info.getString("CALL_DISP_MODE", ""));
                info.put("MON_FEE_LIMIT", info.getString("MON_FEE_LIMIT", ""));
                info.put("OUTNET_CALL_PWD", info.getString("OUTNET_CALL_PWD", ""));
                info.put("INNET_CALL_PWD", info.getString("INNET_CALL_PWD", ""));
                info.put("SHORT_CODE", info.getString("SHORT_CODE", ""));
                info.put("USER_PIN", info.getString("USER_PIN", ""));
                info.put("SERIAL_NUMBER", info.getString("SERIAL_NUMBER", ""));
                info.put("ADMIN_FLAG", info.getString("ADMIN_FLAG", ""));
                info.put("MAIN_TAG", info.getString("MAIN_TAG", ""));
                info.put("TELPHONIST_TAG", info.getString("TELPHONIST_TAG", ""));
                info.put("LINK_MAN_TAG", info.getString("LINK_MAN_TAG", ""));
                info.put("RSRV_STR6", "");
                info.put("RSRV_STR7", "");
                info.put("RSRV_STR8", "");
                info.put("RSRV_STR9", "");
                info.put("RSRV_STR10", "");
                info.put("RSRV_STR11", "");
                info.put("RSRV_STR12", "");
                info.put("RSRV_STR13", "");
                info.put("RSRV_STR14", "");
                info.put("RSRV_STR15", "");
                info.put("RSRV_STR16", "");
                info.put("RSRV_STR17", "");
                info.put("RSRV_STR18", "");
                info.put("RSRV_STR19", "");
                info.put("RSRV_STR20", "");
            }
        }

        return ids;
    }

    /*
     * vpmn 信息查询 短信营业厅
     */
    public static IDataset getUserVpnInfo2ShortMes(IData data) throws Exception
    {
        String snB = IDataUtil.getMandaData(data, "SERIAL_NUMBER_B");
        IData param = new DataMap();
        UcaData memUcaData = new UcaData();
        IData uca = UCAQryIntf.getMemberUCAAndStateBySerialNumber(snB);
        memUcaData = (UcaData) uca.get("UCADATA");
        // 查询该vpn成员所属集团相关信息
        String memUserId = memUcaData.getUserId();
        String memEparchyCode = memUcaData.getUserEparchyCode();
        IDataset idsRes = RelaUUInfoQry.check_byuserida_idbzm(memUserId, "20", null, memEparchyCode);

        if (IDataUtil.isEmpty(idsRes))
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "1:当前查询的号码 " + snB + "不是vpmn用户!");
        }
        String usrIdA = idsRes.getData(0).getString("USER_ID_A");
        param.clear();
        param.put("USER_ID", usrIdA);
        UcaData grpUcaData = chkGroupUCAByUserId(param);
        String grpProductId = grpUcaData.getProductId();
        if (!"8000".equals(grpProductId))
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "1:当前查询的号码 " + snB + "不是vpmn用户!");
        }
        // 本月开通次数
        int openCount = RelaUUInfoQry.getRelaCountThisMonthByUserIdbAndRelaTypeCode(memUserId, "20", memEparchyCode);

        // 查询 vpn集团信息
        IDataset vpnInfos = UserVpnInfoQry.qryUserVpnByUserId(usrIdA);
        if (IDataUtil.isEmpty(vpnInfos))
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "1:当前查询的号码 " + snB + "不是vpmn用户!");
        }
        String vpnNo = vpnInfos.getData(0).getString("VPN_NO");
        String vpnName = vpnInfos.getData(0).getString("VPN_NAME");

        // 返回
        IDataset ids = new DatasetList();

        param.clear();
        param.put("RSRV_STR1", openCount);
        param.put("VPMN", vpnNo);
        param.put("VPMN_NAME", vpnName);
        ids.add(param);

        return ids;
    }

    public static UcaData chkGroupUCABySerialNumber(IData inparam) throws Exception
    {
        String serNumA = IDataUtil.chkParam(inparam, "SERIAL_NUMBER");
        UcaData grpUcaData = UcaDataFactory.getNormalUcaBySnForGrp(inparam);
        String state = grpUcaData.getUser().getUserStateCodeset();
        if (!"0".equals(state) && !"N".equals(state) && !"00".equals(state))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_471, serNumA);
        }
        return grpUcaData;
    }

    public static UcaData chkGroupUCAByUserId(IData inparam) throws Exception
    {
        IDataUtil.chkParam(inparam, "USER_ID");
        UcaData grpUcaData = UcaDataFactory.getNormalUcaByUserIdForGrp(inparam);
        return grpUcaData;
    }

    /**
     * 判断当前的个人套餐与目标VPMN套餐（3/5/8元套餐）互斥性
     * 
     * @param userId
     * @param routeId
     * @return
     * @throws Exception
     */

    public static IDataset getElemLimit358ByUserId(String userId, String routeId) throws Exception
    {
        IDataset limitResult = new DatasetList();
        IDataset limitInfos = ElemLimitInfoQry.getElementLimitByElementId358(); // 所有358优惠的限制配置
        if (IDataUtil.isEmpty(limitInfos))
        {
            return null;
        }
        for (int i = 0; i < limitInfos.size(); i++)
        {
            IData data = limitInfos.getData(i);
            String disCode = data.getString("ELEMENT_ID_A");
            IDataset orderDis = UserDiscntInfoQry.getAllDiscntByUserIdAndRouteId(userId, disCode, routeId);
            // 有互斥资费已订购的情况
            if (IDataUtil.isNotEmpty(orderDis))
            {
                limitResult.add(data);
            }
        }

        return limitResult;
    }

    /**
     * 查当前的个人套餐与目标套餐 互斥性
     * 
     * @param userId
     * @param discntCode
     * @param routeId
     * @return
     * @throws Exception
     */

    public static IDataset getElemLimitByUserIdAndDiscntCode(String userId, String discntCode, String routeId) throws Exception
    {
        IDataset limitResult = new DatasetList();
        IDataset limitInfos = ElemLimitInfoQry.getElementLimitByElementId(discntCode); // 指定优惠的限制配置
        if (IDataUtil.isEmpty(limitInfos))
        {
            return null;
        }
        for (int i = 0; i < limitInfos.size(); i++)
        {
            IData data = limitInfos.getData(i);
            String disCode = data.getString("ELEMENT_ID_A");
            IDataset orderDis = UserDiscntInfoQry.getAllDiscntByUserIdAndRouteId(userId, disCode, routeId);
            // 有互斥资费已订购的情况
            if (IDataUtil.isNotEmpty(orderDis))
            {
                limitResult.add(data);
            }
        }

        return limitResult;
    }

    /**
     * 查询短号信息
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IDataset qryShort(IData inParam) throws Exception
    {
        // 参数校验
        String serialNumber = IDataUtil.chkParam(inParam, "SERIAL_NUMBER");
        String sn = IDataUtil.chkParam(inParam, "SN");

        // 判断主叫号码是否VPN成员
        IDataset relaList20 = RelaUUInfoQry.qryRelaBySerialNumberBAndRelationTypeCode("20", serialNumber, null);

        if (IDataUtil.isEmpty(relaList20))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_222);// 2:主叫号码非VPN集团成员用户!
        }

        // 关系表中USER_ID_A
        String userIdA = "";

        // 判断是否为VPN用户
        for (int i = 0, row = relaList20.size(); i < row; i++)
        {
            String relaUserIdA = relaList20.getData(i).getString("USER_ID_A");

            IDataset userVpnList = UserVpnInfoQry.qryUserVpnByUserId(relaUserIdA);

            if (IDataUtil.isNotEmpty(userVpnList))
            {
                userIdA = relaUserIdA;
                break;
            }
        }

        if (StringUtils.isBlank(userIdA))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_222);// 2:主叫号码非VPN集团成员用户!
        }

        // 查询所有的VPN用户
        IDataset relaList40 = RelaUUInfoQry.qryRealAllByUserIdBForGrp(userIdA);

        IDataset retList = new DatasetList();

        if (IDataUtil.isEmpty(relaList40))
        {
            retList = RelaUUInfoQry.qryRelaByUserIdAAndSerialNumberBForGrp(userIdA, sn);
        }
        else
        {
            // 遍历关系
            for (int i = 0, row = relaList40.size(); i < row; i++)
            {
                String relaUserIdB = relaList40.getData(i).getString("USER_ID_B");

                IDataset relaList = RelaUUInfoQry.qryRelaByUserIdAAndSerialNumberBForGrp(relaUserIdB, sn);

                if (IDataUtil.isNotEmpty(relaList))
                {
                    retList = relaList;
                    break;
                }
            }
        }

        if (IDataUtil.isEmpty(retList))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_223);// 3:被查号码,跟主叫号码不在同一个VPN子母集团里面!
        }

        IData relaData = retList.getData(0);

        String serialNumberB = relaData.getString("SERIAL_NUMBER_B");
        String shortCode = relaData.getString("SHORT_CODE");
        String vpnUserIdA = relaData.getString("USER_ID_A");

        // 查询VPN信息
        IDataset userVpnList = UserVpnInfoQry.qryUserVpnByUserId(vpnUserIdA);

        if (IDataUtil.isEmpty(userVpnList))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_6, vpnUserIdA);
        }

        IData vpnData = userVpnList.getData(0);

        IData retData = new DataMap();

        retData.put("X_RESULTCODE", "0");
        retData.put("X_RESULTINFO", "信息返回成功!");
        retData.put("SN_BACK", sn.equals(serialNumberB) ? shortCode : sn);
        retData.put("VPMN", relaData.getString("SERIAL_NUMBER_A"));
        retData.put("VPN_NAME", vpnData.getString("VPN_NAME"));
        retData.put("SERIAL_NUMBER_B", relaData.getString("SERIAL_NUMBER_B"));

        return IDataUtil.idToIds(retData);

    }

}
