SELECT depart_id,
       sum(sale_num) sale_num,
       sum(fee) sale_money
  FROM ts_s_tfee_day_staff
 WHERE depart_id=:DEPART_ID
   AND clct_day>=:START_DATE
   AND clct_day<=:END_DATE
 GROUP BY depart_id