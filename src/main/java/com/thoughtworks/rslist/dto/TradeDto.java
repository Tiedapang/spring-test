package com.thoughtworks.rslist.dto;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "trade")
public class TradeDto {
    @Id
    @GeneratedValue
    private int id;
    private int amount;
    private int rank;
    @ManyToOne@JoinColumn(name = "rs_event_id")
    private RsEventDto rsEventDto;
}
