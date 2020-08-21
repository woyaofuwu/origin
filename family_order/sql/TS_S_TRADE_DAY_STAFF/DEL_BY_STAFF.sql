DELETE FROM TS_S_TRADE_DAY_STAFF
 WHERE clct_day = :clct_day
   AND staff_id >= :start_staff_id
   AND staff_id <= :end_staff_id