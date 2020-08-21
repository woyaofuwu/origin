--IS CACHE=Y
	SELECT count(1) 业务数
          FROM TF_F_USER_DISCNT D
         WHERE D.DISCNT_CODE = '6633'
           AND D.USER_ID = TO_NUMBER(:USER_ID)
           and d.partition_id = mod( to_number(:USER_ID),10000)
           AND D.END_DATE <=
               ADD_MONTHS(TRUNC(LAST_DAY(SYSDATE) + 1) - 1 / 86400, -1)
           AND D.END_DATE >
               ADD_MONTHS(TRUNC(LAST_DAY(SYSDATE) + 1) - 1 / 86400, -7)