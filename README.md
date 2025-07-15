# Audio Gear Checkout System with Database 

This is a Java Swing application that connects to a SQLite database (`checkout.db`) to manage audio gear checkout records. It supports basic CRUD operations with input validation.

## Features

- Add new gear checkout records
- Delete existing records
- Update any field (name, gear, dates, returned)
- View all records in a table
- Filter and list overdue gear
- Input validation for dates and return status

## JDBC Driver

This project uses the SQLite JDBC driver from Xerial.

To run the project:

1. Download the driver from:
   https://github.com/xerial/sqlite-jdbc/releases
2. In IntelliJ:
   - Go to `File > Project Structure > Modules > Dependencies`
   - Click the `+` button and select the downloaded `.jar` file
   - Set the scope to `Compile`
3. Rebuild the project and run `CheckoutGUI.java`





