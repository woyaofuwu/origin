package com.asiainfo.veris.crm.iorder.web.igroup.twopoleintf;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class SecrityDigest2
{

    private static final String ALGORITHM = "DES";

    private static SecretKey secretKey = null;

//    public static void main(String args[])
//    {
//        System.out.println(System.getProperty("user.dir"));
//        SecrityDigest2 sd = new SecrityDigest2();
////        String enc_result = sd.encrypt("sample text12345567", true, "E:\\asiainfo-linkage\\SVN\\j2ee_hnan\\dev\\ngboss\\apps\\order\\config\\security\\secretekey731.dat");
//        String urlPath = SecrityDigest2.class.getClassLoader().getResource("security/secretekey898.dat").getPath();
//        System.out.println(urlPath);
//        String path = SecrityDigest2.class.getClassLoader().getResource("").getPath();
//        System.out.println(path);
//        String filePath = path+"security/secretekey898.dat";
//        String enc_result = sd.encrypt("sample text12345567", true, filePath);
//        System.out.println("100 encrypt result=" + enc_result);
//        String dec_result = sd.decrypt(enc_result, true, filePath);
//        System.out.println("100 decrypt result=" + dec_result);
//
//        /* int[] ints = new int[]{100,200,210,220,230,240,250,270,280,290,311,351,371,431,451,471,531,551,571,591,731,771,791,851,871,891,898,931,951,971,991}; for(int i :ints){ String enc_result2=sd.encrypt("sample text12345567",
//         * true,"d:\\temp\\secretekey"+i+".dat"); System.out.println( i+" encrypt result="+enc_result2); String dec_result2=sd.decrypt(enc_result2, true,"d:\\temp\\secretekey"+i+".dat"); System.out.println( i+" decrypt result="+dec_result2); } */
//
//    }

    public String encrypt(String source, boolean local, String SERVERKEY)
    {
        String result = null;
        loadSecretKey(local, SERVERKEY);

        try
        {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] cipherByte = cipher.doFinal(source.getBytes());

            result = byte2hex(cipherByte);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * decrypt encryption to original plain text
     * 
     * @param source
     *            encryption
     * @param local
     *            if true, secrete key is store in file system, or it's in the classpath
     * @return original plain text
     */
    public String decrypt(String source, boolean local, String SERVERKEY)
    {
        String result = null;
        loadSecretKey(local, SERVERKEY);

        try
        {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] clearByte = cipher.doFinal(hex2byte(source));
            result = new String(clearByte);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * when we do encrypt/decrypt operation, we should read out the Secret key file at first
     * 
     * @param local
     *            if true, secrete key is store in file system, or it's in the classpath
     */
    private void loadSecretKey(boolean local, String SERVERKEY)
    {

        /**
         * if this program is not execute in container, i.e. local is true, we should confirm weather secretekey file exist or not, it it doesn't exist, we should generate it.
         */
        // if (local) {
        // generateKey("secretekey.dat");
        // }

        try
        {
            // ObjectInputStream is = new ObjectInputStream( ResourceLoader.getFileInputStream(SERVERKEY, local));
            // ObjectInputStream is = new ObjectInputStream( Common.getInstance().getClassResourceStream(SERVERKEY));
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(SERVERKEY));
            secretKey = (SecretKey) is.readObject();
            is.close();
        }
        catch (NullPointerException e)
        {
            StringBuffer msg = new StringBuffer();
            msg.append("Secret Key file can not be found, so: \r\n");
            msg.append("1: find and install the corresponding");
            msg.append(" keyFile.\r\n");
            msg.append("2: generate a new key file, encrypt the ");
            msg.append("password and set the configuration file ");
            msg.append("again. \r\n");
            System.out.println(msg.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * convert bytes to hex, the result is ideal to store and transfer.
     * 
     * @param bytes
     *            byte array
     * @return hex string
     */
    private String byte2hex(byte[] bytes)
    {
        String result = "";
        String temp = "";

        for (int i = 0; i < bytes.length; i++)
        {
            temp = (Integer.toHexString(bytes[i] & 0XFF));
            if (temp.length() == 1)
            {
                result += "0" + temp;
            }
            else
            {
                result += temp;
            }
        }

        return result.toUpperCase();
    }

    /**
     * convert hex to byte array, result byte array ready for calculation
     * 
     * @param bytes
     *            byte array
     * @return hex string
     */
    private byte[] hex2byte(String hex)
    {
        char[] arr = hex.toCharArray();
        byte[] b = new byte[hex.length() / 2];
        for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++)
        {
            String swap = "" + arr[i++] + arr[i];
            int byteint = Integer.parseInt(swap, 16) & 0xFF;
            b[j] = new Integer(byteint).byteValue();
        }
        return b;
    }

    /**
     * in DES arithmetic, we must store secret key we store the key in a dat file for following usage, in addition, we should use certain secret key!
     */
    public void generateKey(String fileName)
    {
        try
        {
            File keyFile = new File(fileName);
            if (!keyFile.exists())
            {
                KeyGenerator keygen = KeyGenerator.getInstance(ALGORITHM);
                SecretKey secretKey = keygen.generateKey();

                ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(new java.io.FileOutputStream(keyFile)));
                os.writeObject(secretKey);
                os.flush();
                os.close();
            }
            else
            {
                System.out.println("Secret Key file has exist.");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
