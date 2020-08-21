--IS_CACHE=N
SELECT DISTINCT A.SERVICE_ID,
                A.GIFT_SERIAL_NUMBER,                
                A.START_DATE,
                A.END_DATE,
                A.BIZ_STATE_CODE,                
                A.REMARK,
                A.UPDATE_STAFF_ID,
                A.UPDATE_DEPART_ID,
                A.UPDATE_TIME,
                A.PARTITION_ID,
                A.USER_ID,
                A.FIRST_DATE,
                A.FIRST_DATE_MON,
                A.GIFT_USER_ID
  FROM TF_F_USER_PLATSVC  A
  WHERE 1= 1
   AND A.USER_ID = :USER_ID
   AND A.PARTITION_ID = MOD(:USER_ID, '10000')
   AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE