import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.hsqldb.ClientConnection;
import org.hsqldb.Server;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RefineryUtilities;

/*
 * Graphs created using free source JFreeChart
 * Tutorial used located at: http://www.tutorialspoint.com/jfreechart/index.htm
 * 
*/
public class steamDB {
	
	public static void main(String[]args)throws SQLException, ClassNotFoundException{
//		insertData();
		Scanner scan = new Scanner(System.in);
		int input;
	//Welcome user to database
		System.out.println("Welcome to the Steam game database!");
		do{
		//Ask user if they want popularity info or a game recommendation
			System.out.println("Please choose a number: ");
			System.out.println("1.) Game statistics");
			System.out.println("2.) Get a game recommendation");
			System.out.println("3.) Exit");
		//scan user input,
			input = scan.nextInt();
		//sends user to appropriate method depending on input	
			if(input == 1){
				trackStats();
			}
			else if(input == 2){
				gameRec();
			}
			else if(input!=3){
				System.out.println("Sorry. Invalid response.");
			}
			
		}while(input!=3);

		System.out.println("Thank You!");
	}

	//method for tracking popularity
	public static void trackStats()throws SQLException, ClassNotFoundException{
		Scanner scan = new Scanner(System.in);
		int input;
		//loops through section until user exits
		do{
			System.out.println("Please choose an option by number:");
			System.out.println("1.) Compare all genres");
			System.out.println("2.) Examine a genre");
			System.out.println("3.) Examine a game");
			System.out.println("4.) Compare 2 games");
			System.out.println("5.) Exit");
			input = scan.nextInt();
			//sends user to appropriate method depending on input	
			if(input == 1){
				allGenres();
			}
			else if(input == 2){
				oneGenre();
			}
			else if(input == 3){
				oneGame();
			}
			else if(input == 4){
				twoGames();
			}
			else if(input!=5){
				System.out.println("Sorry. Invalid response.");
			}
		
	}while(input!=5);
	}//end trackPopularity
	
	//in-depth stats methods
	public static void allGenres() throws ClassNotFoundException, SQLException{
		//initialize scanner and variables
		Scanner scan = new Scanner(System.in);
		int input;
		ArrayList<String> results = new ArrayList<String>();
		String statement ="";
		
		//list options
		System.out.println("Please choose an option by number:");
		System.out.println("1.) View top 5 games");
		System.out.println("21.) Examine average cost by genre");
		System.out.println("3.) Examine average gameplay time by genre");
		input = scan.nextInt();
		
		//sends to top 5 method if chosen
		if(input == 1){
			top5("none");
		}
		
		//presents different costs within all genres
		if (input == 2){
			statement =  "SELECT GENRE, Cost, COUNT(Cost) "
					+"FROM GAME_INFO INNER JOIN GENRES ON GAME_INFO.Title = GENRES.Title"
					+ " GROUP BY Genre, Cost";
			//outputQuery(statement);
			storeResults(statement, results);	
			
			//initializes data set
			DefaultCategoryDataset dataset =    new DefaultCategoryDataset( );  
			
			//prints a description of our price levels
			System.out.println("Cost Level Descriptions");
			System.out.println("Level 0: free.");
			System.out.println("Level 1: very low. $0-5.");
			System.out.println("Level 2: low. $5-10.");
			System.out.println("Level 3: medium. $10-30.");
			System.out.println("Level 4: high. $30-60");
			System.out.println("Level 5: very high. >$60");

			//loops through arraylist of results and stores them in the dataset in proper format
			for(int i = 0; i<results.size()-1; i+=3){
				//	System.out.println(results.get(i)+", "+results.get(i+1));
				double priceCount = Double.parseDouble(results.get(i+2));
				String genreAbbrev = "";
				if(results.get(i).length()<3)
					genreAbbrev = results.get(i);
				else
					genreAbbrev = results.get(i).substring(0, 3);
				dataset.addValue(priceCount, results.get(i+1) , genreAbbrev);      
			}
			//set up and output graph
			testGraph chart = new testGraph("Cost Stats", "Cost Comparison Between Genres", dataset,"Genre", "Number of Games");
		      chart.pack( );        
		      RefineryUtilities.centerFrameOnScreen( chart );        
		      chart.setVisible( true ); 
			
		}
		//presents Avg gameplay time across all genres
		if(input == 3){
		 DefaultCategoryDataset dataset =    new DefaultCategoryDataset( );  
			statement = "SELECT Genre, AVG(HoursRecorded) AS Hours "
					  +"FROM (GAME_INFO INNER JOIN GENRES ON GAME_INFO.Title = GENRES.Title) INNER JOIN REVIEWS ON REVIEWS.Title = GENRES.Title "
					  + " GROUP BY Genre;";
				storeResults(statement, results);
			//puts info from results array into dataset
			for(int i = 0; i<results.size()-1; i+=2){
				//	System.out.println(results.get(i)+", "+results.get(i+1));
				double hours = Double.parseDouble(results.get(i+1)); 
				String genreAbbrev = "";
				if(results.get(i).length()<3)
					genreAbbrev = results.get(i);
				else
					genreAbbrev = results.get(i).substring(0, 3);
				dataset.addValue(hours, "hours" , ""+genreAbbrev);      
			}
			//System.out.println(results);
			//sets up and outputs graph
			testGraph chart = new testGraph("Gameplay Statistics", "Avg Hours vs. Genres", dataset, "Genre","Hours");
		      chart.pack( );        
		      RefineryUtilities.centerFrameOnScreen( chart );        
		      chart.setVisible( true ); 
		}
		
		
		
		

	}
	public static void oneGenre() throws ClassNotFoundException, SQLException{
		Scanner scan = new Scanner(System.in);
		String statement;
		//obtains and checks genre
		System.out.println("What genre would you like to see information for? (If you would like to see a list of all genres in the database, please input 'list')");
		scan.nextLine();
		String genre = scan.nextLine().toLowerCase();
		
		//outputs a list of all genres if user requests so
		if(genre.equals("list")){
			statement = "SELECT DISTINCT Genre FROM GENRES;";
			outputQuery(statement);
			System.out.println("What genre would you like?");
			scan.nextLine();
			genre = scan.nextLine().toLowerCase();
		}
		
		//checks if genre is valid
		statement = "SELECT Genre FROM GENRES WHERE Genre = '"+genre+"';";
		boolean validGenre = isValid(statement);
		
		//if not valid, tells user and does not continue
	 	if(!validGenre){
			System.out.println("That genre is not listed in our database, sorry.");
		}
		//continues otherwise
		else{
		ArrayList<String> results = new ArrayList<String>();
		int input;
			System.out.println("Please choose an option by number:");
			System.out.println("1.) View top 5 games in "+genre);
			System.out.println("2.) View all games in "+genre);
//			System.out.println("3.) Examine cost vs. release date");
			System.out.println("3.) View average hours played over time");
			System.out.println("4.) View ESRB rating stats");
			System.out.println("5.) View DLC info");
			input = scan.nextInt();
			
			//sends to top 5 if chosen
			if(input == 1){
				top5(genre);
			}
			//uses a query to output list of games in genre
			if (input == 2){
				statement = "SELECT Title FROM GENRES WHERE Genre = '"+genre+"'";
				outputQuery(statement);
			}
			

			//shows avg hours recorded v review date
			if(input == 3){
				DefaultCategoryDataset dataset =    new DefaultCategoryDataset( );  
				statement =" SELECT REVIEWS.DATE, AVG(REVIEWS.HoursRecorded) AS Hours "
						+ "FROM ((GAME_INFO INNER JOIN GENRES ON GAME_INFO.Title = GENRES.Title) INNER JOIN REVIEWS ON REVIEWS.Title = GENRES.Title)"
						+ "WHERE Genre = '"+genre+"' "
						+ "GROUP BY REVIEWS.Date ORDER BY REVIEWS.Date;";
				storeResults(statement, results);
				//puts results in dataset
				for(int i = 0; i<results.size()-1; i+=2){
					//	System.out.println(results.get(i)+", "+results.get(i+1));
					double hours = Double.parseDouble(results.get(i+1)); 
					dataset.addValue(hours, "Hours" , ""+results.get(i));      
				}
				//presents graph
				lineChart chart = new lineChart("Hours Vs Review Date", "Hours Vs. Review Date", dataset,"Review Date", "# Hours");
			      chart.pack( );        
			      RefineryUtilities.centerFrameOnScreen( chart );        
			      chart.setVisible( true ); 
			}
			
			/*MAKE SURE ALL RATINGS ARE IN SAME FORMAT*/
			if (input == 4){
				 DefaultCategoryDataset dataset =    new DefaultCategoryDataset( );  
					statement = "SELECT GAME_INFO.ESRBRating, COUNT(GAME_INFO.ESRBRating) "
							  +"FROM (GAME_INFO INNER JOIN GENRES ON GAME_INFO.Title = GENRES.Title)"
							  + " WHERE GENRES.Genre = '"
							  + genre + "' GROUP BY GAME_INFO.ESRBRating";
					//stores query results in arraylist	
					storeResults(statement, results);
					
					//puts data from arraylist in a dataset
					for(int i = 0; i<results.size()-1; i+=2){
						//	System.out.println(results.get(i)+", "+results.get(i+1));
						double numberGames = Double.parseDouble(results.get(i+1)); 
						dataset.addValue(numberGames, "Number of Games" , ""+results.get(i).toUpperCase());      
					}
//					System.out.println(results);
					//shows graph
					testGraph chart = new testGraph("ESRB Statistics", "Number of Games per Rating", dataset,"Rating", "Number of Games");
				      chart.pack( );        
				      RefineryUtilities.centerFrameOnScreen( chart );        
				      chart.setVisible( true ); 
			}
			
			//puts count of games that do and dont offer DLC in one genre
			if (input == 5){
				 DefaultCategoryDataset dataset =    new DefaultCategoryDataset( );  
					statement =   "SELECT DLC, COUNT(DLC) "
							  +"FROM (GAME_INFO INNER JOIN GENRES ON GAME_INFO.Title = GENRES.Title)"
							  + " WHERE Genre = '" 
							  + genre + "' GROUP BY DLC";
					//stores results of query
						storeResults(statement, results);
					//stores results in a dataset
					for(int i = 0; i<results.size()-1; i+=2){
						//	System.out.println(results.get(i)+", "+results.get(i+1));
						double numberGames = Double.parseDouble(results.get(i+1)); 
						dataset.addValue(numberGames, "Number of Games" , ""+results.get(i));      
					}
					//shows in graph form
					//System.out.println(results);
					testGraph chart = new testGraph("DLC Statistics", "Number of Games that Offer DLC", dataset,"Offers DLC?", "Number of Games");
				      chart.pack( );        
				      RefineryUtilities.centerFrameOnScreen( chart );        
				      chart.setVisible( true ); 
			}
			
			
			
		}
	}
	public static void oneGame() throws ClassNotFoundException, SQLException{
		//initialize variables
		Scanner scan = new Scanner(System.in);
		String input;
		String statement;
		//gets game from user
		System.out.println("What game would you like? (If you would like to see a list of all games in the database, please input 'list').");	
		scan.nextLine();
		String title = scan.nextLine().toLowerCase();
		
		//outputs list of titles if user requests
		if(title.equals("list")){
			statement = "SELECT DISTINCT Title FROM GAME_INFO;";
			outputQuery(statement);
			System.out.println("What title would you like?");
			scan.nextLine();
			title = scan.nextLine().toLowerCase();
		}
		
		//checks if title is valid
		statement = "SELECT Title FROM GAME_INFO WHERE Title = '"+title+"';";
		boolean validTitle = isValid(statement);
		
		//if not valid, tells user and does not continue
		if(!validTitle){
			System.out.println("That game is not listed in our database, sorry.");
		}
		
		
		else{
			ArrayList<String> results = new ArrayList<String>();
			//presents options to user
				System.out.println("Please choose an option by number:");
				System.out.println("1.) View game info for "+title);
				System.out.println("2.) View hours over time");
				System.out.println("3.) View recommendation info");
				
				int inputChoice = scan.nextInt();
				
				//outputs basic info and genre for requested game
				if(inputChoice == 1){
					statement = "SELECT * FROM GAME_INFO WHERE Title = '"+title+"';"; 
					outputQuery(statement);
					statement = "SELECT Genre FROM GENRES WHERE Title = '"+title+"';"; 
					outputQuery(statement);
				}
				
				//view hours recorded over time
				if(inputChoice == 2){
					DefaultCategoryDataset dataset =    new DefaultCategoryDataset( );  
					statement = "SELECT REVIEWS.DATE, AVG(REVIEWS.HoursRecorded) AS Hours "
							+ "FROM (GAME_INFO INNER JOIN REVIEWS ON REVIEWS.Title = GAME_INFO.Title)"
							+ "WHERE REVIEWS.Title = '"+title+"' "
							+ "GROUP BY REVIEWS.Date ORDER BY REVIEWS.Date;";
					//stores results in arraylist
					storeResults(statement, results);
					//puts results in dataset
					for(int i = 0; i<results.size()-1; i+=2){
						//	System.out.println(results.get(i)+", "+results.get(i+1));
						double hours = Double.parseDouble(results.get(i+1)); 
						dataset.addValue(hours, "Hours" , ""+results.get(i));      
					}
					
					//shows graph
					lineChart chart = new lineChart("Hours Over Time", "Hours Vs. Review Date", dataset," Date", "# Hours");
				      chart.pack( );        
				      RefineryUtilities.centerFrameOnScreen( chart );        
				      chart.setVisible( true ); 
					
				}
				
				//presents a count of recommended vs not recommended
				if(inputChoice == 3){
					DefaultCategoryDataset dataset =    new DefaultCategoryDataset( );  
					statement = "SELECT UserRating, COUNT(UserRating) "
							+ "FROM GAME_INFO INNER JOIN REVIEWS ON GAME_INFO.Title = REVIEWS.Title "
							+ "WHERE Title = '"+title+"' "
							+ "GROUP BY UserRating;";
					//stores results and puts in dataset
					storeResults(statement, results);
					for(int i = 0; i<results.size()-1; i+=2){
						//	System.out.println(results.get(i)+", "+results.get(i+1));
						double numRec = Double.parseDouble(results.get(i+1)); 
						dataset.addValue(numRec, "Number Recommended" , ""+results.get(i));      
					}
					//shows graph
					testGraph chart = new testGraph("Recommendations", "Recommendations", dataset," Recommendation", "Number");
				      chart.pack( );        
				      RefineryUtilities.centerFrameOnScreen( chart );        
				      chart.setVisible( true ); 
					
				}
		}
		
		
	}
	public static void twoGames() throws ClassNotFoundException, SQLException{
		Scanner scan = new Scanner(System.in);
		String input;
		String statement;
		System.out.println("What game would you like? (If you would like to see a list of all games in the database, please input 'list').");	
		scan.nextLine();
		String title1 = scan.nextLine().toLowerCase();
		
		//outputs list of titles if user requests
		if(title1.equals("list")){
			statement = "SELECT DISTINCT Title FROM GAME_INFO;";
			outputQuery(statement);
			System.out.println("What title would you like?");
			scan.nextLine();
			title1 = scan.nextLine().toLowerCase();
		}
		
		//checks if title is valid
		statement = "SELECT Title FROM GAME_INFO WHERE Title = '"+title1+"';";
		boolean validTitle = isValid(statement);
		
		//if not valid, tells user and does not continue
		if(!validTitle){
			System.out.println("That game is not listed in our database, sorry.");
		}
		
		else{
			//requests for second game
			System.out.println("Please input a 2nd game.");	
			scan.nextLine();
			String title2 = scan.nextLine().toLowerCase();

			
			//checks if title is valid
			statement = "SELECT Title FROM GAME_INFO WHERE Title = '"+title2+"';";
			validTitle = isValid(statement);
			
			//if not valid, tells user and does not continue
			if(!validTitle){
				System.out.println("That game is not listed in our database, sorry.");
			}
			else{
				ArrayList<String> results = new ArrayList<String>();
				//gives options to user
					System.out.println("Please choose an option by number:");
					System.out.println("1.) View game info to compare "+title1+" and "+title2);
					System.out.println("2.) Compare hours over time for each game");
					System.out.println("3.) View recommendation info");
					
					int inputChoice = scan.nextInt();
					
					if(inputChoice == 1){
						//outputs genre and game info for each game title
						System.out.println("Game 1");
						statement = "SELECT * FROM GAME_INFO WHERE Title = '"+title1+"';"; 
						outputQuery(statement);
						statement = "SELECT Genre FROM GENRES WHERE Title = '"+title1+"';"; 
						outputQuery(statement);
						System.out.println("Game 2");
						statement = "SELECT * FROM GAME_INFO WHERE Title = '"+title2+"';"; 
						outputQuery(statement);
						statement = "SELECT Genre FROM GENRES WHERE Title = '"+title2+"';"; 
						outputQuery(statement);
					}
					
					if(inputChoice == 2){
						DefaultCategoryDataset dataset =    new DefaultCategoryDataset( );  
						statement = "SELECT REVIEWS.DATE, AVG(REVIEWS.HoursRecorded) AS Hours "
								+ "FROM (GAME_INFO INNER JOIN REVIEWS ON REVIEWS.Title = GAME_INFO.Title)"
								+ "WHERE REVIEWS.Title = '"+title1+"' "
								+ "GROUP BY REVIEWS.Date ORDER BY REVIEWS.Date;";
						//stores results in arraylist
						storeResults(statement, results);
						//puts results in dataset
						for(int i = 0; i<results.size()-1; i+=2){
							//	System.out.println(results.get(i)+", "+results.get(i+1));
							double hours = Double.parseDouble(results.get(i+1)); 
							dataset.addValue(hours, title1 , ""+results.get(i));      
						}
						statement = "SELECT REVIEWS.DATE, AVG(REVIEWS.HoursRecorded) AS Hours "
								+ "FROM (GAME_INFO INNER JOIN REVIEWS ON REVIEWS.Title = GAME_INFO.Title)"
								+ "WHERE REVIEWS.Title = '"+title2+"' "
								+ "GROUP BY REVIEWS.Date ORDER BY REVIEWS.Date;";
						//stores results in arraylist
						storeResults(statement, results);
						//puts results in dataset
						for(int i = 0; i<results.size()-1; i+=2){
							//	System.out.println(results.get(i)+", "+results.get(i+1));
							double hours = Double.parseDouble(results.get(i+1)); 
							dataset.addValue(hours, title2 , ""+results.get(i));      
						}
						lineChart chart = new lineChart("Hours Over Time", "Hours Vs. Review Date", dataset," Date", "# Hours");
					      chart.pack( );        
					      RefineryUtilities.centerFrameOnScreen( chart );        
					      chart.setVisible( true ); 
					}
					
					if(inputChoice == 3){
						DefaultCategoryDataset dataset =    new DefaultCategoryDataset( );  
						statement = "SELECT UserRating, COUNT(UserRating) "
							+ "FROM GAME_INFO INNER JOIN REVIEWS ON GAME_INFO.Title = REVIEWS.Title "
							+ "WHERE Title = '"+title1+"' "
							+ "GROUP BY UserRating;";
						storeResults(statement, results);
						for(int i = 0; i<results.size()-1; i+=2){
							//	System.out.println(results.get(i)+", "+results.get(i+1));
							double numRec = Double.parseDouble(results.get(i+1)); 
							dataset.addValue(numRec, title1 , ""+results.get(i));      
						}
						statement =  "SELECT UserRating, COUNT(UserRating) "
								+ "FROM GAME_INFO INNER JOIN REVIEWS ON GAME_INFO.Title = REVIEWS.Title "
								+ "WHERE Title = '"+title2+"' "
								+ "GROUP BY UserRating;";
						storeResults(statement, results);
						for(int i = 0; i<results.size()-1; i+=2){
							//	System.out.println(results.get(i)+", "+results.get(i+1));
							double numRec = Double.parseDouble(results.get(i+1)); 
							dataset.addValue(numRec, title2 , ""+results.get(i));      
						}
						testGraph chart = new testGraph("Recommendations", "Recommendations", dataset," Recommendation", "Number");
					      chart.pack( );        
					      RefineryUtilities.centerFrameOnScreen( chart );        
					      chart.setVisible( true ); 
						
					}
			}
		}
		
		
		
	
	}
	public static void top5(String genre) throws ClassNotFoundException, SQLException{

		int [] score= new int[5];
		String [] titles = new String[5];
		int count = 0;
		String statement = "";
		ArrayList<String> results = new ArrayList<String>();
		String title="";
		String overallReview= "";
		String cost = "";
		String DLC="";


		if(genre.equals("none")){
			statement = "SELECT title , overallReview , cost , DLC FROM GAME_INFO;"; 
			storeResults(statement, results);

		}else{
			statement = "SELECT GAME_INFO.title , overallReview , cost , DLC FROM GAME_INFO INNER JOIN GENRES ON GAME_INFO.title = GENRES.Title"
					+ " WHERE Genre = '"+genre+"';" ;
	//	storeResults(statement, results);
		}
		System.out.println(results);
		for(int i =0; i<results.size()-1; i+=4){
			count = 0;
			title = results.get(i);
//			System.out.println(title);
			overallReview = results.get(i+1);
//			System.out.println(overallReview);
			cost = results.get(i+2);
//			System.out.println(cost);
			DLC = results.get(i+3);
//			System.out.println(DLC);
//			System.out.println();

			if (overallReview.equals("Overwheleming Positive"))
				count=+3;

			if (overallReview.equals("Very Positive"))
				count+=2;

			if(overallReview.equals("Positive"))
				count+=1;

			if(cost.equals("Free")|| cost.equals("V Low"))
				count+=1;

			if(DLC.equals("yes"))
				count+=1;

			boolean larger = false;
			int lowestIndex = 0;
			
			for (int j = 0; j<score.length; j++){
				if(count > score[j]){
					larger = true;
//					System.out.println(larger+"\n");
				}
									
				if(score[j]<score[lowestIndex]){
					lowestIndex = j;	
				}

			}
			if(larger == true)
				swapLowest(score, titles, lowestIndex, title, count);
			
			
		}

	bubble(score, titles);
		
		System.out.println();
		for(int i = 0; i < titles.length; i++){
			System.out.println(i+1+".) "+titles[i]);
		}
		System.out.println();



	}


	public static void swapLowest(int [] score, String [] titles, int indexLowest, String title, int count){
		
		score[indexLowest] = count;
		titles[indexLowest] = title;
		
	}
	public static void bubble(int [] score, String [] titles){

		for(int i = 0; i< score.length; i++){
			for(int j = i; j < score.length; j++){
				if(score[i] > score[j]){
					swapScores(i,j,score);
					swapTitles(i,j, titles);
				}
			}
		}

	}

	public static void swapScores(int i, int j, int[] score){

		int temp = score[i];
		score[i] = score[j];
		score[j] = temp;
	}

	public static void swapTitles(int i, int j, String [] titles){

		String temp = titles[i];
		titles[i] = titles[j];
		titles[j] = temp;
	}
	 


	

	//method for game recommender
	public static void gameRec()throws SQLException, ClassNotFoundException{

		String statement ="";
		Scanner scan = new Scanner(System.in);
		int choice;
	//asks user their recommendation preference.
		do{
			System.out.println("Would you like a recommendation based on:"
					+ " \n1.)a genre?"
					+ "\n2.)a specific game?");
			System.out.println("Please enter a number");
			choice = scan.nextInt();
			if(choice!=1&&choice!=2){
				System.out.println("invalid response.");
			}
		}while(choice!=1&&choice!=2);
		
		//if choose genre
		if(choice == 1){
			System.out.println("What genre would you like a recommendation for? (If you would like to see a list of all games in the database, please input 'list')");
			scan.nextLine();
			String genre = scan.nextLine().toLowerCase();
			
			//outputs a list of all genres if user requests so
			if(genre.equals("list")){
				statement = "SELECT DISTINCT Genre FROM GENRES;";
				outputQuery(statement);
				System.out.println("What genre would you like a recommendation for?");
				scan.nextLine();
				genre = scan.nextLine().toLowerCase();
			}
			
			//checks if genre is valid
			statement = "SELECT Genre FROM GENRES WHERE Genre = '"+genre+"';";
			boolean validGenre = isValid(statement);
			
			//if not valid, tells user and does not continue
			if(!validGenre){
				System.out.println("That genre is not listed in our database, sorry.");
			}
			
			//otherwise, continues on
			else{
				//additional factors
				System.out.println("Is there another factor that you would like to take into consideration?");
				System.out.println("1. Cost");
				System.out.println("2. ESRB Rating");
				System.out.println("3. Gameplay Time");
				System.out.println("4. Release Date");
				System.out.println("Please input your choice by numbers 1-4. If none, type any other number.");
				int factor = scan.nextInt();
				//sends to recommendation generator
				String recommendation = genreRec(genre, factor);
				//prints recommendation
				statement = "SELECT * FROM GAME_INFO WHERE Title = '"+recommendation+"';";
				outputQuery(statement);
			}	
		}
		
		//if choose game
		else if(choice == 2){
			System.out.println("What game would you like? (If you would like to see a list of all games in the database, please input 'list').");	
			scan.nextLine();
			String title = scan.nextLine().toLowerCase();
			
			//outputs list of titles if user requests
			if(title.equals("list")){
				statement = "SELECT DISTINCT Title FROM GAME_INFO;";
				outputQuery(statement);
				System.out.println("What title would you like a recommendation for?");
				scan.nextLine();
				title = scan.nextLine().toLowerCase();
			}
			
			//checks if title is valid
			statement = "SELECT Title FROM GAME_INFO WHERE Title = '"+title+"';";
			boolean validTitle = isValid(statement);
			
			//if not valid, tells user and does not continue
			if(!validTitle){
				System.out.println("That game is not listed in our database, sorry.");
			}
			
			//otherwise, continues
			else{
				//additional factors
				System.out.println("Is there another factor that you would like to take into consideration?");
				System.out.println("1. Cost");
				System.out.println("2. ESRB Rating");
				System.out.println("3. Gameplay Time");
				System.out.println("4. Release Date");
				System.out.println("Please input your first choice by numbers 1-4. If none, type any other number.");
				int factor = scan.nextInt();
				//sends to recommendation generator
				String recommendation = titleRec(title, factor);
				//prints recommendation
				statement = "SELECT * FROM GAME_INFO WHERE Title = '"+recommendation+"';";
				outputQuery(statement);
			}
				
			
		}
		
		
		
		
	}//end gameRec	

	public static String genreRec(String genre, int factor)throws SQLException, ClassNotFoundException{
		Scanner scan = new Scanner(System.in);
		Random rand = new Random();
		String statement = "";
		String finalRec ="";
		ArrayList<String> results = new ArrayList<String>();
		
	//determines additional factor info for recommendations
		
		//cost
		if(factor == 1){
			statement = "SELECT Title  FROM GENRES  WHERE Genre = '"+genre+"' AND" +
					" Title IN (SELECT Title FROM GAME_INFO" +
					"  WHERE Cost IN('Free', 'V Low', 'Low'));";
			storeResults(statement, results);					  	
		}
		
		//ESRB rating
		else if(factor == 2){
			statement = "SELECT Title "
					+ "FROM GAME_INFO INNER JOIN GENRES ON GAME_INFO.Title = GENRES.Title "
					+ "WHERE GENRE = '"+genre+"' AND ESRBRating IN('E', 'E10', 'No Rating');";
			storeResults(statement, results);
		}
		
		//play time
		else if(factor == 3){
			System.out.println("Would you like a game with:");
			System.out.println("1. Shorter average play time");
			System.out.println("2. Longer play time");
			int playTime = scan.nextInt();
			//if shorter playtime
			if(playTime == 1){
				statement = "SELECT REVIEWS.Title "
						+ "FROM REVIEWS INNER JOIN GENRES ON REVIEWS.Title = GENRES.Title "
						+ "WHERE GENRE = '"+genre+"'"
						+ "GROUP BY REVIEWS.Title"
						+ " HAVING AVG(REVIEWS.hoursrecorded)<100;";
				storeResults(statement, results);
			}
			//if longer playtime
			else{
				statement = "SELECT REVIEWS.Title "
						+ "FROM REVIEWS INNER JOIN GENRES ON REVIEWS.Title = GENRES.Title "
						+ "WHERE GENRE = '"+genre+"'"
						+ "GROUP BY REVIEWS.Title"
						+ " HAVING AVG(REVIEWS.hoursrecorded)>100;";
				storeResults(statement, results);
			}
		}
		
		//release date
		else if(factor == 4){
			statement = "SELECT GAME_INFO.TITLE "
					+ "FROM GAME_INFO INNER JOIN GENRES ON GAME_INFO.TITLE = GENRES.TITLE   "
					+ "WHERE Genre = '"+genre+"' AND ReleaseDate > '2014-12-31';";
			storeResults(statement, results);
		}
		
		//if they didn't chose a factor
		else {
			statement = "SELECT GAME_INFO.TITLE "
					+ "FROM GAME_INFO INNER JOIN GENRES ON GAME_INFO.TITLE = GENRES.TITLE   "
					+ "WHERE Genre = '"+genre+"';";
			storeResults(statement, results);
		}
		
		
		
//		System.out.println(results);
		
		
		//determine which to recommend
		//finds # games with best rating
		int recNumber = 0;
		String overallReview ="Overwhelmingly Positive";
		for(int i = results.size()-1; i>=0;i--){
			String recTitle = results.get(i);
			statement = "SELECT Title FROM GAME_INFO WHERE Title = '"+recTitle+"' AND OverallReview = '"+overallReview+"'";
			boolean isRec = isValid(statement);
			if(isRec)
				recNumber++;	
		}
		//if no games with best rating, goes to next lowest	
		if(recNumber==0){
			overallReview ="Very Positive";
			for(int i = results.size()-1; i>=0;i--){
				String recTitle = results.get(i);
				statement = "SELECT Title FROM GAME_INFO WHERE Title = '"+recTitle+"' AND OverallReview = '"+overallReview+"'";
				boolean isRec = isValid(statement);
				if(isRec)
					recNumber++;	
			}
			//if no games with 2nd level, goes to lowest positive level
			if(recNumber==0){
				overallReview ="Positive";
				for(int i = results.size()-1; i>=0;i--){
					String recTitle = results.get(i);
					statement = "SELECT Title FROM GAME_INFO WHERE Title = '"+recTitle+"' AND OverallReview = '"+overallReview+"'";
					boolean isRec = isValid(statement);
					if(isRec)
						recNumber++;	
				}
			}
		}
		
	
		//removes possible results that have lower rating than the rest
			for(int i = results.size()-1; i>=0;i--){
				String recTitle = results.get(i);
				statement = "SELECT Title FROM GAME_INFO WHERE Title = '"+recTitle+"' AND OverallReview = '"+overallReview+"'";
				boolean isRec = isValid(statement);
				//if rating is not high enough, removes from list of potential recommendations
				if(!isRec)
					results.remove(i);	
			}
		
		//if arraylist is not empty, provides a recommendation 
		if(!results.isEmpty()){
			int toRec = rand.nextInt(results.size());
			finalRec = results.get(toRec);
		}
		//otherwise, informs user no recommendation can be made
			else{
				System.out.println("Sorry, a positively reviewed recommendation could not be made based on the given criteria.");
				System.out.println();
			}
		
		return finalRec;
	}
	
	public static String titleRec(String title, int factor)throws SQLException, ClassNotFoundException{
		Scanner scan = new Scanner(System.in);
		Random rand = new Random();
		String statement = "";
		String finalRec ="";
		ArrayList<String> results = new ArrayList<String>();
		
	//determines additional factor info for recommendations
		
		//cost
				if(factor == 1){
					statement = "SELECT Title  FROM GAME_INFO "
							+"  WHERE Cost IN('Free', 'V Low', 'Low');";
					storeResults(statement, results);					  	
				}
				
				//ESRB rating
				else if(factor == 2){
					statement = "SELECT Title "
							+ "FROM GAME_INFO  "
							+ "WHERE ESRBRating IN('E', 'E10', 'No Rating');";
					storeResults(statement, results);
				}
				
				//play time
				else if(factor == 3){
					System.out.println("Would you like a game with:");
					System.out.println("1. Shorter average play time");
					System.out.println("2. Longer play time");
					int playTime = scan.nextInt();
					//if shorter playtime
					if(playTime == 1){
						statement = "SELECT REVIEWS.Title "
								+ "FROM REVIEWS "
								+ "GROUP BY REVIEWS.Title"
								+ " HAVING AVG(REVIEWS.hoursrecorded)<100;";
						storeResults(statement, results);
					}
					//if longer playtime
					else{
						statement = "SELECT REVIEWS.Title "
								+ "FROM REVIEWS "
								+ "GROUP BY REVIEWS.Title"
								+ " HAVING AVG(REVIEWS.hoursrecorded)>100;";
						storeResults(statement, results);
					}
				}
				
				//release date
				else if(factor == 4){
					statement = "SELECT TITLE "
							+ "FROM GAME_INFO "
							+ "WHERE ReleaseDate > '2014-12-31';";
					storeResults(statement, results);
				}
				
				//if they didn't chose a factor
				else {
					statement = "SELECT TITLE "
							+ "FROM GAME_INFO;  ";
					storeResults(statement, results);
				}
			System.out.println(results);
			if (results.contains(title)){
				int toRemove = results.indexOf(title);
				results.remove(toRemove);
			}
			System.out.println(results);	
				
		ArrayList<String> genres = new ArrayList<String>();
		statement = "SELECT Genre FROM GENRES WHERE Title = '"+title+"';";
		storeResults(statement, genres);
		
		//finds result options with as many similar genres as the game title entered, returns the biggest int of the genre matches found in 1 game
		int maxGenreCount=0;
		for (int i = results.size()-1; i>=0;i--){
			String recTitle = results.get(i);
			int genreMatch = 0;
			for (int j = genres.size()-1; j>=0; j--){
				String currGenre = genres.get(j);
				statement = "SELECT Title FROM GENRES WHERE Title = '"+recTitle+"' AND Genre = '"+currGenre+"'";
				if(isValid(statement)){
					genreMatch++;
				}
			}
			if(genreMatch>maxGenreCount)
				maxGenreCount = genreMatch;
		}
		
		//if a game doesn't match the top number, removes from list of possible results
		for (int i = results.size()-1; i>=0;i--){
			String recTitle = results.get(i);
			int genreMatch = 0;
			for (int j = genres.size()-1; j>=0; j--){
				String currGenre = genres.get(j);
				statement = "SELECT Title FROM GENRES WHERE Title = '"+recTitle+"' AND Genre = '"+currGenre+"'";
				if(isValid(statement)){
					genreMatch++;
				}
			}
			if(genreMatch!=maxGenreCount)
				results.remove(i);
		}
		
		
		//determine which to recommend
				int recNumber = 0;
			//finds # games with best rating
				String overallReview ="Overwhelmingly Positive";
				for(int i = results.size()-1; i>=0;i--){
					String recTitle = results.get(i);
					statement = "SELECT Title FROM GAME_INFO WHERE Title = '"+recTitle+"' AND OverallReview = '"+overallReview+"'";
					boolean isRec = isValid(statement);
					if(isRec)
						recNumber++;	
				}
			//if no games with best rating, goes to next lowest	
				if(recNumber==0){
					overallReview ="Very Positive";
					for(int i = results.size()-1; i>=0;i--){
						String recTitle = results.get(i);
						statement = "SELECT Title FROM GAME_INFO WHERE Title = '"+recTitle+"' AND OverallReview = '"+overallReview+"'";
						boolean isRec = isValid(statement);
						if(isRec)
							recNumber++;	
					}
				//if no games with 2nd level, goes to lowest positive level
					if(recNumber==0){
						overallReview ="Positive";
						for(int i = results.size()-1; i>=0;i--){
							String recTitle = results.get(i);
							statement = "SELECT Title FROM GAME_INFO WHERE Title = '"+recTitle+"' AND OverallReview = '"+overallReview+"'";
							boolean isRec = isValid(statement);
							if(isRec)
								recNumber++;	
						}
					}
				}
				
			
				//removes possible results that have lower rating than the rest
					for(int i = results.size()-1; i>=0;i--){
						String recTitle = results.get(i);
						statement = "SELECT Title FROM GAME_INFO WHERE Title = '"+recTitle+"' AND OverallReview = '"+overallReview+"'";
						boolean isRec = isValid(statement);
						//if rating is not high enough, removes from list of potential recommendations
						if(!isRec)
							results.remove(i);	
					}
				
				//if arraylist is not empty, provides a recommendation 
				if(!results.isEmpty()){
					int toRec = rand.nextInt(results.size());
					finalRec = results.get(toRec);
				}
				//otherwise, informs user no recommendation can be made
					else{
						System.out.println("Sorry, a positively reviewed recommendation could not be made based on the given criteria.");
						System.out.println();
					}
				
				return finalRec;
	}
	
	
	//DB connection methods
	
	//simply prints results of query
	public static boolean outputQuery(String statement)throws SQLException, ClassNotFoundException{
		 Server hsqlServer = null;
		 try {
		 hsqlServer = new Server();
		 hsqlServer.setLogWriter(null);
		 hsqlServer.setSilent(true);
		 // Update the database name and path to your own!
		 hsqlServer.setDatabaseName(0, "SteamData");
		 hsqlServer.setDatabasePath(0, "file:C:/Users/Christine/Documents/Fall 2015/Database/final/SteamDataBaseNew/SteamDataBase/SteamData");
		 // Start the database!
		 hsqlServer.start();
		 Connection connection = null;
		 try {
		 // Getting a connection to the newly started database
		 Class.forName("org.hsqldb.jdbcDriver");
		 // Default user of the HSQLDB is 'sa' with an empty password
		 connection = DriverManager.getConnection(
		 "jdbc:hsqldb:file:C:/Users/Christine/Documents/Fall 2015/Database/final/SteamDataBaseNew/SteamDataBase/SteamData", "sa", "");
		 // Can execute your SQL commands here!
		//if its a query, executes this statement to print results
		 ResultSet rs = connection.prepareStatement(
				 statement
				).executeQuery();
		 		if (!rs.isBeforeFirst() ) {    
		 			return false;
		 			}
				ResultSetMetaData rsmd = rs.getMetaData();
				int numberOfColumns = rsmd.getColumnCount();
				for (int i = 1; i <= numberOfColumns; i++) {
				 if (i > 1)
				 System.out.print(",\t\t");
				 String columnName = rsmd.getColumnName(i);
				 System.out.print(columnName);
				}
				System.out.println("");
				while (rs.next()) {
				 for (int i = 1; i <= numberOfColumns; i++) {
				 if (i > 1)
				 System.out.print(",\t\t");
				 String columnValue = rs.getString(i);
				 System.out.print(columnValue);
				 }
				 System.out.println("");
				}
		 
		 } finally {
		// Closing the connection
		if (connection != null)
		 connection.close();
		 }
		 } finally {
		 // Closing the server
		 if (hsqlServer != null)
		 hsqlServer.stop();
		 }
		return true;
	}
	
	//checks to see if an input value is in the DB 
	public static boolean isValid(String statement)throws SQLException, ClassNotFoundException{
		Server hsqlServer = null;
		 try {
		 hsqlServer = new Server();
		 hsqlServer.setLogWriter(null);
		 hsqlServer.setSilent(true);
		 // Update the database name and path to your own!
		 hsqlServer.setDatabaseName(0, "SteamData");
		 hsqlServer.setDatabasePath(0, "file:C:/Users/Christine/Documents/Fall 2015/Database/final/SteamDataBaseNew/SteamDataBase/SteamData");
		 // Start the database!
		 hsqlServer.start();
		 Connection connection = null;
		 try {
		 // Getting a connection to the newly started database
		 Class.forName("org.hsqldb.jdbcDriver");
		 // Default user of the HSQLDB is 'sa' with an empty password
		 connection = DriverManager.getConnection(
		 "jdbc:hsqldb:file:C:/Users/Christine/Documents/Fall 2015/Database/final/SteamDataBaseNew/SteamDataBase/SteamData", "sa", "");
		 // Can execute your SQL commands here!
		 ResultSet rs = connection.prepareStatement(
				 statement
				).executeQuery();
		 //if no results, returns false
		 		if (!rs.isBeforeFirst() ) {    
		 			return false;
		 			}
		 		return true;
				
		 
		 } finally {
		// Closing the connection
		if (connection != null)
		 connection.close();
		 }
		 } finally {
		 // Closing the server
		 if (hsqlServer != null)
		 hsqlServer.stop();
		 }
	}
	
	//would store results of a query in a String ArrayList
	public static void storeResults(String statement, ArrayList<String> results)throws SQLException, ClassNotFoundException{
		Server hsqlServer = null;
		 try {
		 hsqlServer = new Server();
		 hsqlServer.setLogWriter(null);
		 hsqlServer.setSilent(true);
		 // Update the database name and path to your own!
		 hsqlServer.setDatabaseName(0, "SteamData");
		 hsqlServer.setDatabasePath(0, "file:C:/Users/Christine/Documents/Fall 2015/Database/final/SteamDataBaseNew/SteamDataBase/SteamData");
		 // Start the database!
		 hsqlServer.start();
		 Connection connection = null;
		 try {
		 // Getting a connection to the newly started database
		 Class.forName("org.hsqldb.jdbcDriver");
		 // Default user of the HSQLDB is 'sa' with an empty password
		 connection = DriverManager.getConnection(
		 "jdbc:hsqldb:file:C:/Users/Christine/Documents/Fall 2015/Database/final/SteamDataBaseNew/SteamDataBase/SteamData", "sa", "");
		 // Can execute your SQL commands here!
		 ResultSet rs = connection.prepareStatement(
				 statement
				).executeQuery();
		 
		ResultSetMetaData rsmd = rs.getMetaData();
		int numberOfColumns = rsmd.getColumnCount();
		for (int i = 1; i <= numberOfColumns; i++) {
		 String columnName = rsmd.getColumnName(i);
		}
		while (rs.next()) {
		 for (int i = 1; i <= numberOfColumns; i++) {
			 results.add(rs.getString(i));
		
		 }
		}
		 
		 } finally {
		// Closing the connection
		if (connection != null)
		 connection.close();
		 }
		 } finally {
		 // Closing the server
		 if (hsqlServer != null)
		 hsqlServer.stop();
		 }
		 
	}
	
	
	//returns a count 
	public static int returnCount(String statement)throws SQLException, ClassNotFoundException{
		 Server hsqlServer = null;
		 try {
		 hsqlServer = new Server();
		 hsqlServer.setLogWriter(null);
		 hsqlServer.setSilent(true);
		 // Update the database name and path to your own!
		 hsqlServer.setDatabaseName(0, "SteamData");
		 hsqlServer.setDatabasePath(0, "file:C:/Users/Christine/Documents/Fall 2015/Database/final/SteamDataBaseNew/SteamDataBase/SteamData");
		 // Start the database!
		 hsqlServer.start();
		 Connection connection = null;
		 try {
		 // Getting a connection to the newly started database
		 Class.forName("org.hsqldb.jdbcDriver");
		 // Default user of the HSQLDB is 'sa' with an empty password
		 connection = DriverManager.getConnection(
		 "jdbc:hsqldb:file:C:/Users/Christine/Documents/Fall 2015/Database/final/SteamDataBaseNew/SteamDataBase/SteamData", "sa", "");
		 // Can execute your SQL commands here!
		 ResultSet rs = connection.prepareStatement(
				 statement
				).executeQuery();
		 
		ResultSetMetaData rsmd = rs.getMetaData();
		int numberOfColumns = rsmd.getColumnCount();
		for (int i = 1; i <= numberOfColumns; i++) {
		 String columnName = rsmd.getColumnName(i);
		}
		while (rs.next()) {
		 for (int i = 1; i <= numberOfColumns; i++) {
		 String columnValue = rs.getString(i);
		 //changes count value from statement to an int and returns
		 int count = Integer.parseInt(columnValue);
		 return count;
		 }
		}
		 
		 } finally {
		// Closing the connection
		if (connection != null)
		 connection.close();
		 }
		 } finally {
		 // Closing the server
		 if (hsqlServer != null)
		 hsqlServer.stop();
		 }
		return 0;
	}
	
	
	public static void insertData()throws SQLException, ClassNotFoundException{
		Server hsqlServer = null;
		 try {
		 hsqlServer = new Server();
		 hsqlServer.setLogWriter(null);
		 hsqlServer.setSilent(true);
		 // Update the database name and path to your own!
		 hsqlServer.setDatabaseName(0, "SteamData");
		 hsqlServer.setDatabasePath(0, "file:C:/Users/Christine/Documents/Fall 2015/Database/final/SteamDataBaseNew/SteamDataBase/SteamData");
		 // Start the database!
		 hsqlServer.start();
		 Connection connection = null;
		 try {
		 // Getting a connection to the newly started database
		 Class.forName("org.hsqldb.jdbcDriver");
		 // Default user of the HSQLDB is 'sa' with an empty password
		 connection = DriverManager.getConnection(
		 "jdbc:hsqldb:file:C:/Users/Christine/Documents/Fall 2015/Database/final/SteamDataBaseNew/SteamDataBase/SteamData", "sa", "");
		 // Can execute your SQL commands here!
		  connection.prepareStatement("INSERT INTO GAME_INFO(Title, ReleaseDate, Developer, DLC, ESRBRating, OverallReview, Cost, About) values (" 
				  
				  
+" ('are you smarter than a 5th grader',		 '2015-08-31',		 'Black Lantern Studios',				'no',	 		'E'	,	 'Mostly Negative',	 		'Medium', 		'How well do you remember the 5th grade? Was it as easy as you think it was? And most importantly do you recall anything you learned? CoMediumian Jeff Foxworthy gathers a group of intelligent full-grown men and women to test their knowledge against children. Watch in awe as these people try to prove that they are in fact smarter than a 5th grader.'), "
+" ('iggys egg adventure'	,	 				'2015-08-28'	,	 'Ginger Labs LLC', 					'no', 		 'T' ,		'Positive', 				'Low', 			'Iggys Egg Adventure is a prehistoric platformer 75 million years in the making. You play as Iggy a baby Velociraptor who must rescue his mother from her evil caveman captors while collecting as many of her eggs as you can along the way.'), " 
+" ('time machine vr',		 					'2015-08-27',		 'Minority Mediumia Inc',				'no',	 	 'T',		 'Positive',		 		'Medium',		'A plague is consuming humankind. Civilization faces extinction. As a time-travelling scientist you must go back to the Jurassic and face dinosaurs whose DNA holds the key to our survival. Pilot your time machine through prehistoric oceans investigate these beasts and save the human race.'), " 
+" ('lord of the dark castle',					 '2015-04-20', 		'Craze Creative Studios', 				'no', 		 	'E',	'Mostly Positive', 			'Low',		 	'Lord of the Dark Castle is a casual turn-based retro style roguelike dungeon crawler with elements of strategy. Randomized levels lots of skills without dependencies and lots of spells to cast. Challenging Monster AI - play it smart or die. Remember: Death is Permanent!'), " 
+" ('axis football', 							'2015-07-15',		 'Axis Games',							'no', 			'E',	 'Mostly Positive', 		'Medium', 		'Axis Football is a 3D American Football Simulation Game that features a unique aiMedium passing system. Play in a single match season mode or local multiplayer! Offers basic mod support for team rosters.'), " 
+" ('contradiction spot the liar',				'2015-07-10',		 'Baggy Cat Ltd',						'no',	 	 'T'		 'Overwhelmingly Positive', 'Low',		 	'Spot the lie and beat the liars in Contradiction - the all-video murder mystery adventure!'), " 
+" ('ground control ii operation exodus',		 '2004-06-18',		'Massive Entertainment',				'no',	 		 'T',	 'Positive',				 'V Low', 		'Its 2741 and the universe is at war. The Terran Empire is laying siege to the NSA a treaty of colonies that upholds freedom and democracy. After losing the battle in space the NSA is forced to retreat to its last stronghold. Can you snatch victory from impending doom?'), "
+" ('subspace continuum',		 				'2015-07-03',		 'Virgin Interative Entertainment',		'no',  			'E',	 'Very Positive',		 	'Free', 		'Subspace Continuum is the longest running massively multiplayer online game. Player run 100% free to play always. From the players for the players. Subspace Continuum: Meet people from all over the world then kill them!') "

				

				  
				  + ");").execute();

		connection.prepareStatement("INSERT INTO REVIEWS(Title, USER, UserRating, HoursRecorded, Date) "
				+ "values ("
				+" ('are you smarter than a 5th grader',			 'casual'), "
				+" ('iggys egg adventure', 					'action'), " 
				+" ('iggys egg adventure',					 'adventure'), "
				+" ('iggys egg adventure',					'indie'), "
				+" ('time machine vr', 						'adventure'), "
				+" ('time machine vr', 						'indie'), "
				+" ('time machine vr', 						'simulation'), "
				+" ('time machine vr', 						'early access'), "
				+" ('lord of the dark castle', 				'adventure'), "
				+" ('lord of the dark castle', 				'casual'), "
				+" ('lord of the dark castle', 				'indie'), "
				+" ('lord of the dark castle', 				'rpg'), "
				+" ('lord of the dark castle',  			'strategy'), "
				+" ('axis football', 							'sports'), "
				+" ('contradiction spot the liar', 		'adventure'), "
				+" ('contradiction spot the liar', 		'rpg'), "
				+" ('contradiction spot the liar',			'strategy'), "
				+" ('ground control ii operation exodus', 	'strategy'), "
				+" ('subspace continuum', 					'action'), "
				+" ('subspace continuum', 					'casual'), "
				+" ('subspace continuum', 					'free to play'), "
				+" ('subspace continuum', 					'massively multiplayer') "
				+ ");").execute();

		connection.prepareStatement("INSERT INTO GENRES(Title, Genre) values("
				+" ('are you smarter than a 5th grader',		 'VodkaFun',		 'Not Recommended',				 11.9,		 '2015-10-23'), "
				  +" ('are you smarter than a 5th grader',		 'Jordan',		 'Not Recommended',		 			2.6,		 '2015-08-31'), "
				  +" ('are you smarter than a 5th grader',		 'Splattered Brains',		 'Not Recommended',		 1.4,		 '2015-10-16'), "
				  +" ('are you smarter than a 5th grader',		 'Merriable',		 'Not Recommended',				 7.2,		 '2015-11-29'), "
				  +" ('are you smarter than a 5th grader',		 'Protoss',		 'Not Recommended',		 			1.8,		 '2015-10-17'), "
				  +" ('are you smarter than a 5th grader',		'Young Savage',		 'Not Recommended',				 1.3,		 '2015-11-24'), "
				  +" ('are you smarter than a 5th grader',		 'Dibzahab',		 'Not Recommended',		 		1.7,		 '2015-10-17'), "
				  +" ('are you smarter than a 5th grader',		'alexandra.moe',		 'Not Recommended',		 	0.4,		 '2015-10-29'), "
				  +" ('are you smarter than a 5th grader',		 'crichton',		 'Not Recommended',		 		3.7,		 '2015-10-18'), "
				  +" ('are you smarter than a 5th grader',		 'yuru',		 'Not Recommended',					 3.9,		 '2015-10-20'), "
				  +" ('are you smarter than a 5th grader',		 'sexy hans gruber',		 'Recommended',		 	2.3,		 '2015-10-18'), "
				  +" ('are you smarter than a 5th grader',		 'a black man',		 'Recommended',		 			9.1,		 '2015-12-01'), "
				  +" ('are you smarter than a 5th grader', 		'netherportal',		 'Recommended',					 3.0, 		'2015-09-01'), "
				  +" ('are you smarter than a 5th grader',		 'pm',		 'Recommended',							 3.7,		 '2015-09-12'), "
				  +" ('are you smarter than a 5th grader',		 'sten',		 'Not Recommended,		 			0.2,		'2015-12-09'), "
				  +" ('are you smarter than a 5th grader',		 'saintfighteraqua',		 'Not Recommended',		 '0.1',		 '2015-12-07'), "
				  +" ('are you smarter than a 5th grader', 		'kz',		 'Recommended',							0.7,		 '2015-10-10'), "
				  +" ('are you smarter than a 5th grader',		 'a zesty italian',		 'Not Recommended',			 '0.4',		 '2015-11-12'), "
				  +" ('are you smarter than a 5th grader',		 'sleazus', 		'Not Recommended',				 0.1,		'2015-11-25'), "
				  +" ('are you smarter than a 5th grader',		'silversuriv',		 'Not Recommended',				0.3,		 '2015-11-14'), "
				  +" ('iggys egg adventure',		 'zemunbre',		 'Recommended',								 8.2,		 '2015-08-28'), "
				  +" ('iggys egg adventure',		 'seph.au',		 'Recommended',		 							6.0,		 '2015-10-10'), "
				  +" ('iggys egg adventure',		 'll',		 'Recommended',										 9.6,		 '2015-08-29'), "
				  +" ('iggys egg adventure',		 'itsamin',		 'Not Recommended',		 						3.5,		 '2015-09-08'), "
				  +" ('iggys egg adventure',		 'mrwoodensheep',		 'Recommended',							 6.1,		 '2015-08-30'), "
				  +" ('iggys egg adventure',		 'noahzark',		 'Recommended',	 							10.5,		 '2015-08-28'), "
				  +" ('iggys egg adventure',		 'festive littlefoot',		 'Recommended',						 13.8,		 '2015-09-20'), "
				  +" ('iggys egg adventure',		 'scruffosaurus',		 'Recommended',		 					54.6,		 '2015-08-28'), "
				  +" ('iggys egg adventure',		 'mathyno',		 'Recommended',									 6.0,		 '2015-08-28'), "
				  +" ('iggys egg adventure', 'hueychewwy', 'Recommended',											 18.7,	 	'2015-08-28'), "
				  +" ('iggys egg adventure', 	'marv', 	'Recommended', 											16.0, 		'2015-09-06'), "
				  +" ('iggys egg adventure', 	'wintersgoddess',	 'Recommended',									 10.4,	 	'2015-08-30'), "
				  +" ('iggys egg adventure', 	'darmazath',	 'Recommended', 									7.7,		 '2015-09-26'), "
				  +" ('iggys egg adventure',	 'capnkistler', 	'Recommended', 									12.3, 		'2015-09-15'), "
				  +" ('iggys egg adventure', 	'bronzewing', 		'Recommended', 									11.5, 	'201-08-29'), "
				  +" ('iggys egg adventure', 		'amon', 		'Recommended', 									9.7, 		'2015-09-02'), "
				  +" ('iggys egg adventure', 		'nicktrappartist', 		'Recommended', 							13.7, 		'2015-08-28'), "
				  +" ('iggys egg adventure', 		'mortee', 		'Recommended', 									12.0, 		'2015-09-02'), "
				  +" ('iggys egg adventure', 		'joriquo',		 'Recommended', 								7.7, 		'2015-08-31'), "
				  +" ('iggys egg adventure', 		'patrick', 		'Recommended', 									1.1, 		'2015-08-30'), "
				  +" ('time machine vr',		 'maltese falcon',		'Recommended',								 3.3,		 '2015-08-29'), "
				  +" ('time machine vr',		 'silverfin',		 'Recommended',									 3.9,		 '2015-09-07'), "
				  +" ('time machine vr',		 'game hard',	 	'Recommended',									 3.6,		 '2015-08-28'), "
				  +" ('time machine vr',		 'mort',		'Recommended',										 1.8,		 '2015-08-28'), "
				  +" ('time machine vr',		 'zulubear',		 'Recommended',									 2.1,		 '2015-08-30'), "
				  +" ('time machine vr',		 'vrgamerdude',		 'Recommended',		 							1.5,		 '2015-08-30'), "
				  +" ('time machine vr',		 'svr king'		 'Recommended',		 4.5,			 '2015-08-31'), "
				  +" ('time machine vr',		 'cutlass',		 'recommmended',		 3.3,		 '2015-08-31'), "
				  +" ('time machine vr', 	'somniumindependent', 		'Recommended', 		3.4, 		'2015-09-05'), "
				  +" ('time machine vr',		 'wovenlair', 		'Recommended', 		1.6, 		'2015-09-03'), "
				  +" ('time machine vr', 		'darayavaush', 		'Not Recommended', 		0.5, 		'2015-08-30'), "
				  +" ('time machine vr', 	'alan', 		'Not Recommended', 		1.2, 		'2015-08-29'), "
				  +" ('time machine vr', 	'relexed', 		'Recommended', 	1.1, 		'2015-10-13'), "
				  +" ('time machine vr', 	'dankest', 		'Recommended', 		1.2, 		'2015-11-08'), "
				  +" ('time machine vr', 	'porter', 		'Recommended', 		1.4, 		'2015-11-12'), "
				  +" ('time machine vr', 	'amberchan', 	'Recommended', 		1.3, 		'2015-11-24'), "
				  +" ('time machine vr', 	'raihaan', 		'Recommended', 		0.2, 		'2015-08-30'), "
				  +" ('time machine vr', 	'zingfodd', 	'Recommended', 		2.3, 		'2015-08-28'), "
				  +" ('time machine vr', 	'sensisnow', 		'Recommended', 		0.9, 		'2015-10-28'), "
				  +" ('lord of the dark castle',		 'merillo',		 'Not Recommended',		 1.8,		 '2015-06-14'), "
				  +" ('lord of the dark castle',		 'kalirion',		 'Recommended'		 25.6,		'2015-04-28'), "
				  +" ('lord of the dark castle',		 'zaxtor',		 'Recommended',			 0.9,		 '2015-04-24'), "
				  +" ('lord of the dark castle',		 'sideways',		'Not Recommended',		 4.0,		 '2015-05-18'), "
				  +" ('lord of the dark castle',		 'fool'		 'Recommended',		 14.4,		 '2015-04-27'), "
				  +" ('lord of the dark castle',		 'easyaccessmedia',		 'Recommended',		2.0,		 '2015-04-26'), "
				  +" ('lord of the dark castle',		 'merfab',		 'Recommended',		 1.7,		 '2015-04-24'), "
				  +" ('lord of the dark castle',		'bakis',		 'Not Recommended',		13.1,		 '2015-07-08'), "
				  +" ('lord of the dark castle',		 'saargoga',		 'Recommended', 		4.4,	 '2015-08-28'), "
				  +" ('lord of the dark castle',		 'pedroq',		 'Recommended',		 0.5,		 '2015-11-23'), "
				  +" ('axis football',		 'abugaj',		 'Recommended',		 75.1,		'2015-07-15'), "
				  +" ('axis football',		 'cbkzhuge',		 'Recommended',		242.2,		 '2015-07-15'), "
				  +" ('axis football',		 'tyronethecalzone',		 'Not Recommended',		 2.4,		 '2015-07-23'), "
				  +" ('axis football',		 'apostkhan',		 'Not Recommended',		 11.3,		 '2015-09-15'), "
				  +" ('axis football',		 'radidazny'		 'Recommended',		 3.5,		 '2015-07-15'), "
				  +" ('axis football',		 'yokohama',		 'Recommended',		 9.7,		 '2015-07-15'), "
				  +" ('axis football',		 'atilies',		 'Recommended',		 24.7,		 '2015-07-16'), "
				  +" ('axis football',		 'rovaira',		 'Recommended',		13.4,		 '2015-07-16'), "
				  +" ('axis football', 		'sherlock hound',		 	'Recommended',		 13.4,		'2015-11-24'), "
				  +" ('axis football',		'cubertt',		 'Recommended',		16.0,		 '2015-07-16'), "
				  +" ('axis football', 		'bizkit', 		'Not Recommended', 	3.8, 		'2015-11-07'), "
				  +" ('axis football', 		'khronic', 		'Not Recommended', 	2.5, 		'2015-11-05'), "
				  +" ('axis football', 		'ptsd', 		'Not Recommended', 		9.5, 		'2015-11-07'), "
				  +" ('axis football', 		'sloaney', 		'recommened', 		4.8, 		'2015-09-14'), "
				  +" ('axis football', 		'trinica', 		'Recommended', 		10.3, 	'2015-07-27'), "
				  +" ('axis football', 		'hyperkabuto', 	'Recommended', 		19.4, 	'2015-09-20'), "
				  +" ('axis football', 		'ec', 			'Recommended', 		0.6, 		'2015-07-28'), " 
				  +" ('axis football', 		'captainslow', 	'Recommended', 		0.8, 		'2015-07-28'), "
				  +" ('axis football', 		'duhbill', 		'Recommended', 		3.0, 		'2015-10-27'), "
				  +" ('axis football', 		'hellbandz', 	'Not Recommended', 	3.0, 		'2015-11-02'), "
				  +" ('contradicton spot the liar',		 'yicklepigeon',		 'Recommended',		 9.0,	 '2015-09-17'), "
				  +" ('contradicton spot the liar',		 'kurtrussell',		 'Recommended',		 5.4,		'2015-09-19'), "
				  +" ('contradicton spot the liar',		 'fang',		 'Recommended',		 8.0,		 '2015-09-20'), "
				  +" ('contradicton spot the liar',		 'jimmy',		 'Recommended',		 8.7,		 '2015-07-10'), "
				  +" ('contradicton spot the liar',		 'default',		 'Recommended',		 9.8,		 '2015-07-26'), "
				  +" ('contradicton spot the liar',		 'shandalara',		'Recommended',		 11.0,	 '2015-08-15 '), "
				  +" ('contradicton spot the liar',		 'monkeyhuouse',	 	'Recommended',		 12.2,	'2015-07-22'), "
				  +" ('contradicton spot the liar', 		'capanson', 		'Recommended', 		7.8, 		'2015-09-15'), "
				  +" ('contradicton spot the liar', 		'everrampafer', 		'Recommended', 	9.6, 		'2015-09-13'), "
				  +" ('contradicton spot the liar', 		'bastwood', 		'Recommended', 		9.3, 		'2015-07-13'), "
				  +" ('contradicton spot the liar', 		'mypantsare', 		'Recommended', 		11.8, 	'2015-07-21'), "
				  +" ('contradicton spot the liar', 		'falcon', 		'Recommended', 		10.2, 		'2015-07-27'), "
				  +" ('contradicton spot the liar', 		'arcos', 		'Recommended', 		6.0, 			'2015-10-11'), "
				  +" ('contradicton spot the liar', 		'pkmn', 		'Recommended', 		10.6, 		'2015-07-11'), "
				  +" ('contradicton spot the liar', 		'jd', 		'Recommended', 		11.2, 		'2015-07-11'), "
				  +" ('contradicton spot the liar', 		'smashuk', 		'Recommended', 		9.0, 		'2015-10-11'), " 
				  +" ('contradicton spot the liar', 		'voxmoose', 	'Recommended', 		18.8, 	'2015-07-21'), "
				  +" ('contradicton spot the liar', 		'gnattress', 	'Recommended', 		10.5,		 '2015-07-11'), "
				  +" ('contradicton spot the liar', 		'lordgaz', 		'Recommended', 		12.1, 		'2015-08-25'), "
				  +" ('contradicton spot the liar', 		'articulateangel', 		'Recommended', 		35.7, 	'2015-09-22'), "
				  +" ('ground control ii operation exodus', 	'the comedian', 	'Recommended', 	0.1, 	'2015-07-08'), "
				  +" ('ground control ii operation exodus', 	'aragar varnus', 	'Recommended', 	8.5, 	'2015-07-09'), "
				  +" ('ground control ii operation exodus', 	'blitzwing', 	'Recommended', 		0.3, 		'2015-07-09'), "
				  +" ('ground control ii operation exodus', 	'tibetan bowtie', 	'Recommended', 	2.2, 		'2015-07-11'), "
				  +" ('ground control ii operation exodus', 	'pikachoo', 		'Recommended', 	8.1, 		'2015-11-08'), "
				  +" ('ground control ii operation exodus', 	'duncs', 		'Recommended', 		19.7, 	'2015-08-31'), "
				  +" ('ground control ii operation exodus', 	'wolf whisper', 	'Recommended', 	0.7, 		'2015-07-29'), "
				  +" ('ground control ii operation exodus', 	'hot air buffoon', 	'Recommended', 	3.0, 		'2015-11-14'), "
				  +" ('ground control ii operation exodus', 	'big crank handle', 	'Recommended', 	4.0, 	'2015-09-13'), "
				  +" ('ground control ii operation exodus', 	'jmb mark', 	'Recommended', 		15.0, 	'2015-08-10'), "
				  +" ('ground control ii operation exodus', 	'meona', 		'Recommended', 		3.0, 		'2015-07-09'), "
				  +" ('ground control ii operation exodus', 	'reaper', 		'Recommended', 		20.9, 	'2015-07-11'), "
				  +" ('ground control ii operation exodus', 	'paradise decay', 	'Recommended', 	1.3, 		'2015-08-01'), "
				  +" ('ground control ii operation exodus', 	'tashrinbackup', 	'Not Recommended', 	0.2, 	'2015-08-20'), "
				  +" ('ground control ii operation exodus', 	'cesar cocoloco', 	'Not Recommended', 	0.6, 	'2015-11-10'), "
				  +" ('ground control ii operation exodus', 	'varrybig', 		'Not Recommended', 	0.5, 	'2015-08-08'), "
				  +" ('ground control ii operation exodus', 	'akalonian', 		'Recommended', 		10.7, '2015-11-28'), "
				  +" ('subspace continuum', 	'aveumcu', 		'Recommended', 		398.0, 	'2015-07-03'), "
				  +" ('subspace continuum', 	'keiver', 		'Recommended', 		568.6, 	'2015-07-03'), "
				  +" ('subspace continuum', 	'lmorchard', 	'Recommended', 		15.4, 	'2015-07-07'), "
				  +" ('subspace continuum', 	'holes', 		'Recommended', 		407.7, 	'2015-07-03 '), "
				  +" ('subspace continuum', 	'datmusicguy', 	'Recommended', 		191.3, 	'2015-07-03'), "
				  +" ('subspace continuum', 	'mike sniper', 	'Recommended', 		131.7, 	'2015-07-03'), "
				  +" ('subspace continuum', 	'alienc', 		'Recommended', 		29.4, 	'2015-07-03'), "
				  +" ('subspace continuum', 	'xifer', 		'Recommended', 		495.5, 	'2015-07-03'), "
				  +" ('subspace continuum', 	'nightwasp', 	'Recommended', 		226.0, 	'2015-07-09') "
				
				
				

				
				+ ");").execute();
		 

		 } finally {
		// Closing the connection
		if (connection != null)
		 connection.close();
		 }
		 } finally {
		 // Closing the server
		 if (hsqlServer != null)
		 hsqlServer.stop();
		 }

		
	}

	
	
}


