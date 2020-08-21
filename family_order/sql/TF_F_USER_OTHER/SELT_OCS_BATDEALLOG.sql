SELECT ROWNUM ROW_NUM,TO_CHAR(subscribe_id) subscribe_id,TO_CHAR(trade_id) trade_id,serial_number,eparchy_code,TO_CHAR(user_id) user_id,DECODE(flag,'0','签约','1','去签约') flag,DECODE(dealtag,'0','未处理','3','成功','4','失败','其他') dealtag,deal_desc,staff_id,depart_id,TO_CHAR(accept_date,'YYYY-MM-DD HH24:MI:SS') accept_date,TO_CHAR(finish_time,'YYYY-MM-DD HH24:MI:SS') finish_time
FROM tl_b_ocs_batdeallog
WHERE accept_date BETWEEN TO_DATE(:START_DATE,'YYYYMMDD') AND TO_DATE(:END_DATE,'YYYYMMDD')+1
AND eparchy_code=:TRADE_EPARCHY_CODE
AND 1=1