
package com.asiainfo.veris.crm.order.soa.group.groupunit;

import java.util.Calendar;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.group.common.query.GroupQueryBean;

public class VpnUnit
{

    /*
     * 合成TF_F_USER_VPN表的CallNetType字段
     */
    public static String comCallNetTypeField(IData paramData)
    {

        String strTag;
        // String strNewTag = "";

        strTag = comFlagField(paramData.getString("CALL_NET_TYPE1", ""));
        // strNewTag = (strTag == null) ? "0" : "1";

        strTag += comFlagField(paramData.getString("CALL_NET_TYPE2", ""));
        // strNewTag += (strTag == null) ? "0" : "1";

        strTag += comFlagField(paramData.getString("CALL_NET_TYPE3", ""));
        // strNewTag += (strTag == null) ? "0" : "1";

        strTag += comFlagField(paramData.getString("CALL_NET_TYPE4", ""));
        // strNewTag += (strTag == null) ? "0" : "1";

        return strTag;
    }

    /*
     * 合成 FeeType字段
     */
    public static String comFeeTypeField(IData paramData)
    {

        String strTag;
        // String strNewTag = "0";

        strTag = comFlagField(paramData.getString("LIMFEE_TYPE_CODE1", ""));
        // strNewTag = (strTag == null) ? "0" : "1";

        strTag += comFlagField(paramData.getString("LIMFEE_TYPE_CODE2", ""));
        // strNewTag += (strTag == null) ? "0" : "1";

        strTag += comFlagField(paramData.getString("LIMFEE_TYPE_CODE3", ""));
        // strNewTag += (strTag == null) ? "0" : "1";

        strTag += comFlagField(paramData.getString("LIMFEE_TYPE_CODE4", ""));
        // strNewTag += (strTag == null) ? "0" : "1";

        return strTag;
    }

    /*
     * 转换 Flag字段
     */
    public static String comFlagField(String strFlag)
    {

        String strNewFlag = "0";
        strNewFlag = (strFlag == "") ? "0" : "1";
        return strNewFlag;

    }

    public static String comFlagField(String strFuncFlag, IDataset vpnTagList) throws Exception
    {
        for (int i = 0, row = vpnTagList.size(); i < row; i++)
        {
            IData vpnTagData = vpnTagList.getData(i);
            String tagCode = vpnTagData.getString("TAG_CODE").trim();
            String tagValue = vpnTagData.getString("TAG_VALUE").trim();
            if (StringUtils.isNotBlank(tagCode) && StringUtils.isNotBlank(tagValue) && tagValue.length() == 1)
            {
                try
                {
                    int codeNumber = Integer.valueOf(tagCode.substring(tagCode.length() - 2));
                    
                    if (strFuncFlag.length() >= codeNumber)
                    {
                        strFuncFlag = StrUtil.replacStrByint(strFuncFlag, tagValue, codeNumber, codeNumber);
                    }
                }
                catch (Exception e)
                {
                    // TODO: handle exception
                }

            }
        }

        return strFuncFlag;
    }

    public static String createParentVpmnNoCrt(IData data) throws Exception
    {
        String workTypeCode = data.getString("WORK_TYPE_CODE");

        StringBuilder buffer = new StringBuilder("20");

        String staffId = CSBizBean.getVisit().getStaffId();

        buffer.append("JT");
        buffer.append("SUPERUSR".equals(staffId) ? "SJ" : staffId.substring(2, 4));
        buffer.append("A".equals(workTypeCode) ? "10" : "0" + workTypeCode);

        String vpnNo = SeqMgr.getVpmnIdIdForGrp();

        buffer.append("0").append(vpnNo.substring(vpnNo.length() - 3, vpnNo.length()));

        return buffer.toString();
    }

    /**
     * 创建母VPMN服务号码
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static String createParentVpnSN(IData data) throws Exception
    {
        String workTypeCode = data.getString("WORK_TYPE_CODE");

        StringBuilder buffer = new StringBuilder(20);

        String staffId = CSBizBean.getVisit().getStaffId();

        buffer.append("JT");
        buffer.append("SUPERUSR".equals(staffId) ? "SJ" : staffId.substring(0, 2));
        buffer.append("A".equals(workTypeCode) ? "10" : "0" + workTypeCode);

        String vpnNo = SeqMgr.getVpmnIdIdForGrp();

        buffer.append("0").append(vpnNo.substring(vpnNo.length() - 3, vpnNo.length()));

        return buffer.toString();
    }

    /**
     * 自动生成短号
     * 
     * @author sht
     * @param pd
     * @param td
     * @param data
     * @return
     * @throws Exception
     */
    public static String createShortCode(IData data) throws Exception
    {
        String shortCode = "";
        String sn = data.getString("SERIAL_NUMBER", "");
        String eparchyCode = data.getString("EPARCHY_CODE");
        IDataset dataset = ParamInfoQry.getCommparaByCode("CGM", "260", "", "ZZZZ");
        if (IDataUtil.isNotEmpty(dataset))
        {
            IData param = new DataMap();
            param.put("sn", sn);
            param.put("v_user_id", data.getString("USER_ID_A", ""));
            param.put("v_sNum", "");
            param.put("v_resultcode", "");
            param.put("v_resulterrinfo", "");
            String[] inParam =
            { "sn", "v_user_id", "v_snum", "v_resultcode", "v_resulterrinfo" };
            Dao.callProc("CREATE_SHORTCODE", inParam, param, eparchyCode);
            // 是否成功
            String resultCode = param.getString("v_resultcode", "");
            if ("0".equals(resultCode))
            {
                shortCode = param.getString("v_snum", "");
            }
            else
            {
                String resultInfo = param.getString("v_resulterrinfo");

                CSAppException.apperr(VpmnUserException.VPMN_USER_23, resultCode, resultInfo);
            }
        }
        else
        {
            boolean enable = false;
            if (sn.length() > 3)
            {
                // 自动分配6+手机号码后三位
                if (!enable)
                {
                    shortCode = "6" + sn.substring(sn.length() - 3);
                    data.put("SHORT_CODE", shortCode);
                    enable = validchk(data, "shortCode");
                }

                // 如果第一步不能满足，调整为6+（1至9，不含第一步自动匹配数字）+手机号码后二位
                if (!enable)
                {
                    for (int i = 1; i <= 9; i++)
                    {
                        shortCode = "6" + String.valueOf(i) + sn.substring(sn.length() - 2);
                        data.put("SHORT_CODE", shortCode);
                        enable = validchk(data, "shortCode");
                        if (enable)
                        {
                            break;
                        }
                    }
                }

                // 如果第二步不能满足，自动分配6+手机号码后四位
                if (!enable)
                {
                    shortCode = "6" + sn.substring(sn.length() - 4);
                    data.put("SHORT_CODE", shortCode);
                    enable = validchk(data, "shortCode");
                }

                // 如果第三步不能满足，调整为6+（1至9，不含第三步自动匹配数字）+手机号码后三位
                if (!enable)
                {
                    for (int i = 1; i <= 9; i++)
                    {
                        shortCode = "6" + String.valueOf(i) + sn.substring(sn.length() - 3);
                        data.put("SHORT_CODE", shortCode);
                        enable = validchk(data, "shortCode");
                        if (enable)
                        {
                            break;
                        }
                    }
                }

                // 如果第四步不能满足，自动分配6开头五位未使用短号最大数
                if (!enable)
                {
                    String code = "";
                    // for (int i = 1; i <= 9999; i++) {
                    for (int i = 9999; i > 0; i--)
                    {

                        if (i < 10)
                        {
                            code = "000" + String.valueOf(i);
                        }
                        else if (i < 100)
                        {
                            code = "00" + String.valueOf(i);
                        }
                        else if (i < 1000)
                        {
                            code = "0" + String.valueOf(i);
                        }
                        else
                        {
                            code = String.valueOf(i);
                        }

                        if (!code.equals(sn.substring(sn.length() - 4)))
                        {
                            shortCode = "6" + code;
                            data.put("SHORT_CODE", shortCode);
                            enable = validchk(data, "shortCode");
                        }

                        if (enable)
                        {
                            break;
                        }
                    }
                }
            }

            if (enable)
            {
            }
            else
            {
                shortCode = "";
            }
        }
        return shortCode;
    }

    /**
     * VPMN短号验证
     * 
     * @param shortCode
     *            短号码
     * @param resultMessage
     *            错误信息
     * @return
     */
    public static IData shortCodeValidateVpn(IData data) throws Exception
    {
        String shortCode = data.getString("SHORT_CODE", "");
        String result = "true";
        if (shortCode.length() < 3 || shortCode.length() > 6)
        {
            data.put("ERROR_MESSAGE", "短号长度只能为3~6，请检查!");
            result = "false";
        }
        if (!shortCode.substring(0, 1).equals("6"))
        {
            data.put("ERROR_MESSAGE", "短号码必须以【6】开头，请检查!");
            result = "false";
        }
        if (shortCode.substring(0, 2).equals("60"))
        {
            data.put("ERROR_MESSAGE", "短号码不能以【60】开头，请检查！");
            result = "false";
        }

        // 短号唯一性校验规则
        IData inData = new DataMap();
        inData.put("USER_ID_A", data.getString("USER_ID_A"));
        inData.put("SHORT_CODE", shortCode);
        String eparchyCode = data.getString("EPARCHY_CODE");
        IDataset dataset = ParamInfoQry.getCommparaByCode("BMS", "259", "", eparchyCode);
        if (IDataUtil.isNotEmpty(dataset))
        {
            IData commData = dataset.getData(0);
            StringBuilder strBuffer = new StringBuilder();
            strBuffer.append(commData.getString("PARA_CODE23"));
            strBuffer.append(commData.getString("PARA_CODE24"));
            strBuffer.append(commData.getString("PARA_CODE25"));

            String str = strBuffer.toString();
            if (0 <= str.indexOf("|" + shortCode + "|"))
            {
                data.put("ERROR_MESSAGE", "短号码不能是特殊代码，短号为【" + shortCode + "】请检查！");
                result = "false";
            }
        }

        IDataset existShortCode = GroupQueryBean.CheckVUserShortCode(inData); // uu表条件USER_ID_A,SHORT_CODE，relatyopeCOde（20,41)
        int existShortCodeInt = existShortCode.getData(0).getInt("RECORDCOUNT");

        if (existShortCodeInt > 0)
        {
            data.put("ERROR_MESSAGE", "业务规则限制：所填短号(在同一VPMN集团)已经被使用！所填短号为：" + shortCode);
            result = "false";
        }

        IDataset existShortCodeByTrade = GroupQueryBean.CheckVUserShortCodeByTrade(inData);// trade_uu表trade_type_code('3034','3035')
        // 条件USER_ID_A,SHORT_CODE
        int existShortCodeByTradeInt = existShortCodeByTrade.getData(0).getInt("RECORDCOUNT");

        if (existShortCodeByTradeInt > 0)
        {
            data.put("ERROR_MESSAGE", "业务规则限制：所填短号(在同一VPMN集团)在未完工工单表已经被使用！所填短号为：" + shortCode);
            result = "false";
        }

        IData param = new DataMap();
        param.put("USER_ID_B", data.getString("USER_ID_A"));
        param.put("RELATION_TYPE_CODE", "40");
        String userIdA = "";
        IDataset Parentinfos = RelaUUInfoQry.getRelaUUInfoByUseridB(data.getString("USER_ID_A"), Route.getCrmDefaultDb(), null);// 只有条件
        // USER_ID_B,40是子母VPMN
        if (IDataUtil.isNotEmpty(Parentinfos))
        {
            IDataset subParentinfo = DataHelper.filter(Parentinfos, "RELATION_TYPE_CODE=40");
            if (IDataUtil.isNotEmpty(subParentinfo))
            {
                userIdA = subParentinfo.getData(0).getString("USER_ID_A"); // 只能加入一个母VPMN集团
            }

        }
        if (StringUtils.isNotBlank(userIdA))
        {
            param.put("USER_ID_A", userIdA);
            param.put("RELATION_TYPE_CODE", "40");
            IDataset subParentinfos = RelaUUInfoQry.getGrpRelaUUInfoByUserIda(userIdA, "40");

            for (int m = 0; m < subParentinfos.size(); m++)
            {
                String userida = subParentinfos.getData(m).getString("USER_ID_B");

                // uu表条件USER_ID_A,SHORT_CODE，relatyopeCOde（20,41)
                inData.clear();
                inData.put("USER_ID_A", userida);
                inData.put("SHORT_CODE", shortCode);
                IDataset grpoutserialnumbers = GroupQueryBean.CheckVUserShortCode(inData);
                if (IDataUtil.isNotEmpty(grpoutserialnumbers))
                {
                    if (grpoutserialnumbers.getData(0).getInt("RECORDCOUNT") > 0)
                    {
                        data.put("ERROR_MESSAGE", "业务规则限制：新增子VPMN集团的短号码与母VPMN集团其它短号码重复，业务不能继续！！所填短号为：" + shortCode);
                        result = "false";
                    }
                }
                IData info = new DataMap();
                info.put("USER_ID_A", userida);
                info.put("SHORT_CODE", shortCode);
                // trade_uu表 条件USER_ID_A,SHORT_CODE ,trade_type_code('3034','3035')
                IDataset grpoutserialnumbersbytrade = GroupQueryBean.CheckVUserShortCodeByTrade(info);
                int grpoutserialnumbersbytradeInt = grpoutserialnumbersbytrade.getData(0).getInt("RECORDCOUNT");

                if (grpoutserialnumbersbytradeInt > 0)
                {
                    data.put("ERROR_MESSAGE", "业务规则限制：新增子VPMN集团的短号码在未完工单表已经被使用，业务不能继续！！所填短号为：" + shortCode);
                    result = "false";
                }

            }
            IDataset outserialnumbers = RelaUUInfoQry.qryRelaOutNetInfo(userIdA, null, shortCode, null, null, Route.getCrmDefaultDb());
            if (IDataUtil.isNotEmpty(outserialnumbers))
            {
                data.put("ERROR_MESSAGE", "业务规则限制：新增子VPMN集团的短号码与母VPMN集团其它短号码重复，业务不能继续！！所填短号为：" + shortCode);
                result = "false";
            }
        }
        data.put("RESULT", result);
        return data;
    }

    /**
     * VPMN验证函数
     * 
     * @param step
     *            (shortCode表示短号验证)
     * @return (true:正确 false:错误)
     * @author sht
     */
    public static boolean validchk(IData data, String chkFlag) throws Exception
    {
        if (chkFlag.equals("shortCode"))
        {
            IData reData = shortCodeValidateVpn(data);

            if (IDataUtil.isNotEmpty(reData))
            {
                return "false".equals(reData.getString("RESULT")) ? false : true;
            }

        }

        return true;
    }

    /**
     * VPMN产品编码生成规则
     * 
     * @author tengg
     * @param pd
     * @param work_yype_code
     * @return
     * @throws Exception
     */
    public static String vpmnNoCrt(String work_yype_code) throws Exception
    {
        String l_s_vpn, l_s_code;

        if ("A".equals(work_yype_code))
        {
            l_s_code = "10";
        }
        else
        {
            l_s_code = "0" + work_yype_code;
        }
        String vpnno = SeqMgr.getVpmnIdIdForGrp();
        l_s_vpn = CSBizBean.getVisit().getCityCode().substring(2, 4);
        String vpnNo = "V0" + l_s_vpn + "0" + l_s_code + vpnno.substring(vpnno.length() - 3, vpnno.length());

        return vpnNo;
    }

    /**
     * 获取VPN业务执行时间
     * 
     * @return
     * @throws Exception
     */
    public static String createVpmnExecTime() throws Exception
    {
        String systime = SysDateMgr.getSysTime();
        String sysdtimehour = systime.substring(11, 19);
        String defaulttime = "22:00:00";
        // 获取执行时间
        String exect_time = "";
        IDataset defualttimedata = ParamInfoQry.getCommparaByParamattr("CGM", "962", "RAISEVPN_DEFAULTTIME", CSBizBean.getTradeEparchyCode());
        if (IDataUtil.isNotEmpty(defualttimedata))
        {
            defaulttime = defualttimedata.getData(0).getString("PARA_CODE1");
        }

        IDataset exectimeset = ParamInfoQry.getCommparaByParamattr("CGM", "962", "RAISEVPN_EXECTIME", CSBizBean.getTradeEparchyCode());
        if (IDataUtil.isEmpty(exectimeset))
        {
            exect_time = defaulttime;
        }
        else
        {
            int len = exectimeset.size();
            boolean ishasexec_time = false;
            for (int i = 0; i < len; i++)
            {
                IData exectimedata = exectimeset.getData(i);
                String start_time = exectimedata.getString("PARA_CODE1");
                String end_time = exectimedata.getString("PARA_CODE2");

                if (sysdtimehour.compareTo(start_time) >= 0 && sysdtimehour.compareTo(end_time) <= 0)
                {
                    String exec_tag = exectimedata.getString("PARA_CODE3", "1");
                    if (exec_tag.equals("1"))
                    {
                        exect_time = sysdtimehour;
                    }
                    else
                    {
                        exect_time = exectimedata.getString("PARA_CODE4");
                    }
                    ishasexec_time = true;
                    break;
                }
            }

            if (ishasexec_time == false)
            {
                exect_time = defaulttime;
            }
        }
        // 获取执行日期

        String current_date = SysDateMgr.getSysDate();
        String exec_date = current_date;
        if (exect_time.compareTo(sysdtimehour) < 0)
        {
            Calendar cal = Calendar.getInstance();
            cal.add(5, 1);
            String nextday = DateFormatUtils.format(cal.getTime(), SysDateMgr.PATTERN_STAND_YYYYMMDD);
            exec_date = nextday;
        }

        // 支持特殊日期不做执行
        IDataset specialdataset = ParamInfoQry.getCommparaByParamattr("CGM", "962", "RAISEVPN_SPECIALDAY", CSBizBean.getTradeEparchyCode());
        if (specialdataset != null && specialdataset.size() > 0)
        {
            String day = exec_date.substring(8, 10);

            for (int i = 0; i < specialdataset.size(); i++)
            {
                IData specialdata = specialdataset.getData(i);
                String specialday = specialdata.getString("PARA_CODE1");
                if (specialday.equals("-1"))
                {

                    specialday = getLastDay(SysDateMgr.getSysDate());
                }

                if (day.equals(specialday))
                {
                    String addday = specialdata.getString("PARA_CODE2", "1");
                    Calendar cal1 = Calendar.getInstance();
                    cal1.setTime(SysDateMgr.encodeTimestamp(exec_date));

                    cal1.add(5, Integer.parseInt(addday));
                    exec_date = DateFormatUtils.format(cal1.getTime(), SysDateMgr.PATTERN_STAND_YYYYMMDD);
                    break;
                }
            }
        }
        exect_time = exec_date + " " + exect_time;
        return exect_time;
    }

    public static String getLastDay(String timestr) throws Exception
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(SysDateMgr.encodeTimestamp(timestr));
        cal.set(5, cal.getActualMaximum(5));
        return DateFormatUtils.format(cal.getTime(), SysDateMgr.PATTERN_STAND_YYYYMMDD);
    }

    /**
     * 订购“漫游短号服务（发指令）801”时判断 @ （办理跨省或漫游拨打短号）校验所有成员的短号是否符合规则
     * 
     * @param opertype
     *            1-仅判断短号规则，2-判断短号规则和校园卡
     * @param user_id
     *            集团userID
     * @throws Exception
     * @author lixiuyu@20100512
     */
    public static void validchk801Svc(String user_id, String operType) throws Exception
    {
        /*
        if ("2".equals(operType)) // 集团成员有校园卡不能办理本业务
        {
            IDataset xykDs = RelaUUInfoQry.getRelationXykByUserIdARelationgType(user_id, "20");
            if (IDataUtil.isNotEmpty(xykDs))
            {
                IData data = xykDs.getData(0);
                int num = data.getInt("NUM");
                if (num > 0)
                {
                    CSAppException.apperr(VpmnUserException.VPMN_USER_181);
                }
            }
        }
        */
        
        if ("1".equals(operType) || "2".equals(operType)) // 判断集团成员短号有效性
        {
            IDataset dset = RelaUUInfoQry.getRelationShortCodeIsNullByUserIdARelationgType(user_id, "20");
            if (IDataUtil.isNotEmpty(dset))
            {
                IData data = dset.getData(0);
                int num = data.getInt("NUM");
                if (num > 0)
                {
                    CSAppException.apperr(VpmnUserException.VPMN_USER_182);
                }
            }
            dset.clear();
            dset = RelaUUInfoQry.getRelationShortCode6ByUserIdARelationgType(user_id, "20");
            if (IDataUtil.isNotEmpty(dset))
            {
                IData data = dset.getData(0);
                int num = data.getInt("NUM");
                if (num > 0)
                {
                    CSAppException.apperr(VpmnUserException.VPMN_USER_183);
                }
            }
            dset.clear();
            dset = RelaUUInfoQry.getRelationShortCode60ByUserIdARelationgType(user_id, "20");
            if (IDataUtil.isNotEmpty(dset))
            {
                IData data = dset.getData(0);
                int num = data.getInt("NUM");
                if (num > 0)
                {
                    CSAppException.apperr(VpmnUserException.VPMN_USER_184);
                }
            }
            dset.clear();
            dset = RelaUUInfoQry.getRelationShortLenIsErrByUserIdARelationgType(user_id, "20");
            if (IDataUtil.isNotEmpty(dset))
            {
                IData data = dset.getData(0);
                int num = data.getInt("NUM");
                if (num > 0)
                {
                    CSAppException.apperr(VpmnUserException.VPMN_USER_185);
                }
            }
        }

    }
}
