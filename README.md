# Fantasy Cricket Auction Application

## Overview
The **Fantasy Cricket Auction Application** is a Java-based auction system for fantasy cricket teams. The application allows participants to place bids on cricket players, manage their budgets, and view the leaderboard for the auction.

This application utilizes a simple graphical user interface (GUI) built using JavaFX and stores auction data in an Excel file using Apache POI.

## Features
- **Bid Placement**: Participants can place bids on cricket players.
- **Budget Management**: Each participant has a budget, which is updated as they place bids.
- **Leaderboard**: The application displays the leaderboard showing the winner of each auctioned player.
- **Excel Integration**: The data (players, participants, and bids) is loaded and saved to an Excel file using Apache POI.
- **Bid Processing**: After all bids are placed, the application processes the highest bid for each player and updates the budgets accordingly.

## Requirements
- Java 11 or later.
- Apache POI library for reading and writing Excel files.
- JavaFX for the GUI components.

## Installation

### 1. Clone the repository

```bash
git clone https://github.com/yourusername/FantasyCricketAuctionApp.git

## 2. Install Apache POI

- The application uses Apache POI to read and write Excel files. Ensure the POI dependencies are included in your project. If you're using Maven, add the following to your pom.xml:
```html
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.3</version>
</dependency>

## 3. Excel File Structure

- Ensure you have an auction_data.xlsx file with the following sheets:
- Players Sheet:
|Player Name |Role | Base Price
|Virat Kohli | Batsman | 500
|Ben Stokes | All-Rounder | 400
|Jasprit Bumrah | Bowler | 300
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

- The application will initialize the budgets for all participants to 100 and clear the bids when it starts.
## Running the Application
- 1. Build and Run using Maven:

- To compile and run the application with Maven, use the following commands in your project directory:
```bash
mvn clean install
mvn javafx:run

## 2. Running from IDE:

- If you're using an IDE like IntelliJ IDEA or Eclipse, you can directly run the FantasyCricketAppUI class as a Java application. Ensure that the auction_data.xlsx file is placed in the correct directory, or specify the - full path to the file in the code.
- Features in Detail
- 1. Placing Bids

- Participants can select a player and enter a bid amount. When the "Place Bid" button is clicked, the bid is recorded and the participant's budget is reduced accordingly.
- 2. Processing Bids

- After all bids are placed, participants can click "Process Bids". The application will:

-    Determine the highest bid for each player.
-     Award the player to the participant who placed the highest bid.
-    Update the budget of the winning participant and reset the budgets of the others.

## 3. Resetting Bids

- Clicking "Reset Bids" will:

-   Clear all bids.
-   Reset the budgets of all participants to the initial value (100).

## 4. Leaderboard

-The leaderboard displays:

    The player name.
    The winner’s name (participant who placed the highest bid).
    The winning bid amount.

## 5. Excel Integration

When the application starts:

    The application will initialize participant budgets to 100 and clear the existing bids in the Bids sheet.
    After processing bids, the data is saved back to the auction_data.xlsx file, ensuring persistence across application restarts.


    Add a timer for auction rounds.
    Allow participants to set a maximum bid limit.
    Implement notifications when a participant’s bid is outbid.
    Improve UI design for better user experience.
