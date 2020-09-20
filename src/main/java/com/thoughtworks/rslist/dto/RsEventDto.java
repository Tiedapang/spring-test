package com.thoughtworks.rslist.dto;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rsEvent")
public class RsEventDto {
  @Id @GeneratedValue private int id;
  private String eventName;
  private String keyword;
  private int voteNum;
  private int rank;
  @ManyToOne private UserDto user;
  @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "rsEventDto")
  private List<TradeDto> tradeDtos;
}
