
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.Utility;

/**
 * 根据html中的style生成的包含wadePrint支持的position和style
 * 
 * @author Lin
 */
class PositionStyle
{
    private String position;

    private String style;

    public PositionStyle(String position, String style)
    {

        this.position = position;
        this.style = style;
    }

    public String getPosition()
    {

        return position;
    }

    public String getStyle()
    {

        return style;
    }

    public void setPosition(String position)
    {

        this.position = position;
    }

    public void setStyle(String style)
    {

        this.style = style;
    }
}

/**
 * 支持WadePrint控件的工具类 包含：将html中可以显现的page转换成WadePrint支持的命令串 根据模版编码从数据库中获取出命令串
 * 
 * @author Lin
 */
public class PrintUtil
{
    // private static EHCache cache = null;

    public static final int DEFAULT_ITEM_TYPE = 0;

    public static final double DEFAULT_PAGE_HEIGHT = 28.5;

    public static final double DEFAULT_PAGE_SCALELEFT = 0.25;

    public static final String DEFAULT_PAGE_SCALEMODE = "3";

    public static final double DEFAULT_PAGE_SCALETOP = 0.1;

    public static final double DEFAULT_PAGE_WIDTH = 21;

    public static final String DEFAULT_SEPARATOR = ",";

    private static final int IS_POSITION = 1;

    private static final int IS_STYLE = 2;

    public static final int ITEM_TYPE_STATIC = 0;

    public static final int ITEM_TYPE_TEXTFIELD = 1;

    private static final Logger log = Logger.getLogger(PrintUtil.class);

    public static final int PAGE_SCALEMODE_CM = 7;

    public static final int PAGE_SCALEMODE_PX = 3;

    private static final String POSITIONS_STR = "top,left,right,bottom,height,width";

    private static final String PRINT_TEMPLET_PATH = "printtemplet";

    public static final double RATE_CM_PX = 37.8;

    private static final String STYLES_STR = "font-name,font-size,font-weight,font-style,fore-color," + "align,valign," + "border-width,border-style,border-color";

    private static final boolean webMode = true;

    static
    {
        try
        {
            // cache = EHCacheManager.getInstance().getCache("COM_ACCT_PRINT");
            // if (log.isDebugEnabled())
            // log.debug("get the cache COM_ACCT_PRINT ......" + cache);
        }
        catch (Exception e)
        {
            // cache = null;
        }
    }

    private static Object getCache(String name) throws Exception
    {

        /*
         * if (cache != null) { ICacheElement element = cache.get(name); return element.getValue(); }
         */
        return null;
    }

    /**
     * 将px的值转换成cm 换算率为RATE_CM_PX
     * 
     * @param px
     * @return
     */
    public static String getCmByPx(String px)
    {

        return String.format("%1$2.1f", new Object[]
        { new Double(Double.parseDouble(px) / RATE_CM_PX) });
    }

    /**
     * 从一个静态模版配置文件xml中解析出来的根节点的Element中获取出WadePrint的命令串
     * 
     * @param root
     * @return
     * @throws Exception
     */
    public static String getCmdByRootElement(Element root) throws Exception
    {

        StringBuilder cmd = new StringBuilder();

        // String name = root.getAttributeValue("name", "A4未命名页面");
        String width = root.getAttributeValue("width", Double.toString(DEFAULT_PAGE_WIDTH));
        String height = root.getAttributeValue("height", Double.toString(DEFAULT_PAGE_HEIGHT));
        // String scaleLeft = root.getAttributeValue("scaleleft",
        // Double.toString(DEFAULT_PAGE_SCALELEFT));
        // String scaleTop = root.getAttributeValue("scaletop",
        // Double.toString(DEFAULT_PAGE_SCALETOP));
        String scaleMode = root.getAttributeValue("scalemode", DEFAULT_PAGE_SCALEMODE);
        String defItemType = root.getAttributeValue("defaultItemType", Integer.toString(DEFAULT_ITEM_TYPE));

        cmd.append("<?SetPaper " +
        // "name=\"" + name + "\" " +
                "width=\"" + width + "\" " + "height=\"" + height + "\" " +
                // "scaleleft=\"" + scaleLeft + "\" " +
                // "scaletop=\"" + scaleTop + "\"" +
                "?>");

        List children = root.getChildren();
        for (int i = 0; i < children.size(); i++)
        {
            Element child = (Element) children.get(i);

            String eleName = child.getName();
            if ("input".equalsIgnoreCase(eleName))
                continue;

            String id = child.getAttributeValue("id");
            String style = child.getAttributeValue("style");
            String itemTypeStr = child.getAttributeValue("type");
            String value = child.getText();
            int itemType = Integer.parseInt(defItemType);
            if (itemTypeStr != null && itemTypeStr.matches("\\d+"))
                itemType = Integer.parseInt(itemTypeStr);
            if (id == null || id.trim().length() == 0)
                itemType = ITEM_TYPE_STATIC;

            PositionStyle posStyle = getPositionStyle(style, scaleMode);

            switch (itemType)
            {
                /*
                 * <?String value=\"仅仅是个测试，能打出来的话说明控件没问题了\", position=\"top:10;left:3;\",
                 * style=\"valign:top;font-size:12;font-weight:bold;\", spec=\"auto-scale:TRUE;\"?>
                 */
                case ITEM_TYPE_STATIC:
                    cmd.append("<?String " + "value=\"" + value + "\" " + "position=\"" + posStyle.getPosition() + "\" " + "style=\"" + posStyle.getStyle() + "\" " + "?>");
                    break;

                case ITEM_TYPE_TEXTFIELD:
                    cmd.append("<?String " + "value=\"[" + id + "]\" " + "position=\"" + posStyle.getPosition() + "\" " + "style=\"" + posStyle.getStyle() + "\" " + "?>");
                    break;

                default:
                    throw new Exception("未支持的打印类型 【" + itemType + "】!");
            }

        }

        return cmd.toString();

    }

    /**
     * 从一个xml配置的String串中获取命令串 该String必须符合xml的规则
     * 
     * @param str
     * @return
     * @throws Exception
     */
    public static String getCmdByString(String str) throws Exception
    {

        Element root = getRootElementByStr(str);
        String cmd = getCmdByRootElement(root);
        if (log.isDebugEnabled())
            log.debug("getCmdByString...." + cmd);
        return cmd;
    }

    /**
     * 从一个xml配置文件中获取WadePrint的命令串
     * 
     * @param xml
     *            xml路径 在PRINT_TEMPLET_PATH目录下
     * @return
     * @throws Exception
     */
    public static String getCmdByXml(String xml) throws Exception
    {

        String cmd = null;
        String cacheName = PRINT_TEMPLET_PATH + "_" + xml;
        if (hasCache(cacheName))
            cmd = (String) getCache(cacheName);
        else
        {
            Element root = getRootElementByXml(xml);
            cmd = getCmdByRootElement(root);
            setCache(cacheName, cmd);
        }
        if (log.isDebugEnabled())
            log.debug("getCmdByXml..." + cmd);
        return cmd;
    }

    /**
     * 根据预处理的命令串(其中包含以[ID_XXXX]命名的需绑定的命令)和IData，生成一页打印的命令串
     * 
     * @param preCmd
     * @param data
     * @return
     * @throws Exception
     */
    public static String getFinalCmdByPreCmd(String preCmd, IData data) throws Exception
    {

        if (data == null)
            return preCmd;

        String finalCmd = preCmd;
        IData bindData = new DataMap();
        String noBingNames = null;

        Pattern pat = Pattern.compile("(\\[[a-zA-Z_0-9\\$]*\\])"); // [div_serialNumber]
        for (Matcher matcher = pat.matcher(preCmd); matcher.find();)
        {
            String item = matcher.group();
            String key = item.substring(1, item.length() - 1);
            if (!data.containsKey(key) || data.getString(key) == null)
            {
                // throw new AmException(item + "未被绑定!!");
                noBingNames = noBingNames == null ? item : noBingNames + ", " + item;
                continue;
            }
            String value = data.getString(key);
            bindData.put(key, value);
            finalCmd = finalCmd.replaceAll("\\[" + key + "\\]", value);
        }
        if (log.isDebugEnabled())
            log.debug("binding items IData..." + bindData);
        if (noBingNames != null)
            Utility.error("this items not binding...." + noBingNames);
        if (log.isDebugEnabled())
            log.debug("getFinalCmdByPreCmd....." + finalCmd);
        return finalCmd;
    }

    /**
     * 根据预处理的命令串(其中包含以[ID_XXXX]命名的需绑定的命令)和IDataset，生成多页打印的命令串
     * 
     * @param preCmd
     * @param dataset
     * @return
     * @throws Exception
     */
    public static String getFinalCmdByPreCmd(String preCmd, IDataset dataset) throws Exception
    {

        if (dataset == null || dataset.isEmpty())
        {
            return "";
        }
        StringBuilder cmd = new StringBuilder();
        for (int i = 0; i < dataset.size(); i++)
        {
            IData data = dataset.getData(i);
            cmd.append(getFinalCmdByPreCmd(preCmd, data));
            if (i < dataset.size() - 1)
            {
                cmd.append(getNewPageCmd());
            }
        }
        return cmd.toString();
    }

    /**
     * 根据从String的静态模版及IData，将IData中的值绑定到模版中，生成一页打印的命令串
     * 
     * @param divs
     * @param data
     * @return
     * @throws Exception
     */
    public static String getFinalCmdByString(String divs, IData data) throws Exception
    {

        String preCmd = getCmdByString(divs);
        return getFinalCmdByPreCmd(preCmd, data);
    }

    /**
     * 根据从String的静态模版及IDataset，将IDataset中的值绑定到模版中，生成多页打印的命令串
     * 
     * @param divs
     * @param dataset
     * @return
     * @throws Exception
     */
    public static String getFinalCmdByString(String divs, IDataset dataset) throws Exception
    {

        String preCmd = getCmdByString(divs);
        return getFinalCmdByPreCmd(preCmd, dataset);
    }

    /**
     * 根据xml配置的静态模版及IData，将IData中的值绑定到模版中，生成一页打印的命令串
     * 
     * @param xml
     * @param data
     * @return
     * @throws Exception
     */
    public static String getFinalCmdByXml(String xml, IData data) throws Exception
    {

        String preCmd = getCmdByXml(xml);
        return getFinalCmdByPreCmd(preCmd, data);
    }

    /**
     * 根据xml配置的静态模版及IDataset，将IDataset中的值绑定到模版中，生成多页打印的命令串
     * 
     * @param xml
     * @param dataset
     * @return
     * @throws Exception
     */
    public static String getFinalCmdByXml(String xml, IDataset dataset) throws Exception
    {

        String preCmd = getCmdByXml(xml);
        return getFinalCmdByPreCmd(preCmd, dataset);
    }

    /**
     * 根据style中的名字来判断该样式名称属于WadePrint的position还是style
     * 
     * @param name
     * @return
     */
    public static int getNameType(String name)
    {

        if (POSITIONS_STR.indexOf(name) >= 0)
            return IS_POSITION;
        else if (STYLES_STR.indexOf(name) >= 0)
            return IS_STYLE;
        else
            return -1;
    }

    public static String getNewPageCmd()
    {

        return "<?NewPage?>";
    }

    /**
     * 从html的style中获取出WadePrint支持的position和style
     * 
     * @param styleHtml
     * @return
     */
    public static PositionStyle getPositionStyle(String styleHtml, String scaleModeStr) throws Exception
    {

        if (!scaleModeStr.matches("\\d"))
            throw new Exception("scalemode必须为数字！");
        int scaleMode = Integer.parseInt(scaleModeStr);

        StringBuilder position = new StringBuilder();
        StringBuilder style = new StringBuilder();
        // left:350;top:30px;font-weight:bold
        // position="top:0;left:0;right:10;bottom:20;width:10;height:20"
        String[] styles = styleHtml.split(";");

        for (String perStyle : styles)
        {
            String[] nameValue = perStyle.split(":");

            if (nameValue.length < 2)
                continue;

            String name = nameValue[0].trim();
            String value = nameValue[1].trim().replaceAll("px", "");

            switch (getNameType(name))
            {
                case IS_POSITION:
                    // 如果是double 判断是否为px
                    if (value.matches("\\d+(\\.\\d*)?"))
                    {
                        // 将其他类型转成cm用
                        switch (scaleMode)
                        {
                            case PAGE_SCALEMODE_PX:
                                value = getCmByPx(value);
                                break;
                        }
                    }
                    position.append(name + ":" + value + ";");
                    break;
                case IS_STYLE:
                    style.append(perStyle + ";");
                    break;
            }
        }
        if (position.indexOf("top") < 0 || position.indexOf("left") < 0)
            throw new Exception("在div的style中必须定义left和top！style=" + styleHtml);

        return new PositionStyle(position.toString(), style.toString());
    }

    /**
     * 从一个xml配置的String中获取一个root的Element 方便在java里生成的html的内容解析出来，该String必须符合xml配置，必须有个Root的根节点
     * 
     * @param str
     * @return
     * @throws Exception
     */
    public static Element getRootElementByStr(String str) throws Exception
    {

        SAXBuilder builder = new SAXBuilder();
        ByteArrayInputStream stream = new ByteArrayInputStream(str.getBytes("UTF-8"));
        Document document = builder.build(stream);
        Element root = document.getRootElement();
        return root;
    }

    /**
     * 从xml中获取根节点的Element xml的路径为etc/PRINT_TEMPLET_PATH/下的路径 可以将静态模版的html形式配置到xml中
     * 
     * @param xml
     * @return
     * @throws Exception
     */
    public static Element getRootElementByXml(String xml) throws Exception
    {

        if (webMode)
            xml = null;// BaseUtil.getClassResourceStream(PRINT_TEMPLET_PATH + "/" + xml).toString();
        else
            xml = "E:/WorkSpace/Web_WorkSpace/NGCRM/crm/j2ee/acctmanm/etc/common/printtemplet/" + xml;

        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(xml);
        Element root = document.getRootElement();
        return root;
    }

    private static boolean hasCache(String name) throws Exception
    {

        /*
         * if (cache != null) { ICacheElement element = cache.get(name); return element == null ? false : true; }
         */
        return false;
    }

    private static void setCache(String name, Serializable object) throws Exception
    {

        /*
         * if (cache == null) { // ParameterMgr.getInstance(); } if (cache != null) cache.put(name, (Serializable)
         * object);
         */
    }
}
