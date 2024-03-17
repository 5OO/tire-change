# Tire Change Booking app

## Project Overview

This project is a tire change booking application, allowing users to view available tire change times and book appointments. The backend is built with Java 17, Spring Boot, and Maven, while the frontend uses Vue.js with Vite.

## Prerequisites

Before you begin, ensure you have the following installed:
- Java 17
- Maven
- Node.js (for the frontend)
- Your preferred IDE (IntelliJ IDEA or VS Code)

## Setting Up and Running the Backend
## Getting Started

Clone this repository using `git clone https://github.com/5OO/tire-change`



## Building the Application

### IntelliJ IDEA Users:
1. From the main menu, select **"Maven" -> "Lifecycle" -> "Install"**

### VS Code Users:
1. Open Terminal in VS Code (from the top menu: **"Terminal" -> "New Terminal"**)
2. In the Terminal, run: `mvn clean install`

## Running the Application

## IntelliJ IDEA Users:
1. **Open the Project**: Open IntelliJ IDEA, select `File > Open` and choose the project directory.
2. **Install and Enable Lombok**: Install the Lombok plugin by navigating to `File > Settings > Plugins > Marketplace > search for 'Lombok Plugin'`. Enable annotation processing by going to `File > Settings > Build, Execution, Deployment > Compiler > Annotation Processors`.
3. **Run the Application**: Right-click on the `src/main/java/your/package/Application.java` file and select `Run 'Application'`.

## VS Code Users:
1. **Install Java Extension Pack**: Open VS Code, and install the 'Java Extension Pack' from the marketplace.
2. **Open the Project**: Open the project folder by selecting `File > Open Folder`.
3. **Install and Enable Lombok**: Install the [Lombok Annotations Support for VS Code](https://marketplace.visualstudio.com/items?itemName=GabrielBB.vscode-lombok) and follow its documentation to enable it.
4. **Run the Application**: Navigate to the `src/main/java/your/package/Application.java` file, right-click, and select `Run Java`.

# Contribution

Contributions to both the frontend and backend are welcome. Please ensure to follow the project's coding standards.

To contribute:
1. Fork this repository
2. Create your feature branch (`git checkout -b feature/yourFeature`)
3. Commit your changes (`git commit -m 'Add yourFeature'`)
4. Push to the branch (`git push origin feature/yourFeature`)
5. Create a new Pull Request

## Frontend Setup:
1. Navigate to the frontend directory: `cd frontend`
2. Install dependencies: `npm install`
3. Serve the application: `npm run serve`

### Note:

- The instructions assume basic familiarity with running Java and Node.js projects.
- For VS Code users, especially on Mac, Lombok might require additional setup. Instructions are generalized because the exact steps can depend on the extensions and settings used. Users are encouraged to refer to the Lombok documentation or relevant extensions' documentation for detailed setup instructions.

