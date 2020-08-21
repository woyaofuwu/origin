SELECT prevaluec1,
       prevaluec2,
       prevaluec3,
       prevaluec4 
  FROM ts_r_formis_day_depart a
 WHERE a.clct_day = :clct_day
   AND a.depart_id = :depart_id
   AND a.day_tag = :day_tag 
 ORDER BY item_order,prevaluec1