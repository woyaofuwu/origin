
package com.asiainfo.veris.crm.order.soa.frame.bcf.demotest;

public final class LyMain
{

    public static String getLineInfo()
    {

        StackTraceElement[] steZ = new Throwable().getStackTrace();
        StackTraceElement ste = steZ[1];
        return "SQLParser:ClassName=[" + ste.getClassName() + "]MethodName=[" + ste.getMethodName() + "]FileLine=[" + ste.getLineNumber() + "]";
    }

    public static void getUserDiscntInfoByAB(String errDesc)
    {

        errDesc = "dfa";
    }

    public static void main(String args[])
    {

        String errDesc = "123";

        System.out.println(errDesc);

        getUserDiscntInfoByAB(errDesc);
        System.out.println(errDesc);
    }

    public static void mainf(String[] args) throws Exception
    {
        int iIndex = 1000;
        int iCount = 10000;

        double rate = 0.0D;
        rate = iIndex * 100 / iCount;

        String srate = String.valueOf(rate);
        srate = srate.substring(0, srate.length() - 2);
    }
}
