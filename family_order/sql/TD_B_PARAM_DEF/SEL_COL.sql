select 
        t.RULE_ID,
        t.TABLE_NAME,
        t.COL_NAME,
        t.COL_DESC,
        t.COL_VALUE,
        t.IS_FIXED,
        to_char(trunc(t.START_DATE,'dd'),'yyyy-mm-dd') START_DATE,
    		to_char(trunc(t.END_DATE,'dd'),'yyyy-mm-dd') END_DATE,
        t.RSRV_STR1,
        t.RSRV_STR2,
        t.RSRV_STR3,
        t.COL_TYPE,
        t.COL_NULLABLE,
        t.ORDERS
    from TD_B_PARAM_DEF t
    where 1=1
    	  and sysdate < t.end_date 
    	  and sysdate >= t.start_date
        and t.RULE_ID=:RULE_ID
        and t.TABLE_NAME=:TABLE_NAME
    order by t.IS_FIXED desc, t.orders asc