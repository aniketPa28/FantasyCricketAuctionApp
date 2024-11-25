// Sample Participant class
public static class Participant {
    private String name;
    private int budget;
    private List<Player> players;
    private int score;

    public Participant(String name, int budget) {
        this.name = name;
        this.budget = budget;
        this.players = new ArrayList<>();
        this.score = 0;
    }

    public String getName() { return name; }
    public int getBudget() { return budget; }
    public int getScore() { return score; }

    public boolean buyPlayer(Player player, int price) {
        if (price <= budget) {
            players.add(player);
            budget -= price;
            return true;
        }
        return false;
    }

    public void updateScore(int points) {
        this.score += points;
    }

    @Override
    public String toString() {
        return name;  // Return participant's name as the string representation
    }
}
