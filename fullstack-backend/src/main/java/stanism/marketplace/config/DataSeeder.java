package stanism.marketplace.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.ApplicationArguments;
import org.springframework.security.crypto.password.PasswordEncoder;
import stanism.marketplace.model.Category;
import stanism.marketplace.model.Favorite;
import stanism.marketplace.model.Image;
import stanism.marketplace.model.Item;
import stanism.marketplace.model.Message;
import stanism.marketplace.model.Order;
import stanism.marketplace.model.OrderStatus;
import stanism.marketplace.model.Role;
import stanism.marketplace.model.User;
import stanism.marketplace.repository.CategoryRepository;
import stanism.marketplace.repository.FavoriteRepository;
import stanism.marketplace.repository.ImageRepository;
import stanism.marketplace.repository.ItemRepository;
import stanism.marketplace.repository.MessageRepository;
import stanism.marketplace.repository.OrderRepository;
import stanism.marketplace.repository.UserRepository;

import java.util.Random;
import java.util.List;

/**
 * Configuration class for seeding initial data into the database.
 */
@Configuration
public class DataSeeder {

    /** Random number generator for creating random data. */
    private final Random random = new Random();

    /** Repository for user data access. */
    private final UserRepository userRepository;

    /** Repository for category data access. */
    private final CategoryRepository categoryRepository;

    /** Repository for item data access. */
    private final ItemRepository itemRepository;

    /** Repository for image data access. */
    private final ImageRepository imageRepository;

    /** Repository for favorite data access. */
    private final FavoriteRepository favoriteRepository;

    /** Repository for message data access. */
    private final MessageRepository messageRepository;

    /** Repository for order data access. */
    private final OrderRepository orderRepository;

    /** Password encoder for user passwords. */
    private final PasswordEncoder passwordEncoder;

    /** Array of Norwegian city coordinates for random location generation. */
    private final double[][] cityCoordinates = {
            { 59.9139, 10.7522 }, // Oslo
            { 60.3913, 5.3221 }, // Bergen
            { 63.4305, 10.3951 }, // Trondheim
            { 58.9700, 5.7331 }, // Stavanger
            { 59.7441, 10.2049 }, // Drammen
            { 59.2167, 10.9500 }, // Fredrikstad
            { 58.1467, 7.9956 }, // Kristiansand
            { 58.8526, 5.7333 }, // Sandnes
            { 69.6492, 18.9553 }, // Tromsø
            { 59.2833, 11.1167 } // Sarpsborg
    };

    /**
     * Creates a new DataSeeder with the specified repositories and password encoder.
     *
     * @param userRepository
     *            repository for user data access
     * @param categoryRepository
     *            repository for category data access
     * @param itemRepository
     *            repository for item data access
     * @param imageRepository
     *            repository for image data access
     * @param favoriteRepository
     *            repository for favorite data access
     * @param messageRepository
     *            repository for message data access
     * @param orderRepository
     *            repository for order data access
     * @param passwordEncoder
     *            the password encoder to use
     */
    public DataSeeder(
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            ItemRepository itemRepository,
            ImageRepository imageRepository,
            FavoriteRepository favoriteRepository,
            MessageRepository messageRepository,
            OrderRepository orderRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.itemRepository = itemRepository;
        this.imageRepository = imageRepository;
        this.favoriteRepository = favoriteRepository;
        this.messageRepository = messageRepository;
        this.orderRepository = orderRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private double[] getRandomCoordinates() {
        return cityCoordinates[random.nextInt(cityCoordinates.length)];
    }

    /**
     * Initializes the database with sample data only if it's empty.
     *
     * @return a CommandLineRunner that initializes the database
     */
    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            // Check if database is empty by checking if any users exist
            if (userRepository.count() == 0) {
                createCategories();
                createUsers();
                createItemsAndRelatedData();
                createRandomFavoritesAndMessages();
                createRandomOrders();
            }
        };
    }

    /**
     * Cleans the database by removing all data.
     * This method should be called with caution as it will delete all data.
     * Only runs if the command line argument '--clean-db' is present.
     * 
     * @param args
     *            the command line arguments
     * @return a CommandLineRunner that cleans the database
     */
    @Bean
    CommandLineRunner cleanDatabase(ApplicationArguments args) {
        return commandLineArgs -> {
            if (args.containsOption("clean-db")) {
                // Delete all data in reverse order of dependencies
                orderRepository.deleteAll();
                messageRepository.deleteAll();
                favoriteRepository.deleteAll();
                imageRepository.deleteAll();
                itemRepository.deleteAll();
                categoryRepository.deleteAll();
                userRepository.deleteAll();
            }
        };
    }

    private void createCategories() {
        String[] categoryNames = {
                "Elektronikk", "Klær", "Møbler", "Bøker", "Sport", "Bil", "Hjem", "Barn"
        };

        for (String categoryName : categoryNames) {
            if (!categoryRepository.findByName(categoryName).isPresent()) {
                Category category = new Category();
                category.setName(categoryName);
                categoryRepository.save(category);
            }
        }
    }

    private void createUsers() {
        // Check if admin user already exists
        if (!userRepository.findByEmail("admin@example.com").isPresent()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }

        String[] firstNames = { "Ola", "Kari", "Erik", "Ingrid", "Lars", "Mari", "Anders", "Hanne", "Per", "Anne" };
        String[] lastNames = { "Hansen", "Johansen", "Olsen", "Larsen", "Andersen",
                "Nilsen", "Kristiansen", "Jensen", "Karlsen", "Berg" };

        // Create regular users, checking for duplicates
        int usersCreated = 0;
        while (usersCreated < 20) {
            String firstName = firstNames[random.nextInt(firstNames.length)];
            String lastName = lastNames[random.nextInt(lastNames.length)];
            String username = firstName + "_" + lastName;
            String email = username.toLowerCase() + "@example.com";

            // Skip if user with this email already exists
            if (userRepository.findByEmail(email).isPresent()) {
                continue;
            }

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("password123"));
            user.setRole(Role.USER);
            userRepository.save(user);
            usersCreated++;
        }
    }

    private void createItemsAndRelatedData() {
        String[] electronicsItems = {
                "iPhone 14 Pro", "Samsung Galaxy S23", "MacBook Air M2", "iPad Pro", "Sony WH-1000XM5",
                "Apple Watch Series 8", "Samsung 65\" QLED TV", "PlayStation 5", "Xbox Series X", "Nintendo Switch OLED"
        };

        String[] clothingItems = {
                "Canada Goose jakke", "Helly Hansen regnjakke", "Dale of Norway genser", "Norrøna skikkjakke",
                "Bergans vinterjakke", "Devold ullundertøy", "Stormberg vinterstøvler", "Kari Traa skijakke"
        };

        String[] furnitureItems = {
                "Stressless lenestol", "Ekkeberg spisebord", "Høie of Norway sofa", "Svensson seng",
                "Vestre stuebord", "Fora Design bokhylle", "Vatne møbler skap", "Ting stol"
        };

        String[] booksItems = {
                "Min kamp", "Sofies verden", "Naiv. Super", "Beatles", "Knut Hamsun samlede verker",
                "Harry Potter og de vises stein", "Hundre år med ensomhet", "Kafka på stranden"
        };

        String[] sportItems = {
                "Atomic skisett", "Fischer langrennsski", "Rossignol snowboard", "Swix stavar",
                "Salomon skisko", "Dynafit fjellski", "Black Diamond klatreutstyr", "Norrøna fjelljakke"
        };

        String[] carItems = {
                "Volkswagen Golf", "Toyota RAV4", "Tesla Model 3", "Volvo XC60", "Audi A4",
                "BMW 3-serie", "Mercedes C-klasse", "Hyundai Tucson"
        };

        String[] homeItems = {
                "Høie of Norway lampe", "Vestre design stol", "Fora Design bokhylle", "Ekkeberg spisebord",
                "Stressless lenestol", "Svensson seng", "Vatne møbler skap", "Ting stol"
        };

        String[] childrenItems = {
                "Stokke Tripp Trapp", "Hauck barnevogn", "BabyBjörn bæresele", "Stokke Xplory",
                "Leander seng", "Done by Deer leker", "Konges Sløjd klær", "Liewood leker"
        };

        createItemsForCategory("Elektronikk", electronicsItems, 50);
        createItemsForCategory("Klær", clothingItems, 40);
        createItemsForCategory("Møbler", furnitureItems, 35);
        createItemsForCategory("Bøker", booksItems, 30);
        createItemsForCategory("Sport", sportItems, 45);
        createItemsForCategory("Bil", carItems, 25);
        createItemsForCategory("Hjem", homeItems, 30);
        createItemsForCategory("Barn", childrenItems, 25);
    }

    private void createItemsForCategory(String categoryName, String[] itemTemplates, int count) {
        Category category = categoryRepository.findByName(categoryName).get();

        for (int i = 0; i < count; i++) {
            String title = itemTemplates[random.nextInt(itemTemplates.length)];
            String fullTitle = title + " " + (random.nextInt(5) + 1);

            // Skip if item with this title already exists
            if (itemRepository.findByTitle(fullTitle).isPresent()) {
                continue;
            }

            double[] coords = getRandomCoordinates();

            Item item = new Item.Builder()
                    .title(fullTitle)
                    .briefDescription("Flott " + title.toLowerCase() + " i god stand")
                    .fullDescription("Dette er en " + title.toLowerCase() +
                            " i veldig god stand. Brukt i " + (random.nextInt(3) + 1) +
                            " år. Komplett med original emballasje og tilbehør.")
                    .price((double) (1000 + random.nextInt(20000)))
                    .latitude(coords[0])
                    .longitude(coords[1])
                    .user(userRepository.findAll().get(random.nextInt(20) + 1)) // Skip admin
                    .category(category)
                    .build();
            itemRepository.save(item);

            // Add 1-3 images per item
            int imageCount = random.nextInt(3) + 1;
            for (int j = 0; j < imageCount; j++) {
                String imageUrl = "https://example.com/" +
                        title.toLowerCase().replace(" ", "_") + "_" + j + ".jpg";
                Image image = new Image(item, imageUrl, title + " bilde " + (j + 1));
                imageRepository.save(image);
            }
        }
    }

    private void createRandomFavoritesAndMessages() {
        // Create 100 random favorites
        for (int i = 0; i < 100; i++) {
            User user = userRepository.findAll().get(random.nextInt(20) + 1);
            Item item = itemRepository.findAll().get(random.nextInt(itemRepository.findAll().size()));

            // Skip if this favorite already exists
            if (favoriteRepository.findByUserAndItem(user, item).isPresent()) {
                continue;
            }

            Favorite favorite = new Favorite(user, item);
            favoriteRepository.save(favorite);
        }

        // Create 200 random messages
        for (int i = 0; i < 200; i++) {
            User sender = userRepository.findAll().get(random.nextInt(20) + 1);
            User receiver;
            do {
                receiver = userRepository.findAll().get(random.nextInt(20) + 1);
            } while (receiver.equals(sender));

            Item item = itemRepository.findAll().get(random.nextInt(itemRepository.findAll().size()));

            String[] messages = {
                    "Hei, er dette fortsatt tilgjengelig?",
                    "Kan du gi meg mer informasjon?",
                    "Hva er din beste pris?",
                    "Kan jeg komme og se på det?",
                    "Er det mulig å forhandle om prisen?",
                    "Når kan jeg hente det?",
                    "Har du flere bilder?",
                    "Er det i god stand?"
            };

            String messageContent = messages[random.nextInt(messages.length)];

            // Skip if this exact message already exists
            List<Message> existingMessages = messageRepository
                    .findBySenderAndReceiverAndItemOrReceiverAndSenderAndItemOrderByTimestampAsc(
                            sender, receiver, item, sender, receiver, item);
            if (!existingMessages.isEmpty() && existingMessages.stream()
                    .anyMatch(m -> m.getContent().equals(messageContent))) {
                continue;
            }

            Message message = new Message(sender, receiver, item, messageContent);
            messageRepository.save(message);
        }
    }

    private void createRandomOrders() {
        // Create 50 random orders
        for (int i = 0; i < 50; i++) {
            User buyer = userRepository.findAll().get(random.nextInt(20) + 1);
            Item item = itemRepository.findAll().get(random.nextInt(itemRepository.findAll().size()));

            // Skip if order for this buyer and item already exists
            if (orderRepository.findByBuyerAndItem(buyer, item).isPresent()) {
                continue;
            }

            String[] paymentMethods = { "Vipps", "Kredittkort", "BankID", "PayPal" };
            OrderStatus[] statuses = { OrderStatus.RESERVED, OrderStatus.COMPLETED, OrderStatus.CANCELLED };

            Order order = new Order(buyer, item, paymentMethods[random.nextInt(paymentMethods.length)]);
            order.setStatus(statuses[random.nextInt(statuses.length)]);
            order.setTransactionId("TRX" + String.format("%06d", random.nextInt(1000000)));
            orderRepository.save(order);
        }
    }
}