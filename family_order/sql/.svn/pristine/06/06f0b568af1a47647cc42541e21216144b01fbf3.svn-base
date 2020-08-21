select b.INST_ID,
       b.User_Id,
       b.Service_Id,
       Null,
       b.Main_Tag,
       '-1' PRODUCT_ID,
       '-1' PACKAGE_ID,
       TO_CHAR(b.START_DATE, 'YYYYMMDDHH24MISS'),
       TO_CHAR(b.END_DATE, 'YYYYMMDDHH24MISS'),
       TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS')
  from TF_F_USER_SVC b
 WHERE b.USER_ID = :USER_ID
   AND SYSDATE < b.END_DATE
   AND b.START_DATE < b.END_DATE