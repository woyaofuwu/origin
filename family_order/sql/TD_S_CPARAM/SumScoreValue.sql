SELECT nvl(SUM(integral_fee),0) recordcount FROM tf_ah_integralbill
   WHERE user_id=to_number(:USER_ID)
     AND partition_id=MOD(to_number(:USER_ID), 10000)