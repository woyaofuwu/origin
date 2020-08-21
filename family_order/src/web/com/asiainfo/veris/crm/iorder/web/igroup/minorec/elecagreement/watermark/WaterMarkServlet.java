package com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.watermark;

import com.ailk.biz.BizVisit;
import com.ailk.biz.util.StaticUtil;
import com.ailk.biz.view.IBizCommon;
import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.web.view.VisitManager;
import com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.utils.EpaperSerletUtil;
import com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.watermark.maker.WaterMarkMakerUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Enumeration;

/**
 * @author ckh
 * @date 2018/12/14.
 */
public class WaterMarkServlet extends HttpServlet implements IBizCommon
{
    private static final long serialVersionUID = 1189357762421586131L;
    private static final transient Logger logger = Logger.getLogger(WaterMarkServlet.class);
    private static WaterMarkDispatcher dispatcher = null;

    /**
     * 初始化Servlet
     * @param config {@linkplain ServletConfig}
     * @throws ServletException Servlet初始化异常
     */
    @Override
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);

        // 1- 取水印处理信息
        IDataset watermarkConfig = null;
        try
        {
            watermarkConfig = StaticUtil.getStaticList("FILE_WATERMARK");
        }
        catch (Exception e)
        {
            logger.error("文件水印配置获取错误！", e);
            return;
        }

        if (DataUtils.isEmpty(watermarkConfig))
        {
            logger.error("文件水印配置获取错误！");
            return;
        }

        // 2- 初始化派发对象
        dispatcher = new WaterMarkDispatcher(watermarkConfig);
    }

    /**
     * Servlet处理方法
     * @param req 请求对象
     * @param resp 回复对象
     * @throws IOException IO处理异常
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        /*如需要post中数据可使用如下语句来获取
        BaseContext ctx = new BaseContext(req, resp);
        IData inData = ctx.getData();*/
        try
        {
            BizVisit visit = (BizVisit) VisitManager.getVisit();
            String staffName = visit.getStaffName();
            String staffId = visit.getStaffId();
            if (StringUtils.isEmpty(staffId) || StringUtils.isEmpty(staffName))
            {
                CSViewException.apperr(CrmCommException.CRM_COMM_103,"无法获取当前登录员工信息，请确认是否登录！");
            }

            IData parameters = new DataMap();
            Enumeration e = req.getParameterNames();
            while (e.hasMoreElements())
            {
                String paramName = (String) e.nextElement();

                String paramValue = req.getParameter(paramName);
                //形成键值对应的map
                parameters.put(paramName, paramValue);
            }

            String action = req.getParameter("ACTION");
            // 默认走水印处理，其他可自行扩展
            if (StringUtils.isEmpty(action))
            {
                String type = req.getParameter("TYPE");
                if (StringUtils.isEmpty(type))
                {
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "水印处理类型不能为空！");
                }
                dispatcher.dispatch(type, parameters, resp);

            }else if("NO_WATER_MARK".equals(action)){
                downloadFile(parameters,resp);
            }

            if(StringUtils.isNotBlank(parameters.getString("AGREEMENT_ID"))){//记录操作日志
                //获取客户端IP
                parameters.put("REQUEST_IP",WaterMarkMakerUtils.getIpAddress(req));
                //处理合同日志
                WaterMarkMakerUtils.insertLog(this,parameters,visit);
            }
        }
        catch (Exception e)
        {
            logger.error("水印处理失败!", e);
            printException(req, resp, e);
        }
    }

    private void downloadFile(IData param, HttpServletResponse resp) throws Exception{
        String fileId = param.getString("FILE_ID");
        boolean hasSuffix = param.getBoolean("needSuffix",true);

        File file = WaterMarkMakerUtils.getFTPFile(fileId, hasSuffix);
        // 兼容无后缀的情况
        if (!file.exists())
        {
            file = WaterMarkMakerUtils.getFTPFile(fileId, false);
        }

        String fileName = param.getString("FILE_NAME",file.getName());
        setResponseHeader(param,resp,file,fileName);

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            OutputStream outputStream = resp.getOutputStream();
            outputStream.write(bytes);
        }catch (Exception e){
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "下载文件失败！");
        }finally {
            if(inputStream != null){
                inputStream.close();
            }
        }
    }

    private void setResponseHeader(IData paramMap, HttpServletResponse response, File file, String fileName)
            throws UnsupportedEncodingException
    {
        String contentType = EpaperSerletUtil.getContentTypeByFileName(fileName);
        response.setContentType(contentType);
        boolean isDownload = !StringUtils.isEmpty(paramMap.getString("DOWNLOAD"));
        WaterMarkMakerUtils.setHeaderDownloadOrInline(isDownload, file, fileName, response);
    }

    /**
     * 普通Servlet向前台抛出异常
     *
     * @param request  请求对象
     * @param response 回复对象
     * @param e        处理异常
     * @throws IOException io异常
     */
    private static void printException(HttpServletRequest request, HttpServletResponse response, Exception e)
            throws IOException
    {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String stack = sw.toString();

        String info = Utility.parseExceptionMessage(e);
        int index = info.indexOf(BaseException.INFO_SPLITE_CHAR);

        String code = "";
        String msg = "";
        if (index != -1)
        {
            code = info.substring(0, index);
            msg = info.substring(index + 1);
        }

        info = code + "~" + msg + "~" + stack;

        response.setContentType("text/plain;charset=gbk");
        response.setContentLength(info == null ? 0 : info.getBytes("gbk").length);
        response.setCharacterEncoding("gbk");
        response.setHeader("Connection", "close");

        PrintWriter out = response.getWriter();
        out.print(info);
    }

    @Override
    public String getPageName()
    {
        return null;
    }

    @Override
    public String getMenuId()
    {
        return null;
    }

    @Override
    public String getLoginLogId()
    {
        return null;
    }

    @Override
    public IData getRequestData()
    {
        return null;
    }
}
