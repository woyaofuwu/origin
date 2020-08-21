SELECT DECODE(F_CSB_ENCRYPT(:USER_PASSWD, A.USER_ID),
              A.USER_PASSWD,
              '0',
              DECODE(B.ENCRYPT_GENE,
                     NULL,
                     '-1',
                     DECODE(F_CSB_ENCRYPT(:USER_PASSWD, B.ENCRYPT_GENE),
                            A.USER_PASSWD,
                            '0',
                            '-1'))) RS
  FROM TF_F_USER A, TF_F_USER_ENCRYPT_GENE B
 WHERE A.USER_ID = B.USER_ID(+)
   AND A.PARTITION_ID = B.PARTITION_ID(+)
   AND A.USER_ID = :USER_ID
   AND A.PARTITION_ID=MOD(TO_NUMBER(:USER_ID),10000)