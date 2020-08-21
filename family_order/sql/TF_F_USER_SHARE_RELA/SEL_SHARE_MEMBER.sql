SELECT R.SHARE_ID,
       R.USER_ID_B,
       R.ROLE_CODE,
       R.EPARCHY_CODE,
       R.INST_ID,
       F.SERIAL_NUMBER,
       to_char(R.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       to_char(R.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       case
         when months_between(R.END_DATE, sysdate) > 1 then
          '取消'
       end OPERATION,
       'EXIST' ADD_TYPE,
       '流量共享' SHARE_RES,
	   DECODE(R.RSRV_TAG1,'1','统付','') RSRV_TAG1NAME,
	   R.RSRV_TAG1
  FROM TF_F_USER_SHARE_RELA R, TF_F_USER F
 WHERE F.USER_ID = R.USER_ID_B
   AND ROLE_CODE = '02'
   AND R.SHARE_ID =
       (SELECT max(SHARE_ID)
          FROM TF_F_USER_SHARE_RELA
         WHERE ROLE_CODE = '01'
           AND USER_ID_B = :USER_ID
           AND PARTITION_ID = Mod(:USER_ID, 10000)
           AND END_DATE > sysdate
           AND END_DATE >START_DATE)
   and R.END_DATE > sysdate
   and END_DATE > R.START_DATE