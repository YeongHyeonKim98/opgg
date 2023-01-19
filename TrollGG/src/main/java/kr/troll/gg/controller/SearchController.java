package kr.troll.gg.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import kr.troll.gg.VersionCheck;
import kr.troll.gg.dto.LeagueEntrydto;
import kr.troll.gg.dto.Summoner;
import kr.troll.gg.service.SearchService;


@Controller
public class SearchController {
	final static String API_KEY = "RGAPI-3f01085c-7118-4d32-97c9-ffd6b6166d96";		// API
	
	@Autowired SearchService service;
	
	@RequestMapping("/")
	public String home() {
		return "home";
	}
	
	
	@RequestMapping("/ranking")
	public ModelAndView ranking() {
		VersionCheck.checkVersion();
		ModelAndView mv = new ModelAndView();	// ModelAndView는 컴포넌트 즉, 객체 방식으로 작성되고 돌려줌. > 인자가 없으며 리턴값도 ModelAndView이다.
		//Model -> model.addAttribute를 사용하여 데이터만 저장
		//ModelAndView -> 데이터와 이동하고자 하는 View Page를 같이 저장
		mv.setViewName("ranking");
		mv.addObject("profileiconVersion", VersionCheck.profileiconVersion);
		mv.addObject("championVersion", VersionCheck.championVersion);
		mv.addObject("summonerVersion", VersionCheck.summonerVersion);
		mv.addObject("itemVersion", VersionCheck.itemVersion);
		return mv;
	}
	
	
	@RequestMapping("/search")
	public ModelAndView search(String SummonerName) {
		Summoner temp= null;			// 소환사 정보를 넣을 temp
		VersionCheck.checkVersion();	// 왜 하는거지?
		BufferedReader br = null;		// 초기화
		JsonParser jsonParser = new JsonParser();		// 파서 선언
		String[] matchList = null;		// 초기화
		
		VersionCheck.checkVersion();
		ModelAndView mv = new ModelAndView();		// Controller 처리 결과 후 응답할 view와 view에 전달할 값을 저장하는 역할.
		mv.setViewName("search");
		
		try{            
			//소환사 정보
			String urlstr = "https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/"+
					SummonerName.replace(" ", "")		+"?api_key="+API_KEY;		// https://developer.riotgames.com/apis#summoner-v4/GET_getBySummonerName
			URL url = new URL(urlstr);	// // HttpURLConnection을 사용하기 위한 절차. > 여기에 URLConnection 함.
			System.out.println(urlstr);	// https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/콜럼버스애디?api_key=RGAPI-1a27484f-e344-4477-88cc-3794054e1ced
			
			HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();		// 해당 url(소환사 정보)을 연결
			urlconnection.setRequestMethod("GET");	// GET 방식으로 set
			br = new BufferedReader(new InputStreamReader(urlconnection.getInputStream(),"UTF-8")); 		// 프로그램과 입력장치(키보드)를 연결해주는 역할. UTF-8로 받아들인다고함. (구글에서그럼)
			String result = "";
			String line;
			while((line = br.readLine()) != null) { 		// null이 아니면 실행
				result = result + line;	
			}
			if (!result.equals("{}")) {	// result 값이 공백이 아니면?
				JsonObject k = (JsonObject) jsonParser.parse(result);		// json 파서 공간 초기화
				temp = new Summoner(
						k.get("profileIconId").getAsInt(),		// 4668
						k.get("name").getAsString(),			// 콜럼버스 애디
						k.get("puuid").getAsString(),			// YQSKk9XeL0MYktHB7cNJ4zhfMwlujfM1QDBRFifJX-nLNQINr2cvyjHX_KDB6VyRiyrwsYRbmHYm7w
						k.get("summonerLevel").getAsLong(),		// 272
						k.get("revisionDate").getAsLong(),		// 1673953223257
						k.get("id").getAsString(),				// y9ztJ8HLgQsAZen5XLkTkD_tan5H-u52TUFcwOvod-3LzmA
						k.get("accountId").getAsString());		// 9aj_VkxFTGGaqFSYRZoQ0Js8I1T7ZQvc8zKpSvuspooAUYk]
				System.out.println(temp);
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		try{            
			//매치정보
			String urlstr = "https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/"+
					temp.getPuuid()+"/ids?start=0&count=20&"+"api_key="+API_KEY;		// https://developer.riotgames.com/apis#match-v5/GET_getMatchIdsByPUUID  (start default: 0 / count default: 20)
			
			System.out.println(urlstr);		// https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/YQSKk9XeL0MYktHB7cNJ4zhfMwlujfM1QDBRFifJX-nLNQINr2cvyjHX_KDB6VyRiyrwsYRbmHYm7w/ids?start=0&count=20&api_key=RGAPI-1a27484f-e344-4477-88cc-3794054e1ced
			URL url = new URL(urlstr);		// HttpURLConnection을 사용하기 위한 절차. > 여기에 URLConnection 함.  (ex. URLConnection urlCon = url.openConnection();) /// https://blueyikim.tistory.com/2199
			
			HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
			urlconnection.setRequestMethod("GET");		// Get 방식으로 받기
			
			br = new BufferedReader(new InputStreamReader(urlconnection.getInputStream(),"UTF-8"));		// 프로그램과 입력장치(키보드)를 연결해주는 역할. UTF-8로 받아들임 + 말 그대로 입력 스트림 가져오는 역할 및 데이터 읽기.
			String result = "";
			String line;
			
			while((line = br.readLine()) != null) { 
				result = result + line;
			}
			System.out.println(result);
			// ["KR_6313383943","KR_6313331951","KR_6313327274","KR_6313273714","KR_6313219665","KR_6313175962","KR_6313114067","KR_6313046785","KR_6312545056","KR_6312493867","KR_6312450213","KR_6312409175","KR_6312372046","KR_6312244151","KR_6312093903","KR_6312062101","KR_6311933946","KR_6311842088","KR_6311748385","KR_6311644964"]
			
			if (!result.equals("[]")) {
				JsonArray arr = (JsonArray) jsonParser.parse(result);		// JSON 파일 읽기.  https://studyingazae.tistory.com/196
				matchList = new String[arr.size()];
				for(int i=0; i<arr.size(); i++) {
					matchList[i] = arr.get(i).toString();
					System.out.println(matchList[i]);
					/*
					 * "KR_6313383943" "KR_6313331951" "KR_6313327274" "KR_6313273714"
					 * "KR_6313219665" "KR_6313175962" "KR_6313114067" "KR_6313046785"
					 * "KR_6312545056" "KR_6312493867" "KR_6312450213" "KR_6312409175"
					 * "KR_6312372046" "KR_6312244151" "KR_6312093903" "KR_6312062101"
					 * "KR_6311933946" "KR_6311842088" "KR_6311748385" "KR_6311644964"
					 */
					
				}				
			}

		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		// 반환값으로 ModelAndView 객체를 반환한다. + ModelAndView 객체를 선언 및 생성한다.!!
		// 아래 일련의 작업(mv.addObject("변수이름", "데이터 값"); 은 데이터를 보내는 과정이다.
		mv.addObject("SummonerName", SummonerName);
		mv.addObject("profileiconVersion", VersionCheck.profileiconVersion);
		mv.addObject("championVersion", VersionCheck.championVersion);
		mv.addObject("summonerVersion", VersionCheck.summonerVersion);
		mv.addObject("itemVersion", VersionCheck.itemVersion);
		mv.addObject("user", temp);
		mv.addObject("matchId", matchList);
		
		return mv;		// modelandview에 저장한값을 리턴
	}
	@RequestMapping("/test2")
	public ModelAndView test2(String SummonerName) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("search2");
		mv.addObject("SummonerName", SummonerName);
		return mv;
	}
	
	@RequestMapping("/reSearchUser")
	public ModelAndView reSearchUser(String SummonerName) {
		ModelAndView mv = new ModelAndView();
		
		return mv;
	}
	
	
}
