SELECT a.discnt_code element_id,
       b.para_code1 limit_day,
       to_char(a.start_date,'yyyy-mm-dd') start_date,
       to_char(a.end_date,'yyyy-mm-dd') end_date
       FROM TF_F_USER_DISCNT A,TD_S_COMMPARA B
       WHERE TO_NUMBER(TRIM(B.PARAM_CODE)) = A.DISCNT_CODE
       AND SYSDATE BETWEEN A.start_date AND A.end_date
       AND SYSDATE BETWEEN B.start_Date AND B.end_Date
       AND B.SUBSYS_CODE = 'CSM'
       AND B.PARAM_ATTR = 355
       AND (nvl(B.Para_Code3,'z') != 'y')
       AND A.user_id =:USER_ID
       AND EXISTS (SELECT 1 FROM TF_F_USER C
       WHERE C.USER_ID = A.USER_ID
       AND C.ACCT_TAG = '0')