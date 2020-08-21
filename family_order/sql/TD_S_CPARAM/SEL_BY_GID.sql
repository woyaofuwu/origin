select count(1) recordcount FROM ts_mx_custgroup_bill a
  WHERE (stat_month=:PARAM1 OR :PARAM1 IS NULL)
    AND (group_id=:PARAM0 OR :PARAM0 IS NULL)
    and ((item_type = :PARAM2 OR :PARAM2 IS NULL)
        or (item_code = :PARAM2 OR :PARAM2 IS NULL))