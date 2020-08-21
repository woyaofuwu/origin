UPDATE TD_M_CORPORATION_SP
   SET SP_NAME = :SP_NAME,
       SP_TYPE = :SP_TYPE,
       SP_ATTR = :SP_ATTR,
       SERV_CODE = :SERV_CODE,
       IN_PROVINCE = :IN_PROVINCE,
       CON_PROVINCE = :CON_PROVINCE,
       PLAT_CODE = :PLAT_CODE,   
       START_DATE = to_date(:START_DATE,'yyyy-mm-dd'),
       END_DATE = to_date(:END_DATE,'yyyy-mm-dd'),
       SP_DESC = :SP_DESC,
       REMARK = :REMARK,
       UPDATE_TIME = sysdate,
       FILE_NAME = :FILE_NAME
 WHERE SP_CODE = :SP_CODE