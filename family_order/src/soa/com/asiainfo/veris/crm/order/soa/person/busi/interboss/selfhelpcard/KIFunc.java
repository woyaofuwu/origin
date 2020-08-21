
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.selfhelpcard;

public final class KIFunc
{
    private boolean desInited;

    static char pc2[] =
    { 14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21, 10, 23, 19, 12, 4, 26, 8, 16, 7, 27, 20, 13, 2, 41, 52, 31, 37, 47, 55, 30, 40, 51, 45, 33, 48, 44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29, 32 }; /*
                                                                                                                                                                                                 * pc2[
                                                                                                                                                                                                 * ]
                                                                                                                                                                                                 */

    static char totRot[] =
    { 1, 2, 4, 6, 8, 10, 12, 14, 15, 17, 19, 21, 23, 25, 27, 28 }; /* totRot[] */

    static char pc1[] =
    { 57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 27, 19, 11, 3, 60, 52, 44, 36,

    63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22, 14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 28, 20, 12, 4 };

    static byte[] fp =
    { 40, 8, 48, 16, 56, 24, 64, 32, 39, 7, 47, 15, 55, 23, 63, 31, 38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45, 13, 53, 21, 61, 29, 36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11, 51, 19, 59, 27, 34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 49, 17, 57,
            25 }; /* fp[] */

    static int[] byteBit =
    { 0200, 0100, 040, 020, 010, 04, 02, 01 };

    static int[] nibbleBit =
    { 010, 004, 002, 001 };

    static byte ip[] =
    { 58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 30, 22, 14, 6, 64, 56, 48, 40, 32, 24, 16, 8, 57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19, 11, 3, 61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15,
            7 }; /* ip[] */

    static char[] p32i =
    { 16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 23, 26, 5, 18, 31, 10, 2, 8, 24, 14, 32, 27, 3, 9, 19, 13, 30, 6, 22, 11, 4, 25 };

    static char[] si =
    // [8][64]
    {
    /* S1 */
    14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7, 0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8, 4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0, 15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13,
    /* S2 */
    15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10, 3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5, 0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15, 13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9,
    /* S3 */
    10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8, 13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1, 13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7, 1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12,
    /* S4 */
    7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15, 13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9, 10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4, 3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14,
    /* S5 */
    2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9, 14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6, 4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14, 11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3,
    /* S6 */
    12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11, 10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8, 9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6, 4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13,
    /* S7 */
    4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1, 13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6, 1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2, 6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12,
    /* S8 */
    13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7, 1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2, 7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8, 2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11 }; /*
                                                                                                                                                                                                                               * si[
                                                                                                                                                                                                                               * ]
                                                                                                                                                                                                                               */

    // int[] sp =new int[8*64];
    int[] sp = new int[8 * 64];

    char[] iperm = new char[16 * 16 * 8];

    char[] fperm = new char[16 * 16 * 8];

    char[] kn = new char[16 * 8];

    public KIFunc()
    {
        desInited = false;
    }

    void bytes2Int(int[] out, int outLen, byte[] in)
    {
        for (int i = 0; i < (outLen); i++)
        {
            out[i] = 0;
            out[i] = out[i] + in[i * 4 + 3];
            out[i] = out[i] + (in[i * 4 + 2] << 8);
            out[i] = out[i] + (in[i * 4 + 1] << 16);
            out[i] = out[i] + (in[i * 4 + 0] << 24);
        }
    }

    int ByteSwap(int x)
    {
        return x;
    } /* ByteSwap() */

    char C_H(char h1, char h2)
    {
        int x1, x2;
        x1 = 0;
        x2 = 0;
        if (h1 == '1')
            x1 = 1;
        if (h1 == '2')
            x1 = 2;
        if (h1 == '3')
            x1 = 3;
        if (h1 == '4')
            x1 = 4;
        if (h1 == '5')
            x1 = 5;
        if (h1 == '6')
            x1 = 6;
        if (h1 == '7')
            x1 = 7;
        if (h1 == '8')
            x1 = 8;
        if (h1 == '9')
            x1 = 9;
        if (h1 == '0')
            x1 = 0;
        if ((h1 == 'a') || (h1 == 'A'))
            x1 = 10;
        if ((h1 == 'b') || (h1 == 'B'))
            x1 = 11;
        if ((h1 == 'c') || (h1 == 'C'))
            x1 = 12;
        if ((h1 == 'd') || (h1 == 'D'))
            x1 = 13;
        if ((h1 == 'e') || (h1 == 'E'))
            x1 = 14;
        if ((h1 == 'f') || (h1 == 'F'))
            x1 = 15;

        if (h2 == '1')
            x2 = 1;
        if (h2 == '2')
            x2 = 2;
        if (h2 == '3')
            x2 = 3;
        if (h2 == '4')
            x2 = 4;
        if (h2 == '5')
            x2 = 5;
        if (h2 == '6')
            x2 = 6;
        if (h2 == '7')
            x2 = 7;
        if (h2 == '8')
            x2 = 8;
        if (h2 == '9')
            x2 = 9;
        if (h2 == '0')
            x2 = 0;
        if ((h2 == 'a') || (h2 == 'A'))
            x2 = 10;
        if ((h2 == 'b') || (h2 == 'B'))
            x2 = 11;
        if ((h2 == 'c') || (h2 == 'C'))
            x2 = 12;
        if ((h2 == 'd') || (h2 == 'D'))
            x2 = 13;
        if ((h2 == 'e') || (h2 == 'E'))
            x2 = 14;
        if ((h2 == 'f') || (h2 == 'F'))
            x2 = 15;

        return (char) (x1 * 16 + x2);
    }

    void char2Int(int[] out, int outLen, char[] in)
    {
        for (int i = 0; i < (outLen); i++)
        {
            out[i] = 0;
            out[i] = out[i] + in[i * 4 + 0];
            out[i] = out[i] + (in[i * 4 + 1] << 8);
            out[i] = out[i] + (in[i * 4 + 2] << 16);
            out[i] = out[i] + (in[i * 4 + 3] << 24);
        }
    }

    String DecryptKI(char[] KI, char[] EKI)
    {
        char[] tmp1 = new char[9];
        char[] tmp2 = new char[9];
        char[] key1 = new char[9];
        char[] key2 = new char[9];

        key1[0] = 'l';
        key1[1] = 'i';
        key1[2] = 'n';
        key1[3] = 'k';
        key1[4] = 'a';
        key1[5] = 'g';
        key1[6] = 'e';
        key1[7] = 'K';
        key1[8] = 0;
        key2[0] = 'I';
        key2[1] = 'l';
        key2[2] = 'i';
        key2[3] = 'n';
        key2[4] = 'k';
        key2[5] = 'a';
        key2[6] = 'g';
        key2[7] = 'e';
        key2[8] = 0;

        for (int i = 0; i < 8; i++)
            tmp1[i] = C_H(EKI[i * 2], EKI[i * 2 + 1]);

        for (int i = 0; i < 8; i++)
            tmp2[i] = C_H(EKI[16 + i * 2], EKI[16 + i * 2 + 1]);

        DesDecrypt(key1, tmp1);
        DesDecrypt(key2, tmp2);

        String sEK = "";
        for (int i = 0; i < 8; i++)
        {
            if (tmp1[i] < 0x10)
            {
                sEK = sEK + "0";
            }
            sEK = sEK + Integer.toHexString(tmp1[i]).toUpperCase();
        }
        for (int i = 0; i < 8; i++)
        {
            if (tmp2[i] < 0x10)
            {
                sEK = sEK + "0";
            }
            sEK = sEK + Integer.toHexString(tmp2[i]).toUpperCase();
        }
        return sEK;
    }

    /**
     * 作用：KI解密
     * 
     * @param EKI
     * @return
     */
    public String DecryptKI(String EKI)
    {
        String sDeKey = EKI;
        String sDeData = EKI;
        char[] szDeKey = new char[sDeKey.getBytes().length];
        sDeKey.getChars(0, sDeKey.getBytes().length, szDeKey, 0);
        char[] szDeData = new char[sDeData.getBytes().length];
        sDeData.getChars(0, sDeData.getBytes().length, szDeData, 0);
        sDeData = DecryptKI(szDeKey, szDeData);
        return sDeData;
    }

    void DES(char[] key, char[] data, int doEncrypt)
    {
        int i;
        int[] work = new int[2]; /* Working data storage */
        int tmp;

        if (!desInited)
        {
            SPInit();
            PermInit(iperm, ip);
            PermInit(fperm, fp);
            desInited = true;
        }
        SetKey(key);
        char[] char_work = new char[9];
        int2Char(char_work, 9, work);
        Permute(data, iperm, char_work);
        char2Int(work, 2, char_work);
        if (doEncrypt != 0)
        {
            for (i = 0; i < 16; i++)
                Round(i, work);
            tmp = work[0];
            work[0] = work[1];
            work[1] = tmp;
        }
        else
        {
            tmp = work[0];
            work[0] = work[1];
            work[1] = tmp;
            for (i = 15; i >= 0; i--)
                Round(i, work);
        }
        work[0] = ByteSwap(work[0]);
        work[1] = ByteSwap(work[1]);
        int2Char(char_work, 9, work);
        Permute(char_work, fperm, data);
    }

    void DesDecrypt(char[] key, char[] data)
    {
        DES(key, data, 0);
    } /* DesDecrypt() */

    void DesEncrypt(char[] key, char[] data)
    {
        DES(key, data, 1);
    }

    String EncryptKI(char[] KI, char[] EKI)
    {
        char[] tmp1 = new char[9];
        char[] tmp2 = new char[9];
        char[] key1 = new char[9];
        char[] key2 = new char[9];

        key1[0] = 'l';
        key1[1] = 'i';
        key1[2] = 'n';
        key1[3] = 'k';
        key1[4] = 'a';
        key1[5] = 'g';
        key1[6] = 'e';
        key1[7] = 'K';
        key1[8] = 0;
        key2[0] = 'I';
        key2[1] = 'l';
        key2[2] = 'i';
        key2[3] = 'n';
        key2[4] = 'k';
        key2[5] = 'a';
        key2[6] = 'g';
        key2[7] = 'e';
        key2[8] = 0;

        for (int i = 0; i < 8; i++)
            tmp1[i] = C_H(KI[i * 2], KI[i * 2 + 1]);

        for (int i = 0; i < 8; i++)
            tmp2[i] = C_H(KI[16 + i * 2], KI[16 + i * 2 + 1]);

        DesEncrypt(key1, tmp1);
        DesEncrypt(key2, tmp2);

        String sEKI = "";
        for (int i = 0; i < 8; i++)
        {
            if (tmp1[i] < 0x10)
            {
                sEKI = sEKI + "0";
            }
            sEKI = sEKI + Integer.toHexString(tmp1[i]).toUpperCase();
        }
        for (int i = 0; i < 8; i++)
        {
            if (tmp2[i] < 0x10)
            {
                sEKI = sEKI + "0";
            }
            sEKI = sEKI + Integer.toHexString(tmp2[i]).toUpperCase();
        }
        return sEKI;

    }

    /**
     * 作用：KI加密
     * 
     * @param KI
     * @return
     */
    public String EncryptKI(String KI)
    {
        String sKey = KI;
        String sData = KI;
        char[] szKey = new char[sKey.getBytes().length];
        sKey.getChars(0, sKey.getBytes().length, szKey, 0);
        char[] szData = new char[sData.getBytes().length];
        sData.getChars(0, sData.getBytes().length, szData, 0);
        sData = EncryptKI(szKey, szData);
        return sData;
    }

    int F(int r, char[] subKey)
    {
        int rVal;
        long rt;
        int curS = 0;
        rt = (getUnsignedIntt(r) >> 1) | (0 != ((r & 1)) ? 0x80000000 : 0);
        ;
        rVal = 0;
        rVal |= sp[Integer.valueOf(String.valueOf(0 * 64 + (((rt >> 26) ^ subKey[0]) & 0x3f)))];
        curS++;
        rVal |= sp[Integer.valueOf(String.valueOf(1 * 64 + (((rt >> 22) ^ subKey[1]) & 0x3f)))];
        curS++;
        rVal |= sp[Integer.valueOf(String.valueOf(2 * 64 + (((rt >> 18) ^ subKey[2]) & 0x3f)))];
        curS++;
        rVal |= sp[Integer.valueOf(String.valueOf(3 * 64 + (((rt >> 14) ^ subKey[3]) & 0x3f)))];
        curS++;
        rVal |= sp[Integer.valueOf(String.valueOf(4 * 64 + (((rt >> 10) ^ subKey[4]) & 0x3f)))];
        curS++;
        rVal |= sp[Integer.valueOf(String.valueOf(5 * 64 + (((rt >> 6) ^ subKey[5]) & 0x3f)))];
        curS++;
        rVal |= sp[Integer.valueOf(String.valueOf(6 * 64 + (((rt >> 2) ^ subKey[6]) & 0x3f)))];
        curS++;
        rt = (r << 1) | ((0 != (r & 0x80000000)) ? 1 : 0);
        rVal |= sp[Integer.valueOf(String.valueOf(7 * 64 + ((rt ^ subKey[7]) & 0x3f)))];
        return rVal;
    }/* F() */

    public long getUnsignedIntt(int data)
    { // 将int数据转换为0~4294967295 (0xFFFFFFFF即DWORD)。
        return data & 0x0FFFFFFFFl;
    }

    void int2Bytes(byte[] out, int outLen, int[] in)
    {
        for (int i = 0; i < (outLen / 4); i++)
        {
            out[i * 4 + 3] = Byte.parseByte(String.valueOf(in[i] & 0x000000FF));
            out[i * 4 + 2] = Byte.parseByte(String.valueOf((in[i] & 0x0000FF00) >> 8));
            out[i * 4 + 1] = Byte.parseByte(String.valueOf((in[i] & 0x00FF0000) >> 16));
            out[i * 4 + 0] = Byte.parseByte(String.valueOf((in[i] & 0xFF000000) >> 24));
        }
    }

    void int2Char(char[] out, int outLen, int[] in)
    {
        for (int i = 0; i < (outLen / 4); i++)
        {
            out[i * 4 + 0] = (char) (in[i] & 0x000000FF);
            out[i * 4 + 1] = (char) ((in[i] & 0x0000FF00) >> 8);
            out[i * 4 + 2] = (char) ((in[i] & 0x00FF0000) >> 16);
            out[i * 4 + 3] = (char) ((in[i] & 0xFF000000) >> 24);
        }
    }

    void long2Char(char[] out, long outLen, long[] in)
    {
        for (int i = 0; i < (outLen / 4); i++)
        {
            out[i * 4 + 3] = (char) (in[i] & 0x000000FF);
            out[i * 4 + 2] = (char) ((in[i] & 0x0000FF00) >> 8);
            out[i * 4 + 1] = (char) ((in[i] & 0x00FF0000) >> 16);
            out[i * 4 + 0] = (char) ((in[i] & 0xFF000000) >> 24);
        }
    }

    void PermInit(char[] perm, byte[] p)
    {
        int i, j, k, m, n;
        /* Clear the permutation array */
        for (i = 0; i < 16 * 16 * 8; i++)
        {
            perm[i] = 0;
        } /* end of for i */

        for (i = 0; i < 16; i++)
        {
            for (j = 0; j < 16; j++)
            {
                for (k = 0; k < 64; k++)
                {
                    n = p[k] - 1;
                    if ((n >> 2) != i)
                        continue;
                    if ((j & nibbleBit[n & 3]) == 0)
                        continue;
                    m = k & 07; /* Which bit is this in the byte */
                    perm[i * 16 * 8 + j * 8 + (k >> 3)] |= byteBit[m];
                } /* end of for k */
            } /* end of for j */
        } /* end of for i */
        return;
    } /* PermInit() */

    void Permute(char[] inIntBlock, char perm[], char[] outBlock)
    {
        int curOut = 0, curIn = 0;
        int i;
        int j;
        char[] p = new char[8];
        char[] q = new char[8];
        // char[] oBlock; int curO=0;
        if (perm == null)
        {
            /* No permutation, just copy */
            for (i = 0; i < 8; i++)
            {
                outBlock[curOut] = inIntBlock[curIn];
                curOut++;
                curIn++;
            }
            return;
        }

        // memset(outBlock, 0, 8);
        for (i = 0; i < 8; i++)
        {
            outBlock[i] = 0;
        }
        for (j = 0; j < 16; j += 2)
        {
            curOut = 0;
            /* For each input nibble and each output byte, OR the masks together */
            for (int z = 0; z < 8; z++)
            {
                p[z] = (char) perm[j * 16 * 8 + ((inIntBlock[curIn] >> 4) & 017) * 8 + z];
            }
            for (int z = 0; z < 8; z++)
            {
                q[z] = (char) perm[(j + 1) * 16 * 8 + (inIntBlock[curIn] & 017) * 8 + z];
            }

            for (i = 0; i < 8; i++)
            {
                outBlock[i] |= p[i] | q[i];
            }
            curIn++;
        } /* end of for j */
        return;
    } /* Permute() */

    void Round(int num, int[] block)
    {
        char[] knParam = new char[8];
        for (int i = 0; i < 8; i++)
        {
            knParam[i] = kn[(int) num * 8 + i];
        }
        if (0 != (num & 1))
        {
            block[1] ^= F(block[0], knParam);
        }
        else
        {
            block[0] ^= F(block[1], knParam);
        }
        return;
    } /* Round() */

    void SetKey(char[] key)
    {
        byte[] pc1m = new byte[56]; /* Place to modify pc1 into */
        byte[] pcr = new byte[56]; /* Place to rotate pc1 into */
        int i, j, k;
        int m;

        // 本处有段废弃代码

        /* Clear key schedule */
        for (i = 0; i < 16; i++)
        {
            for (j = 0; j < 8; j++)
                kn[i * 8 + j] = 0;
        } /* end of for i */

        /* Convert pc1 to bits of key */
        for (j = 0; j < 56; j++)
        {
            k = pc1[j] - 1; /* Integer bit location */
            m = k & 07; /* find bit */

            /*
             * Find which key byte l is in and which bit of that byte and store 1-bit result
             */
            if ((key[k >> 3] & byteBit[m]) != 0)
            {
                pc1m[j] = 1;
            }
            else
            {
                pc1m[j] = 0;
            }
            // pc1m[j] = ((key[k >> 3] & byteBit[m])) ? char(1) : char(0);
        } /* end of for j */

        /* Key chunk for each iteration */
        for (i = 0; i < 16; i++)
        {
            /* rotate pc1 the right amount */
            for (j = 0; j < 56; j++)
            {
                k = j + totRot[i];
                pcr[j] = pc1m[(k < (j < 28 ? 28 : 56)) ? k : k - 28];
            } /* end of for j */

            /* Rotate left and right halves independently */
            for (j = 0; j < 48; j++)
            {
                /* Select bits individually, check bit that goes to kn[j] */
                if (pcr[pc2[j] - 1] != 0)
                {
                    /* mask it in if it's there */
                    k = j % 6;
                    kn[i * 8 + j / 6] |= byteBit[k] >> 2;
                } /* end of if */
            } /* end of j */
        } /* end of for i */

        return;
    } /* SetKey() */

    void SPInit()
    {
        char[] pBox = new char[32];
        int p, i, s, j, rowCol;
        int val;
        /* Compute pbox, the inverse of p32i. This is easier to work with. */
        for (p = 0; p < 32; p++)
        {
            for (i = 0; i < 32; i++)
            {
                if (p32i[i] - 1 == p)
                {
                    pBox[p] = (char) i;
                    break;
                }
            }
        }
        for (s = 0; s < 8; s++)
        {
            for (i = 0; i < 64; i++)
            {
                val = 0;
                rowCol = (i & 32) | ((1 == (i & 1)) ? 16 : 0) | ((i >> 1) & 0xf);
                for (j = 0; j < 4; j++)
                {
                    if (0 < (si[s * 64 + rowCol] & (8 >> j)))
                        val |= 1L << (31 - pBox[4 * s + j]);
                }
                sp[s * 64 + i] = val;
            }
        }
        return;
    }
}
