**Fantasy Cricket Auction Application**

A Java-based application for managing a fantasy cricket auction, where participants can bid on players using their allocated budgets. The application uses Excel files to store and load data such as participant budgets, player details, and bids. This application allows participants to place bids, process the highest bids, and update their budgets accordingly.
Features

    Auction Interface: Allows participants to place bids on players in the auction.
    Bid Processing: Processes bids and determines the highest bid for each player.
    Budget Management: Each participant has an allocated budget, and their budget is updated after processing bids.
    Excel Integration: Data is stored and loaded from Excel files for players, participants, and bids.
    Leaderboard: Displays the winning participant for each player and their bid amount.
    Reset & Initialization: Resets the bids and participant budgets, and initializes Excel data when the application starts.

**Prerequisites**

    Java 8 or later
    Apache POI library for Excel file handling
    Apache Maven for project management (optional)

**Setup**
1. Clone the repository

Clone the repository to your local machine:

git clone <repository_url>

2. Install Apache POI

The application uses Apache POI to read and write Excel files. Ensure the POI dependencies are included in your project. If you're using Maven, add the following to your pom.xml:

<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.3</version>
</dependency>

3. Excel File Structure

Ensure you have an auction_data.xlsx file with the following sheets:
Players Sheet:
Player Name	Role	Base Price
Virat Kohli	Batsman	500
Ben Stokes	All-Rounder	400
Jasprit Bumrah	Bowler	300
...	...	...
Participants Sheet:
Participant Name	Budget
John	100
Mike	100
...	...
Bids Sheet:
Player Name	Participant Name	Bid Amount
Virat Kohli	John	50
Ben Stokes	Mike	30
...	...	...

The application will initialize the budgets for all participants to 100 and clear the bids when it starts.
Running the Application
1. Build and Run using Maven:

To compile and run the application with Maven, use the following commands in your project directory:

mvn clean install
mvn javafx:run

2. Running from IDE:

If you're using an IDE like IntelliJ IDEA or Eclipse, you can directly run the FantasyCricketAppUI class as a Java application. Ensure that the auction_data.xlsx file is placed in the correct directory, or specify the full path to the file in the code.
Features in Detail
1. Placing Bids

Participants can select a player and enter a bid amount. When the "Place Bid" button is clicked, the bid is recorded and the participant's budget is reduced accordingly.
2. Processing Bids

After all bids are placed, participants can click "Process Bids". The application will:

    Determine the highest bid for each player.
    Award the player to the participant who placed the highest bid.
    Update the budget of the winning participant and reset the budgets of the others.

3. Resetting Bids

Clicking "Reset Bids" will:

    Clear all bids.
    Reset the budgets of all participants to the initial value (100).

4. Leaderboard

The leaderboard displays:

    The player name.
    The winner’s name (participant who placed the highest bid).
    The winning bid amount.

5. Excel Integration

When the application starts:

    The application will initialize participant budgets to 100 and clear the existing bids in the Bids sheet.
    After processing bids, the data is saved back to the auction_data.xlsx file, ensuring persistence across application restarts.

Screenshots

Include relevant screenshots here for reference (optional).
Future Enhancements

    Add a timer for auction rounds.
    Allow participants to set a maximum bid limit.
    Implement notifications when a participant’s bid is outbid.
    Improve UI design for better user experience.
