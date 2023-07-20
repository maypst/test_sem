package com.napier.sem;

import java.sql.*;
import java.util.*;
public class App
{
    public static void main(String[] args)

    {
        //Scanner sc = new Scanner(System.in);
        //System.out.println("Enter the amount of employees to display");
        //int limit = sc.nextInt();

        int limit = 2;


        Employee e = new Employee();
        // Create new Application
        App a = new App();

        // Connect to database
        //a.connect();
        a.connect();
        // Extract employee salary information
        ArrayList<Employee> employees = e.getAllSalaries(a.con);
        e.printSalaries(employees);

        // Test the size of the returned data - should be 240124
        System.out.println(employees.size());
        // Get Employee
        Employee emp = a.getEmployee("Bezalel");

        // Display results
        a.displayEmployee(emp);

        // Disconnect from database
        a.disconnect();
    }


    /**
     * Connection to MySQL database.
     */
    private Connection con = null;

    /**
     * Connect to the MySQL database.
     */
    public void connect() {
        try {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 3;
        for (int i = 0; i < retries; ++i) {
            System.out.println("Connecting to database...");
            try {
                // Wait a bit for db to start
                Thread.sleep(30000);
                // Connect to database
                //con = DriverManager.getConnection("jdbc:mysql://db:3306/employees?useSSL=false", "root", "example");
                // Connect to database in localhost
                con = DriverManager.getConnection("jdbc:mysql://localhost:33061/employees", "root", "example");
                System.out.println("Successfully connected");
                break;
            } catch (SQLException sqle) {
                System.out.println("Failed to connect to database attempt " + Integer.toString(i));
                System.out.println(sqle.getMessage());
            } catch (InterruptedException ie) {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }
    /**
     * Disconnect from the MySQL database.
     */
    public void disconnect()
    {
        if (con != null)
        {
            try
            {
                // Close connection
                con.close();
            }
            catch (Exception e)
            {
                System.out.println("Error closing connection to database");
            }
        }
    }

    public Employee getEmployee(String first_name)
    {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT dept_manager.emp_no as manager_emp_id, employees.first_name,"+
                            "employees.last_name, employees.emp_no, titles.title, salaries.salary,"+
                            "departments.dept_name, titles.from_date, titles.to_date "+
                            "FROM dept_emp "+
                            "JOIN employees ON employees.emp_no = dept_emp.emp_no "+
                            "JOIN departments ON departments.dept_no = dept_emp.dept_no "+
                            "JOIN salaries ON salaries.emp_no = employees.emp_no "+
                            "JOIN titles ON titles.emp_no = employees.emp_no "+
                            "JOIN dept_manager ON dept_manager.emp_no = employees.emp_no "+
                            "WHERE employees.first_name = " + first_name;
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new employee if valid.
            // Check one is returned
            if (rset.next())
            {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
                emp.title = rset.getString("title");
                emp.salary = rset.getInt("salary");
                emp.dept_name = rset.getString("dept_name");
                return emp;
            }
            else
                return null;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return null;
        }
    }

    public void displayEmployee(Employee emp)
    {
        if (emp != null)
        {
            System.out.println(
                    emp.emp_no + " "
                            + emp.first_name + " "
                            + emp.last_name + "\n"
                            + emp.title + "\n"
                            + "Salary:" + emp.salary + "\n"
                            + emp.dept_name + "\n");
            //+ "Manager: " + emp.manager + "\n");
        }
    }
    public void printSalaries(ArrayList<Employee> employees)
    {
        // Check employees is not null
        if (employees == null)
        {
            System.out.println("No employees");
            return;
        }
        // Print header
        System.out.println(String.format("%-10s %-15s %-20s %-8s", "Emp No", "First Name", "Last Name", "Salary"));
        // Loop over all employees in the list
        for (Employee emp : employees)
        {
            if (emp == null)
                continue;
            String emp_string =
                    String.format("%-10s %-15s %-20s %-8s",
                            emp.emp_no, emp.first_name, emp.last_name, emp.salary);
            System.out.println(emp_string);
        }
    }
}