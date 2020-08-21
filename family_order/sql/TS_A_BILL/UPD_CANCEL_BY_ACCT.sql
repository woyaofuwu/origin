update ts_a_bill set balance=balance - to_number(:BALANCE),
                       late_balance=late_balance - to_number(:LATE_BALANCE),
                       pay_tag=:PAY_TAG,bill_pay_tag='1'
                 where acct_id=to_number(:ACCT_ID)
                   and partition_id=mod(to_number(:ACCT_ID),10000)
                   and bill_id=to_number(:BILL_ID)
                   and integrate_item_code=:INTEGRATE_ITEM_CODE