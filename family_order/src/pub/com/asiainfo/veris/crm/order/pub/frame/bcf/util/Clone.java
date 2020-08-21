
package com.asiainfo.veris.crm.order.pub.frame.bcf.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class Clone
{

    /**
     * 深度克隆
     * 
     * @param srcObj
     * @return
     * @throws Exception
     */
    public static Object deepClone(Object srcObj) throws Exception
    {

        Object cloneObj = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;

        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(srcObj);

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ois = new ObjectInputStream(bais);
            cloneObj = ois.readObject();

            baos.close();
            bais.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            oos.close();
            ois.close();
        }

        return cloneObj;
    }
}
