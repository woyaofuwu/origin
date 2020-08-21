SELECT A.STAT_MONTH,
       A.PARTITION_ID,
       A.USER_ID,
       A.BRAND_CODE,
       A.PRODUCT_ID,
       A.EPARCHY_CODE,
       A.CITY_CODE,
       A.USER_TYPE_CODE,
       A.SERIAL_NUMBER,
       A.ACCT_TAG,
       TO_CHAR(A.IN_DATE, 'YYYY-MM-DD HH24:MI:SS') AS IN_DATE,
       TO_CHAR(A.OPEN_DATE, 'YYYY-MM-DD HH24:MI:SS') AS OPEN_DATE,
       A.REMOVE_TAG,
       A.DEVELOP_EPARCHY_CODE,
       A.DEVELOP_DEPART_ID,
       A.DEVELOP_STAFF_ID,
       A.DEVELOP_DATE,
       A.SIM_CARD_NO,
       A.SIM_TYPE_CODE,
       A.CARD_FEE,
       A.BACK_DATE,
       A.BACK_STAFF_ID,
       A.TAG,
       A.REMARK,
       A.VALUEN2 AS VALUEN1


  FROM TS_S_USER_BACK A , TD_M_DEPART B, TD_M_DEPARTKIND C  
 WHERE 1=1
      AND A.DEVELOP_DEPART_ID >=  ( SELECT DEPART_ID FROM   TD_M_DEPART      WHERE 1=1   AND DEPART_CODE=:START_AGENT_NO AND VALIDFLAG = 0 ) 
     AND A.DEVELOP_DEPART_ID <=  ( SELECT DEPART_ID FROM   TD_M_DEPART      WHERE 1=1   AND DEPART_CODE=:END_AGENT_NO AND VALIDFLAG = 0 ) 
     AND A.VALUEN2 IS NULL
   AND a.open_date >= to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss')
   AND a.open_date <= to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss')
   AND A.SERIAL_NUMBER = :SERIAL_NUMBER
   AND c.depart_kind_code LIKE '%' || :DEPART_KIND_CODE || '%'
   AND a.develop_depart_id = b.depart_id
   AND b.depart_kind_code = c.depart_kind_code
   AND A.TAG = '0'
   AND A.OPEN_DATE >= trunc(add_months(last_day(sysdate), -7)+1)
   AND A.OPEN_DATE <= SYSDATE


ORDER BY TO_NUMBER(A.STAT_MONTH) ASC