SELECT depart_id,
       sum(sale_num) sale_num,
       sum(fee) sale_money       
  FROM ts_s_pfee_day_staff
 WHERE depart_id=:DEPART_ID
   AND clct_day>=:START_DATE
   AND clct_day<=:END_DATE
   AND charge_source_code not in (select tag_number from td_s_tag 
                                  where tag_code='ASM_CAN_NOT_BANK_COOP'
                                  AND eparchy_code='0022')
 GROUP BY depart_id