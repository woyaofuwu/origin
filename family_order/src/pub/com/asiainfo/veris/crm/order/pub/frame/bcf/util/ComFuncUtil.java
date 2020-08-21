
package com.asiainfo.veris.crm.order.pub.frame.bcf.util;

import com.ailk.biz.bean.BizDAOLogger;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * 接口公共函数库
 *
 * @author xieyf5
 * @date 2018-04-14
 */
public final class ComFuncUtil {
    public final static String CODE_RIGHT = "0";

    public final static String CODE_ERROR = "2999";

    private static final Logger log = Logger.getLogger(BizDAOLogger.class);

    // 检验必填入参是否为空
    public static IData checkInParam(IData inParam, IDataset inParamSet) throws Exception {
        if (IDataUtil.isEmpty(inParam)) {
            throw new Exception("checkInParam入参为空（Xyf inParam）");
        }
        if (IDataUtil.isEmpty(inParamSet)) {
            throw new Exception("checkInParam入参为空（Xyf inParamSet）");
        }

        boolean emptyFlag = false; // 默认所有的入参都不为空
        String emptyField = ""; // 为空的字段名称
        for (int i = 0; i < inParamSet.size(); i++) {
            IData mapItem = inParamSet.getData(i);
            String field = mapItem.getString("FIELD");
            String type = mapItem.getString("TYPE");
            if ("String".equals(type)) {
                String item = inParam.getString(field);
                if (StringUtils.isBlank(item)) {
                    emptyFlag = true;
                }
            } else if ("Map".equals(type)) {
                IData item = inParam.getData(field);
                if (IDataUtil.isEmpty(item)) {
                    emptyFlag = true;
                }
            } else if ("List".equals(type)) {
                IDataset item = inParam.getDataset(field);
                if (IDataUtil.isEmpty(item)) {
                    emptyFlag = true;
                }
            }
            if (emptyFlag) {
                emptyField = field;
                break;
            }
        }
        IData returnMap = new DataMap();
        if (emptyFlag) {
            returnMap.put("respCode", CODE_ERROR);
            returnMap.put("respDesc", "入参" + emptyField + "不能为空");
        } else {
            returnMap.put("respCode", CODE_RIGHT);
            returnMap.put("respDesc", "入参必填字段校验通过");
        }
        return returnMap;
    }

    public static IData transOutParam(IData inParam) throws Exception {
        IData returnMap = new DataMap();
        returnMap.put("object", inParam);
        String respCode = inParam.getString("respCode");
        if (CODE_RIGHT.equals(respCode)) {
            returnMap.put("rtnCode", CODE_RIGHT);
            returnMap.put("rtnMsg", "成功");
        } else {
            returnMap.put("rtnCode", CODE_ERROR);
            returnMap.put("rtnMsg", "失败");
        }
        return returnMap;
    }
    
    public static IData transOutParamNew(IData inParam) throws Exception {
        IData returnMap = new DataMap();
        returnMap.put("object", inParam);
        String respCode = inParam.getString("respCode");
        if (CODE_RIGHT.equals(respCode)) {
            returnMap.put("rtnCode", "0");
            returnMap.put("rtnMsg", "成功");
        } else {
            returnMap.put("rtnCode", "-9999");
            returnMap.put("rtnMsg", "失败");
        }
        return returnMap;
    }

}
