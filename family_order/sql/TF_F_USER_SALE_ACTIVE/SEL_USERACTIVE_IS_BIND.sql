select /*+index(a IDX_TF_F_USER_SALE_ACTIVE_PROD)*/ a.user_id from tf_f_user_sale_active a,td_s_commpara b
        WHERE a.product_id=b.param_code
        AND b.subsys_code='CSM'
        AND b.param_attr=83
        AND a.process_tag='0'
        AND a.rsrv_str2=:PHONE_CODE_B
        AND nvl(a.rsrv_date2,a.end_date)>SYSDATE
        AND SYSDATE BETWEEN b.start_date AND b.end_Date