
package com.asiainfo.veris.crm.order.pub.frame.bcf.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONArray;

import com.ailk.biz.bean.BizBean;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;

public final class IDataUtil
{
    /**
     * 校验入参是否在静态参数表中存在
     * 
     * @param data
     * @param name
     * @param typeId
     *            静态参数表TYPE_ID值
     * @return
     * @throws Exception
     */
    public static void chkDataByStaticValue(IData param, String name, String typeId) throws Exception
    {

        boolean flag = true;
        String value = param.getString(name);

        if (StringUtils.isBlank(value))
        {
            return;
        }

        IDataset ids = StaticUtil.getStaticList(typeId);
        for (int i = 0; i < ids.size(); i++)
        {
            if (value.equals(ids.getData(i).getString("DATA_ID")))
            {
                flag = false;
                return;
            }
        }

        if (flag)
        {
            String message = "接口参数检查: 输入参数[" + name + "=" + value + "]在静态参数[TYPE_ID=" + typeId + "]中不存在!";

            Utility.error("-1", null, message);
        }

    }

    /**
     * 校验入参是否在指定的数据表中是否存在
     * 
     * @param param
     * @param name
     * @param tabName
     *            表名
     * @param colName
     *            表字段
     * @throws Exception
     */
    public static void chkDataByTableValue(IData param, String name, String tabName, String colName) throws Exception
    {

        String value = param.getString(name);

        if (StringUtils.isBlank(value))
        {
            return;
        }

        String str = StaticUtil.getStaticValue(BizBean.getVisit(), tabName, colName, colName, value);

        if (StringUtils.isBlank(str))
        {
            String message = "接口参数检查: 输入参数[" + name + "=" + value + "]在表[" + tabName + "]字段[" + colName + "]中没有找到匹配的参数!";

            Utility.error("-1", null, message);
        }

    }

    public static String chkParam(IData data, String strColName) throws Exception
    {
        String strParam = data.getString(strColName);

        if (StringUtils.isBlank(strParam))
        {
            String strError = "接口参数检查: 输入参数[" + strColName + "]不存在或者参数值为空";

            Utility.error("-1", null, strError);
        }

        return strParam;
    }

    public static String chkParam(IData data, String strColName, String strDefault) throws Exception
    {
        String strParam = data.getString(strColName, strDefault);

        if (StringUtils.isBlank(strParam))
        {
            String strError = "接口参数检查: 输入参数[" + strColName + "]不存在或者参数值为空";
            Utility.error("-1", null, strError);
        }

        return strParam;
    }

    public static String chkParamNoStr(IData data, String strColName) throws Exception
    {
        String strParam = data.getString(strColName);

        if (StringUtils.isBlank(strParam) || "".equals(strParam))
        {
            String strError = "接口参数检查: 输入参数[" + strColName + "]不存在或者参数值为空";

            Utility.error("-1", null, strError);
        }

        return strParam;
    }

    /**
     * 判断一个IDataset里是否包含keys对应的所以values值
     * 
     * @param dataset
     * @param keys
     * @param values
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static boolean dataSetContainsAllKeysAndValues(IDataset dataset, String[] keys, String[] values) throws Exception
    {
        if (keys.length < 0 || values.length < 0 || keys.length != values.length)
        {
            Utility.error("-1", null, "入参keys不能为空或者values不能为空或者keys和values的长度不相等!");
        }

        Iterator iterator = dataset.iterator();
        while (iterator.hasNext())
        {
            int matchCount = 0;
            IData data = (IData) iterator.next();
            for (int i = 0, size = keys.length; i < size; i++)
            {
                if (StringUtils.equals(values[i], data.getString(keys[i])))
                {
                    matchCount++;
                }
            }

            // 匹配数和入参keys的长度相同 则完全匹配上
            if (matchCount == keys.length)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断一个IDataset里是否包含某个key对应的值
     * 
     * @param dataset
     * @param key
     * @param value
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static boolean dataSetContainsKeyAndValue(IDataset dataset, String key, String value) throws Exception
    {
        if (StringUtils.isBlank(key))
        {
            String message = "入参key不能为空!";
            Utility.error("-1", null, message);
        }

        Iterator iterator = dataset.iterator();
        while (iterator.hasNext())
        {
            IData data = (IData) iterator.next();
            if (StringUtils.equals(value, data.getString(key)))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据传入的2个col，比较两个IDataset对应col相同的IData 返回oriList中符合条件的数据
     * 
     * @param oriList
     * @param col
     * @param compareList
     * @param compareCol
     * @return
     * @throws Exception
     */
    public static IDataset filterByEqualsCol(IDataset oriList, String col, IDataset compareList, String compareCol) throws Exception
    {
        IDataset rtList = new DatasetList();
        for (int i = 0, size = oriList.size(); i < size; i++)
        {
            IData oriData = oriList.getData(i);
            String value = oriData.getString(col);
            if (StringUtils.isBlank(value))
            {
                continue;
            }
            for (int j = 0, size2 = compareList.size(); j < size2; j++)
            {
                IData compareData = compareList.getData(j);
                String compareValue = compareData.getString(compareCol);
                if (value.equals(compareValue))
                {
                    rtList.add(oriData);
                }
            }
        }

        return rtList;
    }

    /**
     * 传入dataset，列名返回这个列所有行拼成的String
     * 
     * @create_date Aug 10, 2009
     * @author heyq
     */
    @SuppressWarnings("unchecked")
    public static String fromDatasetToString(IDataset dataset, String columnName) throws Exception
    {
        StringBuilder buffer = new StringBuilder();
        for (Iterator iter = dataset.iterator(); iter.hasNext();)
        {
            IData element = (IData) iter.next();
            buffer.append(element.getString(columnName));
            buffer.append(",");
        }
        return buffer.length() > 0 ? buffer.substring(0, buffer.length() - 1).toString() : "";
    }

    /**
     * 用于从一个IData内 取某个字段的值返回,并且从此IData内 将该字段删除(用于处理插个性参数台帐时,还有多余的的字段时,插通用attr表)
     * 
     * @author tengg 2012-2-16
     * @param data
     * @param colname
     * @return
     * @throws Exception
     */
    public static Object getAndDelColValueFormIData(IData data, String colname) throws Exception
    {

        Object returnobj = data.get(colname);
        data.remove(colname);
        return returnobj;
    }

    /**
     * 根据KEY获取一个IData
     * 
     * @author tengg 2012-2-16
     * @param map
     * @param key
     * @return
     * @throws Exception
     */
    public static IData getData(IData map, String key) throws Exception
    {

        Object object = map.get(key);
        if (object == null)
        {
            String message = "getData从map中读取key=[" + key + "]不存在!";
            Utility.error("-1", null, message);
        }
        return (IData) object;
    }

    /**
     * 根据KEY获取一个IDataset
     * 
     * @author tengg 2012-2-16
     * @param map
     * @param key
     * @return
     * @throws Exception
     */
    public static IDataset getDataList(IData map, String key) throws Exception
    {

        Object object = map.get(key);
        if (object == null)
        {
            String message = "getDataList从map中读取key=[" + key + "]不存在!";
            Utility.error("-1", null, message);
        }
        return (IDataset) object;
    }

    /**
     * 根据键值获取DataMap
     * 
     * @param key
     * @param data
     * @param isNull
     * @return
     * @author chenlei
     */
    public static IData getDataMap(String key, IData data, boolean isNull) throws Exception
    {

        if ("".equals(key))
        {

            String message = "KEY不能为空!";

            Utility.error("-1", null, message);
        }
        if (!isNull)
        {
            if (!data.containsKey(key))
            {
                String message = "KEY[" + key + "]必须传!";

                Utility.error("-1", null, message);
            }
        }
        else
        {
            if (!data.containsKey(key))
            {
                return new DataMap();
            }
        }
        IData result = new DataMap();
        if (data.get(key) instanceof String)
        {
            result.put(key, data.get(key));
        }
        else if (data.get(key) instanceof IData)
        {
            result = data.getData(key);
        }
        if (!isNull)
        {
            if (result == null || result.size() == 0)
            {

                String message = "KEY[" + key + "]不能为空!";

                Utility.error("-1", null, message);
            }
        }
        return result;
    }

    /**
     * 取IDataset的方法
     * 
     * @param map
     *            从map中getString得到的结果，适用于所有类型的value
     * @return
     */
    public static IDataset getDataset(IData map, String key) throws Exception
    {

        String str = map.getString(key);
        if (str == null || str.equals(""))
        {
            return null;
        }
        else
        {
            return map.getDataset(key);
        }
    }

    /**
     * 通过关键字获取IDataset
     * 
     * @param key
     * @param data
     * @return
     * @author chenlei
     * @throws Exception
     */
    public static IDataset getDataset(String key, IData data) throws Exception
    {

        return getDataset(key, data, true);
    }

    /**
     * 通过关键字获取IDataset
     * 
     * @param key
     * @param data
     * @return
     * @author chenlei
     */
    public static IDataset getDataset(String key, IData data, boolean isNull) throws Exception
    {

        if ("".equals(key))
        {
            String message = "KEY不能为空!";

            Utility.error("-1", null, message);
        }
        if (!isNull)
        {
            if (!data.containsKey(key))
            {

                String message = "KEY[" + key + "]不能为空!";

                Utility.error("-1", null, message);
            }
        }
        else
        {
            if (!data.containsKey(key))
            {
                return new DatasetList();
            }
        }
        IDataset result = new DatasetList();
        if (data.get(key) instanceof String)
        {
            result.add(data.get(key));
        }
        else if (data.get(key) instanceof IDataset)
        {
            result = data.getDataset(key);
        }
        if (!isNull)
        {
            if (result == null || result.size() == 0)
            {
                String message = "未传入FORCE_OBJECT";

                Utility.error("-1", null, message);
            }
        }
        return result;
    }

    /**
     * 从paramMeen中取出拥有key值的数据放到IDataset中
     * 
     * @param key
     * @param paramMeen
     * @param paramValue
     * @return
     * @author chenlei
     */
    public static IDataset getDataset(String key, IDataset paramMeen, IDataset paramValue) throws Exception
    {

        if ("".equals(key))
        {
            String message = "KEY不能为空!";

            Utility.error("-1", null, message);
        }
        IDataset result = new DatasetList();
        for (int i = 0; i != paramMeen.size(); ++i)
        {
            if (key.equals(paramMeen.get(i)))
            {
                result.add(paramValue.get(i));
            }
        }
        return result;
    }

    /**
     * 返回一个IDataset里是否包含keys对应的所以values值的所有data
     * 
     * @param dataset
     * @param keys
     * @param values
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static IDataset getDataSetContainsAllKeysAndValues(IDataset dataset, String[] keys, String[] values) throws Exception
    {
        if (keys.length < 0 || values.length < 0 || keys.length != values.length)
        {
            Utility.error("-1", null, "入参keys不能为空或者values不能为空或者keys和values的长度不相等!");
        }
        IDataset resultDataset = new DatasetList();

        Iterator iterator = dataset.iterator();
        while (iterator.hasNext())
        {
            int matchCount = 0;
            IData data = (IData) iterator.next();
            for (int i = 0, size = keys.length; i < size; i++)
            {
                if (StringUtils.equals(values[i], data.getString(keys[i])))
                {
                    matchCount++;
                }
            }

            // 匹配数和入参keys的长度相同 则完全匹配上
            if (matchCount == keys.length)
            {
                resultDataset.add(data);
            }
        }

        return resultDataset;
    }

    /**
     * 通过关键字获取IDataset 特别解析 ["", ""]的字符串到 IDataset
     * 
     * @param key
     * @param data
     * @return
     * @author hud
     */
    public static IDataset getDatasetSpecl(String key, IData data)
    {

        String str = data.getString(key, "");
        IDataset ValueList = new DatasetList();
        if (!"".equals(str) && !"[]".equals(str))
        {
            String[] strparam = str.split(",");
            if (strparam != null && strparam.length > 0)
            {
                String param = "";
                String temp = "";
                if (strparam.length > 1)
                {
                    for (int i = 0; i < strparam.length; i++)
                    {
                        temp = strparam[i].replace(" ", "");
                        if (i == 0)
                        {
                            param = temp.substring(2, strparam[i].length() - 1);
                        }
                        else if (i == (strparam.length - 1))
                        {
                            param = temp.substring(1, strparam[i].length() - 2);
                        }
                        else
                        {
                            param = temp.substring(1, strparam[i].length() - 1);
                        }
                        ValueList.add(param);
                    }
                }
                else
                {
                    if (strparam[0].startsWith("[\""))
                    {
                        ValueList.add(strparam[0].substring(2, strparam[0].length() - 2));
                    }
                    else if (strparam[0].startsWith("["))
                    {
                        ValueList.add(strparam[0].substring(1, strparam[0].length() - 1));
                    }
                    else
                    {
                        ValueList.add(strparam[0]);
                    }
                }
            }
        }
        return ValueList;
    }

    public static String getMandaData(IData data, String name) throws Exception
    {

        String value = data.getString(name);

        if (StringUtils.isBlank(value))
        {
            String message = "字段[" + name + "]不存在!";

            Utility.error("-1", null, message);
        }

        return value;
    }

    public static String getMandaData(IData data, String name, String defaultStr) throws Exception
    {

        String value = data.getString(name, defaultStr);

        if (StringUtils.isBlank(value))
        {
            String message = "字段[" + name + "]不存在!";

            Utility.error("-1", null, message);
        }

        return value;
    }

    /**
     * 通过关键字获取字符串,为null则抛出异常
     * 
     * @param key
     * @param data
     * @param isNull
     *            是否允许为空
     * @return
     */
    public static String getString(String key, IData data, boolean isNull)
    {

        if ("".equals(key))
        {

            Utility.error("-1", null, "其他错误,[" + key + "]不能为空!");
        }
        if (data.get(key) instanceof IDataset)
        {
            Utility.error("-1", null, "[" + key + "]传值重复，该值只允许出现一次!");
        }
        String result = data.getString(key, "");

        if (!isNull && "".equals(result))
        {
            Utility.error("-1", null, "其他错误,[" + key + "]不能为空!");
        }
        return result;
    }

    /**
     * IDataset转IData 把IDataset对应每条记录的key对应的value作为IData的key, 整条记录的IData加一个objvaluekey 字段(字段的值为该记录sourceValueKey字段的值)
     * 
     * @param dataset
     * @param key
     * @param sourceValueKey
     * @param objValueKey
     * @return
     * @throws Exception
     */
    public static IData hTable2STable(IDataset dataset, String key, String sourceValueKey, String objValueKey) throws Exception
    {

        IData map = new DataMap();
        Iterator<Object> iterator = dataset.iterator();
        while (iterator.hasNext())
        {
            IData rowData = (IData) iterator.next();
            rowData.put(objValueKey, rowData.getString(sourceValueKey, ""));
            map.put(rowData.getString(key), rowData);
        }
        return map;
    }

    /**
     * IDataset转IData 把IDataset对应每条记录的key字段的值作为IData的key, 把sourceValueKey字段的值为key对应的data值)
     * 
     * @param dataset
     * @param key
     * @param sourceValueKey
     * @return
     * @throws Exception
     */
    public static IData hTable2StdSTable(IDataset dataset, String key, String sourceValueKey) throws Exception
    {

        IData map = new DataMap();
        Iterator<Object> iterator = dataset.iterator();
        while (iterator.hasNext())
        {
            IData rowData = (IData) iterator.next();
            String value = rowData.getString(sourceValueKey, "");
            map.put(rowData.getString(key), value);
        }
        return map;
    }

    /**
     * 提供一个将IData转换成IDataset的方法。 IDataset的每条记录都是一个DataMap，它有两条记录：
     * 第一条将IData的key作为值、传入的newKey作为键；第二条将IData的value作为值、传入的newValue作为键
     * 
     * @param data
     * @param key
     * @param value
     * @return
     * @throws Exception
     */
    public static IDataset iData2iDataset(IData data, String newKey, String newValue) throws Exception
    {

        IDataset dataset = new DatasetList();
        Iterator iterator = data.keySet().iterator();
        while (iterator.hasNext())
        {
            Object objkey = iterator.next();
            IData map = new DataMap();
            map.put(newKey, objkey);
            map.put(newValue, data.get(objkey));
            dataset.add(map);
        }
        return dataset;
    }

    /**
     * 提供将一个IData 封装为一个IData(a)包含另一个IData(b)的形式,以实现页面的统一展现需要 其中IData(a)的key为 IData的key,新的IData(b)构造为key=方法中的valuekey
     * value=IData.get(IData.key)
     * 
     * @param data
     *            需要转换的IData
     * @param valuekey
     *            转换后新的IData(b)对应的key
     */
    public static IData iDataA2iDataB(IData data, String valuekey) throws Exception
    {

        IData returndata = new DataMap();
        Iterator iterator = data.keySet().iterator();
        while (iterator.hasNext())
        {
            Object datakey = iterator.next();
            IData datainfo = new DataMap();
            datainfo.put(valuekey, data.get(datakey));
            returndata.put((String) datakey, datainfo);
        }
        return returndata;
    }

    /**
     * IData转换成IDataset
     * 
     * @author tengg 2012-2-16
     * @param object
     * @return
     * @throws Exception
     */
    public final static IDataset idToIds(Object object) throws Exception
    {

        if (object instanceof IData)
        {
            IDataset dataset = new DatasetList();

            if (IDataUtil.isEmpty((IData) object))
            {
                return dataset;
            }

            dataset.add(object);

            return dataset;
        }
        else if (object instanceof IDataset)
        {
            return (IDataset) object;
        }
        else
        {
            return null;
        }
    }

    public final static boolean isEmpty(IData data)
    {

        if (data == null || data.isEmpty())
        {
            return true;
        }

        return false;
    }

    public final static boolean isEmpty(IDataset dataset)
    {

        if (dataset == null || dataset.isEmpty())
        {
            return true;
        }

        return false;
    }

    public final static boolean isNotEmpty(IData data)
    {

        return !isEmpty(data);
    }

    public final static boolean isNotEmpty(IDataset ids)
    {

        return !isEmpty(ids);
    }

    public final static boolean isNull(IData data)
    {

        return data == null ? true : false;
    }

    public final static boolean isNull(IDataset dataset)
    {

        return dataset == null ? true : false;
    }

    /**
     * 判断一个itemaIdataset(对应表TD_B_ATTR_ITEMA结构)内的 Idata内容的ID字段是否包含指定的id
     * 
     * @param dataset
     * @param id
     * @return
     * @throws Exception
     */
    public static boolean itemadatasetContainsId(IDataset dataset, String id) throws Exception
    {

        Iterator iterator = dataset.iterator();
        while (iterator.hasNext())
        {
            IData data = (IData) iterator.next();
            if (id.equals(data.getString("ID")))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 将map中的IDataset、IDataMap字符串转化为对象
     * 
     * @param time
     * @param format
     * @return
     * @throws Exception
     * @author chenlei
     */
    public static IData mapValue2Object(IData map) throws Exception
    {

        Iterator iterator = map.keySet().iterator();
        while (iterator.hasNext())
        {
            Object datakey = iterator.next();
            IData datainfo = new DataMap();
            String value = map.getString(datakey.toString());
            if ((value.indexOf("[") >= 0 && value.indexOf("]") >= 0))
            {
                map.put((String) datakey, new DatasetList(value));
            }
            else if (value.indexOf("{") >= 0 && value.indexOf("}") >= 0)
            {
                map.put((String) datakey, new DataMap(value));
            }
        }
        return map;
    }

    /**
     * 判断对象是否为IDataset类型 如果是string类型则转换成IDataset类型
     * 
     * @param obj
     * @param key
     * @throws Exception
     * @author chenlei
     */
    public static IDataset modiIDataset(Object obj, String key) throws Exception
    {

        if (obj instanceof String)
        {
            IDataset tmp = new DatasetList();
            tmp.add(obj);
            obj = tmp;
        }
        else if (obj instanceof JSONArray)
        {
            IDataset tmp = new DatasetList();
            tmp.addAll((Collection<? extends Object>) obj);
            obj = tmp;
        }

        if (!(obj instanceof IDataset))
        {

            String message = "必须为多条IDataset类型,key=[" + key + ']';
            Utility.error("-1", null, message);
        }
        return (IDataset) obj;
    }

    /**
     * 字段非空校验
     * 
     * @param tableName
     * @param FieldName
     * @param map
     * @return
     * @throws Exception
     */
    public static String putMandaData(IData data, String tableName, Boolean bManda, String fieldName, IData map) throws Exception
    {
        String value = map.getString(fieldName, "");

        return putMandaData(data, tableName, bManda, fieldName, value);
    }

    /**
     * 字段非空校验
     * 
     * @param tableName
     * @param FieldName
     * @param map
     * @return
     * @throws Exception
     */
    public static String putMandaData(IData data, String tableName, Boolean bManda, String fieldName, String value) throws Exception
    {

        if (bManda && StringUtils.isBlank(value))
        {

            String message = "表[" + tableName + "]字段[" + fieldName + "]不能为空!";
            Utility.error("-1", null, message);
        }

        data.put(fieldName, value);

        return value;
    }

    /**
     * 转换一个IData的key,往原有的key上拼一个前缀
     * 
     * @author tengg 2012-2-16
     * @param data
     * @param prefix
     * @return
     * @throws Exception
     */
    public static IData replaceIDataKeyAddPrefix(IData data, String prefix) throws Exception
    {

        IData map = new DataMap();

        Iterator<String> iterator = data.keySet().iterator();
        while (iterator.hasNext())
        {
            String key = iterator.next();
            map.put(prefix + key, data.get(key));
        }
        return map;
    }

    /**
     * 转换一个IData的key,去掉原有的key的某个前缀
     * 
     * @author tengg 2012-2-16
     * @param data
     * @param prefix
     * @return
     * @throws Exception
     */
    public static IData replaceIDataKeyDelPrefix(IData data, String prefix) throws Exception
    {

        IData map = new DataMap();
        Iterator<String> iterator = data.keySet().iterator();
        while (iterator.hasNext())
        {
            String key = iterator.next();
            map.put(key.replaceFirst(prefix, ""), data.get(key));
        }
        return map;
    }

    /**
     * 替换指定位置字符串
     * 
     * @param str
     *            被替换字符串
     * @param relacestr
     *            替换字符串
     * @param startidx
     *            开始位置下标 从1计
     * @param endidx
     *            结束位置下标(不含结束下标位置字符)从1计
     * @return String 返回替换后字符串
     * @author zouli
     */
    public static String replacStrByint(String sourceString, String replaceStr, int startidx, int endidx) throws Exception
    {

        StringBuilder str = new StringBuilder(sourceString);
        try
        {
            str.replace(startidx - 1, endidx, replaceStr);
        }
        catch (Exception e)
        {

            String message = "字符串替换失败：用" + replaceStr + "替换" + sourceString + "的第" + startidx + "个字符到第" + endidx + "个字符";
            Utility.error("-1", null, message);
        }

        return str.toString();
    }

    /**
     * 按包ID来存放产品元素
     * 
     * @param source
     * @param key
     * @author xiajj
     */
    public static IDataset sortProductElement(IDataset source, String key) throws Exception
    {

        IData data = new DataMap();
        for (int i = 0; i < source.size(); i++)
        {
            IData map = source.getData(i);
            String keyValue = map.getString(key, "");
            IDataset storyDataset = data.getDataset(keyValue);
            if (storyDataset == null)
            {
                storyDataset = new DatasetList();
                data.put(keyValue, storyDataset);
            }
            storyDataset.add(map);
        }
        IDataset result = new DatasetList();
        Iterator it = data.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry) it.next();
            IDataset value = (IDataset) entry.getValue();
            result.addAll(value);
        }
        return result;
    }
    
    /**
     * 
     * @Title: sourceAddKeyAndValue  
     * @Description: 为里面的每一个idata新增一个键值对
     * @param @param source
     * @param @param key
     * @param @param value
     * @param @return
     * @param @throws Exception    设定文件  
     * @return IDataset    返回类型  
     * @throws
     */
    public static IDataset sourceAddKeyAndValue(IDataset source, String key, String value) throws Exception
    {
        if (isEmpty(source))
        {
            return source;
        }
        for (int i = 0, size = source.size(); i < size; i++)
        {
            source.getData(i).put(key, value);
        }
        
        return source;
    }
    
    /**
     * 
     * @Title: removeFilter  
     * @Description: 把满足条件的过滤出去
     * @param @param source
     * @param @param filter
     * @param @return
     * @param @throws Exception    设定文件  
     * @return IDataset    返回类型  
     * @throws
     */
    public static final IDataset removeFilter(IDataset source, String filter) throws Exception
    {
        if (isEmpty(source))
        {
            return source;
        }
        if (null == filter || filter.length() == 0)
            return source;
        IData ftdt = new DataMap();
        String fts[] = filter.split(",");
        for (int i = 0, size = fts.length; i < size; i++)
        {
            String ft[] = fts[i].split("=");
            ftdt.put(ft[0], ft[1]);
        }

        for (int i = source.size() - 1; i >= 0; i--)
        {
            IData subdata = source.getData(i);
            boolean include = true;
            String ftdtNames[] = ftdt.getNames();
            int j = 0;
            int nameSize = ftdtNames.length;
            do
            {
                if (j >= nameSize)
                    break;
                String subvalue = (String) subdata.get(ftdtNames[j]);
                if (subvalue == null || !subvalue.equals(ftdt.get(ftdtNames[j])))
                {
                    include = false;
                    break;
                }
                j++;
            }
            while (true);
            if (include)
                source.remove(i);
        }

        return source;
    }
    
    public static void insertIDatasetItem(IDataset input,String key ,String value) throws Exception
    { 
        for (Iterator iterator = input.iterator(); iterator.hasNext();)
        {
            IData object = (IData) iterator.next();
            object.put(key, value);
            
        }
    }
    
    public static IDataset chkParamDataset(IData data, String strColName) throws Exception
    {
        IDataset dataset = data.getDataset(strColName);

        if (IDataUtil.isEmpty(dataset))
        {
            String strError = "接口参数检查: 输入参数[" + strColName + "]不存在或者参数值为空";

            Utility.error("-1", null, strError);
        }

        return dataset;
    }
  /**
     * 
     * @description 递归排序IData 按key排序 空字段不参与排序
     * @param @param map
     * @param @return
     * @return Map
     * @author tanzheng
     * @date 2019年5月30日
     * @param map
     * @return
     */
    public static Map sortIData(IData map) {
        final Map<String, Object> sortMap = new TreeMap<String, Object>(new Comparator<String>() {
            @Override
            public int compare(final String str1, final String str2) {
                return str1.compareTo(str2);
            }
        });
    	
        for(String key : map.keySet()){
        	if(map.get(key) instanceof IData){
        		sortMap.put(key, sortIData((IData)map.get(key)));
        	}else{
        		if(StringUtils.isNotBlank(map.getString(key))){
        			sortMap.put(key, map.get(key));
            	}
        	}
        }
		return sortMap;
	}
    
}
