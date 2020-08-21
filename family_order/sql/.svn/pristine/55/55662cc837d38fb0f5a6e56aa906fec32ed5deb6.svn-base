SELECT COUNT(1) recordcount
FROM   tf_f_user_infochange a
WHERE  end_date > SYSDATE
AND    end_date > start_date
AND    EXISTS (SELECT 1
        FROM   tf_f_user b
        WHERE  a.user_id = b.user_id
        and    b.partition_id = a.partition_id
        and    b.remove_tag = '0'
        AND    b.serial_number = :SERIAL_NUMBER)