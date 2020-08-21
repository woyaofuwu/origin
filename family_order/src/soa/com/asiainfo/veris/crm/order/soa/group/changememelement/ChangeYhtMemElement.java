
package com.asiainfo.veris.crm.order.soa.group.changememelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeYhtMemElementReqData;

public class ChangeYhtMemElement extends ChangeMemElement
{
    private ChangeYhtMemElementReqData reqData = null;

    private String strZyht = "[]";

    private String strByht = "[]";

    private String zTag = "0";

    private boolean flag = true;

    private String sertype = ""; // 业务类型取值（0～2） =0：开通被叫一号通业务 =1：开通主叫一号通业务 =2：同时开通主被叫一号通业务

    public ChangeYhtMemElement()
    {

    }

    /**
     * 生成登记信息
     * 
     * @author liaoyi
     * @throws Exception
     */
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();

        String mebProductId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        IData params = reqData.cd.getProductParamMap(mebProductId);
        if (IDataUtil.isEmpty(params))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_345);
        }

        if (params.containsKey("byht"))
        {
            params.remove("byht");
        }
        if (params.containsKey("zyht"))
        {
            params.remove("zyht");
        }
        if (params.containsKey("oldbyht"))
        {
            params.remove("oldbyht");
        }
        if (params.containsKey("oldzyht"))
        {
            params.remove("oldzyht");
        }
        if (StringUtils.isNotBlank(params.getString("Z_TAG_VAL")))
        {
            params.put("CNTRX_MEMB_ONE_RTYPE", params.getString("Z_TAG_VAL")); // 设置振动方式保存在ATTR表
            params.remove("Z_TAG_VAL");
        }
        if (StringUtils.isNotBlank(params.getString("zpause")))
        {
            params.put("CallingActivated", params.getString("zpause"));
            params.remove("zpause");
        }
        if (StringUtils.isNotBlank(params.getString("bpause")))
        {
            params.put("CalledActivated", params.getString("bpause"));
            params.remove("bpause");
        }

    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author liaoyi
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        infoRegDataYhtMeb();
        infoRegDataOther();
    }

    /**
     * 处理融合一号通，主被叫副号数据
     * 
     * @param tradeParams
     * @param rela_type
     * @return
     * @throws Exception
     */
    public IDataset dealByhtRelaParam(IDataset tradeParams, String rela_type) throws Exception
    {
        IDataset paramSet = new DatasetList();
        String meb_user_id = reqData.getUca().getUserId();
        IDataset params = RelaUUInfoQry.getAllRelaUUInfoByUserIda(meb_user_id, rela_type);

        for (int i = 0; i < tradeParams.size(); i++)
        {
            IData tradeParam = tradeParams.getData(i);

            String tag = tradeParam.getString("tag", ""); // 操作标识 0 新增 1 删除 2 修改
            String inst_id = tradeParam.getString("INST_ID", ""); // 实例标识

            String buser_id = tradeParam.getString("BUSERID", ""); // 被叫副号user_id
            String bserial_number = tradeParam.getString("BSERIAL_NUMBER", ""); // 被叫副号sn

            String zuser_id = tradeParam.getString("ZUSERID", ""); // 主叫副号user_id
            String serial_number = tradeParam.getString("SERIAL_NUMBER", ""); // 主叫副号sn

            String main_flag_code = tradeParam.getString("MAIN_FLAG_CODE", ""); // 主显标志1；0

            boolean isExist = false;
            IData rela = new DataMap();
            for (int j = 0, iSize = params.size(); j < iSize; j++)
            {
                IData param = params.getData(j);
                if (inst_id.equals(param.getString("INST_ID")))
                {
                    if (StringUtils.isNotBlank(tag))
                    {
                        if (tag.equals("1")) // 删除
                        {
                            param.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                            param.put("END_DATE", getAcceptTime());
                            param.put("REMARK", reqData.getGrpUca().getUserId());
                            paramSet.add(param);
                        }
                        if (tag.equals("2")) // 修改
                        {
                            // 删除老的数据
                            param.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                            param.put("END_DATE", getAcceptTime());
                            param.put("REMARK", reqData.getGrpUca().getUserId());
                            paramSet.add(param);

                            // 新增新的数据
                            rela.put("INST_ID", SeqMgr.getInstId());
                            rela.put("RELATION_TYPE_CODE", rela_type);
                            rela.put("USER_ID_A", reqData.getUca().getUserId());
                            rela.put("SERIAL_NUMBER_A", reqData.getUca().getSerialNumber());
                            if (rela_type.equals("S7")) // 被叫副号
                            {
                                rela.put("USER_ID_B", buser_id);
                                rela.put("SERIAL_NUMBER_B", bserial_number); //
                            }
                            if (rela_type.equals("S6")) // 主叫副号
                            {
                                rela.put("USER_ID_B", zuser_id);
                                rela.put("SERIAL_NUMBER_B", serial_number); //
                            }
                            rela.put("ROLE_CODE_A", "1");
                            rela.put("ROLE_CODE_B", "0");
                            if (rela_type.equals("S7")) // 被叫副号
                            {
                                rela.put("RSRV_TAG1", zTag); // 同振0；顺振1；
                            }
                            if (rela_type.equals("S6")) // 主叫副号
                            {
                                rela.put("RSRV_TAG1", main_flag_code); // 主显标志1；0
                            }
                            rela.put("SHORT_CODE", "0");
                            rela.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                            rela.put("START_DATE", getAcceptTime());
                            rela.put("END_DATE", SysDateMgr.getTheLastTime());
                            rela.put("REMARK", reqData.getGrpUca().getUserId());
                            paramSet.add(rela);
                        }
                    }
                    else
                    // 没有修改
                    {
                        param.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                        param.put("END_DATE", param.getString("END_DATE"));
                        param.put("REMARK", reqData.getGrpUca().getUserId());
                        paramSet.add(param);
                    }
                    isExist = true;
                    break;
                }
            }
            if (!isExist) // 新增
            {
                rela.put("INST_ID", SeqMgr.getInstId());
                rela.put("RELATION_TYPE_CODE", rela_type);
                rela.put("USER_ID_A", reqData.getUca().getUserId());
                rela.put("SERIAL_NUMBER_A", reqData.getUca().getSerialNumber());
                if (rela_type.equals("S7")) // 被叫副号
                {
                    rela.put("USER_ID_B", buser_id);
                    rela.put("SERIAL_NUMBER_B", bserial_number); //
                }
                if (rela_type.equals("S6")) // 主叫副号
                {
                    rela.put("USER_ID_B", zuser_id);
                    rela.put("SERIAL_NUMBER_B", serial_number); //
                }
                rela.put("ROLE_CODE_A", "1");
                rela.put("ROLE_CODE_B", "0");
                if (rela_type.equals("S7")) // 被叫副号
                {
                    rela.put("RSRV_TAG1", zTag); // 同振0；顺振1；
                }
                if (rela_type.equals("S6")) // 主叫副号
                {
                    rela.put("RSRV_TAG1", main_flag_code); // 主显标志1；0
                }
                rela.put("SHORT_CODE", "0");
                rela.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                rela.put("START_DATE", getAcceptTime());
                rela.put("END_DATE", SysDateMgr.getTheLastTime());
                rela.put("REMARK", reqData.getGrpUca().getUserId());
                paramSet.add(rela);
            }
        }
        return paramSet;
    }

    /**
     * 获取个性化参数
     * 
     * @return
     * @throws Exception
     */
    public IData getParamData() throws Exception
    {
        // 性化参数
        String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());

        IData paramData = reqData.cd.getProductParamMap(baseMemProduct);
        if (IDataUtil.isEmpty(paramData))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_345);
        }
        return paramData;
    }

    protected BaseReqData getReqData() throws Exception
    {
        return new ChangeYhtMemElementReqData();
    }

    public void infoRegDataOther() throws Exception
    {
        IDataset dataset = new DatasetList();
        IData dataOtherCNTRX = new DataMap();
        dataOtherCNTRX.put("USER_ID", reqData.getUca().getUserId());
        dataOtherCNTRX.put("RSRV_VALUE_CODE", "CNTRX");
        dataOtherCNTRX.put("RSRV_VALUE", "融合一号通成员CNTRX服务");
        String userType = "0";// =0 (SIP终端): IMS SIP-UE 用户

        if (flag)
        {
            userType = "4";// =4 : 传统移动用户
        }
        dataOtherCNTRX.put("RSRV_STR1", userType);// 配置主号码用户类型
        if ("0".equals(sertype) || "2".equals(sertype))
        {
            dataOtherCNTRX.put("RSRV_STR2", zTag);// 振铃方式 ， 取值(范围)：Int(0~1) , =0：同振=1：顺振
        }
        dataOtherCNTRX.put("RSRV_STR9", "8174");// 用于服务开通，service_id
        dataOtherCNTRX.put("OPER_CODE", "33");// 用于服务开通，注册用03
        dataOtherCNTRX.put("RSRV_STR11", "33");// 用于服务开通，注册用03
        dataOtherCNTRX.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        dataOtherCNTRX.put("START_DATE", getAcceptTime());
        dataOtherCNTRX.put("END_DATE", SysDateMgr.getTheLastTime());
        dataOtherCNTRX.put("INST_ID", SeqMgr.getInstId());
        dataset.add(dataOtherCNTRX);

        IData dataOtherCNTRX1 = new DataMap();
        dataOtherCNTRX1.putAll(dataOtherCNTRX);
        dataOtherCNTRX1.put("OPER_CODE", "03");
        dataOtherCNTRX1.put("RSRV_STR11", "03"); // 操作类型
        dataOtherCNTRX1.put("INST_ID", SeqMgr.getInstId());
        dataset.add(dataOtherCNTRX1);

        this.addTradeOther(dataset);
    }

    /**
     * 处理台帐uu表的数据
     * 
     * @param Datas
     * @author luoyong
     * @throws Exception
     */
    public void infoRegDataYhtMeb() throws Exception
    {
        IDataset zyhtds = new DatasetList(strZyht);
        IDataset byhtds = new DatasetList(strByht);

        IDataset lineDataset = new DatasetList();

        IDataset zyhtdaset = dealByhtRelaParam(zyhtds, "S6"); // 主叫副号数据
        IDataset byhtdaset = dealByhtRelaParam(byhtds, "S7"); // 被叫副号数据

        lineDataset.addAll(zyhtdaset);
        lineDataset.addAll(byhtdaset);

        this.addTradeRelation(lineDataset);
    }

    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (ChangeYhtMemElementReqData) getBaseReqData();
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        IData paramData = getParamData();
        if (IDataUtil.isEmpty(paramData))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_345);
        }

        strZyht = paramData.getString("zyht", "[]");
        if (StringUtils.isBlank(strZyht))
        {
            strZyht = "[]";
        }
        if (StringUtils.isNotBlank(strZyht) && !strZyht.equals("[]"))
        {
            if (StringUtils.isBlank(sertype))
                sertype = "1";
            else if (sertype.equals("0"))
                sertype = "2";
        }
        strByht = paramData.getString("byht", "[]");
        if (StringUtils.isBlank(strByht))
        {
            strByht = "[]";
        }
        if (StringUtils.isNotBlank(strByht) && !strByht.equals("[]"))
        {
            if (StringUtils.isBlank(sertype))
                sertype = "0";
            else if (sertype.equals("1"))
                sertype = "2";
        }
        zTag = paramData.getString("Z_TAG_VAL", "0");

        flag = RouteInfoQry.isChinaMobile(reqData.getUca().getSerialNumber());

        // protal接口传递的数据
        reqData.setZyht_nameid(map.getString("ZYHT_NAMEID"));
        reqData.setZyht_zsn(map.getString("ZYHT_ZSN"));

    }

    /**
     * @description 处理主台账表数据
     * @author yish
     * @date 2013-10-14
     */
    protected void regTrade() throws Exception
    {
        super.regTrade();

        IData data = bizData.getTrade();
        data.put("RSRV_STR10", sertype);
    }
}
