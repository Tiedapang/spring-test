package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.Trade;
import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.dto.RsEventDto;
import com.thoughtworks.rslist.dto.TradeDto;
import com.thoughtworks.rslist.dto.UserDto;
import com.thoughtworks.rslist.dto.VoteDto;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.TradeRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class RsService {
  final RsEventRepository rsEventRepository;
  final UserRepository userRepository;
  final VoteRepository voteRepository;
  final TradeRepository tradeRepository;

  public RsService(RsEventRepository rsEventRepository, UserRepository userRepository, VoteRepository voteRepository,TradeRepository tradeRepository) {
    this.rsEventRepository = rsEventRepository;
    this.userRepository = userRepository;
    this.voteRepository = voteRepository;
    this.tradeRepository = tradeRepository;
  }

  public void vote(Vote vote, int rsEventId) {
    Optional<RsEventDto> rsEventDto = rsEventRepository.findById(rsEventId);
    Optional<UserDto> userDto = userRepository.findById(vote.getUserId());
    if (!rsEventDto.isPresent()
        || !userDto.isPresent()
        || vote.getVoteNum() > userDto.get().getVoteNum()) {
      throw new RuntimeException();
    }
    VoteDto voteDto =
        VoteDto.builder()
            .localDateTime(vote.getTime())
            .num(vote.getVoteNum())
            .rsEvent(rsEventDto.get())
            .user(userDto.get())
            .build();
    voteRepository.save(voteDto);
    UserDto user = userDto.get();
    user.setVoteNum(user.getVoteNum() - vote.getVoteNum());
    userRepository.save(user);
    RsEventDto rsEvent = rsEventDto.get();
    rsEvent.setVoteNum(rsEvent.getVoteNum() + vote.getVoteNum());
    rsEventRepository.save(rsEvent);
  }

  public void buy(Trade trade, int rsEventId) {
    Optional<RsEventDto> rsEventDto = rsEventRepository.findById(rsEventId);
    if (!rsEventDto.isPresent()) {
      throw new RuntimeException();
    }
    List<TradeDto> tradeDtos = tradeRepository.findAllByRank(trade.getRank());
    tradeRepository.save(TradeDto.builder().amount(trade.getAmount())
            .rank(trade.getRank())
            .rsEventDto(rsEventDto.get())
            .build());
    if(tradeDtos.size() == 0){
      rsEventDto.get().setRank(trade.getRank());
      rsEventRepository.save(rsEventDto.get());
    }else{
      TradeDto maxAmountTradeDto =  tradeDtos.stream().max(Comparator.comparing(TradeDto::getAmount)).get();
      if(trade.getAmount()>maxAmountTradeDto.getAmount()){
        rsEventRepository.deleteById(maxAmountTradeDto.getRsEventDto().getId());
        rsEventDto.get().setRank(trade.getRank());
        rsEventRepository.save(rsEventDto.get());
      }else{
        throw new RuntimeException();
      }
    }
  }
  public int getRsEventRank(){
    List<RsEventDto> rsEventDtos = rsEventRepository.findAll();
    int rank = 0;
    if(rsEventDtos.size()>0){
      rank = rsEventDtos.stream().max(Comparator.comparing(RsEventDto::getRank)).get().getRank();
    }
    return rank;
  }
}
