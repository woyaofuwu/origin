update tf_f_user_sale_deposit
           SET start_date = to_date(:START_DATE, 'yyyy-MM-dd HH24:mi:ss'),
               end_date   = ADD_MONTHS(end_date, :BET_MONTH),
               remark     = substrb(remark, 1, 80) || ' 原开始时间 ' ||
                            to_char(start_date, 'yyyymmdd')
         WHERE partition_id = mod(to_number(:USER_ID), 10000)
           and user_id = :USER_ID
           and product_id = :PRODUCT_ID
           and package_id = :PACKAGE_Id