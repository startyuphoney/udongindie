package com.udongindie.udong.kakao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class KakaoPayApproveResponse {

    @Id @GeneratedValue
    @Column(name = "pay_idx")
    public Long idx;
    public String aid;
    public String tid;
    public String cid;
    public String partner_order_id;
    public String partner_user_id;
    public String payment_method_type;
    public String item_name;
    public Integer quantity;
    @OneToOne
    @JoinColumn(name = "amount_id")
    public Amount amount;
    public String created_at;
    public String approved_at;


}

@Entity
@Getter
@Setter
class Amount {

    @Id @GeneratedValue
    @Column(name = "amount_idx")
    public Long idx;
    public Integer total;
    public Integer tax_free;
    public Integer vat;
    public Integer point;
    public Integer discount;
    public Integer green_deposit;
    @OneToOne(mappedBy = "amount")
    public KakaoPayApproveResponse kakaoPayApproveResponse;

}