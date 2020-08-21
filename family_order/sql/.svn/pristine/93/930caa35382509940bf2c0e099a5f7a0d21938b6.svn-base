select 

        A.TRADE_ID,
        TO_CHAR(A.ACCEPT_DATE, 'yyyy-MM-dd HH24:MI:SS') ACCEPT_DATE,
        TO_CHAR(A.EXEC_TIME, 'yyyy-MM-dd HH24:MI:SS') EXEC_TIME,
        TO_CHAR(A.FINISH_DATE, 'yyyy-MM-dd HH24:MI:SS') FINISH_DATE,
        A.TRADE_DEPART_ID,
        A.TRADE_STAFF_ID,
        B.OPER_CODE
 from   ucr_crm1.tf_b_trade a,ucr_crm1.tf_b_trade_platsvc b
where a.trade_id=b.trade_id
and  a.accept_month=b.accept_month
and b.service_id=:SERVICE_ID
and a.serial_number=:SERIAL_NUMBER
and a.accept_date=(select  max(a1.accept_date) from   ucr_crm1.tf_b_trade a1,ucr_crm1.tf_b_trade_platsvc b1
where a1.trade_id=b1.trade_id
and  a1.accept_month=b1.accept_month
and b1.service_id=:SERVICE_ID
and a1.serial_number=:SERIAL_NUMBER)