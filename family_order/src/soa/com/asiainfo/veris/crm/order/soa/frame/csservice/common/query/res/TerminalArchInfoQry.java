
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.res;

public class TerminalArchInfoQry
{
    // 查询终端信息调用终端接口 TerminalCall
    // 查询终端信息调用终端接口
    // 查询终端信息调用终端接口
    // 查询终端信息调用终端接口

    /*
     * public static IDataset getCenChlTerminalArchInfo(IData param) throws Exception { return
     * Dao.qryByCodeParser("TF_R_CEN_CHLTERMINAL_ARCH", "SEL_BY_CHNLTERMINAL", param, Route.CONN_CRM_CEN); }
     */

    /**
     * @param terminalId
     * @return
     * @throws Exception
     */
    /*
     * public static IDataset getTerminalById(String terminalId) throws Exception { IData param = new DataMap();
     * param.put("TERMINAL_ID", terminalId); return Dao.qryByCode("TF_R_TERMINAL_ARCH", "SEL_BY_TERMINAL_ID_NEW",
     * param); }
     */

    /*
     * public static IDataset getTerminalBySelDiffByTerminalID(String terminalId) throws Exception { IData param = new
     * DataMap(); param.put("TERMINAL_ID", terminalId); return Dao.qryByCode("TF_R_TERMINAL_ARCH",
     * "SEL_DIFF_BY_TERMINAL_ID", param); }
     */

    // 查询输入的串号资料
    /*
     * public static IDataset queryTerminal(IData inparam) throws Exception { return Dao.qryByCode("TF_R_TERMINAL_ARCH",
     * "SEL_FOR_SALE", inparam); }
     */

    /**
     * 查询手机保障服务变更的新串号是否在15天内出库
     * 
     * @param params
     *            TERMINAL_ID 查询所需参数
     * @return IDataset
     * @throws Exception
     */
    /*
     * public static IDataset queryTerminalByIdAndDate(String terminalId) throws Exception { IData param = new
     * DataMap(); param.put("TERMINAL_ID", terminalId); return Dao.qryByCode("TF_R_TERMINAL_ARCH",
     * "SEL_BY_TERMINAL_ID_NEW", param); }
     */
}
