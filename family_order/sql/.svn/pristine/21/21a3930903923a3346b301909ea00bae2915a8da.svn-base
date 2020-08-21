Select COUNT(1) recordcount
FROM   dual
WHERE  (SELECT COUNT(1) recordcount
        FROM   tf_f_user_sale_active t
        WHERE  user_id = TO_NUMBER(:USER_ID)
        AND    partition_id= Mod(TO_NUMBER(:USER_ID),10000)
        AND    package_id in(select p.package_id from td_b_package p where p.package_kind_code like :RSRV_STR1)
        AND    (rsrv_str2 = :RSRV_STR2 OR :RSRV_STR2 IS NULL)
        AND    (rsrv_str3 = :RSRV_STR3 OR :RSRV_STR3 IS NULL)
        AND    (rsrv_str4 = :RSRV_STR4 OR :RSRV_STR4 IS NULL)
        AND    (rsrv_str5 <= :RSRV_STR5 OR :RSRV_STR5 IS NULL)
        AND    end_date > trunc(last_day(SYSDATE)) + 1) > :NUM