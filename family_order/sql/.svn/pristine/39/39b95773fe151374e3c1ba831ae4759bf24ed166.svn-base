SELECT depart_id,
       clct_day,
       sum(sale_num) sale_num,
       sum(fee) sale_money
  FROM ts_s_tfee_day_staff
 WHERE clct_day>=:START_DATE
   AND clct_day<=:END_DATE
   AND depart_id=:DEPART_ID
 GROUP BY depart_id,clct_day