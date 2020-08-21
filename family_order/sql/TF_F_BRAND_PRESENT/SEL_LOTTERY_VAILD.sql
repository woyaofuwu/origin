SELECT brand_code,info, to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,
			 to_char( end_date, 'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_brand_present 
 WHERE brand_code = :BRAND_CODE
       AND sysdate between start_date and end_date