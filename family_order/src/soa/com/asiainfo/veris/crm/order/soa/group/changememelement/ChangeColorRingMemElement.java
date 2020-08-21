
package com.asiainfo.veris.crm.order.soa.group.changememelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;

public class ChangeColorRingMemElement extends ChangeMemElement
{
    protected IData paramInfo = new DataMap(); // 产品参数

    String firstTimeNextAcct = "";// 下账期第一时间

    String lastTimeThisAcctday = ""; // 本账期最后时间

    public ChangeColorRingMemElement()
    {

    }

    /**
     * 生成登记信息
     * 
     * @throws Exception
     */
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
        paramInfo = getParamInfo();
        // j2ee还没确定要不要处理预约时间
        // String memUserId = reqData.getUca().getUserId();
        // firstTimeNextAcct = DiversifyAcctUtil.getFirstTimeNextAcct(memUserId); // 下账期第一时间
        // lastTimeThisAcctday = DiversifyAcctUtil.getLastTimeThisAcctday(memUserId, null);// 本账期最后时间
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author liaoyi
     * @throws Exception
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        boolean isAdd = "1".equals(paramInfo.getString("CANCEL_LING", ""));
        infoRegDataColorRingOther(isAdd);

    }

    /**
     * 获取参数
     * 
     * @return
     * @throws Exception
     */
    public IData getParamInfo() throws Exception
    {
        String memProductId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        IData paraminfo = reqData.cd.getProductParamMap(memProductId);
        return paraminfo;
    }

    /**
     * 生成Other表数据
     * 
     * @throws Exception
     */
    public void infoRegDataColorRingOther(boolean add) throws Exception
    {
        IData map = new DataMap();
        IData inparam = new DataMap();
        inparam.put("USER_ID", reqData.getUca().getUserId());
        inparam.put("RSRV_VALUE_CODE", "DLMR");
        IDataset datas = UserOtherInfoQry.getUserOtherByUseridRsrvcode(reqData.getUca().getUserId(), "DLMR", null);
        if (add)
        {
            if (IDataUtil.isEmpty(datas))
            {
                map.put("USER_ID", reqData.getUca().getUserId());
                map.put("RSRV_VALUE_CODE", "DLMR");
                map.put("RSRV_VALUE", "不可来电取消彩铃业务");
                map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                map.put("START_DATE", this.getAcceptTime());
                map.put("END_DATE", SysDateMgr.getTheLastTime());
            }
        }
        else
        {
            if (IDataUtil.isNotEmpty(datas))
            {
                map = datas.getData(0);
                map.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                map.put("END_DATE", this.getAcceptTime());
            }
        }

        this.addTradeOther(map);
    }

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());

        IData data = bizData.getTrade();
        // 主台帐预留字段
        data.put("RSRV_STR1", reqData.getGrpUca().getUser().getUserId()); // A用户标识：对应关系类型参数表中的A角，通常为一集团用户或虚拟用户
        data.put("RSRV_STR2", relationTypeCode);// 关系类型
        data.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber());// corpnumber集团编号
        data.put("RSRV_STR6", reqData.getGrpUca().getCustomer().getCustName());

        // 1、个人付费(增加优惠:950)
        // 2、集团付费(取消优惠:950)
    }

    @Override
    protected void makInit(IData map) throws Exception
    {
        makUcaForMebNormal(map); // 提前查三户
        makBeforeInit(map);
        super.makInit(map);

    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        // 因为在makInit已经查出三户资料了，所以这里不再查
    }

    /**
     * VPMN成员产品变更登记服务子台帐，走服务开通
     * 
     * @param map
     * @return
     * @throws Exception
     */
    public void makBeforeInit(IData map) throws Exception
    {

//        IDataset elelist = map.getDataset("ELEMENT_INFO", null);
//
//        if (IDataUtil.isNotEmpty(elelist))
//        {
//            for (int i = 0; i < elelist.size(); i++)
//            {
//                IData info = elelist.getData(i);
//                if ("S".equals(info.getString("ELEMENT_TYPE_CODE")) && "20".equals(info.getString("ELEMENT_ID")))
//                {
//                    // add by lixiuyu@20100125 用户要求集团彩铃成员新增如果有个人彩铃(20)时，就不绑定集团彩铃包的个人彩铃
//                    IDataset userSvcInfos = UserSvcInfoQry.qrySvcInfoByUserIdSvcId(reqData.getUca().getUser().getUserId(), "20");
//                    if (IDataUtil.isNotEmpty(userSvcInfos))
//                    {
//                        elelist.remove(i);
//                        break;
//                    }
//                }
//
//            }
//        }

    }
}
