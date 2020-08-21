SELECT count(1) recordcount
  FROM ts_mx_custgroup_bill a
  WHERE (stat_month=:PARAM1 or :PARAM1 IS NULL)
    AND (serial_number=:PARAM0 OR :PARAM0 IS NULL)