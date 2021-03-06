select B.RECORDID,
       B.MOBILENO SERIAL_NUMBER,
       B.IMEI,
       B.MATERIELCODE,
       B.PACKAGECODE,
       TO_CHAR(B.ACTIVEDATE, 'yyyy-mm-dd HH24:MI:SS') ACTIVEDATE,
       B.DEALFLAG,
       C.PARA_CODE4 TERMINAL_TYPE,
       C.PARAM_NAME PACKAGE_NAME,
       C.PARA_CODE1 PRODUCT_ID,
       C.PARA_CODE2 PACKAGE_ID
  from TI_R_USRPACKAGEDATA_DOWN B, TD_S_COMMPARA C
 where 1 = 1
   and C.PARAM_ATTR = 2003
   and B.PACKAGECODE = C.PARAM_CODE
   and B.MATERIELCODE = C.PARA_CODE3
   and B.MOBILENO = :SERIAL_NUMBER
   and B.IMEI = :IMEI
   and B.PACKAGECODE = :PACKAGE_CODE
   and B.DEALFLAG = :DEAL_FLAG
   and B.ACTIVEDATE >= TO_DATE(:START_DATE, 'yyyy-mm-dd hh24:mi:ss')
   and B.ACTIVEDATE <= TO_DATE(:END_DATE, 'yyyy-mm-dd hh24:mi:ss') + 1
   and C.PARA_CODE4 = :TERMINAL_TYPE
 order by ACTIVEDATE desc