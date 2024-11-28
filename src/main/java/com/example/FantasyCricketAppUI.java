package com.example;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class FantasyCricketAppUI extends Application {

    private List<Player> players;
    private List<Participant> participants;
    private TableView<AwardedPlayer> leaderboardTable;
    private TableView<ParticipantBudget> participantBudgetTable;
    private Map<String, List<Bid>> playerBidMap;
    private Player selectedPlayer; // Store the player selected by "Select Player"
    private List<Player> remainingPlayers; // To track unselected players
    private Label selectedPlayerLabel = new Label("No player selected");
    private Label selectedPlayerBasePrice = new Label("");
    private List<AwardedPlayer> awardedPlayers = new ArrayList<>();

    private static final int INITIAL_BUDGET = 100;
    private int bidCounter = 0; // Counter for tracking bids

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Initialize players and participants
        players = new ArrayList<>();
        participants = new ArrayList<>();
        playerBidMap = new HashMap<>();
        String filePath = "auction_data.xlsx";
        // Reset Excel data on application start
        resetExcelData(filePath);

        // Create some sample data
        createSampleData();
        //Initialize Auction Player List
        initializePlayersFromExcel();

        // UI Elements for Auction
        Label auctionLabel = new Label("Fantasy Cricket Auction");
        //ComboBox<Player> playerComboBox = new ComboBox<>();
        //playerComboBox.getItems().addAll(players);

        // Selected player label
        //Label selectedPlayerLabel = new Label("No player selected");
        selectedPlayerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        selectedPlayerBasePrice.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Select Player button
        Button selectPlayerButton = new Button("Select Player");
        selectPlayerButton.setOnAction(e -> selectPlayer());

        ComboBox<Participant> participantComboBox = new ComboBox<>();
        participantComboBox.getItems().addAll(participants);

        TextField bidField = new TextField();
        bidField.setPromptText("Enter your bid");

        Button bidButton = new Button("Place Bid");
        bidButton.setOnAction(event -> handleBid(participantComboBox, bidField));

        Button processBidsButton = new Button("Process Bids");
        processBidsButton.setOnAction(event -> processBids(selectedPlayer));

        Button resetBidsButton = new Button("Reset Bids");
        resetBidsButton.setOnAction(event -> resetBids());

        // UI Elements for Leaderboard
        leaderboardTable = new TableView<>();
        setUpLeaderboardTable();

        // UI Elements for Participant Budget Display
        participantBudgetTable = new TableView<>();
        setUpParticipantBudgetTable();

// Auction Layout
        VBox auctionLayout = new VBox(10);
        auctionLayout.getChildren().addAll(auctionLabel, selectPlayerButton, selectedPlayerLabel, selectedPlayerBasePrice, participantComboBox, bidField, bidButton, processBidsButton, resetBidsButton);
        auctionLayout.setPrefWidth(300);
        auctionLayout.setFillWidth(true); // Allow components to resize with the VBox

// Leaderboard Layout
        VBox leaderboardLayout = new VBox(10);
        leaderboardLayout.getChildren().addAll(new Label("Leaderboard"), leaderboardTable);
        leaderboardLayout.setPrefWidth(300);
        leaderboardLayout.setFillWidth(true);

// Participant Budget Layout
        VBox participantBudgetLayout = new VBox(10);
        participantBudgetLayout.getChildren().addAll(new Label("Participant Budgets"), participantBudgetTable);
        participantBudgetLayout.setPrefWidth(300);
        participantBudgetLayout.setFillWidth(true);

// Main Layout
        HBox mainLayout = new HBox(20);
        mainLayout.getChildren().addAll(auctionLayout, leaderboardLayout, participantBudgetLayout);
        mainLayout.setPadding(new Insets(20));

// Enable scaling for all layouts
        mainLayout.setHgrow(auctionLayout, Priority.ALWAYS);
        mainLayout.setHgrow(leaderboardLayout, Priority.ALWAYS);
        mainLayout.setHgrow(participantBudgetLayout, Priority.ALWAYS);
        auctionLayout.setVgrow(auctionLabel, Priority.ALWAYS);
        leaderboardLayout.setVgrow(leaderboardTable, Priority.ALWAYS);
        participantBudgetLayout.setVgrow(participantBudgetTable, Priority.ALWAYS);

// Set up the scene
        Scene scene = new Scene(mainLayout, 800, 400);
        scene.getStylesheets().add("styles.css"); // Optional: Add styling for finer control

        primaryStage.setTitle("Fantasy Cricket Auction");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void createSampleData() {
        // Load players and participants from the Excel file
        loadParticipantsAndPlayersFromExcel("auction_data.xlsx");

        // Sample Players (if needed for testing without Excel)
        if (players.isEmpty()) {
            players.add(new Player("Virat Kohli", "Batsman", 500));
            players.add(new Player("Jasprit Bumrah", "Bowler", 300));
            players.add(new Player("Ben Stokes", "All-Rounder", 400));
        }

        // Sample Participants (if needed for testing without Excel)
        if (participants.isEmpty()) {
            participants.add(new Participant("John", INITIAL_BUDGET));
            participants.add(new Participant("Mike", INITIAL_BUDGET));
        }
    }
    private void resetExcelData(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            // Reset Participants' budgets to 100
            Sheet participantSheet = workbook.getSheet("Participants");
            for (Row row : participantSheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
                row.getCell(1).setCellValue(100); // Set budget to 100
            }

            // Clear Bids sheet
            Sheet bidSheet = workbook.getSheet("Bids");
            int lastRow = bidSheet.getLastRowNum();
            for (int i = lastRow; i > 0; i--) {
                bidSheet.removeRow(bidSheet.getRow(i)); // Remove all rows except the header
            }

            // Save changes back to the Excel file
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

        } catch (IOException e) {
            showAlert("Error", "Failed to reset Excel data: " + e.getMessage());
        }
    }

    private void initializePlayersFromExcel() {
        remainingPlayers = new ArrayList<>(players);
        awardedPlayers.clear();
//        try (FileInputStream fis = new FileInputStream(new File(filePath))) {
//            Workbook workbook = new XSSFWorkbook(fis);
//            Sheet playerSheet = workbook.getSheet("Players");
//
//            // Read player data from the "Players" sheet
//            for (Row row : playerSheet) {
//                if (row.getRowNum() == 0) continue; // Skip header row
//                String playerName = row.getCell(0).getStringCellValue();
//                String playerRole = row.getCell(1).getStringCellValue();
//                int basePrice = (int) row.getCell(2).getNumericCellValue();
//                playerList.add(new Player(playerName, playerRole, basePrice));
//            }
//
//            // Copy all players to remainingPlayers
//            remainingPlayers.addAll(playerList);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void selectPlayer() {
        if (remainingPlayers.isEmpty()) {
            // No players left to select
            selectedPlayerLabel.setText("All players have been auctioned!");
            selectedPlayerBasePrice.setText("");
            return;
        }

        // Randomly select a player
        Random random = new Random();
        int randomIndex = random.nextInt(remainingPlayers.size());
        selectedPlayer = remainingPlayers.remove(randomIndex);

        // Display the selected player
        selectedPlayerLabel.setText("Selected Player: " + selectedPlayer.getName());
        selectedPlayerBasePrice.setText("Base  Price: " + selectedPlayer.getBasePrice());
    }

    private void loadParticipantsAndPlayersFromExcel(String filePath) {
        try (FileInputStream fis = new FileInputStream(new File(filePath))) {
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet playerSheet = workbook.getSheet("Players");
            Sheet participantSheet = workbook.getSheet("Participants");

            // Load Players
            for (Row row : playerSheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
                String playerName = row.getCell(0).getStringCellValue();
                String playerRole = row.getCell(1).getStringCellValue();
                int basePrice = (int) row.getCell(2).getNumericCellValue();
                players.add(new Player(playerName, playerRole, basePrice));
            }

            // Load Participants
            for (Row row : participantSheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
                String participantName = row.getCell(0).getStringCellValue();
                int budget = (int) row.getCell(1).getNumericCellValue();
                participants.add(new Participant(participantName, budget));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleBid(ComboBox<Participant> participantComboBox, TextField bidField) {
        Participant selectedParticipant = participantComboBox.getSelectionModel().getSelectedItem();
        int bidAmount;

        try {
            bidAmount = Integer.parseInt(bidField.getText());
        } catch (NumberFormatException e) {
            showAlert("Invalid Bid", "Please enter a valid bid amount.");
            return;
        }

        if (selectedPlayer != null && selectedParticipant != null) {
            if (selectedParticipant.getBudget() < bidAmount) {
                showAlert("Insufficient Budget", "Participant " + selectedParticipant.getName() + " does not have enough budget.");
                return;
            }

            // Initialize currentBudget only on the first bid (bidCounter == 0)
//            if (bidCounter == 0) {
//                for (Participant participant : participants) {
//                    System.out.println("Participant Handle:" + participant.getName() + " Budget Handle:" + participant.getBudget());
//                    participant.setCurrentBudget(participant.getBudget());
//                }
//            }

            // Subtract the bid amount from the participant's budget
            selectedParticipant.setBudget(selectedParticipant.getBudget() - bidAmount);

            // Store the bid for the selected player and participant
            Bid bid = new Bid(selectedParticipant.getName(), bidAmount);
            playerBidMap.computeIfAbsent(selectedPlayer.getName(), k -> new ArrayList<>()).add(bid);

            // Increment the bid counter
            bidCounter++;
            System.out.println("Bid Map :" + playerBidMap);

            // Update the participant list and disable bidding if budget is exhausted
            updateParticipantList();

            showAlert("Bid Placed", "Bid placed for " + selectedPlayer.getName() + " by " + selectedParticipant.getName());
        }
    }

    private void processBids(Player selectedPlayer) {

        if (!playerBidMap.containsKey(selectedPlayer.getName())) {
            System.out.println("No bids available for the selected player: " + selectedPlayer);
            return;
        }

        List<Bid> bids = playerBidMap.get(selectedPlayer.getName());
        Bid highestBid = null;

            // Determine the highest bid
            for (Bid bid : bids) {
                if (highestBid == null || bid.getAmount() > highestBid.getAmount()) {
                    highestBid = bid;
                }
            }

            if (highestBid != null) {
                // Award the player to the participant with the highest bid
                AwardedPlayer awardedPlayer = new AwardedPlayer(
                        selectedPlayer.getName(),
                        highestBid.getParticipant(),
                        String.valueOf(highestBid.getAmount())
                );
                awardedPlayers.add(awardedPlayer);

                // Update budgets for all participants
                for (Participant participant : participants) {
                    if (participant.getName().equals(highestBid.getParticipant())) {
                        // Winner: Subtract the bid amount from their current budget
                        participant.setBudget(participant.getCurrentBudget() - highestBid.getAmount());
                        participant.setCurrentBudget(participant.getCurrentBudget() - highestBid.getAmount());
                    } else {
                        // Non-winners: Reset their budget to the saved currentBudget
                        participant.setBudget(participant.getCurrentBudget());
                    }
                }
            }

        // Reset the bid counter after processing bids
        bidCounter = 0;

        // Update the leaderboard table
        leaderboardTable.getItems().setAll(awardedPlayers);

        // Save updated participant data to Excel and bids
        saveParticipantsToExcel("auction_data.xlsx");
        saveBidsToExcel("auction_data.xlsx");
        // Update the participant budget table after processing bids
        updateParticipantBudgetTable();

        showAlert("Bids Processed", "Bids have been processed successfully.");
    }

    private void updateParticipantList() {
        // Disable bidding for participants with exhausted budget
        for (Participant participant : participants) {
            if (participant.getBudget() <= 0) {
                // Disable the participant from bidding (You can modify ComboBox items here as per your UI)
                // participantComboBox.getItems().remove(participant); // Or just display as disabled in UI
            }
        }
    }

    private void saveParticipantsToExcel(String filePath) {
        try (FileInputStream fis = new FileInputStream(new File(filePath))) {
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet participantSheet = workbook.getSheet("Participants");

            // Update participants' budgets in Excel
            int rowNum = 1;
            for (Participant participant : participants) {
                Row row = participantSheet.getRow(rowNum++);
                row.getCell(1).setCellValue(participant.getBudget());
            }

            // Write the updated data back to Excel file
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveBidsToExcel(String filePath) {
        try (FileInputStream fis = new FileInputStream(new File(filePath))) {
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet bidSheet = workbook.getSheet("Bids");

            int rowNum = 1;
            for (String playerName : playerBidMap.keySet()) {
                List<Bid> bids = playerBidMap.get(playerName);
                for (Bid bid : bids) {
                    Row row = bidSheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(playerName);
                    row.createCell(1).setCellValue(bid.getParticipant());
                    row.createCell(2).setCellValue(bid.getAmount());
                }
            }

            // Write the updated data back to Excel file
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetBids() {
        // Reset all bids and participant budgets
        playerBidMap.clear();
        for (Participant participant : participants) {
            participant.setBudget(INITIAL_BUDGET);
        }

        // Update participant list
        updateParticipantList();

        //Initialize auction list
        initializePlayersFromExcel();
        selectedPlayerLabel.setText("No player selected");
        selectedPlayerBasePrice.setText("");

        showAlert("Bids Reset", "All bids have been cleared and participants' budgets have been reset to 100.");
    }

    private void updateParticipantBudgetTable() {
        List<ParticipantBudget> participantBudgets = new ArrayList<>();
        for (Participant participant : participants) {
            participantBudgets.add(new ParticipantBudget(participant.getName(), participant.getBudget()));
        }
        participantBudgetTable.getItems().setAll(participantBudgets);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setUpLeaderboardTable() {
        TableColumn<AwardedPlayer, String> playerNameColumn = new TableColumn<>("Player");
        playerNameColumn.setCellValueFactory(cellData -> cellData.getValue().playerNameProperty());

        TableColumn<AwardedPlayer, String> winnerNameColumn = new TableColumn<>("Winner");
        winnerNameColumn.setCellValueFactory(cellData -> cellData.getValue().winnerNameProperty());

        TableColumn<AwardedPlayer, String> winningBidColumn = new TableColumn<>("Winning Bid");
        winningBidColumn.setCellValueFactory(cellData -> cellData.getValue().winningBidProperty());

        leaderboardTable.getColumns().addAll(playerNameColumn, winnerNameColumn, winningBidColumn);
    }

    private void setUpParticipantBudgetTable() {
        TableColumn<ParticipantBudget, String> participantNameColumn = new TableColumn<>("Participant");
        participantNameColumn.setCellValueFactory(cellData -> cellData.getValue().participantNameProperty());

        TableColumn<ParticipantBudget, Integer> budgetColumn = new TableColumn<>("Remaining Budget");
        budgetColumn.setCellValueFactory(cellData -> cellData.getValue().remainingBudgetProperty().asObject());

        participantBudgetTable.getColumns().addAll(participantNameColumn, budgetColumn);
    }

    // Awarded Player class for displaying leaderboard
    public static class AwardedPlayer {
        private final StringProperty playerName;
        private final StringProperty winnerName;
        private final StringProperty winningBid;

        public AwardedPlayer(String playerName, String winnerName, String winningBid) {
            this.playerName = new SimpleStringProperty(playerName);
            this.winnerName = new SimpleStringProperty(winnerName);
            this.winningBid = new SimpleStringProperty(winningBid);
        }

        public StringProperty playerNameProperty() {
            return playerName;
        }

        public StringProperty winnerNameProperty() {
            return winnerName;
        }

        public StringProperty winningBidProperty() {
            return winningBid;
        }
    }

    public static class Bid {
        private final String participant;
        private final int amount;

        public Bid(String participant, int amount) {
            this.participant = participant;
            this.amount = amount;
        }

        public String getParticipant() {
            return participant;
        }

        public int getAmount() {
            return amount;
        }
    }

    public static class Player {
        private final String name;
        private final String role;
        private final int basePrice;

        public Player(String name, String role, int basePrice) {
            this.name = name;
            this.role = role;
            this.basePrice = basePrice;
        }

        public String getName() {
            return name;
        }

        public String getRole() {
            return role;
        }

        public int getBasePrice() {
            return basePrice;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class Participant {
        private final String name;
        private int budget;
        private int currentBudget; // Stores budget before "Process Bid" is hit.

        public Participant(String name, int budget) {
            this.name = name;
            this.budget = budget;
            this.currentBudget = budget; // Initialize to the same value as budget.
        }

        public String getName() {
            return name;
        }

        public int getBudget() {
            return budget;
        }

        public void setBudget(int budget) {
            this.budget = budget;
        }

        public int getCurrentBudget() {
            return currentBudget;
        }

        public void setCurrentBudget(int currentBudget) {
            this.currentBudget = currentBudget;
        }

        // Override toString to return the participant's name
        @Override
        public String toString() {
            return name;
        }
    }

    // Class to represent Participant with their remaining budget for display
    public static class ParticipantBudget {
        private final StringProperty participantName;
        private final IntegerProperty remainingBudget;

        public ParticipantBudget(String participantName, int remainingBudget) {
            this.participantName = new SimpleStringProperty(participantName);
            this.remainingBudget = new SimpleIntegerProperty(remainingBudget);
        }

        public StringProperty participantNameProperty() {
            return participantName;
        }

        public IntegerProperty remainingBudgetProperty() {
            return remainingBudget;
        }
    }
}
