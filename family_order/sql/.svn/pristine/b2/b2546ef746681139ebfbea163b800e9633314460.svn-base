SELECT ROWNUM ROW_NUM,TO_CHAR(subscribe_id) subscribe_id,TO_CHAR(trade_id) trade_id,serial_number,eparchy_code,DECODE(flag,'0','签约','1','去签约') flag,staff_id,depart_id,TO_CHAR(accept_date,'YYYY-MM-DD HH24:MI:SS') accept_date
FROM tf_b_ocs_batdeal
WHERE accept_date BETWEEN TO_DATE(:START_DATE,'YYYYMMDD') AND TO_DATE(:END_DATE,'YYYYMMDD')+1
AND eparchy_code=:TRADE_EPARCHY_CODE
AND 1=1