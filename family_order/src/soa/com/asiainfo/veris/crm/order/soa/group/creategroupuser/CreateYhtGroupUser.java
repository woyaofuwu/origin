
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.biz.service.BizRoute;
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
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

public class CreateYhtGroupUser extends CreateGroupUser
{
    boolean flattag = false;

    public CreateYhtGroupUser()
    {

    }

    /**
     * @description 生成登记信息
     * @throws Exception
     */
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();

        IData paramData = getParamData();
        if (IDataUtil.isNotEmpty(paramData))
        {
            String vpn_no = paramData.getString("VPN_NO", ""); // 页面传过来的vpn编码
            if (StringUtils.isNotBlank(vpn_no))
            {
                flattag = false;
            }
            else
            {
                flattag = true;
            }
        }

        // 没有vpn_no，则取多媒体桌面电话页面参数初始值，写入attr表
        if (flattag)
        {
            IDataset paramList = reqData.cd.getProductParamList(reqData.getUca().getProductId());
            String eparchyCode = BizRoute.getRouteId();
            // 取多媒体桌面电话默认参数值
            IDataset dataset = AttrItemInfoQry.getAttrItemAByIDTO("2222", "P", "0", eparchyCode, null);
            for (int row = 0, len = dataset.size(); row < len; row++)
            {
                IData dparam = (IData) dataset.get(row);
                String attr_code = dparam.getString("ATTR_CODE");
                String attr_init_value = dparam.getString("ATTR_INIT_VALUE");

                IData attr = new DataMap();
                attr.put("ATTR_CODE", attr_code);
                attr.put("ATTR_VALUE", attr_init_value);
                paramList.add(attr);
            }
        }
    }

    /**
     * @description 其它台帐处理
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        infoRegDataVPMNVpn();
        infoRegDataCentreOther();
    }

    /**
     * @description 得到融合一号通产品参数
     * @author yish
     * @return
     * @throws Exception
     */
    private IData getParamData() throws Exception
    {
        String curProductId = reqData.getUca().getProductId();
        // 融合一号通产品参数
        IData paramData = reqData.cd.getProductParamMap(curProductId);
        if (IDataUtil.isEmpty(paramData))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_345);
        }
        return paramData;
    }

    /**
     * @description 登记平台other表
     * @throws Exception
     */
    public void infoRegDataCentreOther() throws Exception
    {
        IDataset dataset = new DatasetList();
        // 如果VPN_NO没数据则发创建集团
        if (flattag)
        {
            IData centreData1 = new DataMap();

            centreData1.put("USER_ID", reqData.getUca().getUserId());
            centreData1.put("RSRV_VALUE_CODE", "CNTRX");// domain域
            centreData1.put("RSRV_VALUE", "创建集团");
            centreData1.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID

            centreData1.put("RSRV_STR9", "817"); // 服务id
            centreData1.put("OPER_CODE", "01"); // 操作类型 01 注册

            centreData1.put("STATE", TRADE_MODIFY_TAG.Add.getValue());
            centreData1.put("START_DATE", getAcceptTime());
            centreData1.put("END_DATE", SysDateMgr.getTheLastTime());
            centreData1.put("INST_ID", SeqMgr.getInstId());
            dataset.add(centreData1);
        }

        addTradeOther(dataset);
    }

    /**
     * @description 处理台帐VPN子表的数据-用户
     * @throws Exception
     */
    public void infoRegDataVPMNVpn() throws Exception
    {
        // VPMN个性化参数
        IData paramData = getParamData();

        // VPN数据
        IDataset dataset = new DatasetList();
        IData vpnData = super.infoRegDataVpn();
        vpnData.put("SCP_CODE", "10");// 湖南固定为00  海南固定为10  否则无法被叫
        vpnData.put("MAX_USERS", "50000"); // 集团最大用户数-初始值50000
        vpnData.put("VPMN_TYPE", "0");// VPN集团类型
        vpnData.put("VPN_USER_CODE", "2");

        String vpn_no = paramData.getString("VPN_NO", "");
        if (StringUtils.isNotBlank(vpn_no))
        {
            vpnData.put("VPN_NO", vpn_no); // 集团VPN编码
        }
        else
        {
            vpnData.put("VPN_NO", reqData.getUca().getSerialNumber());
        }
        dataset.add(vpnData);
        addTradeVpn(dataset);
    }

    protected void regTrade() throws Exception
    {
        super.regTrade();
    }
}
