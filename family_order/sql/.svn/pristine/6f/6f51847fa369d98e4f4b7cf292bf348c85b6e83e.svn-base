select mod(user_id, 10000),
         user_id,
         rsrv_value,
         rsrv_Str1 vip_id, --vip_id
         rsrv_Str2 vip_type_code, --vip_type_code
         rsrv_Str3 vip_class_id, --vip_class_id
         rsrv_Str4 gift_type_code, --gift_type_code
         rsrv_Str5 gift_id, --gift_id
         rsrv_Str6 gift_name, --gift_name
         rsrv_Str7 is_send_sms, --is_send_sms
         rsrv_Str8 exchange_num, --exchange_num
         start_date, --start_date
         end_date, --end_date
         update_time, --update_time
         staff_id update_staff_id, --update_staff_id
         depart_id update_depart_id, --update_depart_id
         null eparchy_code, --eparchy_code
         remark, --remark
         trade_id rsrv_str1, --rsrv_str1
         rsrv_tag1 rsrv_str2, --rsrv_str2
         null rsrv_str3, --rsrv_str3
         null rsrv_str4, --rsrv_str4
         null rsrv_str5, --rsrv_str5
         null rsrv_str6, --rsrv_str6
         null rsrv_str7, --rsrv_str7
         null rsrv_str8,  --rsrv_str8
         null rsrv_str9,  --rsrv_str9
         null rsrv_str10,  --rsrv_str10
         null rsrv_date1,  --rsrv_date1
         null rsrv_date2,	 --rsrv_date2
         null rsrv_date3  --rsrv_date3
    from tf_b_trade_other
	where trade_id =:TRADE_ID
	and rsrv_value_code = 'VIP_EXCHANGE_GIFT'
	and inst_id = :INST_ID
	and accept_month = :ACCEPT_MONTH