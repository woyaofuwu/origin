
package com.asiainfo.veris.crm.order.soa.person.busi.plat.crossprovauth;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;

public class MPayCrossProvAuthBean extends CSBizBean
{
    /**
     * 参考文档：《手机支付3期内部接口规范.doc》 (青海版本) 鉴权（本省作为落地方） 交易编码：BIP2B085_ T3000004 BUSI_SIGN：BIP2B085_ T3000004_0_0，BIP2B085_
     * T3000004_1_0 流程名：落地调用的流程名由CRM确定，此处定为MPayCrossProvAuthIntf
     * --------------------------------------------------------------------------------------------------------
     * 发起方传过来数据格式为 序号 元素名称 约束 类型 宽度 描述 取值说明 1 IDTYPE 1 String F2 标识类型 01-手机号码 2 IDVALUE 1 String V32 标识值 手机号码 3
     * IDCARDTYPE ? String F2 证件类型 参见客户证件类型编码 4 IDCARDNUM ? String V32 证件号码 5 PASSWD ? String V64 客服密码 用户在省BOSS中的客服密码。
     * （外省传过来时是密文，经过一级BOSS后，到CRM时，是明文，要把此明文用CRM的加密方法加密后，才能去数据库的密文进行比较）
     * -------------------------------------------------------------------------------------------------------- 要求应答报文为：
     * 序号 元素名称 约束 类型 宽度 描述 取值说明 1 USER_STATE_CODESET 1 String F2 用户状态代码 参见用户状态编码 2 STATUS_CHG_TIME ? String F14 用户状态变更时间
     * YYYYMMDDHHMMSS 3 MPAY 1 String F1 是否开通手机支付业务 0 开通 1未开通
     */
    private static Logger log = Logger.getLogger(MPayCrossProvAuthBean.class);

    /**
     * 类型转换，COPY and modify From com.linkage.saleserv.bean.common.person.interboss.util.LanuchUtil.encodeIdType();
     * Remark by caorl; 把原始一级BOSS通用的几种证件类型 00 身份证件 01 VIP卡 02 护照 04 军官证 05 武装警察身份证 99 其他证件 转换为证件类型 SELECT A.*,A.ROWID
     * FROM td_s_static A WHERE 1=1 AND a.type_id='IBOSS_PSPT_TYPE_CODE' order by a.data_id asc ; 0 身份证 1 VIP卡 A 护照 C
     * 军官证 G 户口簿 K 武装警察身份证 Z 其他证件
     */
    public String encodeIdType(String IdType)
    {
        String lanuchTdType = "Z";

        if ("00".equals(IdType))
        {
            lanuchTdType = "0"; // 0 身份证
        }
        else if ("01".equals(IdType))
        {
            lanuchTdType = "1"; // 1 VIP卡
        }
        else if ("02".equals(IdType))
        {
            lanuchTdType = "A"; // A 护照
        }
        else if ("04".equals(IdType))
        {
            lanuchTdType = "C"; // C 军官证
            // }else if("05".equals(IdType))//tf_f_customer表中K对应的证件类型为：组织机构代码证，如其他省K对应的为：武装警察身份证，请使此段代码生效！
            // // SELECT A.*,A.ROWID FROM td_s_static A WHERE 1=1 AND a.type_id LIKE 'TD_S_PASSPORTTYPE' order by
            // a.data_id asc ;
            // lanuchTdType = "K"; //05 武装警察身份证
        }
        else
        {
            lanuchTdType = "Z";// Z 其他证件
        }
        return lanuchTdType;

        // LanuchUtil util = new LanuchUtil();
        // return util.decodeIdType(IdType);
    }

    public IData MPayCrossProvAuth(IData param) throws Exception
    {
        IData retData = new DataMap();// 返回结果集
        param.put("SERIAL_NUMBER", param.getString("IDVALUE", param.getString("SERIAL_NUMBER")));

        // 设置路由地州 这个在RouteUtil.getRoute里面已经考虑
        // util.setRouteEparchyCodeBySn(pd, param.getString("SERIAL_NUMBER"));

        /*******************************************************************************************************************
         * 验证输入参数是否完整
         */
        if (param.getString("SERIAL_NUMBER", "").length() != 11)
        {
            CSAppException.apperr(PlatException.CRM_PLAT_1001_1, param.getString("SERIAL_NUMBER", ""));
        }
        if ("".equals(param.getString("PASSWD", "")))
        {// 客服密码为空时，则表示使用证件来验证，此时，证件类型和号码必须都正确
            if ("".equals(param.getString("IDCARDTYPE", "")) && "".equals(param.getString("IDCARDNUM", "")))
            {
                CSAppException.apperr(PlatException.CRM_PLAT_1001_2);
            }
            else if (!"".equals(param.getString("IDCARDTYPE", "")) && "".equals(param.getString("IDCARDNUM", "")))
            {
                CSAppException.apperr(PlatException.CRM_PLAT_1001_3);
            }
            else if ("".equals(param.getString("IDCARDTYPE", "")) && !"".equals(param.getString("IDCARDNUM", "")))
            {
                CSAppException.apperr(PlatException.CRM_PLAT_1001_4);
            }
        }

        /*******************************************************************************************************************
         * 查询最新的一条记录。（update_time在表tf_f_user中并不是最新的。所以未使用） SELECT
         * A.SERIAL_NUMBER,a.USER_PASSWD,a.CUST_ID,A.USER_ID,A.USER_STATE_CODESET
         * ,to_char(A.LAST_STOP_TIME,'yyyyMMddhh24miss') STATUS_CHG_TIME FROM TF_F_USER A WHERE 1=1 AND
         * A.SERIAL_NUMBER='13574102335' AND A.LAST_STOP_TIME = (SELECT MAX(B.LAST_STOP_TIME) FROM TF_F_USER B WHERE
         * B.SERIAL_NUMBER='13574102335') AND A.USER_PASSWD='clgWou' AND EXISTS( SELECT 1 FROM TF_F_CUSTOMER C WHERE
         * C.CUST_ID=A.CUST_ID AND C.PSPT_TYPE_CODE='0' AND C.PSPT_ID='******');
         */
        IDataset userDataset = UserInfoQry.getUserInfoLastStop(param.getString("SERIAL_NUMBER"));
        if (userDataset.size() < 1)
        {
            CSAppException.apperr(PlatException.CRM_PLAT_1001_5, param.getString("SERIAL_NUMBER"));
        }
        else
        {
            // 只要找到用户资料，用户是否停机，销户等信息，在表tf_f_user的字段USER_STATE_CODESET中就会体现出来，在后面会把此标识返回一级BOSS。
            // 所以此处不用特别的在意 号码的状态remove_tag的值是什么。
            // 如果有相同的此号码的几条记录时，通过条件：AND A.LAST_STOP_TIME = (SELECT MAX(B.LAST_STOP_TIME) FROM TF_F_USER B WHERE
            // B.SERIAL_NUMBER=:SERIAL_NUMBER )
            // 将会找到有效的那条记录。
        }
        IData data = userDataset.getData(0);
        /***********************************************************************************************************************
         * 验证 客服密码或证件的有效性 IsCheckPass为验证结果，默认为没有通过。 如果同时输入了客服密码和证件号码，则同时进行验证。否则只验证一项。
         */
        boolean IsCheckPass = false;// 如果同时输入了客服密码和证件号码，则同时进行验证。否则只验证一项。
        /******************************************************************************************************************
         * 验证客服密码
         */
        if (param.getString("PASSWD", "").length() > 0)
        {// 用客服密码验证
            data.put("USER_PASSWD", param.getString("PASSWD"));// 明文密码
            boolean passOk = UserInfoQry.checkUserPassWd(data.getString("USER_ID"), data.getString("USER_PASSWD"));// 本地客服密码密文，与传来密码明文加密后进行比较。返回0表示成功，-1表示失败
            if (passOk)
            {
                // 密码正确
                IsCheckPass = true;
            }
            else if (!passOk)// 密码错误
            {
                CSAppException.apperr(PlatException.CRM_PLAT_1001_6);
            }
        }
        /******************************************************************************************************************
         * 验证证件号码
         */
        if (param.getString("IDCARDTYPE", "").length() > 0)
        {// 用 证件类型和号码验证
            param.put("CUST_ID", data.getString("CUST_ID"));
            param.put("IDCARDTYPE", this.encodeIdType(param.getString("IDCARDTYPE")));// 转换IBOSS证件类型为tf_f_customer表中的证件类型
            param.put("IDCARDNUM", param.getString("IDCARDNUM"));// 此为冗余代码，当证件类型存在时，一定存在证件号码，前面有验证代码。

            if ("1".equals(param.getString("IDCARDTYPE")))
            {// VIP卡 ，从tf_f_cust_vip中验证
                IDataset CustomerDataset = CustVipInfoQry.getCustInfoByCustidAndVipid(param.getString("CUST_ID"), param.getString("IDCARDNUM"));
                if (CustomerDataset.size() < 1)
                {// 没有找到记录，表示会员卡号码错误，直接返回错误信息。
                    CSAppException.apperr(PlatException.CRM_PLAT_1001_7);
                }
                else
                {
                    IsCheckPass = true;
                }
            }
            else if ("Z".equals(param.getString("IDCARDTYPE")))
            {// 其他类型，从tf_f_customer表中验证，不验证证件类型。
                IDataset CustomerDataset = CustomerInfoQry.getCustByCustidAndPsptid2(param.getString("CUST_ID"), param.getString("IDCARDNUM"));
                if (CustomerDataset.size() < 1)
                {// 没有找到记录，表示证件号码错误，直接返回错误信息。
                    CSAppException.apperr(PlatException.CRM_PLAT_1001_8);
                }
                else
                {
                    IsCheckPass = true;
                }
            }
            else
            {// 从tf_f_customer表中验证，验证证件类型证件号码。
                IDataset CustomerDataset = CustomerInfoQry.getCustByCustidAndPsptidAndType2(param.getString("CUST_ID"), param.getString("IDCARDNUM"), param.getString("IDCARDTYPE"));
                if (CustomerDataset.size() < 1)
                {// 没有找到记录，表示证件类型和证件号码不匹配，直接返回错误信息。
                    CSAppException.apperr(PlatException.CRM_PLAT_1001_8);
                }
                else
                {
                    IsCheckPass = true;
                }
            }
        }
        /***************************************************************************************************************************
         * 检查 验证结果IsCheckPass 的状态，看是否通过的验证
         */
        if (IsCheckPass == false)
        {
            CSAppException.apperr(PlatException.CRM_PLAT_1001_9);
        }
        /*******************************************************************************************************************************
		 */
        retData.put("USER_STATE_CODESET", data.getString("USER_STATE_CODESET").length() == 1 ? "0" + data.getString("USER_STATE_CODESET") : data.getString("USER_STATE_CODESET", "99"));// 用户状态代码,99此号码不存在
        retData.put("STATUS_CHG_TIME", data.getString("STATUS_CHG_TIME"));// 用户状态变更时间

        /***************************************************************************************************************************
         * 验证 是否开通了手机支付业务 查询是否存在有效的手机支付业务。 SELECT A.*,A.ROWID FROM tf_f_user_platsvc A WHERE 1=1 AND
         * a.user_id='3107031704970572' and sysdate between a.start_date and a.end_date and a.biz_type_code='54' and
         * a.biz_state_code='A';
         */
        IDataset mpayDataset = UserPlatSvcInfoQry.queryByUseridBiztype(data.getString("USER_ID"), "54", "A");

        if (mpayDataset.size() > 0)
        {
            retData.put("MPAY", "0");// 开通了手机支付服务。
        }
        else
        {
            retData.put("MPAY", "1");// 未开通，或暂停了手机支付业务
        }

        retData.put("X_RESULTCODE", "0");
        retData.put("X_RESULTINFO", "受理成功！");
        return retData;
    }
}
