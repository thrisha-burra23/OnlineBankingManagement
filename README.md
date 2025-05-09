#**FINVIA - Online Banking Application**

## Overview
This project is a banking application that allows users to manage their accounts, apply for loans, and perform transfers. It includes functionalities for both users and administrators, providing a comprehensive solution for banking operations.
-> users accounts are allowed to create only when admin approves.
->Email is received when an loan applcation is made and also when a loan is approved.
->Admin has the authoriy to add money to users accounts.
->Users can create multiple accounts with same email(checking and savings).
->Loan is calcuted with days based on intrest while applying.


## Project Structure
The project is organized into several packages and files, each serving a specific purpose:

### 1. **Model Classes**
These classes represent the core entities of the application.

- **User .java**: Represents a user in the banking system.
- **Admin.java**: Represents an administrator with access to manage users and accounts.
- **BankAccount.java**: Represents a bank account associated with a user.
- **Loan.java**: Represents a loan taken by a user.
- **Transfer.java**: Represents a transfer transaction between accounts.

### 2. **Data Access Objects (DAOs)**
These classes handle database operations for the respective models.

- **User DAO.java**: Manages user-related database operations such as registration, authentication, and user status updates.
- **AdminDAO.java**: Manages admin-related operations, including authentication.
- **BankAccountDAO.java**: Handles operations related to bank accounts, including account creation and balance updates.
- **LoanDAO.java**: Manages loan-related operations, including loan creation and status updates.
- **TransferDAO.java**: Handles transfer operations, including creating transfers and retrieving transfer history.

### 3. **Servlets**
These classes handle HTTP requests and responses, acting as controllers in the MVC architecture.

- **AdminLoginServlet.java**: Manages admin login and session handling.
- **LoanServlet.java**: Handles loan applications and displays user loans.
- **ProcessLoanServlet.java**: Processes loan approvals and rejections by admins.
- **RegisterServlet.java**: Manages user registration.
- **TransactionHistoryServlet.java**: Displays the transaction history for users.
- **TransferServlet.java**: Manages fund transfers between accounts.
- **AdminAccountsServlet.java**: Displays all accounts for admin management.

### 4. **Utilities**
These classes provide utility functions for the application.

- **DatabaseUtil.java**: Contains methods for establishing database connections.
- **EmailUtil.java**: Handles email notifications for loan applications and approvals.

### 5. **Configuration Files**
These files contain configuration settings for the application.

- **server.xml**: Configuration for the Tomcat server.
- **context.xml**: Context configuration for web applications.
- **web.xml**: Deployment descriptor for the web application.
- **logging.properties**: Configuration for logging settings.

### 6. **XML Files**
These files are used for various configurations and settings.

- **encodings.xml**: Defines character encodings for the project.
- **compiler.xml**: Configuration for the compiler settings.
- **jarRepositories.xml**: Configuration for remote repositories.

### 7. **Git Files**
These files are related to version control and are not directly part of the application logic.

- **.gitignore**: Specifies files and directories to be ignored by Git.
- **commit-msg.sample**: Sample commit message template.
- **applypatch-msg.sample**: Sample apply patch message template.

## Features
- **User  Registration**: Users can register with their details and receive admin approval.
- **Admin Management**: Admins can manage users, accounts, loans, and transfers.
- **Loan Application**: Users can apply for loans, which can be approved or rejected by admins.
- **Account Management**: Users can create and manage their bank accounts.
- **Transfer Funds**: Users can transfer funds between their accounts.

## Technologies Used
- **Java**: The primary programming language for the application.
- **JSP/Servlets**: For handling web requests and responses.
- **SQL**: For database operations.
- **Tomcat**: The web server for deploying the application.
- **SLF4J**: For logging purposes.

## Setup Instructions
1. **Clone the Repository**:
   
   git clone <repository-url>
   cd <repository-directory>
 

2. **Set Up Database**:
   - Create a database and configure the connection settings in -->DatabaseUtil.java.

3. **Deploy on Tomcat**:
   - Package the application as a WAR file and deploy it on a Tomcat server.

4. **Access the Application**:
   - Open a web browser and navigate to -->http://localhost:8080/<context-path>..

## Contributing
Contributions are welcome! Please follow these steps:
1. Fork the repository.
2. Create a new branch
    --> git checkout -b feature-branch.
3. Make your changes and commit them
    --> git commit -m 'Add new feature'
4. Push to the branch
    -->git push origin feature-branch
5. Create a pull request.



## Contact
For any inquiries, please contact the project maintainer at thrishagoud023@gmail.com.
