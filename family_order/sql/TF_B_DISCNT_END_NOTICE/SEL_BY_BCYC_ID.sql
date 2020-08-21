SELECT bcyc_id,discnt_code,discnt_name,TO_CHAR(end_date,'yyyy-mm') end_date,
       NVL(discnt_explain,SUBSTR(discnt_name,1,40)) discnt_explain,
       deal_time,deal_staff_id,deal_depart_id,
       COUNT(*) field_name1,
       SUM(DECODE(deal_state,'0',1,0)) field_name2,
       SUM(DECODE(deal_state,'1',1,0)) field_name3,
       SUM(DECODE(deal_state,'2',1,0)) field_name4
  FROM tf_b_discnt_end_notice 
 WHERE bcyc_id = :BCYC_ID
   AND eparchy_code = :TRADE_EPARCHY_CODE
   AND TO_CHAR(end_date,'yyyy-mm') = :END_DATE
   AND deal_state IN ('0','1','2')
GROUP BY bcyc_id,discnt_code,discnt_name,TO_CHAR(end_date,'yyyy-mm'),
         NVL(discnt_explain,SUBSTR(discnt_name,1,40)),deal_time,deal_staff_id,deal_depart_id