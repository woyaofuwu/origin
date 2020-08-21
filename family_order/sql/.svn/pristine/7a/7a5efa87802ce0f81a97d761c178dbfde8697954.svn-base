SELECT /*+ ordered use_nl(a b) */TO_CHAR(b.accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date,b.trade_depart_id,b.trade_staff_id,b.trade_type_code,
       a.brand_code,a.bcyc_id,a.score_type_code,a.score_changed,DECODE(b.cancel_tag,'0','','已返销') cancel_tag,
       TO_CHAR(b.cancel_date,'yyyy-mm-dd hh24:mi:ss') cancel_date,b.cancel_depart_id,b.cancel_staff_id,a.remark
  FROM ucr_crm1.tf_b_trade_score_new a,ucr_crm1.tf_bh_trade b
 WHERE a.serial_number = :SERIAL_NUMBER
   AND a.trade_id+0 = b.trade_id
   AND a.accept_month+0 = b.accept_month
   AND b.accept_date||NULL >= TO_DATE(:START_DATE,'yyyy-mm-dd hh24:mi:ss')
   AND b.accept_date||NULL <= TO_DATE(:END_DATE,'yyyy-mm-dd hh24:mi:ss')
   AND b.cancel_tag <> '2'
   AND (b.trade_type_code = :TRADE_TYPE_CODE OR :TRADE_TYPE_CODE = -1)
ORDER BY accept_date DESC,bcyc_id,score_type_code