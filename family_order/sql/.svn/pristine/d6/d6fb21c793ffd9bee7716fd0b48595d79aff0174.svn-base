select 
   	t.RULE_ID,
   	t.RULE_NAME,
   	t.TABLE_NAME,
   	t.RIGHT_CODE,
   	to_char(trunc(t.START_DATE,'dd'),'yyyy-mm-dd') START_DATE,
    to_char(trunc(t.END_DATE,'dd'),'yyyy-mm-dd') END_DATE
    from TD_B_PARAM_RULE t
    where 1=1
        and sysdate < t.END_DATE
        and sysdate >= t.START_DATE
        and  (:RULE_NAME is null or t.RULE_NAME like ('%'||:RULE_NAME||'%'))
        and  (:TABLE_NAME is null or t.TABLE_NAME like ('%'||:TABLE_NAME||'%'))