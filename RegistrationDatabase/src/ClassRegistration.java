
import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

// We import java.io to be able to read from the command line

import java.io.*;

public class ClassRegistration
{

	/**
	 * @param args
	 *            the command line arguments
	 */

	public static void main(String[] args) throws SQLException, IOException
	{
		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver"); // Load the Oracle JDBC driver
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("Could not load the driver");
		}
		
//log-in block
		try
		{
			String Oracleuser, Oraclepass;
			Oracleuser = readEntry("Oracle username:");
			Oraclepass = readEntry("Oracle Password:");
			String url = "jdbc:oracle:thin:@fedora2.uscupstate.edu:1521:xe";
			Connection conn = DriverManager.getConnection(url, Oracleuser, Oraclepass);// connect oracle

			Statement st = conn.createStatement();
			ResultSet student_set = st.executeQuery("SELECT * FROM Student");
			ResultSet course_set = st.executeQuery("SELECT * FROM Course");
			ResultSet registered_set = st.executeQuery("SELECT * FROM Registered");
			Scanner scan = new Scanner(System.in);
//menu options
			int input = 1;
			while (input != 10)
			{

				System.out.println("************************************************************************");
				System.out.println("***\t\t\t\t\t\t\t\t     ***");
				System.out.println("***\t\tWelcome to Online Registration System\t\t     ***");
				System.out.println("***\t\t\t\t\t\t\t\t     ***");
				System.out.println("************************************************************************");

				System.out.println("1. Add a course");
				System.out.println("2. Delete a course");
				System.out.println("3. Add a student");
				System.out.println("4. Delete a student");
				System.out.println("5. Register a course");
				System.out.println("6. Drop a course");
				System.out.println("7. Check student registration");
				System.out.println("8. Upload grades");
				System.out.println("9. Check grade");
				System.out.println("10. Quit\n\n");

				System.out.print("Please enter a menu option: ");
				input = scan.nextInt();
				switch (input)
				{
				case 1:
					add_course(conn);
					break;
				case 2:
					delete_course(conn);
					break;
				case 3:
					add_student(conn);
					break;
				case 4:
					delete_student(conn);
					break;
				case 5:
					register_course(conn);
					break;
				case 6:
					drop_course(conn);
					break;
				case 7:
					check_registration(conn);
					break;
				case 8:
					upload_grades(conn);
					break;
				case 9:
					check_grade(conn);
					break;
				case 10:
					quit(conn);
				}
				while (student_set.next())
				{
					int ssn = student_set.getInt(1);
					String name = student_set.getString(2);
					String addr = student_set.getString(3);
					String major = student_set.getString(4);
				}
				while (course_set.next())
				{
					String code = course_set.getString(1);
					String title = course_set.getString(2);
				}
				while (registered_set.next())
				{
					int ssn = registered_set.getInt(1);
					String code = registered_set.getString(2);
					int year = registered_set.getInt(3);
					String semester = registered_set.getString(4);
					String grade = registered_set.getString(5);
				}

			}
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
		}
	}

//add course
	static void add_course(Connection conn) throws SQLException, IOException
	{
		Statement st = conn.createStatement();
		System.out.println("Please enter Courses information below: \n");
		String code = readEntry("Course Code: ");
		String title = readEntry("Course Title: ");
		String output = title.substring(0, 1).toUpperCase() + title.substring(1);
		String query = "insert into course values (" + "'" + code.toUpperCase() + "','" + output + "')";
		ResultSet pre_check = st
				.executeQuery("select count(code) from course where code = '" + code.toUpperCase() + "'");
		pre_check.next();

		try
		{
			if (pre_check.getInt(1) > 0)
			{
				System.out.println("That course code already exist in the System");
			}
			else
			{
				st.executeUpdate(query);
				System.out.println("You have successfully added the course: " + title.toUpperCase() + " " + code.toUpperCase());
			}

		}
		catch (SQLException e)
		{
			System.out.println("Sorry that course is already in the system as a course;");
			while (e != null)
			{
				System.out.println("Message:" + e.getMessage());
				e = e.getNextException();
			}
			return;
		}

		System.out.println();
	}

//delete course
	static void delete_course(Connection conn) throws SQLException, IOException
	{
		Statement st = conn.createStatement();
		System.out.println("Please enter the Course code in which you want to delete below: \n");
		String code = readEntry("Course Code: ");
		String query = "delete from course where code = '" + code.toUpperCase() + "'";

		ResultSet pre_check = st
				.executeQuery("select count(code) from course where code = '" + code.toUpperCase() + "'");
		pre_check.next();
		try
		{
			if (pre_check.getInt(1) > 0)
			{
				st.executeUpdate("delete from registered where code = '" + code.toUpperCase() + "'");
				st.executeUpdate(query);
				System.out.println("You have successfully deleted the course with code: " + code.toUpperCase());
			}
			else
			{
				System.out.println("Sorry the course you are trying to delete does not exist");
			}

		}
		catch (SQLException e)
		{
			System.out.println("Sorry there was an error deleting that course");
			while (e != null)
			{
				System.out.println("Message:" + e.getMessage());
				e = e.getNextException();
			}
			return;
		}

		System.out.println();
	}

//add student
	static void add_student(Connection conn) throws SQLException, IOException
	{
		Statement st = conn.createStatement();
		System.out.println("Please enter the Students information below: \n");
		String ssn = readEntry("Social Security Number ");
		String name = readEntry("First and Last Name: ");
		String address = readEntry("Student Address: ");
		String major = readEntry("Students Major: ");
		String name_output = name.toUpperCase().substring(0, 1) + name.substring(1);
		String query = "insert into student values (" + "'" + ssn + "','" + name_output + "','" + address + "','"
				+ major.toUpperCase() + "')";
		ResultSet pre_check = st.executeQuery("select count(ssn) from student where ssn = '" + ssn + "'");
		pre_check.next();
		try
		{
			if (pre_check.getInt(1) > 0)
			{
				System.out.println("Sorry a student under SSN: " + ssn + " is already in the System");
			}
			else
			{
				st.executeUpdate(query);
				System.out.println("You have successfully added the Student: " + name);
			}

		}
		catch (SQLException e)
		{
			System.out.println("Sorry there was an error adding that Student");
			while (e != null)
			{
				System.out.println("Message:" + e.getMessage());
				e = e.getNextException();
			}
			return;
		}

		System.out.println();
	}

//delete student
	static void delete_student(Connection conn) throws SQLException, IOException
	{
		Statement st = conn.createStatement();
		System.out.println("Please enter the SSN of the Student: \n");
		String ssn = readEntry("Social Security Number ");
		String query = "delete from student where ssn = '" + ssn + "'";
		String query2 = "delete from registered where ssn = '" + ssn + "'";
		ResultSet pre_check = st.executeQuery("select count(ssn) from student where ssn = '" + ssn + "'");
		pre_check.next();
		try
		{
			if (pre_check.getInt(1) > 0)
			{
				st.executeUpdate(query2);
				st.executeUpdate(query);
				System.out.println("You have successfully deleted the Student with the SSN: " + ssn);
			}
			else
			{
				System.out.println("Sorry that student does not exist or has already been deleted from the system.");
			}
		}
		catch (SQLException e)
		{
			System.out.println("Sorry there was an error deleting that Student");
			while (e != null)
			{
				System.out.println("Message:" + e.getMessage());
				e = e.getNextException();
			}
			return;
		}

		System.out.println();
	}

//register for course
	static void register_course(Connection conn) throws SQLException, IOException
	{
		Statement st = conn.createStatement();
		System.out.println("Please enter the Course information below to register: \n");
		String ssn = readEntry("Students SSN: ");
		String code = readEntry("Course Code: ");
		String year = readEntry("Year of Semester: ");
		String semester = readEntry("Semester: ");
		semester = semester.substring(0, 1).toUpperCase() + semester.substring(1).toLowerCase();
		String query = "insert into registered (ssn, code, year, semester) values (" + "'" + ssn + "','"
				+ code.toUpperCase() + "','" + year + "','" + semester + "')";
		ResultSet pre_check = st.executeQuery("Select count(ssn) from registered where ssn = '" + ssn + "' and code = '"
				+ code.toUpperCase() + "' and year = '" + year + "' and semester = '" + semester + "'");
		pre_check.next();
		try
		{
			if (pre_check.getInt(1) > 0)
			{
				System.out.println("Sorry that student is already registered for that course");
			}
			else
			{
				st.executeUpdate(query);
				System.out.println("You have successfully registered for the Course under code: " + code.toUpperCase()
						+ " for " + semester + " " + year);
			}

		}
		catch (SQLException e)
		{
			System.out.println("Sorry there was an error registering that Student for that course");
			while (e != null)
			{
				System.out.println("Message:" + e.getMessage());
				e = e.getNextException();
			}
			return;
		}

		System.out.println();
	}

//remove from course
	static void drop_course(Connection conn) throws SQLException, IOException
	{
		Statement st = conn.createStatement();
		System.out.println("Please enter the Course information below to drop: \n");
		String code = readEntry("Course Code: ");
		String ssn = readEntry("Students SSN: ");
		String year = readEntry("Year of Semester: ");
		String semester = readEntry("Semester: ");
		semester = semester.substring(0, 1).toUpperCase() + semester.substring(1);
		String query = "delete from registered where code = '" + code.toUpperCase() + "'and ssn = '" + ssn
				+ "'and year = '" + year + "'and semester = '" + semester + "'";
		ResultSet pre_check = st.executeQuery("select count(ssn) from registered where code = '" + code.toUpperCase()
				+ "' and ssn = '" + ssn + "' and year = '" + year + "' and semester = '" + semester + "'");
		pre_check.next();
		try
		{
			if (pre_check.getInt(1) > 0)
			{
				st.executeUpdate(query);
				System.out.println("You have successfully dropped the Course for ssn: " + ssn + " for course: " + code
						+ " for " + semester + " " + year);
			}
			else
			{
				System.out.println("Sorry that student is not registered for course code: " + code);
			}

		}
		catch (SQLException e)
		{
			System.out.println("Sorry there was an error dropping that Course please try again");
			while (e != null)
			{
				System.out.println("Message:" + e.getMessage());
				e = e.getNextException();
			}
			return;
		}

		System.out.println();
	}
//check registration function
	static void check_registration(Connection conn) throws SQLException, IOException
	{
		Statement st = conn.createStatement();
		System.out.println("Please enter the Students SSN below to see their registration: \n");
		String ssn = readEntry("Students SSN: ");
		String query = "Select * from registered where ssn = '" + ssn + "'";
		ResultSet student_registration = st.executeQuery(query);

		try
		{
			if (student_registration.next())
			{
				System.out.println("SSN: Course:\tYear: Semester:");
				while (student_registration.next())
				{
					String SSN = student_registration.getString("SSN");
					String code = student_registration.getString("CODE");
					String year = student_registration.getString("YEAR");
					String semester = student_registration.getString("SEMESTER");
					System.out.println(SSN + " " + code + " " + year + "  " + semester);
				}
				System.out.println("You have successfully accessed the registration for ssn: " + ssn);
			}
			else
			{
				System.out.println("That student is not registered for any classes");
			}
		}
		catch (SQLException e)
		{
			System.out.println(
					"Sorry there was an error retrieving the registration for ssn: " + ssn + " please try again");
			while (e != null)
			{
				System.out.println("Message:" + e.getMessage());
				e = e.getNextException();
			}
			return;
		}
		System.out.println();
	}
//upload grade function
	static void upload_grades(Connection conn) throws SQLException, IOException
	{
		Statement st = conn.createStatement();
		Statement st2 = conn.createStatement();
		System.out.println("Please enter the Course information below to upload grades: \n");
		String code = readEntry("Course code: ");
		code = code.toUpperCase();
		String year = readEntry("Semester Year: ");
		String semester = readEntry("Semester: ");
		semester = semester.substring(0, 1).toUpperCase() + semester.substring(1).toLowerCase();
		String query = ("Select * from registered where code = '" + code + "' and year = " + year + " and semester ='"
				+ semester + "'");
		Scanner scan2 = new Scanner(System.in);
		ResultSet pre_check = st.executeQuery("Select * from registered where code = '" + code + "'");

		try
		{
			ResultSet u = st.executeQuery(query);
			if(pre_check != null)
			{
				while(u.next())
				{
					String ssn = u.getString("SSN");
					System.out.println("Please enter the grade of student: " + ssn + " for course: " + code);
					String grade_entry = scan2.next().toUpperCase();
					String update = ("update registered set grade = '" + grade_entry + "' where code = '" + code
						+ "' and ssn = '" + ssn + "'");
					st2.executeUpdate(update);
				}
				System.out.println("You have successfully uploaded the grades for Course: " + code);
			}
			else
			{
				System.out.println("That course does not exist");
			}
		}
		catch (SQLException e)
		{
			System.out.println("Sorry there was error uploading grades for that course, please try again");
			while (e != null)
			{
				System.out.println("Message:" + e.getMessage());
				e = e.getNextException();
			}
			return;
		}
		
		System.out.println();
	}
//grade check function
	static void check_grade(Connection conn) throws SQLException, IOException
	{
		Statement st = conn.createStatement();
		System.out.println("Please enter the Course information below to upload grades: \n");
		String code = readEntry("Course code: ");
		code = code.toUpperCase();
		String ssn = readEntry("Student SSN: ");
		String query = ("Select grade from registered where code = '" + code + "' and ssn = '" + ssn + "'");

		ResultSet student_grade = st.executeQuery(query);
		student_grade.next();

		try
		{
			if (student_grade != null)
			{
				System.out.println(
						"The grade for student " + ssn + " in Course " + code + " is a " + student_grade.getString(1));
				System.out.println(
						"\nYou have successfully checked the grade of Student " + ssn + " for Course: " + code);
			}
		}
		catch (SQLException e)
		{
			System.out.println("Sorry there was error finding the grade for that Student for course: " + code);
			while (e != null)
			{
				System.out.println("Message:" + e.getMessage());
				e = e.getNextException();
			}
			return;
		}

		System.out.println();
	}
//closes function
	static void quit(Connection conn) throws Exception
	{
		try
		{
			System.out.print("**You are now logged out of the Registration System**");
			conn.close();
		}
		catch (InputMismatchException e)
		{
			System.out.println("Sorry answer must be Y for yes or N for No, please try again");
		}
		return;

	}

	// Utility function to read a line from standard input
	static String readEntry(String prompt)
	{
		try
		{
			StringBuffer buffer = new StringBuffer();
			System.out.print(prompt);
			System.out.flush();
			int c = System.in.read();
			while (c != '\n' && c != -1)
			{
				buffer.append((char) c);
				c = System.in.read();
			}
			return buffer.toString().trim();
		}
		catch (IOException e)
		{
			return "";
		}
	}

}
