package com.asiainfo.veris.crm.order.pub.util;

import java.util.List;

public class ArrayUtil
{

    public static boolean isEmpty(List args)
    {
        if (args == null || args.size() == 0)
        {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(String[] args)
    {
        if (args == null || args.length == 0)
        {
            return true;
        }
        return false; 
    }

    public static boolean isEmpty(Object[] args)
    {
        if (args == null || args.length == 0)
        {
            return true;
        }
        return false;
    }

    public static boolean isNotEmpty(Object[] args)
    {
        return !isEmpty(args);
    }

    public static boolean isNotEmpty(List args)
    {
        return !isEmpty(args);
    }

    public static boolean checkItemType(List args, Class c)
    {
        boolean fix = true;
        if (ArrayUtil.isEmpty(args))
        {
            return true;
        }

        for (int i = 0, size = args.size(); i < size; i++)
        {
            Object value = args.get(i);
            if (c == String.class)
            {
                if (!(value instanceof String))
                {
                    fix = false;
                    break;
                }
            }
            else if (!(c.isAssignableFrom(value.getClass())))
            {
                fix = false;
                break;
            }
        }
        return fix;
    }
}
