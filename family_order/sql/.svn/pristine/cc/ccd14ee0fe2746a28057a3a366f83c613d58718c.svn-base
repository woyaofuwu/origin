SELECT MIN(a.operate_id) operate_id
  FROM tf_b_trade_batdeal a,tf_b_trade_batdeal b
 WHERE b.operate_id = TO_NUMBER(:OPERATE_ID)
   AND b.serial_number||NULL = a.serial_number
   AND b.operate_id > a.operate_id+0
   AND (
           (:REPEAT_TAG = '1' AND b.batch_id+0 = a.batch_id+0)
           OR
	       (:REPEAT_TAG = '2' AND b.batch_task_id+0 = a.batch_task_id+0)
	   )
   AND (:DATA1 IS NULL OR (:DATA1 IS NOT NULL AND b.data1 = a.data1))
   AND (:DATA2 IS NULL OR (:DATA2 IS NOT NULL AND b.data2 = a.data2))
   AND (:DATA3 IS NULL OR (:DATA3 IS NOT NULL AND b.data3 = a.data3))
   AND (:DATA4 IS NULL OR (:DATA4 IS NOT NULL AND b.data4 = a.data4))
   AND (:DATA5 IS NULL OR (:DATA5 IS NOT NULL AND b.data5 = a.data5))
   AND (:DATA6 IS NULL OR (:DATA6 IS NOT NULL AND b.data6 = a.data6))
   AND (:DATA7 IS NULL OR (:DATA7 IS NOT NULL AND b.data7 = a.data7))
   AND (:DATA8 IS NULL OR (:DATA8 IS NOT NULL AND b.data8 = a.data8))
   AND (:DATA9 IS NULL OR (:DATA9 IS NOT NULL AND b.data9 = a.data9))
   AND (:DATA10 IS NULL OR (:DATA10 IS NOT NULL AND b.data10 = a.data10))
   AND (:DATA11 IS NULL OR (:DATA11 IS NOT NULL AND b.data11 = a.data11))
   AND (:DATA12 IS NULL OR (:DATA12 IS NOT NULL AND b.data12 = a.data12))
   AND (:DATA13 IS NULL OR (:DATA13 IS NOT NULL AND b.data13 = a.data13))
   AND (:DATA14 IS NULL OR (:DATA14 IS NOT NULL AND b.data14 = a.data14))
   AND (:DATA15 IS NULL OR (:DATA15 IS NOT NULL AND b.data15 = a.data15))
   AND (:DATA16 IS NULL OR (:DATA16 IS NOT NULL AND b.data16 = a.data16))
   AND (:DATA17 IS NULL OR (:DATA17 IS NOT NULL AND b.data17 = a.data17))
   AND (:DATA18 IS NULL OR (:DATA18 IS NOT NULL AND b.data18 = a.data18))
   AND (:DATA19 IS NULL OR (:DATA19 IS NOT NULL AND b.data19 = a.data19))
   AND (:DATA20 IS NULL OR (:DATA20 IS NOT NULL AND b.data20 = a.data20))