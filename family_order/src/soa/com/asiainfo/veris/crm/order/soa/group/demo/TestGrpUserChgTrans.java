
package com.asiainfo.veris.crm.order.soa.group.demo;

import com.ailk.biz.BizConstants;
import com.ailk.biz.process.BizProcess;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.rule.CRMMVELMiscCache;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script.MVELExecutor;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script.MVELMiscCache;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.GrpRegTradeData;

/**
 * 后台进程示例，每个IProcess.start时，系统会单独分配一个线程来执行run()里的逻辑，支持超时设置setTimeout()
 * 
 * @author liaos
 */
public class TestGrpUserChgTrans extends BizProcess
{

    /**
     * 所有的业务逻辑都在这里处理
     */
    public void run() throws Exception
    {
        IDataset idataset = CSAppCall.call("http://127.0.0.1:8080/saleserv/service", "CS.ChangeUserElementSvc.changeUserElement", getInput(), false);

        // 数据库操作
        // CustBean bean = BeanManager.createBean(CustBean.class);
        // IData cust = bean.queryCustById("123221312301", "3109022309365911");
        // bean.Test();

        // 设置SessionCache数据, 在QCS_CustMgrByName里可以通过CUST_INFO获取到SessionCache的数据
        // getSessionCache().setAttribute("CUST_INFO", cust);

        // getInput().put("VIP_ID", "123221312301");
        // getInput().put("CUST_ID", "3109022309365911");
        // bean.updateCustName(getInput());

        // CustDAO dao = CustBean.createDAO(CustDAO.class, "0731");
        // getInput().put("TRADE_ID", "3113010143224247");
        // dao.executeUpdateByCodeCode("TF_B_TRADE_SVC", "UPD_TRADESVC_STARTDATE2", getInput());

        // 独立事务,根据Process归属的子系统获取0731的连接,连接默认已setAutoCommit(true)
        // String route = DBRouteCfg.getRoute(DBRouteCfg.getGroup(getGroup()), "0731");
        // DBConnection conn = SessionManager.getInstance().getAsyncConnection(route);

        // PreparedStatement stmt =
        // conn.prepareStatement("UPDATE tf_b_trade_svc SET start_date = sysdate where trade_id=?");
        // stmt.setString(1, "4413010143224300");
        // stmt.executeUpdate();
        // stmt.close();
        // conn.commit(); //若不提交将自动回滚
        // conn.close(); //必须手动close
    }

    public static void main(String[] args)
    {
        IData input = new DataMap();

        // 上下文数据,即Visit里的信息
        input.put(BizConstants.STAFF_ID, "SUPERUSR");
        input.put(BizConstants.STAFF_NAME, "SUPERUSR");
        input.put(BizConstants.STAFF_EPARCHY_CODE, "0898");
        input.put(BizConstants.STAFF_EPARCHY_NAME, "0898");
        input.put(BizConstants.LOGIN_EPARCHY_CODE, "0898");
        input.put(BizConstants.LOGIN_EPARCHY_NAME, "0898");
        input.put(BizConstants.ROUTE_EPARCHY_CODE, "0898");
        // input.put(Route.USER_EPARCHY_CODE,"0898");
        input.put(BizConstants.DEPART_ID, "xxxx");
        input.put(BizConstants.DEPART_CODE, "xxxx");
        input.put(BizConstants.DEPART_NAME, "xxxx");
        input.put(BizConstants.CITY_CODE, "xxxx");
        input.put(BizConstants.CITY_NAME, "xxxx");
        input.put(BizConstants.PROVINCE_CODE, "xxxx");
        input.put(BizConstants.IN_MODE_CODE, "2");
        input.put(BizConstants.REMOTE_ADDR, "xxxx");
        input.put(BizConstants.SUBSYS_CODE, "saleserv");

        // 业务数据
        input.put("USER_ID", "1114071022726942");
        input.put("PRODUCT_ID", "8000");

        IDataset list = new DatasetList();

        IData aa = new DataMap();
        aa.put("DISCNT_CODE", "1286");
        aa.put("MODIFY_TAG", "1");
        aa.put("PACKAGE_ID", "80000102");
        list.add(aa);

        IData bb = new DataMap();
        bb.put("DISCNT_CODE", "1285");
        bb.put("MODIFY_TAG", "0");
        bb.put("PACKAGE_ID", "80000102");
        list.add(bb);

        input.put("LIST_INFOS", list);

        // 9130 ADC利客发产品产品办理
        /*
         * input.put("USER_ID", "1113102549250290"); input.put("PRODUCT_ID", "10005742");
         */

        // 成员定制 服务
        /*
         * StringBuilder sb1 = new StringBuilder(100); sb1.append("9130,91300001,9000;913001,91300101,91300101");
         * input.put("SERVICE_CODE", sb1.toString());
         */

        // 成员定制 资费
        /*
         * StringBuilder sb2 = new StringBuilder(100); sb1.append("800001,80000102,90050101"); input.put("DISCNT_CODE",
         * sb1.toString());
         */

        /*
         * IDataset list = new DatasetList(); IData aa= new DataMap(); aa.put("DISCNT_CODE", "671");
         * aa.put("MODIFY_TAG", "1"); //aa.put("PACKAGE_ID", "57420002"); list.add(aa);
         */

        /*
         * IData bb= new DataMap(); bb.put("DISCNT_CODE", "100023"); bb.put("MODIFY_TAG", "0"); bb.put("PACKAGE_ID",
         * "57420001"); list.add(bb);
         */

        input.put("LIST_INFOS", list);
        // 创建Process对象，并设置归属组用来控制数据库连接的路由
        TestGrpUserChgTrans process = new TestGrpUserChgTrans();
        process.setGroup("saleserv");
        // process.setTradeEparchyCode("0898");

        // 显示执行结果
        boolean result = process.start(input);

    }

    public IDataset testMvel(IData param) throws Exception
    {
        // String orderId = param.getString("ORDER_ID");
        String templateContent = param.getString("TEMPLATE_CONTENT");

        // RegOrderData regOrder = new RegOrderData(orderId);
        IData aa = new DataMap();
        aa.put("PRODUCT_ID999", "phb");

        IData aa2 = new DataMap();
        IData bb1 = new DataMap();
        aa2.put("TF_B_TRADE_SVC", new DatasetList());
        aa2.put("TF_B_TRADE_DISCNT", new DatasetList());
        aa2.put("TF_B_TRADE", aa);

        GrpRegTradeData tt = new GrpRegTradeData(aa);
        tt.setGrpRegTradeData(aa2);

        MVELMiscCache miscCache = CRMMVELMiscCache.getMacroCache();
        MVELExecutor exector = new MVELExecutor();
        exector.setMiscCache(miscCache);
        exector.prepare(tt);

        String result = exector.applyTemplate(templateContent);
        IDataset results = new DatasetList();
        IData map = new DataMap();
        map.put("RESULT", result);
        results.add(map);
        return results;
    }
}
