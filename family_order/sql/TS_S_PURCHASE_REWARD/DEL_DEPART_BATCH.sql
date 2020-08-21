DELETE FROM TS_S_PURCHASE_REWARD A
 WHERE a.clct_day = :clct_day
   AND a.depart_id = :depart_id
   AND (a.staff_id = :staff_id OR :staff_id = '%')