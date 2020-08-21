
package com.asiainfo.veris.crm.order.soa.person.busi.vipcardrestore;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public class VIPCardRestoreBean extends CSBizBean
{

    public String decodeIdType(String IdType)
    {
        String iBossTdType = null;

        if ("0".equals(IdType))
        {
            iBossTdType = "00";
        }
        else if ("1".equals(IdType))
        {
            iBossTdType = "01";
        }
        else if ("A".equals(IdType))
        {
            iBossTdType = "02";
        }
        else if ("C".equals(IdType))
        {
            iBossTdType = "04";
        }
        else if ("K".equals(IdType))
        {
            iBossTdType = "05";
        }
        else
        {
            iBossTdType = "99";
        }

        return iBossTdType;
    }

    public String encodeIdType(String IdType)
    {
        String lanuchTdType = null;

        if ("00".equals(IdType))
        {
            lanuchTdType = "0";
        }
        else if ("01".equals(IdType))
        {
            lanuchTdType = "1";
        }
        else if ("02".equals(IdType))
        {
            lanuchTdType = "A";
        }
        else if ("04".equals(IdType))
        {
            lanuchTdType = "C";
        }
        else if ("05".equals(IdType))
        {
            lanuchTdType = "K";
        }
        else
        {
            lanuchTdType = "Z";
        }

        return lanuchTdType;
    }

    public IData getCommonParam(IData inparam) throws Exception
    {

        IData commonparam = new DataMap();
        commonparam.put("PROVINCE_CODE", inparam.getString("PROVCODE"));// 省别编码
        commonparam.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        // 接入方式0 营业厅 1 客服(callcenter)2 网上客服 3 网上营业厅 4 银行 5 短信平台 6 一级BOSS 7 手机支付 8 统一帐户服务系统(uasp)
        // 9 短信营销/短信营业厅/短信代办 A 触摸屏 B 自助打印机 C 多媒体 D 自助营业厅 E 个人代扣/银行代扣 F 电话开通 G 168点播信息
        // H 空中充值 I 积分平台 J 彩铃接口 K 梦网接口 L WAP接口 M 大客户接口 N 电信卡余额 O 家校通 P 缴费卡缴费 Q 手机钱包 R POS机缴费

        commonparam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode()); // 交易地州编码
        commonparam.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode()); // 交易城市代码
        commonparam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId()); // 员工部门编码
        commonparam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId()); // 员工城市编码
        commonparam.put("TRADE_DEPART_PASSWD", ""); // 渠道接入密码
        commonparam.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode()); // 路由地州编码
        commonparam.put("ROUTETYPE", inparam.getString("ROUTETYPE")); // 路由类型 00-省代码，01-手机号
        if ("01".equals(inparam.getString("ROUTETYPE"))) // 路由值
            commonparam.put("ROUTEVALUE", inparam.getString("MOBILENUM"));
        else
            commonparam.put("ROUTEVALUE", inparam.getString("PROVCODE"));

        return commonparam;
    }
}
