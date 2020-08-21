select t.sp_code,t.biz_code,to_char(start_date, 'yyyy-MM-dd')start_date,to_char(end_date, 'yyyy-MM-dd')end_date,t.status,t.offer_code from TL_B_PLATSVC_STOP_MNG t
