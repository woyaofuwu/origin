select COUNT(1) recordcount
        From tf_a_paylog a,tf_a_writesnap_log b
		where a.charge_id=b.operate_id
              and a.recv_time>=to_date(:BEGIN_DATE,'yyyy-mm-dd hh24:mi:ss')
          	  and a.user_id=:USER_ID
                 and (a.recv_staff_id=:RECV_STAFF_ID or :RECV_STAFF_ID='*')
                  and a.cancel_tag='0'
                    and a.recv_fee>=TO_NUMBER(:MONEY)
                      and b.all_new_balance>=TO_NUMBER(:MONEY)