package kr.troll.gg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.troll.gg.dto.Summoner;
import kr.troll.gg.mapper.SearchMapper;

@Service
public class SearchService {
	final static String API_KEY = "RGAPI-e657640e-1876-4960-b6d7-0be34b916ef6";
	@Autowired
	SearchMapper map;
	public int summonerInsert(Summoner sm) {
		return map.summonerInsert(sm);
	}
	public Summoner summonerSelect(String SummonerName) {
		return map.summonerSelect(SummonerName);
	}
}
