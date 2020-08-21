
package com.asiainfo.veris.crm.order.soa.group.groupTrans.check;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class ParamCheckBaseBean
{
    public static void checkAllParams(IDataset allParams) throws Exception
    {
        boolean errFlag = false;
        StringBuilder errStr = new StringBuilder("参数校验失败：");
        // 一次返回所有校验错误
        for (int i = 0; i < allParams.size(); i++)
        {
            IData param = allParams.getData(i);
            String paramName = param.getString("PARAM_NAME", ""); // 检验参数名称
            String paramCode = param.getString("PARA_CODE1", ""); // 检验参数编码
            String paramValue = param.getString("PARA_CODE4", ""); // 校验参数值
            String paraCode5 = param.getString("PARA_CODE5", ""); // 必填性校验
            String paraCode7 = param.getString("PARA_CODE7", ""); // 外围渠道必填性校验
            String paraCode11 = param.getString("PARA_CODE11", ""); // 通用校验
            String paraCode12 = param.getString("PARA_CODE12", ""); // 长度校验
            String paraCode13 = param.getString("PARA_CODE13", ""); // 长度校验
            boolean innerFlag = false;
            StringBuilder paramErrStr = new StringBuilder();
            paramErrStr.append("[");
            paramErrStr.append(paramName);
            paramErrStr.append("|");
            paramErrStr.append(paramCode);
            paramErrStr.append("]");

            String rtnStr = "";
            // 根据PARA_CODE7进行必填性校验，PARA_CODE7，设定外围渠道是否必须填写

            if ("1".equals(paraCode7) && "".equals(paramValue))
            {
                innerFlag = true;
                paramErrStr.append("必须填写！");
            }
            if ("1".equals(paraCode5))
            {
                // 根据PARA_CODE11进行通用校验，1-数字，2-日期，3-下拉列表（3:type_id）,4-手机号码。
                if (!"".equals(paraCode11))
                {
                    rtnStr = checkParamFormat(paramValue, paraCode11);
                    if (!"".equals(rtnStr))
                    {
                        innerFlag = true;
                        paramErrStr.append(rtnStr);
                    }
                }
                // 根据PARA_CODE12进行长度校验。
                if (!"".equals(paraCode12))
                {
                    rtnStr = checkParamLength(paramValue, paraCode12);
                    if (!"".equals(rtnStr))
                    {
                        innerFlag = true;
                        paramErrStr.append(rtnStr);
                    }
                }
                // 根据PARA_CODE13获取其它需要校验的次数。
                // 采用反射的方式，以避免不同的业务配置不同的校验类，如果不想反射过多，可以在某一个必填参数中配置一个方法处理相关的所有校验即可。
                /*
                 * if (!"".equals(paraCode13)){ int initNum = 14; int checkTimes = Integer.parseInt(paraCode13); IData
                 * allParamData=IDataUtil.hTable2StdSTable(allParams, "PARA_CODE1", "PARA_CODE4"); for (int checkTime=0;
                 * checkTime<checkTimes && initNum<=30; checkTime++){ //根据PARA_CODE14…30获取其它校验的方法。校验方法格式:方法名;方法名 String
                 * checkStr = param.getString("PARA_CODE"+initNum++, ""); if (!"".equals(checkStr)) { String[] cSplit =
                 * checkStr.split(";"); //调用各个校验方法进行校验，由于存在联动，所以要传入所有的参数 for (int j=0; j<cSplit.length; j++) {
                 * java.lang.reflect.Method method = this.getClass().getMethod(cSplit[j], new Class[] { IData.class,
                 * IData.class }); rtnStr = (String) method.invoke(this, new Object[]{allParamData, iData}); if
                 * (!"".equals(rtnStr)){ paramErrStr.append(rtnStr); innerFlag = true; } } } } }
                 */
            }
            // 构造错误串
            if (innerFlag)
            {
                errFlag = true;
                errStr.append(paramErrStr);
            }
        }
        if (errFlag)
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, errStr.toString());
        }
    }

    public static String checkParamLength(String paramValue, String checkString) throws Exception
    {
        String errStr = "";
        if (!"".equals(checkString.trim()))
        {
            String[] minmax = checkString.trim().split(",");
            int min = 0, max = 0;
            if (minmax.length == 1 && Pattern.compile("^[0-9]*$").matcher(minmax[0].trim()).matches())
            {
                min = max = Integer.valueOf(minmax[0].trim()).intValue();
            }
            else if (minmax.length == 2 && Pattern.compile("^[0-9]*$").matcher(minmax[0].trim()).matches() && Pattern.compile("^[0-9]*$").matcher(minmax[1].trim()).matches())
            {
                min = Integer.valueOf(minmax[0].trim()).intValue();
                max = Integer.valueOf(minmax[1].trim()).intValue();
            }
            else
            {
                CSAppException.apperr(GrpException.CRM_GRP_714);
            }
            if (min > paramValue.getBytes().length || max < paramValue.getBytes().length)
            {
                errStr = "长度只能在[" + min + "-" + max + "]之间！";
            }
        }
        return errStr;
    }

    /**
     * 基础校验: 校验参数类型
     * 
     * @author lijie9, add for ng3.5, 2011-11-14
     * @param pd
     * @param paramValue
     * @param checkType
     * @return
     * @throws Exception
     */
    public static String checkParamFormat(String paramValue, String checkType) throws Exception
    {
        StringBuilder errStr = new StringBuilder();
        if ("1".equals(checkType))
        {
            Matcher matcher = Pattern.compile("^[0-9]*$").matcher(paramValue);
            if (!matcher.matches())
            {
                errStr.append("只能是数字！");
            }
        }
        else if ("2".equals(checkType))
        {
            Matcher matcher = Pattern.compile("\\d{4}[\\-]\\d{1,2}[\\-]\\d{1,2}").matcher(paramValue);
            if (!matcher.matches())
            {
                errStr.append("只能是日期格式[yyyy-mm-dd]！");
            }
        }
        else if (checkType.startsWith("3"))
        {
            String typeId = checkType.substring(2);

            IDataset typeDs = StaticUtil.getStaticList(typeId);
            IDataset filterTypeDs = DataHelper.filter(typeDs, "DATA_ID=" + paramValue);
            if (IDataUtil.isEmpty(filterTypeDs))
            {
                errStr.append("只能是下列值之一[");
                for (int i = 0, iSize = typeDs.size(); i < iSize; i++)
                {
                    IData type = typeDs.getData(i);
                    errStr.append(type.getString("DATA_ID") + ",");
                }
                errStr.replace(errStr.lastIndexOf(","), errStr.length(), "]!");
            }
        }
        else if ("4".equals(checkType))
        {
            IData idata = UserInfoQry.checkMebUserInfoBySn(paramValue);
            if (!"0".equals(idata.getString("USER_RESULT_CODE")))
                errStr.append("不是有效的手机号码!");

        }
        return errStr.toString();
    }
}
