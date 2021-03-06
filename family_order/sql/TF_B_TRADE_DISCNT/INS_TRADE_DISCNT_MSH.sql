insert into tf_b_trade_discnt
		      (trade_id,
		       accept_month,
		       user_id,
		       user_id_a,
		       product_id,
		       package_id,
		       discnt_code,
		       spec_tag,
		       relation_type_code,
		       inst_id,
		       campn_id,
		       start_date,
		       end_date,
		       modify_tag,
		       update_time,
		       update_staff_id,
		       update_depart_id,
		       remark,
		       rsrv_num1,
		       rsrv_num2,
		       rsrv_num3,
		       rsrv_num4,
		       rsrv_num5,
		       rsrv_str1,
		       rsrv_str2,
		       rsrv_str3,
		       rsrv_str4,
		       rsrv_str5,
		       rsrv_date1,
		       rsrv_date2,
		       rsrv_date3,
		       rsrv_tag1,
		       rsrv_tag2,
		       rsrv_tag3)
		      select trade_id,
		           to_number(to_char(sysdate, 'MM')),
		           user_id,
		           '-1',
		           '50000000',
		           '50000000',
		           '6730',
		           '0',
		           '',
		           f_sys_getseqid(nvl('0898', '0022'), 'seq_inst_id'),
		           '',
		           sysdate,
		           add_months(sysdate, 12),
		           '0',
		           sysdate,
		           update_staff_id,
		           update_depart_id,
		           '美食汇绑定包年优惠',
		           '',
		           '',
		           '',
		           '',
		           '',
		           '',
		           '',
		           '',
		           '',
		           '',
		           '',
		           '',
		           '',
		           '',
		           '',
		           ''
		        from tf_b_trade
		       where trade_id = :TRADE_ID