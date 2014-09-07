
/**
 * InfoCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.5.1  Built on : Oct 19, 2009 (10:59:00 EDT)
 */

    package eu.dataaccess.footballpool2;

    /**
     *  InfoCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class InfoCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public InfoCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public InfoCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for topGoalScorers method
            * override this method for handling normal response from topGoalScorers operation
            */
           public void receiveResulttopGoalScorers(
                    eu.dataaccess.footballpool2.InfoStub.TopGoalScorersResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from topGoalScorers operation
           */
            public void receiveErrortopGoalScorers(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for gamesPlayed method
            * override this method for handling normal response from gamesPlayed operation
            */
           public void receiveResultgamesPlayed(
                    eu.dataaccess.footballpool2.InfoStub.GamesPlayedResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from gamesPlayed operation
           */
            public void receiveErrorgamesPlayed(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for dateLastGroupGame method
            * override this method for handling normal response from dateLastGroupGame operation
            */
           public void receiveResultdateLastGroupGame(
                    eu.dataaccess.footballpool2.InfoStub.DateLastGroupGameResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from dateLastGroupGame operation
           */
            public void receiveErrordateLastGroupGame(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for allStadiumInfo method
            * override this method for handling normal response from allStadiumInfo operation
            */
           public void receiveResultallStadiumInfo(
                    eu.dataaccess.footballpool2.InfoStub.AllStadiumInfoResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from allStadiumInfo operation
           */
            public void receiveErrorallStadiumInfo(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for stadiumInfo method
            * override this method for handling normal response from stadiumInfo operation
            */
           public void receiveResultstadiumInfo(
                    eu.dataaccess.footballpool2.InfoStub.StadiumInfoResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from stadiumInfo operation
           */
            public void receiveErrorstadiumInfo(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for allForwards method
            * override this method for handling normal response from allForwards operation
            */
           public void receiveResultallForwards(
                    eu.dataaccess.footballpool2.InfoStub.AllForwardsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from allForwards operation
           */
            public void receiveErrorallForwards(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for allPlayerNames method
            * override this method for handling normal response from allPlayerNames operation
            */
           public void receiveResultallPlayerNames(
                    eu.dataaccess.footballpool2.InfoStub.AllPlayerNamesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from allPlayerNames operation
           */
            public void receiveErrorallPlayerNames(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for groupCompetitors method
            * override this method for handling normal response from groupCompetitors operation
            */
           public void receiveResultgroupCompetitors(
                    eu.dataaccess.footballpool2.InfoStub.GroupCompetitorsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from groupCompetitors operation
           */
            public void receiveErrorgroupCompetitors(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for allPlayersWithRedCards method
            * override this method for handling normal response from allPlayersWithRedCards operation
            */
           public void receiveResultallPlayersWithRedCards(
                    eu.dataaccess.footballpool2.InfoStub.AllPlayersWithRedCardsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from allPlayersWithRedCards operation
           */
            public void receiveErrorallPlayersWithRedCards(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for dateOfFirstGame method
            * override this method for handling normal response from dateOfFirstGame operation
            */
           public void receiveResultdateOfFirstGame(
                    eu.dataaccess.footballpool2.InfoStub.DateOfFirstGameResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from dateOfFirstGame operation
           */
            public void receiveErrordateOfFirstGame(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for redCardsTotal method
            * override this method for handling normal response from redCardsTotal operation
            */
           public void receiveResultredCardsTotal(
                    eu.dataaccess.footballpool2.InfoStub.RedCardsTotalResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from redCardsTotal operation
           */
            public void receiveErrorredCardsTotal(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for stadiumURL method
            * override this method for handling normal response from stadiumURL operation
            */
           public void receiveResultstadiumURL(
                    eu.dataaccess.footballpool2.InfoStub.StadiumURLResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from stadiumURL operation
           */
            public void receiveErrorstadiumURL(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for playedAtWorldCup method
            * override this method for handling normal response from playedAtWorldCup operation
            */
           public void receiveResultplayedAtWorldCup(
                    eu.dataaccess.footballpool2.InfoStub.PlayedAtWorldCupResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from playedAtWorldCup operation
           */
            public void receiveErrorplayedAtWorldCup(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for coaches method
            * override this method for handling normal response from coaches operation
            */
           public void receiveResultcoaches(
                    eu.dataaccess.footballpool2.InfoStub.CoachesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from coaches operation
           */
            public void receiveErrorcoaches(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for yellowAndRedCardsTotal method
            * override this method for handling normal response from yellowAndRedCardsTotal operation
            */
           public void receiveResultyellowAndRedCardsTotal(
                    eu.dataaccess.footballpool2.InfoStub.YellowAndRedCardsTotalResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from yellowAndRedCardsTotal operation
           */
            public void receiveErroryellowAndRedCardsTotal(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for gamesPerCity method
            * override this method for handling normal response from gamesPerCity operation
            */
           public void receiveResultgamesPerCity(
                    eu.dataaccess.footballpool2.InfoStub.GamesPerCityResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from gamesPerCity operation
           */
            public void receiveErrorgamesPerCity(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for allGames method
            * override this method for handling normal response from allGames operation
            */
           public void receiveResultallGames(
                    eu.dataaccess.footballpool2.InfoStub.AllGamesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from allGames operation
           */
            public void receiveErrorallGames(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for gameResultCodes method
            * override this method for handling normal response from gameResultCodes operation
            */
           public void receiveResultgameResultCodes(
                    eu.dataaccess.footballpool2.InfoStub.GameResultCodesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from gameResultCodes operation
           */
            public void receiveErrorgameResultCodes(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for goalsScored method
            * override this method for handling normal response from goalsScored operation
            */
           public void receiveResultgoalsScored(
                    eu.dataaccess.footballpool2.InfoStub.GoalsScoredResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from goalsScored operation
           */
            public void receiveErrorgoalsScored(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for groups method
            * override this method for handling normal response from groups operation
            */
           public void receiveResultgroups(
                    eu.dataaccess.footballpool2.InfoStub.GroupsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from groups operation
           */
            public void receiveErrorgroups(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for allGroupCompetitors method
            * override this method for handling normal response from allGroupCompetitors operation
            */
           public void receiveResultallGroupCompetitors(
                    eu.dataaccess.footballpool2.InfoStub.AllGroupCompetitorsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from allGroupCompetitors operation
           */
            public void receiveErrorallGroupCompetitors(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for dateOfLastGame method
            * override this method for handling normal response from dateOfLastGame operation
            */
           public void receiveResultdateOfLastGame(
                    eu.dataaccess.footballpool2.InfoStub.DateOfLastGameResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from dateOfLastGame operation
           */
            public void receiveErrordateOfLastGame(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for allPlayersWithYellowCards method
            * override this method for handling normal response from allPlayersWithYellowCards operation
            */
           public void receiveResultallPlayersWithYellowCards(
                    eu.dataaccess.footballpool2.InfoStub.AllPlayersWithYellowCardsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from allPlayersWithYellowCards operation
           */
            public void receiveErrorallPlayersWithYellowCards(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for yellowCardsTotal method
            * override this method for handling normal response from yellowCardsTotal operation
            */
           public void receiveResultyellowCardsTotal(
                    eu.dataaccess.footballpool2.InfoStub.YellowCardsTotalResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from yellowCardsTotal operation
           */
            public void receiveErroryellowCardsTotal(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for cities method
            * override this method for handling normal response from cities operation
            */
           public void receiveResultcities(
                    eu.dataaccess.footballpool2.InfoStub.CitiesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from cities operation
           */
            public void receiveErrorcities(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for countryNames method
            * override this method for handling normal response from countryNames operation
            */
           public void receiveResultcountryNames(
                    eu.dataaccess.footballpool2.InfoStub.CountryNamesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from countryNames operation
           */
            public void receiveErrorcountryNames(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for tournamentInfo method
            * override this method for handling normal response from tournamentInfo operation
            */
           public void receiveResulttournamentInfo(
                    eu.dataaccess.footballpool2.InfoStub.TournamentInfoResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from tournamentInfo operation
           */
            public void receiveErrortournamentInfo(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for stadiumNames method
            * override this method for handling normal response from stadiumNames operation
            */
           public void receiveResultstadiumNames(
                    eu.dataaccess.footballpool2.InfoStub.StadiumNamesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from stadiumNames operation
           */
            public void receiveErrorstadiumNames(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for numberOfGames method
            * override this method for handling normal response from numberOfGames operation
            */
           public void receiveResultnumberOfGames(
                    eu.dataaccess.footballpool2.InfoStub.NumberOfGamesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from numberOfGames operation
           */
            public void receiveErrornumberOfGames(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for teamsCompeteList method
            * override this method for handling normal response from teamsCompeteList operation
            */
           public void receiveResultteamsCompeteList(
                    eu.dataaccess.footballpool2.InfoStub.TeamsCompeteListResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from teamsCompeteList operation
           */
            public void receiveErrorteamsCompeteList(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fullTeamInfo method
            * override this method for handling normal response from fullTeamInfo operation
            */
           public void receiveResultfullTeamInfo(
                    eu.dataaccess.footballpool2.InfoStub.FullTeamInfoResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fullTeamInfo operation
           */
            public void receiveErrorfullTeamInfo(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for teams method
            * override this method for handling normal response from teams operation
            */
           public void receiveResultteams(
                    eu.dataaccess.footballpool2.InfoStub.TeamsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from teams operation
           */
            public void receiveErrorteams(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for groupCount method
            * override this method for handling normal response from groupCount operation
            */
           public void receiveResultgroupCount(
                    eu.dataaccess.footballpool2.InfoStub.GroupCountResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from groupCount operation
           */
            public void receiveErrorgroupCount(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for allDefenders method
            * override this method for handling normal response from allDefenders operation
            */
           public void receiveResultallDefenders(
                    eu.dataaccess.footballpool2.InfoStub.AllDefendersResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from allDefenders operation
           */
            public void receiveErrorallDefenders(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for allCards method
            * override this method for handling normal response from allCards operation
            */
           public void receiveResultallCards(
                    eu.dataaccess.footballpool2.InfoStub.AllCardsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from allCards operation
           */
            public void receiveErrorallCards(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for gameInfo method
            * override this method for handling normal response from gameInfo operation
            */
           public void receiveResultgameInfo(
                    eu.dataaccess.footballpool2.InfoStub.GameInfoResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from gameInfo operation
           */
            public void receiveErrorgameInfo(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for allMidFields method
            * override this method for handling normal response from allMidFields operation
            */
           public void receiveResultallMidFields(
                    eu.dataaccess.footballpool2.InfoStub.AllMidFieldsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from allMidFields operation
           */
            public void receiveErrorallMidFields(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for allGoalKeepers method
            * override this method for handling normal response from allGoalKeepers operation
            */
           public void receiveResultallGoalKeepers(
                    eu.dataaccess.footballpool2.InfoStub.AllGoalKeepersResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from allGoalKeepers operation
           */
            public void receiveErrorallGoalKeepers(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for allPlayersWithYellowOrRedCards method
            * override this method for handling normal response from allPlayersWithYellowOrRedCards operation
            */
           public void receiveResultallPlayersWithYellowOrRedCards(
                    eu.dataaccess.footballpool2.InfoStub.AllPlayersWithYellowOrRedCardsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from allPlayersWithYellowOrRedCards operation
           */
            public void receiveErrorallPlayersWithYellowOrRedCards(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for nextGame method
            * override this method for handling normal response from nextGame operation
            */
           public void receiveResultnextGame(
                    eu.dataaccess.footballpool2.InfoStub.NextGameResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from nextGame operation
           */
            public void receiveErrornextGame(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for topSelectedGoalScorers method
            * override this method for handling normal response from topSelectedGoalScorers operation
            */
           public void receiveResulttopSelectedGoalScorers(
                    eu.dataaccess.footballpool2.InfoStub.TopSelectedGoalScorersResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from topSelectedGoalScorers operation
           */
            public void receiveErrortopSelectedGoalScorers(java.lang.Exception e) {
            }
                


    }
    