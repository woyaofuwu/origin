SELECT TO_CHAR(A.USER_ID) USER_ID,
       A.SERVICE_ID,
       A.SERIAL_NUMBER,
       A.INFO_CODE,
       DECODE(A.INFO_CODE,'WLAN_NPWD',F_DECRYPT(A.INFO_VALUE),A.INFO_VALUE) INFO_VALUE,
       A.RSRV_NUM1,
       A.RSRV_NUM2,
       A.RSRV_STR1,
       A.RSRV_STR2,
       A.UPDATE_TIME
  FROM TF_F_USER_PLATSVC_ATTR A
 WHERE  A.USER_ID = TO_NUMBER(:USER_ID)
   AND A.SERVICE_ID = :SERVICE_ID
   AND A.INFO_CODE = :INFO_CODE
   AND A.INFO_VALUE IS NOT NULL
   AND A.INFO_VALUE <> '-1'