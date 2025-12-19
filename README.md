# Restaurant Management System

A JavaFX-based desktop application for managing a restaurant's operations, including menu management, order processing, sales tracking, and user feedback.

## Features

- **User Interface**: Intuitive GUI with login, menu browsing, checkout, and admin dashboard
- **Menu Management**: Add, view, and manage food and drink items
- **Order Processing**: Handle customer orders with cart functionality
- **Sales Tracking**: Monitor sales data and generate receipts
- **Feedback System**: Collect and manage customer reviews
- **Admin Features**: Administrative access for managing the system

## Technologies Used

- **Java**: Core programming language
- **JavaFX**: For building the desktop GUI
- **Maven**: Build and dependency management

## Project Structure

```
OOP-Project/
├── restaurantsystem/          # Main application code
│   ├── src/main/java/com/anas/
│   │   ├── gui/               # GUI components
│   │   ├── logic/             # Business logic
│   │   ├── models/            # Data models
│   │   └── data/              # Data management
│   ├── src/main/resources/    # Resources (images, CSS)
│   └── pom.xml                # Maven configuration
├── *.txt                      # Data files (favorites, sales, etc.)
├── .gitignore                 # Git ignore file
└── README.md                  # This file
```

## Prerequisites

- Java 21 or higher
- Maven 3.6 or higher

## How to Run

1. Clone the repository:

   ```
   git clone https://github.com/MuhammadAnas126/OOP-Project.git
   cd OOP-Project
   ```

2. Navigate to the project directory:

   ```
   cd restaurantsystem
   ```

3. Build the project:

   ```
   mvn clean compile
   ```

4. Run the application:
   ```
   mvn javafx:run
   ```

## Usage

- **Login**: Use admin credentials to access the system
- **Menu**: Browse available items, add to cart
- **Checkout**: Process orders and generate receipts
- **Admin Dashboard**: Manage menu items, view sales data

## Data Files

The application uses text files in the root directory for data persistence:

- `favorites.txt`: User's favorite items
- `sales_data.txt`: Sales records
- `reviews.txt`: Customer feedback
- And others for orders, receipts, etc.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## License

This project is for educational purposes.

## Author

Muhammad Anas
