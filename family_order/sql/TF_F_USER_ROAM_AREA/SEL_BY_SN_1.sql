SELECT A.SERIAL_NUMBER,
       A.PARTITION_ID,
       A.MPROVINCE,
       A.MCITY,
       A.IN_DATE,
       A.REMARK,
       A.RSRV_NUM1,
       A.RSRV_NUM2,
       A.RSRV_NUM3,
       A.RSRV_NUM4,
       A.RSRV_NUM5,
       A.RSRV_STR1,
       A.RSRV_STR2,
       A.RSRV_STR3,
       A.RSRV_STR4,
       A.RSRV_STR5,
       A.RSRV_STR6,
       A.RSRV_STR7,
       A.RSRV_STR8,
       A.RSRV_STR9,
       A.RSRV_STR10,
       A.RSRV_DATE1,
       A.RSRV_DATE2,
       A.RSRV_DATE3,
       A.RSRV_TAG1,
       A.RSRV_TAG2,
       A.RSRV_TAG3
  FROM TF_F_USER_ROAM_AREA A
 WHERE A.SERIAL_NUMBER = :SERIAL_NUMBER
   AND A.PARTITION_ID = MOD(TO_NUMBER(:SERIAL_NUMBER), 10000)
   AND A.RSRV_DATE1 = TRUNC(SYSDATE - 1,'DD')
   AND A.RSRV_TAG1 IS NOT NULL