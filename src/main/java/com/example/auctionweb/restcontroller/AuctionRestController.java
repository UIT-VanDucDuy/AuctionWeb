package com.example.auctionweb.restcontroller;

import com.example.auctionweb.dto.AuctionDto;
import com.example.auctionweb.entity.Auction;
import com.example.auctionweb.entity.BidHistory;
import com.example.auctionweb.service.AuctionService;
import com.example.auctionweb.service.BidHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auctionInfo")
public class AuctionRestController {
    @Autowired
    private AuctionService auctionService;

    @GetMapping("/{id}")
    public AuctionDto getAuctionInfo(@PathVariable(name = "id") int id) {
        return auctionService.getAuctionInfoById(id);
    }

}
