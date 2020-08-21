select b.INST_ID,
       b.User_Id,
       b.Service_Id,
       b.Main_Tag,
       b.State_Code,
       TO_CHAR(b.START_DATE, 'YYYYMMDDHH24MISS'),
       TO_CHAR(b.END_DATE, 'YYYYMMDDHH24MISS'),
       TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS')
  from TF_F_USER_SVCSTATE b
 WHERE b.USER_ID = :USER_ID
   AND b.START_DATE < b.END_DATE
   AND SYSDATE < b.END_DATE
