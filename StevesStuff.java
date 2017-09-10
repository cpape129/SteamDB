import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.hsqldb.Server;

import java.util.*;

public class StevesStuff {
	 public static void main(String[] args) throws ClassNotFoundException, SQLException
	 {
	  Server hsqlServer = null;
	  try {
	  hsqlServer = new Server();
	  hsqlServer.setLogWriter(null);
	  hsqlServer.setSilent(true);
	  // Update the database name and path to your own!
	  hsqlServer.setDatabaseName(0, "SteamData");
	  hsqlServer.setDatabasePath(0, "file:C:/Users/Christine/Documents/Fall 2015/Database/final/SteamDataBase/SteamDataBase/SteamData");
	  // Start the database!
	  hsqlServer.start();
	  Connection connection = null;
	  try {
	  // Getting a connection to the newly started database
	  Class.forName("org.hsqldb.jdbcDriver");
	  // Default user of the HSQLDB is 'sa' with an empty password
	  connection = DriverManager.getConnection(
	  "jdbc:hsqldb:file:C:/Users/Christine/Documents/Fall 2015/Database/final/SteamDataBase/SteamDataBase/SteamData", "sa", "");
	  
	  //connection.prepareStatement("CREATE TABLE GAME_INFO(Title VARCHAR(50) DEFAULT 'none' NOT NULL, ReleaseDate DATE, Developer VARCHAR(50), DLC VARCHAR(25), ESRBRating VARCHAR(50), OverallReview VARCHAR(50), Cost VARCHAR(50), About VARCHAR(1000), CONSTRAINT TitlePK PRIMARY KEY(Title));").execute();
	  
	  //connection.prepareStatement("CREATE TABLE REVIEWS(Title VARCHAR(50) DEFAULT 'none' NOT NULL, USER VARCHAR(50) DEFAULT 'none' NOT NULL, UserRating VARCHAR(25), HoursRecorded DOUBLE, Date DATE, CONSTRAINT ReviewsPK PRIMARY KEY(Title, User), CONSTRAINT ReviewsFK FOREIGN KEY(Title) REFERENCES GAME_INFO(Title) ON UPDATE CASCADE ON DELETE CASCADE);").execute();
	  
	  //connection.prepareStatement("CREATE TABLE GENRES(Title VARCHAR(50) DEFAULT 'none' NOT NULL, Genre VARCHAR(50) DEFAULT 'none' NOT NULL, CONSTRAINT GenrePK PRIMARY KEY(Title, Genre), CONSTRAINT GenreFK FOREIGN KEY(Title) REFERENCES GAME_INFO(Title) ON UPDATE CASCADE ON DELETE CASCADE);").execute();
	  
	  //connection.prepareStatement("DROP TABLE REVIEWS;").execute();
	  
//	  connection.prepareStatement("INSERT INTO GAME_INFO(Title, ReleaseDate, Developer, DLC, ESRBRating, OverallReview, Cost, About) " 
//			  + "values ('warframe', '2013-03-25', 'digital extremes', 'yes', 'm', 'very positive', 'free', 'Warframe is a cooperative free-to-play third person online action game set in an evolving sci-fi world.'); ").execute();
	  
//	  connection.prepareStatement("INSERT INTO REVIEWS(Title, USER, UserRating, HoursRecorded, Date) values ('warframe', 'Section-Nine', 'recommended', 1295.3, '2015-07-10');").execute();
	  
//	  connection.prepareStatement("INSERT INTO GENRES(Title, Genre) values ('warframe', 'action');").execute();
	  
//	  ResultSet rs =connection.prepareStatement(
//			  "SELECT * FROM GENRES").executeQuery();
//			  ResultSetMetaData rsmd = rs.getMetaData();
//			  int numberOfColumns = rsmd.getColumnCount();
//			  for (int i = 1; i <= numberOfColumns; i++) {
//			   if (i > 1)
//			   System.out.print(",\t\t");
//			   String columnName = rsmd.getColumnName(i);
//			   System.out.print(columnName);
//			   }
//			  System.out.println("");
//			  while (rs.next()) {
//			   for (int i = 1; i <= numberOfColumns; i++) {
//			   if (i > 1)
//			   System.out.print(",\t\t");
//			   String columnValue = rs.getString(i);
//			   System.out.print(columnValue);
//			   }
//			  System.out.println("");
//			  }
	  //searchGame(connection);
	  //hoursOverTime(connection);
	  //averageHoursOneGenre(connection);
	  //DLC(connection);
	  //ratingStats(connection);
	//  costVsRelease(connection);
	  averageCostAllGenres(connection);
	  //averageHoursAllGenres(connection);
	  }finally{
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
	 //1 game options 
	 //search for all of a games info
	 public static void searchGame(Connection connection) throws SQLException{
		 try{
			 //get game title from user
			 System.out.println("What game would you like to search for?");
			 Scanner title = new Scanner(System.in);
			 String gameTitle = title.nextLine();
		  ResultSet rs =connection.prepareStatement(
		  "SELECT Title, ReleaseDate, Developer, DLC, ESRBRating, OverallReview, Cost, About, USER, UserRating, HoursRecorded, Date, Genre "
				  +"FROM ((GAME_INFO INNER JOIN GENRES ON GAME_INFO.Title = GENRES.Title) INNER JOIN REVIEWS ON REVIEWS.Title = GENRES.Title) WHERE Title LIKE '" + gameTitle + "'").executeQuery();
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
		 }catch(Exception e){
			 System.out.println("error");
		 }
			 finally{
			 System.out.println("");
		 }
	 }
	 
	 //hours over time
	 public static void hoursOverTime(Connection connection) throws SQLException{
		 try{
			 //get game title from user
			 System.out.println("What game would you like to search for?");
			 Scanner title = new Scanner(System.in);
			 String gameTitle = title.nextLine();
		  ResultSet rs =connection.prepareStatement(
		  "SELECT HoursRecorded, Date "
				  +"FROM ((GAME_INFO INNER JOIN GENRES ON GAME_INFO.Title = GENRES.Title) INNER JOIN REVIEWS ON REVIEWS.Title = GENRES.Title) WHERE Title LIKE '" + gameTitle + "' ORDER BY Date").executeQuery();
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
		 }catch(Exception e){
			 System.out.println("error");
		 }
			 finally{
			 System.out.println("");
		 }
	 }
	 
	 //2 game options
	 //hours over time
	 
	 //1 Genre options
	 //cost vs release
	 public static void costVsRelease(Connection connection) throws SQLException{
		 try{
			 //get game genre from user
			 System.out.println("What genre would you like to search for?");
			 Scanner genre = new Scanner(System.in);
			 String gameGenre = genre.nextLine();
		  ResultSet rs =connection.prepareStatement(
		  "SELECT Title, Cost, ReleaseDate, Genre "
				  +"FROM ((GAME_INFO INNER JOIN GENRES ON GAME_INFO.Title = GENRES.Title) INNER JOIN REVIEWS ON REVIEWS.Title = GENRES.Title) WHERE Genre LIKE '" + gameGenre + "' GROUP BY Genre, Title, Cost, ReleaseDate").executeQuery();
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
		 }catch(Exception e){
			 System.out.println("error");
		 }
			 finally{
			 System.out.println("");
		 }
	 }
	 
	 //average hours
	 public static void averageHoursOneGenre(Connection connection) throws SQLException{
		 try{
			 //get game genre from user
			 System.out.println("What genre would you like to search for?");
			 Scanner genre = new Scanner(System.in);
			 String gameGenre = genre.nextLine();
		  ResultSet rs =connection.prepareStatement(
		  "SELECT Genre, AVG(HoursRecorded) AS Hours "
				  +"FROM ((GAME_INFO INNER JOIN GENRES ON GAME_INFO.Title = GENRES.Title) INNER JOIN REVIEWS ON REVIEWS.Title = GENRES.Title) WHERE Genre LIKE '" + gameGenre + "' GROUP BY Genre").executeQuery();
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
		 }catch(Exception e){
			 System.out.println("error");
		 }
			 finally{
			 System.out.println("");
		 }
	 }
	 
	 //DLC vs Non DLC
	 public static void DLC(Connection connection) throws SQLException{
		 try{
			 //get game genre from user
			 System.out.println("What genre would you like to search for?");
			 Scanner genre = new Scanner(System.in);
			 String gameGenre = genre.nextLine();
		  ResultSet rs =connection.prepareStatement(
		  "SELECT Genre, DLC "
				  +"FROM ((GAME_INFO INNER JOIN GENRES ON GAME_INFO.Title = GENRES.Title) INNER JOIN REVIEWS ON REVIEWS.Title = GENRES.Title) WHERE Genre LIKE '" + gameGenre + "' GROUP BY Genre, DLC").executeQuery();
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
		 }catch(Exception e){
			 System.out.println("error");
		 }
			 finally{
			 System.out.println("");
		 }
	 }
	 
	 //ratingStats
	 public static void ratingStats(Connection connection) throws SQLException{
		 try{
			 //get game genre from user
			 System.out.println("What genre would you like to search for?");
			 Scanner genre = new Scanner(System.in);
			 String gameGenre = genre.nextLine();
		  ResultSet rs =connection.prepareStatement(
		  "SELECT Genre, COUNT(ESRBRating) "
				  +"FROM ((GAME_INFO INNER JOIN GENRES ON GAME_INFO.Title = GENRES.Title) INNER JOIN REVIEWS ON REVIEWS.Title = GENRES.Title) WHERE Genre LIKE '" + gameGenre + "' GROUP BY Genre").executeQuery();
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
		 }catch(Exception e){
			 System.out.println("error");
		 }
			 finally{
			 System.out.println("");
		 }
	 }
	 
	 //all genres
	 //average cost
	 public static void averageCostAllGenres(Connection connection) throws SQLException{
		 try{
		  ResultSet rs =connection.prepareStatement(
		  "SELECT Genre, Cost "
				  +"FROM ((GAME_INFO INNER JOIN GENRES ON GAME_INFO.Title = GENRES.Title) INNER JOIN "
				  + "REVIEWS ON REVIEWS.Title = GENRES.Title) GROUP BY Genre, Cost").executeQuery();
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
		 }catch(Exception e){
			 System.out.println("error");
		 }
			 finally{
			 System.out.println("");
		 }
	 }
	 
	 //averagehours 
	 public static void averageHoursAllGenres(Connection connection) throws SQLException{
		 try{
		  ResultSet rs =connection.prepareStatement(
		  "SELECT Genre, AVG(HoursRecorded) AS Hours "
				  +"FROM ((GAME_INFO INNER JOIN GENRES ON GAME_INFO.Title = GENRES.Title) INNER JOIN REVIEWS ON REVIEWS.Title = GENRES.Title) GROUP BY Genre").executeQuery();
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
		 }catch(Exception e){
			 System.out.println("error");
		 }
			 finally{
			 System.out.println("");
		 }
	 }

}
