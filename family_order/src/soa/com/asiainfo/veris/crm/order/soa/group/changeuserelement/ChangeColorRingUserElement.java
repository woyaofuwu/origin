
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.fee.PayMoneyData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;

public class ChangeColorRingUserElement extends ChangeUserElement
{
    public final static String SUBSYS_CODE = "CSM"; // 子系统编码

    public final static String TAG_CHAR = "0"; // 标记对应字符值

    protected IDataset dctDataset = new DatasetList();// 优惠

    private String extraFee = ""; // 附加费

    private String memRent = ""; // 成员月租费

    private boolean musicRingMode = false; // 集团彩铃付费实现方式标志

    private String payMode = ""; // 付费方式

    private String paysn = ""; // 主付费号码

    protected IDataset resDataset = new DatasetList();// 资源

    private String ring = ""; // 最大铃音数

    protected IDataset staDataset = new DatasetList();// 服务状态

    protected IDataset svcDataset = new DatasetList();// 服务

    protected IData userParam = new DataMap(); // 主付费号码成员新增的参数

    /**
     * 构造函数
     */
    public ChangeColorRingUserElement()
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

    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author xiajj
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // add by lixiuyu@20100427 集团彩铃资料修改界面实现一次性制作费多次收取需求
        if ("6200".equals(reqData.getUca().getProductId()) && !"0".equals(getOperFee()))
        {
            reqData.setIsChange(true);
        }
    }

    /**
     * 修改用户资料
     * 
     * @throws Exception
     */
    public IData getTradeUserExtendData() throws Exception
    {
        IData userData = super.getTradeUserExtendData();
        userData.put("RSRV_STR6", userParam.getString("MANAGER_NAME", ""));
        userData.put("RSRV_STR7", userParam.getString("MANAGER_PHONE", ""));
        userData.put("RSRV_STR8", userParam.getString("MANAGER_INFO", ""));
        userData.put("RSRV_STR10", userParam.getString("SERIAL_NUMBER", ""));
        userData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        return userData;
    }

    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData map = bizData.getTrade();
        map.put("RSRV_STR6", userParam.getString("MANAGER_NAME", ""));
        map.put("RSRV_STR7", userParam.getString("MANAGER_PHONE", ""));
        map.put("RSRV_STR8", userParam.getString("MANAGER_INFO", ""));
        map.put("RSRV_STR10", userParam.getString("SERIAL_NUMBER", ""));

    }

    /**
     * 处理台帐费用挂帐子表的数据(用户要求费用为0也要记录台帐)
     */
    @Override
    protected void setTradefeeDefer(IData map) throws Exception
    {
        super.setTradefeeDefer(map);

        map.put("FEE_MODE", "0");
        map.put("ACT_TAG", "1");
        map.put("FEE_TYPE_CODE", "620");
        map.put("DEFER_CYCLE_ID", "-1");
        map.put("DEFER_ITEM_CODE", "15516");
        map.put("USER_ID", reqData.getUca().getUserId());// 用户标识
    }

   //集团彩铃特殊处理
    protected void actTradePayMode() throws Exception
    {
        IDataset dataset = new DatasetList();

        List<PayMoneyData> payMoneyList = reqData.getPayMoneyList();

        if (payMoneyList == null || payMoneyList.size() == 0)
        {
            return;
        }

        for (PayMoneyData fd : payMoneyList)
        {
            dataset.add(fd.toData());
        }
        
        IDataset deferDataset = new DatasetList();
        IDataset checkDataset = new DatasetList();

        for (int i = 0, row = dataset.size(); i < row; i++)
        {
            IData data = dataset.getData(i);
            // 挂帐
            deferDataset.add(data);
        }

        // 处理支票子表
        if (checkDataset.size() > 0)
        {
            setRegCheck(checkDataset);
        }

        // 处理挂账子表
        if (deferDataset.size() > 0)
        {
            IDataset commParaInfos = CommparaInfoQry.getCommParas("CSM", "7788", reqData.getUca().getProductId(), "DEFER", CSBizBean.getUserEparchyCode());// 配置挂账信息
            if (IDataUtil.isNotEmpty(commParaInfos))
            {
                IData commParaInfo = commParaInfos.getData(0);
                for (int i = 0, size = deferDataset.size(); i < size; i++)
                {
                    IData deferData = deferDataset.getData(i);
                    deferData.put("ACT_TAG", commParaInfo.getString("PARA_CODE2", "1"));
                    deferData.put("FEE_MODE", commParaInfo.getString("PARA_CODE3", "0")); // 费用类型：0-营业费用项，1-押金，2-预存
                    deferData.put("FEE_TYPE_CODE", commParaInfo.getString("PARA_CODE4")); // 营业费用类型
                    deferData.put("DEFER_CYCLE_ID", commParaInfo.getString("PARA_CODE5", "-1")); // 账期
                    deferData.put("DEFER_ITEM_CODE", commParaInfo.getString("PARA_CODE6")); // 挂帐帐目:为明细帐目,不指定时挂到默认的帐目
                }
            }

            super.addTradefeeDefer(deferDataset);
         }
        
    }
    
    @Override
    protected void actTradefeeSub() throws Exception{
        //集团彩铃特殊处理
        
    }
    
}
