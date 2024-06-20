package my.edu.utar.fyp;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import androidx.core.content.ContextCompat;

import android.widget.TableLayout;
import android.widget.TableRow;


public class TimetableActivity extends AppCompatActivity {

    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        tableLayout = findViewById(R.id.tableLayout);

        // Perform database operation in a separate thread to avoid blocking the main thread
        new Thread(this::queryDatabase).start();
    }

    private void queryDatabase() {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            // Connection parameters
            String connectionString = "jdbc:mysql://10.0.2.2:3306/exam";
            String username = "root";
            String password = "020528";

            // Establish connection to the database
            connection = DriverManager.getConnection(connectionString, username, password);

            // Create statement
            statement = connection.createStatement();

            // Execute query
            String query = "SELECT examDate, CONCAT(startTime, ' - ', endTime) AS time_range, exam.courseID, course.courseName, roomID \n" +
                    "FROM exam \n" +
                    "INNER JOIN course ON exam.courseID = course.courseID\n" +
                    "ORDER BY examDate;";
            resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String date = resultSet.getString("examDate");
                String timeRange = resultSet.getString("time_range");
                String courseID = resultSet.getString("courseID");
                String courseName = resultSet.getString("courseName");
                String venue = resultSet.getString("roomID");

                runOnUiThread(() -> addRowToTable(date, timeRange, courseID, courseName, venue));
            }

        } catch (ClassNotFoundException | SQLException e) {
            Log.e("TimetableActivity", "Error querying database", e);
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                Log.e("TimetableActivity", "Error closing resources", e);
            }
        }
    }

    private void addRowToTable(String date, String timeRange, String courseID, String courseName, String venue) {
        // Create a row for Date, Time Range, and Venue
        TableRow row = new TableRow(this);
        row.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_background));

        // Add Date TextView
        TextView textViewDate = new TextView(this);
        textViewDate.setText(date);
        textViewDate.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_background));
        textViewDate.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f)); // Set layout weight
        textViewDate.setGravity(Gravity.CENTER); // Align text to center
        row.addView(textViewDate);

        // Add Time Range TextView
        TextView textViewTimeRange = new TextView(this);
        textViewTimeRange.setText(timeRange);
        textViewTimeRange.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_background));
        textViewTimeRange.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f)); // Set layout weight
        textViewTimeRange.setGravity(Gravity.CENTER); // Align text to center
        row.addView(textViewTimeRange);

        // Add Venue TextView
        TextView textViewVenue = new TextView(this);
        textViewVenue.setText(venue);
        textViewVenue.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_background));
        textViewVenue.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f)); // Set layout weight
        textViewVenue.setGravity(Gravity.CENTER); // Align text to center
        row.addView(textViewVenue);

        // Add the row to the tableLayout
        tableLayout.addView(row);

        // Add empty row for space
        TableRow emptyRow = new TableRow(this);
        emptyRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 20)); // Set height

        // Create a new row for Course ID and Course Name
        TableRow courseRow = new TableRow(this);
        courseRow.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_background));

        // Add Course ID TextView
        TextView textViewCourseID = new TextView(this);
        textViewCourseID.setText(courseID);
        textViewCourseID.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_background));
        textViewCourseID.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f)); // Set layout weight
        textViewCourseID.setGravity(Gravity.CENTER); // Align text to center
        courseRow.addView(textViewCourseID);

        // Add Course Name TextView
        TextView textViewCourseName = new TextView(this);
        textViewCourseName.setText(courseName);
        textViewCourseName.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_background));
        textViewCourseName.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f)); // Set layout weight
        textViewCourseName.setGravity(Gravity.CENTER); // Align text to center
        courseRow.addView(textViewCourseName);

        // Add the courseRow to the same tableLayout
        tableLayout.addView(courseRow);
        tableLayout.addView(emptyRow);


    }
}
