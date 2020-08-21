
package com.asiainfo.veris.crm.order.soa.group.destroygroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser.DestroyGroupUser;

public class DestroyCentrexSuperTeleGroupUser extends DestroyGroupUser
{
    IDataset mebSvcDataList = new DatasetList();

    public DestroyCentrexSuperTeleGroupUser()
    {

    }

    /**
     * 生成登记信息
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // 处理VPN信息
        super.infoRegDataVpn();

        // 处理other表里总机信息
        // infoRegDataSuperNumberOther();//other表信息在destroygroupuser里有统一删除处理

        // 处理报文信息
        infoRegDataCentrexOther();

        // 加入TRADE_ID
        for (int i = 0, row = mebSvcDataList.size(); i < row; i++)
        {
            mebSvcDataList.getData(i).put("TRADE_ID", getTradeId());
        }
    }

    /**
     * 处理Other表,用来发融合总机报文
     * 
     * @throws Exception
     */
    public void infoRegDataCentrexOther() throws Exception
    {
        String userId = reqData.getUca().getUserId();
        String custId = reqData.getUca().getCustId();

        IDataset dataset = new DatasetList();
        IData centrexData = new DataMap();

        IDataset uservpninfos = UserVpnInfoQry.getUserVPNInfoByCstId(custId, null);
        IDataset uservpninfo = DataHelper.filter(uservpninfos, "USER_ID=" + userId);
        // 如果只有一条记录并且为此集团USER_ID则删除集团指令,其它则发退定哪个业务
        if (uservpninfos.size() == 1 && uservpninfo.size() == 1)
        {
            centrexData.put("USER_ID", userId);
            centrexData.put("RSRV_VALUE_CODE", "CNTRX");// domain域
            centrexData.put("RSRV_VALUE", "删除集团");
            centrexData.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID

            centrexData.put("RSRV_STR9", "817"); // 服务id
            centrexData.put("OPER_CODE", "02"); // 操作类型 02 注销
            centrexData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            centrexData.put("START_DATE", getAcceptTime());
            centrexData.put("END_DATE", getAcceptTime()); // 立即截止
            centrexData.put("INST_ID", SeqMgr.getInstId());
            dataset.add(centrexData);
        }
        else
        {
            centrexData.put("USER_ID", userId);// 集团用户ID
            centrexData.put("RSRV_VALUE_CODE", "CNTRX");// domain域
            centrexData.put("RSRV_VALUE", "Centrex集团总机业务退订");
            centrexData.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID
            centrexData.put("RSRV_STR9", "6300");// 服务ID
            centrexData.put("OPER_CODE", "07");// 操作类型 07 退订
            centrexData.put("RSRV_STR21", "CenBG");// 操作类型 //标识退订的集团业务 CenBG 融合总机
            centrexData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            centrexData.put("START_DATE", getAcceptTime());
            centrexData.put("END_DATE", getAcceptTime());
            centrexData.put("INST_ID", SeqMgr.getInstId());
            dataset.add(centrexData);
        }
        addTradeOther(dataset);

    }

    /**
     * 处理融合总机其他信息[MUTISUPERTEL]
     * 
     * @throws Exception
     */
    public void infoRegDataSuperNumberOther() throws Exception
    {
        IDataset centrexOtherInfos = UserOtherInfoQry.getUserOtherByUserRsrvValueCode(reqData.getUca().getUserId(), "MUTISUPERTEL", null);

        if (IDataUtil.isNotEmpty(centrexOtherInfos))
        {
            for (int i = 0, row = centrexOtherInfos.size(); i < row; i++)
            {
                IData centrexOtherData = centrexOtherInfos.getData(i);
                centrexOtherData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                centrexOtherData.put("END_DATE", getAcceptTime());
            }
            super.addTradeOther(centrexOtherInfos);
        }
    }

    /**
     * 构建成员服务数据
     * 
     * @param map
     * @throws Exception
     */
    public void makMebSvcData(IData map) throws Exception
    {
        IDataset superNumberList = UserOtherInfoQry.getUserOtherByUserRsrvValueCode(reqData.getUca().getUserId(), "MUTISUPERTEL", null);

        for (int i = 0, row = superNumberList.size(); i < row; i++)
        {
            IData mebSvcData = new DataMap();
            mebSvcData.put("USER_ID", reqData.getUca().getUserId());// 集团用户ID
            mebSvcData.put("SERIAL_NUMBER", superNumberList.getData(i).getString("RSRV_VALUE"));// 总机号码
            mebSvcData.put("MEB_MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            mebSvcDataList.add(mebSvcData);
        }

        map.put("MEB_SVC_DATA", mebSvcDataList);
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        // 构建成员服务数据,为调用成员服务准备数据
        makMebSvcData(map);
    }
}
