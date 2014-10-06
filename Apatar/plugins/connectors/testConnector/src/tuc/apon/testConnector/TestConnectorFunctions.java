package tuc.apon.testConnector;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;

import org.apache.axis2.AxisFault;

import eu.dataaccess.footballpool2.InfoStub;
import eu.dataaccess.footballpool2.InfoStub.*;

public class TestConnectorFunctions {

	public static void gameInfo(HashMap<String, String> argVals, HashMap<String, Object> retVals){
		System.err.println("gameInfo1");
		
		retVals.clear();
		try{
			InfoStub _stub = new InfoStub();
			java.lang.Integer _GameInfo_iGameId= new java.lang.Integer(argVals.get("iGameId"));
			GameInfo _GameInfo = new GameInfo();
			_GameInfo.setIGameId(_GameInfo_iGameId);
			GameInfoResponse _GameInfoResponse = new GameInfoResponse();
			_GameInfoResponse = _stub.gameInfo( _GameInfo );
			TGameInfo _GameInfoResponse_GameInfoResult= _GameInfoResponse.getGameInfoResult();
			
			java.lang.Integer _GameInfoResponse_GameInfoResult_iId = _GameInfoResponse_GameInfoResult.getIId();
			retVals.put("iId", _GameInfoResponse_GameInfoResult_iId);
			System.out.println("_GameInfoResponse_GameInfoResult_iId: "+_GameInfoResponse_GameInfoResult_iId);
			
			java.lang.String _GameInfoResponse_GameInfoResult_sDescription = _GameInfoResponse_GameInfoResult.getSDescription();
			retVals.put("sDescription", _GameInfoResponse_GameInfoResult_sDescription);
			System.out.println("_GameInfoResponse_GameInfoResult_sDescription: "+_GameInfoResponse_GameInfoResult_sDescription);
			
			Date _GameInfoResponse_GameInfoResult_dPlayDate = _GameInfoResponse_GameInfoResult.getDPlayDate();
			retVals.put("dPlayDate", _GameInfoResponse_GameInfoResult_dPlayDate);
			System.out.println("_GameInfoResponse_GameInfoResult_dPlayDate: "+_GameInfoResponse_GameInfoResult_dPlayDate);
			
			org.apache.axis2.databinding.types.Time _GameInfoResponse_GameInfoResult_tPlayTime = _GameInfoResponse_GameInfoResult.getTPlayTime();
			retVals.put("tPlayTime", axis2sqlTime(_GameInfoResponse_GameInfoResult_tPlayTime));
			System.out.println("_GameInfoResponse_GameInfoResult_tPlayTime: "+_GameInfoResponse_GameInfoResult_tPlayTime);
			TStadiumInfo _GameInfoResponse_GameInfoResult_StadiumInfo= _GameInfoResponse_GameInfoResult.getStadiumInfo();
			
			java.lang.String _GameInfoResponse_GameInfoResult_StadiumInfo_sStadiumName = _GameInfoResponse_GameInfoResult_StadiumInfo.getSStadiumName();
			retVals.put("sStadiumName", _GameInfoResponse_GameInfoResult_StadiumInfo_sStadiumName);
			System.out.println("_GameInfoResponse_GameInfoResult_StadiumInfo_sStadiumName: "+_GameInfoResponse_GameInfoResult_StadiumInfo_sStadiumName);
			
			
			java.lang.Integer _GameInfoResponse_GameInfoResult_StadiumInfo_iSeatsCapacity = _GameInfoResponse_GameInfoResult_StadiumInfo.getISeatsCapacity();
			retVals.put("iSeatsCapacity", _GameInfoResponse_GameInfoResult_StadiumInfo_iSeatsCapacity);
			System.out.println("_GameInfoResponse_GameInfoResult_StadiumInfo_iSeatsCapacity: "+_GameInfoResponse_GameInfoResult_StadiumInfo_iSeatsCapacity);
			
			java.lang.String _GameInfoResponse_GameInfoResult_StadiumInfo_sCityName = _GameInfoResponse_GameInfoResult_StadiumInfo.getSCityName();
			retVals.put("sCityName", _GameInfoResponse_GameInfoResult_StadiumInfo_sCityName);
			System.out.println("_GameInfoResponse_GameInfoResult_StadiumInfo_sCityName: "+_GameInfoResponse_GameInfoResult_StadiumInfo_sCityName);
			
			java.lang.String _GameInfoResponse_GameInfoResult_StadiumInfo_sWikipediaURL = _GameInfoResponse_GameInfoResult_StadiumInfo.getSWikipediaURL();
			retVals.put("sWikipediaURL",_GameInfoResponse_GameInfoResult_StadiumInfo_sWikipediaURL );
			System.out.println("_GameInfoResponse_GameInfoResult_StadiumInfo_sWikipediaURL: "+_GameInfoResponse_GameInfoResult_StadiumInfo_sWikipediaURL);
			
			java.lang.String _GameInfoResponse_GameInfoResult_StadiumInfo_sGoogleMapsURL = _GameInfoResponse_GameInfoResult_StadiumInfo.getSGoogleMapsURL();
			retVals.put("sGoogleMapsURL", _GameInfoResponse_GameInfoResult_StadiumInfo_sGoogleMapsURL);
			System.out.println("_GameInfoResponse_GameInfoResult_StadiumInfo_sGoogleMapsURL: "+_GameInfoResponse_GameInfoResult_StadiumInfo_sGoogleMapsURL);
			
			TTeamInfo _GameInfoResponse_GameInfoResult_Team1= _GameInfoResponse_GameInfoResult.getTeam1();
			
			java.lang.Integer _GameInfoResponse_GameInfoResult_Team1_iId = _GameInfoResponse_GameInfoResult_Team1.getIId();
			retVals.put("Team1_iId",_GameInfoResponse_GameInfoResult_Team1_iId );
			System.out.println("_GameInfoResponse_GameInfoResult_Team1_iId: "+_GameInfoResponse_GameInfoResult_Team1_iId);
			
			java.lang.String _GameInfoResponse_GameInfoResult_Team1_sName = _GameInfoResponse_GameInfoResult_Team1.getSName();
			retVals.put("Team1_sName",_GameInfoResponse_GameInfoResult_Team1_sName );
			System.out.println("_GameInfoResponse_GameInfoResult_Team1_sName: "+_GameInfoResponse_GameInfoResult_Team1_sName);
			
			java.lang.String _GameInfoResponse_GameInfoResult_Team1_sCountryFlag = _GameInfoResponse_GameInfoResult_Team1.getSCountryFlag();
			retVals.put("Team1_sCountryFlag", _GameInfoResponse_GameInfoResult_Team1_sCountryFlag);
			System.out.println("_GameInfoResponse_GameInfoResult_Team1_sCountryFlag: "+_GameInfoResponse_GameInfoResult_Team1_sCountryFlag);
			
			java.lang.String _GameInfoResponse_GameInfoResult_Team1_sWikipediaURL = _GameInfoResponse_GameInfoResult_Team1.getSWikipediaURL();
			retVals.put("Team1_sWikipediaURL", _GameInfoResponse_GameInfoResult_Team1_sWikipediaURL);
			System.out.println("_GameInfoResponse_GameInfoResult_Team1_sWikipediaURL: "+_GameInfoResponse_GameInfoResult_Team1_sWikipediaURL);
			
			TTeamInfo _GameInfoResponse_GameInfoResult_Team2= _GameInfoResponse_GameInfoResult.getTeam2();
			
			java.lang.Integer _GameInfoResponse_GameInfoResult_Team2_iId = _GameInfoResponse_GameInfoResult_Team2.getIId();
			retVals.put("Team2_iId",_GameInfoResponse_GameInfoResult_Team2_iId );
			System.out.println("_GameInfoResponse_GameInfoResult_Team2_iId: "+_GameInfoResponse_GameInfoResult_Team2_iId);
			
			java.lang.String _GameInfoResponse_GameInfoResult_Team2_sName = _GameInfoResponse_GameInfoResult_Team2.getSName();
			retVals.put("Team2_sName", _GameInfoResponse_GameInfoResult_Team2_sName);
			System.out.println("_GameInfoResponse_GameInfoResult_Team2_sName: "+_GameInfoResponse_GameInfoResult_Team2_sName);
			
			java.lang.String _GameInfoResponse_GameInfoResult_Team2_sCountryFlag = _GameInfoResponse_GameInfoResult_Team2.getSCountryFlag();
			retVals.put("Team2_sCountryFlag", _GameInfoResponse_GameInfoResult_Team2_sCountryFlag);
			System.out.println("_GameInfoResponse_GameInfoResult_Team2_sCountryFlag: "+_GameInfoResponse_GameInfoResult_Team2_sCountryFlag);

			java.lang.String _GameInfoResponse_GameInfoResult_Team2_sWikipediaURL = _GameInfoResponse_GameInfoResult_Team2.getSWikipediaURL();
			retVals.put("Team2_sWikipediaURL",_GameInfoResponse_GameInfoResult_Team2_sWikipediaURL );
			System.out.println("_GameInfoResponse_GameInfoResult_Team2_sWikipediaURL: "+_GameInfoResponse_GameInfoResult_Team2_sWikipediaURL);
			
			java.lang.String _GameInfoResponse_GameInfoResult_sResult = _GameInfoResponse_GameInfoResult.getSResult();
			retVals.put("sResult", _GameInfoResponse_GameInfoResult_sResult);
			System.out.println("_GameInfoResponse_GameInfoResult_sResult: "+_GameInfoResponse_GameInfoResult_sResult);
			
			java.lang.String _GameInfoResponse_GameInfoResult_sScore = _GameInfoResponse_GameInfoResult.getSScore();
			retVals.put("sScore", _GameInfoResponse_GameInfoResult_sScore);
			System.out.println("_GameInfoResponse_GameInfoResult_sScore: "+_GameInfoResponse_GameInfoResult_sScore);
			
			java.lang.Integer _GameInfoResponse_GameInfoResult_iYellowCards = _GameInfoResponse_GameInfoResult.getIYellowCards();
			retVals.put("iYellowCards", _GameInfoResponse_GameInfoResult_iYellowCards);
			System.out.println("_GameInfoResponse_GameInfoResult_iYellowCards: "+_GameInfoResponse_GameInfoResult_iYellowCards);
			
			java.lang.Integer _GameInfoResponse_GameInfoResult_iRedCards = _GameInfoResponse_GameInfoResult.getIRedCards();
			retVals.put("iRedCards" , _GameInfoResponse_GameInfoResult_iRedCards);
			System.out.println("_GameInfoResponse_GameInfoResult_iRedCards: "+_GameInfoResponse_GameInfoResult_iRedCards);
//			
//			ArrayOftGameCard _GameInfoResponse_GameInfoResult_Cards= _GameInfoResponse_GameInfoResult.getCards();
//			TGameCard[] _GameInfoResponse_GameInfoResult_Cards_tGameCard= _GameInfoResponse_GameInfoResult_Cards.getTGameCard();
//			if( _GameInfoResponse_GameInfoResult_Cards_tGameCard != null ){
//				for(int i_0=0;i_0<_GameInfoResponse_GameInfoResult_Cards_tGameCard.length;i_0++){
//					Date _GameInfoResponse_GameInfoResult_Cards_tGameCard_dGame = _GameInfoResponse_GameInfoResult_Cards_tGameCard[i_0].getDGame();
//					System.out.println("_GameInfoResponse_GameInfoResult_Cards_tGameCard_dGame: "+_GameInfoResponse_GameInfoResult_Cards_tGameCard_dGame);
//					java.lang.Integer _GameInfoResponse_GameInfoResult_Cards_tGameCard_iMinute = _GameInfoResponse_GameInfoResult_Cards_tGameCard[i_0].getIMinute();
//					System.out.println("_GameInfoResponse_GameInfoResult_Cards_tGameCard_iMinute: "+_GameInfoResponse_GameInfoResult_Cards_tGameCard_iMinute);
//					java.lang.String _GameInfoResponse_GameInfoResult_Cards_tGameCard_sPlayerName = _GameInfoResponse_GameInfoResult_Cards_tGameCard[i_0].getSPlayerName();
//					System.out.println("_GameInfoResponse_GameInfoResult_Cards_tGameCard_sPlayerName: "+_GameInfoResponse_GameInfoResult_Cards_tGameCard_sPlayerName);
//					java.lang.Boolean _GameInfoResponse_GameInfoResult_Cards_tGameCard_bYellowCard = _GameInfoResponse_GameInfoResult_Cards_tGameCard[i_0].getBYellowCard();
//					System.out.println("_GameInfoResponse_GameInfoResult_Cards_tGameCard_bYellowCard: "+_GameInfoResponse_GameInfoResult_Cards_tGameCard_bYellowCard);
//					java.lang.Boolean _GameInfoResponse_GameInfoResult_Cards_tGameCard_bRedCard = _GameInfoResponse_GameInfoResult_Cards_tGameCard[i_0].getBRedCard();
//					System.out.println("_GameInfoResponse_GameInfoResult_Cards_tGameCard_bRedCard: "+_GameInfoResponse_GameInfoResult_Cards_tGameCard_bRedCard);
//					java.lang.String _GameInfoResponse_GameInfoResult_Cards_tGameCard_sTeamName = _GameInfoResponse_GameInfoResult_Cards_tGameCard[i_0].getSTeamName();
//					System.out.println("_GameInfoResponse_GameInfoResult_Cards_tGameCard_sTeamName: "+_GameInfoResponse_GameInfoResult_Cards_tGameCard_sTeamName);
//					java.lang.String _GameInfoResponse_GameInfoResult_Cards_tGameCard_sTeamFlag = _GameInfoResponse_GameInfoResult_Cards_tGameCard[i_0].getSTeamFlag();
//					System.out.println("_GameInfoResponse_GameInfoResult_Cards_tGameCard_sTeamFlag: "+_GameInfoResponse_GameInfoResult_Cards_tGameCard_sTeamFlag);
//				}
//			}
//			ArrayOftGoal _GameInfoResponse_GameInfoResult_Goals= _GameInfoResponse_GameInfoResult.getGoals();
//			TGoal[] _GameInfoResponse_GameInfoResult_Goals_tGoal= _GameInfoResponse_GameInfoResult_Goals.getTGoal();
//			if( _GameInfoResponse_GameInfoResult_Goals_tGoal != null ){
//				for(int i_0=0;i_0<_GameInfoResponse_GameInfoResult_Goals_tGoal.length;i_0++){
//					Date _GameInfoResponse_GameInfoResult_Goals_tGoal_dGame = _GameInfoResponse_GameInfoResult_Goals_tGoal[i_0].getDGame();
//					System.out.println("_GameInfoResponse_GameInfoResult_Goals_tGoal_dGame: "+_GameInfoResponse_GameInfoResult_Goals_tGoal_dGame);
//					java.lang.Integer _GameInfoResponse_GameInfoResult_Goals_tGoal_iMinute = _GameInfoResponse_GameInfoResult_Goals_tGoal[i_0].getIMinute();
//					System.out.println("_GameInfoResponse_GameInfoResult_Goals_tGoal_iMinute: "+_GameInfoResponse_GameInfoResult_Goals_tGoal_iMinute);
//					java.lang.String _GameInfoResponse_GameInfoResult_Goals_tGoal_sPlayerName = _GameInfoResponse_GameInfoResult_Goals_tGoal[i_0].getSPlayerName();
//					System.out.println("_GameInfoResponse_GameInfoResult_Goals_tGoal_sPlayerName: "+_GameInfoResponse_GameInfoResult_Goals_tGoal_sPlayerName);
//					java.lang.String _GameInfoResponse_GameInfoResult_Goals_tGoal_sTeamName = _GameInfoResponse_GameInfoResult_Goals_tGoal[i_0].getSTeamName();
//					System.out.println("_GameInfoResponse_GameInfoResult_Goals_tGoal_sTeamName: "+_GameInfoResponse_GameInfoResult_Goals_tGoal_sTeamName);
//					java.lang.String _GameInfoResponse_GameInfoResult_Goals_tGoal_sTeamFlag = _GameInfoResponse_GameInfoResult_Goals_tGoal[i_0].getSTeamFlag();
//					System.out.println("_GameInfoResponse_GameInfoResult_Goals_tGoal_sTeamFlag: "+_GameInfoResponse_GameInfoResult_Goals_tGoal_sTeamFlag);
//				}
//			}
			
		}
		catch(AxisFault axisFault){
			axisFault.printStackTrace();
		}
		catch(RemoteException remEx){
			remEx.printStackTrace();
		}
		
	}
	
	public static void gameInfo2(HashMap<String, String> argVals, HashMap<String, Object> retVals){
		System.err.println("gameInfo2");
		
		retVals.clear();
		try{
			InfoStub _stub = new InfoStub();
			java.lang.Integer _GameInfo_iGameId= new java.lang.Integer(argVals.get("iGameId"));
			GameInfo _GameInfo = new GameInfo();
			_GameInfo.setIGameId(_GameInfo_iGameId);
			GameInfoResponse _GameInfoResponse = new GameInfoResponse();
			_GameInfoResponse = _stub.gameInfo( _GameInfo );
			TGameInfo _GameInfoResponse_GameInfoResult= _GameInfoResponse.getGameInfoResult();
			
			java.lang.Integer _GameInfoResponse_GameInfoResult_iId = _GameInfoResponse_GameInfoResult.getIId();
			retVals.put("iId", _GameInfoResponse_GameInfoResult_iId);
			System.out.println("_GameInfoResponse_GameInfoResult_iId: "+_GameInfoResponse_GameInfoResult_iId);
			
			java.lang.String _GameInfoResponse_GameInfoResult_sDescription = _GameInfoResponse_GameInfoResult.getSDescription();
			retVals.put("sDescription", _GameInfoResponse_GameInfoResult_sDescription);
			System.out.println("_GameInfoResponse_GameInfoResult_sDescription: "+_GameInfoResponse_GameInfoResult_sDescription);
			
			Date _GameInfoResponse_GameInfoResult_dPlayDate = _GameInfoResponse_GameInfoResult.getDPlayDate();
			retVals.put("dPlayDate", _GameInfoResponse_GameInfoResult_dPlayDate);
			System.out.println("_GameInfoResponse_GameInfoResult_dPlayDate: "+_GameInfoResponse_GameInfoResult_dPlayDate);
			
			org.apache.axis2.databinding.types.Time _GameInfoResponse_GameInfoResult_tPlayTime = _GameInfoResponse_GameInfoResult.getTPlayTime();
			retVals.put("tPlayTime", axis2sqlTime(_GameInfoResponse_GameInfoResult_tPlayTime));
			System.out.println("_GameInfoResponse_GameInfoResult_tPlayTime: "+_GameInfoResponse_GameInfoResult_tPlayTime);
			TStadiumInfo _GameInfoResponse_GameInfoResult_StadiumInfo= _GameInfoResponse_GameInfoResult.getStadiumInfo();
			
			java.lang.String _GameInfoResponse_GameInfoResult_StadiumInfo_sStadiumName = _GameInfoResponse_GameInfoResult_StadiumInfo.getSStadiumName();
			retVals.put("sStadiumName", _GameInfoResponse_GameInfoResult_StadiumInfo_sStadiumName);
			System.out.println("_GameInfoResponse_GameInfoResult_StadiumInfo_sStadiumName: "+_GameInfoResponse_GameInfoResult_StadiumInfo_sStadiumName);
			
			
			java.lang.Integer _GameInfoResponse_GameInfoResult_StadiumInfo_iSeatsCapacity = _GameInfoResponse_GameInfoResult_StadiumInfo.getISeatsCapacity();
			retVals.put("iSeatsCapacity", _GameInfoResponse_GameInfoResult_StadiumInfo_iSeatsCapacity);
			System.out.println("_GameInfoResponse_GameInfoResult_StadiumInfo_iSeatsCapacity: "+_GameInfoResponse_GameInfoResult_StadiumInfo_iSeatsCapacity);
			
			java.lang.String _GameInfoResponse_GameInfoResult_StadiumInfo_sCityName = _GameInfoResponse_GameInfoResult_StadiumInfo.getSCityName();
			retVals.put("sCityName", _GameInfoResponse_GameInfoResult_StadiumInfo_sCityName);
			System.out.println("_GameInfoResponse_GameInfoResult_StadiumInfo_sCityName: "+_GameInfoResponse_GameInfoResult_StadiumInfo_sCityName);
			
			java.lang.String _GameInfoResponse_GameInfoResult_StadiumInfo_sWikipediaURL = _GameInfoResponse_GameInfoResult_StadiumInfo.getSWikipediaURL();
			retVals.put("sWikipediaURL",_GameInfoResponse_GameInfoResult_StadiumInfo_sWikipediaURL );
			System.out.println("_GameInfoResponse_GameInfoResult_StadiumInfo_sWikipediaURL: "+_GameInfoResponse_GameInfoResult_StadiumInfo_sWikipediaURL);
			
			java.lang.String _GameInfoResponse_GameInfoResult_StadiumInfo_sGoogleMapsURL = _GameInfoResponse_GameInfoResult_StadiumInfo.getSGoogleMapsURL();
			retVals.put("sGoogleMapsURL", _GameInfoResponse_GameInfoResult_StadiumInfo_sGoogleMapsURL);
			System.out.println("_GameInfoResponse_GameInfoResult_StadiumInfo_sGoogleMapsURL: "+_GameInfoResponse_GameInfoResult_StadiumInfo_sGoogleMapsURL);
			
			TTeamInfo _GameInfoResponse_GameInfoResult_Team1= _GameInfoResponse_GameInfoResult.getTeam1();
			
			java.lang.Integer _GameInfoResponse_GameInfoResult_Team1_iId = _GameInfoResponse_GameInfoResult_Team1.getIId();
			retVals.put("Team1_iId",_GameInfoResponse_GameInfoResult_Team1_iId );
			System.out.println("_GameInfoResponse_GameInfoResult_Team1_iId: "+_GameInfoResponse_GameInfoResult_Team1_iId);
			
			java.lang.String _GameInfoResponse_GameInfoResult_Team1_sName = _GameInfoResponse_GameInfoResult_Team1.getSName();
			retVals.put("Team1_sName",_GameInfoResponse_GameInfoResult_Team1_sName );
			System.out.println("_GameInfoResponse_GameInfoResult_Team1_sName: "+_GameInfoResponse_GameInfoResult_Team1_sName);
			
			java.lang.String _GameInfoResponse_GameInfoResult_Team1_sCountryFlag = _GameInfoResponse_GameInfoResult_Team1.getSCountryFlag();
			retVals.put("Team1_sCountryFlag", _GameInfoResponse_GameInfoResult_Team1_sCountryFlag);
			System.out.println("_GameInfoResponse_GameInfoResult_Team1_sCountryFlag: "+_GameInfoResponse_GameInfoResult_Team1_sCountryFlag);
			
			java.lang.String _GameInfoResponse_GameInfoResult_Team1_sWikipediaURL = _GameInfoResponse_GameInfoResult_Team1.getSWikipediaURL();
			retVals.put("Team1_sWikipediaURL", _GameInfoResponse_GameInfoResult_Team1_sWikipediaURL);
			System.out.println("_GameInfoResponse_GameInfoResult_Team1_sWikipediaURL: "+_GameInfoResponse_GameInfoResult_Team1_sWikipediaURL);
			
			TTeamInfo _GameInfoResponse_GameInfoResult_Team2= _GameInfoResponse_GameInfoResult.getTeam2();
			
			java.lang.Integer _GameInfoResponse_GameInfoResult_Team2_iId = _GameInfoResponse_GameInfoResult_Team2.getIId();
			retVals.put("Team2_iId",_GameInfoResponse_GameInfoResult_Team2_iId );
			System.out.println("_GameInfoResponse_GameInfoResult_Team2_iId: "+_GameInfoResponse_GameInfoResult_Team2_iId);
			
			java.lang.String _GameInfoResponse_GameInfoResult_Team2_sName = _GameInfoResponse_GameInfoResult_Team2.getSName();
			retVals.put("Team2_sName", _GameInfoResponse_GameInfoResult_Team2_sName);
			System.out.println("_GameInfoResponse_GameInfoResult_Team2_sName: "+_GameInfoResponse_GameInfoResult_Team2_sName);
			
			java.lang.String _GameInfoResponse_GameInfoResult_Team2_sCountryFlag = _GameInfoResponse_GameInfoResult_Team2.getSCountryFlag();
			retVals.put("Team2_sCountryFlag", _GameInfoResponse_GameInfoResult_Team2_sCountryFlag);
			System.out.println("_GameInfoResponse_GameInfoResult_Team2_sCountryFlag: "+_GameInfoResponse_GameInfoResult_Team2_sCountryFlag);

			java.lang.String _GameInfoResponse_GameInfoResult_Team2_sWikipediaURL = _GameInfoResponse_GameInfoResult_Team2.getSWikipediaURL();
			retVals.put("Team2_sWikipediaURL",_GameInfoResponse_GameInfoResult_Team2_sWikipediaURL );
			System.out.println("_GameInfoResponse_GameInfoResult_Team2_sWikipediaURL: "+_GameInfoResponse_GameInfoResult_Team2_sWikipediaURL);
			
			java.lang.String _GameInfoResponse_GameInfoResult_sResult = _GameInfoResponse_GameInfoResult.getSResult();
			retVals.put("sResult", _GameInfoResponse_GameInfoResult_sResult);
			System.out.println("_GameInfoResponse_GameInfoResult_sResult: "+_GameInfoResponse_GameInfoResult_sResult);
			
			java.lang.String _GameInfoResponse_GameInfoResult_sScore = _GameInfoResponse_GameInfoResult.getSScore();
			retVals.put("sScore", _GameInfoResponse_GameInfoResult_sScore);
			System.out.println("_GameInfoResponse_GameInfoResult_sScore: "+_GameInfoResponse_GameInfoResult_sScore);
			
			java.lang.Integer _GameInfoResponse_GameInfoResult_iYellowCards = _GameInfoResponse_GameInfoResult.getIYellowCards();
			retVals.put("iYellowCards", _GameInfoResponse_GameInfoResult_iYellowCards);
			System.out.println("_GameInfoResponse_GameInfoResult_iYellowCards: "+_GameInfoResponse_GameInfoResult_iYellowCards);
			
			java.lang.Integer _GameInfoResponse_GameInfoResult_iRedCards = _GameInfoResponse_GameInfoResult.getIRedCards();
			retVals.put("iRedCards" , _GameInfoResponse_GameInfoResult_iRedCards);
			System.out.println("_GameInfoResponse_GameInfoResult_iRedCards: "+_GameInfoResponse_GameInfoResult_iRedCards);
//			
//			ArrayOftGameCard _GameInfoResponse_GameInfoResult_Cards= _GameInfoResponse_GameInfoResult.getCards();
//			TGameCard[] _GameInfoResponse_GameInfoResult_Cards_tGameCard= _GameInfoResponse_GameInfoResult_Cards.getTGameCard();
//			if( _GameInfoResponse_GameInfoResult_Cards_tGameCard != null ){
//				for(int i_0=0;i_0<_GameInfoResponse_GameInfoResult_Cards_tGameCard.length;i_0++){
//					Date _GameInfoResponse_GameInfoResult_Cards_tGameCard_dGame = _GameInfoResponse_GameInfoResult_Cards_tGameCard[i_0].getDGame();
//					System.out.println("_GameInfoResponse_GameInfoResult_Cards_tGameCard_dGame: "+_GameInfoResponse_GameInfoResult_Cards_tGameCard_dGame);
//					java.lang.Integer _GameInfoResponse_GameInfoResult_Cards_tGameCard_iMinute = _GameInfoResponse_GameInfoResult_Cards_tGameCard[i_0].getIMinute();
//					System.out.println("_GameInfoResponse_GameInfoResult_Cards_tGameCard_iMinute: "+_GameInfoResponse_GameInfoResult_Cards_tGameCard_iMinute);
//					java.lang.String _GameInfoResponse_GameInfoResult_Cards_tGameCard_sPlayerName = _GameInfoResponse_GameInfoResult_Cards_tGameCard[i_0].getSPlayerName();
//					System.out.println("_GameInfoResponse_GameInfoResult_Cards_tGameCard_sPlayerName: "+_GameInfoResponse_GameInfoResult_Cards_tGameCard_sPlayerName);
//					java.lang.Boolean _GameInfoResponse_GameInfoResult_Cards_tGameCard_bYellowCard = _GameInfoResponse_GameInfoResult_Cards_tGameCard[i_0].getBYellowCard();
//					System.out.println("_GameInfoResponse_GameInfoResult_Cards_tGameCard_bYellowCard: "+_GameInfoResponse_GameInfoResult_Cards_tGameCard_bYellowCard);
//					java.lang.Boolean _GameInfoResponse_GameInfoResult_Cards_tGameCard_bRedCard = _GameInfoResponse_GameInfoResult_Cards_tGameCard[i_0].getBRedCard();
//					System.out.println("_GameInfoResponse_GameInfoResult_Cards_tGameCard_bRedCard: "+_GameInfoResponse_GameInfoResult_Cards_tGameCard_bRedCard);
//					java.lang.String _GameInfoResponse_GameInfoResult_Cards_tGameCard_sTeamName = _GameInfoResponse_GameInfoResult_Cards_tGameCard[i_0].getSTeamName();
//					System.out.println("_GameInfoResponse_GameInfoResult_Cards_tGameCard_sTeamName: "+_GameInfoResponse_GameInfoResult_Cards_tGameCard_sTeamName);
//					java.lang.String _GameInfoResponse_GameInfoResult_Cards_tGameCard_sTeamFlag = _GameInfoResponse_GameInfoResult_Cards_tGameCard[i_0].getSTeamFlag();
//					System.out.println("_GameInfoResponse_GameInfoResult_Cards_tGameCard_sTeamFlag: "+_GameInfoResponse_GameInfoResult_Cards_tGameCard_sTeamFlag);
//				}
//			}
//			ArrayOftGoal _GameInfoResponse_GameInfoResult_Goals= _GameInfoResponse_GameInfoResult.getGoals();
//			TGoal[] _GameInfoResponse_GameInfoResult_Goals_tGoal= _GameInfoResponse_GameInfoResult_Goals.getTGoal();
//			if( _GameInfoResponse_GameInfoResult_Goals_tGoal != null ){
//				for(int i_0=0;i_0<_GameInfoResponse_GameInfoResult_Goals_tGoal.length;i_0++){
//					Date _GameInfoResponse_GameInfoResult_Goals_tGoal_dGame = _GameInfoResponse_GameInfoResult_Goals_tGoal[i_0].getDGame();
//					System.out.println("_GameInfoResponse_GameInfoResult_Goals_tGoal_dGame: "+_GameInfoResponse_GameInfoResult_Goals_tGoal_dGame);
//					java.lang.Integer _GameInfoResponse_GameInfoResult_Goals_tGoal_iMinute = _GameInfoResponse_GameInfoResult_Goals_tGoal[i_0].getIMinute();
//					System.out.println("_GameInfoResponse_GameInfoResult_Goals_tGoal_iMinute: "+_GameInfoResponse_GameInfoResult_Goals_tGoal_iMinute);
//					java.lang.String _GameInfoResponse_GameInfoResult_Goals_tGoal_sPlayerName = _GameInfoResponse_GameInfoResult_Goals_tGoal[i_0].getSPlayerName();
//					System.out.println("_GameInfoResponse_GameInfoResult_Goals_tGoal_sPlayerName: "+_GameInfoResponse_GameInfoResult_Goals_tGoal_sPlayerName);
//					java.lang.String _GameInfoResponse_GameInfoResult_Goals_tGoal_sTeamName = _GameInfoResponse_GameInfoResult_Goals_tGoal[i_0].getSTeamName();
//					System.out.println("_GameInfoResponse_GameInfoResult_Goals_tGoal_sTeamName: "+_GameInfoResponse_GameInfoResult_Goals_tGoal_sTeamName);
//					java.lang.String _GameInfoResponse_GameInfoResult_Goals_tGoal_sTeamFlag = _GameInfoResponse_GameInfoResult_Goals_tGoal[i_0].getSTeamFlag();
//					System.out.println("_GameInfoResponse_GameInfoResult_Goals_tGoal_sTeamFlag: "+_GameInfoResponse_GameInfoResult_Goals_tGoal_sTeamFlag);
//				}
//			}
		}
		catch(AxisFault axisFault){
			axisFault.printStackTrace();
		}
		catch(RemoteException remEx){
			remEx.printStackTrace();
		}
	}

	private static java.sql.Time axis2sqlTime(org.apache.axis2.databinding.types.Time axisTime){
		java.sql.Time sqlTime= new java.sql.Time(axisTime.getAsCalendar().getTimeInMillis());
		return sqlTime;
	}
	
}
