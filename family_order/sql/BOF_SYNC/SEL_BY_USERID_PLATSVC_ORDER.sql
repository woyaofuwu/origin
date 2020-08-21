SELECT b.User_Id,
       nvl(b.gift_User_Id, 0),
       nvl(b.gift_Serial_Number, 0),
       b.service_id,
       nvl(b.product_id, 0),
       b.Biz_State_Code,
       TO_CHAR(b.START_DATE, 'YYYYMMDDHH24MISS'),
       TO_CHAR(b.END_DATE, 'YYYYMMDDHH24MISS'),
       nvl(to_char(b.first_date, 'YYYYMMDDHH24miss'),
           to_char(sysdate, 'YYYYMMDDHH24miss')),
       b.Product_Id,
       b.Package_Id,
       TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'),
       b.Opr_source,
       b.RSRV_STR8,
       b.INST_ID
  FROM TF_F_USER_PLATSVC_TRACE b
 WHERE b.PARTITION_ID = MOD(:USER_ID, 10000)
   AND b.USER_ID = :USER_ID
   AND b.SERVICE_ID NOT IN
       (98008003, 98008004, 98008005, 98008013, 98009044)
   AND b.START_DATE < b.END_DATE
   AND SYSDATE < b.END_DATE
