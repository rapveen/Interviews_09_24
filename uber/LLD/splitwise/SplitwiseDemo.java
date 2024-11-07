package uber.LLD.splitwise;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// Core entities
class User {
    private String userId;
    private String name;
    private String email;
    private Set<Group> groups;
    private Map<User, BigDecimal> balances;
    private ReentrantLock lock;

    public User(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.groups = new HashSet<>();
        this.balances = new ConcurrentHashMap<>();
        this.lock = new ReentrantLock();
    }

    public void updateBalance(User user, BigDecimal amount) {
        lock.lock();
        try {
            balances.merge(user, amount, BigDecimal::add);
        } finally {
            lock.unlock();
        }
    }

    // Getters and setters
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public Map<User, BigDecimal> getBalances() { return new HashMap<>(balances); }
}

class Group {
    private String groupId;
    private String name;
    private Set<User> members;
    private List<Expense> expenses;
    private ReentrantLock lock;

    public Group(String groupId, String name) {
        this.groupId = groupId;
        this.name = name;
        this.members = new HashSet<>();
        this.expenses = new ArrayList<>();
        this.lock = new ReentrantLock();
    }

    public void addMember(User user) {
        lock.lock();
        try {
            members.add(user);
        } finally {
            lock.unlock();
        }
    }

    public void addExpense(Expense expense) {
        lock.lock();
        try {
            expenses.add(expense);
            // Notify observers about new expense
            ExpenseNotifier.getInstance().notifyObservers(expense);
        } finally {
            lock.unlock();
        }
    }

    // Getters
    public Set<User> getMembers() { return new HashSet<>(members); }
    public String getGroupId() { return groupId; }
}

// Expense related classes using Strategy Pattern
interface SplitStrategy {
    Map<User, BigDecimal> calculateSplits(BigDecimal amount, List<User> users, Map<User, Integer> shares);
}

class EqualSplitStrategy implements SplitStrategy {
    @Override
    public Map<User, BigDecimal> calculateSplits(BigDecimal amount, List<User> users, Map<User, Integer> shares) {
        Map<User, BigDecimal> splits = new HashMap<>();
        BigDecimal splitAmount = amount.divide(BigDecimal.valueOf(users.size()), 2, BigDecimal.ROUND_HALF_UP);
        users.forEach(user -> splits.put(user, splitAmount));
        return splits;
    }
}

class PercentageSplitStrategy implements SplitStrategy {
    @Override
    public Map<User, BigDecimal> calculateSplits(BigDecimal amount, List<User> users, Map<User, Integer> percentages) {
        Map<User, BigDecimal> splits = new HashMap<>();
        percentages.forEach((user, percentage) -> {
            BigDecimal split = amount.multiply(BigDecimal.valueOf(percentage))
                    .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
            splits.put(user, split);
        });
        return splits;
    }
}

// Factory Pattern for creating split strategies
class SplitStrategyFactory {
    public static SplitStrategy createStrategy(String strategyType) {
        return switch (strategyType.toLowerCase()) {
            case "equal" -> new EqualSplitStrategy();
            case "percentage" -> new PercentageSplitStrategy();
            default -> throw new IllegalArgumentException("Unknown split strategy: " + strategyType);
        };
    }
}

// Observer Pattern for expense notifications
interface ExpenseObserver {
    void onExpenseAdded(Expense expense);
}

class ExpenseNotifier {
    private static ExpenseNotifier instance;
    private List<ExpenseObserver> observers;

    private ExpenseNotifier() {
        observers = new ArrayList<>();
    }

    public static synchronized ExpenseNotifier getInstance() {
        if (instance == null) {
            instance = new ExpenseNotifier();
        }
        return instance;
    }

    public void addObserver(ExpenseObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers(Expense expense) {
        observers.forEach(observer -> observer.onExpenseAdded(expense));
    }
}

// Expense class using Builder Pattern
class Expense {
    private final String expenseId;
    private final String description;
    private final BigDecimal amount;
    private final User paidBy;
    private final Map<User, BigDecimal> splits;
    private final LocalDateTime createdAt;
    private final String category;

    private Expense(ExpenseBuilder builder) {
        this.expenseId = builder.expenseId;
        this.description = builder.description;
        this.amount = builder.amount;
        this.paidBy = builder.paidBy;
        this.splits = builder.splits;
        this.createdAt = builder.createdAt;
        this.category = builder.category;
    }

    public static class ExpenseBuilder {
        private String expenseId;
        private String description;
        private BigDecimal amount;
        private User paidBy;
        private Map<User, BigDecimal> splits;
        private LocalDateTime createdAt;
        private String category;

        public ExpenseBuilder(String expenseId, BigDecimal amount, User paidBy) {
            this.expenseId = expenseId;
            this.amount = amount;
            this.paidBy = paidBy;
            this.createdAt = LocalDateTime.now();
        }

        public ExpenseBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ExpenseBuilder withSplits(Map<User, BigDecimal> splits) {
            this.splits = splits;
            return this;
        }

        public ExpenseBuilder withCategory(String category) {
            this.category = category;
            return this;
        }

        public Expense build() {
            return new Expense(this);
        }
    }

    // Getters
    public String getExpenseId() { return expenseId; }
    public BigDecimal getAmount() { return amount; }
    public User getPaidBy() { return paidBy; }
    public Map<User, BigDecimal> getSplits() { return new HashMap<>(splits); }
}

// Service layer
class SplitwiseService {
    private final Map<String, User> users;
    private final Map<String, Group> groups;
    private final ReentrantLock lock;

    public SplitwiseService() {
        this.users = new ConcurrentHashMap<>();
        this.groups = new ConcurrentHashMap<>();
        this.lock = new ReentrantLock();
    }

    public User createUser(String userId, String name, String email) {
        User user = new User(userId, name, email);
        users.put(userId, user);
        return user;
    }

    public Group createGroup(String groupId, String name, User creator) {
        lock.lock();
        try {
            Group group = new Group(groupId, name);
            group.addMember(creator);
            groups.put(groupId, group);
            return group;
        } finally {
            lock.unlock();
        }
    }

    public void addExpense(Group group, String expenseId, String description, 
                          BigDecimal amount, User paidBy, String splitStrategy, 
                          List<User> involvedUsers, Map<User, Integer> shares) {
        // Calculate splits using strategy pattern
        SplitStrategy strategy = SplitStrategyFactory.createStrategy(splitStrategy);
        Map<User, BigDecimal> splits = strategy.calculateSplits(amount, involvedUsers, shares);

        // Create expense using builder pattern
        Expense expense = new Expense.ExpenseBuilder(expenseId, amount, paidBy)
                .withDescription(description)
                .withSplits(splits)
                .withCategory("General")
                .build();

        // Update balances
        splits.forEach((user, split) -> {
            if (!user.equals(paidBy)) {
                paidBy.updateBalance(user, split);
                user.updateBalance(paidBy, split.negate());
            }
        });

        group.addExpense(expense);
    }

    public Map<User, BigDecimal> getBalances(User user) {
        return user.getBalances();
    }
}

// Main class to demonstrate usage
public class SplitwiseDemo {
    public static void main(String[] args) {
        SplitwiseService splitwiseService = new SplitwiseService();

        // Create users
        User alice = splitwiseService.createUser("u1", "Alice", "alice@example.com");
        User bob = splitwiseService.createUser("u2", "Bob", "bob@example.com");
        User charlie = splitwiseService.createUser("u3", "Charlie", "charlie@example.com");

        // Create a group
        Group group = splitwiseService.createGroup("g1", "Weekend Trip", alice);
        group.addMember(bob);
        group.addMember(charlie);

        // Add expense observer for notifications
        ExpenseNotifier.getInstance().addObserver(expense -> 
            System.out.println("New expense added: "  + 
                             " Amount: $" + expense.getAmount()));

        // Add some expenses
        try {
            // Equal split expense
            splitwiseService.addExpense(
                group,
                "e1",
                "Dinner",
                new BigDecimal("100"),
                alice,
                "equal",
                List.of(alice, bob, charlie),
                new HashMap<>()
            );

            // Percentage split expense
            Map<User, Integer> percentages = new HashMap<>();
            percentages.put(bob, 40);
            percentages.put(charlie, 60);
            
            splitwiseService.addExpense(
                group,
                "e2",
                "Movie tickets",
                new BigDecimal("50"),
                bob,
                "percentage",
                List.of(bob, charlie),
                percentages
            );

            // Print balances
            System.out.println("\nFinal Balances:");
            for (User user : List.of(alice, bob, charlie)) {
                System.out.println(user.getName() + "'s balances:");
                splitwiseService.getBalances(user).forEach((otherUser, amount) -> 
                    System.out.println("  " + otherUser.getName() + ": $" + amount));
            }

        } catch (Exception e) {
            System.err.println("Error processing expenses: " + e.getMessage());
        }
    }
}
